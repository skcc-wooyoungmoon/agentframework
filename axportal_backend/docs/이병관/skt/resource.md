# SKTAI Resource API 문서

## 개요
SKTAI Resource 시스템은 AI 워크로드를 위한 컴퓨팅 리소스 관리 플랫폼입니다. GPU, CPU, 메모리 등의 리소스 할당, 모니터링, 최적화, 비용 분석 기능을 제공합니다.

## Client 정보

### SktaiResourceClient
리소스 관리를 위한 통합 클라이언트

**Base URL**: `${sktai.api.base-url}`
**Configuration**: `SktaiClientConfig.class`

#### 주요 기능
- 리소스 사용량 기록 및 조회
- 리소스 할당 및 해제
- 자동 스케일링 관리
- 리소스 최적화 권장사항
- 비용 분석 및 보고

## API 목록

### Resource Usage APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 리소스 사용량 기록 | POST | `/api/v1/resources/usage` | Object | Object | 리소스 사용량을 기록합니다 |
| 리소스 사용량 조회 | GET | `/api/v1/resources/{resource_id}/usage` | resourceId, 기간 파라미터 | Object | 특정 리소스의 사용량을 조회합니다 |

### Resource Management APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 리소스 목록 조회 | GET | `/api/v1/resources` | page, size, sort, filter, search | Object | 리소스 목록을 페이징하여 조회합니다 |
| 리소스 상세 조회 | GET | `/api/v1/resources/{resource_id}` | resourceId | Object | 특정 리소스의 상세 정보를 조회합니다 |
| 리소스 할당 | POST | `/api/v1/resources/allocate` | Object | Object | 새로운 리소스를 할당합니다 |
| 리소스 해제 | DELETE | `/api/v1/resources/{resource_id}` | resourceId | void | 할당된 리소스를 해제합니다 |

### Scaling APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 리소스 스케일링 | POST | `/api/v1/resources/{resource_id}/scale` | resourceId, Object | Object | 리소스를 수동으로 스케일링합니다 |
| 자동 스케일링 설정 | PUT | `/api/v1/resources/{resource_id}/auto-scaling` | resourceId, Object | Object | 자동 스케일링 정책을 설정합니다 |
| 자동 스케일링 해제 | DELETE | `/api/v1/resources/{resource_id}/auto-scaling` | resourceId | void | 자동 스케일링을 해제합니다 |

### Optimization APIs

| 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 | 설명 |
|------|-------------|------------|----------|-----------|------|
| 최적화 권장사항 조회 | GET | `/api/v1/resources/optimization/recommendations` | resourceType, projectId | Object | 리소스 최적화 권장사항을 조회합니다 |
| 비용 분석 조회 | GET | `/api/v1/resources/cost-analysis` | period, resourceType, projectId | Object | 리소스 비용 분석 정보를 조회합니다 |

## 주요 기능

### 1. 리소스 할당 및 관리
- GPU, CPU, 메모리 등 다양한 리소스 타입 지원
- 실시간 리소스 할당 및 해제
- 리소스 풀 관리

### 2. 사용량 모니터링
- 실시간 리소스 사용량 추적
- 이력 데이터 분석
- 성능 메트릭 수집

### 3. 자동 스케일링
- 부하에 따른 자동 확장/축소
- 사용자 정의 스케일링 정책
- 비용 최적화 기반 스케일링

### 4. 비용 최적화
- 리소스 비용 분석
- 최적화 권장사항 제공
- 예산 관리 및 알림

## 인증 및 권한
모든 API는 Bearer Token 인증이 필요합니다.

## 사용 예시
```java
@Autowired
private SktaiResourceClient resourceClient;

// 리소스 할당
Object allocationRequest = Map.of(
    "resourceType", "gpu",
    "quantity", 2,
    "duration", "2h"
);
Object allocation = resourceClient.allocateResource(allocationRequest);
```
