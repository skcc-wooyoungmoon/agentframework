package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.skax.aiplatform.dto.common.PageableReq;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "모델 Provider 요청")
public class GetModelPrvdReq extends PageableReq {
    // 추가 필드 없음
}