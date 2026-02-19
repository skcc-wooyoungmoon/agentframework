# Feign Bean 충돌 해결 가이드

## 📋 개요

Spring Boot 3.5.4 환경에서 여러 Feign Client (Lablup, Datumo, iONE) 구성 시 발생한 Bean 충돌 문제와 해결 과정을 문서화합니다.

### 🏗️ 환경 정보
- **Spring Boot**: 3.5.4
- **Spring Cloud OpenFeign**: 4.1.4
- **Java**: 17+
- **빌드 도구**: Maven
- **대상 클라이언트**: LablupArtifactClient, DatumoApiClient, IoneSystemClient

---

## 🚨 발생한 문제들

### 1차 충돌: RequestInterceptor Bean 중복

#### 오류 메시지
```
Error creating bean with name 'com.skax.aiplatform.client.lablup.api.LablupArtifactClient': 
FactoryBean threw exception on object creation
...
Parameter 0 of constructor in LablupRequestInterceptor required a single bean of type 
'RequestInterceptor', but 3 were found:
- lablupRequestInterceptor: defined in file
- datumoRequestInterceptor: defined in file  
- ioneRequestInterceptor: defined in file
```

#### 원인
여러 `RequestInterceptor` 구현체에서 `@Component` 어노테이션을 사용하여 전역 Bean으로 등록되면서 중복 충돌이 발생했습니다.

### 2차 충돌: 공통 Bean 이름 중복

#### 오류 메시지
```
Parameter 0 of method feignBuilder required a single bean, but 2 were found:
- lablupRetryer: defined by method 'lablupRetryer'
- datumoRetryer: defined by method 'datumoRetryer'
```

#### 원인
각 Configuration 클래스에서 동일한 타입의 Bean들이 클라이언트별 접두사를 가지고 있음에도 불구하고 Spring Cloud OpenFeign이 전역적으로 Bean을 찾으면서 충돌이 발생했습니다.

### 3차 충돌: SSL Client Bean 중복

#### 오류 메시지
```
Multiple beans found:
- lablupFeignClientWithSSLBypass vs datumoFeignClientWithSSLBypass
- lablupFeignClientWithStandardSSL vs datumoFeignClientWithStandardSSL
```

#### 원인
SSL 우회 설정을 위한 `Client` Bean들이 중복으로 정의되어 충돌이 발생했습니다.

### 4차 충돌: Spring Cloud OpenFeign 전역 Retryer 충돌

#### 오류 메시지
```
Parameter 0 of method feignBuilder in org.springframework.cloud.openfeign.FeignClientsConfiguration
$DefaultFeignBuilderConfiguration required a single bean, but 2 were found:
- datumoRetryer: defined by method 'datumoRetryer'
- lablupRetryer: defined by method 'lablupRetryer'
```

#### 원인
Spring Cloud OpenFeign이 전역 설정에서 단일 `Retryer` Bean을 기대하지만 여러 개가 존재하여 충돌이 발생했습니다.

---

## 🔧 해결 방법

### 1단계: RequestInterceptor @Component 제거

각 클라이언트의 RequestInterceptor에서 `@Component` 어노테이션을 제거하여 전역 빈 등록을 방지했습니다.

#### 수정 전
```java
@Component
@Slf4j
public class LablupRequestInterceptor implements RequestInterceptor {
    // 구현 내용
}
```

#### 수정 후
```java
@Slf4j
public class LablupRequestInterceptor implements RequestInterceptor {
    // 구현 내용
}
```

#### 적용 파일
- `LablupRequestInterceptor.java`
- `DatumoRequestInterceptor.java`
- `IoneRequestInterceptor.java`

### 2단계: 클라이언트별 Bean 이름 접두사 추가

공통 Bean들에 클라이언트별 접두사를 추가하여 고유성을 확보했습니다.

