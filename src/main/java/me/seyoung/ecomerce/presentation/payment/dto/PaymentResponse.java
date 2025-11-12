package me.seyoung.ecomerce.presentation.payment.dto;

import me.seyoung.ecomerce.domain.payment.PaymentInfo;
import me.seyoung.ecomerce.domain.payment.PaymentStatus;

import java.time.LocalDateTime;

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
