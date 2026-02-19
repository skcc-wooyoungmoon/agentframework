package com.skax.aiplatform.dto.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 모델 기본 응답 DTO
 * 
 * <p>모델의 기본 정보를 반환하는 응답 데이터 구조입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-01-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 기본 응답 정보")
public class ModelRes {
    
    @JsonProperty("id")
    @Schema(description = "모델 ID", format = "uuid")
    private String id;
    
    @JsonProperty("display_name")
    @Schema(description = "모델 표시 이름")
    private String displayName;
    
    @JsonProperty("name")
    @Schema(description = "모델 관리 이름")
    private String name;
    
    @JsonProperty("type")
    @Schema(description = "모델 타입")
    private String type;
    
    @JsonProperty("description")
    @Schema(description = "모델 설명")
    private String description;
    
    @JsonProperty("size")
    @Schema(description = "모델 파라미터 크기")
    private String size;
    
    @JsonProperty("token_size")
    @Schema(description = "토큰 크기")
    private String tokenSize;
    
    @JsonProperty("serving_type")
    @Schema(description = "서빙 타입")
    private String servingType;
    
    @JsonProperty("is_private")
    @Schema(description = "비공개 여부")
    private Boolean isPrivate;
    
    @JsonProperty("is_valid")
    @Schema(description = "유효성 여부")
    private Boolean isValid;
    
    @JsonProperty("provider_name")
    @Schema(description = "모델 제공자 이름")
    private String providerName;
    
    @JsonProperty("last_version")
    @Schema(description = "마지막 버전 번호")
    private Integer lastVersion;
    
    @JsonProperty("is_custom")
    @Schema(description = "커스텀 코드 필요 여부")
    private Boolean isCustom;
    
    @JsonProperty("created_at")
    @Schema(description = "생성 일시", format = "date-time")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @Schema(description = "수정 일시", format = "date-time")
    private LocalDateTime updatedAt;
}
