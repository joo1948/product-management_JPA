package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryReposiroty {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> result = findOrders(); // XToOne관계의 데이터들 조회
            result.forEach(o -> {
                List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());// orderItems > OneToMany 관계의 데이터를 for문으로 조회
                o.setOrderItems(orderItems);

                //이렇게 하면 성능 문제로 N+1 문제 발생
        });
        return result;

        /**
         * DTO 직접 조회 할 시
         * ToOne(N:1, 1:1) 관계들을 먼저 조회하고, ToMany(1:N) 관계는 각각 별도로 처리한다.
         * 이런 방식을 선택한 이유는 다음과 같다.
         * ToOne 관계는 조인해도 데이터 row 수가 증가하지 않는다.
         * ToMany(1:N) 관계는 조인하면 row 수가 증가한다
         * >> 따라서 ToOne관계의 Order, Member, Delivery는 join으로 먼저 가져온 후, 필요한 OrderItems 는 따로 가져와 set 해줌.
         */
    }

    /**
     *
     * ToOne 관계들을 먼저 조회하고, 여기서 얻은 식별자 orderId로 ToMany 관계인 OrderItem 을
     * 한꺼번에 조회
     * MAP을 사용해서 매칭 성능 향상(O(1))
     */
    public List<OrderQueryDto> findAllOrderItems() {
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMaps = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.stream()
                .forEach(o -> o.setOrderItems(orderItemMaps.get(o.getOrderId())));

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                    " from OrderItem oi" +
                    " join oi.item i" +
                    " where oi.order.id = : orderId",OrderItemQueryDto.class)
                    .setParameter("orderId", orderId)
                    .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                                .getResultList();

    }


}
