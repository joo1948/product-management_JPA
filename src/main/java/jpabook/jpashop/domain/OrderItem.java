package jpabook.jpashop.domain;

import jpabook.jpashop.domain.Item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice;//주문가격

    private int count;//주문 수량

    /*protected OrderItem(){
     Service에서 OrderItem orderItem1 = new OrderItem(); 이런 식으로 만들지 않게 하기 위해 생성자 proptected 설정
     == @NoArgsConstructor(access = AccessLevel.PROTECTED)
    }*/

    //==생성메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);//주문할 상품이 들어오게 된다면 상품의 총 갯수가 줄어야함.

        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().aadStock(count); // 주문 취소 하면 주문 상품의 갯수 원복
    }

    /*
    * 주문 상품에 대한 총 가격 조회
    * */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
