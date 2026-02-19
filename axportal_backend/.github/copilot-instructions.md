# GitHub Copilot Instructions for AxPortal Backend Project

## 프로젝트 정보
- **프로젝트명**: AxPortal Backend - AI Platform RESTful API
- **개발자**: ByounggwanLee
- **생성일**: 2025-08-13
- **최종 업데이트**: 2025-10-27
- **목표**: Spring Boot 기반의 AI Platform 통합 포털 RESTful API 개발
- **Java 버전**: 17+
- **Spring Boot 버전**: 3.5.4
- **빌드 도구**: Maven 3.9.9
- **Spring Cloud 버전**: 2025.0.0
- **데이터베이스**: Multi-Database 지원
  - **로컬 개발**: H2 (2.3.232) in-memory
  - **외부 로컬**: H2 (elocal profile)
  - **외부 개발**: PostgreSQL (42.7.7)
  - **개발/스테이징/운영**: Tibero (8.0.11)

## 기술 스택 (확장)
- **ORM**: Spring Data JPA 3.5.4, MyBatis 3.0.4
- **External API**: Spring Cloud OpenFeign 4.2.0 (7개 주요 외부 시스템 연동)
- **Documentation**: SpringDoc OpenAPI 3 (2.8.8)
- **Testing**: JUnit 5, Mockito, TestContainers
- **Logging**: SLF4J, Logback
- **Security**: Spring Security 6.5.4, JWT, OAuth2
- **Utilities**: Lombok, Jakarta Validation, MapStruct
- **DevTools**: Spring Boot DevTools (개발 환경 전용)
- **Monitoring**: Micrometer, Prometheus
- **Scheduling**: Spring Scheduler (@Scheduled)
- **Batch Processing**: 다중 배치 처리 지원 (Agent Gateway, Lablup Artifact)

## 코딩 스타일 가이드

### 1. 네이밍 컨벤션
- **클래스명**: PascalCase (예: UserService, ProductController)
- **메서드명**: camelCase (예: createUser, findByEmail)
- **변수명**: camelCase (예: userId, createdAt)
- **상수명**: UPPER_SNAKE_CASE (예: MAX_RETRY_COUNT)
- **패키지명**: lowercase with dots (예: com.skax.aiplatform)

### 2. 파일 구조 및 패키지 규칙 (실제 프로젝트 구조 기반)
```
com.skax.aiplatform/
├── batch/                 # 배치 작업 (IdeDeleteBatch 등)
├── client/                # 외부 API 연동 (7개 주요 외부 시스템)
│   ├── sktai/            # SKTAI 연동 (19개 서브 모듈)
│   │   ├── agent/        # Agent 관리
│   │   ├── auth/         # 인증
│   │   ├── model/        # 모델 관리
│   │   ├── serving/      # 서빙 관리
│   │   ├── agentgateway/ # Agent Gateway (배치 처리)
│   │   ├── resource/     # 리소스 관리 (스케일링)
│   │   └── ...
│   ├── datumo/           # Datumo 연동
│   ├── lablup/           # Lablup 연동
│   ├── elastic/          # Elasticsearch 연동
│   ├── ione/            # I-ONE 연동
│   ├── shinhan/         # 신한은행 연동
│   └── udp/             # UDP 연동
├── controller/           # REST 컨트롤러 (19개 도메인)
│   ├── admin/           # 관리자 기능
│   ├── agent/           # 에이전트 관리
│   ├── auth/            # 인증
│   ├── common/          # 공통 기능
│   ├── data/            # 데이터 관리
│   ├── deploy/          # 배포 관리
│   ├── elastic/         # Elasticsearch
│   ├── eval/            # 평가
│   ├── home/            # 홈/IDE
│   ├── knowledge/       # 지식 관리
│   ├── lineage/         # 데이터 계보
│   ├── log/             # 로그 관리
│   ├── model/           # 모델 관리
│   ├── notice/          # 공지사항
│   ├── prompt/          # 프롬프트 관리
│   ├── resource/        # 리소스 관리
│   └── sample/          # 샘플/테스트
├── service/             # 비즈니스 로직
├── repository/          # 데이터 액세스
├── entity/              # JPA 엔티티
├── dto/                 # 데이터 전송 객체
├── mapper/              # MapStruct 매퍼
├── config/              # 설정 클래스
└── common/              # 공통 기능
    ├── constant/        # 상수 정의
    ├── exception/       # 예외 처리
    ├── response/        # 응답 관련 (AxResponse, AxResponseEntity, PageResponse)
    ├── security/        # 보안 관련
    └── util/            # 유틸리티
```
- **도메인별 하위 디렉토리**: 각 계층에서 도메인명으로 세분화
- **DTO 세분화**: Request는 Req 접미사로 request 디렉토리, Response는 Res 접미사로 response 디렉토리
- **Service Implementation**: 도메인별 impl 하위 디렉토리
- **외부 API Client**: 시스템별 독립적 구조 및 Service 래핑 패턴

### 3. 어노테이션 사용 규칙
- **Lombok**: @Getter, @Builder, @NoArgsConstructor 우선 사용
- **JPA**: @Entity, @Table, @Column 명시적 설정
- **Validation**: @Valid, @NotNull, @NotBlank 적극 활용
- **Spring**: @Service, @Repository, @RestController 표준 사용

### 4. 메서드 구현 패턴
```java
// Service 메서드 패턴
@Transactional(readOnly = true)
public EntityResponse getEntityById(Long id) {
    Entity entity = findEntityById(id);
    return EntityResponse.from(entity);
}

private Entity findEntityById(Long id) {
    return entityRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
}
```

### 5. 공통 기능
- **RESTful API 설계**: 일관된 REST API 패턴
- **통합된 응답 포맷**: AxResponseEntity<T> 래퍼를 통한 표준화된 응답 (ResponseEntity + AxResponse 통합)
- **페이징 처리**: AxResponseEntity<PageResponse<T>>를 통한 효율적인 대용량 데이터 처리
- **예외 처리**: GlobalExceptionHandler를 통한 통합 예외 관리
- **API 문서화**: OpenAPI 3 기반 자동 문서 생성
- **입력 검증**: Jakarta Validation을 통한 요청 데이터 검증
- **로깅**: 구조화된 로깅 및 요청 추적 자동생성
- **주석**: JavaDoc과 OpenApi 생성
- **상수화**: 애플리케이션 상수 클래스를 생성하여 사용
- **Mapping**: MapStruct를 통한 DTO와 Entity 간 변환
- **HTTP 상태 코드**: 표준화된 상태 코드와 메시지 자동 설정

## API 설계 규칙

### 1. REST API 엔드포인트 패턴
```
GET    /api/v1/users          # 목록 조회
GET    /api/v1/users/{id}     # 단일 조회
POST   /api/v1/users          # 생성
PUT    /api/v1/users/{id}     # 전체 수정
PATCH  /api/v1/users/{id}     # 부분 수정
DELETE /api/v1/users/{id}     # 삭제
```

### 2. 응답 형식 표준화 (AxResponseEntity 사용)

#### 개요

##### 목적
- API 응답의 일관성 확보
- 클라이언트 개발 효율성 향상
- 에러 처리 표준화
- 다국어 지원 기반 마련

##### 적용 범위
- 모든 REST API 엔드포인트
- 에러 응답 처리
- 페이지네이션 응답
- 파일 업로드/다운로드 응답

#### 🏗️ 응답 구조 표준

##### 기본 응답 구조

```json
{
  "success": boolean,
  "message": "string",
  "data": object | array | null,
  "error": {
    "code": "string",
    "message": "string",
    "details": "string",
    "timestamp": "2025-08-08T04:01:33Z",
    "path": "/api/users/123",
    "fieldErrors": [
      {
        "field": "email",
        "rejectedValue": "invalid-email",
        "message": "올바른 이메일 형식이 아닙니다"
      }
    ]
  },
  "timestamp": "2025-08-08T04:01:33Z",
  "path": "/api/users"
}
```

##### 응답 필드 설명

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `success` | boolean | ✅ | 요청 성공 여부 |
| `message` | string | ✅ | 응답 메시지 |
| `data` | any | ❌ | 응답 데이터 (성공 시) |
| `error` | object | ❌ | 에러 정보 (실패 시) |
| `timestamp` | string | ✅ | 응답 생성 시간 (ISO 8601) |
| `path` | string | ✅ | 요청 경로 |


#### ✅ 성공 응답 표준
// Controller 메서드 패턴 - 단일조회
@GetMapping("/{id}")
public AxResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    UserResponse user = userService.getUserById(id);
    return AxResponseEntity.ok(user, "사용자 정보를 성공적으로 조회했습니다.");
}

// Controller 메서드 패턴 - 목록조회
@GetMapping
public AxResponseEntity<PageResponse<UserResponse>> getUsers(
        @PageableDefault(size = 20) Pageable pageable) {
    Page<UserResponse> users = userService.getUsers(pageable);
    return AxResponseEntity.okPage(users, "사용자 목록을 성공적으로 조회했습니다.");


// Controller 메서드 패턴 - 생성
@PostMapping
public AxResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
    UserResponse user = userService.createUser(request);
    return AxResponseEntity.created(user, "사용자가 성공적으로 생성되었습니다.");
}

// Controller 메서드 패턴 - 수정
@PutMapping("/{id}")
public AxResponseEntity<UserResponse> updateUser(
        @PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
    UserResponse user = userService.updateUser(id, request);
    return AxResponseEntity.updated(user, "사용자 정보가 성공적으로 수정되었습니다.");
}

// Controller 메서드 패턴 - 삭제
@DeleteMapping("/{id}")
public AxResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return AxResponseEntity.deleted("사용자가 성공적으로 삭제되었습니다.");
}

#### ❌ 에러 응답 표준

##### 1. 리소스 없음 (404)

