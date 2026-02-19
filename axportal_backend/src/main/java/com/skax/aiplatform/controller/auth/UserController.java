package com.skax.aiplatform.controller.auth;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.service.auth.AuthService;
import com.skax.aiplatform.service.auth.UsersService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.dto.auth.response.UsersMeRes;

@Slf4j
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UsersService usersService;

    /**
     * 현재 사용자 정보 조회
     *
     * @return 사용자 정보 응답
     */
    @GetMapping("/me")
    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "현재 인증된 사용자의 정보를 조회합니다.",
            security = {
                    @SecurityRequirement(name = "OAuth2PasswordBearer"),
                    @SecurityRequirement(name = "HTTPBearer")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public AxResponseEntity<UsersMeRes> getUsersMe() throws Exception {
        log.info("현재 사용자 정보 조회 요청");

        UsersMeRes userInfo = usersService.getUserInfo();

        return AxResponseEntity.ok(userInfo, "사용자 정보를 성공적으로 조회했습니다.");
    }

    /**
     * 기본그룹(public) exchange 토큰 발급
     *
     * @return JWT(exchanged) 토큰 응답
     */
    @PostMapping(value = "/exchange/default")
    @Operation(
            summary = "기본그룹(public) exchange 토큰 발급",
            description = "기본그룹(public) exchange 토큰 발급",
            security = {
                    @SecurityRequirement(name = "OAuth2PasswordBearer"),
                    @SecurityRequirement(name = "HTTPBearer")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "exchange 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<Boolean> exchangeDefault() throws Exception {
        log.info("exchangeDefault 요청");

        try {
            authService.exchangeAndSave();
            log.info("exchangeDefault 성공");

            return AxResponseEntity.ok(true, "토큰이 성공적으로 갱신되었습니다.");
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("exchangeDefault 실패 (BusinessException): {}", e.getMessage(), e);
            throw e;
        } catch (BadCredentialsException e) {
            // 인증 실패 예외는 그대로 전파
            log.error("exchangeDefault 실패 (BadCredentialsException): {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("exchangeDefault 실패 (잘못된 인자): {}", e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.EXTERNAL_API_ERROR, "exchange 실패했습니다: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("exchangeDefault 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.EXTERNAL_API_ERROR, "exchange 실패했습니다: 데이터베이스 오류");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("exchangeDefault 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.EXTERNAL_API_ERROR, "exchange 실패했습니다.");
        }
    }

    /**
     * 프로젝트 그룹 exchange 토큰 발급
     *
     * prjSeq에 해당하는 현재 사용자 roleSeq를 조회하여 P{prjSeq}_R{roleSeq} 그룹으로 교환
     */
    @PostMapping(value = "/exchange/group")
    @Operation(
            summary = "프로젝트 그룹 exchange 토큰 발급",
            description = "prjSeq에 해당하는 현재 사용자 roleSeq를 조회하여 P{prjSeq}_R{roleSeq} 그룹으로 교환합니다.",
            security = {
                    @SecurityRequirement(name = "OAuth2PasswordBearer"),
                    @SecurityRequirement(name = "HTTPBearer")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "exchange 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<Boolean> exchangeGroup(@RequestParam("prjSeq") Long prjSeq) throws Exception {
        log.info("exchangeGroup 요청: prjSeq={}", prjSeq);
        try {
            authService.exchangeAndSave(prjSeq);
            log.info("exchangeGroup 성공");
            return AxResponseEntity.ok(true, "토큰이 성공적으로 갱신되었습니다.");
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("exchangeGroup 실패 (BusinessException): prjSeq={}, error={}", prjSeq, e.getMessage(), e);
            throw e;
        } catch (BadCredentialsException e) {
            // 인증 실패 예외는 그대로 전파
            log.error("exchangeGroup 실패 (BadCredentialsException): prjSeq={}, error={}", prjSeq, e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("exchangeGroup 실패 (잘못된 인자): prjSeq={}, error={}", prjSeq, e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.EXTERNAL_API_ERROR, "exchange 실패했습니다: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("exchangeGroup 실패 (데이터베이스 오류): prjSeq={}, error={}", prjSeq, e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.EXTERNAL_API_ERROR, "exchange 실패했습니다: 데이터베이스 오류");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("exchangeGroup 실패 (예상치 못한 오류): prjSeq={}, error={}", prjSeq, e.getMessage(), e);
            return AxResponseEntity.error(ErrorCode.EXTERNAL_API_ERROR, "exchange 실패했습니다.");
        }
    }
}
