package me.seyoung.ecomerce.infrastructure.order;

import me.seyoung.ecomerce.infrastructure.order.entitiy.OrderItemEntitiy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderItemEntitiy, Long> {
    @Query("""
        SELECT oi.productId AS productId, SUM(oi.quantity) AS totalQuantity
        FROM OrderItemEntitiy oi
        JOIN OrderEntity o ON oi.orderId = o.orderId
        WHERE o.status = 'COMPLETED'
          AND o.createdAt >= :since
        GROUP BY oi.productId
    """)
    List<ProductSalesProjection> countSalesByProductSince(@Param("since") LocalDateTime since);
}
