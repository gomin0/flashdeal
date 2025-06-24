package com.flashdeal.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    
    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다."),
    PRODUCT_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "판매중인 상품이 아닙니다."),
    
    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_PAYMENT_TIMEOUT(HttpStatus.BAD_REQUEST, "결제 시간이 초과되었습니다."),
    EXCEEDED_MAX_QUANTITY(HttpStatus.BAD_REQUEST, "최대 구매 수량을 초과했습니다."),
    
    // Payment
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제에 실패했습니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다.");
    
    private final HttpStatus status;
    private final String message;
    
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
}