```json
{
  "success": false,
  "message": "사용자를 찾을 수 없습니다",
  "error": {
    "hscode": "NOT_FOUND",
    "code": "U001",
    "message": "사용자를 찾을 수 없습니다",
    "details": "ID 999에 해당하는 사용자가 존재하지 않습니다",
    "timestamp": "2025-08-15T08:07:48",
    "path": "/api/users/999"
  },
  "timestamp": "2025-08-15T08:07:48",
  "path": "/api/users/999"
}
```

##### 2. 유효성 검증 실패 (400)

```json
{
  "success": false,
  "message": "입력값 검증에 실패했습니다",
  "error": {
    "hscode": "BAD_REQUEST",
    "code": "V001",
    "message": "입력값이 올바르지 않습니다",
    "details": "필수 필드가 누락되었거나 형식이 올바르지 않습니다",
    "timestamp": "2025-08-15T08:07:48",
    "path": "/api/users",
    "fieldErrors": [
      {
        "field": "name",
        "rejectedValue": "",
        "message": "이름은 필수입니다"
      },
      {
        "field": "email",
        "rejectedValue": "invalid-email",
        "message": "올바른 이메일 형식이 아닙니다"
      },
      {
        "field": "age",
        "rejectedValue": -1,
        "message": "나이는 0 이상이어야 합니다"
      }
    ]
  },
  "timestamp": "2025-08-15T08:07:48",
  "path": "/api/users"
}
```

##### 3. 인증 실패 (401)

```json
{
  "success": false,
  "message": "인증에 실패했습니다",
  "error": {
    "hscode": "UNAUTHORIZED",
    "code": "A001",
    "message": "유효하지 않은 인증 정보입니다",
    "details": "JWT 토큰이 만료되었거나 올바르지 않습니다",
    "timestamp": "2025-08-15T08:07:48",
    "path": "/api/users/profile"
  },
  "timestamp": "2025-08-15T08:07:48",
  "path": "/api/users/profile"
}
```

##### 4. 권한 부족 (403)

```json
{
  "success": false,
  "message": "접근 권한이 없습니다",
  "error": {
    "hscode": "FORBIDDEN",
    "code": "A002",
    "message": "해당 리소스에 접근할 권한이 없습니다",
    "details": "ADMIN 권한이 필요합니다",
    "timestamp": "2025-08-15T08:07:48",
    "path": "/api/admin/users"
  },
  "timestamp": "2025-08-15T08:07:48",
  "path": "/api/admin/users"
}
```

##### 5. 비즈니스 로직 오류 (409)

```json
{
  "success": false,
  "message": "비즈니스 규칙 위반",
  "error": {
    "hscode": "CONFLICT",
    "code": "B001",
    "message": "이미 존재하는 이메일입니다",
    "details": "hong@example.com은 이미 다른 사용자가 사용 중입니다",
    "timestamp": "2025-08-15T08:07:48",
    "path": "/api/users"
  },
  "timestamp": "2025-08-15T08:07:48",
  "path": "/api/users"
}
```

##### 6. 서버 내부 오류 (500)

```json
{
  "success": false,
  "message": "서버 내부 오류가 발생했습니다",
  "error": {
    "hscode": "INTERNAL_SERVER_ERROR",
    "code": "S001",
    "message": "예상치 못한 오류가 발생했습니다",
    "details": "시스템 관리자에게 문의하세요",
    "timestamp": "2025-08-15T08:07:48",
    "path": "/api/users"
  },
  "timestamp": "2025-08-15T08:07:48",
  "path": "/api/users"
}
    "code": "ACCESS_DENIED",
    "message": "해당 리소스에 접근할 권한이 없습니다",
    "details": "ADMIN 권한이 필요합니다",
    "timestamp": "2025-08-08T04:01:33Z",
    "path": "/api/admin/users"
  },
  "timestamp": "2025-08-08T04:01:33Z",
  "path": "/api/admin/users"
}
```

##### 5. 비즈니스 로직 오류 (409)

```json
{
  "success": false,
  "message": "비즈니스 규칙 위반",
  "error": {
    "code": "BUSINESS_RULE_VIOLATION",
    "message": "이미 존재하는 이메일입니다",
    "details": "hong@example.com은 이미 다른 사용자가 사용 중입니다",
    "timestamp": "2025-08-08T04:01:33Z",
    "path": "/api/users"
  },
  "timestamp": "2025-08-08T04:01:33Z",
  "path": "/api/users"
}
```

##### 6. 서버 내부 오류 (500)

```json
{
  "success": false,
  "message": "서버 내부 오류가 발생했습니다",
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "예상치 못한 오류가 발생했습니다",
    "details": "시스템 관리자에게 문의하세요",
    "timestamp": "2025-08-08T04:01:33Z",
    "path": "/api/users"
  },
  "timestamp": "2025-08-08T04:01:33Z",
  "path": "/api/users"
}
```

#### 📄 페이지네이션 응답

##### 페이지네이션 성공 응답
```json
{
  "success": true,
  "message": "사용자 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "홍길동",
        "email": "hong@example.com"
      },
      {
        "id": 2,
        "name": "김철수",
        "email": "kim@example.com"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 20,
      "sort": "createdAt,desc"
    },
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false,
  },
  "timestamp": "2025-08-08T04:01:33Z",
  "path": "/api/users"
}
```

#### 🌐 HTTP 상태 코드 가이드

##### 성공 응답 (2xx)

| 코드 | 의미 | 사용 사례 |
|------|------|----------|
| 200 | OK | 조회, 수정 성공 |
| 201 | Created | 생성 성공 |
| 204 | No Content | 삭제 성공 (응답 바디 없음) |

##### 클라이언트 오류 (4xx)

| 코드 | 의미 | 에러 코드 | 사용 사례 |
|------|------|-----------|----------|
| 400 | Bad Request | VALIDATION_FAILED | 유효성 검증 실패 |
| 401 | Unauthorized | AUTHENTICATION_FAILED | 인증 실패 |
| 403 | Forbidden | ACCESS_DENIED | 권한 부족 |
| 404 | Not Found | RESOURCE_NOT_FOUND | 리소스 없음 |
| 409 | Conflict | BUSINESS_RULE_VIOLATION | 비즈니스 규칙 위반 |
| 429 | Too Many Requests | RATE_LIMIT_EXCEEDED | 요청 한도 초과 |

##### 서버 오류 (5xx)

| 코드 | 의미 | 에러 코드 | 사용 사례 |
|------|------|-----------|----------|
| 500 | Internal Server Error | INTERNAL_SERVER_ERROR | 서버 내부 오류 |
| 502 | Bad Gateway | SERVICE_UNAVAILABLE | 외부 서비스 오류 |
| 503 | Service Unavailable | SERVICE_UNAVAILABLE | 서비스 일시 중단 |

#### 🌍 메시지 다국화

##### 메시지 키 구조

```
{domain}.{action}.{result}
{domain}.{validation}.{field}
error.{errorType}.{specificError}
```

##### 다국화 메시지 파일

###### messages.properties (기본, 한국어)
```properties
# 성공 메시지
user.create.success=사용자 생성 성공
user.update.success=사용자 정보 수정 성공
user.delete.success=사용자 삭제 성공
user.get.success=사용자 조회 성공
user.list.success=사용자 목록 조회 성공

# 유효성 검증 메시지
user.validation.name=이름은 필수입니다
user.validation.email=올바른 이메일 형식이 아닙니다
user.validation.age=나이는 1 이상 150 이하여야 합니다
user.validation.password=비밀번호는 8자 이상이어야 합니다

# 에러 메시지
error.user.notFound=사용자를 찾을 수 없습니다
error.user.alreadyExists=이미 존재하는 이메일입니다
error.user.inactiveUser=비활성화된 사용자입니다

# 공통 에러 메시지
error.authentication.failed=인증에 실패했습니다
error.authorization.denied=접근 권한이 없습니다
error.validation.failed=입력값 검증에 실패했습니다
error.internal.server=서버 내부 오류가 발생했습니다
```

###### messages_en.properties (영어)
```properties
# Success messages
user.create.success=User created successfully
user.update.success=User updated successfully
user.delete.success=User deleted successfully
user.get.success=User retrieved successfully
user.list.success=User list retrieved successfully

# Validation messages
user.validation.name=Name is required
user.validation.email=Invalid email format
user.validation.age=Age must be between 1 and 150
user.validation.password=Password must be at least 8 characters

# Error messages
error.user.notFound=User not found
error.user.alreadyExists=Email already exists
error.user.inactiveUser=User is inactive

# Common error messages
error.authentication.failed=Authentication failed
error.authorization.denied=Access denied
error.validation.failed=Validation failed
error.internal.server=Internal server error occurred
```


### 4. AxResponseEntity 표준 메서드 (최신 - ErrorCode 지원)
```java
// 주요 성공 응답 메서드들 (권장 사용)
AxResponseEntity.ok(data, message)                             // 200 OK
AxResponseEntity.okPage(pageResponse, message)                 // 200 OK
AxResponseEntity.created(data, message)                        // 201 CREATED
AxResponseEntity.updated(data, message)                        // 200 OK
AxResponseEntity.deleted(message)                              // 200 OK (삭제 성공)

// ErrorCode를 사용한 실패 응답 메서드들 (권장)
AxResponseEntity.error(errorCode)                              // ErrorCode 객체 사용
AxResponseEntity.error(errorCode, customMessage)              // ErrorCode + 커스텀 메시지
AxResponseEntity.notFound(errorCode)                           // 404 + ErrorCode
AxResponseEntity.badRequest(errorCode)                         // 400 + ErrorCode
AxResponseEntity.unauthorized(errorCode)                       // 401 + ErrorCode
AxResponseEntity.forbidden(errorCode)                          // 403 + ErrorCode
AxResponseEntity.conflict(errorCode)                           // 409 + ErrorCode
AxResponseEntity.internalServerError(errorCode)               // 500 + ErrorCode

// 메시지와 구체적 코드를 직접 지정하는 방법
AxResponseEntity.notFound(message, "U001")                     // 404 + 구체적 코드
AxResponseEntity.badRequest(message, "C002")                   // 400 + 구체적 코드

// 기존 방식 (하위 호환성 유지)
AxResponseEntity.badRequest(message)                          // 400 BAD REQUEST
AxResponseEntity.unauthorized(message)                        // 401 UNAUTHORIZED
AxResponseEntity.forbidden(message)                           // 403 FORBIDDEN
AxResponseEntity.notFound(message)                            // 404 NOT FOUND
AxResponseEntity.conflict(message)                            // 409 CONFLICT
AxResponseEntity.internalServerError(message)                 // 500 INTERNAL SERVER ERROR
```

