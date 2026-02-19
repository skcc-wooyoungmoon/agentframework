package com.skax.aiplatform.dto.vertica.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "vertica DW Account 목록 쿼리 조회 응답")

public class DwAccountByIdRes {
    private String userName;
    private String empNo;
}
