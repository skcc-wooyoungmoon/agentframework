package com.skax.aiplatform.dto.notice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공지사항 상세 정보")
public class GetNoticeByIdRes {
    private Long notiId;
    private String title;
    private String msg;
    private String type;
    private String useYn;
    private LocalDateTime expFrom;
    private LocalDateTime expTo;
    private LocalDateTime createAt;
    private String createBy;
    private String createByName; // 생성자 이름
    private LocalDateTime updateAt;
    private String updateBy;
    private String updateByName; // 수정자 이름

    private List<FileRes> files;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "공지사항 첨부파일 정보")
    public static class FileRes {
        private Long fileId;             // NoticeFile.fileId
        private String originalFilename; // NoticeFile.originalFilename
        private String storedFilename;   // NoticeFile.storedFilename
        private String contentType;      // NoticeFile.contentType
        private String filePath;         // NoticeFile.filePath
        private String fileSize;           // NoticeFile.fileSize
    }
}
