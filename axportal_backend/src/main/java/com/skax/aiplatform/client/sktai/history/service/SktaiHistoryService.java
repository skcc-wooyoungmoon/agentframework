package com.skax.aiplatform.client.sktai.history.service;

import com.skax.aiplatform.client.sktai.history.SktaiHistoryClient;
import com.skax.aiplatform.client.sktai.history.dto.response.*;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI History API 서비스
 *
 * <p>
 * SKTAI History API 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스 클래스입니다.
 * 모델 사용 이력, 에이전트 사용 이력, 문서 지능형 분석 통계 등의 기능을 제공합니다.
 * </p>
 *
 * <h3>주요 기능:</h3>
 * <ul>
 * <li><strong>모델 이력 관리</strong>: 모델 사용 이력 조회 및 통계</li>
 * <li><strong>에이전트 이력 관리</strong>: 에이전트 사용 이력 조회 및 통계</li>
 * <li><strong>문서 분석 통계</strong>: 문서 지능형 분석 성과 추적</li>
 * <li><strong>통합 로깅</strong>: 모든 API 호출에 대한 상세 로깅</li>
 * <li><strong>예외 처리</strong>: 외부 API 오류를 내부 예외로 변환</li>
 * </ul>
 *
 * <h3>사용 패턴:</h3>
 *
 * <pre>
 * // 모델 사용 이력 조회
 * ModelHistoryRead history = sktaiHistoryService.getModelHistoryList(
 *         "2025-09-01", "2025-09-30", 1, 20, "created_at:desc", null, null);
 *
 * // 에이전트 통계 조회
 * AgentStatsRead stats = sktaiHistoryService.getAgentStats(
 *         "2025-09-01", "2025-09-30", "daily", null);
 * </pre>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @see SktaiHistoryClient SKTAI History FeignClient
 * @since 2025-09-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiHistoryService {

    private final SktaiHistoryClient sktaiHistoryClient;

    /**
     * 모델 사용 이력 목록 조회
     *
     * <p>
     * 지정된 기간 동안의 모델 사용 이력을 페이징하여 조회합니다.
     * 모델별 사용 현황, 성능 지표, 비용 정보 등을 확인할 수 있습니다.
     * </p>
     *
     * @param fields    응답에 포함할 필드 목록 (콤마로 구분된 문자열, 선택적)
     * @param errorLogs 오류 로그만 조회 여부 (선택적)
     * @param fromDate  조회 시작 날짜 (YYYY-MM-DD 형식)
     * @param toDate    조회 종료 날짜 (YYYY-MM-DD 형식)
     * @param page      페이지 번호 (1부터 시작)
     * @param size      페이지 크기
     * @param filter    필터 조건 (key:value,... 형식, 선택적)
     * @param search    검색 조건 (key:*value*,... 형식, 선택적)
     * @param sort      정렬 기준 (예: "created_at:desc")
     * @return 모델 사용 이력 응답
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelHistoryRead getModelHistoryList(String fields, Boolean errorLogs, String fromDate, String toDate, Integer page, Integer size, String filter, String search, String sort) {
        log.debug("모델 사용 이력 조회 요청 - fields: {}, errorLogs: {}, fromDate: {}, toDate: {}, page: {}, size: {}, filter: {}, search: {}, sort: {}", fields, errorLogs, fromDate, toDate, page, size, filter, search, sort);

        try {
            ModelHistoryRead response = sktaiHistoryClient.getModelHistoryList(fields, errorLogs, fromDate, toDate, page, size, filter, search, sort);
            log.debug("모델 사용 이력 조회 성공 - response.data: {}", response.getData());
            log.debug("모델 사용 이력 조회 성공 - response.payload: {}", response.getPayload());
            log.debug("모델 사용 이력 조회 성공 - 데이터 건수: {}", response.getData() != null ? response.getData().size() : 0);
            return response;
        } catch (BusinessException e) {
            log.error("모델 사용 이력 조회 실패 - fromDate: {}, toDate: {}", fromDate, toDate, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 사용 이력 조회 실패 - fromDate: {}, toDate: {}", fromDate, toDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 사용 이력 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 통계 테스트 조회
     *
     * <p>
     * 모델 통계 API의 연결 상태와 기본 응답을 테스트합니다.
     * 시스템 상태 확인 및 API 연동 검증 용도로 사용됩니다.
     * </p>
     *
     * @param groupBy  그룹핑 필드 (콤마로 구분된 문자열)
     * @param fromDate 조회 시작 날짜 (YYYY-MM-DD 형식)
     * @param toDate   조회 종료 날짜 (YYYY-MM-DD 형식)
     * @param filter   필터 조건 (key:value,... 형식, 선택적)
     * @param search   검색 조건 (key:*value*,... 형식, 선택적)
     * @return 모델 통계 테스트 응답
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelStatsRead getModelStatsTest(String groupBy, String fromDate, String toDate, String filter, String search) {
        log.debug("모델 통계 테스트 요청 - groupBy: {}, fromDate: {}, toDate: {}", groupBy, fromDate, toDate);

        try {
            ModelStatsRead response = sktaiHistoryClient.getModelStatsTest(groupBy, fromDate, toDate, filter, search);
            log.debug("모델 통계 테스트 성공");
            return response;
        } catch (BusinessException e) {
            log.error("모델 통계 테스트 실패 - groupBy: {}, fromDate: {}, toDate: {}", groupBy, fromDate, toDate, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 통계 테스트 실패 - groupBy: {}, fromDate: {}, toDate: {}", groupBy, fromDate, toDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 통계 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 모델 사용 통계 조회
     *
     * <p>
     * 지정된 기간 동안의 모델 사용 통계를 조회합니다.
     * 전체 성과 지표, 모델별 성능 비교, 시간대별 사용 패턴 등을 확인할 수 있습니다.
     * </p>
     *
     * @param startDate   조회 시작 날짜 (YYYY-MM-DD 형식)
     * @param endDate     조회 종료 날짜 (YYYY-MM-DD 형식)
     * @param granularity 집계 단위 (daily, weekly, monthly 등)
     * @param projectId   프로젝트 ID 필터 (선택적)
     * @return 모델 통계 응답
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public ModelStatsRead getModelStats(String startDate, String endDate, String granularity, String projectId) {
        log.debug("모델 통계 조회 요청 - startDate: {}, endDate: {}, granularity: {}", startDate, endDate, granularity);

        try {
            ModelStatsRead response = sktaiHistoryClient.getModelStats(startDate, endDate, granularity, projectId);
            log.debug("모델 통계 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("모델 통계 조회 실패 - startDate: {}, endDate: {}", startDate, endDate, e);
            throw e;
        } catch (Exception e) {
            log.error("모델 통계 조회 실패 - startDate: {}, endDate: {}", startDate, endDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "모델 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Agent History 목록 조회
     *
     * <p>
     * Agent 앱의 실행 이력을 조회합니다.
     * 시간 범위, 프로젝트, 앱 ID 등 다양한 조건으로 필터링하여 조회할 수 있습니다.
     * </p>
     *
     * <h4>주요 기능:</h4>
     * <ul>
     * <li>요청 파라미터 유효성 검증</li>
     * <li>날짜 형식 검증 및 정규화</li>
     * <li>페이징 파라미터 정규화</li>
     * <li>사용자 권한에 따른 데이터 필터링</li>
     * </ul>
     *
     * @param fromDate   시작 날짜 (YYYY-MM-DD)
     * @param toDate     종료 날짜 (YYYY-MM-DD)
     * @param page       페이지 번호
     * @param size       페이지당 항목 수
     * @param projectId  프로젝트 ID (선택사항)
     * @param agentAppId Agent 앱 ID (선택사항)
     * @return 페이징된 Agent History 목록
     * @throws BusinessException 외부 API 호출 실패 시
     * @implNote 대용량 데이터 조회 시 성능을 위해 적절한 제한을 둡니다.
     */
    public AgentHistoryResponse getAgentHistoryList(String fromDate, String toDate, Integer page, Integer size, String fields, Boolean errorLogs, String additionalHistoryOption, String filter, String search, String sort) {
        log.debug("Agent History 조회 요청 - 기간: {} ~ {}, 페이지: {}, 크기: {}, 프로젝트: {}, 앱: {}", fromDate, toDate, page, size);
        try {
            AgentHistoryResponse response = sktaiHistoryClient.getAgentHistoryList(fromDate, toDate, page, size, fields, errorLogs, additionalHistoryOption, filter, search, sort);

            log.debug("Agent History 조회 성공 - 총 {}건, 현재페이지: {}, 전체페이지: {}", response.getPayload().getPagination().getTotal(), response.getPayload().getPagination().getPage(), response.getPayload().getPagination().getLastPage());

            return response;
        } catch (BusinessException e) {
            log.error("Agent History 조회 실패 - 기간: {} ~ {}, 프로젝트: {}, 앱: {}", fromDate, toDate, e);
            throw e;
        } catch (Exception e) {
            log.error("Agent History 조회 실패 - 기간: {} ~ {}, 프로젝트: {}, 앱: {}", fromDate, toDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Agent History 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 통계 테스트 조회
     *
     * <p>
     * 에이전트 통계 API의 연결 상태와 기본 응답을 테스트합니다.
     * 시스템 상태 확인 및 API 연동 검증 용도로 사용됩니다.
     * </p>
     *
     * @return 에이전트 통계 테스트 응답
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public AgentStatsRead getAgentStatsTest() {
        log.debug("에이전트 통계 테스트 요청");

        try {
            AgentStatsRead response = sktaiHistoryClient.getAgentStatsTest();
            log.debug("에이전트 통계 테스트 성공");
            return response;
        } catch (BusinessException e) {
            log.error("에이전트 통계 테스트 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 통계 테스트 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 통계 테스트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 에이전트 사용 통계 조회
     *
     * <p>
     * 지정된 기간 동안의 에이전트 사용 통계를 조회합니다.
     * 전체 성과 지표, 에이전트별 성능 비교, 작업 유형별 분포 등을 확인할 수 있습니다.
     * </p>
     *
     * @param startDate   조회 시작 날짜 (YYYY-MM-DD 형식)
     * @param endDate     조회 종료 날짜 (YYYY-MM-DD 형식)
     * @param granularity 집계 단위 (daily, weekly, monthly 등)
     * @param projectId   프로젝트 ID 필터 (선택적)
     * @return 에이전트 통계 응답
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public AgentStatsRead getAgentStats(String startDate, String endDate, String granularity, String projectId) {
        log.debug("에이전트 통계 조회 요청 - startDate: {}, endDate: {}, granularity: {}", startDate, endDate, granularity);

        try {
            AgentStatsRead response = sktaiHistoryClient.getAgentStats(startDate, endDate, granularity, projectId);
            log.debug("에이전트 통계 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("에이전트 통계 조회 실패 - startDate: {}, endDate: {}", startDate, endDate, e);
            throw e;
        } catch (Exception e) {
            log.error("에이전트 통계 조회 실패 - startDate: {}, endDate: {}", startDate, endDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "에이전트 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 문서 지능형 분석 통계 조회
     *
     * <p>
     * 지정된 기간 동안의 문서 지능형 분석 통계를 조회합니다.
     * 문서 처리 성능, OCR 정확도, 처리량 통계 등을 확인할 수 있습니다.
     * </p>
     *
     * @param startDate   조회 시작 날짜 (YYYY-MM-DD 형식)
     * @param endDate     조회 종료 날짜 (YYYY-MM-DD 형식)
     * @param granularity 집계 단위 (daily, weekly, monthly 등)
     * @param projectId   프로젝트 ID 필터 (선택적)
     * @return 문서 지능형 분석 통계 응답
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public DocIntelligenceStatsRead getDocIntelligenceStats(String startDate, String endDate, String granularity, String projectId) {
        log.debug("문서 지능형 분석 통계 조회 요청 - startDate: {}, endDate: {}, granularity: {}", startDate, endDate, granularity);

        try {
            DocIntelligenceStatsRead response = sktaiHistoryClient.getDocIntelligenceStats(startDate, endDate, granularity, projectId);

            log.debug("문서 지능형 분석 통계 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("문서 지능형 분석 통계 조회 실패 - startDate: {}, endDate: {}", startDate, endDate, e);
            throw e;
        } catch (Exception e) {
            log.error("문서 지능형 분석 통계 조회 실패 - startDate: {}, endDate: {}", startDate, endDate, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "문서 지능형 분석 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }
}