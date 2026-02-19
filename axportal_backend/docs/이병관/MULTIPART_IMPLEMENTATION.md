# SKTAI Feign Client MultiPart 지원 구현 가이드

## 📋 개요

### 프로젝트 정보
- **프로젝트명**: AxportalBackend
- **작업 기간**: 2025-09-17 ~ 2025-09-18  
- **목표**: SKTAI Feign Client에서 MultiPart/form-data 파일 업로드 지원
- **주요 해결 과제**: "Missing boundary in multipart" 오류 및 완전한 MultiPart 지원 구현

### 작업 배경
기존 SKTAI Feign Client는 JSON 요청만 지원했으나, 파일 업로드 기능이 필요한 Custom Scripts API를 위해 MultiPart/form-data 지원이 필요했습니다.

---

## 🔧 1. Maven 의존성 추가

### 1.1 필요한 라이브러리

**파일**: `pom.xml`

```xml
<!-- Feign Form 지원 (MultiPart 처리용) -->
<dependency>
    <groupId>io.github.openfeign.form</groupId>
    <artifactId>feign-form</artifactId>
    <version>3.8.0</version>
</dependency>

<dependency>
    <groupId>io.github.openfeign.form</groupId>
    <artifactId>feign-form-spring</artifactId>
    <version>3.8.0</version>
</dependency>
```

### 1.2 의존성 설명

| 라이브러리 | 역할 | 주요 기능 |
|-----------|------|----------|
| `feign-form` | Feign MultiPart 지원 | FormEncoder, 기본 form-data 인코딩 |
| `feign-form-spring` | Spring 통합 | SpringFormEncoder, Spring MultipartFile 지원 |

---

## 🛠️ 2. Feign Configuration 구성

### 2.1 SktaiClientConfig 수정

**파일**: `src/main/java/com/skax/aiplatform/client/sktai/config/SktaiClientConfig.java`

#### SpringFormEncoder Bean 추가

```java
/**
 * MultiPart 지원을 위한 SpringFormEncoder 설정
 * 
 * <p>feign-form 라이브러리의 SpringFormEncoder만 사용하여 MultiPart/form-data 요청을 처리합니다.
 * SpringEncoder와 함께 사용하면 Jackson이 MultiPartFile을 직렬화하려다 실패하므로 순수하게 사용합니다.</p>
 * 
 * <h3>지원하는 Content-Type:</h3>
 * <ul>
 *   <li><strong>multipart/form-data</strong>: 파일 업로드용 (주목적)</li>
 *   <li><strong>application/x-www-form-urlencoded</strong>: 폼 데이터용</li>
 * </ul>
 * 
 * <p><strong>주의:</strong> JSON 요청은 다른 Feign Client를 통해 처리해야 합니다.</p>
 * 
 * @return 순수 SpringFormEncoder
 */
@Bean
public feign.codec.Encoder feignFormEncoder() {
    log.info("SpringFormEncoder 설정 - 순수 MultiPart/form-data 요청 지원 활성화");
    log.warn("주의: JSON 요청은 이 Encoder에서 처리되지 않습니다.");
    
    try {
        // 순수 SpringFormEncoder 사용 (SpringEncoder 없이)
        feign.form.spring.SpringFormEncoder formEncoder = new feign.form.spring.SpringFormEncoder();
        
        log.info("순수 SpringFormEncoder 생성 성공");
        return formEncoder;
        
    } catch (Exception e) {
        log.error("SpringFormEncoder 생성 실패: {}", e.getMessage(), e);
        throw new RuntimeException("SpringFormEncoder 초기화 실패", e);
    }
}
```

#### 기본 RequestInterceptor Bean 제거

```java
// ================================
// 기본 Feign 설정
// ================================

// 참고: RequestInterceptor는 SktaiRequestInterceptor 컴포넌트에서 처리됨

// 기존 코드 제거됨:
// @Bean
// public RequestInterceptor requestInterceptor() {
//     return requestTemplate -> {
//         requestTemplate.header("Content-Type", "application/json");
//         requestTemplate.header("Accept", "application/json");
//     };
// }
```

#### Request.Options 생성자 업데이트 (Deprecated 해결)

```java
/**
 * 요청 옵션 설정 (타임아웃 등)
 */
@Bean
public Request.Options requestOptions() {
    log.info("SKTAI 요청 타임아웃 설정 - 연결: 10000ms, 읽기: 60000ms");
    
    return new Request.Options(
        10000L,  // 연결 타임아웃 (밀리초)
        java.util.concurrent.TimeUnit.MILLISECONDS,  // 연결 타임아웃 단위
        60000L,  // 읽기 타임아웃 (밀리초)  
        java.util.concurrent.TimeUnit.MILLISECONDS,  // 읽기 타임아웃 단위
        true     // 리다이렉트 따르기
    );
}
```

