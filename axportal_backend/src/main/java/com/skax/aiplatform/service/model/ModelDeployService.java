package com.skax.aiplatform.service.model;

import java.util.List;

import com.skax.aiplatform.client.lablup.api.dto.response.GetEndpointResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetSessionLogResponse;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.request.CreateBackendAiModelDeployReq;
import com.skax.aiplatform.dto.model.request.CreateModelDeployReq;
import com.skax.aiplatform.dto.model.request.DeleteModelDeployReq;
import com.skax.aiplatform.dto.model.request.GetInferencePerformanceReq;
import com.skax.aiplatform.dto.model.request.GetModelDeployReq;
import com.skax.aiplatform.dto.model.request.PutModelDeployReq;
import com.skax.aiplatform.dto.model.response.CreateBackendAiModelDeployRes;
import com.skax.aiplatform.dto.model.response.CreateModelDeployRes;
import com.skax.aiplatform.dto.model.response.GetInferencePerformanceRes;
import com.skax.aiplatform.dto.model.response.GetModelDeployRes;
import com.skax.aiplatform.dto.model.response.ModelDeployResourceInfo;

public interface ModelDeployService {
    /**
     * 모델 배포 목록 조회
     * 
     * @author 김예리
     * @since 2025-08-20
     * @version 1.0.0
     * 
     * @return 모델 배포 목록
     */
    public PageResponse<GetModelDeployRes> getModelDeploy(GetModelDeployReq request);

    /**
     * 모델 배포 상세 조회
     * 
     * @author 김예리
     * @since 2025-08-20
     * @version 1.0.0
     * 
     * @return 모델 배포 상세 정보
     */
    public GetModelDeployRes getModelDeployById(String id);

    /**
     * 모델 배포 상태 변경
     * 
     * @author 김예리
     * @since 2025-09-08
     * @version 1.0.0
     * 
     */
    public void changeModelDeployStatus(String servingType, String id, String status);

    /**
     * 모델 배포 삭제
     * 
     * @author 김예리
     * @since 2025-09-09
     * @version 1.0.0
     * 
     */
    public void deleteModelDeployBulk(List<DeleteModelDeployReq> deleteRequests);

    /**
     * 모델 배포 생성
     * 
     * @author AXPortal Team
     * @since 2025-01-27
     * @version 1.0.0
     * 
     * @param request 모델 배포 생성 요청
     * @return 생성된 모델 배포 정보
     */
    public CreateModelDeployRes createModelDeploy(CreateModelDeployReq request, Long projectId);

    /**
     * Backend.AI 연동 기반 모델 배포 생성
     * 
     * @author AXPortal Team
     * @since 2025-01-27
     * @version 1.0.0
     * 
     * @param request Backend.AI 모델 배포 생성 요청
     * @return 생성된 Backend.AI 모델 배포 정보
     */
    public CreateBackendAiModelDeployRes createBackendAiModelDeploy(CreateBackendAiModelDeployReq request, Long projectId);

    /**
     * 모델 배포 수정
     * 
     * @author AXPortal Team
     * @since 2025-09-19
     * @version 1.0.0
     * 
     * @param id      모델 배포 ID
     * @param request 모델 배포 수정 요청
     */
    public void updateModelDeploy(String id, PutModelDeployReq request);

    /**
     * 모델 배포 수정
     *
     * @author AXPortal Team
     * @since 2025-09-19
     * @version 1.0.0
     *
     * @param id      모델 배포 ID
     * @param request 모델 배포 수정 요청
     */
    public void updateBackendAiModelDeploy(String id, PutModelDeployReq request);

    /**
     * 모델 배포 시스템 로그 조회
     *
     * @param id 서빙(세션) ID
     * @return 세션 로그 응답
     */
    public GetSessionLogResponse getSystemLogById(String id);

    /**
     * 모델 배포 엔드포인트 정보 조회
     *
     * @param id 서빙 ID (endpoint_id)
     * @return 엔드포인트 상세 정보
     */
    public GetEndpointResponse getEndpointInfoById(String id);

    /**
     * 추론 성능 조회 (Time To First Token)
     *
     * @param request 추론 성능 조회 요청 (servingId, startDate, endDate)
     * @return 추론 성능 데이터 (TTFT 구간별 호출 수 분포)
     */
    public GetInferencePerformanceRes getInferencePerformance(
            GetInferencePerformanceReq request);

    /**
     * 모델 배포 자원 현황 조회
     *
     * @param servingId 서빙 ID
     * @return 모델 배포 자원 현황 정보 (CPU, Memory, GPU 사용량 및 할당량)
     */
    public ModelDeployResourceInfo getModelDeployResourceInfo(String servingId);

    /**
     * 모델 배포 Policy 설정
     *
     * @param servingId 서빙 ID
     * @param memberId 사용자 ID
     * @param projectName 프로젝트명
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    List<PolicyRequest> setModelDeployPolicy(String servingId, String memberId, String projectName);

    /**
     * 도커 이미지 URL 조회 (SYS_U_V 값으로 조회, DEL_YN = 0인 경우만)
     *
     * @param sysUV 시스템 유형값
     * @return 도커 이미지 URL 목록
     */
    List<com.skax.aiplatform.dto.model.response.GetDockerImgUrlRes> getDockerImgUrlBySysUV(String sysUV);
}
