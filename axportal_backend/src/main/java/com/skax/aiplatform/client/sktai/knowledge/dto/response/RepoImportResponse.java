package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtImportRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI External Knowledge Repository Import 응답 DTO
 * 
 * <p>External Knowledge Repository Import 요청이 성공적으로 처리된 후 반환되는 응답 데이터 구조입니다.
 * Import된 Repository의 고유 식별자를 포함합니다.</p>
 * 
 * <h3>응답 정보:</h3>
 * <ul>
 *   <li><strong>repo_id</strong>: Import된 Repository의 UUID</li>
 * </ul>
 * 
 * <h3>HTTP 상태 코드:</h3>
 * <ul>
 *   <li><strong>201 Created</strong>: Repository가 성공적으로 Import됨</li>
 *   <li><strong>422 Unprocessable Entity</strong>: 요청 데이터 검증 실패</li>
 *   <li><strong>403 Forbidden</strong>: 권한 부족</li>
 *   <li><strong>404 Not Found</strong>: External Repository를 찾을 수 없음</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * RepoImportResponse response = sktaiReposClient.importExternalRepo(request);
 * String repoId = response.getRepoId();
 * log.info("Repository imported successfully: {}", repoId);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-11-11
 * @version 1.0
 * @see RepoExtImportRequest External Repository Import 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI External Knowledge Repository Import 응답 정보",
    example = """
        {
          "repo_id": "11111111-1111-1111-1111-111111111111"
        }
        """
)
public class RepoImportResponse {
    
    /**
     * Import된 Repository 식별자
     * 
     * <p>성공적으로 Import된 Knowledge Repository의 고유 식별자입니다.
     * 이 ID를 사용하여 Repository를 조회하거나 관리할 수 있습니다.</p>
     * 
     * @apiNote 이 ID는 /api/v1/knowledge/repos/{repo_id} 엔드포인트에서 사용됩니다.
     * @implNote UUID v4 형식의 문자열입니다.
     */
    @JsonProperty("repo_id")
    @Schema(
        description = "Import된 Repository ID (UUID)", 
        example = "11111111-1111-1111-1111-111111111111",
        required = true,
        format = "uuid"
    )
    private String repoId;
}
