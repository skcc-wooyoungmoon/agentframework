package com.skax.aiplatform.client.elastic.search.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * Elasticsearch Index 생성 요청 DTO
 * 
 * <p>Elasticsearch에 새로운 인덱스를 생성하기 위한 요청 데이터 구조입니다.</p>
 * <p>Elasticsearch Index Creation API 스펙:
 * <ul>
 *   <li>indexName은 Path Parameter로 전달되므로 Body에서 제외</li>
 *   <li>null 값인 필드는 Body에서 제외</li>
 *   <li>options는 Elasticsearch API에 없는 필드이므로 제외</li>
 * </ul>
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-01-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Elasticsearch Index 생성 요청 정보")
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 값 필드 제외
public class CreateIndexRequest {

    /**
     * 인덱스명 (Path Parameter로 전달되므로 Body에서 제외)
     */
    @JsonIgnore  // Body에서 제외
    @NotBlank(message = "인덱스명은 필수입니다")
    @Schema(description = "인덱스명", example = "gaf_default_rag_550e8400-e29b-41d4-a716-446655440000", required = true)
    private String indexName;

    /**
     * 인덱스 설정 (settings)
     */
    @Schema(description = "인덱스 설정")
    private Map<String, Object> settings;

    /**
     * 매핑 설정 (mappings)
     */
    @Schema(description = "매핑 설정")
    private Map<String, Object> mappings;

    /**
     * 별칭 설정
     */
    @Schema(description = "별칭 설정")
    private Map<String, Object> aliases;

    /**
     * 인덱스 생성 옵션 (Elasticsearch API에서 지원하지 않으므로 Body에서 제외)
     */
    @JsonIgnore  // Body에서 제외
    @Schema(description = "인덱스 생성 옵션")
    private Map<String, Object> options;
}
