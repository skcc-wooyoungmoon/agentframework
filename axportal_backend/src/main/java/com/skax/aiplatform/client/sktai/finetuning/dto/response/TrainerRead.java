package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Trainer 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Trainer 정보를 조회한 결과를 담는 응답 데이터 구조입니다.
 * 생성 요청의 모든 정보와 함께 시스템 생성 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 레지스트리 URL, 설명</li>
 *   <li><strong>설정 정보</strong>: 기본 파라미터</li>
 *   <li><strong>메타데이터</strong>: 생성일시, 수정일시</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainerCreate Trainer 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Trainer 조회 응답 정보",
    example = """
        {
          "registry_url": "https://registry.example.com/my-trainer:latest",
          "description": "Custom GPT fine-tuning trainer",
          "default_params": "{\\"learning_rate\\": 0.001, \\"batch_size\\": 32}",
          "id": "trainer-123",
          "created_at": "2025-08-15T10:30:00Z",
          "updated_at": "2025-08-15T10:30:00Z"
        }
        """
)
public class TrainerRead {
    
    /**
     * Trainer 레지스트리 URL
     * 
     * <p>Trainer 이미지가 저장된 컨테이너 레지스트리의 URL입니다.</p>
     */
    @JsonProperty("registry_url")
    @Schema(
        description = "Trainer 레지스트리 URL (Docker 이미지 경로)", 
        example = "https://registry.example.com/my-trainer:latest",
        maxLength = 255
    )
    private String registryUrl;
    
    /**
     * Trainer 설명
     * 
     * <p>Trainer의 목적, 특징, 사용법 등을 설명하는 텍스트입니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "Trainer 설명 (목적, 특징, 사용법 등)", 
        example = "Custom GPT fine-tuning trainer"
    )
    private String description;
    
    /**
     * 기본 파라미터
     * 
     * <p>Trainer가 사용하는 기본 파라미터 설정입니다.</p>
     */
    @JsonProperty("default_params")
    @Schema(
        description = "Trainer 기본 파라미터 (JSON 문자열)", 
        example = "{\"learning_rate\": 0.001, \"batch_size\": 32}"
    )
    private String defaultParams;
    
    /**
     * Trainer 고유 식별자
     * 
     * <p>시스템에서 자동 생성되는 UUID 형식의 고유 식별자입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "Trainer 고유 식별자 (UUID)", 
        example = "trainer-123",
        format = "uuid"
    )
    private String id;
    
    /**
     * 생성 일시
     * 
     * <p>Trainer가 생성된 일시입니다.</p>
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
     * <p>Trainer가 마지막으로 수정된 일시입니다.</p>
     */
    @JsonProperty("updated_at")
    @Schema(
        description = "수정 일시", 
        example = "2025-08-15T10:30:00Z",
        format = "date-time"
    )
    private LocalDateTime updatedAt;
}
