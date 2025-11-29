package me.seyoung.ecomerce.application.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeductStockUseCase {

    private final ProductRepository productRepository;

    public ProductInfo.StockDecrease execute(Long productId, int quantity) {

        // 1. 재고 차감 대상 조회 (비관적 락으로 조회해야 함)
        //Product product = productRepository.findByIdForUpdate(productId)
        //        .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. productId=" + productId));

        // 1. 레디스 사용
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. productId=" + productId));

        // 2. 도메인 로직(엔티티 비즈니스 로직) 수행
        product.decreaseStock(quantity);

        // 3. 저장
        productRepository.save(product);

        return ProductInfo.StockDecrease.of(productId, quantity, product.getStock());
    }
}
