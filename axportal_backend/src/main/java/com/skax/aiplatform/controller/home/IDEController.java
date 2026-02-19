package com.skax.aiplatform.controller.home;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.home.request.IdeCreateReq;
import com.skax.aiplatform.dto.home.request.IdeDeleteReq;
import com.skax.aiplatform.dto.home.request.IdeExtendReq;
import com.skax.aiplatform.dto.home.request.SearchIdeStatusReq;
import com.skax.aiplatform.dto.home.response.IdeCreateRes;
import com.skax.aiplatform.dto.home.response.IdeImageRes;
import com.skax.aiplatform.dto.home.response.IdeStatusRes;
import com.skax.aiplatform.dto.kube.request.DwGetAccountCredentialsReq;
import com.skax.aiplatform.dto.kube.response.DwGetAccountCredentialsRes;
import com.skax.aiplatform.dto.vertica.response.DwAccountListRes;
import com.skax.aiplatform.service.home.IDEService;
import com.skax.aiplatform.service.kube.KubeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/home/ide")
@RequiredArgsConstructor
public class IDEController {

    private final IDEService ideService;
    private final KubeService kubeService;
    private final TokenInfo tokenInfo;

    @GetMapping("/images")
    @Operation(
            summary = "IDE 이미지 목록 조회",
            description = "IDE 이미지 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "IDE 이미지 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "IDE 이미지 목록 조회 실패")
    })
    public AxResponseEntity<IdeImageRes> getIdeImages() {
        IdeImageRes response = ideService.getIdeImage();
        return AxResponseEntity.ok(response, "IDE 파이썬 버전 목록 조회에 성공하였습니다.");
    }

    @GetMapping("/dw-account/{user_id}")
    @Operation(
            summary = "사용중인 DW 계정 목록 조회",
            description = "사용중인 DW 계정 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "DW 계정 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "DW 계정 목록 조회 실패")
    })
    public AxResponseEntity<List<DwAccountListRes>> getDWAccount(@PathVariable String user_id) {
        List<DwAccountListRes> dwAccountList = ideService.getDWAccount(user_id);
        return AxResponseEntity.ok(dwAccountList, "DW 계정 목록 조회에 성공하였습니다.");
    }

    @GetMapping("/dw-all-accounts")
    @Operation(
            summary = "신청 가능한 전체 DW 계정 목록 조회",
            description = "신청 가능한 전체 DW 계정 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 DW 계정 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<List<String>> getAllDWAccounts() {
        List<String> dwAccounts = kubeService.getDwAllAccounts();
        return AxResponseEntity.ok(dwAccounts, "신청 가능한 전체 DW 계정 목록 조회에 성공하였습니다.");
    }

    @GetMapping("/create-available")
    @Operation(
            summary = "IDE 생성 가능 여부 확인",
            description = "현재 사용자가 해당 타입의 IDE를 추가로 생성할 수 있는지 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 IDE 타입"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Boolean> checkIdeCreateAvailable(
            @Parameter(description = "IDE 타입 (JUPYTER|VSCODE)", required = true)
            @RequestParam("ideType") String ideType) {
        String userId = tokenInfo.getUserName();
        log.info("IDE 생성 가능 여부 확인 요청: ideType={}, userId={}", ideType, userId);
        boolean isAvailable = ideService.isIdeCreateAvailable(ideType, userId);
        return AxResponseEntity.ok(isAvailable, "IDE 생성 가능 여부 확인에 성공하였습니다.");
    }

    @PostMapping
    @Operation(
            summary = "IDE 생성",
            description = "새로운 IDE를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "IDE 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "IDE 생성 조회 실패")
    })
    public AxResponseEntity<IdeCreateRes> createIde(@RequestBody IdeCreateReq request) {
        IdeCreateRes ideCreateRes = ideService.createIde(request);
        return AxResponseEntity.ok(ideCreateRes, "IDE 생성에 성공하였습니다.");
    }


    /**
     * IDE 삭제
     *
     * @param ideDeleteReq IDE Delete Request Paramater
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{ide_id}")
    @Operation(
            summary = "IDE 사용 종료",
            description = "사용자의 IDE를 종료합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "IDE 종료 성공"),
            @ApiResponse(responseCode = "404", description = "IDE를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> deleteIde(
            @PathVariable("ide_id") String ideId,
            @RequestBody
            @Parameter(description = "IDE 정보", required = true)
            IdeDeleteReq ideDeleteReq) {
        log.info("IDE 종료 요청: ideId={}", ideId);
        ideService.deleteIde(ideDeleteReq);
        return AxResponseEntity.ok(null, "IDE가 성공적으로 종료되었습니다.");
    }

    @PostMapping("/dw-credentials")
    @Operation(
            summary = "DW 사용자 인증 정보 조회",
            description = "DW USER_ID를 받아 해당 사용자의 DW 접속정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 인증 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<DwGetAccountCredentialsRes> getUserCredentials(
            @RequestBody
            @Parameter(description = "사용자 DW ID 정보", required = true)
            DwGetAccountCredentialsReq request) {
        log.info("사용자 인증 정보 조회 요청: userId={}", request.getAccountId());
        DwGetAccountCredentialsRes response = kubeService.dwGetAccountCredentials(request);
        return AxResponseEntity.ok(response, "사용자 인증 정보를 성공적으로 조회하였습니다.");
    }

    @GetMapping("/status/{member_id}")
    @Operation(
            summary = "사용자 IDE 목록 조회",
            description = "특정 사용자가 사용 중인 IDE 목록을 조회합니다. keyword로 도구명, 이미지명, DW계정을 검색할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "IDE 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<PageResponse<IdeStatusRes>> getIdeStatus(
            @PathVariable("member_id") String memberId,
            @Valid SearchIdeStatusReq request
    ) {
        Page<IdeStatusRes> response = ideService.getIdeStatus(memberId, request);
        return AxResponseEntity.okPage(response, "사용자 IDE 목록 조회에 성공하였습니다.");
    }

    @PutMapping("/status/{status_uuid}/extend")
    @Operation(
            summary = "IDE 사용 기간 연장",
            description = "IDE의 만료일시를 지정한 기간만큼 연장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "IDE 사용 기간 연장 성공"),
            @ApiResponse(responseCode = "404", description = "IDE를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public AxResponseEntity<Void> extendIdeExpiration(
            @PathVariable("status_uuid") String statusUuid,
            @RequestBody @Parameter(description = "연장 기간 정보", required = true) IdeExtendReq request
    ) {
        ideService.extendIdeExpiration(statusUuid, request);
        return AxResponseEntity.success("IDE 사용 기간이 성공적으로 연장되었습니다.");
    }

}
