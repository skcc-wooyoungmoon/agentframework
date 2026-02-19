package com.skax.aiplatform.service.model;


import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.request.CreateModelCtlgReq;
import com.skax.aiplatform.dto.model.request.DeleteModelCtlgBulkReq;
import com.skax.aiplatform.dto.model.request.GetModelCtlgReq;
import com.skax.aiplatform.dto.model.request.GetUpdateModelCtlgReq;
import com.skax.aiplatform.dto.model.response.DeleteModelCtlgBulkRes;
import com.skax.aiplatform.dto.model.response.GetModelCtlgRes;
import com.skax.aiplatform.dto.model.response.GetModelPrvdRes;
import com.skax.aiplatform.dto.model.response.GetModelTagsRes;
import com.skax.aiplatform.dto.model.response.GetModelTypesRes;

public interface ModelCtlgService {

    /**
     * 모델 카탈로그 목록 조회
     * 
     * @author 김예리
     * @since 2025-08-20
     * @version 1.0.0
     * 
     * @param request 모델 카탈로그 요청
     * @return 모델 카탈로그 목록
     */
    public PageResponse<GetModelCtlgRes> getModelCtlg(GetModelCtlgReq request);

    /**
     * 모델 카탈로그 생성
     * 
     * @author 김예리
     * @since 2025-08-22
     * @version 1.0.0
     * 
     * @param modelCtlgReq 모델 카탈로그 요청
     * @return 모델 카탈로그 생성
     */
    public GetModelCtlgRes createModelCtlg(CreateModelCtlgReq request);

    /**
     * 모델 상세 조회
     * 
     * @author 김예리
     * @since 2025-08-20
     * @version 1.0.0
     * 
     * @param id
     * @return
     */
    public GetModelCtlgRes getModelCtlgById(String id);

    /**
     * 모델 상세 정보 수정
     * 
     * @author 김예리
     * @since 2025-08-22
     * @version 1.0.0
     * 
     * @param id 모델 ID
     * @param request 수정 요청 데이터
     * @return 수정된 모델 정보
     */
    public GetModelCtlgRes updateModelCtlgById(String id, GetUpdateModelCtlgReq request);

    /**
     * 모델 상세 정보 삭제
     * 
     * @author 김예리
     * @since 2025-08-26
     * @version 1.0.0
     * 
     * @param request 모델 카탈로그 삭제 요청
     * @return 삭제된 모델 정보
     */
    public DeleteModelCtlgBulkRes deleteModelCtlgBulk(DeleteModelCtlgBulkReq request);

    /**
     * 모델 Provider 목록 조회
     * 
     * @author 김예리
     * @since 2025-08-20
     * @version 1.0.0
     * 
     * @return 모델 Provider 목록
     */
    public PageResponse<GetModelPrvdRes> getModelProviders();

    /**
     * 모델 타입 목록 조회
     * 
     * @author 김예리
     * @since 2025-08-22
     * @version 1.0.0
     * 
     * @return 모델 타입 목록
     */
    public GetModelTypesRes getModelTypes();

    /**
     * 모델 태그 목록 조회
     * 
     * @author 김예리
     * @since 2025-08-22
     * @version 1.0.0
     * 
     * @return 모델 태그 목록
     */
    public GetModelTagsRes getModelTags();

    /**
     * 모델 Policy 설정
     *
     * @author 정태윤
     * @since 2025-12-29
     * @version 1.0.0
     * 
     * @param id 모델 ID
     * @param memberId 사용자 ID
     * @param projectName 프로젝트명
     */
    public void setModelPolicy(String id, String memberId, String projectName);

    /**
     * 모델 엔드포인트 Policy 설정
     *
     * @author 정태윤
     * @since 2025-12-31
     * @version 1.0.0
     * 
     * @param id 모델 ID
     * @param endpointId 모델 엔드포인트 ID
     * @param memberId 사용자 ID
     * @param projectName 프로젝트명
     */
    public void setModelEndpointPolicy(String id, String endpointId, String memberId, String projectName);
}
