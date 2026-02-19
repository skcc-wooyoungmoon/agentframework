package com.skax.aiplatform.dto.home.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.skax.aiplatform.entity.ide.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeStatusDto {

    private String memberId;
    private String jkwNm;

    private ImageType imgG;
    private String imgNm;

    private String ideStatusId;
    private String dwAccountId;
    private BigDecimal cpuUseHaldngV;
    private BigDecimal memUseHaldngV;
    private LocalDateTime expAt;

}
