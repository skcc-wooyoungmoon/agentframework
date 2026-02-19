package com.skax.aiplatform.common.constant;

/**
 * 애플리케이션 전역 상수 정의 클래스
 *
 * <p>프로젝트에서 사용되는 모든 상수들을 중앙 집중 관리합니다.
 * Magic Number 사용을 방지하고 코드의 가독성을 향상시킵니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
public final class Constants {

    private Constants() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    /**
     * API 관련 상수
     */
    public static final class Api {
        public static final String BASE_PATH = "/api/v1";
        public static final String HEALTH_CHECK = "/health";
        public static final String SWAGGER_PATH = "/swagger-ui.html";
        public static final String API_DOCS_PATH = "/api-docs";

        private Api() {}
    }

    /**
     * 보안 관련 상수
     */
    public static final class Security {
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String JWT_CLAIM_USER_ID = "userId";
        public static final String JWT_CLAIM_USER_EMAIL = "userEmail";
        public static final String JWT_CLAIM_AUTHORITIES = "authorities";

        // Authentication Principal 타입 체크용 상수
        public static final String ANONYMOUS_USER = "anonymousUser";
        public static final String PRINCIPAL_TYPE_STRING = "STRING";
        public static final String PRINCIPAL_TYPE_USER_DETAILS = "USER_DETAILS";

        // 인증 오류 메시지
        public static final String INVALID_PRINCIPAL_TYPE = "Principal 타입이 올바르지 않습니다";
        public static final String USER_NOT_AUTHENTICATED = "사용자가 인증되지 않았습니다";
        public static final String PRINCIPAL_NOT_FOUND = "Principal 정보를 찾을 수 없습니다";

        private Security() {}
    }

    /**
     * 페이징 관련 상수
     */
    public static final class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 20;
        public static final int MAX_PAGE_SIZE = 100;
        public static final String DEFAULT_SORT_DIRECTION = "desc";
        public static final String DEFAULT_SORT_PROPERTY = "createdAt";

        private Pagination() {}
    }

    /**
     * 응답 메시지 상수
     */
    public static final class Message {
        public static final String SUCCESS = "성공";
        public static final String CREATED = "생성 완료";
        public static final String UPDATED = "수정 완료";
        public static final String DELETED = "삭제 완료";
        public static final String NOT_FOUND = "데이터를 찾을 수 없습니다";
        public static final String UNAUTHORIZED = "인증이 필요합니다";
        public static final String FORBIDDEN = "접근 권한이 없습니다";
        public static final String BAD_REQUEST = "잘못된 요청입니다";
        public static final String INTERNAL_SERVER_ERROR = "서버 내부 오류가 발생했습니다";

        // 인증 관련 메시지
        public static final String AUTHENTICATION_FAILED = "인증에 실패했습니다";
        public static final String INVALID_TOKEN = "유효하지 않은 토큰입니다";
        public static final String TOKEN_EXPIRED = "토큰이 만료되었습니다";
        public static final String ACCESS_DENIED = "접근이 거부되었습니다";

        private Message() {}
    }

    /**
     * 예외 처리 관련 상수
     */
    public static final class Exception {
        // 예외 타입
        public static final String AUTHENTICATION_EXCEPTION = "AuthenticationException";
        public static final String AUTHORIZATION_EXCEPTION = "AuthorizationException";
        public static final String VALIDATION_EXCEPTION = "ValidationException";
        public static final String BUSINESS_EXCEPTION = "BusinessException";
        public static final String CLASS_CAST_EXCEPTION = "ClassCastException";

        // 예외 코드
        public static final String AUTH_001 = "AUTH_001"; // 인증 실패
        public static final String AUTH_002 = "AUTH_002"; // 권한 부족
        public static final String AUTH_003 = "AUTH_003"; // 토큰 만료
        public static final String AUTH_004 = "AUTH_004"; // Principal 타입 오류
        public static final String VAL_001 = "VAL_001";   // 입력값 검증 실패
        public static final String BIZ_001 = "BIZ_001";   // 비즈니스 로직 오류

        private Exception() {}
    }

    /**
     * 데이터베이스 관련 상수
     */
    public static final class Database {
        public static final int DEFAULT_STRING_LENGTH = 255;
        public static final int LARGE_TEXT_LENGTH = 1000;
        public static final int EMAIL_LENGTH = 100;
        public static final int NAME_LENGTH = 50;
        public static final int CODE_LENGTH = 20;

        private Database() {}
    }

    /**
     * 캐시 관련 상수
     */
    public static final class Cache {
        public static final String USER_CACHE = "users";
        public static final String ROLE_CACHE = "roles";
        public static final int DEFAULT_TTL_SECONDS = 3600; // 1시간
        public static final int LONG_TTL_SECONDS = 86400; // 24시간

        private Cache() {}
    }

    /**
     * 날짜/시간 관련 상수
     */
    public static final class DateTime {
        public static final String DATE_FORMAT = "yyyy.MM.dd";
        public static final String DATETIME_FORMAT = "yyyy.MM.dd HH:mm:ss";
        public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        public static final String TIMEZONE_KST = "Asia/Seoul";
        public static final String TIMEZONE_UTC = "UTC";

        private DateTime() {}
    }

    /**
     * 파일 관련 상수
     */
    public static final class File {
        public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        public static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};
        public static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx"};

        private File() {}
    }

    /**
     * 정규식 패턴 상수
     */
    public static final class Pattern {
        public static final String EMAIL = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        public static final String PHONE = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
        public static final String KOREAN_NAME = "^[가-힣]{2,10}$";

        private Pattern() {}
    }

    /**
     * 로깅 관련 상수
     */
    public static final class Logging {
        // 로그 레벨
        public static final String TRACE = "TRACE";
        public static final String DEBUG = "DEBUG";
        public static final String INFO = "INFO";
        public static final String WARN = "WARN";
        public static final String ERROR = "ERROR";

        // 로그 카테고리
        public static final String CATEGORY_SERVICE = "SERVICE";
        public static final String CATEGORY_CONTROLLER = "CONTROLLER";
        public static final String CATEGORY_REPOSITORY = "REPOSITORY";
        public static final String CATEGORY_SECURITY = "SECURITY";
        public static final String CATEGORY_CLIENT = "CLIENT";

        // 로그 이벤트
        public static final String METHOD_START = "METHOD_START";
        public static final String METHOD_END = "METHOD_END";
        public static final String METHOD_ERROR = "METHOD_ERROR";
        public static final String AUTHENTICATION_SUCCESS = "AUTH_SUCCESS";
        public static final String AUTHENTICATION_FAILURE = "AUTH_FAILURE";
        public static final String API_CALL_START = "API_CALL_START";
        public static final String API_CALL_END = "API_CALL_END";
        public static final String API_CALL_ERROR = "API_CALL_ERROR";

        // 로그 형식
        public static final String LOG_SEPARATOR = "|";
        public static final String TRACE_ID_HEADER = "X-Trace-ID";
        public static final String SPAN_ID_HEADER = "X-Span-ID";

        // 민감한 정보 키워드
        public static final String[] SENSITIVE_KEYWORDS = {
            "password", "pwd", "secret", "token", "key", "auth", "credential", "authorization"
        };

        // 디버깅 허용 키워드 (ClassCastException 관련)
        public static final String[] DEBUG_KEYWORDS = {
            "classcastexception", "userdetails", "principal", "authentication"
        };

        // 로그 포맷
        public static final String MASKED_VALUE = "[MASKED]";
        public static final int MAX_LOG_LENGTH = 200;
        public static final String TRUNCATE_SUFFIX = "...";

        private Logging() {}
    }
}
