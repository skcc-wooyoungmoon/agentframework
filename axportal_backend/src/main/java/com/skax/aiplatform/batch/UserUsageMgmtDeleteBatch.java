package com.skax.aiplatform.batch;

import com.skax.aiplatform.service.admin.UserUsageMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 사용자 사용량 관리 데이터 삭제 배치
 * 
 * <p>
 * 매일 05:00에 현재 날짜 기준으로 30일 전 데이터를 삭제합니다.
 * </p>
 * 
 * @author sonmunwoo
 * @since 2025-11-05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserUsageMgmtDeleteBatch {
    
    private final UserUsageMgmtService userUsageMgmtService;

    /**
     * 매일 05:00에 30일 전 사용자 사용량 관리 데이터 삭제
     * 
     * <p>
     * application.yml의 userUsageMgmt.delete-batch-cron 설정값에 따라 주기적으로 실행됩니다.
     * 기본값은 매일 05:00 실행 (0 0 5 * * ?)입니다.
     * </p>
     */
    @Scheduled(cron = "${userUsageMgmt.delete-batch-cron:0 0 5 * * ?}", zone = "Asia/Seoul")
    public void deleteOldUserUsageMgmtData() {
        try {
            log.info("[BATCH] 사용자 사용량 관리 데이터 삭제 배치 시작");
            
            long deletedCount = userUsageMgmtService.deleteOldUserUsageMgmtData();
            
            log.info("[BATCH] 사용자 사용량 관리 데이터 삭제 배치 완료 - 삭제된 레코드 수: {}건", deletedCount);
        } catch (DataAccessException e) {
            log.error("[BATCH] 사용자 사용량 관리 데이터 삭제 배치 실패 (DataAccessException): {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("[BATCH] 사용자 사용량 관리 데이터 삭제 배치 실패 (IllegalArgumentException): {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("[BATCH] 사용자 사용량 관리 데이터 삭제 배치 실패 (RuntimeException): {}", e.getMessage(), e);
        }
    }
}

