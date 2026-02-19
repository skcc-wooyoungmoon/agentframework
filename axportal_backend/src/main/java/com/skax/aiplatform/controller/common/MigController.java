package com.skax.aiplatform.controller.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.common.request.AssetValidationRequest;
import com.skax.aiplatform.dto.common.request.MigMasAsstSearchReq;
import com.skax.aiplatform.dto.common.request.MigMasSearchReq;
import com.skax.aiplatform.dto.common.response.MigMasRes;
import com.skax.aiplatform.dto.common.response.MigMasWithMapRes;
import com.skax.aiplatform.service.common.MigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 마이그레이션 관리 컨트롤러
 * 
 * <p>
 * 마이그레이션 정보 관리 및 에셋 검증을 위한 API를 제공합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/common/mig")
@RequiredArgsConstructor
@Tag(name = "마이그레이션 관리", description = "마이그레이션 정보 관리 및 에셋 검증 API")
public class MigController {

    private final MigService migService;
    private final ObjectMapper objectMapper;

    @Value("${migration.base-dir:}")
    private String migrationBaseDir;

    /**
     * 에셋 검증 (Asset Validation)
     * 
     * <p>
     * UUID로 Lineage를 조회하고, 각 target_type에 따라 서비스에서 데이터를 가져와
     * JSON으로 변환한 후 import 거래를 수행하고 검증합니다.
     * </p>
     * 
     * @param uuid      조회할 UUID
     * @param projectId 프로젝트 ID (폴더 구조 생성용)
     * @return 검증 결과 (성공: true, 실패: false)
     */
    @PostMapping("/asset-validation/{uuid}")
    @Operation(summary = "에셋 검증", description = "UUID로 Lineage를 조회하고, 각 target_type에 따라 서비스에서 데이터를 가져와 " +
            "JSON으로 변환한 후 import 거래를 수행하고 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "에셋 검증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "422", description = "에셋 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Boolean> assetValidation(
            @Parameter(description = "조회할 UUID", required = true, example = "0c625dc0-c1ed-4a6e-ba3a-2ebcbf234159") @PathVariable String uuid,
            @Parameter(description = "에셋 검증 요청 (프로젝트 ID 포함)", required = true) @RequestBody AssetValidationRequest request) {

        log.info("에셋 검증 요청 - uuid: {}, projectId: {}", uuid, request != null ? request.getProjectId() : null);

        // 트랜젝션 세션 강제설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username("admin")
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        if (request == null || request.getProjectId() == null || request.getProjectId().trim().isEmpty()) {
            log.error("프로젝트 ID가 누락되었습니다 - uuid: {}", uuid);
            return AxResponseEntity.error(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "프로젝트 ID는 필수입니다.");
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            log.error("타입이 누락되었습니다 - uuid: {}", uuid);
            return AxResponseEntity.error(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "타입은 필수입니다. (FILTER, SERVING_MODEL, APP, KNOWLEDGE 중 하나)");
        }

        boolean result = migService.assetValidation(uuid, request.getProjectId(), request.getType());

        log.info("에셋 검증 완료 - uuid: {}, projectId: {}, type: {}, result: {}",
                uuid, request.getProjectId(), request.getType(), result);

        AxResponseEntity<Boolean> response = AxResponseEntity.ok(result, "에셋 검증이 성공적으로 완료되었습니다.");

        return response;
    }

