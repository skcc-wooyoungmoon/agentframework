package com.skax.aiplatform.controller.data;

import com.skax.aiplatform.dto.data.response.SourceSystemInfo;
import com.skax.aiplatform.service.data.DataCtlgSourceSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 원천 시스템 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/dataCtlg/source-system")
@RequiredArgsConstructor
@Tag(name = "원천 시스템", description = "원천 시스템 관리 API")
public class DataCtlgSourceSystemController {

    private final DataCtlgSourceSystemService dataCtlgSourceSystemService;

    /**
     * 원천 시스템 목록 조회
     * @return 원천 시스템 목록
     */
    @GetMapping("/list")
    @Operation(summary = "원천 시스템 목록 조회", description = "원천 시스템 목록을 조회합니다.")
    public ResponseEntity<List<SourceSystemInfo>> getSourceSystems() {
        log.info("🔍 [Controller] 원천 시스템 목록 조회 API 호출");
        
        try {
            List<SourceSystemInfo> sourceSystems = dataCtlgSourceSystemService.getSourceSystems();
            log.info("✅ [Controller] 원천 시스템 목록 조회 완료 - {} 개", sourceSystems.size());
            log.info("📋 [Controller] 응답 데이터: {}", sourceSystems);
            return ResponseEntity.ok(sourceSystems);
        } catch (NullPointerException e) {
            // sourceSystems가 null이거나 sourceSystems.size()에서 발생 가능 (서비스에서 항상 List를 반환하므로 발생 가능성 낮음)
            log.error("❌ [Controller] 원천 시스템 목록 조회 중 NullPointerException 발생 - 오류: {}", e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            // 기타 런타임 예외 (서비스에서 모든 예외를 처리하므로 발생 가능성 낮음)
            log.error("❌ [Controller] 원천 시스템 목록 조회 중 런타임 오류 발생 - 오류: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // 예상치 못한 예외 (checked exception 등)
            log.error("❌ [Controller] 원천 시스템 목록 조회 중 예상치 못한 오류 발생 - 오류: {}", e.getMessage(), e);
            throw e;
        }
    }
}
