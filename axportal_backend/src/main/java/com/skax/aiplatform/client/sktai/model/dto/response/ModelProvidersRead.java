package com.skax.aiplatform.client.sktai.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Providers 목록 응답 DTO
 * 
 * <p>SKTAI Model 시스템에서 모델 제공자 목록을 페이징하여 반환하는 응답 데이터 구조입니다.
 * 페이지네이션 정보와 함께 모델 제공자 목록을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>data</strong>: 현재 페이지의 모델 제공자 목록</li>
 *   <li><strong>payload</strong>: 페이지네이션 정보 (총 개수, 페이지 링크 등)</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>모델 제공자 목록 조회</li>
 *   <li>페이징된 모델 제공자 검색 결과</li>
 *   <li>필터링된 모델 제공자 목록</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ModelProviderRead 개별 모델 제공자 정보
 * @see Payload 페이지네이션 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Providers 목록 응답 정보",
    example = """
        {
          "data": [
            {
              "id": "550e8400-e29b-41d4-a716-446655440000",
              "name": "OpenAI",
              "description": "OpenAI is a leading AI research company",
              "logo": "https://example.com/openai-logo.png",
              "created_at": "2025-08-15T10:30:00",
              "updated_at": "2025-08-15T10:30:00"
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 25,
              "per_page": 10
            }
          }
        }
        """
)
public class ModelProvidersRead {
    
    /**
     * 모델 제공자 목록
     * 
     * <p>현재 페이지에 포함된 모델 제공자들의 목록입니다.
     * 각 항목은 ModelProviderRead 형태의 완전한 제공자 정보를 포함합니다.</p>
     * 
     * @implNote 페이지 크기에 따라 목록의 크기가 결정됩니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "현재 페이지의 모델 제공자 목록",
        example = """
            [
              {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "name": "OpenAI",
                "description": "OpenAI is a leading AI research company"
              }
            ]
            """
    )
    private List<ModelProviderRead> data;
    
    /**
     * 페이지네이션 정보
     * 
     * <p>목록 조회에 대한 페이지네이션 메타데이터입니다.
     * 총 개수, 현재 페이지, 페이지 링크 등의 정보를 포함합니다.</p>
     * 
     * @apiNote 클라이언트에서 페이지 네비게이션을 구현할 때 사용됩니다.
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이지네이션 메타데이터",
        example = """
            {
              "pagination": {
                "page": 1,
                "total": 25,
                "per_page": 10,
                "last_page": 3
              }
            }
            """
    )
    private Payload payload;
}
