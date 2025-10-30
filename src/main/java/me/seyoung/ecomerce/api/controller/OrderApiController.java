package me.seyoung.ecomerce.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.seyoung.ecomerce.api.dto.ErrorResponse;
import me.seyoung.ecomerce.api.dto.order.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Order", description = "주문 및 결제 관리 API")
@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Operation(
            summary = "주문 생성",
            description = """
                    주문을 생성합니다.
                    - 주문 생성 시점에 재고 확인 필수
                    - 재고가 자동으로 차감됨
                    - 재고 부족 시 주문 생성 실패
                    - 동시성 제어를 통한 재고 정합성 보장
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "주문 생성 성공",
                    content = @Content(schema = @Schema(implementation = OrderCreateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (필수 정보 누락 또는 유효하지 않은 값)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 상품을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "재고 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreateResponse createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "주문 생성 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderCreateRequest.class))
            )
            @Valid @RequestBody OrderCreateRequest request
    ) {
        // 실제 서비스에서는 OrderService를 통해 주문 생성
        // 1. 재고 확인
        // 2. 재고 차감 (동시성 제어: 비관적 락 또는 낙관적 락)
        // 3. 주문 생성
        // 4. 실패 시 트랜잭션 롤백
        return new OrderCreateResponse(
                "ORD001",
                request.userId(),
                List.of(
                        new OrderItemResponse(1L, "P001", "노트북", 1500000, 2, 3000000)
                ),
                3000000,
                "PENDING",
                LocalDateTime.now()
        );
    }

    @Operation(
            summary = "주문 상세 조회",
            description = """
                    특정 주문의 상세 정보를 조회합니다.
                    - 주문 ID, 주문 일시, 주문 상품 목록, 총액, 상태 포함
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 조회 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @Parameter(description = "주문 ID", example = "ORD001", required = true)
            @PathVariable String orderId
    ) {
        // 실제 서비스에서는 OrderService를 통해 데이터 조회
        return new OrderResponse(
                orderId,
                1L,
                List.of(
                        new OrderItemResponse(1L, "P001", "노트북", 1500000, 2, 3000000)
                ),
                3000000,
                5000,
                2995000,
                "PAID",
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5)
        );
    }

    @Operation(
            summary = "주문 결제",
            description = """
                    주문에 대한 결제를 처리합니다.
                    - 잔액 기반 결제 처리
                    - 잔액 부족 시 결제 실패
                    - 쿠폰 할인 적용 가능 (선택)
                    - 결제 완료 후 주문 상태 업데이트 (PENDING → PAID)
                    - 결제 실패 시 차감된 재고 복구
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "결제 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (이미 결제된 주문, 쿠폰 조건 미충족 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문 또는 쿠폰을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "잔액 부족",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{orderId}/payment")
    public PaymentResponse processPayment(
            @Parameter(description = "주문 ID", example = "ORD001", required = true)
            @PathVariable String orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "결제 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentRequest.class))
            )
            @Valid @RequestBody PaymentRequest request
    ) {
        // 실제 서비스에서는 PaymentService를 통해 결제 처리
        // 트랜잭션 관리:
        // 1. 주문 조회 및 상태 확인
        // 2. 쿠폰 유효성 검증 (선택)
        // 3. 할인 금액 계산
        // 4. 잔액 확인
        // 5. 잔액 차감
        // 6. 쿠폰 사용 처리
        // 7. 주문 상태 업데이트
        // 8. 결제 이력 저장
        // 실패 시: 재고 복구, 트랜잭션 롤백
        int originalAmount = 3000000;
        int discountAmount = 5000;
        int finalAmount = originalAmount - discountAmount;

        return new PaymentResponse(
                orderId,
                request.userId(),
                originalAmount,
                discountAmount,
                finalAmount,
                3000000L - finalAmount,
                "PAID",
                LocalDateTime.now()
        );
    }
}
