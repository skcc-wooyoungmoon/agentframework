package com.skax.aiplatform.dto.model.request;


import com.skax.aiplatform.dto.common.PageableReq;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetModelGardenReq extends PageableReq{
    @Schema(description = "배포 유형", example = "self_hosting", allowableValues = {"self_hosting", "serverless"})
    private String dplyTyp;

    @Schema(description = "모델 타입", example = "language", allowableValues = {"language", "embedding", "image", "multimodal", "reranker", "stt", "tts", "audio", "code", "vision", "video"})
    private String type;

    @Schema(description = "모델 상태", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED", "ACTIVE", "INACTIVE", "DEPRECATED"})
    private String status;
}
