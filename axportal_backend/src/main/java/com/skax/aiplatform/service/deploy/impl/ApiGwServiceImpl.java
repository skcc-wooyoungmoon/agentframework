package com.skax.aiplatform.service.deploy.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.ione.api.dto.request.ApiDeleteRequest;
import com.skax.aiplatform.client.ione.api.dto.request.ApiRegistRequest;
import com.skax.aiplatform.client.ione.api.dto.response.ApiInfoResult;
import com.skax.aiplatform.client.ione.api.dto.response.ApiRegistResponse;
import com.skax.aiplatform.client.ione.api.dto.response.PublishWorkInfoResult;
import com.skax.aiplatform.client.ione.api.service.IoneApiService;
import com.skax.aiplatform.client.ione.statistics.dto.response.ApiStatistics;
import com.skax.aiplatform.client.ione.statistics.service.IoneStatisticsService;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppApiKeysResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentAppsService;
import com.skax.aiplatform.client.sktai.serving.dto.request.ApiKeyCreate;
import com.skax.aiplatform.client.sktai.serving.dto.response.ApiKeyResponse;
import com.skax.aiplatform.client.sktai.serving.service.SktaiServingService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.auth.response.UsersMeRes;
import com.skax.aiplatform.dto.deploy.request.CreateApiReq;
import com.skax.aiplatform.dto.deploy.request.GetApiKeyListReq;
import com.skax.aiplatform.dto.deploy.response.CreateApiRes;
import com.skax.aiplatform.dto.deploy.response.GetApiEndpointStatus;
import com.skax.aiplatform.dto.deploy.response.GetApiKeyRes;
import com.skax.aiplatform.entity.deploy.GpoApigwMas;
import com.skax.aiplatform.repository.deploy.GpoApigwMasRepository;
import com.skax.aiplatform.service.auth.UsersService;
import com.skax.aiplatform.service.deploy.ApiGwService;
import com.skax.aiplatform.service.deploy.ApiKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("apiGwService")
@RequiredArgsConstructor
public class ApiGwServiceImpl implements ApiGwService {

    private final IoneApiService ioneApiService;
    private final IoneStatisticsService ioneStatisticsService;
    private final UsersService usersService;
    private final SktaiAgentAppsService sktaiAgentAppsService;
    private final SktaiServingService sktaiServingService;
    private final GpoApigwMasRepository gpoApigwMasRepository;
    private final ApiKeyService apiKeyService;

    @Override
    public CreateApiRes createApiEndpoint(CreateApiReq request) {
        // 사용자 현재 프로젝트 조회
        String projectId = request.getProjectId();

        if(projectId == null || projectId.isEmpty() || projectId.equals("")){
            try {
                UsersMeRes user = usersService.getUserInfo();
                projectId = user.getActiveProject().getPrjSeq();
                if (projectId.equals("-999")) {
                    // public 프로젝트는 공개 프로젝트이므로 public 프로젝트 ID로 설정
                    projectId = "public";
                } else {
                    projectId = projectId.toString();
                }
            } catch (Exception e) {
                log.error("프로젝트 ID 조회 실패: {}", e.getMessage());
                throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT, "프로젝트 ID를 찾을 수 없습니다.");
            }
        }

        if (projectId.equals("-999")) {
            // public 프로젝트는 공개 프로젝트이므로 public 프로젝트 ID로 설정
            projectId = "public";
        } else {
            projectId = projectId.toString();
        }

        String svrGrpId;
        String apiKey;
        String apiId = request.getType() + "-" + request.getUuid();

