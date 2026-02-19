package com.skax.aiplatform.common.interceptor;

import java.util.Collection;
import java.util.Optional;

import org.hibernate.LazyInitializationException;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Component;

import com.skax.aiplatform.common.constant.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * 로그 값 포맷팅 담당 클래스
 * 
 * <p>
 * 로그에 출력될 값들을 포맷팅하고 민감한 정보를 마스킹합니다.
 * </p>
 * 
 * @author sonmunwoo
 * @since 2025-10-19
 * @version 1.0.0
 */
@Slf4j
@Component
public class LogValueFormatter {

    /**
     * 메서드 파라미터 포맷팅
     * 
     * @param args 메서드 파라미터 배열
     * @return 포맷팅된 파라미터 문자열
     */
    public String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatValue(args[i]));
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * 메서드 실행 결과 포맷팅
     * 
     * @param result 메서드 실행 결과
     * @return 포맷팅된 결과 문자열
     */
    public String formatResult(Object result) {
        return formatValue(result);
    }

    /**
     * 객체 값 포맷팅 (민감한 정보 마스킹 + LazyInitializationException 방지)
     * 
     * @param value 포맷팅할 객체
     * @return 포맷팅된 문자열
     */
    public String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        try {
            // Hibernate 프록시 객체인지 확인
            if (value instanceof HibernateProxy) {
                return "HibernateProxy[" + value.getClass().getSimpleName() + "]";
            }

            // Optional 처리
            if (value instanceof Optional) {
                Optional<?> optional = (Optional<?>) value;
                if (optional.isPresent()) {
                    Object optValue = optional.get();
                    // Optional 내부 객체가 Hibernate 프록시인지 확인
                    if (optValue instanceof HibernateProxy) {
                        return "Optional[HibernateProxy[" + optValue.getClass().getSimpleName() + "]]";
                    }
                    return "Optional[" + formatSafeToString(optValue) + "]";
                } else {
                    return "Optional.empty";
                }
            }

            // Collection 처리
            if (value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                return collection.getClass().getSimpleName() + "[size=" + collection.size() + "]";
            }

            return formatSafeToString(value);

        } catch (LazyInitializationException e) {
            log.debug("LazyInitializationException 발생, 안전한 문자열로 변환: {}", e.getMessage());
            return "LazyEntity[" + value.getClass().getSimpleName() + "]";
        } catch (Exception e) {
            log.debug("결과 포맷팅 중 예외 발생: {}", e.getMessage());
            return "Error[" + value.getClass().getSimpleName() + "]";
        }
    }

    /**
     * 안전한 toString 호출 (민감한 정보 마스킹 포함)
     * 
     * @param obj 객체
     * @return 포맷팅된 문자열
     */
    private String formatSafeToString(Object obj) {
        try {
            String valueStr = String.valueOf(obj);
            String lowerValue = valueStr.toLowerCase();

            // Constants에서 정의된 민감한 키워드들로 마스킹 체크
            for (String keyword : Constants.Logging.SENSITIVE_KEYWORDS) {
                if (lowerValue.contains(keyword)) {
                    return Constants.Logging.MASKED_VALUE;
                }
            }

            // Constants에서 정의된 디버깅 허용 키워드들은 상세히 로깅 (디버깅 목적)
            for (String keyword : Constants.Logging.DEBUG_KEYWORDS) {
                if (lowerValue.contains(keyword)) {
                    return valueStr; // 마스킹 없이 전체 정보 표시
                }
            }

            // 긴 문자열 truncate
            if (valueStr.length() > Constants.Logging.MAX_LOG_LENGTH) {
                return valueStr.substring(0, Constants.Logging.MAX_LOG_LENGTH) + Constants.Logging.TRUNCATE_SUFFIX;
            }

            return valueStr;
        } catch (NullPointerException e) {
            log.debug("formatSafeToString 중 NullPointerException 발생: {}", e.getMessage());
            return "null";
        } catch (StringIndexOutOfBoundsException e) {
            log.debug("formatSafeToString 중 StringIndexOutOfBoundsException 발생: {}", e.getMessage());
            try {
                return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
            } catch (NullPointerException | SecurityException fallbackException) {
                log.debug("fallback 처리 중 예외 발생: {}", fallbackException.getMessage());
                return "Error[formatSafeToString]";
            }
        } catch (RuntimeException e) {
            log.debug("formatSafeToString 중 RuntimeException 발생: {}", e.getMessage());
            try {
                return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
            } catch (NullPointerException | SecurityException fallbackException) {
                log.debug("fallback 처리 중 예외 발생: {}", fallbackException.getMessage());
                return "Error[formatSafeToString]";
            }
        } catch (Error e) {
            log.error("formatSafeToString 중 심각한 오류(Error) 발생: {}", e.getMessage(), e);
            return "Error[formatSafeToString]";
        }
    }
}
