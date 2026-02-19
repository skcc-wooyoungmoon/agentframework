package com.skax.aiplatform.client.sktai.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 사용자 비밀번호 변경 요청 DTO
 * 
 * <p>SKTAI Auth 시스템에서 사용자가 본인의 비밀번호를 변경하기 위한 요청 데이터 구조입니다.
 * 비밀번호 리셋과 달리 현재 비밀번호 확인이 필요한 보안이 강화된 변경 작업입니다.</p>
 * 
 * <h3>비밀번호 변경 vs 리셋 차이점:</h3>
 * <ul>
 *   <li><strong>변경(Change)</strong>: 현재 비밀번호 확인 필요, 사용자 본인 인증</li>
 *   <li><strong>리셋(Reset)</strong>: 관리자 권한으로 강제 변경, 현재 비밀번호 불필요</li>
 * </ul>
 * 
 * <h3>보안 요구사항:</h3>
 * <ul>
 *   <li><strong>현재 비밀번호 검증</strong>: 변경 전 현재 비밀번호 확인 필수</li>
 *   <li><strong>새 비밀번호 정책</strong>: 시스템 비밀번호 정책 준수 필요</li>
 *   <li><strong>이력 관리</strong>: 비밀번호 변경 이력 자동 기록</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * PasswordChangeRequestDto request = PasswordChangeRequestDto.builder()
 *     .currentPassword("oldPassword123!")
 *     .newPassword("newPassword456@")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see PasswordResetRequestDto 비밀번호 리셋 요청 (관리자용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 사용자 비밀번호 변경 요청 정보",
    example = """
        {
          "current_password": "oldPassword123!",
          "new_password": "newPassword456@"
        }
        """
)
public class PasswordChangeRequestDto {
    
    /**
     * 현재 비밀번호
     * 
     * <p>사용자가 현재 사용 중인 비밀번호입니다.
     * 비밀번호 변경 전 본인 인증을 위해 확인이 필요합니다.</p>
     * 
     * @apiNote 서버에서 해시 비교를 통해 현재 비밀번호의 유효성을 검증합니다.
     * @implNote 보안상 로그에 기록되지 않으며, 전송 후 즉시 메모리에서 제거됩니다.
     */
    @JsonProperty("current_password")
    @Schema(
        description = "현재 사용 중인 비밀번호 (본인 인증용)", 
        example = "oldPassword123!",
        required = true,
        format = "password"
    )
    private String currentPassword;
    
    /**
     * 새로운 비밀번호
     * 
     * <p>변경하고자 하는 새로운 비밀번호입니다.
     * 시스템 비밀번호 정책을 준수해야 합니다.</p>
     * 
     * <h4>비밀번호 정책 (일반적):</h4>
     * <ul>
     *   <li>최소 8자 이상</li>
     *   <li>영문 대소문자, 숫자, 특수문자 조합</li>
     *   <li>이전 비밀번호와 다른 값</li>
     *   <li>연속된 문자나 반복 문자 제한</li>
     * </ul>
     * 
     * @implNote 서버에서 해시화되어 저장되며, 평문은 즉시 메모리에서 제거됩니다.
     */
    @JsonProperty("new_password")
    @Schema(
        description = "새로운 비밀번호 (시스템 정책 준수 필요)", 
        example = "newPassword456@",
        required = true,
        format = "password",
        minLength = 8
    )
    private String newPassword;
}
