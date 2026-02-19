package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Vector DB 생성 응답 DTO
 * 
 * <p>SKTAI Knowledge 시스템에서 Vector DB 생성 요청에 대한 응답 데이터 구조입니다.
 * 성공적으로 생성된 Vector DB의 고유 식별자를 반환합니다.</p>
 * 
 * <h3>응답 정보:</h3>
 * <ul>
 *   <li><strong>vector_db_id</strong>: 생성된 Vector DB의 고유 식별자</li>
 * </ul>
 * 
 * <h3>활용 방법:</h3>
 * <ul>
 *   <li>반환된 ID로 Vector DB 상세 정보 조회</li>
 *   <li>Knowledge Repository 생성 시 Vector DB ID 참조</li>
 *   <li>Vector DB 수정/삭제 시 식별자로 사용</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * VectorDBCreateResponse response = vectorDbClient.addVectorDb(request);
 * String vectorDbId = response.getVectorDbId();
 * 
 * // 생성된 Vector DB로 Knowledge Repository 생성
 * RepoCreate repoRequest = RepoCreate.builder()
 *     .vectorDbId(vectorDbId)
 *     .build();
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
    description = "SKTAI Vector DB 생성 응답 정보",
    example = """
        {
          "vector_db_id": "550e8400-e29b-41d4-a716-446655440000"
        }
        """
)
public class VectorDBCreateResponse {
    
    /**
     * Vector DB 고유 식별자
     * 
     * <p>새로 생성된 Vector DB의 고유 식별자입니다.
     * UUID 형식으로 제공되며, 이후 모든 Vector DB 관련 API에서 사용됩니다.</p>
     * 
     * @implNote 이 ID는 Knowledge Repository 생성 시 vector_db_id 파라미터로 사용됩니다.
     * @apiNote UUID 형식의 문자열로 반환됩니다 (예: 550e8400-e29b-41d4-a716-446655440000).
     */
    @JsonProperty("vector_db_id")
    @Schema(
        description = "생성된 Vector DB의 고유 식별자 (UUID 형식)",
        example = "550e8400-e29b-41d4-a716-446655440000",
        required = true,
        format = "uuid"
    )
    private String vectorDbId;
}
