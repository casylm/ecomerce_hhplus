package me.seyoung.ecomerce.application.order;

import me.seyoung.ecomerce.domain.order.Order;
import me.seyoung.ecomerce.domain.order.OrderItem;
import me.seyoung.ecomerce.domain.payment.Price;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import me.seyoung.ecomerce.infrastructure.order.InMemoryOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderUseCase 단위 테스트")
class CreateOrderUseCaseTest {

    @Mock
    private InMemoryOrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    @Test
    @DisplayName("재고가 충분하면 주문을 생성할 수 있다")
    void 재고_충분_시_주문_생성() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 3;
        long pricePerItem = 10000L;

        List<OrderItem> items = List.of(OrderItem.create(productId, quantity, pricePerItem));
        Product product = new Product(productId, "테스트 상품", pricePerItem, 10, "카테고리");

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        Order mockOrder = Order.create(userId, items, new Price(30000L));
        mockOrder.assignId(1L);
        given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

        // when
        Long orderId = createOrderUseCase.execute(userId, items);

        // then
        assertThat(orderId).isEqualTo(1L);
        verify(productRepository, atLeastOnce()).findById(productId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("재고가 부족하면 주문 생성에 실패한다")
    void 재고_부족_시_주문_생성_실패() {
        // given
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 10;
        long pricePerItem = 10000L;

        List<OrderItem> items = List.of(OrderItem.create(productId, quantity, pricePerItem));
        Product product = new Product(productId, "테스트 상품", pricePerItem, 5, "카테고리"); // 재고 5개

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, items))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");

        verify(productRepository, never()).deductStock(any(), anyInt());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 주문 생성 시 예외가 발생한다")
    void 존재하지_않는_상품_주문_생성_시_예외() {
        // given
        Long userId = 1L;
        Long productId = 999L;

        List<OrderItem> items = List.of(OrderItem.create(productId, 1, 10000L));
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.execute(userId, items))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품입니다");
    }
}
