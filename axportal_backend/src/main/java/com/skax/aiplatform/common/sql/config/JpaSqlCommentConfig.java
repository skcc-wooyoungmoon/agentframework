package com.skax.aiplatform.common.sql.config;

import com.skax.aiplatform.common.sql.interceptor.SqlCommentInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JPA SQL 주석 설정 클래스
 * 
 * <p>Hibernate StatementInspector를 등록하여 SQL 쿼리에 자동으로 주석을 추가합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-20
 * @version 1.0
 */
@Slf4j
@Configuration
public class JpaSqlCommentConfig {

    private final SqlCommentInterceptor sqlCommentInterceptor;
    
    public JpaSqlCommentConfig(SqlCommentInterceptor sqlCommentInterceptor) {
        this.sqlCommentInterceptor = sqlCommentInterceptor;
    }

    /**
     * Hibernate 속성 커스터마이저를 통해 StatementInspector 등록
     * 
     * @return HibernatePropertiesCustomizer 구현체
     */
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> {
            // StatementInspector 등록
            hibernateProperties.put("hibernate.session_factory.statement_inspector", sqlCommentInterceptor);
            
            // Hibernate 자동 주석 비활성화
            hibernateProperties.put("hibernate.use_sql_comments", false);
            
            // Criteria 쿼리 주석 비활성화
            hibernateProperties.put("hibernate.query.criteria_copy_tree", false);
            
            log.info("SQL Comment Interceptor 등록 완료 - Hibernate 기본 주석 비활성화");
        };
    }
}