### 5. ErrorCode 표준 정의 (최신)
```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 사용자 관련 (U001~U099)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "U003", "비활성화된 사용자입니다"),
    
    // 샘플 관련 (S001~S099)
    SAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "샘플을 찾을 수 없습니다"),
    SAMPLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "S002", "이미 존재하는 샘플입니다"),
    
    // 공통 오류 (C001~C099)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "C002", "입력값 검증에 실패했습니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다"),
    
    // 인증/인가 오류 (A001~A099)
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", "인증에 실패했습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "토큰이 만료되었습니다"),
    
    // 서버 오류 (E001~E099)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "서버 내부 오류가 발생했습니다"),
    EXTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "E002", "외부 서비스 오류가 발생했습니다");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

### 6. 에러 응답 구조 표준 (최신)
```java
// AxError 클래스 구조
public class AxError {
    private String hscode;      // HTTP 상태 기반 코드 (예: "NOT_FOUND")
    private String code;        // ErrorCode 기반 구체적인 코드 (예: "U001")
    private String message;     // 에러 메시지
    private String details;     // 에러 상세 정보
    private String timestamp;   // 에러 발생 시간
    private String path;        // 요청 경로
    private ActionType actionType; // 에러 발생 처리 액션 타입
    private List<FieldError> fieldErrors; // 유효성 검증 에러 목록
    
    // 에러 발생 처리 액션 타입 enum
    public enum ActionType {
        CONFIRM,    // 확인 버튼 표시(기본)
        PREVIOUS,   // 이전 버튼 표시
        NEXT,       // 다음 버튼 표시
        RETRY,      // 재시도 버튼 표시
        CANCEL      // 취소 버튼 표시
    }
}

// AxResponse.failure 메서드 구조 (확장)
AxResponse.failure(message, hscode, code, statusCode, statusText, details, actionType)
AxResponse.failure(message, hscode, code, statusCode, statusText, details) // actionType 없이
AxResponse.failure(message, hscode, code, statusCode, statusText) // details, actionType 없이
AxResponse.failure(message, hscode, code) // 기본 구조
```

## 데이터베이스 설계 규칙

### 1. 테이블 명명 규칙
- 테이블명: snake_case, 복수형 (예: users, products, order_items)
- 컬럼명: snake_case (예: created_at, user_id, email_address)
- 인덱스명: idx_테이블명_컬럼명 (예: idx_users_email)

### 2. 공통 컬럼
```java
// 모든 엔티티에 포함할 공통 필드
@CreatedDate
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;

@LastModifiedDate
@Column(name = "updated_at")
private LocalDateTime updatedAt;

@Column(name = "created_by", length = 50)
private String createdBy;

@Column(name = "updated_by", length = 50)
private String updatedBy;
```

### 3. JPA 엔티티 규칙
```java
@Entity
@Table(name = "table_name")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EntityName extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 나머지 필드들...
}
```

## 예외 처리 규칙

### 1. 커스텀 예외 계층구조
```java
// 기본 예외
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}

// 비즈니스 예외
public class BusinessException extends CustomException {
    // 비즈니스 로직 관련 예외
}

// 검증 예외
public class ValidationException extends CustomException {
    // 입력값 검증 관련 예외
}
```

### 2. ErrorCode 정의 패턴 (최신)
```java
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 사용자 관련 (U001~U099)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "U003", "비활성화된 사용자입니다"),
    
    // 샘플 관련 (S001~S099)
    SAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "샘플을 찾을 수 없습니다"),
    SAMPLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "S002", "이미 존재하는 샘플입니다"),
    
    // 공통 오류 (C001~C099)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "C002", "입력값 검증에 실패했습니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다"),
    
    // 인증/인가 오류 (A001~A099)
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A001", "인증에 실패했습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "토큰이 만료되었습니다"),
    
    // 서버 오류 (E001~E099)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "서버 내부 오류가 발생했습니다"),
    EXTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "E002", "외부 서비스 오류가 발생했습니다");
    
    private final HttpStatus status;  // HTTP 상태 정보 포함
    private final String code;        // 구체적인 에러 코드
    private final String message;     // 에러 메시지
}
```

### 3. GlobalExceptionHandler 업데이트 (2025-10-09 최신)

#### **BusinessException 커스텀 메시지 처리**
```java
/**
 * 비즈니스 예외 처리 - 커스텀 메시지 detail 지원
 * 
 * @param ex BusinessException
 * @return 에러 응답
 */
@ExceptionHandler(BusinessException.class)
public AxResponseEntity<Void> handleBusinessException(BusinessException ex) {
    log.warn("Business exception occurred: {}", ex.getMessage(), ex);
    
    ErrorCode errorCode = ex.getErrorCode();
    String message = ex.getMessage();
    
    // 커스텀 메시지가 ErrorCode의 기본 메시지와 다른 경우, 커스텀 메시지를 detail로 설정
    if (message != null && !message.equals(errorCode.getMessage())) {
        return AxResponseEntity.error(errorCode, errorCode.getMessage(), message);
    } else {
        return AxResponseEntity.error(errorCode);
    }
}

/**
 * 커스텀 예외 처리 - 커스텀 메시지 detail 지원
 * 
 * @param ex CustomException
 * @return 에러 응답
 */
@ExceptionHandler(CustomException.class)
public AxResponseEntity<Void> handleCustomException(CustomException ex) {
    log.warn("Custom exception occurred: {}", ex.getMessage(), ex);
    
    ErrorCode errorCode = ex.getErrorCode();
    String message = ex.getMessage();
    
    // 커스텀 메시지가 ErrorCode의 기본 메시지와 다른 경우, 커스텀 메시지를 detail로 설정
    if (message != null && !message.equals(errorCode.getMessage())) {
        return AxResponseEntity.error(errorCode, errorCode.getMessage(), message);
    } else {
        return AxResponseEntity.error(errorCode);
    }
}
```

#### **AxResponseEntity 확장 메서드**
```java
/**
 * ErrorCode를 사용한 실패 응답 생성 (커스텀 메시지와 상세 정보)
 * 
 * @param errorCode ErrorCode 객체
 * @param customMessage 커스텀 메시지
 * @param details 상세 정보
 * @param <T> 데이터 타입
 * @return 실패 응답
 */
public static <T> AxResponseEntity<T> error(ErrorCode errorCode, String customMessage, String details) {
    AxResponse<T> response = createFailureWithErrorCodeAndDetails(customMessage, errorCode, details);
    return new AxResponseEntity<T>(response, errorCode.getStatus());
}
```

#### **응답 구조 예시**
```java
// 사용 예시
throw new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자 ID 123을 찾을 수 없습니다.");

// 응답 구조
{
  "success": false,
  "message": "사용자를 찾을 수 없습니다",  // ErrorCode의 기본 메시지
  "error": {
    "hscode": "NOT_FOUND",
    "code": "U001", 
    "message": "사용자를 찾을 수 없습니다",   // ErrorCode의 기본 메시지
    "details": "사용자 ID 123을 찾을 수 없습니다",  // 🎯 커스텀 메시지가 detail에 설정
    "timestamp": "2025-10-09T10:30:00",
    "path": "/api/users/123"
  }
}
```

### 4. 예외 처리 모범 사례 (2025-10-09 업데이트)

#### **기본 예외 사용**
```java
// ErrorCode만 사용 (기본 메시지)
throw new BusinessException(ErrorCode.USER_NOT_FOUND);
```

#### **상세 정보가 포함된 예외 사용**
```java
// 커스텀 메시지로 상세 정보 제공 (detail 필드에 설정됨)
throw new BusinessException(ErrorCode.USER_NOT_FOUND, "사용자 ID " + userId + "을 찾을 수 없습니다.");
throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일 형식이 올바르지 않습니다: " + email);
throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 리소스에 대한 " + permission + " 권한이 없습니다.");
```

#### **권장 패턴**
- **ErrorCode 기본 메시지**: 일반적인 에러 상황 설명
- **커스텀 메시지**: 구체적인 컨텍스트와 값 포함
- **Detail 필드**: 디버깅과 사용자 가이드에 유용한 상세 정보

## 테스트 코드 작성 규칙

### 1. 테스트 클래스 구조
```java
@DisplayName("사용자 서비스 테스트")
class UserServiceTest {
    
    // given-when-then 패턴 사용
    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_Success() {
        // given
        UserCreateRequest request = createUserRequest();
        
        // when
        UserResponse result = userService.createUser(request);
        
        // then
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
    }
    
    // 테스트 헬퍼 메서드는 private으로 하단에 배치
    private UserCreateRequest createUserRequest() {
        // 테스트 데이터 생성 로직
    }
}
```

### 2. Mock 사용 규칙
```java
// given 절에서 Mock 동작 정의
given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

// then 절에서 검증
verify(userRepository).save(any(User.class));
verify(userRepository, times(1)).findById(userId);
```

## 로깅 규칙

### 1. 로그 레벨 사용 기준
```java
log.error("시스템 오류", exception);     // 시스템 오류
log.warn("비정상 상황이지만 처리 가능");   // 경고
log.info("중요한 비즈니스 이벤트");       // 정보
log.debug("디버깅 정보");              // 디버그
```

### 2. 로그 메시지 형식
```java
// 성공 로그
log.info("Created new user with id: {}", savedUser.getId());

// 오류 로그
log.error("Failed to create user with email: {}, error: {}", 
          request.getEmail(), exception.getMessage());

