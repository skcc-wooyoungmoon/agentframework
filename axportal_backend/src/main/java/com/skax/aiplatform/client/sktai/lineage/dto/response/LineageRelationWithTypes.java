package com.skax.aiplatform.client.sktai.lineage.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;

/**
 * Lineage 관계 정보 응답 DTO (타입 포함)
 * 
 * <p>Lineage 탐색 결과에서 반환되는 각 관계 정보를 나타내는 DTO입니다.
 * 소스와 타겟 객체의 키, 타입, 액션, 깊이 정보를 포함합니다.</p>
 * 
 * <h3>주요 정보:</h3>
 * <ul>
 *   <li><strong>source_key</strong>: 소스 객체 키</li>
 *   <li><strong>target_key</strong>: 타겟 객체 키</li>
 *   <li><strong>action</strong>: 액션 타입 (USE, CREATE)</li>
 *   <li><strong>depth</strong>: 탐색 깊이</li>
 *   <li><strong>source_type</strong>: 소스 객체 타입</li>
 *   <li><strong>target_type</strong>: 타겟 객체 타입</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 * @see LineageObjectCreate Lineage 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Lineage 관계 정보 (타입 포함)",
    example = """
        {
          "source_key": "a0f49edd-6766-4758-92a3-13c066648bc0",
          "target_key": "b1f49edd-6766-4758-92a3-13c066648bc1",
          "action": "USE",
          "depth": 1,
          "source_type": "KNOWLEDGE",
          "target_type": "VECTOR_DB"
        }
        """
)
public class LineageRelationWithTypes {
    
    /**
     * 소스 객체 키
     * 
     * <p>Lineage 관계에서 출발점이 되는 객체의 고유 키입니다.</p>
     */
    @JsonProperty("source_key")
    @Schema(
        description = "소스 객체의 고유 키",
        example = "a0f49edd-6766-4758-92a3-13c066648bc0",
        required = true
    )
    private String sourceKey;
    
    /**
     * 타겟 객체 키
     * 
     * <p>Lineage 관계에서 도착점이 되는 객체의 고유 키입니다.</p>
     */
    @JsonProperty("target_key")
    @Schema(
        description = "타겟 객체의 고유 키",
        example = "b1f49edd-6766-4758-92a3-13c066648bc1",
        required = true
    )
    private String targetKey;
    
    /**
     * 액션 타입
     * 
     * <p>소스에서 타겟으로의 관계 액션을 나타냅니다.</p>
     */
    @JsonProperty("action")
    @Schema(
        description = "액션 타입 (USE: 사용, CREATE: 생성)",
        example = "USE",
        required = true
    )
    private ActionType action;
    
    /**
     * 탐색 깊이
     * 
     * <p>시작 지점에서부터의 탐색 깊이를 나타냅니다.</p>
     */
    @JsonProperty("depth")
    @Schema(
        description = "탐색 깊이 (0부터 시작)",
        example = "1",
        required = true
    )
    private Integer depth;
    
    /**
     * 소스 객체 타입
     * 
     * <p>소스 객체의 타입을 나타냅니다.</p>
     */
    @JsonProperty("source_type")
    @Schema(
        description = "소스 객체 타입",
        example = "KNOWLEDGE",
        required = true
    )
    private ObjectType sourceType;
    
    /**
     * 타겟 객체 타입
     * 
     * <p>타겟 객체의 타입을 나타냅니다.</p>
     */
    @JsonProperty("target_type")
    @Schema(
        description = "타겟 객체 타입",
        example = "VECTOR_DB",
        required = true
    )
    private ObjectType targetType;
}