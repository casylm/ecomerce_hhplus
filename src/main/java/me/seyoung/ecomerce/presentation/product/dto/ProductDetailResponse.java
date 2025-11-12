package me.seyoung.ecomerce.presentation.product.dto;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;

public record ProductDetailResponse(
        Long id,
        String name,
        Long price,
        int stock
) {
    public static ProductDetailResponse from(ProductInfo.ProductDetailInfo detail) {
        return new ProductDetailResponse(
                detail.getId(),
                detail.getName(),
                detail.getPrice(),
                detail.getStock()
        );
    }
}
