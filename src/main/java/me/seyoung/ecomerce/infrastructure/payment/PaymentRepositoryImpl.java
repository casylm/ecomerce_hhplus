package me.seyoung.ecomerce.infrastructure.payment;

import me.seyoung.ecomerce.domain.payment.Payment;
import me.seyoung.ecomerce.domain.payment.PaymentRepository;

import java.util.Optional;

public class PaymentRepositoryImpl implements PaymentRepository {

    private PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        return paymentJpaRepository.findById(paymentId);
    }
}
