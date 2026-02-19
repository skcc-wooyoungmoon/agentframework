# SKTAI Authentication API 문서

## 개요
SKTAI Authentication 시스템은 사용자 인증, 권한 관리, 프로젝트 관리를 담당하는 종합 인증 플랫폼입니다. OAuth2 기반의 토큰 인증, RBAC(Role-Based Access Control) 권한 모델, 그룹 기반 사용자 관리 등의 기능을 제공합니다.

## Client 정보

### 1. SktaiAuthClient
인증 및 토큰 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- OAuth2 토큰 발급 및 갱신
- 사용자 인증 및 권한 검증
- SSO(Single Sign-On) 연동
- 클라이언트 자격증명 관리

### 2. SktaiUserClient
사용자 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 사용자 정보 조회, 등록, 수정, 삭제
- 사용자 역할(Role) 매핑 관리
- 사용자 그룹 매핑 관리
- 비밀번호 재설정

### 3. SktaiProjectClient
프로젝트 및 역할 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 프로젝트 생성, 조회, 수정, 삭제
- 프로젝트별 역할(Role) 관리
- 역할별 사용자 할당 관리
- 권한 매트릭스 관리

### 4. SktaiGroupClient
그룹 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 사용자 그룹 목록 조회
- 그룹 기반 권한 관리

## API 목록

### Authentication APIs (SktaiAuthClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 토큰 발급 | POST | `/api/v1/auth/token` | grant_type, client_id, client_secret, 기타 | Object | OAuth2 액세스 토큰을 발급합니다 |
| 토큰 갱신 | POST | `/api/v1/auth/token/refresh` | refresh_token | Object | 리프레시 토큰으로 새 액세스 토큰을 발급합니다 |
| 토큰 폐기 | POST | `/api/v1/auth/token/revoke` | token, token_type_hint | Object | 토큰을 폐기합니다 |
| 토큰 검증 | GET | `/api/v1/auth/token/introspect` | token | Object | 토큰의 유효성과 정보를 검증합니다 |
| 클라이언트 등록 | POST | `/api/v1/auth/clients` | CreateClient | Object | 새로운 OAuth2 클라이언트를 등록합니다 |
| SSO 로그인 | GET | `/api/v1/auth/login/sso` | - | Object | SSO 로그인 페이지로 리다이렉트합니다 |
| SSO 콜백 | GET | `/api/v1/auth/login/sso/callback` | code, state | Object | SSO 로그인 콜백을 처리합니다 |

### User Management APIs (SktaiUserClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 내 정보 조회 | GET | `/api/v1/users/me` | - | Object | 현재 로그인한 사용자의 정보를 조회합니다 |
| 사용자 목록 조회 | GET | `/api/v1/users` | page, size, sort, filter, search | Object | 사용자 목록을 페이징하여 조회합니다 |
| 사용자 상세 조회 | GET | `/api/v1/users/{userId}` | userId | Object | 특정 사용자의 상세 정보를 조회합니다 |
| 사용자 등록 | POST | `/api/v1/users/register` | Object | Object | 새로운 사용자를 등록합니다 |
| 사용자 정보 수정 | PUT | `/api/v1/users/{userId}` | userId, Object | Object | 사용자 정보를 수정합니다 |
| 사용자 삭제 | DELETE | `/api/v1/users/{userId}` | userId | void | 사용자를 삭제합니다 |
| 사용자 역할 조회 | GET | `/api/v1/users/{userId}/role-mappings` | userId | Object | 사용자에게 할당된 역할 목록을 조회합니다 |
| 사용자 역할 할당 | POST | `/api/v1/users/{userId}/role-mappings` | userId, Object | Object | 사용자에게 역할을 할당합니다 |
| 사용자 역할 해제 | DELETE | `/api/v1/users/{userId}/role-mappings/{roleId}` | userId, roleId | void | 사용자의 역할을 해제합니다 |
| 사용자 그룹 조회 | GET | `/api/v1/users/{userId}/group-mappings` | userId | Object | 사용자가 속한 그룹 목록을 조회합니다 |
| 사용자 그룹 할당 | POST | `/api/v1/users/{userId}/group-mappings` | userId, Object | Object | 사용자를 그룹에 할당합니다 |
| 사용자 그룹 해제 | DELETE | `/api/v1/users/{userId}/group-mappings/{groupId}` | userId, groupId | void | 사용자를 그룹에서 해제합니다 |
| 비밀번호 재설정 | PUT | `/api/v1/users/{userId}/reset-password` | userId, Object | Object | 사용자의 비밀번호를 재설정합니다 |

