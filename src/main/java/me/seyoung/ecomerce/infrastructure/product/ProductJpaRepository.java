package me.seyoung.ecomerce.infrastructure.product;

import me.seyoung.ecomerce.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    // 상품 조회는 JpaRepository에서 제공
    // Optional<Product> findById(Long productId);

    // 상품 목록 조회는 JpaRepository에서 제공
    // List<Product> findAll();

    // 상품 저장은 JpaRepository에서 제공
    // Product save(Product product);
}