### 2.2 설정 핵심 포인트

| 설정 항목 | 기존 방식 | 새로운 방식 | 이유 |
|----------|-----------|-------------|------|
| Encoder | SpringEncoder + Jackson | 순수 SpringFormEncoder | Jackson이 MultipartFile 직렬화 실패 방지 |
| RequestInterceptor | 통합 Bean | 별도 Component | MultiPart 요청 분리 처리 필요 |
| Request.Options | Deprecated 생성자 | 새로운 생성자 | Spring Boot 호환성 |

---

## 🔄 3. Request Interceptor 개선

### 3.1 SktaiRequestInterceptor 수정

**파일**: `src/main/java/com/skax/aiplatform/client/sktai/config/SktaiRequestInterceptor.java`

#### MultiPart 엔드포인트 패턴 정의

```java
// MultiPart 엔드포인트 패턴들
private static final String[] MULTIPART_ENDPOINTS = {
    "/knowledge/custom_scripts",  // Custom Scripts API
    "/test/loader",               // Loader 테스트
    "/test/splitter",            // Splitter 테스트
    "/upload"                    // 일반 업로드
};
```

#### MultiPart 감지 로직

```java
/**
 * MultiPart 요청인지 확인
 * 
 * @param url 요청 URL
 * @return MultiPart 요청 여부
 */
private boolean isMultipartRequest(String url) {
    if (url == null) {
        return false;
    }
    
    for (String endpoint : MULTIPART_ENDPOINTS) {
        if (url.contains(endpoint)) {
            log.debug("🔴 MultiPart 엔드포인트 매칭: {} -> {}", endpoint, url);
            return true;
        }
    }
    
    return false;
}
```

#### 메인 처리 로직 수정

```java
@Override
public void apply(RequestTemplate template) {
    try {
        log.debug("🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() - Method: {}, URL: {}", 
                  template.method(), template.url());
        
        // 공통 헤더 설정
        setCommonHeaders(template);
        
        // MultiPart 요청인지 확인
        boolean isMultipart = isMultipartRequest(template.url());
        boolean isOAuth2LoginRequest = false;
        
        if (!isMultipart) {
            // MultiPart가 아닌 경우에만 Content-Type 설정 및 OAuth2 요청 확인
            isOAuth2LoginRequest = setContentTypeHeader(template);
        } else {
            // MultiPart 요청의 경우 Content-Type을 설정하지 않음
            log.debug("🔴 MultiPart 요청 - Content-Type 설정 건너뛰기");
        }
        
        // OAuth2 로그인 요청이 아닌 경우에만 토큰 적용
        if (!isOAuth2LoginRequest) {
            setAuthorizationHeader(template);
        }

        log.debug("🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() 완료 - 최종 헤더들: {}", 
                  template.headers());
        
    } catch (Exception e) {
        log.error("SKTAI API 요청 인터셉터 적용 중 오류 발생: {}", e.getMessage(), e);
    }
}
```

#### Content-Type 설정 로직 개선

```java
/**
 * Content-Type 헤더 설정
 * 
 * @param template 요청 템플릿
 * @return OAuth2 로그인 요청 여부
 */
private boolean setContentTypeHeader(RequestTemplate template) {
    boolean isPostWithBody = "POST".equals(template.method());
    boolean isOAuth2LoginRequest = false;
    
    // 디버깅을 위한 로그
    log.debug("🔍 setContentTypeHeader - Method: {}, URL: {}, isPostWithBody: {}", 
              template.method(), template.url(), isPostWithBody);
    
    if (isPostWithBody) {
        String url = template.url();
        
        if (url != null && url.contains(OAUTH_LOGIN_ENDPOINT)) {
            // OAuth2 로그인 요청의 경우 form-urlencoded 사용
            template.header("Content-Type", CONTENT_TYPE_FORM);
            isOAuth2LoginRequest = true;
            log.debug("✅ OAuth2 로그인 요청 - Content-Type: form-urlencoded");
        } else if (isMultipartRequest(url)) {
            // ⚠️ MultiPart 요청의 경우 Content-Type을 설정하지 않음
            // SpringFormEncoder가 boundary와 함께 자동 설정하도록 함
            log.debug("✅ 🔴 MultiPart 요청 감지 - Content-Type 설정 건너뛰기 (SpringFormEncoder가 자동 설정)");
            // Content-Type을 설정하지 않음!
        } else {
            // 일반 요청의 경우 JSON 사용
            template.header("Content-Type", CONTENT_TYPE_JSON);
            log.debug("✅ 일반 API 요청 - Content-Type: JSON");
        }
    } else if (!template.headers().containsKey("Content-Type")) {
        // Content-Type이 설정되지 않은 경우 기본값으로 JSON 설정
        template.header("Content-Type", CONTENT_TYPE_JSON);
        log.debug("✅ 기본 Content-Type: JSON");
    }
    
    return isOAuth2LoginRequest;
}
```

