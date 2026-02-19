package com.skax.aiplatform.service.eval;

import com.skax.aiplatform.client.datumo.api.dto.response.TaskListResponse;

/**
 * Evaluation Service Interface
 *
 * <p>
 * 평가(Evaluation) 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * Datumo API를 통해 평가 Task 목록을 조회하는 기능을 제공합니다.
 * </p>
 *
 * @author System
 * @since 2025-01-27
 */
public interface EvalService {

    /**
     * Datumo 시스템에서 Task 목록 조회
     *
     * <p>
     * Authorization 헤더에서 받은 액세스 토큰을 사용하여
     * 지정된 조건으로 Task 목록을 조회합니다.
     * </p>
     *
     * @param accessToken 액세스 토큰 (Authorization 헤더에서 추출)
     * @param group       조회할 그룹
     * @param category    Task 카테고리
     * @param page        페이지 번호 (1부터 시작)
     * @param pageSize    페이지당 항목 수
     * @param search      검색어 (선택적)
     * @return Task 목록 조회 결과
     */
    TaskListResponse getTaskList(String accessToken, String group, String category, Integer page, Integer pageSize,
                                 String search);
}
