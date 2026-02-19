package com.skax.aiplatform.service.data;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.request.DataStorDatasetSearchReq;
import com.skax.aiplatform.dto.data.request.DataStorDocumentSearchReq;
import com.skax.aiplatform.dto.data.request.DataStorTrainEvalSearchReq;
import com.skax.aiplatform.dto.data.response.DataStorDatasetRes;
import com.skax.aiplatform.dto.data.response.DataStorDocumentRes;
import com.skax.aiplatform.client.udp.dataset.dto.response.UdpEsDatasetAggregationResponse;
import com.skax.aiplatform.dto.data.response.DataStorTrainEvalRes;

/**
 * 데이터 저장소 서비스 인터페이스
 *
 * @author 장지원
 * @version 1.0.0
 * @since 2025-10-18
 */
public interface DataStorService {
    
    /**
     * 데이터셋 검색
     *
     * @param request 검색 요청 파라미터
     * @return 데이터셋 검색 결과 (페이지네이션 포함)
     */
    PageResponse<DataStorDatasetRes> getDatasets(DataStorDatasetSearchReq request);
    
    /**
     * 도큐먼트 검색
     *
     * @param request 도큐먼트 조회 요청 파라미터
     * @return 데이터셋 상세 조회 결과 (페이지네이션 포함)
     */
    PageResponse<DataStorDocumentRes> getDatasetsDocuments(DataStorDocumentSearchReq request);

    /**
     * 학습/평가데이터 검색
     *
     * @param request 학습/평가데이터 검색 요청 파라미터
     * @return 학습/평가데이터 검색 결과 (페이지네이션 포함)
     */
    PageResponse<DataStorTrainEvalRes> getTrainEvalData(DataStorTrainEvalSearchReq request);

    /**
     * 원천 시스템 목록 조회
     *
     * <p>UDP ES를 통해 데이터셋의 원천 시스템 목록을 조회합니다.</p>
     *
     * @return 원천 시스템 목록 (에러 시 빈 리스트 반환)
     */
    UdpEsDatasetAggregationResponse getOriginSystems();
}