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

    private final OrderJpaRepository orderItemJpaRepository;

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return Optional.empty();
    }

    @Override
    public List<Order> findOrdersWithinDays(int days) {
        return null;
    }

    @Override
    public List<OrderItem> findOrderItemsWithinDays(int days) {
        return null;
    }

    @Override
    public Map<Long, Long> getSalesCountByProduct(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<ProductSalesProjection> results = orderItemJpaRepository.countSalesByProductSince(since);

        Map<Long, Long> salesByProduct = new HashMap<>();
        for (ProductSalesProjection result : results) {
            salesByProduct.put(result.getProductId(), result.getTotalQuantity());
        }

        return salesByProduct;
    }
}
