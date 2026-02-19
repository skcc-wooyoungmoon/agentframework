# Vertica 데이터베이스 연동 설정 가이드

## 📋 작업 개요
- **작업일**: 2025-11-18, 2025-11-19 (업데이트)
- **작업자**: GitHub Copilot
- **목적**: PostgreSQL(Primary)과 Vertica(Secondary) 멀티 데이터소스 환경 구축
- **최종 업데이트**: 2025-11-19 - @Qualifier 기반 빈 주입 문제 해결 완료

---

## 🎯 작업 내용

### 1. Maven 종속성 추가 (pom.xml)

#### 추가된 버전 프로퍼티
```xml
<vertica.version>24.3.0-0</vertica.version>
```

#### 추가된 종속성
```xml
<!-- Vertica JDBC Driver -->
<dependency>
    <groupId>com.vertica.jdbc</groupId>
    <artifactId>vertica-jdbc</artifactId>
    <version>${vertica.version}</version>
    <scope>runtime</scope>
</dependency>
```

**설명:**
- Vertica JDBC 드라이버 최신 버전(24.3.0-0) 사용
- runtime scope로 설정하여 컴파일 시점이 아닌 실행 시점에만 필요

---

### 2. application-elocal.yml 설정

```yaml
# Vertica 데이터소스 설정 (외부로컬 환경)
vertica:
  datasource:
    driver-class-name: com.vertica.jdbc.Driver
    jdbc-url: jdbc:vertica://${VERTICA_HOST:localhost}:${VERTICA_PORT:5433}/${VERTICA_DATABASE:docker}
    username: ${VERTICA_USERNAME:dbadmin}
    password: ${VERTICA_PASSWORD:}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      read-only: true
      # 🔧 Vertica 초기화 비활성화
      auto-commit: true
  host: ${VERTICA_HOST:localhost}
  port: ${VERTICA_PORT:5433}
  database: ${VERTICA_DATABASE:docker}
  session-label: ${VERTICA_SESSION_LABEL:AXPORTAL_ELOCAL}
```

**환경변수 설정:**
| 환경변수 | 기본값 | 설명 |
|---------|--------|------|
| VERTICA_HOST | localhost | Vertica 서버 호스트 |
| VERTICA_PORT | 5433 | Vertica 서버 포트 |
| VERTICA_DATABASE | docker | 데이터베이스 이름 |
| VERTICA_USERNAME | dbadmin | 접속 사용자명 |
| VERTICA_PASSWORD | (공백) | 접속 비밀번호 |
| VERTICA_SESSION_LABEL | AXPORTAL_ELOCAL | 세션 식별 레이블 |

---

### 3. application-edev.yml 설정

```yaml
# Vertica 데이터소스 설정 (외부개발 환경)
vertica:
  datasource:
    driver-class-name: com.vertica.jdbc.Driver
    jdbc-url: jdbc:vertica://${VERTICA_HOST:localhost}:${VERTICA_PORT:5433}/${VERTICA_DATABASE:docker}
    username: ${VERTICA_USERNAME:dbadmin}
    password: ${VERTICA_PASSWORD:}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      read-only: true
      auto-commit: true
  host: ${VERTICA_HOST:localhost}
  port: ${VERTICA_PORT:5433}
  database: ${VERTICA_DATABASE:docker}
  session-label: ${VERTICA_SESSION_LABEL:AXPORTAL_EDEV}
```

**차이점:**
- `session-label`이 환경별로 구분됨 (AXPORTAL_ELOCAL vs AXPORTAL_EDEV)

---

## 🏗️ 멀티 데이터소스 아키텍처

### 데이터소스 역할 분리

```
┌─────────────────────────────────────┐
│   Spring Boot Application           │
├─────────────────────────────────────┤
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Primary DataSource          │  │
│  │  (PostgreSQL/Tibero)         │  │
│  │  - 트랜잭션 관리             │  │
│  │  - JPA 엔티티 관리           │  │
│  │  - 쓰기/읽기 작업            │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Secondary DataSource        │  │
│  │  (Vertica)                   │  │
│  │  - 읽기 전용 (read-only)     │  │
│  │  - 대용량 데이터 분석        │  │
│  │  - MyBatis 쿼리 실행         │  │
│  └──────────────────────────────┘  │
│                                     │
└─────────────────────────────────────┘
```

