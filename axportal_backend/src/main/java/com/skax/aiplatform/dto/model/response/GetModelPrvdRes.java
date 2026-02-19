package com.skax.aiplatform.dto.model.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetModelPrvdRes {
    
    /**
     * 모델 제공자 ID
     */
    @Schema(
        description = "모델 제공자 고유 식별자 (UUID)", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 모델 제공자 이름
     */
    @Schema(
        description = "모델 제공자의 고유한 이름", 
        example = "OpenAI",
        maxLength = 255
    )
    private String name;
    
    /**
     * 모델 제공자 설명
     */
    @Schema(
        description = "모델 제공자에 대한 설명", 
        example = "OpenAI is a leading AI research company focused on creating safe and beneficial artificial intelligence"
    )
    private String description;
    
    /**
     * 로고 이미지
     */
    @Schema(
        description = "로고 이미지 URL 또는 파일 경로", 
        example = "https://example.com/openai-logo.png",
        maxLength = 255
    )
    private String logo;
    
    /**
     * 생성 일시
     */
    @Schema(
        description = "모델 제공자 생성 일시", 
        example = "2025-08-15T10:30:00",
        format = "date-time"
    )
    private LocalDateTime createdAt;
    
    /**
     * 수정 일시
     */
    @Schema(
        description = "모델 제공자 수정 일시", 
        example = "2025-08-15T10:30:00",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
}
