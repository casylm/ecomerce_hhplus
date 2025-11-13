package me.seyoung.ecomerce.infrastructure.order;

import me.seyoung.ecomerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.orderedAt >= :since")
    List<Order> findOrdersSince(@Param("since") LocalDateTime since);
}
