package com.skax.aiplatform.dto.model.request;

import com.skax.aiplatform.common.response.PageableInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "모델 사용 가능 모델 조회 요청")
public class GetAvailableModelReq extends PageableInfo{
    
    @Schema(description = "검색 키워드", example = "model")
    private String search;
}
