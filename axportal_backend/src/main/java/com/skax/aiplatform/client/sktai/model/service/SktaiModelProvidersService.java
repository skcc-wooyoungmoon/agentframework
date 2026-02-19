package com.skax.aiplatform.client.sktai.model.service;

import com.skax.aiplatform.client.sktai.model.SktaiModelProvidersClient;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelProviderCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelProviderUpdate;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelProviderRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelProvidersRead;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Model Providers 서비스
 * 
 * <p>SKTAI Model API의 Model Providers 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 비즈니스 규칙을 적용합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Provider 관리</strong>: 모델 제공자의 생성, 조회, 수정, 삭제</li>
 *   <li><strong>목록 조회</strong>: 페이징, 정렬, 검색을 지원하는 제공자 목록 조회</li>
 *   <li><strong>예외 처리</strong>: 외부 API 호출 시 발생하는 예외를 내부 예외로 변환</li>
 *   <li><strong>로깅</strong>: 모든 API 호출에 대한 요청/응답 로깅</li>
 * </ul>
 * 
 * <h3>에러 처리:</h3>
 * <ul>
 *   <li>외부 API 오류 시 BusinessException으로 변환</li>
 *   <li>상세한 에러 메시지와 함께 로깅</li>
 *   <li>원본 예외 정보 보존</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiModelProvidersService {

    private final SktaiModelProvidersClient sktaiModelProvidersClient;

    /**
     * Model Provider 등록
     * 
     * <p>새로운 모델 제공자를 등록합니다.</p>
     * 
     * @param request Provider 생성 요청 정보
     * @return 생성된 Provider 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelProviderRead registerModelProvider(ModelProviderCreate request) {
        log.debug("Model Provider 등록 요청 - name: {}", request.getName());
        
        try {
            ModelProviderRead response = sktaiModelProvidersClient.registerModelProvider(request);
            log.debug("Model Provider 등록 성공 - id: {}, name: {}", response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Model Provider 등록 실패 - name: {}", request.getName(), e);
            throw e;
        } catch (Exception e) {
            log.error("Model Provider 등록 실패 - name: {}", request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Provider 등록에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Provider 목록 조회
     * 
     * <p>등록된 모든 모델 제공자 목록을 페이징하여 조회합니다.</p>
     * 
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @param sort 정렬 기준
     * @param filter 필터 조건
     * @param search 검색어
     * @return 페이징된 Provider 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelProvidersRead readModelProviders(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Model Provider 목록 조회 요청 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            ModelProvidersRead response = sktaiModelProvidersClient.readModelProviders(page, size, sort, filter, search);
            log.debug("Model Provider 목록 조회 성공 - 조회된 개수: {}", response.getData().size());
            return response;
        } catch (BusinessException e) {
            log.error("Model Provider 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw e;
        } catch (Exception e) {
            log.error("Model Provider 목록 조회 실패 - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Provider 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Provider 목록 조회(미페이징)
     * 
     * <p>등록된 모든 모델 제공자 목록을 조회합니다. 정렬, 필터링, 검색 기능을 지원합니다.</p>
     * 
     * @return 모델 제공자 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelProvidersRead readModelProvidersNoPaged() {
        log.debug("Model Provider 목록 조회 요청");
        
        try {
            ModelProvidersRead response = sktaiModelProvidersClient.readModelProvidersNoPaged();
            log.debug("Model Provider 목록 조회 성공 - 조회된 개수: {}", response.getData().size());
            return response;
        } catch (BusinessException e) {
            log.error("Model Provider 목록 조회 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("Model Provider 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Provider 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Provider 상세 조회
     * 
     * <p>지정된 ID의 모델 제공자 상세 정보를 조회합니다.</p>
     * 
     * @param providerId Provider ID (UUID 형식)
     * @return Provider 상세 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelProviderRead readModelProvider(String providerId) {
        log.debug("Model Provider 상세 조회 요청 - providerId: {}", providerId);
        
        try {
            ModelProviderRead response = sktaiModelProvidersClient.readModelProvider(providerId);
            log.debug("Model Provider 상세 조회 성공 - id: {}, name: {}", response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Model Provider 상세 조회 실패 - providerId: {}", providerId, e);
            throw e;
        } catch (Exception e) {
            log.error("Model Provider 상세 조회 실패 - providerId: {}", providerId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Provider 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Provider 수정
     * 
     * <p>지정된 ID의 모델 제공자 정보를 수정합니다.</p>
     * 
     * @param providerId Provider ID (UUID 형식)
     * @param request Provider 수정 요청 정보
     * @return 수정된 Provider 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelProviderRead editModelProvider(String providerId, ModelProviderUpdate request) {
        log.debug("Model Provider 수정 요청 - providerId: {}, name: {}", providerId, request.getName());
        
        try {
            ModelProviderRead response = sktaiModelProvidersClient.editModelProvider(providerId, request);
            log.debug("Model Provider 수정 성공 - id: {}, name: {}", response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Model Provider 수정 실패 - providerId: {}", providerId, e);
            throw e;
        } catch (Exception e) {
            log.error("Model Provider 수정 실패 - providerId: {}", providerId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Provider 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Model Provider 삭제
     * 
     * <p>지정된 ID의 모델 제공자를 삭제합니다.</p>
     * 
     * @param providerId Provider ID (UUID 형식)
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public void removeModelProvider(String providerId) {
        log.debug("Model Provider 삭제 요청 - providerId: {}", providerId);
        
        try {
            sktaiModelProvidersClient.removeModelProvider(providerId);
            log.debug("Model Provider 삭제 성공 - providerId: {}", providerId);
        } catch (BusinessException e) {
            log.error("Model Provider 삭제 실패 - providerId: {}", providerId, e);
            throw e;
        } catch (Exception e) {
            log.error("Model Provider 삭제 실패 - providerId: {}", providerId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Model Provider 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