### 3.2 핵심 개선사항

| 개선 영역 | 기존 방식 | 새로운 방식 | 효과 |
|----------|-----------|-------------|------|
| Content-Type 처리 | 모든 요청에 JSON 강제 | MultiPart 요청 분리 | Boundary 생성 가능 |
| 요청 분류 | 단일 처리 | 요청 타입별 분리 | 각 요청 타입 최적화 |
| 로깅 | 기본 로깅 | 상세 디버그 로깅 | 문제 추적 용이 |
| 에러 처리 | 기본 처리 | Try-catch 강화 | 안정성 향상 |

---

## 📡 4. Feign Client 인터페이스 구현

### 4.1 SktaiCustomScriptsClient 수정

**파일**: `src/main/java/com/skax/aiplatform/client/sktai/knowledge/SktaiCustomScriptsClient.java`

#### Import 문 추가

```java
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
```

#### MultiPart 업로드 메서드 정의

```java
/**
 * Custom Script 등록
 * 
 * <p>새로운 사용자 정의 스크립트를 등록합니다.
 * Python 스크립트 파일과 메타데이터를 함께 업로드하여 Knowledge 시스템에서 사용할 수 있도록 합니다.</p>
 * 
 * <h3>지원하는 스크립트 타입:</h3>
 * <ul>
 *   <li><strong>loader</strong>: 문서 파일을 읽어 텍스트로 변환하는 로더 스크립트</li>
 *   <li><strong>splitter</strong>: 문서를 청크(chunk)로 분할하는 스플리터 스크립트</li>
 * </ul>
 * 
 * <h3>파일 요구사항:</h3>
 * <ul>
 *   <li><strong>파일 형식</strong>: Python (.py) 파일</li>
 *   <li><strong>파일 크기</strong>: 최대 10MB (서버 설정에 따라 변경 가능)</li>
 *   <li><strong>인코딩</strong>: UTF-8 권장</li>
 * </ul>
 * 
 * @param name 스크립트 이름 (필수, 프로젝트 내 고유해야 함)
 * @param description 스크립트 설명 (필수, 스크립트의 목적과 동작 방식 설명)
 * @param script_type 스크립트 타입 (필수, "loader" 또는 "splitter")
 * @param script 스크립트 파일 (필수, Python 파일)
 * @param policy 스크립트 정책 (선택, JSON 형태의 접근 권한 설정)
 * @return 등록된 Custom Script 정보 (ID, 생성 시간 등 포함)
 */
@Operation(
    summary = "Custom Script 등록",
    description = "새로운 사용자 정의 스크립트를 등록합니다. Python 파일과 메타데이터를 업로드합니다."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Custom Script 등록 성공",
        content = @Content(schema = @Schema(implementation = Object.class))
    ),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 형식, 크기 등)"),
    @ApiResponse(responseCode = "401", description = "인증 실패"),
    @ApiResponse(responseCode = "403", description = "권한 없음"),
    @ApiResponse(responseCode = "409", description = "중복된 스크립트 이름"),
    @ApiResponse(responseCode = "422", description = "입력값 검증 실패"),
    @ApiResponse(responseCode = "500", description = "서버 오류")
})
@PostMapping(value = "/api/v1/knowledge/custom_scripts", 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
Object createCustomScript(
    @Parameter(description = "스크립트 이름 (프로젝트 내 고유)", required = true, example = "my_custom_loader")
    @RequestPart("name") String name,
    
    @Parameter(description = "스크립트 설명 (목적과 동작 방식)", required = true, example = "PDF 문서를 처리하는 커스텀 로더")  
    @RequestPart("description") String description,
    
    @Parameter(description = "스크립트 타입", required = true, example = "loader", 
               schema = @Schema(allowableValues = {"loader", "splitter"}))
    @RequestPart("script_type") String script_type,
    
    @Parameter(description = "Python 스크립트 파일", required = true)
    @RequestPart("script") MultipartFile script,

    @Parameter(description = "스크립트 접근 정책 (JSON 형태)", required = false, 
               example = "{\"access_level\": \"private\", \"allowed_users\": []}")
    @RequestPart(value = "policy", required = false) String policy
);
```

### 4.2 핵심 구현 포인트

