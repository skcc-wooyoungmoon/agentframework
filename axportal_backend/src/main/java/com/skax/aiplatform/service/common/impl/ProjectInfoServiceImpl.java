package com.skax.aiplatform.service.common.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentFewShotsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.dto.common.request.SetPublicRequest;
import com.skax.aiplatform.dto.common.response.AssetProjectInfoRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.home.GpoProjectsRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.common.ProjectInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectInfoServiceImpl implements ProjectInfoService {

    private final AdminAuthService adminAuthService;
    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final GpoProjectsRepository projectsRepository;
    private final GpoUsersMasRepository usersMasRepository;
    private final SktaiAgentFewShotsService sktaiAgentFewShotsService;

    /**
     * 공개 설정
     *
     * @param type 타입 (agent, model, few-shot, guardrail, tool, mcp)
     * @param id   ID
     */
    @Override
    @Transactional
    public void setPublicFromPrivate(SetPublicRequest setPublicRequest) {
        log.info("자산 공개 설정 시작 - type: {}, id: {}", setPublicRequest.getType(), setPublicRequest.getId());

        String type = setPublicRequest.getType();
        String id = setPublicRequest.getId();

        List<String> resourceUrls = new ArrayList<>();
        if (type.equals("agent")) {
            resourceUrls.add("/api/v1/agent/agents/" + id);
            resourceUrls.add("/api/v1/agent/agents/" + id + "/versions");
            resourceUrls.add("/api/v1/agent/agents/" + id + "/versions/latest");
        } else if (type.equals("app")) {
            resourceUrls.add("/api/v1/agent/agents/apps/" + id);
            // resourceUrls.add("/api/v1/agent/agents/apps/" + id + "/deployments");
        } else if (type.equals("app-deployment")) {
            resourceUrls.add("/api/v1/agent/agents/apps/deployments/" + id);
        } else if (type.equals("agent-serving")) {
            resourceUrls.add("/api/v1/agent_servings/" + id);
        } else if (type.equals("few-shot")) {
            resourceUrls.add("/api/v1/agent/few-shots/" + id);
            // resourceUrls.add("/api/v1/agent/few-shots/versions/" + id);
            // resourceUrls.add("/api/v1/agent/few-shots/versions/" + id + "/latest");

            // // Few-Shot 버전별 리소스 URL 추가
            // FewShotVersionsResponse response = sktaiAgentFewShotsService.getFewShotVersions(id);
            // if (response != null && response.getData() != null) {
            //     for (FewShotVersionsResponse.FewShotVersionDetail versionDetail : response.getData()) {
            //         if (versionDetail == null || versionDetail.getVersionId() == null) {
            //             continue;
            //         }
            //         resourceUrls.add("/api/v1/agent/few-shots/items/" + versionDetail.getVersionId());
            //         resourceUrls.add("/api/v1/agent/few-shots/tags/" + versionDetail.getVersionId());
            //     }
            // }
        } else if (type.equals("tool")) {
            resourceUrls.add("/api/v1/agent/tools/" + id);
        } else if (type.equals("mcp")) {
            resourceUrls.add("/api/v1/mcp/catalogs/" + id);
            // resourceUrls.add("/api/v1/mcp/catalogs/" + id + "/tools");
            // resourceUrls.add("/api/v1/mcp/catalogs/" + id + "/sync-tools");
            // resourceUrls.add("/api/v1/mcp/catalogs/" + id + "/ping");
        } else if (type.equals("infer-prompts")) {
            resourceUrls.add("/api/v1/agent/inference-prompts/" + id);
            // resourceUrls.add("/api/v1/agent/inference-prompts/prompt/" + id);
            // resourceUrls.add("/api/v1/agent/inference-prompts/versions/" + id);
            // resourceUrls.add("/api/v1/agent/inference-prompts/versions/" + id + "/latest");
            // resourceUrls.add("/api/v1/lineages/" + id + "/upstream");
            //
            // // Inference Prompt 버전별 리소스 URL 추가
            // PromptVersionsResponse response = sktaiAgentInferencePromptsService.getInferencePromptVersions(id);
            // if (response != null && response.getData() != null) {
            //     for (PromptVersionsResponse.VersionData versionDetail : response.getData()) {
            //         if (versionDetail == null || versionDetail.getVersionId() == null) {
            //             continue;
            //         }
            //         resourceUrls.add("/api/v1/agent/inference-prompts/variables/" + versionDetail.getVersionId());
            //         resourceUrls.add("/api/v1/agent/inference-prompts/messages/" + versionDetail.getVersionId());
            //         resourceUrls.add("/api/v1/agent/inference-prompts/tags/" + versionDetail.getVersionId());
            //     }
            // }
        } else if (type.equals("graph")) {
            resourceUrls.add("/api/v1/agent/agents/graphs/" + id);
        } else if (type.equals("model")) {
            resourceUrls.add("/api/v1/servings/" + id);
        } else if (type.equals("knowledge")) {
            resourceUrls.add("/api/v1/knowledge/" + id);
        } else if (type.equals("knowledge-external")) {
            resourceUrls.add("/api/v1/knowledge/repos/" + id);
        } else if (type.equals("dataset")) {
            resourceUrls.add("/datasets/" + id);
            resourceUrls.add("/datasets/" + id + "/tags");
        } else if (type.equals("datasource")) {
            resourceUrls.add("/datasources/" + id);
            resourceUrls.add("/datasources/" + id + "/files");
        } else if (type.equals("model-ctlg")) {
            resourceUrls.add("/api/v1/models/" + id);
        } else if (type.equals("finetuning")) {
            resourceUrls.add("/api/v1/backend-ai/finetuning/trainings/" + id);
        } else if (type.equals("guardrails")) {
            resourceUrls.add("/api/v1/agent/guardrails/" + id);
        } else if (type.equals("safety-filter")) {
            resourceUrls.add("/safety-filters/groups/" + id);
        } else {
            log.warn("지원하지 않는 자산 타입입니다 - type: {}, id: {}", type, id);
            return;
        }

        if (resourceUrls.isEmpty()) {
            log.warn("자산 공개 설정할 리소스 URL이 없습니다 - type: {}, id: {}", type, id);
            return;
        }

        log.info("자산 공개 설정할 리소스 URL 개수: {}", resourceUrls.size());
        for (String resourceUrl : resourceUrls) {
            log.info("자산 공개 설정 요청 - resourceUrl: {}", resourceUrl);
            try {
                adminAuthService.setResourcePublicPolicyFromPrivate(resourceUrl);
                log.info("자산 공개 설정 완료 - resourceUrl: {}", resourceUrl);
            } catch (BusinessException e) {
                // 비즈니스 예외는 경고만 기록 (일부 실패해도 계속 진행)
                log.warn("자산 공개 설정 실패 (BusinessException) - resourceUrl: {}, error: {}", resourceUrl, e.getMessage());
            } catch (DataAccessException e) {
                // 데이터베이스 접근 오류는 경고만 기록
                log.warn("자산 공개 설정 실패 (데이터베이스 오류) - resourceUrl: {}, error: {}", resourceUrl, e.getMessage());
            } catch (IllegalArgumentException | NullPointerException e) {
                // 잘못된 인자나 null 참조 예외는 경고만 기록
                log.warn("자산 공개 설정 실패 (잘못된 인자) - resourceUrl: {}, error: {}", resourceUrl, e.getMessage());
            } catch (Exception e) {
                // 기타 예상치 못한 예외
                log.error("자산 공개 설정 실패 (예상치 못한 오류) - resourceUrl: {}, error: {}", resourceUrl, e.getMessage(), e);
            }
        }
        log.info("자산 공개 설정 완료 - type: {}, id: {}, 총 {}개 리소스 처리", type, id, resourceUrls.size());
    }

    /**
     * UUID로 자산-프로젝트 매핑 정보 조회
     *
     * <p>
     * asst_url LIKE '%{uuid}'로 조회하여,
     * 여러 행이 있을 수 있지만 모든 행의 값이 동일하므로 첫 번째 행만 사용합니다.
     * </p>
     *
     * <p>
     * 반환 정보:
     * <ul>
     * <li>lst_prj_seq: 최종 프로젝트 SEQ</li>
     * <li>userBy: updated_by가 null이 아니면 updated_by, null이면 created_by</li>
     * <li>dateAt: lst_updated_at이 null이 아니면 lst_updated_at, null이면
     * fst_created_at</li>
     * </ul>
     * </p>
     *
     * @param uuid 자산 UUID
     * @return 자산-프로젝트 매핑 정보
     */
    @Override
    @Transactional
    public AssetProjectInfoRes getAssetProjectInfoByUuid(String uuid) {
        log.info("자산-프로젝트 매핑 정보 조회 시작 - uuid: {}", uuid);

        // asst_url LIKE '%{uuid}'로 조회
        List<GpoAssetPrjMapMas> mappings = assetPrjMapMasRepository.findByAsstUrlContaining(uuid);

        log.info("조회된 매핑 개수: {}", mappings != null ? mappings.size() : 0);

        if (mappings == null || mappings.isEmpty()) {
            log.warn("자산-프로젝트 매핑 정보를 찾을 수 없습니다 - uuid: {}", uuid);
            return null;
        }

        // 여러 행이 있을 수 있지만 모든 행의 값이 동일하므로 첫 번째 행만 사용
        GpoAssetPrjMapMas mapping = mappings.get(0);

        // lst_prj_seq, fst_prj_seq 가져오기
        Integer lstPrjSeq = mapping.getLstPrjSeq();
        Integer fstPrjSeq = mapping.getFstPrjSeq();

        // lst_prj_seq가 양수인 경우 gpo_projects_mas에서 prj_nm 조회
        String lstPrjNm = null;
        if (lstPrjSeq != null && lstPrjSeq > 0) {
            Optional<Project> lstProject = projectsRepository.findById(lstPrjSeq.longValue());
            if (lstProject.isPresent()) {
                lstPrjNm = lstProject.get().getPrjNm();
            } else {
                lstPrjNm = "Public";
            }
        } else {
            lstPrjNm = "Public";
        }

        // fst_prj_seq로 gpo_projects_mas에서 prj_nm 조회
        String fstPrjNm = null;
        if (fstPrjSeq != null) {
            Optional<Project> fstProject = projectsRepository.findById(fstPrjSeq.longValue());
            if (fstProject.isPresent()) {
                fstPrjNm = fstProject.get().getPrjNm();
            }
        }

        // updated_by, lst_updated_at이 null이 아니면 이것들을 사용, null이면 created_by,
        // fst_created_at 사용
        String userBy = (mapping.getUpdatedBy() != null) ? mapping.getUpdatedBy() : mapping.getCreatedBy();
        LocalDateTime dateAt = (mapping.getLstUpdatedAt() != null)
                ? mapping.getLstUpdatedAt()
                : mapping.getFstCreatedAt();

        // userBy로 gpo_users_mas에서 jkw_nm, dept_nm, retr_jkw_yn 조회
        String jkwNm = null;
        String deptNm = null;
        Integer retrJkwYn = null;
        if (userBy != null) {
            Optional<GpoUsersMas> user = usersMasRepository.findByMemberId(userBy);
            if (user.isPresent()) {
                GpoUsersMas userMas = user.get();
                jkwNm = userMas.getJkwNm();
                deptNm = userMas.getDeptNm();
                retrJkwYn = userMas.getRetrJkwYn();
            }
        }

        return AssetProjectInfoRes.builder()
                .lstPrjNm(lstPrjNm)
                .fstPrjNm(fstPrjNm)
                .userBy(userBy)
                .jkwNm(jkwNm)
                .deptNm(deptNm)
                .retrJkwYn(retrJkwYn)
                .dateAt(DateUtils.toDateTimeString(dateAt))
                .build();
    }

}
