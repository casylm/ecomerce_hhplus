package me.seyoung.ecomerce.presentation.payment;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.payment.CanclePaymentUseCase;
import me.seyoung.ecomerce.application.payment.CreatePaymentUseCase;
import me.seyoung.ecomerce.domain.payment.Pay;
import me.seyoung.ecomerce.domain.payment.PaymentInfo;
import me.seyoung.ecomerce.domain.payment.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Payment API Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final CanclePaymentUseCase canclePaymentUseCase;

    /**
     * 결제 생성
     * POST /api/payments
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        Pay command = new Pay(
                request.orderId(),
                request.amount(),
                request.userId(),
                request.userCouponId()
        );

        PaymentInfo.Result result = createPaymentUseCase.execute(command);
        PaymentResponse response = PaymentResponse.from(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 결제 취소
     * POST /api/payments/{paymentId}/cancel
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long paymentId) {
        PaymentInfo.Result result = canclePaymentUseCase.execute(paymentId);
        PaymentResponse response = PaymentResponse.from(result);

        return ResponseEntity.ok(response);
    }

    // Request/Response DTOs

    /**
     * 결제 생성 요청
     */
    public record CreatePaymentRequest(
            Long orderId,
            Long amount,
            Long userId,
            Long userCouponId  // nullable
    ) {}

    /**
     * 결제 응답
     */
    public record PaymentResponse(
            Long paymentId,
            Long orderId,
            Long amount,
            PaymentStatus status,
            LocalDateTime paidAt,
            LocalDateTime cancelledAt
    ) {
        public static PaymentResponse from(PaymentInfo.Result result) {
            return new PaymentResponse(
                    result.paymentId(),
                    result.orderId(),
                    result.amount(),
                    result.status(),
                    result.paidAt(),
                    result.cancelledAt()
            );
        }
    }
}