// 비즈니스 로그
log.info("User {} successfully logged in", user.getEmail());
```

## 외부 API 연동 규칙

### 1. Feign Client 설계 패턴
```java
// Feign Client 인터페이스 구조
@FeignClient(
    name = "external-service-client",
    url = "${external.service.base-url}",
    configuration = ExternalClientConfig.class
)
public interface ExternalServiceClient {
    
    /**
     * 외부 서비스 API 호출
     * 
     * @param request 요청 DTO
     * @return 응답 DTO
     */
    @PostMapping(value = "/api/endpoint", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseDto callExternalApi(@RequestBody RequestDto request);
}
```

### 2. External API DTO 규칙 (상세 JavaDoc + OpenAPI 문서화)
```java
// Request DTO - 상세 JavaDoc + OpenAPI 문서화 + snake_case 매핑
/**
 * 외부 API 요청 DTO
 * 
 * <p>외부 API와의 통신을 위한 요청 데이터 구조입니다.
 * OAuth2 표준을 따르며, 다양한 인증 방식을 지원합니다.</p>
 * 
 * <h3>지원하는 인증 타입:</h3>
 * <ul>
 *   <li><strong>password</strong>: 사용자명/비밀번호 기반 인증</li>
 *   <li><strong>client_credentials</strong>: 클라이언트 자격증명 기반 인증</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ExternalRequestDto request = ExternalRequestDto.builder()
 *     .grantType("password")
 *     .clientId("my-client")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "외부 API 요청 정보",
    example = """
        {
          "grant_type": "password",
          "client_id": "my-client"
        }
        """
)
public class ExternalRequestDto {
    
    /**
     * OAuth2 인증 타입
     * 
     * <p>OAuth2 표준에 따른 인증 방식을 지정합니다.</p>
     * 
     * @implNote 일반적으로 "password" 또는 "client_credentials" 값을 사용합니다.
     */
    @JsonProperty("grant_type")
    @Schema(
        description = "OAuth2 인증 타입", 
        example = "password", 
        required = true,
        allowableValues = {"password", "client_credentials", "refresh_token"}
    )
    private String grantType;
    
    /**
     * 클라이언트 식별자
     * 
     * <p>사전에 등록된 OAuth2 클라이언트의 고유 식별자입니다.</p>
     * 
     * @apiNote 클라이언트 등록 시 발급받은 ID를 사용해야 합니다.
     */
    @JsonProperty("client_id")
    @Schema(
        description = "OAuth2 클라이언트 고유 식별자", 
        example = "my-client", 
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String clientId;
}

// Response DTO - 상세 JavaDoc + OpenAPI 문서화 + snake_case 매핑
/**
 * 외부 API 응답 DTO
 * 
 * <p>외부 API로부터 받은 응답 데이터를 담는 구조입니다.
 * OAuth2 토큰 정보를 포함하며, 클라이언트 인증 결과를 나타냅니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>access_token</strong>: API 호출에 사용할 액세스 토큰</li>
 *   <li><strong>expires_in</strong>: 토큰 만료 시간 (초 단위)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ExternalResponseDto response = externalClient.authenticate(request);
 * String token = response.getAccessToken();
 * Integer expiresIn = response.getExpiresIn();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "외부 API 응답 정보",
    example = """
        {
          "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
          "expires_in": 3600
        }
        """
)
public class ExternalResponseDto {
    
    /**
     * OAuth2 액세스 토큰
     * 
     * <p>API 호출 시 Authorization 헤더에 사용할 Bearer 토큰입니다.
     * JWT 형태의 문자열로 제공됩니다.</p>
     * 
     * @implNote Bearer 토큰으로 사용할 때는 "Bearer " 접두사를 추가해야 합니다.
     */
    @JsonProperty("access_token")
    @Schema(
        description = "OAuth2 액세스 토큰 (JWT 형태)", 
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        format = "jwt"
    )
    private String accessToken;
    
    /**
     * 토큰 만료 시간
     * 
     * <p>액세스 토큰의 유효 시간을 초 단위로 나타냅니다.
     * 이 시간이 지나면 토큰을 갱신해야 합니다.</p>
     * 
     * @implNote 일반적으로 3600초(1시간)로 설정됩니다.
     */
    @JsonProperty("expires_in")
    @Schema(
        description = "토큰 만료 시간 (초 단위)", 
        example = "3600",
        minimum = "1"
    )
    private Integer expiresIn;
}
}
```

### 3. Feign Client Configuration
```java
@Configuration
public class ExternalClientConfig {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/x-www-form-urlencoded");
            requestTemplate.header("User-Agent", "AXPORTAL-Backend/1.0");
        };
    }
    
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 3000, 3);
    }
}
```

### 4. External Service 계층 구조
```java
// Service 계층에서 Feign Client 래핑
@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalAuthService {
    
    private final ExternalAuthClient externalAuthClient;
    
    /**
     * 외부 인증 서비스 로그인
     * 
     * @param username 사용자명
     * @param password 비밀번호
     * @return 토큰 응답
     */
    public AccessTokenResponseDto login(String username, String password) {
        try {
            LoginRequestDto request = LoginRequestDto.builder()
                .grantType("password")
                .username(username)
                .password(password)
                .build();
                
            log.info("외부 인증 서비스 로그인 요청: username={}", username);
            AccessTokenResponseDto response = externalAuthClient.login(request);
            log.info("외부 인증 서비스 로그인 성공: username={}", username);
            
            return response;
        } catch (Exception e) {
            log.error("외부 인증 서비스 로그인 실패: username={}, error={}", username, e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_AUTH_FAILED);
        }
    }
}
```

### 5. External API 에러 처리
```java
// Feign Error Decoder
@Component
public class ExternalServiceErrorDecoder implements ErrorDecoder {
    
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 401:
                return new BusinessException(ErrorCode.EXTERNAL_AUTH_FAILED);
            case 422:
                return new ValidationException(ErrorCode.EXTERNAL_VALIDATION_FAILED);
            case 500:
                return new BusinessException(ErrorCode.EXTERNAL_SERVER_ERROR);
            default:
                return new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
```

### 6. External API 테스트 규칙
```java
// WireMock을 사용한 외부 API 테스트
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ExternalAuthServiceTest {
    
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8089))
        .build();
    
    @Test
    @DisplayName("외부 인증 서비스 로그인 성공")
    void login_Success() {
        // given
        wireMock.stubFor(post(urlEqualTo("/api/v1/auth/login"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"access_token\":\"token123\",\"expires_in\":3600}")));
        
        // when & then
        assertThat(externalAuthService.login("user", "pass"))
            .isNotNull()
            .extracting("accessToken")
            .isEqualTo("token123");
    }
}
```

### 7. HTTPS 통신 보안 설정
```java
// SKTAI Feign Client HTTPS 설정
@Configuration
public class SktaiClientConfig {
    
    @Bean
    @ConditionalOnProperty(value = "sktai.api.ssl.enabled", havingValue = "true", matchIfMissing = true)
    public Client feignClient() throws Exception {
        return new Client.Default(
            createTrustAllSslSocketFactory(),
            createHostnameVerifier()
        );
    }
    
    @Bean
    public SSLSocketFactory createTrustAllSslSocketFactory() throws Exception {
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }
    
    @Bean
    public HostnameVerifier createHostnameVerifier() {
        return (hostname, session) -> true; // 개발환경용, 운영환경에서는 실제 검증 필요
    }
    
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            Duration.ofMillis(10000), // 연결 타임아웃
            Duration.ofMillis(60000)  // 읽기 타임아웃
        );
    }
}

// 운영환경용 SSL 설정
@Configuration
@Profile("!local & !elocal")
public class ProductionSslConfig {
    
    @Bean
    public Client feignClient(@Value("${sktai.api.ssl.trust-store}") String trustStore,
                             @Value("${sktai.api.ssl.trust-store-password}") String password) 
                             throws Exception {
        
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream trustStoreIS = new ClassPathResource(trustStore).getInputStream()) {
            keyStore.load(trustStoreIS, password.toCharArray());
        }
        
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        
        return new Client.Default(
            sslContext.getSocketFactory(),
            HttpsURLConnection.getDefaultHostnameVerifier()
        );
    }
}
```

## 보안 규칙

### 1. 인증/인가 처리
```java
// JWT 토큰 검증
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public UserResponse getUserProfile() {
    // 구현
}

// 자원 소유자 검증
@PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
public UserResponse updateUser(@PathVariable Long userId, ...) {
    // 구현
}
```

### 2. 민감한 정보 처리
```java
// 비밀번호는 로그에 출력하지 않음
@ToString.Exclude
private String password;

// API 응답에서 민감한 정보 제외
@JsonIgnore
private String password;
```

## 성능 최적화 규칙

### 1. 데이터베이스 쿼리 최적화
```java
// N+1 문제 방지를 위한 Fetch Join 사용
@Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.active = true")
List<User> findActiveUsersWithOrders();

// 페이징 쿼리 최적화
@Query(value = "SELECT u FROM User u WHERE u.name LIKE %:name%",
       countQuery = "SELECT count(u) FROM User u WHERE u.name LIKE %:name%")
Page<User> findByNameContaining(@Param("name") String name, Pageable pageable);
```

### 2. 캐싱 전략
```java
@Cacheable(value = "users", key = "#id")
public UserResponse getUserById(Long id) {
    // 구현
}