| 항목 | 설정 값 | 목적 |
|------|---------|------|
| `consumes` | `MediaType.MULTIPART_FORM_DATA_VALUE` | Feign이 MultiPart 요청임을 명시적으로 인식 |
| `@RequestPart` | 각 파라미터에 적용 | SpringFormEncoder가 올바른 part로 인코딩 |
| `MultipartFile` | script 파라미터 타입 | Spring의 표준 파일 업로드 인터페이스 |
| `required = false` | policy 파라미터 | 선택적 파라미터 지원 |

---

## 📊 5. 로깅 및 모니터링 설정

### 5.1 로깅 설정 강화

**파일**: `src/main/resources/application-elocal.yml`

```yaml
# Logging Configuration
logging:
  group:
    rbac: com.skax.aiplatform
  level:
    rbac: DEBUG
    feign: DEBUG                                    # 추가
    feign.Logger: DEBUG
    com.skax.aiplatform.client.sktai: DEBUG        # 추가
    org:
      hibernate:
        SQL: DEBUG
        orm:
          jdbc:
            bind: TRACE
    com.skax.aiplatform.client.sktai.com.skax.aiplatform.comm.feign.GenericFeignClient: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/aiplatform-api.log
```

### 5.2 로깅 레벨 설명

| 로거 | 레벨 | 목적 |
|------|------|------|
| `feign` | DEBUG | Feign 클라이언트 전체 동작 로깅 |
| `feign.Logger` | DEBUG | 요청/응답 상세 정보 |
| `com.skax.aiplatform.client.sktai` | DEBUG | SKTAI 클라이언트 상세 로깅 |

### 5.3 로그 출력 예시

```log
2025-09-17 16:55:06.968 DEBUG c.s.a.c.s.k.s.SktaiCustomScriptsService : 🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() - Method: POST, URL: /api/v1/knowledge/custom_scripts
2025-09-17 16:55:06.968 DEBUG c.s.a.c.s.k.s.SktaiCustomScriptsService : 🔴 MultiPart 엔드포인트 매칭: /knowledge/custom_scripts -> /api/v1/knowledge/custom_scripts
2025-09-17 16:55:06.968 DEBUG c.s.a.c.s.k.s.SktaiCustomScriptsService : 🔴 MultiPart 요청 - Content-Type 설정 건너뛰기
2025-09-17 16:55:06.968 DEBUG c.s.a.c.s.k.s.SktaiCustomScriptsService : 🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() 완료 - 최종 헤더들: {Authorization=[Bearer eyJ...], User-Agent=[AXPORTAL-Backend/1.0], Accept=[application/json]}
```

---

## 🐛 6. 발생했던 문제점과 해결 과정

### 6.1 문제 1: Missing boundary in multipart

#### 증상
```
feign.FeignException$BadRequest: [400] during [POST] to [https://aip-stg.sktai.io/api/v1/knowledge/custom_scripts] 
[SktaiCustomScriptsClient#createCustomScript(String,String,String,MultipartFile,String)]: 
[Missing boundary in multipart]
```

#### 원인 분석
- `SktaiRequestInterceptor`에서 모든 POST 요청에 `Content-Type: application/json` 강제 설정
- SpringFormEncoder가 MultiPart boundary를 생성하지 못함
- HTTP 표준에 따르면 multipart/form-data는 boundary 파라미터가 필수

#### 해결책
```java
// Before: 모든 POST 요청에 JSON Content-Type 강제
template.header("Content-Type", "application/json");

// After: MultiPart 요청 감지 및 Content-Type 설정 건너뛰기  
if (isMultipartRequest(url)) {
    log.debug("MultiPart 요청 - Content-Type 설정 건너뛰기");
    // Content-Type을 설정하지 않아서 SpringFormEncoder가 처리하도록 함
} else {
    template.header("Content-Type", "application/json");
}
```

### 6.2 문제 2: NullPointerException (messageConverters is null)

#### 증상
```
feign.codec.EncodeException: Cannot invoke "org.springframework.beans.factory.ObjectFactory.getObject()" 
because "this.messageConverters" is null
```

#### 원인 분석
- SpringFormEncoder와 SpringEncoder를 함께 사용 시 messageConverters 미주입
- Spring Boot의 자동 구성이 ObjectFactory를 주입하지 않음
- feign-form-spring 버전 호환성 문제

#### 해결책
```java
// Before: SpringEncoder와 조합 사용
@Bean
public feign.codec.Encoder feignFormEncoder(
        ObjectFactory<HttpMessageConverters> messageConverters) {
    SpringEncoder springEncoder = new SpringEncoder(messageConverters);
    return new SpringFormEncoder(springEncoder);
}

// After: 순수 SpringFormEncoder 사용
@Bean
public feign.codec.Encoder feignFormEncoder() {
    return new feign.form.spring.SpringFormEncoder();
}
```

### 6.3 문제 3: Jackson 직렬화 오류

