package com.skax.aiplatform.controller.agent;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.agent.request.AgentToolReq;
import com.skax.aiplatform.dto.agent.response.AgentToolCreateRes;
import com.skax.aiplatform.dto.agent.response.AgentToolRes;
import com.skax.aiplatform.dto.agent.response.AgentToolUpdateRes;
import com.skax.aiplatform.service.agent.AgentToolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/agentTool")
@RequiredArgsConstructor
@Tag(name = "Agent Tools", description = "Agent Tools 관리 API")
public class AgentToolController {

    private final AgentToolService agentToolService;

    @GetMapping
    @Operation(
        summary = "Agent Tools 목록 조회",
        description = "Agent Tools 목록을 페이징하여 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent Tools 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<AgentToolRes>> getAgentToolsList(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search) {

        log.info("Agent Tools 목록 조회 요청 Controller - name={}, page={}, size={}, sort={}, filter={}, search={}", name, page, size, sort, filter, search);

        PageResponse<AgentToolRes> agentToolRes = agentToolService.getAgentToolsList(name, page, size, sort, filter, search);
        return AxResponseEntity.ok(agentToolRes, "Agent Tools 목록 조회 성공");
    }

    @GetMapping("/{agentToolId}")
    @Operation(
        summary = "Agent Tool 상세 정보 조회",
        description = "UUID를 통해 특정 Agent Tool의 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent Tool 조회 성공"),
        @ApiResponse(responseCode = "404", description = "Agent Tool을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentToolRes> getAgentToolById(
            @PathVariable("agentToolId") 
            @Parameter(description = "Agent Tool UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String agentToolId) {
        
        log.info("Agent Tool 상세 조회 요청 Controller - agentToolId={}", agentToolId);
        
        AgentToolRes agentToolRes = agentToolService.getAgentToolById(agentToolId);
        
        log.info("Agent Tool 상세 조회 완료 Controller - agentToolId={}", agentToolId);

        return AxResponseEntity.ok(agentToolRes, "Agent Tool 정보를 성공적으로 조회했습니다.");
    }

    @PostMapping
    @Operation(
        summary = "새로운 Agent Tool 생성",
        description = "새로운 Agent Tool을 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Agent Tool 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentToolCreateRes> createAgentTool(
            @Valid @RequestBody AgentToolReq request) {
        
        log.info("새로운 Agent Tool 생성 요청 Controller - name={}", request.getName());
        
        AgentToolCreateRes agentToolCreateRes = agentToolService.createAgentTool(request);
        
        log.info("Agent Tool 생성 완료 Controller - name={}", request.getName());
        
        return AxResponseEntity.created(agentToolCreateRes, "새로운 Agent Tool이 성공적으로 생성되었습니다.");
    }

    @PutMapping("/{agentToolId}")
    @Operation(
        summary = "Agent Tool 정보 수정",
        description = "기존 Agent Tool의 정보를 수정합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent Tool 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "Agent Tool을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<AgentToolUpdateRes> updateAgentToolById(
            @PathVariable("agentToolId") 
            @Parameter(description = "Agent Tool UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String agentToolId,
            @Valid @RequestBody AgentToolReq request) {
        
        log.info("Agent Tool 수정 요청: agentToolId={}", agentToolId);
        
        AgentToolUpdateRes agentToolUpdateRes = agentToolService.updateAgentToolById(agentToolId, request);
        
        log.info("Agent Tool 수정 완료: agentToolId={}", agentToolId);

        return AxResponseEntity.updated(agentToolUpdateRes, "Agent Tool 정보를 성공적으로 수정했습니다.");
    }

    @DeleteMapping("/{agentToolId}")
    @Operation(
        summary = "Agent Tool 삭제",
        description = "기존 Agent Tool을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agent Tool 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "Agent Tool을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteAgentToolById(
            @PathVariable("agentToolId") 
            @Parameter(description = "Agent Tool UUID", example = "bf63869d-df14-44f7-9a73-9ad9c014575c") 
            String agentToolId) {
        
        log.info("Agent Tool 삭제 요청: agentToolId={}", agentToolId);
        
        agentToolService.deleteAgentToolById(agentToolId);
        
        log.info("Agent Tool 삭제 완료: agentToolId={}", agentToolId);

        return AxResponseEntity.ok(null, "Agent Tool을 성공적으로 삭제했습니다.");
    }
}
