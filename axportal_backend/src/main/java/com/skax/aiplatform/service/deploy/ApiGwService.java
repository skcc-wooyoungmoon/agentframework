package com.skax.aiplatform.service.deploy;

import java.util.List;

import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.dto.deploy.request.CreateApiReq;
import com.skax.aiplatform.dto.deploy.response.CreateApiRes;
import com.skax.aiplatform.dto.deploy.response.GetApiEndpointStatus;

public interface ApiGwService {

    /**
     * API 생성
     * @param request
     */
    public CreateApiRes createApiEndpoint(CreateApiReq request);

    /**
     * API 엔드포인트 조회
     * @param apiId
     */
    public GetApiEndpointStatus checkApiEndpoint(String apiId);

    /**
     * API 엔드포인트 등록 재요청
     * @param apiId
     */
    public void postRetryApiEndpoint(String apiId);

    /**
     * API 엔드포인트 통계
     * @param servingId
     */
    public List<ApiStatistics> getApiEndpointStatistics(String servingId, String startDate, String endDate);

    /**
     * API 엔드포인트 삭제
     * @param servingId
     */
    public void deleteApiEndpoint(String type, String servingId);
}
