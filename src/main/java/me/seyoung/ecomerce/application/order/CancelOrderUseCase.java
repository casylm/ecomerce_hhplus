package me.seyoung.ecomerce.application.order;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PointRepository pointRepository;
    private final UserCouponRepository userCouponRepository;

    public void cancel(Long orderId, Long usedCouponId, Long usedPointAmount) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. orderId=" + orderId));

        // 2. 주문 취소
        order.cancel();

        // 3. 주문 상태 저장
        orderRepository.save(order);
    }
}
