package com.skax.aiplatform.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 정의 열거형
 *
 * <p>애플리케이션에서 발생할 수 있는 모든 에러 코드를 정의합니다.
 * HTTP 상태 코드, 에러 코드, 에러 메시지를 포함합니다.</p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-08-01
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 4xx Client Errors
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 타입 값입니다"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "C003", "필수 요청 파라미터가 누락되었습니다"),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "C004", "잘못된 JSON 형식입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C005", "지원하지 않는 HTTP 메서드입니다"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "C006", "요청 횟수를 초과했습니다"),

    // 인증/인가 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A004", "접근 권한이 없습니다"),
    INSUFFICIENT_PRIVILEGES(HttpStatus.FORBIDDEN, "A005", "권한이 부족합니다"),
    AUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A006", "서비스 오류로 로그인할 수 없습니다.\n" +
            "자세한 내용은 포탈 관리자에게 문의해주세요."),
    SSO_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A007", "SSO 연계 로그인에 실패했습니다."),
    SMS_AUTH_FAILED(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "A008", "인증번호를 잘못 입력했습니다. 정확하게 입력해 주세요."),

    // 리소스 관련 에러
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "요청한 리소스를 찾을 수 없습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "사용자를 찾을 수 없습니다"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "역할을 찾을 수 없습니다"),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "R004", "프로젝트를 찾을 수 없습니다"),
    USER_PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "R005", "사용자가 해당 프로젝트에 참여하고 있지 않습니다"),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "R006", "조회된 데이터가 없습니다"),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "R007", "이미지를 찾을 수 없습니다"),

    // 중복 데이터 관련 에러
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "D001", "이미 존재하는 리소스입니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "D002", "이미 존재하는 이메일입니다"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "D003", "이미 존재하는 사용자명입니다"),
    PROJECT_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "D005", "동일한 이름의 프로젝트가 이미 존재합니다. \n" +
            "다른 이름을 입력 후 다시 시도해주세요."),
    ROLE_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "D006", "이미 동일한 이름의 역할이 존재합니다.\n" +
            "역할명을 변경 후 다시 시도해주세요."),
    SAFETY_FILTER_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "D007", "입력하신 분류명이 이미 존재합니다. \n" +
            "다른 분류명을 입력해주세요."),
    PROJECT_NAME_IN_APPROVAL(HttpStatus.CONFLICT, "D008", "해당 프로젝트명으로 생성 참여 요청 진행 중 입니다.\n" +
            "다른 프로젝트명을 입력해주세요."),
    DUPLICATE_IMAGE_NAME(HttpStatus.CONFLICT, "D009", "해당 도구에 동일한 이미지명이 이미 존재합니다. \n" +
            "다른 이미지명을 입력 후 다시 시도해주세요."),

    // 비즈니스 로직 관련 에러
    BUSINESS_LOGIC_ERROR(HttpStatus.BAD_REQUEST, "B001", "비즈니스 로직 오류입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "B002", "잘못된 비밀번호입니다"),
    ACCOUNT_LOCKED(HttpStatus.BAD_REQUEST, "B003", "계정이 잠겨있습니다"),
    ACCOUNT_DISABLED(HttpStatus.BAD_REQUEST, "B004", "퇴사한 사용자는 로그인할 수 없습니다."),
    PROJECT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "B005", "이미 종료된 프로젝트입니다"),
    CANNOT_DELETE_DEFAULT_ROLE(HttpStatus.BAD_REQUEST, "B006", "기본 역할은 삭제할 수 없습니다"),
    ROLE_HAS_ACTIVE_USERS(HttpStatus.BAD_REQUEST, "B007", "역할에 할당된 구성원이 있는 경우 역할을 삭제할 수 없습니다. 구성원 삭제 후 다시 시도해주세요."),
    ROLE_NOT_BELONG_TO_PROJECT(HttpStatus.FORBIDDEN, "B008", "해당 프로젝트의 역할이 아닙니다"),
    PROJECT_NEEDS_AT_LEAST_ONE_MANAGER(HttpStatus.BAD_REQUEST, "B009", "프로젝트 운영을 위해 최소 1명 이상의 프로젝트 관리자가 필요합니다. 최소 1명 " +
            "이상을 프로젝트 관리자로 설정한 뒤 다시 시도해 주세요."),
    CANNOT_DELETE_PUBLIC_PROJECT(HttpStatus.BAD_REQUEST, "B010", "공개 프로젝트는 종료할 수 없습니다"),
    ACCOUNT_INACTIVE_LONG_TERM(HttpStatus.FORBIDDEN, "B011", "마지막 로그인일 이후 1년이 경과하여 로그인이 불가합니다. 계정 활성화를 위해 포탈 관리자에게 " +
            "문의해주세요."),
    LAST_PORTAL_ADMIN_CANNOT_CHANGE_ROLE(HttpStatus.BAD_REQUEST, "B012", "포탈 관리자는 최소 1명 이상이어야 합니다. 다른 사용자를 포탈 관리자로 " +
            "지정한 후 다시 시도해주세요."),
    CANNOT_DELETE_PROJECT_HAS_PUBLIC_ASSETS(HttpStatus.BAD_REQUEST, "B013", "프로젝트를 종료할 수 없습니다.\n" +
            "포탈 관리자에게 요청해주세요."),
    CANNOT_DELETE_ONGOING_PROJECT(HttpStatus.BAD_REQUEST, "B014", "결재 진행중인 프로젝트가 아닙니다."),
    GUARDRAIL_MODEL_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "B015", "이미 사용중인 배포 모델이 포함되어 있는 경우 가드레일을 생성할 수 없습니다. 해당 모델" +
            " 제외 후 다시 시도해주세요."),

    // 외부 서비스 관련 에러
    EXTERNAL_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "E001", "외부 서비스 오류입니다"),
    EXTERNAL_SERVICE_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "E002", "외부 서비스 응답 시간 초과입니다"),
    EXTERNAL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "E003", "외부 서비스를 사용할 수 없습니다"),

    // 외부 API 관련 에러
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "E004", "외부 API 호출 오류"),
    EXTERNAL_API_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E005", "외부 API 인증 실패"),
    EXTERNAL_API_FORBIDDEN(HttpStatus.FORBIDDEN, "E006", "해당 기능에 대한 권한이 없습니다."),
    EXTERNAL_API_NOT_FOUND(HttpStatus.NOT_FOUND, "E007", "외부 API 리소스 없음"),
    EXTERNAL_API_BAD_REQUEST(HttpStatus.BAD_REQUEST, "E008", "외부 API 잘못된 요청"),
    EXTERNAL_API_VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "E009", "외부 API 유효성 검증 실패"),
    EXTERNAL_API_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "E010", "외부 API 서버 오류"),
    EXTERNAL_API_CONFLICT(HttpStatus.CONFLICT, "E011", "외부 API 리소스 충돌"),


    // 5xx Server Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "데이터베이스 오류가 발생했습니다"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "S003", "서비스를 사용할 수 없습니다"),

    // 파일 관련 에러
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "파일을 찾을 수 없습니다"),
    FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "F002", "파일 업로드 중 오류가 발생했습니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "F003", "파일 크기가 제한을 초과했습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "F004", "지원하지 않는 파일 형식입니다"),

    // 데이터 검증 관련 에러
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "V001", "데이터 검증 오류입니다"),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "V002", "데이터 제약 조건 위반입니다"),
    DATA_INTEGRITY_ERROR(HttpStatus.BAD_REQUEST, "V003", "데이터 무결성 오류입니다"),
    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST, "V004", "잘못된 날짜 형식입니다."),

    // DATE
    INVALID_PERIOD_30DAYS(HttpStatus.BAD_REQUEST, "D001", "30일을 초과한 데이터는 모니터링할 수 없습니다.\n조회 기간을 다시 설정 해 주세요."),
    INVALID_PERIOD_72HOURS(HttpStatus.BAD_REQUEST, "D002", "조회 기간이 72시간을 초과합니다.\n조회 기간을 72시간 이하로 설정해주세요."),


    // 모델 카탈로그 관련 에러
    MODEL_CTLG_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "해당 모델 카탈로그 정보를 찾을 수 없습니다"),
    MODEL_CTLG_DELETE_FAILED(HttpStatus.BAD_REQUEST, "C002", "모델 카탈로그 삭제에 실패했습니다"),
    MODEL_CTLG_AFTER_DELETE_FAILED(HttpStatus.BAD_REQUEST, "C003", "모델 카탈로그 삭제 후 모델 가든 정보 업데이트에 실패했습니다."),

    // 모델 배포 관련 에러
    INVALID_MODEL_DEPLOY_STATUS(HttpStatus.BAD_REQUEST, "M001", "잘못된 모델 배포 상태입니다."),
    INVALID_MODEL_QUERY_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "M002", "추론 성능 결과를 조회할 수 없습니다."),
    INVALID_MODEL_DEPLOY_INFO(HttpStatus.BAD_REQUEST, "M004", "모델 배포 정보를 찾을 수 없습니다."),

    // 모델 가든 관련 에러
    MODEL_GARDEN_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "해당 모델 가든 등록 정보를 찾을 수 없습니다"),
    MODEL_GARDEN_INVALID_STATUS(HttpStatus.NOT_FOUND, "G002", "해당 모델 가든 등록 정보를 찾을 수 없습니다"),
    MODEL_GARDEN_DELETE_FAILED(HttpStatus.BAD_REQUEST, "G003", "모델 가든 삭제에 실패했습니다"),
    MODEL_GARDEN_DUPLICATE_NAME(HttpStatus.CONFLICT, "G004", "이미 같은 이름의 모델이 반입되어 있습니다"),
    MODEL_GARDEN_IMPORT_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "G005", "모델 가든 반입 취소에 실패했습니다"),
    MODEL_GARDEN_CHANGE_STATUS_OUTER(HttpStatus.INTERNAL_SERVER_ERROR, "G006", "모델 가든 상태 처리에 실패했습니다"),

    // API KEY 관련 에러
    API_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "K001", "해당 API KEY를 찾을 수 없습니다"),
    API_KEY_INVALID_REPLENISH_INTERVAL_TYPE(HttpStatus.BAD_REQUEST, "K002", "유효하지 않은 갱신 주기입니다."),
    API_KEY_CREATE_FAILED(HttpStatus.BAD_REQUEST, "K003", "API KEY 생성에 실패했습니다."),
    API_KEY_ALREADY_EXISTS(HttpStatus.CONFLICT, "K004", "이미 발급된 사용자 API Key가 존재합니다."),

    // API GW 관련 에러
    API_GW_WORK_GROUP_REGIST_FAILED(HttpStatus.BAD_REQUEST, "W001", "API GW 업무 코드 등록에 실패했습니다."),
    API_GW_WORK_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "W002", "API GW 업무 코드를 찾을 수 없습니다."),


    // 간편결재 에러
    SWING_APPROVAL_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "P001", "결재요청을 실패하였습니다."),

    // 지식 테이트 에러
    KWLG_TEST_FAILED(HttpStatus.BAD_REQUEST, "Z001", "테스트를 실패하였습니다.\n지식 등록 설정값 또는 호출 오류일 수 있으니, 확인 후 다시 시도해 주세요.");

    /// ////// Error ENUM  /////////
    private final HttpStatus status;
    private final String code;
    private final String message;
}
