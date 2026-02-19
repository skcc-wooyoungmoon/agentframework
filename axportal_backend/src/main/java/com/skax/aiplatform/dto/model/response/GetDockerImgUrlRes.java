package com.skax.aiplatform.dto.model.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 도커 이미지 URL 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "도커 이미지 URL 조회 응답")
public class GetDockerImgUrlRes {

    @Schema(description = "시퀀스 번호")
    private Long seqNo;

    @Schema(description = "SYSTEM 유형값")
    private String sysUV;

    @Schema(description = "이미지 URL")
    private String imgUrl;

    @Schema(description = "삭제 여부")
    private Integer delYn;

    @Schema(description = "최초 생성일시")
    private LocalDateTime fstCreatedAt;

    @Schema(description = "생성자")
    private String createdBy;

    @Schema(description = "최종 수정일시")
    private LocalDateTime lstUpdatedAt;

    @Schema(description = "수정자")
    private String updatedBy;
}
