# E-Commerce API 설계 명세서

## 목차
1. [개요](#개요)
2. [공통 사항](#공통-사항)
3. [API 엔드포인트](#api-엔드포인트)
   - [1. 잔액 관리 API](#1-잔액-관리-api)
   - [2. 상품 관리 API](#2-상품-관리-api)
   - [3. 쿠폰 관리 API](#3-쿠폰-관리-api)
4. [에러 코드](#에러-코드)

---

## 개요

본 문서는 E-Commerce 플랫폼의 RESTful API 설계 명세를 정의합니다.

### API 기본 정보
- **Base URL**: `http://localhost:8080`
- **Protocol**: HTTP/HTTPS
- **Data Format**: JSON
- **Character Encoding**: UTF-8

---

## 공통 사항

### HTTP 상태 코드

| 상태 코드 | 설명 | 사용 케이스 |
|---------|------|------------|
| `200 OK` | 요청 성공 | 조회, 수정 성공 |
| `201 Created` | 리소스 생성 성공 | 등록 성공 |
| `400 Bad Request` | 잘못된 요청 | 유효하지 않은 입력값 |
| `403 Forbidden` | 권한 없음 | 관리자 권한 필요 |
| `404 Not Found` | 리소스 없음 | 존재하지 않는 리소스 조회 |
| `405 Method Not Allowed` | 비즈니스 규칙 위반 | 한도 초과 등 |
| `406 Not Acceptable` | 상품 없음 | 상품을 찾을 수 없음 |
| `409 Conflict` | 리소스 충돌 | 중복 발급, 이미 사용됨 등 |
| `500 Internal Server Error` | 서버 오류 | 예상치 못한 서버 오류 |

### 공통 응답 형식

#### 성공 응답
```json
{
  "필드명": "값"
}
```

#### 에러 응답
```json
{
  "errorCode": "ERROR_CODE",
  "message": "에러 메시지"
}
```

---

## API 엔드포인트

### 1. 잔액 관리 API

#### 1.1 잔액 조회

사용자의 현재 잔액을 조회합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `GET /api/points/{userId}` |
| **Description** | 사용자의 현재 잔고를 조회합니다 |
| **Authentication** | Required |
| **권한** | Customer |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `userId` | Long | Y | 사용자 ID | `1` |

**Response (200 OK)**
```json
{
  "userId": 1,
  "balance": 50000
}
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `userId` | Long | 사용자 ID | `1` |
| `balance` | Long | 현재 잔액 | `50000` |

**Error Responses**

| 상태 코드 | 에러 코드 | 설명 |
|----------|----------|------|
| `404` | `USER_NOT_FOUND` | 사용자를 찾을 수 없습니다 |

---

#### 1.2 잔액 충전

사용자의 잔액을 충전합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `POST /api/points/charge` |
| **Description** | 입력받은 금액만큼 잔액을 충전합니다 |

**Request Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `userId` | Long | Y | 사용자 ID | `1` |
| `point` | Long | Y | 충전 금액 (양수) | `10000` |

**Request Example**
```
POST /api/points/charge?userId=1&point=10000
```

**Response (200 OK)**
```json
{
  "userId": 1,
  "balance": 60000
}
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `userId` | Long | 사용자 ID | `1` |
| `balance` | Long | 충전 후 잔액 | `60000` |

**Error Responses**

| 상태 코드 | 에러 코드 | 설명 |
|----------|----------|------|
| `404` | `USER_NOT_FOUND` | 사용자를 찾을 수 없습니다 |
| `405` | `Charge_Max_Point` | 충전 한도를 초과했습니다 |

**비즈니스 규칙**
- 충전 금액은 양수여야 함
- 충전 이력이 관리됨
- 동시 충전 요청 시 순차 처리

---

### 2. 상품 관리 API

#### 2.1 상품 목록 조회

상품 목록을 조회합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `GET /api/products` |
| **Description** | 상품 목록을 조회합니다 (카테고리 가능) |

**Query Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 | 기본값 |
|---------|------|------|------|------|-------|
| `category` | String | N | 카테고리 필터 | `electronics` | - |

**Request Example**
```
GET /api/products?category=electronics&sort=price
```

**Response (200 OK)**
```json
[
  {
    "productId": "P001",
    "name": "노트북",
    "price": 1500000,
    "stock": 10,
    "category": "electronics"
  },
  {
    "productId": "P002",
    "name": "무선 이어폰",
    "price": 200000,
    "stock": 35,
    "category": "electronics"
  }
]
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `productId` | String | 상품 ID | `P001` |
| `name` | String | 상품명 | `노트북` |
| `price` | Integer | 가격 | `1500000` |
| `stock` | Integer | 재고 수량 | `10` |
| `category` | String | 카테고리 | `electronics` |

---

#### 2.2 상품 상세 정보 조회

특정 상품의 상세 정보를 조회합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `GET /api/products/{productId}` |
| **Description** | 특정 상품의 상세 정보를 조회합니다 |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `productId` | String | Y | 상품 ID | `P001` |

**Request Example**
```
GET /api/products/P001
```

**Response (200 OK)**
```json
{
  "productId": "P001",
  "name": "노트북",
  "description": "고성능 업무용 노트북, Intel i7 프로세서, 16GB RAM, 512GB SSD",
  "price": 1500000,
  "stock": 10,
  "category": "electronics",
  "soldOut": false
}
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `productId` | String | 상품 ID | `P001` |
| `name` | String | 상품명 | `노트북` |
| `description` | String | 상품 설명 | `고성능 업무용 노트북...` |
| `price` | Integer | 가격 | `1500000` |
| `stock` | Integer | 재고 수량 | `10` |
| `category` | String | 카테고리 | `electronics` |
| `soldOut` | Boolean | 품절 여부 | `false` |

**Error Responses**

| 상태 코드 | 에러 코드 | 설명 |
|----------|----------|------|
| `406` | `PRODUCT_NOT_FOUND` | 상품을 찾을 수 없습니다 |

**비즈니스 규칙**
- 재고 수량이 0인 경우 `soldOut`이 `true`로 설정됨

---

#### 2.3 상품 재고 조회

특정 상품의 현재 재고를 실시간으로 조회합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `GET /api/products/{productId}/stock` |
| **Description** | 특정 상품의 현재 재고를 실시간으로 조회합니다 |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `productId` | String | Y | 상품 ID | `P001` |

**Request Example**
```
GET /api/products/P001/stock
```

**Response (200 OK)**
```json
{
  "productId": "P001",
  "stock": 10,
  "soldOut": false
}
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `productId` | String | 상품 ID | `P001` |
| `stock` | Integer | 재고 수량 | `10` |
| `soldOut` | Boolean | 품절 여부 | `false` |

**Error Responses**

| 상태 코드 | 에러 코드 | 설명 |
|----------|----------|------|
| `404` | `PRODUCT_NOT_FOUND` | 상품을 찾을 수 없습니다 |

**비즈니스 규칙**
- 재고 수량이 0인 경우 `soldOut`이 `true`로 설정됨
- 실시간 재고 반영

---

#### 2.4 인기 상품 목록 조회

최근 3일간 판매량 기준 인기 상품 Top 5를 조회합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `GET /api/products/popular` |
| **Description** | 최근 3일간의 판매 데이터를 기반으로 인기 상품 Top 5를 조회합니다 |

**Request Example**
```
GET /api/products/popular
```

**Response (200 OK)**
```json
[
  {
    "productId": "P002",
    "name": "무선 이어폰",
    "price": 200000,
    "stock": 35,
    "category": "electronics",
    "salesCount": 150
  },
  {
    "productId": "P001",
    "name": "노트북",
    "price": 1500000,
    "stock": 10,
    "category": "electronics",
    "salesCount": 120
  },
  {
    "productId": "P005",
    "name": "스마트워치",
    "price": 350000,
    "stock": 25,
    "category": "electronics",
    "salesCount": 95
  },
  {
    "productId": "P010",
    "name": "키보드",
    "price": 120000,
    "stock": 50,
    "category": "electronics",
    "salesCount": 80
  },
  {
    "productId": "P015",
    "name": "마우스",
    "price": 45000,
    "stock": 100,
    "category": "electronics",
    "salesCount": 75
  }
]
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `productId` | String | 상품 ID | `P002` |
| `name` | String | 상품명 | `무선 이어폰` |
| `price` | Integer | 가격 | `200000` |
| `stock` | Integer | 재고 수량 | `35` |
| `category` | String | 카테고리 | `electronics` |
| `salesCount` | Integer | 판매량 (최근 3일) | `150` |

**비즈니스 규칙**
- 최근 3일간의 판매 데이터 기반
- 판매량 기준 내림차순 정렬
- 최대 5개 상품 반환
- 주기적 갱신 (캐싱 활용)

---

### 3. 쿠폰 관리 API

#### 3.1 쿠폰 발급 (선착순)

선착순으로 쿠폰을 발급받습니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `POST /api/coupons/{couponId}/issue` |
| **Description** | 선착순으로 쿠폰을 발급받습니다 |
| **권한** | Customer |
| **요구사항** | FR-3.2.1, FR-3.2.2, FR-3.2.3 |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `couponId` | String | Y | 쿠폰 ID | `C001` |

**Request Body**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|-----|------|------|------|------|
| `userId` | Long | Y | 사용자 ID | `1` |

**Request Example**
```json
POST /api/coupons/C001/issue
Content-Type: application/json

{
  "userId": 1
}
```

**Response (201 Created)**
```json
{
  "userCouponId": 1001,
  "couponId": "C001",
  "userId": 1,
  "couponName": "신규 회원 할인 쿠폰",
  "discountAmount": 5000,
  "issuedAt": "2025-10-30T10:00:00",
  "expiresAt": "2025-11-30T23:59:59"
}
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `userCouponId` | Long | 사용자 쿠폰 ID | `1001` |
| `couponId` | String | 쿠폰 ID | `C001` |
| `userId` | Long | 사용자 ID | `1` |
| `couponName` | String | 쿠폰명 | `신규 회원 할인 쿠폰` |
| `discountAmount` | Integer | 할인 금액 | `5000` |
| `issuedAt` | DateTime | 발급 일시 | `2025-10-30T10:00:00` |
| `expiresAt` | DateTime | 만료 일시 | `2025-11-30T23:59:59` |

**Error Responses**

| 상태 코드 | 에러 코드 | 설명 |
|----------|----------|------|
| `400` | `(다양)` | 잘못된 요청 |
| `404` | `COUPON_NOT_FOUND` | 쿠폰을 찾을 수 없습니다 |
| `409` | `COUPON_OUT_OF_STOCK` | 쿠폰 수량이 모두 소진되었습니다 |
| `409` | `COUPON_ALREADY_ISSUED` | 이미 발급받은 쿠폰입니다 |

**비즈니스 규칙**
- 한정된 수량 내에서만 발급 가능
- 동시성 제어를 통해 설정된 수량 초과 방지 (분산 락 또는 DB 락)
- 고객당 발급 개수 제한 (중복 발급 방지)
- Race Condition 방지 필수

---

#### 3.2 사용자 쿠폰 목록 조회

고객이 보유한 쿠폰 목록을 조회합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `GET /api/coupons/users/{userId}` |
| **Description** | 고객이 보유한 쿠폰 목록을 조회합니다 |
| **권한** | Customer |
| **요구사항** | FR-3.3.1 |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `userId` | Long | Y | 사용자 ID | `1` |

**Query Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 | 기본값 |
|---------|------|------|------|------|-------|
| `status` | String | N | 쿠폰 상태 필터 (AVAILABLE, USED, EXPIRED) | `AVAILABLE` | - |

**Request Example**
```
GET /api/coupons/users/1?status=AVAILABLE
```

**Response (200 OK)**
```json
[
  {
    "userCouponId": 1001,
    "couponId": "C001",
    "couponName": "신규 회원 할인 쿠폰",
    "discountAmount": 5000,
    "status": "AVAILABLE",
    "issuedAt": "2025-10-29T10:00:00",
    "usedAt": null,
    "expiresAt": "2025-11-29T23:59:59"
  },
  {
    "userCouponId": 1002,
    "couponId": "C002",
    "couponName": "10% 할인 쿠폰",
    "discountAmount": 10000,
    "status": "USED",
    "issuedAt": "2025-10-25T10:00:00",
    "usedAt": "2025-10-28T14:30:00",
    "expiresAt": "2025-11-25T23:59:59"
  }
]
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `userCouponId` | Long | 사용자 쿠폰 ID | `1001` |
| `couponId` | String | 쿠폰 ID | `C001` |
| `couponName` | String | 쿠폰명 | `신규 회원 할인 쿠폰` |
| `discountAmount` | Integer | 할인 금액 | `5000` |
| `status` | String | 쿠폰 상태 (AVAILABLE, USED, EXPIRED) | `AVAILABLE` |
| `issuedAt` | DateTime | 발급 일시 | `2025-10-29T10:00:00` |
| `usedAt` | DateTime | 사용 일시 (nullable) | `2025-10-28T14:30:00` |
| `expiresAt` | DateTime | 만료 일시 | `2025-11-29T23:59:59` |

**Error Responses**

| 상태 코드 | 에러 코드 | 설명 |
|----------|----------|------|
| `404` | `USER_NOT_FOUND` | 사용자를 찾을 수 없습니다 |

**비즈니스 규칙**
- 사용 가능/사용 완료/만료 상태 구분
- 발급 일시, 사용 일시, 만료 일시 포함

---

#### 3.3 쿠폰 사용

결제 시 쿠폰을 사용합니다.

| 항목 | 내용 |
|-----|------|
| **Endpoint** | `POST /api/coupons/{userCouponId}/use` |
| **Description** | 결제 시 쿠폰을 사용합니다 |
| **권한** | Customer |
| **요구사항** | FR-3.3.2, FR-3.3.3, FR-3.3.4 |

**Path Parameters**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---------|------|------|------|------|
| `userCouponId` | Long | Y | 사용자 쿠폰 ID | `1001` |

**Request Body**

| 필드 | 타입 | 필수 | 설명 | 예시 |
|-----|------|------|------|------|
| `userId` | Long | Y | 사용자 ID | `1` |
| `orderId` | String | Y | 주문 ID | `ORD001` |
| `orderAmount` | Integer | Y | 주문 금액 | `50000` |

**Request Example**
```json
POST /api/coupons/1001/use
Content-Type: application/json

{
  "userId": 1,
  "orderId": "ORD001",
  "orderAmount": 50000
}
```

**Response (200 OK)**
```json
{
  "userCouponId": 1001,
  "couponId": "C001",
  "discountAmount": 5000,
  "originalAmount": 50000,
  "finalAmount": 45000,
  "usedAt": "2025-10-30T14:30:00"
}
```

**Response Fields**

| 필드 | 타입 | 설명 | 예시 |
|-----|------|------|------|
| `userCouponId` | Long | 사용자 쿠폰 ID | `1001` |
| `couponId` | String | 쿠폰 ID | `C001` |
| `discountAmount` | Integer | 할인 금액 | `5000` |
| `originalAmount` | Integer | 원래 주문 금액 | `50000` |
| `finalAmount` | Integer | 할인 후 금액 | `45000` |
| `usedAt` | DateTime | 사용 일시 | `2025-10-30T14:30:00` |

**Error Responses**

| 상태 코드 | 에러 코드 | 설명 |
|----------|----------|------|
| `400` | `COUPON_MIN_AMOUNT_NOT_MET` | 쿠폰 사용 최소 금액을 충족하지 못했습니다 |
| `400` | `COUPON_INVALID_OWNER` | 쿠폰 소유자가 일치하지 않습니다 |
| `404` | `COUPON_NOT_FOUND` | 쿠폰을 찾을 수 없습니다 |
| `409` | `COUPON_ALREADY_USED` | 이미 사용된 쿠폰입니다 |
| `409` | `COUPON_EXPIRED` | 만료된 쿠폰입니다 |

**비즈니스 규칙**
- 유효성 검증: 유효기간, 사용 여부, 소유자 확인
- 사용 조건 검증: 최소 주문 금액 등
- 사용된 쿠폰은 재사용 불가
- 사용 일시 및 주문 ID 기록

---

## 에러 코드

### 에러 응답 형식

모든 에러 응답은 아래 형식을 따릅니다:

```json
{
  "errorCode": "ERROR_CODE",
  "message": "에러 메시지"
}
```

### 에러 코드 목록

#### 사용자 관련 에러

| 에러 코드 | HTTP 상태 코드 | 메시지 | 설명 |
|----------|---------------|--------|------|
| `USER_NOT_FOUND` | 404 | 사용자를 찾을 수 없습니다 | 존재하지 않는 사용자 ID |
| `DUPLICATE_USER` | 400 | 이미 존재하는 사용자입니다 | 사용자 ID 중복 |

#### 쿠폰 관련 에러

| 에러 코드 | HTTP 상태 코드 | 메시지 | 설명 |
|----------|---------------|--------|------|
| `COUPON_NOT_FOUND` | 404 | 쿠폰을 찾을 수 없습니다 | 존재하지 않는 쿠폰 ID |
| `COUPON_OUT_OF_STOCK` | 409 | 쿠폰 수량이 모두 소진되었습니다 | 선착순 수량 소진 |
| `COUPON_ALREADY_ISSUED` | 409 | 이미 발급받은 쿠폰입니다 | 중복 발급 시도 |
| `COUPON_ALREADY_USED` | 409 | 이미 사용된 쿠폰입니다 | 재사용 방지 |
| `COUPON_EXPIRED` | 409 | 만료된 쿠폰입니다 | 유효기간 초과 |
| `COUPON_INVALID_OWNER` | 400 | 쿠폰 소유자가 일치하지 않습니다 | 타인의 쿠폰 사용 시도 |
| `COUPON_MIN_AMOUNT_NOT_MET` | 400 | 쿠폰 사용 최소 금액을 충족하지 못했습니다 | 최소 주문 금액 미충족 |

#### 상품 관련 에러

| 에러 코드 | HTTP 상태 코드 | 메시지 | 설명 |
|----------|---------------|--------|------|
| `PRODUCT_NOT_FOUND` | 404, 406 | 상품을 찾을 수 없습니다 | 존재하지 않는 상품 ID |

#### 결제 관련 에러

| 에러 코드 | HTTP 상태 코드 | 메시지 | 설명 |
|----------|---------------|--------|------|
| `PAYMENT_FAILED` | 400 | 결제에 실패했습니다 | 결제 처리 실패 |
| `INSUFFICIENT_BALANCE` | 400 | 잔액이 부족합니다 | 사용자 잔액 부족 |

#### 포인트 충전 관련 에러

| 에러 코드 | HTTP 상태 코드 | 메시지 | 설명 |
|----------|---------------|--------|------|
| `Charge_Max_Point` | 405 | 충전 한도를 초과했습니다 | 1회 충전 한도 초과 |

#### 시스템 공통 에러

| 에러 코드 | HTTP 상태 코드 | 메시지 | 설명 |
|----------|---------------|--------|------|
| `INTERNAL_SERVER_ERROR` | 500 | 서버 내부 오류가 발생했습니다 | 예상치 못한 서버 오류 |

---

## 부록

### A. RESTful API 설계 원칙

본 API는 다음 RESTful 원칙을 준수합니다:

1. **리소스 기반 URL 설계**
   - 명사형 URL 사용 (`/api/products`, `/api/points`, `/api/coupons`, `/api/orders`)
   - 계층 구조 표현 (`/api/products/{productId}/stock`, `/api/points/{userId}`, `/api/coupons/users/{userId}`)

2. **HTTP 메소드 활용**
   - `GET`: 조회
   - `POST`: 생성
   - `PUT`: 전체 수정
   - `PATCH`: 부분 수정
   - `DELETE`: 삭제

3. **상태 코드 활용**
   - 적절한 HTTP 상태 코드 사용
   - 일관된 에러 응답 형식

4. **무상태성 (Stateless)**
   - 각 요청은 독립적