package com.skax.aiplatform.service.common;

import com.skax.aiplatform.dto.common.request.SetPublicRequest;
import com.skax.aiplatform.dto.common.response.AssetProjectInfoRes;

public interface ProjectInfoService {
    
    /**
     * 공개 설정
     *
     * @param request 자산 공개 설정 요청 (type, id)
     */
    void setPublicFromPrivate(SetPublicRequest request);
    
    /**
     * UUID로 자산-프로젝트 매핑 정보 조회
     * 
     * @param uuid 자산 UUID
     * @return 자산-프로젝트 매핑 정보 (lst_prj_seq, userBy, dateAt)
     */
    AssetProjectInfoRes getAssetProjectInfoByUuid(String uuid);
}
