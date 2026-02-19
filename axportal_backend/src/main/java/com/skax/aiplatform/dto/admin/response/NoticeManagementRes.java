package com.skax.aiplatform.dto.admin.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import lombok.*;

@Schema(description = "공지사항 응답 DTO")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NoticeManagementRes {

    @Schema(description = "공지사항 ID", example = "1")
    private Long notiId;

    @Schema(description = "공지사항 title", example = "제목")
    private String title;

    @Schema(description = "공지사항 내용", example = "내용")
    private String msg;

    @Schema(description = "공지사항 타입", example = "긴급/보통")
    private String type;

    @Schema(description = "공지사항 사용여부", example = "Y/N")
    private String useYn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "공지사항 공지기간", example = "2025-08-03 10:30:00")
    private LocalDateTime expFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "공지사항 공지기간", example = "2025-08-03 10:30:00")
    private LocalDateTime expTo;

    @Schema(description = "첨부파일 목록")
    private List<NoticeFileRes> files;

    // 공통 필드들 직접 정의
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성일시", example = "2025-08-03 10:30:00")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정일시", example = "2025-08-03 10:30:00")
    private LocalDateTime updateAt;

    @Schema(description = "생성자", example = "admin")
    private String createBy;

    @Schema(description = "수정자", example = "admin")
    private String updateBy;

    // 담당자 정보 필드 추가
    @Schema(description = "생성자 한글명", example = "김철수")
    private String createdByName;

    @Schema(description = "생성자 부서명", example = "IT개발팀")
    private String createdByDepts;

    @Schema(description = "생성자 직급명", example = "팀장")
    private String createdByPos;

    @Schema(description = "수정자 한글명", example = "이영희")
    private String updatedByName;

    @Schema(description = "수정자 부서명", example = "IT개발팀")
    private String updatedByDepts;

    @Schema(description = "수정자 직급명", example = "대리")
    private String updatedByPos;

}