#### 증상
```
feign.codec.EncodeException: Error converting request body
...
Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: 
No serializer found for class sun.nio.ch.ChannelInputStream and no properties discovered to create BeanSerializer
(to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) 
(through reference chain: java.util.LinkedHashMap["script"]->...StandardMultipartFile["inputStream"])
```

#### 원인 분석
- SpringEncoder가 MultipartFile의 InputStream을 JSON으로 직렬화 시도
- Jackson이 ChannelInputStream을 직렬화할 수 없음
- MultiPart 데이터와 JSON 직렬화의 충돌

#### 해결책
- SpringEncoder 완전 제거
- SpringFormEncoder만 사용하여 Jackson 직렬화 우회

### 6.4 문제 4: LinkedHashMap 인코딩 오류

#### 증상
```
feign.codec.EncodeException: class java.util.LinkedHashMap is not a type supported by this encoder.
```

#### 원인 분석
- Feign이 @RequestPart 파라미터들을 LinkedHashMap으로 변환
- 순수 SpringFormEncoder가 LinkedHashMap 타입을 처리하지 못함
- Feign의 파라미터 변환 로직과 SpringFormEncoder의 불일치

#### 해결책
```java
// Feign Client에 consumes 속성 명시적 추가
@PostMapping(value = "/api/v1/knowledge/custom_scripts", 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
```

### 6.5 문제 해결 타임라인

| 순서 | 문제 | 해결 시간 | 주요 해결책 |
|------|------|----------|-------------|
| 1 | Missing boundary | 30분 | MultiPart 감지 로직 추가 |
| 2 | messageConverters null | 45분 | SpringEncoder 제거 |
| 3 | Jackson 직렬화 | 20분 | 순수 SpringFormEncoder 사용 |
| 4 | LinkedHashMap 오류 | 15분 | consumes 속성 명시 |

---

## 📐 7. 최종 아키텍처 구조

### 7.1 데이터 플로우

```
┌─────────────────────────────────────────────────────────┐
│                   Client Request                        │
│              (multipart/form-data)                      │
│  ┌─────────────┬─────────────┬─────────────┬─────────┐  │
│  │    name     │ description │ script_type │ script  │  │
│  │   (String)  │  (String)   │  (String)   │ (File)  │  │
│  └─────────────┴─────────────┴─────────────┴─────────┘  │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              Spring MVC Controller                      │
│  • @RequestPart 파라미터 바인딩                         │
│  • MultipartFile 자동 변환                              │
│  • 유효성 검증 (@Valid)                                 │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                Service Layer                            │
│  • 비즈니스 로직 처리                                   │
│  • 파라미터 검증 및 로깅                                │
│  • Feign Client 호출                                   │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              SktaiRequestInterceptor                    │
│  ┌─────────────────────────────────────────────────────┐ │
│  │  1. isMultipartRequest(url) 체크                   │ │
│  │     • /knowledge/custom_scripts 매칭               │ │
│  │  2. MultiPart 요청인 경우:                         │ │
│  │     • Content-Type 설정 건너뛰기                   │ │
│  │     • SpringFormEncoder가 처리하도록 위임           │ │
│  │  3. 일반 요청인 경우:                               │ │
│  │     • Content-Type: application/json 설정         │ │
│  │  4. Authorization 헤더 추가                        │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                SpringFormEncoder                        │
│  ┌─────────────────────────────────────────────────────┐ │
│  │  1. Content-Type 자동 생성:                        │ │
│  │     multipart/form-data; boundary=----WebKit...    │ │
│  │  2. 각 @RequestPart를 MultiPart 파트로 변환:       │ │
│  │     --boundary                                      │ │
│  │     Content-Disposition: form-data; name="name"    │ │
│  │     이름                                            │ │
│  │     --boundary                                      │ │
│  │     Content-Disposition: form-data; name="script"; │ │
│  │                         filename="script.py"       │ │
│  │     Content-Type: application/octet-stream         │ │
│  │     [파일 바이너리 데이터]                          │ │
│  │  3. HTTP Body에 MultiPart 데이터 작성              │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              SktaiCustomScriptsClient                   │
│  • @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)  │
│  • @RequestPart 파라미터 정의                           │
│  • MultipartFile 타입 지원                             │
│  • OpenAPI 3 문서화                                    │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                 HTTP Request                            │
│  POST /api/v1/knowledge/custom_scripts                 │
│  Content-Type: multipart/form-data; boundary=...       │
│  Authorization: Bearer eyJ...                          │
│                                                         │
│  --boundary                                             │
│  Content-Disposition: form-data; name="name"           │
│  이름                                                   │
│  --boundary                                             │
│  Content-Disposition: form-data; name="script";        │
│                      filename="script.py"              │
│  Content-Type: application/octet-stream                │
│  [바이너리 데이터]                                      │
│  --boundary--                                           │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                 SKTAI API Server                        │
│              (External Service)                         │
└─────────────────────────────────────────────────────────┘
```

