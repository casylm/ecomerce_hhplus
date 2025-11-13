package me.seyoung.ecomerce.application.order;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompleteOrderUseCase {

    private final OrderRepository orderRepository;

    public void execute(Long orderId) {
        // 1. 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. orderId=" + orderId));

        // 2. 주문 완료 처리 (도메인 규칙 검증)
        order.complete();

        // 3. 주문 상태 저장
        orderRepository.save(order);
    }
}
