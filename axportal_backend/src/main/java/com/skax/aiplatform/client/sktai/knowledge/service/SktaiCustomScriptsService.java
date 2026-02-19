package com.skax.aiplatform.client.sktai.knowledge.service;

import com.skax.aiplatform.client.sktai.knowledge.SktaiCustomScriptsClient;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.*;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * SKTAI Knowledge Custom Scripts 관리 서비스
 * 
 * <p>SKTAI Knowledge API의 Custom Scripts 관리 기능을 제공하는 비즈니스 서비스입니다.
 * 사용자 정의 로더(Loader) 및 스플리터(Splitter) 스크립트의 전체 생명주기를 관리합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Script 관리</strong>: 등록, 조회, 수정, 삭제 및 상태 관리</li>
 *   <li><strong>Script 테스트</strong>: 실제 문서를 사용한 스크립트 동작 검증</li>
 *   <li><strong>품질 보증</strong>: 스크립트 배포 전 품질 및 성능 검증</li>
 *   <li><strong>버전 관리</strong>: 스크립트 업데이트 및 변경 이력 추적</li>
 * </ul>
 * 
 * <h3>지원 스크립트 타입:</h3>
 * <ul>
 *   <li><strong>Loader Script</strong>: 다양한 문서 포맷을 텍스트로 변환</li>
 *   <li><strong>Splitter Script</strong>: 문서를 의미 있는 청크로 분할</li>
 * </ul>
 * 
 * <h3>테스트 및 검증:</h3>
 * <ul>
 *   <li><strong>단위 테스트</strong>: 개별 스크립트 기능 검증</li>
 *   <li><strong>통합 테스트</strong>: 전체 처리 파이프라인 검증</li>
 *   <li><strong>성능 테스트</strong>: 처리 속도 및 자원 사용량 측정</li>
 *   <li><strong>품질 검증</strong>: 출력 결과의 정확성 및 일관성 확인</li>
 * </ul>
 * 
 * <h3>오류 처리:</h3>
 * <ul>
 *   <li><strong>연결 오류</strong>: SKTAI API 서버 연결 실패 시 BusinessException 발생</li>
 *   <li><strong>파일 오류</strong>: 스크립트 파일 업로드 또는 처리 실패 시 상세 오류 정보 제공</li>
 *   <li><strong>스크립트 오류</strong>: 스크립트 실행 오류 시 디버깅 정보 포함</li>
 *   <li><strong>비즈니스 오류</strong>: 스크립트 상태 충돌 등 비즈니스 로직 오류 처리</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiCustomScriptsService {

    private final SktaiCustomScriptsClient sktaiCustomScriptsClient;

    /**
     * 공통 예외 처리 메서드
     * 
     * <p>외부 API 호출 시 발생하는 예외를 일관된 방식으로 처리합니다.</p>
     * 
     * @param operation 작업 설명 (예: "Custom Script 목록 조회", "Custom Script 생성" 등)
     * @param e 발생한 예외
     * @return 변환된 비즈니스 예외 (항상 BusinessException)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            // ErrorDecoder에서 변환된 BusinessException (HTTP 응답이 있는 경우: 400, 401, 403, 404, 422, 500 등)
            log.error("❌ SKTAI Knowledge {} 중 BusinessException 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            // HTTP 응답이 없는 경우 (연결 실패, 타임아웃 등) 또는 ErrorDecoder를 거치지 않은 FeignException
            // FeignException의 상세 정보(status, content, request)를 활용할 수 있음
            FeignException feignEx = (FeignException) e;
            log.error("❌ SKTAI Knowledge {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}", 
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    String.format("SKTAI Knowledge API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(), feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            // 기타 런타임 예외
            log.error("❌ SKTAI Knowledge {} 중 런타임 오류 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "SKTAI Knowledge API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } else {
            // 예상치 못한 예외 (checked exception 등)
            log.error("❌ SKTAI Knowledge {} 중 예상치 못한 오류 발생 - 오류: {}", 
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "SKTAI Knowledge API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // Custom Script 기본 관리
    // ========================================

    /**
     * Custom Script 목록 조회
     * 
     * <p>등록된 Custom Script 목록을 조회합니다.
     * 스크립트 타입, 검색어 등으로 필터링하여 원하는 스크립트를 효율적으로 찾을 수 있습니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지당 항목 수
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @param customScriptType 스크립트 타입 필터
     * @return Custom Script 목록
     * @throws BusinessException SKTAI API 호출 실패 시
     */
    public CustomScriptsResponse getCustomScripts(Integer page, Integer size, String sort, String filter, String search, String customScriptType) {
        try {
            log.info("SKTAI Knowledge Custom Script 목록 조회 시작 - page: {}, size: {}, scriptType: {}", 
                    page, size, customScriptType);
            
            CustomScriptsResponse scripts = sktaiCustomScriptsClient.getCustomScripts(page, size, sort, filter, search, customScriptType);
            
            log.info("SKTAI Knowledge Custom Script 목록 조회 완료 - 조회된 Script 수: {}", 
                    scripts.getData() != null ? scripts.getData().size() : 0);
            return scripts;
            
        } catch (BusinessException e) {
            throw handleException("Custom Script 목록 조회", e);
        } catch (FeignException e) {
            throw handleException("Custom Script 목록 조회", e);
        } catch (RuntimeException e) {
            throw handleException("Custom Script 목록 조회", e);
        } catch (Exception e) {
            throw handleException("Custom Script 목록 조회", e);
        }
    }

    /**
     * Custom Script 등록
     * 
     * <p>새로운 Custom Script를 등록합니다.
     * 스크립트 파일의 유효성을 검증하고 메타데이터와 함께 안전하게 저장합니다.</p>
     * 
     * @param name 스크립트 이름
     * @param description 스크립트 설명
     * @param scriptType 스크립트 타입 (loader/splitter)
     * @param script 스크립트 파일
     * @return 등록된 Custom Script 정보
     * @throws BusinessException Custom Script 등록 실패 시
     */
    public void createCustomScript(String name, String description, String script_type, MultipartFile script, String policy) {
        try {
            sktaiCustomScriptsClient.createCustomScript(name, description, script_type, script, policy);
            
        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Custom Script 등록 실패 (BusinessException) - name: {}, message: {}", name, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Custom Script 등록 실패 (예상치 못한 오류) - name: {}", name, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                "SKTAI Knowledge Custom Script 등록에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Custom Script 상세 조회
     * 
     * <p>특정 Custom Script의 상세 정보를 조회합니다.
     * 스크립트 메타데이터, 생성 정보, 사용 현황 등을 포함합니다.</p>
     * 
     * @param scriptId Custom Script ID
     * @return Custom Script 상세 정보
     * @throws BusinessException Custom Script 조회 실패 시
     */
    public CustomScriptDetailResponse getCustomScript(String scriptId) {
        try {
            log.info("SKTAI Knowledge Custom Script 상세 조회 시작 - scriptId: {}", scriptId);
            
            CustomScriptDetailResponse script = sktaiCustomScriptsClient.getCustomScript(scriptId);
            
            log.info("SKTAI Knowledge Custom Script 상세 조회 완료 - scriptId: {}", scriptId);
            return script;
            
        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Custom Script 상세 조회 실패 (BusinessException) - scriptId: {}, message: {}", scriptId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Custom Script 상세 조회 실패 (예상치 못한 오류) - scriptId: {}", scriptId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                "SKTAI Knowledge Custom Script 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Custom Script 수정
     * 
     * <p>기존 Custom Script의 정보를 수정합니다.
     * 이름, 설명, 스크립트 파일 등을 선택적으로 업데이트할 수 있습니다.</p>
     * 
     * @param scriptId Custom Script ID
     * @param name 수정할 스크립트 이름 (선택적)
     * @param description 수정할 스크립트 설명 (선택적)
     * @param script 수정할 스크립트 파일 (선택적)
     * @return 수정 처리 결과
     * @throws BusinessException Custom Script 수정 실패 시
     */
    public void updateCustomScript(String scriptId, String name, String description, MultipartFile script) {
        try {
            log.info("SKTAI Knowledge Custom Script 수정 시작 - scriptId: {}, name: {}, hasNewScript: {}", 
                    scriptId, name, script != null);
            
            sktaiCustomScriptsClient.updateCustomScript(scriptId, name, description, script);
            
        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Custom Script 수정 실패 (BusinessException) - scriptId: {}, message: {}", scriptId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Custom Script 수정 실패 (예상치 못한 오류) - scriptId: {}", scriptId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                "SKTAI Knowledge Custom Script 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Custom Script 삭제
     * 
     * <p>특정 Custom Script를 삭제합니다.
     * 현재 사용 중인 스크립트는 삭제할 수 없으며, 관련 종속성을 먼저 해제해야 합니다.</p>
     * 
     * @param scriptId Custom Script ID
     * @throws BusinessException Custom Script 삭제 실패 시
     */
    public void deleteCustomScript(String scriptId) {
        try {
            log.info("SKTAI Knowledge Custom Script 삭제 시작 - scriptId: {}", scriptId);
            
            sktaiCustomScriptsClient.deleteCustomScript(scriptId);
            
            log.info("SKTAI Knowledge Custom Script 삭제 완료 - scriptId: {}", scriptId);
            
        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Custom Script 삭제 실패 (BusinessException) - scriptId: {}, message: {}", scriptId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Custom Script 삭제 실패 (예상치 못한 오류) - scriptId: {}", scriptId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                "SKTAI Knowledge Custom Script 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // Custom Script 테스트
    // ========================================

    /**
     * Custom Loader Script 테스트
     * 
     * <p>Custom Loader Script의 동작을 실제 문서 파일로 테스트합니다.
     * 스크립트가 문서를 올바르게 로드하고 텍스트를 추출하는지 검증합니다.</p>
     * 
     * @param documentFile 테스트할 문서 파일
     * @param fileMetadata 파일 메타데이터 (JSON 형태)
     * @param loaderScriptFile 테스트할 Loader 스크립트 파일
     * @return 테스트 결과 및 처리 통계
     * @throws BusinessException Loader Script 테스트 실패 시
     */
    public ScriptTestResponse testLoaderScript(MultipartFile documentFile, String fileMetadata, MultipartFile loaderScriptFile) {
        try {
            log.info("SKTAI Knowledge Custom Loader Script 테스트 시작 - documentFile: {}, scriptFile: {}", 
                    documentFile != null ? documentFile.getOriginalFilename() : "null",
                    loaderScriptFile != null ? loaderScriptFile.getOriginalFilename() : "null");
            
            ScriptTestResponse response = sktaiCustomScriptsClient.testLoaderScript(documentFile, fileMetadata, loaderScriptFile);
            
            log.info("SKTAI Knowledge Custom Loader Script 테스트 완료");
            return response;
            
        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Custom Loader Script 테스트 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Custom Loader Script 테스트 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                "SKTAI Knowledge Custom Loader Script 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Custom Splitter Script 테스트
     * 
     * <p>Custom Splitter Script의 동작을 실제 문서 파일로 테스트합니다.
     * 문서 로딩 후 스플리터 스크립트가 문서를 적절히 청크로 분할하는지 검증합니다.</p>
     * 
     * @param documentFile 테스트할 문서 파일
     * @param fileMetadata 파일 메타데이터 (JSON 형태)
     * @param loaderType 사용할 로더 타입
     * @param customLoaderId 커스텀 로더 ID (loaderType이 CustomLoader인 경우 필수)
     * @param toolId 데이터 수집 도구 ID (loaderType이 DataIngestionTool인 경우 필수)
     * @param splitterScriptFile 테스트할 Splitter 스크립트 파일
     * @return 테스트 결과 및 분할 통계
     * @throws BusinessException Splitter Script 테스트 실패 시
     */
    public ScriptTestResponse testSplitterScript(MultipartFile documentFile, String fileMetadata, String loaderType, 
                                   String customLoaderId, String toolId, MultipartFile splitterScriptFile) {
        try {
            log.info("SKTAI Knowledge Custom Splitter Script 테스트 시작 - documentFile: {}, loaderType: {}, scriptFile: {}", 
                    documentFile != null ? documentFile.getOriginalFilename() : "null",
                    loaderType,
                    splitterScriptFile != null ? splitterScriptFile.getOriginalFilename() : "null");
            
            ScriptTestResponse response = sktaiCustomScriptsClient.testSplitterScript(
                documentFile, fileMetadata, loaderType, customLoaderId, toolId, splitterScriptFile);
            
            log.info("SKTAI Knowledge Custom Splitter Script 테스트 완료");
            return response;
            
        } catch (BusinessException e) {
            log.error("SKTAI Knowledge Custom Splitter Script 테스트 실패 (BusinessException) - loaderType: {}, message: {}", 
                    loaderType, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI Knowledge Custom Splitter Script 테스트 실패 (예상치 못한 오류) - loaderType: {}", loaderType, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                "SKTAI Knowledge Custom Splitter Script 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    // ========================================
    // 헬퍼 메서드
    // ========================================

    // /**
    //  * 스크립트 타입별 목록 조회
    //  * 
    //  * <p>특정 타입의 Custom Script 목록만 조회합니다.</p>
    //  * 
    //  * @param scriptType 스크립트 타입 (loader/splitter)
    //  * @param page 페이지 번호
    //  * @param size 페이지 크기
    //  * @return 타입별 Custom Script 목록
    //  * @throws BusinessException 조회 실패 시
    //  */
    // public CustomScriptsResponse getCustomScriptsByType(String scriptType, Integer page, Integer size) {
    //     return getCustomScripts(page, size, null, null, null, scriptType);
    // }

    // /**
    //  * 스크립트 이름으로 검색
    //  * 
    //  * <p>스크립트 이름에 검색어가 포함된 Custom Script 목록을 조회합니다.</p>
    //  * 
    //  * @param searchTerm 검색어
    //  * @param page 페이지 번호
    //  * @param size 페이지 크기
    //  * @return 검색 결과 Custom Script 목록
    //  * @throws BusinessException 검색 실패 시
    //  */
    // public MultiResponse searchCustomScripts(String searchTerm, Integer page, Integer size) {
    //     return getCustomScripts(page, size, null, null, searchTerm, null);
    // }

    /**
     * 로더 스크립트 목록 조회
     * 
     * <p>Loader 타입의 Custom Script 목록만 조회합니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Loader Script 목록
     * @throws BusinessException 조회 실패 시
     */
    // public MultiResponse getLoaderScripts(Integer page, Integer size) {
    //     return getCustomScriptsByType("loader", page, size);
    // }

    /**
     * 스플리터 스크립트 목록 조회
     * 
     * <p>Splitter 타입의 Custom Script 목록만 조회합니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Splitter Script 목록
     * @throws BusinessException 조회 실패 시
     */
    // public MultiResponse getSplitterScripts(Integer page, Integer size) {
    //     return getCustomScriptsByType("splitter", page, size);
    // }
}
