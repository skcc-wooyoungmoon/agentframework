package com.skax.aiplatform.service.deploy;

import java.util.List;

import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.deploy.request.CreateApiKeyReq;
import com.skax.aiplatform.dto.deploy.request.GetApiKeyListReq;
import com.skax.aiplatform.dto.deploy.request.GetApiKeyStaticReq;
import com.skax.aiplatform.dto.deploy.request.UpdateApiKeyQuotaReq;
import com.skax.aiplatform.dto.deploy.response.GetApiKeyRes;

public interface ApiKeyService {

    /**
     * API Key 목록 조회
     * 
     * @param getApiKeyListRequest
     * @return
     */
    public PageResponse<GetApiKeyRes> getApiKeys(String type, GetApiKeyListReq request) throws Exception;

    /**
     * API Key 목록 조회 (관리자)
     * @deprecated 관리자는 프로젝트 단위로 확인함
     * @param getApiKeyListRequest
     * @return
     */
    // public PageResponse<GetApiKeyRes> getAdminApiKeys(GetApiKeyAdminListReq getApiKeyListRequest);

    /**
     * API Key 조회
     * 
     * @param id
     * @return
     */
    public GetApiKeyRes getApiKey(String id) throws Exception;
    /**
     * API Key 상세 조회
     * 
     * @param id
     * @return
     */
    public List<ApiStatistics> getApiKeyStatic(String id, GetApiKeyStaticReq request);
    /**
     * API Key 생성
     * 
     * @param request
     * @return
     */
    public GetApiKeyRes createApiKey(CreateApiKeyReq request) throws Exception;

    /**
     * API Key 할당량 수정
     * 
     * @param id
     * @param request
     * @return
     */
    public void updateApiKeyQuota(String id, UpdateApiKeyQuotaReq request);
    
    /**
     * API Key 사용 차단
     * 
     * @param id
     * @return
     */
    public void updateApiKeyExpire(String id);

    /**
     * API Key 차단 해제
     * 
     * @param id
     * @return
     */
    public void restoreApiKey(String id);

    /**
     * API Key 삭제
     * 
     * @param id
     * @return
     */
    public void deleteApiKey(String id);

    /**
     * API Key 복수 삭제
     * 
     * @param ids
     * @return
     */
    public void deleteApiKeyBulk(List<String> ids);
}
