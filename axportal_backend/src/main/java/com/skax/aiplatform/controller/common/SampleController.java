package com.skax.aiplatform.controller.common;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.skax.aiplatform.client.shinhan.dto.ApprovalReq;
import com.skax.aiplatform.client.shinhan.dto.ApprovalRes;
import com.skax.aiplatform.dto.common.response.SwingResCommon;
import com.skax.aiplatform.dto.common.response.ApprovalResData;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
@Tag(name = "Common 관련", description = "결재요청 관련 API")
public class SampleController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/approval")
    public ApprovalRes sampleResponse(@Valid @RequestBody ApprovalReq approvalReq,
                                      HttpServletResponse response) throws Exception {

        log.debug("+++++++++++++++++++++++++ sampleResponse +++++++++++++++++++++++++++++++++" + approvalReq.toString());
        
        try {
            // 파라미터 검증 (approvalReq는 @Valid로 검증되므로 null 체크 불필요)
            if (response == null) {
                log.error(">>> 결재 요청 실패 - HttpServletResponse가 null입니다.");
                throw new IllegalArgumentException("HttpServletResponse는 필수입니다.");
            }
            
            String uuid = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            ApprovalRes approvalRes = ApprovalRes.builder()
                    .data(ApprovalResData.builder()
                            .approvalDocumentId(uuid)
                            .build())
                    .common(SwingResCommon.builder()
                            .resultCode(200)
                            .responseDatetime("2025-10-10 10:10:10")
                            .requestUniqueKey("1234567890aABCDEFG")
                            .transactionId("1234567890aABCDEFG")
                            .build())
                    .build();
            
            log.debug("결재 요청 처리 성공 - approvalDocumentId: {}", uuid);
            return approvalRes;
            
        } catch (IllegalArgumentException e) {
            log.error(">>> 결재 요청 실패 - 잘못된 파라미터: error={}", e.getMessage(), e);
            throw new RuntimeException("결재 요청 실패: 잘못된 파라미터입니다.", e);
        } catch (NullPointerException e) {
            log.error(">>> 결재 요청 실패 - 필수 데이터 null: error={}", e.getMessage(), e);
            throw new RuntimeException("결재 요청 실패: 필수 데이터를 찾을 수 없습니다.", e);
        } catch (Exception e) {
            log.error("=== 결재 요청 처리 실패 ===");
            log.error("예외 타입: {}", e.getClass().getName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("전체 스택 트레이스:", e);
            throw new RuntimeException("결재 요청 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    @PostMapping("/approvalCallBack")
    public void sampleCallbackResponse(@Param("approvalDocmentId") String approvalDocmentId,
                                       @Param("approvalEmployeeNo") String approvalEmployeeNo) throws Exception {
        
        log.debug("=== 결재 콜백 처리 시작 ===");
        log.debug("approvalDocmentId: {}, approvalEmployeeNo: {}", approvalDocmentId, approvalEmployeeNo);
        
        try {
            // 파라미터 검증
            if (approvalDocmentId == null || approvalDocmentId.trim().isEmpty()) {
                log.error(">>> 결재 콜백 실패 - approvalDocmentId가 null 또는 빈 문자열입니다.");
                throw new IllegalArgumentException("결재 문서 ID는 필수입니다.");
            }
            
            if (approvalEmployeeNo == null || approvalEmployeeNo.trim().isEmpty()) {
                log.error(">>> 결재 콜백 실패 - approvalEmployeeNo가 null 또는 빈 문자열입니다.");
                throw new IllegalArgumentException("결재자 사번은 필수입니다.");
            }
            
            // 호출할 URL 생성
            String url = UriComponentsBuilder.fromUriString("http://localhost:8080/api/common/approvalCallBack")
                    .queryParam("approvalDocumentId", approvalDocmentId)
                    .queryParam("resultCode", "APPROVAL")
                    .queryParam("approvalOpinion", "승인합니다")
                    .queryParam("approvalCount", "1")
                    .queryParam("approvalEmployeeNo", approvalEmployeeNo)
                    .queryParam("agentYn", "N")
                    .queryParam("gyljLineNm", "프로젝트생성")
                    .toUriString();

            log.debug("호출 URL: {}", url);

            // GET 요청 호출
            ResponseEntity<ApprovalRes> responseEntity = restTemplate.getForEntity(url, ApprovalRes.class);

            ApprovalRes approvalRes = responseEntity.getBody();

            log.debug("응답 상태 코드: {}", responseEntity.getStatusCode());
            log.debug("응답 데이터: {}", approvalRes);
            log.debug("=== 결재 콜백 처리 완료 ===");

            return;

        } catch (IllegalArgumentException e) {
            log.error(">>> 결재 콜백 실패 - 잘못된 파라미터: approvalDocmentId={}, approvalEmployeeNo={}, error={}", 
                    approvalDocmentId, approvalEmployeeNo, e.getMessage(), e);
            throw new RuntimeException("결재 콜백 실패: 잘못된 파라미터입니다.", e);
        } catch (NullPointerException e) {
            log.error(">>> 결재 콜백 실패 - 필수 데이터 null: approvalDocmentId={}, approvalEmployeeNo={}, error={}", 
                    approvalDocmentId, approvalEmployeeNo, e.getMessage(), e);
            throw new RuntimeException("결재 콜백 실패: 필수 데이터를 찾을 수 없습니다.", e);
        } catch (org.springframework.web.client.RestClientException e) {
            log.error(">>> 결재 콜백 실패 - REST 호출 오류: approvalDocmentId={}, approvalEmployeeNo={}, error={}", 
                    approvalDocmentId, approvalEmployeeNo, e.getMessage(), e);
            throw new RuntimeException("결재 콜백 실패: 외부 API 호출 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("=== 결재 콜백 처리 실패 ===");
            log.error("approvalDocmentId: {}, approvalEmployeeNo: {}", approvalDocmentId, approvalEmployeeNo);
            log.error("예외 타입: {}", e.getClass().getName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("전체 스택 트레이스:", e);
            throw new RuntimeException("결재 콜백 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
