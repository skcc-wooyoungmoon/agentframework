package com.skax.aiplatform.service.data;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.response.*;
import com.skax.aiplatform.dto.data.request.*;
import java.util.List;
public interface DataToolVectorDBService {
    /**
     * 데이터 VectorDB 목록 조회
     *
     * @param pageable 페이징 정보
     * @param sort 정렬 기준
     * @return 데이터 VectorDB 목록
     */
    PageResponse<DataToolVectorDBRes> getVectorDBList(Integer page, Integer size, 
                          String sort, String filter, String search);
    DataToolVectorDBDetailRes getVectorDBById(String vectorDbId);

    /**
     * 데이터 VectorDB 삭제
     *
     * @param vectorDbId 데이터 VectorDB ID
     */
    void deleteVectorDB(String vectorDbId);

    /**
     * 데이터 VectorDB 수정
     *
     * @param vectorDbId 데이터 VectorDB ID
     */
    void updateVectorDB(String vectorDbId, DataToolVectorDBUpdateReq request);

    /**
     * 데이터 VectorDB 생성
     *
     * @param vectorDbId 데이터 VectorDB ID
     */
    DataToolVectorDBCreateRes createVectorDB(DataToolVectorDBCreateReq request);


    /**
     * VectorDB 연결정보 조회
     */
    List<DataArgRes> getConnectionArgs();

    List<PolicyRequest> setVectorDBPolicy(String knowledgeId, String memberId, String projectName);
}
