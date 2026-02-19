package com.skax.aiplatform.client.shinhan.config;

import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

/**
 * Shinhan Feign Client 설정
 * 
 * <p>Shinhan 승인 시스템 API와의 통신을 위한 Feign Client 설정을 정의합니다.
 * 타임아웃, 재시도, 에러 처리 등의 공통 설정을 포함합니다.</p>
 * 
 * <p>이 클래스는 @Configuration 어노테이션을 사용하지 않습니다.
 * 전역 Bean 등록을 피하고 Shinhan FeignClient에만 적용되도록 합니다.</p>
 * 
 * <h3>주요 설정:</h3>
 * <ul>
 *   <li><strong>타임아웃</strong>: 연결 10초, 읽기 60초</li>
 *   <li><strong>재시도</strong>: 최대 3회, 지수 백오프</li>
 *   <li><strong>에러 처리</strong>: 커스텀 에러 디코더</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-11-17
 * @version 1.0
 */
public class ShinhanClientConfig {
    
    /**
     * Shinhan API 요청 인터셉터 설정
     * 
     * <p>Shinhan FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.</p>
     * 
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor shinhanRequestInterceptor() {
        return new ShinhanRequestInterceptor();
    }
    
    /**
     * Shinhan API 요청 옵션 설정
     * 
     * <p>Shinhan FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.</p>
     * 
     * <p>연결 타임아웃과 읽기 타임아웃을 설정합니다.</p>
     * 
     * @return 설정된 요청 옵션
     */
    @Bean
    public Request.Options shinhanRequestOptions() {
        return new Request.Options();
    }
    
    /**
     * 재시도 설정
     * 
     * <p>Shinhan FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.</p>
     * 
     * <p>네트워크 오류나 일시적 장애 시 자동 재시도를 수행합니다.</p>
     * 
     * @return 재시도 설정
     */
    @Bean
    public Retryer shinhanRetryer() {
        return new Retryer.Default(
            1000,  // 초기 재시도 간격: 1초
            3000,  // 최대 재시도 간격: 3초
            3      // 최대 재시도 횟수: 3회
        );
    }
    
    /**
     * Shinhan API 에러 디코더 설정
     * 
     * <p>Shinhan FeignClient에서만 사용되어 전역 Bean 등록을 방지합니다.</p>
     * 
     * <p>Shinhan API 에러 응답을 적절한 예외로 변환합니다.</p>
     * 
     * @return 커스텀 에러 디코더
     */
    @Bean
    public ErrorDecoder shinhanErrorDecoder() {
        return new ShinhanErrorDecoder();
    }
}
