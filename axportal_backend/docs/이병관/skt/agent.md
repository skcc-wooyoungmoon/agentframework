# SKTAI Agent API 문서

## 개요

SKTAI Agent 도메인은 AI 에이전트 시스템의 핵심 기능을 제공합니다. 에이전트 애플리케이션, Few-Shot 학습, 그래프 워크플로우, 추론 프롬프트, 도구 관리, 권한 제어 등의 기능을 포함합니다.

### 주요 컴포넌트

- **Agent Apps**: AI 에이전트 애플리케이션 생성 및 관리
- **Few-Shots**: Few-Shot Learning을 위한 예제 데이터 관리
- **Graphs**: 에이전트 워크플로우 그래프 관리
- **Inference Prompts**: 추론용 프롬프트 템플릿 관리
- **Tools**: 에이전트가 사용할 수 있는 도구 관리
- **Permissions**: 에이전트 접근 권한 관리
- **Health**: 시스템 상태 모니터링
- **Default**: 기본 시스템 정보 및 상태

## API 클라이언트 목록

### 1. SktaiAgentAppsClient
**에이전트 애플리케이션 관리**

#### 주요 기능
- Agent 애플리케이션 CRUD 작업
- 애플리케이션 빌드 및 배포 관리
- 애플리케이션 상태 모니터링

#### API 엔드포인트

##### GET /api/v1/agent/agent/apps
- **기능**: Agent Apps 목록 조회
- **파라미터**: 
  - page (Integer): 페이지 번호 (기본값: 1)
  - size (Integer): 페이지 크기 (기본값: 20)
  - sort (String): 정렬 옵션
  - filter (String): 필터 조건
  - search (String): 검색어
- **응답 타입**: `AppsResponse`

##### POST /api/v1/agent/agent/apps
- **기능**: 새로운 Agent App 생성
- **요청 타입**: `AppCreateRequest`
- **응답 타입**: `AppCreateResponse`

##### GET /api/v1/agent/agent/apps/{appUuid}
- **기능**: Agent App 상세 정보 조회
- **파라미터**: appUuid (String): App UUID
- **응답 타입**: `AppResponse`

##### PUT /api/v1/agent/agent/apps/{appUuid}
- **기능**: Agent App 정보 수정
- **파라미터**: appUuid (String): App UUID
- **요청 타입**: `AppUpdateRequest`
- **응답 타입**: `AppUpdateOrDeleteResponse`

##### DELETE /api/v1/agent/agent/apps/{appUuid}
- **기능**: Agent App 삭제
- **파라미터**: appUuid (String): App UUID
- **응답 타입**: `AppUpdateOrDeleteResponse`

---

### 2. SktaiAgentFewShotsClient
**Few-Shot Learning 관리**

#### 주요 기능
- Few-Shot 예제 생성 및 관리
- 버전 관리 및 이력 추적
- 태그 시스템을 통한 분류 및 검색
- 예제 복사 및 재사용

#### API 엔드포인트

##### GET /api/v1/agent/few-shots
- **기능**: Few-Shot 목록 조회
- **파라미터**:
  - projectId (String): 프로젝트 ID (기본값: d89a7451-3d40-4bab-b4ee-6aecd55b4f32)
  - page (Integer): 페이지 번호 (기본값: 1)
  - size (Integer): 페이지 크기 (기본값: 10)
  - sort (String): 정렬 기준
  - filter (String): 필터 조건
  - search (String): 검색어
- **응답 타입**: `FewShotsResponse`

##### POST /api/v1/agent/few-shots
- **기능**: Few-Shot 생성
- **요청 타입**: `FewShotCreateRequest`
- **응답 타입**: `FewShotCreateResponse`

##### GET /api/v1/agent/few-shots/perf
- **기능**: 성능 측정용 Few-Shot 목록 조회
- **파라미터**:
  - projectId (String): 프로젝트 ID
  - ignoreOption (Integer): 무시 옵션 (기본값: 1)
  - page, size, sort, filter, search
