package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 사용자 등록 응답 DTO
 * 
 * <p>SKTAI Auth API의 "/api/v1/users/register" 엔드포인트 응답을 위한 DTO입니다.
 * 새로운 사용자 등록 후 반환되는 사용자 정보를 담고 있습니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 사용자명, 이메일</li>
 *   <li><strong>개인 정보</strong>: 이름, 성</li>
 * </ul>
 * 
 * <h3>UserBase와의 관계:</h3>
 * <ul>
 *   <li><strong>동일한 구조</strong>: 현재는 UserBase와 동일한 필드 구성</li>
 *   <li><strong>향후 확장 가능</strong>: 등록 관련 추가 정보 포함 가능</li>
 *   <li><strong>타입 안전성</strong>: 등록 응답 전용 타입으로 명확한 구분</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li><strong>사용자 등록</strong>: POST /api/v1/users/register 응답</li>
 *   <li><strong>등록 확인</strong>: 새로 생성된 사용자 정보 반환</li>
 *   <li><strong>UI 업데이트</strong>: 등록 후 사용자 목록 갱신</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-16
 * @version 1.0
 * @see RegisterUserPayload 사용자 등록 요청 DTO
 * @see UserBase 기본 사용자 정보 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 등록 응답 정보",
    example = """
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "username": "john.doe",
          "email": "john.doe@company.com",
          "first_name": "John",
          "last_name": "Doe"
        }
        """
)
public class UserRepresentation {
    
    /**
     * 사용자 고유 식별자
     * 
     * <p>새로 생성된 사용자의 고유 식별자입니다.
     * 시스템에서 자동으로 생성되는 UUID입니다.</p>
     */
    @JsonProperty("id")
    @Schema(
        description = "새로 생성된 사용자의 고유 식별자 (UUID)", 
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid"
    )
    private String id;
    
    /**
     * 사용자명
     * 
     * <p>등록 요청에서 제공된 사용자명입니다.
     * 등록이 성공했음을 확인할 수 있습니다.</p>
     */
    @JsonProperty("username")
    @Schema(
        description = "등록된 사용자명", 
        example = "john.doe",
        minLength = 3,
        maxLength = 50
    )
    private String username;
    
    /**
     * 이메일 주소
     * 
     * <p>등록 요청에서 제공된 이메일 주소입니다.
     * null인 경우 이메일을 제공하지 않고 등록한 것입니다.</p>
     */
    @JsonProperty("email")
    @Schema(
        description = "등록된 이메일 주소", 
        example = "john.doe@company.com",
        format = "email"
    )
    private String email;
    
    /**
     * 사용자 이름
     * 
     * <p>등록 요청에서 제공된 사용자의 이름입니다.
     * null인 경우 이름을 제공하지 않고 등록한 것입니다.</p>
     */
    @JsonProperty("first_name")
    @Schema(
        description = "등록된 사용자 이름", 
        example = "John",
        maxLength = 50
    )
    private String firstName;
    
    /**
     * 사용자 성
     * 
     * <p>등록 요청에서 제공된 사용자의 성입니다.
     * null인 경우 성을 제공하지 않고 등록한 것입니다.</p>
     */
    @JsonProperty("last_name")
    @Schema(
        description = "등록된 사용자 성", 
        example = "Doe",
        maxLength = 50
    )
    private String lastName;
}
