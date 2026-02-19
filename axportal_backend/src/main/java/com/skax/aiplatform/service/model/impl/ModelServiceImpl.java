package com.skax.aiplatform.service.model.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.sktai.model.dto.response.ModelRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelsRead;
import com.skax.aiplatform.client.sktai.model.service.SktaiModelsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import feign.FeignException;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.response.PageableInfo;
import com.skax.aiplatform.dto.model.response.GetModelTypesRes;
import com.skax.aiplatform.dto.model.response.ModelDetailRes;
import com.skax.aiplatform.dto.model.response.ModelRes;
import com.skax.aiplatform.mapper.model.ModelMapper;
import com.skax.aiplatform.service.model.ModelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 모델 관리 서비스 구현체
 * 
 * <p>모델의 CRUD 작업과 관련된 비즈니스 로직을 구현합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-01-16
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelServiceImpl implements ModelService {
    
    private final SktaiModelsService sktaiModelsService;
    private final ModelMapper modelMapper;
    
    /**
     * 모델 목록 조회
     */
    @Override
    public PageResponse<ModelRes> getModels(Pageable pageable, String sort, String filter, String search, String ids) {
        try {
            log.debug("모델 목록 조회 요청 - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
            
            // SKTAI 서비스 호출
            ModelsRead sktaiResponse = sktaiModelsService.readModels(
                pageable.getPageNumber() + 1, // SKTAI는 1부터 시작
                pageable.getPageSize(),
                sort,
                filter,
                search,
                ids
            );
            
            // 내부 DTO로 변환
            List<ModelRes> modelList = modelMapper.toModelResList(sktaiResponse.getData());
            
            // PageResponse 생성
            PageableInfo pageableInfo = PageableInfo.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .sort(sort)
                .build();
            
            long totalElements = sktaiResponse.getPayload() != null && sktaiResponse.getPayload().getPagination() != null 
                ? sktaiResponse.getPayload().getPagination().getTotal() 
                : modelList.size();
            
            PageResponse<ModelRes> response = PageResponse.<ModelRes>builder()
                .content(modelList)
                .pageable(pageableInfo)
                .totalElements(totalElements)
                .totalPages(calculateTotalPages(totalElements, pageable.getPageSize()))
                .first(pageable.getPageNumber() == 0)
                .last(pageable.getPageNumber() >= calculateTotalPages(totalElements, pageable.getPageSize()) - 1)
                .hasNext(pageable.getPageNumber() < calculateTotalPages(totalElements, pageable.getPageSize()) - 1)
                .hasPrevious(pageable.getPageNumber() > 0)
                .build();
            
            log.debug("모델 목록 조회 성공 - count: {}", modelList.size());
            return response;
            
        } catch (BusinessException e) {
            log.error("모델 목록 조회 실패 (BusinessException) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize(), e);
            throw e;
        } catch (FeignException e) {
            log.error("모델 목록 조회 실패 (FeignException) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 목록 조회에 실패했습니다: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("모델 목록 조회 실패 (NullPointerException) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 목록 조회 중 null 참조 오류가 발생했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("모델 목록 조회 실패 (IllegalArgumentException) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize(), e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "모델 목록 조회 파라미터가 잘못되었습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("모델 목록 조회 실패 (RuntimeException) - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 상세 조회
     */
    @Override
    public ModelDetailRes getModelById(String modelId) {
        try {
            log.debug("모델 상세 조회 요청 - modelId: {}", modelId);
            
            // SKTAI 서비스 호출
            ModelRead sktaiResponse = sktaiModelsService.readModel(modelId);
            
            // 내부 DTO로 변환
            ModelDetailRes response = modelMapper.toModelDetailRes(sktaiResponse);
            
            log.debug("모델 상세 조회 성공 - modelId: {}", modelId);
            return response;
            
        } catch (BusinessException e) {
            log.error("모델 상세 조회 실패 (BusinessException) - modelId: {}", modelId, e);
            throw e;
        } catch (FeignException e) {
            log.error("모델 상세 조회 실패 (FeignException) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 상세 조회에 실패했습니다: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("모델 상세 조회 실패 (NullPointerException) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 상세 조회 중 null 참조 오류가 발생했습니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("모델 상세 조회 실패 (IllegalArgumentException) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "모델 ID가 잘못되었습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("모델 상세 조회 실패 (RuntimeException) - modelId: {}", modelId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 타입 목록 조회
     */
    @Override
    public GetModelTypesRes getModelTypes() {
        try {
            log.debug("모델 타입 목록 조회 요청");
            
            // SKTAI 서비스 호출
            List<String> response = sktaiModelsService.readModelTypes();
            GetModelTypesRes modelTypesRes = new GetModelTypesRes();
            modelTypesRes.setTypes(response);
            
            log.debug("모델 타입 목록 조회 성공");
            return modelTypesRes;
            
        } catch (BusinessException e) {
            log.error("모델 타입 목록 조회 실패 (BusinessException)", e);
            throw e;
        } catch (FeignException e) {
            log.error("모델 타입 목록 조회 실패 (FeignException)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 타입 목록 조회에 실패했습니다: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("모델 타입 목록 조회 실패 (NullPointerException)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 타입 목록 조회 중 null 참조 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("모델 타입 목록 조회 실패 (RuntimeException)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 타입 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 모델 태그 목록 조회
     */
    @Override
    public List<String> getModelTags() {
        try {
            log.debug("모델 태그 목록 조회 요청");
            
            // SKTAI 서비스 호출
            List<String> response = sktaiModelsService.readModelTags();
            
            log.debug("모델 태그 목록 조회 성공");
            return response;
            
        } catch (BusinessException e) {
            log.error("모델 태그 목록 조회 실패 (BusinessException)", e);
            throw e;
        } catch (FeignException e) {
            log.error("모델 태그 목록 조회 실패 (FeignException)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 태그 목록 조회에 실패했습니다: " + e.getMessage());
        } catch (NullPointerException e) {
            log.error("모델 태그 목록 조회 실패 (NullPointerException)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 태그 목록 조회 중 null 참조 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("모델 태그 목록 조회 실패 (RuntimeException)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "모델 태그 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 전체 페이지 수 계산
     * 
     * @param totalElements 전체 요소 수
     * @param pageSize 페이지 크기
     * @return 전체 페이지 수
     */
    private int calculateTotalPages(long totalElements, int pageSize) {
        return (int) Math.ceil((double) totalElements / pageSize);
    }
}
