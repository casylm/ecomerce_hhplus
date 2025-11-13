package me.seyoung.ecomerce.infrastructure.payment;

import me.seyoung.ecomerce.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