- **응답 타입**: `FewShotsResponse`

##### GET /api/v1/agent/few-shots/{fewShotUuid}
- **기능**: Few-Shot 상세 조회
- **파라미터**: fewShotUuid (String): Few-Shot UUID
- **응답 타입**: `FewShotResponse`

##### PUT /api/v1/agent/few-shots/{fewShotUuid}
- **기능**: Few-Shot 수정
- **파라미터**: fewShotUuid (String): Few-Shot UUID
- **요청 타입**: `FewShotUpdateRequest`
- **응답 타입**: `FewShotUpdateOrDeleteResponse`

##### DELETE /api/v1/agent/few-shots/{fewShotUuid}
- **기능**: Few-Shot 삭제
- **파라미터**: fewShotUuid (String): Few-Shot UUID
- **응답 타입**: `void`

##### GET /api/v1/agent/few-shots/versions/{fewShotUuid}/latest
- **기능**: Few-Shot 최신 버전 조회
- **파라미터**: fewShotUuid (String): Few-Shot UUID
- **응답 타입**: `FewShotVersionResponse`

##### GET /api/v1/agent/few-shots/versions/{fewShotUuid}
- **기능**: Few-Shot 버전 목록 조회
- **파라미터**: fewShotUuid (String): Few-Shot UUID
- **응답 타입**: `FewShotVersionsResponse`

##### GET /api/v1/agent/few-shots/examples/{versionId}
- **기능**: Few-Shot 예제 조회
- **파라미터**: versionId (String): Few-Shot 버전 ID
- **응답 타입**: `FewShotExamplesResponse`

##### GET /api/v1/agent/few-shots/tags/{versionId}
- **기능**: Few-Shot 태그 조회 (버전별)
- **파라미터**: versionId (String): Few-Shot 버전 ID
- **응답 타입**: `FewShotTagsResponse`

##### GET /api/v1/agent/few-shots/list/tags
- **기능**: Few-Shot 태그 목록 조회
- **응답 타입**: `FewShotTagListResponse`

##### GET /api/v1/agent/few-shots/search/tags
- **기능**: 태그로 Few-Shot 검색
- **파라미터**: filters (String): 검색할 태그 필터
- **응답 타입**: `FewShotFilterByTagsResponse`

##### POST /api/v1/agent/few-shots/copy/{fewShotUuid}
- **기능**: Few-Shot 복사
- **파라미터**: fewShotUuid (String): 복사할 Few-Shot UUID
- **요청 타입**: `FewShotCopyRequest`
- **응답 타입**: `FewShotCreateResponse`

##### DELETE /api/v1/agent/few-shots/hard-delete
- **기능**: Few-Shot 하드 삭제
- **응답 타입**: `void`

---

### 3. SktaiAgentGraphsClient
**그래프 워크플로우 관리**

#### 주요 기능
- 에이전트 워크플로우 그래프 생성 및 관리
- 노드 및 엣지 관리
- 그래프 실행 및 모니터링

#### API 엔드포인트

##### GET /api/v1/agent/agent/graphs
- **기능**: Agent Graphs 목록 조회
- **파라미터**: page, size, sort, filter, search
- **응답 타입**: `GraphsResponse`

##### POST /api/v1/agent/agent/graphs
- **기능**: 새로운 Agent Graph 생성
- **요청 타입**: `GraphCreateRequest`
- **응답 타입**: `GraphCreateResponse`

##### GET /api/v1/agent/agent/graphs/{graphUuid}
- **기능**: Agent Graph 상세 정보 조회
- **파라미터**: graphUuid (String): Graph UUID
- **응답 타입**: `GraphResponse`

##### PUT /api/v1/agent/agent/graphs/{graphUuid}
- **기능**: Agent Graph 정보 수정
- **파라미터**: graphUuid (String): Graph UUID
- **요청 타입**: `GraphUpdateRequest`
- **응답 타입**: `GraphUpdateOrDeleteResponse`

