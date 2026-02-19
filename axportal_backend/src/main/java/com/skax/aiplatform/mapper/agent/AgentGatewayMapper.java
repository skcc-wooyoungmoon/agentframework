package com.skax.aiplatform.mapper.agent;

import com.skax.aiplatform.client.sktai.agentgateway.dto.request.StreamRequest;
import com.skax.aiplatform.client.sktai.agentgateway.dto.response.StreamResponse;
import com.skax.aiplatform.dto.agent.request.StreamReq;
import com.skax.aiplatform.dto.agent.response.StreamRes;
import org.springframework.stereotype.Component;

@Component
public class AgentGatewayMapper {
    
    /**
     * StreamReq를 StreamRequest로 변환
     * 
     * @param streamReq 내부 DTO
     * @return SKTAI 클라이언트 DTO
     */
    public StreamRequest from(StreamReq streamReq) {
        if (streamReq == null) {
            return null;
        }
        
        return StreamRequest.builder()
                .input(streamReq.getInput())
                .config(streamReq.getConfig())
                .kwargs(streamReq.getKwargs())
                .build();
    }
    
    /**
     * StreamResponse를 StreamRes로 변환
     * 
     * @param streamResponse SKTAI 클라이언트 DTO
     * @return 내부 DTO
     */
    public StreamRes from(StreamResponse streamResponse) {
        if (streamResponse == null) {
            return null;
        }
        
        return StreamRes.builder()
                .chunk(streamResponse.getChunk())
                .isFinal(streamResponse.getIsFinal())
                .metadata(streamResponse.getMetadata())
                .build();
    }
}
