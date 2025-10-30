# 시퀀스 다이어그램 문서

> 이커머스 플랫폼의 핵심 비즈니스 로직 흐름을 시각화한 문서입니다.

## 📑 목차

- [1. 개요](#1-개요)
- [2. 포인트 관리](#2-포인트-관리)
  - [2.1 잔액 조회](#21-잔액-조회)
  - [2.2 잔액 충전](#22-잔액-충전)
- [3. 상품 관리](#3-상품-관리)
  - [3.1 상품 조회](#31-상품-조회)
- [4. 쿠폰 관리](#4-쿠폰-관리)
  - [4.1 선착순 쿠폰 목록 조회](#41-선착순-쿠폰-목록-조회)
- [5. 주문 관리](#5-주문-관리)
  - [5.1 주문 생성](#51-주문-생성)
- [6. 결제 관리](#6-결제-관리)
  - [6.1 결제 처리](#61-결제-처리)

---

## 1. 개요

본 문서는 이커머스 시스템의 주요 API 엔드포인트에 대한 시퀀스 다이어그램을 제공합니다. 각 다이어그램은 클라이언트 요청부터 응답까지의 전체 흐름과 컴포넌트 간 상호작용을 보여줍니다.

### 문서 구성

- **시퀀스 다이어그램**: Mermaid 형식으로 작성된 플로우 차트
- **API 설명**: 각 엔드포인트의 목적과 역할
- **주요 로직**: 핵심 비즈니스 로직 및 검증 규칙
- **예외 처리**: 발생 가능한 오류 상황 및 처리 방법
- **입출력 명세**: 요청/응답 파라미터 정보

---

## 2. 포인트 관리

사용자의 포인트 잔액을 관리하는 기능들입니다. 포인트는 결제 시 사용되며, 충전을 통해 증액할 수 있습니다.

### 2.1 잔액 조회

```mermaid
sequenceDiagram
    participant User
    participant PointController
    participant PointService

    User->>+PointController: 잔액 조회 요청(userId)
    PointController->>+PointService: 사용자 잔액 조회(userId)

    alt 잔액이 존재함
        PointService-->>PointController: 잔액 반환(balance)
    else 잔액이 없음
        PointService-->>PointController: 잔액 0 반환(0)
    end

    PointService-->>-PointController: 서비스 응답 완료
    PointController-->>-User: 잔액 응답(balance or 0)
```

---

### 2.2 잔액 충전

#### 시퀀스 다이어그램

```mermaid
sequenceDiagram
    participant User
    participant PointController
    participant PointService

    User->>+PointController: 포인트 충전 요청(userId, amount)
    PointController->>+PointService: 충전 로직 실행(userId, amount)

    alt 충전 금액이 최대 한도 이하
        PointService-->>PointController: 충전 완료(잔액 갱신)
    else 최대 충전 한도 초과
        PointService-->>PointController: 에러 반환(MaxChargeLimitExceededException)
    end

    PointService-->>-PointController: 서비스 처리 완료
    PointController-->>-User: 결과 응답(정상 or 에러 메시지)
```

---

## 3. 상품 관리

```mermaid
sequenceDiagram
    participant User
    participant ProductController
    participant ProductService

    User->>+ProductController: 상품 정보 조회 요청(productId)
    ProductController->>+ProductService: 상품 정보 조회(productId)

    alt 상품 존재함
        ProductService-->>ProductController: 상품 정보 반환(이름, 가격, 잔여수량)
    else 상품 없음
        ProductService-->>ProductController: 오류 반환(ProductNotFoundException)
    end

    ProductService-->>-ProductController: 서비스 처리 완료
    ProductController-->>-User: 조회 결과 응답(이름, 가격, 잔여수량 or 에러 메시지)
```

---

## 4. 쿠폰 관리

```mermaid
sequenceDiagram
    participant User
    participant CouponController
    participant CouponService

    User->>+CouponController: 선착순 쿠폰 목록 조회 요청
    CouponController->>+CouponService: 발급 가능 쿠폰 목록 조회()

    alt 발급 가능 쿠폰 존재
        CouponService-->>CouponController: 쿠폰 목록 반환([coupon...])
    else 발급 가능 쿠폰 없음
        CouponService-->>CouponController: 빈 목록 반환([])
    end

    CouponService-->>-CouponController: 서비스 처리 완료
    CouponController-->>-User: 조회 결과 응답(목록/빈 목록)
```

---

## 5. 주문 관리

사용자의 상품 주문 생성 및 관리 기능입니다.

### 5.1 주문 생성

```mermaid
sequenceDiagram
    participant User
    participant OrderController
    participant OrderService
    participant OrderRepository
    participant OrderHistoryRepository

    User->>+OrderController: 주문 생성 요청(userId, productId, quantity)
    OrderController->>+OrderService: 주문 처리 요청(userId, productId, quantity)

    OrderService->>+OrderRepository: 주문 데이터 저장(save)
    OrderRepository-->>OrderService: 저장된 주문 반환(orderId, status)

    OrderService->>+OrderHistoryRepository: 주문 내역 저장(saveHistory)
    OrderHistoryRepository-->>OrderService: 내역 저장 완료

    OrderService-->>OrderController: 주문 생성 결과 반환(orderId, status)
    OrderController-->>-User: 주문 생성 응답(orderId, status)
```

---

## 6. 결제 관리

```mermaid
sequenceDiagram
    participant User
    participant PaymentController
    participant PaymentService
    participant PaymentRepository
    participant PointService

    User->>+PaymentController: 결제 요청(userId, orderId, amount)
    PaymentController->>+PaymentService: 결제 처리 요청(userId, orderId, amount)

    PaymentService->>+PointService: 사용자 잔액 확인(userId, amount)
    alt 잔액 부족
        PointService-->>PaymentService: 잔액 부족(false)
        PaymentService-->>PaymentRepository: 롤백 처리(transaction rollback)
        PaymentService-->>PaymentController: 결제 실패 응답(status=FAIL, code=E001, message="Insufficient balance")
    else 잔액 충분
        PointService-->>PaymentService: 잔액 충분(true)
        PaymentService->>+PaymentRepository: 결제 정보 저장(save)
        PaymentRepository-->>PaymentService: 저장 완료(paymentId, status=SUCCESS)
        PaymentService-->>PaymentController: 결제 성공 응답(paymentId, status=SUCCESS)
    end

    PaymentController-->>-User: 결제 결과 응답(status, code, message)
```

---
