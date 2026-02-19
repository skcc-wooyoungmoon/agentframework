package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;

import java.util.List;

/**
 * SKTAI 프로젝트 역할 매핑 목록 응답 DTO
 * 
 * <p>특정 프로젝트의 사용자-역할 매핑 정보를 페이징 형태로 조회할 때 반환되는 데이터 구조입니다.
 * 프로젝트 내 권한 관리와 사용자 접근 제어를 위한 역할 할당 현황을 제공합니다.</p>
 * 
 * <h3>역할 매핑 정보:</h3>
 * <ul>
 *   <li><strong>사용자 식별</strong>: 각 매핑에 포함된 사용자 정보</li>
 *   <li><strong>역할 정보</strong>: 할당된 역할의 상세 정보</li>
 *   <li><strong>매핑 메타데이터</strong>: 할당 일시, 할당자 등의 추가 정보</li>
 * </ul>
 * 
 * <h3>페이징 지원:</h3>
 * <ul>
 *   <li><strong>대용량 데이터 처리</strong>: 많은 사용자가 있는 프로젝트에서 효율적 조회</li>
 *   <li><strong>성능 최적화</strong>: 필요한 만큼만 데이터 로드</li>
 *   <li><strong>사용자 경험</strong>: 빠른 페이지 로딩과 부드러운 탐색</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>프로젝트 권한 관리 화면</li>
 *   <li>사용자 역할 할당 현황 확인</li>
 *   <li>권한 감사 및 리포팅</li>
 *   <li>역할 기반 접근 제어 설정</li>
 * </ul>
 * 
 * <h3>응답 예시:</h3>
 * <pre>
 * {
 *   "payload": [
 *     {
 *       "user": {
 *         "id": "user-123",
 *         "username": "john.doe",
 *         "email": "john@example.com"
 *       },
 *       "role": {
 *         "id": "role-456",
 *         "name": "project_admin",
 *         "display_name": "프로젝트 관리자"
 *       },
 *       "assigned_at": "2025-08-15T10:30:00Z",
 *       "assigned_by": "admin"
 *     }
 *   ],
 *   "pagination": {
 *     "page": 1,
 *     "size": 20,
 *     "total": 45,
 *     "totalPages": 3
 *   }
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see RoleBase 역할 기본 정보
 * @see Pagination 페이징 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 역할 매핑 목록 응답",
    example = """
        {
          "payload": [
            {
              "user": {
                "id": "user-123",
                "username": "john.doe",
                "email": "john@example.com"
              },
              "role": {
                "id": "role-456",
                "name": "project_admin",
                "display_name": "프로젝트 관리자"
              },
              "assigned_at": "2025-08-15T10:30:00Z",
              "assigned_by": "admin"
            }
          ],
          "pagination": {
            "page": 1,
            "size": 20,
            "total": 45,
            "totalPages": 3
          }
        }
        """
)
public class ProjectRoleMappingsRead {
    
    /**
     * 프로젝트 역할 매핑 목록
     * 
     * <p>현재 페이지에 포함된 사용자-역할 매핑 정보의 배열입니다.
     * 각 항목은 사용자 정보, 할당된 역할, 매핑 메타데이터를 포함합니다.</p>
     * 
     * @implNote 매핑 정보는 사용자와 역할의 관계를 나타내는 JSON 객체 형태로 제공됩니다.
     * @apiNote 빈 프로젝트의 경우 빈 배열이 반환됩니다.
     */
    @JsonProperty("payload")
    @Schema(
        description = "프로젝트 역할 매핑 정보 목록",
        example = """
            [
              {
                "user": {
                  "id": "user-123",
                  "username": "john.doe",
                  "email": "john@example.com"
                },
                "role": {
                  "id": "role-456",
                  "name": "project_admin",
                  "display_name": "프로젝트 관리자"
                },
                "assigned_at": "2025-08-15T10:30:00Z",
                "assigned_by": "admin"
              }
            ]
            """
    )
    private List<Object> payload;
    
    /**
     * 페이징 정보
     * 
     * <p>현재 조회 결과의 페이징 관련 메타데이터입니다.
     * 전체 데이터 수, 현재 페이지, 페이지 크기, 총 페이지 수 등을 포함합니다.</p>
     * 
     * @apiNote 클라이언트에서 페이지 네비게이션 UI 구성에 필요한 모든 정보를 제공합니다.
     */
    @JsonProperty("pagination")
    @Schema(
        description = "페이징 메타데이터",
        required = true
    )
    private Pagination pagination;
}
