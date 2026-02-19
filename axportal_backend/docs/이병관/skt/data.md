# SKTAI Data API 문서

## 개요
SKTAI Data 시스템은 AI 모델 훈련과 추론을 위한 데이터 관리 플랫폼입니다. 데이터소스 연동, 데이터셋 관리, 데이터 생성, 데이터 처리 등의 기능을 통해 효율적인 데이터 파이프라인을 제공합니다.

## Client 정보

### 1. SktaiDataDatasetsClient
데이터셋 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 데이터셋 생성, 조회, 수정, 삭제
- 데이터셋 파일 업로드
- 데이터셋 미리보기
- 데이터셋 태그 관리
- 하드 삭제 기능

### 2. SktaiDataDatasourcesClient
데이터소스 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 데이터소스 생성, 조회, 수정, 삭제
- 데이터소스 연결 테스트
- 다양한 데이터소스 타입 지원

### 3. SktaiDataGenerationsClient
데이터 생성 작업 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 데이터 생성 작업 조회, 생성, 수정, 삭제
- 생성 작업 실행 및 모니터링

### 4. SktaiDataGeneratorsClient
데이터 생성기 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 데이터 생성기 조회, 생성, 수정, 삭제
- 생성기 설정 및 템플릿 관리

### 5. SktaiDataProcessorsClient
데이터 처리기 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 데이터 처리기 조회, 생성, 수정, 삭제
- 데이터 변환 및 정제 파이프라인 관리

## API 목록

### Dataset Management APIs (SktaiDataDatasetsClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 데이터셋 목록 조회 | GET | `/api/v1/datasets` | page, size, sort, filter, search | Object | 데이터셋 목록을 페이징하여 조회합니다 |
| 데이터셋 생성 | POST | `/api/v1/datasets` | Object | Object | 새로운 데이터셋을 생성합니다 |
| 데이터셋 상세 조회 | GET | `/api/v1/datasets/{dataset_id}` | datasetId | Object | 특정 데이터셋의 상세 정보를 조회합니다 |
| 데이터셋 수정 | PUT | `/api/v1/datasets/{dataset_id}` | datasetId, Object | Object | 데이터셋 정보를 수정합니다 |
| 데이터셋 삭제 | DELETE | `/api/v1/datasets/{dataset_id}` | datasetId | void | 데이터셋을 삭제합니다 (소프트 삭제) |
| 데이터셋 하드 삭제 | POST | `/api/v1/datasets/hard-delete` | - | Object | 삭제 플래그가 설정된 모든 데이터셋을 완전히 삭제합니다 |
| 데이터셋 파일 업로드 | POST | `/api/v1/datasets/upload/files` | MultipartFile | Object | 데이터셋 파일을 업로드합니다 |
| 데이터셋 미리보기 | GET | `/api/v1/datasets/{dataset_id}/previews` | datasetId, page, size | Object | 데이터셋의 미리보기 데이터를 조회합니다 |
| 데이터셋 태그 추가 | PUT | `/api/v1/datasets/{dataset_id}/tags` | datasetId, tags | Object | 데이터셋에 태그를 추가합니다 |
| 데이터셋 태그 삭제 | DELETE | `/api/v1/datasets/{dataset_id}/tags` | datasetId, tags | Object | 데이터셋의 태그를 삭제합니다 |

### Datasource Management APIs (SktaiDataDatasourcesClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 데이터소스 목록 조회 | GET | `/api/v1/datasources` | page, size, sort, filter, search | Object | 데이터소스 목록을 페이징하여 조회합니다 |
| 데이터소스 생성 | POST | `/api/v1/datasources` | Object | Object | 새로운 데이터소스를 생성합니다 |
| 데이터소스 상세 조회 | GET | `/api/v1/datasources/{datasourceId}` | datasourceId | Object | 특정 데이터소스의 상세 정보를 조회합니다 |
| 데이터소스 수정 | PUT | `/api/v1/datasources/{datasourceId}` | datasourceId, Object | Object | 데이터소스 정보를 수정합니다 |
| 데이터소스 삭제 | DELETE | `/api/v1/datasources/{datasourceId}` | datasourceId | void | 데이터소스를 삭제합니다 |
| 데이터소스 연결 테스트 | POST | `/api/v1/datasources/{datasourceId}/test-connection` | datasourceId | Object | 데이터소스 연결 상태를 테스트합니다 |

### Data Generation APIs (SktaiDataGenerationsClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 데이터 생성 목록 조회 | GET | `/api/v1/generations` | page, size, sort, filter, search | Object | 데이터 생성 작업 목록을 페이징하여 조회합니다 |
| 데이터 생성 작업 시작 | POST | `/api/v1/generations` | Object | Object | 새로운 데이터 생성 작업을 시작합니다 |
| 데이터 생성 상세 조회 | GET | `/api/v1/generations/{generationId}` | generationId | Object | 특정 데이터 생성 작업의 상세 정보를 조회합니다 |
| 데이터 생성 작업 수정 | PUT | `/api/v1/generations/{generationId}` | generationId, Object | Object | 데이터 생성 작업 정보를 수정합니다 |
| 데이터 생성 작업 삭제 | DELETE | `/api/v1/generations/{generationId}` | generationId | void | 데이터 생성 작업을 삭제합니다 |

