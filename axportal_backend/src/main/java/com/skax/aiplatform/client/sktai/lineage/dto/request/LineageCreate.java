package com.skax.aiplatform.client.sktai.lineage.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;

/**
 * Lineage 생성 요청 DTO
 * 
 * <p>여러 Lineage 관계를 한 번에 생성하기 위한 요청 데이터 구조입니다.
 * lineages 배열을 통해 여러 객체 간의 관계를 일괄 생성할 수 있습니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>lineages</strong>: Lineage 관계 배열</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * LineageCreate request = LineageCreate.builder()
 *     .lineages(List.of(
 *         LineageItem.builder()
 *             .sourceKey("dataset-123")
 *             .sourceType(ObjectType.DATASET)
 *             .targetKey("model-456")
 *             .targetType(ObjectType.MODEL)
 *             .action(ActionType.USE)
 *             .build()
 *     ))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 * @see ActionType 액션 타입 정의
 * @see ObjectType 객체 타입 정의
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Lineage 생성 요청 정보",
    example = """
        {
          "lineages": [
            {
              "source_key": "a0f49edd-6766-4758-92a3-13c066648bc0",
              "source_type": "DATASET",
              "target_key": "b1f49edd-6766-4758-92a3-13c066648bc1",
              "target_type": "MODEL",
              "action": "USE"
            }
          ]
        }
        """
)
public class LineageCreate {
    
    /**
     * Lineage 관계 배열
     * 
     * <p>생성할 Lineage 관계들의 목록입니다.
     * 여러 관계를 한 번에 생성할 수 있습니다.</p>
     */
    @JsonProperty("lineages")
    @Schema(
        description = "Lineage 관계 배열",
        required = true
    )
    private List<LineageItem> lineages;
    
    /**
     * Lineage 관계 아이템
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Lineage 관계 아이템")
    public static class LineageItem {
        
        /**
         * 소스 객체 키
         */
        @JsonProperty("source_key")
        @Schema(
            description = "소스 객체의 고유 키",
            example = "a0f49edd-6766-4758-92a3-13c066648bc0",
            required = true
        )
        private String sourceKey;
        
        /**
         * 소스 객체 타입
         */
        @JsonProperty("source_type")
        @Schema(
            description = "소스 객체 타입",
            example = "DATASET",
            required = true
        )
        private ObjectType sourceType;
        
        /**
         * 타겟 객체 키
         */
        @JsonProperty("target_key")
        @Schema(
            description = "타겟 객체의 고유 키",
            example = "b1f49edd-6766-4758-92a3-13c066648bc1",
            required = true
        )
        private String targetKey;
        
        /**
         * 타겟 객체 타입
         */
        @JsonProperty("target_type")
        @Schema(
            description = "타겟 객체 타입",
            example = "MODEL",
            required = true
        )
        private ObjectType targetType;
        
        /**
         * 액션 타입
         */
        @JsonProperty("action")
        @Schema(
            description = "액션 타입 (USE: 사용, CREATE: 생성)",
            example = "USE",
            required = true
        )
        private ActionType action;
    }
}