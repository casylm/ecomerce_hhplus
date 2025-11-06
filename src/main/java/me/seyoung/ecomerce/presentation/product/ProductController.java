package me.seyoung.ecomerce.presentation.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.product.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final GetProductListUseCase getProductListUseCase;
    private final GetProductUseCase getProductUseCase;

    @GetMapping
    public ResponseEntity<ProductInfo.Products> getProducts() {
        ProductInfo.Products response = getProductListUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductInfo.ProductDetailInfo> getProduct(@PathVariable Long productId) {
        ProductInfo.ProductDetailInfo response = getProductUseCase.execute(productId);
        return ResponseEntity.ok(response);
    }
}
