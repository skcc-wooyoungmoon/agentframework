package com.skax.aiplatform.dto.model.request;

import com.skax.aiplatform.dto.common.PageableReq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 모델 서빙 목록 조회 요청 DTO
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "모델 서빙 목록 조회 요청")
public class GetModelServingReq extends PageableReq {

    @Schema(description = "서빙 ID 목록", example = "id1,id2", required = false)
    private String ids;
}