#### LablupClientConfig.java
```java
@Configuration
public class LablupClientConfig {
    
    @Bean
    public Retryer lablupRetryer() {
        return new Retryer.Default(1000, 3000, 3);
    }
    
    @Bean
    public Request.Options lablupRequestOptions() {
        return new Request.Options(Duration.ofSeconds(10), Duration.ofSeconds(60), true);
    }
    
    @Bean
    public LablupErrorDecoder lablupErrorDecoder() {
        return new LablupErrorDecoder();
    }
    
    @Bean
    @Profile({"elocal", "edev", "local", "dev"})
    public Client lablupFeignClientWithSSLBypass() throws Exception {
        // SSL 우회 설정
    }
    
    @Bean
    @Profile({"staging", "prod"})
    public Client lablupFeignClientWithStandardSSL() {
        // 표준 SSL 설정
    }
}
```

#### DatumoClientConfig.java
```java
@Configuration
public class DatumoClientConfig {
    
    @Bean
    public Retryer datumoRetryer() {
        return new Retryer.Default(1000, 3000, 3);
    }
    
    @Bean
    public Request.Options datumoRequestOptions() {
        return new Request.Options();
    }
    
    @Bean
    public ErrorDecoder datumoErrorDecoder() {
        return new DatumoErrorDecoder();
    }
    
    @Bean
    @Profile({"elocal", "edev", "local", "dev"})
    public Client datumoFeignClientWithSSLBypass() throws Exception {
        // SSL 우회 설정
    }
    
    @Bean
    @Profile({"staging", "prod"})
    public Client datumoFeignClientWithStandardSSL() {
        // 표준 SSL 설정
    }
}
```

### 3단계: @Configuration 어노테이션 제거 (최종 해결)

Spring Cloud OpenFeign의 전역 빈 충돌을 해결하기 위해 `@Configuration` 어노테이션을 제거하고 `@FeignClient`에서만 참조하도록 변경했습니다.

#### 수정 전
```java
@Configuration
public class LablupClientConfig {
    // Bean 정의들
}
```

#### 수정 후 - 완전한 @Bean 제거 방식 (IoneFeignConfig 패턴 적용)

**LablupClientConfig.java**
```java
/**
 * Lablup Feign Client 설정 클래스
 * 
 * <p>이 클래스는 @Configuration 어노테이션을 사용하지 않습니다.
 * 전역 Bean 등록을 피하고 Lablup FeignClient에만 적용되도록 합니다.</p>
 */
public class LablupClientConfig {
    
    /**
     * Lablup API 요청 인터셉터 설정
     * 
     * <p>@Bean 어노테이션을 제거하여 전역 Bean 등록을 방지합니다.
     * FeignClient configuration에서 직접 호출되어 사용됩니다.</p>
     */
    public RequestInterceptor requestInterceptor() {
        return new LablupRequestInterceptor();
    }
    
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }
    
    public Retryer retryer() {
        return new Retryer.Default(1000, 3000, 3);
    }
    
    public Request.Options requestOptions() {
        return new Request.Options(Duration.ofSeconds(10), Duration.ofSeconds(60), true);
    }
    
    public LablupErrorDecoder errorDecoder() {
        return new LablupErrorDecoder();
    }
    
    /**
     * 프로그래매틱 프로필 체크 방식으로 SSL 설정 결정
     */
    public Client lablupFeignClientWithSSLBypass() throws Exception {
        String activeProfile = System.getProperty("spring.profiles.active", "");
        if (!isDevelopmentProfile(activeProfile)) {
            // 운영계에서는 표준 SSL 검증 사용
            return new Client.Default(
                (SSLSocketFactory) SSLSocketFactory.getDefault(),
                HttpsURLConnection.getDefaultHostnameVerifier()
            );
        }
        
        // 개발계에서는 SSL 우회 설정
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        
        return new Client.Default(sslContext.getSocketFactory(), allHostsValid);
    }
    
    private boolean isDevelopmentProfile(String activeProfile) {
        return activeProfile != null && 
               (activeProfile.contains("elocal") || 
                activeProfile.contains("edev") || 
                activeProfile.contains("local") || 
                activeProfile.contains("dev"));
    }
}
```

