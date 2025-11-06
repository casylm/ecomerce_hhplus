package me.seyoung.ecomerce.application.order;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.coupon.Coupon;
import me.seyoung.ecomerce.domain.coupon.CouponRepository;
import me.seyoung.ecomerce.domain.coupon.UserCoupon;
import me.seyoung.ecomerce.domain.coupon.UserCouponRepository;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.payment.Price;
import me.seyoung.ecomerce.domain.payment.PriceCalculationService;
import me.seyoung.ecomerce.domain.point.Point;
import me.seyoung.ecomerce.domain.point.PointRepository;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import me.seyoung.ecomerce.infrastructure.order.InMemoryOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final InMemoryOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final PointRepository pointRepository;
    private final PriceCalculationService priceCalculationService;

    public Long execute(Long userId, Long userCouponId, List<OrderItem> items, long pointToUse) {
        // 1. 재고 확인 및 차감
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. productId=" + item.getProductId()));

            // 재고 확인
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("재고가 부족합니다. 상품ID=" + item.getProductId()
                        + ", 필요수량=" + item.getQuantity() + ", 현재재고=" + product.getStock());
            }

            // 재고 차감
            productRepository.deductStock(item.getProductId(), item.getQuantity());
        }

        // 2. 쿠폰 사용 처리
        Coupon coupon = null;
        if (userCouponId != null) {
            UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 쿠폰입니다."));

            if (!userCoupon.isOwnedBy(userId)) {
                throw new IllegalArgumentException("본인 소유의 쿠폰만 사용할 수 있습니다.");
            }

            if (!userCoupon.isAvailable()) {
                throw new IllegalStateException("이미 사용된 쿠폰입니다.");
            }

            // 쿠폰 사용 처리
            userCoupon.use();
            userCouponRepository.save(userCoupon);

            // 쿠폰 마스터 정보 조회 (할인 금액 계산용)
            coupon = couponRepository.findById(userCoupon.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰 정보를 찾을 수 없습니다."));
        }

        // 3. 포인트 차감 처리
        if (pointToUse > 0) {
            Point point = pointRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자의 포인트가 존재하지 않습니다."));

            // 포인트 차감 (도메인 내부에서 잔액 확인)
            point.use(pointToUse);
            pointRepository.save(point);
        }

        // 4. 최종 금액 계산
        Price pointPrice = new Price(pointToUse);
        Price finalPrice = priceCalculationService.calculate(items, coupon, pointPrice);

        // 5. 주문 생성 및 저장
        Order order = Order.create(userId, items, finalPrice);
        Order savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }

    public Long execute(Long userId, List<OrderItem> items) {
        return execute(userId, null, items, 0L);
    }
}
