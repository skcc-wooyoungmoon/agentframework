package com.skax.aiplatform.service.data.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.sktai.data.dto.response.ProcessorDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.ProcessorList;
import com.skax.aiplatform.client.sktai.data.service.SktaiDataProcessorsService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.data.response.DataToolProcDetailRes;
import com.skax.aiplatform.dto.data.response.DataToolProcRes;
import com.skax.aiplatform.mapper.data.DataToolProcMapper;
import com.skax.aiplatform.service.data.DataToolProcService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataToolProcServiceImpl implements DataToolProcService {

    private final SktaiDataProcessorsService sktaiDataProcessorsService;
    private final DataToolProcMapper dataToolProcMapper;

    @Override
    public PageResponse<DataToolProcRes> getProcList(Pageable pageable, String sort, String filter, String search) {
        // log.info("-----------------------------------------------------------------------------------------");
        // log.info("[ Execute Service DataToolProcServiceImpl.getProcList ]");
        // log.info("Page : {}", pageable.getPageNumber());
        // log.info("Size : {}", pageable.getPageSize());
        // log.info("Sort : {}", sort);
        // log.info("Filter : {}", filter);
        // log.info("Search : {}", search);
        // log.info("-----------------------------------------------------------------------------------------");

        try {
            // 1) 외부(SKTAI) API 호출
            ProcessorList response = sktaiDataProcessorsService.getProcessors(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort,
                    filter,
                    search);
            // log.debug("sktaiDataProcessorsService.getProcessors => {}", response);

            // 2) 목록 매핑 (Processor -> DataToolProcRes)
            List<DataToolProcRes> content = Optional.ofNullable(response)
                    .map(ProcessorList::getData)
                    .orElseGet(List::of)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(dataToolProcMapper::from)
                    .collect(Collectors.toList());

            // 3) 페이지네이션 정보 확인 (null-safe 체인으로 NPE 경고 제거)
            // Pagination pagination = Optional.ofNullable(response)
            // .map(ProcessorList::getPayload)
            // .map(com.skax.aiplatform.client.sktai.common.dto.Payload::getPagination)
            // .orElseThrow(() -> {
            // // log.error("SKTAI API 응답에 페이지네이션 정보가 없습니다");
            // return new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "페이지네이션 정보가
            // 없습니다");
            // });

            // // 4) PageableInfo 생성
            // PageableInfo pageableInfo = PageableInfo.builder()
            // .page(Optional.ofNullable(pagination.getPage()).orElse(1))
            // .size(Optional.ofNullable(pagination.getItemsPerPage()).orElse(pageable.getPageSize()))
            // .sort(sort != null ? sort : "")
            // .build();

            // // 5) PageResponse 생성
            // return PageResponse.<DataToolProcRes>builder()
            // .content(content)
            // .pageable(pageableInfo)
            // .totalElements(Optional.ofNullable(pagination.getTotal()).map(Integer::longValue).orElse(0L))
            // .totalPages(Optional.ofNullable(pagination.getLastPage()).orElse(1))
            // .first(Optional.ofNullable(pagination.getPage()).orElse(1) == 1)
            // .last(Objects.equals(pagination.getPage(), pagination.getLastPage()))
            // .hasNext(pagination.getNextPageUrl() != null)
            // .hasPrevious(pagination.getPrevPageUrl() != null)
            // .build();

            PageResponse<DataToolProcRes> pageResponse = PaginationUtils
                    .toPageResponseFromAdxp(response.getPayload(), content);
            return pageResponse;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            // log.error("데이터 도구 프로세서 목록 조회 실패", e);
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 프로세서 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public DataToolProcDetailRes getProcById(String processorId) {
        DataToolProcDetailRes result = null;
        // log.info("-----------------------------------------------------------------------------------------");
        // log.info("[ Execute Service DataToolProcServiceImpl.getInfPromptById ]");
        // log.info("Proccessor_id : {}", processorId);
        // log.info("-----------------------------------------------------------------------------------------");

        try {
            ProcessorDetail response = sktaiDataProcessorsService.getProcessor(UUID.fromString(processorId));
            // log.debug("sktaiAgentInferencePromptsService.getProcessor => {}", response);
            result = dataToolProcMapper.from(response);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.EXTERNAL_API_ERROR,
                    "데이터 도구 프로세서 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
}