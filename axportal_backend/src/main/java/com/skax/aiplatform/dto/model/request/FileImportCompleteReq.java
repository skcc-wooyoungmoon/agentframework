package com.skax.aiplatform.dto.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "파일 반입 완료 요청 (form-urlencoded)")
public class FileImportCompleteReq {

    @Schema(description = "앱 번호", example = "20")
    private String appNo;

    @Schema(description = "앱 ID", example = "A000000001")
    private String appId;

    @Schema(description = "메시지 타입", example = "01")
    private String msgType;

    @Schema(description = "예약 여부", example = "N")
    private String reserveYn;

    @Schema(description = "발신자 푸시 여부", example = "N")
    private String senderPushYn;

    @Schema(description = "내용 (메시지와 파일명 포함)", example = "파일이 정상 수신되었습니다.(XXX.dat)")
    private String cnts;

    @Schema(description = "등록자 ID", example = "06217567")
    private String registerId;

    @Schema(description = "시스템 코드", example = "20001")
    private String systemCd;

    @Schema(description = "사용자 목록", example = "06140701")
    private String userList;
}

