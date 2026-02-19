package com.skax.aiplatform.service.model;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.log.response.ModelHistoryRecordRes;
import com.skax.aiplatform.dto.log.response.ModelHistoryRes;

/**
 * 모델 배포 로그 서비스 인터페이스
 * 
 * <p>
 * 모델 배포와 관련된 사용 이력 및 로그를 조회하는 서비스 인터페이스입니다.
 * SKTAI History API를 통해 모델 사용 이력을 조회합니다.
 * </p>
 * 
 * @author System
 * @since 2025-01-27
 * @version 1.0.0
 */
public interface ModelDeployLogService {

    /**
     * 모델 사용 이력 목록 조회
     * 
     * <p>
     * SKTAI History API를 통해 모델 사용 이력을 조회합니다.
     * 모델 실행 기록, 성능 지표, 사용자 정보 등을 포함한 페이징된 이력 데이터를 제공합니다.
     * </p>
     * 
     * @param fields    응답에 포함할 필드 목록 (콤마로 구분된 문자열, 선택사항)
     * @param errorLogs 오류 로그만 조회 여부 (선택사항, 기본값: false)
     * @param fromDate  조회 시작 날짜 (YYYY-MM-DD 형식, 필수)
     * @param toDate    조회 종료 날짜 (YYYY-MM-DD 형식, 필수)
     * @param page      페이지 번호 (1부터 시작, 필수)
     * @param size      페이지당 항목 수 (필수)
     * @param filter    필터 조건 (key:value,... 형식, 선택사항)
     * @param search    검색 조건 (key:*value*,... 형식, 선택사항)
     * @param sort      정렬 기준 (필드명,정렬방향 형식, 예: request_time,asc)
     * @return 모델 사용 이력 목록과 페이징 정보
     */
    PageResponse<ModelHistoryRecordRes> getModelHistoryList(String fields, Boolean errorLogs, String fromDate, String toDate,
                                                            Integer page, Integer size, String filter, String search, String sort);

    /**
     * 모델 사용 이력을 CSV 형식으로 변환
     * 
     * <p>
     * 모델 사용 이력 데이터를 CSV 형식의 바이트 배열로 변환합니다.
     * 다운로드 기능을 위해 사용됩니다.
     * </p>
     * 
     * @param modelHistoryRes 모델 사용 이력 응답 데이터
     * @return CSV 형식의 바이트 배열
     */
    byte[] generateCsvData(ModelHistoryRes modelHistoryRes);
}
