package com.skax.aiplatform.client.udp.document.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.udp.document.UdpDocumentClient;
import com.skax.aiplatform.client.udp.document.dto.request.DocumentSearchRequest;
import com.skax.aiplatform.client.udp.document.dto.response.DocumentSearchResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UDP Document API 서비스
 * 
 * <p>
 * UDP Document 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스입니다.
 * 문서 검색 관련 API에 대한 서비스 메서드를 제공합니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UdpDocumentService {

    private final UdpDocumentClient udpDocumentClient;

    @Value("${udp.api.auth.dataset-document-search-key:}")
    private String datasetDocumentSearchKey;

    /**
     * 공통 예외 처리 메서드
     * 
     * <p>
     * 외부 API 호출 시 발생하는 예외를 일관된 방식으로 처리합니다.
     * </p>
     * 
     * @param operation 작업 설명 (예: "UDP 문서 검색" 등)
     * @param e         발생한 예외
     * @return 변환된 비즈니스 예외 (항상 BusinessException)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            // ErrorDecoder에서 변환된 BusinessException (HTTP 응답이 있는 경우: 400, 401, 403, 404,
            // 422, 500 등)
            log.error("❌ UDP Document {} 중 BusinessException 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            // HTTP 응답이 없는 경우 (연결 실패, 타임아웃 등) 또는 ErrorDecoder를 거치지 않은 FeignException
            // FeignException의 상세 정보(status, content, request)를 활용할 수 있음
            FeignException feignEx = (FeignException) e;
            log.error("❌ UDP Document {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}",
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    String.format("UDP Document API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(),
                            feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            // 기타 런타임 예외
            log.error("❌ UDP Document {} 중 런타임 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "UDP Document API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } else {
            // 예상치 못한 예외 (checked exception 등)
            log.error("❌ UDP Document {} 중 예상치 못한 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "UDP Document API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 데이터셋 문서 검색
     * 
     * <p>
     * 지정된 데이터셋 내에서 키워드를 기반으로 문서를 검색합니다.
     * </p>
     * 
     * @param request 문서 검색 요청 정보
     * @return 검색된 문서 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public DocumentSearchResponse searchDocuments(DocumentSearchRequest request) {
        try {
            log.info(">>> UDP 문서 검색 요청 시작 - datasetCd: {}, searchWord: {}, uuid: {}, page: {}, countPerPage: {}",
                    request.getDatasetCd(), request.getSearchWord(), request.getDocUuid(), request.getPage(),
                    request.getCountPerPage());

            // 요청 파라미터 검증
            if (request.getDatasetCd() == null || request.getDatasetCd().trim().isEmpty()) {
                log.error(">>> UDP 문서 검색 실패 - datasetCd가 null이거나 빈 값입니다");
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "데이터셋 코드는 필수입니다");
            }

            log.info(">>> UDP Document API 전송 JSON 요청: {}", request);

            DocumentSearchResponse response = udpDocumentClient.searchDocuments(datasetDocumentSearchKey, request);
            log.info(">>> UDP Document API 호출 완료");

            // origin_metadata에서 필드 보강 (docTitle, createDate, lastModDate)
            if (response != null && response.getResultLists() != null) {
                response.getResultLists().forEach(this::applyOriginMetadataSafely);
            }

            log.info(">>> UDP 문서 검색 성공 - datasetCd: {}, searchWord: {}, 결과 건수: {}",
                    request.getDatasetCd(),
                    request.getSearchWord(),
                    response != null && response.getResultLists() != null ? response.getResultLists().size() : 0);

            return response;
        } catch (BusinessException e) {
            throw handleException("UDP 문서 검색", e);
        } catch (FeignException e) {
            throw handleException("UDP 문서 검색", e);
        } catch (RuntimeException e) {
            throw handleException("UDP 문서 검색", e);
        } catch (Exception e) {
            throw handleException("UDP 문서 검색", e);
        }
    }

    /**
     * origin_metadata의 값들을 안전하게 적용
     * - doc_nm이 비어있을 때에만 title로 보강
     * - create_date, mdfcn_date를 yyyyMMdd 형태로 안전 파싱하여 Long에 설정 (실패시 유지)
     */
    private void applyOriginMetadataSafely(
            com.skax.aiplatform.client.udp.document.dto.response.DocumentInfo documentInfo) {
        try {
            Map<String, Object> meta_tmp = documentInfo.getOriginMetadata();
            Map<String, Object> meta = (Map<String, Object>) meta_tmp.get("origin_metadata");
            if (meta == null) {
                return;
            }

            // 제목 보강: docTitle이 비었을 때만 origin_metadata.title 사용
            try {
                String currentTitle = safeTrim(documentInfo.getDocTitle());
                if (currentTitle == null || currentTitle.isEmpty()) {
                    Object titleObj = meta.get("title");
                    String title = safeTrim(titleObj);
                    if (title != null && !title.isEmpty()) {
                        documentInfo.setDocTitle(title);
                    }
                }
            } catch (NullPointerException e) {
                log.debug("origin_metadata 제목 보강 적용 중 NullPointerException - msg: {}", e.getMessage());
                // 경미한 오류는 무시
            } catch (ClassCastException e) {
                log.debug("origin_metadata 제목 보강 적용 중 ClassCastException - msg: {}", e.getMessage());
                // 경미한 오류는 무시
            } catch (RuntimeException e) {
                log.debug("origin_metadata 제목 보강 적용 중 RuntimeException - msg: {}", e.getMessage());
                // 기타 런타임 예외는 무시
            }

            // 생성일시 보강: origin_metadata.create_date (yyyyMMdd) -> Long
            try {
                Object createObj = meta.get("create_date");
                Long parsed = parseYyyyMmDdToLong(createObj);
                if (parsed != null) {
                    documentInfo.setDocCreateDay(parsed.toString());
                }
            } catch (NullPointerException e) {
                log.debug("origin_metadata 생성일시 보강 적용 중 NullPointerException - msg: {}", e.getMessage());
                // 경미한 오류는 무시
            } catch (ClassCastException e) {
                log.debug("origin_metadata 생성일시 보강 적용 중 ClassCastException - msg: {}", e.getMessage());
                // 경미한 오류는 무시
            } catch (RuntimeException e) {
                log.debug("origin_metadata 생성일시 보강 적용 중 RuntimeException - msg: {}", e.getMessage());
                // 기타 런타임 예외는 무시
            }

            // 최종수정일시 보강: origin_metadata.mdfcn_date (yyyyMMdd) -> Long
            try {
                Object modObj = meta.get("mdfcn_date");
                Long parsed = parseYyyyMmDdToLong(modObj);
                if (parsed != null) {
                    documentInfo.setDocMdfcnDay(parsed.toString());
                }
            } catch (NullPointerException e) {
                log.debug("origin_metadata 최종수정일시 보강 적용 중 NullPointerException - msg: {}", e.getMessage());
                // 경미한 오류는 무시
            } catch (ClassCastException e) {
                log.debug("origin_metadata 최종수정일시 보강 적용 중 ClassCastException - msg: {}", e.getMessage());
                // 경미한 오류는 무시
            } catch (RuntimeException e) {
                log.debug("origin_metadata 최종수정일시 보강 적용 중 RuntimeException - msg: {}", e.getMessage());
                // 기타 런타임 예외는 무시
            }

        } catch (NullPointerException e) {
            log.debug("origin_metadata 적용 중 NullPointerException - docUuid: {}, msg: {}",
                    documentInfo.getDocUuid(), e.getMessage());
        } catch (ClassCastException e) {
            log.debug("origin_metadata 적용 중 ClassCastException - docUuid: {}, msg: {}",
                    documentInfo.getDocUuid(), e.getMessage());
        } catch (RuntimeException e) {
            log.debug("origin_metadata 적용 중 런타임 오류 - docUuid: {}, msg: {}",
                    documentInfo.getDocUuid(), e.getMessage());
        } catch (Exception e) {
            log.debug("origin_metadata 적용 중 예상치 못한 오류 - docUuid: {}, msg: {}",
                    documentInfo.getDocUuid(), e.getMessage());
        }
    }

    private String safeTrim(Object value) {
        if (value == null)
            return null;
        String s = value.toString();
        return s == null ? null : s.trim();
    }

    private Long parseYyyyMmDdToLong(Object value) {
        String s = safeTrim(value);
        if (s == null || s.isEmpty())
            return null;
        // 숫자만 남기고 8자리 보장 시에만 파싱
        String digits = s.replaceAll("[^0-9]", "");
        if (digits.length() < 8)
            return null;
        try {
            return Long.parseLong(digits.substring(0, 8));
        } catch (NumberFormatException e) {
            // 숫자 파싱 실패
            return null;
        } catch (RuntimeException e) {
            // 기타 런타임 예외
            return null;
        }
    }

    /**
     * Document Form Body 생성 메서드
     * 
     * @param request 문서 검색 요청
     * @return URL 인코딩된 Form Body 문자열
     */
    private String buildDocumentFormBody(DocumentSearchRequest request) {
        try {
            Map<String, Object> params = new LinkedHashMap<>();

            // 필수 파라미터들 (null 체크 후 기본값 설정)
            String datasetCd = request.getDatasetCd();
            if (datasetCd == null || datasetCd.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "데이터셋 코드는 필수입니다");
            }
            params.put("dataset_cd", datasetCd);

            // 선택적 문자열 파라미터들
            addStringParam(params, "search_word", request.getSearchWord());
            addStringParam(params, "doc_mod_start", request.getDocModStart());
            addStringParam(params, "doc_mod_end", request.getDocModEnd());
            addStringParam(params, "origin_metadata_yn", request.getOriginMetadataYn());
            addStringParam(params, "doc_uuid", request.getDocUuid());

            // 숫자 파라미터들 (기본값 설정)
            params.put("count_per_page", request.getCountPerPage() != null ? request.getCountPerPage() : 20L);
            params.put("page", request.getPage() != null ? request.getPage() : 1L);

            String formBody = buildFormBodyFromMap(params);
            log.info(">>> Document Form Body 생성 완료 - 길이: {}, 내용: {}", formBody.length(), formBody);

            return formBody;

        } catch (BusinessException e) {
            throw e; // BusinessException은 그대로 전파
        } catch (NullPointerException e) {
            log.error(">>> Document Form Body 생성 중 NullPointerException 발생 - 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Document Form Body 생성에 실패했습니다: 필수 파라미터가 null입니다.");
        } catch (RuntimeException e) {
            log.error(">>> Document Form Body 생성 중 런타임 오류 발생 - 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Document Form Body 생성에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error(">>> Document Form Body 생성 중 예상치 못한 오류 발생 - 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Document Form Body 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 문자열 파라미터 추가
     */
    private void addStringParam(Map<String, Object> params, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            params.put(key, value);
        }
    }

    /**
     * Map에서 Form Body 문자열 생성
     */
    private String buildFormBodyFromMap(Map<String, Object> params) {
        if (params.isEmpty()) {
            return "";
        }

        return params.entrySet().stream()
                .map(entry -> {
                    try {
                        String value = entry.getValue() instanceof String
                                ? URLEncoder.encode((String) entry.getValue(), StandardCharsets.UTF_8)
                                : entry.getValue().toString();
                        return entry.getKey() + "=" + value;
                    } catch (IllegalArgumentException e) {
                        // URLEncoder.encode()에서 발생 가능 (하지만 StandardCharsets.UTF_8 사용 시 실제로는 발생하지 않음)
                        log.warn("파라미터 인코딩 실패 (IllegalArgumentException) - key: {}, value: {}, 오류: {}",
                                entry.getKey(), entry.getValue(), e.getMessage());
                        return entry.getKey() + "=" + entry.getValue();
                    } catch (NullPointerException e) {
                        // entry.getValue()가 null인 경우
                        log.warn("파라미터 값이 null (NullPointerException) - key: {}", entry.getKey());
                        return entry.getKey() + "=";
                    } catch (RuntimeException e) {
                        // 기타 런타임 예외 (ClassCastException 등)
                        log.warn("파라미터 처리 실패 (RuntimeException) - key: {}, value: {}, 오류: {}",
                                entry.getKey(), entry.getValue(), e.getMessage());
                        return entry.getKey() + "=" + entry.getValue();
                    } catch (Exception e) {
                        // 예상치 못한 예외 (checked exception 등)
                        log.warn("파라미터 처리 실패 (Exception) - key: {}, value: {}, 오류: {}",
                                entry.getKey(), entry.getValue(), e.getMessage());
                        return entry.getKey() + "=" + entry.getValue();
                    }
                })
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

}