package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetProductListUseCase 단위 테스트")
class GetProductListUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetProductListUseCase getProductListUseCase;

    @Test
    @DisplayName("전체 상품 목록을 조회한다")
    void 전체_상품_목록을_조회한다() {
        // given
        List<Product> products = Arrays.asList(
                new Product(1L, "노트북", 1500000L, 10, "전자제품"),
                new Product(2L, "마우스", 50000L, 20, "전자제품"),
                new Product(3L, "키보드", 100000L, 15, "전자제품")
        );
        given(productRepository.findAll()).willReturn(products);

        // when
        ProductInfo.Products result = getProductListUseCase.execute();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(3);
        assertThat(result.getProducts().get(0).getName()).isEqualTo("노트북");
        assertThat(result.getProducts().get(1).getName()).isEqualTo("마우스");
        assertThat(result.getProducts().get(2).getName()).isEqualTo("키보드");

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("상품이 없으면 빈 목록을 반환한다")
    void 상품이_없으면_빈_목록을_반환한다() {
        // given
        given(productRepository.findAll()).willReturn(List.of());

        // when
        ProductInfo.Products result = getProductListUseCase.execute();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).isEmpty();

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("재고가 있는 상품과 없는 상품이 모두 목록에 포함된다")
    void 재고_유무_상관없이_모든_상품을_조회한다() {
        // given
        List<Product> products = Arrays.asList(
                new Product(1L, "재고있음", 10000L, 10, "기타"),
                new Product(2L, "품절", 20000L, 0, "기타")
        );
        given(productRepository.findAll()).willReturn(products);

        // when
        ProductInfo.Products result = getProductListUseCase.execute();

        // then
        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts().get(0).getStock()).isEqualTo(10);
        assertThat(result.getProducts().get(1).getStock()).isEqualTo(0);
    }
}
