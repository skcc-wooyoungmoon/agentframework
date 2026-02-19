package com.skax.aiplatform.service.home.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.constant.CommCode;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.dto.auth.request.AlarmCreateRequest;
import com.skax.aiplatform.dto.common.request.PayApprovalReq;
import com.skax.aiplatform.dto.common.response.ApprovalCancelInfo;
import com.skax.aiplatform.dto.common.response.ApprovalUserInfo;
import com.skax.aiplatform.dto.home.request.ProjectCreateReq;
import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMas;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMasId;
import com.skax.aiplatform.entity.common.approval.GpoGyljCallbackMas;
import com.skax.aiplatform.enums.ModelGardenStatus;
import com.skax.aiplatform.mapper.common.ApprovalMapper;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.common.GpoGyljCallbackMasRepository;
import com.skax.aiplatform.repository.home.GpoAlarmsRepository;
import com.skax.aiplatform.service.common.PayReqService;
import com.skax.aiplatform.service.home.AlarmService;
import com.skax.aiplatform.service.home.ProjectService;
import com.skax.aiplatform.service.model.ModelGardenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("AlarmService")
public class AlarmServiceImpl implements AlarmService {

    private final GpoGyljCallbackMasRepository gpoGyljCallbackMasRepository;
    private final GpoAlarmsRepository gpoAlarmsRepository;
    private final GpoUsersMasRepository usersRepository;

    private final ApprovalMapper approvalMapper;
    private final PayReqService payReqService;
    private final ProjectService projectService;
    private final ModelGardenService modelGardenService;
    
    private static final TypeReference<Map<String, Object>> STRING_OBJECT_MAP = new TypeReference<>() {
    };

    public AlarmServiceImpl(
            GpoGyljCallbackMasRepository gpoGyljCallbackMasRepository,
            GpoAlarmsRepository gpoAlarmsRepository,
            GpoUsersMasRepository usersRepository,
            ApprovalMapper approvalMapper,
            @Lazy PayReqService payReqService,
            ProjectService projectService,
            @Lazy ModelGardenService modelGardenService) {
        this.gpoGyljCallbackMasRepository = gpoGyljCallbackMasRepository;
        this.gpoAlarmsRepository = gpoAlarmsRepository;
        this.usersRepository = usersRepository;
        this.approvalMapper = approvalMapper;
        this.payReqService = payReqService;
        this.projectService = projectService;
        this.modelGardenService = modelGardenService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public int createAlarm(AlarmCreateRequest alarmCreateRequest) {
        GpoAlarmsMasId gpoAlarmsMasId = new GpoAlarmsMasId();
        gpoAlarmsMasId.setMemberId(alarmCreateRequest.getTargetUser());
        gpoAlarmsMasId.setAlarmId(alarmCreateRequest.getAlarmId());

        GpoAlarmsMas gpoAlarmsMas = GpoAlarmsMas.builder()
                .id(gpoAlarmsMasId)
                .alarmTtl(alarmCreateRequest.getTitle())
                .dtlCtnt(alarmCreateRequest.getContent())
                .apiRstMsg(alarmCreateRequest.getApprovalDataId())
                .statusNm(alarmCreateRequest.getStatusNm())
                .readYn(BigDecimal.ZERO)
                .build();
        gpoAlarmsRepository.save(gpoAlarmsMas);
        return 0;
    }

    // 알람 조회 (최근30일)
    @Override
    public List<GpoAlarmsMas> getUnreadAlarms(String username) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        // 30일 이내의 알람 조회
        List<GpoAlarmsMas> recentAlarms = gpoAlarmsRepository.findAlarmsAfterDate(username, thirtyDaysAgo);
        log.debug(">>>>> getReadAlarms: {}", recentAlarms.size());
        return recentAlarms;
    }

