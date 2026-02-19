package com.skax.aiplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP 설정
 * 
 * <p>Aspect Oriented Programming 설정을 담당합니다.
 * 로깅 인터셉터를 활성화하기 위해 AOP를 활성화합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class AopConfig {
}
