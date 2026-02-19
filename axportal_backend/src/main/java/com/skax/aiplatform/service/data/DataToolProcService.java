package com.skax.aiplatform.service.data;

import org.springframework.data.domain.Pageable;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.response.DataToolProcDetailRes;
import com.skax.aiplatform.dto.data.response.DataToolProcRes;

public interface DataToolProcService {
    /**
     * 데이터셋 목록 조회
     *
     * @param pageable 페이징 정보
     * @param sort 정렬 기준
     * @return 데이터셋 목록
     */
    PageResponse<DataToolProcRes> getProcList(Pageable pageable, String sort, String filter, String search);
    DataToolProcDetailRes getProcById(String processorId);
}
