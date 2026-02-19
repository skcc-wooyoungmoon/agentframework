package com.skax.aiplatform.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data   
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 사용량 관리 요청")
public class UserUsageMgmtReq {
    
    
    private String id;

    @Schema(description = "사용자명", example = "강신한")
    private String userName;

    @Schema(description = "프로젝트명", example = "데이터분석")
    private String projectName;

    @Schema(description = "역할명", example = "관리자")
    private String roleName;

    @Schema(description = "메뉴경로 (프론트엔드 URL을 한글로 변환, 매핑 없으면 URL 그대로)", 
            example = "홈 > 대시보드")
    private String menuPath;
    
    @Schema(description = "액션", example = "생성, 수정, 삭제, 조회")
    private String action;

    @Schema(description = "대상자산", example = "데이터세트")
    private String targetAsset;

    @Schema(description = "리소스타입", example = "데이터세트")
    private String resourceType;    

    @Schema(description = "API엔드포인트", example = "http://localhost:8080/api/v1/data/dataset")
    private String apiEndpoint;

    @Schema(description = "에러 코드", example = "E001, E002, E003")
    private String errCode;

    @Schema(description = "클라이언트 IP", example = "192.168.1.100")
    private String clientIp;

    @Schema(description = "사용자 에이전트(접속 환경)", example = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
    private String userAgent;

    @Schema(description = "요청 내용 (POST/PUT/DELETE 시)", example = "{\"name\":\"test\",\"value\":\"data\"}")
    private String requestContent;

    @Schema(description = "요청 상세 내용 1 (1000자)", example = "")
    private String firstRequestDetail;

    @Schema(description = "요청 상세 내용 2 (1000자)", example = "")
    private String secondRequestDetail;

    @Schema(description = "요청 상세 내용 3 (1000자)", example = "")
    private String thirdRequestDetail;

    @Schema(description = "요청 상세 내용 4 (1000자)", example = "")
    private String fourthRequestDetail;

    @Schema(description = "응답 내용 (POST/PUT/DELETE 시)", example = "{\"id\":123,\"status\":\"success\"}")
    private String responseContent;

    @Schema(description = "응답 상세 내용 1 (1000자)", example = "")
    private String firstResponseDetail;

    @Schema(description = "응답 상세 내용 2 (1000자)", example = "")
    private String secondResponseDetail;

    @Schema(description = "응답 상세 내용 3 (1000자)", example = "")
    private String thirdResponseDetail;

    @Schema(description = "응답 상세 내용 4 (1000자)", example = "")
    private String fourthResponseDetail;

    @Schema(description = "생성일시", example = "2025-01-01 00:00:00")
    private String createdAt;
    
    @Schema(description = "생성자", example = "admin")
    private String createdBy;

}
