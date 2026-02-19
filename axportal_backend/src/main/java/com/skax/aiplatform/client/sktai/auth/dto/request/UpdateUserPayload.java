package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 사용자 정보 수정 요청 DTO
 * 
 * <p>SKTAI Auth API의 "/api/v1/users/{user_id}" PUT 엔드포인트 요청을 위한 DTO입니다.
 * 기존 사용자의 정보를 수정할 때 사용되며, 모든 필드는 선택적입니다.</p>
 * 
 * <h3>수정 가능한 정보:</h3>
 * <ul>
 *   <li><strong>email</strong>: 사용자 이메일 주소</li>
 *   <li><strong>first_name</strong>: 사용자 이름</li>
 *   <li><strong>last_name</strong>: 사용자 성</li>
 * </ul>
 * 
 * <h3>수정 불가능한 정보:</h3>
 * <ul>
 *   <li><strong>username</strong>: 사용자명은 생성 후 변경 불가</li>
 *   <li><strong>password</strong>: 별도 비밀번호 변경 API 사용</li>
 * </ul>
 * 
 * <h3>부분 업데이트:</h3>
 * <ul>
 *   <li>null이 아닌 필드만 업데이트됩니다</li>
 *   <li>빈 문자열("")은 해당 필드를 비우는 것으로 처리될 수 있습니다</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-16
 * @version 1.0
 * @see UserBase 사용자 수정 후 응답 구조 참고
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 정보 수정 요청",
    example = """
        {
          "email": "john.doe.updated@company.com",
          "first_name": "John",
          "last_name": "Doe"
        }
        """
)
public class UpdateUserPayload {
    
    /**
     * 이메일 주소
     * 
     * <p>수정할 사용자의 이메일 주소입니다.
     * 유효한 이메일 형식이어야 하며, 시스템 내에서 중복되지 않아야 합니다.</p>
     */
    @JsonProperty("email")
    @Schema(
        description = "수정할 이메일 주소", 
        example = "john.doe.updated@company.com",
        format = "email"
    )
    private String email;
    
    /**
     * 사용자 이름
     * 
     * <p>수정할 사용자의 이름(first name)입니다.
     * UI에서 사용자를 식별할 때 사용됩니다.</p>
     */
    @JsonProperty("first_name")
    @Schema(
        description = "수정할 사용자 이름", 
        example = "John",
        maxLength = 50
    )
    private String firstName;
    
    /**
     * 사용자 성
     * 
     * <p>수정할 사용자의 성(last name)입니다.
     * UI에서 사용자를 식별할 때 사용됩니다.</p>
     */
    @JsonProperty("last_name")
    @Schema(
        description = "수정할 사용자 성", 
        example = "Doe",
        maxLength = 50
    )
    private String lastName;
}
