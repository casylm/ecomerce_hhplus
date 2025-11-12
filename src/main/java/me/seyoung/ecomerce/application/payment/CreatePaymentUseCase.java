package me.seyoung.ecomerce.application.payment;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.payment.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;

    public PaymentInfo.Result execute(Pay command) {

        Optional<Payment> existing = paymentRepository.findByOrderId(command.orderId());
        if (existing.isPresent() && existing.get().getStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }

        Payment payment = Payment.create(command.orderId(), new Price(command.amount()));
        payment.complete();
        paymentRepository.save(payment);
        return PaymentInfo.Result.from(payment);
    }
}