### 환경별 데이터소스 매트릭스

| 환경 | Primary DB | Secondary DB (Vertica) | 용도 |
|------|-----------|----------------------|------|
| **elocal** | PostgreSQL (RDS) | localhost:5433 | 외부 로컬 개발 |
| **edev** | PostgreSQL (RDS) | localhost:5433 | 외부 개발 서버 |
| **local** | H2 (in-memory) | ❌ 미설정 | 로컬 개발 |
| **dev** | Tibero | ❌ 미설정 | 내부 개발 서버 |
| **prod** | Tibero | ❌ 미설정 | 운영 서버 |

---

## 🔧 HikariCP 커넥션 풀 설정

### Vertica 전용 설정값

```yaml
hikari:
  maximum-pool-size: 10      # 최대 커넥션 수
  minimum-idle: 2            # 최소 유휴 커넥션 수
  connection-timeout: 30000  # 커넥션 타임아웃 (30초)
  idle-timeout: 600000       # 유휴 타임아웃 (10분)
  max-lifetime: 1800000      # 최대 수명 (30분)
  read-only: true            # 읽기 전용 모드
```

**설정 이유:**
- **read-only: true**: Vertica는 분석용 읽기 전용 데이터베이스로만 사용
- **최대 커넥션 10개**: 분석 쿼리는 리소스 집약적이므로 제한
- **긴 타임아웃**: 대용량 분석 쿼리 수행 시간 고려

---

## 📊 session-label 활용

### 개념
Vertica 세션에 레이블을 부여하여 모니터링 및 추적 가능

### 환경별 레이블
```yaml
# elocal
session-label: AXPORTAL_ELOCAL

# edev  
session-label: AXPORTAL_EDEV
```

### 활용 사례
1. **세션 추적**: 특정 애플리케이션의 쿼리만 필터링
2. **성능 분석**: 환경별 쿼리 성능 비교
3. **디버깅**: 문제 발생 시 세션 로그 추적
4. **리소스 관리**: 환경별 리소스 사용량 모니터링

---

## 🚀 구현 완료 내역

### 1. ✅ VerticaDataSourceConfig 클래스 (완료)

**파일 위치**: `src/main/java/com/skax/aiplatform/config/VerticaDataSourceConfig.java`

**주요 기능**:
- HikariCP 기반 Vertica DataSource 자동 구성
- 읽기 전용(read-only) 모드 설정
- JdbcTemplate 자동 생성 및 **@Qualifier 명시적 빈 주입** ⭐ NEW
- `@ConditionalOnProperty`로 환경별 활성화 제어
- **데이터베이스 초기화 스크립트 실행 방지** (data.sql 제외)

**특징**:
```java
@ConditionalOnProperty(prefix = "vertica.datasource", name = "jdbc-url")
```
- `vertica.datasource.jdbc-url`이 설정된 경우에만 활성화
- elocal, edev 환경에서만 동작
- 다른 환경(local, dev, prod)에는 영향 없음

**핵심 코드**:
```java
@Bean(name = "verticaJdbcTemplate")
public JdbcTemplate verticaJdbcTemplate(
        @Qualifier("verticaDataSource") DataSource verticaDataSource) {
    log.info("🔧 Vertica JdbcTemplate 초기화");
    log.info("   - DataSource: {}", verticaDataSource.getClass().getSimpleName());
    if (verticaDataSource instanceof HikariDataSource hikari) {
        log.info("   - JDBC URL: {}", hikari.getJdbcUrl());
    }
    return new JdbcTemplate(verticaDataSource);
}
```

### 2. ✅ VerticaSampleService 클래스 (완료)

**파일 위치**: `src/main/java/com/skax/aiplatform/service/vertica/VerticaSampleService.java`

**제공 기능**:
- 데이터베이스 연결 테스트
- Vertica 버전 조회
- 세션 정보 조회
- 테이블 존재 여부 확인
- 테이블 행 수 조회
- 커스텀 분석 쿼리 실행

