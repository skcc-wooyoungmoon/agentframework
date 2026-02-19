package com.skax.aiplatform.client.sktai.finetuning.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.PolicyPayload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Trainer 생성 요청 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 새로운 Trainer를 생성하기 위한 요청 데이터 구조입니다.
 * 모델 fine-tuning을 수행할 Trainer의 레지스트리 URL, 기본 파라미터 등을 설정합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>registry_url</strong>: Trainer 레지스트리 URL</li>
 *   <li><strong>default_params</strong>: 기본 파라미터</li>
 * </ul>
 * 
 * <h3>옵션 정보:</h3>
 * <ul>
 *   <li><strong>description</strong>: Trainer 설명</li>
 *   <li><strong>id</strong>: Trainer ID (사용자 지정 가능)</li>
 *   <li><strong>policy</strong>: 접근 권한 정책</li>
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
    description = "SKTAI Fine-tuning Trainer 생성 요청 정보",
    example = """
        {
          "registry_url": "https://registry.example.com/my-trainer:latest",
          "description": "Custom GPT fine-tuning trainer",
          "default_params": "{\\"learning_rate\\": 0.001, \\"batch_size\\": 32}",
          "id": "trainer-123"
        }
        """
)
public class TrainerCreate {
    
    /**
     * Trainer 레지스트리 URL
     * 
     * <p>Trainer 이미지가 저장된 컨테이너 레지스트리의 URL입니다.
     * Docker 이미지 형태로 배포된 Trainer를 참조합니다.</p>
     * 
     * @apiNote 필수 필드이며, 최대 255자까지 입력 가능합니다.
     */
    @JsonProperty("registry_url")
    @Schema(
        description = "Trainer 레지스트리 URL (Docker 이미지 경로)", 
        example = "https://registry.example.com/my-trainer:latest",
        required = true,
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
        example = "Custom GPT fine-tuning trainer with advanced optimization techniques"
    )
    private String description;
    
    /**
     * 기본 파라미터
     * 
     * <p>Trainer가 사용하는 기본 파라미터 설정입니다.
     * JSON 문자열 형태로 저장되며, 학습률, 배치 크기 등의 하이퍼파라미터를 포함합니다.</p>
     * 
     * @apiNote 필수 필드이며, 유효한 JSON 형식이어야 합니다.
     */
    @JsonProperty("default_params")
    @Schema(
        description = "Trainer 기본 파라미터 (JSON 문자열, 하이퍼파라미터 포함)", 
        example = "{\"learning_rate\": 0.001, \"batch_size\": 32, \"epochs\": 10}",
        required = true
    )
    private String defaultParams;
    
    /**
     * Trainer ID
     * 
     * <p>사용자가 직접 지정할 수 있는 Trainer ID입니다.
     * 지정하지 않으면 시스템에서 자동으로 UUID를 생성합니다.</p>
     * 
     * @implNote UUID 형식이어야 하며, 선택적 필드입니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "Trainer ID (UUID 형식, 미지정시 자동 생성)", 
        example = "trainer-123",
        format = "uuid"
    )
    private String id;
    
    /**
     * 접근 권한 정책
     * 
     * <p>Trainer에 대한 접근 권한을 정의하는 정책 배열입니다.
     * 사용자, 그룹, 역할별 접근 권한과 허용 범위를 설정합니다.</p>
     */
    @JsonProperty("policy")
    @Schema(
        description = "접근 권한 정책 배열 (사용자, 그룹, 역할별 접근 권한 정의)"
    )
    private PolicyPayload policy;
}
