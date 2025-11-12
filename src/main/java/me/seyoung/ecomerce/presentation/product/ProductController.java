package me.seyoung.ecomerce.presentation.product;

import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.application.product.*;
import me.seyoung.ecomerce.application.product.dto.ProductInfo;
import me.seyoung.ecomerce.presentation.product.dto.ProductDetailResponse;
import me.seyoung.ecomerce.presentation.product.dto.ProductListResponse;
import me.seyoung.ecomerce.presentation.product.dto.TopSellingProductsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final GetProductListUseCase getProductListUseCase;
    private final GetProductUseCase getProductUseCase;
    private final GetTopSellingProductsUseCase getTopSellingProductsUseCase;

    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts() {
        ProductInfo.Products productInfo = getProductListUseCase.execute();
        ProductListResponse response = ProductListResponse.from(productInfo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable Long productId) {
        ProductInfo.ProductDetailInfo productDetail = getProductUseCase.execute(productId);
        ProductDetailResponse response = ProductDetailResponse.from(productDetail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top")
    public ResponseEntity<TopSellingProductsResponse> getTopSellingProducts(
            @RequestParam(required = false, defaultValue = "3") int days,
            @RequestParam(required = false, defaultValue = "5") int limit) {
        List<ProductInfo.TopProductResult> topProducts = getTopSellingProductsUseCase.execute(days, limit);
        TopSellingProductsResponse response = TopSellingProductsResponse.from(topProducts);
        return ResponseEntity.ok(response);
    }
}  