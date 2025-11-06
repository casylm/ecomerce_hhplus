package me.seyoung.ecomerce.domain.payment;

import java.time.LocalDateTime;

public class PaymentInfo {

    @lombok.Builder
    public record Result(
            Long paymentId,
            Long orderId,
            long amount,
            PaymentStatus status,
            LocalDateTime paidAt,
            LocalDateTime cancelledAt
    ) {
        public static Result from(Payment payment) {
            return Result.builder()
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .amount(payment.getAmount().getValue())
                    .status(payment.getStatus())
                    .paidAt(payment.getPaidAt())
                    .cancelledAt(payment.getCancelledAt())
                    .build();
        }
    }
}
