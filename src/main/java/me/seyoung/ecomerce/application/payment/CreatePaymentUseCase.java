package me.seyoung.ecomerce.application.payment;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.coupon.ApplyCouponUseCase;
import me.seyoung.ecomerce.application.coupon.CouponInfo;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.payment.*;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ApplyCouponUseCase applyCouponUseCase;
    private final PointRepository pointRepository;

    /**
     * 결제 생성 (쿠폰 사용, 포인트 차감, 재고 차감 실제 수행)
     * 주문 생성 시 재고 확인과 상품 총액 계산이 완료된 상태
     * 결제 시점에 쿠폰 할인과 포인트 차감을 적용하여 최종 금액 계산
     */
    public PaymentInfo.Result execute(Pay command) {
        // 1. 주문 조회
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. orderId=" + command.orderId()));

        // 주문에서 상품 총액 가져오기
        long totalAmount = order.getTotalPrice();
        long discountAmount = 0;

        // 2. 쿠폰 사용 처리 및 할인 적용 (ApplyCouponUseCase 사용)
        if (command.userCouponId() != null) {
            CouponInfo.CouponUseResult couponResult = applyCouponUseCase.execute(command.userId(), command.userCouponId());
            discountAmount += couponResult.getDiscountAmount();
        }

        // 3. 포인트 차감 처리 및 할인 적용
        long pointToUse = command.pointToUse() != null ? command.pointToUse() : 0L;
        if (pointToUse > 0) {
            Point point = pointRepository.findByUserId(command.userId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자의 포인트가 존재하지 않습니다."));

            // 포인트 잔액 확인 및 차감
            point.use(pointToUse);
            pointRepository.save(point);

            // 포인트 할인 적용
            discountAmount += pointToUse;
        }

        // 4. 재고 실제 차감
        for (OrderItem item : order.getItems()) {
            productRepository.deductStock(item.getProductId(), item.getQuantity());
        }

        // 5. 최종 결제 금액 계산
        long finalAmount = Math.max(0, totalAmount - discountAmount);
        Price finalPrice = new Price(finalAmount);

        // 6. 결제 생성 및 완료 처리
        Payment payment = Payment.create(command.orderId(), finalPrice, command.userCouponId(), pointToUse);
        payment.complete();
        paymentRepository.save(payment);

        return PaymentInfo.Result.from(payment);
    }
}