##### DELETE /api/v1/agent/agent/graphs/{graphUuid}
- **기능**: Agent Graph 삭제
- **파라미터**: graphUuid (String): Graph UUID
- **응답 타입**: `GraphUpdateOrDeleteResponse`

---

### 4. SktaiAgentInferencePromptsClient
**추론 프롬프트 관리**

#### 주요 기능
- 추론용 프롬프트 템플릿 생성 및 관리
- 프롬프트 버전 관리
- 프롬프트 성능 분석

#### API 엔드포인트

##### GET /api/v1/agent/prompts/inference
- **기능**: Inference Prompts 목록 조회
- **파라미터**:
  - projectId (String): 프로젝트 ID
  - page, size, sort, filter, search
- **응답 타입**: `InferencePromptsResponse`

##### POST /api/v1/agent/prompts/inference
- **기능**: 새로운 Inference Prompt 생성
- **요청 타입**: `InferencePromptCreateRequest`
- **응답 타입**: `InferencePromptCreateResponse`

##### GET /api/v1/agent/prompts/inference/{promptUuid}
- **기능**: Inference Prompt 상세 정보 조회
- **파라미터**: promptUuid (String): Prompt UUID
- **응답 타입**: `InferencePromptResponse`

##### PUT /api/v1/agent/prompts/inference/{promptUuid}
- **기능**: Inference Prompt 정보 수정
- **파라미터**: promptUuid (String): Prompt UUID
- **요청 타입**: `InferencePromptUpdateRequest`
- **응답 타입**: `InferencePromptUpdateOrDeleteResponse`

##### DELETE /api/v1/agent/prompts/inference/{promptUuid}
- **기능**: Inference Prompt 삭제
- **파라미터**: promptUuid (String): Prompt UUID
- **응답 타입**: `InferencePromptUpdateOrDeleteResponse`

##### GET /api/v1/agent/prompts/inference/versions/{promptUuid}/latest
- **기능**: Inference Prompt 최신 버전 조회
- **파라미터**: promptUuid (String): Prompt UUID
- **응답 타입**: `InferencePromptVersionResponse`

##### GET /api/v1/agent/prompts/inference/versions/{promptUuid}
- **기능**: Inference Prompt 버전 목록 조회
- **파라미터**: promptUuid (String): Prompt UUID
- **응답 타입**: `InferencePromptVersionsResponse`

##### POST /api/v1/agent/prompts/inference/copy/{promptUuid}
- **기능**: Inference Prompt 복사
- **파라미터**: promptUuid (String): 복사할 Prompt UUID
- **요청 타입**: `InferencePromptCopyRequest`
- **응답 타입**: `InferencePromptCreateResponse`

##### DELETE /api/v1/agent/prompts/inference/hard-delete
- **기능**: Inference Prompt 하드 삭제
- **응답 타입**: `void`

---

### 5. SktaiAgentToolsClient
**에이전트 도구 관리**

#### 주요 기능
- 에이전트가 사용할 수 있는 도구 관리
- 도구 등록 및 설정
- 도구 사용 권한 관리

#### API 엔드포인트

##### GET /api/v1/agent/agent/tools
- **기능**: Agent Tools 목록 조회
- **파라미터**: page, size, sort, filter, search
- **응답 타입**: `ToolsResponse`

##### POST /api/v1/agent/agent/tools
- **기능**: 새로운 Agent Tool 생성
- **요청 타입**: `ToolCreateRequest`
- **응답 타입**: `ToolCreateResponse`

##### GET /api/v1/agent/agent/tools/{toolUuid}
- **기능**: Agent Tool 상세 정보 조회
- **파라미터**: toolUuid (String): Tool UUID
- **응답 타입**: `ToolResponse`

##### PUT /api/v1/agent/agent/tools/{toolUuid}
- **기능**: Agent Tool 정보 수정
- **파라미터**: toolUuid (String): Tool UUID
- **요청 타입**: `ToolUpdateRequest`
- **응답 타입**: `ToolUpdateOrDeleteResponse`

