package com.skax.aiplatform.dto.notice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공지사항 기본 정보")
public class GetNoticeRes {
    @Schema(description = "공지 ID")
    private String id;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "유형", example = "공지사항|FAQ|업데이트|시스템|서비스|보안|교육|정책")
    private String type;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "생성일시", example = "2025-09-29T10:20:30Z")
    private String createdDate;

    @Schema(description = "수정일시", example = "2025-09-29T10:20:30Z")
    private String modifiedDate;
}
