package com.skax.aiplatform.batch;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.skax.aiplatform.service.batch.HrDataBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR 직원 정보 파일 처리 배치 작업
 * /gapdat/HR 경로의 최신 파일을 읽어 gpo_grpco_jkw_mas 테이블에 저장
 * 매일 00시 00분에 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HrDataBatchJob {

    private final HrDataBatchService hrDataBatchService;

    /**
     * HR 데이터 파일 처리 배치
     * 매일 00시 00분에 실행 (cron: 0 0 0 * * ?)
     * application.yml의 batch.hr.cron 설정 사용
     */
    @Scheduled(cron = "${batch.hr.cron:0 0 0 * * ?}", zone = "Asia/Seoul")
    public void processHrDataFile() {
        log.info("[BATCH] HR 데이터 파일 처리 배치 시작");

        try {
            // 1. 직원 테이블 저장
            hrDataBatchService.processHrDataFile();

            // 2. 유저 테이블에 동기화 처리
            hrDataBatchService.syncUsersWithHrData();

            log.info("[BATCH] HR 데이터 파일 처리 배치 완료");
        } catch (RuntimeException re) {
            log.error("[BATCH] HR 데이터 파일 처리 배치 실패", re);
        } catch (Exception e) {
            log.error("[BATCH] HR 데이터 파일 처리 배치 실패", e);
        }
    }

    // 앱 기동 직후 1회 실행
    @EventListener(ApplicationReadyEvent.class)
    public void runOnceOnStartup() {
        log.info("[BATCH] HR 데이터 파일 처리 배치 시작");

        try {
            // 1. 직원 테이블 저장
            hrDataBatchService.processHrDataFile();

            // 2. 유저 테이블에 동기화 처리
            hrDataBatchService.syncUsersWithHrData();

            log.info("[BATCH] HR 데이터 파일 처리 배치 완료");
        } catch (RuntimeException re) {
            log.error("[BATCH] HR 데이터 파일 처리 배치 실패", re);
        } catch (Exception e) {
            log.error("[BATCH] HR 데이터 파일 처리 배치 실패", e);
        }
    }

}
