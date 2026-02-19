# Lablup API FeignClient

Lablup 시스템의 아티팩트 관리를 위한 Spring Cloud OpenFeign 기반 클라이언트입니다.

## 📋 개요

이 모듈은 Lablup (Reservoir Sync Manual) API와의 통신을 담당하며, 아티팩트 스캔, 가져오기, 검색, 정리, 업로드, 다운로드 등의 기능을 제공합니다.

## 🏗️ 아키텍처

```
client/lablup/
├── api/                          # Feign Client 인터페이스 및 서비스
│   ├── LablupArtifactClient.java # 메인 Feign Client 인터페이스
│   ├── dto/
│   │   ├── request/             # 요청 DTO
│   │   └── response/            # 응답 DTO  
│   └── service/
│       └── LablupArtifactService.java # 비즈니스 로직 래퍼
├── common/
│   └── dto/                     # 공통 DTO
│       ├── LablupResponse.java  # 공통 응답 래퍼
│       └── Pagination.java     # 페이지네이션 정보
└── config/                      # 설정 클래스
    ├── LablupClientConfig.java  # Feign 설정
    ├── LablupRequestInterceptor.java # 요청 인터셉터
    └── LablupErrorDecoder.java  # 에러 디코더
```

## 🚀 주요 기능

### 1. 아티팩트 스캔
- **벌크 스캔**: 여러 아티팩트 동시 스캔
- **단일 모델 스캔**: 개별 아티팩트 모델 분석
- **배치 모델 스캔**: 대량 모델 배치 처리

### 2. 아티팩트 관리
- **메타데이터 조회**: 아티팩트 상세 정보 확인
- **가져오기/내보내기**: 외부 저장소 연동
- **검색**: 다양한 조건으로 아티팩트 검색
- **정리**: 사용하지 않는 아티팩트 정리

### 3. 파일 관리
- **업로드**: 멀티파트 파일 업로드
- **다운로드**: 보안 다운로드 URL 생성

### 4. 작업 추적
- **상태 모니터링**: 비동기 작업 진행 상황 추적
- **작업 취소**: 진행 중인 작업 취소

## 📝 사용 방법

### 1. 의존성 주입

```java
@Service
@RequiredArgsConstructor
public class YourService {
    private final LablupArtifactService lablupArtifactService;
    
    // 사용 예시
}
```

### 2. 벌크 아티팩트 스캔

```java
public void performBulkScan() {
    BulkArtifactScanRequest request = BulkArtifactScanRequest.builder()
        .artifactIds(List.of("artifact-1", "artifact-2", "artifact-3"))
        .scanType("security")
        .options(Map.of("deep_scan", true))
        .build();
    
    BulkArtifactScanResponse response = lablupArtifactService.bulkArtifactScan(request);
    log.info("벌크 스캔 시작됨. 배치 ID: {}", response.getBatchId());
}
```

### 3. 아티팩트 메타데이터 조회

```java
public void getArtifactInfo(String artifactId) {
    GetArtifactMetadataResponse metadata = lablupArtifactService.getArtifactMetadata(artifactId);
    log.info("아티팩트 정보: 이름={}, 버전={}, 크기={}", 
        metadata.getName(), metadata.getVersion(), metadata.getSize());
}
```

### 4. 아티팩트 검색

```java
public void searchArtifacts() {
    SearchArtifactRequest request = SearchArtifactRequest.builder()
        .query("tensorflow")
        .filters(Map.of(
            "type", "model",
            "framework", "tensorflow"
        ))
        .pagination(Map.of("page", 0, "size", 20))
        .build();
    
    SearchArtifactResponse response = lablupArtifactService.searchArtifact(request);
    log.info("검색 결과: {} 개 아티팩트 발견", response.getTotalCount());
}
```

### 5. 아티팩트 업로드

```java
public void uploadArtifact(MultipartFile file) {
    UploadArtifactResponse response = lablupArtifactService.uploadArtifact(
        file, "my-model", "v1.0.0", "tensorflow");
    log.info("업로드 완료. 아티팩트 ID: {}", response.getArtifactId());
}
```

### 6. 작업 상태 확인

```java
public void checkTaskStatus(String taskId) {
    GetTaskStatusResponse status = lablupArtifactService.getTaskStatus(taskId);
    log.info("작업 상태: {}, 진행률: {}%", status.getStatus(), status.getProgress());
}
```

## ⚙️ 설정

### application.yml 설정

```yaml
lablup:
  api:
    base-url: https://api.lablup.com  # Lablup API 기본 URL
    timeout:
      connect: 10000      # 연결 타임아웃 (밀리초)
      read: 60000        # 읽기 타임아웃 (밀리초)
    retry:
      max-attempts: 3    # 최대 재시도 횟수
      initial-interval: 1000  # 초기 재시도 간격
      max-interval: 3000      # 최대 재시도 간격
```

### Feign Client 설정

