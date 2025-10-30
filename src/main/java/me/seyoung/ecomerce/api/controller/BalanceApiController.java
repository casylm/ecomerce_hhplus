package me.seyoung.ecomerce.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.seyoung.ecomerce.api.dto.ErrorResponse;
import me.seyoung.ecomerce.api.dto.balance.BalanceResponse;
import me.seyoung.ecomerce.api.dto.balance.ChargePointRequest;
import me.seyoung.ecomerce.api.dto.balance.ChargePointResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Balance", description = "잔액 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/balances")
public class BalanceApiController {

    @Operation(
            summary = "잔액 조회",
            description = """
                    사용자의 현재 잔액을 조회합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "잔액 조회 성공",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{userId}")
    public BalanceResponse getBalance(
            @Parameter(description = "사용자 ID", example = "1", required = true)
            @PathVariable Long userId
    ) {
        // 실제 서비스에서는 BalanceService를 통해 데이터 조회
        return new BalanceResponse(userId, 50000L);
    }

    @Operation(
            summary = "잔액 충전",
            description = """
                    사용자의 잔액을 충전합니다.
                    - 충전 금액은 양수여야 함
                    - 충전 한도를 넘을 수 없음
                    - 충전 이력이 관리됨
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "잔액 충전 성공",
                    content = @Content(schema = @Schema(implementation = ChargePointResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 충전 금액)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "405",
                    description = "충전 한도 초과",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/charge")
    public ChargePointResponse chargePoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "잔액 충전 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChargePointRequest.class))
            )
            @Valid @RequestBody ChargePointRequest request
    ) {
        // 실제 서비스에서는 BalanceService를 통해 충전
        Long newBalance = 50000L + request.amount();
        return new ChargePointResponse(request.userId(), newBalance, request.amount());
    }
}