### 7.2 컴포넌트 역할 분담

| 컴포넌트 | 주요 역할 | MultiPart 관련 처리 |
|----------|-----------|---------------------|
| **Controller** | 요청 수신, 파라미터 바인딩 | @RequestPart로 MultipartFile 수신 |
| **Service** | 비즈니스 로직, 검증 | 파일 메타데이터 검증 및 로깅 |
| **RequestInterceptor** | 공통 헤더 설정, 인증 | MultiPart 감지 시 Content-Type 건너뛰기 |
| **SpringFormEncoder** | 요청 인코딩 | MultiPart boundary 생성, 파트별 인코딩 |
| **Feign Client** | 외부 API 호출 | consumes 속성으로 MultiPart 명시 |

### 7.3 설정 의존성 관계

```
SktaiClientConfig
├── feignFormEncoder() Bean
│   └── SpringFormEncoder (순수)
├── requestOptions() Bean  
│   └── 타임아웃 설정
└── feignLoggerLevel() Bean
    └── 로깅 레벨 설정

SktaiRequestInterceptor (Component)
├── isMultipartRequest() 
├── setCommonHeaders()
├── setContentTypeHeader()
└── setAuthorizationHeader()

SktaiCustomScriptsClient
├── @FeignClient(configuration = SktaiClientConfig.class)
├── @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
└── @RequestPart parameters
```

---

## ✅ 8. 테스트 및 검증

### 8.1 테스트 시나리오

#### 시나리오 1: 성공적인 파일 업로드
```
입력:
- name: "test_loader"
- description: "테스트용 로더 스크립트"  
- script_type: "loader"
- script: test_loader.py (1.2KB Python 파일)
- policy: null

예상 결과:
- HTTP 201 Created
- 응답: {"id": "uuid-xxx", "name": "test_loader", ...}
- 로그: MultiPart 감지, Content-Type 건너뛰기 확인
```

#### 시나리오 2: 필수 파라미터 누락
```
입력:
- name: null (누락)
- description: "설명"
- script_type: "loader" 
- script: test.py
- policy: null

예상 결과:
- HTTP 400 Bad Request
- 에러 메시지: name 파라미터 필수
```

#### 시나리오 3: 잘못된 파일 형식
```
입력:
- name: "test"
- description: "설명"
- script_type: "loader"
- script: test.txt (Python 파일 아님)
- policy: null

예상 결과:  
- HTTP 422 Unprocessable Entity
- 에러 메시지: Python 파일만 허용
```

### 8.2 로그 검증 포인트

#### 성공 케이스 로그 패턴
```log
[INFO ] 🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() - Method: POST, URL: /api/v1/knowledge/custom_scripts
[DEBUG] 🔴 MultiPart 엔드포인트 매칭: /knowledge/custom_scripts -> /api/v1/knowledge/custom_scripts  
[DEBUG] 🔴 MultiPart 요청 - Content-Type 설정 건너뛰기
[DEBUG] 🔴 [MULTIPART DEBUG] SktaiRequestInterceptor.apply() 완료 - 최종 헤더들: {Authorization=[Bearer ...], User-Agent=[AXPORTAL-Backend/1.0], Accept=[application/json]}
[INFO ] SpringFormEncoder 설정 - 순수 MultiPart/form-data 요청 지원 활성화
```

#### 실패 케이스 로그 패턴  
```log
[ERROR] feign.codec.EncodeException: [구체적 오류 메시지]
[ERROR] API 호출 중 오류 발생 [client=$Proxy301, method=createCustomScript, duration=XXXms, exceptionType=EncodeException, errorCode=UNKNOWN, exceptionMessage=...]
```

### 8.3 성능 측정 기준

| 메트릭 | 기준값 | 측정 방법 |
|--------|--------|----------|
| 파일 업로드 속도 | 1MB/초 이상 | 1MB 파일 업로드 시간 측정 |
| 메모리 사용량 | 파일 크기의 2배 이하 | JVM 힙 메모리 모니터링 |
| 응답 시간 | 10초 이하 (10MB 파일 기준) | End-to-End 시간 측정 |
| 동시 업로드 | 10개 이상 | 동시 요청 처리 능력 |

---

## 🎯 9. 운영 고려사항

### 9.1 보안 설정

#### 파일 업로드 보안
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB      # 단일 파일 최대 크기
      max-request-size: 15MB   # 전체 요청 최대 크기
      file-size-threshold: 2KB # 메모리 임계값
