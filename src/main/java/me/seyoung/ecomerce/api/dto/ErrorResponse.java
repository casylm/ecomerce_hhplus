package me.seyoung.ecomerce.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답 정보")
public enum ErrorResponse {
        // 사용자 관련
        USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
        DUPLICATE_USER("DUPLICATE_USER", "이미 존재하는 사용자입니다."),

        // 쿠폰 관련
        COUPON_NOT_FOUND("COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."),
        COUPON_OUT_OF_STOCK("COUPON_OUT_OF_STOCK", "쿠폰 수량이 모두 소진되었습니다."),
        COUPON_ALREADY_ISSUED("COUPON_ALREADY_ISSUED", "이미 발급받은 쿠폰입니다."),
        COUPON_ALREADY_USED("COUPON_ALREADY_USED", "이미 사용된 쿠폰입니다."),
        COUPON_EXPIRED("COUPON_EXPIRED", "만료된 쿠폰입니다."),
        COUPON_INVALID_OWNER("COUPON_INVALID_OWNER", "쿠폰 소유자가 일치하지 않습니다."),
        COUPON_MIN_AMOUNT_NOT_MET("COUPON_MIN_AMOUNT_NOT_MET", "쿠폰 사용 최소 금액을 충족하지 못했습니다."),

        // 상품 관련
        PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND","상품을 찾을 수 없습니다."),
        PRODUCT_OUT_OF_STOCK("PRODUCT_OUT_OF_STOCK", "상품 재고가 부족합니다."),

        // 주문 관련
        ORDER_NOT_FOUND("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
        ORDER_ALREADY_PAID("ORDER_ALREADY_PAID", "이미 결제된 주문입니다."),
        ORDER_ALREADY_CANCELLED("ORDER_ALREADY_CANCELLED", "이미 취소된 주문입니다."),

        // 결제 관련
        PAYMENT_FAILED("PAYMENT_FAILED", "결제에 실패했습니다."),
        INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "잔액이 부족합니다."),

        // 포인트 충전 관련
        Charge_Max_Point("Charge_Max_Point","충전 한도를 초과했습니다"),

        // 시스템 공통
        INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

        private final String code;
        private final String message;

        ErrorResponse(String code, String message) {
                this.code = code;
                this.message = message;
        }

        public String getCode() {
                return code;
        }

        public String getMessage() {
                return message;
        }
}