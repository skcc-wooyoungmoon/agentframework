package com.skax.aiplatform.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * MapStruct 설정
 * 
 * <p>MapStruct 매퍼들이 Spring Bean으로 등록되도록 보장합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-01
 * @version 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.skax.aiplatform.mapper")
public class MapStructConfig {
}
