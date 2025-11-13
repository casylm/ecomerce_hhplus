package me.seyoung.ecomerce.infrastructure.payment;

import me.seyoung.ecomerce.domain.payment.Payment;
import me.seyoung.ecomerce.domain.payment.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<Long, Payment> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public Payment save(Payment payment) {
        Long id = payment.getId();

        // 신규 저장인 경우 id 자동 생성
        if (id == null) {
            id = sequence.getAndIncrement();
            payment.assignId(id);
        }

        storage.put(id, payment);
        return payment;
    }

    public Optional<Payment> findById(Long paymentId) {
        return Optional.ofNullable(storage.get(paymentId));
    }
}
