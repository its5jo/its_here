<div align="center">

# 🍽️ It's Here

### AI 기반 상품 설명 생성을 지원하는 주문 서비스

가게 등록부터 상품 관리, 주문, 결제, 리뷰까지 하나의 서비스에서 관리할 수 있는 플랫폼입니다.

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.16-6DB33F?style=for-the-badge&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions)

</div>

---

# 📌 프로젝트 소개

**It's Here**는 고객과 점주를 연결하는 주문 플랫폼입니다.

운영자는 가게와 상품을 등록하고 AI를 활용하여 상품 설명을 자동 생성할 수 있으며, 주문을 효율적으로 관리할 수 있습니다.

고객은 원하는 가게의 상품을 조회하고 주문 및 결제를 진행한 뒤, 주문 완료 후 리뷰를 작성할 수 있습니다.

Spring Security 기반의 역할(Role)별 권한 관리와 JWT 인증을 적용하여 안전한 서비스를 제공합니다.

---

# 🎯 프로젝트 목표
✅ JWT 기반 인증 및 인가 구현

✅ 역할(Role) 기반 접근 제어

✅ 주문부터 결제, 리뷰까지 이어지는 비즈니스 플로우 구현

✅ Google Gemini 기반 AI 상품 설명 생성

✅ 완료된 주문만 리뷰 작성 가능하도록 비즈니스 규칙 적용

✅ 리뷰 변경 시 평점 자동 반영

✅ 테스트 코드 및 CI를 통한 코드 품질 확보

---

# ✨ 주요 기능

| Domain | 기능 |
|---------|------|
| 👤 Authentication | 회원가입, 로그인, JWT 인증 |
| 🙍 User | 회원 조회, 수정, 삭제 |
| 📍 Address | 배송지 관리 |
| 🏪 Store | 가게 등록 및 관리 |
| 🗂 Category | 카테고리 관리 |
| 🛍 Product | 상품 등록 및 관리 |
| 🤖 AI | AI 상품 설명 생성 |
| 🛒 Order | 주문 생성 및 조회 |
| 💳 Payment | 결제 처리 |
| ⭐ Review | 리뷰 작성 및 수정 |
| 🌎 Area | 서비스 지역 관리 |

---

# 👥 역할

| Role | 권한 |
|------|------|
| CUSTOMER | 주문, 결제, 리뷰 |
| OWNER | 가게 및 상품 관리 |
| MANAGER | 카테고리 및 지역 관리 |
| MASTER | 전체 관리자 |

---

# 🚀 서비스 흐름

```text
회원가입
 ↓
로그인
 ↓
가게 조회
 ↓
상품 조회
 ↓
주문
 ↓
결제
 ↓
주문 완료
 ↓
리뷰 작성
```

---

# 🏗 프로젝트 구조

```text
src
├── domain
│   ├── address
│   ├── aihistory
│   ├── area
│   ├── category
│   ├── order
│   ├── payment
│   ├── product
│   ├── review
│   ├── store
│   └── user
│
├── global
│   ├── advice
│   ├── base
│   ├── config
│   ├── constant
│   ├── response
│   └── security
│
└── infrastructure
    ├── ai
    └── storage
```

---

# 🛠 기술 스택

## Backend

- Java 17
- Spring Boot 3.5.16
- Spring Security
- Spring Data JPA
- Hibernate
- JWT

## Database

- PostgreSQL 16
- H2

## AI

- Google Gemini

## Build

- Gradle
- JUnit5

## Infra

- Docker
- Docker Compose
- AWS EC2
- GitHub Actions

---

# 💡 기술적 특징

## 🔐 인증 및 보안

- JWT 기반 인증
- Spring Security
- Role 기반 접근 제어
- Global Exception Handler
- 공통 API Response

---

## 🤖 AI 상품 설명 생성

Google Gemini를 이용하여

- 상품 설명 생성
- AI 요청 및 응답 이력 저장

---

## 🛒 주문 시스템

- 주문 생성
- 주문 상태 관리
- 결제 처리
- 완료된 주문만 리뷰 작성 가능

---

## ⭐ 리뷰 시스템

리뷰 생성/수정/삭제 시

- 리뷰 개수
- 평점 합계
- 평균 평점

자동 반영

---

# 📚 ERD

<div align="center">

### 🔗 ERD

https://www.erdcloud.com/d/pPB3N3bdCCxfeaDMr

</div>

---

# 🏛 아키텍처

<div align="center">

  <img width="581" height="433" alt="이츠오조 drawio (1)" src="https://github.com/user-attachments/assets/d1be2fac-d317-4a4a-ac89-8bbeab2a1a20" />
  
</div>


```text
GitHub
  ↓
GitHub Actions
  ↓
Docker Build
  ↓
AWS EC2
  ↓
Docker Compose
  ↓
Nginx
  ↓
Spring Boot
  ↓
PostgreSQL
```

---

# 📖 API

Swagger를 통해 API를 테스트할 수 있습니다.

| 환경 | URL |
|------|-----|
| Local | http://localhost:8080/swagger-ui/index.html |
| Production | http://43.200.62.125/swagger-ui/index.html |

---

# ⚙ 실행 방법

## 프로젝트 실행

```bash
./gradlew bootRun
```

## 테스트

```bash
./gradlew test
```

## 빌드

```bash
./gradlew build
```

---

# 👨‍💻 팀원

| 이름 | 담당 |
|------|------|
| 김경민 | Auth / User / Address |
| 김태언 | AI / Product |
| 문은서 | Order / Payment / Order Product |
| 정나영 | Store / Category / CI/CD |
| 최한솔 | Review / Area |

---

# 📈 기술적 도전

| 문제 | 해결 |
|------|------|
| JWT 인증 구현 | Spring Security Filter 기반 인증 |
| 역할별 접근 제어 | @PreAuthorize 활용 |
| AI 호출로 인한 트랜잭션 장기 점유 | 트랜잭션 외부에서 AI 호출 |
| 리뷰 평점 관리 | 평점 합계 및 리뷰 개수 동기화 |
| 공통 응답 | ApiResponse 적용 |
| 예외 처리 | Global Exception Handler |
| CI | GitHub Actions 자동 빌드 및 테스트 |

---

# ✅ 테스트

- Service Test
- Repository Test
- Security Test

---

# 🔄 CI/CD

```text
PR
 ↓
Build
 ↓
Test
 ↓
Merge
 ↓
Docker Image Build
 ↓
EC2 배포
 ↓
Docker Compose
 ↓
Service Running
```

---

# 📌 향후 개선 사항

- Redis Refresh Token
- Redis Cache
- Blue-Green Deployment
- Monitoring (Prometheus + Grafana)
- 동시성 제어 고도화
- 성능 최적화

---

<div align="center">

### ⭐ It's Here

AI 기반 주문 서비스를 구현하며

Spring Security, JPA, Docker, GitHub Actions를 활용한
실무형 백엔드 프로젝트입니다.

</div>
