#  It's Here

가게와 상품의 등록부터 주문, 결제, 리뷰까지 관리하고,
AI를 활용한 상품 설명 생성을 지원하는 주문 서비스입니다.

---

## 1. 프로젝트 목적 및 상세

### 프로젝트 목적

**It's Here**는 고객과 가게 운영자 사이에서 발생하는 상품 조회, 주문, 결제, 리뷰 과정을 하나의 서비스로 관리하기 위해 개발한 프로젝트입니다.

고객은 등록된 가게와 상품을 조회한 후 주문과 결제를 진행할 수 있으며, 주문이 완료되면 해당 주문에 대한 리뷰를 작성할 수 있습니다.

가게 운영자는 가게와 상품 정보를 관리하고 주문을 처리할 수 있습니다. 상품을 등록하거나 수정할 때는 AI를 이용해 상품 설명을 생성할 수 있으며, AI 요청 및 응답 내역을 별도로 관리합니다.

관리자는 서비스 운영에 필요한 지역과 카테고리 등의 정보를 관리할 수 있습니다.

### 주요 기능

| 도메인    | 주요 기능                                |
| ------ | ------------------------------------ |
| 인증·회원  | 회원가입, 로그인, 회원 정보 조회·수정·삭제, 역할별 권한 관리 |
| 주소     | 사용자 배송지 등록, 조회, 수정, 삭제               |
| 서비스 지역 | 서비스 제공 지역 등록 및 조회                    |
| 가게     | 가게 등록, 조회, 수정, 삭제                    |
| 카테고리   | 가게 카테고리 등록, 조회, 수정, 삭제               |
| 상품     | 상품 등록, 조회, 수정, 삭제 및 공개 여부 관리         |
| AI     | AI를 활용한 상품 설명 생성 및 요청·응답 이력 관리       |
| 주문     | 주문 생성, 조회, 취소 및 주문 상태 관리             |
| 주문 상품  | 주문에 포함된 상품과 수량, 가격 정보 관리             |
| 결제     | 주문 결제 처리 및 결제 정보 관리                  |
| 리뷰     | 완료된 주문의 리뷰 작성, 조회, 수정, 삭제            |

### 프로젝트 주요 목표

* 사용자 역할에 따른 인증 및 접근 권한 분리
* 가게, 상품, 주문, 결제, 리뷰로 이어지는 비즈니스 흐름 구현
* AI를 활용한 상품 설명 생성 기능 구현
* 주문 및 결제 상태의 일관성 있는 관리
* 완료된 주문에 대해서만 리뷰를 작성할 수 있도록 비즈니스 규칙 적용
* 리뷰 변경 시 가게의 평점 합계와 리뷰 개수에 정확하게 반영
* 공통 응답과 전역 예외 처리를 통한 일관된 API 응답 제공
* 테스트 코드와 CI를 통한 기능 검증

---

## 2. 팀원 역할 분담

| 팀원 | 담당 도메인                        | 
| :--: | :-----------------------------: | 
| 김경민 | Auth, User, Address           | 
| 김태언 | AI, Product                   |
| 문은서 | Order, Payment, Order Product | 
| 정나영 | Store, Category, CI/CD               | 
| 최한솔 | Review, Area                  | 

---

## 3. 서비스 구성 및 실행 방법

- Java 17
- Spring Boot 3.5.16
- Gradle
- PostgreSQL 16
- Docker
- Docker Compose

### 로컬 실행

```bash
./gradlew bootRun
```

### 테스트 실행

```bash
./gradlew test
```

### 전체 빌드


```bash
./gradlew build
```

---

## 4. ERD


### [🔗 ItsHereERD 링크](https://www.erdcloud.com/d/pPB3N3bdCCxfeaDMr)

---

## 5. 기술 스택
### Backend

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)

### Database

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-09476B?style=for-the-badge&logoColor=white)

### Build · Test

![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)

### API · AI

![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=black)
![Google Gemini](https://img.shields.io/badge/Google_Gemini-8E75B2?style=for-the-badge&logo=google-gemini&logoColor=white)

### Infra · CI/CD

![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-web-services&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon_EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

### Collaboration

![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-121011?style=for-the-badge&logo=github&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white)
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)


---

## 6. API Docs

애플리케이션 실행 후 Swagger UI에서 전체 API 명세를 확인하고 테스트할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

