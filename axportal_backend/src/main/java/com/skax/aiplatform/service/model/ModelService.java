package com.skax.aiplatform.service.model;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.response.ModelDetailRes;
import com.skax.aiplatform.dto.model.response.ModelRes;
import org.springframework.data.domain.Pageable;

/**
 * 모델 관리 서비스 인터페이스
 * 
 * <p>모델의 CRUD 작업과 관련된 비즈니스 로직을 정의합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-01-16
 * @version 1.0
 */
public interface ModelService {
    
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
    PageResponse<ModelRes> getModels(Pageable pageable, String sort, String filter, String search, String ids);
    
    /**
     * 모델 상세 조회
     * 
     * @param modelId 모델 ID
     * @return 모델 상세 정보
     */
    ModelDetailRes getModelById(String modelId);
    
    /**
     * 모델 타입 목록 조회
     * 
     * @return 모델 타입 목록
     */
    Object getModelTypes();
    
    /**
     * 모델 태그 목록 조회
     * 
     * @return 모델 태그 목록
     */
    Object getModelTags();
}
