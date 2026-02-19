package com.skax.aiplatform.service.model;


import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.common.ModelGardenInfo;
import com.skax.aiplatform.dto.model.request.CreateModelGardenReq;
import com.skax.aiplatform.dto.model.request.FileImportCompleteReq;
import com.skax.aiplatform.dto.model.request.GetAvailableModelReq;
import com.skax.aiplatform.dto.model.request.GetModelGardenReq;
import com.skax.aiplatform.dto.model.request.PostInProcessStatusReq;
import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.dto.model.response.GetAvailableModelRes;
import com.skax.aiplatform.dto.model.response.GetVaccineCheckResultRes;
import com.skax.aiplatform.entity.model.GpoUseGnynModelMas;

/**
 * 모델 가든 서비스 인터페이스
 * 
 * <p>모델의 CRUD 작업과 관련된 비즈니스 로직을 정의합니다.</p>
 *
 * @author 김예리
 * @since 2025-10-02
 * @version 1.0
 */
public interface ModelGardenService {
    public enum UPDATE_FIND_TYPE {
        ID,
        NAME,
        MODEL_CTLG_ID;
    }
    
    /**
     * 모델 목록 조회
     * 
     * @param pageable 페이지 정보
     * @param sort     정렬 기준
     * @param filter   필터 조건
     * @param search   검색어
     * @param ids      모델 ID 목록
     * @return 페이징된 모델 목록
     */
    PageResponse<ModelGardenInfo> getModelGardens(GetModelGardenReq modelCtlgReq);
    
    /**
     * 모델 상세 조회
     * 
     * @param id 모델 ID
     * @return 모델 상세 정보
     */
    ModelGardenInfo getModelGardenById(String id);
    
    /**
     * 모델 등록
     * 
     * @param request 모델 생성 요청
     * @return 생성된 모델 정보
     */
    ModelGardenInfo createModelGarden(CreateModelGardenReq request);
    
    /**
     * 모델 수정
     * 
     * @param id 모델 ID
     * @param request 모델 수정 요청
     * @return 수정된 모델 정보
     */
    ModelGardenInfo updateModelGarden(UPDATE_FIND_TYPE type, String id, UpdateModelGardenReq request);
    // 실제 엔티티 업데이트
    ModelGardenInfo updateModelGarden(GpoUseGnynModelMas existingGardenInfo, UpdateModelGardenReq request);
    
        
    /**
     * 모델 가든 삭제
     * 
     * @param id 모델 ID
     */
    void deleteModelGarden(String id);

    /**
     * 모델 파일 반입 완료 처리
     * 
     * @param id 모델 ID
     * @return 업데이트된 모델 정보
     */
    ModelGardenInfo completeModelImport(PostInProcessStatusReq request);
    
    /**
     * 모델 백신검사 완료 처리
     * 
     * @param id 모델 ID
     * @return 업데이트된 모델 정보
     */
    ModelGardenInfo completeVaccineScan(PostInProcessStatusReq request);
    
    /**
     * 모델 내부망반입 완료 처리
     * (form-urlencoded)
     * 
     * @param id 모델 ID
     * @return 업데이트된 모델 정보
     */
    ModelGardenInfo completeInternalNetworkImport(FileImportCompleteReq request);
    
    /**
     * 모델 취약점점검 완료 처리
     * 
     * @param id 모델 ID
     * @return 업데이트된 모델 정보
     */
    ModelGardenInfo completeVulnerabilityCheck(PostInProcessStatusReq request);
    
    /**
     * 모델 반입 절차 실패 처리
     * 
     * @param id 모델 ID
     * @return 업데이트된 모델 정보
     */
    // ModelGardenInfo handleImportFailure(PostInProcessStatusReq request);

    /**
     * 모델 사용 가능 모델 조회
     * 
     * @param param 모델 파라미터
     * @return 모델 사용 가능 모델 목록
     */
    GetAvailableModelRes getAvailableModel(GetAvailableModelReq request);

    /**
     * 모델 가든 백신검사 결과 조회
     * 
     * @param id 모델 가든 ID
     * @return 백신검사 결과 정보
     */
    GetVaccineCheckResultRes getModelGardenVaccineCheckResult(String id);

}