### Data Generator APIs (SktaiDataGeneratorsClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 데이터 생성기 목록 조회 | GET | `/api/v1/generators` | - | Object | 데이터 생성기 목록을 조회합니다 |
| 데이터 생성기 생성 | POST | `/api/v1/generators` | Object | Object | 새로운 데이터 생성기를 생성합니다 |
| 데이터 생성기 상세 조회 | GET | `/api/v1/generators/{generatorId}` | generatorId | Object | 특정 데이터 생성기의 상세 정보를 조회합니다 |
| 데이터 생성기 수정 | PUT | `/api/v1/generators/{generatorId}` | generatorId, Object | Object | 데이터 생성기 정보를 수정합니다 |
| 데이터 생성기 삭제 | DELETE | `/api/v1/generators/{generatorId}` | generatorId | void | 데이터 생성기를 삭제합니다 |

### Data Processor APIs (SktaiDataProcessorsClient)

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 데이터 처리기 목록 조회 | GET | `/api/v1/processors` | - | Object | 데이터 처리기 목록을 조회합니다 |
| 데이터 처리기 생성 | POST | `/api/v1/processors` | Object | Object | 새로운 데이터 처리기를 생성합니다 |
| 데이터 처리기 상세 조회 | GET | `/api/v1/processors/{processorId}` | processorId | Object | 특정 데이터 처리기의 상세 정보를 조회합니다 |
| 데이터 처리기 수정 | PUT | `/api/v1/processors/{processorId}` | processorId, Object | Object | 데이터 처리기 정보를 수정합니다 |
| 데이터 처리기 삭제 | DELETE | `/api/v1/processors/{processorId}` | processorId | void | 데이터 처리기를 삭제합니다 |

## DTO 클래스

### 공통 타입
- **Object**: 동적 스키마를 가진 요청/응답에 사용
- **MultipartFile**: 파일 업로드용 Spring 표준 타입
- **List<String>**: 태그 목록 등에 사용

### 데이터 관련 DTOs
- 각 API별로 특화된 Request/Response DTO가 존재하나, 현재 구현에서는 Object 타입으로 추상화되어 있음
- 실제 구현 시에는 구체적인 DTO 클래스명이 지정될 예정

## API 상세 정보

### 1. 데이터셋 관리

#### 데이터셋 생성
새로운 데이터셋을 생성하고 설정합니다:
- 데이터셋 메타데이터 (이름, 설명, 타입)
- 스키마 정의
- 접근 권한 설정

#### 파일 업로드
다양한 형식의 데이터 파일을 업로드할 수 있습니다:
- **지원 파일 형식**: CSV, JSON, JSONL, Parquet, Excel
- **최대 파일 크기**: 2GB
- **배치 업로드**: 여러 파일 동시 업로드 지원

#### 데이터셋 미리보기
업로드된 데이터의 구조와 내용을 미리 확인할 수 있습니다:
- 페이징된 데이터 조회
- 스키마 정보 표시
- 통계 정보 제공

#### 태그 관리
데이터셋 분류와 검색을 위한 태그 시스템:
- 사용자 정의 태그
- 계층적 태그 구조
- 태그 기반 검색 및 필터링

### 2. 데이터소스 관리

#### 지원 데이터소스 타입
- **데이터베이스**: MySQL, PostgreSQL, Oracle, SQL Server
- **클라우드 스토리지**: AWS S3, Azure Blob, Google Cloud Storage
- **API**: REST API, GraphQL
- **파일 시스템**: Local File System, FTP, SFTP
- **빅데이터**: Hadoop HDFS, Apache Kafka

#### 연결 설정
각 데이터소스 타입별로 필요한 연결 정보:
- 호스트 정보 및 포트
- 인증 정보 (사용자명/비밀번호, API 키)
- 연결 옵션 (SSL, 타임아웃 등)

#### 연결 테스트
데이터소스 설정 후 연결 상태를 검증:
- 실시간 연결 테스트
- 오류 진단 및 해결 가이드
- 성능 측정 (응답 시간, 처리량)

### 3. 데이터 생성

#### 생성 전략
다양한 데이터 생성 방법론을 지원합니다:
- **규칙 기반**: 사전 정의된 규칙에 따른 데이터 생성
- **템플릿 기반**: 템플릿 패턴을 활용한 데이터 생성
- **AI 기반**: 생성형 AI를 활용한 합성 데이터 생성
- **샘플링**: 기존 데이터에서 표본 추출

#### 생성 작업 관리
데이터 생성 작업의 전체 라이프사이클을 관리:
- 작업 스케줄링
- 진행률 모니터링
- 결과 검증
- 품질 평가

