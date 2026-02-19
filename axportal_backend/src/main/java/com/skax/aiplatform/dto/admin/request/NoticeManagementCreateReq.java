package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공지사항 생성 요청 DTO
 * 
 * <p>
 * 새로운 공지사항을 생성할 때 사용되는 요청 데이터입니다.
 * </p>
 * 
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공지사항 생성 요청")
public class NoticeManagementCreateReq {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(min = 1, max = 500, message = "제목은 1자 이상 500자 이하로 입력해주세요.")
    // @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자명은 영문, 숫자, 언더스코어만 사용
    // 가능합니다.")
    @Schema(description = "제목", example = "가나다", required = true)
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Size(min = 1, max = 4000, message = "내용은 1자 이상 4000자 이하로 입력해주세요.")
    @Schema(description = "공지 내용", example = "가나다라마바사", required = true)
    private String msg;

    @Size(max = 100, message = "type은 100자 이하로 입력해주세요.")
    @Schema(description = "공지 타입", example = "일반")
    private String type;

    @Size(min = 1, max = 1, message = "사용여부 1자")
    @Schema(description = "사용여부", example = "Y or N")
    private String useYn;

    @Schema(description = "사용기간 from", example = "yyyy-mm-dd hh:mm:ss")
    private String expFrom;

    @Schema(description = "사용기간 to", example = "yyyy-mm-dd hh:mm:ss")
    private String expTo;

}
