package com.skax.aiplatform.dto.admin.response;

import com.skax.aiplatform.entity.ide.GpoIdeResourceMas;
import com.skax.aiplatform.entity.ide.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * IDE 리소스 조회 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "IDE 리소스 정보")
public class ImageResourceRes {

    @Schema(description = "이미지 구분 (도구명)", enumAsRef = true, example = "VSCODE")
    private ImageType imgG;

    @Schema(description = "이미지 생성 가능 개수", example = "10")
    private int limitCnt;

    public static ImageResourceRes of(GpoIdeResourceMas resource) {
        return ImageResourceRes.builder()
                .imgG(resource.getImgG())
                .limitCnt(resource.getLimitCnt())
                .build();
    }

}