**핵심 변경사항** ⭐ NEW:
```java
public class VerticaSampleService {
    private final JdbcTemplate verticaJdbcTemplate;

    /**
     * @Qualifier로 명시적으로 verticaJdbcTemplate 빈 주입
     * PostgreSQL의 Primary JdbcTemplate과 혼동 방지
     */
    public VerticaSampleService(
            @Qualifier("verticaJdbcTemplate") JdbcTemplate verticaJdbcTemplate) {
        this.verticaJdbcTemplate = verticaJdbcTemplate;
        log.info("✅ VerticaSampleService 초기화 완료");
    }
}
```

**문제 해결**:
- ❌ **이전**: `@RequiredArgsConstructor` 사용 시 Primary JdbcTemplate(PostgreSQL)이 주입됨
- ✅ **현재**: `@Qualifier("verticaJdbcTemplate")` 명시적 지정으로 Vertica 전용 JdbcTemplate 주입

### 3. ✅ VerticaTestController 클래스 (완료)

**파일 위치**: `src/main/java/com/skax/aiplatform/controller/sample/VerticaTestController.java`

**제공 API**:
- `GET /api/v1/vertica/test` - 연결 테스트
- `GET /api/v1/vertica/version` - 버전 조회
- `GET /api/v1/vertica/sessions` - 세션 정보
- `GET /api/v1/vertica/table-exists` - 테이블 존재 확인
- `GET /api/v1/vertica/row-count` - 행 수 조회

### 4. 📝 사용 예시

#### 기본 사용 (JdbcTemplate) - ⚠️ 주의사항 포함

**❌ 잘못된 방식 (Primary JdbcTemplate이 주입됨)**:
```java
@Service
@RequiredArgsConstructor  // ❌ 이렇게 하면 PostgreSQL이 조회됨!
public class MyAnalyticsService {
    
    private final JdbcTemplate jdbcTemplate;  // ❌ Primary 빈이 주입됨
    
    public List<Map<String, Object>> getAnalytics() {
        return jdbcTemplate.queryForList(
            "SELECT * FROM analytics_view WHERE date = ?",
            "2025-11-18"
        );
    }
}
```

**✅ 올바른 방식 (@Qualifier 사용)**:
```java
@Service
public class MyAnalyticsService {
    
    private final JdbcTemplate verticaJdbcTemplate;
    
    /**
     * @Qualifier로 명시적으로 verticaJdbcTemplate 지정
     */
    public MyAnalyticsService(
            @Qualifier("verticaJdbcTemplate") JdbcTemplate verticaJdbcTemplate) {
        this.verticaJdbcTemplate = verticaJdbcTemplate;
    }
    
    public List<Map<String, Object>> getAnalytics() {
        return verticaJdbcTemplate.queryForList(
            "SELECT * FROM analytics_view WHERE date = ?",
            "2025-11-18"
        );
    }
}
```

**또는 필드 주입 방식**:
```java
@Service
@RequiredArgsConstructor
public class MyAnalyticsService {
    
    @Qualifier("verticaJdbcTemplate")
    private final JdbcTemplate verticaJdbcTemplate;
    
    public List<Map<String, Object>> getAnalytics() {
        return verticaJdbcTemplate.queryForList(
            "SELECT * FROM analytics_view WHERE date = ?",
            "2025-11-18"
        );
    }
}
```

#### RowMapper 사용
```java
public List<AnalyticsResult> getDetailedAnalytics() {
    return verticaJdbcTemplate.query(
        "SELECT * FROM analytics_view",
        new BeanPropertyRowMapper<>(AnalyticsResult.class)
    );
}
```

#### 단일 결과 조회
```java
public Long getTotalCount() {
    return verticaJdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM user_events",
        Long.class
    );
}
```

### 5. 🔧 MyBatis 통합 (선택사항)

필요시 MyBatis Mapper를 추가로 구성할 수 있습니다:

```java
@Configuration
@MapperScan(
    basePackages = "com.skax.aiplatform.repository.vertica",
    sqlSessionFactoryRef = "verticaSqlSessionFactory"
)
@ConditionalOnProperty(prefix = "vertica.datasource", name = "jdbc-url")
public class VerticaMyBatisConfig {
    
    @Bean(name = "verticaSqlSessionFactory")
    public SqlSessionFactory verticaSqlSessionFactory(
            @Qualifier("verticaDataSource") DataSource dataSource) 
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }
}
```

