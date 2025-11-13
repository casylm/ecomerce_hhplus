package me.seyoung.ecomerce.application.order;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.payment.Price;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Long execute(Long userId, Long userCouponId, List<OrderItem> items, long pointToUse) {
        // 1. 재고 확인 (차감은 결제 시점에 수행)
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. productId=" + item.getProductId()));

            // 재고 확인
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("재고가 부족합니다. 상품ID=" + item.getProductId()
                        + ", 필요수량=" + item.getQuantity() + ", 현재재고=" + product.getStock());
            }
        }

        // 2. 상품 총액 계산
        long totalAmount = items.stream()
                .mapToLong(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
                    return product.getPrice() * item.getQuantity();
                })
                .sum();
        Price totalPrice = new Price(totalAmount);

        // 3. 주문 생성 및 저장 (상품 총액 포함, 할인은 결제 시점에 적용)
        Order order = Order.create(userId, items, totalPrice);
        Order savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }

    public Long execute(Long userId, List<OrderItem> items) {
        return execute(userId, null, items, 0L);
    }
}
