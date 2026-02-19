package com.skax.aiplatform.dto.admin.request;

import com.skax.aiplatform.dto.admin.request.common.PageReq;
import com.skax.aiplatform.entity.ide.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchImageReq extends PageReq {

    @Schema(description = "검색어")
    private String keyword;

    @Schema(description = "이미지 구분 (=도구명)", enumAsRef = true)
    private ImageType imgG;

}
