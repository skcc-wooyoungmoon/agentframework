package com.skax.aiplatform.dto.notice.request;

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
public class GetNoticeReq {
    @Schema(description = "검색 타입", example = "title|content|type")
    private String searchType;

    @Schema(description = "검색 값")
    private String searchValue;

    @Schema(description = "조회 일자 유형", example = "최종 수정일시")
    private String dateType;

    @Schema(description = "시작일자", example = "2025.09.01")
    private String dateFrom; // YYYY.MM.DD

    @Schema(description = "종료일자", example = "2025.09.30")
    private String dateTo;   // YYYY.MM.DD

    @Schema(description = "조건", example = "전체|시스템|서비스|보안|교육|정책")
    private String condition;

    @Schema(description = "공지 타입", example = "전체|공지사항|FAQ|업데이트")
    private String noticeType;

    @Schema(description = "정렬 조건", example = "modifiedDate,desc")
    private String sort;
}