```

#### 허용 파일 타입 검증
```java
// Service Layer에서 검증
private void validateScriptFile(MultipartFile file) {
    String filename = file.getOriginalFilename();
    if (!filename.endsWith(".py")) {
        throw new ValidationException("Python 파일만 업로드 가능합니다");
    }
    
    if (file.getSize() > MAX_FILE_SIZE) {
        throw new ValidationException("파일 크기가 너무 큽니다");
    }
}
```

### 9.2 에러 처리 및 복구

#### Circuit Breaker 적용
```java
@Component
public class SktaiCircuitBreaker {
    
    @CircuitBreaker(name = "sktai-api", fallbackMethod = "fallbackUpload")
    public Object uploadWithCircuitBreaker(String name, String description, 
                                          String scriptType, MultipartFile script, String policy) {
        return sktaiCustomScriptsService.createCustomScript(name, description, scriptType, script, policy);
    }
    
    public Object fallbackUpload(Exception ex) {
        log.error("SKTAI API Circuit Breaker 동작: {}", ex.getMessage());
        throw new ServiceUnavailableException("외부 서비스 일시 중단");
    }
}
```

#### Retry 정책
```java
// SktaiClientConfig에 추가
@Bean
public Retryer retryer() {
    return new Retryer.Default(
        1000,   // 초기 지연 (1초)
        3000,   // 최대 지연 (3초)  
        3       // 최대 재시도 (3회)
    );
}
```

### 9.3 모니터링 및 알림

#### 메트릭 수집
```java
@Component
public class MultiPartMetrics {
    
    private final MeterRegistry meterRegistry;
    
    @EventListener
    public void onFileUpload(FileUploadEvent event) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        // 업로드 시간 측정
        sample.stop(Timer.builder("file.upload.duration")
            .tag("file.type", event.getFileType())
            .tag("file.size", String.valueOf(event.getFileSize()))
            .register(meterRegistry));
        
        // 업로드 카운터 증가
        Counter.builder("file.upload.count")
            .tag("status", event.getStatus())
            .register(meterRegistry)
            .increment();
    }
}
```

#### 알림 설정  
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: axportal-backend
      service: sktai-client
```

---

## 🔄 10. 향후 개선 계획

### 10.1 단기 개선 사항 (1-2주)

#### 테스트 코드 작성
```java
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class SktaiCustomScriptsClientIntegrationTest {
    
    @Test
    @Order(1)
    @DisplayName("MultiPart 파일 업로드 성공 테스트")
    void uploadCustomScript_Success() {
        // Given
        MockMultipartFile scriptFile = new MockMultipartFile(
            "script", "test_loader.py", "text/python", 
            "def load_document(file_path): return 'test'".getBytes());
        
        // When & Then
        assertDoesNotThrow(() -> {
            Object result = sktaiCustomScriptsService.createCustomScript(
                "test_loader", "테스트 로더", "loader", scriptFile, null);
            assertThat(result).isNotNull();
        });
    }
    
    @Test
    @Order(2) 
    @DisplayName("파일 크기 제한 테스트")
    void uploadCustomScript_FileSizeLimit() {
        // Given: 11MB 파일 생성
        byte[] largeFile = new byte[11 * 1024 * 1024];
        MockMultipartFile scriptFile = new MockMultipartFile(
            "script", "large_script.py", "text/python", largeFile);
        
        // When & Then
        assertThrows(ValidationException.class, () -> {
            sktaiCustomScriptsService.createCustomScript(
                "large_script", "큰 파일", "loader", scriptFile, null);
        });
    }
}
```

#### WireMock을 활용한 외부 API 테스트
```java
@ExtendWith(MockitoExtension.class)
class SktaiCustomScriptsClientMockTest {
    
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8089))
        .build();
    
    @Test
    @DisplayName("SKTAI API MultiPart 요청 목킹 테스트")
    void mockSktaiApiMultiPartRequest() {
        // Given
        wireMock.stubFor(post(urlEqualTo("/api/v1/knowledge/custom_scripts"))
            .withHeader("Content-Type", containing("multipart/form-data"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"test-uuid\",\"name\":\"test_script\"}")));
        
        // When & Then - 실제 MultiPart 요청 테스트
    }
}
```

### 10.2 중기 개선 사항 (1-2개월)

#### 비동기 파일 업로드
```java
@Service
public class AsyncFileUploadService {
    
    @Async("fileUploadExecutor")
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public CompletableFuture<UploadResult> uploadFileAsync(FileUploadRequest request) {
        try {
            Object result = sktaiCustomScriptsService.createCustomScript(
                request.getName(), request.getDescription(), 
                request.getScriptType(), request.getScript(), request.getPolicy());
                
            return CompletableFuture.completedFuture(
                UploadResult.success(result));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                UploadResult.failure(e.getMessage()));
        }
    }
}
```