@CacheEvict(value = "users", key = "#result.id")
public UserResponse updateUser(Long id, UserUpdateRequest request) {
    // 구현
}
```

## 문서화 규칙

### 1. API 문서화
```java
@Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
    @ApiResponse(responseCode = "409", description = "이메일 중복")
})
public AxResponseEntity<UserResponse> createUser(
        @Valid @RequestBody UserCreateRequest request) {
    // 구현
}
```

### 2. Swagger UI OAuth2 인증 설정 (2025-10-09 최신)

#### **OpenAPI 설정 (OAuth2PasswordBearer + HTTPBearer)**
```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers())
                // 글로벌 보안 요구사항 설정 - OAuth2PasswordBearer 우선적용
                .security(getSecurityRequirements())
                .components(getComponents());
    }

    private Components getComponents() {
        return new Components()
                // OAuth2 Password Bearer 인증 스키마
                .addSecuritySchemes("OAuth2PasswordBearer", 
                        new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                        .password(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                .tokenUrl("/auth/login")
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("read", "읽기 권한")
                                                        .addString("write", "쓰기 권한")
                                                        .addString("admin", "관리자 권한")
                                                )
                                        )
                                )
                                .description("🔐 OAuth2 자동 인증 (사용자명/비밀번호)")
                )
                // HTTP Bearer 인증 스키마 (JWT)
                .addSecuritySchemes("HTTPBearer", 
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("🔑 HTTP Bearer 토큰 인증 (JWT)")
                );
    }
}
```

#### **application.yml OAuth2 설정**
```yaml
springdoc:
  swagger-ui:
    # OAuth2 및 JWT 인증 관련 UI 설정
    persist-authorization: true # 인증 정보 브라우저에 저장
    oauth:
      client-id: "SK DEMO" # OAuth2 클라이언트 ID
      client-secret:  # OAuth2 클라이언트 시크릿 (개발용)
      use-basic-authentication-with-access-code-grant: false
      use-pkce-with-authorization-code-grant: false
      scopes: read,write,admin # 사용 가능한 OAuth2 스코프
      additional-query-string-params:
        grant_type: password # OAuth2 password grant type 명시
      # Client credentials location을 Request Body로 설정 
      client-credentials-in-token-request-body: true
    # OAuth2 설정 확장
    oauth2:
      client-authentication-scheme: form # form 또는 header
      send-client-credentials-in-body: true # client credentials를 body에 전송
```

#### **LoginController 듀얼 엔드포인트**
```java
@RestController
public class LoginController {
    
    /**
     * JSON 로그인 (HTTPBearer용)
     */
    @PostMapping("/auth/login")
    public AxResponseEntity<JwtTokenRes> login(@Valid @RequestBody LoginReq loginReq) {
        JwtTokenRes tokenRes = authService.login(loginReq);
        return AxResponseEntity.ok(tokenRes, "로그인이 성공적으로 완료되었습니다.");
    }

