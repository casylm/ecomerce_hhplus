package me.seyoung.ecomerce.application.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.order.OrderRepository;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GetTopSellingProductsUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * 최근 3일간 인기 상품 조회 (상위 5개)
     */
    public List<ProductInfo.TopProductResult> execute() {
        return execute(3, 5);
    }

    /**
     * 지정한 기간 내 인기 상품 조회
     *
     * @param days 조회 기간 (일)
     * @param limit 조회할 상품 개수
     * @return 인기 상품 목록
     */
    public List<ProductInfo.TopProductResult> execute(int days, int limit) {
        // 1. 상품별 판매량 집계 조회
        Map<Long, Long> salesByProduct = orderRepository.getSalesCountByProduct(days);

        if (salesByProduct.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 판매량 내림차순 정렬 후 상위 N개 productId 추출
        // Map의 엔트리를 리스트로 변환
        List<Map.Entry<Long, Long>> entryList = new ArrayList<>(salesByProduct.entrySet());

        // 값(판매량)을 기준으로 내림차순 정렬
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // 상위 N개(productId)만 추출
        List<Long> topProductIds = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, entryList.size()); i++) {
            topProductIds.add(entryList.get(i).getKey());
        }

        // 3. 상품 정보 일괄 조회
        List<Product> products = productRepository.findAllByIds(topProductIds);

        // productId를 키로 하는 Map 생성 (빠른 조회를 위해)
        Map<Long, Product> productMap = new HashMap<>();
        for (Product product : products) {
            productMap.put(product.getId(), product);
        }

        // 4. 결과 생성 (정렬 순서 유지)
        List<ProductInfo.TopProductResult> results = new ArrayList<>();
        for (Long productId : topProductIds) {
            Product product = productMap.get(productId);

            if (product != null) {
                Long totalSold = salesByProduct.get(productId);
                results.add(ProductInfo.TopProductResult.of(
                        productId,
                        product.getName(),
                        totalSold
                ));
            }
        }

        return results;
    }
}
