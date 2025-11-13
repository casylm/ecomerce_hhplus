package me.seyoung.ecomerce.infrastructure.order;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class OrderRepsitoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public Order save(Order order) {
        // Order 저장
        Order savedOrder = orderJpaRepository.save(order);

        // OrderItem들 저장
        if (savedOrder.getItems() != null && !savedOrder.getItems().isEmpty()) {
            for (OrderItem item : savedOrder.getItems()) {
                item.assignOrderId(savedOrder.getId());
                orderItemJpaRepository.save(item);
            }
        }

        return savedOrder;
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        Optional<Order> orderOpt = orderJpaRepository.findById(orderId);

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // OrderItem들 조회 및 설정
            List<OrderItem> items = orderItemJpaRepository.findByOrderId(orderId);
            order.setItems(items);
        }

        return orderOpt;
    }

    @Override
    public List<Order> findOrdersWithinDays(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Order> orders = orderJpaRepository.findOrdersSince(since);

        // 각 Order에 OrderItem들 설정
        for (Order order : orders) {
            List<OrderItem> items = orderItemJpaRepository.findByOrderId(order.getId());
            order.setItems(items);
        }

        return orders;
    }

    @Override
    public List<OrderItem> findOrderItemsWithinDays(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return orderItemJpaRepository.findOrderItemsSince(since);
    }

    @Override
    public Map<Long, Long> getSalesCountByProduct(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> results = orderItemJpaRepository.countSalesByProductSince(since);

        Map<Long, Long> salesByProduct = new HashMap<>();
        for (Object[] result : results) {
            Long productId = (Long) result[0];
            Long totalQuantity = (Long) result[1];
            salesByProduct.put(productId, totalQuantity);
        }

        return salesByProduct;
    }
}