##### DELETE /api/v1/agent/agent/tools/{toolUuid}
- **기능**: Agent Tool 삭제
- **파라미터**: toolUuid (String): Tool UUID
- **응답 타입**: `ToolUpdateOrDeleteResponse`

---

### 6. SktaiAgentPermissionsClient
**에이전트 권한 관리**

#### 주요 기능
- 에이전트 접근 권한 설정
- 사용자별 권한 관리
- 리소스별 접근 제어

#### API 엔드포인트

##### GET /api/v1/agent/agent/permissions
- **기능**: Agent Permissions 목록 조회
- **파라미터**: page, size, sort, filter, search
- **응답 타입**: `PermissionsResponse`

##### POST /api/v1/agent/agent/permissions
- **기능**: 새로운 Agent Permission 생성
- **요청 타입**: `PermissionCreateRequest`
- **응답 타입**: `PermissionCreateResponse`

##### GET /api/v1/agent/agent/permissions/{permissionUuid}
- **기능**: Agent Permission 상세 정보 조회
- **파라미터**: permissionUuid (String): Permission UUID
- **응답 타입**: `PermissionResponse`

##### PUT /api/v1/agent/agent/permissions/{permissionUuid}
- **기능**: Agent Permission 정보 수정
- **파라미터**: permissionUuid (String): Permission UUID
- **요청 타입**: `PermissionUpdateRequest`
- **응답 타입**: `PermissionUpdateOrDeleteResponse`

##### DELETE /api/v1/agent/agent/permissions/{permissionUuid}
- **기능**: Agent Permission 삭제
- **파라미터**: permissionUuid (String): Permission UUID
- **응답 타입**: `PermissionUpdateOrDeleteResponse`

---

### 7. SktaiAgentHealthClient
**시스템 헬스 체크**

#### 주요 기능
- 서비스 상태 모니터링
- Liveness 및 Readiness 체크
- Kubernetes 프로브 지원

#### API 엔드포인트

##### GET /api/v1/agent/health/live
- **기능**: 서비스 Liveness 상태 확인
- **응답 타입**: `CommonResponse`

##### GET /api/v1/agent/health/ready
- **기능**: 서비스 Readiness 상태 확인
- **응답 타입**: `CommonResponse`

---

### 8. SktaiAgentDefaultClient
**기본 시스템 정보**

#### 주요 기능
- 시스템 상태 조회
- 기본 정보 및 설정 확인
- 버전 정보 제공

#### API 엔드포인트

##### GET /api/v1/agent/default/status
- **기능**: Agent 시스템 상태 조회
- **응답 타입**: `DefaultStatusResponse`

##### GET /api/v1/agent/default/info
- **기능**: Agent 시스템 정보 조회
- **응답 타입**: `DefaultInfoResponse`

## 데이터 타입 정의

### 요청 DTO 클래스
- `AppCreateRequest`: 애플리케이션 생성 요청
- `AppUpdateRequest`: 애플리케이션 수정 요청
- `FewShotCreateRequest`: Few-Shot 생성 요청
- `FewShotUpdateRequest`: Few-Shot 수정 요청
- `FewShotCopyRequest`: Few-Shot 복사 요청
- `GraphCreateRequest`: 그래프 생성 요청
- `GraphUpdateRequest`: 그래프 수정 요청
- `InferencePromptCreateRequest`: 추론 프롬프트 생성 요청
- `InferencePromptUpdateRequest`: 추론 프롬프트 수정 요청
- `InferencePromptCopyRequest`: 추론 프롬프트 복사 요청
- `ToolCreateRequest`: 도구 생성 요청
- `ToolUpdateRequest`: 도구 수정 요청
- `PermissionCreateRequest`: 권한 생성 요청
- `PermissionUpdateRequest`: 권한 수정 요청