### Project Management APIs (SktaiProjectClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 프로젝트 생성 | POST | `/api/v1/projects` | Object | Object | 새로운 프로젝트를 생성합니다 |
| 프로젝트 목록 조회 | GET | `/api/v1/projects` | page, size, sort, filter, search | Object | 프로젝트 목록을 페이징하여 조회합니다 |
| 프로젝트 상세 조회 | GET | `/api/v1/projects/{project_id}` | projectId | Object | 특정 프로젝트의 상세 정보를 조회합니다 |
| 프로젝트 수정 | PUT | `/api/v1/projects/{project_id}` | projectId, Object | Object | 프로젝트 정보를 수정합니다 |
| 프로젝트 삭제 | DELETE | `/api/v1/projects/{project_id}` | projectId | void | 프로젝트를 삭제합니다 |
| 프로젝트 역할 조회 | GET | `/api/v1/projects/{client_id}/roles` | clientId | Object | 프로젝트의 역할 목록을 조회합니다 |
| 프로젝트 역할 생성 | POST | `/api/v1/projects/{client_id}/roles` | clientId, Object | Object | 프로젝트에 새로운 역할을 생성합니다 |
| 프로젝트 역할 수정 | PUT | `/api/v1/projects/{client_id}/roles/{role_name}` | clientId, roleName, Object | Object | 프로젝트 역할 정보를 수정합니다 |
| 프로젝트 역할 삭제 | DELETE | `/api/v1/projects/{client_id}/roles/{role_name}` | clientId, roleName | void | 프로젝트 역할을 삭제합니다 |
| 역할 상세 조회 | GET | `/api/v1/projects/roles/{role_id}` | roleId | Object | 특정 역할의 상세 정보를 조회합니다 |
| 역할별 사용자 조회 | GET | `/api/v1/projects/{client_id}/roles/{role_name}/users` | clientId, roleName | Object | 특정 역할에 할당된 사용자 목록을 조회합니다 |

### Group Management APIs (SktaiGroupClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 그룹 목록 조회 | GET | `/api/v1/groups` | - | Object | 사용자 그룹 목록을 조회합니다 |

## DTO 클래스

### Request DTOs
- **CreateClient**: OAuth2 클라이언트 생성 요청
- **UpdateClient**: OAuth2 클라이언트 수정 요청 (현재 미사용)

### 공통 타입
- **Object**: 동적 스키마를 가진 요청/응답에 사용
- **String**: 사용자 ID, 역할 ID, 그룹 ID 등 식별자

### Authentication DTOs
- 토큰 요청/응답, 클라이언트 정보 등은 Object 타입으로 추상화되어 있음
- OAuth2 표준 파라미터들 (grant_type, client_id, client_secret, refresh_token 등)

## API 상세 정보

### 1. OAuth2 인증 흐름

#### Authorization Code Flow
1. 클라이언트가 인증 서버로 사용자를 리다이렉트
2. 사용자가 로그인하고 권한 승인
3. 인증 서버가 인증 코드를 클라이언트로 전달
4. 클라이언트가 인증 코드로 액세스 토큰 요청

#### Client Credentials Flow
직접적인 API 호출을 위한 서비스 계정 인증:
```http
POST /api/v1/auth/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id={client_id}&client_secret={client_secret}
```

#### Refresh Token Flow
액세스 토큰 갱신:
```http
POST /api/v1/auth/token/refresh
Content-Type: application/x-www-form-urlencoded

refresh_token={refresh_token}
```

### 2. 사용자 관리

#### 사용자 등록
새로운 사용자를 시스템에 등록합니다:
- 기본 정보 (이름, 이메일, 사용자명)
- 초기 비밀번호 설정
- 기본 역할 할당

#### 사용자 정보 수정
기존 사용자의 정보를 수정합니다:
- 개인 정보 업데이트
- 상태 변경 (활성/비활성)
- 추가 속성 설정

#### 역할 기반 권한 관리
사용자에게 역할을 할당하여 권한을 관리합니다:
- 다중 역할 할당 가능
- 역할별 권한 매트릭스
- 동적 권한 변경

