package com.skax.aiplatform.client.sktai.serving.service;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.serving.SktaiServingClient;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingScale;
import com.skax.aiplatform.client.sktai.serving.dto.request.AgentServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyVerify;
import com.skax.aiplatform.client.sktai.serving.dto.request.BackendAiServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.McpServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.McpServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingScale;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.SharedAgentBackendUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.request.SharedBackendCreate;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingInfo;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyVerifyResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeysResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.BackendAiServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.CreateServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingInfo;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.McpServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.PolicyPayload;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingModelView;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingUpdateResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.SharedAgentBackendRead;
import com.skax.aiplatform.client.sktai.serving.dto.response.SharedAgentBackendsResponse;
import com.skax.aiplatform.client.sktai.serving.dto.response.SktaiOperationResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Serving API Service
 * 
 * <p>
 * SKTAI Serving API와의 통신을 담당하는 비즈니스 로직 서비스입니다.
 * 모델 서빙, 에이전트 서빙, 공유 백엔드, API 키 관리 등의 기능을 제공합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>Model Serving</strong>: 일반 모델 서빙 관리 및 스케일링</li>
 * <li><strong>Agent Serving</strong>: 대화형 AI 서빙 관리</li>
 * <li><strong>Shared Backend</strong>: 공유 백엔드 리소스 관리</li>
 * <li><strong>API Key Management</strong>: 서빙 접근을 위한 API 키 관리</li>
 * </ul>
 * 
 * <h3>에러 처리:</h3>
 * <p>
 * 모든 외부 API 호출에 대해 적절한 예외 처리와 로깅을 수행합니다.
 * </p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SktaiServingClient SKTAI Serving API 클라이언트
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiServingService {

    private final SktaiServingClient sktaiServingClient;

    // ==================== Model Serving Management ====================

    /**
     * 새로운 모델 서빙 생성
     * 
     * <p>
     * 지정된 모델을 사용하여 새로운 서빙 인스턴스를 생성합니다.
     * </p>
     * 
     * @param request 모델 서빙 생성 요청 정보
     * @return 생성된 모델 서빙 정보
     * @throws BusinessException 서빙 생성 실패 시
     */
    public CreateServingResponse createServing(ServingCreate request) {
        log.debug("모델 서빙 생성 요청 - name: {}, modelId: {}",
                request.getName(), request.getModelId());

        try {
            CreateServingResponse response = sktaiServingClient.createServing(request);
            log.info("모델 서빙 생성 성공 - servingId: {}, servingName: {}",
                    response.getServingId(), response.getName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 생성 실패 (BusinessException) - name: {}, modelId: {}, message: {}",
                    request.getName(), request.getModelId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 생성 실패 (예상치 못한 오류) - name: {}, modelId: {}",
                    request.getName(), request.getModelId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Backend.AI 연동 기반 모델 서빙 생성
     * 
     * <p>
     * Backend.AI 시스템과 연동하여 새로운 모델 서빙 인스턴스를 생성합니다.
     * Backend.AI의 런타임과 이미지를 사용하여 모델을 서빙합니다.
     * </p>
     * 
     * @param request Backend.AI 모델 서빙 생성 요청 정보
     * @return 생성된 Backend.AI 모델 서빙 정보
     * @throws BusinessException Backend.AI 서빙 생성 실패 시
     */
    public BackendAiServingResponse createBackendAiServing(BackendAiServingCreate request) {
        log.debug("Backend.AI 모델 서빙 생성 요청 - {}", request);

        try {
            BackendAiServingResponse response = sktaiServingClient.createBackendAiServing(request);
            log.info("Backend.AI 모델 서빙 생성 성공 - servingId: {}, servingName: {}",
                    response.getServingId(), response.getName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Backend.AI 모델 서빙 생성 실패 (BusinessException) - name: {}, modelId: {}, message: {}",
                    request.getName(), request.getModelId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Backend.AI 모델 서빙 생성 실패 (예상치 못한 오류) - name: {}, modelId: {}",
                    request.getName(), request.getModelId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Backend.AI 모델 서빙 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 서빙 목록 조회
     * 
     * <p>
     * 사용자가 접근 가능한 모든 모델 서빙의 목록을 조회합니다.
     * </p>
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 옵션
     * @param filter 필터 조건
     * @param search 검색어
     * @return 모델 서빙 목록
     * @throws BusinessException 목록 조회 실패 시
     */
    public ServingsResponse getServings(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("모델 서빙 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);

        try {
            ServingsResponse response = sktaiServingClient.getServings(page, size, sort, filter, search);

            log.debug("모델 서빙 목록 조회 성공 - 조회된 항목 수: {}",
                    response != null ? "응답 데이터 존재" : "응답 데이터 없음");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 목록 조회 실패 (BusinessException) - page: {}, size: {}, search: {}, message: {}",
                    page, size, search, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, search: {}", page, size, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 모델 서빙 상세 조회
     * 
     * <p>
     * 서빙 ID를 사용하여 특정 모델 서빙의 상세 정보를 조회합니다.
     * </p>
     * 
     * @param servingId 조회할 서빙의 ID
     * @return 모델 서빙 상세 정보
     * @throws BusinessException 서빙 조회 실패 시
     */
    public ServingResponse getServing(String servingId) {
        log.debug("모델 서빙 상세 조회 요청 - servingId: {}", servingId);

        try {
            ServingResponse response = sktaiServingClient.getServing(servingId);
            log.info("모델 서빙 상세 조회 성공 - servingId: {}, servingName: {}",
                    servingId, response.getName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 상세 조회 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 상세 조회 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 서빙 정보 수정
     * 
     * <p>
     * 기존 모델 서빙의 설정을 수정합니다.
     * </p>
     * 
     * @param servingId 수정할 서빙의 ID
     * @param request   모델 서빙 수정 요청 정보
     * @return 수정된 모델 서빙 정보
     * @throws BusinessException 서빙 수정 실패 시
     */
    public ServingUpdateResponse updateServing(String servingId, ServingUpdate request) {
        log.debug("모델 서빙 수정 요청 - servingId: {}", servingId);

        try {
            ServingUpdateResponse response = sktaiServingClient.updateServing(servingId, request);
            log.info("모델 서빙 수정 성공 - servingId: {}, servingName: {}",
                    servingId, response.getName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 수정 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 수정 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 서빙 삭제
     * 
     * <p>
     * 지정된 모델 서빙을 삭제합니다.
     * </p>
     * 
     * @param servingId 삭제할 서빙의 ID
     * @throws BusinessException 서빙 삭제 실패 시
     */
    public void deleteServing(String servingId) {
        log.debug("모델 서빙 삭제 요청 - servingId: {}", servingId);

        try {
            sktaiServingClient.deleteServing(servingId);
            log.info("모델 서빙 삭제 성공 - servingId: {}", servingId);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 삭제 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 삭제 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Backend.AI 모델 서빙 정보 수정
     * 
     * <p>
     * Backend.AI 기반 모델 서빙의 설정을 수정합니다.
     * </p>
     * 
     * @param servingId 수정할 서빙의 ID
     * @param request   모델 서빙 수정 요청 정보
     * @return 수정된 모델 서빙 정보
     * @throws BusinessException 서빙 수정 실패 시
     */
    public ServingUpdateResponse updateBackendAiServing(String servingId, ServingUpdate request) {
        log.debug("Backend.AI 모델 서빙 수정 요청 - servingId: {}", servingId);

        try {
            ServingUpdateResponse response = sktaiServingClient.updateBackendAiServing(servingId, request);
            log.info("Backend.AI 모델 서빙 수정 성공 - servingId: {}, servingName: {}",
                    servingId, response.getName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Backend.AI 모델 서빙 수정 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Backend.AI 모델 서빙 수정 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Backend.AI 모델 서빙 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Backend.AI 모델 서빙 삭제
     * 
     * <p>
     * 지정된 Backend.AI 모델 서빙을 삭제합니다.
     * </p>
     * 
     * @param servingId 삭제할 서빙의 ID
     * @throws BusinessException 서빙 삭제 실패 시
     */
    public void deleteBackendAiServing(String servingId) {
        log.debug("Backend.AI 모델 서빙 삭제 요청 - servingId: {}", servingId);

        try {
            sktaiServingClient.deleteBackendAiServing(servingId);
            log.info("Backend.AI 모델 서빙 삭제 성공 - servingId: {}", servingId);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Backend.AI 모델 서빙 삭제 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Backend.AI 모델 서빙 삭제 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Backend.AI 모델 서빙 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 서빙 스케일링
     * 
     * <p>
     * 모델 서빙의 레플리카 수를 조정합니다.
     * </p>
     * 
     * @param servingId 스케일링할 서빙의 ID
     * @param request   스케일링 요청 정보
     * @return 스케일링된 모델 서빙 정보
     * @throws BusinessException 서빙 스케일링 실패 시
     */
    public ServingResponse scaleServing(String servingId, ServingScale request) {
        log.debug("모델 서빙 스케일링 요청 - servingId: {}, replicas: {}",
                servingId, request.getReplicas());

        try {
            ServingResponse response = sktaiServingClient.scaleServing(servingId, request);
            log.info("모델 서빙 스케일링 성공 - servingId: {}, replicas: {} -> {}",
                    servingId, request.getReplicas(), response.getMinReplicas());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 스케일링 실패 (BusinessException) - servingId: {}, replicas: {}, message: {}",
                    servingId, request.getReplicas(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 스케일링 실패 (예상치 못한 오류) - servingId: {}, replicas: {}",
                    servingId, request.getReplicas(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 스케일링에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 서빙 시작
     * 
     * <p>
     * 중지된 모델 서빙을 시작합니다.
     * </p>
     * 
     * @param servingId 시작할 서빙의 ID
     * @return 시작 결과
     * @throws BusinessException 모델 서빙 시작 실패 시
     */
    public SktaiOperationResponse startServing(String servingId) {
        log.debug("모델 서빙 시작 요청 - servingId: {}", servingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.startServing(servingId);
            log.info("모델 서빙 시작 성공 - servingId: {}", servingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 시작 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 시작 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 시작에 실패했습니다: " + e.getMessage());
        }
    }

    public SktaiOperationResponse startBackendAiServing(String servingId) {
        log.debug("Backend.AI 모델 서빙 시작 요청 - servingId: {}", servingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.startBackendAiServing(servingId);
            log.info("Backend.AI 모델 서빙 시작 성공 - servingId: {}", servingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Backend.AI 모델 서빙 시작 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Backend.AI 모델 서빙 시작 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Backend.AI 모델 서빙 시작에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 서빙 중지
     * 
     * <p>
     * 실행 중인 모델 서빙을 중지합니다.
     * </p>
     * 
     * @param servingId 중지할 서빙의 ID
     * @return 중지 결과
     * @throws BusinessException 모델 서빙 중지 실패 시
     */
    public SktaiOperationResponse stopServing(String servingId) {
        log.debug("모델 서빙 중지 요청 - servingId: {}", servingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.stopServing(servingId);
            log.info("모델 서빙 중지 성공 - servingId: {}", servingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 중지 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 중지 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 중지에 실패했습니다: " + e.getMessage());
        }
    }

    public SktaiOperationResponse stopBackendAiServing(String servingId) {
        log.debug("Backend.AI 모델 서빙 중지 요청 - servingId: {}", servingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.stopBackendAiServing(servingId);
            log.info("Backend.AI 모델 서빙 중지 성공 - servingId: {}", servingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Backend.AI 모델 서빙 중지 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Backend.AI 모델 서빙 중지 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Backend.AI 모델 서빙 중지에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 서빙 API 키 목록 조회
     * 
     * <p>
     * 특정 모델 서빙에서 사용 가능한 Gateway API 키 목록을 조회합니다.
     * </p>
     * 
     * @param servingId 서빙 ID
     * @param page      페이지 번호
     * @param size      페이지 크기
     * @param sort      정렬 옵션
     * @param filter    필터 조건
     * @param search    검색어
     * @return 사용 가능한 API 키 목록
     * @throws BusinessException API 키 목록 조회 실패 시
     */
    public ApiKeysResponse getServingApiKeys(String servingId, Integer page, Integer size, String sort, String filter,
            String search) {
        log.debug("모델 서빙 API 키 목록 조회 요청 - servingId: {}, page: {}, size: {}", servingId, page, size);

        try {
            ApiKeysResponse response = sktaiServingClient.getServingApiKeys(servingId, page, size, sort, filter,
                    search);
            log.info("모델 서빙 API 키 목록 조회 성공 - servingId: {}, 조회된 키 수: {}",
                    servingId, response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모델 서빙 API 키 목록 조회 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모델 서빙 API 키 목록 조회 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모델 서빙 API 키 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모든 모델 서빙 하드 삭제
     * 
     * <p>
     * 모든 모델 서빙 DB 행을 하드 삭제합니다 (is_deleted를 True로 마킹).
     * </p>
     * 
     * @throws BusinessException 하드 삭제 실패 시
     */
    public void hardDeleteServings() {
        log.debug("모든 모델 서빙 하드 삭제 요청");

        try {
            sktaiServingClient.hardDeleteServings();
            log.info("모든 모델 서빙 하드 삭제 성공");
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모든 모델 서빙 하드 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모든 모델 서빙 하드 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모든 모델 서빙 하드 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    // ==================== Agent Serving Management ====================

    /**
     * 새로운 에이전트 서빙 생성
     * 
     * <p>
     * 지정된 에이전트를 사용하여 새로운 에이전트 서빙 인스턴스를 생성합니다.
     * </p>
     * 
     * @param request 에이전트 서빙 생성 요청 정보
     * @return 생성된 에이전트 서빙 정보
     * @throws BusinessException 에이전트 서빙 생성 실패 시
     */
    public AgentServingResponse createAgentServing(AgentServingCreate request) {
        log.debug("에이전트 서빙 생성 요청 - agentServingName: {}, agentId: {}",
                request.getAgentServingName(), request.getAgentId());

        try {
            AgentServingResponse response = sktaiServingClient.createAgentServing(request);
            log.info("에이전트 서빙 생성 성공 - agentServingId: {}, agentServingName: {}",
                    response.getAgentServingId(), response.getAgentServingName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 생성 실패 (BusinessException) - agentServingName: {}, agentId: {}, message: {}",
                    request.getAgentServingName(), request.getAgentId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 생성 실패 (예상치 못한 오류) - agentServingName: {}, agentId: {}",
                    request.getAgentServingName(), request.getAgentId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 서빙 목록 조회
     * 
     * <p>
     * 사용자가 접근 가능한 모든 에이전트 서빙의 목록을 조회합니다.
     * </p>
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 옵션
     * @param filter 필터 조건
     * @param search 검색어
     * @return 에이전트 서빙 목록
     * @throws BusinessException 목록 조회 실패 시
     */
    public AgentServingsResponse getAgentServings(Integer page, Integer size, String sort, String filter,
            String search) {
        log.debug("에이전트 서빙 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);

        try {
            AgentServingsResponse response = sktaiServingClient.getAgentServings(page, size, sort, filter, search);
            log.info("에이전트 서빙 목록 조회 성공 - page: {}, size: {}", page, size);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}",
                    page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 에이전트 서빙 상세 조회
     * 
     * <p>
     * 에이전트 서빙 ID를 사용하여 특정 에이전트 서빙의 상세 정보를 조회합니다.
     * </p>
     * 
     * @param agentServingId 조회할 에이전트 서빙의 ID
     * @return 에이전트 서빙 상세 정보
     * @throws BusinessException 에이전트 서빙 조회 실패 시
     */
    public AgentServingResponse getAgentServing(String agentServingId) {
        log.debug("에이전트 서빙 상세 조회 요청 - agentServingId: {}", agentServingId);

        try {
            AgentServingResponse response = sktaiServingClient.getAgentServing(agentServingId);
            log.info("에이전트 서빙 상세 조회 성공 - agentServingId: {}, agentServingName: {}",
                    agentServingId, response.getAgentServingName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 상세 조회 실패 (BusinessException) - agentServingId: {}, message: {}",
                    agentServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 상세 조회 실패 (예상치 못한 오류) - agentServingId: {}", agentServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 서빙 정보 수정
     * 
     * <p>
     * 기존 에이전트 서빙의 설정을 수정합니다.
     * </p>
     * 
     * @param agentServingId 수정할 에이전트 서빙의 ID
     * @param request        에이전트 서빙 수정 요청 정보
     * @return 수정된 에이전트 서빙 정보
     * @throws BusinessException 에이전트 서빙 수정 실패 시
     */
    public AgentServingResponse updateAgentServing(String agentServingId, AgentServingUpdate request) {
        log.debug("에이전트 서빙 수정 요청 - agentServingId: {}", agentServingId);

        try {
            AgentServingResponse response = sktaiServingClient.updateAgentServing(agentServingId, request);
            log.info("에이전트 서빙 수정 성공 - agentServingId: {}, agentServingName: {}",
                    agentServingId, response.getAgentServingName());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 수정 실패 (BusinessException) - agentServingId: {}, message: {}",
                    agentServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 수정 실패 (예상치 못한 오류) - agentServingId: {}", agentServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 서빙 삭제
     * 
     * <p>
     * 지정된 에이전트 서빙을 삭제합니다.
     * </p>
     * 
     * @param agentServingId 삭제할 에이전트 서빙의 ID
     * @throws BusinessException 에이전트 서빙 삭제 실패 시
     */
    public void deleteAgentServing(String agentServingId) {
        log.debug("에이전트 서빙 삭제 요청 - agentServingId: {}", agentServingId);

        try {
            sktaiServingClient.deleteAgentServing(agentServingId);
            log.info("에이전트 서빙 삭제 성공 - agentServingId: {}", agentServingId);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 삭제 실패 (BusinessException) - agentServingId: {}, message: {}",
                    agentServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 삭제 실패 (예상치 못한 오류) - agentServingId: {}", agentServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 서빙 스케일링
     * 
     * <p>
     * 에이전트 서빙의 레플리카 수를 조정합니다.
     * </p>
     * 
     * @param agentServingId 스케일링할 에이전트 서빙의 ID
     * @param request        스케일링 요청 정보
     * @return 스케일링된 에이전트 서빙 정보
     * @throws BusinessException 에이전트 서빙 스케일링 실패 시
     */
    public AgentServingResponse scaleAgentServing(String agentServingId, AgentServingScale request) {
        log.debug("에이전트 서빙 스케일링 요청 - agentServingId: {}, replicas: {}",
                agentServingId, request.getReplicas());

        try {
            AgentServingResponse response = sktaiServingClient.scaleAgentServing(agentServingId, request);
            log.info("에이전트 서빙 스케일링 성공 - agentServingId: {}, replicas: {} -> {}",
                    agentServingId, request.getReplicas(), response.getCurrentReplicas());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 스케일링 실패 (BusinessException) - agentServingId: {}, replicas: {}, message: {}",
                    agentServingId, request.getReplicas(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 스케일링 실패 (예상치 못한 오류) - agentServingId: {}, replicas: {}",
                    agentServingId, request.getReplicas(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 스케일링에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 서빙 시작
     * 
     * <p>
     * 중지된 에이전트 서빙을 시작합니다.
     * </p>
     * 
     * @param agentServingId 시작할 에이전트 서빙의 ID
     * @return 시작 결과
     * @throws BusinessException 에이전트 서빙 시작 실패 시
     */
    public SktaiOperationResponse startAgentServing(String agentServingId) {
        log.debug("에이전트 서빙 시작 요청 - agentServingId: {}", agentServingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.startAgentServing(agentServingId);
            log.info("에이전트 서빙 시작 성공 - agentServingId: {}", agentServingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 시작 실패 (BusinessException) - agentServingId: {}, message: {}",
                    agentServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 시작 실패 (예상치 못한 오류) - agentServingId: {}", agentServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 시작에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 서빙 중지
     * 
     * <p>
     * 실행 중인 에이전트 서빙을 중지합니다.
     * </p>
     * 
     * @param agentServingId 중지할 에이전트 서빙의 ID
     * @return 중지 결과
     * @throws BusinessException 에이전트 서빙 중지 실패 시
     */
    public SktaiOperationResponse stopAgentServing(String agentServingId) {
        log.debug("에이전트 서빙 중지 요청 - agentServingId: {}", agentServingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.stopAgentServing(agentServingId);
            log.info("에이전트 서빙 중지 성공 - agentServingId: {}", agentServingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 서빙 중지 실패 (BusinessException) - agentServingId: {}, message: {}",
                    agentServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 서빙 중지 실패 (예상치 못한 오류) - agentServingId: {}", agentServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 중지에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 앱 API 키 목록 조회
     * 
     * <p>
     * 특정 에이전트 앱에서 사용 가능한 Gateway API 키 목록을 조회합니다.
     * </p>
     * 
     * @param agentAppId 에이전트 앱 ID
     * @param page       페이지 번호
     * @param size       페이지 크기
     * @param sort       정렬 옵션
     * @param filter     필터 조건
     * @param search     검색어
     * @return 사용 가능한 API 키 목록
     * @throws BusinessException API 키 목록 조회 실패 시
     */
    public ApiKeysResponse getAgentAppApiKeys(String agentAppId, Integer page, Integer size, String sort, String filter,
            String search) {
        log.debug("에이전트 앱 API 키 목록 조회 요청 - agentAppId: {}, page: {}, size: {}", agentAppId, page, size);

        try {
            ApiKeysResponse response = sktaiServingClient.getAgentAppApiKeys(agentAppId, page, size, sort, filter,
                    search);
            log.info("에이전트 앱 API 키 목록 조회 성공 - agentAppId: {}, 조회된 키 수: {}",
                    agentAppId, response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("에이전트 앱 API 키 목록 조회 실패 (BusinessException) - agentAppId: {}, message: {}",
                    agentAppId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("에이전트 앱 API 키 목록 조회 실패 (예상치 못한 오류) - agentAppId: {}", agentAppId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 앱 API 키 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모든 에이전트 서빙 하드 삭제
     * 
     * <p>
     * 모든 에이전트 서빙 DB 행을 하드 삭제합니다 (is_deleted를 True로 마킹).
     * </p>
     * 
     * @throws BusinessException 하드 삭제 실패 시
     */
    public void hardDeleteAgentServings() {
        log.debug("모든 에이전트 서빙 하드 삭제 요청");

        try {
            sktaiServingClient.hardDeleteAgentServings();
            log.info("모든 에이전트 서빙 하드 삭제 성공");
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("모든 에이전트 서빙 하드 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("모든 에이전트 서빙 하드 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "모든 에이전트 서빙 하드 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    // ==================== API Key Management ====================

    /**
     * 새로운 API 키 생성
     * 
     * <p>
     * 서빙 엔드포인트에 접근하기 위한 새로운 API 키를 생성합니다.
     * </p>
     * 
     * @param request API 키 생성 요청 정보
     * @return 생성된 API 키 정보 (키 값 포함)
     * @throws BusinessException API 키 생성 실패 시
     */
    public ApiKeyResponse createApiKey(ApiKeyCreate request) {
        log.debug("API 키 생성 요청 - servingId: {}, gatewayType: {}",
                request.getServingId(), request.getGatewayType());

        try {
            ApiKeyResponse response = sktaiServingClient.createApiKey(request);
            log.info("API 키 생성 성공 - apiKeyId: {}, gatewayType: {}",
                    response.getApiKeyId(), response.getGatewayType());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("API 키 생성 실패 (BusinessException) - servingId: {}, gatewayType: {}, message: {}",
                    request.getServingId(), request.getGatewayType(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("API 키 생성 실패 (예상치 못한 오류) - servingId: {}, gatewayType: {}",
                    request.getServingId(), request.getGatewayType(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "API 키 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * API 키 목록 조회
     * 
     * <p>
     * 사용자가 생성한 모든 API 키의 목록을 조회합니다.
     * </p>
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 옵션
     * @param filter 필터 조건
     * @param search 검색어
     * @return API 키 목록
     * @throws BusinessException 목록 조회 실패 시
     */
    public ApiKeysResponse getApiKeys(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("API 키 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);

        try {
            ApiKeysResponse response = sktaiServingClient.getApiKeys(page, size, sort, filter, search);
            log.info("API 키 목록 조회 성공 - page: {}, size: {}", page, size);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("API 키 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}",
                    page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("API 키 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "API 키 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 API 키 상세 조회
     * 
     * <p>
     * API 키 ID를 사용하여 특정 API 키의 상세 정보를 조회합니다.
     * </p>
     * 
     * @param apiKeyId 조회할 API 키의 ID
     * @return API 키 상세 정보
     * @throws BusinessException API 키 조회 실패 시
     */
    public ApiKeyResponse getApiKey(String apiKeyId) {
        log.debug("API 키 상세 조회 요청 - apiKeyId: {}", apiKeyId);

        try {
            ApiKeyResponse response = sktaiServingClient.getApiKey(apiKeyId);
            log.info("API 키 상세 조회 성공 - apiKeyId: {}, gatewayType: {}",
                    apiKeyId, response.getGatewayType());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("API 키 상세 조회 실패 (BusinessException) - apiKeyId: {}, message: {}",
                    apiKeyId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("API 키 상세 조회 실패 (예상치 못한 오류) - apiKeyId: {}", apiKeyId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "API 키 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * API 키 삭제
     * 
     * <p>
     * 지정된 API 키를 삭제합니다.
     * </p>
     * 
     * @param apiKeyId 삭제할 API 키의 ID
     * @throws BusinessException API 키 삭제 실패 시
     */
    public void deleteApiKey(String apiKeyId) {
        log.debug("API 키 삭제 요청 - apiKeyId: {}", apiKeyId);

        try {
            sktaiServingClient.deleteApiKey(apiKeyId);
            log.info("API 키 삭제 성공 - apiKeyId: {}", apiKeyId);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("API 키 삭제 실패 (BusinessException) - apiKeyId: {}, message: {}",
                    apiKeyId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("API 키 삭제 실패 (예상치 못한 오류) - apiKeyId: {}", apiKeyId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "API 키 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * API 키 업데이트
     * 
     * <p>
     * 기존 API 키의 설정을 업데이트합니다.
     * </p>
     * 
     * @param apiKeyId 업데이트할 API 키의 ID
     * @param request  API 키 업데이트 요청 정보
     * @return 업데이트된 API 키 정보
     * @throws BusinessException API 키 업데이트 실패 시
     */
    public ApiKeyResponse updateApiKey(String apiKeyId, ApiKeyUpdate request) {
        log.debug("API 키 업데이트 요청 - apiKeyId: {}", apiKeyId);

        try {
            ApiKeyResponse response = sktaiServingClient.updateApiKey(apiKeyId, request);
            log.info("API 키 업데이트 성공 - apiKeyId: {}", apiKeyId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("API 키 업데이트 실패 (BusinessException) - apiKeyId: {}, message: {}",
                    apiKeyId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("API 키 업데이트 실패 (예상치 못한 오류) - apiKeyId: {}", apiKeyId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 키 업데이트에 실패했습니다.");
        }
    }

    /**
     * API 키 검증
     * 
     * <p>
     * API 키의 유효성을 검증합니다.
     * </p>
     * 
     * @param request API 키 검증 요청 정보
     * @return 검증 결과
     * @throws BusinessException API 키 검증 실패 시
     */
    public ApiKeyVerifyResponse verifyApiKey(ApiKeyVerify request) {
        log.debug("API 키 검증 요청 - projectId: {}, gatewayType: {}", request.getProjectId(), request.getGatewayType());

        try {
            ApiKeyVerifyResponse response = sktaiServingClient.verifyApiKey(request);
            log.info("API 키 검증 성공 - projectId: {}", request.getProjectId());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("API 키 검증 실패 (BusinessException) - projectId: {}, gatewayType: {}, message: {}",
                    request.getProjectId(), request.getGatewayType(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("API 키 검증 실패 (예상치 못한 오류) - projectId: {}", request.getProjectId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 키 검증에 실패했습니다.");
        }
    }

    // ==================== MCP Serving Management ====================

    /**
     * MCP 서빙 생성
     * 
     * <p>
     * 새로운 MCP 서빙을 생성합니다.
     * </p>
     * 
     * @param request MCP 서빙 생성 요청 정보
     * @return 생성된 MCP 서빙 정보
     * @throws BusinessException MCP 서빙 생성 실패 시
     */
    public McpServingResponse createMcpServing(McpServingCreate request) {
        log.debug("MCP 서빙 생성 요청 - deploymentName: {}, mcpId: {}", request.getDeploymentName(), request.getMcpId());

        try {
            McpServingResponse response = sktaiServingClient.createMcpServing(request);
            log.info("MCP 서빙 생성 성공 - mcpServingId: {}", response.getMcpServingId());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 생성 실패 (BusinessException) - deploymentName: {}, message: {}",
                    request.getDeploymentName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 생성 실패 (예상치 못한 오류) - deploymentName: {}", request.getDeploymentName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 생성에 실패했습니다.");
        }
    }

    /**
     * MCP 서빙 목록 조회
     * 
     * <p>
     * 페이징된 MCP 서빙 목록을 조회합니다.
     * </p>
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색 조건
     * @return MCP 서빙 목록
     * @throws BusinessException MCP 서빙 목록 조회 실패 시
     */
    public McpServingsResponse getMcpServings(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("MCP 서빙 목록 조회 요청 - page: {}, size: {}", page, size);

        try {
            McpServingsResponse response = sktaiServingClient.getMcpServings(page, size, sort, filter, search);
            log.info("MCP 서빙 목록 조회 성공 - 조회된 서빙 수: {}", response.getData().size());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}",
                    page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 목록 조회에 실패했습니다.");
        }
    }

    /**
     * MCP 서빙 상세 조회
     * 
     * <p>
     * 특정 MCP 서빙의 상세 정보를 조회합니다.
     * </p>
     * 
     * @param mcpServingId MCP 서빙 ID
     * @return MCP 서빙 상세 정보
     * @throws BusinessException MCP 서빙 조회 실패 시
     */
    public McpServingInfo getMcpServing(String mcpServingId) {
        log.debug("MCP 서빙 상세 조회 요청 - mcpServingId: {}", mcpServingId);

        try {
            McpServingInfo response = sktaiServingClient.getMcpServing(mcpServingId);
            log.info("MCP 서빙 상세 조회 성공 - mcpServingId: {}", mcpServingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 상세 조회 실패 (BusinessException) - mcpServingId: {}, message: {}",
                    mcpServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 상세 조회 실패 (예상치 못한 오류) - mcpServingId: {}", mcpServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 상세 조회에 실패했습니다.");
        }
    }

    /**
     * MCP 서빙 업데이트
     * 
     * <p>
     * 기존 MCP 서빙의 설정을 업데이트합니다.
     * </p>
     * 
     * @param mcpServingId MCP 서빙 ID
     * @param request      MCP 서빙 업데이트 요청 정보
     * @return 업데이트 결과
     * @throws BusinessException MCP 서빙 업데이트 실패 시
     */
    public SktaiOperationResponse updateMcpServing(String mcpServingId, McpServingUpdate request) {
        log.debug("MCP 서빙 업데이트 요청 - mcpServingId: {}", mcpServingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.updateMcpServing(mcpServingId, request);
            log.info("MCP 서빙 업데이트 성공 - mcpServingId: {}", mcpServingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 업데이트 실패 (BusinessException) - mcpServingId: {}, message: {}",
                    mcpServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 업데이트 실패 (예상치 못한 오류) - mcpServingId: {}", mcpServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 업데이트에 실패했습니다.");
        }
    }

    /**
     * MCP 서빙 삭제
     * 
     * <p>
     * 지정된 MCP 서빙을 삭제합니다.
     * </p>
     * 
     * @param mcpServingId 삭제할 MCP 서빙 ID
     * @throws BusinessException MCP 서빙 삭제 실패 시
     */
    public void deleteMcpServing(String mcpServingId) {
        log.debug("MCP 서빙 삭제 요청 - mcpServingId: {}", mcpServingId);

        try {
            sktaiServingClient.deleteMcpServing(mcpServingId);
            log.info("MCP 서빙 삭제 성공 - mcpServingId: {}", mcpServingId);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 삭제 실패 (BusinessException) - mcpServingId: {}, message: {}",
                    mcpServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 삭제 실패 (예상치 못한 오류) - mcpServingId: {}", mcpServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 삭제에 실패했습니다.");
        }
    }

    /**
     * MCP 서빙 시작
     * 
     * <p>
     * 지정된 MCP 서빙을 시작합니다.
     * </p>
     * 
     * @param mcpServingId 시작할 MCP 서빙 ID
     * @return 시작 결과
     * @throws BusinessException MCP 서빙 시작 실패 시
     */
    public SktaiOperationResponse startMcpServing(String mcpServingId) {
        log.debug("MCP 서빙 시작 요청 - mcpServingId: {}", mcpServingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.startMcpServing(mcpServingId);
            log.info("MCP 서빙 시작 성공 - mcpServingId: {}", mcpServingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 시작 실패 (BusinessException) - mcpServingId: {}, message: {}",
                    mcpServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 시작 실패 (예상치 못한 오류) - mcpServingId: {}", mcpServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 시작에 실패했습니다.");
        }
    }

    /**
     * MCP 서빙 중지
     * 
     * <p>
     * 지정된 MCP 서빙을 중지합니다.
     * </p>
     * 
     * @param mcpServingId 중지할 MCP 서빙 ID
     * @return 중지 결과
     * @throws BusinessException MCP 서빙 중지 실패 시
     */
    public SktaiOperationResponse stopMcpServing(String mcpServingId) {
        log.debug("MCP 서빙 중지 요청 - mcpServingId: {}", mcpServingId);

        try {
            SktaiOperationResponse response = sktaiServingClient.stopMcpServing(mcpServingId);
            log.info("MCP 서빙 중지 성공 - mcpServingId: {}", mcpServingId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 중지 실패 (BusinessException) - mcpServingId: {}, message: {}",
                    mcpServingId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 중지 실패 (예상치 못한 오류) - mcpServingId: {}", mcpServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 중지에 실패했습니다.");
        }
    }

    /**
     * MCP 서빙 하드 삭제
     * 
     * <p>
     * 모든 MCP 서빙을 하드 삭제합니다.
     * </p>
     * 
     * @throws BusinessException MCP 서빙 하드 삭제 실패 시
     */
    public void hardDeleteMcpServings() {
        log.debug("MCP 서빙 하드 삭제 요청");

        try {
            sktaiServingClient.hardDeleteMcpServings();
            log.info("MCP 서빙 하드 삭제 성공");
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP 서빙 하드 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP 서빙 하드 삭제 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP 서빙 하드 삭제에 실패했습니다.");
        }
    }

    /**
     * MCP API 키 목록 조회
     * 
     * <p>
     * 지정된 MCP에서 사용 가능한 API 키 목록을 조회합니다.
     * </p>
     * 
     * @param mcpId  MCP ID
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색 조건
     * @return 사용 가능한 API 키 목록
     * @throws BusinessException API 키 목록 조회 실패 시
     */
    public ApiKeysResponse getMcpApiKeys(String mcpId, Integer page, Integer size, String sort, String filter,
            String search) {
        log.debug("MCP API 키 목록 조회 요청 - mcpId: {}, page: {}, size: {}", mcpId, page, size);

        try {
            ApiKeysResponse response = sktaiServingClient.getMcpApiKeys(mcpId, page, size, sort, filter, search);
            log.info("MCP API 키 목록 조회 성공 - mcpId: {}, 조회된 키 수: {}", mcpId,
                    response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("MCP API 키 목록 조회 실패 (BusinessException) - mcpId: {}, message: {}",
                    mcpId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("MCP API 키 목록 조회 실패 (예상치 못한 오류) - mcpId: {}", mcpId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "MCP API 키 목록 조회에 실패했습니다.");
        }
    }

    // ==================== Shared Agent Backend Management ====================

    /**
     * Shared Backend 목록 조회
     * 
     * <p>
     * 모든 Shared Backend 목록을 조회합니다.
     * </p>
     * 
     * @return Shared Backend 목록
     * @throws BusinessException Shared Backend 목록 조회 실패 시
     */
    public SharedAgentBackendsResponse getSharedBackends() {
        log.debug("Shared Backend 목록 조회 요청");

        try {
            SharedAgentBackendsResponse response = sktaiServingClient.getSharedBackends();
            log.info("Shared Backend 목록 조회 성공 - 조회된 백엔드 수: {}", response.getData().size());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Shared Backend 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Shared Backend 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Shared Backend 목록 조회에 실패했습니다.");
        }
    }

    /**
     * Shared Backend 조회
     * 
     * <p>
     * 프로젝트 ID로 Shared Backend 정보를 조회합니다.
     * </p>
     * 
     * @param projectId 프로젝트 ID
     * @return Shared Backend 정보
     * @throws BusinessException Shared Backend 조회 실패 시
     */
    public SharedAgentBackendRead getSharedBackend(String projectId) {
        log.debug("Shared Backend 조회 요청 - projectId: {}", projectId);

        try {
            SharedAgentBackendRead response = sktaiServingClient.getSharedBackend(projectId);
            log.info("Shared Backend 조회 성공 - projectId: {}", projectId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Shared Backend 조회 실패 (BusinessException) - projectId: {}, message: {}",
                    projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Shared Backend 조회 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Shared Backend 조회에 실패했습니다.");
        }
    }

    /**
     * Shared Backend 생성
     * 
     * <p>
     * 새로운 Shared Backend를 생성합니다.
     * </p>
     * 
     * @param projectId 프로젝트 ID
     * @param request   Shared Backend 생성 요청 정보
     * @return 생성된 Shared Backend 정보
     * @throws BusinessException Shared Backend 생성 실패 시
     */
    public SharedAgentBackendRead createSharedBackend(String projectId, SharedBackendCreate request) {
        log.debug("Shared Backend 생성 요청 - projectId: {}", projectId);

        try {
            SharedAgentBackendRead response = sktaiServingClient.createSharedBackend(projectId, request);
            log.info("Shared Backend 생성 성공 - projectId: {}, backendId: {}", projectId,
                    response.getSharedAgentBackendId());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Shared Backend 생성 실패 (BusinessException) - projectId: {}, message: {}",
                    projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Shared Backend 생성 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Shared Backend 생성에 실패했습니다.");
        }
    }

    /**
     * Shared Backend 업데이트
     * 
     * <p>
     * 기존 Shared Backend를 업데이트합니다.
     * </p>
     * 
     * @param projectId 프로젝트 ID
     * @param request   Shared Backend 업데이트 요청 정보
     * @return 업데이트된 Shared Backend 정보
     * @throws BusinessException Shared Backend 업데이트 실패 시
     */
    public SharedAgentBackendRead updateSharedBackend(String projectId, SharedAgentBackendUpdate request) {
        log.debug("Shared Backend 업데이트 요청 - projectId: {}", projectId);

        try {
            SharedAgentBackendRead response = sktaiServingClient.updateSharedBackend(projectId, request);
            log.info("Shared Backend 업데이트 성공 - projectId: {}", projectId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Shared Backend 업데이트 실패 (BusinessException) - projectId: {}, message: {}",
                    projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Shared Backend 업데이트 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Shared Backend 업데이트에 실패했습니다.");
        }
    }

    /**
     * Shared Backend 시작
     * 
     * <p>
     * 지정된 Shared Backend를 시작합니다.
     * </p>
     * 
     * @param projectId 프로젝트 ID
     * @return 시작 결과
     * @throws BusinessException Shared Backend 시작 실패 시
     */
    public SktaiOperationResponse startSharedBackend(String projectId) {
        log.debug("Shared Backend 시작 요청 - projectId: {}", projectId);

        try {
            SktaiOperationResponse response = sktaiServingClient.startSharedBackend(projectId);
            log.info("Shared Backend 시작 성공 - projectId: {}", projectId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Shared Backend 시작 실패 (BusinessException) - projectId: {}, message: {}",
                    projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Shared Backend 시작 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Shared Backend 시작에 실패했습니다.");
        }
    }

    /**
     * Shared Backend 중지
     * 
     * <p>
     * 지정된 Shared Backend를 중지합니다.
     * </p>
     * 
     * @param projectId 프로젝트 ID
     * @return 중지 결과
     * @throws BusinessException Shared Backend 중지 실패 시
     */
    public SktaiOperationResponse stopSharedBackend(String projectId) {
        log.debug("Shared Backend 중지 요청 - projectId: {}", projectId);

        try {
            SktaiOperationResponse response = sktaiServingClient.stopSharedBackend(projectId);
            log.info("Shared Backend 중지 성공 - projectId: {}", projectId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Shared Backend 중지 실패 (BusinessException) - projectId: {}, message: {}",
                    projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Shared Backend 중지 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Shared Backend 중지에 실패했습니다.");
        }
    }

    /**
     * 서빙 모델 뷰 조회
     * 
     * <p>
     * 지정된 서빙의 모델 뷰 정보를 조회합니다.
     * </p>
     * 
     * @param servingId 조회할 서빙 ID
     * @return 모델 뷰 정보
     * @throws BusinessException 모델 뷰 조회 실패 시
     */
    public ServingModelView getServingModelView(String servingId) {
        log.debug("서빙 모델 뷰 조회 요청 - servingId: {}", servingId);

        try {
            ServingModelView response = sktaiServingClient.getServingModelView(servingId);
            log.info("서빙 모델 뷰 조회 성공 - servingId: {}", servingId);
            return response;
        } catch (BusinessException e) {
            log.error("서빙 모델 뷰 조회 실패 (BusinessException) - servingId: {}, message: {}",
                    servingId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("서빙 모델 뷰 조회 실패 (예상치 못한 오류) - servingId: {}", servingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "서빙 모델 뷰 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 서빙 상세 정보 조회
     * 
     * <p>
     * 지정된 에이전트 서빙의 상세 정보를 조회합니다.
     * </p>
     * 
     * @param agentServingId 조회할 에이전트 서빙 ID
     * @return 에이전트 서빙 상세 정보
     * @throws BusinessException 에이전트 서빙 정보 조회 실패 시
     */
    public AgentServingInfo getAgentServingInfo(String agentServingId) {
        log.debug("에이전트 서빙 상세 정보 조회 요청 - agentServingId: {}", agentServingId);

        try {
            AgentServingInfo response = sktaiServingClient.getAgentServingInfo(agentServingId);
            log.info("에이전트 서빙 상세 정보 조회 성공 - agentServingId: {}", agentServingId);
            return response;
        } catch (BusinessException e) {
            log.error("에이전트 서빙 상세 정보 조회 실패 (BusinessException) - agentServingId: {}, message: {}",
                    agentServingId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("에이전트 서빙 상세 정보 조회 실패 (예상치 못한 오류) - agentServingId: {}", agentServingId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 서빙 상세 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 정책 목록 조회
     * 
     * <p>
     * 서빙 접근 정책 목록을 조회합니다.
     * </p>
     * 
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 옵션
     * @param search 검색어
     * @return 정책 목록
     * @throws BusinessException 정책 목록 조회 실패 시
     */
    public PolicyPayload getPolicies(Integer page, Integer size, String sort, String search) {
        log.debug("정책 목록 조회 요청 - page: {}, size: {}", page, size);

        try {
            PolicyPayload response = sktaiServingClient.getPolicies(page, size, sort, search);
            log.info("정책 목록 조회 성공 - page: {}, size: {}", page, size);
            return response;
        } catch (BusinessException e) {
            log.error("정책 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}",
                    page, size, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("정책 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "정책 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Shared Backend 버전 업그레이드
     * 
     * <p>
     * 시작된 모든 Shared Backend의 버전을 업그레이드합니다.
     * </p>
     * 
     * @return 버전 업그레이드 결과
     * @throws BusinessException 버전 업그레이드 실패 시
     */
    public SktaiOperationResponse versionUpSharedBackends() {
        log.debug("Shared Backend 버전 업그레이드 요청");

        try {
            SktaiOperationResponse response = sktaiServingClient.versionUpSharedBackends();
            log.info("Shared Backend 버전 업그레이드 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Shared Backend 버전 업그레이드 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Shared Backend 버전 업그레이드 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Shared Backend 버전 업그레이드에 실패했습니다.");
        }
    }
}
