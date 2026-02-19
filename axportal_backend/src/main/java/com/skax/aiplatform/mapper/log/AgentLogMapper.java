package com.skax.aiplatform.mapper.log;

import com.skax.aiplatform.client.sktai.history.dto.response.AgentHistoryResponse;
import com.skax.aiplatform.dto.log.response.AgentLogRes;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AgentLogMapper {
    
    /**
     * AgentHistoryRecord를 AgentLogRes로 변환
     */
    AgentLogRes from(AgentHistoryResponse.AgentHistoryRecord record);
}
