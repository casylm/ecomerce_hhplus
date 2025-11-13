package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.infrastructure.product.InMemoryProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeductStockUseCaseTest {

    @Mock
    private InMemoryProductRepository productRepository;

    @InjectMocks
    private DeductStockUseCase deductStockUseCase;

    @Test
    @DisplayName("상품의 재고를 차감한다")
    void 재고를_차감한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");
        product.decreaseStock(3);

        when(productRepository.deductStock(1L, 3))
                .thenReturn(Optional.of(product));

        // when
        ProductInfo.StockDecrease result = deductStockUseCase.execute(1L, 3);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getRemainingStock()).isEqualTo(product.getStock());
        assertThat(result.getRemainingStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("존재하지 않는 상품의 재고를 차감하면 예외가 발생한다")
    void 존재하지_않는_상품의_재고_차감_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> deductStockUseCase.execute(999L, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품이 존재하지 않습니다. productId=" + 999L);
    }

    @Test
    @DisplayName("재고가 부족하면 예외가 발생한다")
    void 재고가_부족하면_예외가_발생한다() {
        // given
        Product product = new Product(1L, "노트북", 1500000L, 5, "전자제품");

        // when & then
        assertThatThrownBy(() -> deductStockUseCase.execute(1L, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품이 존재하지 않습니다.");
    }
}
