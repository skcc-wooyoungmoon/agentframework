package com.skax.aiplatform.client.sktai.finetuning.service;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.finetuning.SktaiFinetuningClient;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainerCreate;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainerUpdate;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingCreate;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingMetricCreate;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingStatusUpdate;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingTaskCallback;
import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingUpdate;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainerRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainersRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingEventsRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingMetricRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingMetricsRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingStatusRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingsRead;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Fine-tuning 서비스
 *
 * <p>SKTAI Fine-tuning API와의 통신을 담당하는 비즈니스 로직 래퍼 서비스입니다.
 * Training 및 Trainer 관리 기능을 제공하며, 예외 처리와 로깅을 통해 안정성을 보장합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Training 관리</strong>: 생성, 조회, 수정, 삭제, 상태 관리</li>
 *   <li><strong>Training 모니터링</strong>: 이벤트 조회, 메트릭 관리</li>
 *   <li><strong>Trainer 관리</strong>: 생성, 조회, 수정, 삭제</li>
 *   <li><strong>예외 처리</strong>: 외부 API 오류를 내부 예외로 변환</li>
 *   <li><strong>로깅</strong>: 모든 API 호출에 대한 상세 로깅</li>
 * </ul>
 *
 * <h3>에러 처리:</h3>
 * <ul>
 *   <li>네트워크 오류 시 자동 재시도</li>
 *   <li>HTTP 상태 코드별 적절한 예외 변환</li>
 *   <li>상세한 오류 로깅으로 디버깅 지원</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktaiFinetuningClient SKTAI Fine-tuning API 클라이언트
 * @since 2025-08-15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiFinetuningService {

    private final SktaiFinetuningClient sktaiFinetuningClient;

    // ========== Training Management Methods ==========

    /**
     * Training 신규 생성
     *
     * <p>새로운 Fine-tuning Training을 생성합니다.
     * 생성 과정에서 발생하는 오류를 적절히 처리하고 로깅합니다.</p>
     *
     * @param request Training 생성 요청 정보
     * @return 생성된 Training 상세 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingRead createTraining(TrainingCreate request) {
        log.debug("Training 생성 요청 - name: {}, project_id: {}",
                request.getName(), request.getProjectId());

        try {
            TrainingRead response = sktaiFinetuningClient.createTraining(request);
            log.debug("Training 생성 성공 - id: {}, name: {}",
                    response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Training 생성 실패 - name: {}, project_id: {}",
                    request.getName(), request.getProjectId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Training 생성 실패 - name: {}, project_id: {}",
                    request.getName(), request.getProjectId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 목록 조회
     *
     * <p>조건에 맞는 Training 목록을 페이지네이션으로 조회합니다.
     * 검색 조건과 페이징 정보를 로깅합니다.</p>
     *
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터링 조건
     * @param search 검색 키워드
     * @return 페이지네이션된 Training 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingsRead getTrainings(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Training 목록 조회 요청 - page: {}, size: {}, search: {}", page, size, search);

        try {
            TrainingsRead response = sktaiFinetuningClient.getTrainings(page, size, sort, filter, search);
            log.debug("Training 목록 조회 성공 - 조회된 항목 수: {}",
                    response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            log.error("Training 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 상세 조회
     *
     * <p>지정된 Training ID에 해당하는 Training의 상세 정보를 조회합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @return Training 상세 정보
     * @throws BusinessException Training을 찾을 수 없거나 외부 API 오류 시
     */
    public TrainingRead getTraining(String trainingId) {
        log.debug("Training 상세 조회 요청 - id: {}", trainingId);

        try {
            TrainingRead response = sktaiFinetuningClient.getTraining(trainingId);
            log.debug("Training 상세 조회 성공 - id: {}, name: {}",
                    response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Training 상세 조회 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 상세 조회 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 정보 수정
     *
     * <p>기존 Training의 설정이나 메타데이터를 수정합니다.
     * 수정 전후의 상태를 로깅합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    Training 수정 요청 정보
     * @return 수정된 Training 상세 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingRead updateTraining(String trainingId, TrainingUpdate request) {
        log.debug("Training 수정 요청 - id: {}", trainingId);

        try {
            TrainingRead response = sktaiFinetuningClient.updateTraining(trainingId, request);
            log.debug("Training 수정 성공 - id: {}, name: {}",
                    response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Training 수정 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 수정 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 상태만 수정
     *
     * <p>Training의 상태값만 수정합니다.
     * 빠른 상태 변경을 위한 전용 API입니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    변경할 상태 정보
     * @return 수정된 Training 상세 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingRead updateTrainingStatus(String trainingId, TrainingStatusUpdate request, String scalingGroup, String agentList) {
        log.debug("Training 상태 수정 요청 - id: {}, status: {}", trainingId, request.getStatus());

        try {
            TrainingRead response = sktaiFinetuningClient.updateTrainingStatus(trainingId, scalingGroup, agentList, request);
            log.debug("Training 상태 수정 성공 - id: {}, status: {}",
                    response.getId(), response.getStatus());
            return response;
        } catch (BusinessException e) {
            log.error("Training 상태 수정 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 상태 수정 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 상태 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 삭제 (Soft Delete)
     *
     * <p>지정된 Training을 소프트 삭제합니다.
     * 삭제 작업의 성공 여부를 로깅합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @throws BusinessException 외부 API 오류 시
     */
    public void deleteTraining(String trainingId) {
        log.debug("Training 삭제 요청 - id: {}", trainingId);

        try {
            sktaiFinetuningClient.deleteTraining(trainingId);
            log.debug("Training 삭제 성공 - id: {}", trainingId);
        } catch (BusinessException e) {
            log.error("Training 삭제 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 삭제 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 완전 삭제 (Hard Delete)
     *
     * <p>지정된 Training을 완전히 삭제합니다.
     * 중요한 작업이므로 상세히 로깅합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @throws BusinessException 외부 API 오류 시
     */
    public void hardDeleteTraining(String trainingId) {
        log.warn("Training 완전 삭제 요청 - id: {} (복구 불가능)", trainingId);

        try {
            sktaiFinetuningClient.hardDeleteTraining(trainingId);
            log.warn("Training 완전 삭제 성공 - id: {}", trainingId);
        } catch (BusinessException e) {
            log.error("Training 완전 삭제 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 완전 삭제 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 완전 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 상태 조회
     *
     * <p>지정된 Training의 현재 상태를 조회합니다.
     * 상태 변경 추적을 위해 상태 정보를 로깅합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @return Training 상태 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingStatusRead getTrainingStatus(String trainingId) {
        log.debug("Training 상태 조회 요청 - id: {}", trainingId);

        try {
            TrainingStatusRead response = sktaiFinetuningClient.getTrainingStatus(trainingId);
            log.debug("Training 상태 조회 성공 - id: {}, status: {}",
                    trainingId, response.getStatus());
            return response;
        } catch (BusinessException e) {
            log.error("Training 상태 조회 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 상태 조회 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 상태 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 이벤트 조회
     *
     * <p>지정된 Training의 이벤트 목록을 조회합니다.
     * 실시간 모니터링을 위한 이벤트 스트림을 제공합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @param last       마지막 이벤트 식별자 (증분 조회용)
     * @return Training 이벤트 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingEventsRead getTrainingEvents(String trainingId, String last) {
        log.debug("Training 이벤트 조회 요청 - id: {}, last: {}", trainingId, last);

        try {
            TrainingEventsRead response = sktaiFinetuningClient.getTrainingEvents(trainingId, last);
            log.debug("Training 이벤트 조회 성공 - id: {}, events: {}",
                    trainingId, response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            log.error("Training 이벤트 조회 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 이벤트 조회 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 이벤트 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 작업 콜백 처리
     *
     * <p>Task Manager로부터 Training 작업 상태 변경 알림을 처리합니다.
     * 콜백 데이터를 로깅하여 작업 추적을 지원합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    작업 콜백 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public void trainingTaskCallback(String trainingId, TrainingTaskCallback request) {
        log.debug("Training 작업 콜백 처리 - id: {}, status: {}",
                trainingId, request.getStatus());

        try {
            sktaiFinetuningClient.trainingTaskCallback(trainingId, request);
            log.debug("Training 작업 콜백 처리 성공 - id: {}", trainingId);
        } catch (BusinessException e) {
            log.error("Training 작업 콜백 처리 실패 - id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 작업 콜백 처리 실패 - id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 작업 콜백 처리에 실패했습니다: " + e.getMessage());
        }
    }

    // ========== Training Metrics Methods ==========

    /**
     * Training 메트릭 생성
     *
     * <p>Training 진행 중 수집된 메트릭을 등록합니다.
     * 메트릭 데이터의 유효성을 로깅합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @param request    메트릭 생성 요청 정보
     * @return 생성된 메트릭 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingMetricRead createTrainingMetric(String trainingId, TrainingMetricCreate request) {
        log.debug("Training 메트릭 생성 요청 - training_id: {}, step: {}, type: {}",
                trainingId, request.getStep(), request.getType());

        try {
            TrainingMetricRead response = sktaiFinetuningClient.createTrainingMetric(trainingId, request);
            log.debug("Training 메트릭 생성 성공 - id: {}, step: {}, loss: {}",
                    response.getId(), response.getStep(), response.getLoss());
            return response;
        } catch (BusinessException e) {
            log.error("Training 메트릭 생성 실패 - training_id: {}, step: {}",
                    trainingId, request.getStep(), e);
            throw e;
        } catch (Exception e) {
            log.error("Training 메트릭 생성 실패 - training_id: {}, step: {}",
                    trainingId, request.getStep(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 메트릭 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Training 메트릭 목록 조회
     *
     * <p>지정된 Training의 메트릭 목록을 페이지네이션으로 조회합니다.
     * 메트릭 집계 정보를 로깅합니다.</p>
     *
     * @param trainingId Training 고유 식별자
     * @param page       페이지 번호
     * @param size       페이지 크기
     * @param sort       정렬 조건
     * @param filter     필터링 조건
     * @return 페이지네이션된 메트릭 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainingMetricsRead getTrainingMetrics(String trainingId, Integer page, Integer size,
                                                  String sort, String filter) {
        log.debug("Training 메트릭 목록 조회 요청 - training_id: {}, page: {}, size: {}",
                trainingId, page, size);

        try {
            TrainingMetricsRead response = sktaiFinetuningClient.getTrainingMetrics(
                    trainingId, page, size, sort, filter);
            log.debug("Training 메트릭 목록 조회 성공 - training_id: {}, metrics: {}",
                    trainingId, response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            log.error("Training 메트릭 목록 조회 실패 - training_id: {}", trainingId, e);
            throw e;
        } catch (Exception e) {
            log.error("Training 메트릭 목록 조회 실패 - training_id: {}", trainingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Training 메트릭 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ========== Trainer Management Methods ==========

    /**
     * Trainer 신규 생성
     *
     * <p>새로운 Trainer를 생성합니다.
     * Trainer 설정 정보를 로깅합니다.</p>
     *
     * @param request Trainer 생성 요청 정보
     * @return 생성된 Trainer 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainerRead createTrainer(TrainerCreate request) {
        log.debug("Trainer 생성 요청 - registry_url: {}", request.getRegistryUrl());

        try {
            TrainerRead response = sktaiFinetuningClient.createTrainer(request);
            log.debug("Trainer 생성 성공 - id: {}, registry_url: {}",
                    response.getId(), response.getRegistryUrl());
            return response;
        } catch (BusinessException e) {
            log.error("Trainer 생성 실패 - registry_url: {}", request.getRegistryUrl(), e);
            throw e;
        } catch (Exception e) {
            log.error("Trainer 생성 실패 - registry_url: {}", request.getRegistryUrl(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Trainer 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Trainer 목록 조회
     *
     * <p>등록된 Trainer 목록을 페이지네이션으로 조회합니다.
     * 검색 조건과 결과를 로깅합니다.</p>
     *
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터링 조건
     * @param search 검색 키워드
     * @return 페이지네이션된 Trainer 목록
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainersRead getTrainers(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Trainer 목록 조회 요청 - page: {}, size: {}, search: {}", page, size, search);

        try {
            TrainersRead response = sktaiFinetuningClient.getTrainers(page, size, sort, filter, search);
            log.debug("Trainer 목록 조회 성공 - 조회된 항목 수: {}",
                    response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            log.error("Trainer 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw e;
        } catch (Exception e) {
            log.error("Trainer 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Trainer 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Trainer 상세 조회
     *
     * <p>지정된 Trainer ID에 해당하는 Trainer의 상세 정보를 조회합니다.</p>
     *
     * @param trainerId Trainer 고유 식별자
     * @return Trainer 상세 정보
     * @throws BusinessException Trainer를 찾을 수 없거나 외부 API 오류 시
     */
    public TrainerRead getTrainer(String trainerId) {
        log.debug("Trainer 상세 조회 요청 - id: {}", trainerId);

        try {
            TrainerRead response = sktaiFinetuningClient.getTrainer(trainerId);
            log.debug("Trainer 상세 조회 성공 - id: {}, registry_url: {}",
                    response.getId(), response.getRegistryUrl());
            return response;
        } catch (BusinessException e) {
            log.error("Trainer 상세 조회 실패 - id: {}", trainerId, e);
            throw e;
        } catch (Exception e) {
            log.error("Trainer 상세 조회 실패 - id: {}", trainerId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Trainer 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Trainer 정보 수정
     *
     * <p>기존 Trainer의 설정이나 메타데이터를 수정합니다.
     * 수정 사항을 로깅합니다.</p>
     *
     * @param trainerId Trainer 고유 식별자
     * @param request   Trainer 수정 요청 정보
     * @return 수정된 Trainer 정보
     * @throws BusinessException 외부 API 오류 시
     */
    public TrainerRead updateTrainer(String trainerId, TrainerUpdate request) {
        log.debug("Trainer 수정 요청 - id: {}", trainerId);

        try {
            TrainerRead response = sktaiFinetuningClient.updateTrainer(trainerId, request);
            log.debug("Trainer 수정 성공 - id: {}, registry_url: {}",
                    response.getId(), response.getRegistryUrl());
            return response;
        } catch (BusinessException e) {
            log.error("Trainer 수정 실패 - id: {}", trainerId, e);
            throw e;
        } catch (Exception e) {
            log.error("Trainer 수정 실패 - id: {}", trainerId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Trainer 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Trainer 삭제
     *
     * <p>지정된 Trainer를 삭제합니다.
     * 삭제 작업의 성공 여부를 로깅합니다.</p>
     *
     * @param trainerId Trainer 고유 식별자
     * @throws BusinessException 외부 API 오류 시
     */
    public void deleteTrainer(String trainerId) {
        log.debug("Trainer 삭제 요청 - id: {}", trainerId);

        try {
            sktaiFinetuningClient.deleteTrainer(trainerId);
            log.debug("Trainer 삭제 성공 - id: {}", trainerId);
        } catch (BusinessException e) {
            log.error("Trainer 삭제 실패 - id: {}", trainerId, e);
            throw e;
        } catch (Exception e) {
            log.error("Trainer 삭제 실패 - id: {}", trainerId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Trainer 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