### 3. 프로젝트 관리

#### 프로젝트 생성
새로운 프로젝트를 생성하고 설정합니다:
- 프로젝트 메타데이터
- 초기 역할 구성
- 접근 정책 설정

#### 역할 관리
프로젝트별 세분화된 역할을 관리합니다:
- 커스텀 역할 정의
- 권한 매트릭스 구성
- 역할 상속 관계 설정

### 4. 그룹 기반 관리

#### 그룹 구조
조직 구조를 반영한 그룹 기반 관리:
- 계층적 그룹 구조
- 그룹별 기본 권한
- 그룹 상속 정책

## 권한 모델

### RBAC (Role-Based Access Control)
- **사용자**: 시스템 사용자
- **역할**: 권한의 집합
- **권한**: 특정 리소스에 대한 작업 권한
- **프로젝트**: 권한 범위의 경계

### 권한 계층
1. **시스템 관리자**: 전체 시스템 관리 권한
2. **프로젝트 관리자**: 특정 프로젝트 관리 권한
3. **개발자**: 프로젝트 내 개발 권한
4. **사용자**: 기본 사용 권한

### 권한 상속
- 그룹 권한은 하위 그룹으로 상속
- 역할 권한은 사용자에게 부여
- 명시적 거부 권한이 허용 권한보다 우선

## 보안 고려사항

### 토큰 보안
- 액세스 토큰은 짧은 만료 시간 (1시간)
- 리프레시 토큰은 긴 만료 시간 (30일)
- 토큰 폐기 시 즉시 무효화

### 비밀번호 정책
- 최소 8자 이상
- 대소문자, 숫자, 특수문자 조합
- 주기적 변경 권장
- 이전 비밀번호 재사용 제한

### 감사 로그
- 모든 인증 시도 기록
- 권한 변경 이력 추적
- 비정상적 접근 패턴 감지

## 인증 및 권한
- **시스템 API**: Bearer Token 인증 필요
- **관리 API**: 관리자 권한 필요
- **사용자 API**: 본인 또는 관리 권한 필요

## 오류 코드
- **400 Bad Request**: 잘못된 요청 파라미터
- **401 Unauthorized**: 인증 실패
- **403 Forbidden**: 권한 부족
- **404 Not Found**: 사용자/프로젝트를 찾을 수 없음
- **409 Conflict**: 중복된 사용자명/이메일
- **422 Unprocessable Entity**: 입력값 검증 실패

## 사용 예시

### Java (Spring Boot)
```java
@Autowired
private SktaiAuthClient authClient;

@Autowired
private SktaiUserClient userClient;

@Autowired
private SktaiProjectClient projectClient;

// 클라이언트 자격증명으로 토큰 발급
Object tokenRequest = Map.of(
    "grant_type", "client_credentials",
    "client_id", "my-client",
    "client_secret", "my-secret"
);
Object tokenResponse = authClient.issueToken(tokenRequest);

// 사용자 정보 조회
Object currentUser = userClient.getCurrentUser();

// 프로젝트 생성
Object projectRequest = Map.of(
    "name", "My Project",
    "description", "Project description"
);
Object project = projectClient.createProject(projectRequest);

// 사용자에게 역할 할당
Object roleRequest = Map.of(
    "roleId", "developer",
    "projectId", project.get("id")
);
userClient.assignUserRole("user123", roleRequest);
```

## 설정
application.yml에서 SKTAI API 설정을 구성합니다:

```yaml
sktai:
  api:
    base-url: ${sktai.api.base-url}
    timeout:
      connect: 10000
      read: 30000

  auth:
    client-id: ${SKTAI_CLIENT_ID}
    client-secret: ${SKTAI_CLIENT_SECRET}
    token-url: ${sktai.api.base-url}/api/v1/auth/token
```

## 참고사항
- 모든 토큰은 안전한 저장소에 보관해야 합니다
- 프로덕션 환경에서는 HTTPS 통신만 허용됩니다
- 관리자 권한이 필요한 작업은 별도 승인 절차가 있을 수 있습니다
- SSO 연동 시 외부 IdP(Identity Provider) 설정이 필요합니다
- 대량 사용자 등록 시 배치 API 사용을 권장합니다
- 정기적인 토큰 순환(rotation)을 권장합니다