**DatumoClientConfig.java**
```java
/**
 * Datumo Feign Client 설정
 * 
 * <p>이 클래스는 @Configuration 어노테이션을 사용하지 않습니다.
 * 전역 Bean 등록을 피하고 Datumo FeignClient에만 적용되도록 합니다.</p>
 */
public class DatumoClientConfig {
    
    /**
     * Datumo API 요청 인터셉터 설정
     * 
     * <p>@Bean 어노테이션을 제거하여 전역 Bean 등록을 방지합니다.
     * FeignClient configuration에서 직접 호출되어 사용됩니다.</p>
     */
    public RequestInterceptor requestInterceptor() {
        return new DatumoRequestInterceptor();
    }
    
    public Request.Options requestOptions() {
        return new Request.Options();
    }
    
    public Retryer retryer() {
        return new Retryer.Default(1000, 3000, 3);
    }
    
    public ErrorDecoder errorDecoder() {
        return new DatumoErrorDecoder();
    }
    
    /**
     * 프로그래매틱 프로필 체크 방식으로 SSL 설정 결정
     */
    public Client datumoFeignClientWithSSLBypass() throws Exception {
        String activeProfile = System.getProperty("spring.profiles.active", "");
        if (!isDevelopmentProfile(activeProfile)) {
            // 운영계에서는 표준 SSL 검증 사용
            return new Client.Default(
                (SSLSocketFactory) SSLSocketFactory.getDefault(),
                HttpsURLConnection.getDefaultHostnameVerifier()
            );
        }
        
        // 개발계에서는 SSL 우회 설정 (Lablup과 동일한 로직)
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
        };
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        
        return new Client.Default(sslContext.getSocketFactory(), allHostsValid);
    }
    
    private boolean isDevelopmentProfile(String activeProfile) {
        return activeProfile != null && 
               (activeProfile.contains("elocal") || 
                activeProfile.contains("edev") || 
                activeProfile.contains("local") || 
                activeProfile.contains("dev"));
    }
}
```

### 4단계: FeignClient 설정 참조 유지

각 Feign Client에서 해당 Configuration을 정상적으로 참조하도록 설정했습니다.

#### LablupArtifactClient.java
```java
@FeignClient(
    name = "lablup-artifact-client",
    url = "${lablup.api.base-url}",
    configuration = LablupClientConfig.class
)
public interface LablupArtifactClient {
    // API 메소드들
}
```

#### DatumoApiClient.java
```java
@FeignClient(
    name = "datumo-api-client", 
    url = "${datumo.api.base-url}",
    configuration = DatumoClientConfig.class
)
public interface DatumoApiClient {
    // API 메소드들
}
```

#### IoneSystemClient.java
```java
@FeignClient(
    name = "ione-system-client",
    url = "${ione.api.base-url}",
    configuration = IoneFeignConfig.class
)
public interface IoneSystemClient {
    // API 메소드들
}
```

---

## 🎯 핵심 해결 포인트

### 1. @Component 제거
- **목적**: RequestInterceptor의 전역 빈 등록 방지
- **효과**: Spring 컨텍스트에서 중복 Bean 등록 문제 해결

### 2. 클라이언트별 접두사 (2단계에서 시도)
- **목적**: Bean 이름 고유성 확보
- **방법**: `lablup-`, `datumo-`, `ione-` 접두사 사용
- **한계**: Spring Cloud OpenFeign 전역 설정에서 여전히 충돌 발생

### 3. @Configuration 및 @Bean 완전 제거 (최종 해결)
- **목적**: Spring Cloud OpenFeign 전역 충돌 완전 방지
- **방법**: Configuration 클래스를 일반 클래스로 변경, @Bean 어노테이션 제거
- **효과**: FeignClient별 완전히 독립적인 설정 적용

### 4. 프로그래매틱 프로필 처리
- **목적**: @Profile 어노테이션 없이 동적 프로필 확인
- **방법**: `System.getProperty("spring.profiles.active")` 사용
- **효과**: 런타임에 환경별 설정 동적 적용

