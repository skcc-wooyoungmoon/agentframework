package com.skax.aiplatform.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.service.admin.ResrcMgmtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 자원 관리 컨트롤러
 * 
 * @author SonMunWoo
 * @since 2025-09-27
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/admin/resrc-mgmt")
@RequiredArgsConstructor
@Tag(name = "ResrcMgmtController", description = "자원 관리 API")
public class ResrcMgmtController {
    
    private final ResrcMgmtService resrcMgmtService;


    @GetMapping("/portal")
    @Operation(summary = "포탈 자원 현황 조회", description = "에이전트 배포, 모델 배포, IDE 자원 현황을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "포탈 자원 현황 조회 성공"),
        @ApiResponse(responseCode = "500", description = "포탈 자원 현황 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getPortalResources(
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String searchValue
    ) {
        log.info("포탈 자원 현황 조회 요청");

        /*
        2025-12-29 smw 프로메테우스(모델, 에이전트)데이터를 배제하고 ide와 프론트쪽의 api 데이터(할당량)로 조회 변경
        Map<String, Object> portalResources = resrcMgmtService.getPortalResources();
        searchType, searchValue 파라미터 추가
        */
        Map<String, Object> portalIdeResources = resrcMgmtService.getPortalIdeResources(searchType, searchValue);

        return AxResponseEntity.ok(portalIdeResources, "포탈 IDE 자원 현황을 성공적으로 조회했습니다.");
    }

    @GetMapping("/portal/agent-pods")
    @Operation(summary = "포탈 에이전트 파드별 자원 현황 조회", description = "에이전트 파드별 자원 현황을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "포탈 에이전트 파드별 자원 현황 조회 성공"),
        @ApiResponse(responseCode = "500", description = "포탈 에이전트 파드별 자원 현황 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getPortalAgentPodResources() {
        log.info("포탈 에이전트 파드별 자원 현황 조회 요청");

        Map<String, Object> agentPodResources = resrcMgmtService.getPortalAgentPodResources();

        return AxResponseEntity.ok(agentPodResources, "포탈 에이전트 파드별 자원 현황을 성공적으로 조회했습니다.");
    }

    @GetMapping("/portal/model-pods")
    @Operation(summary = "포탈 모델 파드별 자원 현황 조회", description = "모델 파드별 자원 현황을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "포탈 모델 파드별 자원 현황 조회 성공"),
        @ApiResponse(responseCode = "500", description = "포탈 모델 파드별 자원 현황 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getPortalModelPodResources() {
        log.info("포탈 모델 파드별 자원 현황 조회 요청");

        Map<String, Object> portalResources = resrcMgmtService.getPortalResources();
        Map<String, Object> modelResources = new HashMap<>();
        if (portalResources != null) {
            Object componentsObj = portalResources.get("components");
            if (componentsObj instanceof Map<?, ?> componentsMap) {
                Object modelObj = componentsMap.get("Model");
                if (modelObj instanceof Map<?, ?> modelMap) {
                    modelMap.forEach((k, v) -> modelResources.put(String.valueOf(k), v));
                }
            }
        }

        if (modelResources.isEmpty()) {
            return AxResponseEntity.ok(modelResources, "모델 자원 데이터가 없어 빈 결과를 반환합니다.");
        }

        return AxResponseEntity.ok(modelResources, "포탈 모델 파드별 자원 현황을 성공적으로 조회했습니다.");
    }


    @GetMapping("/gpu-node")
    @Operation(summary = "GPU 노드별 자원 현황 조회", description = "GPU 노드별 자원 현황을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "GPU 노드별 자원 현황 조회 성공"),
        @ApiResponse(responseCode = "500", description = "GPU 노드별 자원 현황 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getGpuNodeResources() {
        log.info("GPU 노드별 자원 현황 조회 요청");

        Map<String, Object> gpuNodeResources = resrcMgmtService.getGpuNodeResources();

        return AxResponseEntity.ok(gpuNodeResources, "GPU 노드별 자원 현황을 성공적으로 조회했습니다.");
    }


    @GetMapping("/gpu-node/detail")
    @Operation(summary = "GPU 노드별 상세 자원 현황 조회", description = "특정 GPU 노드의 상세 자원 현황을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "GPU 노드별 상세 자원 현황 조회 성공"),
        @ApiResponse(responseCode = "500", description = "GPU 노드별 상세 자원 현황 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getGpuNodeDetailResources(
            @RequestParam String nodeName,
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam(required = false) String workloadName) {
        log.info("GPU 노드별 상세 자원 현황 조회 요청 - 노드: {}, 기간: {} ~ {}, 워크로드: {}", nodeName, fromDate, toDate, workloadName);

        // 날짜를 timestamp로 변환하고 차이값 계산
        long fromTimestamp = convertToUnixTimestamp(fromDate);
        long toTimestamp = convertToUnixTimestamp(toDate);
        long durationSeconds = toTimestamp - fromTimestamp;
        String durationParam = durationSeconds + "s";

        Map<String, Object> gpuNodeDetailResources = resrcMgmtService.getGpuNodeDetailResources(nodeName, fromDate, toDate, durationParam, fromTimestamp, toTimestamp, workloadName);

        return AxResponseEntity.ok(gpuNodeDetailResources, "GPU 노드별 상세 자원 현황을 성공적으로 조회했습니다.");
    }


    @GetMapping("/solution")
    @Operation(summary = "솔루션 자원 현황 조회", description = "솔루션별 자원 현황을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "솔루션 자원 현황 조회 성공"),
        @ApiResponse(responseCode = "500", description = "솔루션 자원 현황 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getSolutionResources() {
        log.info("솔루션 자원 현황 조회 요청");

        Map<String, Object> solutionResources = resrcMgmtService.getSolutionResources();

        return AxResponseEntity.ok(solutionResources, "솔루션 자원 현황을 성공적으로 조회했습니다.");
    }



    @GetMapping("/solution/detail")
    @Operation(summary = "솔루션별 상세 자원 현황 조회", description = "특정 솔루션의 상세 자원 현황을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "솔루션별 상세 자원 현황 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "솔루션별 상세 자원 현황 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getSolutionDetailResources(
            @RequestParam String nameSpace,
            @RequestParam(required = false) String podName,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        log.info("솔루션별 상세 자원 현황 조회 요청 - 네임스페이스: {}, Pod: {}, 기간: {} ~ {}", 
                nameSpace, podName != null ? podName : "전체", fromDate, toDate);

        // 날짜 형식 정규화 (공백을 T로 변환하여 RFC3339 형식으로)
        String normalizedFromDate = normalizeDateFormat(fromDate);
        String normalizedToDate = normalizeDateFormat(toDate);
        
        log.info("정규화된 날짜 - from: {}, to: {}", normalizedFromDate, normalizedToDate);
        
        Map<String, Object> solutionDetailResources = resrcMgmtService.getSolutionDetailResources(
            nameSpace, podName, normalizedFromDate, normalizedToDate, null);

        return AxResponseEntity.ok(solutionDetailResources, "솔루션별 상세 자원 현황을 성공적으로 조회했습니다.");
    }

    /**
     * 날짜 형식 정규화
     * 다양한 날짜 형식을 RFC3339 형식으로 변환
     * 
     * @param dateString 날짜 문자열 (공백 또는 T로 구분)
     * @return RFC3339 형식의 날짜 문자열
     */
    private String normalizeDateFormat(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return dateString;
        }
        
        // 이미 RFC3339 형식인 경우 (T와 Z 포함)
        if (dateString.contains("T") && dateString.endsWith("Z")) {
            return dateString;
        }
        
        // 공백을 T로 변환하고 Z 추가 (예: "2025-10-09 14:30:00" -> "2025-10-09T14:30:00Z")
        if (dateString.contains(" ")) {
            String normalized = dateString.replace(" ", "T");
            if (!normalized.endsWith("Z")) {
                normalized += "Z";
            }
            return normalized;
        }
        
        // T는 있지만 Z가 없는 경우 (예: "2025-10-09T14:30:00" -> "2025-10-09T14:30:00Z")
        if (dateString.contains("T") && !dateString.endsWith("Z")) {
            return dateString + "Z";
        }
        
        // 날짜만 있는 경우 (예: "2025-10-09" -> "2025-10-09T00:00:00Z")
        if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return dateString + "T00:00:00Z";
        }
        
        return dateString;
    }
    
    /**
     * 날짜 문자열을 Unix timestamp로 변환
     * 
     * @param dateString 날짜 문자열 (ISO 8601 형식 또는 날짜만)
     * @return Unix timestamp (초 단위)
     */
    private long convertToUnixTimestamp(String dateString) {
        try {
            if (dateString == null || dateString.isBlank()) {
                return System.currentTimeMillis() / 1000;
            }

            String normalizedDate = dateString.trim();

            if (normalizedDate.contains(" ") && !normalizedDate.contains("T")) {
                normalizedDate = normalizedDate.replace(" ", "T");
            }

            if (normalizedDate.endsWith("Z")) {
                return java.time.Instant.parse(normalizedDate).getEpochSecond();
            }

            // ISO 8601 형식 파싱 (날짜+시간)
            if (normalizedDate.contains("T")) {
                LocalDateTime dateTime = LocalDateTime.parse(normalizedDate);
                return dateTime.atZone(ZoneId.of("UTC")).toEpochSecond();
            } else {
                // 날짜만 있는 경우
                LocalDate date = LocalDate.parse(normalizedDate);
                return date.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
            }
        } catch (java.time.format.DateTimeParseException e) {
            log.warn("날짜 파싱 실패: {}, 현재 시간 사용 - {}", dateString, e.getMessage());
            return System.currentTimeMillis() / 1000;
        } catch (RuntimeException e) {
            log.warn("날짜 변환 실패: {}, 현재 시간 사용 - {}", dateString, e.getMessage());
            return System.currentTimeMillis() / 1000;
        }
    }

    @GetMapping("/solution/info")
    @Operation(summary = "솔루션 정보 조회", description = "솔루션의 기본 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "솔루션 정보 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "솔루션 정보 조회 실패")
    })
    public AxResponseEntity<Map<String, Object>> getSolutionInfo(
            @RequestParam String nameSpace) {
        
        Map<String, Object> solutionInfo = resrcMgmtService.getSolutionInfo(nameSpace);
        
        return AxResponseEntity.ok(solutionInfo, "솔루션 정보를 성공적으로 조회했습니다.");
    }

}
