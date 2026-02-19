package com.skax.aiplatform.client.lablup.config;

import com.skax.aiplatform.client.lablup.common.service.LablupCommonService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * Lablup API 요청 인터셉터
 * 
 * <p>
 * Lablup API 호출 시 공통으로 적용되는 헤더 및 설정을 담당합니다.
 * 모든 요청에 필요한 공통 헤더를 자동으로 추가하며, MultiPart 요청을 감지하여 적절한 처리를 수행합니다.
 * </p>
 * 
 * <p>
 * 이 클래스는 {@link LablupClientConfig}에서 빈으로 등록되어 사용됩니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Slf4j
public class LablupRequestInterceptor implements RequestInterceptor {

    @Autowired
    private LablupCommonService lablupCommonService;

    private static final String CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;

    // MultiPart 요청을 감지하기 위한 엔드포인트 패턴
    private static final String[] MULTIPART_ENDPOINTS = {
            "/upload",
            "/import",
            "/files"
    };

    // GraphQL 요청을 감지하기 위한 엔드포인트 패턴
    private static final String[] GRAPHQL_ENDPOINTS = {
            "/graphql"
    };

    /**
     * 요청 인터셉트 처리
     * 
     * <p>
     * 모든 Lablup API 요청에 공통 헤더를 추가하며, MultiPart 요청의 경우 특별한 처리를 수행합니다.
     * 개발계에서는 SSL 우회 설정 정보도 함께 로깅합니다.
     * </p>
     * 
     * @param template 요청 템플릿
     */
    @Override
    public void apply(RequestTemplate template) {
        try {
            // 파라미터 검증
            if (template == null) {
                log.error(">>> [LABLUP REQUEST] 인터셉터 적용 실패 - RequestTemplate이 null입니다.");
                throw new IllegalArgumentException("RequestTemplate은 필수입니다.");
            }
            
            log.debug("🔍 [LABLUP REQUEST] API 호출 시작 - URL: {}, Method: {}",
                    template.url(), template.method());

            // HTTPS 요청인지 확인하여 SSL 우회 설정 로깅
            if (template.url() != null && template.url().startsWith("https://")) {
                log.debug("🔒 [LABLUP SSL] HTTPS 요청 감지 - SSL 우회 설정이 적용됩니다");
            }

            // Lablup API 인증 헤더 생성
            try {
                String relUrl = extractRelativeUrl(template.url());
                Map<String, String> commonHeaders = lablupCommonService.generateCommonHeaders(
                        template.method(), relUrl, template.feignTarget().name());

                // 생성된 공통 헤더들을 템플릿에 추가
                if (commonHeaders != null) {
                    for (Map.Entry<String, String> entry : commonHeaders.entrySet()) {
                        template.header(entry.getKey(), entry.getValue());
                    }
                }

                log.debug("✅ [LABLUP AUTH] 인증 헤더 생성 및 적용 완료");
            } catch (NullPointerException e) {
                log.error("❌ [LABLUP AUTH] 인증 헤더 생성 실패 - 필수 데이터 null: {}", e.getMessage(), e);
                // 인증 헤더 생성 실패 시에도 기본 헤더는 유지
            } catch (IllegalArgumentException e) {
                log.error("❌ [LABLUP AUTH] 인증 헤더 생성 실패 - 잘못된 파라미터: {}", e.getMessage(), e);
                // 인증 헤더 생성 실패 시에도 기본 헤더는 유지
            } catch (Exception e) {
                log.error("❌ [LABLUP AUTH] 인증 헤더 생성 실패: {}", e.getMessage(), e);
                // 인증 헤더 생성 실패 시에도 기본 헤더는 유지
            }

            // MultiPart 요청 감지
            boolean isMultipartRequest = isMultipartRequest(template.url());
            boolean isGraphqlRequest = isGraphqlRequest(template.url());

            if (isMultipartRequest) {
                // MultiPart 요청의 경우 Content-Type을 설정하지 않음 (SpringFormEncoder가 boundary와 함께 자동
                // 설정)
                log.debug("✅ 🔴 MultiPart 요청 감지 - Content-Type 설정 건너뛰기 (SpringFormEncoder가 자동 설정)");
            } else if (isGraphqlRequest) {
                // GraphQL 요청의 경우 Content-Type: application/json 설정
                template.header("Content-Type", CONTENT_TYPE_JSON);
                log.debug("✅ 🔴 GraphQL 요청 감지 - Content-Type: JSON");
            } else {
                // 일반 요청의 경우 JSON Content-Type 설정 (인증 헤더에서 이미 설정되었을 수 있음)
                if (template.headers() != null && !template.headers().containsKey("Content-Type")) {
                    template.header("Content-Type", CONTENT_TYPE_JSON);
                    log.debug("✅ 일반 API 요청 - Content-Type: JSON");
                }
            }

            // Accept 설정 (모든 요청에 적용)
            template.header("Accept", MediaType.APPLICATION_JSON_VALUE);

            // User-Agent 설정 (모든 요청에 적용)
            template.header("User-Agent", "AXPORTAL-Backend/1.0");

            log.debug("🔴 [LABLUP REQUEST] 요청 헤더 설정 완료 - 최종 헤더들: {}",
                    template.headers());

        } catch (IllegalArgumentException e) {
            log.error(">>> [LABLUP REQUEST] 인터셉터 적용 실패 - 잘못된 파라미터: error={}", e.getMessage(), e);
            throw new RuntimeException("Lablup 요청 인터셉터 적용 실패: 잘못된 파라미터입니다.", e);
        } catch (NullPointerException e) {
            log.error(">>> [LABLUP REQUEST] 인터셉터 적용 실패 - 필수 데이터 null: error={}", e.getMessage(), e);
            throw new RuntimeException("Lablup 요청 인터셉터 적용 실패: 필수 데이터를 찾을 수 없습니다.", e);
        } catch (Exception e) {
            log.error("❌ [LABLUP REQUEST] 요청 인터셉터 적용 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Lablup 요청 인터셉터 적용 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * MultiPart 요청 여부 감지
     * 
     * <p>
     * URL 패턴을 기반으로 MultiPart 요청인지 판단합니다.
     * </p>
     * 
     * @param url 요청 URL
     * @return MultiPart 요청 여부
     */
    private boolean isMultipartRequest(String url) {
        try {
            if (url == null) {
                log.debug("🔍 MultiPart 검사 - URL이 null이므로 false 반환");
                return false;
            }

            // MultiPart 엔드포인트 패턴 검사
            for (String endpoint : MULTIPART_ENDPOINTS) {
                if (endpoint != null && url.contains(endpoint)) {
                    log.debug("🔍 MultiPart 엔드포인트 감지: {} -> {}", url, endpoint);
                    return true;
                }
            }

            return false;
            
        } catch (NullPointerException e) {
            log.warn(">>> MultiPart 요청 검사 실패 - 필수 데이터 null: url={}, error={}", url, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(">>> MultiPart 요청 검사 중 오류 발생: url={}, error={}", url, e.getMessage(), e);
            return false;
        }
    }

    /**
     * GraphQL 요청 여부 감지
     * 
     * <p>
     * URL 패턴을 기반으로 GraphQL 요청인지 판단합니다.
     * </p>
     * 
     * @param url 요청 URL
     * @return GraphQL 요청 여부
     */
    private boolean isGraphqlRequest(String url) {
        try {
            if (url == null) {
                log.debug("🔍 GraphQL 검사 - URL이 null이므로 false 반환");
                return false;
            }

            // GraphQL 엔드포인트 패턴 검사
            for (String endpoint : GRAPHQL_ENDPOINTS) {
                if (endpoint != null && url.contains(endpoint)) {
                    log.debug("🔍 GraphQL 엔드포인트 감지: {} -> {}", url, endpoint);
                    return true;
                }
            }

            return false;
            
        } catch (NullPointerException e) {
            log.warn(">>> GraphQL 요청 검사 실패 - 필수 데이터 null: url={}, error={}", url, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(">>> GraphQL 요청 검사 중 오류 발생: url={}, error={}", url, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 전체 URL에서 상대 URL 경로 추출
     * 
     * <p>
     * Feign 템플릿의 전체 URL에서 상대 경로 부분만 추출합니다.
     * </p>
     * 
     * @param fullUrl 전체 URL (예:
     *                "http://110.45.167.85:8091/v1/artifact-registries/scan")
     * @return 상대 URL 경로 (예: "/v1/artifact-registries/scan")
     */
    private String extractRelativeUrl(String fullUrl) {
        try {
            if (fullUrl == null || fullUrl.trim().isEmpty()) {
                log.debug("🔍 [LABLUP URL] fullUrl이 null 또는 빈 문자열이므로 기본값 '/' 반환");
                return "/";
            }

            // URL에서 경로 부분만 추출
            int protocolEnd = fullUrl.indexOf("://");
            if (protocolEnd == -1) {
                return fullUrl.startsWith("/") ? fullUrl : "/" + fullUrl;
            }

            int pathStart = fullUrl.indexOf("/", protocolEnd + 3);
            if (pathStart == -1) {
                return "/";
            }

            String relativeUrl = fullUrl.substring(pathStart);
            log.debug("🔍 [LABLUP URL] 상대 URL 추출: {} -> {}", fullUrl, relativeUrl);
            return relativeUrl;

        } catch (StringIndexOutOfBoundsException e) {
            log.warn(">>> [LABLUP URL] 상대 URL 추출 실패 - 문자열 인덱스 오류: fullUrl={}, error={}", fullUrl, e.getMessage());
            return "/";
        } catch (NullPointerException e) {
            log.warn(">>> [LABLUP URL] 상대 URL 추출 실패 - null 참조: fullUrl={}, error={}", fullUrl, e.getMessage());
            return "/";
        } catch (Exception e) {
            log.warn("⚠️ [LABLUP URL] 상대 URL 추출 실패, 기본값 사용: fullUrl={}, error={}", fullUrl, e.getMessage());
            return "/";
        }
    }
}