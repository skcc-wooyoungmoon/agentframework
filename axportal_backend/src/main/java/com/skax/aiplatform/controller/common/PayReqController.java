package com.skax.aiplatform.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.shinhan.dto.ApprovalRes;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.common.request.ApprovalCallBakReq;
import com.skax.aiplatform.dto.common.request.PayApprovalReq;
import com.skax.aiplatform.dto.common.response.SwingResCommon;
import com.skax.aiplatform.dto.common.response.ApprovalStatusCheckRes;
import com.skax.aiplatform.dto.home.request.ProjectCreateReq;
import com.skax.aiplatform.service.common.PayReqService;
import com.skax.aiplatform.service.home.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
@Tag(name = "Common 관련", description = "결재요청 관련 API")
public class PayReqController {
    private final PayReqService payReqService;
    private final ProjectService projectService;


    @PostMapping("/payreq")
    @Operation(
            summary = "결재 요청 생성",
            description = "결재요청 생성 함."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결재요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 결재요청 데이터"),
            @ApiResponse(responseCode = "401", description = "결재요청 실패")
    })
    public AxResponseEntity<PayApprovalReq> createPayReq(@Valid @RequestBody PayApprovalReq payApprovalReq,
                                                         HttpServletResponse response) throws Exception {
        log.debug("+++++++++++++++++++++++++ createPayReq ++++++++++++++++++++++++++++++++\n{}", payApprovalReq);

        // 결재차수 초기화 설정 (화면을 통해 요청하는 결재는 모두 1차수)
        payApprovalReq.getApprovalInfo().setCurrentApprovalCount(1);
        payApprovalReq.getApprovalInfo().setMaxApprovalCount(payApprovalReq.getApprovalTypeInfo().getApprovalTarget().size());

        int returnVal = payReqService.approval(payApprovalReq);

        if (returnVal > -1) {
            return AxResponseEntity.ok(payApprovalReq, "결재요청을 성공하였습니다.");
        } else {
            // 프로젝트 생성 롤백
            if (payApprovalReq.getApprovalInfo().getApprovalType().equals("01")) {
                projectService.deleteProject(new ObjectMapper().readValue(
                        payApprovalReq.getApprovalInfo().getAfterProcessParamString(),
                        ProjectCreateReq.class).getPrjSeq());
            }

            return AxResponseEntity.error(ErrorCode.SWING_APPROVAL_REQUEST_FAILED, "결재요청을 실패하였습니다.");
        }
    }

    @GetMapping("/approvalCallBack")
    @Operation(
            summary = "결재 요청 콜백",
            description = "결재요청 콜백 함."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결재요청 콜백 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 결재요청 콜백 데이터")
    })
    public ApprovalRes approvalCreateProjCallBack(
            @RequestParam String approvalDocumentId,
            @RequestParam String resultCode,
            @RequestParam(required = false) String approvalOpinion,
            @RequestParam(required = false) String approvalCount,
            @RequestParam(required = false) String approvalEmployeeNo,
            @RequestParam(required = false) String agentYn,
            @RequestParam(required = false) String gyljLineNm,
            HttpServletResponse response) throws Exception {

        // 필요한 경우 다른 파라미터도 디코딩
        try {
            ApprovalCallBakReq approvalCallBakReq = ApprovalCallBakReq.builder()
                    .approvalDocumentId(approvalDocumentId)
                    .resultCode(resultCode)
                    .approvalOpinion(approvalOpinion)
                    .approvalCount(approvalCount)
                    .approvalEmployeeNo(approvalEmployeeNo)
                    .agentYn(agentYn)
                    .gyljLineNm("")
                    .build();

            payReqService.approvalCallBack(approvalCallBakReq);
            log.debug("+++++++++++++++++++++++++ approvalProjectCallBack ++++++++++++++++++++++++++++++++" + approvalCallBakReq.toString());
        } catch (RuntimeException re) {
            log.debug("+++++++++++++++++++++++++ approvalProjectCallBack Exception ++++++++++++++++++++++++++++++++" + re.getMessage());
        } catch (Exception e) {
            log.debug("+++++++++++++++++++++++++ approvalProjectCallBack Exception ++++++++++++++++++++++++++++++++" + e.getMessage());
        }

        ApprovalRes approvalRes = ApprovalRes.builder()
                .common(SwingResCommon.builder()
                        .resultCode(200)
                        .errorMessage("정상 처리되었습니다.")
                        .build())
                .build();
        return approvalRes;
    }

    @GetMapping("/approvalStatus")
    @Operation(
            summary = "결재 진행중 여부 확인",
            description = "approvalUniqueKey를 기준으로 현재 진행중인 결재건이 있는지 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public AxResponseEntity<ApprovalStatusCheckRes> checkApprovalStatus(
            @RequestParam String approvalUniqueKey) {
        log.debug("+++++++++++++++++++++++++ checkApprovalStatus: {} ++++++++++++++++++++++++++++++++", approvalUniqueKey);

        boolean inProgress = payReqService.isApprovalInProgress(approvalUniqueKey);

        ApprovalStatusCheckRes response = ApprovalStatusCheckRes.builder()
                .inProgress(inProgress)
                .build();

        return AxResponseEntity.ok(response, "결재 진행 여부 조회에 성공하였습니다.");
    }
}
