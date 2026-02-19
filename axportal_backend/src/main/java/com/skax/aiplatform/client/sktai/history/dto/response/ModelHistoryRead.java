package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 모델 사용 이력 조회 응답 DTO
 *
 * <p>
 * SKTAI History API에서 모델 사용 이력 목록 조회 시 반환되는 응답 데이터 구조입니다.
 * 모델 실행 기록, 성능 지표, 사용자 정보 등을 포함한 페이징된 이력 데이터를 제공합니다.
 * </p>
 *
 * <h3>응답 구조:</h3>
 * <ul>
 * <li><strong>data</strong>: 실제 모델 사용 이력 데이터 배열</li>
 * <li><strong>payload</strong>: 페이징 정보 및 메타데이터</li>
 * </ul>
 *
 * <h3>사용 사례:</h3>
 * <ul>
 * <li>모델 사용 현황 모니터링</li>
 * <li>성능 지표 분석 및 최적화</li>
 * <li>사용자별 모델 활용 패턴 분석</li>
 * <li>시스템 리소스 사용량 추적</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 1.0
 * @see Payload 페이징 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 조회 응답 정보", example = """
        {
          "data": [
            {
              "request_time": "2025-09-30T10:00:16.791681+09:00",
              "response_time": "2025-09-30T10:00:23.337407+09:00",
              "elapsed_time": 6.545726,
              "endpoint": "https://api.openai.com/v1",
              "model_name": "gpt-4o-mini",
              "model_identifier": "gpt-4o-mini",
              "model_id": "d6b092d7-7f4b-4052-aef5-5b135e84660f",
              "model_type": "language",
              "model_serving_id": "560c81b3-cec5-4e4b-8c53-4c46d05c919b",
              "model_serving_name": "gpt-4o-mini",
              "object_type": "chat",
              "api_key": "sk-287798228cf7e19d9a549f52ef9bd943",
              "model_key": "sk-proj-...",
              "input_json": "{\\"messages\\":[{\\"content\\":\\"You are an AI Assistant...\\",\\"role\\":\\"system\\"}],\\"model\\":\\"gpt-4o-mini\\"}",
              "output_json": "data: {\\"id\\":\\"chatcmpl-...\\",\\"choices\\":[...]}",
              "completion_tokens": 52,
              "prompt_tokens": 60,
              "total_tokens": 112,
              "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
              "user": "admin",
              "transaction_id": "c6ea62d4-8c07-4012-ba2d-8cd55a25e09d",
              "app_id": "aip-agent-playground",
              "agent_app_serving_id": "",
              "company": "",
              "department": "",
              "chat_id": ""
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "first_page_url": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=1&size=1",
              "from_": 1,
              "last_page": 152,
              "links": [
                {
                  "url": null,
                  "label": "&laquo; Previous",
                  "active": false,
                  "page": null
                },
                {
                  "url": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=1&size=1",
                  "label": "1",
                  "active": true,
                  "page": 1
                }
              ],
              "next_page_url": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=2&size=1",
              "items_per_page": 1,
              "prev_page_url": null,
              "to": 1,
              "total": 152
            }
          }
        }
        """)
public class ModelHistoryRead {

    /**
     * 모델 사용 이력 데이터 목록
     *
     * <p>
     * 각 항목은 모델 실행에 대한 상세 정보를 포함합니다.
     * 동적 구조로 되어 있어 다양한 모델 타입의 정보를 수용할 수 있습니다.
     * </p>
     *
     * <h4>일반적으로 포함되는 필드:</h4>
     * <ul>
     * <li><strong>request_id</strong>: 요청 고유 식별자</li>
     * <li><strong>model_id</strong>: 사용된 모델 식별자</li>
     * <li><strong>user</strong>: 요청한 사용자 정보</li>
     * <li><strong>request_time</strong>: 요청 시간</li>
     * <li><strong>response_time</strong>: 응답 시간 (밀리초)</li>
     * <li><strong>status</strong>: 처리 상태 (success/error)</li>
     * <li><strong>input_tokens</strong>: 입력 토큰 수</li>
     * <li><strong>output_tokens</strong>: 출력 토큰 수</li>
     * </ul>
     */
    @JsonProperty("data")
    @Schema(description = "모델 사용 이력 데이터 목록", example = """
            [
              {
                "request_time": "2025-09-30T10:00:16.791681+09:00",
                "response_time": "2025-09-30T10:00:23.337407+09:00",
                "elapsed_time": 6.545726,
                "endpoint": "https://api.openai.com/v1",
                "model_name": "gpt-4o-mini",
                "model_identifier": "gpt-4o-mini",
                "model_id": "d6b092d7-7f4b-4052-aef5-5b135e84660f",
                "model_type": "language",
                "model_serving_id": "560c81b3-cec5-4e4b-8c53-4c46d05c919b",
                "model_serving_name": "gpt-4o-mini",
                "object_type": "chat",
                "api_key": "sk-287798228cf7e19d9a549f52ef9bd943",
                "model_key": "sk-proj-...",
                "input_json": "{\\"messages\\":[{\\"content\\":\\"You are an AI Assistant...\\",\\"role\\":\\"system\\"}],\\"model\\":\\"gpt-4o-mini\\"}",
                "output_json": "data: {\\"id\\":\\"chatcmpl-...\\",\\"choices\\":[...]}",
                "completion_tokens": 52,
                "prompt_tokens": 60,
                "total_tokens": 112,
                "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
                "user": "admin",
                "transaction_id": "c6ea62d4-8c07-4012-ba2d-8cd55a25e09d",
                "app_id": "aip-agent-playground",
                "agent_app_serving_id": "",
                "company": "",
                "department": "",
                "chat_id": ""
              }
            ]
            """)
    private List<ModelHistoryRecord> data;

    /**
     * 페이징 정보 및 메타데이터
     *
     * <p>
     * 조회 결과의 페이징 정보와 추가 메타데이터를 포함합니다.
     * 대용량 이력 데이터의 효율적인 조회를 위한 페이징 관련 정보를 제공합니다.
     * </p>
     */
    @JsonProperty("payload")
    @Schema(description = "페이징 정보 및 메타데이터", implementation = Payload.class)
    private Payload payload;
}