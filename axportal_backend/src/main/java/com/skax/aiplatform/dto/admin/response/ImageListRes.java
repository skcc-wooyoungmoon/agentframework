package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.entity.ide.GpoIdeImageMas;
import com.skax.aiplatform.entity.ide.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "이미지 목록 응답 DTO")
@Getter
@Builder
public class ImageListRes {

    @Schema(description = "이미지 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String uuid;

    @Schema(description = "이미지 타입", example = "DOCKER")
    private ImageType imgG;

    @Schema(description = "이미지명", example = "Ubuntu 20.04")
    private String imgNm;

    @Schema(description = "이미지 상세 설명", example = "Ubuntu 20.04 LTS 기반 이미지")
    private String dtlCtnt;

    @Schema(description = "생성 일시", example = "2025-12-29 10:30:00")
    private String fstCreatedAt;

    @Schema(description = "수정 일시", example = "2025-12-29 10:30:00")
    private String lstUpdatedAt;

    public static ImageListRes of(GpoIdeImageMas image) {
        return ImageListRes.builder()
                .uuid(image.getUuid())
                .imgG(image.getImgG())
                .imgNm(image.getImgNm())
                .dtlCtnt(image.getDtlCtnt())
                .fstCreatedAt(DateUtils.toDateTimeString(image.getFstCreatedAt()))
                .lstUpdatedAt(DateUtils.toDateTimeString(image.getLstUpdatedAt()))
                .build();
    }

}
