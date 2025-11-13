package me.seyoung.ecomerce.infrastructure.product;

import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;

import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Optional<Product> findById(Long productId) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        return null;
    }

    @Override
    public List<Product> findAllAvailable() {
        return null;
    }

    @Override
    public Product save(Product product) {
        return null;
    }

    @Override
    public Optional<Product> deductStock(Long productId, int quantity) {
        return Optional.empty();
    }

    @Override
    public List<Product> findAllByIds(List<Long> productIds) {
        return null;
    }

    @Override
    public Optional<Product> restoreStock(Long productId, int quantity) {
        return Optional.empty();
    }
}
