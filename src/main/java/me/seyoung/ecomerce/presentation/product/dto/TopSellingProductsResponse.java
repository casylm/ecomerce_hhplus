package me.seyoung.ecomerce.presentation.product.dto;

import me.seyoung.ecomerce.application.product.dto.ProductInfo;

import java.util.ArrayList;
import java.util.List;

public record TopSellingProductsResponse(List<TopSellingProductDto> products) {

    public static TopSellingProductsResponse from(List<ProductInfo.TopProductResult> topProductResults) {
        List<TopSellingProductDto> products = new ArrayList<>();

        for (ProductInfo.TopProductResult result : topProductResults) {
            products.add(TopSellingProductDto.from(result));
        }

        return new TopSellingProductsResponse(products);
    }

    public record TopSellingProductDto(
            Long productId,
            String productName,
            Long totalSold
    ) {
        public static TopSellingProductDto from(ProductInfo.TopProductResult result) {
            return new TopSellingProductDto(
                    result.getProductId(),
                    result.getProductName(),
                    result.getTotalSold()
            );
        }
    }
}
