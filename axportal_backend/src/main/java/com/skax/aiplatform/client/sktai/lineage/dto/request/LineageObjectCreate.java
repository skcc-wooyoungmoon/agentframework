package com.skax.aiplatform.client.sktai.lineage.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;

/**
 * Lineage 객체 생성 요청 DTO
 * 
 * <p>Lineage 관계와 함께 새로운 객체를 생성하기 위한 요청 데이터 구조입니다.
 * 소스 객체에서 시작하여 새로운 타겟 객체를 생성하고 그 관계를 정의합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>source_key</strong>: 소스 객체의 고유 키</li>
 *   <li><strong>source_type</strong>: 소스 객체 타입</li>
 *   <li><strong>target_key</strong>: 생성할 타겟 객체의 키</li>
 *   <li><strong>target_type</strong>: 타겟 객체 타입</li>
 *   <li><strong>action</strong>: 관계 액션 타입 (USE/CREATE)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * LineageObjectCreate request = LineageObjectCreate.builder()
 *     .sourceKey("dataset-123")
 *     .sourceType(ObjectType.DATASET)
 *     .targetKey("model-456")
 *     .targetType(ObjectType.MODEL)
 *     .action(ActionType.CREATE)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 * @see ActionType 액션 타입 정의
 * @see ObjectType 객체 타입 정의
 * @see LineageCreate 기본 Lineage 생성
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Lineage 객체 생성 요청 정보",
    example = """
        {
          "source_key": "a0f49edd-6766-4758-92a3-13c066648bc0",
          "source_type": "DATASET",
          "target_key": "b1f49edd-6766-4758-92a3-13c066648bc1",
          "target_type": "MODEL",
          "action": "CREATE"
        }
        """
)
public class LineageObjectCreate {
    
    /**
     * 소스 객체 키
     * 
     * <p>Lineage 관계에서 출발점이 되는 기존 객체의 고유 식별자입니다.
     * 이 객체는 이미 존재하는 객체여야 하며, 새로운 타겟 객체와의 관계를 형성합니다.</p>
     * 
     * @apiNote 반드시 시스템에 등록된 유효한 객체 키여야 합니다.
     */
    @JsonProperty("source_key")
    @Schema(
        description = "소스 객체의 고유 키 (기존 객체)",
        example = "a0f49edd-6766-4758-92a3-13c066648bc0",
        required = true,
        minLength = 1,
        maxLength = 255
    )
    private String sourceKey;
    
    /**
     * 소스 객체 타입
     * 
     * <p>소스 객체의 타입을 나타냅니다.</p>
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
     * 
     * <p>새로 생성될 타겟 객체의 고유 식별자입니다.
     * 이 키로 새로운 객체가 시스템에 등록되며, 소스 객체와의 관계가 설정됩니다.</p>
     * 
     * @apiNote 시스템에서 고유해야 하며, 중복된 키는 허용되지 않습니다.
     */
    @JsonProperty("target_key")
    @Schema(
        description = "생성할 타겟 객체의 고유 키",
        example = "b1f49edd-6766-4758-92a3-13c066648bc1",
        required = true,
        minLength = 1,
        maxLength = 255
    )
    private String targetKey;
    
    /**
     * 타겟 객체 타입
     * 
     * <p>생성할 타겟 객체의 타입을 나타냅니다.</p>
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
     * 
     * <p>소스에서 타겟으로의 관계 성격을 정의합니다.
     * 객체 생성 시에는 주로 CREATE 액션이 사용되지만, USE 액션도 가능합니다.</p>
     * 
     * @implNote CREATE: 소스가 타겟을 생성, USE: 소스가 타겟을 참조/사용
     */
    @JsonProperty("action")
    @Schema(
        description = "액션 타입 (CREATE: 생성, USE: 사용)",
        example = "CREATE",
        required = true
    )
    private ActionType action;
}