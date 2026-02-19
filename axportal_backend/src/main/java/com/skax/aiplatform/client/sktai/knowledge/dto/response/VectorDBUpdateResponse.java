package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Vector DB 수정 응답 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 Vector DB 수정 요청에 대한 응답 데이터 구조입니다.
 * 성공적으로 수정된 Vector DB의 고유 식별자를 반환합니다.</p>
 * 
 * <h3>응답 정보:</h3>
 * <ul>
 *   <li><strong>vector_db_id</strong>: 수정된 Vector DB의 고유 식별자</li>
 * </ul>
 * 
 * <h3>활용 방법:</h3>
 * <ul>
 *   <li>수정 완료 확인</li>
 *   <li>수정된 Vector DB 상세 정보 재조회</li>
 *   <li>연결된 Knowledge Repository 상태 확인</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * VectorDBUpdateResponse response = vectorDbClient.updateVectorDb(vectorDbId, request);
 * String updatedVectorDbId = response.getVectorDbId();
 * 
 * // 수정된 Vector DB 정보 재조회
 * VectorDBDetailResponse detail = vectorDbClient.getVectorDb(updatedVectorDbId);
 * </pre>
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
    description = "SKTAI Vector DB 수정 응답 정보",
    example = """
        {
          "vector_db_id": "550e8400-e29b-41d4-a716-446655440000"
        }
        """
)
public class VectorDBUpdateResponse {
    
    /**
     * Vector DB 고유 식별자
     * 
     * <p>수정된 Vector DB의 고유 식별자입니다.
     * 수정 작업이 완료되었음을 확인하는 용도로 사용됩니다.</p>
     * 
     * @implNote 수정 후에도 동일한 ID가 유지됩니다.
     * @apiNote UUID 형식의 문자열로 반환됩니다.
     */
    @JsonProperty("vector_db_id")
    @Schema(
        description = "수정된 Vector DB의 고유 식별자 (UUID 형식)",
        example = "550e8400-e29b-41d4-a716-446655440000",
        required = true,
        format = "uuid"
    )
    private String vectorDbId;
}
