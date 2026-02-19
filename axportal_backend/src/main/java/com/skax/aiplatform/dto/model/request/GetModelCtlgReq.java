package com.skax.aiplatform.dto.model.request;

import com.skax.aiplatform.dto.common.PageableReq;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 모델 카탈로그 요청 DTO
 * 
 * <p>모델 카탈로그 요청을 위한 요청 데이터를 담는 DTO입니다.</p>
 * 
 * @author 김예리
 * @since 2025-08-19
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor  
@AllArgsConstructor
@Schema(description = "모델 카탈로그 요청")
public class GetModelCtlgReq extends PageableReq {

    @Schema(description = "모델 ID 목록", example = "1,2,3", required = false)
    private String ids;
}