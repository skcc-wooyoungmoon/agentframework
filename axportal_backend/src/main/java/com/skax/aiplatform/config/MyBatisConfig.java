package com.skax.aiplatform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis 설정
 * 
 * <p>Primary DataSource(PostgreSQL/Tibero)를 위한 MyBatis 설정입니다.
 * Vertica는 JdbcTemplate만 사용하므로 MyBatis에서 제외됩니다.</p>
 * 
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-11-19
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@MapperScan(
    basePackages = "com.skax.aiplatform.mapper",
    annotationClass = org.apache.ibatis.annotations.Mapper.class,
    sqlSessionTemplateRef = "sqlSessionTemplate"
)
public class MyBatisConfig {

    /**
     * SqlSessionFactory 생성
     * 
     * <p>Primary DataSource(PostgreSQL/Tibero)만 사용합니다.
     * Vertica DataSource는 별도의 JdbcTemplate으로 처리됩니다.</p>
     * 
     * @param dataSource Primary DataSource (Spring Boot 자동 설정)
     * @return SqlSessionFactory
     * @throws Exception 설정 오류
     */
    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        log.info("🔧 MyBatis SqlSessionFactory 초기화 시작");
        log.info("   - DataSource: {}", dataSource.getClass().getSimpleName());
        
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // Mapper XML 파일 위치 설정
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mappers/**/*.xml"));
        
        // 타입 별칭 패키지 설정
        sessionFactory.setTypeAliasesPackage("com.skax.aiplatform.entity");
        
        // MyBatis Configuration 설정
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);  // snake_case -> camelCase 자동 변환
        configuration.setCacheEnabled(true);               // 2차 캐시 활성화
        configuration.setLazyLoadingEnabled(true);        // 지연 로딩 활성화
        configuration.setAggressiveLazyLoading(false);    // 적극적 지연 로딩 비활성화
        
        sessionFactory.setConfiguration(configuration);
        
        log.info("✅ MyBatis SqlSessionFactory 초기화 완료");
        return sessionFactory.getObject();
    }

    /**
     * SqlSessionTemplate 생성
     * 
     * @param sqlSessionFactory SqlSessionFactory
     * @return SqlSessionTemplate
     */
    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        log.info("🔧 MyBatis SqlSessionTemplate 초기화");
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}