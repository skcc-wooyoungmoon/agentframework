package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 그룹 멤버 목록 응답 DTO
 * 
 * <p>SKTAI Auth API에서 그룹의 멤버 목록을 페이징하여 반환할 때 사용하는 데이터 구조입니다.
 * 페이지네이션 정보와 함께 멤버 목록을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>멤버 목록</strong>: 페이지별 그룹 멤버 정보</li>
 *   <li><strong>페이지네이션</strong>: 총 개수, 페이지 정보</li>
 *   <li><strong>통계 정보</strong>: 역할별 멤버 수</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 그룹 멤버 목록 응답",
    example = """
        {
          "data": [
            {
              "user_id": "user-123",
              "username": "john.doe",
              "email": "john.doe@example.com",
              "full_name": "John Doe",
              "role": "member",
              "is_active": true,
              "is_approved": true,
              "joined_at": "2025-08-22T10:30:00Z"
            }
          ],
          "pagination": {
            "page": 1,
            "size": 10,
            "total": 15,
            "total_pages": 2
          }
        }
        """
)
public class GroupMembersResponse {

    /**
     * 그룹 멤버 목록
     */
    @JsonProperty("data")
    @Schema(description = "그룹 멤버 정보 목록")
    private List<GroupMemberResponse> data;

    /**
     * 페이지네이션 정보
     */
    @JsonProperty("pagination")
    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
