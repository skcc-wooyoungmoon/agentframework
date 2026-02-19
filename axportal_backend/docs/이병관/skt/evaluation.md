# SKTAI Evaluation API 문서

## 개요
SKTAI Evaluation 시스템은 AI 모델과 RAG(Retrieval-Augmented Generation) 시스템의 성능을 평가하는 종합 플랫폼입니다. 다양한 평가 방법론과 벤치마크를 통해 모델 품질을 정량적으로 측정하고 비교할 수 있는 기능을 제공합니다.

## Client 정보

### 1. SktaiEvaluationsClient
일반 평가 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 평가 프로젝트 목록 조회
- 새로운 평가 생성
- 평가 상세 정보 조회

### 2. SktaiEvaluationTasksClient
평가 작업 실행을 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 평가 작업 생성 및 실행

### 3. SktaiEvaluationResultsClient
평가 결과 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 평가 결과 목록 조회
- 평가 결과 생성 및 수정
- 평가 결과 요약 정보 조회
- 평가 결과 삭제

### 4. SktaiEvaluationLogsClient
평가 로그 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 평가 로그 조회
- 로그 정보 업데이트

### 5. SktaiEvaluationAuthClient
평가 시스템 인증을 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 평가 시스템 권한 승인

### 6. SktaiRagEvaluationsClient
RAG 평가 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- RAG 평가 프로젝트 목록 조회
- 새로운 RAG 평가 생성
- RAG 평가 상세 정보 조회

### 7. SktaiRagEvaluationTasksClient
RAG 평가 작업 실행을 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- RAG 평가 작업 생성 및 실행

### 8. SktaiRagEvaluationResultsClient
RAG 평가 결과 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- RAG 평가 결과 목록 조회
- RAG 평가 결과 생성 및 수정
- RAG 평가 결과 요약 정보 조회
- RAG 평가 결과 삭제

### 9. SktaiRagEvaluationLogsClient
RAG 평가 로그 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- RAG 평가 로그 조회
- RAG 로그 정보 업데이트

### 10. SktaiModelBenchmarkClient
모델 벤치마크 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 모델 벤치마크 목록 조회
- 새로운 벤치마크 생성
- 벤치마크 상세 정보 조회
- 작업 파일 업로드 및 관리

### 11. SktaiModelBenchmarkTasksClient
모델 벤치마크 작업 실행을 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 모델 벤치마크 작업 생성 및 실행

### 12. SktaiModelBenchmarkResultsClient
모델 벤치마크 결과 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 벤치마크 결과 목록 조회
- 벤치마크 결과 생성 및 수정
- 벤치마크 결과 요약 정보 조회
- 벤치마크 결과 삭제

### 13. SktaiModelBenchmarkLogsClient
모델 벤치마크 로그 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 벤치마크 로그 조회
- 벤치마크 로그 정보 업데이트

## API 목록

### General Evaluation APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 평가 목록 조회 | GET | `/api/v1/evaluations` | - | Object | 평가 프로젝트 목록을 조회합니다 |
| 평가 생성 | POST | `/api/v1/evaluations` | Object | Object | 새로운 평가를 생성합니다 |
| 평가 상세 조회 | GET | `/api/v1/evaluations/{id}` | id | Object | 특정 평가의 상세 정보를 조회합니다 |
| 평가 작업 생성 | POST | `/api/v1/evaluation-tasks` | Object | Object | 평가 작업을 생성하고 실행합니다 |

### Evaluation Results APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 평가 결과 목록 조회 | GET | `/api/v1/evaluation-results` | - | Object | 평가 결과 목록을 조회합니다 |
| 평가 결과 생성 | POST | `/api/v1/evaluation-results` | Object | Object | 새로운 평가 결과를 생성합니다 |
| 평가 결과 요약 조회 | GET | `/api/v1/evaluation-results/summary` | - | Object | 평가 결과 요약 정보를 조회합니다 |
| 평가 결과 상세 조회 | GET | `/api/v1/evaluation-results/{id}` | id | Object | 특정 평가 결과의 상세 정보를 조회합니다 |
| 평가 결과 삭제 | DELETE | `/api/v1/evaluation-results/{id}` | id | void | 평가 결과를 삭제합니다 |
| 평가 결과 업데이트 | POST | `/api/v1/evaluation-results/update` | Object | Object | 평가 결과 정보를 업데이트합니다 |

### Evaluation Logs APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 평가 로그 조회 | GET | `/api/v1/evaluation-logs` | - | Object | 평가 로그를 조회합니다 |
| 평가 로그 업데이트 | POST | `/api/v1/evaluation-logs/update` | Object | Object | 평가 로그를 업데이트합니다 |

### RAG Evaluation APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| RAG 평가 목록 조회 | GET | `/api/v1/rag-evaluations` | - | Object | RAG 평가 프로젝트 목록을 조회합니다 |
| RAG 평가 생성 | POST | `/api/v1/rag-evaluations` | Object | Object | 새로운 RAG 평가를 생성합니다 |
| RAG 평가 상세 조회 | GET | `/api/v1/rag-evaluations/{id}` | id | Object | 특정 RAG 평가의 상세 정보를 조회합니다 |
| RAG 평가 작업 생성 | POST | `/api/v1/rag-evaluation-tasks` | Object | Object | RAG 평가 작업을 생성하고 실행합니다 |

