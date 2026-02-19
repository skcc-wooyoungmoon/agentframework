# 구조화된 로깅 및 요청 추적 시스템

본 프로젝트에 구현된 구조화된 로깅 및 요청 추적 시스템에 대한 설명서입니다.

## 📋 개요

- **추적 ID (Trace ID)**: 단일 요청의 전체 흐름을 추적하는 고유 식별자
- **스팬 ID (Span ID)**: 요청 내의 개별 작업 단위를 식별하는 고유 식별자
- **MDC (Mapped Diagnostic Context)**: 스레드 로컬 컨텍스트에 추적 정보를 저장
- **구조화된 로깅**: JSON 형태의 일관된 로그 포맷
- **자동 요청 추적**: 모든 HTTP 요청에 대한 자동 추적

## 🏗️ 아키텍처

### 핵심 컴포넌트

1. **RequestTraceFilter**: HTTP 요청 진입점에서 추적 ID 생성 및 MDC 설정
2. **TraceUtils**: 추적 정보 관리 및 구조화된 로깅 유틸리티
3. **LoggingInterceptor**: AOP 기반 메서드 수준 로깅
4. **logback-spring.xml**: 환경별 로그 설정 및 JSON 포맷

### 데이터 흐름

```
HTTP 요청 → RequestTraceFilter → Controller → Service → Repository
    ↓              ↓                ↓         ↓         ↓
추적 ID 생성 → MDC 설정 → AOP 로깅 → AOP 로깅 → AOP 로깅
```

## 🚀 주요 기능

### 1. 자동 요청 추적

모든 HTTP 요청에 대해 자동으로:
- 고유한 추적 ID 생성 (또는 기존 추적 ID 재사용)
- 스팬 ID 생성
- 클라이언트 IP, User-Agent, 요청 URI/메서드 수집
- 응답 헤더에 추적 ID 포함

### 2. 구조화된 로깅

```json
{
  "timestamp": "2025-08-01 16:28:03.123",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "com.skax.aiplatform.controller.HealthController",
  "traceId": "a1b2c3d4e5f6789012345678",
  "spanId": "a1b2c3d4e5f67890",
  "parentSpanId": "parent123456789",
  "userId": "user123",
  "requestUri": "/api/v1/health",
  "requestMethod": "GET",
  "clientIp": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "message": "요청 처리 시작",
  "exception": ""
}
```

### 3. 메서드 수준 추적

AOP를 통해 자동으로:
- Controller, Service, Repository 메서드 실행 추적
- 메서드 파라미터 및 실행 시간 기록
- 예외 발생 시 상세 로깅
- Feign Client 호출 추적

## 🛠️ 사용법

### TraceUtils 주요 메서드

```java
// 추적 정보 조회
String traceId = TraceUtils.getTraceId();
String spanId = TraceUtils.getSpanId();

// 요청 시작/종료 로깅
TraceUtils.logRequestStart("GET", "/api/users", "192.168.1.1");
TraceUtils.logRequestEnd("GET", "/api/users", 200, 150);

// API 호출 로깅
TraceUtils.logApiCallStart("UserClient", "getUser", "userId=123");
TraceUtils.logApiCallEnd("UserClient", "getUser", 200, "User{id=123}");

// 컨텍스트 포함 에러 로깅
TraceUtils.logError("사용자 조회 실패", exception,
    "userId", "123",
    "operation", "getUser",
    "retryCount", "3");

// MDC 컨텍스트 조회
Map<String, String> context = TraceUtils.getContext();
```

### 커스텀 로깅 추가

```java
@Service
public class UserService {
    
    public User getUser(Long userId) {
        log.info("사용자 조회 시작 - userId: {}", userId);
        
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));
            
            log.info("사용자 조회 성공 - userId: {}, userName: {}", 
                    userId, user.getName());
            
            return user;
            
        } catch (Exception e) {
            TraceUtils.logError("사용자 조회 중 오류 발생", e,
                    "userId", userId.toString(),
                    "operation", "getUser");
            throw e;
        }
    }
}
```

## 🔧 환경별 설정

### 개발 환경 (elocal, local)
- 콘솔 + 파일 출력
- 컬러 로그 포맷
- DEBUG 레벨 로깅
- SQL 로그 활성화

### 외부개발 환경 (edev)
- 콘솔 + JSON 파일 출력
- PostgreSQL 로그 활성화
- INFO 레벨 로깅

### 개발/스테이징 환경 (dev, staging)
- JSON 파일 출력
- 에러 로그 별도 파일
- 성능 로그 별도 파일
- WARN 레벨 로깅

### 운영 환경 (prod)
- JSON 파일 출력
- 에러/성능/감사 로그 분리
- 장기 보관 정책
- ERROR 레벨 로깅

## 📊 로그 분석

### 요청 추적 예시

```bash
# 특정 추적 ID로 전체 요청 흐름 조회
grep "a1b2c3d4e5f6789012345678" application.log

# 특정 사용자의 모든 요청 조회
grep "\"userId\":\"user123\"" application.log

# 성능 이슈 분석 (5초 이상 소요된 요청)
grep -E "duration.*[5-9][0-9]{3,}ms|duration.*[0-9]{5,}ms" application.log
```

### 에러 패턴 분석

```bash
# 자주 발생하는 예외 TOP 10
grep -o '"exception":"[^"]*"' error.log | sort | uniq -c | sort -nr | head -10

# 특정 시간대 에러 발생률
grep "2025-08-01 14:" error.log | wc -l
```

## 🧪 테스트

### 로깅 기능 테스트

```bash
# 애플리케이션 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=elocal

# 헬스 체크 (기본 추적)
curl http://localhost:8080/health

# 로깅 테스트 (다양한 로그 레벨)
curl http://localhost:8080/logging-test

# 추적 ID 전달 테스트
curl -H "X-Trace-Id: custom-trace-id-123" http://localhost:8080/health
```

### 로그 확인

```bash
# 콘솔 로그 확인 (개발 환경)
tail -f logs/axportal-backend.log

# JSON 로그 확인 (운영 환경)
tail -f logs/axportal-backend.json | jq .

# 에러 로그 확인
tail -f logs/axportal-backend-error.json | jq .
```

## 🔒 보안 고려사항

1. **민감 정보 마스킹**: 비밀번호, 토큰 등 자동 마스킹
2. **로그 접근 제어**: 로그 파일 권한 관리
3. **개인정보 보호**: 개인식별정보 로깅 제한
4. **감사 로그**: 보안 관련 이벤트 별도 로깅

## 📈 성능 고려사항

1. **비동기 로깅**: Logback의 AsyncAppender 활용
2. **로그 레벨 최적화**: 운영 환경에서 DEBUG 로그 비활성화
3. **파일 로테이션**: 크기 및 시간 기반 로그 로테이션
4. **압축 아카이브**: 오래된 로그 파일 자동 압축

## 🔄 확장 가능성

1. **외부 로그 수집**: ELK Stack, Fluentd 연동
2. **메트릭 수집**: Micrometer, Prometheus 연동
3. **분산 추적**: Zipkin, Jaeger 연동
4. **알림 시스템**: 에러 로그 기반 실시간 알림

## 📚 참고 자료

- [MDC (Mapped Diagnostic Context)](https://logback.qos.ch/manual/mdc.html)
- [Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [Structured Logging Best Practices](https://engineering.zalando.com/posts/2021/11/structured-logging.html)
- [Distributed Tracing](https://opentracing.io/docs/overview/what-is-tracing/)
