package me.seyoung.ecomerce.application.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestoreStockUseCase {

    private final ProductRepository productRepository;

    public ProductInfo.StockIncrease execute(Long productId, int quantity) {

        // 1. 재고 복구용 Product 조회
        //Product product = productRepository.findByIdForUpdate(productId)
        //        .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. productId=" + productId));

        // 1. 레디스 사용
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. productId=" + productId));

        // 2. 도메인 로직 실행
        product.increaseStock(quantity);

        // 3. 저장
        productRepository.save(product);

        return ProductInfo.StockIncrease.of(productId, quantity, product.getStock());
    }
}
