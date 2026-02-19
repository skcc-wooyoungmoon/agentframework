package com.skax.aiplatform.client.sktai.agent.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Agent Graph 정보 수정 요청 DTO
 * 
 * <p>SKTAI Agent 시스템에서 기존 Graph의 기본 정보(이름, 설명)만을 수정하기 위한 요청 데이터 구조입니다.
 * 전체 Graph 수정이 아닌 메타데이터만 빠르게 업데이트할 때 사용됩니다.</p>
 * 
 * <h3>정보 수정 특징:</h3>
 * <ul>
 *   <li><strong>경량 수정</strong>: 노드나 엣지가 아닌 기본 정보만 수정</li>
 *   <li><strong>빠른 업데이트</strong>: 복잡한 Graph 구조 변경 없이 메타데이터만 변경</li>
 *   <li><strong>사용자 친화적</strong>: 직관적인 이름과 설명 관리</li>
 *   <li><strong>버전 관리</strong>: Graph 구조는 유지하면서 메타정보만 갱신</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Graph 이름 변경 (예: 프로젝트명 변경에 따른 업데이트)</li>
 *   <li>설명 추가 또는 수정 (Graph 목적 명확화)</li>
 *   <li>문서화 개선 (사용법이나 주의사항 추가)</li>
 *   <li>팀 협업을 위한 정보 갱신</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GraphInfoUpdateRequest request = GraphInfoUpdateRequest.builder()
 *     .name("Customer Service Bot v2")
 *     .description("고객 서비스 자동화를 위한 AI 에이전트 - 업데이트된 버전")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 * @see GraphUpdateRequest 전체 Graph 수정 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Graph 정보 수정 요청",
    example = """
        {
          "name": "Customer Service Bot v2",
          "description": "고객 서비스 자동화를 위한 AI 에이전트 - 업데이트된 버전"
        }
        """
)
public class GraphInfoUpdateRequest {
    
    /**
     * Graph 이름
     * 
     * <p>변경할 Graph의 새로운 이름입니다.
     * 프로젝트 내에서 고유해야 하며, 명확하고 의미 있는 이름을 사용하는 것이 좋습니다.</p>
     * 
     * @implNote 이름은 알파벳, 숫자, 공백, 하이픈, 언더스코어만 허용됩니다.
     * @apiNote 빈 이름은 허용되지 않으며, 최소 2자 이상 입력해야 합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "Graph의 새로운 이름", 
        example = "Customer Service Bot v2",
        required = true,
        minLength = 2,
        maxLength = 100
    )
    private String name;
    
    /**
     * Graph 설명
     * 
     * <p>Graph의 목적, 기능, 사용법 등을 설명하는 텍스트입니다.
     * 마크다운 형식을 지원하며, 다른 사용자들이 Graph를 이해하는 데 도움이 됩니다.</p>
     * 
     * @implNote 설명이 비어있으면 기존 설명이 유지됩니다.
     */
    @JsonProperty("description")
    @Schema(
        description = "Graph의 새로운 설명 (마크다운 지원)", 
        example = "고객 서비스 자동화를 위한 AI 에이전트 - 업데이트된 버전",
        maxLength = 1000
    )
    private String description;
}
