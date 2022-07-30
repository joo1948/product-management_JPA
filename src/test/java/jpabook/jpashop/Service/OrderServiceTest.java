package jpabook.jpashop.Service;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember();

        Item item = createBook("시골 JPA",1000,10);

        int orderCount =2;

        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        //Order의 상태를 가지고 있는지 ?
        assertEquals("상문 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다." , 1000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, item.getStockQuantity());
    }


    @Test
    public void 상품주문_재고수량초과() throws Exception{
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA",1000,1);

        int orderCount = 11;

        //when
        Long getOrder = orderService.order(member.getId(), item.getId(), orderCount);

        //then
        NotEnoughStockException thrown = assertThrows(NotEnoughStockException.class, () -> OrderService.join(getOrder));
        assertEquals("재고 수량 부족으로 인한 예외가 발생해야 한다.", thrown.getMessage());
    }


    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book item = createBook("시골JPA", 10000, 10);

        int orderCount = 2;
        Long order = orderService.order(member.getId(), item.getId(), orderCount);

        //when

        orderService.cancelOrder(order);

        //then
        Order one = orderRepository.findOne(order);
        assertEquals("주문 취소시 상태는 CANCEL입니다.", OrderStatus.CENCEL, one.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());


    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가" , "123-123"));
        em.persist(member);
        return member;
    }
}