package me.seyoung.ecomerce.application.payment.event;

import java.time.LocalDateTime;

public record PaymentCompletedEvent(
    Long paymentId,
    Long orderId,
    long amount,
    String status,
    LocalDateTime paidAt
) {
}
