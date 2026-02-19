package com.skax.aiplatform.service.model.impl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.lablup.api.dto.request.CancelImportArtifactRequest;
import com.skax.aiplatform.client.lablup.api.dto.request.ScanArtifactRequest;
import com.skax.aiplatform.client.lablup.api.dto.response.ScanArtifactResponse;
import com.skax.aiplatform.client.lablup.api.service.LablupArtifactService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.auth.request.AlarmCreateRequest;
import com.skax.aiplatform.dto.common.response.AssetProjectInfoRes;
import com.skax.aiplatform.dto.model.common.ModelGardenInfo;
import com.skax.aiplatform.dto.model.request.CreateModelGardenReq;
import com.skax.aiplatform.dto.model.request.FileImportCompleteReq;
import com.skax.aiplatform.dto.model.request.GetAvailableModelReq;
import com.skax.aiplatform.dto.model.request.GetModelGardenReq;
import com.skax.aiplatform.dto.model.request.PostInProcessStatusReq;
import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.dto.model.response.GetAvailableModelRes;
import com.skax.aiplatform.dto.model.response.GetVaccineCheckResultRes;
import com.skax.aiplatform.entity.model.GpoModelDoipCtntMas;
import com.skax.aiplatform.entity.model.GpoModelDoipMas;
import com.skax.aiplatform.entity.model.GpoModelMngMas;
import com.skax.aiplatform.entity.model.GpoUseGnynModelMas;
import com.skax.aiplatform.enums.ModelDoipCtntType;
import com.skax.aiplatform.enums.ModelGardenStatus;
import com.skax.aiplatform.repository.model.GpoModelDoipCtntMasRepository;
import com.skax.aiplatform.repository.model.GpoModelMngMasRepository;
import com.skax.aiplatform.repository.model.ModelGardenRepository;
import com.skax.aiplatform.service.common.ProjectInfoService;
import com.skax.aiplatform.service.home.AlarmService;
import com.skax.aiplatform.service.model.ModelGardenService;
import com.skax.aiplatform.service.model.handler.ModelGardenStatusHandler;
import com.skax.aiplatform.service.model.handler.ModelGardenStatusHandlerContext;
import com.skax.aiplatform.mapper.model.ModelGardenMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelGardenServiceImpl implements ModelGardenService {

    private static final String L = "[ModelGarden] ";
    private static final String DEPLOY_TYPE_SERVERLESS = "serverless";

    private final ModelGardenRepository modelGardenRepository;
    private final TokenInfo tokenInfo;
    private final LablupArtifactService lablupArtifactService;
    private final GpoModelMngMasRepository gpoModelMngMasRepository;
    private final GpoModelDoipCtntMasRepository gpoModelDoipCtntMasRepository;
    private final AlarmService alarmService;
    private final ProjectInfoService projectInfoService;
    private final ModelGardenStatusHandler statusHandler;
    private final ModelGardenMapper modelGardenMapper;

    private static final int MAX_MESSAGE_LENGTH = 4000;
    
    @Override
    public PageResponse<ModelGardenInfo> getModelGardens(GetModelGardenReq modelCtlgReq) {
        log.info("{}getModelGardens 요청, page={}, size={}", L, modelCtlgReq.getPage(), modelCtlgReq.getSize());

        Pageable pageable = PageRequest.of(modelCtlgReq.getPage() - 1, modelCtlgReq.getSize());

        Page<GpoUseGnynModelMas> gardenInfos;

        // progress 일경우
        if (modelCtlgReq.getStatus() != null && !modelCtlgReq.getStatus().isBlank()
                && (modelCtlgReq.getStatus().equals("PROGRESS") 
                        || modelCtlgReq.getStatus().equals("BEFORE") 
                        || modelCtlgReq.getStatus().equals("COMPLETE"))) {
            ModelGardenStatus[] statusList = ModelGardenStatus.fromGroup(modelCtlgReq.getStatus());
            gardenInfos = modelGardenRepository.getGardenInfoWithDoipByStatusList(
                    pageable,
                    // 파라미터 self-hosting 들어오면 내부 selfhosting 변환 후 조회
                    modelGardenMapper.extractUnderbar(modelCtlgReq.getDplyTyp()),
                    modelCtlgReq.getSearch(),
                    Arrays.asList(statusList));
        } else {
            ModelGardenStatus statusEnum = null;
            if (modelCtlgReq.getStatus() != null && !modelCtlgReq.getStatus().isEmpty()) {
                try {
                    statusEnum = ModelGardenStatus.fromCode(modelCtlgReq.getStatus());
                } catch (IllegalArgumentException e) {
                    log.warn("{}getModelGardens invalid status, status={}", L, modelCtlgReq.getStatus());
                }
            }
            gardenInfos = modelGardenRepository.getGardenInfoWithDoip(
                    pageable,
                    // 파라미터 self-hosting 들어오면 내부 selfhosting 변환 후 조회
                    modelGardenMapper.extractUnderbar(modelCtlgReq.getDplyTyp()),
                    modelCtlgReq.getSearch(),
                    statusEnum, // 모델 상태 (ENUM)
                    modelCtlgReq.getType() // 모델 타입
            );
        }

        Page<ModelGardenInfo> modelGardenInfoPage = gardenInfos.map(modelGardenMapper::toModelGardenInfo);
        log.info("{}getModelGardens 완료, total={}", L, modelGardenInfoPage.getTotalElements());

        return PageResponse.from(modelGardenInfoPage);
    }

    @Override
    public ModelGardenInfo getModelGardenById(String id) {
        log.info("{}getModelGardenById 요청, id={}", L, id);
        GpoUseGnynModelMas gpoUseGnynModelMas = modelGardenRepository.findBySeqNoAndDelYn(Long.parseLong(id), 0);

        if (gpoUseGnynModelMas == null) {
            log.warn("{}getModelGardenById 모델 없음, id={}", L, id);
            throw new BusinessException(ErrorCode.MODEL_GARDEN_NOT_FOUND);
        }

        return modelGardenMapper.toModelGardenInfo(gpoUseGnynModelMas);
    }

    @Override
    public ModelGardenInfo createModelGarden(CreateModelGardenReq request) {
        log.info("{}createModelGarden 요청, name={}", L, request.getName());

        GpoUseGnynModelMas existingModel = modelGardenRepository.findByModelNmAndDelYn(request.getName(), 0);
        if (existingModel != null) {
            log.warn("{}createModelGarden 중복 이름, name={}", L, request.getName());
            throw new BusinessException(ErrorCode.MODEL_GARDEN_DUPLICATE_NAME);
        }

        GpoUseGnynModelMas gpoUseGnynModelMas = modelGardenMapper.toEntity(request);
        GpoUseGnynModelMas savedGardenInfo = modelGardenRepository.save(gpoUseGnynModelMas);
        ModelGardenInfo result = modelGardenMapper.toModelGardenInfo(savedGardenInfo);
        log.info("{}createModelGarden 완료, id={}", L, result.getId());
        return result;
    }

    @Override
    @Transactional
    public ModelGardenInfo updateModelGarden(UPDATE_FIND_TYPE type, String findKey, UpdateModelGardenReq request) {
        log.info("{}updateModelGarden 요청, type={}, findKey={}, status={}", L, type, findKey, request.getStatusNm());

        GpoUseGnynModelMas existingGardenInfo;
        switch (type) {
            case ID:
                existingGardenInfo = modelGardenRepository.findBySeqNoAndDelYn(Long.parseLong(findKey), 0);
                break;
            case NAME:
                existingGardenInfo = modelGardenRepository.findByModelNmAndDelYn(findKey, 0);
                break;
            case MODEL_CTLG_ID:
                existingGardenInfo = modelGardenRepository.findByCallUrlAndDelYn(findKey, 0);
                break;
            default:
                throw new BusinessException(ErrorCode.MODEL_GARDEN_NOT_FOUND, "키가 없습니다.");
        }
        if (existingGardenInfo == null) {
            log.warn("{}updateModelGarden 모델 없음, type={}, findKey={}", L, type, findKey);
            throw new BusinessException(ErrorCode.MODEL_GARDEN_NOT_FOUND);
        }

        return updateModelGarden(existingGardenInfo, request);
    }

    @Override
    @Transactional
    public ModelGardenInfo updateModelGarden(GpoUseGnynModelMas existingGardenInfo, UpdateModelGardenReq request) {
        String currentUser = tokenInfo.getUserName();
        ModelGardenStatus statusNm = request.getStatusNm() != null
                ? ModelGardenStatus.fromCode(request.getStatusNm())
                : null;

        if (statusNm != null) {
            ModelGardenStatusHandlerContext ctx = ModelGardenStatusHandlerContext.builder()
                    .status(statusNm)
                    .currentUser(currentUser)
                    .request(request)
                    .servingTypeFormatter(modelGardenMapper::addUnderbar)
                    .build();
            statusHandler.handle(existingGardenInfo, ctx);
        }

        modelGardenMapper.updateFromRequest(existingGardenInfo, request);
        GpoUseGnynModelMas updatedGardenInfo = modelGardenRepository.save(existingGardenInfo);

        sendServerlessModelAlarms(updatedGardenInfo, "모델 정보 수정",
                "모델 탐색에서 변경된 serverless 모델 정보를 먼저 확인하세요. API 정보가 바뀐 경우, 등록한 %s의 모델 관리 메뉴에도 동일하게 수정해주세요.", false);

        log.info("{}updateModelGarden 완료, id={}", L, updatedGardenInfo.getSeqNo());
        return modelGardenMapper.toModelGardenInfo(updatedGardenInfo);
    }

    @Override
    public void deleteModelGarden(String id) {
        log.info("{}deleteModelGarden 요청, id={}", L, id);
        GpoUseGnynModelMas existingGardenInfo = modelGardenRepository.findBySeqNoAndDelYn(Long.parseLong(id), 0);
        if (existingGardenInfo == null) {
            log.warn("{}deleteModelGarden 모델 없음, id={}", L, id);
            throw new BusinessException(ErrorCode.MODEL_GARDEN_NOT_FOUND);
        }

        if (existingGardenInfo.getDoipInfo().getStatusNm() != null
                && existingGardenInfo.getDoipInfo().getStatusNm() == ModelGardenStatus.IMPORT_REQUEST) {
            CancelImportArtifactRequest cancelImportArtifactRequest = CancelImportArtifactRequest.builder()
                    .artifactRevisionId(existingGardenInfo.getDoipInfo().getRevisionId())
                    .build();
            lablupArtifactService.cancelImportArtifact(cancelImportArtifactRequest);
        }

        // 논리 삭제시 중복 방지 처리
        if (existingGardenInfo.getDplyTyp().equals("slfhosting")) {
            // 이름 + 시퀀스번호로 중복 방지 처리
            existingGardenInfo.setModelNm(existingGardenInfo.getModelNm() + "_" + existingGardenInfo.getSeqNo()); // 중복
        }

        // 논리 삭제 (delYn을 1로 설정)
        existingGardenInfo.setDelYn(1);

        sendServerlessModelAlarms(existingGardenInfo, "모델 삭제",
                "해당 모델이 삭제되어 현재 플랫폼에서 사용할 수 없습니다. 생성형 AI 플랫폼 \"%s\" 프로젝트의 \"모델 > 모델 관리\" 메뉴에서 모델명이 \"%s\" 인 항목을 삭제해주세요. ",
                true);

        modelGardenRepository.save(existingGardenInfo);
        log.info("{}deleteModelGarden 완료, id={}", L, id);
    }

    @Override
    @Transactional
    public ModelGardenInfo completeModelImport(PostInProcessStatusReq request) {
        log.info("{}completeModelImport 요청, name={}, success={}", L, request.getName(), request.isSuccess());

        boolean success = request.isSuccess();
        String statusNm = success ? ModelGardenStatus.FILE_IMPORT_COMPLETED.name()
                : ModelGardenStatus.IMPORT_FAILED.name();

        UpdateModelGardenReq updateModelGardenReq = UpdateModelGardenReq.builder()
                .statusNm(statusNm)
                .build();
        ModelGardenInfo updateModelStatusResult = updateModelGarden(UPDATE_FIND_TYPE.NAME, request.getName(), updateModelGardenReq);
        String message = request.getMessage();
        ModelGardenInfo result = saveModelDoipCtntMas(ModelDoipCtntType.RESORVOIR, updateModelStatusResult, message);

        log.info("{}completeModelImport 완료, name={}", L, request.getName());
        return result;
    }

    @Override
    @Transactional
    public ModelGardenInfo completeVaccineScan(PostInProcessStatusReq request) {
        log.info("{}completeVaccineScan 요청, name={}, success={}", L, request.getName(), request.isSuccess());

        boolean success = request.isSuccess();
        String statusNm = success ? ModelGardenStatus.VACCINE_SCAN_COMPLETED.name()
                : ModelGardenStatus.IMPORT_FAILED.name();
        
        // 모델 상태 업데이트
        UpdateModelGardenReq updateModelGardenReq = UpdateModelGardenReq.builder()
                .statusNm(statusNm)
                .fileDivCnt(request.getSplit_count() != null ? request.getSplit_count() : 1) // 파일 분할 개수
                .build();
        ModelGardenInfo updateModelStatusResult = updateModelGarden(UPDATE_FIND_TYPE.NAME, request.getName(), updateModelGardenReq);

        // VACCINE_SCAN 체크 내용 저장
        String message;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            message = objectMapper.writeValueAsString(Map.of(
                    "success", success,
                    "message", request.getMessage() != null ? request.getMessage() : ""
            ));
        } catch (JsonProcessingException e) {
            log.error("{}completeVaccineScan JSON 변환 실패, name={}", L, request.getName(), e);
            message = request.getMessage();
        }
        ModelGardenInfo result = saveModelDoipCtntMas(ModelDoipCtntType.VACCINE_SCAN, updateModelStatusResult, message);
        log.info("{}completeVaccineScan 완료, name={}", L, request.getName());
        return result;
    }

    @Override
    @Transactional
    public ModelGardenInfo completeVulnerabilityCheck(PostInProcessStatusReq request) {
        log.info("{}completeVulnerabilityCheck 요청, name={}, success={}", L, request.getName(), request.isSuccess());

        boolean success = request.isSuccess();
        String statusNm = success ? ModelGardenStatus.VULNERABILITY_CHECK_COMPLETED.name()
                : ModelGardenStatus.IMPORT_FAILED.name();

        UpdateModelGardenReq updateModelGardenReq = UpdateModelGardenReq.builder()
                .statusNm(statusNm)
                .build();
        ModelGardenInfo updateModelStatusResult = updateModelGarden(UPDATE_FIND_TYPE.NAME, request.getName(), updateModelGardenReq);
        String message = request.getMessage();
        saveModelDoipCtntMas(ModelDoipCtntType.VULNERABILITY_CHECK, updateModelStatusResult, message);
        String summary = request.getSummary();
        ModelGardenInfo result = saveModelDoipCtntMas(ModelDoipCtntType.VULNERABILITY_CHECK_SUMMARY, updateModelStatusResult, summary);

        log.info("{}completeVulnerabilityCheck 완료, name={}", L, request.getName());
        return result;
    }

    
    @Override
    @Transactional
    public ModelGardenInfo completeInternalNetworkImport(FileImportCompleteReq request) {
        log.info("{}completeInternalNetworkImport 요청", L);

        String cnts = request.getCnts();
        if (cnts == null || cnts.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_FORMAT, "cnts 필드가 누락되었습니다.");
        }

        Pattern pattern = Pattern.compile("(.+?)\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(cnts);
        String message;
        String fileName = null;

        if (matcher.find()) {
            message = matcher.group(1).trim();
            String fileNameWithExt = matcher.group(2).trim();
            int firstDotIndex = fileNameWithExt.indexOf(".zip");
            fileName = firstDotIndex > 0 ? fileNameWithExt.substring(0, firstDotIndex) : fileNameWithExt;
        } else {
            message = cnts;
            log.warn("{}completeInternalNetworkImport cnts 파싱 실패, cnts={}", L, cnts);
        }

        boolean success = !message.contains("수신되지 않았습니다") && !message.contains("악성코드");
        String formattedFileName = fileName != null ? fileName.replace("_", "/") : null;
        String modelName = formattedFileName != null ? formattedFileName : request.getAppId();

        String statusNm = success ? ModelGardenStatus.INTERNAL_NETWORK_IMPORT_COMPLETED.name() : ModelGardenStatus.IMPORT_FAILED.name();
        UpdateModelGardenReq updateModelGardenReq = UpdateModelGardenReq.builder().statusNm(statusNm).build();
        ModelGardenInfo result = updateModelGarden(UPDATE_FIND_TYPE.NAME, modelName, updateModelGardenReq);

        log.info("{}completeInternalNetworkImport 완료, modelName={}, status={}", L, modelName, statusNm);
        return result;
    }

    @Override
    public GetAvailableModelRes getAvailableModel(GetAvailableModelReq request) {
        log.info("{}getAvailableModel 요청, search={}", L, request.getSearch());

        ScanArtifactRequest scanArtifactRequest = ScanArtifactRequest.builder()
                .search(request.getSearch())
                .artifactType("MODEL")
                .build();
        ScanArtifactResponse response = lablupArtifactService.scanArtifact(scanArtifactRequest);
        GetAvailableModelRes result = modelGardenMapper.toGetAvailableModelRes(response);

        log.info("{}getAvailableModel 완료, artifactCount={}", L, result.getArtifacts() != null ? result.getArtifacts().size() : 0);
        return result;
    }

    @Override
    public GetVaccineCheckResultRes getModelGardenVaccineCheckResult(String id) {
        log.info("{}getModelGardenVaccineCheckResult 요청, id={}", L, id);

        GpoUseGnynModelMas gpoUseGnynModelMas = modelGardenRepository.findBySeqNoAndDelYn(Long.parseLong(id), 0);
        if (gpoUseGnynModelMas == null) {
            log.warn("{}getModelGardenVaccineCheckResult 모델 없음, id={}", L, id);
            throw new BusinessException(ErrorCode.MODEL_GARDEN_NOT_FOUND);
        }

        GpoModelDoipMas doipInfo = gpoUseGnynModelMas.getDoipInfo();
        String fistChkDtl = null;
        String secndChkDtl = null;
        String vanbBrSmry = null;

        if (doipInfo.getFistChkDtlCtnt() != null && !doipInfo.getFistChkDtlCtnt().isEmpty()) {
            Long fistChkSeqNo = Long.parseLong(doipInfo.getFistChkDtlCtnt());
            GpoModelDoipCtntMas fistChkCtnt = gpoModelDoipCtntMasRepository.findById(fistChkSeqNo).orElse(null);
            if (fistChkCtnt != null) {
                fistChkDtl = fistChkCtnt.getDtlCtnt();
            }
        }
        if (doipInfo.getSecdChkDtlCtnt() != null && !doipInfo.getSecdChkDtlCtnt().isEmpty()) {
            Long secdChkSeqNo = Long.parseLong(doipInfo.getSecdChkDtlCtnt());
            GpoModelDoipCtntMas secdChkCtnt = gpoModelDoipCtntMasRepository.findById(secdChkSeqNo).orElse(null);
            if (secdChkCtnt != null) {
                secndChkDtl = secdChkCtnt.getDtlCtnt();
            }
        }
        if (doipInfo.getVanbBrSmryCtnt() != null && !doipInfo.getVanbBrSmryCtnt().isEmpty()) {
            Long vanbBrSmrySeqNo = Long.parseLong(doipInfo.getVanbBrSmryCtnt());
            GpoModelDoipCtntMas vanbBrSmryCtnt = gpoModelDoipCtntMasRepository.findById(vanbBrSmrySeqNo).orElse(null);
            if (vanbBrSmryCtnt != null) {
                vanbBrSmry = vanbBrSmryCtnt.getDtlCtnt();
            }
        }

        String checkStatus = doipInfo.getStatusNm().name();
        GetVaccineCheckResultRes result = GetVaccineCheckResultRes.builder()
                .modelName(gpoUseGnynModelMas.getModelNm())
                .license(gpoUseGnynModelMas.getCertNm())
                .fistChkDtl(fistChkDtl)
                .secndChkDtl(secndChkDtl)
                .vanbBrSmry(vanbBrSmry)
                .checkBy(doipInfo.getChkMn())
                .checkAt(doipInfo.getChkAt() != null ? doipInfo.getChkAt().toString() : null)
                .checkStatus(checkStatus)
                .build();
        
        log.info("{}getModelGardenVaccineCheckResult 완료, id={}", L, id);
        return result;
    }

    /**
     * serverless 배포 모델인 경우 해당 모델을 사용 중인 대상에게 알림 전송.
     *
     * @param model           모델 가든 엔티티
     * @param titleSuffix     알림 제목 접미사 (예: "모델 정보 수정", "모델 삭제")
     * @param contentTemplate 알림 본문 포맷 (%s: 프로젝트명, 두 번째 오버로드 시 %s 하나 더: 모델명)
     * @param contentWithModelName true이면 contentTemplate에 projectName, modelName 순으로 포맷
     */
    private void sendServerlessModelAlarms(GpoUseGnynModelMas model, String titleSuffix, String contentTemplate,
            boolean contentWithModelName) {
        if (model.getDplyTyp() == null || !DEPLOY_TYPE_SERVERLESS.equals(model.getDplyTyp())) {
            return;
        }
        List<GpoModelMngMas> modelMngMasList = gpoModelMngMasRepository.findByUseGnynModelSeqNo(model.getSeqNo());
        if (modelMngMasList == null || modelMngMasList.isEmpty()) {
            return;
        }
        String modelNm = model.getModelNm();
        for (GpoModelMngMas modelMngMas : modelMngMasList) {
            String targetUser = modelMngMas.getCreatedBy();
            String projectName = resolveProjectName(modelMngMas.getModelMngId());
            String content = contentWithModelName
                    ? contentTemplate.formatted(projectName, modelNm)
                    : contentTemplate.formatted(projectName);
            alarmService.createAlarm(AlarmCreateRequest.builder()
                    .alarmId(UUID.randomUUID().toString())
                    .targetUser(targetUser)
                    .title("[%s] %s".formatted(modelNm, titleSuffix))
                    .content(content)
                    .build());
        }
    }

    private String resolveProjectName(String modelMngId) {
        if (modelMngId == null || modelMngId.isBlank()) {
            return "Public";
        }
        AssetProjectInfoRes assetInfo = projectInfoService.getAssetProjectInfoByUuid(modelMngId);
        if (assetInfo == null || assetInfo.getLstPrjNm() == null || assetInfo.getLstPrjNm().isBlank()) {
            return "Public";
        }
        return assetInfo.getLstPrjNm();
    }

    private ModelGardenInfo saveModelDoipCtntMas(ModelDoipCtntType type, ModelGardenInfo modelGardenInfo, String message) {
        String preprocessedMessage = truncateByBytes(message, MAX_MESSAGE_LENGTH); // 4000byte 이하로 저장
        GpoModelDoipCtntMas gpoModelDoipCtntMas 
                            = gpoModelDoipCtntMasRepository.save(GpoModelDoipCtntMas.builder()
                                            .artifactId(modelGardenInfo.getArtifact_id())
                                            .revisionId(modelGardenInfo.getRevision_id())
                                            .type(type) // RESORVOIR, VACCINE_SCAN, VULNERABILITY_CHECK, VULNERABILITY_CHECK_SUMMARY
                                            .dtlCtnt(preprocessedMessage) // 메시지
                                            .build());

        UpdateModelGardenReq updateModelGardenReq = new UpdateModelGardenReq();
        String seqNo = gpoModelDoipCtntMas.getSeqNo().toString();
        switch (type) {
            case RESORVOIR:
                updateModelGardenReq.setFistChkDtlCtnt(seqNo);
                break;
            case VACCINE_SCAN:
                updateModelGardenReq.setSecdChkDtlCtnt(seqNo);
                break;
            case VULNERABILITY_CHECK:
                updateModelGardenReq.setVanbBrDtlCtnt(seqNo);
                break;
            case VULNERABILITY_CHECK_SUMMARY:
                updateModelGardenReq.setVanbBrSmryCtnt(seqNo);
                break;
            default:
                break;
        }

        return updateModelGarden(UPDATE_FIND_TYPE.NAME, modelGardenInfo.getName(), updateModelGardenReq);
    }

    /**
     * UTF-8 기준 바이트 길이를 초과하지 않도록 문자열을 잘라 반환한다.
     */
    private String truncateByBytes(String input, int maxBytes) {
        if (input == null) {
            return "";
        }
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= maxBytes) {
            return input;
        }
        int accumulated = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            int charBytes = String.valueOf(ch).getBytes(StandardCharsets.UTF_8).length;
            if (accumulated + charBytes > maxBytes) {
                break;
            }
            sb.append(ch);
            accumulated += charBytes;
        }
        return sb.toString();
    }
}
