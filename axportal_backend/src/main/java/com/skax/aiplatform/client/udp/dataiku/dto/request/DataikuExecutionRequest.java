package com.skax.aiplatform.client.udp.dataiku.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Dataiku 실행 요청 DTO (-d {} 본문을 매핑)
 */
@NoArgsConstructor
@Schema(description = "Dataiku 실행 요청 JSON 전체를 그대로 담는 래퍼")
public class DataikuExecutionRequest {

    private Map<String, Object> body = new HashMap<>();

    @JsonCreator
    public DataikuExecutionRequest(Map<String, Object> body) {
        this.body = (body == null) ? new HashMap<>() : new HashMap<>(body);
    }

    public void setBody(Map<String, Object> body) {
        this.body = (body == null) ? new HashMap<>() : new HashMap<>(body);
    }

    @JsonValue
    public Map<String, Object> getBody() {
        return body;
    }
}