        if (request.getType().equals("model")) {
            // API 키 유효 기간 설정 
            ApiKeyCreate apiKeyCreate = ApiKeyCreate.builder()
                    .servingId(Arrays.asList(request.getUuid()))
                    .gatewayType("model")
                    .isActive(true)
                    .isMaster(false)
                    // .startedAt(today.format(dateFormatter))
                    // .expiresAt(expiresDate.format(dateFormatter))
                    .tags(new ArrayList<>())  // tags 필드를 빈 리스트로 설정
                    .build();
            ApiKeyResponse apiKeyResponse = sktaiServingService.createApiKey(apiKeyCreate);
            apiKey = apiKeyResponse.getApiKey();
            log.info("API KEY 생성 성공 apiKeyResponse : {}", apiKeyResponse);
            log.info("API KEY 생성 성공 apiKey: {}", apiKey);

            svrGrpId = "MODEL_GATEWAY";
        } else {
            log.debug("Agent App API 키 목록 조회 요청 - appId: {}", request.getUuid());
            AppApiKeysResponse response = sktaiAgentAppsService.getAppApiKeys(request.getUuid());
            log.debug("Agent App API 키 목록 조회 응답: {}", response);

            apiKey = response.getData().get(0);
            svrGrpId = "AGENT_GATEWAY";
        }

        // 설정
        Integer stripPrefix = request.getType().equals("model") ? 3 : 2;
        String prefixPath = request.getType().equals("model") ? "/api/v1/gateway" : "/api/v1/agent_gateway";

        ApiRegistRequest apiRegistRequest = ApiRegistRequest.builder()
                //////// API ID : 에셋 타입-배포 ID
                .apiId(apiId)
                .apiName(request.getName())
                .apiDesc(request.getDescription())
                //////// 업무 코드와 프로젝트 ID 통일
                .apiTaskId(projectId)
                //////// apigw path : /에셋 타입/배포 ID
                .predicates(ApiRegistRequest.Predicates.builder()
                        .path("/" + request.getType() + "/" + request.getUuid() + "/**") // ** 추가
                        .build())
                .mediator(Arrays.asList(ApiRegistRequest.Mediator.builder()
                        .filterName("SetRequestHeaderOption")
                        .filterKey("Authorization")
                        .filterValue("Bearer " + apiKey)
                        // .encode(false)
                        .build()))
                //////// target
                .route(ApiRegistRequest.Route.builder()
                        .apiSvrGrpId(svrGrpId)
                        // .path("/"+request.getUuid()) // 필요 없음....
                        // .port("8080")
                        .stripPrefix(stripPrefix)
                        .prefixPath(prefixPath)
                        .build())
                .auth(ApiRegistRequest.Auth.builder()
                        .type(13)
                        .build())
                // .traffic(ApiRegistRequest.Traffic.builder()
                // .connectionTimeout(10000)
                // .responseTimeout(10001)
                // .build())
                .build();

        ApiRegistResponse response = null;
        try {
            response = ioneApiService.registApi(apiRegistRequest);
            log.info("API 생성 응답: {}", response);
        } catch (Exception e) {
            log.warn("iONE API 등록 요청 실패 (타임아웃 등) - 계속 진행합니다. 오류: {}", e.getMessage());
            // 타임아웃이나 연결 실패 시에도 계속 진행
            return CreateApiRes.builder()
                    .infWorkStatus("PENDING")
                    .infWorkMsg("iONE API 등록 요청이 지연되고 있습니다. 잠시 후 상태를 확인해주세요. (" + e.getMessage() + ")")
                    .infWorkSeq(null)
                    .build();
        }

        log.info("API 생성 TASK ID: {}", response.getInfWorkSeq());
        GpoApigwMas gpoApigwMas = GpoApigwMas.builder()
                .gpoApiId(request.getUuid())
                .gpoTskId(response.getInfWorkSeq())
                .build();
        gpoApigwMasRepository.save(gpoApigwMas);
        log.info("API 생성 TASK ID 저장: {}", response.getInfWorkSeq());

