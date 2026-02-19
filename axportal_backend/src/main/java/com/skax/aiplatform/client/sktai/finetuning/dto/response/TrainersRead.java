package com.skax.aiplatform.client.sktai.finetuning.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Fine-tuning Trainer 목록 조회 응답 DTO
 * 
 * <p>SKTAI Fine-tuning 시스템에서 Trainer 목록을 조회한 결과를 담는 응답 데이터 구조입니다.
 * 페이지네이션 정보와 함께 Trainer 목록을 제공하여 대량의 Trainer 데이터를 효율적으로 처리할 수 있습니다.</p>
 * 
 * <h3>주요 구성요소:</h3>
 * <ul>
 *   <li><strong>data</strong>: Trainer 목록</li>
 *   <li><strong>payload</strong>: 페이지네이션 정보</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>Trainer 관리 대시보드</li>
 *   <li>Trainer 검색 및 필터링</li>
 *   <li>Training 작업 할당을 위한 Trainer 선택</li>
 *   <li>대용량 Trainer 데이터 페이징 처리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see TrainerRead Trainer 상세 정보
 * @see Payload 페이지네이션 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Fine-tuning Trainer 목록 조회 응답 정보",
    example = """
        {
          "data": [
            {
              "id": "550e8400-e29b-41d4-a716-446655440000",
              "name": "GPT-4 Trainer",
              "status": "active",
              "created_at": "2025-08-15T10:30:00Z"
            }
          ],
          "payload": {
            "page": 1,
            "size": 20,
            "total": 50,
            "total_pages": 3
          }
        }
        """
)
public class TrainersRead {
    
    /**
     * Trainer 목록 데이터
     * 
     * <p>페이지네이션된 Trainer들의 목록입니다.
     * 각 Trainer는 고유 식별자, 이름, 상태 등의 기본 정보를 포함합니다.</p>
     * 
     * @apiNote Trainer 목록은 생성 시간순 또는 이름순으로 정렬되어 제공될 수 있습니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "Trainer 목록 데이터",
        example = """
            [
              {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "name": "GPT-4 Trainer",
                "status": "active",
                "created_at": "2025-08-15T10:30:00Z"
              }
            ]
            """
    )
    private List<TrainerRead> data;
    
    /**
     * 페이지네이션 정보
     * 
     * <p>Trainer 목록의 페이지네이션 관련 정보입니다.
     * 현재 페이지, 페이지 크기, 전체 개수 등을 포함합니다.</p>
     * 
     * @implNote 대량의 Trainer 데이터를 효율적으로 처리하기 위해 페이지네이션이 적용됩니다.
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이지네이션 정보",
        example = """
            {
              "page": 1,
              "size": 20,
              "total": 50,
              "total_pages": 3
            }
            """
    )
    private Payload payload;
}
