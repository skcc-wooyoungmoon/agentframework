package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 사용자 목록 조회 응답 DTO
 * 
 * <p>시스템의 사용자 목록을 페이징 형태로 조회할 때 반환되는 데이터 구조입니다.
 * 사용자 관리, 권한 할당, 프로젝트 멤버 관리 등의 기능에서 활용됩니다.</p>
 * 
 * <h3>사용자 정보 포함:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 사용자 ID, 이름, 이메일 등</li>
 *   <li><strong>상태 정보</strong>: 활성화 여부, 마지막 로그인 등</li>
 *   <li><strong>권한 정보</strong>: 시스템 역할, 그룹 소속 등</li>
 *   <li><strong>메타데이터</strong>: 생성 일시, 수정 일시 등</li>
 * </ul>
 * 
 * <h3>검색 및 필터링:</h3>
 * <ul>
 *   <li><strong>이름 검색</strong>: 사용자 이름으로 검색 가능</li>
 *   <li><strong>이메일 검색</strong>: 이메일 주소로 검색 가능</li>
 *   <li><strong>상태 필터</strong>: 활성/비활성 사용자 필터링</li>
 *   <li><strong>역할 필터</strong>: 특정 역할을 가진 사용자 조회</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>사용자 관리 대시보드</li>
 *   <li>프로젝트 멤버 초대 시 사용자 선택</li>
 *   <li>권한 관리 및 역할 할당</li>
 *   <li>사용자 현황 모니터링</li>
 * </ul>
 * 
 * <h3>응답 예시:</h3>
 * <pre>
 * {
 *   "payload": [
 *     {
 *       "id": "user-123",
 *       "username": "john.doe",
 *       "email": "john@example.com",
 *       "full_name": "John Doe",
 *       "is_active": true,
 *       "last_login": "2025-08-15T10:30:00Z",
 *       "created_at": "2025-08-01T09:00:00Z"
 *     },
 *     {
 *       "id": "user-456",
 *       "username": "jane.smith",
 *       "email": "jane@example.com",
 *       "full_name": "Jane Smith",
 *       "is_active": true,
 *       "last_login": "2025-08-15T14:20:00Z",
 *       "created_at": "2025-08-05T11:30:00Z"
 *     }
 *   ],
 *   "pagination": {
 *     "page": 1,
 *     "size": 20,
 *     "total": 150,
 *     "totalPages": 8
 *   }
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see Pagination 페이징 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 목록 조회 응답",
    example = """
        {
          "payload": [
            {
              "id": "user-123",
              "username": "john.doe",
              "email": "john@example.com",
              "full_name": "John Doe",
              "is_active": true,
              "last_login": "2025-08-15T10:30:00Z",
              "created_at": "2025-08-01T09:00:00Z"
            },
            {
              "id": "user-456",
              "username": "jane.smith",
              "email": "jane@example.com",
              "full_name": "Jane Smith",
              "is_active": true,
              "last_login": "2025-08-15T14:20:00Z",
              "created_at": "2025-08-05T11:30:00Z"
            }
          ],
          "pagination": {
            "page": 1,
            "size": 20,
            "total": 150,
            "totalPages": 8
          }
        }
        """
)
public class UsersRead {
    
    /**
     * 사용자 정보 목록
     * 
     * <p>현재 페이지에 포함된 사용자 정보의 배열입니다.
     * 각 사용자는 기본 정보, 상태, 권한 등의 정보를 포함합니다.</p>
     * 
     * @implNote 사용자 정보는 개인정보 보호를 위해 필요한 최소한의 정보만 포함됩니다.
     * @apiNote 사용자가 없는 경우 빈 배열이 반환됩니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "사용자 정보 목록",
        example = """
            [
              {
                "id": "user-123",
                "username": "john.doe",
                "email": "john@example.com",
                "full_name": "John Doe",
                "is_active": true,
                "last_login": "2025-08-15T10:30:00Z",
                "created_at": "2025-08-01T09:00:00Z"
              }
            ]
            """
    )
    private List<UserBase> data;

    /**
     * 페이징 정보
     *
     * <p>현재 조회 결과의 페이징 관련 메타데이터입니다.
     * 전체 사용자 수, 현재 페이지, 페이지 크기, 총 페이지 수 등을 포함합니다.</p>
     *
     * @apiNote 대용량 사용자 데이터를 효율적으로 처리하기 위한 페이징 정보를 제공합니다.
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이로드",
        required = true
    )
    private Payload payload;
}