### RAG Evaluation Results APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| RAG 평가 결과 목록 조회 | GET | `/api/v1/rag-evaluation-results` | - | Object | RAG 평가 결과 목록을 조회합니다 |
| RAG 평가 결과 생성 | POST | `/api/v1/rag-evaluation-results` | Object | Object | 새로운 RAG 평가 결과를 생성합니다 |
| RAG 평가 결과 요약 조회 | GET | `/api/v1/rag-evaluation-results/summary` | - | Object | RAG 평가 결과 요약 정보를 조회합니다 |
| RAG 평가 결과 상세 조회 | GET | `/api/v1/rag-evaluation-results/{id}` | id | Object | 특정 RAG 평가 결과의 상세 정보를 조회합니다 |
| RAG 평가 결과 삭제 | DELETE | `/api/v1/rag-evaluation-results/{id}` | id | void | RAG 평가 결과를 삭제합니다 |
| RAG 평가 결과 업데이트 | POST | `/api/v1/rag-evaluation-results/update` | Object | Object | RAG 평가 결과 정보를 업데이트합니다 |

### RAG Evaluation Logs APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| RAG 평가 로그 조회 | GET | `/api/v1/rag-evaluation-logs` | - | Object | RAG 평가 로그를 조회합니다 |
| RAG 평가 로그 업데이트 | POST | `/api/v1/rag-evaluation-logs/update` | Object | Object | RAG 평가 로그를 업데이트합니다 |

### Model Benchmark APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 모델 벤치마크 목록 조회 | GET | `/api/v1/model-benchmarks` | page, size, sort, filter, search | Object | 모델 벤치마크 목록을 조회합니다 |
| 모델 벤치마크 생성 | POST | `/api/v1/model-benchmarks` | Object | Object | 새로운 모델 벤치마크를 생성합니다 |
| 모델 벤치마크 상세 조회 | GET | `/api/v1/model-benchmarks/{id}` | id | Object | 특정 모델 벤치마크의 상세 정보를 조회합니다 |
| 벤치마크 작업 파일 업로드 | POST | `/api/v1/model-benchmarks/{id}/task-files` | id, MultipartFile | Object | 벤치마크 작업 파일을 업로드합니다 |
| 벤치마크 작업 파일 조회 | GET | `/api/v1/model-benchmarks/{id}/task-files` | id | Object | 벤치마크 작업 파일 목록을 조회합니다 |
| 벤치마크 작업 파일 삭제 | DELETE | `/api/v1/model-benchmarks/{id}/task-files` | id, fileNames | void | 벤치마크 작업 파일을 삭제합니다 |
| 모델 벤치마크 작업 생성 | POST | `/api/v1/model-benchmark-tasks` | Object | Object | 모델 벤치마크 작업을 생성하고 실행합니다 |

### Model Benchmark Results APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 벤치마크 결과 목록 조회 | GET | `/api/v1/model-benchmark-results` | page, size, sort, filter, search, benchmarkId | Object | 모델 벤치마크 결과 목록을 조회합니다 |
| 벤치마크 결과 생성 | POST | `/api/v1/model-benchmark-results` | Object | Object | 새로운 벤치마크 결과를 생성합니다 |
| 벤치마크 결과 요약 조회 | GET | `/api/v1/model-benchmark-results/summary` | benchmarkId | Object | 벤치마크 결과 요약 정보를 조회합니다 |
| 벤치마크 결과 상세 조회 | GET | `/api/v1/model-benchmark-results/{id}` | id | Object | 특정 벤치마크 결과의 상세 정보를 조회합니다 |
| 벤치마크 결과 삭제 | DELETE | `/api/v1/model-benchmark-results/{id}` | id | void | 벤치마크 결과를 삭제합니다 |
| 벤치마크 결과 업데이트 | POST | `/api/v1/model-benchmark-results/update` | Object | Object | 벤치마크 결과 정보를 업데이트합니다 |

### Model Benchmark Logs APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 벤치마크 로그 조회 | GET | `/api/v1/model-benchmark-logs` | - | Object | 모델 벤치마크 로그를 조회합니다 |
| 벤치마크 로그 업데이트 | PUT | `/api/v1/model-benchmark-logs/{id}` | id, Object | Object | 모델 벤치마크 로그를 업데이트합니다 |

### Authentication APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 권한 승인 | POST | `/api/v1/auth/authorize` | Object | Object | 평가 시스템 사용 권한을 승인합니다 |

## DTO 클래스

### 공통 타입
- **Object**: 동적 스키마를 가진 응답에 사용
- **MultipartFile**: 파일 업로드용 Spring 표준 타입
- **List<String>**: 문자열 리스트 (파일명 등)

