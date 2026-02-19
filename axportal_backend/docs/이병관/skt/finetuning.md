# SKTAI Fine-tuning API 클라이언트 문서

## 개요
SKTAI Fine-tuning API는 AI 모델의 파인튜닝 작업 관리, 트레이너 관리, 메트릭 모니터링 등의 기능을 제공하는 RESTful API입니다. 이 문서는 Fine-tuning 관련 Feign Client의 구현 현황과 API 명세를 정리합니다.

**문서 버전**: 8.0  
**최종 업데이트**: 2025-08-16  
**기준 소스**: src/main/java/com/skax/aiplatform/client/sktai/finetuning/

## SKTAI Fine-tuning 클라이언트 목록

### 1. SktaiFinetuningClient
**패키지**: `com.skax.aiplatform.client.sktai.finetuning`  
**기능**: Fine-tuning 통합 관리 - 트레이닝, 트레이너, 메트릭 관리

#### Training Management APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getTrainings | 트레이닝 목록 조회 | GET | /api/v1/trainings | page(Integer), size(Integer), sort(String), filter(String), search(String) | TrainingsRead |
| createTraining | 새 트레이닝 생성 | POST | /api/v1/trainings | TrainingCreate | TrainingRead |
| getTraining | 트레이닝 상세 조회 | GET | /api/v1/trainings/{training_id} | trainingId(String) | TrainingRead |
| updateTraining | 트레이닝 정보 수정 | PUT | /api/v1/trainings/{training_id} | trainingId(String), TrainingUpdate | TrainingRead |
| deleteTraining | 트레이닝 삭제 | DELETE | /api/v1/trainings/{training_id} | trainingId(String) | void |
| getTrainingStatus | 트레이닝 상태 조회 | GET | /api/v1/trainings/{training_id}/status | trainingId(String) | TrainingStatusRead |
| startTraining | 트레이닝 시작 | POST | /api/v1/trainings/{training_id}/start | trainingId(String) | TrainingRead |
| stopTraining | 트레이닝 중지 | POST | /api/v1/trainings/{training_id}/stop | trainingId(String) | TrainingRead |
| resumeTraining | 트레이닝 재시작 | POST | /api/v1/trainings/{training_id}/resume | trainingId(String) | TrainingRead |

#### Training Events & Monitoring APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getTrainingEvents | 트레이닝 이벤트 조회 | GET | /api/v1/trainings/{training_id}/events | trainingId(String), page(Integer), size(Integer) | TrainingEventsRead |
| streamTrainingEvents | 트레이닝 이벤트 스트리밍 | GET | /api/v1/trainings/{training_id}/events/stream | trainingId(String) | Flux&lt;TrainingEvent&gt; |
| handleTrainingCallback | 트레이닝 콜백 처리 | POST | /api/v1/trainings/{training_id}/callback | trainingId(String), TrainingTaskCallback | void |

#### Training Metrics APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getTrainingMetrics | 트레이닝 메트릭 목록 조회 | GET | /api/v1/trainings/{training_id}/metrics | trainingId(String), page(Integer), size(Integer) | TrainingMetricsRead |
| createTrainingMetric | 트레이닝 메트릭 생성 | POST | /api/v1/trainings/{training_id}/metrics | trainingId(String), TrainingMetricCreate | TrainingMetricRead |
| getTrainingMetric | 특정 메트릭 조회 | GET | /api/v1/trainings/{training_id}/metrics/{metric_id} | trainingId(String), metricId(String) | TrainingMetricRead |
| updateTrainingMetric | 메트릭 정보 수정 | PUT | /api/v1/trainings/{training_id}/metrics/{metric_id} | trainingId(String), metricId(String), TrainingMetricCreate | TrainingMetricRead |
| deleteTrainingMetric | 메트릭 삭제 | DELETE | /api/v1/trainings/{training_id}/metrics/{metric_id} | trainingId(String), metricId(String) | void |

#### Trainer Management APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getTrainers | 트레이너 목록 조회 | GET | /api/v1/trainers | page(Integer), size(Integer), sort(String), filter(String), search(String) | TrainersRead |
| createTrainer | 새 트레이너 생성 | POST | /api/v1/trainers | TrainerCreate | TrainerRead |
| getTrainer | 트레이너 상세 조회 | GET | /api/v1/trainers/{trainer_id} | trainerId(String) | TrainerRead |
| updateTrainer | 트레이너 정보 수정 | PUT | /api/v1/trainers/{trainer_id} | trainerId(String), TrainerUpdate | TrainerRead |
| deleteTrainer | 트레이너 삭제 | DELETE | /api/v1/trainers/{trainer_id} | trainerId(String) | void |

**총 22개 API**

## 총 API 수
- **총 1개 클라이언트**
- **총 22개 API**

### 기능별 API 수
- Training Management: 9개 API
- Training Events & Monitoring: 3개 API
- Training Metrics: 5개 API
- Trainer Management: 5개 API

## 주요 기능
1. **트레이닝 관리**: 파인튜닝 작업의 생성, 조회, 수정, 삭제
2. **트레이닝 제어**: 시작, 중지, 재시작 기능
3. **실시간 모니터링**: 이벤트 스트리밍 및 상태 추적
4. **메트릭 관리**: 학습 지표 생성, 조회, 분석
5. **트레이너 관리**: 트레이너 설정 및 관리
6. **콜백 처리**: 외부 시스템과의 연동
7. **이벤트 스트리밍**: SSE 기반 실시간 이벤트 수신

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
- **409**: 중복 또는 충돌
- **422**: 유효성 검증 실패
- **500**: 서버 내부 오류

## 주요 DTO 클래스

### Request DTO
- **Training**: TrainingCreate, TrainingUpdate, TrainingTaskCallback
- **Trainer**: TrainerCreate, TrainerUpdate
- **Metric**: TrainingMetricCreate

### Response DTO
- **Training**: TrainingRead, TrainingsRead, TrainingStatusRead, TrainingEventsRead
- **Trainer**: TrainerRead, TrainersRead
- **Metric**: TrainingMetricRead, TrainingMetricsRead

### 특별 기능
- **Real-time Monitoring**: Flux를 사용한 실시간 이벤트 스트리밍
- **Callback Integration**: 외부 시스템과의 비동기 연동
- **Metric Analytics**: 학습 지표 수집 및 분석
- **State Management**: 트레이닝 상태 추적 및 제어
