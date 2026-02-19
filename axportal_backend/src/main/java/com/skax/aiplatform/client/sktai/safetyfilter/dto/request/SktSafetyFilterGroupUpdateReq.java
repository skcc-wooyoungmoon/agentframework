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
 * SKTAI SafetyFilter 그룹 수정 요청 DTO
 *
 * <p>기존 SafetyFilter 그룹의 정보를 수정하기 위한 요청 데이터입니다.
 * 현재는 그룹 이름만 수정 가능합니다.</p>
 *
 * <h3>수정 가능한 정보:</h3>
 * <ul>
 *   <li><strong>name</strong>: 그룹 이름 변경</li>
 * </ul>
 *
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>그룹 ID는 변경할 수 없습니다</li>
 *   <li>그룹에 속한 필터들은 영향받지 않습니다</li>
 *   <li>이름 변경 시 중복 검사가 수행됩니다</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * SafetyFilterGroupUpdate request = SafetyFilterGroupUpdate.builder()
 *     .name("수정된 그룹 이름")
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
        description = "SKTAI SafetyFilter 그룹 수정 요청 정보",
        example = """
                {
                  "name": "수정된 그룹 이름"
                }
                """
)
public class SktSafetyFilterGroupUpdateReq {

    /**
     * 수정할 그룹 이름
     *
     * <p>변경하고자 하는 새로운 그룹 이름입니다.
     * null이 아닌 경우에만 업데이트가 수행됩니다.</p>
     *
     * @implNote 최대 255자까지 허용되며, null 값은 업데이트를 수행하지 않음을 의미합니다.
     */
    @NotBlank(message = "그룹 이름은 필수입니다")
    @Size(max = 255, message = "그룹 이름은 255자를 초과할 수 없습니다")
    @JsonProperty("name")
    @Schema(
            description = "변경할 안전 필터 그룹 이름",
            example = "수정된 그룹 이름",
            maxLength = 255
    )
    private String name;

    public static SktSafetyFilterGroupUpdateReq from(String name) {
        return SktSafetyFilterGroupUpdateReq.builder()
                .name(name.trim())
                .build();
    }

}