    /**
     * OAuth2 로그인 (Form-data, OAuth2PasswordBearer용)
     */
    @PostMapping(value = "/auth/login", 
                 consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public JwtTokenRes oauthLogin(@RequestParam("username") String username,
                                  @RequestParam("password") String password) {
        LoginReq loginReq = new LoginReq();
        loginReq.setUsername(username);
        loginReq.setPassword(password);
        
        // OAuth2 표준에 맞게 직접 토큰 객체 반환 (AxResponseEntity 래핑 없이)
        return authService.login(loginReq);
    }
}
```

### 3. 클래스 문서화
```java
/**
 * 사용자 관리 서비스
 * 
 * <p>사용자의 생성, 조회, 수정, 삭제 등의 비즈니스 로직을 처리합니다.
 * 이메일 중복 검증, 비밀번호 암호화 등의 기능을 포함합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-13
 * @version 1.0
 */
@Service
public class UserService {
    // 구현
}
```

## 특별 지시사항

### 1. 코드 생성 우선순위
1. 보안을 최우선으로 고려
2. 예외 처리를 반드시 포함
3. 로깅 구문 적절히 삽입
4. 테스트 가능한 구조로 설계
5. 성능을 고려한 구현
6. 애플리케이션 상수 클래스(ApplicationConstants)를 생성하여 사용
7. sample소스는 아래와 같이 sample디렉토리에 생성
8. Controller에서 사용하는 DTO(/dto/** 디렉토리)생성시 
   - Request는 Req 접미사 사용하고 request 디렉토리에 생성
   - Response는 Res 접미사 사용하고 response 디렉토리에 생성
   - 이외는 기본 디렉토리에 생성
9. **타입 안전성을 최우선으로 고려** (Object 타입 대신 구체적 Generic DTO 사용)
10. **SKTAI 외부 API에서 Generic 응답 DTO 필수 사용** (SktaiResponse<T>, 도메인별 구체적 DTO)
11. **공통 응답 DTO 재사용을 통한 일관성 확보** (SktaiOperationResponse, SktaiResponse<T> 등)

```
# 샘플 도메인 구조 (도메인별 하위 디렉토리)
controller: controller/sample/SampleController.java
service: service/sample/SampleService.java, service/sample/impl/SampleServiceImpl.java
entity: entity/sample/Sample.java
repository: repository/sample/SampleRepository.java
dto: dto/sample/request/SampleCreateReq.java, dto/sample/response/SampleRes.java
mapper: mapper/sample/SampleMapper.java
```

### 2. 응답 형식 최신 규칙 (중요)
```java
// ❌ 기존 방식 (사용 금지)
AxResponseEntity<Page<T>>

// ✅ 최신 방식 (필수 사용)
AxResponseEntity<PageResponse<T>>

// ❌ 기존 AxResponse 구조 (사용 금지)
private final String errorCode;

// ✅ 최신 AxResponse 구조 (필수 사용)
private final Integer statusCode;
private final String statusText;

// ❌ 기존 패키지 구조 (사용 금지)
com.skax.aiplatform.dto.common.PageResponse

// ✅ 최신 패키지 구조 (필수 사용)
com.skax.aiplatform.common.response.PageResponse
```

### 3. 금지사항
- ~~System.out.println() 사용 금지 (로깅 프레임워크 사용)~~
- ~~Raw Type 사용 금지 (제네릭 타입 명시)~~
- ~~Magic Number 사용 금지 (상수로 정의)~~
- ~~하드코딩된 문자열 사용 최소화~~
- ~~try-catch로 예외 숨기기 금지~~
- **AxResponseEntity<Page<T>> 사용 금지** (PageResponse<T> 사용 필수)
- **errorCode 필드 사용 금지** (statusCode, statusText 사용 필수)
- **dto.common 패키지 사용 금지** (common.response 패키지 사용 필수)
- **Controller에서 Entity 사용 금지** (Service 계층에서 처리)
- **DevTools를 운영 환경에 포함 금지** (개발 환경 전용)
- **SKTAI Data API에서 Object 타입 남발 금지** (구체적 DTO 타입 우선 사용)
- **DTO inner class 사용 금지** (별도 파일로 분리하여 visibility 문제 방지)
- **Object 반환 타입 사용 금지** (구체적 Generic DTO 타입 사용 필수)
- **타입 안전성 무시 금지** (명확한 타입 정의로 컴파일 타임 오류 검출)
- **SKTAI Client에서 개별 base-url 사용 금지** (통일된 ${sktai.api.base-url} 사용 필수)

### 4. 권장사항
- Optional 적극 활용으로 NPE 방지
- Stream API 활용한 함수형 프로그래밍
- Builder 패턴 사용으로 객체 생성 명확화
- 인터페이스 기반 설계로 확장성 확보
- 단위 테스트 커버리지 80% 이상 유지
- Mocking 프레임워크 사용으로 외부 의존성 최소화
- Lombok을 활용한 코드 간결화
- MapStruct를 통한 DTO와 Entity 간 변환 최적화
- OpenAPI 3를 통한 API 문서화 자동화
- **PageResponse를 통한 클라이언트 친화적 페이징 응답**
- **AxResponseEntity의 okPage() 메서드 적극 활용**
- **statusCode/statusText 기반 일관된 오류 응답**
- **SKTAI Feign Client에서 OpenAPI 문서화 어노테이션 적극 활용** (타입 안전성과 문서화 품질 향상)
- **External API DTO에는 @JsonProperty와 @Schema 필수 적용** (snake_case ↔ camelCase 매핑 + API 문서화)
- **SKTAI Data API에서 타입 안전성 우선** (명확한 스키마가 있는 경우 구체적 DTO 사용)
- **상속 DTO에서 @EqualsAndHashCode(callSuper = true) 사용** (@Builder 충돌 방지)
- **Generic DTO 패턴 적극 활용** (Object 대신 타입 안전한 응답 DTO 사용)
- **공통 응답 DTO 재사용** (SktaiResponse<T>, SktaiOperationResponse 등 활용)
- **도메인별 구체적 응답 DTO 정의** (FewShotResponse, DatasetResponse 등)
- **타입 안전성을 통한 IDE 지원 극대화** (자동완성, 리팩토링 지원)
- **statusCode/statusText 기반 일관된 오류 응답**
- **SKTAI Feign Client에서 OpenAPI 문서화 어노테이션 적극 활용** (타입 안전성과 문서화 품질 향상)
- **External API DTO에는 @JsonProperty와 @Schema 필수 적용** (snake_case ↔ camelCase 매핑 + API 문서화)
- **SKTAI Data API에서 타입 안전성 우선** (명확한 스키마가 있는 경우 구체적 DTO 사용)
- **상속 DTO에서 @EqualsAndHashCode(callSuper = true) 사용** (@Builder 충돌 방지)
- **SKTAI Client 통일된 Base URL 사용** (모든 Client에서 ${sktai.api.base-url} 사용 필수)

### 5. SKTAI 외부 API 연동 특별 규칙
- **Feign Client**: OpenAPI 어노테이션 필수, 타입 안전성과 문서화 품질 향상
- **DTO 필드 매핑**: @JsonProperty로 snake_case ↔ camelCase 변환 필수
- **에러 처리**: CustomErrorDecoder 구현으로 외부 API 오류를 내부 예외로 변환
- **로깅**: 외부 API 호출 시 요청/응답 로깅 필수 (민감정보 제외)
- **재시도**: Feign Retryer 설정으로 네트워크 오류 시 자동 재시도
- **설정**: 환경별 Base URL 및 타임아웃 설정 분리
- **테스트**: WireMock을 활용한 외부 API 모킹 테스트 작성
- **Base URL 통일**: 모든 SKTAI Feign Client는 ${sktai.api.base-url} 사용 필수
- **HTTPS 통신 보안**: SSL/TLS 설정, 인증서 검증, 타임아웃 최적화
- **Data API 특별 규칙**: 
  - 데이터셋/생성작업/데이터소스는 구체적 DTO 타입 사용
  - 프로세서/생성기는 스키마 불명확으로 Object 타입 허용
  - 모든 DTO 클래스는 별도 파일로 분리 (visibility 문제 방지)
  - Service 계층에서 상세한 로깅과 예외 처리 필수
- **공통 인터셉터**: SktaiRequestInterceptor를 통한 공통 헤더 자동 적용
- **Service 래핑**: 모든 Feign Client는 Service 계층에서 래핑하여 비즈니스 로직과 예외 처리 담당

#### SKTAI Client 구현 패턴

##### Feign Client 인터페이스 패턴
```java
@FeignClient(
    name = "sktai-client-name",
    url = "${sktai.api.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiClientInterface {
    
    /**
     * API 메서드 설명
     * 
     * @param parameter 파라미터 설명
     * @return 응답 설명
     */
    @GetMapping("/api/v1/endpoint")
    ResponseDto methodName(@RequestParam String parameter);
}
```

##### Service 래퍼 패턴
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiService {
    
    private final SktaiClient sktaiClient;
    
    public ResponseDto methodName(String parameter) {
        try {
            log.info("SKTAI API 호출: parameter={}", parameter);
            ResponseDto response = sktaiClient.methodName(parameter);
            log.info("SKTAI API 호출 성공: parameter={}", parameter);
            return response;
        } catch (Exception e) {
            log.error("SKTAI API 호출 실패: parameter={}, error={}", parameter, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
```

##### DTO 매핑 패턴
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SktaiRequestDto {
    
    @JsonProperty("snake_case_field")
    @Schema(description = "필드 설명", example = "예시값")
    private String snakeCaseField;
}
```

## 프로젝트별 특수 요구사항

### 1. AXPORTAL BACKEND 개인 프로젝트 규칙
- 모든 커밋 메시지는 한글로 작성
- API 응답 메시지는 한글로 제공
- 에러 메시지도 사용자 친화적인 한글로 작성
- 주석은 한글로 작성하되 기술적 용어는 영어 병기

### 2. 개발 환경 설정
- 로컬: H2 인메모리 데이터베이스 사용
- 외부개발: PostGreSQL 사용 및 TestContainers 활용한 실제 DB 테스트
- 개발: Tibero 사용 및 TestContainers 활용한 실제 DB 테스트
- 스테이징: Tibero 사용
- 운영: Tibero 사용
- 개발적용시 Docker Compose 활용

#### Spring Boot DevTools 설정
- **개발 환경 전용**: 운영 환경에서는 자동으로 비활성화
- **자동 재시작**: 클래스패스 파일 변경 시 애플리케이션 자동 재시작
- **라이브 리로드**: 정적 리소스 변경 시 브라우저 자동 새로고침
- **JMX 비활성화**: DevTools JMX 연결 오류 방지를 위해 기본적으로 JMX 비활성화
- **설정 파일**: application-elocal.yml에 DevTools 관련 설정 포함

**DevTools 의존성 추가**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

**DevTools 설정 예시** (application-elocal.yml):
```yaml
spring:
  jmx:
    enabled: false  # JMX 연결 오류 방지
  devtools:
    restart:
      enabled: true
      additional-paths: src/main/resources
      exclude: static/**,public/**,templates/**
    livereload:
      enabled: true
      port: 35729
    add-properties: true
```

**JVM 옵션으로 JMX 완전 비활성화**:
```bash
-Dcom.sun.management.jmxremote=false
-Dspring.jmx.enabled=false
```

### 5. SKTAI 외부 API 연동 설정 (실제 구현 기반)
- **Base URL**: 
  - API: ${sktai.api.base-url} (스테이징 환경) - 모든 도메인 통일
- **인증 방식**: OAuth2 Grant Type (password, client_credentials)
- **API 문서**: OpenAPI 3.0 기반 자동 문서 생성
- **응답 형식**: JSON (snake_case 필드명)
- **재시도 정책**: 3회, 지수 백오프
- **타임아웃**: 연결 5초, 읽기 30초

#### SKTAI API 구조 (실제 구현 기준)

##### 주요 외부 시스템 (7개)
```
client/
├── sktai/                    # SKTAI 플랫폼 (19개 서브모듈)
│   ├── agent/               # Agent 관리 API
│   ├── agentgateway/        # Agent Gateway (배치 추론)
│   ├── auth/                # 인증 관리 API
│   ├── data/                # 데이터 관리 API
│   ├── eval/                # 평가 관리 API
│   ├── history/             # 이력 관리 API
│   ├── knowledge/           # 지식 관리 API
│   ├── model/               # 모델 관리 API
│   ├── prompt/              # 프롬프트 관리 API
│   ├── resource/            # 리소스 관리 API (스케일링)
│   ├── resrcMgmt/           # Prometheus 연동
│   ├── serving/             # 서빙 관리 API
│   └── ...
├── datumo/                   # Datumo 연동
├── lablup/                   # Lablup 연동 (배치 아티팩트)
├── elastic/                  # Elasticsearch 연동
├── ione/                     # I-ONE 시스템 연동
├── shinhan/                  # 신한은행 연동
└── udp/                      # UDP 연동
```

##### 배치 및 스케줄링 기능 (실제 구현)
```
batch/
├── IdeDeleteBatch.java       # IDE 정리 배치 (@Scheduled)

client/sktai/agentgateway/
├── dto/request/BatchRequest.java
├── dto/response/BatchResponse.java
└── service/SktaiAgentGatewayService.java  # 배치 추론 서비스

client/lablup/api/
├── dto/request/BatchScanArtifactModelsRequest.java
├── dto/response/BatchScanArtifactModelsResponse.java
└── service/LablupArtifactService.java     # 배치 아티팩트 스캔

client/sktai/resource/
├── dto/request/ResourceScalingRequest.java
└── service/SktaiResourceService.java      # 리소스 스케일링
```

##### 스케일링 및 자동화 기능
- **SKTAI Agent Gateway**: 배치 추론 처리
- **SKTAI Resource**: 자동 스케일링 및 리소스 관리
- **SKTAI Serving**: 모델 서빙 스케일링
- **Lablup Artifact**: 배치 아티팩트 모델 스캔
- **Spring Scheduler**: IDE 정리 등 정기 작업 (@Scheduled)

### 4. CI/CD 관련
- GitHub Actions를 통한 자동 빌드/테스트
- PR시 코드 리뷰 필수
- main 브랜치 직접 푸시 금지
- 태그 기반 배포 전략 사용

## 환경별 설정 가이드

### 1. 외부Local 환경 (elocal) - 기본 개발 환경 ⭐
```yaml
database: H2 in-memory
logging_level: DEBUG
security: 완화된 설정
cache: 비활성화
devtools: 활성화 (JMX 비활성화)
jmx: 비활성화
default_profile: elocal  # 기본 프로필로 설정
```

### 2. 외부개발 환경 (edev)
```yaml
database: PostGreSQL (외부개발 DB)
logging_level: DEBUG
security: 완화된 설정
cache: 비활성화
```

### 3. Local 환경 (local)
```yaml
database: H2 in-memory
logging_level: DEBUG
security: 완화된 설정
cache: 비활성화
```

### 4. 개발 환경 (dev)
```yaml
database: Tibero (개발 DB)
logging_level: DEBUG
security: 완화된 설정
cache: 비활성화
```

### 5. 스테이징 환경 (staging)
```yaml
database: Tibero (테스트 DB)
logging_level: INFO
security: 운영과 동일
cache: 활성화
```

### 6. 운영 환경 (prod)
```yaml
database: Tibero (운영 DB)
logging_level: WARN
security: 강화된 설정
cache: 활성화
monitoring: 전체 활성화
```

## 배치 및 스케줄링 가이드 (실제 구현 기반)

### 1. Spring Scheduler 배치
```java
// IDE 정리 배치 (IdeDeleteBatch.java)
@Slf4j
@Component
@RequiredArgsConstructor
public class IdeDeleteBatch {
    private final IDEService ideService;

    @Value("${kube.ide.delete-batch-startup:true}")
    private boolean runOnStartup;

    // 앱 기동 직후 1회 실행
    @EventListener(ApplicationReadyEvent.class)
    public void runOnceOnStartup() {
        if (runOnStartup) {
            log.info("[BATCH] deleteIdeBatch (startup)");
            ideService.deleteIdeBatch();
        }
    }

    @Scheduled(cron = "${kube.ide.delete-batch-cron}", zone = "Asia/Seoul")
    public void deleteIdeBatch() {
        log.info("[BATCH] deleteIdeBatch start");
        ideService.deleteIdeBatch();
        log.info("[BATCH] deleteIdeBatch end");
    }
}
```

### 2. SKTAI Agent Gateway 배치 추론
```java
// 배치 추론 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchRequest {
    @JsonProperty("inputs")
    private List<Object> inputs;
    
    @JsonProperty("config")
    private Object config;
    
    @JsonProperty("kwargs")
    private Object kwargs;
}

// 배치 추론 서비스
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiAgentGatewayService {
    public BatchResponse batchProcess(String agentId, BatchRequest request, String routerPath) {
        log.info("에이전트 일괄 처리 요청 - agentId: {}, routerPath: {}", agentId, routerPath);
        
        try {
            validateBatchRequest(request);
            BatchResponse response = agentGatewayClient.batch(agentId, routerPath, request);
            log.info("에이전트 일괄 처리 성공 - agentId: {}", agentId);
            return response;
        } catch (BusinessException e) {
            log.error("에이전트 일괄 처리 실패: {}", e.getMessage());
            throw e;
        }
    }
}
```

### 3. Lablup 배치 아티팩트 스캔
```java
// 배치 스캔 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchScanArtifactModelsRequest {
    @JsonProperty("artifact_batch")
    private List<ArtifactBatchItem> artifactBatch;
    
    @JsonProperty("batch_options")
    private Object batchOptions;
}

// 배치 스캔 서비스
@Service
@Slf4j
@RequiredArgsConstructor
public class LablupArtifactService {
    public BatchScanArtifactModelsResponse batchScanArtifactModels(BatchScanArtifactModelsRequest request) {
        try {
            log.info("🔴 Lablup 배치 아티팩트 모델 스캔 요청");
            LablupResponse<BatchScanArtifactModelsResponse> response = lablupArtifactClient
                    .batchScanArtifactModels(request);
            log.info("🔴 Lablup 배치 아티팩트 모델 스캔 성공");
            return response.getData();
        } catch (Exception e) {
            log.error("🔴 Lablup 배치 아티팩트 모델 스캔 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
```

### 4. SKTAI 리소스 스케일링
```java
// 리소스 스케일링 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceScalingRequest {
    @JsonProperty("resource_id")
    private String resourceId;
    
    @JsonProperty("scaling_action")
    private String scalingAction;      // scale_out, scale_in, scale_up, scale_down
    
    @JsonProperty("target_capacity")
    private Integer targetCapacity;
    
    @JsonProperty("trigger")
    private String trigger;            // cpu_threshold, memory_threshold, schedule
    
    @JsonProperty("trigger_conditions")
    private Object triggerConditions;
}

// 리소스 스케일링 서비스
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiResourceService {
    public ResourceAllocationResponse scaleResource(String resourceId, ResourceScalingRequest request) {
        log.debug("리소스 스케일링 요청 - resourceId: {}, action: {}", resourceId, request.getScalingAction());
        
        try {
            ResourceAllocationResponse response = resourceClient.scaleResource(resourceId, request);
            log.debug("리소스 스케일링 성공 - resourceId: {}", resourceId);
            return response;
        } catch (Exception e) {
            log.error("리소스 스케일링 실패 - resourceId: {}", resourceId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
    
    public void setAutoScalingPolicy(String resourceId, Object policy) {
        // 자동 스케일링 정책 설정
    }
    
    public void disableAutoScaling(String resourceId) {
        // 자동 스케일링 해제
    }
}
```

### 5. 모델 서빙 스케일링
```java
// 서빙 생성 시 오토스케일링 설정
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServingCreate {
    @JsonProperty("min_replicas")
    private Integer minReplicas;        // 최소 레플리카 수
    
    @JsonProperty("max_replicas")
    private Integer maxReplicas;        // 최대 레플리카 수
    
    @JsonProperty("autoscaling_class")
    private String autoscalingClass;    // 오토스케일링 클래스
    
    @JsonProperty("autoscaling_metric")
    private String autoscalingMetric;   // 오토스케일링 메트릭
    
    @JsonProperty("target")
    private Integer target;             // 스케일링 타겟 값
}

// 서빙 스케일링 서비스
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiServingService {
    public ServingResponse scaleServing(String servingId, ServingScale request) {
        log.debug("서빙 스케일링 - servingId: {}, replicas: {}", servingId, request.getReplicas());
        
        try {
            ServingResponse response = servingClient.scaleServing(servingId, request);
            log.debug("서빙 스케일링 성공 - servingId: {}", servingId);
            return response;
        } catch (Exception e) {
            log.error("서빙 스케일링 실패 - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
```

## 팀 개발 규칙

### 1. 코드 리뷰 체크리스트
- [ ] 보안 취약점 검토 완료
- [ ] 예외 처리 적절성 확인
- [ ] 테스트 코드 포함 여부
- [ ] 성능 영향도 검토
- [ ] 문서화 완료
- [ ] **PageResponse 사용 여부 확인** (Page<T> 대신)
- [ ] **AxResponse 최신 구조 사용 여부 확인** (statusCode/statusText)

### 2. 브랜치 전략
```
main: 운영 배포 브랜치
develop: 개발 통합 브랜치
feature/기능명: 기능 개발 브랜치
hotfix/이슈번호: 긴급 수정 브랜치
```

### 3. 커밋 메시지 규칙
- 한글 사용: 모든 메시지는 한글로 작성합니다.
- 명확성: 커밋이 무엇을 변경했는지, 왜 변경했는지 명확하게 전달합니다.
- 일관성: 프로젝트 내에서 정한 규칙을 일관성 있게 따릅니다.
- 제목 (Subject Line)
   - 형식: [타입]: [간결한 요약 (명령형 어조)]
   - 길이: 50자 이내 (권장)
   - 명령형 어조 사용: "~을 추가", "~을 수정", "~을 제거" 와 같이 동사로 시작합니다. (예: "기능 추가", "버그 수정")
   - 마침표 사용 금지: 제목 끝에는 마침표를 찍지 않습니다.

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링 (PageResponse 적용, AxResponse 구조 변경 등)
test: 테스트 코드 추가/수정
chore: 기타 작업
```

## 성과 지표
- 코드 일관성: 90% 이상
- 예외 처리 포함률: 95% 이상
- 테스트 커버리지: 80% 이상
- 보안 규칙 준수율: 100%
- 문서화 완성도: 85% 이상
- **PageResponse 적용률: 100%** (새로 추가)
- **최신 AxResponse 구조 적용률: 100%** (새로 추가)
- **Generic DTO 적용률: 100%** (새로 추가)
- **Object 타입 제거율: 100%** (새로 추가)
- **타입 안전성 보장율: 100%** (새로 추가)

## AI 코딩 최적화 설정

### 1. Copilot 코드 생성 우선순위 (최신)
```yaml
priorities:
  - security_first: true
  - performance_aware: true
  - test_driven: true
  - documentation_included: true
  - korean_comments: true
  - page_response_required: true      # PageResponse 필수 사용
  - latest_ax_response: true          # 최신 AxResponse 구조 사용
  - unified_response_format: true     # 통합 응답 형식 사용
  - sktai_client_openapi_required: true # SKTAI Client에서 OpenAPI 어노테이션 필수
  - external_api_comprehensive_docs: true # External API DTO 상세 JavaDoc + OpenAPI 문서화 필수
  - sktai_interceptor_separation: true # SKTAI RequestInterceptor 별도 클래스 분리
  - sktai_comprehensive_user_mgmt: true # SKTAI 사용자 관리 포괄적 구현
  - sktai_data_api_type_safety: true  # SKTAI Data API 타입 안전성 우선
  - sktai_knowledge_api_complete: true # SKTAI Knowledge API 완전 구현 (2025-08-14)
  - dto_class_separation: true        # DTO inner class 분리 필수
  - generic_dto_pattern: true         # Generic DTO 패턴 사용 필수
  - object_type_prohibition: true     # Object 반환 타입 사용 금지
  - type_safety_maximization: true    # 타입 안전성 극대화
  - sktai_unified_base_url: true      # SKTAI Client 통일된 Base URL 사용 필수
  - https_security_enabled: true      # HTTPS 통신 보안 설정 필수
```

### 2. 템플릿 우선순위 (최신)
```java
// 엔티티 생성시 반드시 포함할 패턴
@Entity
@Table(name = "테이블명")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class EntityName extends BaseEntity {
    // 구현
}

// Controller 페이징 메서드 필수 패턴
@GetMapping
public AxResponseEntity<PageResponse<EntityResponse>> getEntities(
        @PageableDefault(size = 20) Pageable pageable) {
    PageResponse<EntityResponse> entities = entityService.getEntities(pageable);
    return AxResponseEntity.ok(entities, "목록을 성공적으로 조회했습니다.");
}

// Service 페이징 메서드 필수 패턴
@Transactional(readOnly = true)
public PageResponse<EntityResponse> getEntities(Pageable pageable) {
    Page<Entity> entityPage = entityRepository.findAll(pageable);
    Page<EntityResponse> responsePage = entityPage.map(entityMapper::toResponse);
    return PageResponse.from(responsePage);
}

// SKTAI Generic 응답 DTO 패턴 (Object 타입 대체)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SktaiResponse<T> {
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private T data;
    
    @JsonProperty("error")
    private String error;
}

// SKTAI 도메인별 구체적 응답 DTO 패턴
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FewShotResponse {
    @JsonProperty("few_shot_uuid")
    private String fewShotUuid;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    // 기타 필드들...
}

// SKTAI Client Object 타입 금지 패턴
// ❌ 금지: Object 반환 타입
@PostMapping("/api/v1/few-shots")
Object createFewShot(@RequestBody FewShotCreateRequest request);

// ✅ 권장: 구체적 DTO 반환 타입
@PostMapping("/api/v1/few-shots")
FewShotResponse createFewShot(@RequestBody FewShotCreateRequest request);

// SKTAI User Client 필수 패턴
@GetMapping("/api/v1/users")
UsersResponseDto getUsers(
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search
);

// SKTAI Service 로깅 패턴 (타입 안전성 포함)
public FewShotResponse createFewShot(FewShotCreateRequest request) {
    try {
        log.info("SKTAI Few-shot 생성 요청: name={}", request.getName());
        FewShotResponse response = sktaiFewShotClient.createFewShot(request);
        log.info("SKTAI Few-shot 생성 성공: uuid={}", response.getFewShotUuid());
        return response;
    } catch (Exception e) {
        log.error("SKTAI Few-shot 생성 실패: name={}", request.getName(), e);
        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
    }
}

// SKTAI Knowledge Repository Client 필수 패턴 (신규 2025-08-14)
@FeignClient(
    name = "sktai-knowledge-repos-client",
    url = "${sktai.knowledge.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiReposClient {
    
    /**
     * Knowledge Repo 신규 생성
     * 
     * @param request Repository 생성 요청
     * @return 생성된 Repository ID
     */
    @PostMapping("/api/v1/knowledge/repos")
    RepoCreateResponse createRepo(@RequestBody RepoCreate request);
    
