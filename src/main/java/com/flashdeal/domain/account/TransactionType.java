package com.flashdeal.domain.account;

public enum TransactionType {
    WITHDRAWAL("출금"),
    REFUND("환불");
    
    private final String description;
    
    TransactionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
