package com.skax.aiplatform.service.deploy.impl;

import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyDeleteRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRegistRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyRescheduleRequest;
import com.skax.aiplatform.client.ione.apikey.dto.request.IntfOpenApiKeyUpdateRequest;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyListResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyRegistResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyUpdateResult;
import com.skax.aiplatform.client.ione.apikey.dto.response.IntfOpenApiKeyVo;
import com.skax.aiplatform.client.ione.apikey.service.IoneApiKeyService;
import com.skax.aiplatform.client.ione.common.dto.InfResponseBody;
import com.skax.aiplatform.client.ione.common.dto.IntfResultBody;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiKeyRatelimitStatistics;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.client.ione.statistics.service.IoneStatisticsService;
import com.skax.aiplatform.client.ione.system.dto.response.ApiInfoResult;
import com.skax.aiplatform.client.ione.system.service.IoneSystemService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.response.PageableInfo;
import com.skax.aiplatform.dto.auth.response.ProjectInfoRes;
import com.skax.aiplatform.dto.auth.response.UsersMeRes;
import com.skax.aiplatform.dto.deploy.common.ApiKeyQuota;
import com.skax.aiplatform.dto.deploy.request.CreateApiKeyReq;
import com.skax.aiplatform.dto.deploy.request.GetApiKeyListReq;
import com.skax.aiplatform.dto.deploy.request.GetApiKeyStaticReq;
import com.skax.aiplatform.dto.deploy.request.UpdateApiKeyQuotaReq;
import com.skax.aiplatform.dto.deploy.response.GetApiKeyRes;
import com.skax.aiplatform.service.auth.UsersService;
import com.skax.aiplatform.service.deploy.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private static final String PROJECT_ADMIN_ROLE_SEQ = "-299"; // 프로젝트 관리자 역할 고정 SEQ
    private static final String PORTAL_ADMIN_ROLE_SEQ = "-199"; // 포탈 관리자 역할 고정 SEQ

    private final IoneApiKeyService ioneApiKeyService;
    private final IoneStatisticsService ioneStatisticsService;
    private final UsersService usersService;
    private final IoneSystemService ioneSystemService;

    private enum ReplenishIntervalType {
        // Y("Y"), // 사용 불가
        M("M"),
        W("W"),
        D("D"),
        HR("HR"),
        MIN("MIN");

        private String value;

        ReplenishIntervalType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public PageResponse<GetApiKeyRes> getApiKeys(String type, GetApiKeyListReq request) throws Exception {
        log.info("API Key 목록 조회 요청: {}, {}", type, request);

        // 분기 처리
        IntfOpenApiKeyListResult result = type.equals("USER") ? getUserApiKeys(request) : getAdminApiKeys(request);

        // Null 체크 추가 - NPE 방지
        List<IntfOpenApiKeyVo> apiKeyList = result.getApiKeyList();
        if (apiKeyList == null) {
            log.warn("IONE API에서 apiKeyList가 null로 반환됨 - 빈 리스트로 처리");
            apiKeyList = new ArrayList<>();
        }

        List<GetApiKeyRes> content = apiKeyList.stream().map(intfOpenApiKeyVo -> {
                    // OpenApiKeyAlias를 "/"로 분리하여 안전하게 접근
                    String alias = intfOpenApiKeyVo.getOpenApiKeyAlias();
                    String[] aliasParts = alias != null ? alias.split("/") : new String[0];

                    int usedCount = type.equals("USER")
                            ? getApiKeyStatisticTotalCount(intfOpenApiKeyVo.getOpenApiKey(), intfOpenApiKeyVo.getReplenishIntervalType(), intfOpenApiKeyVo.getCreateDate())
                            : 0;

                    return GetApiKeyRes.builder()
                            .id(intfOpenApiKeyVo.getOpenApiKey())
                            .apiKey(intfOpenApiKeyVo.getOpenApiKey())
                            // name은 인덱스 2에 있음 (최소 3개 요소 필요)
                            .name(aliasParts.length > 2 ? aliasParts[2] : null)
                            // projectName 인덱스 1에 있음 (최소 2개 요소 필요)
                            .projectName(aliasParts.length > 1 ? aliasParts[1] : null)
                            // type은 인덱스 0에 있음 (최소 1개 요소 필요)
                            .type(aliasParts.length > 0 ? aliasParts[0] : null)
                            ////////////////
                            .expired(isExpired(intfOpenApiKeyVo.getExpireAt())) // 만료 여부 체크
                            .permission(getApiPermissionName(intfOpenApiKeyVo.getScope())) // iONE API로 권한 이름 조회
                            .usedCount(usedCount) // 호출횟수
                            .createdAt(intfOpenApiKeyVo.getCreateDate())
                            .quota(ApiKeyQuota.builder()
                                    .type(intfOpenApiKeyVo.getReplenishIntervalType())
                                    .value(intfOpenApiKeyVo.getAllowedCount() != null ? intfOpenApiKeyVo.getAllowedCount() : 0)
                                    .build())
                            .belongsTo(GetApiKeyRes.BelongsTo.builder() // 담당자
                                    .id(null)
                                    .name(null)
                                    .department(null)
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        return PageResponse.<GetApiKeyRes>builder()
                .content(content)
                .pageable(PageableInfo.builder()
                        .page(1)
                        .size(result.getTotalCount().intValue())
                        .build())
                .totalElements(result.getTotalCount().longValue())
                .totalPages((result.getTotalCount()))
                .build();
    }

    private IntfOpenApiKeyListResult getUserApiKeys(GetApiKeyListReq request) throws Exception {
        // 사용자 현재 상태 조회
        String projectId = "-999";
        String userId = null;
        UsersMeRes user = usersService.getUserInfo();
        userId = user.getUserInfo().getMemberId();

        for (ProjectInfoRes project : user.getProjectList()) {
            projectId += "," + project.getPrjSeq();
        }

        // if(!user.getActiveProject().getPrjSeq().equals("-999")) {
        // // public 제외하고 프로젝트 ID 조회
        // projectId += "," + user.getActiveProject().getPrjSeq();
        // }
        log.info("API KEY 목록 조회 projectId: {}", projectId);

        // 특정 서빙 ID로 조회
        String scope = request.getUuid() != null ? request.getUuid() + "_SVC" : null;

        // 총 갯수 조회 후 APIKEY 전체 목록 조회 요청
        IntfOpenApiKeyListResult totalCountResult = ioneApiKeyService.selectApiKeyList(0, 0, userId, projectId, scope,
                "DESC");
        log.info("API KEY 목록 총 갯수: {}", totalCountResult.getTotalCount());

        // API KEY 목록 조회
        IntfOpenApiKeyListResult result = ioneApiKeyService.selectApiKeyList(1,
                totalCountResult.getTotalCount().intValue(),
                userId, projectId, scope, "DESC");
        log.info("API KEY 목록 조회 결과: {}", result);
        return result;
    }

    private IntfOpenApiKeyListResult getAdminApiKeys(GetApiKeyListReq request) throws Exception {
        // 사용자 현재 상태 조회
        UsersMeRes user = usersService.getUserInfo();
        String projectId = null;

        // 사용자의 roleSeq 값이 -199(슈퍼 관리자) 인지 확인
        boolean isPortalAdmin = user.getProjectList() != null && user.getProjectList().stream()
                .anyMatch(project -> PORTAL_ADMIN_ROLE_SEQ.equals(project.getPrjRoleSeq()));

        // 슈퍼 관리자가 아닐 경우에만 프로젝트 관리자 권한을 가진 프로젝트 목록 조회
        if (!isPortalAdmin && user.getProjectList() != null && !user.getProjectList().isEmpty()) {
            // 사용자가 프로젝트 관리자(prjRoleSeq=-299)로 참여 중인 모든 프로젝트의 API key 조회
            List<String> projectIdList = user.getProjectList().stream()
                    .filter(project -> PROJECT_ADMIN_ROLE_SEQ.equals(project.getPrjRoleSeq()))
                    .map(ProjectInfoRes::getPrjSeq)
                    .filter(seq -> seq != null && !seq.isEmpty())
                    .collect(Collectors.toList());

            // 리스트를 콤마로 구분하여 projectId 생성
            projectId = String.join(",", projectIdList);
        }

        log.info("API KEY 목록 조회 projectId: {}", projectId);

        String scope = request.getUuid() != null ? request.getUuid() + "_SVC" : null;

        // 총 갯수 조회 후 APIKEY 전체 목록 조회 요청
        IntfOpenApiKeyListResult totalCountResult = ioneApiKeyService.selectApiKeyList(0, 0, null, projectId, scope,
                "DESC");
        log.info("API KEY 목록 총 갯수: {}", totalCountResult.getTotalCount());

        // API KEY 목록 조회
        IntfOpenApiKeyListResult result = ioneApiKeyService.selectApiKeyList(1,
                totalCountResult.getTotalCount().intValue(),
                null, projectId, scope, "DESC");
        log.info("API KEY 목록 조회 결과: {}", result);
        return result;
    }

    @Override
    public GetApiKeyRes getApiKey(String id) throws Exception {
        /**
         * API KEY 상세 조회
         */
        IntfOpenApiKeyVo intfOpenApiKeyVo = ioneApiKeyService.selectApiKey(id);
        log.info("API KEY 상세 조회 결과: {}", intfOpenApiKeyVo);

        int apiKeyStatistics = getApiKeyStatisticTotalCount(id, intfOpenApiKeyVo.getReplenishIntervalType(), intfOpenApiKeyVo.getCreateDate());

        String partnerName = "";
        String partnerDepartment = "";
        UsersMeRes user = usersService.getUserInfo(intfOpenApiKeyVo.getPartnerId());
        partnerName = user.getUserInfo().getJkwNm();
        partnerDepartment = user.getUserInfo().getDeptNm();

        GetApiKeyRes result = GetApiKeyRes.builder()
                .id(intfOpenApiKeyVo.getOpenApiKey())
                .apiKey(intfOpenApiKeyVo.getOpenApiKey())
                .name(intfOpenApiKeyVo.getOpenApiKeyAlias().length() > 2
                        ? intfOpenApiKeyVo.getOpenApiKeyAlias().split("/")[2]
                        : null)
                .projectName(intfOpenApiKeyVo.getOpenApiKeyAlias().length() > 1
                        ? intfOpenApiKeyVo.getOpenApiKeyAlias().split("/")[1]
                        : null)
                .type(intfOpenApiKeyVo.getOpenApiKeyAlias().length() > 0
                        ? intfOpenApiKeyVo.getOpenApiKeyAlias().split("/")[0]
                        : null)
                .permission(getApiPermissionName(intfOpenApiKeyVo.getScope())) // iONE API로 권한 이름 조회
                .createdAt(intfOpenApiKeyVo.getCreateDate())
                .usedCount(apiKeyStatistics)
                .expired(isExpired(intfOpenApiKeyVo.getExpireAt())) // 만료 여부 체크
                .quota(ApiKeyQuota.builder()
                        .type(intfOpenApiKeyVo.getReplenishIntervalType())
                        .value(intfOpenApiKeyVo.getAllowedCount() != null ? intfOpenApiKeyVo.getAllowedCount() : 0)
                        .build())
                .belongsTo(GetApiKeyRes.BelongsTo.builder() // 담당자
                        .id(intfOpenApiKeyVo.getPartnerId())
                        .name(partnerName)
                        .department(partnerDepartment)
                        .build())
                .build();
        return result;
    }

    /**
     * API KEY 상세 통계 조회 -> 총 카운트 조회
     */
    private int getApiKeyStatisticTotalCount(String apiKey, String replenishIntervalType, String startFrom) {
        log.info("getApiKeyStatisticTotalCount 요청: apiKey: {}, replenishIntervalType: {}, startFrom: {}", apiKey, replenishIntervalType, startFrom);
        String fromDtm = getFromDtm(replenishIntervalType, startFrom);
        String toDtm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        ;
        log.info("getApiKeyStatisticTotalCount fromDtm: {}, toDtm: {}", fromDtm, toDtm);

        List<ApiKeyRatelimitStatistics> apiKeyRatelimitStatistics = ioneStatisticsService.getApiKeyRatelimitStatistics(fromDtm, toDtm, apiKey);
        log.info("getApiKeyStatisticTotalCount 결과: {}", apiKeyRatelimitStatistics);
        return apiKeyRatelimitStatistics.stream().mapToInt(ApiKeyRatelimitStatistics::getTotalCount).sum();
    }

    /**
     * 만료 여부 체크
     */
    private boolean isExpired(String expireDate) {
        if (expireDate == null || expireDate.isEmpty()) {
            return false;
        }
        try {
            log.info("만료일: {}", expireDate);

            LocalDateTime expiry = LocalDateTime.parse(expireDate.substring(0, 19));
            return LocalDateTime.now().isAfter(expiry);
        } catch (NullPointerException e) {
            log.warn("만료일 파싱 오류: {}", expireDate);
            return false;
        }
    }

    /**
     * fromDtm, toDtm 추가
     */
    @Override
    public List<ApiStatistics> getApiKeyStatic(String id, GetApiKeyStaticReq request) {
        log.info("getApiKeyStatic 요청: {}, {}", id, request);

        String fromDtm = request.getStartDate();
        String toDtm = request.getEndDate();
        if (fromDtm.isEmpty() || toDtm.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT, "시작일 또는 종료일이 누락되었습니다.");
        }

        // 문자열을 LocalDateTime으로 변환하여 정확한 날짜 비교
        LocalDateTime fromDtmDateTime = LocalDateTime.parse(fromDtm, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        log.info("fromDtm: {}", fromDtmDateTime);

        LocalDateTime toDtmDateTime = LocalDateTime.parse(toDtm, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        log.info("toDtm: {}", toDtmDateTime);

        // 조회 기간이 30일을 초과하는지 확인
        if (fromDtmDateTime.plusDays(30).isBefore(toDtmDateTime)) {
            throw new BusinessException(ErrorCode.INVALID_PERIOD_30DAYS);
        }

        // 조회 기간이 72시간을 초과한 경우 확인
        if (fromDtmDateTime.plusHours(72).isBefore(toDtmDateTime)) {
            throw new BusinessException(ErrorCode.INVALID_PERIOD_72HOURS);
        }

        // 시작일이 종료일보다 이후인지 확인
        if (fromDtmDateTime.isAfter(toDtmDateTime)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT, "시작일이 종료일보다 이후입니다.");
        }

        List<ApiStatistics> apiResult = ioneStatisticsService.getApiKeyStatistics(fromDtm, toDtm, "HR", id);
        log.info("API KEY 상세 통계 조회 실제 결과: {}", apiResult);

        // API 결과를 시간대별로 매핑 (year-month-day-hour를 키로 사용)
        Map<String, ApiStatistics> statisticsMap = apiResult.stream()
                .filter(stat -> stat.getYear() != null && stat.getMonth() != null &&
                        stat.getDay() != null && stat.getHour() != null)
                .collect(Collectors.toMap(
                        stat -> String.format("%s-%02d-%02d-%02d",
                                stat.getYear(),
                                Integer.parseInt(stat.getMonth()),
                                Integer.parseInt(stat.getDay()),
                                Integer.parseInt(stat.getHour())),
                        stat -> stat,
                        (existing, replacement) -> existing // 중복 시 기존 값 유지
                ));

        // 시작 시간부터 종료 시간까지 1시간 단위로 리스트 생성 (종료 시간은 제외)
        List<ApiStatistics> result = new ArrayList<>();
        LocalDateTime current = fromDtmDateTime;
        boolean isEmpty = true;

        while (current.isBefore(toDtmDateTime.plusHours(1))) {
            String timeKey = String.format("%s-%02d-%02d-%02d",
                    current.getYear(),
                    current.getMonthValue(),
                    current.getDayOfMonth(),
                    current.getHour());

            // 해당 시간대의 데이터가 있으면 사용, 없으면 0으로 채운 객체 생성
            ApiStatistics stat = statisticsMap.get(timeKey);
            if (stat != null) {
                result.add(stat);
                isEmpty = false;
            } else {
                // 데이터가 없는 시간대는 0으로 채운 객체 생성
                result.add(ApiStatistics.builder()
                        .totalCount(0)
                        .succCount(0)
                        .failCount(0)
                        .resMiliSec(0)
                        .year(String.valueOf(current.getYear()))
                        .month(String.valueOf(current.getMonthValue()))
                        .day(String.valueOf(current.getDayOfMonth()))
                        .hour(String.format("%02d", current.getHour()))
                        .miniute("0")
                        .build());
            }

            // 다음 시간으로 이동
            current = current.plusHours(1);
        }

        log.info("API KEY 상세 통계 조회 결과: isEmpty: {}, result: {}", isEmpty, result);
        return isEmpty ? new ArrayList<>() : result;
    }

    @Override
    public GetApiKeyRes createApiKey(CreateApiKeyReq request) throws Exception {
        log.info("createApiKey 요청: {}", request);
        // 사용자 현재 상태 조회
        String projectId = "public";
        String projectName = "";
        String userId = null;
        String userName = null;

        UsersMeRes user = usersService.getUserInfo();
        userId = user.getUserInfo().getMemberId();
        userName = user.getUserInfo().getJkwNm();
        projectName = user.getActiveProject().getPrjNm();

        if (!projectId.equals("-999")) {
            // public 제외하고 프로젝트 ID 조회
            projectId = user.getActiveProject().getPrjSeq();
        }


        // API ID
        String apiId = request.getScope() + "-" + request.getUuid();
        
        // USE 타입일 경우
        if(request.getType().equals(CreateApiKeyReq.ApiKeyType.USE)) {
            // 사용자일 경우 사용자 이름 세팅
            request.setName(userName);

            // 발급 이력 체크
            GetApiKeyListReq checkRequest = new GetApiKeyListReq();
            checkRequest.setUuid(apiId);
            IntfOpenApiKeyListResult existingKeys = getUserApiKeys(checkRequest);

            boolean hasUseKey = false;
            if (existingKeys != null && existingKeys.getApiKeyList() != null) {
                hasUseKey = existingKeys.getApiKeyList().stream()
                        .anyMatch(key -> key.getOpenApiKeyAlias().equals(request.getType().name() + "/" + user.getActiveProject().getPrjNm() + "/" + request.getName()));
            }
            if (hasUseKey) {
                throw new BusinessException(ErrorCode.API_KEY_ALREADY_EXISTS);
            }
        }


        String alias = (request.getType() != null ? request.getType().name() : "USE") + "/" + projectName + "/"
                + request.getName();
        String scope = apiId + "_SVC";

        log.info("🔍 [DEBUG] scope: {}", scope);
        IntfOpenApiKeyRegistRequest intfOpenApiKeyRegistRequest = IntfOpenApiKeyRegistRequest.builder()
                .partnerId(userId)
                .grpId(projectId)
                .openApiKeyAlias(alias)
                .expireAt("29991231")
                .scope(Arrays.asList(scope))
                .rateLimit(IntfOpenApiKeyRegistRequest.RateLimit.builder()
                        // 시간
                        .replenishIntervalType(ReplenishIntervalType.HR.getValue())
                        // 허용 횟수
                        .allowedCount(100)
                        .build())
                .build();

        log.info("🔍 [DEBUG] iONE API 요청 데이터: {}", intfOpenApiKeyRegistRequest);

        InfResponseBody<IntfOpenApiKeyRegistResult> infResponse = ioneApiKeyService
                .issueApiKey(intfOpenApiKeyRegistRequest);

        IntfResultBody result = infResponse.getResult();
        if (result.getSuccess() != true) {
            throw new BusinessException(ErrorCode.API_KEY_CREATE_FAILED, result.getMsg().getDesc());
        }

        IntfOpenApiKeyRegistResult data = infResponse.getData();
        log.info("createApiKey 성공: {}", data);

        GetApiKeyRes response = GetApiKeyRes.builder()
                .apiKey(data.getOpenApiKey())
                .name(request.getName())
                .projectName(projectName)
                .type(request.getType().name())
                .permission(getApiPermissionName(Arrays.asList(apiId + "_SVC")))
                .createdAt(data.getCreatedAt())
                .usedCount(0)
                .expired(false)
                .quota(ApiKeyQuota.builder()
                        .type(ReplenishIntervalType.HR.getValue())
                        .value(100)
                        .build())
                .belongsTo(GetApiKeyRes.BelongsTo.builder()
                        .id(userId)
                        .name(userName)
                        .department(projectName)
                        .build())
                .build();
        return response;
    }

    @Override
    public void updateApiKeyQuota(String id, UpdateApiKeyQuotaReq request) {
        log.info("updateApiKeyQuota 요청: {}", id);

        IntfOpenApiKeyVo intfOpenApiKeyVo = ioneApiKeyService.selectApiKey(id);

        IntfOpenApiKeyUpdateRequest intfOpenApiKeyUpdateRequest = IntfOpenApiKeyUpdateRequest.builder()
                .openApiKey(id)
                .openApiKeyAlias(intfOpenApiKeyVo.getOpenApiKeyAlias())
                .partnerId(intfOpenApiKeyVo.getPartnerId())
                .grpId(intfOpenApiKeyVo.getGrpId())
                .scope(intfOpenApiKeyVo.getScope())
                .delYn("N")
                .validForDays(intfOpenApiKeyVo.getValidForDays())
                .rateLimit(IntfOpenApiKeyUpdateRequest.RateLimit.builder()
                        .replenishIntervalType(request.getQuota().getType())
                        .allowedCount(request.getQuota().getValue())
                        .build())
                .build();

        IntfOpenApiKeyUpdateResult result = ioneApiKeyService.updateApiKey(intfOpenApiKeyUpdateRequest);
        log.info("updateApiKeyQuota 성공: {}", result);
    }

    @Override
    public void updateApiKeyExpire(String id) {
        log.info("expireApiKey 요청: {}", id);

        IntfOpenApiKeyVo intfOpenApiKeyVo = ioneApiKeyService.selectApiKey(id);

        // startFrom을 ISO 8601 형식에서 yyyyMMdd 형식으로 변환
        String startFromFormatted = null;
        if (intfOpenApiKeyVo.getStartFrom() != null && !intfOpenApiKeyVo.getStartFrom().isEmpty()) {
            // ISO 8601 형식 파싱 (예: 2025-11-13T15:00:00.000+00:00)
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(intfOpenApiKeyVo.getStartFrom());
            // yyyyMMdd 형식으로 변환
            startFromFormatted = offsetDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        }

        IntfOpenApiKeyRescheduleRequest intfOpenApiKeyRescheduleRequest = IntfOpenApiKeyRescheduleRequest.builder()
                .openApiKey(id)
                .startFrom(startFromFormatted)
                .expireAt(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build();

        log.info("🔍 [DEBUG] iONE API 요청 데이터: {}", intfOpenApiKeyRescheduleRequest);

        ioneApiKeyService.rescheduleApiKey(intfOpenApiKeyRescheduleRequest);
        log.info("expireApiKey 성공");
    }

    @Override
    public void restoreApiKey(String id) {
        log.info("restoreApiKey 요청: {}", id);

        IntfOpenApiKeyVo intfOpenApiKeyVo = ioneApiKeyService.selectApiKey(id);

        // startFrom을 ISO 8601 형식에서 yyyyMMdd 형식으로 변환
        String startFromFormatted = null;
        if (intfOpenApiKeyVo.getStartFrom() != null && !intfOpenApiKeyVo.getStartFrom().isEmpty()) {
            // ISO 8601 형식 파싱 (예: 2025-11-13T15:00:00.000+00:00)
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(intfOpenApiKeyVo.getStartFrom());
            // yyyyMMdd 형식으로 변환
            startFromFormatted = offsetDateTime.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        // 만료일을 9999년 12월 31일로 설정하여 차단 해제
        IntfOpenApiKeyRescheduleRequest intfOpenApiKeyRescheduleRequest = IntfOpenApiKeyRescheduleRequest.builder()
                .openApiKey(id)
                .startFrom(startFromFormatted)
                .expireAt("99991231")
                .build();

        log.info("🔍 [DEBUG] iONE API 요청 데이터: {}", intfOpenApiKeyRescheduleRequest);

        ioneApiKeyService.rescheduleApiKey(intfOpenApiKeyRescheduleRequest);
        log.info("restoreApiKey 성공");
    }

    @Override
    public void deleteApiKey(String id) {
        log.info("deleteApiKey 요청: {}", id);

        IntfOpenApiKeyDeleteRequest deleteRequest = IntfOpenApiKeyDeleteRequest.builder()
                .openApiKey(id)
                .build();

        ioneApiKeyService.deleteApiKey(deleteRequest);
        log.info("deleteApiKey 성공: {}", id);
    }

    @Override
    public void deleteApiKeyBulk(List<String> ids) {
        log.info("deleteApiKeyBulk 요청: {}", ids);
        for (String id : ids) {
            IntfOpenApiKeyDeleteRequest deleteRequest = IntfOpenApiKeyDeleteRequest.builder()
                    .openApiKey(id)
                    .build();

            ioneApiKeyService.deleteApiKey(deleteRequest);
        }
    }

    private String getFromDtm(String replenishIntervalType, String startFrom) {
        String fromDtm = "";
        switch (replenishIntervalType) {
            case "M":
                fromDtm = LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                break;
            case "W":
                fromDtm = LocalDateTime.now().minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                break;
            case "D":
                fromDtm = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                break;
            case "HR":
                fromDtm = LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                break;
            case "MIN":
                fromDtm = LocalDateTime.now().minusMinutes(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                break;
            default:
                throw new BusinessException(ErrorCode.API_KEY_INVALID_REPLENISH_INTERVAL_TYPE, "유효하지 않은 갱신 주기입니다.");
        }

        // fromDtm과 startFrom을 비교하여 더 빠른 날짜를 사용
        if (startFrom != null && !startFrom.isEmpty()) {
            // startFrom을 yyyyMMddHHMM 형식으로 변환
            String startFromFormatted = startFrom.replaceAll("[^0-9]", "").substring(0, 12);

            // LocalDateTime으로 변환하여 정확한 날짜 비교
            LocalDateTime fromDtmDateTime = LocalDateTime.parse(fromDtm, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            LocalDateTime startFromDateTime = LocalDateTime.parse(startFromFormatted,
                    DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

            // fromDtm이 startFrom보다 빠르면 startFrom을 사용
            if (fromDtmDateTime.isBefore(startFromDateTime)) {
                fromDtm = startFromFormatted;
                log.info("fromDtm이 startFrom보다 빠름. fromDtm을 startFrom으로 변경: {} -> {}", fromDtm, startFromFormatted);
            }
        }

        return fromDtm;
    }

    /**
     * scope에서 API ID를 추출하고 iONE System API를 호출하여 API 이름(권한 이름)을 조회합니다.
     *
     * @param scope scope 리스트 (예: ["{apiId}_SVC"])
     * @return API 이름 (권한 이름), 조회 실패 시 원래 scope 값 반환
     */
    private String getApiPermissionName(List<String> scope) {
        if (scope == null || scope.isEmpty()) {
            return null;
        }

        String scopeValue = scope.get(0);
        if (scopeValue == null || scopeValue.isEmpty()) {
            return scopeValue;
        }

        // scope에서 _SVC 접미사 제거하여 apiId 추출
        // 예: "2a915f1f-ef03-4707-b064-10e56fb24dc6_SVC" ->
        // "2a915f1f-ef03-4707-b064-10e56fb24dc6"
        String apiId = scopeValue;
        if (scopeValue.endsWith("_SVC")) {
            apiId = scopeValue.substring(0, scopeValue.length() - 4);
        }

        // iONE System API로 API 정보 조회
        ApiInfoResult apiInfo = ioneSystemService.getApiInfo(apiId);

        // API 정보가 있고 apiName이 있으면 반환
        if (apiInfo != null && apiInfo.getApiName() != null) {
            log.debug("API 권한 이름 조회 성공 - apiId: {}, apiName: {}", apiId, apiInfo.getApiName());
            return apiInfo.getApiName();
        }

        log.warn("API 정보 조회 결과에 apiName이 없음 - apiId: {}", apiId);
        return scopeValue; // 원래 scope 값 반환
    }
}
