package com.skax.aiplatform.batch;

import com.skax.aiplatform.service.knowledge.DataPipelineMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.dao.DataAccessException;

/**
 * Data Pipeline 모니터링 배치
 * 
 * <p>주기적으로 실행되어 Data Pipeline의 상태를 모니터링하고 DB를 업데이트합니다.</p>
 * 
 * <h3>실행 주기:</h3>
 * <ul>
 *   <li>기본: 5분마다 실행</li>
 *   <li>설정 파일(application.yml)에서 cron 표현식으로 변경 가능</li>
 * </ul>
 * 
 * @author younglot
 * @since 2025-11-03
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataPipelineMonitorBatch {

    private final DataPipelineMonitorService dataPipelineMonitorService;

    /**
     * Data Pipeline 상태 모니터링 배치 작업
     * 
     * <p>application.yml의 datapipeline.monitor.cron 설정값에 따라 주기적으로 실행됩니다.
     * 기본값은 5분마다 실행 (0 0/5 * * * ?)입니다.</p>
     */
    @Scheduled(cron = "${datapipeline.monitor.cron:0 0/5 * * * ?}", zone = "Asia/Seoul")
    //@Scheduled(fixedRate = 10000) // 10초
    public void monitorDataPipeline() {

        try {
            dataPipelineMonitorService.monitorAndUpdatePipelineStatus();
        } catch (DataAccessException e) {
            // JPA Repository 메서드(findRunningPipelines)에서 발생 가능
            log.error("[BATCH] DataPipelineMonitorBatch 실행 중 데이터베이스 접근 오류 발생: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            // 기타 런타임 예외 (트랜잭션 예외 포함)
            log.error("[BATCH] DataPipelineMonitorBatch 실행 중 런타임 오류 발생: {}", e.getMessage(), e);
        } catch (Exception e) {
            // 예상치 못한 예외 (checked exception 등)
            log.error("[BATCH] DataPipelineMonitorBatch 실행 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * Data Pipeline 상태 모니터링 배치 작업 (30분 주기)
     * 
     * <p>application.yml의 datapipeline.monitor.sync_monitor.cron 설정값에 따라 주기적으로 실행됩니다.
     * 기본값은 30분마다 실행 (0 0/30 * * * ?)입니다.</p>
     */
    @Scheduled(cron = "${datapipeline.monitor.cron_sync:0 0/30 * * * ?}", zone = "Asia/Seoul")
    public void monitorDataikuPipeline() {
        try {
            dataPipelineMonitorService.monitorDataikuContinuousActivities();
        } catch (DataAccessException e) {
            log.error("[BATCH] DataPipelineMonitorBatch (Dataiku) 실행 중 데이터베이스 접근 오류 발생: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("[BATCH] DataPipelineMonitorBatch (Dataiku) 실행 중 런타임 오류 발생: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[BATCH] DataPipelineMonitorBatch (Dataiku) 실행 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
        }
    }
}

