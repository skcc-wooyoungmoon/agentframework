package com.skax.aiplatform.client.sktai.agent;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.skax.aiplatform.client.sktai.agent.dto.request.GraphCopyRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphExecuteRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphInfoUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphSaveRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.GraphUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphAppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphDetailResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphExecuteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphNodeInfoResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphTemplateApiResponse;
// import com.skax.aiplatform.client.sktai.agent.dto.response.GraphTemplatesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphTemplatesApiResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphsResponse;
import com.skax.aiplatform.client.sktai.config.SktaiClientConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * SKTAI Agent Graphs API Feign Client
 * 
 * <p>SKTAI Agent 시스템의 그래프 관리를 위한 Feign Client입니다.
 * Agent Graphs는 워크플로우와 의사결정 트리를 시각적으로 구성하고 관리하는 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Graph CRUD</strong>: 그래프 생성, 조회, 수정, 삭제</li>
 *   <li><strong>워크플로우 관리</strong>: AI 에이전트의 논리적 흐름 정의</li>
 *   <li><strong>노드 및 엣지 관리</strong>: 그래프 구성 요소 관리</li>
 *   <li><strong>실행 추적</strong>: 그래프 실행 과정 모니터링</li>
 * </ul>
 * 
 * <h3>API 엔드포인트:</h3>
 * <ul>
 *   <li><code>GET /api/v1/agent/agent/graphs</code>: 그래프 목록 조회</li>
 *   <li><code>POST /api/v1/agent/agent/graphs</code>: 새 그래프 생성</li>
 *   <li><code>GET /api/v1/agent/agent/graphs/{graphUuid}</code>: 그래프 상세 조회</li>
 *   <li><code>PUT /api/v1/agent/agent/graphs/{graphUuid}</code>: 그래프 수정</li>
 *   <li><code>DELETE /api/v1/agent/agent/graphs/{graphUuid}</code>: 그래프 삭제</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see GraphCreateRequest 그래프 생성 요청 DTO
 * @see GraphResponse 그래프 상세 응답 DTO
 */
@FeignClient(
    name = "sktai-agent-graphs-client",
    url = "${sktai.api.base-url}/api/v1/agent",
    configuration = SktaiClientConfig.class
)
public interface SktaiAgentGraphsClient {

    /**
     * Agent Graphs 목록 조회
     * 
     * <p>등록된 Agent 그래프들의 페이징된 목록을 조회합니다.
     * 필터링 및 정렬 옵션을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 옵션 (예: "created_at", "name")
     * @param filter 필터 조건
     * @param search 검색어
     * @return Agent Graphs 목록 응답
     */
    @Operation(
        summary = "Agent Graphs 목록 조회",
        description = "등록된 Agent 그래프들의 페이징된 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Graphs 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/agents/graphs")
    GraphsResponse getGraphs(
        @Parameter(description = "프로젝트 ID")
        @RequestParam(value = "projectId", required = false) String projectId,
        
        @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        
        @Parameter(description = "페이지 크기", example = "12")
        @RequestParam(value = "size", defaultValue = "12") Integer size,
        
        @Parameter(description = "정렬 옵션", example = "created_at")
        @RequestParam(value = "sort", required = false) String sort,
        
        @Parameter(description = "필터 조건")
        @RequestParam(value = "filter", required = false) String filter,
        
        @Parameter(description = "검색어")
        @RequestParam(value = "search", required = false) String search
    );

