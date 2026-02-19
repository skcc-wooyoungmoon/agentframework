package com.skax.aiplatform.client.sktai.finetuning.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Trainer 수정 요청 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 기존 Trainer를 수정하기 위한 요청 데이터 구조입니다.
 * 모든 필드가 선택적이며, 수정할 필드만 포함하여 전송합니다.</p>
 * 
 * <h3>수정 가능 정보:</h3>
 * <ul>
 *   <li><strong>registry_url</strong>: Trainer 레지스트리 URL</li>
 *   <li><strong>description</strong>: Trainer 설명</li>
 *   <li><strong>default_params</strong>: 기본 파라미터</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainerRead Trainer 조회 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Trainer 수정 요청 정보",
    example = """
        {
          "registry_url": "https://registry.example.com/my-trainer:v2.0",
          "description": "Updated custom GPT fine-tuning trainer with enhanced features",
          "default_params": "{\\"learning_rate\\": 0.0005, \\"batch_size\\": 64}"
        }
        """
)
public class TrainerUpdate {
    
    /**
     * Trainer 레지스트리 URL
     * 
     * <p>Trainer 이미지가 저장된 컨테이너 레지스트리의 URL입니다.
     * Docker 이미지 형태로 배포된 Trainer를 참조합니다.</p>
     */
    @JsonProperty("registry_url")
    @Schema(
        description = "Trainer 레지스트리 URL (Docker 이미지 경로)", 
        example = "https://registry.example.com/my-trainer:v2.0",
        maxLength = 255
    )
    private String registryUrl;
    
    /**
     * Trainer 설명
     * 
     * <p>Trainer의 목적, 특징, 사용법 등을 설명하는 텍스트입니다.
     * 다른 사용자들이 Trainer를 이해하고 선택하는 데 도움이 됩니다.</p>
     */
    @JsonProperty("description")
    @Schema(
        description = "Trainer 설명 (목적, 특징, 사용법 등)", 
        example = "Updated custom GPT fine-tuning trainer with enhanced features"
    )
    private String description;
    
    /**
     * 기본 파라미터
     * 
     * <p>Trainer가 사용하는 기본 파라미터 설정입니다.
     * JSON 문자열 형태로 저장되며, 학습률, 배치 크기 등의 하이퍼파라미터를 포함합니다.</p>
     */
    @JsonProperty("default_params")
    @Schema(
        description = "Trainer 기본 파라미터 (JSON 문자열, 하이퍼파라미터 포함)", 
        example = "{\"learning_rate\": 0.0005, \"batch_size\": 64, \"epochs\": 20}"
    )
    private String defaultParams;
}
