package com.skax.aiplatform.batch;

import com.skax.aiplatform.service.data.DataCtlgDataSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 임시로 생성한 S3 버킷 삭제 배치
 *
 * <p>각 임시 버킷마다 개별 스케줄을 생성하여 상태를 체크하고, 삭제 완료 시 스케줄을 종료합니다.</p>
 *
 * @author 장지원
 * @since 2025-11-20
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BucketDeleteBatch {

    private final DataCtlgDataSetService dataCtlgDataSetService;
    private final TaskScheduler taskScheduler;

    // 각 버킷별 스케줄 관리: key=tempBucketName, value=ScheduledFuture
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @EventListener
    public void handleTempBucketScheduleEvent(TempBucketScheduleEvent event) {
        startSchedule(event.getDatasourceId(), event.getTempBucketName(), event.getUsername());
    }

    /**
     * 임시 버킷에 대한 스케줄을 시작합니다.
     *
     * @param datasourceId 데이터소스 ID (상태 확인용)
     * @param tempBucketName 임시 버킷명
     * @param username 현재 로그인된 사용자명
     */
    @SuppressWarnings("null")
    public void startSchedule(String datasourceId, String tempBucketName, String username) {
        // 이미 스케줄이 있으면 중복 등록 방지
        if (scheduledTasks.containsKey(tempBucketName)) {
            log.warn("[BATCH] 이미 등록된 버킷 스케줄 - tempBucketName: {}", tempBucketName);
            return;
        }

        // 초기 딜레이와 실행 주기 설정
        @SuppressWarnings("null")
        final Duration initialDelay = Duration.ofSeconds(10); // 첫 실행까지 10초 대기
        final Duration interval = Duration.ofSeconds(10); // 이후 10초마다 실행
        
        // 첫 실행 offset : 10초, 주기 : 10초
        Instant startTime = Instant.now().plus(initialDelay);
        ScheduledFuture<?> firstTask = taskScheduler.schedule(() -> {
            ScheduledFuture<?> periodicTask = taskScheduler.scheduleAtFixedRate(
                    () -> checkAndDeleteBucket(datasourceId, tempBucketName, username),
                    interval);
            scheduledTasks.put(tempBucketName, periodicTask);
            checkAndDeleteBucket(datasourceId, tempBucketName, username);
        }, startTime);

        log.info("[BATCH] 임시 버킷 스케줄 등록 완료 (첫 실행: {}초 후) - datasourceId: {}, tempBucketName: {}, username: {}",
                initialDelay.getSeconds(), datasourceId, tempBucketName, username);
    }

    /**
     * 버킷 상태를 체크하고 삭제 조건이 충족되면 삭제합니다.
     * datasource의 status가 preparing이 아닌 경우 삭제합니다.
     */
    private void checkAndDeleteBucket(String datasourceId, String tempBucketName, String username) {
        try {
            log.info("[BATCH] 임시 버킷 삭제 스케줄 시작");
            boolean deleted = dataCtlgDataSetService.checkAndDeleteTempBucket(datasourceId, tempBucketName, username);

            if (deleted) {
                cancelSchedule(tempBucketName);
                log.info("[BATCH] 임시 버킷 삭제 완료 및 스케줄 종료 - tempBucketName: {}", tempBucketName);
            }
        } catch (RuntimeException e) {
            log.error("[BATCH] 버킷 체크 중 런타임 오류 발생 - tempBucketName: {}, error: {}",
                    tempBucketName, e.getMessage(), e);
            cancelSchedule(tempBucketName);
        } catch (Exception e) {
            log.error("[BATCH] 버킷 체크 중 예상치 못한 오류 발생 - tempBucketName: {}, error: {}",
                    tempBucketName, e.getMessage(), e);
            cancelSchedule(tempBucketName);
        }
    }

    /**
     * 스케줄 종료
     *
     * @param tempBucketName 임시 버킷명
     */
    private void cancelSchedule(String tempBucketName) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(tempBucketName);
        if (scheduledTask != null) {
            scheduledTask.cancel(false); // false: 실행 중인 작업은 완료까지 기다림
            log.debug("[BATCH] 스케줄 취소 완료 - tempBucketName: {}", tempBucketName);
        }
    }

    /**
     * 임시 버킷 스케줄 등록 이벤트
     */
    public static class TempBucketScheduleEvent {
        private final String datasourceId;
        private final String tempBucketName;
        private final String username;

        public TempBucketScheduleEvent(String datasourceId, String tempBucketName, String username) {
            this.datasourceId = datasourceId;
            this.tempBucketName = tempBucketName;
            this.username = username;
        }

        public String getDatasourceId() {
            return datasourceId;
        }

        public String getTempBucketName() {
            return tempBucketName;
        }

        public String getUsername() {
            return username;
        }
    }
}