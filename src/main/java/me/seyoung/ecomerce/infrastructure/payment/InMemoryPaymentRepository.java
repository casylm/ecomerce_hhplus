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
            payment.assignId(id); // ✅ Payment 도메인 엔티티가 ID 주입 책임 갖도록 함
        }

        storage.put(id, payment);
        return payment;
    }

    public Optional<Payment> findById(Long paymentId) {
        return Optional.ofNullable(storage.get(paymentId));
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return storage.values().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .findFirst();
    }
}
