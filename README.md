# 📚 NextClass

> 고교학점제 시대를 위한 학생 올인원 앱 — 시간표 · 성적 · TodoList · 커뮤니티

<br>

## 📌 프로젝트 소개

2025년부터 전면 도입되는 **고교학점제**에 맞춰, 학생들이 자신만의 시간표를 작성하고 성적을 계산하며, 할 일을 관리하고 학생들끼리 정보를 공유할 수 있는 기능을 하나로 통합한 Android 앱 서비스입니다.

<br>

## 🛠 기술 스택

<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/firebase-DD2C00?style=for-the-badge&logo=firebase&logoColor=white">

| 분류 | 기술 |
|------|------|
| Backend | Spring Boot 3.3.0, Spring Security, Spring Data JPA, Hibernate |
| 인증 | JWT (jjwt 0.11.5), Jasypt |
| DB | MySQL, Redis |
| ORM | QueryDSL 5.0 |
| 이메일 | Spring Mail |
| 푸시 알림 | Firebase Admin SDK (FCM) |
| AOP | Spring AOP |
| Build | Gradle / Java 17 |

<br>

## 👥 팀 구성

| 역할 | 인원 | 담당 |
|------|------|------|
| Backend | 2명 | 회원 인증, 커뮤니티, AOP / 시간표, 성적, TodoList, FCM |
| Android | 1명 | Android 앱 전체 개발 |

<br>

## ⚙️ 주요 기능

### 🔐 회원 인증
- 로그인 / 회원가입 / 회원탈퇴
- 아이디 찾기 / 비밀번호 찾기 / 정보 변경
- JWT Access Token + Refresh Token 기반 인증
- 이메일 인증번호 발송 및 Redis TTL 기반 유효시간 관리

### 🗓 시간표
- 사용자가 원하는 시간표를 직접 작성 / 수정 / 삭제

### 📊 성적
- 직접 성적 편집 가능
- 이전에 작성한 시간표 데이터를 연동하여 학점 계산

### ✅ TodoList
- Todo 작성 및 마감 시간 기준 정렬 (오름차순 / 내림차순)
- 사용자가 설정한 시간에 맞춰 푸시 알림 발송

### 💬 커뮤니티
- 게시물 작성 / 수정 / 삭제 / 추천
- 댓글 작성 / 삭제
- 게시물 검색 및 최근 검색어 관리
- 게시물 필터링 5종
  - 전체 게시물
  - 내가 속한 학교 게시물
  - 베스트 게시물 (일정 추천 수 이상)
  - 내가 작성한 게시물
  - 내가 댓글 단 게시물
- 새 댓글 등록 시 Firebase(FCM) 푸시 알림 발송
- 알림 터치 시 해당 게시물로 바로 이동

### ⚙️ 사용자 설정
- 이메일 / 비밀번호 등 개인정보 수정
- 푸시 알림 수신 여부 설정

### 📋 AOP 로그
- `LogAspect`로 API 요청/응답 자동 로깅

<br>

## 📂 프로젝트 구조

```
src
└── main
    └── java
        └── com.nextClass
            ├── auth           # JWT 인증 필터 및 토큰 관리
            ├── config         # Spring Security 등 설정
            ├── controller     # 요청 처리
            ├── dto            # 데이터 전송 객체
            ├── entity         # JPA 엔티티
            ├── repository     # DB 접근 (JPA + QueryDSL)
            ├── service        # 비즈니스 로직
            ├── aop            # LogAspect
            └── util           # 공통 유틸리티
```

<br>

## 🖥 스크린샷

<img width="440" height="906" alt="로그인" src="https://github.com/user-attachments/assets/f8a0ac10-7061-463b-bd73-f7ba4a2313f3" />
<img width="440" height="906" alt="회원가입" src="https://github.com/user-attachments/assets/c9970744-0e12-4262-af18-4082ddfc0108" />
<img width="447" height="922" alt="홈" src="https://github.com/user-attachments/assets/d3ae6389-475e-455e-b20f-58701b9fa914" />
<img width="447" height="922" alt="시간표" src="https://github.com/user-attachments/assets/d9bcc7ea-2878-4aa6-b829-f262b6162d05" />
<img width="440" height="906" alt="성적" src="https://github.com/user-attachments/assets/0b19a4a7-b33d-4fa6-a2d9-66a40cfd4c3b" />
<img width="447" height="922" alt="커뮤니티" src="https://github.com/user-attachments/assets/a6b2fee4-2d19-4267-bd18-c8d0bdf87d6a" />
<img width="447" height="922" alt="게시물 세부" src="https://github.com/user-attachments/assets/7d7f3a3d-af8c-4b6e-8119-e10cb6fd2301" />
<img width="440" height="906" alt="TodoList" src="https://github.com/user-attachments/assets/618d9265-3f64-4bec-8bb8-f842bc1b0aad" />
<img width="440" height="906" alt="사용자 설정" src="https://github.com/user-attachments/assets/c18f2ff7-13a1-471d-a5a1-4f4dda2bbf2e" />
<img width="440" height="925" alt="아이디 찾기" src="https://github.com/user-attachments/assets/c521fdd9-9a85-42bb-991a-3bd2470c74ac" />

<br>

## 🚀 로컬 실행 방법

```bash
# 1. 저장소 클론
git clone https://github.com/dlxla4820/NextClass.git

# 2. application.properties에 설정 입력
spring.datasource.url=jdbc:mysql://localhost:3306/{DB명}
spring.datasource.username={유저명}
spring.datasource.password={비밀번호}

spring.redis.host=localhost
spring.redis.port=6379

spring.mail.username={이메일}
spring.mail.password={앱 비밀번호}

# 3. 빌드 및 실행
./gradlew bootRun
```
