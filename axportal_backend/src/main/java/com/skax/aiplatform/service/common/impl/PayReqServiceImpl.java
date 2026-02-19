package com.skax.aiplatform.service.common.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.shinhan.ShinhanSwingClient;
import com.skax.aiplatform.client.shinhan.dto.ApprovalReq;
import com.skax.aiplatform.client.shinhan.dto.ApprovalRes;
import com.skax.aiplatform.common.constant.AlarmMessageType;
import com.skax.aiplatform.common.constant.ApprovalAlarmMessages;
import com.skax.aiplatform.common.constant.CommCode;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.auth.request.AlarmCreateRequest;
import com.skax.aiplatform.dto.common.request.*;
import com.skax.aiplatform.dto.common.response.ApprovalCallbackInfo;
import com.skax.aiplatform.dto.common.response.ApprovalCancelInfo;
import com.skax.aiplatform.dto.common.response.ApprovalResData;
import com.skax.aiplatform.dto.common.response.SwingResCommon;
import com.skax.aiplatform.dto.deploy.request.AppCreateReq;
import com.skax.aiplatform.dto.deploy.request.CreateApiKeyReq;
import com.skax.aiplatform.dto.home.request.IdeCreateReq;
import com.skax.aiplatform.dto.home.request.ProjJoinReq;
import com.skax.aiplatform.dto.home.request.ProjectCreateReq;
import com.skax.aiplatform.dto.model.request.CreateBackendAiModelDeployReq;
import com.skax.aiplatform.dto.model.request.CreateModelDeployReq;
import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.dto.model.response.CreateBackendAiModelDeployRes;
import com.skax.aiplatform.dto.model.response.CreateModelDeployRes;
import com.skax.aiplatform.entity.common.approval.GpoGyljCallbackMas;
import com.skax.aiplatform.entity.common.approval.GpoGyljMas;
import com.skax.aiplatform.enums.ModelGardenStatus;
import com.skax.aiplatform.mapper.common.ApprovalMapper;
import com.skax.aiplatform.repository.common.GpoGyljCallbackMasRepository;
import com.skax.aiplatform.repository.common.GpoGyljMasRepository;
import com.skax.aiplatform.repository.home.GpoAlarmsRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.service.common.PayReqService;
import com.skax.aiplatform.service.deploy.AgentDeployService;
import com.skax.aiplatform.service.deploy.ApiKeyService;
import com.skax.aiplatform.service.home.AlarmService;
import com.skax.aiplatform.service.home.IDEService;
import com.skax.aiplatform.service.home.ProjectService;
import com.skax.aiplatform.service.model.ModelDeployService;
import com.skax.aiplatform.service.model.ModelGardenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayReqServiceImpl implements PayReqService {
    private static final TypeReference<Map<String, Object>> STRING_OBJECT_MAP = new TypeReference<>() {
    };
    private final ShinhanSwingClient shinhanSwingClient;

    private final GpoGyljMasRepository gpoGyljMasRepository;
    private final GpoGyljCallbackMasRepository gpoGyljCallbackMasRepository;
    private final GpoPrjuserroleRepository gpoPrjuserroleMapRepository;
    private final GpoAlarmsRepository gpoAlarmsRepository;

    private final ApprovalMapper approvalMapper;

    private final AlarmService alarmService;
    private final ProjectService projectService;
    private final ApiKeyService apiKeyService;
    private final ModelDeployService modelDeployService;
    private final ModelGardenService modelGardenService;
    private final IDEService ideService;

    private final AgentDeployService agentDeployService;

    @Value("${gw.clientId}")
    private String gwClientId;

    @Value("${gw.clientSecret}")
    private String gwClientSecret;

    @Value("${gw.callbackUrl}")
    private String callBackUrl;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    /**
     * 모든 외부 요청 공통 모듈 approvalRequest feignclient 호출이 됨
     *
     * @param approvalInfo
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int approvalRequest(ApprovalInfo approvalInfo) {
        String compantCode = "SH";
        String formattedDateTime = ZonedDateTime
                .now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        ApprovalReq approvalReq = ApprovalReq.builder()
                .common(SwingReqCommon.builder()
                        .clientId(gwClientId)
                        .clientSecret(gwClientSecret)
                        .companyCode(compantCode)
                        .employeeNo(approvalInfo.getEmployeeNo())
                        .requestUniqueKey(approvalInfo.getUuid())
                        .build())
                .data(ApprovalReqData.builder()
                        .subject(approvalInfo.getSubject())
                        .draftDateTime(formattedDateTime)
                        .draftEmployeeNo(approvalInfo.getEmployeeNo())
                        .currentApprovalEmployeeNo(approvalInfo.getEmployeeNo())
                        .currentApprovalCount("1")
                        .totalApprovalCount("1")
                        .approvalSummary(approvalInfo.getApprovalSummary())
                        .approvalOpinionYn("")
                        /* approvalDocumentId 있어야할 것 같지만, 없는 상태로 동작하니 제거 */
                        // .approvalDocumentId(approvalInfo.getApprovalDataId())
                        .callBackUrl(callBackUrl)
                        .detailPageUrl("")
                        .agentUseYn("")
                        .approvalEmployees(List.of(
                                ApprovalEmployees.builder()
                                        .employeeNo(approvalInfo.getTargetEmployeeNo())
                                        .approvalCount("1")
                                        .build()))
                        .attachments(List.of()) // 빈 배열
                        .build())
                .build();

        try {
            log.debug("approvalReq >>> {} ", approvalReq);

            ApprovalRes approvalRes;

            if (List.of("edev", "elocal").contains(activeProfile)) {
                // 외부망 테스트 환경을 위해 분기처리
                approvalRes = ApprovalRes.builder()
                        .common(SwingResCommon.builder()
                                .resultCode(200)
                                .transactionId("trx-" + UUID.randomUUID())
                                .build())
                        .data(ApprovalResData.builder()
                                .approvalDocumentId(UUID.randomUUID().toString())
                                .build())
                        .build();
            } else {
                // 실제 swing 호출
                approvalRes = shinhanSwingClient.approvalRequest(approvalReq);
            }

            log.debug("approvalRes >>> {} ", approvalRes);

            // Builder 패턴을 사용하여 GpoGyljMas 객체 생성
            GpoGyljMas.GpoGyljMasBuilder builder = GpoGyljMas.builder()
                    .gyljId(approvalInfo.getUuid())
                    .gyljTtl(approvalInfo.getSubject())
                    .gyljRsn(approvalInfo.getApprovalSummary())
                    .clbkUrl(callBackUrl)
                    // dtlCtnt 컬럼에 결재경보 모두 추가
                    .dtlCtnt("%s#|#%s".formatted(approvalInfo.getApprovalDataId(),
                            approvalInfo.getPayApprovalReqString()))
                    .memberId(approvalInfo.getEmployeeNo())
                    .eroCtnt(approvalInfo.getApprovalUniqueKey())
                    .gyljjaMemberId(approvalInfo.getTargetEmployeeNo())
                    .gyljLineSno(new BigDecimal(approvalInfo.getCurrentApprovalCount()))
                    .gyljLineTotSno(new BigDecimal(approvalInfo.getMaxApprovalCount()));

            // 응답 정보 매핑
            if (approvalRes != null && approvalRes.getCommon() != null) {
                builder.gyljRespId(approvalRes.getData().getApprovalDocumentId()) // 취소요청을 위한 구분값
                        .apiRstCd(String.valueOf(approvalRes.getCommon().getResultCode()))
                        .trxId(approvalRes.getCommon().getTransactionId());

                approvalInfo.setReaultCode(String.valueOf(approvalRes.getCommon().getResultCode())); // AOP 에서 사용함
            }

            // Builder로 객체 생성
            GpoGyljMas gpoGyljMas = builder.build();

            // DB 저장
            log.debug("gpoGyljMas >>> {} ", gpoGyljMas);
            gpoGyljMasRepository.save(gpoGyljMas);

            return 0;
        } catch (BusinessException e) {
            // 비즈니스 예외는 로깅 후 실패 반환
            log.error("결재 요청 실패 (BusinessException): {}", e.getMessage(), e);
            return -1;
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("결재 요청 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            return -1;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("결재 요청 실패 (잘못된 인자): {}", e.getMessage(), e);
            return -1;
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("결재 요청 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            return -1;
        }
    }

    /* 25.11.08 박종태 */
    @Override
    public int approval(PayApprovalReq payApprovalReq) {
        List<PayApprovalReq.ApprovalTarget> approvalTargetList = payApprovalReq.getApprovalTypeInfo()
                .getApprovalTarget();
        PayApprovalReq.ApprovalTarget approvalTarget = approvalTargetList
                .get(payApprovalReq.getApprovalInfo().getCurrentApprovalCount() - 1);
        List<String> targetUserList = gpoPrjuserroleMapRepository
                .findMemberIdsByPrjSeqAndRoleSeq(approvalTarget.getPrjSeq(), approvalTarget.getRoleSeq());

        // 결재대상 validation
        if (targetUserList == null || targetUserList.isEmpty()) {
            log.error("결재 대상을 찾을 수 없습니다. [{}:{}]", approvalTarget.getPrjNm(), approvalTarget.getRoleNm());
            return -1;
        }

        String documentId = UUID.randomUUID().toString(); // 멀티요청을 묶어주는 id
        int successCount = 0;
        int failCount = 0;

        // 모든 승인권자에게 순차적으로 결재 요청
        for (String targetUser : targetUserList) {
            String uuid = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                    + String.format("%04d", java.util.concurrent.ThreadLocalRandom.current().nextInt(10000));

            try {
                ApprovalInfo approvalInfo = ApprovalInfo.builder()
                        .approvalUniqueKey(payApprovalReq.getApprovalInfo().getApprovalUniqueKey())
                        .uuid(uuid)
                        .approvalDataId(documentId)
                        .subject("[%s][%s] %s".formatted(
                                "prod".equals(activeProfile) ? "운영" : "개발",
                                truncateWithEllipsis(payApprovalReq.getApprovalInfo().getApprovalItemString(), 20),
                                payApprovalReq.getApprovalTypeInfo().getTypeNm()))
                        .employeeNo(payApprovalReq.getApprovalInfo().getMemberId())
                        .approvalSummary(truncateWithEllipsis(payApprovalReq.getApprovalInfo().getApprovalSummary(), 600))
                        .targetEmployeeNo(targetUser)
                        .currentApprovalCount(payApprovalReq.getApprovalInfo().getCurrentApprovalCount())
                        .maxApprovalCount(approvalTargetList.size())
                        .payApprovalReqString(new ObjectMapper().writeValueAsString(payApprovalReq))
                        .build();

                // 다음은 공통의 승인요청에 대한 함수 입니다. (AOP가 마지막에 실행됨)
                int returnVal = ((PayReqService) AopContext.currentProxy()).approvalRequest(approvalInfo);

                if (returnVal < 0) {
                    failCount++;
                    log.error("결재 요청 실패 [{} -> {}]", payApprovalReq.getApprovalInfo().getMemberId(), targetUser);
                } else {
                    // 결재자에게 알람
                    alarmService.createAlarm(AlarmCreateRequest.builder()
                            .alarmId(UUID.randomUUID().toString())
                            .targetUser(targetUser)
                            .title("[%s] %s".formatted(
                                    payApprovalReq.getApprovalInfo().getApprovalItemString(),
                                    ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                                            .get(payApprovalReq.getApprovalInfo().getApprovalType())
                                            .get(AlarmMessageType.RECV_DONE).title()))
                            .content(ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                                    .get(payApprovalReq.getApprovalInfo().getApprovalType())
                                    .get(AlarmMessageType.RECV_DONE).content())
                            .approvalDataId(documentId)
                            .build());

                    successCount++;
                }
            } catch (BusinessException e) {
                // 비즈니스 예외는 실패 카운트 증가
                failCount++;
                log.error("결재 요청 중 예외 발생 (BusinessException) [{} -> {}]: {}",
                        payApprovalReq.getApprovalInfo().getMemberId(), targetUser, e.getMessage(), e);
            } catch (DataAccessException e) {
                // 데이터베이스 접근 오류
                failCount++;
                log.error("결재 요청 중 예외 발생 (데이터베이스 오류) [{} -> {}]: {}",
                        payApprovalReq.getApprovalInfo().getMemberId(), targetUser, e.getMessage(), e);
            } catch (JsonProcessingException e) {
                // JSON 처리 오류
                failCount++;
                log.error("결재 요청 중 예외 발생 (JSON 처리 오류) [{} -> {}]: {}",
                        payApprovalReq.getApprovalInfo().getMemberId(), targetUser, e.getMessage(), e);
            } catch (IllegalArgumentException | NullPointerException e) {
                // 잘못된 인자나 null 참조 예외
                failCount++;
                log.error("결재 요청 중 예외 발생 (잘못된 인자) [{} -> {}]: {}",
                        payApprovalReq.getApprovalInfo().getMemberId(), targetUser, e.getMessage(), e);
            } catch (Exception e) {
                // 기타 예상치 못한 예외
                failCount++;
                log.error("결재 요청 중 예외 발생 (예상치 못한 오류) [{} -> {}]: {}",
                        payApprovalReq.getApprovalInfo().getMemberId(), targetUser, e.getMessage(), e);
            }
        }

        log.info("프로젝트 생성 결재 요청 완료 - 성공: {}, 실패: {}, 전체: {}", successCount, failCount, targetUserList.size());

        // 요청자에게 알람
        if (successCount > 0) {
            alarmService.createAlarm(AlarmCreateRequest.builder()
                    .alarmId(UUID.randomUUID().toString())
                    .targetUser(payApprovalReq.getApprovalInfo().getMemberId())
                    .title("[%s] %s".formatted(
                            payApprovalReq.getApprovalInfo().getApprovalItemString(),
                            ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                                    .get(payApprovalReq.getApprovalInfo().getApprovalType())
                                    .get(AlarmMessageType.SEND_DONE).title()))
                    .content(ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                            .get(payApprovalReq.getApprovalInfo().getApprovalType())
                            .get(AlarmMessageType.SEND_DONE).content())
                    .approvalDataId(documentId)
                    .statusNm(CommCode.AlarmStatus.REQUEST)
                    .build());
        }

        // 하나라도 성공하면 성공으로 처리
        return successCount > 0 ? successCount : -1;
    }

    /**
     * 공통 callback 함수 저장
     *
     * @param approvalCallBakReq
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected GpoGyljCallbackMas saveApprovalCallbackData(ApprovalCallBakReq approvalCallBakReq) {
        GpoGyljCallbackMas gpoGyljCallbackMas = GpoGyljCallbackMas.builder()
                .gyljRespId(approvalCallBakReq.getApprovalDocumentId())
                .gyljjaMemberId(approvalCallBakReq.getApprovalEmployeeNo())
                .apiSpclV(approvalCallBakReq.getResultCode())
                .build();

        return gpoGyljCallbackMasRepository.save(gpoGyljCallbackMas);
    }

    /**
     * callback 이들어오면
     * 우선 저장하고 -> 관련된 모든 요청정보를 조회하고 -> 요청만 된 모든 거래는 취소를 날리고, 해당 내용은 callback DB 에 저장
     * 함
     *
     * @param approvalCallBakReq
     * @return
     */
    @Transactional
    @Override
    public int approvalCallBack(ApprovalCallBakReq approvalCallBakReq) {
        // Callback 된 데이터 우선 저장 함
        GpoGyljCallbackMas gpoGyljCallbackMas = saveApprovalCallbackData(approvalCallBakReq);

        log.debug("결재 후처리 ????????????????????? 1 {}", gpoGyljCallbackMas);
        // ApprovalDocumentId 을 사용 후처리 값을 조회 함
        ApprovalCallbackInfo approvalCallbackInfo = approvalMapper
                .findApprovalCallbackInfo(gpoGyljCallbackMas.getGyljRespId());
        log.debug("결재 후처리 ????????????????????? 2 {}", approvalCallbackInfo);

        String dtlCtnt = approvalCallbackInfo.getDtlCtnt();
        String documentId = dtlCtnt.split("#\\|#")[0];
        PayApprovalReq payApprovalReq;
        Map<String, Object> afterProcessParamMap;
        String reqMemberId = approvalCallbackInfo.getMemberId();

        // 트랜젝션 세션 강제설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username(reqMemberId)
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        try {
            payApprovalReq = new ObjectMapper().readValue(dtlCtnt.split("#\\|#")[1], PayApprovalReq.class);
            afterProcessParamMap = new ObjectMapper()
                    .readValue(payApprovalReq.getApprovalInfo().getAfterProcessParamString(), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 요청자 결재요청건 완료처리
        alarmService.markComplete(documentId);

        // 승인/반려 이후 나머지 결재권자 결재취소 + 자동종료 알림
        List<ApprovalCancelInfo> cancelInfoList = approvalMapper.findApprovalCancelInfo(documentId).stream()
                .filter(cancelInfo -> !cancelInfo.getGyljjaMemberId().equals(approvalCallBakReq.getApprovalEmployeeNo()))
                .toList();

        log.debug("결재 후처리 ????????????????????? 3 {}", cancelInfoList);

        cancelInfoList.forEach(returnInfo -> {
            // 간편결재 취소전문 송신
            cancelRequest(returnInfo.getGyljId(), returnInfo.getGyljRespId());

            AlarmCreateRequest receiverAlarm = AlarmCreateRequest.builder()
                    .alarmId(UUID.randomUUID().toString())
                    .targetUser(returnInfo.getGyljjaMemberId())
                    .title("[%s] %s".formatted(
                            payApprovalReq.getApprovalInfo().getApprovalItemString(),
                            ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                                    .get(payApprovalReq.getApprovalInfo().getApprovalType())
                                    .get(AlarmMessageType.AUTO_CLOSE).title()))
                    .content(ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                            .get(payApprovalReq.getApprovalInfo().getApprovalType())
                            .get(AlarmMessageType.AUTO_CLOSE).content())
                    .approvalDataId(documentId)
                    .build();

            // 자동종료 알림 등록
            alarmService.createAlarm(receiverAlarm);
        });

        // 상태 결정
        boolean isSuccess = "APPROVAL".equals(gpoGyljCallbackMas.getApiSpclV());

        // 다회차 결재 (앞선 결재가 '승인'일때만)
        if (isSuccess) {
            int currentApprovalCount = payApprovalReq.getApprovalInfo().getCurrentApprovalCount();
            int maxApprovalCount = payApprovalReq.getApprovalInfo().getMaxApprovalCount();

            if (currentApprovalCount < maxApprovalCount) {
                // 결재회차 1 증가
                payApprovalReq.getApprovalInfo().setCurrentApprovalCount(currentApprovalCount + 1);

                // 다음회차 간편결재 호출
                ((PayReqService) AopContext.currentProxy()).approval(payApprovalReq);

                return 0;
            }
        }

        String approvalType = payApprovalReq.getApprovalInfo().getApprovalType();
        log.debug("결재 후처리 ????????????????????? 4 {}", approvalType);
        boolean isError = false;

        try {
            switch (approvalType) {
                case "01" -> {
                    log.info("프로젝트 생성 후처리");

                    ProjectCreateReq projectCreateReq = new ObjectMapper().readValue(
                            payApprovalReq.getApprovalInfo().getAfterProcessParamString(), ProjectCreateReq.class);
                    projectService.createProjectAfterProcess(isSuccess, projectCreateReq);
                }
                case "02" -> {
                    log.info("프로젝트 참여 후처리");

                    ProjJoinReq projJoinReq = new ObjectMapper().readValue(
                            payApprovalReq.getApprovalInfo().getAfterProcessParamString(), ProjJoinReq.class);
                    projectService.joinProjectAfterProcess(isSuccess, projJoinReq);
                }
                case "03", "09" -> {
                    log.info("취약점 점검 요청 후처리 : {}", payApprovalReq.getApprovalInfo().getAfterProcessParamString());

                    Map<String, Object> paramMap = new ObjectMapper()
                            .readValue(payApprovalReq.getApprovalInfo().getAfterProcessParamString(), STRING_OBJECT_MAP);
                    log.debug("취약점 점검 후처리 paramMap : {}", paramMap);

                    Object idObj = paramMap.get("id");
                    log.debug("취약점 점검 후처리 id : {}", idObj);

                    if (idObj == null) {
                        log.error("취약점 점검 후처리 실패 - 모델 가든 ID가 없습니다. paramMap={}", paramMap);
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "모델 가든 ID가 누락되었습니다.");
                    }

                    String modelGardenId = String.valueOf(idObj);
                    log.debug("취약점 점검 후처리 modelGardenId : {}", modelGardenId);

                    String statusNm = isSuccess ? ModelGardenStatus.IMPORT_COMPLETED_REGISTERED.name()
                            : ModelGardenStatus.VULNERABILITY_CHECK_APPROVAL_REJECTED.name();

                    UpdateModelGardenReq updateReq = UpdateModelGardenReq.builder()
                            .statusNm(statusNm)
                            .build();

                    modelGardenService.updateModelGarden(ModelGardenService.UPDATE_FIND_TYPE.ID, modelGardenId, updateReq);
                    log.info("취약점 점검 후처리 완료 - id={}, status={}", modelGardenId, statusNm);
                }
                case "04" -> {
                    log.info("모델 배포 후처리");

                    if (isSuccess) {
                        // afterProcessParamString을 Map으로 먼저 파싱하여 servingType 확인
                        Map<String, Object> paramMap = new ObjectMapper()
                                .readValue(payApprovalReq.getApprovalInfo().getAfterProcessParamString(), STRING_OBJECT_MAP);

                        // runtime 필드가 있으면 self-hosting (Backend.AI), 없으면 serverless
                        if (paramMap.containsKey("runtime")) {
                            CreateBackendAiModelDeployReq createBackendAiModelDeployReq = new ObjectMapper().readValue(
                                    payApprovalReq.getApprovalInfo().getAfterProcessParamString(), CreateBackendAiModelDeployReq.class);
                            CreateBackendAiModelDeployRes createBackendAiModelDeployRes = modelDeployService.createBackendAiModelDeploy(createBackendAiModelDeployReq, null);
                            log.info("Backend.AI 모델 배포 생성 성공 - {}", createBackendAiModelDeployRes);
                        } else {
                            CreateModelDeployReq createModelDeployReq = new ObjectMapper().readValue(
                                    payApprovalReq.getApprovalInfo().getAfterProcessParamString(), CreateModelDeployReq.class);
                            CreateModelDeployRes createModelDeployRes = modelDeployService.createModelDeploy(createModelDeployReq, null);
                            log.info("Serverless 모델 배포 생성 성공 - {}", createModelDeployRes);
                        }
                    } else {
                        log.warn("모델 배포가 승인되지 않아 생성하지 않음");
                    }
                }
                case "05" -> {
                    log.info("에이전트 배포 후처리");
                    if (isSuccess) {
                        AppCreateReq appCreateReq = new ObjectMapper().readValue(
                                payApprovalReq.getApprovalInfo().getAfterProcessParamString(), AppCreateReq.class);
                        agentDeployService.createAgentApp(appCreateReq);
                    } else {
                        log.warn("에이전트 배포가 승인되지 않아 생성하지 않음");
                    }
                }
                case "06" -> {
                    log.info("외부시스템 API Key 발급 후처리");

                    if (isSuccess) {
                        CreateApiKeyReq createApiKeyReq = new ObjectMapper().readValue(
                                payApprovalReq.getApprovalInfo().getAfterProcessParamString(), CreateApiKeyReq.class);
                        apiKeyService.createApiKey(createApiKeyReq);
                        log.info("외부시스템 API Key 발급 성공 - uuid: {}, name: {}", createApiKeyReq.getUuid(),
                                createApiKeyReq.getName());
                    } else {
                        log.warn("외부시스템 API Key 발급이 승인되지 않아 발급하지 않음");
                    }
                }
                case "07" -> {
                    log.info("NAS Storage 상향 신청");
                    // example)
                    // SomeStructClass someStructClass = new
                    // ObjectMapper().readValue(payApprovalReq.getApprovalInfo().getAfterProcessParamString(),
                    // SomeStructClass.class);
                    // anyService.afterProcess(isSuccess, someStructClass);
                }
                case "08" -> {
                    log.info("IDE 생성 요청");

                    IdeCreateReq ideCreateReq = new ObjectMapper().readValue(
                            payApprovalReq.getApprovalInfo().getAfterProcessParamString(), IdeCreateReq.class);
                    ideService.createIde(ideCreateReq);
                }
                default -> throw new RuntimeException("unknown approval type " + approvalType);
            }
        } catch (JsonProcessingException e) {
            // JSON 파싱 오류
            log.error("결재 후처리 중 JSON 파싱 오류: approvalType={}, error={}", approvalType, e.getMessage(), e);
            isError = true;
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("결재 후처리 중 비즈니스 예외: approvalType={}, error={}", approvalType, e.getMessage(), e);
            isError = true;
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("결재 후처리 중 데이터베이스 오류: approvalType={}, error={}", approvalType, e.getMessage(), e);
            isError = true;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("결재 후처리 중 잘못된 인자 오류: approvalType={}, error={}", approvalType, e.getMessage(), e);
            isError = true;
        } catch (RuntimeException e) {
            // RuntimeException은 그대로 전파 (위의 default case에서 발생한 경우 포함)
            log.error("결재 후처리 중 런타임 예외: approvalType={}, error={}", approvalType, e.getMessage(), e);
            isError = true;
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("결재 후처리 중 예상치 못한 오류: approvalType={}, error={}", approvalType, e.getMessage(), e);
            isError = true;
        }

        // 결재요청 결과 알람 (요청자)
        AlarmMessageType messageType = (isSuccess && isError) ? AlarmMessageType.SEND_FAIL
                : isSuccess ? AlarmMessageType.RES_APPROVAL : AlarmMessageType.RES_REJECT;
        AlarmCreateRequest senderAlarm = AlarmCreateRequest.builder()
                .alarmId(UUID.randomUUID().toString())
                .targetUser(payApprovalReq.getApprovalInfo().getMemberId())
                .title("[%s] %s".formatted(
                        payApprovalReq.getApprovalInfo().getApprovalItemString(),
                        ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                                .get(payApprovalReq.getApprovalInfo().getApprovalType())
                                .get(messageType).title()))
                .content(ApprovalAlarmMessages.ALARM_MESSAGE_MAP
                        .get(payApprovalReq.getApprovalInfo().getApprovalType())
                        .get(messageType).content())
                .approvalDataId(documentId)
                .statusNm(isSuccess ? CommCode.AlarmStatus.APPROVAL : CommCode.AlarmStatus.REJECT)
                .build();

        alarmService.createAlarm(senderAlarm);

        return 0;
    }

    @Override
    public void cancelRequests(String approvaDocumentId) {
        approvalMapper.findApprovalCancelInfo(approvaDocumentId).forEach(cancelInfo -> {
            cancelRequest(cancelInfo.getGyljId(), cancelInfo.getGyljRespId());
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelRequest(String gyljId, String approvaDocumentId) {
        ApprovalReq approvalReq = ApprovalReq.builder()
                .common(SwingReqCommon.builder()
                        .clientId(gwClientId)
                        .clientSecret(gwClientSecret)
                        .companyCode("SH")
                        .requestUniqueKey(gyljId)
                        .build())
                .data(ApprovalReqData.builder()
                        .approvalDocumentId(approvaDocumentId)
                        .build())
                .build();

        if (List.of("edev", "elocal").contains(activeProfile)) {
            // 외부망 테스트 환경을 위해 분기처리
        } else {
            // 실제 swing 호출
            shinhanSwingClient.cancelRequest(approvalReq);
        }
    }

    @Override
    public boolean isApprovalInProgress(String approvalUniqueKey) {
        // 1. approvalUniqueKey와 일치하는 모든 레코드 조회
        List<GpoGyljMas> approvalList = gpoGyljMasRepository.findByEroCtnt(approvalUniqueKey);

        if (approvalList.isEmpty()) {
            return false;
        }

        // 2. dtl_ctnt의 앞 36자로 그룹핑
        Map<String, List<GpoGyljMas>> groupedByDocumentId = approvalList.stream()
                .filter(approval -> approval.getDtlCtnt() != null && approval.getDtlCtnt().length() >= 36)
                .collect(java.util.stream.Collectors.groupingBy(
                        approval -> approval.getDtlCtnt().substring(0, 36)));

        // 3. 각 그룹별로 종결 여부 확인
        for (Map.Entry<String, List<GpoGyljMas>> entry : groupedByDocumentId.entrySet()) {
            List<GpoGyljMas> group = entry.getValue();

            // 자체 취소건 확인
            if (group.stream()
                    .filter(approval -> approval.getGyljRespId() != null)
                    .anyMatch(approval -> gpoAlarmsRepository.existsByApiRstMsgAndStatusNm(entry.getKey(), CommCode.AlarmStatus.CANCELED))) {
                continue;
            }

            // 그룹 내의 모든 gylj_id가 callback 테이블에 있는지 확인 (하나라도 종결되지 않은 그룹이 있으면 진행중)
            if (!group.stream()
                    .filter(approval -> approval.getGyljRespId() != null)
                    .anyMatch(approval -> gpoGyljCallbackMasRepository.existsByGyljRespId(approval.getGyljRespId()))) {
                return true;
            }
        }

        // 모든 그룹이 종결되었으면 진행중인 것 없음
        return false;
    }

    /**
     * 문자열이 지정된 최대 길이를 초과하면 말줄임표(...)를 추가하여 반환
     *
     * @param text      원본 문자열
     * @param maxLength 최대 길이 (말줄임표 포함)
     * @return 잘린 문자열 또는 원본 문자열
     */
    private String truncateWithEllipsis(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}