package me.seyoung.ecomerce.infrastructure.order;

import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public Order save(Order order) {
        Long id = order.getId();

        // 신규 저장인 경우 id 자동 생성
        if (id == null) {
            id = sequence.getAndIncrement();
            order.assignId(id);   // ✅ Order 도메인 엔티티에서 ID 할당
        }

        storage.put(id, order);
        return order;
    }

    public Optional<Order> findById(Long orderId) {
        return Optional.ofNullable(storage.get(orderId));
    }

    @Override
    public List<Order> findOrdersWithinDays(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        List<Order> result = new ArrayList<>();

        for (Order order : storage.values()) {
            if (order.getOrderedAt().isAfter(threshold)) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<OrderItem> findOrderItemsWithinDays(int days) {
        List<Order> recentOrders = findOrdersWithinDays(days);
        List<OrderItem> result = new ArrayList<>();

        for (Order order : recentOrders) {
            result.addAll(order.getItems());
        }

        return result;
    }
}
