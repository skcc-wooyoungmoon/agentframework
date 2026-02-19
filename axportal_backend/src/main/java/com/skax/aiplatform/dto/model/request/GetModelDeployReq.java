package com.skax.aiplatform.dto.model.request;

import java.util.List;

import com.skax.aiplatform.dto.common.PageableReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 모델 배포 요청 DTO
 *
 * <p>모델 배포 요청을 위한 요청 데이터를 담는 DTO입니다.</p>
 *
 * @author 김예리
 * @version 1.0.0
 * @since 2025-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "모델 배포 요청")
public class GetModelDeployReq extends PageableReq {

    @Schema(description = "배포 모델 이름 목록")
    private List<String> deployModelNames;

}
