package com.skax.aiplatform.client.sktai.knowledge.service;

import com.skax.aiplatform.client.sktai.knowledge.SktaiChunkStoresClient;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ChunkStoreCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ChunkStoreUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ChunkStoreResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.MultiResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Knowledge ChunkStore 관리 서비스
 * 
 * <p>SKTAI Knowledge 시스템의 ChunkStore 관리 기능을 제공하는 비즈니스 로직 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 비즈니스 규칙을 적용합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>ChunkStore 생성</strong>: 새로운 청크 저장소 등록</li>
 *   <li><strong>ChunkStore 목록 조회</strong>: 페이징된 저장소 목록 검색</li>
 *   <li><strong>ChunkStore 상세 조회</strong>: 특정 저장소 정보 확인</li>
 *   <li><strong>ChunkStore 수정</strong>: 기존 저장소 설정 변경</li>
 *   <li><strong>ChunkStore 삭제</strong>: 저장소 제거</li>
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
public class SktaiChunkStoresService {
    
    private final SktaiChunkStoresClient sktaiChunkStoresClient;
    
    /**
     * ChunkStore 목록 조회
     * 
     * <p>프로젝트에 등록된 ChunkStore 목록을 페이징 형태로 조회합니다.
     * 검색, 필터링, 정렬 기능을 통해 원하는 저장소를 효율적으로 찾을 수 있습니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지당 항목 수
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return 페이징된 ChunkStore 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public MultiResponse getChunkStores(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("ChunkStore 목록 조회 요청 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            MultiResponse response = sktaiChunkStoresClient.getChunkStores(page, size, sort, filter, search);
            log.debug("ChunkStore 목록 조회 성공 - page: {}, size: {}", page, size);
            return response;
        } catch (BusinessException e) {
            log.error("ChunkStore 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("ChunkStore 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "ChunkStore 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * ChunkStore 신규 생성
     * 
     * <p>새로운 ChunkStore를 생성하고 등록합니다.
     * 연결 정보의 유효성을 검증하고 저장소와의 연결을 확인합니다.</p>
     * 
     * @param request ChunkStore 생성 요청 정보
     * @return 생성된 ChunkStore 정보 (ID 포함)
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ChunkStoreResponse createChunkStore(ChunkStoreCreate request) {
        log.debug("ChunkStore 생성 요청 - name: {}, type: {}", request.getName(), request.getType());
        
        try {
            ChunkStoreResponse response = sktaiChunkStoresClient.createChunkStore(request);
            log.debug("ChunkStore 생성 성공 - id: {}, name: {}", response.getId(), request.getName());
            return response;
        } catch (BusinessException e) {
            log.error("ChunkStore 생성 실패 (BusinessException) - name: {}, type: {}, message: {}", request.getName(), request.getType(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("ChunkStore 생성 실패 (예상치 못한 오류) - name: {}, type: {}", request.getName(), request.getType(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "ChunkStore 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * ChunkStore 상세 조회
     * 
     * <p>특정 ChunkStore의 상세 정보를 조회합니다.
     * 연결 상태와 설정 정보를 포함하여 반환합니다.</p>
     * 
     * @param chunkStoreId ChunkStore 고유 식별자
     * @return ChunkStore 상세 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ChunkStoreResponse getChunkStore(String chunkStoreId) {
        log.debug("ChunkStore 상세 조회 요청 - id: {}", chunkStoreId);
        
        try {
            ChunkStoreResponse response = sktaiChunkStoresClient.getChunkStore(chunkStoreId);
            log.debug("ChunkStore 상세 조회 성공 - id: {}, name: {}", chunkStoreId, response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("ChunkStore 상세 조회 실패 (BusinessException) - id: {}, message: {}", chunkStoreId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("ChunkStore 상세 조회 실패 (예상치 못한 오류) - id: {}", chunkStoreId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "ChunkStore 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * ChunkStore 정보 수정
     * 
     * <p>기존 ChunkStore의 설정을 수정합니다.
     * 연결 정보 변경 시 새로운 연결의 유효성을 검증합니다.</p>
     * 
     * @param chunkStoreId ChunkStore 고유 식별자
     * @param request ChunkStore 수정 요청 정보
     * @return 수정된 ChunkStore 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ChunkStoreResponse updateChunkStore(String chunkStoreId, ChunkStoreUpdate request) {
        log.debug("ChunkStore 수정 요청 - id: {}, name: {}, type: {}", 
                chunkStoreId, request.getName(), request.getType());
        
        try {
            ChunkStoreResponse response = sktaiChunkStoresClient.updateChunkStore(chunkStoreId, request);
            log.debug("ChunkStore 수정 성공 - id: {}, name: {}", chunkStoreId, request.getName());
            return response;
        } catch (BusinessException e) {
            log.error("ChunkStore 수정 실패 (BusinessException) - id: {}, name: {}, message: {}", chunkStoreId, request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("ChunkStore 수정 실패 (예상치 못한 오류) - id: {}, name: {}", chunkStoreId, request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "ChunkStore 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * ChunkStore 삭제
     * 
     * <p>특정 ChunkStore를 시스템에서 삭제합니다.
     * 연관된 데이터 확인 후 안전하게 제거됩니다.</p>
     * 
     * @param chunkStoreId ChunkStore 고유 식별자
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public void deleteChunkStore(String chunkStoreId) {
        log.debug("ChunkStore 삭제 요청 - id: {}", chunkStoreId);
        
        try {
            sktaiChunkStoresClient.deleteChunkStore(chunkStoreId);
            log.debug("ChunkStore 삭제 성공 - id: {}", chunkStoreId);
        } catch (BusinessException e) {
            log.error("ChunkStore 삭제 실패 (BusinessException) - id: {}, message: {}", chunkStoreId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("ChunkStore 삭제 실패 (예상치 못한 오류) - id: {}", chunkStoreId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "ChunkStore 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
