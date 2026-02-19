package com.skax.aiplatform.dto.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 관리자 정보 조회 응답 DTO
 */
@Data
@Builder
@AllArgsConstructor
@Schema(description = "관리자 정보 조회 응답")
public class ManagerInfoRes {
    
    @Schema(description = "사용자 아이디", example = "SGO1032949")
    private final String memberId;

    @Schema(description = "UUID")
    private final String uuid;

    @Schema(description = "이름", example = "김신한")
    private final String jkwNm;

    @Schema(description = "부서명", example = "AI플랫폼셀")
    private final String deptNm;

    @Schema(description = "퇴직 여부 (1: 재직, 0: 퇴직)")
    private final Integer retrJkwYn;

    // public static AuditorInfo from(GpoUsersMas user) {
    //     return from(user, null);
    // }


    // @Schema(description = "생성자 정보")
    // private AuditorInfo createdBy;

    // @Schema(description = "수정자 정보")
    // private AuditorInfo updatedBy;

    // @Getter
    // @Builder
    // public static class AuditorInfo {

    //     @Schema(description = "사용자 아이디", example = "SGO1032949")
    //     private final String memberId;

    //     @Schema(description = "UUID")
    //     private final String uuid;

    //     @Schema(description = "이름", example = "김신한")
    //     private final String jkwNm;

    //     @Schema(description = "부서명", example = "AI플랫폼셀")
    //     private final String deptNm;

    //     @Schema(description = "퇴직 여부 (1: 재직, 0: 퇴직)")
    //     private final Integer retrJkwYn;

    //     public static AuditorInfo from(GpoUsersMas user) {
    //         return from(user, null);
    //     }

    //     public static AuditorInfo from(GpoUsersMas user, String uuid) {
    //         if (user == null) {
    //             // 사용자 정보가 없어도 UUID만이라도 반환
    //             if (uuid != null && !uuid.trim().isEmpty()) {
    //                 return AuditorInfo.builder()
    //                         .uuid(uuid)
    //                         .memberId(null)
    //                         .jkwNm(null)
    //                         .deptNm(null)
    //                         .retrJkwYn(null)
    //                         .build();
    //             }
    //             return null;
    //         }
    //         return AuditorInfo.builder()
    //                 .memberId(user.getMemberId())
    //                 .uuid(user.getUuid())
    //                 .jkwNm(user.getJkwNm())
    //                 .deptNm(user.getDeptNm())
    //                 .retrJkwYn(user.getRetrJkwYn())
    //                 .build();
    //     }
    // }
}

