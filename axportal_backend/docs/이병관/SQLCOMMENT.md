# 🎯 DB 모니터링을 위한 SQL COMMENT 자동 삽입 시스템 구현 가이드

## 📋 요구사항 정의

### 🎯 **핵심 목표**
모든 JPA 쿼리에 자동으로 `/* ServiceImpl명.Method명.Repository명.Method명 */` 형태의 주석을 추가하여 DB 모니터링 툴에서 SQL 출처를 추적 가능하게 구현

### 📌 **상세 요구사항**
1. **주석 형식**: `/* AuthServiceImpl.login.MemberRepository.findByMemberId */`
2. **적용 범위**: 모든 JPA SQL (SELECT, UPDATE, INSERT, DELETE)
3. **DB 전송**: 실제 데이터베이스 서버에 주석이 포함된 SQL 전송
4. **모니터링 도구 가시성**: DB 모니터링 툴에서 주석 확인 가능

## 🏗️ 구현 아키텍처

### 📊 **시스템 구성도**
```
Service Layer (AOP)
    ↓ SQL 컨텍스트 설정
Repository Layer (AOP)
    ↓ Repository 정보 추가
SQL 실행 단계
    ├── SELECT: Hibernate StatementInspector
    └── DML: JDBC Connection Proxy → 실제 DB 전송
```

### 🔧 **핵심 컴포넌트**

#### 1. **SQL 컨텍스트 관리** (`SqlCommentContext.java`)
```java
@Component
public class SqlCommentContext {
    private static final ThreadLocal<SqlContext> contextHolder = new ThreadLocal<>();
    
    public static void setServiceContext(String serviceName, String methodName) {
        // Service 컨텍스트 설정
    }
    
    public static void addRepositoryContext(String repositoryName, String methodName) {
        // Repository 컨텍스트 추가
    }
    
    public static String getCurrentComment() {
        // 현재 컨텍스트 기반 주석 생성
        return "ServiceImpl.method.Repository.method";
    }
}
```

#### 2. **Service AOP 인터셉터** (`ServiceSqlCommentAspect.java`)
```java
@Aspect
@Component
public class ServiceSqlCommentAspect {
    @Around("execution(* com.skax.aiplatform.service..*.*(..)) && " +
            "target(org.springframework.stereotype.Service)")
    public Object interceptServiceMethods(ProceedingJoinPoint joinPoint) {
        // Service 메서드 정보를 컨텍스트에 설정
        String serviceName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        SqlCommentContext.setServiceContext(serviceName, methodName);
    }
}
```

#### 3. **Repository AOP 인터셉터** (`RepositorySqlCommentAspect.java`)
```java
@Aspect
@Component
public class RepositorySqlCommentAspect {
    @Around("execution(* org.springframework.data.repository.Repository+.*(..))")
    public Object interceptRepositoryMethods(ProceedingJoinPoint joinPoint) {
        // Repository 메서드 정보를 컨텍스트에 추가
        String repositoryName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        SqlCommentContext.addRepositoryContext(repositoryName, methodName);
    }
}
```

#### 4. **Hibernate StatementInspector** (`SqlCommentInterceptor.java`)
```java
@Component
public class SqlCommentInterceptor implements StatementInspector {
    @Override
    public String inspect(String sql) {
        String comment = SqlCommentContext.getCurrentComment();
        if (comment != null && !comment.trim().isEmpty()) {
            // SELECT 쿼리에 주석 추가 (Hibernate 로그용)
            return String.format("/* %s */ %s", comment, sql);
        }
        return sql;
    }
}
```

#### 5. **JDBC Connection Proxy** (`JdbcConnectionProxy.java`)
```java
@Component
public class JdbcConnectionProxy {
    public DataSource wrapDataSource(DataSource originalDataSource) {
        // DataSource를 프록시로 래핑
        return (DataSource) Proxy.newProxyInstance(
            DataSource.class.getClassLoader(),
            new Class[]{DataSource.class},
            new DataSourceInvocationHandler(originalDataSource)
        );
    }
    
    private static String addCommentToSql(String originalSql) {
        // UPDATE/INSERT/DELETE 쿼리에 주석 강제 추가 (실제 DB 전송용)
        String comment = SqlCommentContext.getCurrentComment();
        if (isDmlOperation(originalSql)) {
            return String.format("/* %s */ %s", comment, cleanSql);
        }
        return originalSql;
    }
}
```

#### 6. **DataSource 설정** (`MonitoringDataSourceConfig.java`)
```java
@Configuration
@Profile({"elocal", "local", "edev"}) // 개발 환경에서만 활성화
public class MonitoringDataSourceConfig {
    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("originalDataSource") DataSource originalDataSource,
                                 JdbcConnectionProxy jdbcProxy) {
        return jdbcProxy.wrapDataSource(originalDataSource);
    }
}
```

## 📝 구현 단계별 가이드

### **1단계: SQL 컨텍스트 관리 구현**
```java
// ThreadLocal 기반 컨텍스트 관리
// 트랜잭션 동기화를 통한 생명주기 관리
// Service와 Repository 정보를 조합한 주석 생성
```

