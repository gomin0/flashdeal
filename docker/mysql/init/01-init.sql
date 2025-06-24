-- FlashDeal Commerce Database 초기화 스크립트

-- 데이터베이스 생성 (이미 docker-compose에서 생성되지만 확인용)
CREATE DATABASE IF NOT EXISTS flashdeal_commerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 권한 설정
GRANT ALL PRIVILEGES ON flashdeal_commerce.* TO 'flashdeal'@'%';
FLUSH PRIVILEGES;

-- 기본 설정
USE flashdeal_commerce;

-- MySQL 성능 최적화를 위한 설정들은 my.cnf에서 처리
-- 여기서는 테스트용 초기 데이터만 삽입

-- 테스트용 사용자 생성 (패스워드: password123 -> BCrypt 해시)
INSERT IGNORE INTO users (email, password, name, user_status, created_at, updated_at) 
VALUES 
('test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '테스트사용자', 'ACTIVE', NOW(), NOW()),
('admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '관리자', 'ACTIVE', NOW(), NOW());

-- 테스트용 계좌 생성 (각각 100만원씩)
INSERT IGNORE INTO accounts (user_id, balance, version, created_at, updated_at) 
VALUES 
(1, 1000000.00, 0, NOW(), NOW()),
(2, 1000000.00, 0, NOW(), NOW());

-- 테스트용 플래시 세일 상품 생성
INSERT IGNORE INTO products (
    name, original_price, sale_price, total_stock, remaining_stock, 
    sale_start_time, sale_end_time, product_status, max_quantity_per_user, 
    created_at, updated_at
) VALUES 
(
    'iPhone 15 Pro 128GB', 1500000.00, 750000.00, 100, 100,
    DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 25 HOUR),
    'UPCOMING', 2, NOW(), NOW()
),
(
    'MacBook Air M3', 1200000.00, 800000.00, 50, 50,
    NOW(), DATE_ADD(NOW(), INTERVAL 24 HOUR),
    'ON_SALE', 1, NOW(), NOW()
),
(
    'AirPods Pro 3세대', 300000.00, 150000.00, 200, 200,
    DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 23 HOUR),
    'ON_SALE', 3, NOW(), NOW()
);
