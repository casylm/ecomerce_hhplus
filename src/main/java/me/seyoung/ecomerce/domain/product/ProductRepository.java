package me.seyoung.ecomerce.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    // 상품 조회
    Optional<Product> findById(Long productId);

    // 여러 ID로 상품 조회
    List<Product> findAllByIds(List<Long> productIds);

    // 상품 목록 조회
    List<Product> findAll();

    // 판매 가능한 상품 목록 조회
    List<Product> findAllAvailable();

    // 상품 저장
    Product save(Product product);

    // 상품조회(락)
    Optional<Product> findByIdForUpdate(Long id);
}
