package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Safety Filter 조회 응답 DTO
 * 
 * <p>SKTAI Safety Filter 시스템에서 안전 필터 정보를 조회한 결과를 담는 응답 데이터 구조입니다.
 * 생성 요청의 모든 정보와 함께 시스템 생성 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 키워드, 라벨, 카테고리</li>
 *   <li><strong>설정 정보</strong>: 제외 소스, 유효 태그, 프로젝트 ID</li>
 *   <li><strong>메타데이터</strong>: 생성일시, 수정일시</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SafetyFilterCreate Safety Filter 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Safety Filter 조회 응답 정보",
    example = """
        {
          "stopword": "inappropriate_content",
          "label": "unsafe_user_defined",
          "category": "",
          "except_sources": "",
          "valid_tags": "ALL",
          "project_id": "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
          "id": "123e4567-e89b-12d3-a456-426614174000",
          "created_at": "2025-08-15T10:30:00Z",
          "updated_at": "2025-08-15T10:30:00Z"
        }
        """
)
public class SafetyFilterRead {
    
    /**
     * 필터링할 키워드
     * 
     * <p>안전 필터링의 대상이 되는 키워드입니다.
     * 공백을 포함할 수 있으며, 술어와 형태소 원형 텍스트를 사용할 수 있습니다.</p>
     */
    @JsonProperty("stopword")
    @Schema(
        description = "필터링할 키워드 (공백 포함 가능, 술어와 형태소 원형 텍스트 사용 가능)", 
        example = "inappropriate_content",
        maxLength = 255
    )
    private String stopword;
    
    /**
     * 안전 필터 라벨
     * 
     * <p>시스템에서 정의된 라벨만 사용할 수 있으며, 하나의 라벨만 등록 가능합니다.
     * 기본값은 "unsafe_user_defined"입니다.</p>
     */
    @JsonProperty("label")
    @Schema(
        description = "안전 필터 라벨 (시스템 정의 라벨만 사용 가능)", 
        example = "unsafe_user_defined",
        maxLength = 255,
        defaultValue = "unsafe_user_defined"
    )
    private String label;
    
    /**
     * 카테고리
     * 
     * <p>famous(인물명), brand(브랜드), culture(콘텐츠명)만 등록 가능하며,
     * 필요한 경우에만 사용합니다.</p>
     */
    @JsonProperty("category")
    @Schema(
        description = "카테고리 (famous, brand, culture 중 선택, 필요시에만 사용)", 
        example = "",
        maxLength = 255,
        defaultValue = ""
    )
    private String category;
    
    /**
     * 필터링 제외 소스 목록
     * 
     * <p>STOPWORD에 반영되지 않아야 할 소스명 목록을 쉼표로 구분한 문자열로 등록합니다.
     * 시스템에서 지정한 소스만 사용 가능합니다.</p>
     */
    @JsonProperty("except_sources")
    @Schema(
        description = "필터링 제외 소스 목록 (쉼표로 구분, 시스템 지정 소스만 사용 가능)", 
        example = "",
        maxLength = 255,
        defaultValue = ""
    )
    private String exceptSources;
    
    /**
     * 유효 태그 타입
     * 
     * <p>ALL(모든 형태소 매칭) 또는 NN(명사만 매칭) 두 가지 타입만 등록 가능합니다.
     * 기본값은 ALL입니다.</p>
     */
    @JsonProperty("valid_tags")
    @Schema(
        description = "유효 태그 타입 (ALL: 모든 형태소 매칭, NN: 명사만 매칭)", 
        example = "ALL",
        defaultValue = "ALL"
    )
    private ValidTagTypeEnum validTags;
    
    /**
     * 프로젝트 ID
     * 
     * <p>Safety Filter가 속한 프로젝트의 고유 식별자입니다.</p>
     */
    @JsonProperty("project_id")
    @Schema(
        description = "Safety Filter가 속한 프로젝트 ID", 
        example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5",
        defaultValue = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"
    )
    private String projectId;
    
    /**
     * Safety Filter 고유 식별자
     * 
     * <p>시스템에서 자동 생성되는 UUID 형식의 고유 식별자입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "Safety Filter 고유 식별자 (UUID)", 
        example = "123e4567-e89b-12d3-a456-426614174000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 생성 일시
     * 
     * <p>Safety Filter가 생성된 일시입니다.</p>
     */
    @JsonProperty("created_at")
    @Schema(
        description = "생성 일시", 
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 일시
     * 
     * <p>Safety Filter가 마지막으로 수정된 일시입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "수정 일시", 
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
}
