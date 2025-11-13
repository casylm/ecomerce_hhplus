package me.seyoung.ecomerce.application.payment;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.payment.Payment;
import me.seyoung.ecomerce.domain.payment.PaymentInfo;
import me.seyoung.ecomerce.domain.payment.PaymentRepository;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointHistory;
import me.seyoung.ecomerce.domain.point.PointHistoryRepository;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.point.PointStatus;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CanclePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserCouponRepository userCouponRepository;

    public PaymentInfo.Result execute(Long paymentId) {
        // 1. 결제 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. paymentId=" + paymentId));

        // 2. 결제 취소 (도메인 규칙 검증)
        payment.cancel();

        // 3. 주문 조회 (재고 복구를 위해)
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다. orderId=" + payment.getOrderId()));

        // 4. 재고 복구
        for (OrderItem item : order.getItems()) {
            productRepository.restoreStock(item.getProductId(), item.getQuantity());
        }

        // 5. 포인트 반환 (사용한 경우에만)
        Long usedPointAmount = payment.getUsedPointAmount();
        if (usedPointAmount != null && usedPointAmount > 0) {
            Point point = pointRepository.findByUserId(order.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자의 포인트가 존재하지 않습니다."));
            point.charge(usedPointAmount); // 사용한 포인트를 다시 충전
            pointRepository.save(point);

            // 포인트 히스토리에 기록
            PointHistory history = new PointHistory(order.getUserId(), PointStatus.CHARGE, usedPointAmount);
            pointHistoryRepository.save(history);
        }

        // 6. 쿠폰 사용 취소 (사용한 경우에만)
        Long usedCouponId = payment.getUsedCouponId();
        if (usedCouponId != null) {
            UserCoupon userCoupon = userCouponRepository.findById(usedCouponId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
            userCoupon.cancelUse(); // 쿠폰 사용 취소
            userCouponRepository.save(userCoupon);
        }

        // 7. 변경된 결제 저장
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentInfo.Result.from(savedPayment);
    }
}
