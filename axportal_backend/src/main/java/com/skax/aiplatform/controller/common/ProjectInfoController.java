package com.skax.aiplatform.controller.common;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.common.request.SetPublicRequest;
import com.skax.aiplatform.dto.common.response.AssetProjectInfoRes;
import com.skax.aiplatform.service.common.ProjectInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 프로젝트 정보 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/common/project-info")
@RequiredArgsConstructor
@Tag(name = "Common 관련", description = "프로젝트 정보 관리 API")
public class ProjectInfoController {

    private final ProjectInfoService projectInfoService;

    /**
     * 자산을 공개 프로젝트로 설정
     * 
     * @param type 자산 타입 (agent, few-shot, tool, mcp)
     * @param id 자산 ID
     */
    @PutMapping("/public")
    @Operation(
            summary = "자산 공개 설정",
            description = "프라이빗 프로젝트에 속한 자산을 공개 프로젝트로 전환합니다. 지원 타입: agent, app, few-shot, tool, mcp"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자산 공개 설정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> setPublicFromPrivate(
            @Valid @RequestBody SetPublicRequest setPublicRequest) {
        
        log.info("자산 공개 설정 요청 - type: {}, id: {}", setPublicRequest.getType(), setPublicRequest.getId());
        
        projectInfoService.setPublicFromPrivate(setPublicRequest);
        
        return AxResponseEntity.ok(null, "자산을 공개 프로젝트로 성공적으로 설정했습니다.");
    }

    /**
     * UUID로 자산-프로젝트 매핑 정보 조회
     * 
     * @param uuid 자산 UUID
     * @return 자산-프로젝트 매핑 정보
     */
    @GetMapping("/asset/{uuid}")
    @Operation(
            summary = "자산-프로젝트 매핑 정보 조회",
            description = "UUID로 자산-프로젝트 매핑 정보를 조회합니다. 프로젝트명, 사용자 정보 등을 포함합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자산-프로젝트 매핑 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "자산을 찾을 수 없음")
    })
    public AxResponseEntity<AssetProjectInfoRes> getAssetProjectInfo(
            @Parameter(description = "자산 UUID", required = true, example = "f1d7c207-7c66-4f26-b995-18a56e4e7cd4")
            @PathVariable String uuid) {
        
        log.info("자산-프로젝트 매핑 정보 조회 요청 - uuid: {}", uuid);
        
        AssetProjectInfoRes assetProjectInfo = projectInfoService.getAssetProjectInfoByUuid(uuid);
        
        if (assetProjectInfo == null) {
            return AxResponseEntity.ok(null, "자산-프로젝트 매핑 정보를 찾을 수 없습니다.");
        }
        
        return AxResponseEntity.ok(assetProjectInfo, "자산-프로젝트 매핑 정보를 성공적으로 조회했습니다.");
    }
}