#### 업로드 진행률 추적
```java
@Component
public class FileUploadProgressTracker {
    
    private final Map<String, UploadProgress> progressMap = new ConcurrentHashMap<>();
    
    public void startTracking(String uploadId, long totalSize) {
        progressMap.put(uploadId, new UploadProgress(totalSize));
    }
    
    public void updateProgress(String uploadId, long uploadedBytes) {
        UploadProgress progress = progressMap.get(uploadId);
        if (progress != null) {
            progress.setUploadedBytes(uploadedBytes);
            // WebSocket으로 클라이언트에 진행률 전송
            messagingTemplate.convertAndSend("/topic/upload/" + uploadId, progress);
        }
    }
}
```

### 10.3 장기 개선 사항 (3-6개월)

#### WebClient 기반 Reactive 업로드
```java
@Component
public class ReactiveFileUploadClient {
    
    private final WebClient webClient;
    
    public Mono<ResponseEntity<Object>> uploadFileReactive(
            String name, String description, String scriptType, 
            DataBuffer fileBuffer, String policy) {
        
        MultiValueMap<String, HttpEntity<?>> parts = new LinkedMultiValueMap<>();
        parts.add("name", new HttpEntity<>(name));
        parts.add("description", new HttpEntity<>(description));
        parts.add("script_type", new HttpEntity<>(scriptType));
        
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        parts.add("script", new HttpEntity<>(fileBuffer, fileHeaders));
        
        return webClient.post()
            .uri("/api/v1/knowledge/custom_scripts")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(parts))
            .retrieve()
            .toEntity(Object.class);
    }
}
```

---

## 📚 11. 참고 자료 및 문서

### 11.1 기술 문서
- [Spring Cloud OpenFeign 공식 문서](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [feign-form GitHub Repository](https://github.com/OpenFeign/feign-form)
- [HTTP MultiPart RFC 7578](https://tools.ietf.org/html/rfc7578)
- [Spring Boot File Upload 가이드](https://spring.io/guides/gs/uploading-files/)

### 11.2 관련 코드 저장소
```bash
# 프로젝트 클론
git clone https://github.com/SK-AX-GenAIPF/axportal_backend.git
cd axportal_backend

# 브랜치 확인
git checkout dev-data-hgh

# MultiPart 관련 파일 확인
find . -name "*.java" -path "*/sktai/*" | grep -E "(Config|Client|Interceptor)"
```

### 11.3 주요 설정 파일 위치
```
src/main/java/com/skax/aiplatform/
├── client/sktai/
│   ├── config/
│   │   ├── SktaiClientConfig.java          # Feign 설정
│   │   └── SktaiRequestInterceptor.java    # 요청 인터셉터
│   └── knowledge/
│       └── SktaiCustomScriptsClient.java   # MultiPart 클라이언트
├── resources/
│   └── application-elocal.yml              # 로깅 설정
└── docs/
    └── MULTIPART_IMPLEMENTATION.md         # 이 문서
```

### 11.4 트러블슈팅 가이드

#### 일반적인 문제와 해결책
| 문제 상황 | 원인 | 해결책 |
|----------|------|--------|
| Missing boundary | Content-Type 중복 설정 | RequestInterceptor 수정 |
| NullPointerException | messageConverters 미주입 | 순수 SpringFormEncoder 사용 |
| 파일 업로드 실패 | 파일 크기 제한 | spring.servlet.multipart 설정 |
| 인코딩 오류 | 잘못된 문자셋 | UTF-8 명시적 설정 |

#### 디버깅 체크리스트
- [ ] Feign 로그 레벨 DEBUG 확인
- [ ] Content-Type 헤더 값 확인  
- [ ] 파일 크기 및 형식 검증
- [ ] RequestInterceptor 동작 로그 확인
- [ ] SpringFormEncoder Bean 등록 확인

---

## 📝 12. 변경 이력

| 버전 | 날짜 | 변경 내용 | 작성자 |
|------|------|----------|--------|
| 1.0 | 2025-09-17 | 초기 MultiPart 지원 구현 | ByounggwanLee |
| 1.1 | 2025-09-18 | RequestInterceptor 개선, 에러 처리 강화 | ByounggwanLee |
| 1.2 | 2025-09-18 | 문서화 및 테스트 가이드 추가 | ByounggwanLee |

---

**문서 작성자**: ByounggwanLee  
**최종 수정일**: 2025-09-18  
**문서 버전**: 1.2  
**상태**: 완료 ✅

---

이 문서는 SKTAI Feign Client MultiPart 지원 구현의 전체 과정을 상세히 기록한 것으로, 향후 유사한 기능 구현 시 참고 자료로 활용할 수 있습니다.