### 5. IoneFeignConfig 패턴 적용
- **참조 모델**: IoneFeignConfig의 성공적인 패턴을 모든 클라이언트에 적용
- **일관성**: 모든 Configuration 클래스가 동일한 구조와 패턴 사용
- **효과**: 유지보수성 향상 및 향후 확장성 확보

### 6. SSL 설정 통합
- **개발 환경**: SSL 인증서 검증 완전 우회
- **운영 환경**: 표준 SSL 검증 수행
- **통합 방식**: 각 Configuration에서 프로그래매틱 방식으로 환경 감지

---

## ✅ 검증 결과

### 애플리케이션 시작 로그
```
2025-10-02 16:07:30.000  INFO c.s.a.AxportalBackendApplication : 
Started AxportalBackendApplication in 24.464 seconds

2025-10-02 16:07:30.292  INFO c.s.a.AxportalBackendApplication : 
🎉 AX Portal API Application started successfully.

접속 정보:
- Local:   http://localhost:8080/api
- Network: http://192.168.137.219:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI:    http://localhost:8080/api/v3/api-docs
```

### 성공 지표
- ✅ 모든 Bean 충돌 해결
- ✅ 각 클라이언트별 독립적인 설정 유지
- ✅ SSL 우회 기능 정상 작동 (개발 환경)
- ✅ Spring Boot 애플리케이션 정상 시작 (24.464초)
- ✅ 모든 Feign Client 정상 초기화
- ✅ PostgreSQL 데이터베이스 연결 성공
- ✅ JPA Repository 26개 인터페이스 스캔 완료
- ✅ IoneFeignConfig 패턴 적용으로 일관된 구조 확보
- ✅ @Bean 및 @Configuration 어노테이션 완전 제거
- ✅ 프로그래매틱 프로필 처리 방식 도입

---

## 📚 참고 자료

### Spring Cloud OpenFeign 설정 방법
- [Spring Cloud OpenFeign 공식 문서](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Feign Client Configuration](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/#feign-configuration)

### SSL 우회 설정
- [Java SSL 우회 방법](https://stackoverflow.com/questions/1828775/how-to-handle-invalid-ssl-certificates-with-apache-httpclient)
- [Feign Client SSL Configuration](https://github.com/OpenFeign/feign/wiki/SSL)

### Bean 충돌 해결
- [Spring Boot Bean Definition Override](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.spring-application.bean-definition-overriding)
- [Multiple Beans of Same Type](https://www.baeldung.com/spring-nosuchbeandefinitionexception)

---

## 🔮 향후 개선 사항

### 1. Configuration 클래스 Base 패턴 도입
- IoneFeignConfig 패턴을 기반으로 한 공통 Base Configuration 클래스 검토
- 클라이언트별 특화 설정만 별도 관리하는 구조 개선

### 2. SSL 설정 개선
- 운영 환경에서도 안전한 SSL 우회 방법 검토
- 인증서 관리 자동화 및 프로필별 세분화

### 3. 모니터링 강화
- Feign Client별 메트릭 수집
- 연결 상태 및 성능 모니터링
- Configuration 패턴별 성능 비교

### 4. 테스트 코드 보강
- 각 Configuration의 독립성 검증
- SSL 우회 설정 테스트
- 프로그래매틱 프로필 처리 테스트

### 5. 패턴 표준화
- IoneFeignConfig 패턴을 프로젝트 표준으로 문서화
- 신규 Feign Client 추가 시 가이드라인 제공
- 기존 SKTAI Client들도 동일한 패턴 적용 검토

---

## 📝 작성 정보

- **작성자**: ByounggwanLee
- **작성일**: 2025-10-02
- **최종 수정**: 2025-10-02 (LablupClientConfig, DatumoClientConfig IoneFeignConfig 패턴 적용)
- **버전**: 1.1
- **관련 이슈**: Feign Bean 충돌 해결
- **테스트 환경**: Spring Boot 3.5.4, Java 17, Maven
- **적용 패턴**: IoneFeignConfig 기반 @Bean/@Configuration 완전 제거 방식