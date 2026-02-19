package com.skax.aiplatform.batch;

import com.skax.aiplatform.service.home.IDEService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdeDeleteBatch {
    private final IDEService ideService;

    @Value("${kube.ide.delete-batch-startup:true}")
    private boolean runOnStartup;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    // 앱 기동 직후 1회 실행
    // @EventListener(ApplicationReadyEvent.class)
    public void runOnceOnStartup() {
        if (runOnStartup && !activeProfile.equals("prod")) {
            log.info("[BATCH] deleteIdeBatch (startup)");
            ideService.deleteIdeBatch();
        }
    }

    /**
     * application.yml 의 ide.delete-batch-cron 사용
     */
    @Scheduled(cron = "${kube.ide.delete-batch-cron}", zone = "Asia/Seoul")
    public void deleteIdeBatch() {
        if (!activeProfile.equals("prod")) {
            log.info("[BATCH] deleteIdeBatch start");
            ideService.deleteIdeBatch();   // 만료 IDE 정리 로직은 서비스에 구현
            log.info("[BATCH] deleteIdeBatch end");
        }
    }
}
