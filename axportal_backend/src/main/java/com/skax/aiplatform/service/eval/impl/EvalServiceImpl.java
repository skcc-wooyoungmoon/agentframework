package com.skax.aiplatform.service.eval.impl;

import com.skax.aiplatform.client.datumo.api.dto.response.TaskListResponse;
import com.skax.aiplatform.client.datumo.api.service.DatumoApiService;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientsRead;
import com.skax.aiplatform.client.sktai.auth.service.SktaiProjectService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.service.eval.EvalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Evaluation Service Implementation
 *
 * <p>
 * 평가(Evaluation) 관련 비즈니스 로직을 구현하는 서비스 클래스입니다.
 * Datumo API를 통해 평가 Task 목록을 조회하는 기능을 제공합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>Task 목록 조회</strong>: Authorization 헤더에서 받은 액세스 토큰으로 Datumo 시스템에서
 * Task 목록 조회</li>
 * <li><strong>예외 처리</strong>: API 호출 실패 시 적절한 예외 변환</li>
 * <li><strong>로깅</strong>: 요청/응답 로깅 및 에러 추적</li>
 * </ul>
 *
 * @author System
 * @since 2025-01-27
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EvalServiceImpl implements EvalService {

    private final DatumoApiService datumoApiService;
    private final SktaiProjectService sktaiProjectService;

    /**
     * Datumo 시스템에서 Task 목록 조회
     *
     * <p>
     * Authorization 헤더에서 받은 액세스 토큰을 사용하여
     * 지정된 조건으로 Task 목록을 조회합니다.
     * </p>
     *
     * @param accessToken 액세스 토큰 (Authorization 헤더에서 추출)
     * @param category    Task 카테고리
     * @param page        페이지 번호 (1부터 시작)
     * @param pageSize    페이지당 항목 수
     * @param search      검색어 (선택적)
     * @return Task 목록 조회 결과
     */
    @Override
    public TaskListResponse getTaskList(String accessToken, String group, String category, Integer page, Integer pageSize, String search) {
        log.info("Eval Task 목록 조회 요청 - 그룹: {}, 카테고리: {}, 페이지: {}/{}, 검색어: {}", group, category, page, pageSize, search);

        try {
            // 프로젝트 ID 조회 - 첫 번째 프로젝트만 필요하므로 최소한의 데이터만 요청
            String projectId = getFirstProjectId();

            // Datumo API를 통한 Task 목록 조회
            TaskListResponse response = datumoApiService.getTaskList(accessToken, group, projectId, category, page, pageSize, search);

            log.info("Eval Task 목록 조회 성공 - 프로젝트: {}, 전체 데이터: {}, 전체 페이지: {}, 조회된 Task 수: {}", projectId, response.getTotalDataCount(), response.getTotalPageCount(), response.getTasks() != null ? response.getTasks().size() : 0);

            return response;

        } catch (BusinessException e) {
            log.error("Eval Task 목록 조회 실패 (BusinessException) - 그룹: {}, 카테고리: {}, 페이지: {}/{}", group, category, page, pageSize, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Eval Task 목록 조회 실패 (RuntimeException) - 그룹: {}, 카테고리: {}, 페이지: {}/{}", group, category, page, pageSize, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Task 목록 조회에 실패했습니다.");
        } catch (Exception e) {
            log.error("Eval Task 목록 조회 실패 (예상치 못한 오류) - 그룹: {}, 카테고리: {}, 페이지: {}/{}", group, category, page, pageSize, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Task 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 첫 번째 프로젝트 ID 조회
     *
     * <p>
     * 사용자가 접근 가능한 첫 번째 프로젝트의 ID를 안전하게 조회합니다.
     * 프로젝트가 없는 경우 적절한 예외를 발생시킵니다.
     * </p>
     *
     * @return 첫 번째 프로젝트 ID
     * @throws BusinessException 프로젝트가 없거나 조회 실패 시
     */
    private String getFirstProjectId() {
        try {
            // 최소한의 데이터만 요청 (첫 번째 프로젝트만)
            ClientsRead clientsRead = sktaiProjectService.getProjects(1, 1, null, null, null);

            // 안전한 배열 접근을 위한 검증
            if (clientsRead.getData() == null || clientsRead.getData().isEmpty()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "접근 가능한 프로젝트가 없습니다.");
            }

            ClientRead firstClient = clientsRead.getData().get(0);
            if (firstClient.getProject() == null || firstClient.getProject().getId() == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "프로젝트 정보가 올바르지 않습니다.");
            }

            String projectId = firstClient.getProject().getId();
            log.debug("첫 번째 프로젝트 ID 조회 성공: {}", projectId);

            return projectId;

        } catch (BusinessException e) {
            log.error("프로젝트 ID 조회 실패 (BusinessException): {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("프로젝트 ID 조회 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "프로젝트 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
