package com.skax.aiplatform.client.ione.apikey.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * IONE Open API Key 정보 VO
 * 
 * <p>IONE API Gateway에서 관리하는 Open API Key의 상세 정보를 담는 DTO입니다.
 * 실제 IONE API Swagger 문서의 IntfOpenApiKeyVo 스키마를 기반으로 구현되었습니다.</p>
 * 
 * <h3>Swagger 스키마 주요 필드:</h3>
 * <ul>
 *   <li><strong>openApiKey</strong>: API Key 값</li>
 *   <li><strong>scope</strong>: 인가된 scope 목록</li>
 *   <li><strong>delYn</strong>: 삭제 여부</li>
 *   <li><strong>validForDays</strong>: 유효 일수</li>
 *   <li><strong>openApiKeyAlias</strong>: API Key 별칭</li>
 *   <li><strong>startFrom</strong>: 유효 시작 일자</li>
 *   <li><strong>expireAt</strong>: 만료 일자</li>
 *   <li><strong>partnerId</strong>: 파트너 ID</li>
 *   <li><strong>grpId</strong>: 그룹 ID</li>
 *   <li><strong>replenishIntervalType</strong>: 허용 건수 갱신 주기</li>
 *   <li><strong>allowedCount</strong>: 허용 요청 건수</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 4.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "IONE Open API Key 정보",
    example = """
        {
          "openApiKey": "202501081856019122HEHDKS017641M4VU62Q4T7ZIUEHFM543",
          "scope": "[\\"api_svc_grp_id_01\\",\\"api_svc_grp_id_02\\"]",
          "delYn": "N",
          "validForDays": 365,
          "openApiKeyAlias": "apiKeyAlias",
          "startFrom": "2025-06-17T02:29:12.000+00:00",
          "expireAt": "2026-06-17T02:29:12.000+00:00",
          "partnerId": "PARTNER_01",
          "grpId": "GROUP_01",
          "replenishIntervalType": "Y",
          "allowedCount": 100
        }
        """
)
public class IntfOpenApiKeyVo {
    
    /**
     * API Key
     */
    @JsonProperty("openApiKey")
    @Schema(
        description = "API Key", 
        example = "202501081856019122HEHDKS017641M4VU62Q4T7ZIUEHFM543",
        required = true
    )
    private String openApiKey;
    
    /**
     * 해당 API Key에 인가된 scope
     * 
     * <p>사용하려고 하는 apiSvcGrpId가 scope로서 String array의 형태로 보여집니다.
     * scope 제약없이 모든 서비스 그룹에 접근할 수 있는 경우 asterisk가 String array의 형태로 보여집니다.</p>
     * 
     * @apiNote 예시: ["api_svc_grp_id_01","api_svc_grp_id_02"] 또는 ["*"]
     */
    @JsonProperty("scope")
    @Schema(
        description = "해당 API Key에 인가된 scope (배열)",
        example = "[\"api_svc_grp_id_01\",\"api_svc_grp_id_02\"]"
    )
    private List<String> scope;

    /**
     * API Key 삭제 여부
     */
    @JsonProperty("delYn")
    @Schema(description = "API Key 삭제 여부", example = "N", required = true)
    private String delYn;

    /**
     * 설정된 API Key의 유효 일수
     */
    @JsonProperty("validForDays")
    @Schema(description = "설정된 API Key의 유효 일수", example = "365")
    private Integer validForDays;

    /**
     * API Key 별칭
     */
    @JsonProperty("openApiKeyAlias")
    @Schema(description = "API Key 별칭", example = "apiKeyAlias", required = true)
    private String openApiKeyAlias;

    /**
     * API Key 유효 시작 일자
     */
    @JsonProperty("startFrom")
    @Schema(description = "API Key 유효 시작 일자", example = "2025-06-17T02:29:12.000+00:00", required = true)
    private String startFrom;

    /**
     * API Key 만료 일자
     */
    @JsonProperty("expireAt")
    @Schema(description = "API Key 만료 일자", example = "2026-06-17T02:29:12.000+00:00", required = true)
    private String expireAt;

    /**
     * API Key를 사용하는 파트너 ID
     */
    @JsonProperty("partnerId")
    @Schema(description = "API Key를 사용하는 파트너 ID", example = "PARTNER_01")
    private String partnerId;

    /**
     * API Key를 사용하는 파트너의 그룹 ID
     */
    @JsonProperty("grpId")
    @Schema(description = "API Key를 사용하는 파트너의 그룹 ID", example = "GROUP_01")
    private String grpId;

    /**
     * 생성일시
     */
    @JsonProperty("createDate")
    @Schema(description = "생성일시", example = "2025-06-17T02:29:12.000+00:00", required = true)
    private String createDate;
    
    /**
     * 허용 건수 갱신 주기(API Key RateLimit설정용)
     * 
     * <p>각 유형 별 갱신 주기는 아래와 같습니다:</p>
     * <ul>
     *   <li>Y(년) - 매 1월1일 00시</li>
     *   <li>M(월) - 매달 1일 00시</li>
     *   <li>W(주) - 매주 일요일 00시</li>
     *   <li>D(일) - 매일 00시</li>
     *   <li>HR(시) - 매 시 00분</li>
     *   <li>MIN(분) - 매 분 00초</li>
     * </ul>
     */
    @JsonProperty("replenishIntervalType")
    @Schema(
        description = "허용 건수 갱신 주기",
        allowableValues = {"MIN", "HR", "D", "W", "M", "Y"},
        examples = {"Y", "M", "W", "D", "HR", "MIN"}
    )
    private String replenishIntervalType;

    /**
     * API Key에 할당된 허용 요청 건수(API Key RateLimit설정용)
     */
    @JsonProperty("allowedCount")
    @Schema(description = "API Key에 할당된 허용 요청 건수", example = "100")
    private Integer allowedCount;

}