package jpabook.jpashop.Service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public static void join(Long getOrder) {
    }

    /*
    * 주문
    * */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());//그냥 회원 Address 로 가져옴.
        
        //주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        //OrderITem은 왜 save 안해줌 ? > Order 클래스에 매핑된 OrderItem은 cascade.ALL로 되어 있기 때문에 Order 저장시 OrderITem도 저장됨.


        return order.getId();
    }
    /*
    취소
    *  */
    
    @Transactional
    public void cancelOrder(Long orderId){
        //엔티티조회
        Order order = orderRepository.findOne(orderId);
        
        //주문취소
        order.cancel();
    }

    /*
    검색색
    * */
    /*
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findSearch();
    }*/

}