    /**
     * Repo ID로 Knowledge Repo 상세 조회
     * 
     * @param repoId Repository ID
     * @return Repository 상세 정보
     */
    @GetMapping("/api/v1/knowledge/repos/{repoId}")
    RepoWithCollection getRepo(@PathVariable String repoId);
}

// SKTAI Knowledge Service 필수 패턴 (신규 2025-08-14)
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiReposService {
    
    private final SktaiReposClient sktaiReposClient;
    
    public RepoCreateResponse createRepo(RepoCreate request) {
        log.debug("Repository 생성 요청 - name: {}", request.getName());
        
        try {
            RepoCreateResponse response = sktaiReposClient.createRepo(request);
            log.debug("Repository 생성 성공 - repoId: {}", response.getRepoId());
            return response;
        } catch (Exception e) {
            log.error("Repository 생성 실패 - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Repository 생성에 실패했습니다: " + e.getMessage());
        }
    }
}

// SKTAI Knowledge DTO 패턴 (신규 2025-08-14) - 상세 JavaDoc + OpenAPI 문서화
/**
 * SKTAI Knowledge Repository 생성 요청 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 새로운 Repository를 생성하기 위한 요청 데이터 구조입니다.
 * 프로젝트 기반으로 Repository를 생성하며, 데이터 소스와 연결하여 지식 저장소를 구축합니다.</p>
 * 
 * <h3>필수 필드:</h3>
 * <ul>
 *   <li><strong>project_id</strong>: Repository가 속할 프로젝트 ID</li>
 *   <li><strong>name</strong>: Repository 고유 이름</li>
 *   <li><strong>datasource_id</strong>: 연결할 데이터 소스 ID</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoCreate request = RepoCreate.builder()
 *     .projectId("project-123")
 *     .name("MyKnowledgeRepo")
 *     .description("AI 학습용 지식 저장소")
 *     .datasourceId("datasource-456")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 * @see RepoCreateResponse Repository 생성 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Knowledge Repository 생성 요청 정보",
    example = """
        {
          "project_id": "project-123",
          "name": "MyKnowledgeRepo",
          "description": "AI 학습용 지식 저장소",
          "datasource_id": "datasource-456"
        }
        """
)
public class RepoCreate {
    
    /**
     * 프로젝트 식별자
     * 
     * <p>Repository가 속할 프로젝트의 고유 식별자입니다.
     * 프로젝트는 Repository의 접근 권한과 관리 범위를 결정합니다.</p>
     * 
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 권한을 가져야 합니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Repository가 속할 프로젝트 ID", 
        example = "project-123",
        required = true,
        minLength = 5,
        maxLength = 50
    )
    private String projectId;
    
    /**
     * Repository 이름
     * 
     * <p>Knowledge Repository의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 영문자와 언더스코어를 사용합니다.</p>
     * 
     * @implNote 이름은 생성 후 변경 가능하지만, URL 경로에 사용되므로 신중하게 결정해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Repository 고유 이름 (영문자, 숫자, 언더스코어만 허용)", 
        example = "MyKnowledgeRepo",
        required = true,
        pattern = "^[a-zA-Z][a-zA-Z0-9_]*$",
        minLength = 3,
        maxLength = 100
    )
    private String name;
    
    /**
     * Repository 설명
     * 
     * <p>Repository의 목적과 용도를 설명하는 텍스트입니다.
     * 다른 사용자들이 Repository의 목적을 이해할 수 있도록 명확하게 작성합니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "Repository 설명 (목적과 용도)", 
        example = "AI 학습용 지식 저장소",
        maxLength = 500
    )
    private String description;
    
    /**
     * 데이터 소스 식별자
     * 
     * <p>Repository와 연결할 데이터 소스의 식별자입니다.
     * 데이터 소스는 Repository의 지식 콘텐츠 원본을 제공합니다.</p>
     * 
     * @apiNote 데이터 소스는 사전에 생성되어야 하며, 연결 가능한 상태여야 합니다.
     */
    @JsonProperty("datasource_id")
    @Schema(
        description = "연결할 데이터 소스 ID", 
        example = "datasource-456",
        required = true,
        minLength = 5,
        maxLength = 50
    )
    private String datasourceId;
}