    /**
     * 새로운 Agent Graph 생성
     * 
     * <p>새로운 Agent 그래프를 생성합니다.
     * 노드, 엣지, 워크플로우 정의 등을 포함해야 합니다.</p>
     * 
     * @param request Graph 생성 요청 데이터
     * @return 생성된 Graph 정보
     */
    @Operation(
        summary = "Agent Graph 생성",
        description = "새로운 Agent 그래프를 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Graph 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "409", description = "Graph 이름 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping(value = "/agents/graphs", consumes = MediaType.APPLICATION_JSON_VALUE)
    GraphCreateResponse createGraph(
        @Parameter(description = "Graph 생성 요청 데이터", required = true)
        @RequestBody GraphCreateRequest request
    );

    /**
     * Agent Graph 상세 정보 조회
     * 
     * <p>특정 Agent 그래프의 상세 정보를 조회합니다.
     * 노드, 엣지, 워크플로우 정의 등을 포함합니다.</p>
     * 
     * @param graphUuid 조회할 Graph의 UUID
     * @return Graph 상세 정보
     */
    @Operation(
        summary = "Agent Graph 상세 조회",
        description = "특정 Agent 그래프의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Graph 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "Graph를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/agents/graphs/{graphUuid}")
    GraphDetailResponse getGraph(
        @Parameter(description = "조회할 Graph의 UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid
    );

    /**
     * Phoenix Trace Project 식별자 조회
     *
     * @param type 리소스 타입 (graph/app)
     * @param id   그래프 또는 앱 ID
     * @return 공통 응답
     */
    @Operation(
        summary = "Phoenix Trace Project 조회",
        description = "지정한 그래프 또는 앱에 대한 Phoenix Trace Project 식별자를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "대상 리소스를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/agents/traces/project")
    Map<String, Object> getTraceProject(
        @Parameter(description = "리소스 타입 (graph/app)", example = "graph")
        @RequestParam("type") String type,

        @Parameter(description = "그래프 또는 앱 ID", example = "graph-uuid")
        @RequestParam("id") String id
    );

    /**
     * Agent Graph 정보 수정
     * 
     * <p>기존 Agent 그래프의 정보를 수정합니다.
     * 노드, 엣지, 워크플로우 정의 등을 업데이트할 수 있습니다.</p>
     * 
     * @param graphUuid 수정할 Graph의 UUID
     * @param request Graph 수정 요청 데이터
     * @return 수정 결과
     */
    @Operation(
        summary = "Agent Graph 수정",
        description = "기존 Agent 그래프의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Graph 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "Graph를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "Graph 이름 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping(value = "/agents/graphs/{graphUuid}/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    GraphUpdateOrDeleteResponse updateGraph(
        @Parameter(description = "수정할 Graph의 UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid,
        
        @Parameter(description = "Graph 수정 요청 데이터", required = true)
        @RequestBody GraphUpdateRequest request
    );

    /**
     * Agent Graph 삭제
     * 
     * <p>특정 Agent 그래프를 시스템에서 삭제합니다.
     * 삭제된 Graph은 복구할 수 없습니다.</p>
     * 
     * @param graphUuid 삭제할 Graph의 UUID
     * @return 삭제 결과
     */
    @Operation(
        summary = "Agent Graph 삭제",
        description = "특정 Agent 그래프를 시스템에서 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Graph 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "Graph를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "Graph가 사용 중이어서 삭제 불가"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/agents/graphs/{graphUuid}")
    GraphUpdateOrDeleteResponse deleteGraph(
        @Parameter(description = "삭제할 Graph의 UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid
    );

    /**
     * Graph 영구 삭제 (DELETE - hard delete)
     * 
     * @param graphUuid 영구 삭제할 Graph UUID
     * @return 삭제 결과
     */
    @PostMapping("/agents/graphs/hard-delete")
    @Operation(
        summary = "그래프 영구 삭제", 
        description = "지정된 UUID의 그래프를 영구적으로 삭제합니다 (복구 불가능)",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "그래프 영구 삭제 성공"),
    })
    void hardDeleteGraph();

    /**
     * Graph 템플릿 목록 조회 (GET)
     * 
     * @return 사용 가능한 Graph 템플릿 목록
     */
    @GetMapping("/agents/graphs/templates")
    @Operation(
        summary = "그래프 템플릿 목록 조회", 
        description = "사용 가능한 그래프 템플릿 목록을 조회합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "템플릿 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphTemplatesApiResponse getGraphTemplates();

    /**
     * Graph 템플릿으로 생성 (POST)
     * 
     * @param templateId 사용할 템플릿 ID
     * @param request Graph 생성 요청
     * @return 생성된 Graph 정보
     */
    @PostMapping("/agents/graphs/templates/{templateId}")
    @Operation(
        summary = "템플릿으로 그래프 생성", 
        description = "지정된 템플릿을 사용하여 새로운 그래프를 생성합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "그래프 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphCreateResponse createGraphFromTemplate(
        @Parameter(description = "사용할 템플릿 ID", required = true, example = "template-123")
        @PathVariable String templateId,
        @Parameter(description = "그래프 생성 정보", required = true)
        @RequestBody GraphCreateRequest request);

    /**
     * Graph 템플릿 상세 조회 (GET)
     * 
     * @param templateId 조회할 템플릿 ID
     * @return 템플릿 상세 정보
     */
    @GetMapping("/agents/graphs/templates/{templateId}")
    @Operation(
        summary = "그래프 템플릿 상세 조회", 
        description = "지정된 ID의 그래프 템플릿 상세 정보를 조회합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "템플릿 조회 성공"),
        @ApiResponse(responseCode = "404", description = "템플릿을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphTemplateApiResponse getGraphTemplate(
        @Parameter(description = "조회할 템플릿 ID", required = true, example = "template-123")
        @PathVariable String templateId);



    /**
     * Graph App ID 조회 (GET)
     * 
     * @param graphUuid Graph UUID
     * @return Graph App ID 정보
     */
    @GetMapping("/agents/graphs/{graphUuid}/app")
    @Operation(
        summary = "그래프 앱 ID 조회", 
        description = "지정된 그래프의 앱 ID 정보를 조회합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "앱 ID 조회 성공"),
        @ApiResponse(responseCode = "404", description = "그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphAppResponse getGraphAppInfo(
        @Parameter(description = "Graph UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid);

    /**
     * Graph 정보 업데이트 (PUT)
     * 
     * @param graphUuid Graph UUID
     * @param request Graph 정보 업데이트 요청
     * @return 업데이트 결과
     */
    @PutMapping("/agents/graphs/{graphUuid}/info")
    @Operation(
        summary = "그래프 정보 업데이트", 
        description = "지정된 그래프의 기본 정보(이름, 설명)를 업데이트합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그래프 정보 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphUpdateOrDeleteResponse updateGraphInfo(
        @Parameter(description = "Graph UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid,
        @Parameter(description = "그래프 정보 업데이트 요청", required = true)
        @RequestBody GraphInfoUpdateRequest request);

    /**
     * Graph 전체 저장 (PUT)
     * 
     * @param graphUuid Graph UUID
     * @param request Graph 전체 저장 요청
     * @return 저장 결과
     */
    @PutMapping("/agents/graphs/{graphUuid}")
    @Operation(
        summary = "그래프 전체 저장", 
        description = "지정된 그래프의 전체 구조(노드, 엣지, 메타데이터)를 저장합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그래프 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphUpdateOrDeleteResponse saveGraph(
        @Parameter(description = "Graph UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid,
        @Parameter(description = "그래프 전체 저장 요청", required = true)
        @RequestBody GraphSaveRequest request);

    /**
     * Graph 실행 - Query 모드 (POST)
     * 
     * @param graphUuid Graph UUID
     * @param request Graph 실행 요청
     * @return 실행 결과
     */
    @PostMapping("/agents/graphs/{graphUuid}/execute/query")
    @Operation(
        summary = "그래프 실행 (Query 모드)", 
        description = "지정된 그래프를 Query 모드로 실행합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그래프 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphExecuteResponse executeGraphQuery(
        @Parameter(description = "Graph UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid,
        @Parameter(description = "그래프 실행 요청", required = true)
        @RequestBody GraphExecuteRequest request);

    /**
     * Graph 실행 - Stream 모드 (POST) - 실시간 스트리밍
     * 
     * @param request Graph 실행 요청
     * @return 실행 결과 (스트림 - feign.Response)
     */
    @PostMapping("/agents/graphs/stream")
    @Operation(
        summary = "그래프 실행 (Stream 모드)", 
        description = "그래프를 Stream 모드로 실행합니다 (graph_id 포함)",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그래프 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    feign.Response executeGraphStream(
        @Parameter(description = "그래프 실행 요청 (graph_id 포함)", required = true)
        @RequestBody GraphExecuteRequest request);

    /**
     * Graph 실행 - Test 모드 (POST)
     * 
     * @param graphUuid Graph UUID
     * @param request Graph 실행 요청
     * @return 실행 결과
     */
    @PostMapping("/agents/graphs/{graphUuid}/execute/test")
    @Operation(
        summary = "그래프 실행 (Test 모드)", 
        description = "지정된 그래프를 Test 모드로 실행합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "그래프 실행 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphExecuteResponse executeGraphTest(
        @Parameter(description = "Graph UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid,
        @Parameter(description = "그래프 실행 요청", required = true)
        @RequestBody GraphExecuteRequest request);

    /**
     * Graph Node 정보 조회 (GET)
     * 
     * @return Graph Node 타입 및 파라미터 정보
     */
    @GetMapping("/agents/graphs/node-info")
    @Operation(
        summary = "그래프 노드 정보 조회", 
        description = "사용 가능한 그래프 노드의 타입과 파라미터 정보를 조회합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "노드 정보 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphNodeInfoResponse getGraphNodeInfo();

    /**
     * Graph 예약 변수 조회 (GET)
     * 
     * @return Graph에서 사용 가능한 예약 변수 목록
     */
    @GetMapping("/agents/graphs/reserved-variables")
    @Operation(
        summary = "그래프 예약 변수 조회", 
        description = "그래프에서 사용 가능한 예약 변수 목록을 조회합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "예약 변수 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    List<String> getReservedVariables();

    /**
     * Graph 복사 (POST)
     * 
     * @param graphUuid 복사할 Graph UUID
     * @param request Graph 복사 요청
     * @return 복사된 Graph 정보
     */
    @PostMapping("/agents/graphs/{graphUuid}/copy")
    @Operation(
        summary = "그래프 복사", 
        description = "지정된 그래프를 새로운 이름과 설명으로 복사합니다",
        tags = {"Agent Graphs"}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "그래프 복사 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "원본 그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    GraphCreateResponse copyGraph(
        @Parameter(description = "복사할 Graph UUID", required = true, example = "graph-12345678-1234-1234-1234-123456789abc")
        @PathVariable("graphUuid") String graphUuid,
        @Parameter(description = "그래프 복사 요청", required = true)
        @RequestBody GraphCopyRequest request);
    
    /**
     * Graph Import (JSON)
     * 
     * <p>JSON 데이터를 받아서 Graph를 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param graphId Graph ID (query parameter)
     * @param jsonData JSON 형식의 Graph 데이터
     * @return 생성된 Graph 정보
     */
    @PostMapping("/agents/graphs/import")
    @Operation(
        summary = "Graph Import (JSON)",
        description = "JSON 데이터를 받아서 Graph를 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Graph Import 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "422", description = "유효성 검증 오류")
    })
    GraphCreateResponse importGraph(
        @Parameter(description = "Agent ID (Graph ID)", required = true) @RequestParam(value = "agent_id", required = true) String agentId,
        @Parameter(description = "JSON 형식의 Graph 데이터", required = true)
        @RequestBody Object jsonData);
    
    /**
     * Agent Graph Export (Python 코드 조회)
     * 
     * @param graphUuid 그래프 UUID
     * @param credentialType 인증 타입 (token/password)
     * @return Python 코드 문자열
     */
    @GetMapping("/agents/graphs/{graphUuid}/export/code")
    @Operation(
        summary = "Agent Graph Export (Python 코드)",
        description = "Agent Graph를 Python 코드로 Export합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Export 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "그래프를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    Map<String, Object> exportGraphCode(
        @Parameter(description = "그래프 UUID", required = true)
        @PathVariable("graphUuid") String graphUuid,
        
        @Parameter(description = "인증 타입 (token/password)", example = "token")
        @RequestParam(value = "credential_type", required = false, defaultValue = "token") String credentialType
    );
}