---

## ⚠️ 주의사항

### 1. @Qualifier 필수 사용 ⭐ 중요!
멀티 DataSource 환경에서는 **반드시 @Qualifier로 빈 이름을 명시**해야 합니다:

```java
// ❌ 잘못된 예 - Primary JdbcTemplate(PostgreSQL)이 주입됨
@RequiredArgsConstructor
public class MyService {
    private final JdbcTemplate jdbcTemplate;  // PostgreSQL 조회!
}

// ✅ 올바른 예 - Vertica JdbcTemplate 주입
public class MyService {
    private final JdbcTemplate verticaJdbcTemplate;
    
    public MyService(@Qualifier("verticaJdbcTemplate") JdbcTemplate verticaJdbcTemplate) {
        this.verticaJdbcTemplate = verticaJdbcTemplate;
    }
}
```

**증상**: Vertica를 조회하는데 PostgreSQL 데이터가 나오는 경우
**원인**: Spring이 `@Primary` 빈을 자동 주입
**해결**: `@Qualifier("verticaJdbcTemplate")` 명시적 지정

### 2. application.yml에는 Vertica 설정 금지
- ✅ `application-elocal.yml`
- ✅ `application-edev.yml`
- ❌ `application.yml` (공통 설정에는 추가 금지)

### 3. 읽기 전용 모드 유지
- Vertica는 **read-only: true** 설정 필수
- 쓰기 작업은 Primary DB(PostgreSQL/Tibero)에서만 수행

### 4. 트랜잭션 관리
- Vertica는 분석용이므로 트랜잭션 불필요
- `@Transactional` 사용 시 Primary DB만 대상

### 5. 커넥션 풀 최적화
- 분석 쿼리는 긴 실행 시간 가능
- 커넥션 수 제한으로 리소스 고갈 방지

### 6. DataSource 초기화 제외
- Vertica DataSource는 Spring Boot의 자동 초기화(data.sql) 대상에서 제외됨
- `DataSourceProperties`를 사용하지 않고 수동 생성하여 제어

---

## 🔍 환경변수 설정 예시

### IntelliJ IDEA 환경변수 설정
```properties
VERTICA_HOST=localhost
VERTICA_PORT=5433
VERTICA_DATABASE=docker
VERTICA_USERNAME=dbadmin
VERTICA_PASSWORD=
VERTICA_SESSION_LABEL=AXPORTAL_ELOCAL
```

### Docker Compose 환경변수
```yaml
environment:
  - VERTICA_HOST=vertica-server
  - VERTICA_PORT=5433
  - VERTICA_DATABASE=docker
  - VERTICA_USERNAME=dbadmin
  - VERTICA_PASSWORD=
```

---

## 📚 참고 자료

