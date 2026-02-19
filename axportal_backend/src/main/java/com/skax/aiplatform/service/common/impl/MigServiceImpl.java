package com.skax.aiplatform.service.common.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.client.sktai.lineage.service.SktaiLineageService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.common.request.MigMasAsstSearchReq;
import com.skax.aiplatform.dto.common.request.MigMasSearchReq;
import com.skax.aiplatform.dto.common.response.MigMasRes;
import com.skax.aiplatform.dto.common.response.MigMasWithMapRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployRes;
import com.skax.aiplatform.entity.deploy.GpoMigAsstMapMas;
import com.skax.aiplatform.entity.deploy.GpoMigMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.repository.deploy.GpoMigAsstMapMasRepository;
import com.skax.aiplatform.repository.deploy.GpoMigMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.common.AgentAppMigService;
import com.skax.aiplatform.service.common.ExternalRepoMigService;
import com.skax.aiplatform.service.common.FewShotMigService;
import com.skax.aiplatform.service.common.GraphMigService;
import com.skax.aiplatform.service.common.GuardrailMigService;
import com.skax.aiplatform.service.common.InferencePromptMigService;
import com.skax.aiplatform.service.common.McpMigService;
import com.skax.aiplatform.service.common.MigService;
import com.skax.aiplatform.service.common.ModelMigService;
import com.skax.aiplatform.service.common.ProjectMigService;
import com.skax.aiplatform.service.common.SafetyFilterMigService;
import com.skax.aiplatform.service.common.ServingModelMigService;
import com.skax.aiplatform.service.common.ToolMigService;
import com.skax.aiplatform.service.common.VectorDbMigService;
import com.skax.aiplatform.service.deploy.AgentDeployService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 마이그레이션 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MigServiceImpl implements MigService {

    private final GpoMigMasRepository repository;
    private final GpoMigAsstMapMasRepository mapMasRepository;
    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final SktaiLineageService sktaiLineageService;
    private final ObjectMapper objectMapper;

    @Qualifier("asyncTaskExecutor")
    private final Executor asyncTaskExecutor;

    @Value("${migration.base-dir:}")
    private String migrationBaseDir;

    // 타입별 MigService
    private final ToolMigService toolMigService;
    private final GuardrailMigService guardrailMigService;
    private final InferencePromptMigService inferencePromptMigService;
    private final ModelMigService modelMigService;
    private final ExternalRepoMigService externalRepoMigService;
    private final FewShotMigService fewShotMigService;
    private final VectorDbMigService vectorDbMigService;
    private final McpMigService mcpMigService;
    private final GraphMigService graphMigService;
    private final ProjectMigService projectMigService;
    private final SafetyFilterMigService safetyFilterMigService;
    private final ServingModelMigService servingModelMigService;
    private final AgentAppMigService agentAppMigService;
    
    @Lazy
    @Autowired
    private AgentDeployService agentDeployService;

    /**
     * 타입별 추출할 필드 목록 정의
     *
     * <p>
     * 각 타입별로 추출해야 하는 필드명을 선언적으로 관리합니다.
     * </p>
     */
    private static final Map<ObjectType, List<String>> TYPE_EXTRACT_FIELDS = new java.util.HashMap<>();

    static {
        TYPE_EXTRACT_FIELDS.put(ObjectType.KNOWLEDGE, List.of("script", "index_name"));
        TYPE_EXTRACT_FIELDS.put(ObjectType.VECTOR_DB, List.of("endpoint", "key", "host", "port", "user",
                "password", "secure", "api_key", "db_name"));
        TYPE_EXTRACT_FIELDS.put(ObjectType.MCP, List.of("server_url", "auth_config"));
        TYPE_EXTRACT_FIELDS.put(ObjectType.TOOL, List.of("server_url", "api_param"));
        TYPE_EXTRACT_FIELDS.put(ObjectType.AGENT_GRAPH, List.of("nodes", "edges", "config", "agent_app_nodes"));
        TYPE_EXTRACT_FIELDS.put(ObjectType.SERVING_MODEL,
                List.of("resourceGroup", "gpuRequest", "cpuRequest", "memRequest", "servingParams", "minReplicas", "maxReplicas"));
        TYPE_EXTRACT_FIELDS.put(ObjectType.MODEL, List.of("endpoints.url", "endpoints.key"));
        // TYPE_EXTRACT_FIELDS.put(ObjectType.AGENT_APP, List.of("image_url", "app_id"));
        TYPE_EXTRACT_FIELDS.put(ObjectType.AGENT_APP, List.of("image_url", "app_id", "cpu_request", "mem_request", "min_replicas", "max_replicas", "cpu_limit", "mem_limit"));
    }

    /**
     * 파일 인코딩 목록 (우선순위 순)
     */
    private static final String[] ENCODINGS = {"UTF-8", "MS949", "EUC-KR", "ISO-8859-1", "Windows-1252"};

    /*
     * -999 -> public
     */
    private String changeM999ToPublic(String projectId) {
        // -999 -> public
        if (projectId.equals("-999")) {
            log.info("MIG :: 프로젝트 ID가 -999입니다.");
            return "public";
        }
        // 이외 기존 id 유지
        return projectId;
    }

    /**
     * public -> -999
     */
    private String changePublicToM999(String projectId) {
        // public -> -999
        if (projectId.equals("public")) {
            log.info("MIG :: 프로젝트 ID가 public입니다.");
            return "-999";
        }
        // 이외 기존 id 유지
        return projectId;
    }

    /**
     * DB에서 에셋의 프로젝트 ID 조회 (모든 타입 통일)
     *
     * @param assetId           에셋 UUID
     * @param fallbackProjectId DB 조회 실패 시 사용할 프로젝트 ID
     * @return 프로젝트 ID (문자열), 없으면 fallbackProjectId 반환
     */
    private String getProjectIdFromDb(String assetId, String fallbackProjectId) {
        try {
            // asst_url LIKE '%{uuid}'로 조회
            List<GpoAssetPrjMapMas> mappings = assetPrjMapMasRepository.findByAsstUrlContaining(assetId);

            if (mappings != null && !mappings.isEmpty()) {
                Integer prjSeq = mappings.get(0).getLstPrjSeq();
                if (prjSeq != null) {
                    // DB에서 조회한 값을 그대로 반환 (JSON에 저장되므로 변환 불필요)
                    String projectId = String.valueOf(prjSeq);
                    log.debug("MIG :: DB에서 프로젝트 ID 조회 성공 - assetId: {}, projectId: {}", assetId, projectId);
                    return projectId;
                }
            }

            log.warn("MIG :: DB에서 프로젝트 ID를 찾을 수 없음 (기존 프로젝트 사용) - assetId: {}, fallbackProjectId: {}",
                    assetId, fallbackProjectId);
            return fallbackProjectId;

        } catch (Exception e) {
            log.warn("MIG :: DB에서 프로젝트 ID 조회 실패 (기존 프로젝트 사용) - assetId: {}, error: {}, fallbackProjectId: {}",
                    assetId, e.getMessage(), fallbackProjectId);
            return fallbackProjectId;
        }
    }

    /**
     * 기본 디렉토리 경로 반환
     *
     * <p>
     * yaml 설정(migration.base-dir)이 있으면 해당 경로 사용,
     * 없으면 기본 경로 사용 (/gapdat/migration/aiplatform)
     * </p>
     *
     * @return 기본 디렉토리 경로
     */
    private String getBaseDir() {
        // yaml 설정이 있으면 우선 사용
        if (StringUtils.hasText(migrationBaseDir)) {
            log.info("MIG :: Migration base directory from config: {}", migrationBaseDir);
            return migrationBaseDir;
        }

        // 설정이 없으면 기본 경로 사용 (OS 무관하게 동일)
        String baseDir = "/gapdat/migration/aiplatform";
        log.info("MIG :: Migration base directory (default, 설정값 없음): {}", baseDir);
        return baseDir;
    }

    /**
     * migration_temp 폴더 경로 생성
     *
     * @param projectId 프로젝트 ID
     * @param type      객체 타입
     * @param id        객체 ID
     * @return migration_temp 폴더 경로
     */
    private String buildMigrationTempPath(String projectId, ObjectType type, String id) {
        String baseDir = getBaseDir();
        // 설정값에 separator가 포함되어 있으므로 그대로 사용
        String separator = baseDir.contains("\\") ? "\\" : "/";

        // SafetyFilter의 경우 여러 ID가 |로 구분되어 있을 수 있으므로, 폴더명에 사용할 키 생성
        // |를 _로 변환하여 폴더명에 사용 (파일 시스템 호환성)
        String folderId = id;
        if (type == ObjectType.SAFETY_FILTER && id != null && id.contains("|")) {
            folderId = id.replace("|", "_");
            log.debug("MIG :: SafetyFilter 여러 ID 처리 (폴더명) - 원본: {}, 폴더명용: {}", id, folderId);
        }

        return String.format("%s%smigration_temp%s%s%s%s%s%s",
                baseDir, separator, separator, projectId, separator, type.name(), separator, folderId);
    }

    /**
     * migration 폴더 경로 생성
     *
     * @param projectId 프로젝트 ID
     * @param type      객체 타입
     * @param id        객체 ID
     * @return migration 폴더 경로
     */
    private String buildMigrationPath(String projectId, ObjectType type, String id) {
        String baseDir = getBaseDir();
        // 설정값에 separator가 포함되어 있으므로 그대로 사용
        String separator = baseDir.contains("\\") ? "\\" : "/";

        // SafetyFilter의 경우 여러 ID가 |로 구분되어 있을 수 있으므로, 폴더명에 사용할 키 생성
        // |를 _로 변환하여 폴더명에 사용 (파일 시스템 호환성)
        String folderId = id;
        if (type == ObjectType.SAFETY_FILTER && id != null && id.contains("|")) {
            folderId = id.replace("|", "_");
            log.debug("MIG :: SafetyFilter 여러 ID 처리 (폴더명) - 원본: {}, 폴더명용: {}", id, folderId);
        }

        return String.format("%s%smigration%s%s%s%s%s%s",
                baseDir, separator, separator, projectId, separator, type.name(), separator, folderId);
    }

    /**
     * 파일명에서 번호 추출
     *
     * @param fileName 파일명 (예: 001_TYPE_ID.json 또는 TYPE_ID.json)
     * @return 파일 번호 (없으면 0)
     */
    private int extractFileNumber(String fileName) {
        try {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));

            // num_type_uuid 형식인지 확인
            if (fileNameWithoutExt.matches("^\\d+_.+")) {
                int firstUnderscore = fileNameWithoutExt.indexOf('_');
                if (firstUnderscore > 0) {
                    try {
                        return Integer.parseInt(fileNameWithoutExt.substring(0, firstUnderscore));
                    } catch (NumberFormatException e) {
                        // 숫자 파싱 실패 시 0 반환
                        return 0;
                    }
                }
            }

            return 0; // 번호가 없으면 0
        } catch (NullPointerException e) {
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 파일명에서 ObjectType과 ID 파싱
     *
     * <p>
     * 파일명 형식 지원:
     * - {num}_{type}_{uuid}.json (새 형식)
     * - {type}_{uuid}.json (기존 형식, 하위 호환성)
     * </p>
     *
     * @param fileName 파일명 (예: 001_TYPE_ID.json 또는 TYPE_ID.json)
     * @return 파싱 결과 [ObjectType, ID], 파싱 실패 시 null
     */
    private Object[] parseFileName(String fileName) {
        try {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));

            // num_type_uuid 형식인지 확인
            String typeAndId = fileNameWithoutExt;
            if (fileNameWithoutExt.matches("^\\d+_.+")) {
                // num_ 접두사 제거
                int firstUnderscore = fileNameWithoutExt.indexOf('_');
                if (firstUnderscore > 0) {
                    typeAndId = fileNameWithoutExt.substring(firstUnderscore + 1);
                }
            }

            // ObjectType enum의 모든 값들을 확인하여 가장 긴 매칭 타입을 찾습니다
            ObjectType[] allTypes = ObjectType.values();
            java.util.Arrays.sort(allTypes, (a, b) -> Integer.compare(b.name().length(), a.name().length()));

            for (ObjectType candidateType : allTypes) {
                String typeName = candidateType.name();
                if (typeAndId.startsWith(typeName + "_")) {
                    String fileId = typeAndId.substring(typeName.length() + 1);
                    return new Object[]{candidateType, fileId};
                }
            }

            return null;
        } catch (NullPointerException e) {
            log.warn("MIG :: 파일명 파싱 실패 - fileName: {}, error: {}", fileName, e.getMessage());
            return null;
        } catch (RuntimeException re) {
            log.warn("MIG :: 파일명 파싱 실패 - fileName: {}, error: {}", fileName, re.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("MIG :: 파일명 파싱 실패 - fileName: {}, error: {}", fileName, e.getMessage());
            return null;
        }
    }

    /**
     * 파일을 읽고 인코딩을 자동 감지하여 JSON 문자열 반환
     *
     * @param filePath 파일 경로
     * @return JSON 문자열
     * @throws IOException 파일 읽기 실패 시
     */
    private String readJsonFileWithEncodingDetection(Path filePath) throws IOException {
        try {
            byte[] fileBytes = Files.readAllBytes(filePath);

            // UTF-8 먼저 시도
            try {
                String fileContent = new String(fileBytes, java.nio.charset.StandardCharsets.UTF_8);
                objectMapper.readTree(fileContent);
                return fileContent;
            } catch (NullPointerException e) {
                log.debug("MIG :: UTF-8 인코딩 실패 (NullPointerException) - filePath: {}, 다른 인코딩 시도", filePath);
                // 다른 인코딩 시도
            } catch (Exception e) {
                log.debug("MIG :: UTF-8 인코딩 실패 - filePath: {}, 다른 인코딩 시도", filePath);
                // 다른 인코딩 시도
            }

            // 다른 인코딩 시도
            for (int i = 1; i < ENCODINGS.length; i++) {
                try {
                    String fileContent = new String(fileBytes, java.nio.charset.Charset.forName(ENCODINGS[i]));
                    objectMapper.readTree(fileContent);
                    log.debug("MIG :: 인코딩 자동 감지 성공 - filePath: {}, encoding: {}", filePath, ENCODINGS[i]);
                    return fileContent;
                } catch (NullPointerException e2) {
                    continue;
                } catch (Exception e2) {
                    continue;
                }
            }

            // 모든 인코딩 실패 시 기본 인코딩 사용
            return new String(fileBytes, java.nio.charset.Charset.defaultCharset());
        } catch (NullPointerException e) {
            log.error("MIG :: 파일 읽기 실패 (NullPointerException) - filePath: {}, error: {}", filePath, e.getMessage(), e);
            throw new IOException("파일 읽기 실패: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("MIG :: 파일 읽기 실패 (IOException) - filePath: {}, error: {}", filePath, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("MIG :: 파일 읽기 실패 (예상치 못한 오류) - filePath: {}, error: {}", filePath, e.getMessage(), e);
            throw new IOException("파일 읽기 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 마이그레이션 정보 등록 (INSERT)
     */
    @Override
    @Transactional
    public GpoMigMas create(String uuid, String asstNm, String pgmDescCtnt) {
        log.info("MIG :: 마이그레이션 정보 등록 - uuid: {}, asstNm: {}, pgmDescCtnt: {}", uuid, asstNm, pgmDescCtnt);
        try {
            // 현재 사용자 정보 가져오기
            String currentUser = getCurrentUser();

            GpoMigMas newEntity = GpoMigMas.builder()
                    .uuid(uuid)
                    .asstNm(asstNm)
                    .pgmDescCtnt(pgmDescCtnt)
                    .delYn(0) // 0 - 정상, 1 - 삭제
                    .fstCreatedAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser)
                    .build();

            // INSERT 실행 (ID가 없으면 INSERT, 있으면 UPDATE)
            GpoMigMas saved = repository.save(newEntity);

            log.info("MIG :: 마이그레이션 정보 등록 완료 - seqNo: {}, uuid: {}", saved.getSeqNo(), saved.getUuid());

            return saved;
        } catch (NullPointerException e) {
            log.error("MIG :: 마이그레이션 정보 등록 실패 (NullPointerException) - uuid: {}, asstNm: {}, error: {}",
                    uuid, asstNm, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "마이그레이션 정보 등록 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("MIG :: 마이그레이션 정보 등록 실패 - uuid: {}, asstNm: {}, error: {}",
                    uuid, asstNm, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "마이그레이션 정보 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 마이그레이션 상태 업데이트 (UPDATE)
     * DEL_YN을 1로 변경 (이행 완료 처리)
     */
    @Override
    @Transactional
    public GpoMigMas updateToDeleted(String uuid) {
        log.info("MIG :: 마이그레이션 상태 업데이트 (이행 완료) - uuid: {}", uuid);

        try {
            GpoMigMas entity = repository.findByUuid(uuid)
                    .orElseThrow(
                            () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "마이그레이션 정보를 찾을 수 없습니다: " + uuid));

            entity.setDelYn(1); // 1 - 삭제
            GpoMigMas updated = repository.save(entity); // UPDATE 실행

            log.info("MIG :: 마이그레이션 상태 업데이트 완료 - uuid: {}, delYn: {}", updated.getUuid(), updated.getDelYn());

            return updated;
        } catch (BusinessException e) {
            throw e; // BusinessException은 그대로 재throw
        } catch (NullPointerException e) {
            log.error("MIG :: 마이그레이션 상태 업데이트 실패 (NullPointerException) - uuid: {}, error: {}",
                    uuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "마이그레이션 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("MIG :: 마이그레이션 상태 업데이트 실패 - uuid: {}, error: {}",

                    uuid, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "마이그레이션 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 마이그레이션 상태 조회 (SELECT)
     * 존재 여부와 삭제 여부를 확인하여 활성 상태 반환
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isActive(String uuid) {
        try {
            if (uuid == null || uuid.trim().isEmpty()) {
                return false;
            }

            // UUID 앞뒤 공백 제거 및 소문자 변환 (일관성 유지)
            String trimmedUuid = uuid.trim();

            boolean isActive = false;
            Optional<GpoMigMas> entity = repository.findByUuid(trimmedUuid);

            if (entity.isEmpty()) {

                // 대소문자 차이를 고려하여 한 번 더 시도 (대문자로)
                Optional<GpoMigMas> entityUpper = repository.findByUuid(trimmedUuid.toUpperCase());
                if (entityUpper.isPresent()) {
                    entity = entityUpper;
                } else {
                    // 소문자로도 시도
                    Optional<GpoMigMas> entityLower = repository.findByUuid(trimmedUuid.toLowerCase());
                    if (entityLower.isPresent()) {
                        entity = entityLower;
                    }
                }
            }

            if (entity.isEmpty()) {
                isActive = false;
            } else {
                GpoMigMas found = entity.get();
                Integer delYn = found.getDelYn();
                isActive = (delYn == null || delYn == 0); // 0: 정상, 1: 삭제
            }

            return isActive;
        } catch (NullPointerException e) {
            log.error("MIG :: 마이그레이션 상태 조회 실패 (NullPointerException) - uuid: {}, error: {}",
                    uuid, e.getMessage(), e);
            return false; // 조회 실패 시 false 반환
        } catch (Exception e) {
            log.error("MIG :: 마이그레이션 상태 조회 실패 - uuid: {}, error: {}",
                    uuid, e.getMessage(), e);
            return false; // 조회 실패 시 false 반환
        }
    }

    /**
     * 에셋 검증 (Asset Validation)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean assetValidation(String uuid, String projectId, String type) {
        List<String> errors = new ArrayList<>();
        List<String> successes = new ArrayList<>();
        int totalProcessed = 0;

        try {
            // 프로젝트 ID 유효성 검증
            if (projectId == null || projectId.trim().isEmpty()) {
                log.error("MIG :: 프로젝트 ID가 비어있습니다.");
                errors.add("MIG :: 프로젝트 ID가 필요합니다.");
                return false;
            }

            // 타입 유효성 검증
            if (type == null || type.trim().isEmpty()) {
                log.error("MIG :: 타입이 비어있습니다.");
                errors.add("MIG :: 타입이 필요합니다.");
                return false;
            }

            // PROJECT 타입인 경우: PROJECT Export만 수행하고 종료
            if ("PROJECT".equalsIgnoreCase(type)) {
                log.info("MIG :: PROJECT 타입 - PROJECT Export만 수행합니다.");

                try {
                    String sanitizedProjectId = changePublicToM999(projectId);
                    Long prjSeq = Long.parseLong(sanitizedProjectId);

                    log.info("MIG :: PROJECT Export 시작 - projectId: {}, prjSeq: {}", projectId, prjSeq);
                    projectMigService.exportProject(prjSeq);

                    // project_migration_data.json 경로
                    String projectExportFilePath = Paths.get(migrationBaseDir, "project_migration_data.json").toString();

                    log.info("MIG :: PROJECT Export 완료 - projectId: {}, filePath: {}", projectId, projectExportFilePath);
                    successes.add(String.format("PROJECT/%s (파일 생성 완료: %s)", projectId, projectExportFilePath));
                    return true;
                } catch (NumberFormatException e) {
                    log.error("MIG :: PROJECT Export 실패 - 유효하지 않은 projectId: {}", projectId, e);
                    errors.add("PROJECT Export 실패: 유효하지 않은 프로젝트 ID - " + projectId);
                    return false;
                } catch (Exception e) {
                    log.error("MIG :: PROJECT Export 실패 - projectId: {}, error: {}", projectId, e.getMessage(), e);
                    errors.add("PROJECT Export 실패: " + e.getMessage());
                    return false;
                }
            }

            // AGENT_APP일 때 커스텀 여부 확인 (deployments[].target_type == "external_graph")
            boolean isCustomAgentApp = false;
            if ("AGENT_APP".equalsIgnoreCase(type)) {
                try {
                    // AgentAppMigService의 isCustomAgentApp() 메서드 사용
                    isCustomAgentApp = agentAppMigService.isCustomAgentApp(uuid);
                    log.info("MIG :: AGENT_APP 커스텀 여부 확인 - uuid: {}, isCustom: {}", uuid, isCustomAgentApp);
                } catch (Exception e) {
                    log.warn("MIG :: AGENT_APP 커스텀 여부 확인 실패 (기본값: 일반 AGENT_APP으로 처리) - uuid: {}, error: {}",
                            uuid, e.getMessage());
                    // 조회 실패 시 일반 AGENT_APP으로 처리
                    isCustomAgentApp = false;
                }
            }

            // SERVING_MODEL, 일반 AGENT_APP만 리니지 조회 (커스텀 AGENT_APP은 리니지 조회 생략)
            // todo
            boolean needsLineage = "SERVING_MODEL".equalsIgnoreCase(type)
                    || "GUARDRAILS".equalsIgnoreCase(type)
                    || ("AGENT_APP".equalsIgnoreCase(type) && !isCustomAgentApp);

            List<LineageRelationWithTypes> filteredRelations = new ArrayList<>();

            if (needsLineage) {
                // 1. UUID로 Lineage 조회 (downstream 방향)
                log.info("MIG :: === Lineage 조회 시작 ===");
                log.info("MIG :: UUID: {}", uuid);
                log.info("MIG :: Project ID: {}", projectId);
                log.info("MIG :: Type: {}", type);

                // 타입에 따라 lineage 조회 depth 결정
                int lineageDepth = "AGENT_APP".equalsIgnoreCase(type) ? 2 : 5;
                log.info("MIG :: Lineage 조회 depth: {}", lineageDepth);

                List<LineageRelationWithTypes> lineageRelations;

                try {
                    lineageRelations = sktaiLineageService.getLineageByObjectKeyAndDirection(
                            uuid,
                            Direction.DOWNSTREAM,
                            ActionType.USE.getValue(),
                            lineageDepth);

                    log.info("MIG :: Lineage 조회 완료 - 총 {}개 관계 발견", lineageRelations != null ?
                            lineageRelations.size() : 0);

                    if (lineageRelations == null || lineageRelations.isEmpty()) {
                        log.info("MIG :: Lineage 관계가 없습니다.");
                        filteredRelations = new ArrayList<>();
                    } else {
                        // Lineage 관계 상세 로깅
                        log.info("MIG :: === Lineage 관계 상세 ===");
                        for (int i = 0; i < lineageRelations.size(); i++) {
                            LineageRelationWithTypes relation = lineageRelations.get(i);
                            log.info(
                                    "MIG :: [{}] source_type: {}, source_key: {}, target_type: {}, target_key: {}, " +
                                            "action: " +
                                            "[{}] source_type: {}, source_key: {}, target_type: {}, target_key: {}, " +
                                            "action: " +
                                            "{}, depth: {}",
                                    i + 1,
                                    relation.getSourceType(),
                                    relation.getSourceKey(),
                                    relation.getTargetType(),
                                    relation.getTargetKey(),
                                    relation.getAction(),
                                    relation.getDepth());
                        }

                        // 2. 필터링 및 depth 기준 정렬
                        log.info("MIG :: === 필터링 및 depth 기준 정렬 ===");
                        log.info("MIG :: 필터링 전 관계 수: {}", lineageRelations.size());
                        filteredRelations = lineageRelations.stream()
                                .filter(relation -> relation.getSourceType() != null && relation.getSourceKey() != null
                                        && relation.getTargetType() != null && relation.getTargetKey() != null)
                                .filter(relation -> {
                                    // AGENT_APP일 때만 depth 기반 필터링 적용
                                    if (!"AGENT_APP".equalsIgnoreCase(type)) {
                                        // AGENT_APP이 아니면 기존 로직 사용
                                        ObjectType sourceType = relation.getSourceType();
                                        ObjectType targetType = relation.getTargetType();
                                        return shouldProcessTargetType(type, sourceType, targetType);
                                    }

                                    // AGENT_APP인 경우 depth 기반 필터링
                                    Integer depth = relation.getDepth() != null ? relation.getDepth() : 0;

                                    // depth 3 이상은 패스
                                    if (depth >= 3) {
                                        log.info("MIG :: AGENT_APP: depth {} >= 3이므로 필터링됨 - sourceType: {}, " +
                                                        "targetType: {}, targetKey: {}",
                                                depth, relation.getSourceType(), relation.getTargetType(),
                                                relation.getTargetKey());
                                        return false;
                                    }

                                    // depth 1: AGENT_APP의 경우 AGENT_GRAPH만 처리
                                    if (depth == 1) {
                                        ObjectType targetType = relation.getTargetType();
                                        boolean isGraph = targetType == ObjectType.AGENT_GRAPH;
                                        if (!isGraph) {
                                            log.info("MIG :: AGENT_APP: depth 1에서 {} 필터링됨 (AGENT_GRAPH만 허용) - " +
                                                            "targetKey: {}",
                                                    targetType, relation.getTargetKey());
                                        }
                                        return isGraph;
                                    }

                                    // depth 2: KNOWLEDGE, VECTOR_DB, SERVING_MODEL 제외하고 나머지 모두 처리
                                    if (depth == 2) {
                                        ObjectType targetType = relation.getTargetType();
                                        boolean shouldExclude = targetType == ObjectType.KNOWLEDGE
                                                || targetType == ObjectType.VECTOR_DB
                                                || targetType == ObjectType.SERVING_MODEL
                                                || targetType == ObjectType.AGENT_APP;
                                        if (shouldExclude) {
                                            log.info("MIG :: AGENT_APP: depth 2에서 {} 필터링됨 (KNOWLEDGE, VECTOR_DB, " +
                                                            "SERVING_MODEL 제외) - targetKey: {}",

                                                    targetType, relation.getTargetKey());
                                        }
                                        return !shouldExclude;
                                    }

                                    // depth 0 또는 기타: 기존 로직 사용
                                    ObjectType sourceType = relation.getSourceType();
                                    ObjectType targetType = relation.getTargetType();
                                    return shouldProcessTargetType(type, sourceType, targetType);
                                })
                                .filter(relation -> {
                                    // SERVING_MODEL은 Pass 처리 (일반적으로, 단 KNOWLEDGE -> SERVING_MODEL -> 하위는 처리)
                                    ObjectType targetType = relation.getTargetType();
                                    if (targetType == ObjectType.SERVING_MODEL
                                            && !"SERVING_MODEL".equalsIgnoreCase(type)) {
                                        ObjectType sourceType = relation.getSourceType();
                                        // KNOWLEDGE 타입이고 sourceType이 KNOWLEDGE인 경우는 SERVING_MODEL을 Pass하되 하위는 처리
                                        if (!("KNOWLEDGE".equalsIgnoreCase(type)
                                                && sourceType == ObjectType.KNOWLEDGE)) {
                                            return false;
                                        }
                                    }
                                    return true;
                                })
                                .sorted((r1, r2) -> {
                                    // depth가 높은 것부터 낮은 순서로 정렬 (depth 1이 가장 마지막)
                                    Integer depth1 = r1.getDepth() != null ? r1.getDepth() : 0;
                                    Integer depth2 = r2.getDepth() != null ? r2.getDepth() : 0;
                                    return depth2.compareTo(depth1); // 내림차순 정렬
                                })
                                .collect(Collectors.toList());
                        log.info("MIG :: 필터링 후 관계 수: {}", filteredRelations.size());
                    }

                } catch (FeignException e) {
                    log.error("MIG :: Lineage 조회 실패 - uuid: {}, error: {}", uuid, e.getMessage(), e);
                    errors.add("Lineage 조회 실패: " + e.getMessage());
                    return false;
                }
            } else {
                log.info("MIG :: === 리니지 조회 생략 (타입: {}) ===", type);
                log.info("MIG :: UUID: {}", uuid);
                log.info("MIG :: Project ID: {}", projectId);
                log.info("MIG :: Type: {}", type);
                log.info("MIG :: Project ID: {}", projectId);
            }

            ///////////////// 3. 단일 폴더 구조 사용 (모든 타입 동일)
            // -999 > public
            projectId = changeM999ToPublic(projectId);
            ObjectType topLevelType = ObjectType.valueOf(type);
            String baseFolderPath = buildMigrationTempPath(projectId, topLevelType, uuid);
            Path baseFolder = Paths.get(baseFolderPath);

            // 설정값 및 경로 로깅
            log.info("MIG :: === 폴더 생성 경로 정보 ===");
            log.info("MIG :: migrationBaseDir 설정값: [{}]", migrationBaseDir);
            log.info("MIG :: getBaseDir() 반환값: [{}]", getBaseDir());
            log.info("MIG :: baseFolderPath (문자열): [{}]", baseFolderPath);
            log.info("MIG :: baseFolder (Path): [{}]", baseFolder);
            log.info("MIG :: baseFolder.toAbsolutePath(): [{}]", baseFolder.toAbsolutePath());
            log.info("MIG :: 현재 작업 디렉토리: [{}]", System.getProperty("user.dir"));

            // 폴더 초기화 및 생성
            try {
                if (Files.exists(baseFolder)) {
                    // 이전 assetValidation 실행에서 남아있는 파일/PROJECT JSON 등을 모두 제거
                    log.info("MIG :: 기존 migration_temp 폴더가 존재하여 초기화합니다 - 경로: {}", baseFolder.toAbsolutePath());
                    deleteDirectoryRecursively(baseFolder);
                }

                log.info("MIG :: 폴더 생성 시도 - 경로: {}", baseFolder.toAbsolutePath());
                createDirectoriesWithPermission(baseFolder);
                log.info("MIG :: 폴더 생성 완료 - 경로: {}", baseFolder.toAbsolutePath());

                // 폴더가 실제로 생성되었는지 확인
                if (!Files.exists(baseFolder)) {
                    log.error("MIG :: 폴더 생성 후에도 존재하지 않음 - 경로: {}", baseFolder.toAbsolutePath());
                    errors.add("폴더 생성 실패: 폴더가 생성되지 않았습니다");
                    return false;
                }
            } catch (NullPointerException e) {
                log.error("MIG :: 폴더 생성 실패 (NullPointerException) - 경로: {}, error: {}", baseFolder.toAbsolutePath(),
                        e.getMessage(), e);
                errors.add("폴더 생성 실패: " + e.getMessage());
                return false;
            } catch (IOException e) {
                log.error("MIG :: 폴더 생성 실패 - 경로: {}, error: {}", baseFolder.toAbsolutePath(), e.getMessage(), e);
                errors.add("폴더 생성 실패: " + e.getMessage());
                return false;
            } catch (Exception e) {
                log.error("MIG :: 폴더 생성 실패 (예상치 못한 오류) - 경로: {}, error: {}", baseFolder.toAbsolutePath(),
                        e.getMessage(), e);
                errors.add("폴더 생성 실패: " + e.getMessage());
                return false;
            }

            // 4. 최상위 타입 자체도 파일로 생성 (depth 0)
            // 커스텀 AGENT_APP은 리니지 조회가 필요 없으므로 여기서 생성하지 않고, else 블록에서 생성
            if (!(type.equalsIgnoreCase("AGENT_APP") && isCustomAgentApp)) {
                try {
                    log.info("MIG :: 최상위 타입 파일 생성 시작 - type: {}, uuid: {}", type, uuid);
                    String topLevelFilePath = createExportFileInFolder(projectId, baseFolder, topLevelType, uuid, 0);
                    if (topLevelFilePath != null) {
                        log.info("MIG :: 최상위 타입 파일 생성 완료 - type: {}, uuid: {}, filePath: {}", type, uuid,
                                topLevelFilePath);
                    } else {
                        log.warn("MIG :: 최상위 타입 파일 생성 실패 - type: {}, uuid: {}", type, uuid);
                    }
                } catch (RuntimeException e) {
                    log.warn("MIG :: 최상위 타입 파일 생성 중 오류 발생 - type: {}, uuid: {}, error: {}", type, uuid, e.getMessage());
                    // 최상위 타입 파일 생성 실패는 에러로 처리하지 않음 (선택적)
                }
            } else {
                log.info("MIG :: 커스텀 AGENT_APP은 최상위 타입 파일 생성을 건너뛰고 else 블록에서 생성합니다 - type: {}, uuid: {}", type, uuid);
            }

            // 5. 리니지 조회가 필요한 경우: 각 relation에 대해 파일 생성 (depth 기준)
            if (needsLineage) {
                log.info("MIG :: === 데이터 조회 및 파일 생성 시작 (리니지 기반) ===");
                for (LineageRelationWithTypes relation : filteredRelations) {
                    ObjectType targetType = relation.getTargetType();
                    String targetKey = relation.getTargetKey();
                    Integer relationDepth = relation.getDepth() != null ? relation.getDepth() : 0;
                    totalProcessed++;

                    log.info("MIG :: [{}/{}] 처리 중 - depth: {}, targetType: {}, targetKey: {}",
                            totalProcessed, filteredRelations.size(), relationDepth, targetType, targetKey);

                    // public -> -999
                    projectId = changePublicToM999(projectId);
                    try {
                        // target_type으로 파일 생성
                        String filePath = createExportFileInFolder(projectId, baseFolder, targetType, targetKey,
                                relationDepth);
                        if (filePath == null) {
                            log.error("MIG :: Export 파일 생성 실패 - targetType: {}, targetKey: {}, depth: {}",
                                    targetType, targetKey, relationDepth);
                            errors.add(String.format("Export 파일 생성 실패: %s/%s (depth: %d)", targetType, targetKey,
                                    relationDepth));
                            continue;
                        }

                        log.info("MIG :: Export 파일 생성 완료 - targetType: {}, targetKey: {}, depth: {}, filePath: {}",
                                targetType, targetKey, relationDepth, filePath);

                        // uuid로도 파일 생성 (targetKey와 다를 경우)
                        // 단, targetType이 최상위 타입(topLevelType)과 일치할 때만 실행
                        // (예: SERVING_MODEL에서 MODEL을 조회할 때, SERVING_MODEL ID로도 MODEL 조회 시도)
                        // targetType이 최상위 타입과 다르면 uuid는 다른 타입의 ID이므로 조회하면 안 됨
                        ObjectType topLevelTypeEnum = ObjectType.valueOf(type);
                        if (!uuid.equals(targetKey) && targetType == topLevelTypeEnum) {
                            try {
                                String uuidFilePath = createExportFileInFolder(projectId, baseFolder, targetType,
                                        uuid, relationDepth);
                                if (uuidFilePath != null) {
                                    log.info("MIG :: UUID 기반 Export 파일 생성 완료 - targetType: {}, uuid: {}, depth: {}, " +
                                                    "filePath: {}",
                                            targetType, uuid, relationDepth, uuidFilePath);
                                }
                            } catch (Exception e) {
                                log.warn("MIG :: UUID 기반 파일 생성 중 오류 (무시) - targetType: {}, uuid: {}, error: {}",
                                        targetType, uuid, e.getMessage());
                            }
                        } else if (!uuid.equals(targetKey) && targetType != topLevelTypeEnum) {
                            log.debug("MIG :: UUID 기반 파일 생성 건너뜀 - targetType: {}와 최상위 타입: {}가 다르므로 " +
                                    "uuid({})는 다른 타입의 ID입니다.", targetType, topLevelTypeEnum, uuid);
                        }

                        // 파일에서 Import 거래 수행
                        // depth가 0인 경우 Import 제외
                        if (relationDepth != null && relationDepth != 0 && targetType != ObjectType.AGENT_APP) {
                            log.info("MIG :: Import 거래 시작 - targetType: {}, targetKey: {}, depth: {}, filePath: {}",
                                    targetType, targetKey, relationDepth, filePath);

                            ImportResult importResult = importFromFileWithResult(targetType, targetKey, filePath);

                            if (importResult.success) {
                                log.info("MIG :: Import 거래 성공 - targetType: {}, targetKey: {}, depth: {}",
                                        targetType, targetKey, relationDepth);
                                successes.add(String.format("%s/%s (파일 생성 및 Import 성공, depth: %d)", targetType,
                                        targetKey, relationDepth));
                            } else if (importResult.isPermissionError) {
                                // 권한 오류는 검증 실패가 아니라 Pass 처리
                                log.warn("MIG :: Import 거래 권한 오류 (Pass 처리) - targetType: {}, targetKey: {}, depth: " +
                                                "{}, " +
                                                "message: {}",
                                        targetType, targetKey, relationDepth, importResult.message);
                                successes.add(String.format("%s/%s (Pass - 권한 없음, depth: %d)", targetType, targetKey,
                                        relationDepth));
                            } else {
                                log.error("MIG :: Import 거래 실패 - targetType: {}, targetKey: {}, depth: {}, message: {}",
                                        targetType, targetKey, relationDepth, importResult.message);
                                errors.add(String.format("Import 거래 실패: %s/%s (depth: %d) - %s",
                                        targetType, targetKey, relationDepth, importResult.message));
                            }
                        } else {
                            log.info("MIG :: Import 거래 생략 - depth가 0인 경우 제외 - targetType: {}, targetKey: {}, depth: {}",
                                    targetType, targetKey, relationDepth);
                        }

                    } catch (NullPointerException e) {
                        log.error("MIG :: 처리 중 오류 발생 (NullPointerException) - targetType: {}, targetKey: {}, depth: " +
                                        "{}, " +
                                        "error: {}",
                                targetType, targetKey, relationDepth, e.getMessage(), e);
                        log.error("MIG :: 스택 트레이스:", e);
                        errors.add(String.format("처리 오류: %s/%s (depth: %d) - %s",
                                targetType, targetKey, relationDepth, e.getMessage()));
                    } catch (Exception e) {
                        log.error("MIG :: 처리 중 오류 발생 - targetType: {}, targetKey: {}, depth: {}, error: {}",
                                targetType, targetKey, relationDepth, e.getMessage(), e);
                        log.error("MIG :: 스택 트레이스:", e);
                        errors.add(String.format("처리 오류: %s/%s (depth: %d) - %s",
                                targetType, targetKey, relationDepth, e.getMessage()));
                    }
                }
            } else {
                // 리니지 조회가 필요 없는 경우: uuid로 바로 파일 생성
                // SERVING_MODEL, 일반 AGENT_APP 제외한 경우 (커스텀 AGENT_APP 포함)
                log.info("MIG :: === 데이터 조회 및 파일 생성 시작 (UUID 기반) ===");
                if (isCustomAgentApp) {
                    log.info("MIG :: 커스텀 AGENT_APP 처리 - 리니지 조회 생략, UUID 기반 파일 생성");
                }
                totalProcessed++;

                try {
                    log.info("MIG :: [1/1] 처리 중 - type: {}, uuid: {}", type, uuid);

                    String filePath = createExportFileInFolder(projectId, baseFolder, topLevelType, uuid, 0);
                    if (filePath == null) {
                        log.error("MIG :: Export 파일 생성 실패 - type: {}, uuid: {}", type, uuid);
                        errors.add(String.format("Export 파일 생성 실패: %s/%s", type, uuid));
                    } else {
                        log.info("MIG :: Export 파일 생성 완료 - type: {}, uuid: {}, filePath: {}", type, uuid, filePath);
                        successes.add(String.format("%s/%s (파일 생성 완료)", type, uuid));
                    }
                } catch (NullPointerException e) {
                    log.error("MIG :: 처리 중 오류 발생 (NullPointerException) - type: {}, uuid: {}, error: {}",
                            type, uuid, e.getMessage(), e);
                    errors.add(String.format("처리 오류: %s/%s - %s", type, uuid, e.getMessage()));
                } catch (Exception e) {
                    log.error("MIG :: 처리 중 오류 발생 - type: {}, uuid: {}, error: {}",
                            type, uuid, e.getMessage(), e);
                    errors.add(String.format("처리 오류: %s/%s - %s", type, uuid, e.getMessage()));
                }
            }

            if (!successes.isEmpty()) {
                log.info("MIG :: 성공 목록:");
                successes.forEach(s -> log.info("MIG ::   ✓ {}", s));
            }

            if (!errors.isEmpty()) {
                log.error("MIG :: 실패 목록:");
                errors.forEach(e -> log.error("MIG ::   ✗ {}", e));
            }

            // 6. PROJECT 파일 생성 (baseFolder에 {num}_PROJECT_{projectId}.json 저장)
            // public -> -999
            projectId = changePublicToM999(projectId);
            try {
                log.info("MIG :: === PROJECT 파일 생성 시작 ===");
                Long prjSeq = Long.parseLong(projectId);

                // exportToImportFormat 사용 (리스트 관리 로직)
                projectMigService.exportProject(prjSeq);

                log.info("MIG :: PROJECT 파일 생성 완료");
                successes.add(String.format("PROJECT/%s (파일 생성 완료, import 제외)", projectId));
            } catch (RuntimeException e) {
                log.error("MIG :: PROJECT 파일 생성 중 오류 발생 - error: {}", e.getMessage(), e);
                errors.add("PROJECT 파일 생성 실패: " + e.getMessage());
            }

            boolean result = errors.isEmpty();

            log.info("MIG :: 에셋 검증 최종 결과 - uuid: {}, projectId: {}, type: {}, result: {}, errors: {}, successes: {}",
                    uuid, projectId, type, result, errors.size(), successes.size());

            return result;

        } catch (NullPointerException e) {
            log.error("MIG :: 에셋 검증 중 예외 발생 (NullPointerException) - uuid: {}, projectId: {}, type: {}, error: {}",
                    uuid, projectId, type, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("MIG :: 에셋 검증 중 예외 발생 - uuid: {}, projectId: {}, type: {}, error: {}",
                    uuid, projectId, type, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 타입별로 targetType을 처리해야 하는지 확인
     *
     * @param targetType 관계의 target 타입
     */
    private boolean shouldProcessTargetType(String type, ObjectType sourceType, ObjectType targetType) {
        if (type == null || targetType == null) {
            return false;
        }

        String upperType = type.toUpperCase();

        switch (upperType) {
            case "SERVING_MODEL":
                // SERVING_MODEL: model, prompt, guardrail만 처리 (없을 수도 있음)
                return targetType == ObjectType.MODEL;

            case "GUARDRAILS":
                return targetType == ObjectType.PROMPT;

            case "AGENT_APP":
                // AGENT_APP: KNOWLEDGE, VectorDB, SERVING_MODEL 제외하고 valid 체크
                // (depth 기반 필터링에서 이미 처리되므로 여기서는 기본 로직 유지)
                return targetType != ObjectType.KNOWLEDGE
                        && targetType != ObjectType.VECTOR_DB
                        && targetType != ObjectType.SERVING_MODEL;

            case "SAFETY_FILTER":
                return true;

            case "KNOWLEDGE":
                return true;

            case "VECTOR_DB":
                return true;

            default:
                log.warn("MIG :: 알 수 없는 타입 - type: {}, sourceType: {}, targetType: {}", type, sourceType, targetType);
                return false;
        }
    }

    /**
     * Export 파일 생성: 지정된 폴더 하위에 num_type_uuid.json
     *
     * <p>
     * 파일명 형식: {num}_{type}_{uuid}.json
     * num은 폴더 내 기존 파일들의 최대 num + 1로 결정됩니다.
     * 파일이 있으면 덮어쓰고, 없으면 생성합니다.
     * </p>
     *
     * @param parentFolder 부모 폴더 경로
     * @param targetType   객체 타입
     * @param targetKey    객체 ID
     * @return 생성된 파일 경로, 실패 시 null
     */
    private String createExportFileInFolder(String projectId, Path parentFolder, ObjectType targetType,
            String targetKey, Integer depth) {
        try {
            log.info("MIG :: Export 파일 생성 시작 - targetType: {}, targetKey: {}, depth: {}, parentFolder: {}",
                    targetType, targetKey, depth, parentFolder);

            // 부모 폴더가 없으면 생성
            if (!Files.exists(parentFolder)) {
                log.info("MIG :: 부모 폴더가 없습니다. 생성합니다 - parentFolder: {}", parentFolder);
                createDirectoriesWithPermission(parentFolder);
                log.info("MIG :: 부모 폴더 생성 완료 - parentFolder: {}", parentFolder.toAbsolutePath());
            }

            // depth가 null이면 0으로 설정
            int fileDepth = depth != null ? depth : 0;

            // SafetyFilter의 경우 여러 ID가 |로 구분되어 있을 수 있으므로, 파일명에 사용할 키 생성
            // |를 _로 변환하여 파일명에 사용 (파일 시스템 호환성)
            String fileNameKey = targetKey;
            if (targetType == ObjectType.SAFETY_FILTER && targetKey != null && targetKey.contains("|")) {
                fileNameKey = targetKey.replace("|", "_");
                log.debug("MIG :: SafetyFilter 여러 ID 처리 - 원본: {}, 파일명용: {}", targetKey, fileNameKey);
            }

            // 파일명 생성: depth_type_uuid.json (depth가 높을수록 먼저 import되므로 depth 1이 가장 마지막)
            String fileName = String.format("%d_%s_%s.json", fileDepth, targetType.name(), fileNameKey);
            Path filePath = parentFolder.resolve(fileName);

            log.debug("MIG :: 파일 경로: {}", filePath.toAbsolutePath());

            // Export → Import 형식으로 변환하여 JSON 문자열 가져오기
            String importJson = getExportJson(projectId, targetType, targetKey);
            if (importJson == null || importJson.isEmpty()) {
                log.error("MIG :: Export JSON 생성 실패 또는 비어있음 - targetType: {}, targetKey: {}, depth: {}. " +
                                "원인: getExportJson이 null을 반환했습니다. 위의 로그에서 자세한 예외 정보를 확인하세요.",
                        targetType, targetKey, fileDepth);
                return null;
            }

            log.debug("MIG :: Export JSON 생성 완료 - 길이: {} bytes", importJson.length());

            // 프로젝트 ID 조회 (DB에서)
            String assetProjectSeq = getProjectIdFromDb(targetKey, projectId);

            // JSON에 prj_seq 필드 직접 추가
            try {
                JsonNode jsonNode = objectMapper.readTree(importJson);
                if (jsonNode instanceof ObjectNode) {
                    ObjectNode objectNode = (ObjectNode) jsonNode;

                    // project_id 필드 추가 (기존에 있으면 덮어쓰기)
                    objectNode.put("prj_seq", assetProjectSeq);

                    // 업데이트된 JSON으로 변환
                    importJson = objectMapper.writeValueAsString(objectNode);
                    log.debug("MIG :: Export JSON에 prj_seq 추가 - targetType: {}, targetKey: {}, projectId: {}",
                            targetType, targetKey, assetProjectSeq);
                }
            } catch (Exception e) {
                log.warn("MIG :: JSON에 prj_seq 추가 실패 (계속 진행) - targetType: {}, targetKey: {}, error: {}",
                        targetType, targetKey, e.getMessage());
                // JSON 파싱 실패해도 기존 JSON으로 계속 진행
            }

            // 파일이 있으면 덮어쓰고, 없으면 생성 (UTF-8 인코딩 명시)
            try (java.io.FileWriter writer = new java.io.FileWriter(
                    filePath.toFile(),
                    java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(importJson);
                writer.flush();
                log.debug("MIG :: 파일 쓰기 완료 - filePath: {}", filePath.toAbsolutePath());
            }
            setFilePermission(filePath);

            // 파일이 실제로 생성되었는지 확인
            if (!Files.exists(filePath)) {
                log.error("MIG :: 파일이 생성되지 않았습니다 - filePath: {}", filePath.toAbsolutePath());
                return null;
            }

            long fileSize = Files.size(filePath);
            log.debug("MIG :: 파일 크기: {} bytes", fileSize);

            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("MIG :: Export 파일 생성 완료 - 경로: {}, depth: {}, 크기: {} bytes", absolutePath, fileDepth, fileSize);

            return absolutePath;

        } catch (IOException e) {
            log.error("MIG :: Export 파일 생성 실패 (IOException) - targetType: {}, targetKey: {}, depth: {}, error: {}",
                    targetType, targetKey, depth, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("MIG :: Export 파일 생성 실패 - targetType: {}, targetKey: {}, depth: {}, error: {}",
                    targetType, targetKey, depth, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Export JSON 문자열 가져오기
     *
     * @param targetType 객체 타입
     * @param targetKey  객체 ID
     * @return Export 형식의 JSON 문자열
     */
    private String getExportJson(String projectId, ObjectType targetType, String targetKey) {
        // public -> -999
        projectId = changePublicToM999(projectId);
        try {
            switch (targetType) {
                case TOOL:
                    return toolMigService.exportToImportFormat(targetKey);
                case MCP:
                    return mcpMigService.exportToImportFormat(targetKey);
                case AGENT_GRAPH:
                    return graphMigService.exportToImportFormat(targetKey);
                case MODEL:
                    return modelMigService.exportToImportFormat(targetKey, projectId);
                case PROMPT:
                    return inferencePromptMigService.exportToImportFormat(targetKey);
                case FEW_SHOT:
                    return fewShotMigService.exportToImportFormat(targetKey);
                case GUARDRAILS:
                    return guardrailMigService.exportToImportFormat(targetKey);
                case KNOWLEDGE:
                    return externalRepoMigService.exportToImportFormat(targetKey);
                case VECTOR_DB:
                    return vectorDbMigService.exportToImportFormat(targetKey);
                case SERVING_MODEL:
                    return servingModelMigService.exportToImportFormat(targetKey);
                case AGENT_APP:
                    return agentAppMigService.exportToImportFormat(targetKey, projectId);
                case SAFETY_FILTER:
                    return safetyFilterMigService.exportToImportFormat(targetKey);
                case PROJECT:
                    // PROJECT는 exportToImportFormat이 void이므로 JSON 문자열 반환 불가
                    // getExportJson에서는 null 반환 (파일 저장은 createExportFileInFolder에서 처리)
                    return null;
                default:
                    log.warn("MIG :: 지원하지 않는 타입 - targetType: {}, targetKey: {}", targetType, targetKey);
                    return null;
            }
        } catch (BusinessException e) {
            // 권한 오류인 경우 특별 처리
            if (e.getErrorCode() == ErrorCode.EXTERNAL_API_FORBIDDEN
                    || e.getErrorCode() == ErrorCode.FORBIDDEN
                    || e.getErrorCode() == ErrorCode.INSUFFICIENT_PRIVILEGES
                    || (e.getMessage() != null && e.getMessage().contains("권한"))) {
                log.warn("MIG :: Export JSON 가져오기 실패 (권한 오류) - targetType: {}, targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                log.warn("MIG :: 권한이 없는 리소스입니다. 다른 프로젝트에 속해있거나 접근 권한이 없을 수 있습니다.");
            } else {
                log.error("MIG :: Export JSON 가져오기 실패 (비즈니스 오류) - targetType: {}, targetKey: {}, errorCode: {}, " +
                                "message: {}",
                        targetType, targetKey, e.getErrorCode(), e.getMessage(), e);
            }
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("MIG :: Export JSON 가져오기 실패 (잘못된 입력값) - targetType: {}, targetKey: {}",
                    targetType, targetKey, e);
            return null;
        } catch (RuntimeException e) {
            // RuntimeException으로 래핑된 BusinessException 처리
            if (e.getMessage() != null && e.getMessage().contains("권한")) {
                log.warn("MIG :: Export JSON 가져오기 실패 (권한 오류 - RuntimeException) - targetType: {}, targetKey: {}, " +
                                "message: {}",
                        targetType, targetKey, e.getMessage());
                log.warn("MIG :: 권한이 없는 리소스입니다. 다른 프로젝트에 속해있거나 접근 권한이 없을 수 있습니다.");
            } else {
                log.error(
                        "Export JSON 가져오기 실패 (RuntimeException) - targetType: {}, targetKey: {}, message: {}, cause: " +
                                "{}",
                        targetType, targetKey, e.getMessage(),
                        e.getCause() != null
                                ? e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage()
                                : "없음",
                        e);
            }
            return null;
        } catch (Exception e) {
            log.error("MIG :: Export JSON 가져오기 실패 - targetType: {}, targetKey: {}, error: {}",
                    targetType, targetKey, e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON에서 prj_seq 필드 추출
     *
     * @param importJson        Import JSON 문자열
     * @param fallbackProjectId 추출 실패 시 사용할 프로젝트 ID
     * @return 프로젝트 ID (문자열)
     */
    private String extractProjectIdFromJson(String importJson, String fallbackProjectId) {
        try {
            JsonNode jsonNode = objectMapper.readTree(importJson);

            // 최상위 레벨에서 prj_seq 필드 추출
            if (jsonNode.has("prj_seq") && !jsonNode.get("prj_seq").isNull()) {
                String projectId = jsonNode.get("prj_seq").asText();
                log.debug("MIG :: JSON에서 prj_seq 추출 성공 - projectId: {}", projectId);
                return projectId;
            }

            log.warn("MIG :: JSON에서 prj_seq 찾을 수 없음 (기존 프로젝트 사용) - fallbackProjectId: {}",
                    fallbackProjectId);
            return fallbackProjectId;

        } catch (Exception e) {
            log.warn("MIG :: JSON에서 prj_seq 추출 실패 (기존 프로젝트 사용) - error: {}, fallbackProjectId: {}",
                    e.getMessage(), fallbackProjectId);
            return fallbackProjectId;
        }
    }

    /**
     * 파일에서 Import 수행 (결과 포함)
     *
     * @param targetType 객체 타입
     * @param targetKey  객체 ID
     * @param filePath   파일 경로
     * @return Import 결과 (성공 여부, 권한 오류 여부 포함)
     */
    private ImportResult importFromFileWithResult(ObjectType targetType, String targetKey, String filePath) {
        try {
            log.info("MIG :: 파일에서 Import 시작 - targetType: {}, targetKey: {}, filePath: {}",
                    targetType, targetKey, filePath);

            // 파일 읽기 (UTF-8 인코딩 명시)
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                log.error("MIG :: 파일이 존재하지 않습니다 - filePath: {}", filePath);
                return new ImportResult(false, false, "파일이 존재하지 않습니다: " + filePath);
            }

            String importJson = new String(Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
            log.debug("MIG :: 파일 읽기 완료 - filePath: {}, 크기: {} bytes", filePath, importJson.length());

            // Graph, FEW_SHOT, PROMPT의 경우 prj_seq 필드 제거 (JSON 구조 변경 방지)
            if (targetType == ObjectType.AGENT_GRAPH || targetType == ObjectType.FEW_SHOT || targetType == ObjectType.PROMPT || targetType == ObjectType.MCP) {
                try {
                    JsonNode jsonNode = objectMapper.readTree(importJson);
                    if (jsonNode.isObject() && jsonNode.has("prj_seq")) {
                        ObjectNode objectNode = (ObjectNode) jsonNode;
                        objectNode.remove("prj_seq");
                        importJson = objectMapper.writeValueAsString(objectNode);
                        log.debug("MIG :: {} 파일에서 prj_seq 제거 완료 - filePath: {}", targetType, filePath);
                    }
                } catch (Exception e) {
                    log.warn("MIG :: {} JSON에서 prj_seq 제거 실패 (계속 진행) - filePath: {}, error: {}",
                            targetType, filePath, e.getMessage());
                    // JSON 파싱 실패해도 기존 JSON으로 계속 진행
                }
            }

            // 각 타입별로 Import 수행 (assetValidation용 - 삭제 없이 import만)
            return importByTypeFromJsonForValidation(targetType, targetKey, importJson, null);

        } catch (IOException e) {
            log.error("MIG :: 파일 읽기 실패 - filePath: {}, error: {}", filePath, e.getMessage(), e);
            return new ImportResult(false, false, "파일 읽기 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("MIG :: 파일에서 Import 실패 - targetType: {}, targetKey: {}, filePath: {}, error: {}",
                    targetType, targetKey, filePath, e.getMessage(), e);
            return new ImportResult(false, false, "Import 실패: " + e.getMessage());
        }
    }

    /**
     * Import 결과를 담는 내부 클래스
     */
    private static class ImportResult {

        boolean success;
        boolean isPermissionError;
        String message;

        ImportResult(boolean success, boolean isPermissionError, String message) {
            this.success = success;
            this.isPermissionError = isPermissionError;
            this.message = message;
        }

    }

    /**
     * JSON 문자열로부터 각 타입별 Import 수행 (assetValidation용)
     *
     * <p>
     * assetValidation에서 사용하는 메서드로, 삭제 없이 import만 수행합니다.
     * 검증 목적이므로 기존 리소스를 삭제하지 않습니다.
     * </p>
     *
     * @param targetType 객체 타입
     * @param targetKey  객체 ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId  프로젝트 ID (null일 경우 valid 체크용, 값이 있을 경우 권한 설정용)
     * @return Import 결과
     */
    private ImportResult importByTypeFromJsonForValidation(ObjectType targetType, String targetKey, String importJson,
            String projectId) {
        try {
            log.debug("MIG :: JSON에서 Import 시작 - targetType: {}, targetKey: {}", targetType, targetKey);

            // 각 타입별로 기존 importFromExport 메서드를 활용
            // 파일에서 읽은 JSON을 사용하여 import 수행
            boolean result = false;
            switch (targetType) {
                case TOOL:
                    // Tool은 JSON 문자열을 직접 받음
                    result = toolMigService.importFromJsonString(targetKey, importJson, null);
                    break;
                case MCP:
                    // MCP는 JSON 문자열을 직접 받음
                    result = mcpMigService.importFromJsonString(targetKey, importJson, null);
                    break;
                case AGENT_GRAPH:
                    // Graph는 JSON 문자열을 직접 받음
                    result = graphMigService.importFromJsonString(targetKey, importJson, null);
                    break;
                case MODEL:
                    // Model은 객체를 받으므로 JSON을 파싱해야 함
                    result = modelMigService.importFromJsonString(importJson, null);
                    break;
                case PROMPT:
                    // Prompt는 JSON 문자열을 직접 받음
                    result = inferencePromptMigService.importFromJsonString(targetKey, importJson, null);
                    break;
                case FEW_SHOT:
                    // FewShot은 JSON 문자열을 직접 받음
                    result = fewShotMigService.importFromJsonString(targetKey, importJson, null);
                    break;
                case GUARDRAILS:
                    // GuardRail은 JSON 문자열을 직접 받음
                    result = guardrailMigService.importFromJsonString(targetKey, importJson, null);
                    break;
                case KNOWLEDGE:
                    // ExternalRepo는 객체를 받으므로 JSON을 파싱해야 함
                    result = externalRepoMigService.importFromJsonString(importJson, null, false);
                    break;
                case VECTOR_DB:
                    // VectorDb는 객체를 받으므로 JSON을 파싱해야 함
                    result = vectorDbMigService.importFromJsonString(importJson, null, false);
                    break;
                case SERVING_MODEL:
                    // SERVING_MODEL는 JSON 문자열을 직접 받음
                    result = servingModelMigService.importFromJsonString(importJson, null);
                    break;
                case AGENT_APP:
                    result = agentAppMigService.importFromJsonString(targetKey, importJson, null);
                    break;
                case SAFETY_FILTER:
                    result = safetyFilterMigService.importFromJsonString(importJson, null);
                    break;
                case PROJECT:
                    projectMigService.importProjects();
                    result = true;
                    break;
                default:
                    log.warn("MIG :: 지원하지 않는 타입 - targetType: {}, targetKey: {}", targetType, targetKey);
                    return new ImportResult(false, false, "지원하지 않는 타입입니다.");
            }

            return new ImportResult(result, false, result ? "Import 성공" : "Import 실패");

        } catch (BusinessException e) {
            // 권한 오류인 경우 특별 처리
            boolean isPermissionError = e.getErrorCode() == ErrorCode.EXTERNAL_API_FORBIDDEN
                    || e.getErrorCode() == ErrorCode.FORBIDDEN
                    || e.getErrorCode() == ErrorCode.INSUFFICIENT_PRIVILEGES
                    || (e.getMessage() != null && e.getMessage().contains("권한"));

            // "duplicate key"나 "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황
            String errorMessage = e.getMessage() != null ? e.getMessage() : "";
            boolean isExpectedError = errorMessage.contains("duplicate key")
                    || errorMessage.contains("Entity를 찾을 수 없습니다")
                    || errorMessage.contains("UniqueViolation")
                    || errorMessage.contains("already exists");

            if (isPermissionError) {
                log.warn("MIG :: JSON에서 Import 실패 (권한 오류) - targetType: {}, targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, true, "권한이 없습니다: " + e.getMessage());
            } else if (isExpectedError) {
                log.warn("MIG :: JSON에서 Import 실패 (BusinessException - 중복 또는 Entity 없음, Import 시 정상적일 수 있음) - " +
                                "targetType: {}, targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, false, "Import 실패 (중복 또는 Entity 없음): " + e.getMessage());
            } else {
                log.error("MIG :: JSON에서 Import 실패 (BusinessException) - targetType: {}, targetKey: {}, error: {}",
                        targetType, targetKey, e.getMessage(), e);
                return new ImportResult(false, false, "Import 실패: " + e.getMessage());
            }
        } catch (Exception e) {
            // RuntimeException으로 래핑된 권한 오류 처리
            boolean isPermissionError = e.getMessage() != null && e.getMessage().contains("권한");

            // "duplicate key"나 "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황
            String errorMessage = e.getMessage() != null ? e.getMessage() : "";
            boolean isExpectedError = errorMessage.contains("duplicate key")
                    || errorMessage.contains("Entity를 찾을 수 없습니다")
                    || errorMessage.contains("UniqueViolation")
                    || errorMessage.contains("already exists");

            if (isPermissionError) {
                log.warn("MIG :: JSON에서 Import 실패 (권한 오류 - Exception) - targetType: {}, targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, true, "권한이 없습니다: " + e.getMessage());
            } else if (isExpectedError) {
                log.warn("MIG :: JSON에서 Import 실패 (Exception - 중복 또는 Entity 없음, Import 시 정상적일 수 있음) - targetType: {}," +
                                " targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, false, "Import 실패 (중복 또는 Entity 없음): " + e.getMessage());
            } else {
                log.error("MIG :: JSON에서 Import 실패 (예상치 못한 오류) - targetType: {}, targetKey: {}, error: {}",
                        targetType, targetKey, e.getMessage(), e);
                return new ImportResult(false, false, "Import 실패: " + e.getMessage());
            }
        }
    }

    /**
     * JSON 문자열로부터 각 타입별 Import 수행 (실제 Migration용)
     *
     * <p>
     * importFromJsonFile, importFromFolder에서 사용하는 메서드로,
     * 삭제 로직이 포함되어 있습니다. 실제 이행 시 사용됩니다.
     * </p>
     *
     * @param targetType 객체 타입
     * @param targetKey  객체 ID
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId  프로젝트 ID (null일 경우 valid 체크용, 값이 있을 경우 권한 설정용)
     * @return Import 결과
     */
    private ImportResult importByTypeFromJsonForMigration(ObjectType targetType, String targetKey, String importJson,
            String projectId) {
        try {
            log.debug("MIG :: JSON에서 Import 시작 - targetType: {}, targetKey: {}", targetType, targetKey);

            // JSON에서 project_id 추출 (우선순위: JSON > 파라미터)
            String finalProjectIdString = extractProjectIdFromJson(importJson, projectId);

            log.info("MIG :: Import 시 프로젝트 ID 사용 - targetType: {}, targetKey: {}, projectId: {} (JSON에서 추출: {})",
                    targetType, targetKey, finalProjectIdString,
                    (projectId != null && finalProjectIdString != null && !finalProjectIdString.equals(projectId)) ?
                            "예" : "아니오");

            // prj_seq가 비어있을 수도 있으므로 안전하게 파싱
            Long finalProjectId = null;
            if (finalProjectIdString != null && !finalProjectIdString.isBlank()) {
                try {
                    finalProjectId = Long.parseLong(finalProjectIdString);
                } catch (NumberFormatException nfe) {
                    log.warn("MIG :: 프로젝트 ID 파싱 실패 (무시하고 null 사용) - value: {}", finalProjectIdString);
                }
            }

            // 각 타입별로 기존 importFromExport 메서드를 활용
            // 파일에서 읽은 JSON을 사용하여 import 수행

            // 공통 존재 여부 체크 (PROJECT, AGENT_APP 제외)
            boolean isExist = false;
            if (targetType != ObjectType.PROJECT && targetType != ObjectType.AGENT_APP) {
                try {
                    isExist = checkIfExists(targetType, targetKey);
                    log.info("MIG :: 존재 여부 확인 완료 - type: {}, id: {}, isExist: {}", targetType, targetKey, isExist);
                } catch (Exception e) {
                    log.debug("MIG :: 존재 여부 확인 중 예외 발생 (없는 것으로 간주) - type: {}, id: {}, error: {}",
                            targetType, targetKey, e.getMessage());
                    isExist = false;
                }
            }

            boolean result = false;
            switch (targetType) {
                case TOOL:
                    // Tool은 JSON 문자열을 직접 받음 - 존재하면 update, 없으면 import
                    result = toolMigService.importFromJsonString(targetKey, importJson, finalProjectId, isExist);
                    break;
                case MCP:
                    // MCP는 JSON 문자열을 직접 받음 - 존재하면 update, 없으면 import
                    result = mcpMigService.importFromJsonString(targetKey, importJson, finalProjectId, isExist);
                    break;
                case AGENT_GRAPH:
                    // Graph는 JSON 문자열을 직접 받음 - 존재하면 update, 없으면 import
                    result = graphMigService.importFromJsonString(targetKey, importJson, finalProjectId, isExist);
                    break;
                case MODEL:
                    // Model은 객체를 받으므로 JSON을 파싱해야 함 - 존재하면 update, 없으면 import
                    result = modelMigService.importFromJsonString(importJson, finalProjectId, isExist);
                    break;
                case PROMPT:
                    // Prompt는 JSON 문자열을 직접 받음 - 존재하면 update, 없으면 import
                    result = inferencePromptMigService.importFromJsonString(targetKey, importJson, finalProjectId, isExist);
                    break;
                case FEW_SHOT:
                    // FewShot은 JSON 문자열을 직접 받음 - 존재하면 update, 없으면 import
                    result = fewShotMigService.importFromJsonString(targetKey, importJson, finalProjectId, isExist);
                    break;
                case GUARDRAILS:
                    // GuardRail은 JSON 문자열을 직접 받음 - 존재하면 update, 없으면 import
                    result = guardrailMigService.importFromJsonString(targetKey, importJson, finalProjectId, isExist);
                    break;
                case KNOWLEDGE:
                    // ExternalRepo는 객체를 받으므로 JSON을 파싱해야 함 - 존재하면 update, 없으면 import
                    result = externalRepoMigService.importFromJsonString(importJson, finalProjectId, isExist);
                    break;
                case VECTOR_DB:
                    // VectorDb는 객체를 받으므로 JSON을 파싱해야 함 - 존재하면 update, 없으면 import
                    result = vectorDbMigService.importFromJsonString(importJson, finalProjectId, isExist);
                    break;
                case SERVING_MODEL:
                    // SERVING_MODEL는 JSON 문자열을 직접 받음 - 존재하면 update, 없으면 import
                    result = servingModelMigService.importFromJsonString(importJson, finalProjectId, isExist);
                    break;
                case AGENT_APP:
                    // AGENT_APP은 내부에서 처리
                    result = agentAppMigService.importFromJsonString(targetKey, importJson, finalProjectId);
                    break;
                case SAFETY_FILTER:
                    // SAFETY_FILTER는 batch import로 존재하면 덮어쓰므로 동일하게 처리
                    result = safetyFilterMigService.importFromJsonString(importJson, finalProjectId, isExist);
                    break;
                case PROJECT:
                    // PROJECT는 별도 처리
                    projectMigService.importProjects();
                    result = true;
                    break;
                default:
                    log.warn("MIG :: 지원하지 않는 타입 - targetType: {}, targetKey: {}", targetType, targetKey);
                    return new ImportResult(false, false, "지원하지 않는 타입입니다.");
            }

            return new ImportResult(result, false, result ? "Import 성공" : "Import 실패");

        } catch (BusinessException e) {
            // 권한 오류인 경우 특별 처리
            boolean isPermissionError = e.getErrorCode() == ErrorCode.EXTERNAL_API_FORBIDDEN
                    || e.getErrorCode() == ErrorCode.FORBIDDEN
                    || e.getErrorCode() == ErrorCode.INSUFFICIENT_PRIVILEGES
                    || (e.getMessage() != null && e.getMessage().contains("권한"));

            // "duplicate key"나 "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황
            String errorMessage = e.getMessage() != null ? e.getMessage() : "";
            boolean isExpectedError = errorMessage.contains("duplicate key")
                    || errorMessage.contains("Entity를 찾을 수 없습니다")
                    || errorMessage.contains("UniqueViolation")
                    || errorMessage.contains("already exists");

            if (isPermissionError) {
                log.warn("MIG :: JSON에서 Import 실패 (권한 오류) - targetType: {}, targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, true, "권한이 없습니다: " + e.getMessage());
            } else if (isExpectedError) {
                log.warn("MIG :: JSON에서 Import 실패 (BusinessException - 중복 또는 Entity 없음, Import 시 정상적일 수 있음) - " +
                                "targetType: {}, targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, false, "Import 실패 (중복 또는 Entity 없음): " + e.getMessage());
            } else {
                log.error("MIG :: JSON에서 Import 실패 (BusinessException) - targetType: {}, targetKey: {}, error: {}",
                        targetType, targetKey, e.getMessage(), e);
                return new ImportResult(false, false, "Import 실패: " + e.getMessage());
            }
        } catch (Exception e) {
            // RuntimeException으로 래핑된 권한 오류 처리
            boolean isPermissionError = e.getMessage() != null && e.getMessage().contains("권한");

            // "duplicate key"나 "Entity를 찾을 수 없습니다" 같은 에러는 Import 시 발생할 수 있는 정상적인 상황
            String errorMessage = e.getMessage() != null ? e.getMessage() : "";
            boolean isExpectedError = errorMessage.contains("duplicate key")
                    || errorMessage.contains("Entity를 찾을 수 없습니다")
                    || errorMessage.contains("UniqueViolation")
                    || errorMessage.contains("already exists");

            if (isPermissionError) {
                log.warn("MIG :: JSON에서 Import 실패 (권한 오류 - Exception) - targetType: {}, targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, true, "권한이 없습니다: " + e.getMessage());
            } else if (isExpectedError) {
                log.warn("MIG :: JSON에서 Import 실패 (Exception - 중복 또는 Entity 없음, Import 시 정상적일 수 있음) - targetType: {}," +
                                " targetKey: {}, message: {}",
                        targetType, targetKey, e.getMessage());
                return new ImportResult(false, false, "Import 실패 (중복 또는 Entity 없음): " + e.getMessage());
            } else {
                log.error("MIG :: JSON에서 Import 실패 (예상치 못한 오류) - targetType: {}, targetKey: {}, error: {}",
                        targetType, targetKey, e.getMessage(), e);
                return new ImportResult(false, false, "Import 실패: " + e.getMessage());
            }
        }
    }

    /**
     * migration_temp 폴더를 migration 폴더로 복사 (JSON 파일로 통합)
     *
     * <p>
     * 화면에서 받은 데이터를 기반으로 각 파일의 dev 값을 prod로 업데이트하고,
     * migration_temp 폴더의 모든 JSON 파일을 하나의 JSON 파일로 합친 후 DB에 저장합니다.
     * </p>
     *
     * @param type          객체 타입
     * @param id            객체 ID
     * @param projectId     프로젝트 ID
     * @param migrationData extractMigrationDataFromFolder의 반환 형태와 동일한 데이터 (null 가능)
     * @param projectName   프로젝트 이름
     * @param assetName     에셋 이름
     * @return 생성된 JSON 파일 경로, 실패 시 null
     */
    @Override
    public String copyFolderFromTempToMigrationAsJson(ObjectType type, String id, String projectId,
            Map<String, List<Map<String, Object>>> migrationData,
            String projectName, String assetName) {
        try {
            if (type == null || id == null || id.trim().isEmpty()) {
                log.error("MIG :: 필수 파라미터가 null이거나 비어있습니다 - type: {}, id: {}", type, id);
                return null;
            }

            if (projectId == null || projectId.trim().isEmpty()) {
                log.warn("MIG :: projectId이 null이거나 비어있습니다. 기본값 'default'를 사용합니다.");
                projectId = "default";
            }

            // PROJECT 타입인 경우: 파일 통합 불필요 (project_migration_data.json에 이미 저장됨)
            if (type == ObjectType.PROJECT) {
                log.info("MIG :: PROJECT 타입 - 파일 통합 불필요. project_migration_data.json 경로 반환");
                String projectExportFilePath = Paths.get(migrationBaseDir, "project_migration_data.json").toString();
                log.info("MIG :: PROJECT 파일 경로: {}", projectExportFilePath);

                // PROJECT도 GpoMigMas에 저장
                try {
                    String projectJsonFileName = "project_migration_data.json";
                    saveMigrationToDatabase(type, id, projectId, projectExportFilePath,
                                projectJsonFileName, projectName, assetName);
                    log.info("MIG :: PROJECT DB 저장 완료 - type: {}, id: {}, projectId: {}", type, id, projectId);
                } catch (Exception e) {
                    log.error("MIG :: PROJECT DB 저장 실패 - type: {}, id: {}, projectId: {}, error: {}",
                            type, id, projectId, e.getMessage(), e);
                    // DB 저장 실패해도 파일 경로는 반환
                }

                return projectExportFilePath;
            }

            // -999 -> public
            projectId = changeM999ToPublic(projectId);

            // 소스 경로: migration_temp/{projectId}/{type}/{id}
            String sourcePath = buildMigrationTempPath(projectId, type, id);
            Path sourceDir = Paths.get(sourcePath);

            log.info("MIG :: 소스 경로: {}", sourceDir.toAbsolutePath());

            // 소스 폴더 존재 확인
            if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
                log.error("MIG :: 소스 폴더가 존재하지 않거나 디렉토리가 아닙니다 - sourcePath: {}", sourceDir.toAbsolutePath());
                return null;
            }

            // 1. migrationData가 있으면 각 파일의 dev 값을 prod로 업데이트
            if (migrationData != null && !migrationData.isEmpty()) {
                updateDevToProdInFiles(sourceDir, migrationData);
            }

            // 1.1 SERVING_MODEL 타입인 경우 폴더 내 MODEL 선처리 : 폴더 복사 -> 물리 파일 이동
            if (type == ObjectType.SERVING_MODEL) {
                log.info("MIG :: SERVING_MODEL 타입인 경우 폴더 내 MODEL 선처리 : 폴더 복사 -> 물리 파일 이동 ");
                log.info("MIG :: SERVING_MODEL 파일 찾을 경로 - sourceDir: {}", sourceDir);
                List<Path> modelFiles = Files.list(sourceDir)
                        .filter(path -> path.getFileName().toString().matches("\\d+_MODEL_[a-f0-9\\-]+\\.json"))
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList());
                log.info("MIG :: MODEL 파일: {}", modelFiles);
                // 파일에서 찾아 처리 : 비동기로 병렬 처리 (백그라운드 실행)
                for (Path modelFile : modelFiles) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            log.info("MIG :: MODEL 파일이 위치한 경로 :{}", modelFile.toAbsolutePath());
                            String modelJson = readJsonFileWithEncodingDetection(modelFile);
                            JsonNode jsonNode = objectMapper.readTree(modelJson);
                            String path = jsonNode.path("model").path("path").asText();
                            log.info("MIG :: MODEL 파일 -> 물리 폴더 명 추출 => path: {}", path);

                            if (path == null || path.trim().isEmpty()) {
                                log.warn("MIG :: MODEL 파일 -> 물리 폴더 명 없음 => serverless인지 확인 필요");
                                return;
                            }

                            Path modelSourceDir = Paths.get("/model/model_file/" + path);
                            Path modelTargetDir = Paths.get("/gapdat/migration/model_file/" + path);

                            // 파일 복사 (비동기 백그라운드 실행)
                            log.info("MIG :: MODEL 폴더 SOURCE 경로 :{}", modelSourceDir.toAbsolutePath());
                            log.info("MIG :: MODEL 폴더 TARGET 경로 :{}", modelTargetDir.toAbsolutePath());
                            copyDirectoryRecursively(modelSourceDir, modelTargetDir);

                            log.info("MIG :: MODEL 폴더 물리 파일 복사 완료 - path: {}", path);
                        } catch (Exception e) {
                            log.error("MIG :: MODEL 폴더 물리 파일 복사 실패 - modelFile: {}, error: {}",
                                    modelFile.toAbsolutePath(), e.getMessage(), e);
                        }
                    }, asyncTaskExecutor);
                }

                log.info("MIG :: MODEL 폴더 물리 파일 복사 작업을 비동기로 시작했습니다. 백그라운드에서 실행 중입니다.");
            }

            // 2. 폴더 내 모든 JSON 파일 읽기 및 정렬 (PROJECT 파일 포함, num 순서로 정렬됨)
            List<Path> jsonFiles = Files.list(sourceDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            log.info("MIG :: JSON 파일 개수: {}", jsonFiles.size());

            if (jsonFiles.isEmpty()) {
                log.warn("MIG :: 폴더에 JSON 파일이 없습니다 - sourcePath: {}", sourceDir.toAbsolutePath());
                return null;
            }

            // 파일 정렬 (PROJECT 우선, num 순서)
            sortJsonFiles(jsonFiles);

            // 3. JSON 파일로 통합
            List<Map<String, Object>> mergedFiles = new ArrayList<>();
            for (Path jsonFile : jsonFiles) {
                try {
                    String fileName = jsonFile.getFileName().toString();
                    log.debug("MIG :: 파일 처리 시작 - fileName: {}", fileName);

                    // 파일명 파싱
                    Object[] parseResult = parseFileName(fileName);
                    if (parseResult == null) {
                        log.warn("MIG :: 파일명 파싱 실패 - fileName: {}", fileName);
                        continue;
                    }

                    ObjectType fileType = (ObjectType) parseResult[0];
                    String fileId = (String) parseResult[1];

                    // 파일 내용 읽기
                    String fileContent = readJsonFileWithEncodingDetection(jsonFile);
                    JsonNode jsonNode = objectMapper.readTree(fileContent); // data
                    String assetProjectId = jsonNode.path("prj_seq").asText(); // project_id

                    // Graph, MCP의 경우 원본 JSON 문자열을 보존 (JSON 구조 변경 방지)
                    String originalJsonString = null;
                    if (fileType == ObjectType.AGENT_GRAPH || fileType == ObjectType.MCP) {
                        // prj_seq만 제거한 원본 JSON 문자열 보존
                        if (jsonNode.has("prj_seq") && jsonNode.isObject()) {
                            ObjectNode tempNode = jsonNode.deepCopy();
                            tempNode.remove("prj_seq");
                            originalJsonString = objectMapper.writeValueAsString(tempNode);
                        } else {
                            originalJsonString = fileContent;
                        }
                    }

                    if (jsonNode.has("prj_seq") && jsonNode.isObject()) {
                        ((ObjectNode) jsonNode).remove("prj_seq"); // project_id 제거
                    }

                    // SAFETY_FILTER의 경우 개별 파일 내부에 prj_seq가 없으므로,
                    // 통합 JSON에서는 현재 projectId를 prj_seq로 사용
                    if ((assetProjectId == null || assetProjectId.isEmpty())
                            && fileType == ObjectType.SAFETY_FILTER) {
                        assetProjectId = projectId;
                    }

                    // 통합 JSON에 추가
                    Map<String, Object> fileEntry = new java.util.HashMap<>();
                    fileEntry.put("fileName", fileName);
                    fileEntry.put("type", fileType.name());
                    fileEntry.put("id", fileId);
                    fileEntry.put("prj_seq", assetProjectId);
                    fileEntry.put("data", jsonNode);
                    // Graph의 경우 원본 JSON 문자열도 저장 (JSON 구조 변경 방지)
                    if (fileType == ObjectType.AGENT_GRAPH && originalJsonString != null) {
                        fileEntry.put("originalJson", originalJsonString);
                    }

                    mergedFiles.add(fileEntry);
                    log.debug("MIG :: 파일 통합 완료 - fileName: {}, type: {}, id: {}", fileName, fileType, fileId);

                } catch (Exception e) {
                    log.error("MIG :: 파일 통합 실패 - fileName: {}, error: {}", jsonFile.getFileName(), e.getMessage(), e);
                }
            }

            // PROJECT 파일은 이미 jsonFiles에 포함되어 있고, sortJsonFiles()로 정렬되어 있음
            // 별도 처리 불필요

            // 4. 통합 JSON 파일 생성
            String targetPath = buildMigrationPath(projectId, type, id);
            Path targetDir = Paths.get(targetPath);
            createDirectoriesWithPermission(targetDir.getParent());

            String jsonFileName = String.format("%s.json", id);
            Path jsonFilePath = targetDir.getParent().resolve(jsonFileName);

            // 메타데이터를 포함한 통합 JSON 객체 생성
            Map<String, Object> migrationFileData = new java.util.LinkedHashMap<>();
            migrationFileData.put("createdAt", java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            migrationFileData.put("projectId", projectId);
            migrationFileData.put("projectName", projectName);
            migrationFileData.put("assetType", type.name());
            migrationFileData.put("assetId", id);
            migrationFileData.put("assetName", assetName);
            migrationFileData.put("fileCount", mergedFiles.size());
            migrationFileData.put("files", mergedFiles);

            // 통합 JSON을 파일로 저장
            String mergedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(migrationFileData);
            try (java.io.FileWriter writer = new java.io.FileWriter(jsonFilePath.toFile(),
                    java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write(mergedJson);
                writer.flush();
            }
            setFilePermission(jsonFilePath);

            String absolutePath = jsonFilePath.toAbsolutePath().toString();
            log.info("MIG :: 통합 JSON 파일 생성 완료 - filePath: {}, 파일 개수: {}, 생성일시: {}",
                    absolutePath, mergedFiles.size(), migrationFileData.get("createdAt"));

            // 5. DB에 마이그레이션 정보 저장
            // JSON 파일명을 fileNms로 사용 (통합 JSON 파일이므로 파일명 하나만 저장)
            try {
                // JSON 파일 경로를 filePath로 저장
                if (migrationData != null && !migrationData.isEmpty()) {
                    saveMigrationToDatabaseWithData(type, id, projectId, absolutePath, jsonFileName, migrationData,
                            projectName, assetName);
                } else {
                    saveMigrationToDatabase(type, id, projectId, absolutePath, jsonFileName, projectName, assetName);
                }
                log.info("MIG :: DB 저장 완료 - type: {}, id: {}, projectId: {}", type, id, projectId);
            } catch (Exception e) {
                log.error("MIG :: DB 저장 실패 - type: {}, id: {}, projectId: {}, error: {}",
                        type, id, projectId, e.getMessage(), e);
                return null;
            }

            return absolutePath;

        } catch (IOException e) {
            log.error("MIG :: JSON 파일 생성 실패 (IOException) - type: {}, id: {}, projectId: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("MIG :: JSON 파일 생성 실패 - type: {}, id: {}, projectId: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON 파일 목록을 정렬 (PROJECT 우선, num 순서)
     *
     * @param jsonFiles 정렬할 JSON 파일 목록
     */
    private void sortJsonFiles(List<Path> jsonFiles) {
        jsonFiles.sort((p1, p2) -> {
            String name1 = p1.getFileName().toString();
            String name2 = p2.getFileName().toString();

            // num_type_uuid 형식인지 확인
            int num1 = extractFileNumber(name1);
            int num2 = extractFileNumber(name2);

            // num이 있으면 num 순서로 (내림차순: 큰 숫자 먼저), num이 같으면 타입별로 정렬
            if (num1 > 0 && num2 > 0) {
                int numCompare = Integer.compare(num2, num1); // 내림차순 (2 > 1 > 0)
                if (numCompare != 0) {
                    return numCompare;
                }
                // num이 같으면 타입별로 정렬
                String type1 = extractFileType(name1);
                String type2 = extractFileType(name2);
                if (type1 != null && type2 != null) {
                    return type1.compareTo(type2);
                } else if (type1 != null) {
                    return -1;
                } else if (type2 != null) {
                    return 1;
                }
                return name1.compareTo(name2);
            } else if (num1 > 0) {
                return -1; // num이 있는 파일을 먼저
            } else if (num2 > 0) {
                return 1;
            } else {
                return name1.compareTo(name2); // 파일명 순서
            }
        });
    }

    /**
     * 파일명에서 타입 추출 (num_TYPE_UUID.json 형식에서 TYPE 추출)
     *
     * @param fileName 파일명
     * @return 타입 문자열 (예: "MCP", "TOOL", "AGENT_GRAPH" 등), 없으면 null
     */
    private String extractFileType(String fileName) {
        try {
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));

            // num_type_uuid 형식인지 확인
            String typeAndId = fileNameWithoutExt;
            if (fileNameWithoutExt.matches("^\\d+_.+")) {
                // num_ 접두사 제거
                int firstUnderscore = fileNameWithoutExt.indexOf('_');
                if (firstUnderscore > 0) {
                    typeAndId = fileNameWithoutExt.substring(firstUnderscore + 1);
                }
            }

            // ObjectType enum의 모든 값들을 확인하여 가장 긴 매칭 타입을 찾습니다
            ObjectType[] allTypes = ObjectType.values();
            java.util.Arrays.sort(allTypes, (a, b) -> Integer.compare(b.name().length(), a.name().length()));

            for (ObjectType candidateType : allTypes) {
                String typeName = candidateType.name();
                if (typeAndId.startsWith(typeName + "_")) {
                    return typeName;
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 파일들의 dev 값을 prod로 업데이트
     *
     * @param sourceDir     소스 디렉토리
     * @param migrationData 마이그레이션 데이터
     */
    private void updateDevToProdInFiles(Path sourceDir, Map<String, List<Map<String, Object>>> migrationData) {
        try {
            log.info("MIG :: dev→prod 업데이트 시작 - sourceDir: {}", sourceDir);

            // 소스 디렉토리의 모든 JSON 파일 읽기
            List<Path> jsonFiles = Files.list(sourceDir)
                    .filter(path -> path.toString().endsWith(".json"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            for (Path jsonFile : jsonFiles) {
                String fileName = jsonFile.getFileName().toString();
                log.debug("MIG :: 파일 처리 시작 - fileName: {}", fileName);

                try {
                    // 파일명 파싱: TYPE_ID.json → TYPE, ID
                    Object[] parseResult = parseFileName(fileName);
                    if (parseResult == null) {
                        log.warn("MIG :: 파일명 파싱 실패 - fileName: {}", fileName);
                        continue;
                    }
                    ObjectType fileType = (ObjectType) parseResult[0];
                    String fileId = (String) parseResult[1];

                    if (fileId == null || fileId.isEmpty()) {
                        log.warn("MIG :: 파일명에서 id가 비어있음 - fileName: {}, fileId: {}", fileName, fileId);
                        continue;
                    }

                    // migrationData에서 해당 id의 데이터 찾기
                    String typeKey = fileType.name();
                    List<Map<String, Object>> typeDataList = migrationData.get(typeKey);

                    if (typeDataList == null || typeDataList.isEmpty()) {
                        log.debug("MIG :: migrationData에서 해당 타입의 데이터를 찾을 수 없음 - type: {}, fileName: {}", typeKey,
                                fileName);
                        continue;
                    }

                    Map<String, Object> fileData = null;
                    for (Map<String, Object> data : typeDataList) {
                        Object dataId = data.get("id");
                        if (dataId != null && fileId.equals(String.valueOf(dataId))) {
                            fileData = data;
                            break;
                        }
                    }

                    if (fileData == null) {
                        log.debug("MIG :: migrationData에서 해당 id의 데이터를 찾을 수 없음 - type: {}, id: {}, fileName: {}",
                                typeKey, fileId, fileName);
                        continue;
                    }

                    // 파일 읽기 (인코딩 자동 감지)
                    String fileContent = readJsonFileWithEncodingDetection(jsonFile);
                    JsonNode jsonNode = objectMapper.readTree(fileContent);

                    // dev 값을 prod로 업데이트
                    updateDevToProdInJsonNode(jsonNode, fileData, fileType);

                    // AGENT_GRAPH의 경우 id 필드를 제거 (query parameter의 agent_id와 충돌 방지)
                    if (fileType == ObjectType.AGENT_GRAPH && jsonNode instanceof ObjectNode) {
                        ObjectNode objectNode = (ObjectNode) jsonNode;
                        if (objectNode.has("id")) {
                            objectNode.remove("id");
                            log.debug("MIG :: AGENT_GRAPH id 필드 제거 (파일 저장 전)");
                        }
                        // graph 객체 내부의 id 필드도 제거
                        if (objectNode.has("graph") && objectNode.get("graph").isObject()) {
                            ObjectNode graphNode = (ObjectNode) objectNode.get("graph");
                            if (graphNode.has("id")) {
                                graphNode.remove("id");
                                log.debug("MIG :: AGENT_GRAPH graph.id 필드 제거 (파일 저장 전)");
                            }
                        }
                    }

                    // 업데이트된 JSON을 파일에 저장
                    String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
                    try (java.io.FileWriter writer = new java.io.FileWriter(jsonFile.toFile(),
                            java.nio.charset.StandardCharsets.UTF_8)) {
                        writer.write(updatedJson);
                        writer.flush();
                    }
                    setFilePermission(jsonFile);

                    log.info("MIG :: 파일 업데이트 완료 - fileName: {}", fileName);

                    // MCP 파일을 업데이트한 경우, 같은 경로의 GRAPH 파일들도 업데이트
                    if (fileType == ObjectType.MCP) {
                        updateMcpUrlInGraphFiles(sourceDir, fileId, fileData);
                    }

                } catch (JsonProcessingException e) {
                    log.error("MIG :: 파일 업데이트 실패 - fileName: {}, error: {}", fileName, e.getMessage(), e);
                } catch (RuntimeException re) {
                    log.error("MIG :: 파일 업데이트 실패 - fileName: {}, error: {}", fileName, re.getMessage(), re);
                } catch (Exception e) {
                    log.error("MIG :: 파일 업데이트 실패 - fileName: {}, error: {}", fileName, e.getMessage(), e);
                }
            }

            log.info("MIG :: dev→prod 업데이트 완료");

        } catch (NullPointerException e) {
            log.error("MIG :: dev→prod 업데이트 실패 (NullPointerException) - sourceDir: {}, error: {}", sourceDir,
                    e.getMessage(),
                    e);
        } catch (Exception e) {
            log.error("MIG :: dev→prod 업데이트 실패 - sourceDir: {}, error: {}", sourceDir, e.getMessage(), e);
        }
    }

    /**
     * MCP URL을 같은 경로의 GRAPH 파일들에서도 업데이트
     *
     * @param sourceDir 소스 디렉토리
     * @param mcpId     MCP ID
     * @param mcpData   MCP 데이터 (server_url의 prod 값 포함)
     */
    private void updateMcpUrlInGraphFiles(Path sourceDir, String mcpId, Map<String, Object> mcpData) {
        try {
            // MCP의 server_url prod 값 가져오기
            Object serverUrlData = mcpData.get("server_url");
            if (serverUrlData == null) {
                log.debug("MIG :: MCP server_url 데이터가 없음 - mcpId: {}", mcpId);
                return;
            }

            String prodServerUrl = null;
            if (serverUrlData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> serverUrlMap = (Map<String, String>) serverUrlData;
                prodServerUrl = serverUrlMap.get("prod");
            }

            if (prodServerUrl == null || prodServerUrl.isEmpty()) {
                log.debug("MIG :: MCP server_url prod 값이 없음 - mcpId: {}", mcpId);
                return;
            }

            log.info("MIG :: GRAPH 파일에서 MCP URL 업데이트 시작 - mcpId: {}, prodServerUrl: {}", mcpId, prodServerUrl);

            // 같은 경로의 모든 GRAPH 파일 찾기
            List<Path> graphFiles;
            try {
                graphFiles = Files.list(sourceDir)
                        .filter(path -> path.toString().endsWith(".json"))
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            String fileName = path.getFileName().toString();
                            Object[] parseResult = parseFileName(fileName);
                            if (parseResult != null) {
                                ObjectType fileType = (ObjectType) parseResult[0];
                                return fileType == ObjectType.AGENT_GRAPH;
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("MIG :: GRAPH 파일 목록 조회 실패 - sourceDir: {}, error: {}", sourceDir, e.getMessage(), e);
                return;
            }

            if (graphFiles.isEmpty()) {
                log.debug("MIG :: 같은 경로에 GRAPH 파일이 없음 - sourceDir: {}", sourceDir);
                return;
            }

            // 각 GRAPH 파일 처리
            for (Path graphFile : graphFiles) {
                try {
                    String fileName = graphFile.getFileName().toString();
                    log.debug("MIG :: GRAPH 파일 처리 시작 - fileName: {}", fileName);

                    // 파일 읽기
                    String fileContent = readJsonFileWithEncodingDetection(graphFile);
                    JsonNode jsonNode = objectMapper.readTree(fileContent);

                    // nodes 배열 찾기
                    JsonNode nodesArray = null;
                    if (jsonNode.has("graph") && jsonNode.get("graph").isObject()) {
                        JsonNode graphNode = jsonNode.get("graph");
                        if (graphNode.has("nodes") && graphNode.get("nodes").isArray()) {
                            nodesArray = graphNode.get("nodes");
                        }
                    }
                    if (nodesArray == null && jsonNode.has("nodes") && jsonNode.get("nodes").isArray()) {
                        nodesArray = jsonNode.get("nodes");
                    }

                    if (nodesArray == null || !nodesArray.isArray()) {
                        log.debug("MIG :: GRAPH 파일에 nodes 배열이 없음 - fileName: {}", fileName);
                        continue;
                    }

                    // nodes 배열에서 type이 "mcp"인 노드 찾기
                    boolean updated = false;
                    com.fasterxml.jackson.databind.node.ArrayNode nodesArrayNode =
                            (com.fasterxml.jackson.databind.node.ArrayNode) nodesArray;

                    for (int i = 0; i < nodesArrayNode.size(); i++) {
                        JsonNode node = nodesArrayNode.get(i);
                        if (node.has("type") && "mcp".equals(node.get("type").asText())) {
                            if (node.has("data") && node.get("data").isObject()) {
                                ObjectNode dataNode = (ObjectNode) node.get("data");

                                // MCP ID가 일치하는지 확인 (data.mcp_id 또는 data.id)
                                String nodeMcpId = null;
                                if (dataNode.has("mcp_id")) {
                                    nodeMcpId = dataNode.get("mcp_id").asText();
                                } else if (dataNode.has("id")) {
                                    nodeMcpId = dataNode.get("id").asText();
                                }

                                if (mcpId.equals(nodeMcpId)) {
                                    // server_url 업데이트
                                    dataNode.put("server_url", prodServerUrl);
                                    updated = true;
                                    log.info("MIG :: GRAPH 파일의 MCP 노드 server_url 업데이트 - fileName: {}, mcpId: {}, " +
                                                    "serverUrl: {}",
                                            fileName, mcpId, prodServerUrl);
                                }
                            }
                        }
                    }

                    // 업데이트된 경우 파일 저장
                    if (updated) {
                        String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
                        try (java.io.FileWriter writer = new java.io.FileWriter(graphFile.toFile(),
                                java.nio.charset.StandardCharsets.UTF_8)) {
                            writer.write(updatedJson);
                            writer.flush();
                        }
                        setFilePermission(graphFile);
                        log.info("MIG :: GRAPH 파일 업데이트 완료 - fileName: {}", fileName);
                    }

                } catch (Exception e) {
                    log.error("MIG :: GRAPH 파일 업데이트 실패 - fileName: {}, error: {}", graphFile.getFileName(),
                            e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("MIG :: GRAPH 파일에서 MCP URL 업데이트 실패 - mcpId: {}, error: {}", mcpId, e.getMessage(), e);
        }
    }

    /**
     * JSON 노드에서 dev 값을 prod로 업데이트
     *
     * @param jsonNode JSON 노드
     * @param fileData 마이그레이션 데이터 (해당 파일의 데이터)
     * @param fileType 파일 타입
     */
    private void updateDevToProdInJsonNode(JsonNode jsonNode, Map<String, Object> fileData, ObjectType fileType) {
        List<String> fields = TYPE_EXTRACT_FIELDS.get(fileType);
        log.info("MIG :: - 타입별 추출 필드 목록 - fileType: {}, fields: {}", fileType, fields);

        if (fields == null || fields.isEmpty()) {
            log.debug("MIG :: 타입별 추출 필드가 정의되지 않음 - type: {}", fileType);
            return;
        }

        if (!(jsonNode instanceof ObjectNode)) {
            return;
        }

        ObjectNode objectNode = (ObjectNode) jsonNode;

        // AGENT_GRAPH는 agent_app_nodes 필드를 graph.nodes 배열 내부의 agent__app 노드에 업데이트
        if (fileType == ObjectType.AGENT_GRAPH) {
            log.info("MIG :: AGENT_GRAPH dev→prod 업데이트 시작");
            Object agentAppNodesData = fileData.get("agent_app_nodes");
            if (agentAppNodesData == null) {
                log.warn("MIG :: AGENT_GRAPH agent_app_nodes 데이터가 없음 - fileData keys: {}", fileData.keySet());
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> agentAppNodesMap = (Map<String, Object>) agentAppNodesData;
            Object prodValue = agentAppNodesMap.get("prod");
            Object devValue = agentAppNodesMap.get("dev");

            log.info("MIG :: AGENT_GRAPH agent_app_nodes - prod: {}, dev: {}",
                    prodValue != null
                            ? (prodValue instanceof List ? ((List<?>) prodValue).size() + "개"
                            : prodValue.getClass().getSimpleName())
                            : "null",
                    devValue != null
                            ? (devValue instanceof List ? ((List<?>) devValue).size() + "개"
                            : devValue.getClass().getSimpleName())
                            : "null");

            // prod 값이 없거나 비어있으면 dev 값 사용
            if (prodValue == null || (prodValue instanceof List && ((List<?>) prodValue).isEmpty())) {
                log.info("MIG :: AGENT_GRAPH prod 값이 없거나 비어있어 dev 값 사용");
                prodValue = devValue;
            }

            if (prodValue == null || (prodValue instanceof List && ((List<?>) prodValue).isEmpty())) {
                log.warn("MIG :: AGENT_GRAPH agent_app_nodes prod/dev 값이 없거나 비어있음");
                return;
            }

            // prodValue는 List<Map<String, String>> 형태
            if (!(prodValue instanceof List)) {
                log.warn("MIG :: AGENT_GRAPH agent_app_nodes prod 값이 List가 아님 - type: {}",
                        prodValue != null ? prodValue.getClass().getSimpleName() : "null");
                return;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, String>> prodAgentAppNodes = (List<Map<String, String>>) prodValue;

            log.info("MIG :: AGENT_GRAPH prodAgentAppNodes 개수: {}", prodAgentAppNodes.size());

            // graph.nodes 배열 찾기
            JsonNode nodesArray = null;
            if (jsonNode.has("graph") && jsonNode.get("graph").isObject()) {
                JsonNode graphNode = jsonNode.get("graph");
                if (graphNode.has("nodes") && graphNode.get("nodes").isArray()) {
                    nodesArray = graphNode.get("nodes");
                    log.info("MIG :: AGENT_GRAPH graph.nodes 배열 찾음 - 노드 개수: {}", nodesArray.size());
                }
            } else if (jsonNode.has("nodes") && jsonNode.get("nodes").isArray()) {
                nodesArray = jsonNode.get("nodes");
                log.info("MIG :: AGENT_GRAPH 최상위 nodes 배열 찾음 - 노드 개수: {}", nodesArray.size());
            }

            if (nodesArray == null || !nodesArray.isArray()) {
                List<String> keys = new ArrayList<>();
                jsonNode.fieldNames().forEachRemaining(keys::add);
                log.warn("MIG :: AGENT_GRAPH nodes 배열을 찾을 수 없음 - jsonNode keys: {}", String.join(", ", keys));
                return;
            }

            // prodAgentAppNodes를 Map으로 변환 (agent_app_id를 키로 사용)
            // 주의: prod 배열의 첫 번째 요소의 agent_app_id를 키로 사용 (dev 배열의 agent_app_id와 매칭)
            Map<String, Map<String, String>> prodNodesMap = new java.util.HashMap<>();
            if (devValue instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> devAgentAppNodes = (List<Map<String, String>>) devValue;
                log.info("MIG :: AGENT_GRAPH devAgentAppNodes 개수: {}", devAgentAppNodes.size());

                // dev 배열의 인덱스와 prod 배열의 인덱스를 매칭
                for (int i = 0; i < devAgentAppNodes.size() && i < prodAgentAppNodes.size(); i++) {
                    Map<String, String> devNode = devAgentAppNodes.get(i);
                    Map<String, String> prodNode = prodAgentAppNodes.get(i);
                    String devAgentAppId = devNode.get("agent_app_id");
                    if (devAgentAppId != null) {
                        prodNodesMap.put(devAgentAppId, prodNode);
                        log.info("MIG :: AGENT_GRAPH 매핑 추가 - dev agent_app_id: {}, prod agent_app_id: {}, prod " +
                                        "api_key: {}",
                                devAgentAppId, prodNode.get("agent_app_id"), prodNode.get("api_key"));
                    }
                }
            } else {
                // dev 값이 없으면 prod 배열의 agent_app_id를 키로 사용
                for (Map<String, String> node : prodAgentAppNodes) {
                    String agentAppId = node.get("agent_app_id");
                    if (agentAppId != null) {
                        prodNodesMap.put(agentAppId, node);
                    }
                }
            }

            log.info("MIG :: AGENT_GRAPH prodNodesMap 크기: {}", prodNodesMap.size());

            // nodes 배열에서 type: "agent__app"인 노드들을 찾아서 업데이트
            // nodesArray는 ArrayNode이므로 ArrayNode로 캐스팅
            if (!(nodesArray instanceof com.fasterxml.jackson.databind.node.ArrayNode)) {
                log.warn("MIG :: AGENT_GRAPH nodes가 ArrayNode가 아님");
                return;
            }

            com.fasterxml.jackson.databind.node.ArrayNode nodesArrayNode =
                    (com.fasterxml.jackson.databind.node.ArrayNode) nodesArray;

            int updatedCount = 0;
            for (int i = 0; i < nodesArrayNode.size(); i++) {
                JsonNode node = nodesArrayNode.get(i);
                if (node.has("type") && "agent__app".equals(node.get("type").asText())) {
                    if (node.has("data") && node.get("data").isObject()) {
                        ObjectNode dataNode = (ObjectNode) node.get("data");
                        if (dataNode.has("agent_app_id")) {
                            String currentAgentAppId = dataNode.get("agent_app_id").asText();
                            Map<String, String> prodNode = prodNodesMap.get(currentAgentAppId);
                            if (prodNode != null) {
                                // agent_app_id 업데이트
                                if (prodNode.containsKey("agent_app_id")) {
                                    String newAgentAppId = prodNode.get("agent_app_id");
                                    dataNode.put("agent_app_id", newAgentAppId);
                                    log.info("MIG :: AGENT_GRAPH agent_app_id 업데이트 - 기존: {}, 새로운: {}",
                                            currentAgentAppId, newAgentAppId);
                                }
                                updatedCount++;
                            } else {
                                log.warn("MIG :: AGENT_GRAPH prodNodesMap에서 매칭되는 노드를 찾을 수 없음 - agent_app_id: {}",
                                        currentAgentAppId);
                            }
                        }
                        // api_key 업데이트 (agent__app 노드의 data 안에 있으면 무조건 업데이트)
                        String newApiKey = "sk-9d2f4281582b82654b98b0d62141e557"; // SKAX  PROD 기본 API KEY
                        dataNode.put("api_key", newApiKey);
                        log.info("MIG :: AGENT_GRAPH api_key 업데이트 - 새로운: {}", newApiKey);
                    }
                }
            }
            log.info("MIG :: AGENT_GRAPH 노드 업데이트 완료 - 총 {}개 노드 업데이트됨", updatedCount);

            // AGENT_GRAPH의 경우 id 필드를 제거 (query parameter의 agent_id와 충돌 방지)
            if (objectNode.has("id")) {
                objectNode.remove("id");
                log.debug("MIG :: AGENT_GRAPH id 필드 제거 (query parameter의 agent_id 사용)");
            }
            // graph 객체 내부의 id 필드도 제거
            if (objectNode.has("graph") && objectNode.get("graph").isObject()) {
                ObjectNode graphNode = (ObjectNode) objectNode.get("graph");
                if (graphNode.has("id")) {
                    graphNode.remove("id");
                    log.debug("MIG :: AGENT_GRAPH graph.id 필드 제거");
                }
            }

            return;
        }

        // VECTOR_DB는 connection_info 내부 필드, 나머지는 최상위 레벨 필드
        if (fileType == ObjectType.VECTOR_DB) {
            // connection_info 객체 내부 필드 업데이트
            Object connectionInfoData = fileData.get("connection_info");
            if (!(connectionInfoData instanceof Map)) {
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Map<String, String>> connectionInfo = (Map<String, Map<String, String>>) connectionInfoData;

            if (!jsonNode.has("connection_info") || !jsonNode.get("connection_info").isObject()) {
                return;
            }

            ObjectNode connInfoNode = (ObjectNode) jsonNode.get("connection_info");

            for (String field : fields) {
                Map<String, String> fieldMap = connectionInfo.get(field);
                if (fieldMap == null) {
                    continue;
                }

                String devValue = fieldMap.get("dev");
                String prodValue = fieldMap.get("prod");
                String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;

                if (newValue != null) {
                    connInfoNode.put(field, newValue);
                    log.debug("MIG :: VECTOR_DB 필드 업데이트 - field: {}, newValue: {}", field,
                            newValue.length() > 50 ? newValue.substring(0, 50) + "..." : newValue);
                }
            }
        } else if (fileType == ObjectType.MCP) {

            // MCP 객체 내부 필드 업데이트
            for (String field : fields) {
                Object fieldData = fileData.get(field);
                if (fieldData == null) {
                    log.info("MIG ::  MCP null 체크 - field: {}", field);
                    continue;
                }

                // auth_config는 복잡한 구조 (Object) - TOOL의 api_param처럼 내부 필드들을 업데이트
                if (fieldData instanceof Map && field.equals("auth_config")) {
                    ObjectNode authConfigNode = (ObjectNode) objectNode.get("auth_config");
                    if (authConfigNode == null) {
                        log.warn("MIG :: auth_config 노드가 없습니다 - type: {}", fileType);
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    Map<String, Object> authConfigInfo = (Map<String, Object>) fieldData;

                    for (String key : authConfigInfo.keySet()) {
                        log.info("MIG ::  MCP - authConfigInfo keys: {}", key);
                        Object fieldDataObj = authConfigInfo.get(key);

                        if (fieldDataObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> fieldMap = (Map<String, Object>) fieldDataObj;
                            Object prodValue = fieldMap.get("prod");
                            Object devValue = fieldMap.get("dev");

                            // prodValue가 있으면 업데이트, 없으면 devValue 사용
                            Object newValue = (prodValue != null) ? prodValue : devValue;

                            if (newValue != null) {
                                // auth_config 내부 필드 업데이트
                                if (newValue instanceof String) {
                                    authConfigNode.put(key, (String) newValue);
                                    log.info("MIG ::  MCP - auth_config.{} 업데이트 (문자열) - newValue: {}", key, newValue);
                                } else if (newValue instanceof Map || newValue instanceof List) {
                                    // Map이나 List인 경우 JsonNode로 변환
                                    JsonNode valueNode = objectMapper.valueToTree(newValue);
                                    authConfigNode.set(key, valueNode);
                                    log.info("MIG ::  MCP - auth_config.{} 업데이트 (객체/배열) - newValue: {}", key, newValue);
                                } else {
                                    // 기타 타입은 문자열로 변환
                                    String valueStr = String.valueOf(newValue);
                                    authConfigNode.put(key, valueStr);
                                    log.info("MIG ::  MCP - auth_config.{} 업데이트 (기타) - newValue: {}", key, valueStr);
                                }
                            } else {
                                log.debug("MIG ::  MCP - auth_config.{} prod/dev 값이 null - key: {}", key);
                            }
                        }
                    }
                } else if (fieldData instanceof Map) {
                    // server_url 등 문자열 필드
                    @SuppressWarnings("unchecked")
                    Map<String, String> fieldMap = (Map<String, String>) fieldData;
                    String devValue = fieldMap.get("dev");
                    String prodValue = fieldMap.get("prod");
                    String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;

                    if (newValue != null) {
                        objectNode.put(field, newValue);
                        log.debug("MIG :: MCP 필드 업데이트 - type: {}, field: {}, newValue: {}", fileType, field,
                                newValue.length() > 50 ? newValue.substring(0, 50) + "..." : newValue);
                    }
                }

            }
        } else if (fileType == ObjectType.TOOL) {

            // TOOL 객체 내부 필드 업데이트
            for (String field : fields) {
                Object fieldData = fileData.get(field);
                if (fieldData == null) {
                    continue;
                }

                // fieldData는 {dev, prod} Map 형태
                if (fieldData instanceof Map && field.equals("api_param")) {

                    ObjectNode apiParamNode = (ObjectNode) objectNode.get("api_param");
                    if (apiParamNode == null) {
                        log.warn("MIG :: api_param 노드가 없습니다 - type: {}", fileType);
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    Map<String, Object> apiParamInfo = (Map<String, Object>) fieldData;

                    for (String key : apiParamInfo.keySet()) {

                        log.info("MIG ::  TOOL - apiParamInfo keys: {}", key);
                        Object fieldDataObj = apiParamInfo.get(key);

                        if (fieldDataObj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> fieldMap = (Map<String, Object>) fieldDataObj;
                            Object prodValue = fieldMap.get("prod");

                            // prodValue가 있으면 업데이트
                            if (prodValue != null) {
                                if (key.equals("body")) {
                                    // body는 문자열이지만 "{}" 같은 경우 JSON 객체로 변환
                                    apiParamNode.remove("body");
                                    try {
                                        if (prodValue instanceof String) {
                                            String bodyValueStr = (String) prodValue;
                                            // "{}" 또는 "[]" 같은 JSON 문자열인지 확인
                                            if ((bodyValueStr.trim().startsWith("{") && bodyValueStr.trim().endsWith(
                                                    "}"))
                                                    || (bodyValueStr.trim().startsWith("[") && bodyValueStr.trim().endsWith("]"))) {
                                                // JSON 문자열을 파싱하여 객체/배열로 변환
                                                JsonNode bodyNode = objectMapper.readTree(bodyValueStr);
                                                apiParamNode.set("body", bodyNode);
                                                log.info("MIG ::  TOOL - body 업데이트 (JSON 파싱) - prodValue: {}",
                                                        bodyValueStr);
                                            } else {
                                                // 일반 문자열인 경우
                                                apiParamNode.put("body", bodyValueStr);
                                                log.info("MIG ::  TOOL - body 업데이트 (문자열) - prodValue: {}",
                                                        bodyValueStr);
                                            }
                                        } else if (prodValue instanceof Map || prodValue instanceof List) {
                                            // 이미 Map이나 List인 경우
                                            JsonNode bodyNode = objectMapper.valueToTree(prodValue);
                                            apiParamNode.set("body", bodyNode);
                                            log.info("MIG ::  TOOL - body 업데이트 (객체/배열) - prodValue: {}", prodValue);
                                        } else {
                                            // 기타 타입은 문자열로 변환
                                            String bodyValueStr = prodValue != null ? String.valueOf(prodValue) : "";
                                            apiParamNode.put("body", bodyValueStr);
                                            log.info("MIG ::  TOOL - body 업데이트 (기타) - prodValue: {}", bodyValueStr);
                                        }
                                    } catch (Exception e) {
                                        // JSON 파싱 실패 시 문자열로 처리
                                        String bodyValueStr = prodValue != null ? String.valueOf(prodValue) : "";
                                        apiParamNode.put("body", bodyValueStr);
                                        log.warn("MIG ::  TOOL - body JSON 파싱 실패, 문자열로 처리 - prodValue: {}, error: {}",
                                                bodyValueStr, e.getMessage());
                                    }
                                } else if (key.equals("params") || key.equals("headers")) {
                                    // params와 headers는 객체
                                    try {
                                        ObjectNode targetNode = (ObjectNode) apiParamNode.get(key);
                                        if (targetNode == null) {
                                            targetNode = apiParamNode.objectNode();
                                            apiParamNode.set(key, targetNode);
                                        }

                                        // prodValue를 Map으로 변환
                                        if (prodValue instanceof Map) {
                                            @SuppressWarnings("unchecked")
                                            Map<String, Object> prodMap = (Map<String, Object>) prodValue;
                                            // 기존 노드 제거 후 새로 설정
                                            apiParamNode.remove(key);
                                            ObjectNode newTargetNode = objectMapper.valueToTree(prodMap);
                                            apiParamNode.set(key, newTargetNode);
                                            log.info("MIG ::  TOOL - {} 업데이트 완료 - keys: {}", key, prodMap.keySet());
                                        } else {
                                            // JSON 문자열인 경우 파싱
                                            String prodValueStr = String.valueOf(prodValue);
                                            if (prodValueStr.startsWith("{") || prodValueStr.startsWith("[")) {
                                                JsonNode prodNode = objectMapper.readTree(prodValueStr);
                                                apiParamNode.remove(key);
                                                apiParamNode.set(key, prodNode);
                                                log.info("MIG ::  TOOL - {} 업데이트 완료 (JSON 파싱)", key);
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.error("MIG ::  TOOL - {} 업데이트 실패 - key: {}, error: {}", key, key,
                                                e.getMessage(), e);
                                    }
                                } else {
                                    // 기타 필드도 처리 (필요시)
                                    log.debug("MIG ::  TOOL - 기타 필드 - key: {}, prodValue: {}", key, prodValue);
                                }
                            } else {
                                log.debug("MIG ::  TOOL - prodValue가 null - key: {}", key);
                            }
                        }
                    }
                }

                // fieldData는 {dev, prod} Map 형태
                if (fieldData instanceof Map) {

                    @SuppressWarnings("unchecked")
                    Map<String, String> fieldMap = (Map<String, String>) fieldData;
                    String devValue = fieldMap.get("dev");
                    String prodValue = fieldMap.get("prod");
                    String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;

                    if (newValue != null) {
                        objectNode.put(field, newValue);
                        log.debug("MIG :: TOOL 필드 업데이트 - type: {}, field: {}, newValue: {}", fileType, field,
                                newValue.length() > 50 ? newValue.substring(0, 50) + "..." : newValue);
                    }
                }
            }
        } else if (fileType == ObjectType.MODEL) {
            // MODEL: endpoints.url, endpoints.key → endpoints[0].url, endpoints[0].key에 적용
            log.info("MIG :: MODEL 필드 업데이트 시작 - fields: {}", fields);

            // endpoints 배열 가져오기
            JsonNode endpointsNode = objectNode.get("endpoints");
            if (endpointsNode != null && endpointsNode.isArray() && endpointsNode.size() > 0) {
                ObjectNode firstEndpoint = (ObjectNode) endpointsNode.get(0);

                for (String field : fields) {
                    Object fieldData = fileData.get(field);
                    if (fieldData == null) {
                        log.debug("MIG :: MODEL 필드 데이터 없음 - field: {}", field);
                        continue;
                    }

                    // 중첩 필드인 경우 (endpoints.url, endpoints.key)
                    if (field.contains(".")) {
                        String[] parts = field.split("\\.", 2);
                        String parentField = parts[0];  // endpoints
                        String childField = parts[1];   // url 또는 key

                        if (!"endpoints".equals(parentField)) {
                            log.warn("MIG :: MODEL - 지원하지 않는 parent 필드: {}", parentField);
                            continue;
                        }

                        // fieldData는 {dev, prod} Map 형태
                        if (fieldData instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, String> fieldMap = (Map<String, String>) fieldData;
                            String devValue = fieldMap.get("dev");
                            String prodValue = fieldMap.get("prod");
                            String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;

                            if (newValue != null) {
                                firstEndpoint.put(childField, newValue);
                                log.info("MIG :: MODEL endpoints[0].{} 업데이트 - newValue: {}", childField, newValue);
                            }
                        }
                    }
                }
            } else {
                log.warn("MIG :: MODEL - endpoints 배열이 없거나 비어있습니다");
            }
        } else if (fileType == ObjectType.AGENT_APP) {
            // AGENT_APP 필드 업데이트 (image_url, app_id는 문자열, 나머지는 숫자)
            for (String field : fields) {
                Object fieldData = fileData.get(field);
                if (fieldData == null) {
                    continue;
                }

                // image_url 처리 (문자열)
                if ("image_url".equals(field)) {
                    if (fieldData instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> fieldMap = (Map<String, String>) fieldData;
                        String devValue = fieldMap.get("dev");
                        String prodValue = fieldMap.get("prod");
                        
                        // if (devValue == null || devValue.isEmpty()) {
                        //     log.debug("MIG :: AGENT_APP image_url dev 값 없음 - 건너뜀");
                        //     continue;
                        // }
                        
                        String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;
                        objectNode.put(field, newValue);
                        log.info("MIG :: AGENT_APP image_url 업데이트 - newValue: {}", newValue);
                    }
                }

                // app_id 처리 (문자열)
                if ("app_id".equals(field)) {
                    if (fieldData instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> fieldMap = (Map<String, String>) fieldData;
                        String devValue = fieldMap.get("dev");
                        String prodValue = fieldMap.get("prod");

                        // if (devValue == null || devValue.isEmpty()) {
                        //     log.debug("MIG :: AGENT_APP app_id dev 값 없음 - 건너뜀");
                        //     continue;
                        // }

                        String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;
                        objectNode.put(field, newValue);
                        log.info("MIG :: AGENT_APP app_id 업데이트 - newValue: {}", newValue);
                    }
                }

                // fieldData는 {dev, prod} Map 형태
                if (fieldData instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> fieldMap = (Map<String, String>) fieldData;
                    String devValue = fieldMap.get("dev");
                    String prodValue = fieldMap.get("prod");
                    String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;

                    if (newValue != null && !newValue.isEmpty()) {
                        try {
                            // 숫자 필드는 숫자로 변환
                            if ("cpu_request".equals(field) || "mem_request".equals(field) 
                                    || "min_replicas".equals(field) || "max_replicas".equals(field) || "cpu_limit".equals(field) || "mem_limit".equals(field)) {
                                // 정수로 변환 시도
                                try {
                                    Integer intValue = Integer.parseInt(newValue);
                                    objectNode.put(field, intValue);
                                    log.debug("MIG :: AGENT_APP 필드 업데이트 (숫자) - field: {}, newValue: {}", field, intValue);
                                } catch (NumberFormatException e) {
                                    // 정수 변환 실패 시 실수로 시도
                                    try {
                                        Double doubleValue = Double.parseDouble(newValue);
                                        objectNode.put(field, doubleValue);
                                        log.debug("MIG :: AGENT_APP 필드 업데이트 (실수) - field: {}, newValue: {}", field, doubleValue);
                                    } catch (NumberFormatException e2) {
                                        // 숫자 변환 실패 시 문자열로 처리
                                        objectNode.put(field, newValue);
                                        log.warn("MIG :: AGENT_APP 필드 업데이트 (숫자 변환 실패, 문자열로 처리) - field: {}, newValue: {}", field, newValue);
                                    }
                                }
                            } else {
                                // 문자열 필드
                                objectNode.put(field, newValue);
                                log.debug("MIG :: AGENT_APP 필드 업데이트 (문자열) - field: {}, newValue: {}", field, 
                                        newValue.length() > 50 ? newValue.substring(0, 50) + "..." : newValue);
                            }
                        } catch (Exception e) {
                            log.error("MIG :: AGENT_APP 필드 업데이트 실패 - field: {}, newValue: {}, error: {}", 
                                    field, newValue, e.getMessage(), e);
                        }
                    }
                }
            }
        } else {
            // 최상위 레벨 필드 업데이트 (KNOWLEDGE 등)
            for (String field : fields) {
                Object fieldData = fileData.get(field);
                if (fieldData == null) {
                    continue;
                }

                // fieldData는 {dev, prod} Map 형태
                if (fieldData instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> fieldMap = (Map<String, String>) fieldData;
                    String devValue = fieldMap.get("dev");
                    String prodValue = fieldMap.get("prod");
                    String newValue = (prodValue != null && !prodValue.isEmpty()) ? prodValue : devValue;

                    if (newValue != null) {
                        objectNode.put(field, newValue);
                        log.debug("MIG :: 필드 업데이트 - type: {}, field: {}, newValue: {}", fileType, field,
                                newValue.length() > 50 ? newValue.substring(0, 50) + "..." : newValue);
                    }
                }
            }
        }
    }

    /**
     * DB에 마이그레이션 정보 저장 (migrationData 포함)
     *
     * @param type          객체 타입
     * @param id            객체 ID
     * @param projectId     프로젝트 ID
     * @param filePath      파일 경로
     * @param targetDir     대상 디렉토리
     * @param migrationData 마이그레이션 데이터
     */
    @Transactional
    private void saveMigrationToDatabaseWithData(ObjectType type, String id, String projectId,
            String filePath, String fileNms,
            Map<String, List<Map<String, Object>>> migrationData,
            String projectName, String assetName) {
        try {
            log.info("MIG :: === DB 저장 시작 (migrationData 포함) ===");
            log.info("MIG :: type: {}", type);
            log.info("MIG :: id: {}", id);
            log.info("MIG :: projectId: {}", projectId);
            log.info("MIG :: projectName: [{}]", projectName);
            log.info("MIG :: assetName: [{}]", assetName);

            // 현재 사용자 정보 가져오기
            String currentUser = getCurrentUser();

            // fileNms가 null이거나 비어있으면 기본값 설정
            if (fileNms == null || fileNms.trim().isEmpty()) {
                log.warn("MIG :: fileNms가 비어있습니다. 기본값으로 설정합니다.");
                fileNms = "";
            }

            log.info("MIG :: 파일 목록 - fileNms: [{}]", fileNms);

            // 모델 서빙일 때 통합 JSON 파일에서 1_MODEL_로 시작하는 fileName 추출(모델 카탈로그 ID) 하여 pgmDescCtnt에 설정
            String pgmDescCtnt = "";
            if (type == ObjectType.SERVING_MODEL) {
                try {
                    Path jsonPath = Paths.get(filePath);
                    if (Files.exists(jsonPath)) {
                        String jsonContent = readJsonFileWithEncodingDetection(jsonPath);
                        JsonNode rootNode = objectMapper.readTree(jsonContent);
                        
                        if (rootNode.has("files")) {
                            JsonNode filesNode = rootNode.get("files");
                            for (JsonNode fileEntry : filesNode) {
                                String fileName = fileEntry.has("fileName") ? fileEntry.get("fileName").asText() : "";
                                // 1_MODEL_로 시작하는 파일명 찾기
                                if (fileName.startsWith("1_MODEL_")) {
                                    pgmDescCtnt = fileName.replace("1_MODEL_", "").replace(".json", "");
                                    log.info("MIG :: 모델 서빙 -> pgmDescCtnt 설정: {}", pgmDescCtnt);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("MIG :: pgmDescCtnt 추출 실패 (계속 진행) - error: {}", e.getMessage());
                }
                
                if (pgmDescCtnt.isEmpty()) {
                    log.info("MIG :: 모델 서빙 -> 1_MODEL_로 시작하는 파일을 찾지 못함, 빈 문자열로 설정");
                }
            } 
            else if (type == ObjectType.AGENT_APP){
                // 1. Agent ID로 배포 목록 조회
                PageResponse<AgentDeployRes> deployList = 
                    agentDeployService.getAgentAppDeployListById(id, 1, 1000, null, null, null);
                
                if (deployList.getContent().isEmpty()) {
                    log.debug("Agent ID에 해당하는 배포가 없습니다: {}", id);
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                        "Agent ID에 해당하는 배포를 찾을 수 없습니다: " + id);
                }
                
                // 2. status가 "Available"인 배포 중 가장 높은 버전 선택
                AgentDeployRes activeDeploy = deployList.getContent().stream()
                    .filter(deploy -> "Available".equals(deploy.getStatus()))
                    .filter(deploy -> deploy.getVersion() != null)
                    .sorted(java.util.Comparator.comparing(AgentDeployRes::getVersion).reversed())
                    .findFirst()
                    .orElse(null);

                pgmDescCtnt = activeDeploy.getId(); // 배포 ID
                log.info("MIG :: AGENT_APP -> pgmDescCtnt 설정 - deploy ID: {}", pgmDescCtnt);
            }

            // GPO_MIG_MAS 저장
            // public -> -999 변환 후 정수로 파싱
            String projectIdForDb = changePublicToM999(projectId);
            Integer prjSeqInt = Integer.parseInt(projectIdForDb);

            log.info("MIG :: === GPO_MIG_MAS 저장 전 ===");
            log.info("MIG :: prjSeqInt: {}", prjSeqInt);
            log.info("MIG :: projectName (gpoPrjNm에 저장될 값): [{}]", projectName);
            log.info("MIG :: assetName (asstNm에 저장될 값): [{}]", assetName);

            // NOT NULL 제약조건을 위한 기본값 설정
            String finalAsstNm = (assetName != null && !assetName.trim().isEmpty())
                    ? assetName.trim()
                    : (type.name() + "_" + id); // 기본값: 타입_UUID
            String finalGpoPrjNm = (projectName != null && !projectName.trim().isEmpty())
                    ? projectName.trim()
                    : projectId; // 기본값: projectId
            String finalFileNms = (fileNms != null && !fileNms.trim().isEmpty())
                    ? fileNms.trim()
                    : ""; // 빈 문자열

            log.info("MIG :: 최종 저장값 - asstNm: [{}], gpoPrjNm: [{}], fileNms: [{}]",
                    finalAsstNm, finalGpoPrjNm, finalFileNms);

            GpoMigMas migMas = GpoMigMas.builder()
                    .uuid(id)
                    .asstG(type.name())
                    .asstNm(finalAsstNm)
                    .prjSeq(prjSeqInt)
                    .gpoPrjNm(finalGpoPrjNm)
                    .migFilePath(filePath)
                    .migFileNm(finalFileNms)
                    .pgmDescCtnt(pgmDescCtnt)
                    .delYn(0)
                    .fstCreatedAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser)
                    .build();

            GpoMigMas savedMigMas;
            try {
                savedMigMas = repository.save(migMas);
                log.info("MIG :: === GPO_MIG_MAS 저장 성공 ===");
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                if (e.getCause() != null && e.getCause().getCause() != null) {
                    log.error("MIG :: 근본 원인: {}", e.getCause().getCause().getMessage());
                }
                throw new RuntimeException("DB 저장 실패 (제약조건 위반): " + e.getMessage(), e);
            } catch (jakarta.persistence.PersistenceException e) {
                throw new RuntimeException("DB 저장 실패 (영속성 예외): " + e.getMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
            }

            log.info("MIG :: ===== MAP_MAS 진입 전 =====");

            // SAFETY_FILTER, GUARDRAILS 타입이 아닌 경우에만 GPO_MIG_ASST_MAP_MAS 저장
            if (type != ObjectType.SAFETY_FILTER && type != ObjectType.GUARDRAILS) {
                log.info("MIG :: ===== SAFETY_FILTER 체크  =====");

                // assetName 기본값 설정 (GPO_MIG_ASST_MAP_MAS 저장용)
                String finalAssetNameForMap = (assetName != null && !assetName.trim().isEmpty())
                        ? assetName.trim()
                        : (type.name() + "_" + id);

                // 각 파일별로 GPO_MIG_MAS_MAP_MAS 저장
                // migrationData에서 타입별로 데이터 추출하여 처리
                int totalFiles = 0;
                if (migrationData != null) {
                    for (List<Map<String, Object>> typeDataList : migrationData.values()) {
                        if (typeDataList != null) {
                            totalFiles += typeDataList.size();
                        }
                    }
                }
                log.info("MIG :: === GPO_MIG_ASST_MAP_MAS 저장 시작 - 총 {}개 항목 ===", totalFiles);

                // migrationData를 순회하여 각 타입별 데이터 처리
                if (migrationData != null) {
                    for (Map.Entry<String, List<Map<String, Object>>> entry : migrationData.entrySet()) {
                        String typeKey = entry.getKey();
                        List<Map<String, Object>> typeDataList = entry.getValue();

                        if (typeDataList == null || typeDataList.isEmpty()) {
                            continue;
                        }

                        ObjectType asstType;
                        try {
                            asstType = ObjectType.valueOf(typeKey);
                        } catch (IllegalArgumentException e) {
                            log.warn("MIG :: 알 수 없는 타입 - typeKey: {}", typeKey);
                            continue;
                        }

                        for (Map<String, Object> fileData : typeDataList) {
                            Object dataId = fileData.get("id");
                            if (dataId == null) {
                                log.warn("MIG :: fileData에 id가 없음 - typeKey: {}, fileData: {}", typeKey, fileData);
                                continue;
                            }

                            // 저장하지 않을 타입 필터링 (필드 변경 없이 개발과 동일하게 운영에서 생성)
                            // AGENT_APP은 isCustomAgentApp이고 image_url이 있을 때만 저장
                            if (asstType == ObjectType.FEW_SHOT
                                    || asstType == ObjectType.PROMPT) {
                                log.info("MIG :: 저장하지 않는 타입 - type: {}, 건너뜀", asstType);
                                continue; // 타입 전체를 건너뜀
                            }

                            String asstUuid = String.valueOf(dataId);

                            // AGENT_APP은 isCustomAgentApp이고 image_url/app_id가 있을 때만 저장 (단, cpu_request 등은 커스텀이 아니어도 저장)
                            if (asstType == ObjectType.AGENT_APP) {
                                boolean isCustom = false;
                                boolean hasImageUrl = false;
                                boolean hasAppId = false;
                                boolean hasOtherFields = false;

                                try {
                                    isCustom = agentAppMigService.isCustomAgentApp(asstUuid);
                                    log.info("MIG :: AGENT_APP 커스텀 여부 확인 - asstUuid: {}, isCustom: {}", asstUuid,
                                            isCustom);

                                    if (fileData != null) {
                                        // 커스텀인 경우에만 image_url, app_id 확인
                                        if (isCustom) {
                                            // image_url 확인
                                            if (fileData.containsKey("image_url")) {
                                                Object imageUrlData = fileData.get("image_url");
                                                if (imageUrlData instanceof Map) {
                                                    @SuppressWarnings("unchecked")
                                                    Map<String, Object> imageUrlMap = (Map<String, Object>) imageUrlData;
                                                    Object devValue = imageUrlMap.get("dev");
                                                    if (devValue != null && !String.valueOf(devValue).isEmpty() && !"-".equals(String.valueOf(devValue))) {
                                                        hasImageUrl = true;
                                                    }
                                                }
                                            }
                                            
                                            // app_id 확인
                                            if (fileData.containsKey("app_id")) {
                                                Object appIdData = fileData.get("app_id");
                                                if (appIdData instanceof Map) {
                                                    @SuppressWarnings("unchecked")
                                                    Map<String, Object> appIdMap = (Map<String, Object>) appIdData;
                                                    Object devValue = appIdMap.get("dev");
                                                    if (devValue != null && !String.valueOf(devValue).isEmpty() && !"-".equals(String.valueOf(devValue))) {
                                                        hasAppId = true;
                                                    }
                                                } else if (appIdData != null && !String.valueOf(appIdData).isEmpty() && !"-".equals(String.valueOf(appIdData))) {
                                                    hasAppId = true;
                                                }
                                            }
                                        }
                                        
                                        // 다른 필드들(cpu_request, mem_request, min_replicas, max_replicas) 확인 (커스텀 여부와 관계없이)
                                        String[] otherFields = {"cpu_request", "mem_request", "min_replicas", "max_replicas", "cpu_limit", "mem_limit"};
                                        for (String field : otherFields) {
                                            if (fileData.containsKey(field)) {
                                                Object fieldData = fileData.get(field);
                                                if (fieldData instanceof Map) {
                                                    @SuppressWarnings("unchecked")
                                                    Map<String, Object> fieldMap = (Map<String, Object>) fieldData;
                                                    Object devValue = fieldMap.get("dev");
                                                    if (devValue != null && !String.valueOf(devValue).isEmpty() && !"-".equals(String.valueOf(devValue))) {
                                                        hasOtherFields = true;
                                                        break;
                                                    }
                                                } else if (fieldData != null && !String.valueOf(fieldData).isEmpty() && !"-".equals(String.valueOf(fieldData))) {
                                                    hasOtherFields = true;
                                                    break;
                                                }
                                            }
                                        }
                                        
                                        log.info("MIG :: AGENT_APP 필드 확인 - asstUuid: {}, isCustom: {}, hasImageUrl: {}, hasAppId: {}, hasOtherFields: {}",
                                                asstUuid, isCustom, hasImageUrl, hasAppId, hasOtherFields);
                                    }
                                } catch (Exception e) {
                                    log.warn("MIG :: AGENT_APP 커스텀 여부 확인 실패 - asstUuid: {}, error: {}", asstUuid,
                                            e.getMessage());
                                }

                                // 저장 조건:
                                // - 커스텀이면 → 저장 (image_url은 반드시 있음, app_id는 있을 수도 없을 수도 있음)
                                // - 커스텀이 아니면 → hasOtherFields(cpu_request 등)가 있으면 저장
                                if (!isCustom && !hasOtherFields) {
                                    log.info("MIG :: AGENT_APP 저장 건너뜀 (커스텀 아님, 다른 필드 없음) - asstUuid: {}, isCustom: {}, hasOtherFields: {}",
                                            asstUuid, isCustom, hasOtherFields);
                                    continue;
                                }

                                log.info("MIG :: AGENT_APP 저장 진행 - asstUuid: {}, isCustom: {}, hasImageUrl: {}, hasAppId: {}, hasOtherFields: {}",
                                        asstUuid, isCustom, hasImageUrl, hasAppId, hasOtherFields);
                            }
                            log.info("MIG :: 파일 처리 시작 - type: {}, asstUuid: {}", asstType, asstUuid);
                            try {
                                // fileData는 이미 migrationData에서 가져온 것

                                // 타입별 추출 필드 목록 가져오기
                                List<String> fields = TYPE_EXTRACT_FIELDS.get(asstType);

                                log.info("MIG :: 타입별 필드 목록 - asstType: {}, fields: {}",
                                        asstType, fields != null ? fields : "null");
                                log.info("MIG :: fileData 상태 - type: {}, asstUuid: {}, fileData: {}",
                                        asstType, asstUuid, fileData != null ?
                                                "있음 (keys: " + fileData.keySet() + ")" : "null");

                                if (fields != null && !fields.isEmpty()) {
                                    // 조건부 저장 타입: extract된 데이터가 실제로 있는지 확인
                                    // AGENT_GRAPH, TOOL, MCP의 경우 추출 필드가 있는지 + dev 값이 있는지 확인
                                    if (asstType == ObjectType.AGENT_GRAPH
                                            || asstType == ObjectType.TOOL
                                            || asstType == ObjectType.MCP) {

                                        boolean hasExtractedData = false;
                                        for (String fieldName : fields) {
                                            if (fileData != null && fileData.containsKey(fieldName)) {
                                                Object fieldData = fileData.get(fieldName);
                                                // fieldData가 유효한지 확인 (null이 아니고, Map이면 dev 값이 있는지)
                                                if (fieldData != null) {
                                                    if (fieldData instanceof Map) {
                                                        @SuppressWarnings("unchecked")
                                                        Map<String, Object> fieldMap = (Map<String, Object>) fieldData;
                                                        Object devValue = fieldMap.get("dev");
                                                        // dev 값이 있고 유효한 경우만 체크
                                                        if (devValue != null && !String.valueOf(devValue).isEmpty() && !"-".equals(String.valueOf(devValue))) {
                                                            // auth_config나 api_param인 경우 내부 필드도 확인
                                                            if ("auth_config".equals(fieldName) || "api_param".equals(fieldName)) {
                                                                // 내부 필드가 Map인 경우 내부 필드들의 dev 값 확인
                                                                if (devValue instanceof Map) {
                                                                    @SuppressWarnings("unchecked")
                                                                    Map<String, Object> innerMap = (Map<String,
                                                                            Object>) devValue;
                                                                    for (Object innerValue : innerMap.values()) {
                                                                        if (innerValue != null && !String.valueOf(innerValue).isEmpty() && !"-".equals(String.valueOf(innerValue))) {
                                                                            hasExtractedData = true;
                                                                            break;
                                                                        }
                                                                    }
                                                                } else {
                                                                    hasExtractedData = true;
                                                                }
                                                            } else {
                                                                hasExtractedData = true;
                                                            }
                                                            if (hasExtractedData) {
                                                                break;
                                                            }
                                                        }
                                                    } else {
                                                        // 단순 값인 경우
                                                        String value = String.valueOf(fieldData);
                                                        if (!value.isEmpty() && !"-".equals(value)) {
                                                            hasExtractedData = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (!hasExtractedData) {
                                            log.info("MIG :: extract된 데이터가 없어 저장 건너뜀 - type: {}, asstUuid: {}",
                                                    asstType, asstUuid);
                                            continue; // 이 fileData는 저장하지 않음
                                        }
                                    }

                                    log.info("MIG :: 필드별 레코드 생성 시작 - type: {}, asstUuid: {}, fields 개수: {}", asstType
                                            , asstUuid, fields.size());
                                    // 각 필드별로 레코드 생성 (fileData가 null이어도 필드별로 레코드 생성)
                                    for (String fieldName : fields) {
                                        // auth_config나 api_param인 경우 내부 필드별로 개별 레코드 생성
                                        if (("auth_config".equals(fieldName) || "api_param".equals(fieldName))
                                                && fileData != null && fileData.containsKey(fieldName)) {
                                            Object fieldData = fileData.get(fieldName);

                                            if (fieldData instanceof Map) {
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> fieldMap = (Map<String, Object>) fieldData;

                                                // fieldMap의 각 키(내부 필드명)를 순회
                                                for (String innerFieldName : fieldMap.keySet()) {
                                                    Object innerFieldData = fieldMap.get(innerFieldName);

                                                    if (innerFieldData instanceof Map) {
                                                        @SuppressWarnings("unchecked")
                                                        Map<String, Object> innerFieldMap =
                                                                (Map<String, Object>) innerFieldData;
                                                        Object devValue = innerFieldMap.get("dev");
                                                        Object prodValue = innerFieldMap.get("prod");

                                                        // dev 값이 있는 경우만 저장
                                                        if (devValue != null && !String.valueOf(devValue).isEmpty() && !"-".equals(String.valueOf(devValue))) {
                                                            String devCtnt = String.valueOf(devValue);
                                                            String prodCtnt = prodValue != null ?
                                                                    String.valueOf(prodValue) : "-";

                                                            // migMapNm을 fieldName.innerFieldName 형태로 저장 (예:
                                                            // auth_config.username)
                                                            String migMapNm = fieldName + "." + innerFieldName;

                                                            log.info("MIG :: 내부 필드 레코드 생성 - type: {}, asstUuid: {}, " +
                                                                            "migMapNm: {}, devCtnt length: {}",
                                                                    asstType, asstUuid, migMapNm, devCtnt.length());

                                                            GpoMigAsstMapMas mapMas = GpoMigAsstMapMas.builder()
                                                                    .migSeqNo(savedMigMas.getSeqNo())
                                                                    .migUuid(id)
                                                                    .asstUuid(asstUuid)
                                                                    .asstG(asstType.name())
                                                                    .assetNm(finalAssetNameForMap)
                                                                    .migMapNm(migMapNm) // fieldName.innerFieldName 형태
                                                                    .dvlpDtlCtnt(devCtnt)
                                                                    .unyungDtlCtnt(prodCtnt)
                                                                    .build();

                                                            mapMasRepository.save(mapMas);
                                                            log.info("MIG :: GPO_MIG_ASST_MAP_MAS 저장 완료 (내부 필드) - " +
                                                                            "type: {}, asstUuid: {}, migMapNm: {}",
                                                                    asstType, asstUuid, migMapNm);
                                                        } else {
                                                            log.debug("MIG :: 내부 필드 dev 값이 없어 저장 건너뜀 - type: {}, " +
                                                                            "fieldName: {}, innerFieldName: {}",
                                                                    asstType, fieldName, innerFieldName);
                                                        }
                                                    }
                                                }

                                                // auth_config나 api_param은 내부 필드로 처리했으므로 continue
                                                continue;
                                            }
                                        }

                                        // 일반 필드 처리 (auth_config, api_param이 아닌 경우)
                                        String devCtnt = "-";
                                        String prodCtnt = "-";

                                        if (fileData != null && fileData.containsKey(fieldName)) {
                                            Object fieldData = fileData.get(fieldName);

                                            log.info("MIG :: 필드 데이터 추출 시작 - fieldName: {}, fieldData type: {}",
                                                    fieldName,
                                                    fieldData != null ? fieldData.getClass().getSimpleName() : "null");

                                            // fieldData가 {dev, prod} Map 형태인 경우
                                            if (fieldData instanceof Map) {
                                                try {
                                                    @SuppressWarnings("unchecked")
                                                    Map<String, Object> fieldMap = (Map<String, Object>) fieldData;
                                                    Object devValue = fieldMap.get("dev");
                                                    Object prodValue = fieldMap.get("prod");

                                                    // dev 값이 있는 경우만 저장 (조건부 저장 타입인 경우)
                                                    if (asstType == ObjectType.AGENT_GRAPH
                                                            || asstType == ObjectType.TOOL
                                                            || asstType == ObjectType.MCP) {
                                                        if (devValue == null || String.valueOf(devValue).isEmpty() || "-".equals(String.valueOf(devValue))) {
                                                            log.debug("MIG :: dev 값이 없어 저장 건너뜀 - type: {}, fieldName:" +
                                                                    " {}", asstType, fieldName);
                                                            continue; // 이 필드는 저장하지 않음
                                                        }
                                                    }

                                                    devCtnt = devValue != null ? String.valueOf(devValue) : devCtnt;
                                                    prodCtnt = prodValue != null ? String.valueOf(prodValue) : prodCtnt;
                                                    log.info(
                                                            "필드 데이터 추출 완료 (Map) - fieldName: {}, devCtnt length: {}, " +
                                                                    "prodCtnt "
                                                                    +
                                                                    "length: {}",
                                                            fieldName, devCtnt.length(), prodCtnt.length());

                                                } catch (RuntimeException e) {
                                                    log.error("MIG :: 필드 데이터 추출 실패 (Map 파싱 오류) - fieldName: {}, " +
                                                                    "error: {}", fieldName,
                                                            e.getMessage(), e);
                                                } catch (Exception e) {
                                                    log.error("MIG :: 필드 데이터 추출 실패 (Map 파싱 오류) - fieldName: {}, " +
                                                                    "error: {}",
                                                            fieldName, e.getMessage(), e);
                                                }
                                            } else {
                                                // 단순 값인 경우
                                                devCtnt = String.valueOf(fieldData);
                                                prodCtnt = String.valueOf(fieldData);
                                                log.info("MIG :: 필드 데이터 추출 완료 (단순값) - fieldName: {}, value length: {}",
                                                        fieldName, devCtnt.length());
                                            }
                                        } else {
                                            log.warn("MIG :: fileData에 필드가 없음 - fieldName: {}, fileData: {}",
                                                    fieldName,
                                                    fileData != null ? "있음 (keys: " + fileData.keySet() + ")" : "null");
                                            // fileData가 null이거나 필드가 없어도 빈 문자열로 저장
                                        }

                                        // VECTOR_DB의 경우 connection_info 내부 필드 처리
                                        if (asstType == ObjectType.VECTOR_DB && fileData != null && fileData.containsKey(
                                                "connection_info")) {
                                            Object connectionInfoData = fileData.get("connection_info");
                                            log.info("MIG :: VECTOR_DB connection_info 처리 시작 - fieldName: {}",
                                                    fieldName);
                                            if (connectionInfoData instanceof Map) {
                                                try {
                                                    @SuppressWarnings("unchecked")
                                                    Map<String, Object> connectionInfo =
                                                            (Map<String, Object>) connectionInfoData;
                                                    if (connectionInfo.containsKey(fieldName)) {
                                                        Object fieldDataObj = connectionInfo.get(fieldName);
                                                        if (fieldDataObj instanceof Map) {
                                                            @SuppressWarnings("unchecked")
                                                            Map<String, Object> fieldMap =
                                                                    (Map<String, Object>) fieldDataObj;
                                                            Object devValue = fieldMap.get("dev");
                                                            Object prodValue = fieldMap.get("prod");
                                                            devCtnt = devValue != null ? String.valueOf(devValue) :
                                                                    devCtnt;
                                                            prodCtnt = prodValue != null ? String.valueOf(prodValue)
                                                                    : prodCtnt;
                                                            log.info(
                                                                    "VECTOR_DB connection_info 필드 추출 완료 - fieldName: " +
                                                                            "{}, devCtnt"
                                                                            +
                                                                            " length: {}, prodCtnt length: {}",
                                                                    fieldName, devCtnt.length(), prodCtnt.length());
                                                        }
                                                    } else {
                                                        log.warn(
                                                                "connection_info에 필드가 없음 - fieldName: {}, " +
                                                                        "connection_info keys: "
                                                                        +
                                                                        "{}",
                                                                fieldName, connectionInfo.keySet());
                                                    }

                                                } catch (RuntimeException e) {
                                                    log.error("MIG :: VECTOR_DB connection_info 처리 실패 - fieldName: " +
                                                                    "{}, error: {}",
                                                            fieldName, e.getMessage(), e);
                                                } catch (Exception e) {
                                                    log.error("MIG :: VECTOR_DB connection_info 처리 실패 - fieldName: " +
                                                                    "{}, error: {}",
                                                            fieldName, e.getMessage(), e);
                                                }
                                            }
                                        }

                                        log.info(
                                                "GPO_MIG_ASST_MAP_MAS 저장 전 - type: {}, asstUuid: {}, fieldName: {}, " +
                                                        "devCtnt length: {}, "
                                                        +
                                                        "prodCtnt length: {}",
                                                asstType, asstUuid, fieldName, devCtnt != null ? devCtnt.length() : 0,
                                                prodCtnt != null ? prodCtnt.length() : 0);

                                        GpoMigAsstMapMas mapMas = GpoMigAsstMapMas.builder()
                                                .migSeqNo(savedMigMas.getSeqNo())
                                                .migUuid(id)
                                                .asstUuid(asstUuid)
                                                .asstG(asstType.name())
                                                .assetNm(finalAssetNameForMap)
                                                .migMapNm(fieldName) // 필드명 저장
                                                .dvlpDtlCtnt(devCtnt.isEmpty() ? "-" : devCtnt)
                                                .unyungDtlCtnt(prodCtnt)
                                                .build();

                                        log.info(
                                                "GPO_MIG_ASST_MAP_MAS 저장 - assetNm: [{}], migMapNm: {}, devCtnt: " +
                                                        "[{}], prodCtnt:"
                                                        +
                                                        " [{}]",
                                                finalAssetNameForMap, fieldName,
                                                devCtnt != null && devCtnt.length() > 50 ?
                                                        devCtnt.substring(0, 50) + "..."
                                                        : devCtnt,
                                                prodCtnt != null && prodCtnt.length() > 50 ? prodCtnt.substring(0,
                                                        50) + "..."
                                                        : prodCtnt);
                                        mapMasRepository.save(mapMas);
                                        log.info("MIG :: GPO_MIG_MAS_MAP_MAS 저장 완료 - type: {}, asstUuid: {}, " +
                                                        "fieldName: {}",
                                                asstType, asstUuid, fieldName);
                                    }
                                    log.info("MIG :: 필드별 레코드 생성 완료 - type: {}, asstUuid: {}, 총 {}개 레코드 저장", asstType,
                                            asstUuid, fields.size());
                                } else {
                                    // 필드가 정의되지 않은 타입은 기본 레코드 하나만 생성
                                    log.info("MIG :: 필드가 정의되지 않은 타입 - type: {}, asstUuid: {}, 기본 레코드 생성", asstType,
                                            asstUuid);
                                    String devCtnt = "";
                                    String prodCtnt = "";

                                    if (fileData != null) {
                                        // fileData를 JSON 문자열로 변환하여 저장
                                        try {
                                            String fileDataJson = objectMapper.writeValueAsString(fileData);
                                            devCtnt = fileDataJson;
                                            prodCtnt = fileDataJson;
                                            log.debug("MIG :: fileData를 JSON으로 변환 완료 - type: {}, asstUuid: {}, " +
                                                            "length: {}",
                                                    asstType, asstUuid, fileDataJson.length());
                                        } catch (NullPointerException e) {
                                            log.warn("MIG :: fileData를 JSON으로 변환 실패 (NullPointerException) - type: " +
                                                            "{}, asstUuid: {}, error: {}",
                                                    asstType, asstUuid, e.getMessage());
                                        } catch (Exception e) {
                                            log.warn("MIG :: fileData를 JSON으로 변환 실패 - type: {}, asstUuid: {}, error: " +
                                                            "{}", asstType, asstUuid,
                                                    e.getMessage());
                                        }
                                    }

                                    GpoMigAsstMapMas mapMas = GpoMigAsstMapMas.builder()
                                            .migSeqNo(savedMigMas.getSeqNo())
                                            .migUuid(id)
                                            .asstUuid(asstUuid)
                                            .asstG(asstType.name())
                                            .assetNm(finalAssetNameForMap)
                                            .migMapNm(asstType.name() + "_" + asstUuid) // 타입_ID 형식
                                            .dvlpDtlCtnt(devCtnt)
                                            .unyungDtlCtnt(prodCtnt)
                                            .build();

                                    log.info("MIG :: GPO_MIG_ASST_MAP_MAS 저장 (기본) - assetNm: [{}], migMapNm: {}",
                                            finalAssetNameForMap,
                                            asstType.name() + "_" + asstUuid);
                                    mapMasRepository.save(mapMas);
                                    log.info("MIG :: GPO_MIG_MAS_MAP_MAS 저장 완료 (기본) - type: {}, asstUuid: {}",
                                            asstType, asstUuid);
                                }

                                log.info("MIG :: 파일 처리 완료 - type: {}, asstUuid: {}", asstType, asstUuid);

                            } catch (NullPointerException e) {
                                log.error("MIG :: 파일별 MAP_MAS 저장 실패 (NullPointerException) - type: {}, asstUuid: {}, " +
                                                "error: {}",
                                        asstType, asstUuid, e.getMessage(), e);
                                log.error("MIG :: 스택 트레이스:", e);
                            } catch (Exception e) {
                                log.error("MIG :: 파일별 MAP_MAS 저장 실패 - type: {}, asstUuid: {}, error: {}", asstType,
                                        asstUuid, e.getMessage(), e);
                                log.error("MIG :: 스택 트레이스:", e);
                            }
                        }
                    }
                }
                log.info("MIG :: === GPO_MIG_ASST_MAP_MAS 저장 완료 - 총 {}개 항목 처리 ===", totalFiles);

                log.info("MIG :: DB 저장 완료 - type: {}, id: {}, projectId: {}", type, id, projectId);
            }

        } catch (NullPointerException e) {
            log.error("MIG :: DB 저장 중 오류 발생 (NullPointerException) - type: {}, id: {}, projectId: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MIG :: DB 저장 중 오류 발생 - type: {}, id: {}, projectId: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 디렉토리를 재귀적으로 복사
     *
     * @param source 소스 디렉토리
     * @param target 대상 디렉토리
     * @throws IOException 복사 실패 시
     */
    private void copyDirectoryRecursively(Path source, Path target) throws IOException {
        log.debug("MIG :: 디렉토리 복사 시작 - source: {}, target: {}", source, target);

        try {
            Files.walk(source).forEach(sourcePath -> {
                try {
                    Path targetPath = target.resolve(source.relativize(sourcePath));

                    if (Files.isDirectory(sourcePath)) {
                        // 디렉토리인 경우 생성
                        if (!Files.exists(targetPath)) {
                            createDirectoriesWithPermission(targetPath);
                            log.debug("MIG :: 디렉토리 생성 - targetPath: {}", targetPath);
                        }
                    } else {
                        // 파일인 경우 복사
                        Files.copy(sourcePath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        setFilePermission(targetPath);
                        log.debug("MIG :: 파일 복사 - sourcePath: {}, targetPath: {}", sourcePath, targetPath);
                    }
                } catch (IOException e) {
                    log.error("MIG :: 파일/디렉토리 복사 실패 - sourcePath: {}, error: {}", sourcePath, e.getMessage(), e);
                    throw new RuntimeException("파일/디렉토리 복사 실패: " + e.getMessage(), e);
                } catch (NullPointerException e) {
                    log.error("MIG :: 파일/디렉토리 복사 실패 (NullPointerException) - sourcePath: {}, error: {}", sourcePath,
                            e.getMessage(), e);
                    throw new RuntimeException("파일/디렉토리 복사 실패: " + e.getMessage(), e);
                } catch (Exception e) {
                    log.error("MIG :: 파일/디렉토리 복사 실패 (예상치 못한 오류) - sourcePath: {}, error: {}", sourcePath,
                            e.getMessage(), e);
                    throw new RuntimeException("파일/디렉토리 복사 실패: " + e.getMessage(), e);
                }
            });

            log.debug("MIG :: 디렉토리 복사 완료 - source: {}, target: {}", source, target);
        } catch (NullPointerException e) {
            log.error("MIG :: 디렉토리 복사 실패 (NullPointerException) - source: {}, target: {}, error: {}",
                    source, target, e.getMessage(), e);
            throw new IOException("디렉토리 복사 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("MIG :: 디렉토리 복사 실패 (RuntimeException) - source: {}, target: {}, error: {}",
                    source, target, e.getMessage(), e);
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new IOException("디렉토리 복사 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MIG :: 디렉토리 복사 실패 (예상치 못한 오류) - source: {}, target: {}, error: {}",
                    source, target, e.getMessage(), e);
            throw new IOException("디렉토리 복사 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 디렉토리를 재귀적으로 삭제
     *
     * @param directory 삭제할 디렉토리
     * @throws IOException 삭제 실패 시
     */
    private void deleteDirectoryRecursively(Path directory) throws IOException {
        log.debug("MIG :: 디렉토리 삭제 시작 - directory: {}", directory);

        try {
            if (Files.exists(directory)) {
                Files.walk(directory)
                        .sorted(java.util.Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                log.debug("MIG :: 삭제 완료 - path: {}", path);
                            } catch (IOException e) {
                                log.error("MIG :: 파일/디렉토리 삭제 실패 - path: {}, error: {}", path, e.getMessage(), e);
                                throw new RuntimeException("파일/디렉토리 삭제 실패: " + e.getMessage(), e);
                            } catch (NullPointerException e) {
                                log.error("MIG :: 파일/디렉토리 삭제 실패 (NullPointerException) - path: {}, error: {}", path,
                                        e.getMessage(), e);
                                throw new RuntimeException("파일/디렉토리 삭제 실패: " + e.getMessage(), e);
                            } catch (Exception e) {
                                log.error("MIG :: 파일/디렉토리 삭제 실패 (예상치 못한 오류) - path: {}, error: {}", path,
                                        e.getMessage(), e);
                                throw new RuntimeException("파일/디렉토리 삭제 실패: " + e.getMessage(), e);
                            }
                        });
            }

            log.debug("MIG :: 디렉토리 삭제 완료 - directory: {}", directory);
        } catch (NullPointerException e) {
            log.error("MIG :: 디렉토리 삭제 실패 (NullPointerException) - directory: {}, error: {}",
                    directory, e.getMessage(), e);
            throw new IOException("디렉토리 삭제 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("MIG :: 디렉토리 삭제 실패 (RuntimeException) - directory: {}, error: {}",
                    directory, e.getMessage(), e);
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new IOException("디렉토리 삭제 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MIG :: 디렉토리 삭제 실패 (예상치 못한 오류) - directory: {}, error: {}",
                    directory, e.getMessage(), e);
            throw new IOException("디렉토리 삭제 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 통합 JSON 파일을 읽어서 import 거래 수행
     *
     * <p>
     * 통합 JSON 파일을 읽어서 배열의 각 항목을 순서대로 import 수행합니다.
     * </p>
     *
     * @param jsonFilePath 통합 JSON 파일 경로
     * @return import 성공 여부 (모든 항목이 성공하면 true)
     */
    @Override
    public boolean importFromJsonFile(String projectId, String jsonFilePath) {
        List<String> errors = new ArrayList<>();
        List<String> successes = new ArrayList<>();

        try {
            log.info("MIG :: 통합 JSON 파일에서 Import 시작 - jsonFilePath: {}", jsonFilePath);

            Path jsonFile = Paths.get(jsonFilePath);
            if (!Files.exists(jsonFile)) {
                log.error("MIG :: JSON 파일이 존재하지 않습니다 - jsonFilePath: {}", jsonFilePath);
                return false;
            }

            if (!Files.isRegularFile(jsonFile)) {
                log.error("MIG :: 경로가 파일이 아닙니다 - jsonFilePath: {}", jsonFilePath);
                return false;
            }

            log.info("MIG :: 통합 JSON 파일에서 Import 시작 - projectId: {}", projectId);

            // JSON 파일 읽기
            String jsonContent = readJsonFileWithEncodingDetection(jsonFile);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            // 새로운 구조(메타데이터 포함) 또는 기존 구조(배열) 모두 지원
            JsonNode filesNode;
            if (rootNode.isArray()) {
                // 기존 구조: 배열 직접
                filesNode = rootNode;
                log.info("MIG :: JSON 파일 형식: 기존 배열 형식");
            } else if (rootNode.isObject() && rootNode.has("files")) {
                // 새로운 구조: 메타데이터 포함
                filesNode = rootNode.get("files");
                String createdAt = rootNode.has("createdAt") ? rootNode.get("createdAt").asText() : "unknown";
                log.info("MIG :: JSON 파일 형식: 메타데이터 포함 형식, 생성일시: {}", createdAt);
            } else {
                log.error("MIG :: JSON 파일 형식이 올바르지 않습니다 - jsonFilePath: {}", jsonFilePath);
                return false;
            }

            if (!filesNode.isArray()) {
                log.error("MIG :: files 필드가 배열 형식이 아닙니다 - jsonFilePath: {}", jsonFilePath);
                return false;
            }

            log.info("MIG :: JSON 파일 읽기 완료 - 항목 개수: {}", filesNode.size());

            if (filesNode.size() == 0) {
                log.warn("MIG :: JSON 파일에 항목이 없습니다 - jsonFilePath: {}", jsonFilePath);
                return true; // 항목이 없으면 성공으로 간주
            }

            // 배열의 각 항목 처리
            for (int i = 0; i < filesNode.size(); i++) {
                JsonNode fileEntry = filesNode.get(i);
                try {
                    String fileName = fileEntry.has("fileName") ? fileEntry.get("fileName").asText() : null;
                    String typeStr = fileEntry.has("type") ? fileEntry.get("type").asText() : null;
                    String id = fileEntry.has("id") ? fileEntry.get("id").asText() : null;
                    String prjSeq = fileEntry.has("prj_seq") ? fileEntry.get("prj_seq").asText() : projectId;
                    JsonNode data = fileEntry.has("data") ? fileEntry.get("data") : null;

                    if (typeStr == null || id == null || data == null) {
                        log.warn("MIG :: JSON 항목에 필수 필드가 없습니다 - fileName: {}, type: {}, id: {}", fileName, typeStr, id);
                        errors.add(String.format("JSON 항목 형식 오류: fileName=%s", fileName));
                        continue;
                    }

                    ObjectType objectType;
                    try {
                        objectType = ObjectType.valueOf(typeStr);
                    } catch (IllegalArgumentException e) {
                        log.warn("MIG :: 알 수 없는 타입 - type: {}, fileName: {}", typeStr, fileName);
                        errors.add(String.format("알 수 없는 타입: %s (fileName: %s)", typeStr, fileName));
                        continue;
                    }

                    log.info("MIG :: [{}/{}] 처리 시작 - fileName: {}, type: {}, id: {}", i + 1, rootNode.size(), fileName,
                            objectType, id);

                    // Import 수행
                    // JsonNode를 문자열로 변환 (Graph의 경우 이미 convertGraphToImportFormat으로 변환된 형식)
                    String importJson;

                    // AGENT_GRAPH, MCP의 경우 원본 JSON 문자열을 사용 (JSON 구조 변경 방지)
                    if ((objectType == ObjectType.AGENT_GRAPH || objectType == ObjectType.MCP) && fileEntry.has(
                            "originalJson")) {
                        // fileEntry에서 originalJson 가져오기 (JsonNode이므로 asText() 사용)
                        String originalJson = fileEntry.get("originalJson").asText();

                        if (originalJson != null && !originalJson.isEmpty()) {
                            // 원본 JSON 문자열에서 id 필드만 제거
                            try {
                                JsonNode originalNode = objectMapper.readTree(originalJson);
                                if (originalNode.isObject()) {
                                    ObjectNode originalObjectNode = (ObjectNode) originalNode;
                                    if (originalObjectNode.has("id")) {
                                        originalObjectNode.remove("id");
                                    }
                                    // Graph의 경우 graph 객체 내부의 id 필드도 제거
                                    if (objectType == ObjectType.AGENT_GRAPH
                                            && originalObjectNode.has("graph")
                                            && originalObjectNode.get("graph").isObject()) {
                                        ObjectNode graphNode = (ObjectNode) originalObjectNode.get("graph");
                                        if (graphNode.has("id")) {
                                            graphNode.remove("id");
                                        }
                                    }
                                    importJson = objectMapper.writeValueAsString(originalObjectNode);
                                } else {
                                    importJson = originalJson;
                                }
                            } catch (Exception e) {
                                log.warn("MIG :: 원본 JSON 파싱 실패, data JsonNode 사용 - type: {}, id: {}, error: {}",
                                        objectType, id, e.getMessage());
                                // 원본 JSON 파싱 실패 시 기존 로직 사용
                                importJson = objectMapper.writeValueAsString(data);
                            }
                        } else {
                            // originalJson이 없으면 기존 로직 사용
                            importJson = objectMapper.writeValueAsString(data);
                        }
                    } else if (data.isObject()) {
                        // AGENT_GRAPH가 아닌 경우 기존 로직 사용
                        // JsonNode를 직접 JSON 문자열로 변환 (pretty printer 사용하지 않음)
                        importJson = objectMapper.writeValueAsString(data);
                    } else if (objectType == ObjectType.SAFETY_FILTER && data.isArray()) {
                        // SAFETY_FILTER는 배열(JSON Array) 형태를 그대로 사용
                        importJson = objectMapper.writeValueAsString(data);
                    } else {
                        log.warn("MIG :: data가 유효하지 않습니다 - type: {}, id: {}, data: {}", objectType, id, data);
                        errors.add(String.format("Import 실패: %s/%s (데이터 형식 오류)", objectType, id));
                        continue;
                    }

                    ImportResult importResult = importByTypeFromJsonForMigration(objectType, id, importJson, prjSeq);
                    boolean importSuccess = importResult.success;

                    if (!importSuccess) {
                        log.error("MIG :: Import 실패 - type: {}, id: {}, fileName: {}", objectType, id, fileName);
                        errors.add(String.format("Import 실패: %s/%s (fileName: %s)", objectType, id, fileName));
                    } else {
                        log.info("MIG :: Import 성공 - type: {}, id: {}, fileName: {}", objectType, id, fileName);
                        successes.add(String.format("%s/%s", objectType, id));
                    }

                } catch (Exception e) {
                    log.error("MIG :: JSON 항목 처리 중 오류 발생 - index: {}, error: {}", i, e.getMessage(), e);
                    errors.add(String.format("JSON 항목 처리 오류: index %d - %s", i, e.getMessage()));
                }
            }

            if (!successes.isEmpty()) {
                log.info("MIG :: 성공 목록:");
                successes.forEach(s -> log.info("MIG ::   ✓ {}", s));
            }

            if (!errors.isEmpty()) {
                log.error("MIG :: 실패 목록:");
                errors.forEach(e -> log.error("MIG ::   ✗ {}", e));
            }

            boolean result = errors.isEmpty();
            log.info("MIG :: 통합 JSON 파일에서 Import 완료 - jsonFilePath: {}, 성공: {}, 실패: {}",
                    jsonFilePath, successes.size(), errors.size());

            return result;

        } catch (IOException e) {
            log.error("MIG :: JSON 파일 읽기 실패 - jsonFilePath: {}, error: {}", jsonFilePath, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("MIG :: 통합 JSON 파일에서 Import 실패 - jsonFilePath: {}, error: {}", jsonFilePath, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 리소스 존재 여부 확인
     *
     * @param objectType 객체 타입
     * @param id         객체 ID
     * @return 존재하면 true, 없으면 false
     */
    private boolean checkIfExists(ObjectType objectType, String id) {
        try {
            switch (objectType) {
                case TOOL:
                    return toolMigService.checkIfExists(id);
                case MCP:
                    return mcpMigService.checkIfExists(id);
                case AGENT_GRAPH:
                    return graphMigService.checkIfExists(id);
                case GUARDRAILS:
                    return guardrailMigService.checkIfExists(id);
                case SAFETY_FILTER:
                    return safetyFilterMigService.checkIfExists(id);
                case PROMPT:
                    return inferencePromptMigService.checkIfExists(id);
                case FEW_SHOT:
                    return fewShotMigService.checkIfExists(id);
                case MODEL:
                    return modelMigService.checkIfExists(id);
                case KNOWLEDGE:
                    return externalRepoMigService.checkIfExists(id);
                case VECTOR_DB:
                    return vectorDbMigService.checkIfExists(id);
                case SERVING_MODEL:
                    return servingModelMigService.checkIfExists(id);
                default:
                    log.warn("MIG :: 존재 여부 확인을 지원하지 않는 타입 - type: {}, id: {}", objectType, id);
                    return false;
            }
        } catch (Throwable t) {
            // 최종 안전장치: 모든 예외(Error 포함)를 catch하여 Import가 중단되지 않도록 함
            // BusinessException, RuntimeException, Exception, Error 모두 잡아서 false 반환
            log.debug("MIG :: 존재 여부 확인 중 예외 발생 (없는 것으로 간주하고 Import 계속 진행) - type: {}, id: {}, error: {}",
                    objectType, id, t.getMessage());
            return false;
        }
    }

    /**
     * 마이그레이션 정보를 DB에 저장
     *
     * @param type      객체 타입
     * @param id        객체 ID
     * @param projectId 프로젝트 ID
     * @param filePath  파일 경로
     * @param targetDir 대상 디렉토리
     */
    @Transactional
    private void saveMigrationToDatabase(ObjectType type, String id, String projectId,
            String filePath, String fileNms,
            String projectName, String assetName) {
        try {
            log.info("MIG :: DB 저장 시작 - type: {}, id: {}, projectId: {}, filePath: {}",
                    type, id, projectId, filePath);

            // 현재 사용자 정보 가져오기
            String currentUser = getCurrentUser();

            // fileNms가 null이거나 비어있으면 기본값 설정
            if (fileNms == null || fileNms.trim().isEmpty()) {
                log.warn("MIG :: fileNms가 비어있습니다. 기본값으로 설정합니다.");
                fileNms = "";
            }

            log.info("MIG :: 파일 목록 - fileNms: [{}]", fileNms);

            // GPO_MIG_MAS 저장
            // public -> -999 변환 후 정수로 파싱
            String projectIdForDb = changePublicToM999(projectId);
            Integer prjSeqInt = Integer.parseInt(projectIdForDb);

            log.info("MIG :: === GPO_MIG_MAS 저장 전 (saveMigrationToDatabase) ===");
            log.info("MIG :: prjSeqInt: {}", prjSeqInt);
            log.info("MIG :: projectName (gpoPrjNm에 저장될 값): [{}]", projectName);
            log.info("MIG :: assetName (asstNm에 저장될 값): [{}]", assetName);

            // NOT NULL 제약조건을 위한 기본값 설정
            String finalAsstNm = (assetName != null && !assetName.trim().isEmpty())
                    ? assetName.trim()
                    : (type.name() + "_" + id); // 기본값: 타입_UUID
            String finalGpoPrjNm = (projectName != null && !projectName.trim().isEmpty())
                    ? projectName.trim()
                    : projectId; // 기본값: projectId
            String finalFileNms = (fileNms != null && !fileNms.trim().isEmpty())
                    ? fileNms.trim()
                    : ""; // 빈 문자열

            log.info("MIG :: 최종 저장값 - asstNm: [{}], gpoPrjNm: [{}], fileNms: [{}]",
                    finalAsstNm, finalGpoPrjNm, finalFileNms);

            GpoMigMas migMas = GpoMigMas.builder()
                    .uuid(id)
                    .asstG(type.name()) // 타입을 asstG로 사용
                    .asstNm(finalAsstNm)
                    .prjSeq(prjSeqInt)
                    .gpoPrjNm(finalGpoPrjNm)
                    .migFilePath(filePath)
                    .migFileNm(finalFileNms)
                    .pgmDescCtnt("")
                    .delYn(0) // 0: 정상
                    .fstCreatedAt(java.time.LocalDateTime.now())
                    .createdBy(currentUser)
                    .build();

            log.info("MIG :: === GPO_MIG_MAS 엔티티 생성 완료 (saveMigrationToDatabase) ===");
            log.info("MIG :: migMas.asstNm: [{}]", migMas.getAsstNm());
            log.info("MIG :: migMas.gpoPrjNm: [{}]", migMas.getGpoPrjNm());

            GpoMigMas savedMigMas;
            try {
                savedMigMas = repository.save(migMas);
                log.info("MIG :: === GPO_MIG_MAS 저장 성공 (saveMigrationToDatabase) ===");
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                log.error("MIG :: 원인 예외: {}", e.getCause() != null ? e.getCause().getMessage() : "null");
                if (e.getCause() != null && e.getCause().getCause() != null) {
                    log.error("MIG :: 근본 원인: {}", e.getCause().getCause().getMessage());
                }
                throw new RuntimeException("DB 저장 실패 (제약조건 위반): " + e.getMessage(), e);
            } catch (jakarta.persistence.PersistenceException e) {
                log.error("MIG :: 원인 예외: {}", e.getCause() != null ? e.getCause().getMessage() : "null");
                throw new RuntimeException("DB 저장 실패 (영속성 예외): " + e.getMessage(), e);
            } catch (Exception e) {
                log.error("MIG :: 원인 예외: {}", e.getCause() != null ? e.getCause().getMessage() : "null");
                throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
            }

            log.info("MIG :: === GPO_MIG_MAS 저장 완료 (saveMigrationToDatabase) ===");
            log.info("MIG :: savedMigMas.seqNo: {}", savedMigMas.getSeqNo());
            log.info("MIG :: savedMigMas.uuid: {}", savedMigMas.getUuid());
            log.info("MIG :: savedMigMas.asstNm: [{}]", savedMigMas.getAsstNm());
            log.info("MIG :: savedMigMas.gpoPrjNm: [{}]", savedMigMas.getGpoPrjNm());

            // assetName 기본값 설정 (GPO_MIG_ASST_MAP_MAS 저장용)
            String finalAssetNameForMap = (assetName != null && !assetName.trim().isEmpty())
                    ? assetName.trim()
                    : (type.name() + "_" + id);

            // fileNms에서 파일명 목록 추출 (쉼표로 구분)
            List<String> fileNames = new ArrayList<>();
            if (fileNms != null && !fileNms.trim().isEmpty()) {
                fileNames = java.util.Arrays.asList(fileNms.split(","));
            }

            // 각 파일별로 GPO_MIG_MAS_MAP_MAS 저장
            for (String fileName : fileNames) {
                try {
                    // 파일명 파싱: TYPE_ID.json -> TYPE, ID
                    Object[] parseResult = parseFileName(fileName.trim());
                    if (parseResult == null) {
                        log.warn("MIG :: 파일명 파싱 실패 - fileName: {}", fileName);
                        continue;
                    }

                    ObjectType asstType = (ObjectType) parseResult[0];
                    String asstUuid = (String) parseResult[1];

                    if (asstType == null || asstUuid == null || asstUuid.isEmpty()) {
                        log.warn("MIG :: 파일명에서 ObjectType을 찾을 수 없거나 id가 비어있음 - fileName: {}", fileName);
                        continue;
                    }

                    // 타입별 추출 필드 목록 가져오기
                    List<String> fields = TYPE_EXTRACT_FIELDS.get(asstType);

                    if (fields != null && !fields.isEmpty()) {
                        // filePath에서 디렉토리 경로 추출
                        Path filePathObj = Paths.get(filePath);
                        Path jsonFilePath = filePathObj.getParent().resolve(fileName.trim());

                        try {
                            if (Files.exists(jsonFilePath)) {
                                // 인코딩 자동 감지하여 파일 읽기
                                String fileContent = readJsonFileWithEncodingDetection(jsonFilePath);
                                JsonNode jsonNode = objectMapper.readTree(fileContent);

                                // 각 필드별로 레코드 생성
                                for (String fieldName : fields) {
                                    String devCtnt = "-";
                                    String prodCtnt = "-";

                                    // VECTOR_DB는 connection_info 내부 필드
                                    if (asstType == ObjectType.VECTOR_DB) {
                                        if (jsonNode.has("connection_info")
                                                && jsonNode.get("connection_info").isObject()) {
                                            JsonNode connInfoNode = jsonNode.get("connection_info");
                                            if (connInfoNode.has(fieldName) && !connInfoNode.get(fieldName).isNull()) {
                                                devCtnt = connInfoNode.get(fieldName).asText();
                                                prodCtnt = connInfoNode.get(fieldName).asText();
                                            }
                                        }
                                    } else {
                                        // KNOWLEDGE 등 최상위 레벨 필드
                                        if (jsonNode.has(fieldName) && !jsonNode.get(fieldName).isNull()) {
                                            devCtnt = jsonNode.get(fieldName).asText();
                                            prodCtnt = jsonNode.get(fieldName).asText();
                                        }
                                    }

                                    GpoMigAsstMapMas mapMas = GpoMigAsstMapMas.builder()
                                            .migSeqNo(savedMigMas.getSeqNo())
                                            .migUuid(id)
                                            .asstUuid(asstUuid)
                                            .asstG(asstType.name())
                                            .assetNm(finalAssetNameForMap)
                                            .migMapNm(fieldName) // 필드명 저장
                                            .dvlpDtlCtnt(devCtnt)
                                            .unyungDtlCtnt(prodCtnt)
                                            .build();

                                    mapMasRepository.save(mapMas);
                                    log.info("MIG :: GPO_MIG_MAS_MAP_MAS 저장 완료 - fileName: {}, asstUuid: {}, " +
                                                    "fieldName: {}, " +
                                                    "devCtnt: {}, prodCtnt: {}",
                                            fileName, asstUuid, fieldName,
                                            devCtnt != null && devCtnt.length() > 50 ? devCtnt.substring(0, 50) + ".." +
                                                    "." : devCtnt,
                                            prodCtnt != null && prodCtnt.length() > 50 ? prodCtnt.substring(0, 50) +
                                                    "..." : prodCtnt);
                                }
                            } else {
                                log.warn("MIG :: 파일을 찾을 수 없음 - jsonFilePath: {}", jsonFilePath);
                            }
                        } catch (NullPointerException e) {
                            log.error("MIG :: 파일 읽기 실패 (NullPointerException) - fileName: {}, error: {}", fileName,
                                    e.getMessage(), e);
                        } catch (Exception e) {
                            log.error("MIG :: 파일 읽기 실패 - fileName: {}, error: {}", fileName, e.getMessage(), e);
                        }
                    }

                } catch (NullPointerException e) {
                    log.error("MIG :: 파일별 MAP_MAS 저장 실패 (NullPointerException) - fileName: {}, error: {}", fileName,
                            e.getMessage(), e);
                } catch (Exception e) {
                    log.error("MIG :: 파일별 MAP_MAS 저장 실패 - fileName: {}, error: {}", fileName, e.getMessage(), e);
                }
            }

            log.info("MIG :: DB 저장 완료 - type: {}, id: {}, projectId: {}", type, id, projectId);

        } catch (NullPointerException e) {
            log.error("MIG :: DB 저장 중 오류 발생 (NullPointerException) - type: {}, id: {}, projectId: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("MIG :: DB 저장 중 오류 발생 - type: {}, id: {}, projectId: {}, error: {}",
                    type, id, projectId, e.getMessage(), e);
            throw new RuntimeException("DB 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 현재 사용자 정보 가져오기
     *
     * @return 현재 사용자명 또는 기본값
     */
    private String getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }
        } catch (NullPointerException e) {
            log.error("MIG :: 현재 사용자 정보 가져오기 실패 (NullPointerException) - error: {}", e.getMessage(), e);
            return "system"; // 기본값
        } catch (Exception e) {
            log.warn("MIG :: 현재 사용자 정보를 가져올 수 없습니다: {}", e.getMessage());
        }
        return "system"; // 기본값
    }

    /**
     * 폴더에서 마이그레이션 데이터 추출
     *
     * <p>
     * migration_temp/{projectName}/{type}/{id} 폴더의 JSON 파일들을 읽어서
     * 타입별로 필요한 정보를 추출하고, DB에서 매핑 정보를 조회하여 보완합니다.
     * </p>
     *
     * @param projectName 프로젝트 이름
     * @param type        객체 타입 (ObjectType enum)
     * @param id          객체 ID
     * @return 타입별로 추출한 정보를 담은 Map (타입별로 리스트 형태)
     */
    @Override
    public Map<String, List<Map<String, Object>>> extractMigrationDataFromFolder(
            String projectName, ObjectType type, String id) {

        log.info("MIG :: 마이그레이션 데이터 추출 시작 - projectName: {}, type: {}, id: {}", projectName, type, id);

        Map<String, List<Map<String, Object>>> result = new java.util.HashMap<>();

        try {
            // -999 프로젝트 ID는 public 프로젝트로 처리
            if (projectName.equals("-999")) {
                log.info("MIG :: 프로젝트 ID가 -999입니다.");
                projectName = "public";
            }

            // PROJECT 타입인 경우: project_migration_data.json에서 데이터 추출
            if (type == ObjectType.PROJECT) {
                log.info("MIG :: PROJECT 타입 - project_migration_data.json에서 데이터 추출");

                String projectExportFilePath = Paths.get(migrationBaseDir, "project_migration_data.json").toString();
                Path projectFile = Paths.get(projectExportFilePath);

                if (!Files.exists(projectFile)) {
                    log.warn("MIG :: project_migration_data.json 파일이 존재하지 않습니다 - path: {}", projectExportFilePath);
                    return result;
                }

                try {
                    String fileContent = readJsonFileWithEncodingDetection(projectFile);
                    JsonNode jsonArray = objectMapper.readTree(fileContent);

                    if (jsonArray.isArray()) {
                        List<Map<String, Object>> projectDataList = new ArrayList<>();

                        for (JsonNode projectNode : jsonArray) {
                            // id(projectName)와 일치하는 프로젝트만 추출
                            String prjSeqStr = projectNode.has("prjSeq") ? projectNode.get("prjSeq").asText() : null;

                            // projectName이 public이면 -999로 비교
                            String compareId = "public".equals(projectName) ? "-999" : projectName;

                            if (prjSeqStr != null && prjSeqStr.equals(compareId)) {
                                Map<String, Object> projectData = objectMapper.convertValue(projectNode,
                                        new TypeReference<Map<String, Object>>() {});
                                projectData.put("filePath", projectExportFilePath);
                                projectDataList.add(projectData);
                                log.info("MIG :: PROJECT 데이터 추출 완료 - prjSeq: {}", prjSeqStr);
                            }
                        }

                        if (!projectDataList.isEmpty()) {
                            result.put("PROJECT", projectDataList);
                        }
                    }

                    log.info("MIG :: PROJECT 마이그레이션 데이터 추출 완료 - 추출된 프로젝트 수: {}",
                            result.containsKey("PROJECT") ? result.get("PROJECT").size() : 0);

                } catch (IOException e) {
                    log.error("MIG :: project_migration_data.json 파일 읽기 실패 - error: {}", e.getMessage(), e);
                }

                return result;
            }

            // AGENT_APP 타입인 경우 AGENT_GRAPH 폴더도 확인 (리니지에서 source_type이 AGENT_GRAPH로 나올 수 있음)
            ObjectType folderType = type;
            if (type == ObjectType.AGENT_APP) {
                // 먼저 AGENT_APP 폴더 확인
                String agentAppPath = buildMigrationTempPath(projectName, ObjectType.AGENT_APP, id);
                Path agentAppFolder = Paths.get(agentAppPath);

                // AGENT_APP 폴더가 없으면 AGENT_GRAPH 폴더 확인
                if (!Files.exists(agentAppFolder) || !Files.isDirectory(agentAppFolder)) {
                    log.info("MIG :: AGENT_APP 폴더가 없습니다. AGENT_GRAPH 폴더 확인 - path: {}", agentAppPath);
                    String agentGraphPath = buildMigrationTempPath(projectName, ObjectType.AGENT_GRAPH, id);
                    Path agentGraphFolder = Paths.get(agentGraphPath);

                    if (Files.exists(agentGraphFolder) && Files.isDirectory(agentGraphFolder)) {
                        log.info("MIG :: AGENT_GRAPH 폴더를 사용합니다 - path: {}", agentGraphPath);
                        folderType = ObjectType.AGENT_GRAPH;
                    }
                }
            }

            // 폴더 경로: migration_temp/{projectName}/{type}/{id}
            String folderPath = buildMigrationTempPath(projectName, folderType, id);

            Path folder = Paths.get(folderPath);

            if (!Files.exists(folder) || !Files.isDirectory(folder)) {
                log.warn("MIG :: 폴더가 존재하지 않거나 디렉토리가 아닙니다 - folderPath: {}", folderPath);
                return result;
            }

            // 폴더 내 모든 .json 파일 읽기
            List<Path> jsonFiles = Files.list(folder)
                    .filter(path -> path.toString().endsWith(".json"))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            log.info("MIG :: JSON 파일 개수: {}", jsonFiles.size());
            jsonFiles.forEach(file -> log.info("MIG :: 발견된 파일: {}", file.getFileName()));

            if (jsonFiles.isEmpty()) {
                log.warn("MIG :: 폴더에 JSON 파일이 없습니다 - folderPath: {}", folderPath);
                return result;
            }

            // 타입별로 그룹화하여 처리
            Map<ObjectType, List<FileData>> filesByType = new java.util.HashMap<>();

            for (Path jsonFile : jsonFiles) {
                String fileName = jsonFile.getFileName().toString();
                log.debug("MIG :: 파일 처리 시작 - fileName: {}", fileName);

                try {
                    // 파일명 파싱: num_TYPE_ID.json 또는 TYPE_ID.json → TYPE, ID
                    // parseFileName 메서드를 사용하여 depth 접두사도 처리
                    Object[] parseResult = parseFileName(fileName);
                    if (parseResult == null) {
                        log.warn("MIG :: 파일명 파싱 실패 - fileName: {}", fileName);
                        continue;
                    }

                    ObjectType fileType = (ObjectType) parseResult[0];
                    String fileId = (String) parseResult[1];

                    if (fileType == null || fileId == null || fileId.isEmpty()) {
                        log.warn("MIG :: 파일명에서 ObjectType을 찾을 수 없거나 id가 비어있음 - fileName: {}", fileName);
                        continue;
                    }

                    log.debug("MIG :: 파일명 파싱 - type: {}, id: {}, fileName: {}", fileType, fileId, fileName);

                    // 파일 내용 읽기 (인코딩 자동 감지)
                    try {
                        String fileContent = readJsonFileWithEncodingDetection(jsonFile);
                        JsonNode jsonNode = objectMapper.readTree(fileContent);

                        filesByType.computeIfAbsent(fileType, k -> new ArrayList<>())
                                .add(new FileData(fileType, fileId, jsonNode, fileName));

                        log.debug("MIG :: 파일 데이터 추가 완료 - type: {}, id: {}, fileName: {}", fileType, fileId, fileName);
                    } catch (IOException e) {
                        log.error("MIG :: 파일 읽기 실패 - fileName: {}, error: {}", fileName, e.getMessage(), e);
                    } catch (NullPointerException e) {
                        log.error("MIG :: 파일 처리 중 오류 발생 (NullPointerException) - fileName: {}, error: {}", fileName,
                                e.getMessage(), e);
                    } catch (Exception e) {
                        log.error("MIG :: 파일 처리 중 오류 발생 - fileName: {}, error: {}", fileName, e.getMessage(), e);
                    }
                } catch (NullPointerException e) {
                    log.error("MIG :: 파일 처리 중 오류 발생 (NullPointerException) - fileName: {}, error: {}", fileName,
                            e.getMessage(), e);
                } catch (Exception e) {
                    log.error("MIG :: 파일 처리 중 오류 발생 - fileName: {}, error: {}", fileName, e.getMessage(), e);
                }
            }

            log.info("MIG :: 타입별 그룹화 완료 - 그룹 수: {}", filesByType.size());
            filesByType.forEach((fileTypeKey, fileList) -> log.info("MIG :: 타입별 파일 목록 - type: {}, 파일 개수: {}",
                    fileTypeKey,
                    fileList.size()));

            // AGENT_APP 타입인 경우 필터링: AGENT_APP, GRAPH, PROMPT, FEW_SHOT, TOOL, MCP, SUB_AGENT만 추출
            if (type == ObjectType.AGENT_APP) {
                log.info("MIG :: AGENT_APP 타입 필터링 적용 - AGENT_APP, GRAPH, PROMPT, FEW_SHOT, TOOL, MCP, SUB_AGENT만 추출");
                Set<ObjectType> allowedTypes = new HashSet<>();
                allowedTypes.add(ObjectType.AGENT_APP); // AGENT_APP 자체도 포함
                allowedTypes.add(ObjectType.AGENT_GRAPH); // GRAPH
                allowedTypes.add(ObjectType.PROMPT);
                allowedTypes.add(ObjectType.FEW_SHOT);
                allowedTypes.add(ObjectType.TOOL);
                allowedTypes.add(ObjectType.MCP);
                allowedTypes.add(ObjectType.SUB_AGENT);

                // 허용된 타입만 남기기
                filesByType.entrySet().removeIf(entry -> !allowedTypes.contains(entry.getKey()));

                log.info("MIG :: 필터링 후 타입별 그룹 수: {}", filesByType.size());
                filesByType.forEach((fileTypeKey, fileList) -> log.info("MIG :: 필터링 후 타입별 파일 목록 - type: {}, 파일 개수: {}",
                        fileTypeKey, fileList.size()));
            }

            // 타입별로 데이터 추출
            log.info("MIG :: 발견된 파일 타입 수: {}", filesByType.size());
            for (Map.Entry<ObjectType, List<FileData>> entry : filesByType.entrySet()) {
                ObjectType fileType = entry.getKey();
                List<FileData> files = entry.getValue();

                log.info("MIG :: 타입별 파일 처리 시작 - type: {}, 파일 개수: {}", fileType, files.size());

                String typeKey = fileType.name();
                List<Map<String, Object>> typeDataList = new ArrayList<>();

                for (FileData fileData : files) {
                    try {
                        log.info("MIG :: extractDataByType 호출 전 - type: {}, id: {}, fileName: {}",
                                fileType, fileData.id, fileData.fileName);
                        Map<String, Object> extractedData = extractDataByType(fileType, fileData);
                        log.info("MIG :: extractDataByType 호출 후 - type: {}, id: {}, extractedData: {}",
                                fileType, fileData.id, extractedData);
                        if (extractedData != null && !extractedData.isEmpty()) {
                            typeDataList.add(extractedData);
                            log.info("MIG :: 데이터 추출 성공 - type: {}, id: {}, fileName: {}, extractedData keys: {}",
                                    fileType, fileData.id, fileData.fileName, extractedData.keySet());
                        } else {
                            log.warn("MIG :: 추출된 데이터가 비어있음 - type: {}, id: {}, fileName: {}",
                                    fileType, fileData.id, fileData.fileName);
                        }
                    } catch (NullPointerException e) {
                        log.error("MIG :: 데이터 추출 실패 (NullPointerException) - type: {}, id: {}, fileName: {}, error: {}",
                                fileType, fileData.id, fileData.fileName, e.getMessage(), e);
                    } catch (Exception e) {
                        log.error("MIG :: 데이터 추출 실패 - type: {}, id: {}, fileName: {}, error: {}",
                                fileType, fileData.id, fileData.fileName, e.getMessage(), e);
                    }
                }

                if (!typeDataList.isEmpty()) {
                    result.put(typeKey, typeDataList);
                    log.info("MIG :: 타입별 데이터 추가 완료 - type: {}, 데이터 개수: {}", typeKey, typeDataList.size());
                } else {
                    log.warn("MIG :: 타입별 데이터가 비어있어 결과에 추가하지 않음 - type: {}", typeKey);
                }
            }

            log.info("MIG :: 마이그레이션 데이터 추출 완료 - 추출된 타입 수: {}", result.size());

        } catch (NullPointerException e) {
            log.error("MIG :: 마이그레이션 데이터 추출 실패 (NullPointerException) - projectName: {}, type: {}, id: {}, error: {}",
                    projectName, type, id, e.getMessage(), e);
        } catch (Exception e) {
            log.error("MIG :: 마이그레이션 데이터 추출 실패 - projectName: {}, type: {}, id: {}, error: {}",
                    projectName, type, id, e.getMessage(), e);
            // 예외 발생 시에도 빈 Map 반환하여 500 에러 방지
            // 컨트롤러에서 빈 결과를 받아서 처리할 수 있도록 함
        }

        return result;
    }

    /**
     * 파일 데이터를 담는 내부 클래스
     */
    private static class FileData {

        ObjectType type;
        String id;
        JsonNode jsonNode;
        String fileName;

        FileData(ObjectType type, String id, JsonNode jsonNode, String fileName) {
            this.type = type;
            this.id = id;
            this.jsonNode = jsonNode;
            this.fileName = fileName;
        }

    }

    /**
     * 타입별로 데이터 추출
     *
     * @param type     객체 타입
     * @param fileData 파일 데이터
     * @return 추출된 데이터 Map
     */
    private Map<String, Object> extractDataByType(ObjectType type, FileData fileData) {
        List<String> fields = TYPE_EXTRACT_FIELDS.get(type);
        log.info("MIG :: 타입별 추출 필드 목록 - type: {}, fields: {}", type, fields);

        Map<String, Object> result = new java.util.HashMap<>();

        try {

            result.put("type", type.name());
            result.put("id", fileData.id);

            // 필드가 정의되지 않은 타입은 기본 정보만 반환
            if (fields == null || fields.isEmpty()) {
                log.debug("MIG :: 타입별 추출 필드가 정의되지 않음 - type: {}, id: {} (기본 정보만 반환)", type, fileData.id);
                return result;
            }

            JsonNode jsonNode = fileData.jsonNode;

            // getValueFromDb를 함수형 인터페이스로 전달
            java.util.function.Function<String, String> getValueFromDbFunc = field -> getValueFromDb(fileData.id,
                    field);

            // 타입별 서비스에서 필드 추출
            Map<String, Object> extractedFields = null;

            log.info("MIG :: extractDataByType switch 진입 - type: {}, id: {}", type, fileData.id);
            switch (type) {
                case KNOWLEDGE:
                    extractedFields = externalRepoMigService.extractFields(jsonNode, fileData.id, fields,
                            getValueFromDbFunc);
                    break;
                case VECTOR_DB:
                    extractedFields = vectorDbMigService.extractFields(jsonNode, fileData.id, fields,
                            getValueFromDbFunc);
                    break;
                case MCP:
                    extractedFields = mcpMigService.extractFields(jsonNode, fileData.id, fields, getValueFromDbFunc);
                    break;
                case AGENT_GRAPH:
                    extractedFields = graphMigService.extractFields(jsonNode, fileData.id, fields, getValueFromDbFunc);
                    break;
                case SERVING_MODEL:
                    extractedFields = servingModelMigService.extractFields(jsonNode, fileData.id, fields,
                            getValueFromDbFunc);
                    break;
                case MODEL:
                    extractedFields = modelMigService.extractFields(jsonNode, fileData.id, fields, getValueFromDbFunc);
                    break;
                case TOOL:
                    extractedFields = toolMigService.extractFields(jsonNode, fileData.id, fields, getValueFromDbFunc);
                    break;
                case AGENT_APP:
                    extractedFields = agentAppMigService.extractFields(jsonNode, fileData.id, fields,
                            getValueFromDbFunc);
                    break;
                default:
                    // 기본 처리: 각 필드를 파일에서 읽고, 없으면 DB에서 조회
                    extractedFields = new java.util.HashMap<>();
                    for (String field : fields) {
                        String fileValue = jsonNode.has(field) && !jsonNode.get(field).isNull()
                                ? jsonNode.get(field).asText()
                                : null;
                        String finalValue = (fileValue != null && !fileValue.isEmpty())
                                ? fileValue
                                : getValueFromDbFunc.apply(field);
                        extractedFields.put(field, finalValue != null ? finalValue : "");
                    }
                    break;
            }

            if (extractedFields != null) {
                result.putAll(extractedFields);
            }

        } catch (NullPointerException e) {
            log.error("MIG :: 데이터 추출 실패 (NullPointerException) - type: {}, id: {}, error: {}", type, fileData.id,
                    e.getMessage(), e);
        } catch (Exception e) {
            log.error("MIG :: 데이터 추출 실패 - type: {}, id: {}, error: {}", type, fileData.id, e.getMessage(), e);
        }
        log.info("MIG :: 데이터 추출 완료 - type: {}, id: {}, result: {}", type, fileData.id, result);
        return result;
    }

    /**
     * DB에서 값 조회
     *
     * <p>
     * GPO_MIG_MAS_MAP_MAS에서 asst_uuid = id인 레코드를 조회하여
     * 해당 필드의 값을 반환합니다.
     * </p>
     *
     * @param asstUuid  어시스트 UUID (파일의 id)
     * @param fieldName 필드명 (script, index_name, endpoint, host 등)
     * @return DB에서 조회한 값, 없으면 null
     */
    private String getValueFromDb(String asstUuid, String fieldName) {
        try {
            List<GpoMigAsstMapMas> mapMasList;
            try {
                mapMasList = mapMasRepository.findByAsstUuid(asstUuid);
            } catch (NullPointerException e) {
                log.warn("MIG :: DB 조회 중 예외 발생 (NullPointerException, 무시하고 계속 진행) - asstUuid: {}, fieldName: {}, " +
                                "error: {}",
                        asstUuid, fieldName, e.getMessage());
                return null;
            } catch (Exception e) {
                log.warn("MIG :: DB 조회 중 예외 발생 (무시하고 계속 진행) - asstUuid: {}, fieldName: {}, error: {}",
                        asstUuid, fieldName, e.getMessage());
                return null;
            }

            if (mapMasList == null || mapMasList.isEmpty()) {
                log.debug("MIG :: DB에서 매핑 정보를 찾을 수 없음 - asstUuid: {}, fieldName: {}", asstUuid, fieldName);
                return null;
            }

            // 첫 번째 레코드 사용 (여러 개가 있을 수 있음)
            GpoMigAsstMapMas mapMas = mapMasList.get(0);

            // 필드명에 따라 적절한 컬럼에서 값 가져오기
            // DVLP_DTL_CTNT 또는 UNYOUNG_DTL_CTNT에서 JSON 파싱하여 필드값 추출
            // 또는 특정 필드명 매핑 로직 구현

            // 간단한 구현: DVLP_DTL_CTNT를 JSON으로 파싱하여 필드값 추출 시도
            if (mapMas.getDvlpDtlCtnt() != null && !mapMas.getDvlpDtlCtnt().isEmpty()) {
                try {
                    JsonNode devCtntNode = objectMapper.readTree(mapMas.getDvlpDtlCtnt());
                    if (devCtntNode.has(fieldName) && !devCtntNode.get(fieldName).isNull()) {
                        return devCtntNode.get(fieldName).asText();
                    }
                } catch (NullPointerException e) {
                    log.debug("MIG :: DVLP_DTL_CTNT JSON 파싱 실패 (NullPointerException) - asstUuid: {}, fieldName: {}",
                            asstUuid, fieldName);
                } catch (Exception e) {
                    log.debug("MIG :: DVLP_DTL_CTNT JSON 파싱 실패 - asstUuid: {}, fieldName: {}", asstUuid, fieldName);
                }
            }

            // UNYOUNG_DTL_CTNT도 시도
            if (mapMas.getUnyungDtlCtnt() != null && !mapMas.getUnyungDtlCtnt().isEmpty()) {
                try {
                    JsonNode prodCtntNode = objectMapper.readTree(mapMas.getUnyungDtlCtnt());
                    if (prodCtntNode.has(fieldName) && !prodCtntNode.get(fieldName).isNull()) {
                        return prodCtntNode.get(fieldName).asText();
                    }
                } catch (NullPointerException e) {
                    log.debug("MIG :: UNYOUNG_DTL_CTNT JSON 파싱 실패 (NullPointerException) - asstUuid: {}, fieldName: {}",
                            asstUuid, fieldName);
                } catch (Exception e) {
                    log.debug("MIG :: UNYOUNG_DTL_CTNT JSON 파싱 실패 - asstUuid: {}, fieldName: {}", asstUuid, fieldName);
                }
            }

            log.debug("MIG :: DB에서 필드값을 찾을 수 없음 - asstUuid: {}, fieldName: {}", asstUuid, fieldName);
            return null;

        } catch (NullPointerException e) {
            log.error("MIG :: DB 값 조회 실패 (NullPointerException) - asstUuid: {}, fieldName: {}, error: {}",
                    asstUuid, fieldName, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("MIG :: DB 값 조회 실패 - asstUuid: {}, fieldName: {}, error: {}",
                    asstUuid, fieldName, e.getMessage(), e);
            return null;
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
     * @param projectId 프로젝트 ID
     * @param type      객체 타입 (ObjectType enum)
     * @param id        객체 ID
     * @return Import 및 후처리 결과 Map
     */
    @Override
    public Map<String, Object> importAndMigration(String projectId, ObjectType type, String id) {
        log.info("MIG :: Import 및 선처리/후처리 시작 - projectId: {}, type: {}, id: {}", projectId, type, id);

        Map<String, Object> result = new java.util.HashMap<>();

        try {
            // migration 폴더 경로: migration/{projectId}/{type}/{id}
            String folderPath = buildMigrationPath(projectId, type, id);

            // 1. 타입별 선처리 함수 실행 (Import 전)
            Map<String, Object> preprocessResult = new java.util.HashMap<>();
            boolean preprocessSuccess = false;
            String preprocessMessage = "";

            log.info("MIG :: 선처리 시작 - type: {}, projectId: {}, id: {}", type, projectId, id);
            switch (type) {
                default:
                    preprocessSuccess = true; // 선처리가 없으면 성공으로 간주
                    preprocessMessage = "선처리 함수가 정의되지 않은 타입입니다.";
                    break;
            }

            preprocessResult.put("success", preprocessSuccess);
            preprocessResult.put("message", preprocessMessage);
            log.info("MIG :: 선처리 완료 - type: {}, success: {}, message: {}", type, preprocessSuccess, preprocessMessage);

            // 2. PROJECT Import 수행 (선처리 후, 자산 Import 전)
            Map<String, Object> projectImportResult = new java.util.HashMap<>();
            boolean projectImportSuccess = false;
            String projectImportMessage = "";

            // public -> -999
            projectId = changePublicToM999(projectId);

            try {
                log.info("MIG :: PROJECT Import 시작 - projectId: {}", projectId);
                projectMigService.importProjects();
                projectImportSuccess = true;
                projectImportMessage = "PROJECT Import 성공";
                log.info("MIG :: PROJECT Import 완료 - projectId: {}", projectId);
            } catch (Exception e) {
                log.warn("MIG :: PROJECT Import 실패 (계속 진행) - projectId: {}, error: {}", projectId, e.getMessage(), e);
                projectImportSuccess = false;
                projectImportMessage = "PROJECT Import 실패: " + e.getMessage();
                // PROJECT Import 실패해도 자산 Import는 계속 진행
            }

            projectImportResult.put("success", projectImportSuccess);
            projectImportResult.put("message", projectImportMessage);
            log.info("MIG :: PROJECT Import 완료 - projectId: {}, success: {}, message: {}",
                    projectId, projectImportSuccess, projectImportMessage);

            // 3. 자산 Import 수행 (있으면 delete & import)
            // 통합 JSON 파일에서 Import 수행
            String jsonFileName = String.format("%s.json", id);
            Path jsonFilePath = Paths.get(folderPath).getParent().resolve(jsonFileName);

            if (!Files.exists(jsonFilePath) || !Files.isRegularFile(jsonFilePath)) {
                log.error("MIG :: 통합 JSON 파일이 없습니다 - jsonFilePath: {}", jsonFilePath.toAbsolutePath());
                result.put("importSuccess", false);
                result.put("error", "통합 JSON 파일이 없습니다: " + jsonFilePath.toAbsolutePath());
                return result;
            }

            log.info("MIG :: 통합 JSON 파일 발견 - jsonFilePath: {}", jsonFilePath.toAbsolutePath());
            log.info("MIG :: 통합 JSON 파일에서 Import 시작");
            boolean importSuccess = importFromJsonFile(projectId, jsonFilePath.toAbsolutePath().toString());

            if (!importSuccess) {
                log.error("MIG :: Import 실패 - folderPath: {}", folderPath);
                result.put("importSuccess", false);
                result.put("error", "Import에 실패했습니다.");
                return result;
            }

            log.info("MIG :: Import 완료 - folderPath: {}", folderPath);

            // 4. 타입별 후처리 함수 실행 (Import 후)
            Map<String, Object> postprocessResult = new java.util.HashMap<>();
            boolean postprocessSuccess = false;
            String postprocessMessage = "";

            try {
                log.info("MIG :: 후처리 시작 - type: {}, projectId: {}, id: {}", type, projectId, id);
                switch (type) {
                    case KNOWLEDGE:
                        log.info("MIG :: 지식 후처리 (구현 필요)");
                        postprocessSuccess = true;
                        postprocessMessage = "지식 후처리 (구현 필요)";
                        break;
                    default:
                        log.info("MIG :: 후처리 함수가 정의되지 않은 타입 - type: {}", type);
                        postprocessSuccess = true; // 후처리가 없으면 성공으로 간주
                        postprocessMessage = "후처리 함수가 정의되지 않은 타입입니다.";
                        break;
                }

                postprocessResult.put("success", postprocessSuccess);
                postprocessResult.put("message", postprocessMessage);
                log.info("MIG :: 후처리 완료 - type: {}, success: {}, message: {}", type, postprocessSuccess,
                        postprocessMessage);

            } catch (NullPointerException e) {
                log.error("MIG :: 후처리 중 오류 발생 (NullPointerException) - type: {}, projectId: {}, id: {}, error: {}",
                        type, projectId, id, e.getMessage(), e);
                postprocessResult.put("success", false);
                postprocessResult.put("message", "후처리 중 오류 발생: " + e.getMessage());
            } catch (Exception e) {
                log.error("MIG :: 후처리 중 오류 발생 - type: {}, projectId: {}, id: {}, error: {}",
                        type, projectId, id, e.getMessage(), e);
                postprocessResult.put("success", false);
                postprocessResult.put("message", "후처리 중 오류 발생: " + e.getMessage());
            }

            result.put("preprocess", preprocessResult);
            result.put("projectImport", projectImportResult);
            result.put("importSuccess", importSuccess);
            result.put("postprocess", postprocessResult);
            result.put("folderPath", folderPath);

            log.info("MIG :: Import 및 선처리/후처리 완료 - projectId: {}, type: {}, id: {}, preprocessSuccess: {}, " +
                            "projectImportSuccess: {}, importSuccess: {}, postprocessSuccess: {}",
                    projectId, type, id, preprocessSuccess, projectImportSuccess, importSuccess, postprocessSuccess);

            return result;

        } catch (NullPointerException e) {
            log.error("MIG :: Import 및 후처리 중 예외 발생 (NullPointerException) - projectId: {}, type: {}, id: {}, error: {}",
                    projectId, type, id, e.getMessage(), e);
            result.put("importSuccess", false);
            result.put("error", "Import 및 후처리 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        } catch (Exception e) {
            log.error("MIG :: Import 및 후처리 중 예외 발생 - projectId: {}, type: {}, id: {}, error: {}",
                    projectId, type, id, e.getMessage(), e);
            result.put("importSuccess", false);
            result.put("error", "Import 및 후처리 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
    }

    /**
     * 운영 이행 관리 조회 (페이지네이션)
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MigMasRes> searchMigMas(
            MigMasSearchReq request) {

        try {
            // 페이지 크기 검증 (12, 36, 60만 허용)
            int size = request.getSize();
            if (size != 12 && size != 36 && size != 60) {
                log.warn("MIG :: 잘못된 페이지 크기: {}. 기본값 12로 설정", size);
                size = 12;
            }

            // 페이지 번호는 1부터 시작하므로 0부터 시작하는 Pageable로 변환
            int page = Math.max(0, request.getPage() - 1);

            // Native Query에서는 Sort를 사용하지 않음 (쿼리 내에서 ORDER BY 사용)
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                    page,
                    size);

            // LocalDateTime을 PostgreSQL TIMESTAMP 형식으로 변환 ('yyyy-MM-dd HH:mm:ss')
            String startDateStr = null;
            String endDateStr = null;

            if (request.getStartDate() != null) {
                startDateStr = request.getStartDate().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            if (request.getEndDate() != null) {
                endDateStr = request.getEndDate().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            // 빈 문자열 처리
            String asstNm = (request.getAsstNm() != null && !request.getAsstNm().trim().isEmpty())
                    ? request.getAsstNm().trim()
                    : null;
            String asstG = (request.getAsstG() != null && !request.getAsstG().trim().isEmpty())
                    ? request.getAsstG().trim()
                    : null;

            log.info("MIG :: 쿼리 파라미터 - startDateStr: [{}], endDateStr: [{}], asstNm: [{}], asstG: [{}]",
                    startDateStr, endDateStr, asstNm, asstG);

            // Repository 조회
            Page<GpoMigMas> pageResult = repository.findMigMasWithFilters(
                    startDateStr,
                    endDateStr,
                    asstNm,
                    asstG,
                    request.getPrjSeq(),
                    pageable);

            // Entity를 DTO로 변환
            List<MigMasRes> content = pageResult.getContent().stream()
                    .map(entity -> MigMasRes.builder()
                            .seqNo(entity.getSeqNo())
                            .uuid(entity.getUuid())
                            .asstG(entity.getAsstG())
                            .asstNm(entity.getAsstNm())
                            .prjSeq(entity.getPrjSeq())
                            .gpoPrjNm(entity.getGpoPrjNm())
                            .filePath(entity.getMigFilePath())
                            .fileNms(entity.getMigFileNm())
                            .pgmDescCtnt(entity.getPgmDescCtnt())
                            .delYn(entity.getDelYn())
                            .fstCreatedAt(entity.getFstCreatedAt())
                            .createdBy(entity.getCreatedBy())
                            .build())
                    .collect(java.util.stream.Collectors.toList());

            log.info("MIG :: === 운영 이행 관리 조회 완료 ===");
            log.info("MIG :: totalElements: {}, totalPages: {}", pageResult.getTotalElements(),
                    pageResult.getTotalPages());

            // PageResponse로 변환
            return PageResponse.from(
                    new PageImpl<>(content, pageable, pageResult.getTotalElements()));
        } catch (NullPointerException e) {
            log.error("MIG :: 운영 이행 관리 조회 실패 (NullPointerException) - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "운영 이행 관리 조회 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("MIG :: 운영 이행 관리 조회 실패 - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "운영 이행 관리 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 운영 이행 관리 조회 (조인 결과)
     */
    @Override
    @Transactional(readOnly = true)
    public List<MigMasWithMapRes> findAllMigMasWithMap(MigMasAsstSearchReq request) {

        log.info("MIG :: === 운영 이행 관리 조회 (조인) 시작 ===");

        try {
            // 빈 문자열 처리
            String uuid = (request.getUuid() != null && !request.getUuid().trim().isEmpty())
                    ? request.getUuid().trim()
                    : null;
            String asstG = (request.getAsstG() != null && !request.getAsstG().trim().isEmpty())
                    ? request.getAsstG().trim()
                    : null;

            log.info("MIG :: 쿼리 파라미터 - sequence: [{}], uuid: [{}], asstG: [{}]", request.getSequence(), uuid, asstG);

            // Repository 조회
            List<Object[]> results = repository.findAllMigMasWithMap(
                    request.getSequence(),
                    uuid,
                    asstG);

            // Object[] 배열을 DTO로 변환
            List<MigMasWithMapRes> content = results.stream()
                    .map(row -> {
                        try {
                            return MigMasWithMapRes.builder()
                                    .masSeqNo(row[0] != null ? ((Number) row[0]).longValue() : null)
                                    .masUuid(row[1] != null ? (String) row[1] : null)
                                    .masAsstG(row[2] != null ? (String) row[2] : null)
                                    .masAsstNm(row[3] != null ? (String) row[3] : null)
                                    .masPrjSeq(row[4] != null ? ((Number) row[4]).intValue() : null)
                                    .masGpoPrjNm(row[5] != null ? (String) row[5] : null)
                                    .masMigFilePath(row[6] != null ? (String) row[6] : null)
                                    .masMigFileNm(row[7] != null ? (String) row[7] : null)
                                    .masPgmDescCtnt(row[8] != null ? (String) row[8] : null)
                                    .masDelYn(row[9] != null ? ((Number) row[9]).intValue() : null)
                                    .masFstCreatedAt(
                                            row[10] != null
                                                    ? (row[10] instanceof java.sql.Timestamp
                                                    ? ((java.sql.Timestamp) row[10]).toLocalDateTime()
                                                    : (java.time.LocalDateTime) row[10])
                                                    : null)
                                    .masCreatedBy(row[11] != null ? (String) row[11] : null)
                                    .mapSeqNo(row[12] != null ? ((Number) row[12]).longValue() : null)
                                    .mapMigSeqNo(row[13] != null ? ((Number) row[13]).longValue() : null)
                                    .mapMigUuid(row[14] != null ? (String) row[14] : null)
                                    .mapAsstUuid(row[15] != null ? (String) row[15] : null)
                                    .mapAsstG(row[16] != null ? (String) row[16] : null)
                                    .mapAssetNm(row[17] != null ? (String) row[17] : null)
                                    .mapMigMapNm(row[18] != null ? (String) row[18] : null)
                                    .mapDvlpDtlCtnt(row[19] != null ? (String) row[19] : null)
                                    .mapUnyungDtlCtnt(row[20] != null ? (String) row[20] : null)
                                    .build();
                        } catch (NullPointerException e) {
                            log.error("MIG :: DTO 변환 실패 (NullPointerException) - row: {}, error: {}", row,
                                    e.getMessage(), e);
                            return null;
                        } catch (Exception e) {
                            log.error("MIG :: DTO 변환 실패 - row: {}, error: {}", row, e.getMessage(), e);
                            return null;
                        }
                    })
                    .filter(result -> result != null) // null 제거
                    .collect(java.util.stream.Collectors.toList());
            log.info("MIG :: === 운영 이행 관리 조회 (조인) 완료 ===");
            log.info("MIG :: totalCount: {}", content.size());

            return content;
        } catch (NullPointerException e) {
            log.error("MIG :: 운영 이행 관리 조회 (조인) 실패 (NullPointerException) - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "운영 이행 관리 조회 (조인) 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("MIG :: 운영 이행 관리 조회 (조인) 실패 - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "운영 이행 관리 조회 (조인) 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ==================== 파일/폴더 권한 유틸리티 메서드 ====================

    /**
     * 현재 OS가 Linux인지 확인
     *
     * @return Linux이면 true, 그 외 false
     */
    private boolean isLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux") || osName.contains("unix");
    }

    /**
     * 디렉토리 생성 후 Linux 환경에서 775 권한 설정
     *
     * @param path 생성할 디렉토리 경로
     * @throws IOException 디렉토리 생성 또는 권한 설정 실패 시
     */
    private void createDirectoriesWithPermission(Path path) throws IOException {
        Files.createDirectories(path);
        if (isLinux()) {
            try {
                // 775 권한: rwxrwxr-x
                Set<PosixFilePermission> dirPermissions = PosixFilePermissions.fromString("rwxrwxr-x");
                Files.setPosixFilePermissions(path, dirPermissions);
                log.debug("MIG :: 디렉토리 권한 설정 완료 (775) - path: {}", path);
            } catch (UnsupportedOperationException e) {
                log.debug("MIG :: POSIX 권한 미지원 환경 - path: {}", path);
            }
        }
    }

    /**
     * 파일에 Linux 환경에서 777 권한 설정
     *
     * @param path 권한을 설정할 파일 경로
     */
    private void setFilePermission(Path path) {
        if (isLinux()) {
            try {
                // 777 권한: rwxrwxrwx
                Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString("rwxrwxrwx");
                Files.setPosixFilePermissions(path, filePermissions);
                log.debug("MIG :: 파일 권한 설정 완료 (777) - path: {}", path);
            } catch (UnsupportedOperationException e) {
                log.debug("MIG :: POSIX 권한 미지원 환경 - path: {}", path);
            } catch (IOException e) {
                log.warn("MIG :: 파일 권한 설정 실패 - path: {}, error: {}", path, e.getMessage());
            }
        }
    }

}
