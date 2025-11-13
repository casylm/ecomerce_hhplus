package me.seyoung.ecomerce.presentation.order;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.order.CancelOrderUseCase;
import me.seyoung.ecomerce.application.order.CompleteOrderUseCase;
import me.seyoung.ecomerce.application.order.CreateOrderUseCase;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.presentation.order.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompleteOrderUseCase completeOrderUseCase;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(item -> OrderItem.create(item.productId(), item.quantity(), item.pricePerItem()))
                .toList();

        Long orderId;
        if (request.userCouponId() != null || request.pointToUse() != null) {
            orderId = createOrderUseCase.execute(
                    request.userId(),
                    request.userCouponId(),
                    items,
                    request.pointToUse() != null ? request.pointToUse() : 0L
            );
        } else {
            orderId = createOrderUseCase.execute(request.userId(), items);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateOrderResponse(orderId));
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        completeOrderUseCase.execute(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody(required = false) CancelOrderRequest request) {
        Long usedCouponId = request != null ? request.usedCouponId() : null;
        Long usedPointAmount = request != null ? request.usedPointAmount() : null;
        cancelOrderUseCase.cancel(orderId, usedCouponId, usedPointAmount);
        return ResponseEntity.ok().build();
    }
}
