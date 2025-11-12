package me.seyoung.ecomerce.application.order;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import me.seyoung.ecomerce.infrastructure.order.InMemoryOrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final InMemoryOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PointRepository pointRepository;
    private final UserCouponRepository userCouponRepository;

    public void cancel(Long orderId, Long usedCouponId, Long usedPointAmount) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. orderId=" + orderId));

        // 2. 주문 취소 (도메인 규칙 검증)
        order.cancel();

        // 3. 재고 복구
        for (OrderItem item : order.getItems()) {
            productRepository.restoreStock(item.getProductId(), item.getQuantity());
        }

        // 4. 포인트 반환 (사용한 경우에만)
        if (usedPointAmount != null && usedPointAmount > 0) {
            Point point = pointRepository.findByUserId(order.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자의 포인트가 존재하지 않습니다."));
            point.charge(usedPointAmount); // 사용한 포인트를 다시 충전
            pointRepository.save(point);
        }

        // 5. 쿠폰 사용 취소 (사용한 경우에만)
        if (usedCouponId != null) {
            UserCoupon userCoupon = userCouponRepository.findById(usedCouponId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
            userCoupon.cancelUse(); // 쿠폰 사용 취소
            userCouponRepository.save(userCoupon);
        }

        // 6. 주문 상태 저장
        orderRepository.save(order);
    }

    public void cancel(Long orderId) {
        cancel(orderId, null, 0L);
    }
}
