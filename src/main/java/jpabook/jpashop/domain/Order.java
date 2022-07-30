package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id") //FK이름 >> member_id가 됨.
    private Member member;//FK

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;//주문 상태 > [Order, Cencel]


    //==연관관계 메서드==
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){ //양방향 메서드
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){ //양방향 메서드
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    /*
    * 주문할 때 주문에 대한 멤버, 배달, 주문 상품들, 주문상태(ORDER), 주문 날짜에 대하여 생성
    * setMember, setDelivery, addOrderItem 은 양방향 메서드로 각각 Order를 관리하게 함
    */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        //...문법 : List형식처럼 사용

        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비즈니스 로직==//
    /*
    * 주문 취소
    * >> 주문 취소를 하게 된다면 재고가 다시 늘어가는 형태
    */
    public void cancel(){
        //배송 완료된 상태인 것 체크
        if(delivery.getStatus()==DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능 합니다.");
        }

        //주문 상태 취소로 변경
        this.setStatus(OrderStatus.CANCEL);

        //취소일 때 재고를 올림림
        for(OrderItem orderItem : this.orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /*
    * 전체 주문 가격 조회
    */
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : this.orderItems)
            totalPrice = orderItem.getTotalPrice();
        return totalPrice;
    }

}
