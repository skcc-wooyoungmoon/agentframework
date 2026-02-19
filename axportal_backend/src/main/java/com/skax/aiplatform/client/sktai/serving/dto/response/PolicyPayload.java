package com.skax.aiplatform.client.sktai.serving.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 정책 페이로드 DTO
 * 
 * <p>SKTAI Serving 시스템에서 정책 관련 응답을 위한 페이로드입니다.
 * 페이지네이션된 정책 정보와 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>정책 목록</strong>: 권한 정책들의 상세 정보</li>
 *   <li><strong>페이지네이션</strong>: 페이지 메타데이터</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>정책 목록 조회 API 응답</li>
 *   <li>정책 검색 결과 반환</li>
 *   <li>정책 관리 화면 데이터 제공</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * PolicyPayload payload = PolicyPayload.builder()
 *     .policies(policyList)
 *     .pagination(paginationData)
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see Policy 정책 정보
 * @see Pagination 페이지네이션
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 정책 페이로드 정보",
    example = """
        {
          "policies": [
            {
              "id": "policy-123",
              "name": "Default Access Policy",
              "description": "기본 접근 권한 정책"
            }
          ],
          "pagination": {
            "current_page": 1,
            "per_page": 20,
            "total": 100,
            "total_pages": 5
          }
        }
        """
)
public class PolicyPayload {
    
    /**
     * 정책 목록
     * 
     * <p>검색 조건에 해당하는 정책들의 상세 정보 목록입니다.
     * 각 정책은 ID, 이름, 설명 등의 메타데이터를 포함합니다.</p>
     */
    @JsonProperty("policies")
    @Schema(
        description = "정책 정보 목록",
        example = """
            [
              {
                "id": "policy-123",
                "name": "Default Access Policy",
                "description": "기본 접근 권한 정책"
              }
            ]
            """
    )
    private List<Policy> policies;
    
    /**
     * 페이지네이션 메타데이터
     * 
     * <p>정책 목록 조회에 대한 페이지 정보입니다.
     * 현재 페이지, 페이지 크기, 전체 개수 등의 메타데이터를 포함합니다.</p>
     */
    @JsonProperty("pagination")
    @Schema(
        description = "페이지네이션 메타데이터",
        example = """
            {
              "current_page": 1,
              "per_page": 20,
              "total": 100,
              "total_pages": 5
            }
            """
    )
    private Pagination pagination;
}