        return CreateApiRes.builder()
                .infWorkStatus(response.getInfWorkStatus())
                .infWorkMsg(response.getInfWorkMsg())
                .infWorkSeq(response.getInfWorkSeq())
                .build();
    }

    @Override
    public GetApiEndpointStatus checkApiEndpoint(String apiId) {
        log.info("API 엔드포인트 발급 상태 조회 : apiId={}", apiId);
        // apiId 형식: ${type}-${id} 에서 id 부분만 추출
        String uuid = apiId.split("-", 2)[1];

        log.info("API 엔드포인트 발급 key ID: {}", uuid);
        GpoApigwMas gpoApigwMas = gpoApigwMasRepository.findByGpoApiId(uuid);
        log.info("API 엔드포인트 발급 GPO APIGW MAS: {}", gpoApigwMas);

        // TODO 정상 데이터 전까지
        // if (gpoApigwMas == null) {
        //     return GetApiEndpointStatus.builder()
        //             .status("SUCCESS")
        //             .message("API ID를 찾을 수 없습니다.")
        //             .infWorkSeq(null)
        //             .build();
        // }

        GetApiEndpointStatus getApiEndpointStatus = GetApiEndpointStatus.builder()
                            .status("FAILED")
                            .message("API가 존재하지 않습니다.")
                    .build();

        if(gpoApigwMas != null) {
            log.info("API 엔드포인트 발급 APP ID: {}의 TASK ID: {}", uuid, gpoApigwMas.getGpoTskId());
            PublishWorkInfoResult result = ioneApiService.getPublishWorkInfo(gpoApigwMas.getGpoTskId());
            log.info("API 엔드포인트 발급 상태 조회 결과: {}", result);

            if(result!=null) {
                // 1 : 진행 중 , 5: 성공, 6: 실패
                getApiEndpointStatus = GetApiEndpointStatus.builder()
                        .status(result.getInfWorkStatus().equals("1") ? "PROCESSING"
                                : result.getInfWorkStatus().equals("5") ? "SUCCESS" : "FAILED")
                        .message(result.getInfWorkMsg())
                        .infWorkSeq(result.getInfWorkSeq())
                        .build();
                log.info("API 엔드포인트 조회 성공: {}", getApiEndpointStatus);

                // FAILED 상태인 경우 실제 API 존재 여부 확인
                if (getApiEndpointStatus.getStatus().equals("FAILED")) {
                    try {
                        log.info("API 엔드포인트 gpoApigwMas != null && FAILED 상태 - API 존재 여부 확인 시도: {}", apiId);
                        ApiInfoResult apiInfoResult = ioneApiService.getApiInfo(apiId);
                        log.info("API 엔드포인트 API 존재 확인 결과: {}", apiInfoResult);
                        // API가 존재하면 SUCCESS로 변경
                        log.info("API 엔드포인트 API 존재 확인됨 - 상태를 SUCCESS로 변경: {}", apiId);
                        if (apiInfoResult != null) {
                            getApiEndpointStatus = GetApiEndpointStatus.builder()
                                    .status("SUCCESS")
                                    .message("API가 실제로 존재하여 상태를 SUCCESS로 변경했습니다.")
                                    .infWorkSeq(getApiEndpointStatus.getInfWorkSeq())
                                    .build();
                        }
                        log.info("API 엔드포인트 조회 성공: {}", getApiEndpointStatus);
                    } catch (Exception e) {
                        log.warn("API 존재 여부 확인 실패 - FAILED 상태 유지: {}, error: {}", apiId, e.getMessage());
                    }
                }
            } else {
                try {
                    log.info("API 엔드포인트 gpoApigwMas != null && FAILED 상태 - API 존재 여부 확인 시도: {}", apiId);
                    ApiInfoResult apiInfoResult = ioneApiService.getApiInfo(apiId);
                    log.info("API 엔드포인트 API 존재 확인 결과: {}", apiInfoResult);
                    // API가 존재하면 SUCCESS로 변경
                    log.info("API 엔드포인트 API 존재 확인됨 - 상태를 SUCCESS로 변경: {}", apiId);
                    if (apiInfoResult != null) {
                        getApiEndpointStatus = GetApiEndpointStatus.builder()
                                .status("SUCCESS")
                                .message("API가 실제로 존재하여 상태를 SUCCESS로 변경했습니다.")
                                .infWorkSeq(getApiEndpointStatus.getInfWorkSeq())
                                .build();
                    }
                    log.info("API 엔드포인트 조회 성공: {}", getApiEndpointStatus);
                } catch (Exception e) {
                    log.warn("API 존재 여부 확인 실패 - FAILED 상태 유지: {}, error: {}", apiId, e.getMessage());
                }
            }
        } else {
            try {
                log.info("API 엔드포인트 gpoApigwMas == null && FAILED 상태 - API 존재 여부 확인 시도: {}", apiId);
                ApiInfoResult apiInfoResult = ioneApiService.getApiInfo(apiId);
                log.info(" API 엔드포인트 API 존재 확인 결과: {}", apiInfoResult);

                // API가 존재하면 SUCCESS로 변경
                log.info("API 엔드포인트 API 존재 확인됨 - 상태를 SUCCESS로 변경: {}", apiId);
                if (apiInfoResult != null) {
                    getApiEndpointStatus = GetApiEndpointStatus.builder()
                            .status("SUCCESS")
                            .message("API가 실제로 존재하여 상태를 SUCCESS로 변경했습니다.")
                            .build();
                } else {
                    getApiEndpointStatus = GetApiEndpointStatus.builder()
                            .status("FAILED")
                            .message("API가 존재하지 않습니다.")
                            .build();
                }
                log.info("API 엔드포인트 gpoApigwMas == null 조회 성공: {}", getApiEndpointStatus);
            } catch (Exception e) {
                log.warn("API 존재 여부 확인 실패 - FAILED 상태 유지: {}, error: {}", apiId, e.getMessage());
            }
        }

        return getApiEndpointStatus;
    }

    @Override
    public void postRetryApiEndpoint(String apiId) {
        log.info("API 엔드포인트 등록 재요청: {}", apiId);

        log.info("API 엔드포인트 등록 APP ID: {}", apiId);
        GpoApigwMas gpoApigwMas = gpoApigwMasRepository.findByGpoApiId(apiId);
        if (gpoApigwMas == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT, "API ID를 찾을 수 없습니다.");
        }
        log.info("API 엔드포인트 등록 TASK ID: {}", gpoApigwMas.getGpoTskId());

        ApiRegistResponse response = ioneApiService.republishWork(gpoApigwMas.getGpoTskId());
        log.info("API 엔드포인트 등록 재요청 응답: {}", response);

        gpoApigwMas.setGpoTskId(response.getInfWorkSeq());
        GpoApigwMas savedGpoApigwMas = gpoApigwMasRepository.save(gpoApigwMas);
        log.info("API 엔드포인트 등록 신규 TASK ID 저장: {}", savedGpoApigwMas);
    }

    @Override
    public List<ApiStatistics> getApiEndpointStatistics(String servingId, String startDate, String endDate) {
        log.info("API 엔드포인트 통계 요청: {}", servingId);

        String fromDtm = startDate;
        String toDtm = endDate;
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

        List<ApiStatistics> apiResult = ioneStatisticsService.getApiCallStatistics(fromDtm, toDtm, servingId, "HR");

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

        return isEmpty ? new ArrayList<>() : result;
    }

    @Override
    public void deleteApiEndpoint(String type, String servingId) {
        log.info("API 엔드포인트 삭제 요청: {}", servingId);
        String apiId = type + "-" + servingId;
        log.info("API 엔드포인트 삭제 API ID: {}", apiId);

        // API 엔드포인트 삭제
        try {
            ioneApiService.deleteApi(ApiDeleteRequest.builder()
                    .apiId(apiId)
                    .build());
        } catch (BusinessException e) {
            log.error("API 엔드포인트 삭제 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API 엔드포인트 삭제에 실패했습니다.");
        }

        // API KEY 삭제
        try {
            // 전체 목록 조회하여 삭제 처리
            PageResponse<GetApiKeyRes> pageResponse = apiKeyService.getApiKeys("ADMIN", GetApiKeyListReq.builder()
                    .uuid(apiId)
                    .build());
            List<GetApiKeyRes> apiKeys = pageResponse.getContent();
            ;
            List<String> apiKeyIds = apiKeys.stream()
                    .map(GetApiKeyRes::getApiKey)
                    .collect(Collectors.toList());
            log.info("API KEY 목록 조회 성공: {}", apiKeys);
            apiKeyService.deleteApiKeyBulk(apiKeyIds);
        } catch (BusinessException e) {
            log.error("API KEY 삭제 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "API KEY 삭제에 실패했습니다.");
        } catch (Exception e) {
            log.error("API KEY 삭제 실패: {}", e.getMessage());
        }
        log.info("API 엔드포인트 삭제 성공: {}", servingId);
    }
}