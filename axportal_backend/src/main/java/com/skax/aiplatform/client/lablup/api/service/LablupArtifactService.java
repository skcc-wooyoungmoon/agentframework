package com.skax.aiplatform.client.lablup.api.service;

import com.skax.aiplatform.client.lablup.api.LablupArtifactClient;
import com.skax.aiplatform.client.lablup.api.dto.request.*;
import com.skax.aiplatform.client.lablup.api.dto.response.*;
import com.skax.aiplatform.client.lablup.common.dto.LablupResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Lablup 아티팩트 관리 서비스
 * 
 * <p>
 * Lablup 아티팩트 API와의 통신을 담당하는 서비스 계층입니다.
 * 비즈니스 로직과 예외 처리를 담당하며, 외부 API 호출을 래핑합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>아티팩트 스캔</strong>: 벌크, 단일, 배치 스캔 기능</li>
 * <li><strong>메타데이터 관리</strong>: 아티팩트 정보 조회 및 관리</li>
 * <li><strong>가져오기/내보내기</strong>: 외부 저장소와의 연동</li>
 * <li><strong>검색 및 정리</strong>: 아티팩트 검색 및 정리 기능</li>
 * <li><strong>파일 관리</strong>: 업로드/다운로드 기능</li>
 * <li><strong>작업 모니터링</strong>: 비동기 작업 상태 추적</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LablupArtifactService {

    private final LablupArtifactClient lablupArtifactClient;

    /**
     * 아티팩트 스캔
     * 
     * @param request 스캔 요청 정보
     * @return 스캔 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ScanArtifactResponse scanArtifact(ScanArtifactRequest request) {
        try {
            log.info("🔴 Lablup 아티팩트 스캔 요청: {}", request);
            ScanArtifactResponse response = lablupArtifactClient.scanArtifact(request);
            log.info("🔴 Lablup 아티팩트 스캔 response: {}", response);
            return response;
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 스캔 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 스캔 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 스캔에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 단일 아티팩트 모델 스캔
     * 
     * @param artifactId 아티팩트 ID
     * @param request    스캔 요청 정보
     * @return 스캔 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ScanSingleArtifactModelResponse scanSingleArtifactModel(String artifactId,
            ScanSingleArtifactModelRequest request) {
        try {
            log.info("🔴 Lablup 단일 아티팩트 모델 스캔 요청: artifactId={}", artifactId);
            LablupResponse<ScanSingleArtifactModelResponse> response = lablupArtifactClient
                    .scanSingleArtifactModel(artifactId, request);
            log.info("🔴 Lablup 단일 아티팩트 모델 스캔 성공: scanId={}", response.getData().getScanId());
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 단일 아티팩트 모델 스캔 실패 - artifactId: {}, BusinessException: {}", artifactId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 단일 아티팩트 모델 스캔 실패 - artifactId: {}, 예상치 못한 오류", artifactId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "단일 아티팩트 모델 스캔에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 배치 아티팩트 모델 스캔
     * 
     * @param request 배치 스캔 요청 정보
     * @return 스캔 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public BatchScanArtifactModelsResponse batchScanArtifactModels(BatchScanArtifactModelsRequest request) {
        try {
            log.info("🔴 Lablup 배치 아티팩트 모델 스캔 요청");
            LablupResponse<BatchScanArtifactModelsResponse> response = lablupArtifactClient
                    .batchScanArtifactModels(request);
            log.info("🔴 Lablup 배치 아티팩트 모델 스캔 성공");
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 배치 아티팩트 모델 스캔 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 배치 아티팩트 모델 스캔 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "배치 아티팩트 모델 스캔에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 아티팩트 검색
     * 
     * @param request 검색 요청 정보
     * @return 검색 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public SearchArtifactsResponse searchArtifacts(SearchArtifactsRequest request) {
        try {
            log.info("🔴 Lablup 아티팩트 검색 요청");
            LablupResponse<SearchArtifactsResponse> response = lablupArtifactClient.searchArtifacts(request);
            log.info("🔴 Lablup 아티팩트 검색 성공: totalCount={}", response.getData().getTotalCount());
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 검색 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 검색 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 검색에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 아티팩트 가져오기
     * 
     * @param request 가져오기 요청 정보
     * @return 가져오기 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ImportArtifactsResponse importArtifacts(ImportArtifactsRequest request) {
        try {
            log.info("🔴 Lablup 아티팩트 가져오기 요청");
            ImportArtifactsResponse response = lablupArtifactClient.importArtifacts(request);
            
            // 응답 데이터 null 체크
            if (response == null) {
                log.error("🔴 Lablup 아티팩트 가져오기 실패 - 응답 데이터가 null입니다.");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Lablup API에서 빈 응답을 받았습니다.");
            }
            
            // tasks 배열 null 체크 및 빈 배열 체크
            if (response.getTasks() == null || response.getTasks().length == 0) {
                log.error("🔴 Lablup 아티팩트 가져오기 실패 - tasks 배열이 비어있습니다.");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "가져올 아티팩트가 없습니다.");
            }
            
            log.info("🔴 Lablup 아티팩트 가져오기 성공: taskId={}", response.getTasks()[0].getTaskId());
            return response;
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 가져오기 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 가져오기 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 가져오기에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 아티팩트 위임 가져오기
     * 
     * @param request 위임 가져오기 요청 정보
     * @return 가져오기 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ImportArtifactsResponse importArtifactsDelegation(ImportArtifactsDelegationRequest request) {
        try {
            log.info("🔴 Lablup 아티팩트 위임 가져오기 요청");
            ImportArtifactsResponse response = lablupArtifactClient.importArtifactsDelegation(request);
            
            // 응답 데이터 null 체크
            if (response == null) {
                log.error("🔴 Lablup 아티팩트 위임 가져오기 실패 - 응답 데이터가 null입니다.");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Lablup API에서 빈 응답을 받았습니다.");
            }
            
            // tasks 배열 null 체크 및 빈 배열 체크
            if (response.getTasks() == null || response.getTasks().length == 0) {
                log.error("🔴 Lablup 아티팩트 위임 가져오기 실패 - tasks 배열이 비어있습니다.");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "가져올 아티팩트가 없습니다.");
            }
            
            log.info("🔴 Lablup 아티팩트 위임 가져오기 성공: taskId={}", response.getTasks()[0].getTaskId());
            return response;
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 위임 가져오기 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 위임 가져오기 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 위임 가져오기에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 아티팩트 정리
     * 
     * @param request 정리 요청 정보
     * @return 정리 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public CleanupArtifactsResponse cleanupArtifacts(CleanupArtifactsRequest request) {
        try {
            log.info("🔴 Lablup 아티팩트 정리 요청");
            LablupResponse<CleanupArtifactsResponse> response = lablupArtifactClient.cleanupArtifacts(request);
            log.info("🔴 Lablup 아티팩트 정리 성공: jobId={}", response.getData().getJobId());
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 정리 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 정리 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 정리에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 아티팩트 가져오기 취소
     * 
     * @param request 취소 요청 정보
     * @return 취소 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public CancelImportArtifactResponse cancelImportArtifact(CancelImportArtifactRequest request) {
        try {
            log.info("🔴 Lablup 아티팩트 가져오기 취소 요청: artifactRevisionId={}", request.getArtifactRevisionId());
            CancelImportArtifactResponse response = lablupArtifactClient.cancelImportArtifact(request);
            log.info("🔴 Lablup 아티팩트 가져오기 취소 성공: artifactRevisionId={}", response.getArtifactRevision().getId());
            return response;
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 가져오기 취소 실패 - artifactRevisionId: {}, BusinessException: {}", request.getArtifactRevisionId(),
                    e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 가져오기 취소 실패 - artifactRevisionId: {}, 예상치 못한 오류", request.getArtifactRevisionId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 가져오기 취소에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 아티팩트 업데이트
     * 
     * @param artifactId 아티팩트 ID
     * @param request    업데이트 요청 정보
     * @return 업데이트 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public UpdateArtifactResponse updateArtifact(String artifactId, UpdateArtifactRequest request) {
        try {
            log.info("🔴 Lablup 아티팩트 업데이트 요청: artifactId={}", artifactId);
            LablupResponse<UpdateArtifactResponse> response = lablupArtifactClient.updateArtifact(artifactId, request);
            log.info("🔴 Lablup 아티팩트 업데이트 성공: artifactId={}", response.getData().getArtifactId());
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 업데이트 실패 - artifactId: {}, BusinessException: {}", artifactId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 업데이트 실패 - artifactId: {}, 예상치 못한 오류", artifactId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 아티팩트 수정본 README 조회
     * 
     * @param artifactId 아티팩트 ID
     * @return README 내용
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public GetArtifactRevisionReadmeResponse getArtifactRevisionReadme(String artifactId) {
        try {
            log.info("🔴 Lablup 아티팩트 수정본 README 조회 요청: artifactId={}", artifactId);
            LablupResponse<GetArtifactRevisionReadmeResponse> response = lablupArtifactClient
                    .getArtifactRevisionReadme(artifactId);
            log.info("🔴 Lablup 아티팩트 수정본 README 조회 성공: exists={}", response.getData().isExists());
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 아티팩트 수정본 README 조회 실패 - artifactId: {}, BusinessException: {}", artifactId,
                    e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 아티팩트 수정본 README 조회 실패 - artifactId: {}, 예상치 못한 오류", artifactId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "아티팩트 수정본 README 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사전 서명된 다운로드 URL 조회
     * 
     * @param request 다운로드 URL 요청 정보
     * @return 사전 서명된 다운로드 URL
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public GetPresignedDownloadUrlResponse getPresignedDownloadUrl(GetPresignedDownloadUrlRequest request) {
        try {
            log.info("🔴 Lablup 사전 서명된 다운로드 URL 조회 요청: artifactId={}", request.getArtifactId());
            LablupResponse<GetPresignedDownloadUrlResponse> response = lablupArtifactClient
                    .getPresignedDownloadUrl(request);
            log.info("🔴 Lablup 사전 서명된 다운로드 URL 조회 성공: compressed={}", response.getData().isCompressed());
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 사전 서명된 다운로드 URL 조회 실패 - artifactId: {}, BusinessException: {}",
                    request.getArtifactId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 사전 서명된 다운로드 URL 조회 실패 - artifactId: {}, 예상치 못한 오류", request.getArtifactId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "사전 서명된 다운로드 URL 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사전 서명된 업로드 URL 조회
     * 
     * @param request 업로드 URL 요청 정보
     * @return 사전 서명된 업로드 URL
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public GetPresignedUploadUrlResponse getPresignedUploadUrl(GetPresignedUploadUrlRequest request) {
        try {
            log.info("🔴 Lablup 사전 서명된 업로드 URL 조회 요청: fileName={}", request.getFileName());
            LablupResponse<GetPresignedUploadUrlResponse> response = lablupArtifactClient
                    .getPresignedUploadUrl(request);
            log.info("🔴 Lablup 사전 서명된 업로드 URL 조회 성공: multipart={}", request.isMultipart());
            return response.getData();
        } catch (BusinessException e) {
            log.error("🔴 Lablup 사전 서명된 업로드 URL 조회 실패 - fileName: {}, BusinessException: {}", request.getFileName(),
                    e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 사전 서명된 업로드 URL 조회 실패 - fileName: {}, 예상치 못한 오류", request.getFileName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "사전 서명된 업로드 URL 조회에 실패했습니다: " + e.getMessage());
        }
    }
}