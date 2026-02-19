package com.skax.aiplatform.client.lablup.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * JSON 문자열을 Map으로 파싱하는 Deserializer
 * 
 * <p>
 * API 응답에서 JSON 문자열로 받은 데이터를 Map으로 변환합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 */
@Slf4j
public class JsonStringDeserializer extends JsonDeserializer<Map<String, Object>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            // 파라미터 검증
            if (parser == null) {
                log.error(">>> JSON Deserialize 실패 - JsonParser가 null입니다.");
                throw new IllegalArgumentException("JsonParser는 필수입니다.");
            }
            
            if (context == null) {
                log.error(">>> JSON Deserialize 실패 - DeserializationContext가 null입니다.");
                throw new IllegalArgumentException("DeserializationContext는 필수입니다.");
            }
            
            String jsonString = parser.getText();

            if (jsonString == null || jsonString.trim().isEmpty()) {
                log.debug("JSON 문자열이 null 또는 비어있음 - null 반환");
                return null;
            }

            try {
                Map<String, Object> result = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
                });
                log.debug("JSON 파싱 성공 - keys: {}", result != null ? result.keySet() : "null");
                return result;
                
            } catch (com.fasterxml.jackson.core.JsonParseException e) {
                log.warn(">>> JSON 파싱 실패 - 잘못된 JSON 형식: jsonString={}, error={}", jsonString, e.getMessage());
                return null;
            } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
                log.warn(">>> JSON 파싱 실패 - 매핑 오류: jsonString={}, error={}", jsonString, e.getMessage());
                return null;
            } catch (IOException e) {
                log.warn(">>> JSON 파싱 실패 - I/O 오류: jsonString={}, error={}", jsonString, e.getMessage());
                return null;
            } catch (Exception e) {
                log.warn(">>> JSON 파싱 실패 - 예상치 못한 오류: jsonString={}, error={}", jsonString, e.getMessage(), e);
                return null;
            }
            
        } catch (IllegalArgumentException e) {
            log.error(">>> JSON Deserialize 실패 - 잘못된 파라미터: error={}", e.getMessage(), e);
            throw new IOException("JSON Deserialize 실패: 잘못된 파라미터입니다.", e);
        } catch (NullPointerException e) {
            log.error(">>> JSON Deserialize 실패 - 필수 데이터 null: error={}", e.getMessage(), e);
            throw new IOException("JSON Deserialize 실패: 필수 데이터를 찾을 수 없습니다.", e);
        } catch (IOException e) {
            // IOException은 메서드 시그니처에 선언되어 있으므로 그대로 전파
            log.error(">>> JSON Deserialize 실패 - I/O 오류: error={}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(">>> JSON Deserialize 실패 - 예상치 못한 오류: error={}", e.getMessage(), e);
            throw new IOException("JSON Deserialize 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
