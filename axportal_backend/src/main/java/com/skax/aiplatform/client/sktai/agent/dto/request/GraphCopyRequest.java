package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Graph 복사 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 기존 Graph를 복사하여 새로운 Graph를 생성하기 위한 요청 데이터 구조입니다.
 * 복잡한 Graph를 처음부터 다시 만들지 않고, 기존 구조를 기반으로 새로운 Graph를 빠르게 생성할 때 사용됩니다.</p>
 * 
 * <h3>Graph 복사 특징:</h3>
 * <ul>
 *   <li><strong>구조 복사</strong>: 노드, 엣지, 워크플로우 전체 구조 복제</li>
 *   <li><strong>독립성 보장</strong>: 원본 Graph와 독립적으로 수정 가능</li>
 *   <li><strong>메타데이터 갱신</strong>: 새로운 이름과 설명으로 구분</li>
 *   <li><strong>빠른 생성</strong>: 복잡한 Graph를 빠르게 복제</li>
 * </ul>
 * 
 * <h3>복사 시나리오:</h3>
 * <ul>
 *   <li><strong>버전 관리</strong>: 기존 Graph의 새 버전 생성</li>
 *   <li><strong>환경별 배포</strong>: 개발/스테이징/운영 환경별 Graph 생성</li>
 *   <li><strong>템플릿 활용</strong>: 표준 Graph를 기반으로 맞춤형 Graph 생성</li>
 *   <li><strong>실험적 변경</strong>: 원본을 보존하면서 실험적 수정 진행</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GraphCopyRequest request = GraphCopyRequest.builder()
 *     .name("Customer Service Bot - Test Version")
 *     .description("기존 고객 서비스 봇의 테스트 버전")
 *     .targetProjectId("project-test-123")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GraphCreateResponse Graph 생성 결과 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Graph 복사 요청",
    example = """
        {
          "name": "Customer Service Bot - Test Version",
          "description": "기존 고객 서비스 봇의 테스트 버전",
          "target_project_id": "project-test-123"
        }
        """
)
public class GraphCopyRequest {
    
    /**
     * 새 Graph 이름
     * 
     * <p>복사하여 생성될 새 Graph의 이름입니다.
     * 원본 Graph와 구분되도록 명확하고 의미 있는 이름을 사용해야 합니다.</p>
     * 
     * @implNote 프로젝트 내에서 고유한 이름이어야 합니다.
     * @apiNote 이름은 원본과 달라야 하며, 복사본임을 나타내는 것이 좋습니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "복사하여 생성할 새 Graph의 이름", 
        example = "Customer Service Bot - Test Version",
        required = true,
        minLength = 2,
        maxLength = 100
    )
    private String name;
    
    /**
     * 새 Graph 설명
     * 
     * <p>복사하여 생성될 새 Graph의 설명입니다.
     * 원본과의 차이점이나 용도를 명확히 설명하는 것이 좋습니다.</p>
     * 
     * @implNote 설명을 제공하지 않으면 원본 Graph의 설명이 복사됩니다.
     */
    @JsonProperty("description")
    @Schema(
        description = "복사하여 생성할 새 Graph의 설명", 
        example = "기존 고객 서비스 봇의 테스트 버전",
        maxLength = 1000
    )
    private String description;
    
    /**
     * 대상 프로젝트 ID
     * 
     * <p>복사된 Graph가 생성될 프로젝트의 식별자입니다.
     * 다른 프로젝트로 Graph를 복사할 때 사용됩니다.</p>
     * 
     * @implNote 제공하지 않으면 원본 Graph와 같은 프로젝트에 생성됩니다.
     * @apiNote 대상 프로젝트에 대한 생성 권한이 있어야 합니다.
     */
    @JsonProperty("target_project_id")
    @Schema(
        description = "복사될 Graph의 대상 프로젝트 ID (선택사항)", 
        example = "project-test-123"
    )
    private String targetProjectId;
    
    /**
     * 복사 옵션
     * 
     * <p>복사 과정에서 적용할 추가 옵션들입니다.
     * 예를 들어, 특정 노드 제외, 설정 초기화 등을 지정할 수 있습니다.</p>
     */
    @JsonProperty("copy_options")
    @Schema(
        description = "복사 옵션 (JSON 형태)", 
        example = """
            {
              "include_history": false,
              "reset_credentials": true,
              "copy_permissions": false
            }
            """
    )
    private String copyOptions;
}
