package com.skax.aiplatform.service.batch;

/**
 * HR 데이터 배치 처리 서비스
 * /gapdat/HR 경로의 직원 정보 파일을 읽어 gpo_grpco_jkw_mas 테이블에 저장
 */
public interface HrDataBatchService {

    /**
     * HR 데이터 파일을 읽어서 데이터베이스에 저장
     * 매일 00:00에 실행되며, 최신 날짜의 파일만 처리
     */
    void processHrDataFile();

    /**
     * 직원 원장 테이블과 유저 테이블을 비교하여 동기화 처리
     */
    void syncUsersWithHrData();

}
