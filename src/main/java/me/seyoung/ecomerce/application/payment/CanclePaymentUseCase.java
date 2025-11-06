package me.seyoung.ecomerce.application.payment;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.payment.Payment;
import me.seyoung.ecomerce.domain.payment.PaymentInfo;
import me.seyoung.ecomerce.domain.payment.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CanclePaymentUseCase {

    private final PaymentRepository paymentRepository;

    public PaymentInfo.Result execute(Long paymentId) {
        // 결제 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. paymentId=" + paymentId));

        // 결제 취소 (도메인 규칙 검증)
        payment.cancel();

        // 변경된 결제 저장
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentInfo.Result.from(savedPayment);
    }
}
