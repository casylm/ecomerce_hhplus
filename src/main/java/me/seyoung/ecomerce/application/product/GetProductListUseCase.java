package me.seyoung.ecomerce.application.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProductListUseCase {
    private final ProductRepository productRepository;

    public ProductInfo.Products execute() {
        // 전체 상품 리스트를 반환
        List<Product> productList = productRepository.findAll();

        return ProductInfo.Products.from(productList);
    }
}
