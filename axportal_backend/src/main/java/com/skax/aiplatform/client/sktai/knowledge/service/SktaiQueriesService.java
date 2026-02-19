package com.skax.aiplatform.client.sktai.knowledge.service;

import com.skax.aiplatform.client.sktai.knowledge.SktaiQueriesClient;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RetrievalAdvancedRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RetrievalRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.TestRetrievalAdvancedRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.TestRetrievalRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RetrievalResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Knowledge 검색 서비스
 * 
 * <p>SKTAI Knowledge 시스템의 문서 검색 및 질의 기능을 제공하는 비즈니스 로직 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 비즈니스 규칙을 적용합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>기본 검색</strong>: 단순 질의를 통한 문서 검색</li>
 *   <li><strong>고급 검색</strong>: 상세 옵션을 포함한 문서 검색</li>
 *   <li><strong>테스트 검색</strong>: 개발/디버깅용 검색 기능</li>
 *   <li><strong>고급 테스트 검색</strong>: 상세 옵션을 포함한 테스트 검색</li>
 * </ul>
 * 
 * <h3>예외 처리:</h3>
 * <ul>
 *   <li>외부 API 호출 실패 시 BusinessException 발생</li>
 *   <li>네트워크 오류, 타임아웃 등에 대한 통합 예외 처리</li>
 *   <li>상세한 오류 로깅으로 디버깅 지원</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiQueriesService {
    
    private final SktaiQueriesClient sktaiQueriesClient;
    
    /**
     * 기본 문서 검색
     * 
     * <p>Knowledge Repository에서 기본 옵션을 사용하여 문서를 검색합니다.
     * 간단한 질의와 기본 설정으로 빠른 검색을 수행합니다.</p>
     * 
     * @param request 기본 검색 요청 정보
     * @return 검색 결과 (문서, 청크, 점수, 메타데이터 포함)
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public RetrievalResponse searchDocuments(RetrievalRequest request) {
        log.debug("기본 문서 검색 요청 - query: {}, repoId: {}", 
                request.getQueryText(), request.getRepoId());
        
        try {
            RetrievalResponse response = sktaiQueriesClient.queries(request);
            log.debug("기본 문서 검색 성공 - query: {}", request.getQueryText());
            return response;
        } catch (BusinessException e) {
            log.error("기본 문서 검색 실패 (BusinessException) - query: {}, repoId: {}, message: {}", 
                    request.getQueryText(), request.getRepoId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("기본 문서 검색 실패 (예상치 못한 오류) - query: {}, repoId: {}", 
                    request.getQueryText(), request.getRepoId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "문서 검색에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 고급 문서 검색
     * 
     * <p>Knowledge Repository에서 고급 옵션을 사용하여 문서를 검색합니다.
     * 검색 알고리즘, 점수 임계값, 반환 개수 등을 세밀하게 조정할 수 있습니다.</p>
     * 
     * @param request 고급 검색 요청 정보 (검색 옵션 포함)
     * @return 검색 결과 (문서, 청크, 점수, 메타데이터 포함)
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public RetrievalResponse searchDocumentsAdvanced(RetrievalAdvancedRequest request) {
        log.debug("고급 문서 검색 요청 - query: {}, repoId: {}", 
                request.getQueryText(), request.getRepoId());
        
        try {
            RetrievalResponse response = sktaiQueriesClient.queriesAdvanced(request);
            log.debug("고급 문서 검색 성공 - query: {}", request.getQueryText());
            return response;
        } catch (BusinessException e) {
            log.error("고급 문서 검색 실패 (BusinessException) - query: {}, repoId: {}, message: {}", 
                    request.getQueryText(), request.getRepoId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("고급 문서 검색 실패 (예상치 못한 오류) - query: {}, repoId: {}", 
                    request.getQueryText(), request.getRepoId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "고급 문서 검색에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 테스트 문서 검색
     * 
     * <p>개발 및 디버깅 목적으로 Knowledge Repository에서 문서를 검색합니다.
     * 특정 Collection을 지정하여 테스트할 수 있습니다.</p>
     * 
     * @param request 테스트 검색 요청 정보
     * @return 검색 결과 (문서, 청크, 점수, 메타데이터 포함)
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public RetrievalResponse testSearchDocuments(TestRetrievalRequest request) {
        log.debug("테스트 문서 검색 요청 - query: {}, repoId: {}, collectionId: {}", 
                request.getQueryText(), request.getRepoId(), request.getCollectionId());
        
        try {
            RetrievalResponse response = sktaiQueriesClient.testQueries(request);
            log.debug("테스트 문서 검색 성공 - query: {}", request.getQueryText());
            return response;
        } catch (BusinessException e) {
            log.error("테스트 문서 검색 실패 (BusinessException) - query: {}, repoId: {}, message: {}", 
                    request.getQueryText(), request.getRepoId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("테스트 문서 검색 실패 (예상치 못한 오류) - query: {}, repoId: {}", 
                    request.getQueryText(), request.getRepoId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "테스트 문서 검색에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 고급 테스트 문서 검색
     * 
     * <p>개발 및 디버깅 목적으로 고급 옵션을 사용하여 Knowledge Repository에서 문서를 검색합니다.
     * 검색 알고리즘, 점수 임계값, 특정 파일 제한 등 모든 옵션을 테스트할 수 있습니다.</p>
     * 
     * @param request 고급 테스트 검색 요청 정보 (모든 검색 옵션 포함)
     * @return 검색 결과 (문서, 청크, 점수, 메타데이터 포함)
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public RetrievalResponse testSearchDocumentsAdvanced(TestRetrievalAdvancedRequest request) {
        log.debug("고급 테스트 문서 검색 요청 - query: {}, repoId: {}, collectionId: {}, topkDocs: {}, topkChunks: {}", 
                request.getQueryText(), request.getRepoId(), request.getCollectionId(),
                request.getTopkDocs(), request.getTopkChunks());
        
        try {
            RetrievalResponse response = sktaiQueriesClient.testQueriesAdvanced(request);
            log.debug("고급 테스트 문서 검색 성공 - query: {}", request.getQueryText());
            return response;
        } catch (BusinessException e) {
            log.error("고급 테스트 문서 검색 실패 (BusinessException) - query: {}, repoId: {}, message: {}", 
                    request.getQueryText(), request.getRepoId(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("고급 테스트 문서 검색 실패 (예상치 못한 오류) - query: {}, repoId: {}", 
                    request.getQueryText(), request.getRepoId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "고급 테스트 문서 검색에 실패했습니다: " + e.getMessage());
        }
    }
}
