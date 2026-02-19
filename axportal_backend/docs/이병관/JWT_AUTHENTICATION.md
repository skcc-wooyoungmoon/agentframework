# Spring Security와 JWT 인증 시스템

본 프로젝트에 구현된 Spring Security와 JWT 기반 인증 시스템에 대한 설명서입니다.

## 📋 개요

- **JWT (JSON Web Token)**: Stateless 인증을 위한 토큰 기반 인증 시스템
- **Spring Security**: Spring 프레임워크의 보안 모듈
- **Access Token**: API 접근을 위한 단기 토큰 (기본 1시간)
- **Refresh Token**: Access Token 갱신을 위한 장기 토큰 (기본 7일)
- **Role-based Access Control**: 역할 기반 접근 제어

## 🏗️ 아키텍처

### 핵심 컴포넌트

1. **JwtTokenProvider**: JWT 토큰 생성, 검증, 파싱
2. **JwtAuthenticationFilter**: HTTP 요청에서 JWT 토큰 추출 및 인증 처리
3. **JwtAuthenticationEntryPoint**: 인증 실패 시 에러 응답 처리
4. **JwtAccessDeniedHandler**: 권한 없는 접근 시 에러 응답 처리
5. **AuthService**: 인증 관련 비즈니스 로직
6. **AuthController**: 인증 관련 REST API

### 인증 흐름

```
클라이언트 → 로그인 요청 → AuthController → AuthService → JwtTokenProvider
    ↓                                                            ↓
JWT 토큰 응답 ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← JWT 토큰 생성

보호된 API 요청 → JwtAuthenticationFilter → 토큰 검증 → SecurityContext 설정
    ↓                                              ↓
API 응답 ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← ← 인증 완료
```

## 🚀 API 엔드포인트

### 인증 관련 API

#### 1. 로그인
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**응답:**
```json
{
  "success": true,
  "message": "로그인이 성공했습니다",
  "data": {
    "token_type": "Bearer",
    "access_token": "eyJhbGciOiJIUzUxMiJ9...",
    "refresh_token": "eyJhbGciOiJIUzUxMiJ9...",
    "expires_in": 3600,
    "issued_at": "2025-08-01T16:30:00",
    "expires_at": "2025-08-01T17:30:00"
  }
}
```

#### 2. 토큰 갱신
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### 3. 현재 사용자 정보
```http
GET /api/v1/auth/me
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**응답:**
```json
{
  "success": true,
  "message": "사용자 정보를 조회했습니다",
  "data": {
    "username": "admin",
    "authorities": ["ROLE_ADMIN", "ROLE_USER"],
    "userId": 1,
    "email": "admin@example.com",
    "name": "관리자"
  }
}
```

#### 4. 로그아웃
```http
POST /api/v1/auth/logout
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

#### 5. 토큰 유효성 검증
```http
GET /api/v1/auth/validate
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### 보호된 API 예시

```http
GET /health/secure
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 👥 테스트 사용자

### 기본 제공 사용자 계정

| 사용자명 | 비밀번호 | 권한 |
|---------|---------|------|
| admin   | admin123 | ROLE_ADMIN, ROLE_USER |
| user    | user123  | ROLE_USER |
| test    | test123  | ROLE_USER |

## 🔧 설정

### JWT 설정 (application-elocal.yml)

```yaml
jwt:
  secret: myDevSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForSecurityPurposes
  access-token-validity-in-seconds: 3600    # 1시간
  refresh-token-validity-in-seconds: 604800 # 7일
```

### 보안 설정

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    // 공개 엔드포인트 (인증 불필요)
    - /api/v1/auth/**
    - /health
    - /actuator/**
    - /swagger-ui/**
    - /v3/api-docs/**
    
    // 관리자 전용 엔드포인트
    - /api/v1/admin/** (ROLE_ADMIN 필요)
    
    // 기타 모든 엔드포인트 (인증 필요)
}
```

## 🧪 테스트 방법

### 1. 애플리케이션 실행
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=elocal
```

### 2. 로그인 테스트
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 3. 보호된 API 접근 테스트
```bash
# 1. 토큰 없이 접근 (401 Unauthorized)
curl http://localhost:8080/health/secure

# 2. 유효한 토큰으로 접근
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  http://localhost:8080/health/secure
```

### 4. 토큰 갱신 테스트
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"YOUR_REFRESH_TOKEN"}'
```

### 5. Swagger UI 테스트
브라우저에서 `http://localhost:8080/swagger-ui.html` 접속 후:
1. "Authorize" 버튼 클릭
2. 로그인 API로 토큰 획득
3. "Bearer YOUR_ACCESS_TOKEN" 형태로 입력
4. 보호된 API 테스트

## 🔒 보안 기능

### 1. 토큰 보안
- **HMAC SHA-512**: 강력한 서명 알고리즘
- **256비트 이상 비밀키**: 보안 강화
- **토큰 만료 시간**: 단기 Access Token, 장기 Refresh Token
- **토큰 타입 검증**: Access/Refresh Token 구분

### 2. 에러 처리
- **표준화된 에러 응답**: AxResponse 형태로 일관된 응답
- **상세 에러 로깅**: 보안 이벤트 추적
- **클라이언트 IP 기록**: 접근 시도 추적

### 3. CORS 설정
- **Origin 패턴 허용**: 개발/운영 환경별 설정
- **인증 헤더 노출**: X-Trace-Id, X-Span-Id 헤더 포함
- **Credentials 허용**: 쿠키 기반 인증 지원

## 📊 모니터링 및 로깅

### 인증 관련 로그

```log
# 로그인 시도
2025-08-01 16:30:00.123  INFO [auth] 사용자 로그인 시도: admin

# 로그인 성공
2025-08-01 16:30:00.456  INFO [auth] 사용자 로그인 성공: admin, 권한: ROLE_ADMIN, ROLE_USER

# 토큰 검증
2025-08-01 16:30:01.789  DEBUG [jwt] JWT 토큰으로 사용자 인증 완료: admin

# 인증 실패
2025-08-01 16:30:02.012  WARN [security] 인증되지 않은 사용자의 접근 시도: GET /api/v1/users
```

### 추적 정보 연동

JWT 인증 시스템은 구조화된 로깅 시스템과 완전히 통합되어:
- **사용자 ID**: MDC에 자동 설정
- **추적 ID**: 요청 전반에 걸쳐 유지
- **보안 이벤트**: 구조화된 로그로 기록

## 🚀 확장 가능성

### 1. 데이터베이스 연동
현재는 메모리 기반 테스트 사용자를 사용하지만, 다음과 같이 확장 가능:
- **UserDetailsService**: 데이터베이스 기반 사용자 조회
- **JPA Entity**: User, Role, Authority 엔티티
- **암호화**: BCrypt 패스워드 인코딩

### 2. 고급 보안 기능
- **토큰 블랙리스트**: Redis 기반 토큰 무효화
- **다중 디바이스 지원**: 디바이스별 토큰 관리
- **2FA 인증**: OTP, SMS 인증 추가
- **소셜 로그인**: OAuth2 연동

### 3. 모니터링 강화
- **메트릭 수집**: 로그인 성공/실패율
- **알림 시스템**: 보안 이벤트 실시간 알림
- **감사 로그**: 상세 보안 로그 수집

## 📚 참고 자료

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/)
- [RFC 7519 - JSON Web Token](https://tools.ietf.org/html/rfc7519)
- [Spring Security JWT Tutorial](https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/)
