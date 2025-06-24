package com.flashdeal.domain.user;

import com.flashdeal.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus;
    
    @Builder
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userStatus = UserStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.userStatus = UserStatus.INACTIVE;
    }
    
    public void activate() {
        this.userStatus = UserStatus.ACTIVE;
    }
    
    public boolean isActive() {
        return this.userStatus == UserStatus.ACTIVE;
    }
}