// SKTAI Data API Client 필수 패턴 (신규)
@FeignClient(
    name = "sktai-data-api-client",
    url = "${sktai.data.base-url}",
    configuration = SktaiClientConfig.class
)
public interface SktaiDataApiClient {
    
    /**
     * API 메서드 설명
     * 
     * @param parameter 파라미터 설명
     * @return 응답 설명
     */
    @GetMapping("/api/v1/endpoint")
    ResponseDto methodName(@RequestParam String parameter);
}

// SKTAI Data Service 패턴 (신규)
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiDataService {
    
    private final SktaiDataClient dataClient;
    
    public DataSourceList getDatasources(Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.info("데이터 소스 목록 조회 요청 - page: {}, size: {}", page, size);
            DataSourceList result = dataClient.getDatasources(page, size, sort, filter, search);
            log.info("데이터 소스 목록 조회 성공");
            return result;
        } catch (Exception e) {
            log.error("데이터 소스 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "데이터 소스 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
}

// SKTAI Data DTO 패턴 (신규) - 상세 JavaDoc + OpenAPI 문서화 + 타입 안전성 우선
/**
 * SKTAI Data 소스 생성 요청 DTO
 * 
 * <p>SKTAI Data 시스템에서 새로운 데이터 소스를 생성하기 위한 요청 데이터 구조입니다.
 * 프로젝트 기반으로 데이터 소스를 생성하며, AI 학습 및 분석에 사용할 데이터를 관리합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>project_id</strong>: 데이터 소스가 속할 프로젝트</li>
 *   <li><strong>name</strong>: 데이터 소스의 고유한 이름</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * DataSourceCreate request = DataSourceCreate.builder()
 *     .projectId("project-123")
 *     .name("Customer Dataset")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 * @see DataSourceDetail 생성된 데이터 소스 상세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 데이터 소스 생성 요청 정보",
    example = """
        {
          "project_id": "project-123",
          "name": "Customer Dataset"
        }
        """
)
public class DataSourceCreate {
    
    /**
     * 프로젝트 식별자
     * 
     * <p>데이터 소스가 속할 프로젝트의 고유 식별자입니다.
     * 프로젝트는 데이터 소스의 접근 권한과 관리 범위를 결정합니다.</p>
     * 
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 권한을 가져야 합니다.
     */
    @JsonProperty("project_id")
    @Schema(
        description = "데이터 소스가 속할 프로젝트 ID", 
        example = "project-123",
        required = true,
        minLength = 5,
        maxLength = 50
    )
    private String projectId;
    
    /**
     * 데이터 소스 이름
     * 
     * <p>데이터 소스의 고유한 이름입니다.
     * 프로젝트 내에서 중복될 수 없으며, 데이터의 목적이나 내용을 나타내는 명확한 이름을 사용합니다.</p>
     * 
     * @implNote 이름은 생성 후 수정 가능하지만, 참조 관계를 고려하여 신중하게 변경해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "데이터 소스 고유 이름 (데이터의 목적이나 내용을 명확히 표현)", 
        example = "Customer Dataset",
        required = true,
        minLength = 3,
        maxLength = 100
    )
    private String name;
}

// SKTAI Data 상속 DTO 패턴 (신규) - 상세 JavaDoc + @Builder 충돌 해결
/**
 * SKTAI 데이터 소스 확장 상세 정보 DTO
 * 
 * <p>기본 데이터 소스 상세 정보에 추가적인 파일 정보를 포함하는 확장된 데이터 구조입니다.
 * DataSourceDetail을 상속받아 파일 목록 정보를 추가로 제공합니다.</p>
 * 
 * <h3>상속 정보:</h3>
 * <ul>
 *   <li><strong>부모 클래스</strong>: DataSourceDetail (기본 데이터 소스 정보)</li>
 *   <li><strong>추가 정보</strong>: files (연관된 파일 목록)</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>데이터 소스 상세 조회 시 파일 정보가 필요한 경우</li>
 *   <li>파일 기반 데이터 소스의 전체 정보 표시</li>
 *   <li>데이터 소스 관리 화면에서의 상세 정보 표시</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 * @see DataSourceDetail 기본 데이터 소스 정보
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "SKTAI 데이터 소스 확장 상세 정보 (파일 목록 포함)",
    example = """
        {
          "id": "datasource-123",
          "name": "Customer Dataset",
          "project_id": "project-456",
          "created_at": "2025-08-14T10:30:00Z",
          "files": [
            {
              "id": "file-001",
              "name": "customers.csv",
              "size": 1024000
            }
          ]
        }
        """
)
public class DataSourceExtendedDetail extends DataSourceDetail {
    
    /**
     * 연관된 파일 목록
     * 
     * <p>데이터 소스에 업로드되거나 연결된 파일들의 정보 목록입니다.
     * 각 파일은 메타데이터 정보(ID, 이름, 크기 등)를 포함합니다.</p>
     * 
     * @implNote 파일 타입은 Object로 정의되어 다양한 파일 메타데이터 구조를 수용할 수 있습니다.
     * @apiNote 파일이 없는 경우 빈 배열이 반환됩니다.
     */
    @JsonProperty("files")
    @Schema(
        description = "데이터 소스에 연관된 파일 목록",
        example = """
            [
              {
                "id": "file-001",
                "name": "customers.csv",
                "size": 1024000,
                "type": "text/csv"
              }
            ]
            """
    )
    private List<Object> files;
}
```

### 3. 의존성 주입 패턴
```java
// Constructor Injection 우선 사용
@RequiredArgsConstructor
public class ServiceClass {
    private final Repository repository;
    // Field Injection 금지, Setter Injection 최소화
}
```

### 4. Import 패턴 (최신)
```java
// 필수 import 패턴
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
// dto.common.PageResponse 사용 금지

// SKTAI External API DTO import 패턴
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
// SKTAI Feign Client는 상세 JavaDoc + OpenAPI 어노테이션 필수 적용
```

이 가이드는 2025년 8월 14일 기준 최신 변경사항과 SKTAI Feign Client DTO의 상세 JavaDoc + OpenAPI 문서화 적용을 반영하여 업데이트되었습니다.