create table coupons
(
    coupon_id       bigint auto_increment
        primary key,
    discount_amount int          not null,
    issued_at       datetime(6)  not null,
    name            varchar(255) not null,
    quantity        int          not null
);

create table point_histories
(
    id          bigint auto_increment
        primary key,
    amount      bigint                 not null,
    occurred_at datetime(6)            not null,
    type        enum ('CHARGE', 'USE') not null,
    user_id     bigint                 not null
);

create table points
(
    id         bigint auto_increment
        primary key,
    balance    bigint      not null,
    updated_at datetime(6) not null,
    user_id    bigint      not null
);

create table products
(
    id       bigint auto_increment
        primary key,
    category varchar(255) not null,
    name     varchar(255) not null,
    price    bigint       not null,
    stock    int          not null
);

create table user
(
    user_id    bigint auto_increment
        primary key,
    name       varchar(100)                       null,
    email      varchar(255)                       not null,
    created_at datetime default CURRENT_TIMESTAMP null,
    constraint email
        unique (email)
);

create table user_coupons
(
    user_coupon_id bigint auto_increment
        primary key,
    coupon_id      bigint      not null,
    issued_at      datetime(6) not null,
    used           bit         not null,
    used_at        datetime(6) null,
    user_id        bigint      not null
);

create table users
(
    user_id    bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    name       varchar(255) not null
);

-- USERS, PRODUCTS, COUPONS 테이블은 이미 존재한다고 가정합니다.

-- 1) orders 테이블
CREATE TABLE orders (
                        order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        total_amount DECIMAL(15, 2),
                        status ENUM('CREATED', 'PAID', 'CANCELLED') DEFAULT 'CREATED',
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 2) order_items 테이블
CREATE TABLE order_items (
                             order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INT,
                             unit_price DECIMAL(15, 2),
                             subtotal DECIMAL(15, 2),
                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id) REFERENCES orders(order_id)

);

-- 3) payments 테이블
CREATE TABLE payments (
                          payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          order_id BIGINT NOT NULL UNIQUE,
                          user_id BIGINT NOT NULL,
                          coupon_id BIGINT,
                          amount_paid DECIMAL(15, 2),
                          status ENUM('SUCCESS', 'FAIL') DEFAULT 'SUCCESS',
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_payments_order
                              FOREIGN KEY (order_id) REFERENCES orders(order_id),
                          CONSTRAINT fk_payments_user
                              FOREIGN KEY (user_id) REFERENCES users(user_id),
                          CONSTRAINT fk_payments_coupon
                              FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id)
);

-- --------------------------
-- 추천 인덱스 (조회 성능 최적화)
-- --------------------------
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_payments_user_id ON payments(user_id);


