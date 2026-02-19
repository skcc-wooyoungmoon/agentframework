package com.skax.aiplatform.dto.log.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 모델 사용 이력 조회 응답 DTO (카멜케이스)
 * 
 * <p>
 * 모델 사용 이력 목록 조회 시 반환되는 응답 데이터 구조입니다.
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
 * @author System
 * @since 2025-01-27
 * @version 1.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 조회 응답 정보", example = """
        {
          "data": [
            {
              "requestTime": "2025-09-30T10:00:16.791681+09:00",
              "responseTime": "2025-09-30T10:00:23.337407+09:00",
              "elapsedTime": 6.545726,
              "endpoint": "https://api.openai.com/v1",
              "modelName": "gpt-4o-mini",
              "modelIdentifier": "gpt-4o-mini",
              "modelId": "d6b092d7-7f4b-4052-aef5-5b135e84660f",
              "modelType": "language",
              "modelServingId": "560c81b3-cec5-4e4b-8c53-4c46d05c919b",
              "modelServingName": "gpt-4o-mini",
              "objectType": "chat",
              "apiKey": "sk-287798228cf7e19d9a549f52ef9bd943",
              "modelKey": "sk-proj-...",
              "inputJson": "{\\"messages\\":[{\\"content\\":\\"You are an AI Assistant...\\",\\"role\\":\\"system\\"}],\\"model\\":\\"gpt-4o-mini\\"}",
              "outputJson": "data: {\\"id\\":\\"chatcmpl-...\\",\\"choices\\":[...]}",
              "completionTokens": 52,
              "promptTokens": 60,
              "totalTokens": 112,
              "projectId": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
              "user": "admin",
              "transactionId": "c6ea62d4-8c07-4012-ba2d-8cd55a25e09d",
              "appId": "aip-agent-playground",
              "agentAppServingId": "",
              "company": "",
              "department": "",
              "chatId": ""
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "firstPageUrl": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=1&size=1",
              "from": 1,
              "lastPage": 152,
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
              "nextPageUrl": "/history/model/list?from_date=2025-08-31&to_date=2025-09-30&page=2&size=1",
              "itemsPerPage": 1,
              "prevPageUrl": null,
              "to": 1,
              "total": 152
            }
          }
        }
        """)
public class ModelHistoryRes {

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
     * <li><strong>requestTime</strong>: 요청 시간</li>
     * <li><strong>responseTime</strong>: 응답 시간</li>
     * <li><strong>modelId</strong>: 사용된 모델 식별자</li>
     * <li><strong>user</strong>: 요청한 사용자 정보</li>
     * <li><strong>elapsedTime</strong>: 경과 시간 (초)</li>
     * <li><strong>completionTokens</strong>: 완성 토큰 수</li>
     * <li><strong>promptTokens</strong>: 프롬프트 토큰 수</li>
     * <li><strong>totalTokens</strong>: 총 토큰 수</li>
     * </ul>
     */
    @Schema(description = "모델 사용 이력 데이터 목록", example = """
            [
              {
                "requestTime": "2025-09-30T10:00:16.791681+09:00",
                "responseTime": "2025-09-30T10:00:23.337407+09:00",
                "elapsedTime": 6.545726,
                "endpoint": "https://api.openai.com/v1",
                "modelName": "gpt-4o-mini",
                "modelIdentifier": "gpt-4o-mini",
                "modelId": "d6b092d7-7f4b-4052-aef5-5b135e84660f",
                "modelType": "language",
                "modelServingId": "560c81b3-cec5-4e4b-8c53-4c46d05c919b",
                "modelServingName": "gpt-4o-mini",
                "objectType": "chat",
                "apiKey": "sk-287798228cf7e19d9a549f52ef9bd943",
                "modelKey": "sk-proj-...",
                "inputJson": "{\\"messages\\":[{\\"content\\":\\"You are an AI Assistant...\\",\\"role\\":\\"system\\"}],\\"model\\":\\"gpt-4o-mini\\"}",
                "outputJson": "data: {\\"id\\":\\"chatcmpl-...\\",\\"choices\\":[...]}",
                "completionTokens": 52,
                "promptTokens": 60,
                "totalTokens": 112,
                "projectId": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
                "user": "admin",
                "transactionId": "c6ea62d4-8c07-4012-ba2d-8cd55a25e09d",
                "appId": "aip-agent-playground",
                "agentAppServingId": "",
                "company": "",
                "department": "",
                "chatId": ""
              }
            ]
            """)
    private List<ModelHistoryRecordRes> data;

    /**
     * 페이징 정보 및 메타데이터
     *
     * <p>
     * 조회 결과의 페이징 정보와 추가 메타데이터를 포함합니다.
     * 대용량 이력 데이터의 효율적인 조회를 위한 페이징 관련 정보를 제공합니다.
     * </p>
     */
    @Schema(description = "페이징 정보 및 메타데이터", implementation = ModelHistoryPayloadRes.class)
    private ModelHistoryPayloadRes payload;
}
