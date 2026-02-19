package com.skax.aiplatform.client.sktai.modelgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Gateway 모델 목록 응답 DTO
 * 
 * <p>SKTAI Model Gateway 시스템에서 사용 가능한 AI 모델 목록을 나타내는 응답 데이터 구조입니다.
 * 다양한 AI 모델들의 기본 정보와 메타데이터를 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>object</strong>: 응답 객체 타입 ("list")</li>
 *   <li><strong>data</strong>: 모델 정보 배열</li>
 * </ul>
 * 
 * <h3>모델 정보:</h3>
 * <ul>
 *   <li>모델 ID 및 이름</li>
 *   <li>생성 시간</li>
 *   <li>소유자 정보</li>
 *   <li>모델 타입 및 특성</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>사용 가능한 모델 탐색</li>
 *   <li>모델 선택을 위한 정보 제공</li>
 *   <li>모델 카탈로그 구성</li>
 *   <li>권한 확인 및 접근성 검증</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Gateway 사용 가능한 모델 목록 응답",
    example = """
        {
          "object": "list",
          "data": [
            {
              "id": "gpt-4",
              "object": "model",
              "created": 1686935002,
              "owned_by": "openai"
            },
            {
              "id": "gpt-3.5-turbo",
              "object": "model", 
              "created": 1686935002,
              "owned_by": "openai"
            }
          ]
        }
        """
)
public class ModelListResponse {
    
    /**
     * 응답 객체 타입
     * 
     * <p>이 응답이 리스트 형태임을 나타내는 식별자입니다.
     * OpenAI API 표준을 따라 "list" 값을 가집니다.</p>
     * 
     * @implNote 항상 "list" 값을 가지며, API 응답 파싱에 사용됩니다.
     */
    @JsonProperty("object")
    @Schema(
        description = "응답 객체 타입 (항상 'list')",
        example = "list"
    )
    private String object;
    
    /**
     * 모델 정보 목록
     * 
     * <p>사용 가능한 AI 모델들의 상세 정보를 담은 배열입니다.
     * 각 모델의 기본 메타데이터와 접근 정보를 포함합니다.</p>
     * 
     * @apiNote 모델 목록은 사용자의 권한과 구독 상태에 따라 달라질 수 있습니다.
     * @implNote 각 모델 객체는 일관된 구조를 가지며, 추가 필드가 포함될 수 있습니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "모델 정보 배열",
        example = """
            [
              {
                "id": "gpt-4",
                "object": "model",
                "created": 1686935002,
                "owned_by": "openai",
                "permission": [],
                "root": "gpt-4",
                "parent": null
              }
            ]
            """
    )
    private List<ModelInfo> data;
    
    /**
     * 개별 모델 정보 DTO
     * 
     * <p>각 AI 모델의 상세 정보를 나타내는 내부 클래스입니다.
     * 모델 식별자, 메타데이터, 권한 정보 등을 포함합니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 모델 정보")
    public static class ModelInfo {
        
        /**
         * 모델 식별자
         * 
         * <p>모델을 고유하게 식별하는 ID입니다.
         * API 호출 시 이 ID를 사용하여 특정 모델을 지정합니다.</p>
         * 
         * @apiNote 대소문자를 구분하며, 정확한 ID를 사용해야 합니다.
         */
        @JsonProperty("id")
        @Schema(description = "모델 고유 식별자", example = "gpt-4")
        private String id;
        
        /**
         * 객체 타입
         * 
         * <p>이 데이터가 모델 정보임을 나타내는 타입 식별자입니다.
         * 일반적으로 "model" 값을 가집니다.</p>
         */
        @JsonProperty("object")
        @Schema(description = "객체 타입", example = "model")
        private String object;
        
        /**
         * 모델 생성 시간
         * 
         * <p>모델이 생성된 시간을 Unix 타임스탬프로 나타냅니다.
         * 모델의 버전이나 업데이트 시기를 확인할 때 사용합니다.</p>
         * 
         * @implNote Unix 타임스탬프 (초 단위)
         */
        @JsonProperty("created")
        @Schema(description = "모델 생성 시간 (Unix 타임스탬프)", example = "1686935002")
        private Long created;
        
        /**
         * 모델 소유자
         * 
         * <p>모델을 소유하고 있는 조직이나 개발자를 나타냅니다.
         * 모델의 출처와 신뢰성을 확인할 때 참고할 수 있습니다.</p>
         */
        @JsonProperty("owned_by")
        @Schema(description = "모델 소유자 조직", example = "openai")
        private String ownedBy;
        
        /**
         * 모델 권한 정보
         * 
         * <p>이 모델에 대한 접근 권한과 제한사항을 나타냅니다.
         * 사용자별로 다른 권한을 가질 수 있습니다.</p>
         * 
         * @apiNote 권한 구조는 API 공급자에 따라 다를 수 있습니다.
         */
        @JsonProperty("permission")
        @Schema(description = "모델 접근 권한 정보")
        private List<Object> permission;
        
        /**
         * 루트 모델
         * 
         * <p>이 모델의 기반이 되는 루트 모델 ID입니다.
         * 파인튜닝된 모델의 경우 원본 모델을 나타냅니다.</p>
         */
        @JsonProperty("root")
        @Schema(description = "루트 모델 ID", example = "gpt-4")
        private String root;
        
        /**
         * 부모 모델
         * 
         * <p>이 모델의 직접적인 부모 모델 ID입니다.
         * 일반적으로 파인튜닝 체인에서 이전 버전을 나타냅니다.</p>
         */
        @JsonProperty("parent")
        @Schema(description = "부모 모델 ID")
        private String parent;
        
        /**
         * 모델 설명
         * 
         * <p>모델의 기능과 특성에 대한 간단한 설명입니다.
         * 모델 선택에 도움이 되는 정보를 제공합니다.</p>
         */
        @JsonProperty("description")
        @Schema(description = "모델 설명", example = "GPT-4는 가장 진보된 대화형 AI 모델입니다.")
        private String description;
        
        /**
         * 모델 카테고리
         * 
         * <p>모델의 주요 용도나 특성에 따른 분류입니다.
         * 예: text, image, audio, multimodal 등</p>
         */
        @JsonProperty("category")
        @Schema(description = "모델 카테고리", example = "text")
        private String category;
        
        /**
         * 최대 토큰 수
         * 
         * <p>이 모델이 처리할 수 있는 최대 토큰 수입니다.
         * 입력과 출력을 합친 전체 토큰 제한입니다.</p>
         */
        @JsonProperty("max_tokens")
        @Schema(description = "최대 처리 가능 토큰 수", example = "8192")
        private Integer maxTokens;
    }
}
