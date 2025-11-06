package me.seyoung.ecomerce.application.product;

import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.infrastructure.product.InMemoryProductRepository;

public class DeductStockUseCase {

    private InMemoryProductRepository productRepository;

    public ProductInfo.StockDecrease execute(Long productId, int quantity) {

        Product product = productRepository.deductStock(productId, quantity)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. productId=" + productId));

        return ProductInfo.StockDecrease.of(productId,quantity,product.getStock());
    }
}
