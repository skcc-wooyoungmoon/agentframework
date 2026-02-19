package com.skax.aiplatform.service.log;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.log.response.AgentLogRes;

public interface AgentLogService {
    /**
     * Agent Log 목록 조회
     *
     * @param pageable 페이징 정보
     * @param sort 정렬 기준
     * @return 데이터셋 목록
     */
    PageResponse<AgentLogRes> getAgentLogList(String fromDate, String toDate, 
                                                    Integer page, Integer size,
                                                    String fields, Boolean errorLogs,
                                                    String additionalHistoryOption,
                                                    String filter, String search, String sort);
}