```java
@Configuration
public class LablupClientConfig {
    
    @Bean
    public RequestInterceptor lablupRequestInterceptor() {
        return new LablupRequestInterceptor();
    }
    
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 3000, 3);
    }
    
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            Duration.ofSeconds(10), 
            Duration.ofSeconds(60),
            true
        );
    }
}
```

## 🔒 보안

### 인증 헤더

모든 요청에는 적절한 인증 정보가 자동으로 포함됩니다:

```java
@Override
public void apply(RequestTemplate template) {
    template.header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    template.header("Accept", MediaType.APPLICATION_JSON_VALUE);
    template.header("User-Agent", "AXPORTAL-Backend/1.0");
}
```

### 에러 처리

HTTP 에러는 자동으로 비즈니스 예외로 변환됩니다:

- `400`: `INVALID_INPUT_VALUE` - 잘못된 요청 데이터
- `401`: `EXTERNAL_API_UNAUTHORIZED` - 인증 실패
- `403`: `EXTERNAL_API_FORBIDDEN` - 접근 권한 없음
- `404`: `EXTERNAL_API_NOT_FOUND` - 리소스 없음
- `409`: `DUPLICATE_RESOURCE` - 리소스 충돌
- `413`: `FILE_SIZE_EXCEEDED` - 파일 크기 초과
- `422`: `EXTERNAL_API_VALIDATION_ERROR` - 유효성 검증 실패
- `5xx`: `EXTERNAL_API_SERVER_ERROR` - 서버 오류

## 📊 로깅

모든 API 호출은 자동으로 로깅됩니다:

```
INFO  - 벌크 아티팩트 스캔 요청 - 아티팩트 수: 3, 스캔 타입: security
INFO  - 벌크 아티팩트 스캔 성공 - 배치 ID: batch-123
ERROR - 벌크 아티팩트 스캔 실패 - 아티팩트 수: 3
```

## 🧪 테스트

### 단위 테스트 예시

```java
@ExtendWith(MockitoExtension.class)
class LablupArtifactServiceTest {
    
    @Mock
    private LablupArtifactClient lablupArtifactClient;
    
    @InjectMocks
    private LablupArtifactService lablupArtifactService;
    
    @Test
    @DisplayName("벌크 아티팩트 스캔 성공")
    void bulkArtifactScan_Success() {
        // given
        BulkArtifactScanRequest request = createBulkScanRequest();
        LablupResponse<BulkArtifactScanResponse> mockResponse = createMockResponse();
        given(lablupArtifactClient.bulkArtifactScan(request)).willReturn(mockResponse);
        
        // when
        BulkArtifactScanResponse result = lablupArtifactService.bulkArtifactScan(request);
        
        // then
        assertThat(result.getBatchId()).isEqualTo("batch-123");
        verify(lablupArtifactClient).bulkArtifactScan(request);
    }
}
```

## 🔄 API 엔드포인트 매핑

| 기능 | HTTP 메서드 | 엔드포인트 | 설명 |
|-----|------------|-----------|------|
| 벌크 스캔 | POST | `/api/v1/artifacts/bulk-scan` | 여러 아티팩트 동시 스캔 |
| 단일 모델 스캔 | POST | `/api/v1/artifacts/{id}/model-scan` | 개별 모델 스캔 |
| 배치 모델 스캔 | POST | `/api/v1/artifacts/batch-model-scan` | 배치 모델 스캔 |
| 메타데이터 조회 | GET | `/api/v1/artifacts/{id}/metadata` | 아티팩트 정보 조회 |
| 가져오기 | POST | `/api/v1/artifacts/import` | 외부에서 가져오기 |
| 검색 | POST | `/api/v1/artifacts/search` | 아티팩트 검색 |
| 정리 | POST | `/api/v1/artifacts/cleanup` | 아티팩트 정리 |
| 가져오기 취소 | DELETE | `/api/v1/artifacts/import/{id}` | 가져오기 취소 |
| 작업 상태 | GET | `/api/v1/tasks/{id}/status` | 작업 상태 조회 |
| 다운로드 | GET | `/api/v1/artifacts/{id}/download` | 다운로드 URL 생성 |
| 업로드 | POST | `/api/v1/artifacts/upload` | 파일 업로드 |

## 📚 관련 문서

- [Lablup API 문서](docs/lablup-api.md)
- [Feign Client 설정 가이드](docs/feign-configuration.md)
- [에러 처리 가이드](docs/error-handling.md)
- [테스트 가이드](docs/testing-guide.md)

## 🤝 기여

1. 새로운 API 엔드포인트 추가 시 적절한 DTO와 서비스 메서드를 함께 구현
2. 모든 public 메서드에 JavaDoc 주석 추가
3. 단위 테스트 작성 필수
4. 로깅 레벨 적절히 설정 (DEBUG, INFO, ERROR)

## 📄 라이센스

이 프로젝트는 사내 프로젝트로 제한적 사용이 가능합니다.

---

**Author**: ByounggwanLee  
**Version**: 1.0  
**Last Updated**: 2025-10-02