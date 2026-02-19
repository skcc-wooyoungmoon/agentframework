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
 * SKTAI Model Version 목록 응답 DTO
 * 
 * <p>SKTAI Model Version 목록 조회 결과를 담는 응답 데이터 구조입니다.
 * 페이징 정보와 함께 버전 목록을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>data</strong>: 모델 버전 목록</li>
 *   <li><strong>payload</strong>: 페이징 정보</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Version 목록 응답",
    example = """
        {
          "data": [
            {
              "id": "mv-123e4567-e89b-12d3-a456-426614174000",
              "version": 1,
              "description": "초기 버전"
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 10
            }
          }
        }
        """
)
public class ModelVersionsRead {
    
    /**
     * 모델 버전 목록
     * 
     * <p>조회된 모델 버전들의 상세 정보 목록입니다.</p>
     */
    @JsonProperty("data")
    @Schema(description = "모델 버전 목록")
    private List<ModelVersionRead> data;
    
    /**
     * 페이징 정보
     * 
     * <p>목록 조회에 대한 페이징 메타데이터입니다.</p>
     */
    @JsonProperty("payload")
    @Schema(description = "페이징 정보")
    private Payload payload;
}
