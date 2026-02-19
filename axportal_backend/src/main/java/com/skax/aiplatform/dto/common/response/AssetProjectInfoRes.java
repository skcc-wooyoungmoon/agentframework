package com.skax.aiplatform.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 자산-프로젝트 매핑 정보 응답 DTO
 * 
 * <p>GPO_ASSTPRJ_MAP_MAS 테이블에서 조회한 정보를 담는 DTO입니다.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetProjectInfoRes {
    
    /**
     * 최종 프로젝트명 (lstPrjSeq가 양수인 경우만 조회)
     */
    private String lstPrjNm;
    
    /**
     * 최초 프로젝트명
     */
    private String fstPrjNm;
    
    /**
     * 생성자 또는 수정자
     * (updated_by가 null이 아니면 updated_by, null이면 created_by)
     */
    private String userBy;
    
    /**
     * 사용자 직원명 (jkw_nm)
     */
    private String jkwNm;
    
    /**
     * 사용자 부서명 (dept_nm)
     */
    private String deptNm;
    
    /**
     * 퇴직 직원 여부 (retr_jkw_yn: 1=재직, 0=퇴직)
     */
    private Integer retrJkwYn;
    
    /**
     * 생성일시 또는 수정일시
     * (lst_updated_at이 null이 아니면 lst_updated_at, null이면 fst_created_at)
     */
    private String dateAt;
}

