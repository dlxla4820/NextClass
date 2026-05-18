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
- 사용자가 직접 시간표 작성 / 수정 / 삭제

### 📊 성적
- 시간표 데이터 연동하여 성적 편집 및 학점 계산

### ✅ TodoList
- Todo 작성 및 마감 시간 기준 정렬 (오름차순 / 내림차순)
- 마감 시간에 맞춘 푸시 알림 발송

### 💬 커뮤니티
- 게시물 작성 / 수정 / 삭제 / 추천
- 댓글 작성 / 삭제
- 게시물 검색 및 최근 검색어 관리
- 게시물 필터링 5종
  - 전체 게시물
  - 내가 속한 학교 게시물
  - 베스트 게시물 (추천 수 기준)
  - 내가 작성한 게시물
  - 내가 댓글 단 게시물
- 새 댓글 등록 시 Firebase(FCM) 푸시 알림 발송

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
