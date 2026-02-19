# SKTAI Model API 문서

## 개요
SKTAI Model 시스템은 AI 모델의 전체 라이프사이클을 관리하는 종합 플랫폼입니다. 모델 등록, 조회, 수정, 삭제, 파일 업로드 등의 기능을 제공하며, 효율적인 AI 모델 운영 환경을 지원합니다.

## Client 정보

### SktaiModelsClient
AI 모델 관리를 위한 통합 Feign 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- **모델 등록**: 새로운 AI 모델 등록
- **모델 조회**: 등록된 모델 목록 및 상세 조회
- **모델 수정**: 기존 모델 정보 업데이트
- **모델 삭제**: 모델 삭제 및 복구
- **파일 업로드**: 모델 파일 업로드
- **모델 조회**: 다중 모델 lookup
- **하드 삭제**: 완전 삭제

## API 목록

### Model Management APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 모델 등록 | POST | `/api/v1/models` | ModelCreate | ModelRead | 새로운 AI 모델을 등록합니다 |
| 모델 목록 조회 | GET | `/api/v1/models` | page, size, sort, filter, search, ids | ModelsRead | 등록된 모든 AI 모델 목록을 조회합니다 |
| 모델 타입 조회 | GET | `/api/v1/models/types` | - | ModelTypesResponse | 사용 가능한 모든 모델 타입을 조회합니다 |
| 모델 태그 조회 | GET | `/api/v1/models/tags` | - | ModelTagsResponse | 사용 가능한 모든 모델 태그를 조회합니다 |
| 모델 상세 조회 | GET | `/api/v1/models/{model_id}` | modelId | ModelRead | 지정된 ID의 모델 상세 정보를 조회합니다 |
| 모델 수정 | PUT | `/api/v1/models/{model_id}` | modelId, ModelUpdate | ModelRead | 지정된 ID의 모델 정보를 수정합니다 |
| 모델 삭제 | DELETE | `/api/v1/models/{model_id}` | modelId | void | 지정된 ID의 모델을 삭제합니다 |
| 모델 복구 | PUT | `/api/v1/models/{model_id}/recovery` | modelId | void | 삭제된 모델을 복구합니다 |

### File Management APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 모델 파일 업로드 | POST | `/api/v1/models/files` | MultipartFile | Object | 모델 등록을 위한 파일을 업로드합니다 |

### Utility APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 모델 하드 삭제 | POST | `/api/v1/models/hard-delete` | - | Object | 삭제 플래그가 설정된 모든 모델을 완전히 삭제합니다 |
| 모델 조회 | POST | `/api/v1/models/lookup` | ModelLookupRequest | ModelLookupResponse | 여러 모델을 (model_id, version_id) 쌍으로 조회합니다 |

## DTO 클래스

### Request DTOs
- **ModelCreate**: 모델 생성 요청 정보
- **ModelUpdate**: 모델 수정 요청 정보
- **ModelLookupRequest**: 다중 모델 조회 요청

### Response DTOs
- **ModelRead**: 모델 상세 정보
- **ModelsRead**: 페이징된 모델 목록
- **ModelLookupResponse**: 다중 모델 조회 결과
- **ModelTypesResponse**: 모델 타입 목록
- **ModelTagsResponse**: 모델 태그 목록

### 공통 타입
- **MultipartFile**: 파일 업로드용 Spring 표준 타입
- **Object**: 응답 스키마가 동적인 경우 사용

## API 상세 정보

### 1. Model 등록 (POST /api/v1/models)
새로운 AI 모델을 시스템에 등록합니다.

**Request Body**: ModelCreate
- 모델 이름, 타입, 설명 등 기본 정보
- 모델 설정 및 메타데이터

**Response**: ModelRead
- 등록된 모델의 상세 정보
- 생성된 모델 ID 포함

### 2. Model 목록 조회 (GET /api/v1/models)
등록된 모든 AI 모델의 목록을 페이징하여 조회합니다.

