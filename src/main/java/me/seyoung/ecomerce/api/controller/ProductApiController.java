package me.seyoung.ecomerce.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.seyoung.ecomerce.api.dto.*;
import me.seyoung.ecomerce.api.dto.balance.StockResponse;
import me.seyoung.ecomerce.api.dto.product.PopularProductResponse;
import me.seyoung.ecomerce.api.dto.product.ProductDetailResponse;
import me.seyoung.ecomerce.api.dto.product.ProductResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Operation(
            summary = "상품 목록 조회",
            description = """
                    상품 목록을 조회합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            )
    })
    @GetMapping
    public List<ProductResponse> getProducts(
            @Parameter(description = "카테고리 필터 (옵션)", example = "electronics")
            @RequestParam(required = false) String category,
            @Parameter(description = "정렬 기준 (newest: 최신순, price: 가격순)", example = "newest")
            @RequestParam(required = false, defaultValue = "newest") String sort
    ) {
        // 실제 서비스에서는 ProductService를 통해 데이터 조회
        return List.of(
                new ProductResponse("P001", "노트북", 1500000, 10, "electronics"),
                new ProductResponse("P002", "무선 이어폰", 200000, 35, "electronics")
        );
    }

    @Operation(
            summary = "상품 상세 정보 조회",
            description = """
                    특정 상품의 상세 정보를 조회합니다.
                    - 상품 ID, 상품명, 설명, 가격, 재고 수량, 카테고리 포함
                    - 재고 수량이 0인 경우 품절 상태 표시

                    **요구사항**: FR-1.1.2
                    """
    )
    @ApiResponse(
            responseCode = "406",
            description = "상품을 찾을 수 없음",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "상품 없음 예시",
                                    value = """
                {
                    "errorCode": "PRODUCT_NOT_FOUND",
                    "message": "상품을 찾을 수 없습니다."
                }
                """
                            )
                    }
            )
    )
    @GetMapping("/{productId}")
    public ProductDetailResponse getProductDetail(
            @Parameter(description = "상품 ID", example = "P001", required = true)
            @PathVariable String productId
    ) {
        // 실제 서비스에서는 ProductService를 통해 데이터 조회
        return new ProductDetailResponse(
                "P001",
                "노트북",
                "고성능 업무용 노트북, Intel i7 프로세서, 16GB RAM, 512GB SSD",
                1500000,
                10,
                "electronics",
                false
        );
    }

    @Operation(
            summary = "상품 재고 조회",
            description = """
                    특정 상품의 현재 재고를 실시간으로 조회합니다.
                    - 재고 수량이 0인 경우 품절 상태로 표시

                    **요구사항**: FR-1.1.3
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재고 조회 성공",
                    content = @Content(schema = @Schema(implementation = StockResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음"
            )
    })
    @GetMapping("/{productId}/stock")
    public StockResponse getProductStock(
            @Parameter(description = "상품 ID", example = "P001", required = true)
            @PathVariable String productId
    ) {
        // 실제 서비스에서는 ProductService를 통해 데이터 조회
        return new StockResponse("P001", 10, false);
    }

    @Operation(
            summary = "인기 상품 목록 조회",
            description = """
                    최근 3일간의 판매 데이터를 기반으로 인기 상품 Top 5를 조회합니다.
                    - 판매량 기준으로 정렬
                    - 상품 정보와 판매량 포함

                    **요구사항**: FR-1.3.1, FR-1.3.2
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 상품 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PopularProductResponse.class))
            )
    })
    @GetMapping("/popular")
    public List<PopularProductResponse> getPopularProducts() {
        // 실제 서비스에서는 ProductService를 통해 데이터 조회
        return List.of(
                new PopularProductResponse("P002", "무선 이어폰", 200000, 35, "electronics", 150),
                new PopularProductResponse("P001", "노트북", 1500000, 10, "electronics", 120),
                new PopularProductResponse("P005", "스마트워치", 350000, 25, "electronics", 95),
                new PopularProductResponse("P010", "키보드", 120000, 50, "electronics", 80),
                new PopularProductResponse("P015", "마우스", 45000, 100, "electronics", 75)
        );
    }
}
