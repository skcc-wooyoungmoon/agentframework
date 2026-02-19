package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI Agent Graph 템플릿 목록 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 사용 가능한 Graph 템플릿 목록을 나타내는 응답 데이터 구조입니다.
 * 미리 정의된 템플릿을 사용하여 빠르게 Graph를 생성할 수 있는 정보를 제공합니다.</p>
 * 
 * <h3>템플릿 시스템 특징:</h3>
 * <ul>
 *   <li><strong>재사용성</strong>: 공통적으로 사용되는 Graph 패턴 제공</li>
 *   <li><strong>표준화</strong>: 검증된 구조와 모범 사례 포함</li>
 *   <li><strong>커스터마이징</strong>: 템플릿 기반 맞춤형 Graph 생성</li>
 *   <li><strong>빠른 시작</strong>: 복잡한 Graph를 빠르게 구축</li>
 * </ul>
 * 
 * <h3>템플릿 카테고리:</h3>
 * <ul>
 *   <li><strong>기본 템플릿</strong>: 단순한 질의응답, 분류 등</li>
 *   <li><strong>고급 템플릿</strong>: 복합 워크플로우, 멀티 에이전트 등</li>
 *   <li><strong>도메인별 템플릿</strong>: 고객 서비스, 문서 분석 등</li>
 *   <li><strong>사용자 정의</strong>: 조직별 맞춤 템플릿</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * GraphTemplatesResponse templates = graphClient.getGraphTemplates();
 * List&lt;GraphTemplate&gt; availableTemplates = templates.getTemplates();
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
    description = "SKTAI Agent Graph 템플릿 목록 응답",
    example = """
        {
          "templates": [
            {
              "template_id": "template-qna-basic",
              "name": "Basic Q&A Template",
              "description": "기본적인 질의응답 처리를 위한 템플릿",
              "category": "basic",
              "version": "1.0",
              "tags": ["qna", "basic", "general"],
              "created_at": "2025-08-01T10:00:00Z"
            }
          ],
          "total_count": 15,
          "categories": ["basic", "advanced", "domain-specific"]
        }
        """
)
public class GraphTemplatesResponse {
    
    /**
     * 템플릿 목록
     * 
     * <p>사용 가능한 Graph 템플릿들의 상세 정보 목록입니다.</p>
     */
    @JsonProperty("templates")
    @Schema(
        description = "Graph 템플릿 목록", 
        example = """
            [
              {
                "template_id": "template-qna-basic",
                "name": "Basic Q&A Template",
                "description": "기본적인 질의응답 처리를 위한 템플릿",
                "category": "basic"
              }
            ]
            """
    )
    private List<GraphTemplate> templates;
    
    /**
     * 총 템플릿 개수
     * 
     * <p>시스템에서 사용 가능한 전체 템플릿의 개수입니다.</p>
     */
    @JsonProperty("total_count")
    @Schema(
        description = "총 템플릿 개수", 
        example = "15"
    )
    private Integer totalCount;
    
    /**
     * 템플릿 카테고리 목록
     * 
     * <p>템플릿들을 분류하는 카테고리 목록입니다.</p>
     */
    @JsonProperty("categories")
    @Schema(
        description = "템플릿 카테고리 목록", 
        example = "[\"basic\", \"advanced\", \"domain-specific\"]"
    )
    private List<String> categories;
    
    /**
     * 개별 Graph 템플릿 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 Graph 템플릿 정보")
    public static class GraphTemplate {
        
        /**
         * 템플릿 ID
         */
        @JsonProperty("template_id")
        @Schema(description = "템플릿 고유 식별자", example = "template-qna-basic")
        private String templateId;
        
        /**
         * 템플릿 이름
         */
        @JsonProperty("name")
        @Schema(description = "템플릿 이름", example = "Basic Q&A Template")
        private String name;
        
        /**
         * 템플릿 설명
         */
        @JsonProperty("description")
        @Schema(description = "템플릿 설명", example = "기본적인 질의응답 처리를 위한 템플릿")
        private String description;
        
        /**
         * 템플릿 카테고리
         */
        @JsonProperty("category")
        @Schema(description = "템플릿 카테고리", example = "basic")
        private String category;
        
        /**
         * 템플릿 버전
         */
        @JsonProperty("version")
        @Schema(description = "템플릿 버전", example = "1.0")
        private String version;
        
        /**
         * 템플릿 태그
         */
        @JsonProperty("tags")
        @Schema(description = "템플릿 태그 목록", example = "[\"qna\", \"basic\", \"general\"]")
        private List<String> tags;
        
        /**
         * 생성일시
         */
        @JsonProperty("created_at")
        @Schema(description = "템플릿 생성일시", example = "2025-08-01T10:00:00Z")
        private LocalDateTime createdAt;
        
        /**
         * 사용 횟수
         */
        @JsonProperty("usage_count")
        @Schema(description = "템플릿 사용 횟수", example = "142")
        private Integer usageCount;
    }
}