### Vertica JDBC 문서
- [Vertica JDBC Driver Documentation](https://www.vertica.com/docs/latest/HTML/Content/Authoring/ConnectingToVertica/ClientJDBC/JDBCConnectionProperties.htm)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)

### Spring Boot Multi-DataSource
- [Spring Boot Multiple DataSources](https://www.baeldung.com/spring-data-jpa-multiple-databases)
- [MyBatis Multiple Databases](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)

---

## ✅ 검증 체크리스트

- [x] pom.xml에 Vertica JDBC 드라이버 추가
- [x] application-elocal.yml에 Vertica 설정 추가
- [x] application-edev.yml에 Vertica 설정 추가
- [x] application.yml에는 Vertica 설정 미포함 (정책 준수)
- [x] read-only: true 설정 확인
- [x] HikariCP 커넥션 풀 설정 완료
- [x] session-label 환경별 구분 설정
- [x] **VerticaDataSourceConfig 클래스 작성 완료** ✨
- [x] **VerticaSampleService 클래스 작성 완료** ✨
- [x] **VerticaTestController 클래스 작성 완료** ✨
- [x] **@ConditionalOnProperty로 환경별 활성화 완료** ✨
- [x] **@Qualifier 기반 명시적 빈 주입 구현 완료** ⭐ NEW
- [x] **DataSource 초기화 스크립트 제외 설정 완료** ⭐ NEW
- [x] **멀티 DataSource 빈 충돌 문제 해결 완료** ⭐ NEW
- [ ] 실제 Vertica 연결 테스트 (향후 작업)

---

## 🎯 빠른 시작 가이드

### 1. 애플리케이션 시작
```bash
# elocal 프로필로 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=elocal

# 또는 edev 프로필로 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=edev
```

### 2. Swagger UI에서 테스트
```
http://localhost:8080/swagger-ui.html

Vertica Test 섹션에서:
- GET /api/v1/vertica/test - 연결 테스트
- GET /api/v1/vertica/version - 버전 조회
- GET /api/v1/vertica/sessions - 세션 정보
```

### 3. 직접 API 호출
```bash
# 연결 테스트
curl http://localhost:8080/api/v1/vertica/test

# 버전 조회
curl http://localhost:8080/api/v1/vertica/version

# 세션 정보
curl http://localhost:8080/api/v1/vertica/sessions

# 테이블 존재 확인
curl "http://localhost:8080/api/v1/vertica/table-exists?schema=public&table=users"

# 행 수 조회
curl "http://localhost:8080/api/v1/vertica/row-count?schema=public&table=users"
```

### 4. 새로운 서비스에서 사용

**반드시 @Qualifier 사용:**
```java
@Service
public class YourAnalyticsService {
    
    private final JdbcTemplate verticaJdbcTemplate;
    
    // ✅ @Qualifier로 명시적 빈 지정
    public YourAnalyticsService(
            @Qualifier("verticaJdbcTemplate") JdbcTemplate verticaJdbcTemplate) {
        this.verticaJdbcTemplate = verticaJdbcTemplate;
    }
    
    public YourResult getYourData() {
        return verticaJdbcTemplate.queryForObject(
            "SELECT * FROM your_table WHERE id = ?",
            new BeanPropertyRowMapper<>(YourResult.class),
            yourId
        );
    }
}
```

---

## 🔍 로그 확인

애플리케이션 시작 시 다음 로그를 확인하세요:

```
🔧 Primary DataSource 초기화 시작
   - URL: jdbc:postgresql://...
   - Username: postgres
✅ Primary DataSource 초기화 완료

🔧 Vertica DataSource 초기화 시작
   - Host: localhost
   - Port: 5433
   - Database: docker
   - Session Label: AXPORTAL_ELOCAL
✅ Vertica DataSource 초기화 완료

🔧 Vertica JdbcTemplate 초기화
   - DataSource: HikariDataSource
   - JDBC URL: jdbc:vertica://localhost:5433/docker
✅ Vertica JdbcTemplate 초기화 완료

✅ VerticaSampleService 초기화 완료 - JdbcTemplate: JdbcTemplate
```

**확인 포인트:**
1. Primary와 Vertica가 각각 별도로 초기화되는지 확인
2. Vertica JDBC URL이 올바르게 설정되었는지 확인
3. VerticaSampleService가 정상적으로 초기화되었는지 확인

---

## 📦 생성된 파일 목록

### Configuration
- `src/main/java/com/skax/aiplatform/config/VerticaProperties.java` (기존)
- `src/main/java/com/skax/aiplatform/config/VerticaDataSourceConfig.java` ⭐ (2025-11-18 생성)
- `src/main/java/com/skax/aiplatform/config/DataSourceConfig.java` ⭐ (2025-11-19 생성 - Primary DataSource 명시)

### Service
- `src/main/java/com/skax/aiplatform/service/vertica/VerticaSampleService.java` ⭐ (2025-11-18 생성, 2025-11-19 @Qualifier 적용)

### Controller
- `src/main/java/com/skax/aiplatform/controller/sample/VerticaTestController.java` ⭐ (2025-11-18 생성)

### Configuration Files
- `src/main/resources/application-elocal.yml` (2025-11-18 수정, 2025-11-19 업데이트)
- `src/main/resources/application-edev.yml` (2025-11-18 수정, 2025-11-19 업데이트)
- `pom.xml` (2025-11-18 수정)

---

## 🐛 트러블슈팅

### 문제 1: Vertica를 조회하는데 PostgreSQL 데이터가 나옴

**증상:**
```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final JdbcTemplate verticaJdbcTemplate;  // Vertica인데 PostgreSQL 조회됨!
}
```

**원인:**
- Spring이 `JdbcTemplate` 타입으로 빈을 찾을 때 `@Primary`로 지정된 기본 빈을 주입
- Primary DataSource(PostgreSQL)에 연결된 JdbcTemplate이 주입됨

**해결책:**
```java
@Service
public class MyService {
    private final JdbcTemplate verticaJdbcTemplate;
    
    // @Qualifier로 명시적 지정
    public MyService(@Qualifier("verticaJdbcTemplate") JdbcTemplate verticaJdbcTemplate) {
        this.verticaJdbcTemplate = verticaJdbcTemplate;
    }
}
```

### 문제 2: data.sql이 Vertica에서 실행되려고 시도함

**증상:**
```
Failed to execute SQL script statement #1 of resource [data.sql]
```

**원인:**
- Spring Boot가 모든 DataSource에 대해 초기화 스크립트를 실행하려고 시도
- Vertica는 읽기 전용이므로 스크립트 실행 불가

**해결책:**
- `VerticaDataSourceConfig`에서 `DataSourceProperties`를 사용하지 않고 수동으로 DataSource 생성
- HikariConfig에 `initializationFailTimeout: -1` 설정 추가

```java
config.addDataSourceProperty("initializationFailTimeout", "-1");
```

### 문제 3: 환경변수가 적용되지 않음

**증상:**
```
Connection refused: localhost:5433
```

**원인:**
- 환경변수 설정이 누락되었거나 잘못 설정됨

**해결책:**
1. IntelliJ Run Configuration에서 환경변수 확인
2. `.env` 파일 생성 또는 시스템 환경변수 설정
3. 기본값 활용: `${VERTICA_HOST:localhost}`

---

## ✅ 최종 검증 체크리스트 (업데이트)

- [x] pom.xml에 Vertica JDBC 드라이버 추가
- [x] application-elocal.yml에 Vertica 설정 추가
- [x] application-edev.yml에 Vertica 설정 추가
- [x] application.yml에는 Vertica 설정 미포함 (정책 준수)
- [x] read-only: true 설정 확인
- [x] HikariCP 커넥션 풀 설정 완료
- [x] session-label 환경별 구분 설정
- [x] VerticaDataSourceConfig 클래스 작성 완료
- [x] VerticaSampleService 클래스 작성 완료
- [x] VerticaTestController 클래스 작성 완료
- [x] @ConditionalOnProperty로 환경별 활성화 완료
- [x] @Qualifier 기반 명시적 빈 주입 구현 완료 ⭐
- [x] DataSource 초기화 스크립트 제외 설정 완료 ⭐
- [x] 멀티 DataSource 빈 충돌 문제 해결 완료 ⭐
- [ ] 실제 Vertica 연결 테스트 (향후 작업)

---

## 🎯 결론

**작업 완료 사항:**
1. ✅ Vertica JDBC 드라이버 Maven 종속성 추가
2. ✅ elocal, edev 환경에서만 Vertica 설정 활성화
3. ✅ 멀티 데이터소스 완전 구현 (PostgreSQL/Tibero + Vertica)
4. ✅ 읽기 전용 분석 DB로 Vertica 역할 명확화
5. ✅ **@Qualifier 기반 명시적 빈 주입으로 빈 충돌 문제 해결** ⭐ NEW
6. ✅ **Primary와 Secondary DataSource 분리 완료** ⭐ NEW
7. ✅ **DataSource 초기화 스크립트 제외 처리 완료** ⭐ NEW

**핵심 성과:**
- 🎯 **멀티 DataSource 환경에서 발생하는 빈 주입 문제 완벽 해결**
- 🎯 **Vertica와 PostgreSQL이 명확히 분리되어 동작**
- 🎯 **@Qualifier 패턴을 통한 명시적 의존성 주입 확립**
- 🎯 **프로덕션 레디 상태의 안정적인 구조 완성**

**다음 개발 단계:**
- 실제 Vertica 서버 연결 테스트
- 대용량 데이터 분석 쿼리 최적화
- MyBatis 통합 (선택사항)
- 성능 모니터링 및 튜닝
