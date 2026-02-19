package com.skax.aiplatform.client.datumo.api.service;

import com.skax.aiplatform.client.datumo.api.DatumoApiClient;
import com.skax.aiplatform.client.datumo.api.dto.response.TaskListResponse;
import com.skax.aiplatform.client.datumo.config.DatumoRequestInterceptor;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Datumo API 서비스
 *
 * <p>Datumo 시스템과의 API 통신을 담당하는 서비스 계층입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 제공합니다.</p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>인증 관리</strong>: 로그인 및 토큰 관리</li>
 *   <li><strong>Task 관리</strong>: Task 목록 조회</li>
 *   <li><strong>예외 처리</strong>: API 호출 실패 시 적절한 예외 변환</li>
 *   <li><strong>로깅</strong>: 요청/응답 로깅 및 에러 추적</li>
 * </ul>
 *
 * <h3>사용 예시:</h3>
 * <pre>
 * // 로그인
 * LoginResponse loginResponse = datumoApiService.login("shinhan_admin", "shinhanadmin12!");
 * String accessToken = loginResponse.getAccessToken();
 *
 * // Task 목록 조회
 * TaskListResponse taskList = datumoApiService.getTaskList(accessToken, 1L, "JUDGE", 1, 12, "RAGAS");
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-02
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DatumoApiService {

    private final DatumoApiClient datumoApiClient;

    /**
     * Task 목록 조회
     *
     * <p>지정된 프로젝트의 Task 목록을 조회합니다.
     * 인증 토큰을 자동으로 설정하고 API를 호출한 후 토큰을 정리합니다.</p>
     *
     * @param accessToken API 호출용 액세스 토큰
     * @param projectId   조회할 프로젝트 ID
     * @param category    Task 카테고리
     * @param page        페이지 번호 (1부터 시작)
     * @param pageSize    페이지당 항목 수
     * @param search      검색어 (선택적)
     * @return Task 목록 조회 결과
     * @throws BusinessException API 호출 실패 시
     */
    public TaskListResponse getTaskList(String accessToken, String group, String projectId, String category, Integer page, Integer pageSize, String search) {
        log.info("Datumo Task 목록 조회 요청 - 프로젝트: {}, 카테고리: {}, 페이지: {}/{}, 검색어: {}", projectId, category, page, pageSize, search);

        try {
            // 인증 토큰 설정
            DatumoRequestInterceptor.setAccessToken(accessToken);

            // API 호출
            TaskListResponse response = datumoApiClient.getTaskList(group, projectId, category, page, pageSize, search);

            log.info("Datumo Task 목록 조회 성공 - 프로젝트: {}, 전체 데이터: {}, 전체 페이지: {}, 조회된 Task 수: {}", projectId, response.getTotalDataCount(), response.getTotalPageCount(), response.getTasks() != null ? response.getTasks().size() : 0);

            return response;

        } catch (BusinessException e) {
            // BusinessException인 경우 DatumoErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Datumo Task 목록 조회 실패 (BusinessException) - 프로젝트: {}, message: {}", projectId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Datumo Task 목록 조회 실패 (예상치 못한 오류) - 프로젝트: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Task 목록 조회에 실패했습니다: " + e.getMessage());
        } finally {
            // 인증 토큰 정리
            DatumoRequestInterceptor.clearAccessToken();
        }
    }
}