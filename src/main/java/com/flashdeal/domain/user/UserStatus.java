package com.flashdeal.domain.user;

public enum UserStatus {
    ACTIVE("활성"),
    INACTIVE("비활성");
    
    private final String description;
    
    UserStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
