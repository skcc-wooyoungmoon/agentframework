package com.skax.aiplatform.client.sktai.knowledge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.knowledge.SktaiVectorDbsClient;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectordbImportRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ArgResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBCreateResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBDetailResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBUpdateResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDbsResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectordbImportResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKTAI Knowledge Vector DB 관리 서비스
 * 
 * <p>SKTAI Knowledge API의 Vector DB 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * Vector DB 생성, 조회, 수정, 삭제 등의 작업을 수행하며, 예외 처리와 로깅을 담당합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Vector DB 생성</strong>: 새로운 Vector DB 등록 및 검증</li>
 *   <li><strong>Vector DB 목록 조회</strong>: 페이징된 Vector DB 목록 조회</li>
 *   <li><strong>Vector DB 상세 조회</strong>: 특정 Vector DB 정보 조회</li>
 *   <li><strong>Vector DB 수정</strong>: Vector DB 설정 업데이트</li>
 *   <li><strong>Vector DB 삭제</strong>: Vector DB 제거</li>
 * </ul>
 * 
 * <h3>예외 처리:</h3>
 * <ul>
 *   <li>외부 API 호출 실패 시 BusinessException 발생</li>
 *   <li>모든 작업에 대한 상세 로깅 제공</li>
 *   <li>재시도 로직은 Feign Client 설정에서 처리</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiVectorDbsService {

    private final SktaiVectorDbsClient sktaiVectorDbsClient;

    /**
     * Vector DB 목록 조회
     * 
     * <p>등록된 Vector DB 목록을 페이징하여 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return Vector DB 목록 응답
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public VectorDbsResponse getVectorDbs(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Vector DB 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                 page, size, sort, filter, search);
        
        try {
            VectorDbsResponse response = sktaiVectorDbsClient.getVectorDbs(page, size, sort, filter, search);
            log.debug("Vector DB 목록 조회 성공 - 결과 수: {}", 
                     response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            log.error("Vector DB 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Vector DB 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Vector DB 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Vector DB 신규 등록
     * 
     * <p>Knowledge에 사용할 새로운 Vector DB를 등록합니다.
     * Vector DB 종류와 접속 정보 검증을 포함합니다.</p>
     * 
     * @param request Vector DB 생성 요청 정보
     * @return 생성된 Vector DB ID
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public VectorDBCreateResponse addVectorDb(VectorDBCreate request) {
        log.debug("Vector DB 생성 요청 - name: {}, type: {}", request.getName(), request.getType());
        
        try {
            VectorDBCreateResponse response = sktaiVectorDbsClient.addVectorDb(request);
            log.debug("Vector DB 생성 성공 - vectorDbId: {}", response.getVectorDbId());
            return response;
        } catch (BusinessException e) {
            log.error("Vector DB 생성 실패 (BusinessException) - name: {}, type: {}, message: {}", request.getName(), request.getType(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Vector DB 생성 실패 (예상치 못한 오류) - name: {}, type: {}", request.getName(), request.getType(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Vector DB 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Vector DB 정보 조회
     * 
     * <p>지정된 ID의 Vector DB 상세 정보를 조회합니다.
     * 연결 정보와 설정 상태를 확인할 수 있습니다.</p>
     * 
     * @param vectorDbId Vector DB 고유 식별자
     * @return Vector DB 상세 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public VectorDBDetailResponse getVectorDb(String vectorDbId) {
        log.debug("Vector DB 조회 요청 - vectorDbId: {}", vectorDbId);
        
        try {
            VectorDBDetailResponse response = sktaiVectorDbsClient.getVectorDb(vectorDbId);
            log.debug("Vector DB 조회 성공 - vectorDbId: {}, name: {}", vectorDbId, response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Vector DB 조회 실패 (BusinessException) - vectorDbId: {}, message: {}", vectorDbId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Vector DB 조회 실패 (예상치 못한 오류) - vectorDbId: {}", vectorDbId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Vector DB 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Vector DB 정보 수정
     * 
     * <p>기존 Vector DB의 설정 정보를 업데이트합니다.
     * 이름, 연결 정보, 기본 설정 등을 변경할 수 있습니다.</p>
     * 
     * @param vectorDbId Vector DB 고유 식별자
     * @param request Vector DB 수정 요청 정보
     * @return 수정된 Vector DB 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public VectorDBUpdateResponse updateVectorDb(String vectorDbId, VectorDBUpdate request) {
        log.debug("Vector DB 수정 요청 - vectorDbId: {}, name: {}", vectorDbId, request.getName());
        
        try {
            VectorDBUpdateResponse response = sktaiVectorDbsClient.updateVectorDb(vectorDbId, request);
            log.debug("Vector DB 수정 성공 - vectorDbId: {}", response.getVectorDbId());
            return response;
        } catch (BusinessException e) {
            log.error("Vector DB 수정 실패 (BusinessException) - vectorDbId: {}, name: {}, message: {}", vectorDbId, request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Vector DB 수정 실패 (예상치 못한 오류) - vectorDbId: {}, name: {}", vectorDbId, request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Vector DB 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Vector DB 정보 삭제
     * 
     * <p>지정된 Vector DB를 시스템에서 완전히 제거합니다.
     * 삭제 전 연결된 Knowledge Repository 확인이 권장됩니다.</p>
     * 
     * @param vectorDbId Vector DB 고유 식별자
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public void deleteVectorDb(String vectorDbId) {
        log.debug("Vector DB 삭제 요청 - vectorDbId: {}", vectorDbId);
        
        try {
            sktaiVectorDbsClient.deleteVectorDb(vectorDbId);
            log.debug("Vector DB 삭제 성공 - vectorDbId: {}", vectorDbId);
        } catch (BusinessException e) {
            log.error("Vector DB 삭제 실패 (BusinessException) - vectorDbId: {}, message: {}", vectorDbId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Vector DB 삭제 실패 (예상치 못한 오류) - vectorDbId: {}", vectorDbId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Vector DB 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Tool 연결 정보 조회
     * 
     * <p> Tool의 연결 정보 정보를 조회합니다.</p>
     * 
     * @return Tool 상세 정보
     * 
     * @apiNote 반환되는 정보에는 연결 설정과 상태 정보가 포함됩니다.
     */
    public List<ArgResponse> getConnectionArgs() {
        log.debug("Tool 연결 정보 조회 요청");
        
        try {
            List<ArgResponse> response = sktaiVectorDbsClient.getConnectionArgs();
            log.debug("Tool 연결 정보 조회 성공 - 결과 수: {}", response != null ? response.size() : 0);
            return response;
        } catch (BusinessException e) {
            log.error("Tool 연결 정보 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 연결 정보 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "Tool 연결 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Vector Database Import
     * 
     * <p>외부 Vector Database를 SKTAI Knowledge 시스템으로 Import합니다.
     * Knowledge Repository 생성 시 사용할 수 있는 Vector Database를 등록합니다.</p>
     * 
     * <h4>지원하는 연결 정보 형식:</h4>
     * <ul>
     *   <li><strong>Milvus</strong>: host, port, user, password</li>
     *   <li><strong>Azure AI Search</strong>: endpoint, key</li>
     *   <li><strong>OpenSearch/ElasticSearch</strong>: hosts, username, password, use_ssl</li>
     * </ul>
     * 
     * @param request Vector Database Import 요청 정보
     * @return Import된 Vector Database ID
     * @throws BusinessException Vector Database Import 실패 시
     */
    public VectordbImportResponse importVectorDatabase(VectordbImportRequest request) {
        try {
            log.info("SKTAI Vector Database Import 시작 - name: {}, type: {}, isDefault: {}", 
                    request.getName(), request.getType(), request.getIsDefault());
            
            VectordbImportResponse response = sktaiVectorDbsClient.importVectordb(request);
            
            log.info("SKTAI Vector Database Import 완료 - vectorDbId: {}", 
                    response.getVectorDbId());
            return response;
            
        } catch (feign.FeignException.Forbidden e) {
            log.error("SKTAI Vector Database Import 권한 부족 - name: {}, error: {}", request.getName(), e.getMessage());
            throw new BusinessException(ErrorCode.FORBIDDEN, 
                "Vector Database Import 권한이 없습니다");
        } catch (feign.FeignException.Conflict e) {
            log.error("SKTAI Vector Database 이름 중복 - name: {}, error: {}", request.getName(), e.getMessage());
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, 
                "동일한 이름의 Vector Database가 이미 존재합니다: " + request.getName());
        } catch (feign.FeignException.UnprocessableEntity e) {
            log.error("SKTAI Vector Database Import 요청 데이터 검증 실패 - name: {}, error: {}", request.getName(), e.getMessage());
            throw new BusinessException(ErrorCode.EXTERNAL_API_VALIDATION_ERROR, 
                "Vector Database Import 요청 데이터가 올바르지 않습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("SKTAI Vector Database Import 실패 - name: {}, error: {}", request.getName(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                "SKTAI Vector Database Import에 실패했습니다: " + e.getMessage());
        }
    }
}
