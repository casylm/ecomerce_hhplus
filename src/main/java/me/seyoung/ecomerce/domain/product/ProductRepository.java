package me.seyoung.ecomerce.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    // 상품 조회
    Optional<Product> findById(Long productId);

    // 상품 목록 조회
    List<Product> findAll();

    // 판매 가능한 상품 목록 조회
    List<Product> findAllAvailable();

    // 상품 저장
    Product save(Product product);

    // 재고 차감
    Optional<Product> deductStock(Long productId, int quantity);

    // 재고 복구
    Optional<Product> restoreStock(Long productId, int quantity);
}
