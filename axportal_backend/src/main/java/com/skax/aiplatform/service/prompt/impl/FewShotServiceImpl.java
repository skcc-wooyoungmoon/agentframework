package com.skax.aiplatform.service.prompt.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotItemsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphAppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentFewShotsService;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentGraphsService;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.client.sktai.lineage.service.SktaiLineageService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.lineage.response.LineageRelationRes;
import com.skax.aiplatform.dto.prompt.request.FewShotCreateReq;
import com.skax.aiplatform.dto.prompt.request.FewShotUpdateReq;
import com.skax.aiplatform.dto.prompt.response.FewShotCreateRes;
import com.skax.aiplatform.dto.prompt.response.FewShotItemRes;
import com.skax.aiplatform.dto.prompt.response.FewShotLineageRes;
import com.skax.aiplatform.dto.prompt.response.FewShotRes;
import com.skax.aiplatform.dto.prompt.response.FewShotTagListRes;
import com.skax.aiplatform.dto.prompt.response.FewShotTagRes;
import com.skax.aiplatform.dto.prompt.response.FewShotVerRes;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.lineage.LineageMapper;
import com.skax.aiplatform.mapper.prompt.FewShotMapper;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.prompt.FewShotService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Few-Shot 관리 서비스 구현체
 * 
 * <p>Few-Shot 예제 데이터 관리를 위한 비즈니스 로직을 구현합니다.
 * SKTAX Agent Few-Shots API와 연동하여 작동합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-11
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FewShotServiceImpl implements FewShotService {

    private static final String DEFAULT_FEW_SHOT_FILTER = "tags:FewShot";
    private static final int TAG_FETCH_PAGE_SIZE = 100;

    private final SktaiAgentFewShotsService sktaiAgentFewShotsService;
    private final SktaiLineageService sktaiLineageService;
    private final SktaiAgentGraphsService sktaiAgentGraphsService;
    private final AdminAuthService adminAuthService;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;

    private final LineageMapper lineageMapper;
    private final FewShotMapper fewShotMapper;

    private final SktaiAuthService sktaiAuthService;


    @Override
    public PageResponse<FewShotRes> getFewShotList(String projectId, Integer page, Integer size, 
                                       String sort, String filter, String search, Boolean release_only) {
        try {
            FewShotsResponse response = sktaiAgentFewShotsService.getFewShots(projectId, page, size, sort, filter, search, release_only);

            // Few-Shot 목록 변환
            List<FewShotRes> fewShotList = response.getData().stream()
                    .map(fewShotMapper::from)
                    .collect(Collectors.toList());
            
            // 각 FewShot에 대해 Lineage 조회 및 AGENT_GRAPH 필터링
            for (FewShotRes fewShot : fewShotList) {
                try {
                    // FewShot ID로 Lineage 조회 (downstream 방향으로 - FewShot에서 나가는 관계)
                    List<LineageRelationWithTypes> lineageRelations = sktaiLineageService.getLineageByObjectKeyAndDirection(
                            fewShot.getUuid(), 
                            Direction.UPSTREAM, 
                            ActionType.USE.getValue(), 
                            5
                    );
                    
                    // SourceType AGENT_GRAPH인 것만 필터링
                    List<LineageRelationWithTypes> agentGraphRelations = lineageRelations.stream()
                            .filter(relation -> ObjectType.AGENT_GRAPH.equals(relation.getSourceType()))
                            .collect(Collectors.toList());
                    
                    Boolean deployed = false;

                    // Lineage 정보를 FewShotRes에 설정
                    List<LineageRelationRes> agentGraphRelationRes = lineageMapper.toLineageRelationResList(agentGraphRelations);
                    // 배포 여부 계산: 연결된 AGENT_GRAPH 중 하나라도 배포 정보가 있으면 true
                    for (LineageRelationRes rel : agentGraphRelationRes) {
                        String sourceGraphId = rel.getSourceKey();
                        try {
                            GraphAppResponse graphAppResponse = sktaiAgentGraphsService.getGraphAppInfo(sourceGraphId);
                            if (graphAppResponse != null && graphAppResponse.getData() != null) {
                                deployed = true;
                                break;
                            }
                        } catch (FeignException e) {
                            // 배포 정보 조회 실패는 정상적인 경우일 수 있음 (배포되지 않은 Graph)
                            log.debug("Graph {} App 정보 조회 실패 (배포되지 않음): {}", sourceGraphId, e.getMessage());
                        } catch (Exception e) {
                            // 예상치 못한 오류는 debug 레벨로 로깅
                            log.debug("Graph {} App 정보 조회 중 예상치 못한 오류: {}", sourceGraphId, e.getMessage());
                        }
                    }

                    // 공개 여부 설정 (lst_prj_seq 값에 따라)
                    GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl("/api/v1/agent/few-shots/" + fewShot.getUuid()).orElse(null);
                    String publicStatus = null;
                    if (existing != null && existing.getLstPrjSeq() != null) {
                        // 음수면 "전체공유", 양수면 "내부공유"
                        publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";
                    } else {
                        publicStatus = "전체공유"; // null 인 경우 전체공유로 설정
                    }

                    fewShot.setPublicStatus(publicStatus);
                    fewShot.setDeployed(deployed);
                    fewShot.setAgentGraphRelations(agentGraphRelationRes);
                    
                    // 연결된 에이전트 수 설정 (AGENT_GRAPH 관계 개수)
                    fewShot.setConnectedAgentCount(agentGraphRelations.size());
                    
                } catch (FeignException e) {
                    log.warn("FewShot {} Lineage 조회 실패 (FeignException): {}", fewShot.getUuid(), e.getMessage());
                    // Lineage 조회 실패 시 기본값 설정
                    fewShot.setConnectedAgentCount(0);
                    fewShot.setAgentGraphRelations(List.of()); // 빈 리스트로 설정
                } catch (RuntimeException e) {
                    log.warn("FewShot {} Lineage 조회 실패 (RuntimeException): {}", fewShot.getUuid(), e.getMessage());
                    // Lineage 조회 실패 시 기본값 설정
                    fewShot.setConnectedAgentCount(0);
                    fewShot.setAgentGraphRelations(List.of()); // 빈 리스트로 설정
                }
            }

            // ADXP Pagination을 PageResponse로 변환
            return PaginationUtils.toPageResponseFromAdxp(response.getPayload(), fewShotList);
            
        } catch (FeignException e) {
            log.error("Few-Shot 목록 조회 실패: projectId={}, 에러={}", projectId, e.getMessage());
            log.debug("Few-Shot 목록 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Few-Shot 목록을 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public FewShotCreateRes createFewShot(FewShotCreateReq request) {
        try {
            FewShotCreateRequest createRequest = fewShotMapper.toNewCreateRequest(request);
            FewShotCreateResponse createResponse = sktaiAgentFewShotsService.createFewShot(createRequest);
            adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/few-shots/" + createResponse.getData().getFewShotUuid());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/few-shots/versions/" + createResponse.getData().getFewShotUuid());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/few-shots/versions/" + createResponse.getData().getFewShotUuid()+ "/latest");;
            
            // Few-Shot API 호출
            // FewShotVersionsResponse response = sktaiAgentFewShotsService.getFewShotVersions(createResponse.getData().getFewShotUuid());

            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/few-shots/items/" + response.getData().get(0).getVersionId());
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/few-shots/tags/" + response.getData().get(0).getVersionId());

            // 응답을 FewShotRes로 변환
            FewShotCreateRes fewShotCreateRes = fewShotMapper.toNewCreateResponse(createResponse);
            
            return fewShotCreateRes;
            
        } catch (FeignException e) {
            log.error("Few-Shot 생성 실패: name={}, 에러={}", request.getName(), e.getMessage());
            log.debug("Few-Shot 생성 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Few-Shot을 생성할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public FewShotRes getFewShotById(String fewShotUuid) {
        try {
            FewShotResponse response = sktaiAgentFewShotsService.getFewShot(fewShotUuid);
            FewShotRes fewShotRes = fewShotMapper.toNewResponseForById(response);
            return fewShotRes;
            
        } catch (FeignException e) {
            log.error("Few-Shot 상세 조회 실패: fewShotUuid={}, 에러={}", fewShotUuid, e.getMessage());
            log.debug("Few-Shot 상세 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Few-Shot 정보를 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateFewShotById(String fewShotUuid, FewShotUpdateReq request) {
        try {
            // FewShotUpdateReq를 FewShotUpdateRequest로 변환
            FewShotUpdateRequest updateRequest = fewShotMapper.toNewUpdateRequest(fewShotUuid, request);


            sktaiAgentFewShotsService.updateFewShot(fewShotUuid, updateRequest);

            // Few-Shot 새 버전 조회 및 리소스 정책 설정
            FewShotVersionsResponse response = sktaiAgentFewShotsService.getFewShotVersions(fewShotUuid);

            // 새 버전이 존재하지 않으면 리소스 정책 설정하지 않음
            if(assetPrjMapMasRepository.findByAsstUrlContaining(response.getData().get(0).getVersionId()).isEmpty()){
                adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/few-shots/items/" + response.getData().get(0).getVersionId());
                adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/few-shots/tags/" + response.getData().get(0).getVersionId());
            }
            
        } catch (FeignException e) {
            log.error("Few-Shot 수정 중 예상치 못한 오류: fewShotUuid={}, 에러={}", fewShotUuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Few-Shot을 수정할 수 없습니다: " + e.getMessage());
        }   
    }

    @Override
    @Transactional
    public void deleteFewShotById(String fewShotUuid) {
        try {
            sktaiAgentFewShotsService.deleteFewShot(fewShotUuid);
        } catch (FeignException e) {
            log.error("Few-Shot 삭제 중 예상치 못한 오류: fewShotUuid={}, 에러={}", fewShotUuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Few-Shot을 삭제할 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public FewShotVerRes getLtstFewShotVerById(String fewShotUuid) {
        
        log.info("Few-Shot 최신 버전 조회 요청: fewShotUuid={}", fewShotUuid);
        
        try {
            FewShotVersionResponse response = sktaiAgentFewShotsService.getLatestFewShotVersion(fewShotUuid);
            FewShotVerRes fewShotRes = fewShotMapper.toNewResponseForVersion(response);
            return fewShotRes;
            
        } catch (FeignException e) {
            log.error("Few-Shot 최신 버전 조회 실패: fewShotUuid={}, 에러={}", fewShotUuid, e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Few-Shot 최신 버전을 조회할 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public List<FewShotVerRes> getFewShotVerListById(String fewShotUuid) {
        
        log.info("Few-Shot 버전 목록 조회 요청: fewShotUuid={}", fewShotUuid);
        
        try {
            FewShotVersionsResponse response = sktaiAgentFewShotsService.getFewShotVersions(fewShotUuid);
            List<FewShotVerRes> fewShotRes = fewShotMapper.toNewResponseForVersions(response);
            return fewShotRes;
            
        } catch (FeignException e) {
            log.error("Few-Shot 버전 목록 조회 실패: fewShotUuid={}, 에러={}", fewShotUuid, e.getMessage());
            log.debug("Few-Shot 버전 목록 조회 실패 상세: {}", e.contentUTF8());
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "Few-Shot 버전 목록을 조회할 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public List<FewShotItemRes> getFewShotItemListById(String versionId, Integer page, Integer size,
                                          String sort, String filter, String search) {
        try {
            FewShotItemsResponse response = sktaiAgentFewShotsService.getFewShotItems(versionId, page, size, sort, filter, search);
            List<FewShotItemRes> fewShotRes = fewShotMapper.toNewResponseForItems(response);
            return fewShotRes;
            
        } catch (FeignException e) {
            log.error("Few-Shot 아이템 목록 조회 실패: versionId={}, 에러={}", versionId, e.getMessage());
            log.debug("Few-Shot 아이템 목록 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Few-Shot 아이템 목록을 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FewShotTagRes> getFewShotTagsByVerId(String versionId) {
        try {
            // Few-Shot API 호출
            FewShotTagsResponse response = sktaiAgentFewShotsService.getFewShotTagsByVersion(versionId);
            
            // FewShotTagsResponse를 List<FewShotTagRes>로 변환
            List<FewShotTagRes> fewShotRes = fewShotMapper.toNewResponseForTags(response);

            return fewShotRes;
            
        } catch (FeignException e) {
            log.error("Few-Shot 태그 조회 실패: versionId={}, 에러={}", versionId, e.getMessage());
            log.debug("Few-Shot 태그 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Few-Shot 태그를 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public FewShotTagListRes getFewShotTagList() {
        log.info("[ Execute Service FewShotServiceImpl.getFewShotTagList ]");

        Set<String> tagAccumulator = collectDistinctFewShotTags();

        List<String> sortedTags = tagAccumulator.stream()
                .sorted(String::compareToIgnoreCase)
                .toList();

        FewShotTagListRes fewShotTagListRes = new FewShotTagListRes();
        fewShotTagListRes.setTags(sortedTags);
        return fewShotTagListRes;
    }

    private Set<String> collectDistinctFewShotTags() {
        int currentPage = 1;
        Set<String> tagAccumulator = new LinkedHashSet<>();
        boolean hasNext = true;

        while (hasNext) {
            FewShotsResponse response = sktaiAgentFewShotsService.getFewShots(
                    "",
                    currentPage,
                    TAG_FETCH_PAGE_SIZE,
                    null,
                    null,
                    null,
                    false);

            if (response == null) {
                break;
            }

            List<FewShotRes> content = response.getData().stream()
                    .map(fewShotMapper::from)
                    .collect(Collectors.toList());

            content.stream()
                    .flatMap(fewShot -> Optional.ofNullable(fewShot.getTags())
                            .orElse(Collections.emptyList())
                            .stream())
                    .filter(tag -> tag != null && !tag.isBlank())
                    .forEach(tagAccumulator::add);

            Pagination pagination = Optional.ofNullable(response.getPayload())
                    .map(Payload::getPagination)
                    .orElse(null);

            if (pagination != null && pagination.getPage() != null && pagination.getLastPage() != null
                    && pagination.getPage() < pagination.getLastPage()) {
                currentPage = pagination.getPage() + 1;
            } else {
                hasNext = false;
            }
        }

        return tagAccumulator;
    }

    @Override
    public PageResponse<FewShotLineageRes> getFewShotLineageRelations(String fewShotUuid, Integer page, Integer size) {
        try {
            // 기본값 설정
            if (page == null) page = 1;
            if (size == null) size = 6;
            
            // 화면에서는 1부터 시작하므로 0부터 시작하는 백엔드 로직에 맞게 변환
            int backendPage = page - 1;
            
            // FewShot ID로 Lineage 조회 (upstream 방향으로 - FewShot에서 나가는 관계)
            List<LineageRelationWithTypes> lineageRelations = sktaiLineageService.getLineageByObjectKeyAndDirection(
                    fewShotUuid, 
                    Direction.UPSTREAM, 
                    ActionType.USE.getValue(), 
                    5
            );

            // SourceType AGENT_GRAPH인 것만 필터링
            List<LineageRelationWithTypes> agentGraphRelations = lineageRelations.stream()
                    .filter(relation -> ObjectType.AGENT_GRAPH.equals(relation.getSourceType()))
                    .collect(Collectors.toList());

            // Lineage 관계에서 sourceKey(AGENT_GRAPH ID)를 추출하여 Graph 정보 조회
            List<FewShotLineageRes> allRelations = new ArrayList<>();
            for (LineageRelationWithTypes relation : agentGraphRelations) {
                try {
                    // sourceKey AGENT_GRAPH ID라고 가정하고 Graph 정보 조회
                    String sourceGraphId = relation.getSourceKey();
                    
                    // Graph 상세 정보 조회
                    GraphResponse graphResponse = sktaiAgentGraphsService.getGraph(sourceGraphId);
                    
                    if (graphResponse == null) {
                        log.warn("Graph {} 정보가 null입니다.", sourceGraphId);
                        continue;
                    }
                    
                    // Graph App 정보 조회 (배포 여부 확인)
                    // 배포 정보 조회 실패는 배포되지 않은 것으로 간주하고 계속 진행
                    boolean deployed = false;
                    try {
                        GraphAppResponse graphAppResponse = sktaiAgentGraphsService.getGraphAppInfo(sourceGraphId);
                        deployed = graphAppResponse != null && graphAppResponse.getData() != null;
                    } catch (FeignException e) {
                        // 배포 정보 조회 실패는 정상적인 경우일 수 있음 (배포되지 않은 Graph)
                        log.debug("Graph {} App 정보 조회 실패 (배포되지 않음): {}", sourceGraphId, e.getMessage());
                        deployed = false;
                    } catch (Exception e) {
                        // 예상치 못한 오류는 debug 레벨로 로깅
                        log.debug("Graph {} App 정보 조회 중 예상치 못한 오류: {}", sourceGraphId, e.getMessage());
                        deployed = false;
                    }
                    allRelations.add(FewShotLineageRes.builder()
                            .id(graphResponse.getId())
                            .name(graphResponse.getName())
                            .description(graphResponse.getDescription())
                            .deployed(deployed)
                            .createdAt(graphResponse.getCreatedAt())
                            .updatedAt(graphResponse.getUpdatedAt()).build());
                } catch (FeignException e) {
                    // Graph 정보 조회 실패는 무시하고 다음 Graph로 계속 진행
                    log.debug("Graph {} 정보 조회 실패 (FeignException): {}", relation.getSourceKey(), e.getMessage());
                } catch (RuntimeException e) {
                    // 예상치 못한 오류는 warn 레벨로 로깅
                    log.warn("Graph {} 정보 조회 실패 (RuntimeException): {}", relation.getSourceKey(), e.getMessage());
                }
            }
            
            // 페이징 처리
            int totalElements = allRelations.size();
            int startIndex = backendPage * size;
            int endIndex = Math.min(startIndex + size, totalElements);
            
            List<FewShotLineageRes> pagedRelations;
            if (startIndex >= totalElements) {
                pagedRelations = Collections.emptyList();
            } else {
                pagedRelations = allRelations.subList(startIndex, endIndex);
            }
            
            // Page 객체 생성
            Page<FewShotLineageRes> result = new PageImpl<>(
                    pagedRelations, 
                    PageRequest.of(backendPage, size), 
                    totalElements
            );
            
            return PageResponse.from(result);
            
        } catch (FeignException e) {
            log.error("Few-Shot Lineage 관계 조회 실패 (FeignException): fewShotUuid={}, 에러={}", fewShotUuid, e.getMessage());
            // 에러 시 빈 페이지 반환
            Page<FewShotLineageRes> emptyPage = new PageImpl<>(
                    Collections.emptyList(), 
                    PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 6), 
                    0
            );
            return PageResponse.from(emptyPage);
        } catch (RuntimeException e) {
            log.error("Few-Shot Lineage 관계 조회 실패 (RuntimeException): fewShotUuid={}, 에러={}", fewShotUuid, e.getMessage());
            // 에러 시 빈 페이지 반환
            Page<FewShotLineageRes> emptyPage = new PageImpl<>(
                    Collections.emptyList(), 
                    PageRequest.of(page != null ? page - 1 : 0, size != null ? size : 6), 
                    0
            );
            return PageResponse.from(emptyPage);
        }
    }

    @Override
    @Transactional
    public List<PolicyRequest> setFewShotPolicy(String fewShotUuid, String memberId, String projectName) {
        log.info("Few-Shot Policy 설정 요청 - fewShotUuid: {}, memberId: {}, projectName: {}", fewShotUuid, memberId, projectName);

        // fewShotUuid 검증
        if (!StringUtils.hasText(fewShotUuid)) {
            log.error("Few-Shot Policy 설정 실패 - fewShotUuid가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "Few-Shot UUID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("Few-Shot Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("Few-Shot Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/few-shots/" + fewShotUuid, memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/few-shots/versions/" + fewShotUuid, memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/few-shots/versions/" + fewShotUuid + "/latest", memberId, projectName);

            // Few-Shot 버전별 리소스 URL 추가
            // FewShotVersionsResponse response = sktaiAgentFewShotsService.getFewShotVersions(fewShotUuid);
            // if (response != null && response.getData() != null) {
            //     for (FewShotVersionsResponse.FewShotVersionDetail versionDetail : response.getData()) {
            //         if (versionDetail == null || versionDetail.getVersionId() == null) {
            //             continue;
            //         }
            //         adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/few-shots/items/" + versionDetail.getVersionId(), memberId, projectName);
            //         adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/few-shots/tags/" + versionDetail.getVersionId(), memberId, projectName);
            //     }
            // }

            String resourceUrl = "/api/v1/agent/few-shots/" + fewShotUuid;
            log.info("Few-Shot Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId, projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("Few-Shot Policy 조회 결과가 null - fewShotUuid: {}, resourceUrl: {}", fewShotUuid, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
            }

            // policies에 type이 "role"인 항목이 하나라도 있는 PolicyRequest 객체는 policy 리스트에서 제외
            List<PolicyRequest> filteredPolicy = policy.stream()
                    .filter(policyReq -> {
                        if (policyReq.getPolicies() != null) {
                            // policies에 type이 "role"인 항목이 있는지 확인
                            return policyReq.getPolicies().stream()
                                    .noneMatch(p -> "role".equals(p.getType()));
                        }
                        return true; // policies가 null이면 포함
                    })
                    .collect(Collectors.toList());

            log.info("Few-Shot Policy 설정 완료 - fewShotUuid: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", fewShotUuid, filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("Few-Shot Policy 설정 실패 (BusinessException) - fewShotUuid: {}, errorCode: {}", fewShotUuid, e.getErrorCode(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Few-Shot Policy 설정 실패 (RuntimeException) - fewShotUuid: {}, error: {}", fewShotUuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Few-Shot Policy 설정 실패 (Exception) - fewShotUuid: {}", fewShotUuid, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Few-Shot Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }
} 