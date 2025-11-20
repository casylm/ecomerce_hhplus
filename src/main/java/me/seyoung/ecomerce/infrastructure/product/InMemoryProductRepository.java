package me.seyoung.ecomerce.infrastructure.product;

import me.seyoung.ecomerce.domain.product.Product;
import me.seyoung.ecomerce.domain.product.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryProductRepository implements ProductRepository {

    // 상품을 저장할 메모리 Map
    private final Map<Long, Product> store = new ConcurrentHashMap<>();

    // ID 자동 생성용
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<Product> findById(Long productId) {
        return Optional.ofNullable(store.get(productId));
    }

    @Override
    public List<Product> findAllByIds(List<Long> productIds) {
        List<Product> products = new ArrayList<>();

        for (Long productId : productIds) {
            Product product = store.get(productId);
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public List<Product> findAllAvailable() {
        List<Product> availableProducts = new ArrayList<>();

        for (Product product : store.values()) {
            if (product.isAvailable()) {
                availableProducts.add(product);
            }
        }

        return availableProducts;
    }

    @Override
    public Product save(Product product) {
        // ID가 없으면 새로 생성
        if (product.getId() == null) {
            Long newId = idGenerator.getAndIncrement();
            Product newProduct = new Product(newId, product.getName(),
                    product.getPrice(), product.getStock(), product.getCategory());
            store.put(newId, newProduct);
            return newProduct;
        }

        // ID가 있으면 업데이트
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long id) {
        // InMemory 구현에서는 실제 락을 사용할 수 없으므로 일반 조회와 동일하게 동작
        return findById(id);
    }
}
