package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * External Knowledge 수정 요청 DTO
 * 
 * <p>기본 지식 및 사용자 정의 지식의 정보를 수정하기 위한 요청 데이터입니다.</p>
 * 
 * <h3>수정 가능한 항목:</h3>
 * <ul>
 *   <li><strong>기본 지식</strong>: 이름, 설명, 스크립트</li>
 *   <li><strong>사용자 정의 지식</strong>: 이름, 설명, 스크립트, 인덱스명</li>
 * </ul>
 * 
 * @author AI Assistant
 * @since 2025-10-30
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "External Knowledge 수정 요청")
public class DataCtlgExternalKnowledgeUpdateReq {

    /**
     * 지식 이름
     */
    @NotBlank(message = "지식 이름은 필수입니다")
    @Schema(description = "지식 이름", example = "업데이트된 지식 이름", required = true)
    private String name;

    /**
     * 지식 설명
     */
    @Schema(description = "지식 설명", example = "업데이트된 설명")
    private String description;

    /**
     * Retrieval Script
     */
    @Schema(description = "Retrieval Script (Python)", example = "def get_relevant_documents():\n    return []")
    private String script;

    /**
     * 인덱스명 (사용자 정의 지식만 해당)
     */
    @Schema(description = "Vector DB 인덱스명 (사용자 정의 지식만 수정 가능)", example = "my_custom_index")
    private String indexName;
}