    /**
     * migration_temp 폴더를 migration 폴더로 복사
     * 
     * <p>
     * migration_temp/{projectName}/{type}/{id} 폴더를
     * migration/{projectName}/{type}/{id} 폴더로 전체 복사합니다.
     * </p>
     * 
     * @param type        객체 타입 (ObjectType enum)
     * @param id          객체 ID
     * @param projectName 프로젝트 이름
     * @return 복사된 대상 폴더 경로
     */
    @PostMapping("/copy-folder/{projectId}/{type}/{id}")
    @Operation(summary = "폴더 복사 (JSON 통합)", description = "migration_temp 폴더의 모든 JSON 파일을 하나의 JSON 파일로 통합합니다. " +
            "migration_temp/{projectId}/{type}/{id} → migration/{projectId}/{type}/{id}/{projectId}_{type}_{id}.json")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "JSON 파일 통합 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "소스 폴더를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<String> copyFolderFromTempToMigration(
            @Parameter(description = "프로젝트 ID", required = true, example = "-999") @PathVariable("projectId") String projectId,
            @Parameter(description = "객체 타입 (TOOL, MODEL, PROMPT, FEW_SHOT, GUARDRAILS, KNOWLEDGE, VECTOR_DB 등)", required = true, example = "TOOL") @PathVariable("type") String typeStr,
            @Parameter(description = "객체 ID", required = true, example = "tool-id-123") @PathVariable("id") String id,
            @Parameter(description = "마이그레이션 데이터 (dev 값을 prod로 업데이트할 데이터)", required = false) @RequestBody(required = false) JsonNode requestBody) {

        // // 트랜젝션 세션 강제설정
        // SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
        //         .username("admin")
        //         .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
        //         .authorities(Collections.emptyList())
        //         .build(), null, Collections.emptyList()));

        // JsonNode에서 migrationData, projectName, assetName 추출
        Map<String, List<Map<String, Object>>> migrationData = null;
        String projectName = null;
        String assetName = null;

        if (requestBody != null && !requestBody.isEmpty() && requestBody.isObject()) {
            log.debug("requestBody 내용: {}", requestBody.toString());

            // 1. 최상위 레벨에서 projectName, assetName 추출 시도
            if (requestBody.has("projectName")) {
                projectName = requestBody.get("projectName").asText();
                log.info("projectName 추출 성공 (최상위 레벨): {}", projectName);
            }

            if (requestBody.has("assetName")) {
                assetName = requestBody.get("assetName").asText();
                log.info("assetName 추출 성공 (최상위 레벨): {}", assetName);
            }

            // 2. migDeployData에서 projectName, assetName 추출 시도 (최상위 레벨에 없을 경우)
            if (requestBody.has("migDeployData")) {
                JsonNode migDeployData = requestBody.get("migDeployData");
                log.debug("migDeployData 존재: {}", migDeployData.toString());

                if (projectName == null && migDeployData.has("prjNm")) {
                    projectName = migDeployData.get("prjNm").asText();
                    log.info("projectName 추출 성공 (migDeployData.prjNm): {}", projectName);
                } else if (projectName == null) {
                    log.warn("migDeployData에 prjNm 필드가 없습니다. 사용 가능한 필드: {}", migDeployData.fieldNames());
                }

                if (assetName == null && migDeployData.has("name")) {
                    assetName = migDeployData.get("name").asText();
                    log.info("assetName 추출 성공 (migDeployData.name): {}", assetName);
                } else if (assetName == null) {
                    log.warn("migDeployData에 name 필드가 없습니다. 사용 가능한 필드: {}", migDeployData.fieldNames());
                }
            } else {
                log.warn("requestBody에 migDeployData 필드가 없습니다. 사용 가능한 필드: {}", requestBody.fieldNames());
            }

            // migrationData 추출 (resourceEndpoints 또는 나머지 데이터)
            try {
                // 1. resourceEndpoints가 있으면 그것을 migrationData로 사용
                if (requestBody.has("resourceEndpoints")) {
                    JsonNode resourceEndpoints = requestBody.get("resourceEndpoints");
                    @SuppressWarnings("unchecked")
                    Map<String, List<Map<String, Object>>> converted = objectMapper.convertValue(resourceEndpoints,
                            Map.class);
                    if (converted != null && !converted.isEmpty()) {
                        migrationData = converted;
                        log.info("migrationData 추출 성공 (resourceEndpoints에서): {}개 타입", migrationData.size());
                    }
                } else {
                    // 2. resourceEndpoints가 없으면 전체를 변환하고 projectName, assetName, migDeployData 제외
                    @SuppressWarnings("unchecked")
                    Map<String, List<Map<String, Object>>> converted = objectMapper.convertValue(requestBody,
                            Map.class);
                    if (converted != null) {
                        // projectName, assetName, migDeployData 제외
                        converted.remove("projectName");
                        converted.remove("assetName");
                        converted.remove("migDeployData");
                        if (!converted.isEmpty()) {
                            migrationData = converted;
                            log.info("migrationData 추출 성공 (전체에서 제외 후): {}개 타입", migrationData.size());
                        }
                    }
                }
            } catch (NullPointerException e) {
                log.error("migrationData 파싱 실패 - error: {}", e.getMessage(), e);
                migrationData = null;
            } catch (Exception e) {
                log.error("migrationData 파싱 실패 - error: {}", e.getMessage(), e);
                migrationData = null;
            }
        }

        log.info("=== 폴더 복사 요청 ===");
        log.info("projectId: {}", projectId);
        log.info("type: {}", typeStr);
        log.info("id: {}", id);
        log.info("projectName: [{}]", projectName);
        log.info("assetName: [{}]", assetName);
        log.info("migrationData: {}", migrationData != null ? "있음 (" + migrationData.size() + "개 타입)" : "없음");

        // String을 ObjectType으로 변환
        ObjectType type;
        try {
            type = ObjectType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 ObjectType - type: {}", typeStr);
            return AxResponseEntity.error(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "잘못된 객체 타입입니다: " + typeStr);
        }

        try {
            // JSON 파일로 통합 (migrationData 있으면 dev→prod 업데이트 포함)
            String jsonFilePath = migService.copyFolderFromTempToMigrationAsJson(type, id, projectId, migrationData,
                    projectName, assetName);

            if (jsonFilePath == null) {
                log.error("JSON 파일 통합 실패 - type: {}, id: {}, projectId: {}", type, id, projectId);
                return AxResponseEntity.error(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "JSON 파일 통합에 실패했습니다. 소스 폴더가 존재하지 않거나 통합 중 오류가 발생했습니다.");
            }

            log.info("JSON 파일 통합 완료 - type: {}, id: {}, projectName: {}, jsonFilePath: {}",
                    type, id, projectId, jsonFilePath);

            return AxResponseEntity.ok(jsonFilePath, "JSON 파일 통합이 성공적으로 완료되었습니다.");

        } catch (NullPointerException e) {
            log.error("폴더 복사 중 예외 발생 - type: {}, id: {}, projectName: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.INTERNAL_SERVER_ERROR,
                    "폴더 복사 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("폴더 복사 중 예외 발생 - type: {}, id: {}, projectName: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            return AxResponseEntity.error(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "폴더 복사 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 리소스 엔드포인트 추출
     * 
     * <p>
     * migration_temp/{projectId}/{type}/{id} 폴더의 JSON 파일들을 읽어서
     * 타입별로 필요한 정보를 추출하고, DB에서 매핑 정보를 조회하여 보완합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>폴더에서 모든 .json 파일 읽기</li>
     * <li>파일명 파싱: {TYPE}_{ID}.json</li>
     * <li>타입별로 정보 추출:
     * <ul>
     * <li>KNOWLEDGE: script, index_name을 {value, value} 형태로</li>
     * <li>VECTOR_DB: connection_info의 각 필드</li>
     * </ul>
     * </li>
     * <li>DB에서 GPO_MIG_MAS_MAP_MAS 조회하여 값 보완</li>
     * </ol>
     * 
     * @param projectId 프로젝트 ID
     * @param typeStr   객체 타입 문자열 (TOOL, MODEL, PROMPT, FEW_SHOT, GUARDRAILS,
     *                  KNOWLEDGE, VECTOR_DB 등)
     * @param id        객체 ID
     * @return 타입별로 추출한 정보를 담은 Map (타입별로 리스트 형태)
     */
    @GetMapping("/resource-endpoints/{projectId}/{type}/{id}")
    @Operation(summary = "리소스 엔드포인트 추출", description = "migration_temp 폴더의 JSON 파일들을 읽어서 타입별로 필요한 정보를 추출하고, " +
            "DB에서 매핑 정보를 조회하여 보완합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리소스 엔드포인트 추출 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "폴더를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Map<String, List<Map<String, Object>>>> extractResourceEndpoints(
            @Parameter(name = "projectId", description = "프로젝트 ID", required = true, example = "-999") @PathVariable("projectId") String projectId,
            @Parameter(name = "type", description = "객체 타입 (FILTER, SERVING_MODEL, AGENT_APP, KNOWLEDGE)", required = true, example = "KNOWLEDGE") @PathVariable("type") String typeStr,
            @Parameter(name = "id", description = "객체 ID", required = true, example = "knowledge-id-123") @PathVariable("id") String id) {

        // 트랜젝션 세션 강제설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username("admin")
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        log.info("리소스 엔드포인트 추출 요청 - projectId: {}, type: {}, id: {}", projectId, typeStr, id);

        // String을 ObjectType으로 변환
        ObjectType type;
        try {
            type = ObjectType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 ObjectType - type: {}", typeStr);
            return AxResponseEntity.error(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "잘못된 객체 타입입니다: " + typeStr);
        }

        try {
            Map<String, List<Map<String, Object>>> result = migService.extractMigrationDataFromFolder(
                    projectId, type, id);

            log.info("리소스 엔드포인트 추출 완료 - projectId: {}, type: {}, id: {}, extractedTypes: {}",
                    projectId, type, id, result != null ? result.keySet() : "empty");

            return AxResponseEntity.ok(result, "리소스 엔드포인트 추출이 성공적으로 완료되었습니다.");

        } catch (NullPointerException e) {
            log.error("리소스 엔드포인트 추출 중 예외 발생 - projectId: {}, type: {}, id: {}, error: {}",
                    projectId, type, id, e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.INTERNAL_SERVER_ERROR,
                    "리소스 엔드포인트 추출 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("리소스 엔드포인트 추출 중 예외 발생 - projectId: {}, type: {}, id: {}, error: {}",
                    projectId, type, id, e.getMessage(), e);
            return AxResponseEntity.error(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "리소스 엔드포인트 추출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 폴더에서 Import 및 후처리 수행
     * 
     * <p>
     * migration/{projectId}/{type}/{id} 폴더의 모든 JSON 파일을 읽어서
     * 존재하면 delete하고 import를 수행한 후, 타입별 후처리 함수를 실행합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>migration/{projectId}/{type}/{id} 폴더 경로 생성</li>
     * <li>폴더 내 모든 .json 파일 읽기</li>
     * <li>각 파일에 대해 존재 여부 확인 및 delete</li>
     * <li>파일 내용을 읽어서 import 수행</li>
     * <li>타입별 후처리 함수 실행</li>
     * </ol>
     * 
     * @param projectId 프로젝트 ID
     * @param typeStr   객체 타입 문자열 (SAFETY_FILTER, SERVING_MODEL, KNOWLEDGE,
     *                  AGENT_APP, PROJECT)
     * @param id        객체 ID
     * @return Import 및 후처리 결과
     */
    @PostMapping("/import-and-postprocess/{projectId}/{type}/{id}")
    @Operation(summary = "Import 및 후처리 수행", description = "migration 폴더의 JSON 파일들을 읽어서 import를 수행하고, 타입별 후처리 함수를 실행합니다. "
            +
            "지원 타입: SAFETY_FILTER, SERVING_MODEL, KNOWLEDGE, AGENT_APP(AGENT_GRAPH), PROJECT")
    public AxResponseEntity<Map<String, Object>> importAndMigration(
            @Parameter(name = "projectId", description = "프로젝트 ID", required = true, example = "-999") @PathVariable("projectId") String projectId,
            @Parameter(name = "type", description = "객체 타입 (SAFETY_FILTER, SERVING_MODEL, KNOWLEDGE, AGENT_APP, PROJECT)", required = true, example = "SAFETY_FILTER") @PathVariable("type") String typeStr,
            @Parameter(name = "id", description = "객체 ID", required = true, example = "tool-id-123") @PathVariable("id") String id) {

        // 트랜젝션 세션 강제설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username("admin")
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        log.info("import-and-postprocess 요청 시작 - projectId: {}, type: {}, id: {}", projectId, typeStr, id);

        // String을 ObjectType으로 변환
        ObjectType type;
        try {
            type = ObjectType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 ObjectType - type: {}", typeStr);
            return AxResponseEntity.error(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "잘못된 객체 타입입니다: " + typeStr);
        }

        try {
            Map<String, Object> result = migService.importAndMigration(projectId, type, id);

            Boolean importSuccess = (Boolean) result.get("importSuccess");
            if (importSuccess == null || !importSuccess) {
                log.error("Import 실패 - projectId: {}, type: {}, id: {}", projectId, type, id);
                return AxResponseEntity.error(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        result.get("error") != null ? result.get("error").toString() : "Import에 실패했습니다.");
            }

            log.info("Import 및 후처리 완료 - projectId: {}, type: {}, id: {}", projectId, type, id);

            return AxResponseEntity.ok(result, "Import 및 후처리가 성공적으로 완료되었습니다.");

        } catch (NullPointerException e) {
            log.error("Import 및 후처리 중 예외 발생 - projectId: {}, type: {}, id: {}, error: {}",
                    projectId, type, id, e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Import 및 후처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Import 및 후처리 중 예외 발생 - projectId: {}, type: {}, id: {}, error: {}",
                    projectId, type, id, e.getMessage(), e);
            return AxResponseEntity.error(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "Import 및 후처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 운영 이행 관리 조회
     * 
     * <p>
     * GPO_MIG_MAS 테이블을 조회하여 운영 이행 이력을 확인합니다.
     * </p>
     * 
     * <h3>조회 조건:</h3>
     * <ul>
     * <li>조회 기간: fst_created_at BETWEEN startDate AND endDate</li>
     * <li>이행 대상: asst_nm LIKE %asstNm%</li>
     * <li>이행 분류: asst_g = asstG</li>
     * </ul>
     * 
     * <h3>페이지네이션:</h3>
     * <ul>
     * <li>페이지 크기: 12, 36, 60개씩 보기</li>
     * <li>정렬: 이행 요청일시 내림차순 (최신순)</li>
     * </ul>
     * 
     * @param request 조회 요청 DTO
     * @return 페이지네이션된 운영 이행 목록
     */
    @GetMapping("/mig-mas")
    @Operation(summary = "운영 이행 관리 조회", description = "GPO_MIG_MAS 테이블을 조회하여 운영 이행 이력을 확인합니다. " +
            "조회 기간, 이행 대상, 이행 분류로 필터링할 수 있으며, 페이지네이션을 지원합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<MigMasRes>> searchMigMas(
            @Parameter(description = "조회 요청", required = true) @org.springframework.web.bind.annotation.ModelAttribute MigMasSearchReq request) {

        log.info("운영 이행 관리 조회 요청 - page: {}, size: {}, startDate: {}, endDate: {}, asstNm: {}, asstG: {}, prjSeq: {}",
                request.getPage(), request.getSize(), request.getStartDate(), request.getEndDate(),
                request.getAsstNm(), request.getAsstG(), request.getPrjSeq());

        // 트랜젝션 세션 강제설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username("admin")
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        try {
            PageResponse<MigMasRes> result = migService.searchMigMas(request);

            log.info("운영 이행 관리 조회 완료 - totalElements: {}, totalPages: {}",
                    result.getTotalElements(), result.getTotalPages());

            return AxResponseEntity.ok(result, "운영 이행 관리 조회가 성공적으로 완료되었습니다.");

        } catch (NullPointerException e) {
            log.error("운영 이행 관리 조회 중 예외 발생 - error: {}", e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.INTERNAL_SERVER_ERROR,
                    "운영 이행 관리 조회 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("운영 이행 관리 조회 중 예외 발생 - error: {}", e.getMessage(), e);
            return AxResponseEntity.error(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "운영 이행 관리 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 운영 이행 관리 조회 (조인 결과)
     * 
     * <p>
     * GPO_MIG_MAS와 GPO_MIG_ASST_MAP_MAS를 조인하여 모든 정보를 조회합니다.
     * </p>
     * 
     * <h3>조회 내용:</h3>
     * <ul>
     * <li>GPO_MIG_MAS의 모든 컬럼</li>
     * <li>GPO_MIG_ASST_MAP_MAS의 모든 컬럼</li>
     * </ul>
     * 
     * <h3>정렬:</h3>
     * <ul>
     * <li>이행 요청일시 내림차순 (최신순)</li>
     * <li>매핑 시퀀스 오름차순</li>
     * </ul>
     * 
     * @return 조인된 운영 이행 목록
     */
    @GetMapping("/mig-mas-with-map")
    @Operation(summary = "운영 이행 관리 조회 (조인 결과)", description = "GPO_MIG_MAS와 GPO_MIG_ASST_MAP_MAS를 조인하여 모든 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<MigMasWithMapRes>> findAllMigMasWithMap(
            @Parameter(description = "SEQUENCE 번호", required = false) @RequestParam(required = false) Integer sequence,
            @Parameter(description = "UUID", required = false) @RequestParam(required = false) String uuid,
            @Parameter(description = "이행 분류", required = false) @RequestParam(required = false) String asstG) {
        
        // Request DTO 생성
        MigMasAsstSearchReq request = MigMasAsstSearchReq.builder()
                .sequence(sequence)
                .uuid(uuid)
                .asstG(asstG)
                .build();


        
        // 트랜젝션 세션 강제설정
        // 트랜젝션 세션 강제설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username("admin")
                .password("") // JWT 토큰 기반 인증에서는 비밀번호 불필요
                .authorities(Collections.emptyList())
                .build(), null, Collections.emptyList()));

        log.info("운영 이행이력 추가정보 조회 요청 - sequence: {}, uuid: {}, asstG: {}",
                request.getSequence(), request.getUuid(), request.getAsstG());

        try {
            List<MigMasWithMapRes> result = migService.findAllMigMasWithMap(request);

            log.info("운영 이행 관리 조회 (조인) 완료 - totalCount: {}", result != null ? result.size() : 0);

            return AxResponseEntity.ok(result, "운영 이행 관리 조회 (조인)가 성공적으로 완료되었습니다.");

        } catch (NullPointerException e) {
            log.error("운영 이행 관리 조회 (조인) 중 예외 발생 - error: {}", e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.INTERNAL_SERVER_ERROR,
                    "운영 이행 관리 조회 (조인) 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("운영 이행 관리 조회 (조인) 중 예외 발생 - error: {}", e.getMessage(), e);
            return AxResponseEntity.error(
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "운영 이행 관리 조회 (조인) 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
