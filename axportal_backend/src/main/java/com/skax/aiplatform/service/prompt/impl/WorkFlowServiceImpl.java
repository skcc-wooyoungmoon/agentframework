package com.skax.aiplatform.service.prompt.impl;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.prompt.request.WorkFlowBatchDeleteReq;
import com.skax.aiplatform.dto.prompt.request.WorkFlowCreateReq;
import com.skax.aiplatform.dto.prompt.request.WorkFlowUpdateReq;
import com.skax.aiplatform.dto.prompt.response.WorkFlowCreateRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowDeleteRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowRes;
import com.skax.aiplatform.dto.prompt.response.WorkFlowVerListByIdRes;
import com.skax.aiplatform.entity.prompt.WorkFlow;
import com.skax.aiplatform.mapper.prompt.WorkFlowMapper;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.admin.ProjectUserRoleRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.prompt.WorkFlowRepository;
import com.skax.aiplatform.service.prompt.WorkFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 워크플로우 서비스 구현체
 *
 * <p>워크플로우 관리 비즈니스 로직을 구현하는 서비스 클래스입니다.
 * SKTAI 워크플로우 API와의 연동을 통해 워크플로우 CRUD 작업을 수행합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>워크플로우 목록 조회 (페이지네이션 지원)</li>
 *   <li>워크플로우 상세 정보 조회</li>
 *   <li>새로운 워크플로우 생성</li>
 *   <li>워크플로우 정보 수정</li>
 *   <li>워크플로우 삭제</li>
 * </ul>
 *
 * @author yunyoseob
 * @version 0.0.1
 * @since 2025-09-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkFlowServiceImpl implements WorkFlowService {

    private final WorkFlowRepository workFlowRepository;
    private final ProjectMgmtRepository projectMgmtRepository;
    private final GpoUsersMasRepository gpoUsersMasRepository;
    private final WorkFlowMapper workFlowMapper;
    private final TokenInfo tokenInfo;
    private final ProjectUserRoleRepository projectUserRoleRepository;

    /**
     * UUID 형식의 workflowId를 8자리 형식으로 변환
     * 예: "ED93FBF3-0000-0000-0000-000000000000" -> "ED93FBF3"
     */
    private String extractWorkflowId(String workFlowId) {
        if (workFlowId == null || workFlowId.isBlank()) {
            return workFlowId;
        }
        // UUID 형식(36자)이면 앞 8자리만 추출
        if (workFlowId.length() >= 36 && workFlowId.contains("-")) {
            return workFlowId.substring(0, 8).toUpperCase();
        }
        // 이미 8자리면 그대로 반환
        return workFlowId.toUpperCase();
    }

    private String mapSortKey(String key) {
        return switch (key) {
            case "created_at" -> "fstCreatedAt";
            case "updated_at" -> "lst_updated_at";
            case "workflow_name" -> "workflowName";
            case "project_seq" -> "projectSeq";
            case "tag" -> "tag";
            case "versionNo" -> "versionNo";
            default -> null;
        };
    }

    @Override
    public PageResponse<WorkFlowRes> getWorkFlowList(Pageable pageable, String search, String tag, String sort) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service WorkFlowServiceImpl.getWorkFlowList ]");
        log.info("Page                  : {}", pageable.getPageNumber());
        log.info("Size                  : {}", pageable.getPageSize());
        log.info("Search                : {}", search);
        log.info("Sort                  : {}", sort);
        log.info("-----------------------------------------------------------------------------------------");
        try {
            int rawPage = pageable.getPageNumber();
            int size = pageable.getPageSize();
            Sort sortObj = pageable.getSort();

            // 정렬 문자열 우선 적용
            if (sort != null && !sort.isBlank()) {
                String[] parts = sort.split(",", 2);
                String key = parts[0].trim().toLowerCase();
                String dir = parts.length == 2 ? parts[1].trim().toLowerCase() : "asc";
                String prop = mapSortKey(key);
                if (prop != null) {
                    sortObj = "desc".equals(dir) ? Sort.by(Sort.Order.desc(prop)) : Sort.by(Sort.Order.asc(prop));
                }
            }

            pageable = PageRequest.of(Math.max(0, rawPage - 1), size, sortObj);

            // DB 조회
            Page<WorkFlow> page = workFlowRepository.findWorkFlowsBySearch(
                    getCurrentProjectSeq(),
                    (search == null || search.isBlank()) ? null : search,
                    (tag == null || tag.isBlank()) ? null : tag,
                    pageable);

            // 엔티티 → DTO 변환
            List<WorkFlowRes> content = page.getContent().stream()
                    .map(workFlowMapper::toDto)
                    .toList();

            // DTO 리스트로 새 Page 객체 생성
            Page<WorkFlowRes> mappedPage = new PageImpl<>(
                    content,
                    page.getPageable(),
                    page.getTotalElements());

            return PageResponse.from(mappedPage);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("워크플로우 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public List<String> getWorkFlowTagList() {
        return workFlowRepository.findDistinctTags(getCurrentProjectSeq()).stream()
                .filter(tag -> !tag.isBlank())
                .flatMap(tag -> List.of(tag.split(",")).stream())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private long getCurrentProjectSeq() {
        String username = tokenInfo.getUserName();
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회 실패");
        }

        // 현재 사용자의 활성 프로젝트 조회
        Long prjSeq = projectUserRoleRepository.findActivePrjSeqByMemberId(username)
                .orElse(null);
        if (prjSeq == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활성 프로젝트 조회 실패");
        }

        return prjSeq;
    }

    @Override
    @Transactional
    public WorkFlowCreateRes createWorkflow(WorkFlowCreateReq request) {
        try {
            // wf_id 생성: 8자리 (예: WFL00001)
            String wfId = generateWfId();
            int versionNo = 1;

            String xml = (request.getXmlText() == null || request.getXmlText().isBlank())
                    ? "<xml/>"
                    : request.getXmlText();

            // isActive: char 'Y'/'N' -> Integer 1/0 변환
            Integer active = (request.getIsActive() == 'Y' || request.getIsActive() == 'y') ? 1 : 0;

            WorkFlow entity = WorkFlow.builder()
                    .workflowId(wfId)
                    .workflowName(request.getWorkflowName())
                    .versionNo(versionNo)
                    .description(request.getDescription())
                    .isActive(active)
                    .tag(request.getTag())
                    .projectSeq(getCurrentProjectSeq())
                    .projectScope("")
                    .build();
            entity.setXmlText(xml);

            WorkFlow saved = workFlowRepository.save(entity);

            return WorkFlowCreateRes.builder()
                    .workFlowId(saved.getWorkflowId())
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("워크플로우 생성 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * wf_id 생성 (8자리)
     */
    private String generateWfId() {
        // UUID 앞 8자리 사용
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    @Override
    public WorkFlowVerListByIdRes getWorkFlowVerListById(String workFlowId) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service WorkFlowServiceImpl.getWorkFlowVerListById ]");
        log.info("WorkFlow ID            : {}", workFlowId);
        log.info("-----------------------------------------------------------------------------------------");

        try {
            String actualWorkFlowId = extractWorkflowId(workFlowId);
            List<WorkFlow> workFlowVerListByIdList = workFlowRepository.findVersionsByWorkflowId(actualWorkFlowId);
            var versions = workFlowVerListByIdList.stream()
                    .map(w -> WorkFlowVerListByIdRes.VersionItem.builder()
                            .versionNo(w.getVersionNo())
                            .createdAt(w.getFstCreatedAt() != null
                                    ? w.getFstCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                    : null)
                            .updatedAt(w.getLstUpdatedAt() != null
                                    ? w.getLstUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                    : null)
                            .build())
                    .toList();

            return WorkFlowVerListByIdRes.builder()
                    .workFlowId(workFlowId)
                    .totalVersions(versions.size())
                    .versions(versions)
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("워크플로우 버전 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 버전 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public WorkFlowRes getWorkFlowLatestVerById(String workFlowId) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service WorkFlowServiceImpl.getWorkFlowLatestVerById ]");
        log.info("WorkFlow ID            : {}", workFlowId);
        log.info("-----------------------------------------------------------------------------------------");
        try {
            String actualWorkFlowId = extractWorkflowId(workFlowId);
            WorkFlow workFlow = workFlowRepository.findLatestByWorkflowId(actualWorkFlowId);
            if (workFlow == null) {
                throw new BusinessException(
                        ErrorCode.EXTERNAL_API_ERROR,
                        "해당 워크플로우의 최신 버전을 찾을 수 없습니다. workflowId=" + workFlowId);
            }
            // Mapper로 DTO 변환
            return workFlowMapper.toDto(workFlow);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("워크플로우 최신 버전 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 최신 버전 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public WorkFlowRes getWorkFlowVerById(String workFlowId, Integer versionNo) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service WorkFlowServiceImpl.getWorkFlowLatestVerById ]");
        log.info("WorkFlow ID               : {}", workFlowId);
        log.info("Version No                : {}", versionNo);
        log.info("-----------------------------------------------------------------------------------------");
        try {
            String actualWorkFlowId = extractWorkflowId(workFlowId);
            WorkFlow workFlow = workFlowRepository.findByWorkflowIdAndVersion(actualWorkFlowId, versionNo);
            if (workFlow == null) {
                throw new BusinessException(
                        ErrorCode.EXTERNAL_API_ERROR,
                        "해당 워크플로우의 특정 버전을 찾을 수 없습니다. workflowId=" + workFlowId);
            }
            // Mapper로 DTO 변환
            return workFlowMapper.toDto(workFlow);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("워크플로우 특정 버전 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 특정 버전 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateWorkFlow(String workflowId, WorkFlowUpdateReq request) {
        try {
            String actualWorkFlowId = extractWorkflowId(workflowId);

            // 1) 최신 버전 조회(존재 확인 + 이름 비교용)
            WorkFlow latest = workFlowRepository
                    .findTopByWorkflowIdOrderByVersionNoDesc(actualWorkFlowId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                            "워크플로우가 없습니다: " + workflowId));

            // 2) 이름 변경 필요 시, 해당 ID 전 레코드 rename
            String newName = request.getWorkflowName();
            if (!latest.getWorkflowName().equals(newName)) {
                workFlowRepository.renameAllByWorkflowId(actualWorkFlowId, newName);
            }

            // 3) 다음 버전 계산
            int nextVersion = workFlowRepository.findMaxVersionNo(actualWorkFlowId) + 1;

            // isActive: char 'Y'/'N' -> Integer 1/0 변환
            char activeChar = Character.toUpperCase(request.getIsActive());
            if (activeChar != 'Y' && activeChar != 'N') {
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "isActive 값은 Y/N 이어야 합니다.");
            }
            Integer active = (activeChar == 'Y') ? 1 : 0;

            // 새 버전의 wf_id 생성 (8자리)
            String newWfId = generateWfId();

            WorkFlow newEntity = WorkFlow.builder()
                    .workflowId(newWfId)
                    .workflowName(newName)
                    .versionNo(nextVersion)
                    .description(request.getDescription())
                    .isActive(active)
                    .tag(request.getTag())
                    .projectSeq(latest.getProjectSeq())
                    .projectScope(latest.getProjectScope())
                    .build();
            newEntity.setXmlText(request.getXmlText());

            workFlowRepository.save(newEntity);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("워크플로우 업데이트 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteWorkFlowById(String workflowId) {
        log.info("워크플로우 삭제 요청: workflowId={}", workflowId);

        String actualWorkFlowId = extractWorkflowId(workflowId);

        if (!workFlowRepository.existsByWorkflowId(actualWorkFlowId)) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우가 없습니다: " + workflowId);
        }
        try {
            int deleted = workFlowRepository.deleteAllByWorkflowId(actualWorkFlowId);
            if (deleted <= 0) {
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "삭제할 레코드가 없습니다: " + workflowId);
            }
            log.info("워크플로우 삭제 성공: workflowId={}, deleted={}", workflowId, deleted);
        } catch (BusinessException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("워크플로우 삭제 실패 (DataAccessException): workflowId={}", workflowId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "워크플로우 삭제 중 데이터베이스 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("워크플로우 삭제 실패 (RuntimeException): workflowId={}", workflowId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public WorkFlowDeleteRes deleteWorkFlowsByIds(WorkFlowBatchDeleteReq request) {
        log.info("워크플로우 일괄 삭제 요청: ids={}", request.getIds());

        if (request.getIds() == null || request.getIds().isEmpty()) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "삭제할 워크플로우 ID가 없습니다.");
        }

        List<String> ids = request.getIds();
        int totalCount = ids.size();
        int successCount = 0;
        int failCount = 0;

        for (String id : ids) {
            try {
                String actualWorkFlowId = extractWorkflowId(id);

                if (workFlowRepository.existsByWorkflowId(actualWorkFlowId)) {
                    int deleted = workFlowRepository.deleteAllByWorkflowId(actualWorkFlowId);
                    if (deleted > 0) {
                        successCount++;
                        log.info("워크플로우 삭제 성공: workflowId={}, deleted={}", actualWorkFlowId, deleted);
                    } else {
                        failCount++;
                        log.warn("워크플로우 삭제 실패 (레코드 없음): workflowId={}", actualWorkFlowId);
                    }
                } else {
                    failCount++;
                    log.warn("워크플로우 존재하지 않음: workflowId={}", actualWorkFlowId);
                }
            } catch (BusinessException e) {
                failCount++;
                log.error("워크플로우 삭제 중 오류 (BusinessException): workflowId={}", id, e);
            } catch (DataAccessException e) {
                failCount++;
                log.error("워크플로우 삭제 중 오류 (DataAccessException): workflowId={}", id, e);
            } catch (RuntimeException e) {
                failCount++;
                log.error("워크플로우 삭제 중 오류 (RuntimeException): workflowId={}", id, e);
            }
        }

        log.info("워크플로우 일괄 삭제 완료: total={}, success={}, fail={}", totalCount, successCount, failCount);

        return WorkFlowDeleteRes.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failCount(failCount)
                .build();
    }

    @Override
    @Transactional
    public void makeWorkFlowPublic(String workflowId) {
        log.info("워크플로우 공개 설정 요청: workflowId={}", workflowId);

        String actualWorkFlowId = extractWorkflowId(workflowId);

        if (!workFlowRepository.existsByWorkflowId(actualWorkFlowId)) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우가 없습니다: " + workflowId);
        }
        try {
            WorkFlow latestByWorkflowId = workFlowRepository.findLatestByWorkflowId(actualWorkFlowId);
            int updated = workFlowRepository.updateProjectSeqToPublic(
                    actualWorkFlowId,
                    gpoUsersMasRepository.findByMemberId(tokenInfo.getUserName()).get().getJkwNm(),
                    projectMgmtRepository.findByPrjSeq(latestByWorkflowId.getProjectSeq()).get().getPrjNm());
            if (updated <= 0) {
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "업데이트할 레코드가 없습니다: " + workflowId);
            }
            log.info("워크플로우 공개 설정 성공: workflowId={}, updated={}", workflowId, updated);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("워크플로우 공개 설정 실패: workflowId={}", workflowId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "워크플로우 공개 설정 중 오류");
        }
    }

}