### 평가 관련 DTOs
- 각 API별로 특화된 Request/Response DTO가 존재하나, 현재 구현에서는 Object 타입으로 추상화되어 있음
- 실제 구현 시에는 구체적인 DTO 클래스명이 지정될 예정

## API 상세 정보

### 1. General Evaluation
일반적인 AI 모델 평가를 위한 기능입니다.

**평가 프로세스**:
1. 평가 프로젝트 생성
2. 평가 작업 실행
3. 결과 수집 및 분석
4. 로그 모니터링

### 2. RAG Evaluation
RAG(Retrieval-Augmented Generation) 시스템 전용 평가 기능입니다.

**RAG 평가 메트릭**:
- **검색 정확도**: 관련 문서 검색 성능
- **생성 품질**: 답변 생성 품질
- **일관성**: 검색과 생성의 일치성
- **사실 정확성**: 생성된 답변의 사실 확인

### 3. Model Benchmark
표준화된 벤치마크를 통한 모델 성능 평가입니다.

**벤치마크 타입**:
- **GLUE**: 자연어 이해 태스크
- **SuperGLUE**: 고급 자연어 이해 태스크
- **Custom**: 사용자 정의 벤치마크

### 4. 파일 관리
벤치마크 작업에 필요한 파일들을 관리합니다.

**지원 파일 타입**:
- 데이터셋 파일 (CSV, JSON, JSONL)
- 설정 파일 (YAML, JSON)
- 스크립트 파일 (Python, Shell)

### 5. 로그 및 모니터링
평가 과정의 모든 단계를 기록하고 모니터링합니다.

**로그 타입**:
- **실행 로그**: 작업 실행 과정
- **에러 로그**: 오류 발생 정보
- **성능 로그**: 성능 메트릭
- **시스템 로그**: 시스템 리소스 사용량

## 평가 메트릭

### 일반 평가 메트릭
- **정확도 (Accuracy)**: 전체 예측 중 정확한 예측의 비율
- **정밀도 (Precision)**: 양성 예측 중 실제 양성의 비율
- **재현율 (Recall)**: 실제 양성 중 올바르게 예측한 비율
- **F1 Score**: 정밀도와 재현율의 조화 평균

### RAG 전용 메트릭
- **BLEU**: 기계 번역 품질 평가
- **ROUGE**: 요약 품질 평가
- **BERTScore**: 의미적 유사성 평가
- **Faithfulness**: 검색된 문서와의 일치성
- **Answer Relevancy**: 질문과 답변의 관련성

### 벤치마크 메트릭
- **표준 벤치마크 점수**: GLUE, SuperGLUE 등의 표준 점수
- **처리 속도**: 초당 처리 토큰 수
- **메모리 사용량**: 모델 실행 시 메모리 사용량
- **추론 지연시간**: 단일 추론 요청 처리 시간

## 인증 및 권한
모든 API는 Bearer Token 인증이 필요합니다.
- Authorization: Bearer {access_token}

특정 평가 작업의 경우 추가적인 권한 승인이 필요할 수 있습니다.

## 오류 코드
- **401 Unauthorized**: 인증 실패
- **403 Forbidden**: 권한 부족
- **404 Not Found**: 리소스를 찾을 수 없음
- **422 Unprocessable Entity**: 입력값 검증 실패
- **500 Internal Server Error**: 평가 실행 중 오류 발생

## 사용 예시

### Java (Spring Boot)
```java
@Autowired
private SktaiEvaluationsClient evaluationsClient;

@Autowired
private SktaiModelBenchmarkClient benchmarkClient;

@Autowired
private SktaiRagEvaluationsClient ragEvaluationsClient;

// 일반 평가 생성
Object evaluationRequest = // 평가 설정 정보
Object evaluation = evaluationsClient.createEvaluation(evaluationRequest);

// 모델 벤치마크 실행
Object benchmarkRequest = // 벤치마크 설정 정보
Object benchmark = benchmarkClient.createModelBenchmark(benchmarkRequest);

// RAG 평가 실행
Object ragRequest = // RAG 평가 설정 정보
Object ragEvaluation = ragEvaluationsClient.createRagEvaluation(ragRequest);

// 결과 조회
Object results = evaluationsClient.getEvaluationResults();
```

## 설정
application.yml에서 SKTAI API 설정을 구성합니다:

```yaml
sktai:
  api:
    base-url: ${sktai.api.base-url}
    timeout:
      connect: 10000
      read: 300000  # 평가 작업은 시간이 오래 걸릴 수 있음
```

## 참고사항
- 평가 작업은 비동기로 실행되며, 완료까지 시간이 오래 걸릴 수 있습니다
- 대용량 데이터셋 평가 시 충분한 타임아웃 설정이 필요합니다
- 벤치마크 파일 업로드 시 파일 크기 제한에 주의하세요
- RAG 평가는 Knowledge Repository가 사전에 설정되어야 합니다
- 평가 결과는 정기적으로 백업하는 것을 권장합니다
- 동시 실행 가능한 평가 작업 수에 제한이 있을 수 있습니다
