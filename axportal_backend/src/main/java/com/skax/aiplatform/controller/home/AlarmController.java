package com.skax.aiplatform.controller.home;


import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.auth.request.AlarmCreateRequest;
import com.skax.aiplatform.dto.auth.response.AlarmListResponse;
import com.skax.aiplatform.dto.common.response.ApprovalUserInfo;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMas;
import com.skax.aiplatform.service.home.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/home/alarms")
@RequiredArgsConstructor
@Tag(name = "Alarm 관련", description = "Alarm 관련 API")
public class AlarmController {

    private final AlarmService alarmService;
    private final TokenInfo tokenInfo;
    // LBG private final UserContextService userContextService;

    @GetMapping("/{username}/read")
    @Operation(
            summary = "알림 데이터 조회",
            description = "알림 데이터를 조회 함 "
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 데이터 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<AlarmListResponse> getRead(@PathVariable String username) {
        // 알람 목록 조회
        // 토큰에서 유저정보 추출하여 모의해킹 취약점 대응
        List<GpoAlarmsMas> alarms = alarmService.getUnreadAlarms(tokenInfo.getUserName());

        // AlarmListResponse 객체 생성
        AlarmListResponse response = AlarmListResponse.from(alarms);

        return AxResponseEntity.ok(response, "알림 정보를 성공적으로 조회하였습니다.");
    }

    @PutMapping("/{alarmId}/{username}/read")
    @Operation(
            summary = "알림 데이터 읽음 변경",
            description = "조회된 알림 데이터 클릭시 읽음으로 처리 함"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 데이터 생성 성공"),
    })
    public void markAsRead(@PathVariable String alarmId, @PathVariable String username) {
        // 토큰에서 유저정보 추출하여 모의해킹 취약점 대응
        alarmService.markAsRead(alarmId, tokenInfo.getUserName());
    }

    @PutMapping("/{username}/readBulk")
    @Operation(
            summary = "알림 데이터 읽음 변경",
            description = "조회된 알림 데이터 클릭시 읽음으로 처리 함"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 데이터 생성 성공"),
    })
    public void markAsReadBulk(@PathVariable String username) {
        // 토큰에서 유저정보 추출하여 모의해킹 취약점 대응
        alarmService.markAsReadBulk(tokenInfo.getUserName());
    }

    @PutMapping("/{alarmId}/{username}/cancel")
    @Operation(
            summary = "알림 데이터 읽음 변경",
            description = "조회된 알림 데이터 클릭시 읽음으로 처리 함"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 데이터 생성 성공"),
    })
    public void cancelAlarmInfo(@PathVariable String alarmId, @PathVariable String username) {
        log.debug("+++++++++++++++++++++++++++++++++++++++++++++ markAsRead alarmId={}, username={}", alarmId, username);
        // 토큰에서 유저정보 추출하여 모의해킹 취약점 대응
        alarmService.cancelAlarmInfo(alarmId, tokenInfo.getUserName());
    }


    @PostMapping
    @Operation(
            summary = "알림 데이터 입력",
            description = "알림 데이터를 생성 함 "
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 데이터 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<GpoAlarmsMas> create(@RequestBody AlarmCreateRequest request) {
        // 이렇게 가져올수도 있음
        // LBG String username = userContextService.getAuthUsername();

        if (request != null && "".equals(request.getTargetUser())) {
            // request.setUsername(username);
        }
        // alarmService.createAlarm()
        return AxResponseEntity.ok(null, "일림정보를 성공적으로 생성 되었습니다.");
    }

    @GetMapping("/approvalUserInfo/{alarmId}")
    public AxResponseEntity<ApprovalUserInfo> approvalUserInfo(@PathVariable String alarmId) {
        log.debug("+++++++++++++++++++++++++++++++++++++ alarmId={}", alarmId);
        ApprovalUserInfo approvalUserInfo = alarmService.getApprovalUsrInfo(alarmId);

        return AxResponseEntity.ok(approvalUserInfo, "일림정보를 성공적으로 생성 되었습니다.");
    }
}