**Query Parameters**:
- page: 페이지 번호 (기본값: 1)
- size: 페이지 크기 (기본값: 10)
- sort: 정렬 기준
- filter: 필터 조건
- search: 검색어
- ids: 모델 ID 목록

**Response**: ModelsRead
- 페이징된 모델 목록
- 각 모델의 기본 정보 포함

### 3. Model Types 조회 (GET /api/v1/models/types)
시스템에서 지원하는 모든 모델 타입을 조회합니다.

**Response**: ModelTypesResponse
- 사용 가능한 모델 타입 목록
- 각 타입의 설명 및 특성

### 4. Model Tags 조회 (GET /api/v1/models/tags)
모델 분류를 위한 태그 목록을 조회합니다.

**Response**: ModelTagsResponse
- 사용 가능한 모델 태그 목록
- 태그별 분류 정보

### 5. Model 상세 조회 (GET /api/v1/models/{model_id})
특정 모델의 상세 정보를 조회합니다.

**Path Parameters**:
- model_id: 조회할 모델의 ID

**Response**: ModelRead
- 모델의 상세 정보
- 설정, 메타데이터, 상태 정보 포함

### 6. Model 수정 (PUT /api/v1/models/{model_id})
기존 모델의 정보를 수정합니다.

**Path Parameters**:
- model_id: 수정할 모델의 ID

**Request Body**: ModelUpdate
- 수정할 모델 정보
- 부분 업데이트 지원

**Response**: ModelRead
- 수정된 모델의 정보

### 7. Model 삭제 (DELETE /api/v1/models/{model_id})
지정된 모델을 삭제합니다 (소프트 삭제).

**Path Parameters**:
- model_id: 삭제할 모델의 ID

### 8. Model 복구 (PUT /api/v1/models/{model_id}/recovery)
삭제된 모델을 복구합니다.

**Path Parameters**:
- model_id: 복구할 모델의 ID

### 9. Model 파일 업로드 (POST /api/v1/models/files)
모델 등록을 위한 파일을 업로드합니다.

**Request**: MultipartFile
- 업로드할 모델 파일

**Response**: Object
- 업로드 결과 정보

### 10. Model 하드 삭제 (POST /api/v1/models/hard-delete)
삭제 플래그가 설정된 모든 모델을 완전히 삭제합니다.

**Response**: Object
- 삭제 처리 결과

### 11. Model Lookup (POST /api/v1/models/lookup)
여러 모델을 (model_id, version_id) 쌍으로 조회합니다.

**Request Body**: ModelLookupRequest
- 조회할 모델-버전 쌍 목록

**Response**: ModelLookupResponse
- 조회된 모델들의 정보

## 인증 및 권한
모든 API는 Bearer Token 인증이 필요합니다.
- Authorization: Bearer {access_token}

## 오류 코드
- **401 Unauthorized**: 인증 실패
- **404 Not Found**: 모델을 찾을 수 없음
- **422 Unprocessable Entity**: 입력값 검증 실패

## 사용 예시

### Java (Spring Boot)
```java
@Autowired
private SktaiModelsClient modelsClient;

// 모델 등록
ModelCreate createRequest = ModelCreate.builder()
    .name("My AI Model")
    .type("transformer")
    .description("Custom transformer model")
    .build();
ModelRead model = modelsClient.registerModel(createRequest);

// 모델 목록 조회
ModelsRead models = modelsClient.readModels(1, 10, null, null, null, null);

// 모델 상세 조회
ModelRead modelDetail = modelsClient.readModel("model_id");
```

## 설정
application.yml에서 SKTAI API 설정을 구성합니다:

```yaml
sktai:
  api:
    base-url: ${sktai.api.base-url}
    timeout:
      connect: 10000
      read: 60000
```

## 참고사항
- 모든 API는 비동기 처리를 지원합니다
- 대용량 모델 파일 업로드 시 타임아웃 설정에 주의하세요
- 모델 삭제는 기본적으로 소프트 삭제되며, 복구가 가능합니다
- 하드 삭제는 복구가 불가능하므로 신중하게 사용하세요