### 4. 데이터 처리

#### 처리 파이프라인
데이터 변환 및 정제를 위한 파이프라인:
- **데이터 클렌징**: 결측치 처리, 이상값 제거
- **데이터 변환**: 형식 변환, 정규화, 인코딩
- **데이터 통합**: 여러 소스 데이터 병합
- **데이터 분할**: 훈련/검증/테스트 세트 분할

#### 처리 규칙
사용자 정의 처리 규칙을 설정할 수 있습니다:
- 조건부 변환 규칙
- 데이터 검증 규칙
- 품질 기준 설정
- 예외 처리 로직

## 데이터 품질 관리

### 품질 메트릭
- **완전성**: 결측치 비율
- **정확성**: 데이터 형식 준수율
- **일관성**: 중복 데이터 비율
- **유효성**: 비즈니스 규칙 준수율

### 자동 검증
데이터 품질을 자동으로 검증합니다:
- 스키마 검증
- 데이터 타입 검증
- 범위 및 제약 조건 검증
- 중복 데이터 탐지

### 품질 리포트
데이터 품질 현황을 종합적으로 보고합니다:
- 품질 점수 산정
- 문제 지점 식별
- 개선 권장 사항
- 품질 추이 분석

## 보안 및 규정 준수

### 데이터 보안
- **암호화**: 저장 및 전송 중 데이터 암호화
- **접근 제어**: 역할 기반 데이터 접근 권한
- **마스킹**: 민감 정보 자동 마스킹
- **감사 로그**: 모든 데이터 접근 기록

### 개인정보 보호
- **GDPR 준수**: 유럽 개인정보보호 규정
- **CCPA 준수**: 캘리포니아 소비자 프라이버시법
- **개인정보 식별**: 자동 PII 탐지
- **익명화**: 개인정보 익명화 처리

## 인증 및 권한
모든 API는 Bearer Token 인증이 필요합니다.
- Authorization: Bearer {access_token}

## 오류 코드
- **400 Bad Request**: 잘못된 요청 파라미터
- **401 Unauthorized**: 인증 실패
- **403 Forbidden**: 데이터 접근 권한 없음
- **404 Not Found**: 데이터셋/데이터소스를 찾을 수 없음
- **413 Payload Too Large**: 파일 크기 초과
- **422 Unprocessable Entity**: 데이터 형식 오류
- **429 Too Many Requests**: API 호출 한도 초과

## 사용 예시

### Java (Spring Boot)
```java
@Autowired
private SktaiDataDatasetsClient datasetsClient;

@Autowired
private SktaiDataDatasourcesClient datasourcesClient;

@Autowired
private SktaiDataGenerationsClient generationsClient;

// 데이터소스 생성
Object datasourceRequest = Map.of(
    "name", "My Database",
    "type", "postgresql",
    "host", "localhost",
    "port", 5432,
    "database", "mydb",
    "username", "user",
    "password", "password"
);
Object datasource = datasourcesClient.createDatasource(datasourceRequest);

// 데이터소스 연결 테스트
Object connectionResult = datasourcesClient.testConnection(datasource.get("id"));

// 데이터셋 생성
Object datasetRequest = Map.of(
    "name", "Training Dataset",
    "description", "Dataset for model training",
    "datasourceId", datasource.get("id")
);
Object dataset = datasetsClient.createDataset(datasetRequest);

// 파일 업로드
MultipartFile file = // 업로드할 파일
Object uploadResult = datasetsClient.uploadDatasetFile(file);

// 데이터 생성 작업 시작
Object generationRequest = Map.of(
    "generatorId", "text-generator",
    "outputDatasetId", dataset.get("id"),
    "parameters", Map.of("count", 1000, "type", "synthetic")
);
Object generation = generationsClient.createGeneration(generationRequest);
```

## 설정
application.yml에서 SKTAI API 설정을 구성합니다:

```yaml
sktai:
  api:
    base-url: ${sktai.api.base-url}
    timeout:
      connect: 10000
      read: 120000  # 데이터 처리는 시간이 오래 걸릴 수 있음

  data:
    upload:
      max-file-size: 2GB
      allowed-types:
        - csv
        - json
        - jsonl
        - parquet
        - xlsx
    
    quality:
      auto-validation: true
      quality-threshold: 0.8
```

## 참고사항
- 대용량 파일 업로드 시 청크 업로드를 사용하세요
- 데이터 생성 작업은 비동기로 처리되며 완료까지 시간이 걸릴 수 있습니다
- 민감한 데이터 처리 시 개인정보보호 규정을 준수하세요
- 데이터 품질 검증은 파이프라인 성능에 영향을 줄 수 있습니다
- 정기적인 데이터 백업과 아카이빙을 권장합니다
- 데이터소스 연결 정보는 안전하게 암호화되어 저장됩니다
- API 호출 빈도 제한이 있으니 효율적인 배치 처리를 사용하세요
