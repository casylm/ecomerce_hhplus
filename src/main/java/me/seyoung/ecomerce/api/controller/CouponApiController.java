package me.seyoung.ecomerce.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.seyoung.ecomerce.api.dto.*;
import me.seyoung.ecomerce.api.dto.coupon.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Coupon", description = "쿠폰 관련 API")
@RestController
@RequestMapping("/api/coupons")
public class CouponApiController {

    @Operation(
            summary = "쿠폰 발급 (선착순)",
            description = """
                    선착순으로 쿠폰을 발급받습니다.
                    - 한정된 수량 내에서만 발급 가능
                    - 동시성 제어를 통해 설정된 수량 초과 방지
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "쿠폰 발급 성공",
                    content = @Content(schema = @Schema(implementation = CouponIssueResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "쿠폰을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "쿠폰 발급 불가 (수량 소진 또는 중복 발급)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{couponId}/issue")
    @ResponseStatus(HttpStatus.CREATED)
    public CouponIssueResponse issueCoupon(
            @Parameter(description = "쿠폰 ID", example = "C001", required = true)
            @PathVariable String couponId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "쿠폰 발급 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CouponIssueRequest.class))
            )
            @Valid @RequestBody CouponIssueRequest request
    ) {
        // 실제 서비스에서는 CouponService를 통해 발급 처리
        // 동시성 제어 필요: 분산 락 또는 DB 락 활용
        return new CouponIssueResponse(
                couponId,
                request.userId(),
                "신규 회원 할인 쿠폰",
                5000,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );
    }

    @Operation(
            summary = "사용자 쿠폰 목록 조회",
            description = """
                    고객이 보유한 쿠폰 목록을 조회합니다.
                    - 사용 가능/사용 완료/만료 상태 구분
                    - 발급 일시, 사용 일시, 만료 일시 포함
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/users/{userId}")
    public List<CouponResponse> getUserCoupons(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable Long userId,
            @Parameter(description = "쿠폰 상태 (AVAILABLE, USED, EXPIRED)", example = "AVAILABLE")
            @RequestParam(required = false) String status
    ) {
        // 실제 서비스에서는 CouponService를 통해 데이터 조회
        return List.of(
                new CouponResponse(
                        "C001",
                        "신규 회원 할인 쿠폰",
                        5000,
                        "AVAILABLE",
                        LocalDateTime.now().minusDays(1),
                        null,
                        LocalDateTime.now().plusDays(29)
                ),
                new CouponResponse(
                        "C002",
                        "10% 할인 쿠폰",
                        10000,
                        "USED",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().plusDays(25)
                )
        );
    }

    @Operation(
            summary = "쿠폰 사용",
            description = """
                    결제 시 쿠폰을 사용합니다.
                    - 유효성 검증: 유효기간, 사용 여부
                    - 사용된 쿠폰은 재사용 불가
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 사용 성공",
                    content = @Content(schema = @Schema(implementation = CouponUseResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 쿠폰)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "쿠폰을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "쿠폰 사용 불가 (이미 사용됨 또는 만료됨)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{userCouponId}/use")
    public CouponUseResponse useCoupon(
            @Parameter(description = "사용자 쿠폰 ID", example = "1001", required = true)
            @PathVariable Long userCouponId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "쿠폰 사용 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CouponUseRequest.class))
            )
            @Valid @RequestBody CouponUseRequest request
    ) {
        // 실제 서비스에서는 CouponService를 통해 사용 처리
        // 유효성 검증:
        // 1. 사용 여부 확인 (재사용 방지)
        // 2. 유효기간 확인
        int discountAmount = 5000;
        return new CouponUseResponse(
                "C001",
                discountAmount,
                request.orderAmount(),
                request.orderAmount() - discountAmount,
                LocalDateTime.now()
        );
    }
}
