package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTopSellingProductsUseCase 단위 테스트")
class GetTopSellingProductsUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetTopSellingProductsUseCase getTopSellingProductsUseCase;

    @Test
    @DisplayName("지정한 기간 내 인기 상품을 판매량 순으로 조회한다")
    void 인기_상품을_판매량_순으로_조회한다() {
        // given
        int days = 3;
        int limit = 5;

        // 상품별 판매량 집계 데이터
        Map<Long, Long> salesByProduct = new HashMap<>();
        salesByProduct.put(1L, 100L);  // 1번 상품: 100개 판매
        salesByProduct.put(2L, 200L);  // 2번 상품: 200개 판매 (1위)
        salesByProduct.put(3L, 50L);   // 3번 상품: 50개 판매
        salesByProduct.put(4L, 150L);  // 4번 상품: 150개 판매 (2위)

        given(orderRepository.getSalesCountByProduct(days)).willReturn(salesByProduct);

        // 상품 정보
        List<Product> products = List.of(
                new Product(2L, "상품2", 20000L, 10, "전자제품"),
                new Product(4L, "상품4", 15000L, 5, "가전"),
                new Product(1L, "상품1", 10000L, 3, "도서"),
                new Product(3L, "상품3", 5000L, 8, "식품")
        );
        given(productRepository.findAllByIds(List.of(2L, 4L, 1L, 3L))).willReturn(products);

        // when
        List<ProductInfo.TopProductResult> result = getTopSellingProductsUseCase.execute(days, limit);

        // then
        assertThat(result).hasSize(4);

        // 판매량 순서대로 정렬되어 있는지 확인
        assertThat(result.get(0).getProductId()).isEqualTo(2L);
        assertThat(result.get(0).getProductName()).isEqualTo("상품2");
        assertThat(result.get(0).getTotalSold()).isEqualTo(200L);

        assertThat(result.get(1).getProductId()).isEqualTo(4L);
        assertThat(result.get(1).getTotalSold()).isEqualTo(150L);

        assertThat(result.get(2).getProductId()).isEqualTo(1L);
        assertThat(result.get(2).getTotalSold()).isEqualTo(100L);

        assertThat(result.get(3).getProductId()).isEqualTo(3L);
        assertThat(result.get(3).getTotalSold()).isEqualTo(50L);

        verify(orderRepository, times(1)).getSalesCountByProduct(days);
        verify(productRepository, times(1)).findAllByIds(any());
    }
}