    // 안 읽은 알람 조회
    @Override
    public List<GpoAlarmsMas> getNewAlarms(String username) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        // 30일 이내의 알람 조회
        List<GpoAlarmsMas> recentAlarms = gpoAlarmsRepository.findNewAlarmsAfterDate(username, thirtyDaysAgo);
        log.debug(">>>>> getReadAlarms: {}", recentAlarms.size());
        return recentAlarms;
    }

    // 알람 읽음 처리
    @Transactional
    public void markAsRead(String alarmId, String username) {
        GpoAlarmsMas alarm = gpoAlarmsRepository.findByAlarmIdAndMemberId(alarmId, username);

        if (alarm == null) {
            throw new IllegalArgumentException("Alarm not found for alarmId: " + alarmId + ", memberId: " + username);
        }
        alarm.setReadYn(BigDecimal.valueOf(1));
        alarm.setReadAt(LocalDateTime.now());
        gpoAlarmsRepository.save(alarm);
    }

    // 알람 읽음 처리 (일괄)
    @Transactional
    public void markAsReadBulk(String username) {
        List<GpoAlarmsMas> alarmList = gpoAlarmsRepository.findByAlarmMemberId(username);

        for (GpoAlarmsMas alarm : alarmList) {
            alarm.setReadYn(BigDecimal.valueOf(1));
            alarm.setReadAt(LocalDateTime.now());
            gpoAlarmsRepository.save(alarm);
        }
    }

    @Transactional
    public void cancelAlarmInfo(String alarmId, String username) {
        GpoAlarmsMas alarm = gpoAlarmsRepository.findByAlarmIdAndMemberId(alarmId, username);

        // 업무별 취소처리
        String documentId = alarm.getApiRstMsg();
        List<ApprovalCancelInfo> approvalCancelInfo = approvalMapper.findApprovalCancelInfo(documentId);

        if (approvalCancelInfo != null && !approvalCancelInfo.isEmpty()) {
            ApprovalCancelInfo cancelInfo = approvalCancelInfo.get(0);
            PayApprovalReq payApprovalReq;
            Map<String, Object> afterProcessParamMap;

            try {
                payApprovalReq = new ObjectMapper().readValue(cancelInfo.getDtlCtnt().split("#\\|#")[1], PayApprovalReq.class);
                afterProcessParamMap = new ObjectMapper().readValue(payApprovalReq.getApprovalInfo().getAfterProcessParamString(), Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String approvalType = payApprovalReq.getApprovalInfo().getApprovalType();

            switch (approvalType) {
                case "01" -> {
                    log.info("프로젝트 생성 취소처리");

                    ProjectCreateReq projectCreateReq;
                    try {
                        projectCreateReq = new ObjectMapper().readValue(payApprovalReq.getApprovalInfo().getAfterProcessParamString(), ProjectCreateReq.class);
                        projectService.deleteProject(projectCreateReq.getPrjSeq());
                    } catch (JsonProcessingException jpe) {
                        log.debug("JsonProcessingException: {}", jpe.getMessage());
                    }
                }
                case "02" -> {
                    log.info("프로젝트 참여 취소처리");
                }
                case "03", "09" -> {
                    log.info("취약점 점검 요청 취소처리 : {}", payApprovalReq.getApprovalInfo().getAfterProcessParamString());
                    try {
                        Map<String, Object> paramMap = new ObjectMapper()
                                .readValue(payApprovalReq.getApprovalInfo().getAfterProcessParamString(), STRING_OBJECT_MAP);

                        Object idObj = paramMap.get("id");
                        log.debug("취약점 점검 취소처리 id : {}", idObj);

                        if (idObj == null) {
                            log.error("취약점 점검 취소처리 실패 - 모델 가든 ID가 없습니다. paramMap={}", paramMap);
                            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "모델 가든 ID가 누락되었습니다.");
                        }

                        String modelGardenId = String.valueOf(idObj);
                        log.debug("취약점 점검 취소처리 modelGardenId : {}", modelGardenId);

                        String statusNm = ModelGardenStatus.VULNERABILITY_CHECK_COMPLETED.name();
                        UpdateModelGardenReq updateReq = UpdateModelGardenReq.builder()
                                .statusNm(statusNm)
                                .build();
                        modelGardenService.updateModelGarden(ModelGardenService.UPDATE_FIND_TYPE.ID, modelGardenId, updateReq);
                        log.info("취약점 점검 취소처리 완료 - id={}, status={}", modelGardenId, statusNm);
                    } catch (JsonProcessingException jpe) {
                        log.error("취약점 점검 취소처리 실패 - JSON 파싱 오류: {}", jpe.getMessage());
                        throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT, "취약점 점검 취소처리 중 오류가 발생했습니다.");
                    }
                }
                case "04" -> {
                    log.info("모델 배포 취소처리");
                }
                case "05" -> {
                    log.info("에이전트 배포 취소처리");
                }
                case "06" -> {
                    log.info("외부시스템 API Key 발급 취소처리");
                }
                case "07" -> {
                    log.info("NAS Storage 상향 신청 취소처리");
                }
                case "08" -> {
                    log.info("IDE 생성 요청 취소처리");
                }
                default -> throw new RuntimeException("unknown approval type " + approvalType);
            }
        }

        // 알림 처리
        gpoAlarmsRepository.deleteByApiRstMsgAndNotMatchMemberId(alarm.getApiRstMsg(), username);
        payReqService.cancelRequests(alarm.getApiRstMsg()); // documentId

        if (alarm == null) {
            throw new IllegalArgumentException("Alarm not found for alarmId: " + alarmId + ", memberId: " + username);
        }
        alarm.setStatusNm(CommCode.AlarmStatus.CANCELED);
        gpoAlarmsRepository.save(alarm);
    }

    @Transactional
    @Override
    public void markComplete(String documentId) {
        List<GpoAlarmsMas> alarmList = gpoAlarmsRepository.findByDocumentIdAndStatusNm(documentId, CommCode.AlarmStatus.REQUEST);
        alarmList.forEach(alarm -> {
            alarm.setStatusNm(CommCode.AlarmStatus.COMPLETED);
            gpoAlarmsRepository.save(alarm);
        });
    }

    @Override
    public ApprovalUserInfo getApprovalUsrInfo(String alarmId) {
        GpoAlarmsMas alarm = gpoAlarmsRepository.findByAlarmId(alarmId);
        PayApprovalReq payApprovalReq = null;
        String documentId = alarm.getApiRstMsg();
        GpoGyljCallbackMas callback = null;
        GpoUsersMas gyljjaUserInfo = null;
        ApprovalCancelInfo approvalCancelInfo = approvalMapper.findApprovalCancelInfo(documentId).stream()
                .filter(_approvalCancelInfo -> gpoGyljCallbackMasRepository.findByGyljRespId(_approvalCancelInfo.getGyljRespId()) != null)
                .findFirst()
                .orElse(null);

        if (approvalCancelInfo != null) {
            Map<String, Object> afterProcessParamMap;
            callback = gpoGyljCallbackMasRepository.findByGyljRespId(approvalCancelInfo.getGyljRespId());
            gyljjaUserInfo = usersRepository.findByMemberId(callback.getGyljjaMemberId()).orElse(null);


            try {
                payApprovalReq = new ObjectMapper().readValue(approvalCancelInfo.getDtlCtnt().split("#\\|#")[1], PayApprovalReq.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return ApprovalUserInfo.builder()
                .jkwNm(gyljjaUserInfo != null ? gyljjaUserInfo.getJkwNm() : "")
                .deptNm(gyljjaUserInfo != null ? gyljjaUserInfo.getDeptNm() : "")
                .apiSpclV(callback != null ? callback.getApiSpclV() : "")
                .memberId(payApprovalReq != null ? payApprovalReq.getApprovalInfo() != null ? payApprovalReq.getApprovalInfo().getMemberId() : "" : "")
                .payApprovalInfo(payApprovalReq)
                .fstCreatedAt(callback != null ? DateUtils.utcToKstDateTimeString(callback.getFstCreatedAt()) : "")
                .build();
    }

}
