package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryReposiroty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryReposiroty orderQueryReposiroty;

    /**
     * 사용자에 따른 주문 상품들 보여주기
     * @OneToMany
     */


    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch()) ;
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기환
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //Lazy 강제초기화
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     *  >> join fetch 사용하여 해당 문제 해결
     */
    @GetMapping("/api/v2/orders")
    public Result orderV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }


    /**
     * V3. fetch join 사용
     */
    @GetMapping("/api/v3/orders")
    public Result orderV3(){
        List<Order> orders = orderRepository.findAllWithItems();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }
    /**
     * V3_1. fetch join 사용 & 페이징 처리
     * order, member, delivery 에 관한 쿼리 1번 ( why ? : fetch join 사용했기 때문 )
     * orderItems에 관한 쿼리 1번 ( why ? : default_batch_fetch_size를 사용하여 in절로 나가게 처리했기 떄문)
     * orderItems와 관련있는 item에 관한 쿼리 1 번 ( why ? : default_batch_fetch_size를 사용하여 in절로 나가게 처리했기 떄문)
     */
    @GetMapping("/api/v3_1/orders")
    public Result orderV3_1(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "0") int limit
    ){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        //orderItems(OneToMany)는 betch-size를 통하여 개별 쿼리가 나가지 않고, in절로 묶여서 나가게 된다.
        /**
        default_batch_fetch_size : global하게 전체적으로 정하는것
        OneToMany에서는 엔티티의 요소 위에 @@BatchSize(size = ?) 달기
        XToOne은 엔티티 최 상단에 @BatchSize(size = ?)
        default_batch_fect_size로 정하는 것이 좋다 ( 100 ~ 1000 사이 )
        **/
        return new Result(collect.size(), collect);
    }

    /**
     * v4 : DTO 직접 조회
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4(){
       return orderQueryReposiroty.findOrderQueryDtos();
    }

    /**
     * v5 : DTO 직접 조회 컬렉션 최적화
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5(){
        return orderQueryReposiroty.findAllOrderItems();
    }

    @Data
    static class OrderDto{
        private Long id;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        //private List<OrderItem> orderITem; >> 생성자에서 OrderItem을 그대로 가져와도 되지만 모든 경우에서 Entity 노출은 하지말자
        //OrderItems에도 DTO 생성하기

        public OrderDto(Order order) {
            id = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());

        }
    }

    @Data
    static class OrderItemDto{
        private String name;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            name = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }
}



