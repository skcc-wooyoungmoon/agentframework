# SKTAI History API 클라이언트 문서

## 개요
SKTAI History API는 시스템 활동 이력, 감사 로그, 사용 통계 등을 관리하는 RESTful API입니다. 이 문서는 History 관련 Feign Client의 구현 현황과 API 명세를 정리합니다.

**문서 버전**: 8.0  
**최종 업데이트**: 2025-08-16  
**기준 소스**: src/main/java/com/skax/aiplatform/client/sktai/history/

## SKTAI History 클라이언트 목록

### 1. SktaiHistoryClient
**패키지**: `com.skax.aiplatform.client.sktai.history`  
**기능**: History 통합 관리 - 활동 이력, 감사 로그, 통계 분석

#### History Records APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getHistoryRecords | 활동 이력 목록 조회 | GET | /api/v1/history/records | page(Integer), size(Integer), sort(String), filter(String), search(String), startDate(String), endDate(String) | HistoryRecordListResponse |
| queryHistoryRecords | 복합 조건 이력 검색 | POST | /api/v1/history/records/query | HistoryQueryRequest | HistoryRecordListResponse |
| getHistoryRecord | 특정 이력 상세 조회 | GET | /api/v1/history/records/{record_id} | recordId(String) | HistoryRecordResponse |

#### Audit Logs APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getAuditLogs | 감사 로그 목록 조회 | GET | /api/v1/history/audit-logs | page(Integer), size(Integer), sort(String), filter(String), search(String), startDate(String), endDate(String) | AuditLogListResponse |
| queryAuditLogs | 복합 조건 감사 로그 검색 | POST | /api/v1/history/audit-logs/query | AuditLogRequest | AuditLogListResponse |
| getAuditLog | 특정 감사 로그 상세 조회 | GET | /api/v1/history/audit-logs/{log_id} | logId(String) | AuditLogResponse |
| exportAuditLogs | 감사 로그 내보내기 | GET | /api/v1/history/audit-logs/export | format(String), startDate(String), endDate(String), filter(String) | byte[] |

#### Activity Statistics APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getActivityStats | 활동 통계 조회 | GET | /api/v1/history/stats/activity | period(String), granularity(String), metrics(String), filter(String) | ActivityStatsResponse |
| queryActivityStats | 복합 조건 통계 분석 | POST | /api/v1/history/stats/activity/query | ActivityStatsRequest | ActivityStatsResponse |
| getUsageReport | 사용량 리포트 생성 | GET | /api/v1/history/stats/usage | reportType(String), period(String), format(String) | UsageReportResponse |

#### System Monitoring APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getSystemMetrics | 시스템 메트릭 조회 | GET | /api/v1/history/system/metrics | period(String), metrics(String) | SystemMetricsResponse |
| getPerformanceStats | 성능 통계 조회 | GET | /api/v1/history/system/performance | period(String), component(String) | PerformanceStatsResponse |
| getErrorLogs | 오류 로그 조회 | GET | /api/v1/history/system/errors | page(Integer), size(Integer), severity(String), startDate(String), endDate(String) | ErrorLogListResponse |

#### Data Export APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| exportHistory | 이력 데이터 내보내기 | GET | /api/v1/history/export | format(String), type(String), startDate(String), endDate(String), filter(String) | byte[] |
| scheduleExport | 내보내기 스케줄 생성 | POST | /api/v1/history/export/schedule | ExportScheduleRequest | ExportScheduleResponse |
| getExportStatus | 내보내기 상태 조회 | GET | /api/v1/history/export/{export_id}/status | exportId(String) | ExportStatusResponse |

**총 16개 API**

## 총 API 수
- **총 1개 클라이언트**
- **총 16개 API**

### 기능별 API 수
- History Records: 3개 API
- Audit Logs: 4개 API
- Activity Statistics: 3개 API
- System Monitoring: 3개 API
- Data Export: 3개 API

## 주요 기능
1. **활동 이력 관리**: 사용자 활동, 시스템 이벤트, API 호출 기록 조회
2. **감사 로그 관리**: 규정 준수를 위한 감사 추적 정보 조회
3. **통계 분석**: 사용 패턴, 트렌드 분석, 예측 정보 제공
4. **시스템 모니터링**: 성능 지표, 오류 현황, 가용성 추적
5. **데이터 내보내기**: 이력 데이터 아카이브 및 백업
6. **복합 검색**: 다양한 조건을 통한 정교한 이력 검색
7. **실시간 모니터링**: 시스템 상태 및 성능 실시간 추적

## 공통 설정
- **Base URL**: `${sktai.api.base-url}`
- **Configuration**: SktaiClientConfig
- **Authentication**: Bearer Token
- **Content-Type**: application/json

## 에러 처리
모든 API는 다음과 같은 공통 에러 응답을 따릅니다:
- **400**: 잘못된 요청 데이터
- **401**: 인증 실패
- **403**: 권한 부족
- **404**: 리소스를 찾을 수 없음
- **422**: 유효성 검증 실패
- **500**: 서버 내부 오류

## 주요 DTO 클래스

### Request DTO
- **Query**: HistoryQueryRequest, AuditLogRequest, ActivityStatsRequest
- **Export**: ExportScheduleRequest

### Response DTO
- **History**: HistoryRecordResponse, HistoryRecordListResponse
- **Audit**: AuditLogResponse, AuditLogListResponse
- **Statistics**: ActivityStatsResponse, UsageReportResponse
- **System**: SystemMetricsResponse, PerformanceStatsResponse, ErrorLogListResponse
- **Export**: ExportScheduleResponse, ExportStatusResponse

### 특별 기능
- **Data Privacy**: 민감한 정보의 자동 마스킹 처리
- **Compliance**: GDPR, SOX, HIPAA 등 국제 표준 지원
- **Multi-format Export**: CSV, JSON, XML, PDF 등 다양한 포맷 지원
- **Real-time Analytics**: 실시간 통계 분석 및 대시보드 지원
