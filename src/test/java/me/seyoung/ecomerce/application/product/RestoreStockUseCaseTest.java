package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.infrastructure.product.InMemoryProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestoreStockUseCaseTest {

    @Mock
    private InMemoryProductRepository productRepository;

    @InjectMocks
    private RestoreStockUseCase restoreStockUseCase;

    @Test
    @DisplayName("상품의 재고를 복구한다")
    void 재고를_복구한다() {
        // given

        Long productId = 1L;
        int quantity = 5;

        Product product = new Product(productId, "노트북", 1500000L, 10, "전자제품");
        product.increaseStock(quantity);

        when(productRepository.restoreStock(productId, quantity))
                .thenReturn(Optional.of(product));

        // when
        ProductInfo.StockIncrease result = restoreStockUseCase.execute(productId, quantity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getIncreasedQuantity()).isEqualTo(quantity);
        assertThat(result.getTotalStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("품절 상품의 재고를 복구할 수 있다")
    void 품절_상품의_재고를_복구할_수_있다() {
        // given
        Long productId = 2L;
        int quantity = 10;

        Product product = new Product(productId, "품절상품", 10000L, 0, "기타");
        product.increaseStock(quantity);

        when(productRepository.restoreStock(productId, quantity))
                .thenReturn(Optional.of(product));

        // when
        ProductInfo.StockIncrease result = restoreStockUseCase.execute(productId, quantity);

        // then
        assertThat(result.getTotalStock()).isEqualTo(quantity);
    }
}
