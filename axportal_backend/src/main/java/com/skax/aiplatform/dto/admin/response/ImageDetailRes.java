package com.skax.aiplatform.dto.admin.response;

import java.time.LocalDateTime;

import com.skax.aiplatform.entity.ide.GpoIdeImageMas;
import com.skax.aiplatform.entity.ide.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * IDE 이미지 상세 조회 응답 DTO
 */
@Builder
@Getter
@Schema(title = "이미지 상세 조회 응답", description = "IDE 이미지 상세 정보")
public class ImageDetailRes {

    @Schema(description = "이미지 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;

    @Schema(description = "이미지명", example = "VSCode Python Dev")
    private String imgNm;

    @Schema(description = "이미지 설명", example = "Python 개발 환경 기본 구성")
    private String dtlCtnt;

    @Schema(description = "이미지 구분 (도구명)", example = "VSCODE", enumAsRef = true)
    private ImageType imgG;

    @Schema(description = "이미지 경로 (URL)", example = "https://registry.example.com/vscode/python:1.0")
    private String imgUrl;

    @Schema(description = "생성일시", example = "2025-12-29T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "생성자", example = "admin")
    private String createdBy;

    @Schema(description = "수정일시", example = "2025-12-29T15:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "수정자", example = "admin")
    private String updatedBy;

    public static ImageDetailRes of(GpoIdeImageMas image) {
        return ImageDetailRes.builder()
                .uuid(image.getUuid())
                .imgNm(image.getImgNm())
                .dtlCtnt(image.getDtlCtnt())
                .imgG(image.getImgG())
                .imgUrl(image.getImgUrl())
                .createdAt(image.getFstCreatedAt())
                .createdBy(image.getCreatedBy())
                .updatedAt(image.getLstUpdatedAt())
                .updatedBy(image.getUpdatedBy())
                .build();
    }

}