### **2단계: AOP 인터셉터 구현**
```java
// Service 계층 AOP: @Service 어노테이션 대상
// Repository 계층 AOP: Spring Data Repository 인터페이스 대상
// 메서드 시작/종료 시점에 컨텍스트 설정/정리
```

### **3단계: Hibernate StatementInspector 구현**
```java
// SELECT 쿼리 주석 처리 (Hibernate 로그 출력용)
// Hibernate 설정에 StatementInspector 등록
// 주석 중복 방지 로직
```

### **4단계: JDBC Connection Proxy 구현**
```java
// UPDATE/INSERT/DELETE 쿼리 주석 처리 (실제 DB 전송용)
// PreparedStatement 생성 시점에 SQL 주석 추가
// Dynamic Proxy를 통한 Connection 래핑
```

### **5단계: DataSource 설정 및 통합**
```java
// 개발 환경에서만 프록시 활성화
// 기존 DataSource를 프록시로 래핑
// Profile 기반 조건부 활성화
```

## 🔧 핵심 기술 스택

### **필수 의존성**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### **설정 파일**
```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        session_factory:
          statement_inspector: com.example.SqlCommentInterceptor
```

## 🎯 예상 결과

### **DB 모니터링 툴에서 확인되는 SQL**
```sql
-- SELECT 쿼리
/* AuthServiceImpl.login.MemberRepository.findByMemberId */ 
SELECT m.member_id, m.name FROM members m WHERE m.member_id = ?

-- UPDATE 쿼리
/* AuthServiceImpl.login.MemberRepository.save */ 
UPDATE users SET last_login = ? WHERE member_id = ?

-- INSERT 쿼리
/* UserServiceImpl.createUser.UserRepository.save */ 
INSERT INTO users (name, email) VALUES (?, ?)
```

### **로그 출력 예시**
```
✅ SQL 주석 교체 완료 - 타입: SELECT, 주석: AuthServiceImpl.login.MemberRepository.findByMemberId
🔄 DML 쿼리 JDBC 재확인 - 타입: UPDATE, 주석: AuthServiceImpl.login.MemberRepository.save
🚀 JDBC PreparedStatement 주석 적용됨 - 길이: 120 -> 180
```

## 🚨 주의사항

### **제한사항**
1. **Hibernate StatementInspector**: UPDATE/INSERT/DELETE는 반환값 무시됨
2. **JDBC Proxy 필요성**: DML 쿼리는 JDBC 레벨에서 처리 필수
3. **성능 영향**: 개발 환경에서만 활성화 권장

### **환경별 설정**
```java
@Profile({"elocal", "local", "edev"}) // 개발 환경만
// @Profile("!prod") // 운영 환경 제외
```

## 📊 성과 지표

### **달성 목표**
- ✅ 모든 SQL 타입 주석 적용률: 100%
- ✅ DB 모니터링 도구 가시성: 완전 지원
- ✅ Service → Repository 추적: 완벽한 호출 경로 표시
- ✅ 성능 영향: 개발 환경 제한으로 최소화

## 🔍 구현된 파일 목록

### **핵심 구현 파일**
```
src/main/java/com/skax/aiplatform/
├── common/sql/
│   ├── SqlCommentContext.java                    # ThreadLocal 기반 컨텍스트 관리
│   └── interceptor/
│       ├── SqlCommentInterceptor.java            # Hibernate StatementInspector
│       └── JdbcConnectionProxy.java              # JDBC Connection Proxy
├── config/
│   ├── sql/aspect/
│   │   ├── ServiceSqlCommentAspect.java          # Service AOP 인터셉터
│   │   └── RepositorySqlCommentAspect.java       # Repository AOP 인터셉터
│   └── datasource/
│       └── MonitoringDataSourceConfig.java      # DataSource 프록시 설정
```

### **설정 파일**
```
src/main/resources/
├── application-elocal.yml    # 개발 환경 설정
├── application-local.yml     # 로컬 환경 설정
└── application-edev.yml      # 외부개발 환경 설정
```

## 🎉 구현 완료 확인

### **성공 로그 패턴**
```
INFO  SqlCommentInterceptor : ✅ SQL 주석 교체 완료 - 타입: SELECT
INFO  JdbcConnectionProxy   : 🔄 DML 쿼리 JDBC 재확인 - 타입: UPDATE
INFO  JdbcConnectionProxy   : ✅ JDBC DML SQL 주석 강제 추가 완료 - 타입: UPDATE
```

### **DB 모니터링 툴 확인 사항**
1. SELECT 쿼리: Hibernate 로그에서 주석 확인
2. UPDATE/INSERT/DELETE: 실제 DB 서버에서 주석 포함된 SQL 확인
3. 주석 형식: `/* ServiceImpl.method.Repository.method */`

이 가이드를 따라 구현하면 **완벽한 DB 모니터링용 SQL 주석 시스템**을 구축할 수 있습니다! 🎉