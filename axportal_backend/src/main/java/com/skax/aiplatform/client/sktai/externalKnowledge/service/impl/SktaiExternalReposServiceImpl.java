package com.skax.aiplatform.client.sktai.externalKnowledge.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.externalKnowledge.SktaiExternalReposClient;
import com.skax.aiplatform.client.sktai.externalKnowledge.dto.response.ExternalRepoListResponse;
import com.skax.aiplatform.client.sktai.externalKnowledge.service.SktaiExternalReposService;
import com.skax.aiplatform.client.sktai.knowledge.SktaiReposClient;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtCreateRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtImportRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtTestRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoImportResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeTestResult;

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ADXP External Knowledge Repository 서비스 구현체
 * 
 * <p>ADXP API의 External Knowledge Repository 관련 비즈니스 로직을 구현하는 서비스입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-11
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiExternalReposServiceImpl implements SktaiExternalReposService {

    private final SktaiExternalReposClient sktaiExternalReposClient;
    private final SktaiReposClient sktaiReposClient;

    /**
     * 공통 예외 처리 메서드
     * 
     * <p>외부 API 호출 시 발생하는 예외를 일관된 방식으로 처리합니다.</p>
     * 
     * @param operation 작업 설명 (예: "목록 조회", "생성", "수정" 등)
     * @param e 발생한 예외
     * @return 변환된 비즈니스 예외 (항상 BusinessException)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            // ErrorDecoder에서 변환된 BusinessException (HTTP 응답이 있는 경우: 400, 401, 403, 404, 422, 500 등)
            log.error("❌ ADXP External Knowledge Repository {} 중 BusinessException 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            // HTTP 응답이 없는 경우 (연결 실패, 타임아웃 등) 또는 ErrorDecoder를 거치지 않은 FeignException
            // FeignException의 상세 정보(status, content, request)를 활용할 수 있음
            FeignException feignEx = (FeignException) e;
            log.error("❌ ADXP External Knowledge Repository {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}", 
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    String.format("ADXP API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(), feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            // 기타 런타임 예외
            log.error("❌ ADXP External Knowledge Repository {} 중 런타임 오류 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "ADXP API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } else {
            // 예상치 못한 예외 (checked exception 등)
            log.error("❌ ADXP External Knowledge Repository {} 중 예상치 못한 오류 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "ADXP API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * External Knowledge Repository 목록 조회
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return External Knowledge Repository 목록
     */
    @Override
    public ExternalRepoListResponse getExternalRepos(Integer page, Integer size, String sort, String filter, String search) {
        log.info("🔍 ADXP External Knowledge Repository 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                page, size, sort, filter, search);

        try {
            // ADXP API 호출
            ExternalRepoListResponse response = sktaiExternalReposClient.getExternalRepos(page, size, sort, filter, search);
            
            log.info("✅ ADXP External Knowledge Repository 목록 조회 성공 - 데이터 개수: {}", 
                    response.getData() != null ? response.getData().size() : 0);
            
            return response;
            
        } catch (BusinessException e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        } catch (FeignException e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        } catch (Exception e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        }
    }

    /**
     * External Knowledge Repository 생성
     * 
     * @param request External Repository 생성 요청
     * @return 생성된 External Repository 정보
     */
    @Override
    public RepoResponse createExternalRepo(RepoExtCreateRequest request) {
        log.info("🚀 ADXP External Knowledge Repository 생성 요청 - name: {}", request.getName());

        try {
            // Script String을 MultipartFile로 변환
            MultipartFile scriptFile = convertStringToMultipartFile(
                request.getScript() != null ? request.getScript() : "",
                "retrieval_script.py"
            );
            
            log.info("📄 Script를 MultipartFile로 변환 완료 - 크기: {} bytes", scriptFile.getSize());
            
            // 요청 파라미터 상세 로그
            log.info("📋 [ADXP API Multipart 요청 파라미터]");
            log.info("  - name: {}", request.getName());
            log.info("  - description: {}", request.getDescription());
            log.info("  - embedding_model_name: {}", request.getEmbeddingModelName());
            log.info("  - vector_db_id: {}", request.getVectorDbId());
            log.info("  - index_name: {}", request.getIndexName());
            log.info("  - script_file: name={}, size={}, contentType={}", 
                    scriptFile.getOriginalFilename(), scriptFile.getSize(), scriptFile.getContentType());
            
            // ADXP API 호출 (multipart/form-data)
            RepoResponse response = sktaiExternalReposClient.createExternalRepo(
                request.getName(),
                request.getDescription() != null ? request.getDescription() : "",
                request.getEmbeddingModelName(),
                request.getVectorDbId(),
                request.getIndexName(),
                scriptFile
            );
            
            log.info("✅ ADXP External Knowledge Repository 생성 성공 - repoId: {}", response.getRepoId());
            
            return response;
            
        } catch (BusinessException e) {
            throw handleException("생성", e);
        } catch (FeignException e) {
            throw handleException("생성", e);
        } catch (RuntimeException e) {
            throw handleException("생성", e);
        } catch (Exception e) {
            throw handleException("생성", e);
        }
    }

    /**
     * String을 MultipartFile로 변환
     * 
     * @param content 파일 내용
     * @param filename 파일명
     * @return MultipartFile 객체
     */
    private MultipartFile convertStringToMultipartFile(String content, String filename) {
        try {
            byte[] bytes = content.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            return new ByteArrayMultipartFile("script_file", filename, "text/x-python", bytes);
        } catch (RuntimeException re) {
            log.error("❌ String을 MultipartFile로 변환 중 오류 발생: {}", re.getMessage(), re);
            throw new RuntimeException("Script 변환 중 오류가 발생했습니다: " + re.getMessage(), re);
        } catch (Exception e) {
            log.error("❌ String을 MultipartFile로 변환 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Script 변환 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * ByteArray 기반의 간단한 MultipartFile 구현
     */
    @SuppressWarnings("null")
    private static class ByteArrayMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name != null ? name : "file";
            this.originalFilename = originalFilename != null ? originalFilename : "file";
            this.contentType = contentType != null ? contentType : "application/octet-stream";
            this.content = content != null ? content : new byte[0];
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return this.content.length == 0;
        }

        @Override
        public long getSize() {
            return this.content.length;
        }

        @Override
        public byte[] getBytes() {
            // private 배열의 복사본을 반환하여 외부 수정 방지
            return this.content != null ? java.util.Arrays.copyOf(this.content, this.content.length) : new byte[0];
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("transferTo not implemented");
        }
    }

    /**
     * External Knowledge Repository 상세 조회
     * 
     * @param repoId External Repository ID
     * @return External Repository 상세 정보 (script 포함)
     */
    @Override
    public Object getExternalRepo(String repoId) {
        log.info("🔍 ADXP External Knowledge Repository 상세 조회 요청 - repoId: {}", repoId);

        try {
            // ADXP API 호출 - 실제 응답을 Object로 받아 그대로 반환 (script 포함)
            Object response = sktaiExternalReposClient.getExternalRepo(repoId);
            
            log.info("✅ ADXP External Knowledge Repository 상세 조회 성공 - repoId: {}", repoId);
            log.debug("📋 Response: {}", response);
            
            return response;
            
        } catch (BusinessException e) {
            throw handleException("External Knowledge Repository 상세 조회", e);
        } catch (FeignException e) {
            throw handleException("External Knowledge Repository 상세 조회", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge Repository 상세 조회", e);
        } catch (Exception e) {
            throw handleException("External Knowledge Repository 상세 조회", e);
        }
    }

    /**
     * External Knowledge Repository 테스트
     * 
     * @param request 테스트 요청 정보
     * @return 테스트 결과
     */
    @Override
    public ExternalKnowledgeTestResult testExternalRepo(RepoExtTestRequest request) {
        log.info("🧪 ADXP External Knowledge Repository 테스트 요청 - embeddingModelName: {}, vectorDbId: {}, indexName: {}",
                request.getEmbeddingModelName(), request.getVectorDbId(), request.getIndexName());

        try {
            if (request.getScript() == null || request.getScript().isBlank()) {
                throw new IllegalArgumentException("script 내용이 비어있습니다.");
            }

            MultipartFile scriptFile = convertStringToMultipartFile(request.getScript(), "retrieval_script.py");
            log.info("📄 테스트용 Script 변환 완료 - 크기: {} bytes", scriptFile.getSize());

            String query = request.getQuery();
            if (query == null || query.isBlank()) {
                query = "sample query";
            }
            String retrievalOptions = request.getRetrievalOptions();
            if (retrievalOptions == null || retrievalOptions.isBlank()) {
                retrievalOptions = "{\"top_k\":3}";
            }

            Map<String, Object> response = sktaiExternalReposClient.testExternalRepo(
                    request.getEmbeddingModelName(),
                    request.getVectorDbId(),
                    request.getIndexName(),
                    scriptFile,
                    query,
                    retrievalOptions
            );

            ExternalKnowledgeTestResult result = parseTestResponse(response);

            log.info("✅ ADXP External Knowledge Repository 테스트 결과 해석 완료 - success: {}, status: {}",
                    result.isSuccess(), result.getStatus());

            return result;

        } catch (BusinessException e) {
            throw handleException("External Knowledge Repository 테스트", e);
        } catch (FeignException e) {
            throw handleException("External Knowledge Repository 테스트", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge Repository 테스트", e);
        } catch (Exception e) {
            throw handleException("External Knowledge Repository 테스트", e);
        }
    }

    /**
     * External Knowledge Repository 수정
     * 
     * @param repoId External Repository ID
     * @param name Repository 이름
     * @param description Repository 설명
     * @param script Script 내용
     * @param indexName 인덱스명
     * @return 수정된 Repository 정보
     */
    @Override
    public Object updateExternalRepo(String repoId, String name, String description, String script, String indexName) {
        log.info("✏️ ADXP External Knowledge Repository 수정 요청 - repoId: {}", repoId);
        log.info("📤 SKTAI API로 전송할 요청 데이터:");
        log.info("  - name: {}", name);
        log.info("  - description: {}", description);
        log.info("  - script length: {}", script != null ? script.length() : 0);
        log.info("  - indexName: {}", indexName);

        try {
            // Script String을 MultipartFile로 변환
            MultipartFile scriptFile = null;
            if (script != null && !script.trim().isEmpty()) {
                scriptFile = convertStringToMultipartFile(script, "retrieval_script.py");
                log.info("📄 Script를 MultipartFile로 변환 완료 - 크기: {} bytes", scriptFile.getSize());
            }
            
            // ADXP API 호출 (multipart/form-data)
            Object response = sktaiExternalReposClient.updateExternalRepo(
                repoId,
                name != null ? name : "",
                description != null ? description : "",
                "", // embedding_model_name (빈값)
                indexName != null ? indexName : "",
                scriptFile // MultipartFile로 전송
            );
            
            log.info("✅ ADXP External Knowledge Repository 수정 성공 - repoId: {}", repoId);
            log.info("📋 SKTAI API 응답: {}", response);
            
            return response;
            
        } catch (BusinessException e) {
            throw handleException("External Knowledge Repository 수정", e);
        } catch (FeignException e) {
            throw handleException("External Knowledge Repository 수정", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge Repository 수정", e);
        } catch (Exception e) {
            throw handleException("External Knowledge Repository 수정", e);
        }
    }

    /**
     * External Knowledge Repository 삭제
     * 
     * @param repoId External Repository ID
     */
    @Override
    public void deleteExternalRepo(String repoId) {
        log.info("🗑️ ADXP External Knowledge Repository 삭제 요청 - repoId: {}", repoId);

        try {
            // ADXP API 호출
            sktaiExternalReposClient.deleteExternalRepo(repoId);
            
            log.info("✅ ADXP External Knowledge Repository 삭제 성공 - repoId: {}", repoId);
            
        } catch (BusinessException e) {
            throw handleException("External Knowledge Repository 삭제", e);
        } catch (FeignException e) {
            throw handleException("External Knowledge Repository 삭제", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge Repository 삭제", e);
        } catch (Exception e) {
            throw handleException("External Knowledge Repository 삭제", e);
        }
    }

    /**
     * External Knowledge Repository Import
     * 
     * <p>외부에서 생성된 VectorDB Index를 조회하기 위한 External Knowledge Repository를 Import합니다.
     * 기존 External Repository의 설정과 데이터를 기반으로 새로운 Internal Repository를 생성합니다.</p>
     * 
     * @param request External Repository Import 요청 정보
     * @return Import된 Repository ID
     */
    @Override
    public RepoImportResponse importExternalRepo(RepoExtImportRequest request) {
        log.info("📥 ADXP External Knowledge Repository Import 요청 - id: {}, name: {}, vectorDbId: {}", 
                request.getId(), request.getName(), request.getVectorDbId());

        try {
            // SktaiReposClient를 사용하여 Import API 호출
            RepoImportResponse response = sktaiReposClient.importExternalRepo(request);
            
            log.info("✅ ADXP External Knowledge Repository Import 성공 - repoId: {}", 
                    response.getRepoId());
            return response;
            
        } catch (feign.FeignException.NotFound e) {
            log.error("❌ ADXP External Repository를 찾을 수 없음 - id: {}, error: {}", request.getId(), e.getMessage());
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "External Repository를 찾을 수 없습니다: " + request.getId());
        } catch (feign.FeignException.Forbidden e) {
            log.error("❌ ADXP External Repository Import 권한 부족 - id: {}, error: {}", request.getId(), e.getMessage());
            throw new BusinessException(ErrorCode.FORBIDDEN, 
                "External Repository Import 권한이 없습니다");
        } catch (feign.FeignException.UnprocessableEntity e) {
            String errorContent = "";
            try {
                errorContent = e.contentUTF8();
            } catch (RuntimeException ex) {
                log.warn("❌ ADXP External Repository Import 응답 본문 읽기 실패 (RuntimeException) - id: {}, error: {}", 
                        request.getId(), ex.getMessage());
                errorContent = "응답 본문 읽기 실패: " + ex.getMessage();
            } catch (Exception ex) {
                log.warn("❌ ADXP External Repository Import 응답 본문 읽기 실패 (Exception) - id: {}, error: {}", 
                        request.getId(), ex.getMessage());
                errorContent = "응답 본문 읽기 실패: " + ex.getMessage();
            }
            log.error("❌ ADXP External Repository Import 요청 데이터 검증 실패 - id: {}, status: {}, error: {}", 
                    request.getId(), e.status(), e.getMessage());
            log.error("❌ 응답 본문: {}", errorContent);
            // 보안: 시스템 내부 정보가 포함된 예외 메시지를 사용자에게 노출하지 않음
            throw new BusinessException(ErrorCode.EXTERNAL_API_VALIDATION_ERROR, 
                "External Repository Import 요청 데이터가 올바르지 않습니다.");
        } catch (feign.FeignException e) {
            String errorContent = "";
            try {
                errorContent = e.contentUTF8();
            } catch (RuntimeException ex) {
                log.warn("❌ ADXP External Repository Import 응답 본문 읽기 실패 (RuntimeException) - id: {}, error: {}", 
                        request.getId(), ex.getMessage());
                errorContent = "응답 본문 읽기 실패: " + ex.getMessage();
            } catch (Exception ex) {
                log.warn("❌ ADXP External Repository Import 응답 본문 읽기 실패 (Exception) - id: {}, error: {}", 
                        request.getId(), ex.getMessage());
                errorContent = "응답 본문 읽기 실패: " + ex.getMessage();
            }
            log.error("❌ ADXP External Repository Import 실패 (FeignException) - id: {}, status: {}, error: {}", 
                    request.getId(), e.status(), e.getMessage());
            log.error("❌ 응답 본문: {}", errorContent);
            // 보안: 시스템 내부 정보가 포함된 예외 메시지를 사용자에게 노출하지 않음
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                "ADXP External Knowledge Repository Import에 실패했습니다.");
        } catch (Exception e) {
            log.error("❌ ADXP External Knowledge Repository Import 실패 - id: {}, error: {}", request.getId(), e.getMessage(), e);
            // 보안: 시스템 내부 정보가 포함된 예외 메시지를 사용자에게 노출하지 않음
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                "ADXP External Knowledge Repository Import에 실패했습니다.");
        }
    }

    private ExternalKnowledgeTestResult parseTestResponse(Map<String, Object> response) {
        if (response == null || response.isEmpty()) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "ADXP 테스트 응답이 비어있습니다.");
        }

        String status = response.get("status") != null ? Objects.toString(response.get("status"), null) : null;
        Object detail = response.get("detail");

        if (status != null) {
            if ("success".equalsIgnoreCase(status)) {
                String message = buildSuccessMessage(detail);
                return ExternalKnowledgeTestResult.builder()
                        .success(true)
                        .status(status)
                        .message(message)
                        .detail(detail)
                        .build();
            }

            // status가 "error"인 경우 예외를 던지지 않고 결과 반환 (200 응답으로 처리)
            if ("error".equalsIgnoreCase(status)) {
                String message = buildErrorMessage(detail);
                return ExternalKnowledgeTestResult.builder()
                        .success(false)
                        .status(status)
                        .message(message)
                        .detail(detail)
                        .build();
            }
        }

        if (response.containsKey("detail")) {
            String message = buildErrorMessage(response.get("detail"));
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, message);
        }

        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "ADXP 테스트 응답을 해석할 수 없습니다.");
    }

    private String buildSuccessMessage(Object detail) {
        if (detail == null) {
            return "External Repository 테스트가 완료되었습니다.";
        }

        if (detail instanceof String str && !str.isBlank()) {
            return str;
        }

        if (detail instanceof Iterable<?> iterable) {
            String summary = buildIterableSummary(iterable);
            if (!summary.isBlank()) {
                return summary;
            }
        }

        return "External Repository 테스트가 완료되었습니다.";
    }

    private String buildErrorMessage(Object detail) {
        if (detail == null) {
            return "External Repository 테스트 중 오류가 발생했습니다.";
        }

        if (detail instanceof String str && !str.isBlank()) {
            return str;
        }

        if (detail instanceof Iterable<?> iterable) {
            String summary = buildIterableSummary(iterable);
            if (!summary.isBlank()) {
                return summary;
            }
        }

        return detail.toString();
    }

    private String buildIterableSummary(Iterable<?> iterable) {
        StringBuilder sb = new StringBuilder();
        for (Object item : iterable) {
            if (item == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(item);
        }
        return sb.toString();
    }
}



