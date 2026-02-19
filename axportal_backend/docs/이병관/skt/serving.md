# SKTAI Serving API 문서

## 개요
SKTAI Serving 시스템은 AI 모델 배포 및 서빙을 위한 플랫폼입니다. 모델 서빙, 에이전트 서빙, 공유 백엔드 관리, API 키 관리 등의 기능을 제공합니다.

## Client 정보

### SktaiServingClient
모델 서빙 관리를 위한 통합 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 모델 서빙 생성, 조회, 수정, 삭제
- 에이전트 서빙 관리
- 공유 백엔드 관리
- API 키 관리

## API 목록

### Model Serving APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 모델 서빙 생성 | POST | `/api/v1/serving/servings` | Object | Object | 새로운 모델 서빙을 생성합니다 |
| 모델 서빙 목록 조회 | GET | `/api/v1/serving/servings` | page, size, sort, filter, search | Object | 모델 서빙 목록을 페이징하여 조회합니다 |
| 모델 서빙 상세 조회 | GET | `/api/v1/serving/servings/{serving_id}` | servingId | Object | 특정 모델 서빙의 상세 정보를 조회합니다 |
| 모델 서빙 수정 | PUT | `/api/v1/serving/servings/{serving_id}` | servingId, Object | Object | 모델 서빙 설정을 수정합니다 |
| 모델 서빙 삭제 | DELETE | `/api/v1/serving/servings/{serving_id}` | servingId | void | 모델 서빙을 삭제합니다 |

### Agent Serving APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 에이전트 서빙 생성 | POST | `/api/v1/serving/agent-servings` | Object | Object | 새로운 에이전트 서빙을 생성합니다 |
| 에이전트 서빙 목록 조회 | GET | `/api/v1/serving/agent-servings` | page, size, sort, filter, search | Object | 에이전트 서빙 목록을 페이징하여 조회합니다 |
| 에이전트 서빙 상세 조회 | GET | `/api/v1/serving/agent-servings/{agent_serving_id}` | agentServingId | Object | 특정 에이전트 서빙의 상세 정보를 조회합니다 |
| 에이전트 서빙 수정 | PUT | `/api/v1/serving/agent-servings/{agent_serving_id}` | agentServingId, Object | Object | 에이전트 서빙 설정을 수정합니다 |
| 에이전트 서빙 삭제 | DELETE | `/api/v1/serving/agent-servings/{agent_serving_id}` | agentServingId | void | 에이전트 서빙을 삭제합니다 |

### Shared Backend APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 공유 백엔드 생성 | POST | `/api/v1/serving/shared-backends` | Object | Object | 새로운 공유 백엔드를 생성합니다 |
| 공유 백엔드 목록 조회 | GET | `/api/v1/serving/shared-backends` | page, size, sort, filter, search | Object | 공유 백엔드 목록을 페이징하여 조회합니다 |
| 공유 백엔드 상세 조회 | GET | `/api/v1/serving/shared-backends/{shared_backend_id}` | sharedBackendId | Object | 특정 공유 백엔드의 상세 정보를 조회합니다 |
| 공유 백엔드 삭제 | DELETE | `/api/v1/serving/shared-backends/{shared_backend_id}` | sharedBackendId | void | 공유 백엔드를 삭제합니다 |

### API Key Management APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| API 키 생성 | POST | `/api/v1/serving/api-keys` | Object | Object | 새로운 API 키를 생성합니다 |
| API 키 목록 조회 | GET | `/api/v1/serving/api-keys` | page, size, sort, filter, search | Object | API 키 목록을 페이징하여 조회합니다 |
| API 키 상세 조회 | GET | `/api/v1/serving/api-keys/{api_key_id}` | apiKeyId | Object | 특정 API 키의 상세 정보를 조회합니다 |
| API 키 삭제 | DELETE | `/api/v1/serving/api-keys/{api_key_id}` | apiKeyId | void | API 키를 삭제합니다 |

## 주요 기능

### 1. 모델 서빙
- AI 모델 배포 및 호스팅
- 로드 밸런싱 및 스케일링
- 성능 모니터링

### 2. 에이전트 서빙
- AI 에이전트 배포
- 대화형 서비스 제공
- 세션 관리

### 3. 공유 백엔드
- 여러 서빙에서 공유하는 백엔드 리소스
- 비용 효율적인 리소스 활용
- 중앙화된 관리

### 4. API 키 관리
- 서빙 접근을 위한 API 키 발급
- 키별 권한 및 사용량 관리
- 보안 및 감사

## 인증 및 권한
모든 API는 Bearer Token 인증이 필요합니다.

## 사용 예시
```java
@Autowired
private SktaiServingClient servingClient;

// 모델 서빙 생성
Object servingRequest = Map.of(
    "modelId", "my-model-id",
    "name", "My Model Serving",
    "instances", 2
);
Object serving = servingClient.createServing(servingRequest);

// API 키 생성
Object keyRequest = Map.of(
    "servingId", serving.get("id"),
    "name", "My API Key"
);
Object apiKey = servingClient.createApiKey(keyRequest);
```
