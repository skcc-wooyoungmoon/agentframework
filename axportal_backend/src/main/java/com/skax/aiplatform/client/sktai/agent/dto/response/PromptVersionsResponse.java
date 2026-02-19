package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI Agent Inference Prompt 버전 목록 응답 DTO
 *
 * <p>특정 프롬프트의 모든 버전 목록을 담는 외부 API 응답 구조에 맞춘 DTO입니다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "SKTAI Agent Inference Prompt 버전 목록 응답",
        example = """
                   {
                    "timestamp": 1756150372872,
                    "code": 1,
                    "detail": "성공",
                    "traceId": null,
                    "data": [
                        {
                            "created_by": "f676500c-1866-462a-ba8e-e7f76412b1dc",
                            "version": 5,
                            "release": true,
                            "delete_flag": false,
                            "version_id": "76788be4-8bbf-490f-a324-fb0721b78af3",
                            "created_at": "2025-08-18T01:41:38.318862Z",
                            "uuid": "4e806085-b2f7-4f0f-9b3a-5e777c199b01"
                        },
                        {
                            "created_by": "f676500c-1866-462a-ba8e-e7f76412b1dc",
                            "version": 4,
                            "release": false,
                            "delete_flag": false,
                            "version_id": "1f17d085-7bb7-492e-a3ab-c791209c9fbf",
                            "created_at": "2025-08-18T01:41:17.960566Z",
                            "uuid": "4e806085-b2f7-4f0f-9b3a-5e777c199b01"
                        },
                        {
                            "created_by": "f676500c-1866-462a-ba8e-e7f76412b1dc",
                            "version": 3,
                            "release": false,
                            "delete_flag": false,
                            "version_id": "a25c09f4-381e-46ab-a806-9ab33771855e",
                            "created_at": "2025-08-18T00:58:50.981301Z",
                            "uuid": "4e806085-b2f7-4f0f-9b3a-5e777c199b01"
                        },
                        {
                            "created_by": "f676500c-1866-462a-ba8e-e7f76412b1dc",
                            "version": 2,
                            "release": false,
                            "delete_flag": false,
                            "version_id": "dcc95c31-6747-42e2-b943-241bd3310021",
                            "created_at": "2025-08-18T00:58:50.968072Z",
                            "uuid": "4e806085-b2f7-4f0f-9b3a-5e777c199b01"
                        },
                        {
                            "created_by": "f676500c-1866-462a-ba8e-e7f76412b1dc",
                            "version": 1,
                            "release": false,
                            "delete_flag": false,
                            "version_id": "519eecf1-c984-4362-bcb1-a2e00e160bb1",
                            "created_at": "2025-08-18T00:58:50.942802Z",
                            "uuid": "4e806085-b2f7-4f0f-9b3a-5e777c199b01"
                        }
                    ],
                    "payload": null
                   }
                """)
public class PromptVersionsResponse {

    @JsonProperty("timestamp")
    @Schema(description = "응답 타임스탬프(밀리초)", example = "1756150372872")
    private Long timestamp;

    @JsonProperty("code")
    @Schema(description = "응답 코드", example = "1")
    private Integer code;

    @JsonProperty("detail")
    @Schema(description = "상세 메시지", example = "성공")
    private String detail;

    @JsonProperty("traceId")
    @Schema(description = "트레이스 ID", example = "null")
    private String traceId;

    @JsonProperty("data")
    @Schema(description = "버전 정보 목록")
    private List<VersionData> data;

    @JsonProperty("payload")
    @Schema(description = "부가 페이로드")
    private Object payload;

    /**
     * 단일 버전 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "버전 정보")
    public static class VersionData {

        @JsonProperty("created_by")
        @Schema(description = "생성자 사용자 ID", example = "f676500c-1866-462a-ba8e-e7f76412b1dc")
        private String createdBy;

        @JsonProperty("version")
        @Schema(description = "버전 번호", example = "5")
        private Integer version;

        @JsonProperty("release")
        @Schema(description = "릴리즈(배포) 여부", example = "true")
        private Boolean release;

        @JsonProperty("delete_flag")
        @Schema(description = "삭제 여부", example = "false")
        private Boolean deleteFlag;

        @JsonProperty("version_id")
        @Schema(description = "버전 ID", example = "76788be4-8bbf-490f-a324-fb0721b78af3")
        private String versionId;

        @JsonProperty("created_at")
        @Schema(description = "생성 일시(UTC ISO-8601)", example = "2025-08-18T01:41:38.318862Z")
        private String createdAt;

        @JsonProperty("uuid")
        @Schema(description = "프롬프트 UUID", example = "4e806085-b2f7-4f0f-9b3a-5e777c199b01")
        private String uuid;

    }

}
