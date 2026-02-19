package com.skax.aiplatform.client.sktai.safetyfilter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupUpdateRes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI SafetyFilter 그룹 생성 요청 DTO
 *
 * <p>SafetyFilter 시스템에서 새로운 안전 필터 그룹을 생성하기 위한 요청 데이터입니다.
 * 그룹을 통해 관련된 안전 필터들을 조직화하고 관리할 수 있습니다.</p>
 *
 * <h3>그룹의 역할:</h3>
 * <ul>
 *   <li><strong>조직화</strong>: 관련된 안전 필터들을 묶어서 관리</li>
 *   <li><strong>일괄 처리</strong>: 그룹 단위로 불용어 추가/삭제</li>
 *   <li><strong>권한 관리</strong>: 그룹별 접근 권한 설정</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * SafetyFilterGroupCreate request = SafetyFilterGroupCreate.builder()
 *     .name("욕설 필터 그룹")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktSafetyFilterGroupUpdateRes
 * @since 2025-10-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "SKTAI SafetyFilter 그룹 생성 요청 정보",
        example = """
                {
                  "name": "욕설 필터 그룹"
                }
                """
)
public class SktSafetyFilterGroupCreateReq {

    /**
     * 그룹 이름
     *
     * <p>안전 필터 그룹의 고유한 이름입니다.
     * 그룹의 목적이나 포함된 필터의 특성을 나타내는 명확한 이름을 사용합니다.</p>
     *
     * @implNote 최대 255자까지 허용되며, 빈 문자열은 허용되지 않습니다.
     */
    @NotBlank(message = "그룹 이름은 필수입니다")
    @Size(max = 255, message = "그룹 이름은 255자를 초과할 수 없습니다")
    @JsonProperty("name")
    @Schema(
            description = "안전 필터 그룹 이름 (목적이나 특성을 나타내는 명확한 이름)",
            example = "욕설 필터 그룹",
            required = true,
            maxLength = 255
    )
    private String name;

    public static SktSafetyFilterGroupCreateReq from(String name) {
        return SktSafetyFilterGroupCreateReq.builder()
                .name(name.trim())
                .build();
    }

}
