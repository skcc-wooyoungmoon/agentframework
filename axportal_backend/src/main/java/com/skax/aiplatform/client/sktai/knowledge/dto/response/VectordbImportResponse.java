package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectordbImportRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Vector Database Import 응답 DTO
 * 
 * <p>Vector Database Import 요청이 성공적으로 처리된 후 반환되는 응답 데이터 구조입니다.
 * Import된 Vector Database의 고유 식별자를 포함합니다.</p>
 * 
 * <h3>응답 정보:</h3>
 * <ul>
 *   <li><strong>vector_db_id</strong>: Import된 Vector Database의 UUID</li>
 * </ul>
 * 
 * <h3>HTTP 상태 코드:</h3>
 * <ul>
 *   <li><strong>201 Created</strong>: Vector Database가 성공적으로 Import됨</li>
 *   <li><strong>422 Unprocessable Entity</strong>: 요청 데이터 검증 실패</li>
 *   <li><strong>403 Forbidden</strong>: 권한 부족</li>
 *   <li><strong>409 Conflict</strong>: 동일한 이름의 Vector Database가 이미 존재함</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * VectordbImportResponse response = sktaiVectordbsClient.importVectordb(request);
 * String vectorDbId = response.getVectorDbId();
 * log.info("Vector Database imported successfully: {}", vectorDbId);
 * 
 * // Import된 Vector DB를 Repository 생성 시 사용
 * RepoCreate repoRequest = RepoCreate.builder()
 *     .vectorDbId(vectorDbId)
 *     .name("My Knowledge Repo")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-11-11
 * @version 1.0
 * @see VectordbImportRequest Vector Database Import 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Vector Database Import 응답 정보",
    example = """
        {
          "vector_db_id": "a0f59edd-6766-4758-92a3-13c066648bc0"
        }
        """
)
public class VectordbImportResponse {
    
    /**
     * Import된 Vector Database 식별자
     * 
     * <p>성공적으로 Import된 Vector Database의 고유 식별자입니다.
     * 이 ID를 사용하여 Knowledge Repository 생성 시 Vector Database를 지정할 수 있습니다.</p>
     * 
     * @apiNote 이 ID는 /api/v1/knowledge/vectordbs/{vector_db_id} 엔드포인트에서 사용됩니다.
     * @apiNote Repository 생성 시 vectorDbId 파라미터로 사용됩니다.
     * @implNote UUID v4 형식의 문자열입니다.
     */
    @JsonProperty("vector_db_id")
    @Schema(
        description = "Import된 Vector Database ID (UUID)", 
        example = "a0f59edd-6766-4758-92a3-13c066648bc0",
        required = true,
        format = "uuid"
    )
    private String vectorDbId;
}
