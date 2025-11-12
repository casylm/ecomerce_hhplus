package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetProductUseCase 단위 테스트")
class GetProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductUseCase getProductUseCase;

    @Test
    @DisplayName("상품 ID로 상품을 조회한다")
    void 상품을_조회한다() {
        // given
        Long productId = 1L;
        Product product = new Product(1L, "노트북", 1500000L, 10, "전자제품");
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductInfo.ProductDetailInfo result = getProductUseCase.execute(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("노트북");
        assertThat(result.getPrice()).isEqualTo(1500000L);
        assertThat(result.getStock()).isEqualTo(10);

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회하면 예외가 발생한다")
    void 존재하지_않는_상품을_조회하면_예외가_발생한다() {
        // given
        Long productId = 999L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getProductUseCase.execute(productId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 아이디가 없습니다");

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("재고가 0인 상품도 조회할 수 있다")
    void 재고가_0인_상품도_조회할_수_있다() {
        // given
        Long productId = 1L;
        Product product = new Product(1L, "품절상품", 10000L, 0, "기타");
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductInfo.ProductDetailInfo result = getProductUseCase.execute(productId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStock()).isEqualTo(0);
    }
}
