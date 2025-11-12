package me.seyoung.ecomerce.domain.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long orderId);

    List<Order> findOrdersWithinDays(int days);

    List<OrderItem> findOrderItemsWithinDays(int days);

    // 상품별 판매량 집계 (key: productId, value: 총 판매 수량)
    Map<Long, Long> getSalesCountByProduct(int days);
}
