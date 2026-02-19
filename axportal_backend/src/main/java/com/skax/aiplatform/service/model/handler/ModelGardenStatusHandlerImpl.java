package com.skax.aiplatform.service.model.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skax.aiplatform.client.deepsecurity.dto.request.ScanRequest;
import com.skax.aiplatform.client.deepsecurity.service.DeepSecurityService;
import com.skax.aiplatform.client.deepsecurity.service.VulnerabilityService;
import com.skax.aiplatform.client.lablup.api.dto.request.ImportArtifactsRequest;
import com.skax.aiplatform.client.lablup.api.service.LablupArtifactService;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelLanguageRequest;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelTagRequest;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelRead;
import com.skax.aiplatform.client.sktai.model.service.SktaiModelsService;
import com.skax.aiplatform.common.context.AdminContext;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.entity.model.GpoModelDoipMas;
import com.skax.aiplatform.entity.model.GpoUseGnynModelMas;
import com.skax.aiplatform.enums.ModelGardenStatus;
import com.skax.aiplatform.service.admin.AdminAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 모델 가든 상태별 업데이트 처리를 한 곳에서 담당하는 핸들러.
 * <p>
 * {@link ModelGardenStatusHandlerContext#getStatus()}에 따라 분기하여 각 상태별 로직을 수행한다.
 * </p>
 * <h3>상태별 처리 구분</h3>
 * <ul>
 *   <li><b>NO_OP_STATUSES</b>: 별도 처리 없이 통과</li>
 *   <li><b>switch 분기</b>: 실제 부가 처리(래블업 반입, 백신/취약점 요청, 카탈로그 등록 등)가 필요한 6개 상태</li>
 *   <li><b>default</b>: 현재 enum에 없는 값 → 비즈니스 예외 발생</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelGardenStatusHandlerImpl implements ModelGardenStatusHandler {

    private static final String LH = "[ModelGarden:Handler] ";
    private static final String ADMIN_USERNAME = "admin";
    private static final String MODEL_PATH_PREFIX = "/pv-model/model/model_file_tmp/";

    /**
     * 이 상태들은 여기서 바로 return
     * 따로 후처리 로직 필요 없이 상태 변경만 필요한 상태 모음
     */
    private static final Set<ModelGardenStatus> NO_OP_STATUSES = Set.of(
            ModelGardenStatus.PENDING,
            ModelGardenStatus.VACCINE_SCAN_COMPLETED,
            ModelGardenStatus.VULNERABILITY_CHECK_COMPLETED,
            ModelGardenStatus.IMPORT_FAILED,
            ModelGardenStatus.VULNERABILITY_CHECK_APPROVAL_REJECTED,
            ModelGardenStatus.IMPORT_COMPLETED
    );

    private final LablupArtifactService lablupArtifactService;
    private final DeepSecurityService deepSecurityService;
    private final VulnerabilityService vulnerabilityService;
    private final SktaiModelsService sktaiModelService;
    private final AdminAuthService adminAuthService;

    @Override
    public void handle(GpoUseGnynModelMas existing, ModelGardenStatusHandlerContext ctx) {
        ModelGardenStatus status = ctx.getStatus();
        if (status == null) {
            return;
        }
        // 원래 switch에서 break만 하던 6개 상태 → 별도 처리 없이 통과
        if (NO_OP_STATUSES.contains(status)) {
            return;
        }

        switch (status) {
            case IMPORT_REQUEST -> handleImportRequest(existing, ctx);
            case FILE_IMPORT_COMPLETED -> handleFileImportCompleted(existing);
            case INTERNAL_NETWORK_IMPORT_COMPLETED -> handleInternalNetworkImportCompleted(existing, ctx);
            case VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS -> handleVulnerabilityCheckApprovalInProgress(existing, ctx);
            case IMPORT_COMPLETED_REGISTERED -> handleImportCompletedRegistered(existing, ctx);
            case IMPORT_COMPLETED_UNREGISTERED -> handleImportCompletedUnregistered(existing);
            // 현재 ModelGardenStatus enum 12개 값은 위 NO_OP 6개 + case 6개로 모두 처리됨.
            // default는 enum에 새 값이 추가되었을 때만 도달하며, 이때 비즈니스 예외를 던진다.
            default -> {
                log.warn("{}status=unknown, value={}", LH, status);
                throw new BusinessException(ErrorCode.MODEL_GARDEN_INVALID_STATUS);
            }
        }
    }

    /** 반입 요청: 래블업 아티팩트 반입 API 호출 후 반입 담당자/일자 갱신 */
    private void handleImportRequest(GpoUseGnynModelMas existing, ModelGardenStatusHandlerContext ctx) {
        log.info("{}status=IMPORT_REQUEST, step=아티팩트 반입 요청, revisionId={}", LH, existing.getDoipInfo().getRevisionId());
        ImportArtifactsRequest importArtifactsRequest = ImportArtifactsRequest.builder()
                .artifactRevisionIds(new String[] { existing.getDoipInfo().getRevisionId() })
                .build();
        lablupArtifactService.importArtifacts(importArtifactsRequest);
        existing.getDoipInfo().setDoipMn(ctx.getCurrentUser());
        existing.getDoipInfo().setDoipAt(LocalDateTime.now());
    }

    /** 파일 반입 완료: 백신 점검(Deep Security) 요청 */
    private void handleFileImportCompleted(GpoUseGnynModelMas existing) {
        log.info("{}status=FILE_IMPORT_COMPLETED, step=백신 점검 요청, name={}", LH, existing.getModelNm());
        deepSecurityService.requestDeepSecurity(ScanRequest.builder()
                .uid(existing.getSeqNo().toString())
                .name(existing.getModelNm())
                .filename(existing.getModelNm())
                .build());
    }

    /**
     * 내부망 반입 완료: 모든 파일 검사가 끝났으면 취약점 점검 요청,
     * 아니면 파일 완료 수만 증가하고 상태는 VACCINE_SCAN_COMPLETED 유지
     */
    private void handleInternalNetworkImportCompleted(GpoUseGnynModelMas existing, ModelGardenStatusHandlerContext ctx) {
        GpoModelDoipMas doipInfo = existing.getDoipInfo();
        Integer fileDivCnt = doipInfo.getFileDivCnt() != null ? doipInfo.getFileDivCnt() : 1;
        Integer currentFileIndex = doipInfo.getFileChkCpltCnt() != null ? doipInfo.getFileChkCpltCnt() : 1;
        if (currentFileIndex.equals(fileDivCnt)) {
            log.info("{}status=INTERNAL_NETWORK_IMPORT_COMPLETED, step=취약점 점검 요청, name={}", LH, existing.getModelNm());
            vulnerabilityService.requestVulnerability(ScanRequest.builder()
                    .uid(existing.getSeqNo().toString())
                    .name(existing.getModelNm())
                    .filename(existing.getModelNm())
                    .splitCount(fileDivCnt)
                    .build());
        } else {
            currentFileIndex++;
            log.info("{}status=INTERNAL_NETWORK_IMPORT_COMPLETED, step=파일 완료 수 증가, name={}, fileChkCpltCnt={}", LH, existing.getModelNm(), currentFileIndex);
            ctx.getRequest().setStatusNm(ModelGardenStatus.VACCINE_SCAN_COMPLETED.name());
            doipInfo.setFileChkCpltCnt(currentFileIndex);
            existing.setDoipInfo(doipInfo);
        }
    }

    /** 취약점 점검 결재 진행: 결재 요청자·요청 일시 설정 */
    private void handleVulnerabilityCheckApprovalInProgress(GpoUseGnynModelMas existing,
            ModelGardenStatusHandlerContext ctx) {
        existing.getDoipInfo().setChkMn(ctx.getCurrentUser());
        existing.getDoipInfo().setChkAt(LocalDateTime.now());
    }

    /**
     * 반입 완료·등록: SKT AI 모델 카탈로그 등록(관리자 토큰 + 권한 설정) 후 callUrl에 모델 ID 저장
     */
    private void handleImportCompletedRegistered(GpoUseGnynModelMas existing, ModelGardenStatusHandlerContext ctx) {
        String fileNm = existing.getModelNm().replace("/", "_");
        String modelPath = MODEL_PATH_PREFIX + fileNm + "/" + fileNm + ".zip";

        ModelCreate sktRequest = ModelCreate.builder()
                .name(existing.getModelNm())
                .description(existing.getDtlCtn())
                .type(existing.getModelTyp())
                .isValid(true)
                .size(existing.getPrmtCnt() != null ? existing.getPrmtCnt().toString() : "")
                .servingType(ctx.getServingTypeFormatter().apply(existing.getDplyTyp()))
                .providerId(existing.getSupjCoId())
                .license(existing.getCertNm() != null ? existing.getCertNm() : "")
                .tags(toTagRequests(existing.getTagCtnt()))
                .languages(toLanguageRequests(existing.getPgmDescCtnt()))
                .path(modelPath)
                .build();

        log.info("{}status=IMPORT_COMPLETED_REGISTERED, step=카탈로그 등록 요청, name={}", LH, existing.getModelNm());

        ModelRead response;
        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            adminAuthService.ensureAdminToken();
            response = sktaiModelService.registerModel(sktRequest);

            adminAuthService.setResourcePolicyByProjectSequence("/api/v1/models/" + response.getId(), -999);
        } catch (BusinessException e) {
            log.error("{}status=IMPORT_COMPLETED_REGISTERED, step=카탈로그 등록 실패, name={}", LH, existing.getModelNm(), e);
            throw e;
        } finally {
            AdminContext.clear();
        }

        existing.setCallUrl(response.getId());
    }

    /** 반입 완료·등록 해제: 카탈로그 등록 해제(callUrl null) */
    private void handleImportCompletedUnregistered(GpoUseGnynModelMas existing) {
        log.info("{}status=IMPORT_COMPLETED_UNREGISTERED, step=카탈로그 등록 취소, name={}", LH, existing.getModelNm());
        existing.setCallUrl(null);
    }


    ///////////////////////// Util /////////////////////////

    /** 콤마 구분 태그 문자열 → ModelTagRequest 목록 */
    private static List<ModelTagRequest> toTagRequests(String tagCtnt) {
        if (tagCtnt == null || tagCtnt.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(tagCtnt.split(","))
                .map(tag -> ModelTagRequest.builder().name(tag).build())
                .collect(Collectors.toList());
    }

    /** 콤마 구분 언어 문자열 → ModelLanguageRequest 목록 */
    private static List<ModelLanguageRequest> toLanguageRequests(String pgmDescCtnt) {
        if (pgmDescCtnt == null || pgmDescCtnt.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(pgmDescCtnt.split(","))
                .map(language -> ModelLanguageRequest.builder().name(language).build())
                .collect(Collectors.toList());
    }
}
