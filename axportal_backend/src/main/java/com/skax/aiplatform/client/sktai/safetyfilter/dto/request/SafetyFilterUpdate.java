package com.skax.aiplatform.client.sktai.safetyfilter.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.ValidTagTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Safety Filter 수정 요청 DTO
 * 
 * <p>SKTAI Safety Filter 시스템에서 기존 안전 필터를 수정하기 위한 요청 데이터 구조입니다.
 * 모든 필드가 선택적이며, 수정할 필드만 포함하여 전송합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>stopword</strong>: 필터링할 키워드 (수정시 필수)</li>
 * </ul>
 * 
 * <h3>옵션 정보:</h3>
 * <ul>
 *   <li><strong>label</strong>: 안전 필터 라벨</li>
 *   <li><strong>category</strong>: 카테고리</li>
 *   <li><strong>except_sources</strong>: 필터링 제외 소스 목록</li>
 *   <li><strong>valid_tags</strong>: 유효 태그 타입</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SafetyFilterRead Safety Filter 조회 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "SKTAI Safety Filter 수정 요청 정보",
    example = """
        {
          "stopword": "inappropriate_content_updated",
          "label": "unsafe_user_defined",
          "category": "",
          "except_sources": "",
          "valid_tags": "ALL"
        }
        """
)
public class SafetyFilterUpdate {
    
    /**
     * 필터링할 키워드
     * 
     * <p>안전 필터링의 대상이 되는 키워드입니다.
     * 공백을 포함할 수 있으며, 술어와 형태소 원형 텍스트를 사용할 수 있습니다.</p>
     * 
     * @apiNote 수정시 필수 필드이며, 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("stopword")
    @Schema(
        description = "필터링할 키워드 (공백 포함 가능, 술어와 형태소 원형 텍스트 사용 가능)", 
        example = "inappropriate_content_updated",
        required = true,
        maxLength = 255
    )
    private String stopword;
    
    /**
     * 안전 필터 라벨
     * 
     * <p>시스템에서 정의된 라벨만 사용할 수 있으며, 하나의 라벨만 등록 가능합니다.
     * 기본값은 "unsafe_user_defined"입니다.</p>
     * 
     * @implNote 시스템 정의 라벨 외에는 사용할 수 없습니다.
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
     * 
     * @apiNote 빈 문자열이 기본값입니다.
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
     * 
     * @implNote 빈 문자열이 기본값입니다.
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
     * 
     * @apiNote ALL 또는 NN만 사용 가능합니다.
     */
    @JsonProperty("valid_tags")
    @Schema(
        description = "유효 태그 타입 (ALL: 모든 형태소 매칭, NN: 명사만 매칭)", 
        example = "ALL",
        defaultValue = "ALL"
    )
    private ValidTagTypeEnum validTags;
}
