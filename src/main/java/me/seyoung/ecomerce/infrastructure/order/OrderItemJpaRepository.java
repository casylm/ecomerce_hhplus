package me.seyoung.ecomerce.infrastructure.order;

import me.seyoung.ecomerce.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId IN " +
           "(SELECT o.id FROM Order o WHERE o.orderedAt >= :since)")
    List<OrderItem> findOrderItemsSince(@Param("since") LocalDateTime since);

    @Query("""
        SELECT oi.productId, SUM(oi.quantity)
        FROM OrderItem oi
        JOIN Order o ON oi.orderId = o.id
        WHERE o.status = 'COMPLETED'
          AND o.orderedAt >= :since
        GROUP BY oi.productId
    """)
    List<Object[]> countSalesByProductSince(@Param("since") LocalDateTime since);
}
