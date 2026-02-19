package com.skax.aiplatform.client.sktai.safetyfilter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.safetyfilter.SktaiSafetyFiltersClient;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterCreate;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterUpdate;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.CheckSafeOrNot;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyFilterRead;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyFiltersRead;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyCheckOutput;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

/**
 * SKTAI SafetyFilter 개별 필터 관리 서비스
 * 
 * <p>개별 SafetyFilter의 CRUD 작업과 안전성 검사를 담당하는 서비스 계층입니다.
 * Feign Client를 래핑하여 필터 관리와 텍스트 안전성 검사의 비즈니스 로직과 예외 처리를 제공합니다.</p>
 * 
 * <h3>제공 기능:</h3>
 * <ul>
 *   <li><strong>SafetyFilter CRUD</strong>: 필터 생성, 조회, 수정, 삭제</li>
 *   <li><strong>필터 목록 조회</strong>: 페이지네이션, 정렬, 필터링, 검색 지원</li>
 *   <li><strong>텍스트 안전성 검사</strong>: 유해 콘텐츠 감지 및 필터링</li>
 * </ul>
 * 
 * <h3>안전성 검사 기능:</h3>
 * <ul>
 *   <li><strong>유해 콘텐츠 감지</strong>: 욕설, 혐오 표현, 불법 콘텐츠 등 감지</li>
 *   <li><strong>개인정보 보호</strong>: 개인식별정보 (PII) 감지 및 차단</li>
 *   <li><strong>윤리적 가이드라인</strong>: AI 윤리 기준에 따른 콘텐츠 필터링</li>
 * </ul>
 * 
 * <h3>예외 처리:</h3>
 * <ul>
 *   <li>SktaiErrorDecoder를 통한 HTTP 오류 자동 변환</li>
 *   <li>BusinessException dual catch 패턴 적용</li>
 *   <li>상세한 로깅 및 오류 추적</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiSafetyFiltersService {
    
    private final SktaiSafetyFiltersClient safetyFiltersClient;
    
    /**
     * SafetyFilter 등록
     * 
     * <p>새로운 안전 필터를 생성합니다.
     * 키워드, 라벨, 정책 등을 설정하여 텍스트 필터링 규칙을 등록합니다.</p>
     * 
     * @param request SafetyFilter 생성 요청 정보
     * @return 생성된 SafetyFilter 정보
     * @throws BusinessException SafetyFilter 생성 실패 시
     */
    public SafetyFilterRead registerSafetyFilter(SafetyFilterCreate request) {
        log.debug("🛡️ SafetyFilter 등록 요청 - 불용어: {}, 라벨: {}", 
                 request.getStopword(), request.getLabel());
        
        try {
            SafetyFilterRead response = safetyFiltersClient.registerSafetyFilter(request);
            log.debug("🛡️ SafetyFilter 등록 성공 - ID: {}, 불용어: {}", 
                     response.getId(), response.getStopword());
            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 등록 실패 - 불용어: {}, error: {}", 
                     request.getStopword(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 등록 중 예상치 못한 오류 - 불용어: {}", 
                     request.getStopword(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                                       "SafetyFilter 등록에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * SafetyFilter 목록 조회
     * 
     * <p>등록된 안전 필터 목록을 조회합니다.
     * 페이지네이션, 정렬, 필터링, 검색 기능을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10, -1: 모든 필터)
     * @param sort 정렬 조건 (예: "stopword,asc")
     * @param filter 필터 조건 (예: "group_id:uuid")
     * @param search 검색 키워드 (불용어로 검색)
     * @return SafetyFilter 목록과 페이지네이션 정보
     * @throws BusinessException SafetyFilter 목록 조회 실패 시
     */
    public SafetyFiltersRead getSafetyFilters(Integer page, Integer size, String sort, 
                                            String filter, String search) {
        log.debug("🛡️ SafetyFilter 목록 조회 요청 - page: {}, size: {}, filter: {}, search: {}", 
                 page, size, filter, search);
        
        try {
            SafetyFiltersRead response = safetyFiltersClient.getSafetyFilters(page, size, sort, filter, search);
            int totalFilters = response.getData() != null ? response.getData().size() : 0;
            log.debug("🛡️ SafetyFilter 목록 조회 성공 - 조회된 필터 수: {}", totalFilters);
            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 목록 조회 실패 - page: {}, size: {}, error: {}", 
                     page, size, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 목록 조회 중 예상치 못한 오류 - page: {}, size: {}", 
                     page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                                       "SafetyFilter 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * SafetyFilter 상세 조회
     * 
     * <p>특정 안전 필터의 상세 정보를 조회합니다.</p>
     * 
     * @param safetyFilterId 조회할 SafetyFilter ID (UUID 형태)
     * @return SafetyFilter 상세 정보
     * @throws BusinessException SafetyFilter 조회 실패 시
     */
    public SafetyFilterRead getSafetyFilter(String safetyFilterId) {
        log.debug("🛡️ SafetyFilter 상세 조회 요청 - ID: {}", safetyFilterId);
        
        try {
            SafetyFilterRead response = safetyFiltersClient.getSafetyFilter(safetyFilterId);
            log.debug("🛡️ SafetyFilter 상세 조회 성공 - ID: {}, 불용어: {}", 
                     response.getId(), response.getStopword());
            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 상세 조회 실패 - ID: {}, error: {}", 
                     safetyFilterId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 상세 조회 중 예상치 못한 오류 - ID: {}", 
                     safetyFilterId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                                       "SafetyFilter 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * SafetyFilter 수정
     * 
     * <p>기존 안전 필터의 정보를 수정합니다.
     * 키워드, 라벨, 그룹 등의 설정을 업데이트할 수 있습니다.</p>
     * 
     * @param safetyFilterId 수정할 SafetyFilter ID (UUID 형태)
     * @param request SafetyFilter 수정 요청 정보
     * @return 수정된 SafetyFilter 정보
     * @throws BusinessException SafetyFilter 수정 실패 시
     */
    public SafetyFilterRead updateSafetyFilter(String safetyFilterId, SafetyFilterUpdate request) {
        log.debug("🛡️ SafetyFilter 수정 요청 - ID: {}, 새 불용어: {}", 
                 safetyFilterId, request.getStopword());
        
        try {
            SafetyFilterRead response = safetyFiltersClient.updateSafetyFilter(safetyFilterId, request);
            log.debug("🛡️ SafetyFilter 수정 성공 - ID: {}, 불용어: {}", 
                     response.getId(), response.getStopword());
            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 수정 실패 - ID: {}, error: {}", 
                     safetyFilterId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 수정 중 예상치 못한 오류 - ID: {}", 
                     safetyFilterId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                                       "SafetyFilter 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * SafetyFilter 삭제
     * 
     * <p>지정된 안전 필터를 삭제합니다.</p>
     * 
     * @param safetyFilterId 삭제할 SafetyFilter ID (UUID 형태)
     * @throws BusinessException SafetyFilter 삭제 실패 시
     */
    public void deleteSafetyFilter(String safetyFilterId) {
        log.debug("🛡️ SafetyFilter 삭제 요청 - ID: {}", safetyFilterId);
        
        try {
            safetyFiltersClient.deleteSafetyFilter(safetyFilterId);
            log.debug("🛡️ SafetyFilter 삭제 성공 - ID: {}", safetyFilterId);
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 삭제 실패 - ID: {}, error: {}", 
                     safetyFilterId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 삭제 중 예상치 못한 오류 - ID: {}", 
                     safetyFilterId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                                       "SafetyFilter 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 텍스트 안전성 검사
     * 
     * <p>지정된 텍스트의 유해성을 판단합니다.
     * 등록된 안전 필터들을 사용하여 텍스트에 유해한 내용이 포함되어 있는지 검사합니다.</p>
     * 
     * @param clientSecret API 클라이언트 시크릿 (인증용)
     * @param projectId 프로젝트 ID (선택사항)
     * @param request 안전성 검사 요청 정보
     * @return 안전성 검사 결과
     * @throws BusinessException 안전성 검사 실패 시
     */
    public SafetyCheckOutput checkSafety(String clientSecret, String projectId, CheckSafeOrNot request) {
        log.debug("🛡️ 텍스트 안전성 검사 요청 - projectId: {}, 텍스트 길이: {}", 
                 projectId, request.getUtterance() != null ? request.getUtterance().length() : 0);
        
        try {
            SafetyCheckOutput response = safetyFiltersClient.checkSafety(clientSecret, projectId, request);
            log.debug("🛡️ 텍스트 안전성 검사 완료 - 안전: {}, 실행시간: {}초", 
                     response.getIsSafe(), response.getExecutionTime());
            return response;
        } catch (BusinessException e) {
            log.error("🛡️ 텍스트 안전성 검사 실패 - projectId: {}, error: {}", 
                     projectId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ 텍스트 안전성 검사 중 예상치 못한 오류 - projectId: {}", 
                     projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                                       "텍스트 안전성 검사에 실패했습니다: " + e.getMessage());
        }
    }
}