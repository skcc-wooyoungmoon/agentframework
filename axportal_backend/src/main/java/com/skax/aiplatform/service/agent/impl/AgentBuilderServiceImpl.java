package com.skax.aiplatform.service.agent.impl;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphExecuteRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphInfoUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphSaveRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphAppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphTemplatesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptResponse;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentGraphsService;
import com.skax.aiplatform.client.sktai.agent.service.SktaiAgentInferencePromptsService;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageCreate;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.client.sktai.lineage.service.SktaiLineageService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.agent.response.AgentAppInfoRes;
import com.skax.aiplatform.dto.agent.response.AgentBuilderRes;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.agent.AgentBuilderMapper;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.agent.AgentBuilderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 에이전트 빌더 서비스 구현체
 * 
 * <p>
 * SKT AI Platform의 에이전트 그래프 관련 비즈니스 로직을 구현합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-19
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentBuilderServiceImpl implements AgentBuilderService {

    private final SktaiAgentGraphsService sktaiAgentGraphsService;

    private final AgentBuilderMapper agentBuilderMapper;

    private final SktaiLineageService sktaiLineageService;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final SktaiAgentInferencePromptsService sktaiAgentInferencePromptsService;

    private final ObjectMapper objectMapper;
    private final AdminAuthService adminAuthService;
    private final SktaiAuthService sktaiAuthService;
    @Value("${sktai.api.recursion-limit:200}")
    private Integer recursionLimit;

    @Override
    public PageResponse<AgentBuilderRes> getAgentBuilders(String projectId, Integer page, Integer size, String sort,
            String filter, String search) {

        try {
            log.debug("에이전트 빌더 목록 조회 시작: projectId={}, page={}, size={}, sort={}, filter={}, search={}",
                    projectId, page, size, sort, filter, search);

            GraphsResponse response = sktaiAgentGraphsService.getGraphs("24ba585a-02fc-43d8-b9f1-f7ca9e020fe5", page, size, sort,
                    filter, search);

            // Mapper를 사용하여 GraphResponse 리스트를 AgentBuilderRes 리스트로 변환
            List<AgentBuilderRes> agentBuilders = agentBuilderMapper.toAgentBuilderResList(response.getData());

            if (agentBuilders == null) {
                agentBuilders = new ArrayList<>();
            }

            // publicStatus 설정 (Mapper로 변환한 후 추가 정보 설정)
            for (AgentBuilderRes agentBuilder : agentBuilders) {
                String graphId = agentBuilder.getId();
                GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                        .findByAsstUrl("/api/v1/agent/agents/graphs/" + graphId).orElse(null);
                String publicStatus;
                if (existing != null && existing.getLstPrjSeq() != null) {
                    publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";

                    agentBuilder.setFstPrjSeq(existing.getFstPrjSeq());
                    agentBuilder.setLstPrjSeq(existing.getLstPrjSeq());
                } else {
                    publicStatus = "전체공유";
                }
                agentBuilder.setPublicStatus(publicStatus);

                // 배포 상태 설정
                agentBuilder.setDeploymentStatus(getAgentDeployInfo(graphId).getName() != null ? "개발배포" : "미배포");
            }

            // ADXP Pagination을 PageResponse로 변환
            return PaginationUtils.toPageResponseFromAdxp(response.getPayload().getPagination(), agentBuilders);

        } catch (BusinessException e) {
            log.error("에이전트 빌더 목록 조회 실패 (비즈니스 오류): {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 빌더 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 빌더 목록 조회에 실패했습니다.");
        }
    }

    @Override
    public AgentBuilderRes getAgentBuilder(String agentId) {
        try {
            log.debug("에이전트 빌더 상세 조회 시작: agentId={}", agentId);
            if (agentId == null || agentId.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "에이전트 ID가 필요합니다.");
            }
            GraphResponse graphResponse = sktaiAgentGraphsService.getGraph(agentId);
            if (graphResponse == null) {
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "해당 에이전트를 찾을 수 없습니다.");
            }
            AgentBuilderRes result = agentBuilderMapper.toAgentBuilderRes(graphResponse);

            return result;
        } catch (BusinessException e) {
            // 권한 오류인 경우 (생성 직후 프로젝트 할당이 완료되지 않은 경우)
            if (e.getMessage() != null && e.getMessage().contains("권한")) {
                log.warn("에이전트 빌더 조회 권한 오류 (생성 직후일 수 있음): agentId={}", agentId);
                // 권한 오류인 경우에도 기본 정보로 응답 생성 (프론트엔드에서 처리 가능하도록)
                AgentBuilderRes result = AgentBuilderRes.builder()
                        .id(agentId)
                        .name("")
                        .description("")
                        .nodes(new ArrayList<>())
                        .edges(new ArrayList<>())
                        .build();
                return result;
            }
            log.error("에이전트 빌더 상세 조회 실패 (비즈니스 오류): agentId={}", agentId, e);
            throw e;
        } catch (Exception e) {
            // 권한 오류인 경우 (생성 직후 프로젝트 할당이 완료되지 않은 경우)
            if (e.getMessage() != null && e.getMessage().contains("권한")) {
                log.warn("에이전트 빌더 조회 권한 오류 (생성 직후일 수 있음): agentId={}", agentId);
                // 권한 오류인 경우에도 기본 정보로 응답 생성 (프론트엔드에서 처리 가능하도록)
                AgentBuilderRes result = AgentBuilderRes.builder()
                        .id(agentId)
                        .name("")
                        .description("")
                        .nodes(new ArrayList<>())
                        .edges(new ArrayList<>())
                        .build();
                return result;
            }
            log.error("에이전트 빌더 상세 조회 실패 (예상치 못한 오류): agentId={}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 빌더 상세 조회에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteAgentBuilder(String agentId) {
        try {
            log.debug("에이전트 빌더 삭제 시작: agentId={}", agentId);
            if (agentId == null || agentId.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "에이전트 ID가 필요합니다.");
            }
            sktaiAgentGraphsService.deleteGraph(agentId);
            sktaiLineageService.deleteLineage(agentId); // graph 삭제시 lineage 삭제 로직 추가
            log.debug("에이전트 빌더 삭제 완료: agentId={}", agentId);
        } catch (BusinessException e) {
            log.error("에이전트 빌더 삭제 실패 (비즈니스 오류): agentId={}", agentId, e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 빌더 삭제 실패 (예상치 못한 오류): agentId={}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 빌더 삭제에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public AgentBuilderRes updateAgentInfo(String agentId, Map<String, Object> updateReq) {
        try {
            log.debug("에이전트 정보 수정 시작: agentId={}, updateReq={}", agentId, updateReq);
            String name = updateReq.get("name") != null ? updateReq.get("name").toString() : null;
            String description = updateReq.get("description") != null ? updateReq.get("description").toString() : null;
            GraphInfoUpdateRequest request = GraphInfoUpdateRequest.builder()
                    .name(name)
                    .description(description)
                    .build();
            sktaiAgentGraphsService.updateGraphInfo(agentId, request);
            GraphResponse graphResponse = sktaiAgentGraphsService.getGraph(agentId);
            AgentBuilderRes result = agentBuilderMapper.toAgentBuilderRes(graphResponse);
            log.debug("에이전트 정보 수정 완료: agentId={}", agentId);
            return result;
        } catch (BusinessException e) {
            log.error("에이전트 정보 수정 실패 (비즈니스 오류): agentId={}", agentId, e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 정보 수정 실패 (예상치 못한 오류): agentId={}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 정보 수정에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public AgentBuilderRes saveAgentGraph(String agentId, Map<String, Object> saveReq) {
        try {
            log.debug("에이전트 그래프 저장 시작: agentId={}", agentId);
            String name = saveReq.get("name") != null ? saveReq.get("name").toString() : null;
            String description = saveReq.get("description") != null ? saveReq.get("description").toString() : null;
            @SuppressWarnings("unchecked")
            Map<String, Object> graphMap = (Map<String, Object>) saveReq.get("graph");
            if (graphMap == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "그래프 데이터가 없습니다.");
            }

            // 🔥 원본 데이터 그대로 전달 (외부 API가 처리하도록)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> nodesForSave = (List<Map<String, Object>>) graphMap.get("nodes");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> edgesForSave = (List<Map<String, Object>>) graphMap.get("edges");

            String nodesJson = objectMapper.writeValueAsString(nodesForSave);
            List<GraphSaveRequest.GraphNode> nodeList = objectMapper.readValue(nodesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class,
                            GraphSaveRequest.GraphNode.class));

            // 🔥 edges 저장 검증 및 로깅 (변환된 edgesForSave 사용)
            List<GraphSaveRequest.GraphEdge> edgeList = new ArrayList<>();

            if (edgesForSave != null) {
                try {
                    String edgesJson = objectMapper.writeValueAsString(edgesForSave);
                    edgeList = objectMapper.readValue(edgesJson,
                            objectMapper.getTypeFactory().constructCollectionType(List.class,
                                    GraphSaveRequest.GraphEdge.class));

                    if (edgeList == null) {
                        log.warn("edges 파싱 결과가 null입니다. 빈 리스트로 처리합니다. agentId={}", agentId);
                        edgeList = new ArrayList<>();
                    }

                    log.debug("에이전트 그래프 edges 저장: agentId={}, edgesCount={}", agentId, edgeList.size());

                } catch (Exception e) {
                    log.error("edges 파싱 실패 (빈 리스트로 처리): agentId={}, error={}", agentId, e.getMessage(), e);
                    edgeList = new ArrayList<>();
                }
            } else {
                log.warn("edges가 null입니다. 빈 리스트로 처리합니다. agentId={}", agentId);
            }

            GraphSaveRequest.GraphStructure graphStructure = GraphSaveRequest.GraphStructure.builder()
                    .nodes(nodeList)
                    .edges(edgeList)
                    .build();
            GraphSaveRequest request = GraphSaveRequest.builder()
                    .name(name)
                    .description(description)
                    .graph(graphStructure)
                    .build();
            sktaiAgentGraphsService.saveGraph(agentId, request);
            GraphResponse graphResponse = sktaiAgentGraphsService.getGraph(agentId);
            AgentBuilderRes result = agentBuilderMapper.toAgentBuilderRes(graphResponse);
            
            log.debug("에이전트 그래프 저장 완료: agentId={}", agentId);

            // // 그래프 권한 설정 (저장 시에도 권한이 확실히 설정되도록)
            // try {
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/graphs/"
            // + agentId);
            // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/lineages/" +
            // agentId + "/upstream");
            // log.debug("그래프 권한 설정 완료: agentId={}", agentId);
            // } catch (Exception e) {
            // log.warn("그래프 권한 설정 실패 (계속 진행): agentId={}, error={}", agentId,
            // e.getMessage());
            // }

            saveAgentLineages(agentId, nodeList);
            return result;
        } catch (BusinessException e) {
            log.error("에이전트 그래프 저장 실패 (비즈니스 오류): agentId={}", agentId, e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 그래프 저장 실패 (예상치 못한 오류): agentId={}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 그래프 저장에 실패했습니다.");
        }
    }

    @Override
    public Object getAgentBuilderTemplates() {
        try {
            log.debug("에이전트 빌더 템플릿 목록 조회 시작");
            GraphTemplatesResponse response = sktaiAgentGraphsService.getGraphTemplates();
            log.debug("에이전트 빌더 템플릿 목록 조회 완료");
            return response;
        } catch (BusinessException e) {
            log.error("에이전트 빌더 템플릿 목록 조회 실패 (비즈니스 오류)", e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 빌더 템플릿 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 빌더 템플릿 목록 조회에 실패했습니다.");
        }
    }

    @Override
    public Object getAgentBuilderTemplate(String templateId) {
        try {
            log.debug("에이전트 빌더 템플릿 상세 조회 시작: templateId={}", templateId);
            Map<String, Object> response = sktaiAgentGraphsService.getTemplate(templateId);
            log.debug("에이전트 빌더 템플릿 상세 조회 완료: templateId={},  response={}", templateId, response);
            return response;
        } catch (BusinessException e) {
            log.error("에이전트 빌더 템플릿 상세 조회 실패 (비즈니스 오류): templateId={}", templateId, e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 빌더 템플릿 상세 조회 실패 (예상치 못한 오류): templateId={}", templateId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 빌더 템플릿 상세 조회에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public Object createAgentFromTemplate(Map<String, Object> requestBody) {
        try {
            log.debug("템플릿 기반 에이전트 생성 시작: requestBody={}", requestBody);
            String templateId = requestBody.get("template_id") != null ? requestBody.get("template_id").toString()
                    : null;
            String name = requestBody.get("name") != null ? requestBody.get("name").toString() : null;
            String description = requestBody.get("description") != null ? requestBody.get("description").toString()
                    : null;

            // 템플릿 기반 생성 시 graph 필드가 필수이므로 빈 그래프 구조 생성
            Map<String, Object> emptyGraph = new java.util.HashMap<>();
            emptyGraph.put("nodes", new ArrayList<>());
            emptyGraph.put("edges", new ArrayList<>());

            // GraphCreateRequest 생성 (template_id와 빈 graph 포함)
            GraphCreateRequest request = GraphCreateRequest.builder()
                    .name(name)
                    .description(description)
                    .templateId(templateId)
                    .graph(emptyGraph)
                    .build();

            // policy 필드 추가
            List<PolicyRequest> policyRequests = adminAuthService.getPolicyRequestsByCurrentGroup();
            if (policyRequests != null && !policyRequests.isEmpty()) {
                request.setPolicy(policyRequests);
                log.debug("템플릿 기반 에이전트 생성 요청에 policy 추가: policyRequests={}", policyRequests);
            }

            // 일반 그래프 생성 API 사용 (template_id가 요청 본문에 포함됨)
            GraphCreateResponse response = sktaiAgentGraphsService.createGraph(request);
            log.debug("템플릿 기반 에이전트 생성 완료: templateId={}", templateId);

            // 생성된 그래프의 프로젝트 매핑 등록 및 권한 설정
            if (response != null && response.getGraphUuid() != null && !response.getGraphUuid().isEmpty()) {
                String graphUuid = response.getGraphUuid();
                try {
                    // 그래프 권한 설정
                    adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/agent/agents/graphs/" + graphUuid);
                    // adminAuthService.setResourcePolicyByCurrentGroup("/api/v1/lineages/" + graphUuid + "/upstream");
                    log.info("그래프 권한 설정 완료: graphUuid={}", graphUuid);

                } catch (Exception e) {
                    log.warn("그래프 프로젝트 매핑 및 권한 설정 실패 (계속 진행): graphUuid={}, error={}", graphUuid, e.getMessage());
                }
            }

            return response;
        } catch (BusinessException e) {
            log.error("템플릿 기반 에이전트 생성 실패 (비즈니스 오류)", e);
            throw e;
        } catch (Exception e) {
            log.error("템플릿 기반 에이전트 생성 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "템플릿 기반 에이전트 생성에 실패했습니다.");
        }
    }


    @Override
    public void streamAgentGraph(Map<String, Object> request, OutputStream outputStream) throws Exception {
        try {
            log.debug("에이전트 그래프 스트리밍 실행 시작 (OutputStream)");
            String graphId = request.get("graph_id") != null ? request.get("graph_id").toString() : null;
            if (graphId == null || graphId.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "graph_id가 필요합니다.");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> inputData = (Map<String, Object>) request.get("input_data");
            GraphExecuteRequest executeRequest = GraphExecuteRequest.builder()
                    .graphId(graphId)
                    .inputData(inputData)
                    .build();
            feign.Response response = sktaiAgentGraphsService.executeGraphStreamResponse(executeRequest);
            log.debug("에이전트 그래프 스트리밍 응답 수신 (OutputStream) - status: {}, headers: {}", 
                    response != null ? response.status() : "null", 
                    response != null ? response.headers() : "null");
            if (response == null || response.body() == null) {
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "스트리밍 응답을 받을 수 없습니다.");
            }
            try (java.io.InputStream inputStream = response.body().asInputStream()) {
                // 원본 응답이 이미 UTF-8로 인코딩되어 있으므로 바이트를 그대로 전달
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;
                int chunkCount = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    chunkCount++;
                    String preview = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    log.debug("청크 #{}: {} bytes - 시작: {}", chunkCount, bytesRead, preview);
                    
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush(); // 실시간 스트리밍을 위해 매번 flush
                    totalBytesRead += bytesRead;
                }
                log.debug("에이전트 그래프 스트리밍 데이터 전송 완료 - 총 {} 청크, {} bytes", chunkCount, totalBytesRead);
            } catch (java.io.InterruptedIOException e) {
                // 🔥 스트리밍 중 연결이 끊어진 경우: 정상 종료로 간주 (클라이언트가 연결을 끊었거나 타임아웃)
                log.debug("에이전트 그래프 스트리밍 중 연결 종료 (정상 종료 가능): {}", e.getMessage());
                // 이미 전송된 데이터는 정상적으로 처리되었으므로 에러를 throw하지 않음
            }
            log.debug("에이전트 그래프 스트리밍 실행 완료 (OutputStream)");
        } catch (BusinessException e) {
            log.error("에이전트 그래프 스트리밍 실행 실패 (비즈니스 오류)", e);
            throw e;
        } catch (java.io.InterruptedIOException e) {
            // 🔥 스트리밍 중 연결이 끊어진 경우: 정상 종료로 간주
            log.debug("에이전트 그래프 스트리밍 중 연결 종료 (정상 종료 가능): {}", e.getMessage());
            // 이미 전송된 데이터는 정상적으로 처리되었으므로 에러를 throw하지 않음
        } catch (Exception e) {
            log.error("에이전트 그래프 스트리밍 실행 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 그래프 스트리밍 실행에 실패했습니다.");
        }
    }

    @Override
    public AgentAppInfoRes getAgentDeployInfo(String agentId) {
        try {
            log.debug("에이전트 배포 정보 조회 시작: agentId={}", agentId);
            GraphAppResponse response = sktaiAgentGraphsService.getGraphAppInfo(agentId);
            if (response == null || response.getData() == null) {
                log.warn("배포 정보 없음: agentId={}", agentId);
                return AgentAppInfoRes.builder()
                        .id(null)
                        .name(null)
                        .description(null)
                        .build();
            }
            AgentAppInfoRes result = agentBuilderMapper.toAgentAppInfoRes(response);
            log.debug("에이전트 배포 정보 조회 완료: agentId={}", agentId);
            return result;
        } catch (BusinessException e) {
            log.warn("에이전트 배포 정보 조회 실패 (비즈니스 오류): agentId={}", agentId, e);
            return AgentAppInfoRes.builder()
                    .id(null)
                    .name(null)
                    .description(null)
                    .build();
        } catch (Exception e) {
            log.error("에이전트 배포 정보 조회 실패 (예상치 못한 오류): agentId={}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 배포 정보 조회에 실패했습니다.");
        }
    }

    @Override
    public List<LineageRelationWithTypes> getAgentLineages(String graphId) {
        try {
            log.debug("에이전트 Lineage 조회 시작: graphId={}", graphId);
            List<LineageRelationWithTypes> lineages = sktaiLineageService.getFullLineage(graphId, null);
            log.debug("에이전트 Lineage 조회 완료: graphId={}, count={}", graphId,
                    lineages != null ? lineages.size() : 0);
            return lineages != null ? lineages : new ArrayList<>();
        } catch (BusinessException e) {
            log.error("에이전트 Lineage 조회 실패 (비즈니스 오류): graphId={}", graphId, e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 Lineage 조회 실패 (예상치 못한 오류): graphId={}", graphId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "에이전트 Lineage 조회에 실패했습니다.");
        }
    }

    private void saveAgentLineages(String graphId, List<GraphSaveRequest.GraphNode> nodeList) {
        try {
            if (nodeList == null || nodeList.isEmpty()) {
                log.warn("노드 목록이 없습니다: graphId={}", graphId);
                return;
            }
            log.debug("에이전트 Lineage 저장 시작: graphId={}, nodeList={}", graphId, nodeList);
            sktaiLineageService.deleteLineage(graphId);
            log.info("기존 Lineage 삭제 완료: sourceKey={}", graphId);
            List<LineageCreate.LineageItem> lineages = new ArrayList<>();
            for (GraphSaveRequest.GraphNode node : nodeList) {
                if (node == null) {
                    continue;
                }
                String nodeId = node.getId() != null ? node.getId().toString() : null;
                @SuppressWarnings("unchecked")
                Map<String, Object> nodeData = (Map<String, Object>) node.getData();
                if (nodeData == null) {
                    continue;
                }
                // 최상위 레벨의 serving_model 확인
                String servingModel = nodeData.get("serving_model") != null ? nodeData.get("serving_model").toString()
                        : null;
                if (servingModel != null && !servingModel.trim().isEmpty()
                        && !"D".equals(servingModel)
                        && servingModel.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                    lineages.add(LineageCreate.LineageItem.builder()
                            .sourceKey(graphId)
                            .sourceType(ObjectType.AGENT_GRAPH)
                            .targetKey(servingModel)
                            .targetType(ObjectType.SERVING_MODEL)
                            .action(ActionType.USE)
                            .build());
                    log.debug("모델 Lineage 추가 - graphId: {}, nodeId: {}, servingModel: {}", graphId, nodeId,
                            servingModel);
                }
                String promptId = nodeData.get("prompt_id") != null ? nodeData.get("prompt_id").toString() : null;
                if (promptId != null && !promptId.trim().isEmpty()) {
                    PromptResponse response = sktaiAgentInferencePromptsService.getInferencePrompt(promptId);
                    if(response != null && response.getData() != null & response.getData().getProjectId() == "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"){
                        lineages.add(LineageCreate.LineageItem.builder()
                                .sourceKey(graphId)
                                .sourceType(ObjectType.AGENT_GRAPH)
                                .targetKey(promptId)
                                .targetType(ObjectType.PROMPT)
                                .action(ActionType.USE)
                                .build());
                    }
                }
                Object toolIdsObj = nodeData.get("tool_ids");
                if (toolIdsObj != null && toolIdsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> toolIds = (List<Object>) toolIdsObj;
                    for (Object toolId : toolIds) {
                        lineages.add(LineageCreate.LineageItem.builder()
                                .sourceKey(graphId)
                                .sourceType(ObjectType.AGENT_GRAPH)
                                .targetKey(toolId.toString())
                                .targetType(ObjectType.TOOL)
                                .action(ActionType.USE)
                                .build());
                    }
                }
                String fewshotId = nodeData.get("fewshot_id") != null ? nodeData.get("fewshot_id").toString() : null;
                if (fewshotId != null && !fewshotId.trim().isEmpty()) {
                    lineages.add(LineageCreate.LineageItem.builder()
                            .sourceKey(graphId)
                            .sourceType(ObjectType.AGENT_GRAPH)
                            .targetKey(fewshotId)
                            .targetType(ObjectType.FEW_SHOT)
                            .action(ActionType.USE)
                            .build());
                }
                Object mcpCatalogsObj = nodeData.get("mcp_catalogs");
                if (mcpCatalogsObj != null && mcpCatalogsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> mcpCatalogs = (List<Object>) mcpCatalogsObj;
                    for (Object mcpCatalogObj : mcpCatalogs) {
                        if (mcpCatalogObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> mcpCatalog = (Map<String, Object>) mcpCatalogObj;
                            Object mcpIdObj = mcpCatalog.get("id");
                            if (mcpIdObj != null) {
                                String mcpId = mcpIdObj.toString();
                                if (!mcpId.trim().isEmpty()) {
                                    lineages.add(LineageCreate.LineageItem.builder()
                                            .sourceKey(graphId)
                                            .sourceType(ObjectType.AGENT_GRAPH)
                                            .targetKey(mcpId)
                                            .targetType(ObjectType.MCP)
                                            .action(ActionType.USE)
                                            .build());
                                    log.debug("MCP Lineage 추가 - graphId: {}, nodeId: {}, mcpId: {}",
                                            graphId, nodeId, mcpId);
                                }
                            }
                        }
                    }
                }
                Object knowledgeRetrieverObj = nodeData.get("knowledge_retriever");
                if (knowledgeRetrieverObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> knowledgeRetriever = (Map<String, Object>) knowledgeRetrieverObj;
                    String knowledgeId = knowledgeRetriever.get("repo_id").toString();
                    if (knowledgeId != null && !knowledgeId.toString().trim().isEmpty()) {
                        lineages.add(LineageCreate.LineageItem.builder()
                                .sourceKey(graphId)
                                .sourceType(ObjectType.AGENT_GRAPH)
                                .targetKey(knowledgeId)
                                .targetType(ObjectType.KNOWLEDGE)
                                .action(ActionType.USE)
                                .build());
                        log.info("📖 지식 Lineage 추가: {}", knowledgeId);
                    }
                }
        
            }
            if (!lineages.isEmpty()) {
                LineageCreate request = LineageCreate.builder()
                        .lineages(lineages)
                        .build();
                sktaiLineageService.createLineage(request);
                log.info("Lineage 일괄 저장 완료: {}건", lineages.size());
            }
        } catch (BusinessException e) {
            log.error("에이전트 Lineage 저장 실패 (비즈니스 오류): graphId={}", graphId, e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 Lineage 저장 실패 (예상치 못한 오류): graphId={}", graphId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 Lineage 저장에 실패했습니다.");
        }
    }


    @Override
    public String getPhoenixProjectIdentifier(String type, String id) {
        try {
            log.debug("Phoenix 프로젝트 식별자 조회 시작: type={}, id={}", type, id);
            String projectId = sktaiAgentGraphsService.getPhoenixProjectIdentifier(type, id);
            log.debug("Phoenix 프로젝트 식별자 조회 완료: type={}, id={}, projectId={}", type, id, projectId);
            return projectId;
        } catch (BusinessException e) {
            log.error("Phoenix 프로젝트 식별자 조회 실패 (비즈니스 오류): type={}, id={}", type, id, e);
            throw e;
        } catch (Exception e) {
            log.error("Phoenix 프로젝트 식별자 조회 실패 (예상치 못한 오류): type={}, id={}", type, id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Phoenix 프로젝트 식별자 조회에 실패했습니다.");
        }
    }

    @Override
    public String exportAgentGraphCode(String graphId, String credentialType) {
        try {
            log.debug("Agent Graph Export 시작: graphId={}, credentialType={}", graphId, credentialType);
            
            Map<String, Object> response = sktaiAgentGraphsService.exportGraphCode(graphId, credentialType);
            if (response == null || response.isEmpty()) {
                log.warn("Agent Graph Export 응답 데이터가 없습니다: graphId={}", graphId);
                return null;
            }
            Object rawData = response.get("data");
            if (!(rawData instanceof String)) {
                if (rawData == null) {
                    log.warn("Agent Graph Export 응답 데이터가 null입니다: graphId={}", graphId);
                    return null;
                }
                log.warn("Agent Graph Export 응답 타입이 String이 아닙니다: graphId={}, type={}", graphId,
                        rawData.getClass().getSimpleName());
                return null;
            }
            String code = ((String) rawData).trim();
            log.debug("Agent Graph Export 완료: graphId={}, codeLength={}", graphId, code.length());
            return code;
        } catch (BusinessException e) {
            log.error("Agent Graph Export 실패 (비즈니스 오류): graphId={}", graphId, e);
            throw e;
        } catch (Exception e) {
            log.error("Agent Graph Export 실패 (예상치 못한 오류): graphId={}", graphId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Agent Graph Export에 실패했습니다.");
        }
    }

       /**
     * 에이전트 빌더 Policy 설정
     *
     * @param agentId     에이전트 ID
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @return 설정된 Policy 목록
     */
    @Override
    @Transactional
    public void setAgentBuilderPolicy(String agentId, String memberId, String projectName) {
        log.info("에이전트 빌더 Policy 설정 요청 - agentId: {}, memberId: {}, projectName: {}", agentId, memberId, projectName);

        // agentId 검증
        if (!StringUtils.hasText(agentId)) {
            log.error("에이전트 빌더 Policy 설정 실패 - agentId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "에이전트 ID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("에이전트 빌더 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("에이전트 빌더 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            // ADXP 권한부여
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/agent/agents/graphs/" + agentId,
                    memberId, projectName);
            // adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/lineages/" + agentId + "/upstream",
            //         memberId, projectName);

            String resourceUrl = "/api/v1/agent/agents/graphs/" + agentId;
            log.info("에이전트 빌더 Policy 설정 완료 - resourceUrl: {}, memberId: {}, projectName: {}", resourceUrl, memberId,
                    projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy가 null인 경우 예외 발생
            if (policy == null) {
                log.error("에이전트 빌더 Policy 조회 결과가 null - agentId: {}, resourceUrl: {}", agentId, resourceUrl);
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 빌더 Policy 조회에 실패했습니다. Policy 정보를 찾을 수 없습니다.");
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

            log.info("에이전트 빌더 Policy 설정 완료 - agentId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", agentId, filteredPolicy.size(), policy.size(), filteredPolicy.size());

        } catch (BusinessException e) {
            log.error("에이전트 빌더 Policy 설정 실패 (BusinessException) - agentId: {}, errorCode: {}", agentId,
                    e.getErrorCode(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("에이전트 빌더 Policy 설정 실패 (RuntimeException) - agentId: {}, error: {}", agentId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 빌더 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("에이전트 빌더 Policy 설정 실패 (Exception) - agentId: {}", agentId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 빌더 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }

}