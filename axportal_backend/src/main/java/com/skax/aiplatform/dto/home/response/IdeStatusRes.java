package com.skax.aiplatform.dto.home.response;

import java.time.LocalDateTime;

import com.skax.aiplatform.entity.ide.ImageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "사용자 IDE 목록 조회 응답")
public class IdeStatusRes {

    @Schema(description = "IDE UUID")
    private String ideUuid;

    @Schema(description = "도구명 (VSCODE, JUPYTER)")
    private ImageType imgG;

    @Schema(description = "이미지명")
    private String imgNm;

    @Schema(description = "DW 계정 ID")
    private String dwAccountId;

    @Schema(description = "만료일시")
    private LocalDateTime expAt;

    @Schema(description = "서버 URL")
    private String svrUrlNm;

    /**
     * JPQL DTO 프로젝션용 생성자
     */
    public IdeStatusRes(
            String ideUuid, String svrUrlNm, String dwAccountId,
            LocalDateTime expAt, ImageType imgG, String imgNm
    ) {
        this.ideUuid = ideUuid;
        this.svrUrlNm = svrUrlNm;
        this.dwAccountId = dwAccountId;
        this.expAt = expAt;
        this.imgG = imgG;
        this.imgNm = imgNm;
    }

}