### 응답 DTO 클래스
- `AppsResponse`: 애플리케이션 목록 응답
- `AppCreateResponse`: 애플리케이션 생성 응답
- `AppResponse`: 애플리케이션 상세 응답
- `AppUpdateOrDeleteResponse`: 애플리케이션 수정/삭제 응답
- `FewShotsResponse`: Few-Shot 목록 응답
- `FewShotCreateResponse`: Few-Shot 생성 응답
- `FewShotResponse`: Few-Shot 상세 응답
- `FewShotUpdateOrDeleteResponse`: Few-Shot 수정/삭제 응답
- `FewShotVersionResponse`: Few-Shot 버전 응답
- `FewShotVersionsResponse`: Few-Shot 버전 목록 응답
- `FewShotExamplesResponse`: Few-Shot 예제 응답
- `FewShotTagsResponse`: Few-Shot 태그 응답
- `FewShotTagListResponse`: Few-Shot 태그 목록 응답
- `FewShotFilterByTagsResponse`: 태그별 필터링 응답
- `GraphsResponse`: 그래프 목록 응답
- `GraphCreateResponse`: 그래프 생성 응답
- `GraphResponse`: 그래프 상세 응답
- `GraphUpdateOrDeleteResponse`: 그래프 수정/삭제 응답
- `InferencePromptsResponse`: 추론 프롬프트 목록 응답
- `InferencePromptCreateResponse`: 추론 프롬프트 생성 응답
- `InferencePromptResponse`: 추론 프롬프트 상세 응답
- `InferencePromptUpdateOrDeleteResponse`: 추론 프롬프트 수정/삭제 응답
- `InferencePromptVersionResponse`: 추론 프롬프트 버전 응답
- `InferencePromptVersionsResponse`: 추론 프롬프트 버전 목록 응답
- `ToolsResponse`: 도구 목록 응답
- `ToolCreateResponse`: 도구 생성 응답
- `ToolResponse`: 도구 상세 응답
- `ToolUpdateOrDeleteResponse`: 도구 수정/삭제 응답
- `PermissionsResponse`: 권한 목록 응답
- `PermissionCreateResponse`: 권한 생성 응답
- `PermissionResponse`: 권한 상세 응답
- `PermissionUpdateOrDeleteResponse`: 권한 수정/삭제 응답
- `CommonResponse`: 공통 응답
- `DefaultStatusResponse`: 기본 상태 응답
- `DefaultInfoResponse`: 기본 정보 응답

## 인증 및 권한

모든 API는 Bearer Token 인증을 사용합니다. (Health Check API 제외)

```
Authorization: Bearer {access_token}
```

## 에러 처리

표준 HTTP 상태 코드를 사용하며, 에러 응답에는 상세한 에러 정보가 포함됩니다.

- `200`: 성공
- `201`: 생성 성공
- `400`: 잘못된 요청
- `401`: 인증 실패
- `403`: 권한 부족
- `404`: 리소스 없음
- `409`: 충돌 (중복 등)
- `422`: 유효성 검증 실패
- `500`: 서버 내부 오류

## 사용 예시

### Few-Shot 생성 예시
```java
FewShotCreateRequest request = FewShotCreateRequest.builder()
    .name("Customer Sentiment Analysis")
    .description("고객 리뷰 감정 분석을 위한 Few-Shot 예제")
    .examples(Arrays.asList(
        FewShotExample.builder()
            .input("이 제품은 정말 좋아요!")
            .output("positive")
            .build(),
        FewShotExample.builder()
            .input("배송이 너무 늦었습니다.")
            .output("negative")
            .build()
    ))
    .tags(Arrays.asList(
        FewShotTag.builder().tag("sentiment-analysis").build()
    ))
    .build();

FewShotCreateResponse response = fewShotsClient.createFewShot(request);
```

### Agent App 생성 예시
```java
AppCreateRequest request = AppCreateRequest.builder()
    .name("ChatBot Assistant")
    .description("고객 지원용 챗봇 애플리케이션")
    .type("chatbot")
    .build();

AppCreateResponse response = appsClient.createApp(request);
```
