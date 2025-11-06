package me.seyoung.ecomerce.application.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProductUseCase {
    private final ProductRepository productRepository;

    public ProductInfo.ProductDetailInfo execute(Long productId) {
        // 상품 아이디의 존재 여부 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 아이디가 없습니다"));

        // 상품 정보 반환
        return ProductInfo.ProductDetailInfo.from(product);
    }
}
