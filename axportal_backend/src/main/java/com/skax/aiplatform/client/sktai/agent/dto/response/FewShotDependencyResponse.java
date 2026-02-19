package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Few-Shot 의존성 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 Few-Shot의 의존성 정보를 나타내는 응답 데이터 구조입니다.
 * Few-Shot이 다른 리소스나 모델과 어떤 관계를 가지고 있는지, 삭제나 수정 시 영향을 받는 요소들을 확인할 때 사용됩니다.</p>
 * 
 * <h3>의존성 정보 포함 항목:</h3>
 * <ul>
 *   <li><strong>참조 관계</strong>: Few-Shot을 참조하는 다른 리소스들</li>
 *   <li><strong>연관 모델</strong>: Few-Shot을 사용하는 AI 모델들</li>
 *   <li><strong>파이프라인</strong>: Few-Shot이 포함된 처리 파이프라인들</li>
 *   <li><strong>프로젝트</strong>: Few-Shot을 사용하는 프로젝트들</li>
 * </ul>
 * 
 * <h3>의존성 체크 시나리오:</h3>
 * <ul>
 *   <li><strong>삭제 전 검증</strong>: Few-Shot 삭제 시 영향도 분석</li>
 *   <li><strong>수정 전 검증</strong>: Few-Shot 변경 시 영향받는 리소스 확인</li>
 *   <li><strong>관계 분석</strong>: Few-Shot과 다른 리소스 간의 관계 파악</li>
 *   <li><strong>사용량 추적</strong>: Few-Shot의 실제 활용도 측정</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * FewShotDependencyResponse deps = fewShotClient.getDependency(versionId);
 * boolean canDelete = deps.getReferences().isEmpty();
 * List&lt;String&gt; affectedModels = deps.getUsedByModels();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Few-Shot 의존성 정보 응답",
    example = """
        {
          "version_id": "version-456",
          "few_shot_uuid": "fs-456e7890-e12b-34d5-a678-426614174111",
          "references": [
            {
              "type": "model",
              "id": "model-123",
              "name": "Customer Service Bot"
            }
          ],
          "used_by_models": ["model-123", "model-456"],
          "used_by_pipelines": ["pipeline-789"],
          "used_by_projects": ["project-abc"],
          "can_delete": false,
          "dependency_count": 3
        }
        """
)
public class FewShotDependencyResponse {
    
    /**
     * Few-Shot 버전 식별자
     * 
     * <p>의존성을 조회한 Few-Shot 버전의 식별자입니다.</p>
     */
    @JsonProperty("version_id")
    @Schema(
        description = "Few-Shot 버전 ID", 
        example = "version-456"
    )
    private String versionId;
    
    /**
     * Few-Shot 고유 식별자
     * 
     * <p>의존성을 조회한 Few-Shot의 UUID입니다.</p>
     */
    @JsonProperty("few_shot_uuid")
    @Schema(
        description = "Few-Shot UUID", 
        example = "fs-456e7890-e12b-34d5-a678-426614174111",
        format = "uuid"
    )
    private String fewShotUuid;
    
    /**
     * 참조 관계 목록
     * 
     * <p>Few-Shot을 참조하거나 사용하는 다른 리소스들의 상세 정보입니다.
     * 각 참조는 타입, ID, 이름 등의 정보를 포함합니다.</p>
     */
    @JsonProperty("references")
    @Schema(
        description = "Few-Shot을 참조하는 리소스 목록", 
        example = """
            [
              {
                "type": "model",
                "id": "model-123",
                "name": "Customer Service Bot",
                "description": "고객 서비스용 AI 모델"
              }
            ]
            """
    )
    private List<Object> references;
    
    /**
     * 사용 중인 모델 목록
     * 
     * <p>Few-Shot을 사용하고 있는 AI 모델들의 ID 목록입니다.</p>
     */
    @JsonProperty("used_by_models")
    @Schema(
        description = "Few-Shot을 사용하는 모델 ID 목록", 
        example = "[\"model-123\", \"model-456\"]"
    )
    private List<String> usedByModels;
    
    /**
     * 사용 중인 파이프라인 목록
     * 
     * <p>Few-Shot이 포함된 처리 파이프라인들의 ID 목록입니다.</p>
     */
    @JsonProperty("used_by_pipelines")
    @Schema(
        description = "Few-Shot을 사용하는 파이프라인 ID 목록", 
        example = "[\"pipeline-789\"]"
    )
    private List<String> usedByPipelines;
    
    /**
     * 사용 중인 프로젝트 목록
     * 
     * <p>Few-Shot을 사용하고 있는 프로젝트들의 ID 목록입니다.</p>
     */
    @JsonProperty("used_by_projects")
    @Schema(
        description = "Few-Shot을 사용하는 프로젝트 ID 목록", 
        example = "[\"project-abc\"]"
    )
    private List<String> usedByProjects;
    
    /**
     * 삭제 가능 여부
     * 
     * <p>Few-Shot을 안전하게 삭제할 수 있는지 여부입니다.
     * 다른 리소스에서 사용 중인 경우 false를 반환합니다.</p>
     */
    @JsonProperty("can_delete")
    @Schema(
        description = "Few-Shot 삭제 가능 여부", 
        example = "false"
    )
    private Boolean canDelete;
    
    /**
     * 총 의존성 개수
     * 
     * <p>Few-Shot과 관련된 의존성의 총 개수입니다.
     * 참조하는 모든 리소스의 합계를 나타냅니다.</p>
     */
    @JsonProperty("dependency_count")
    @Schema(
        description = "총 의존성 개수", 
        example = "3",
        minimum = "0"
    )
    private Integer dependencyCount;
    
    /**
     * 경고 메시지
     * 
     * <p>의존성과 관련된 경고나 주의사항 메시지입니다.
     * 삭제나 수정 시 주의해야 할 점들을 안내합니다.</p>
     */
    @JsonProperty("warning_message")
    @Schema(
        description = "의존성 관련 경고 메시지", 
        example = "이 Few-Shot을 삭제하면 3개의 모델이 영향을 받습니다."
    )
    private String warningMessage;
}
