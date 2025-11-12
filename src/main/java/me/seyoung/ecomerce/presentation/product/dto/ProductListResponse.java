package me.seyoung.ecomerce.presentation.product.dto;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;

import java.util.ArrayList;
import java.util.List;

public record ProductListResponse(List<ProductResponse> products) {

    public static ProductListResponse from(ProductInfo.Products productInfo) {
        List<ProductResponse> products = new ArrayList<>();

        for (ProductInfo.ProductDetailInfo detail : productInfo.getProducts()) {
            products.add(ProductResponse.from(detail));
        }

        return new ProductListResponse(products);
    }

    public record ProductResponse(
            Long id,
            String name,
            Long price,
            int stock
    ) {
        public static ProductResponse from(ProductInfo.ProductDetailInfo detail) {
            return new ProductResponse(
                    detail.getId(),
                    detail.getName(),
                    detail.getPrice(),
                    detail.getStock()
            );
        }
    }
}
