package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.infrastructure.product.product.InMemoryProductRepository;

public class RestoreStockUseCase {

    private InMemoryProductRepository productRepository;

    public ProductInfo.StockIncrease execute(Long productId, int quantity) {

        Product product = productRepository.restoreStock(productId,quantity)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. productId=" + productId));

        return ProductInfo.StockIncrease.of(productId,quantity,product.getStock());
    }
}
