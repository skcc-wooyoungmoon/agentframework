package com.skax.aiplatform.dto.home.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "IDE 목록 응답")
public class IdeListRes {
    private boolean inUse;          // 하나라도 있으면 true
    private int total;              // 전체 건수
    private List<IdeItemRes> items;     // 실제 IDE 리스트
}
