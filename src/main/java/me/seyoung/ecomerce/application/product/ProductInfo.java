package me.seyoung.ecomerce.application.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seyoung.ecomerce.domain.product.Product;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductInfo {

    /**
     * 상품 상세 정보
     */
    @Getter
    @Builder
    public static class ProductDetailInfo {
        private final Long id;
        private final String name;
        private final Long price;
        private final int stock;

        public static ProductDetailInfo from(Product product) {
            return ProductDetailInfo.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .build();
        }
    }

    /**
     * 상품 목록
     */
    @Getter
    public static class Products {
        private final List<ProductDetailInfo> products; // ⭐ 필드 추가

        private Products(List<ProductDetailInfo> products) {
            this.products = products;
        }

        public static Products from(List<Product> productList) {
            List<ProductDetailInfo> productInfoList = new ArrayList<>();

            for (Product product : productList) {
                ProductDetailInfo info = ProductDetailInfo.from(product);
                productInfoList.add(info);
            }

            return new Products(productInfoList);
        }
    }

    /**
     * 재고 차감 결과
     */
    @Getter
    @Builder
    public static class StockDecrease {
        private final Long productId;
        private final int decreasedQuantity;
        private final int remainingStock;

        public static StockDecrease of(Long productId, int decreasedQuantity, int remainingStock) {
            return StockDecrease.builder()
                    .productId(productId)
                    .decreasedQuantity(decreasedQuantity)
                    .remainingStock(remainingStock)
                    .build();
        }
    }

    /**
     * 재고 증가 결과
     */
    @Getter
    @Builder
    public static class StockIncrease {
        private final Long productId;
        private final int increasedQuantity;
        private final int totalStock;

        public static StockIncrease of(Long productId, int increasedQuantity, int totalStock) {
            return StockIncrease.builder()
                    .productId(productId)
                    .increasedQuantity(increasedQuantity)
                    .totalStock(totalStock)
                    .build();
        }
    }
}

