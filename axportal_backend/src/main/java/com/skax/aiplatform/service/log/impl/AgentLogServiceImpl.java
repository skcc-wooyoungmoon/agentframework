package com.skax.aiplatform.service.log.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.client.sktai.history.dto.response.AgentHistoryResponse;
import com.skax.aiplatform.client.sktai.history.service.SktaiHistoryService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.dto.log.response.AgentLogRes;
import com.skax.aiplatform.mapper.log.AgentLogMapper;
import com.skax.aiplatform.service.log.AgentLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentLogServiceImpl implements AgentLogService {

    private final SktaiHistoryService sktaiHistoryService;
    private final AgentLogMapper agentLogMapper;

    @Override
    public PageResponse<AgentLogRes> getAgentLogList(String fromDate, String toDate, 
                                                    Integer page, Integer size,
                                                    String fields, Boolean errorLogs,
                                                    String additionalHistoryOption,
                                                    String filter, String search, String sort) {
        
        log.info("Agent History 목록 조회 요청 Service - fromDate={}, toDate={}, page={}, errorLogs={}, size={}, sort={}, filter={}, search={}", 
                fromDate, toDate, page, errorLogs, size, sort, filter, search);
        
        try {
            // Agent History 목록 조회
            AgentHistoryResponse response = sktaiHistoryService.getAgentHistoryList(
                    fromDate, toDate, page, size, fields, errorLogs,
                    additionalHistoryOption, filter, search, sort);

            // Agent History 목록 변환
            List<AgentLogRes> agentLogList = response.getData().stream()
                    .map(agentLogMapper::from)
                    .collect(Collectors.toList());

            // ADXP Pagination을 PageResponse로 변환
            return PaginationUtils.toPageResponseFromAdxp(response.getPayload().getPagination(), agentLogList);

        } catch (BusinessException e) {
            log.error("Agent History 목록 조회 실패 (비즈니스 오류) - fromDate={}, toDate={}, page={}, size={}, sort={}, filter={}, search={}, error={}", 
                    fromDate, toDate, page, size, sort, filter, search, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent History 목록을 조회할 수 없습니다: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Agent History 목록 조회 실패 (파라미터 오류) - fromDate={}, toDate={}, page={}, size={}, sort={}, filter={}, search={}, error={}", 
                    fromDate, toDate, page, size, sort, filter, search, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Agent History 목록 조회 파라미터가 잘못되었습니다: " + e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("Agent History 목록 조회 실패 (Null 포인터 오류) - fromDate={}, toDate={}, page={}, size={}, sort={}, filter={}, search={}, error={}", 
                    fromDate, toDate, page, size, sort, filter, search, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Agent History 목록 조회 중 필수 데이터가 누락되었습니다.", e);
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("Agent History 목록 조회 실패 (외부 API 호출 오류) - fromDate={}, toDate={}, page={}, size={}, sort={}, filter={}, search={}, error={}", 
                    fromDate, toDate, page, size, sort, filter, search, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "외부 API 호출에 실패했습니다: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Agent History 목록 조회 실패 (런타임 오류) - fromDate={}, toDate={}, page={}, size={}, sort={}, filter={}, search={}, error={}", 
                    fromDate, toDate, page, size, sort, filter, search, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent History 목록을 조회할 수 없습니다: " + e.getMessage(), e);
        }
    }
}