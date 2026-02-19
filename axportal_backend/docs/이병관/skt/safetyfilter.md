# SKTAI Safety Filter API 문서

## 개요
SKTAI Safety Filter 시스템은 AI 생성 콘텐츠의 안전성을 보장하기 위한 필터링 플랫폼입니다. 유해 콘텐츠 탐지, 부적절한 내용 차단, 안전 정책 적용 등의 기능을 제공합니다.

## Client 정보

### SktaiSafetyFilterClient
안전 필터 관리를 위한 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 안전 필터 생성, 조회, 수정, 삭제
- 콘텐츠 안전성 검사
- 안전 정책 관리

## API 목록

### Safety Filter Management APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 안전 필터 생성 | POST | `/api/v1/safety-filters` | Object | Object | 새로운 안전 필터를 생성합니다 |
| 안전 필터 목록 조회 | GET | `/api/v1/safety-filters` | page, size, sort, filter, search | Object | 안전 필터 목록을 페이징하여 조회합니다 |
| 안전 필터 수정 | PUT | `/api/v1/safety-filters/{safety_filter_id}` | safetyFilterId, Object | Object | 안전 필터 설정을 수정합니다 |
| 안전 필터 상세 조회 | GET | `/api/v1/safety-filters/{safety_filter_id}` | safetyFilterId | Object | 특정 안전 필터의 상세 정보를 조회합니다 |
| 안전 필터 삭제 | DELETE | `/api/v1/safety-filters/{safety_filter_id}` | safetyFilterId | void | 안전 필터를 삭제합니다 |

### Content Safety APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 콘텐츠 안전성 검사 | POST | `/api/v1/safety-filters/safe` | Object | Object | 콘텐츠의 안전성을 검사합니다 |

## 주요 기능

### 1. 안전 필터 관리
- 다양한 안전 정책 설정
- 필터링 강도 조절
- 카테고리별 필터링

### 2. 콘텐츠 검사
- 텍스트 안전성 분석
- 이미지 콘텐츠 검사
- 실시간 필터링

### 3. 안전 카테고리
- 폭력적 콘텐츠
- 성인 콘텐츠
- 혐오 발언
- 개인정보 유출

## 인증 및 권한
모든 API는 Bearer Token 인증이 필요합니다.

## 사용 예시
```java
@Autowired
private SktaiSafetyFilterClient safetyFilterClient;

// 콘텐츠 안전성 검사
Object safetyRequest = Map.of(
    "content", "검사할 텍스트 내용",
    "filterType", "comprehensive"
);
Object safetyResult = safetyFilterClient.checkContentSafety(safetyRequest);
```
