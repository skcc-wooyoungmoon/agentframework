package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공지사항 관리 검색 조건")
public class NoticeManagementSearchReq {
    
    @Schema(description = "날짜 유형 (수정일시만 지원)", example = "수정일시")
    private String dateType;
    
    @Schema(description = "시작일자", example = "2025-09-10")
    private String startDate;
    
    @Schema(description = "종료일자", example = "2025-10-10")
    private String endDate;
    
    @Schema(description = "검색 타입", example = "제목|내용")
    private String searchType;
    
    @Schema(description = "검색 키워드")
    private String searchKeyword;
    
    @Schema(description = "공지사항 타입", example = "이용 가이드|공지사항|FAQ")
    private String type;
    
    @Schema(description = "게시 상태", example = "게시|미게시")
    private String status;
}

