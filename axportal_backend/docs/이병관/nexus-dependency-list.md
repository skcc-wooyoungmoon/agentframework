# Nexus Repository 구성을 위한 의존성 목록

## 프로젝트 정보
- **프로젝트명**: AxPortal Backend
- **프로젝트 버전**: 1.0.0
- **Maven 저장소**: ~/.m2/repository
- **대상 의존성**: 336개 라이브러리
- **생성일**: 2025-10-08
- **최종 업데이트**: 2025-10-08

## 의존성 분류 현황

### 주요 카테고리별 분포

| 분류 | 라이브러리 수 | 주요 구성 요소 | 설명 |
|------|-------------|-------------|------|
| **Spring Framework** | 89개 | Boot, Cloud, Security, Data, Core | Spring 생태계 전체 스택 |
| **Testing** | 47개 | JUnit 5, Mockito, TestContainers, Hamcrest | 종합 테스트 프레임워크 |
| **Build Tools** | 35개 | Maven Plugins, Plexus Utilities | 빌드 및 배포 도구 |
| **Apache Commons** | 18개 | Lang3, IO, Codec, Collections | Apache 공통 유틸리티 |
| **JSON Processing** | 17개 | Jackson, JSON Path, Smart JSON | JSON 처리 및 변환 |
| **Network** | 16개 | Netty, Vert.x | 네트워크 통신 라이브러리 |
| **Kubernetes** | 26개 | Fabric8 Client, Models | 쿠버네티스 클라이언트 |
| **Database** | 13개 | H2, HikariCP, PostgreSQL, JPA | 데이터베이스 연동 |
| **Logging** | 12개 | Logback, SLF4J, Log4j | 로깅 프레임워크 |
| **Monitoring** | 8개 | Micrometer | 애플리케이션 모니터링 |
| **Security** | 9개 | JWT, Spring Security | 보안 및 인증 |
| **HTTP Client** | 8개 | Feign, Apache HTTP Client | HTTP 클라이언트 |
| **Compression** | 7개 | ZStd, XZ, Air Compressor | 압축 라이브러리 |
| **Jakarta EE** | 7개 | Activation, Annotation, Persistence | Jakarta EE API |
| **기타** | 21개 | Utility, Validation, Configuration | 기타 지원 라이브러리 |

### 스코프별 분포

| 스코프 | 라이브러리 수 | 비율 | 설명 |
|--------|-------------|------|------|
| **compile** | 289개 | 86.0% | 런타임 필수 의존성 |
| **test** | 47개 | 14.0% | 테스트 전용 의존성 |

### 버전 관리 현황

| 메이저 프레임워크 | 버전 | 최신성 | 호환성 |
|-----------------|------|--------|--------|
| Spring Boot | 3.5.4 | 최신 | 안정 |
| Spring Framework | 6.2.9 | 최신 | 안정 |
| Spring Security | 6.5.4 | 최신 | 안정 |
| Spring Cloud | 4.2.1 | 최신 | 안정 |
| JUnit 5 | 5.11.4 | 최신 | 안정 |
| Jackson | 2.19.2 | 최신 | 안정 |
| Netty | 4.1.123.Final | 최신 | 안정 |
| TestContainers | 1.21.2 | 최신 | 안정 |

## 의존성 목록

| 번호 | 분류 | 구분 | 라이브러리명 | 그룹ID | 아티팩토리ID | VERSION | 설명 | SCOPE |
|------|------|------|-------------|---------|-------------|---------|------|-------|
| 1 | Logging | Logging Framework | Logback Classic | ch.qos.logback | logback-classic | 1.5.18 | Logback 클래식 로깅 구현체 | compile |
| 2 | Logging | Logging Framework | Logback Core | ch.qos.logback | logback-core | 1.5.18 | Logback 코어 로깅 엔진 | compile |
| 3 | Validation | Bean Validation | ClassMate | com.fasterxml | classmate | 1.7.0 | 타입 정보 처리 라이브러리 | compile |
| 4 | JSON Processing | JSON Framework | Jackson Annotations | com.fasterxml.jackson.core | jackson-annotations | 2.19.2 | Jackson JSON 어노테이션 | compile |
| 5 | JSON Processing | JSON Framework | Jackson Core | com.fasterxml.jackson.core | jackson-core | 2.19.2 | Jackson JSON 코어 라이브러리 | compile |
| 6 | JSON Processing | JSON Framework | Jackson Databind | com.fasterxml.jackson.core | jackson-databind | 2.19.2 | Jackson JSON 데이터 바인딩 | compile |
| 7 | JSON Processing | Data Format | Jackson YAML | com.fasterxml.jackson.dataformat | jackson-dataformat-yaml | 2.19.2 | Jackson YAML 데이터 포맷 | compile |
| 8 | JSON Processing | Data Type | Jackson JDK8 | com.fasterxml.jackson.datatype | jackson-datatype-jdk8 | 2.19.2 | Jackson JDK8 데이터 타입 지원 | compile |
| 9 | JSON Processing | Data Type | Jackson JSR310 | com.fasterxml.jackson.datatype | jackson-datatype-jsr310 | 2.19.2 | Jackson JSR310 날짜/시간 지원 | compile |
| 10 | JSON Processing | Module | Jackson Parameter Names | com.fasterxml.jackson.module | jackson-module-parameter-names | 2.19.2 | Jackson 파라미터 이름 모듈 | compile |
| 11 | Cache | Cache Framework | Caffeine | com.github.ben-manes.caffeine | caffeine | 3.1.8 | 고성능 Java 캐시 라이브러리 | compile |
| 12 | JSON Processing | JSON Library | JSON Simple | com.github.cliftonlabs | json-simple | 3.0.2 | 간단한 JSON 파싱 라이브러리 | compile |
| 13 | Container | Docker Client | Docker Java API | com.github.docker-java | docker-java-api | 3.4.2 | Docker Java API 클라이언트 | compile |
| 14 | Container | Docker Client | Docker Java Transport | com.github.docker-java | docker-java-transport | 3.4.2 | Docker Java 전송 계층 | compile |
| 15 | Container | Docker Client | Docker Java Transport ZeroDep | com.github.docker-java | docker-java-transport-zerodep | 3.4.2 | Docker Java 무의존성 전송 | compile |
| 16 | Compression | Compression Library | ZStd JNI | com.github.luben | zstd-jni | 1.5.5-11 | ZStandard 압축 JNI 바인딩 | compile |
| 17 | Compression | Compression Library | ZStd JNI | com.github.luben | zstd-jni | 1.5.6-3 | ZStandard 압축 JNI 바인딩 | compile |
| 18 | Office | MS Office | Curves API | com.github.virtuald | curvesapi | 1.08 | Microsoft Office 곡선 API | compile |
| 19 | Code Quality | Static Analysis | JSR305 | com.google.code.findbugs | jsr305 | 3.0.2 | JSR305 어노테이션 | compile |
| 20 | Code Quality | Static Analysis | Error Prone Annotations | com.google.errorprone | error_prone_annotations | 2.21.1 | Google Error Prone 어노테이션 | compile |
| 21 | Database | In-Memory Database | H2 Database | com.h2database | h2 | 2.3.232 | H2 인메모리 데이터베이스 | compile |
| 22 | JSON Processing | JSON Path | JSON Path | com.jayway.jsonpath | json-path | 2.9.0 | JSON 경로 표현식 라이브러리 | compile |
| 23 | JAXB | JAXB Runtime | iStack Commons Runtime | com.sun.istack | istack-commons-runtime | 4.1.2 | iStack 공통 런타임 라이브러리 | compile |
| 24 | Code Analysis | Code Parser | QDox | com.thoughtworks.qdox | qdox | 2.0.3 | Java 소스 코드 파서 | compile |
| 25 | Code Analysis | Code Parser | QDox | com.thoughtworks.qdox | qdox | 2.2.0 | Java 소스 코드 파서 | compile |
| 26 | JSON Processing | Android JSON | Android JSON | com.vaadin.external.google | android-json | 0.0.20131108.vaadin1 | Android JSON 라이브러리 | compile |
| 27 | Database | Connection Pool | HikariCP | com.zaxxer | HikariCP | 6.3.1 | 고성능 JDBC 커넥션 풀 | compile |
| 28 | Utility | Data Structure | SparseBitSet | com.zaxxer | SparseBitSet | 1.3 | 희소 비트셋 구현체 | compile |
| 29 | Apache Commons | Bean Utils | Commons BeanUtils | commons-beanutils | commons-beanutils | 1.9.4 | Apache Commons Bean 유틸리티 | compile |
| 30 | Apache Commons | Codec | Commons Codec | commons-codec | commons-codec | 1.16.1 | Apache Commons 인코딩/디코딩 | compile |
| 31 | Apache Commons | Codec | Commons Codec | commons-codec | commons-codec | 1.17.0 | Apache Commons 인코딩/디코딩 | compile |
| 32 | Apache Commons | Codec | Commons Codec | commons-codec | commons-codec | 1.17.1 | Apache Commons 인코딩/디코딩 | compile |
| 33 | Apache Commons | Codec | Commons Codec | commons-codec | commons-codec | 1.18.0 | Apache Commons 인코딩/디코딩 | compile |
| 34 | Apache Commons | Collections | Commons Collections | commons-collections | commons-collections | 3.2.2 | Apache Commons 컬렉션 유틸리티 | compile |
| 35 | Apache Commons | File Upload | Commons FileUpload | commons-fileupload | commons-fileupload | 1.5 | Apache Commons 파일 업로드 | compile |
| 36 | Apache Commons | IO | Commons IO | commons-io | commons-io | 2.11.0 | Apache Commons I/O 유틸리티 | compile |
| 37 | Apache Commons | IO | Commons IO | commons-io | commons-io | 2.16.1 | Apache Commons I/O 유틸리티 | compile |
| 38 | Apache Commons | IO | Commons IO | commons-io | commons-io | 2.18.0 | Apache Commons I/O 유틸리티 | compile |
| 39 | Apache Commons | Logging | Commons Logging | commons-logging | commons-logging | 1.2 | Apache Commons 로깅 추상화 | compile |
| 40 | Compression | Compression Library | Air Compressor | io.airlift | aircompressor | 0.27 | Airlift 압축 라이브러리 | compile |
| 41 | Kubernetes | Kubernetes Client | Kubernetes Client | io.fabric8 | kubernetes-client | 7.4.0 | Fabric8 Kubernetes 클라이언트 | compile |
| 42 | Kubernetes | Kubernetes Client | Kubernetes Client API | io.fabric8 | kubernetes-client-api | 6.13.5 | Kubernetes 클라이언트 API | compile |
| 43 | Kubernetes | HTTP Client | Kubernetes HTTP Client Vertx | io.fabric8 | kubernetes-httpclient-vertx | 6.13.5 | Kubernetes Vertx HTTP 클라이언트 | compile |
| 44 | Kubernetes | Model | Kubernetes Model Admission Registration | io.fabric8 | kubernetes-model-admissionregistration | 6.13.5 | Kubernetes 승인 등록 모델 | compile |
| 45 | Kubernetes | Model | Kubernetes Model API Extensions | io.fabric8 | kubernetes-model-apiextensions | 6.13.5 | Kubernetes API 확장 모델 | compile |
| 46 | Kubernetes | Model | Kubernetes Model Apps | io.fabric8 | kubernetes-model-apps | 6.13.5 | Kubernetes 앱 모델 | compile |
| 47 | Kubernetes | Model | Kubernetes Model Autoscaling | io.fabric8 | kubernetes-model-autoscaling | 6.13.5 | Kubernetes 오토스케일링 모델 | compile |
| 48 | Kubernetes | Model | Kubernetes Model Batch | io.fabric8 | kubernetes-model-batch | 6.13.5 | Kubernetes 배치 모델 | compile |
| 49 | Kubernetes | Model | Kubernetes Model Certificates | io.fabric8 | kubernetes-model-certificates | 6.13.5 | Kubernetes 인증서 모델 | compile |
| 50 | Kubernetes | Model | Kubernetes Model Common | io.fabric8 | kubernetes-model-common | 6.13.5 | Kubernetes 공통 모델 | compile |
| 51 | Kubernetes | Model | Kubernetes Model Coordination | io.fabric8 | kubernetes-model-coordination | 6.13.5 | Kubernetes 조정 모델 | compile |
| 52 | Kubernetes | Model | Kubernetes Model Core | io.fabric8 | kubernetes-model-core | 6.13.5 | Kubernetes 코어 모델 | compile |
| 53 | Kubernetes | Model | Kubernetes Model Discovery | io.fabric8 | kubernetes-model-discovery | 6.13.5 | Kubernetes 디스커버리 모델 | compile |
| 54 | Kubernetes | Model | Kubernetes Model Events | io.fabric8 | kubernetes-model-events | 6.13.5 | Kubernetes 이벤트 모델 | compile |
| 55 | Kubernetes | Model | Kubernetes Model Extensions | io.fabric8 | kubernetes-model-extensions | 6.13.5 | Kubernetes 확장 모델 | compile |
| 56 | Kubernetes | Model | Kubernetes Model Flow Control | io.fabric8 | kubernetes-model-flowcontrol | 6.13.5 | Kubernetes 플로우 제어 모델 | compile |
| 57 | Kubernetes | Model | Kubernetes Model Gateway API | io.fabric8 | kubernetes-model-gatewayapi | 6.13.5 | Kubernetes 게이트웨이 API 모델 | compile |
| 58 | Kubernetes | Model | Kubernetes Model Metrics | io.fabric8 | kubernetes-model-metrics | 6.13.5 | Kubernetes 메트릭 모델 | compile |
| 59 | Kubernetes | Model | Kubernetes Model Networking | io.fabric8 | kubernetes-model-networking | 6.13.5 | Kubernetes 네트워킹 모델 | compile |
| 60 | Kubernetes | Model | Kubernetes Model Node | io.fabric8 | kubernetes-model-node | 6.13.5 | Kubernetes 노드 모델 | compile |
| 61 | Kubernetes | Model | Kubernetes Model Policy | io.fabric8 | kubernetes-model-policy | 6.13.5 | Kubernetes 정책 모델 | compile |
| 62 | Kubernetes | Model | Kubernetes Model RBAC | io.fabric8 | kubernetes-model-rbac | 6.13.5 | Kubernetes RBAC 모델 | compile |
| 63 | Kubernetes | Model | Kubernetes Model Resource | io.fabric8 | kubernetes-model-resource | 6.13.5 | Kubernetes 리소스 모델 | compile |
| 64 | Kubernetes | Model | Kubernetes Model Scheduling | io.fabric8 | kubernetes-model-scheduling | 6.13.5 | Kubernetes 스케줄링 모델 | compile |
| 65 | Kubernetes | Model | Kubernetes Model Storage Class | io.fabric8 | kubernetes-model-storageclass | 6.13.5 | Kubernetes 스토리지 클래스 모델 | compile |
| 66 | JSON Processing | JSON Patch | ZJson Patch | io.fabric8 | zjsonpatch | 7.4.0 | JSON 패치 라이브러리 | compile |
| 67 | Build Tools | Maven Plugin | Git Commit ID Maven Plugin | io.github.git-commit-id | git-commit-id-maven-plugin | 9.0.2 | Git 커밋 ID Maven 플러그인 | compile |
| 68 | HTTP Client | Feign Client | Feign Core | io.github.openfeign | feign-core | 13.5 | Feign HTTP 클라이언트 코어 | compile |
| 69 | HTTP Client | Feign Client | Feign Form | io.github.openfeign | feign-form | 13.5 | Feign 폼 데이터 지원 | compile |
| 70 | HTTP Client | Feign Client | Feign Form Spring | io.github.openfeign | feign-form-spring | 13.5 | Feign Spring 폼 지원 | compile |
| 71 | HTTP Client | Feign Client | Feign SLF4J | io.github.openfeign | feign-slf4j | 13.5 | Feign SLF4J 로깅 지원 | compile |
| 72 | Security | JWT | JJWT API | io.jsonwebtoken | jjwt-api | 0.12.6 | Java JWT API | compile |
| 73 | Security | JWT | JJWT Impl | io.jsonwebtoken | jjwt-impl | 0.12.6 | Java JWT 구현체 | compile |
| 74 | Security | JWT | JJWT Jackson | io.jsonwebtoken | jjwt-jackson | 0.12.6 | Java JWT Jackson 지원 | compile |
| 75 | Monitoring | Metrics | Micrometer Commons | io.micrometer | micrometer-commons | 1.14.9 | Micrometer 공통 라이브러리 | compile |
| 76 | Monitoring | Metrics | Micrometer Commons | io.micrometer | micrometer-commons | 1.15.2 | Micrometer 공통 라이브러리 | compile |
| 77 | Monitoring | Metrics | Micrometer Core | io.micrometer | micrometer-core | 1.15.2 | Micrometer 코어 메트릭 | compile |
| 78 | Monitoring | Metrics | Micrometer Jakarta9 | io.micrometer | micrometer-jakarta9 | 1.15.2 | Micrometer Jakarta EE 9 지원 | compile |
| 79 | Monitoring | Metrics | Micrometer Observation | io.micrometer | micrometer-observation | 1.14.9 | Micrometer 관찰 라이브러리 | compile |
| 80 | Monitoring | Metrics | Micrometer Observation | io.micrometer | micrometer-observation | 1.15.2 | Micrometer 관찰 라이브러리 | compile |
| 81 | Network | Network Framework | Netty Buffer | io.netty | netty-buffer | 4.1.123.Final | Netty 버퍼 관리 | compile |
| 82 | Network | Network Framework | Netty Codec | io.netty | netty-codec | 4.1.123.Final | Netty 코덱 라이브러리 | compile |
| 83 | Network | Network Framework | Netty Codec DNS | io.netty | netty-codec-dns | 4.1.123.Final | Netty DNS 코덱 | compile |
| 84 | Network | Network Framework | Netty Codec HTTP | io.netty | netty-codec-http | 4.1.123.Final | Netty HTTP 코덱 | compile |
| 85 | Network | Network Framework | Netty Codec HTTP2 | io.netty | netty-codec-http2 | 4.1.123.Final | Netty HTTP/2 코덱 | compile |
| 86 | Network | Network Framework | Netty Codec SOCKS | io.netty | netty-codec-socks | 4.1.123.Final | Netty SOCKS 코덱 | compile |
| 87 | Network | Network Framework | Netty Common | io.netty | netty-common | 4.1.123.Final | Netty 공통 라이브러리 | compile |
| 88 | Network | Network Framework | Netty Handler | io.netty | netty-handler | 4.1.123.Final | Netty 핸들러 라이브러리 | compile |
| 89 | Network | Network Framework | Netty Handler Proxy | io.netty | netty-handler-proxy | 4.1.123.Final | Netty 프록시 핸들러 | compile |
| 90 | Network | Network Framework | Netty Resolver | io.netty | netty-resolver | 4.1.123.Final | Netty 리졸버 라이브러리 | compile |
| 91 | Network | Network Framework | Netty Resolver DNS | io.netty | netty-resolver-dns | 4.1.123.Final | Netty DNS 리졸버 | compile |
| 92 | Network | Network Framework | Netty Transport | io.netty | netty-transport | 4.1.123.Final | Netty 전송 계층 | compile |
| 93 | Network | Network Framework | Netty Transport Native Unix Common | io.netty | netty-transport-native-unix-common | 4.1.123.Final | Netty Unix 네이티브 전송 공통 | compile |
| 94 | Indexing | Annotation Indexing | Jandex | io.smallrye | jandex | 3.1.2 | 어노테이션 인덱싱 라이브러리 | compile |
| 95 | API Documentation | Swagger | Swagger Annotations Jakarta | io.swagger.core.v3 | swagger-annotations-jakarta | 2.2.30 | Swagger Jakarta 어노테이션 | compile |
| 96 | API Documentation | Swagger | Swagger Core Jakarta | io.swagger.core.v3 | swagger-core-jakarta | 2.2.30 | Swagger Jakarta 코어 | compile |
| 97 | API Documentation | Swagger | Swagger Models Jakarta | io.swagger.core.v3 | swagger-models-jakarta | 2.2.30 | Swagger Jakarta 모델 | compile |
| 98 | Network | Reactive Framework | Vert.x Auth Common | io.vertx | vertx-auth-common | 4.5.8 | Vert.x 인증 공통 라이브러리 | compile |
| 99 | Network | Reactive Framework | Vert.x Core | io.vertx | vertx-core | 4.5.8 | Vert.x 코어 라이브러리 | compile |
| 100 | Network | Reactive Framework | Vert.x Web Client | io.vertx | vertx-web-client | 4.5.8 | Vert.x 웹 클라이언트 | compile |
| 101 | Network | Reactive Framework | Vert.x Web Common | io.vertx | vertx-web-common | 4.5.8 | Vert.x 웹 공통 라이브러리 | compile |
| 102 | Jakarta EE | Activation API | Jakarta Activation API | jakarta.activation | jakarta.activation-api | 2.1.3 | Jakarta EE 활성화 API | compile |
| 103 | Jakarta EE | Annotation API | Jakarta Annotation API | jakarta.annotation | jakarta.annotation-api | 2.1.1 | Jakarta EE 어노테이션 API | compile |
| 104 | Jakarta EE | Injection API | Jakarta Inject API | jakarta.inject | jakarta.inject-api | 2.0.1 | Jakarta EE 의존성 주입 API | compile |
| 105 | Jakarta EE | Persistence API | Jakarta Persistence API | jakarta.persistence | jakarta.persistence-api | 3.1.0 | Jakarta EE 퍼시스턴스 API | compile |
| 106 | Jakarta EE | Transaction API | Jakarta Transaction API | jakarta.transaction | jakarta.transaction-api | 2.0.1 | Jakarta EE 트랜잭션 API | compile |
| 107 | Jakarta EE | Validation API | Jakarta Validation API | jakarta.validation | jakarta.validation-api | 3.0.2 | Jakarta EE 검증 API | compile |
| 108 | Jakarta EE | XML Bind API | Jakarta XML Bind API | jakarta.xml.bind | jakarta.xml.bind-api | 4.0.2 | Jakarta EE XML 바인딩 API | compile |
| 109 | Legacy | Injection API | Javax Inject | javax.inject | javax.inject | 1 | JSR-330 의존성 주입 API | compile |
| 110 | Testing | Legacy Test Framework | JUnit 4 | junit | junit | 4.13.2 | JUnit 4 테스트 프레임워크 | test |
| 111 | Utility | Bytecode Manipulation | Byte Buddy | net.bytebuddy | byte-buddy | 1.17.6 | 바이트코드 조작 라이브러리 | compile |
| 112 | Utility | Bytecode Manipulation | Byte Buddy Agent | net.bytebuddy | byte-buddy-agent | 1.17.6 | Byte Buddy 에이전트 | compile |
| 113 | System Integration | Native Access | JNA | net.java.dev.jna | jna | 5.13.0 | Java Native Access 라이브러리 | compile |
| 114 | System Integration | Native Access | JNA | net.java.dev.jna | jna | 5.17.0 | Java Native Access 라이브러리 | compile |
| 115 | System Integration | Native Access | JNA Platform | net.java.dev.jna | jna-platform | 5.17.0 | JNA 플랫폼 지원 라이브러리 | compile |
| 116 | JSON Processing | JSON Library | Accessors Smart | net.minidev | accessors-smart | 2.5.2 | 스마트 접근자 라이브러리 | compile |
| 117 | JSON Processing | JSON Library | JSON Smart | net.minidev | json-smart | 2.5.2 | 빠른 JSON 라이브러리 | compile |
| 118 | Code Analysis | Parser Runtime | ANTLR4 Runtime | org.antlr | antlr4-runtime | 4.13.0 | ANTLR 파서 런타임 | compile |
| 119 | Code Analysis | Parser Runtime | ANTLR4 Runtime | org.antlr | antlr4-runtime | 4.7.2 | ANTLR 파서 런타임 | compile |
| 120 | Apache Commons | Collections | Commons Collections4 | org.apache.commons | commons-collections4 | 4.4 | Apache Commons 컬렉션 4 | compile |
| 121 | Apache Commons | Compression | Commons Compress | org.apache.commons | commons-compress | 1.26.1 | Apache Commons 압축 라이브러리 | compile |
| 122 | Apache Commons | Compression | Commons Compress | org.apache.commons | commons-compress | 1.26.2 | Apache Commons 압축 라이브러리 | compile |
| 123 | Apache Commons | Compression | Commons Compress | org.apache.commons | commons-compress | 1.27.1 | Apache Commons 압축 라이브러리 | compile |
| 124 | Apache Commons | XML Processing | Commons Digester3 | org.apache.commons | commons-digester3 | 3.2 | Apache Commons XML 규칙 엔진 | compile |
| 125 | Apache Commons | Language Utils | Commons Lang3 | org.apache.commons | commons-lang3 | 3.12.0 | Apache Commons 언어 유틸리티 | compile |
| 126 | Apache Commons | Language Utils | Commons Lang3 | org.apache.commons | commons-lang3 | 3.14.0 | Apache Commons 언어 유틸리티 | compile |
| 127 | Apache Commons | Language Utils | Commons Lang3 | org.apache.commons | commons-lang3 | 3.16.0 | Apache Commons 언어 유틸리티 | compile |
| 128 | Apache Commons | Language Utils | Commons Lang3 | org.apache.commons | commons-lang3 | 3.17.0 | Apache Commons 언어 유틸리티 | compile |
| 129 | Apache Commons | Math Library | Commons Math3 | org.apache.commons | commons-math3 | 3.6.1 | Apache Commons 수학 라이브러리 | compile |
| 130 | Apache Commons | Text Processing | Commons Text | org.apache.commons | commons-text | 1.12.0 | Apache Commons 텍스트 처리 | compile |
| 131 | HTTP Client | HTTP Client | HTTP Client5 | org.apache.httpcomponents.client5 | httpclient5 | 5.5 | Apache HTTP Client 5 | compile |
| 132 | HTTP Client | HTTP Core | HTTP Core5 | org.apache.httpcomponents.core5 | httpcore5 | 5.3.4 | Apache HTTP Core 5 | compile |
| 133 | HTTP Client | HTTP Core | HTTP Core5 H2 | org.apache.httpcomponents.core5 | httpcore5-h2 | 5.3.4 | Apache HTTP Core 5 HTTP/2 | compile |
| 134 | Logging | Log4j | Log4j API | org.apache.logging.log4j | log4j-api | 2.24.3 | Log4j 2 API | compile |
| 135 | Logging | Log4j | Log4j to SLF4J | org.apache.logging.log4j | log4j-to-slf4j | 2.24.3 | Log4j to SLF4J 브리지 | compile |
| 136 | Documentation | Doxia | Doxia Core | org.apache.maven.doxia | doxia-core | 2.0.0 | Maven Doxia 문서 생성 코어 | compile |
| 137 | Documentation | Doxia | Doxia Integration Tools | org.apache.maven.doxia | doxia-integration-tools | 2.0.0 | Doxia 통합 도구 | compile |
| 138 | Documentation | Doxia | Doxia Module APT | org.apache.maven.doxia | doxia-module-apt | 2.0.0 | Doxia APT 모듈 | compile |
| 139 | Documentation | Doxia | Doxia Module XDoc | org.apache.maven.doxia | doxia-module-xdoc | 2.0.0 | Doxia XDoc 모듈 | compile |
| 140 | Documentation | Doxia | Doxia Module XHTML5 | org.apache.maven.doxia | doxia-module-xhtml5 | 2.0.0 | Doxia XHTML5 모듈 | compile |
| 141 | Documentation | Doxia | Doxia Sink API | org.apache.maven.doxia | doxia-sink-api | 2.0.0 | Doxia Sink API | compile |
| 142 | Documentation | Doxia | Doxia Site Model | org.apache.maven.doxia | doxia-site-model | 2.0.0 | Doxia 사이트 모델 | compile |
| 143 | Documentation | Doxia | Doxia Site Renderer | org.apache.maven.doxia | doxia-site-renderer | 2.0.0 | Doxia 사이트 렌더러 | compile |
| 144 | Documentation | Doxia | Doxia Skin Model | org.apache.maven.doxia | doxia-skin-model | 2.0.0 | Doxia 스킨 모델 | compile |
| 145 | Build Tools | Maven | Maven Archiver | org.apache.maven | maven-archiver | 3.6.2 | Maven 아카이브 생성 도구 | compile |
| 146 | Build Tools | Maven Plugin | Maven Antrun Plugin | org.apache.maven.plugins | maven-antrun-plugin | 3.1.0 | Maven Ant 실행 플러그인 | compile |
| 147 | Build Tools | Maven Plugin | Maven Assembly Plugin | org.apache.maven.plugins | maven-assembly-plugin | 3.7.1 | Maven 어셈블리 플러그인 | compile |
| 148 | Build Tools | Maven Plugin | Maven Clean Plugin | org.apache.maven.plugins | maven-clean-plugin | 3.4.1 | Maven 정리 플러그인 | compile |
| 149 | Build Tools | Maven Plugin | Maven Compiler Plugin | org.apache.maven.plugins | maven-compiler-plugin | 3.13.0 | Maven 컴파일러 플러그인 | compile |
| 150 | Build Tools | Maven Plugin | Maven Compiler Plugin | org.apache.maven.plugins | maven-compiler-plugin | 3.14.0 | Maven 컴파일러 플러그인 | compile |
| 151 | Build Tools | Maven Plugin | Maven Dependency Plugin | org.apache.maven.plugins | maven-dependency-plugin | 3.8.1 | Maven 의존성 플러그인 | compile |
| 152 | Build Tools | Maven Plugin | Maven Deploy Plugin | org.apache.maven.plugins | maven-deploy-plugin | 3.1.4 | Maven 배포 플러그인 | compile |
| 153 | Build Tools | Maven Plugin | Maven Install Plugin | org.apache.maven.plugins | maven-install-plugin | 3.1.4 | Maven 설치 플러그인 | compile |
| 154 | Build Tools | Maven Plugin | Maven JAR Plugin | org.apache.maven.plugins | maven-jar-plugin | 3.4.2 | Maven JAR 생성 플러그인 | compile |
| 155 | Build Tools | Maven Plugin | Maven Resources Plugin | org.apache.maven.plugins | maven-resources-plugin | 3.3.1 | Maven 리소스 플러그인 | compile |
| 156 | Build Tools | Maven Plugin | Maven Shade Plugin | org.apache.maven.plugins | maven-shade-plugin | 3.6.0 | Maven 쉐이드 플러그인 | compile |
| 157 | Build Tools | Maven Plugin | Maven Site Plugin | org.apache.maven.plugins | maven-site-plugin | 3.12.1 | Maven 사이트 플러그인 | compile |
| 158 | Build Tools | Maven Plugin | Maven Surefire Plugin | org.apache.maven.plugins | maven-surefire-plugin | 3.5.3 | Maven 테스트 실행 플러그인 | compile |
| 159 | Build Tools | Maven | Maven Reporting API | org.apache.maven.reporting | maven-reporting-api | 4.0.0 | Maven 리포팅 API | compile |
| 160 | Build Tools | Maven | Maven Reporting Impl | org.apache.maven.reporting | maven-reporting-impl | 4.0.0 | Maven 리포팅 구현체 | compile |
| 161 | Build Tools | Maven | Maven Resolver API | org.apache.maven.resolver | maven-resolver-api | 1.4.1 | Maven 리졸버 API | compile |
| 162 | Build Tools | Maven | Maven Resolver Util | org.apache.maven.resolver | maven-resolver-util | 1.4.1 | Maven 리졸버 유틸리티 | compile |
| 163 | Build Tools | Maven Shared | File Management | org.apache.maven.shared | file-management | 3.1.0 | Maven 파일 관리 유틸리티 | compile |
| 164 | Build Tools | Maven Shared | Maven Artifact Transfer | org.apache.maven.shared | maven-artifact-transfer | 0.13.1 | Maven 아티팩트 전송 | compile |
| 165 | Build Tools | Maven Shared | Maven Common Artifact Filters | org.apache.maven.shared | maven-common-artifact-filters | 3.4.0 | Maven 공통 아티팩트 필터 | compile |
| 166 | Build Tools | Maven Shared | Maven Dependency Analyzer | org.apache.maven.shared | maven-dependency-analyzer | 1.15.0 | Maven 의존성 분석기 | compile |
| 167 | Build Tools | Maven Shared | Maven Dependency Tree | org.apache.maven.shared | maven-dependency-tree | 3.3.0 | Maven 의존성 트리 | compile |
| 168 | Build Tools | Maven Shared | Maven Filtering | org.apache.maven.shared | maven-filtering | 3.3.1 | Maven 필터링 | compile |
| 169 | Build Tools | Maven Shared | Maven Shared Incremental | org.apache.maven.shared | maven-shared-incremental | 1.1 | Maven 증분 빌드 | compile |
| 170 | Build Tools | Maven Shared | Maven Shared Utils | org.apache.maven.shared | maven-shared-utils | 3.4.2 | Maven 공유 유틸리티 | compile |
| 171 | Testing | Surefire | Common Java5 | org.apache.maven.surefire | common-java5 | 3.5.3 | Surefire Java5 공통 | compile |
| 172 | Testing | Surefire | Maven Surefire Common | org.apache.maven.surefire | maven-surefire-common | 3.5.3 | Maven Surefire 공통 | compile |
| 173 | Testing | Surefire | Surefire API | org.apache.maven.surefire | surefire-api | 3.5.3 | Surefire API | compile |
| 174 | Testing | Surefire | Surefire Booter | org.apache.maven.surefire | surefire-booter | 3.5.3 | Surefire 부트스트랩 | compile |
| 175 | Testing | Surefire | Surefire Extensions API | org.apache.maven.surefire | surefire-extensions-api | 3.5.3 | Surefire 확장 API | compile |
| 176 | Testing | Surefire | Surefire Extensions SPI | org.apache.maven.surefire | surefire-extensions-spi | 3.5.3 | Surefire 확장 SPI | compile |
| 177 | Testing | Surefire | Surefire JUnit Platform | org.apache.maven.surefire | surefire-junit-platform | 3.5.3 | Surefire JUnit 플랫폼 | compile |
| 178 | Testing | Surefire | Surefire Logger API | org.apache.maven.surefire | surefire-logger-api | 3.5.3 | Surefire 로거 API | compile |
| 179 | Testing | Surefire | Surefire Shared Utils | org.apache.maven.surefire | surefire-shared-utils | 3.5.3 | Surefire 공유 유틸리티 | compile |
| 180 | Office | Apache POI | POI | org.apache.poi | poi | 5.4.1 | Apache POI 오피스 문서 처리 | compile |
| 181 | Office | Apache POI | POI OOXML | org.apache.poi | poi-ooxml | 5.4.1 | Apache POI OOXML 지원 | compile |
| 182 | Office | Apache POI | POI OOXML Lite | org.apache.poi | poi-ooxml-lite | 5.4.1 | Apache POI OOXML Lite | compile |
| 183 | Web Server | Tomcat Embedded | Tomcat Embed Core | org.apache.tomcat.embed | tomcat-embed-core | 10.1.43 | 임베디드 Tomcat 코어 | compile |
| 184 | Web Server | Tomcat Embedded | Tomcat Embed EL | org.apache.tomcat.embed | tomcat-embed-el | 10.1.43 | 임베디드 Tomcat EL | compile |
| 185 | Web Server | Tomcat Embedded | Tomcat Embed WebSocket | org.apache.tomcat.embed | tomcat-embed-websocket | 10.1.43 | 임베디드 Tomcat WebSocket | compile |
| 186 | Template Engine | Velocity | Velocity Tools Generic | org.apache.velocity.tools | velocity-tools-generic | 3.1 | Velocity 템플릿 도구 | compile |
| 187 | Template Engine | Velocity | Velocity Engine Core | org.apache.velocity | velocity-engine-core | 2.4 | Velocity 템플릿 엔진 코어 | compile |
| 188 | XML Processing | XMLBeans | XMLBeans | org.apache.xmlbeans | xmlbeans | 5.3.0 | Apache XMLBeans XML 바인딩 | compile |
| 189 | Testing | API Guardian | API Guardian API | org.apiguardian | apiguardian-api | 1.1.2 | JUnit 5 API 가디언 | test |
| 190 | AOP | AspectJ | AspectJ Runtime | org.aspectj | aspectjrt | 1.9.22 | AspectJ 런타임 | compile |
| 191 | AOP | AspectJ | AspectJ Weaver | org.aspectj | aspectjweaver | 1.9.22 | AspectJ 위버 | compile |
| 192 | Testing | Assertion Library | AssertJ Core | org.assertj | assertj-core | 3.27.3 | AssertJ 어서션 라이브러리 | test |
| 193 | Testing | Asynchronous Testing | Awaitility | org.awaitility | awaitility | 4.2.2 | 비동기 테스트 라이브러리 | test |
| 194 | Security | Cryptography | Bouncy Castle Provider | org.bouncycastle | bcprov-jdk18on | 1.78.1 | Bouncy Castle 암호화 프로바이더 | compile |
| 195 | Code Quality | Static Analysis | Checker Qual | org.checkerframework | checker-qual | 3.37.0 | Checker Framework 한정자 | compile |
| 196 | Build Tools | Maven Plugin | Build Helper Maven Plugin | org.codehaus.mojo | build-helper-maven-plugin | 3.6.1 | Maven 빌드 헬퍼 플러그인 | compile |
| 197 | Build Tools | Plexus | Plexus Archiver | org.codehaus.plexus | plexus-archiver | 4.10.0 | Plexus 아카이버 | compile |
| 198 | Build Tools | Plexus | Plexus Archiver | org.codehaus.plexus | plexus-archiver | 4.9.2 | Plexus 아카이버 | compile |
| 199 | Build Tools | Plexus | Plexus Classworlds | org.codehaus.plexus | plexus-classworlds | 2.6.0 | Plexus 클래스 로더 | compile |
| 200 | Build Tools | Plexus | Plexus Compiler API | org.codehaus.plexus | plexus-compiler-api | 2.15.0 | Plexus 컴파일러 API | compile |
| 201 | Build Tools | Plexus | Plexus Compiler Javac | org.codehaus.plexus | plexus-compiler-javac | 2.15.0 | Plexus Java 컴파일러 | compile |
| 202 | Build Tools | Plexus | Plexus Compiler Manager | org.codehaus.plexus | plexus-compiler-manager | 2.15.0 | Plexus 컴파일러 매니저 | compile |
| 203 | Build Tools | Plexus | Plexus Component Annotations | org.codehaus.plexus | plexus-component-annotations | 2.0.0 | Plexus 컴포넌트 어노테이션 | compile |
| 204 | Build Tools | Plexus | Plexus I18N | org.codehaus.plexus | plexus-i18n | 1.0-beta-10 | Plexus 국제화 지원 | compile |
| 205 | Build Tools | Plexus | Plexus Interpolation | org.codehaus.plexus | plexus-interpolation | 1.26 | Plexus 문자열 보간 | compile |
| 206 | Build Tools | Plexus | Plexus Interpolation | org.codehaus.plexus | plexus-interpolation | 1.27 | Plexus 문자열 보간 | compile |
| 207 | Build Tools | Plexus | Plexus IO | org.codehaus.plexus | plexus-io | 3.4.2 | Plexus I/O 유틸리티 | compile |
| 208 | Build Tools | Plexus | Plexus IO | org.codehaus.plexus | plexus-io | 3.5.1 | Plexus I/O 유틸리티 | compile |
| 209 | Build Tools | Plexus | Plexus Java | org.codehaus.plexus | plexus-java | 1.2.0 | Plexus Java 도구 | compile |
| 210 | Build Tools | Plexus | Plexus Java | org.codehaus.plexus | plexus-java | 1.4.0 | Plexus Java 도구 | compile |
| 211 | Build Tools | Plexus | Plexus Utils | org.codehaus.plexus | plexus-utils | 3.5.1 | Plexus 유틸리티 | compile |
| 212 | Build Tools | Plexus | Plexus Utils | org.codehaus.plexus | plexus-utils | 4.0.0 | Plexus 유틸리티 | compile |
| 213 | Build Tools | Plexus | Plexus Utils | org.codehaus.plexus | plexus-utils | 4.0.1 | Plexus 유틸리티 | compile |
| 214 | Build Tools | Plexus | Plexus Velocity | org.codehaus.plexus | plexus-velocity | 2.2.0 | Plexus Velocity 통합 | compile |
| 215 | Build Tools | Plexus | Plexus XML | org.codehaus.plexus | plexus-xml | 3.0.0 | Plexus XML 처리 | compile |
| 216 | Build Tools | Plexus | Plexus XML | org.codehaus.plexus | plexus-xml | 3.0.1 | Plexus XML 처리 | compile |
| 217 | Security | SBOM | CycloneDX Maven Plugin | org.cyclonedx | cyclonedx-maven-plugin | 2.9.1 | SBOM 생성 Maven 플러그인 | compile |
| 218 | JAXB | Activation | Angus Activation | org.eclipse.angus | angus-activation | 2.0.2 | Eclipse Angus 활성화 구현체 | compile |
| 219 | Dependency Injection | Eclipse Sisu | Eclipse Sisu Inject | org.eclipse.sisu | org.eclipse.sisu.inject | 0.9.0.M3 | Eclipse Sisu 의존성 주입 | compile |
| 220 | Dependency Injection | Eclipse Sisu | Eclipse Sisu Plexus | org.eclipse.sisu | org.eclipse.sisu.plexus | 0.9.0.M3 | Eclipse Sisu Plexus 통합 | compile |
| 221 | Database Migration | Flyway | Flyway Maven Plugin | org.flywaydb | flyway-maven-plugin | 11.7.2 | Flyway 데이터베이스 마이그레이션 플러그인 | compile |
| 222 | JAXB | JAXB Runtime | JAXB Core | org.glassfish.jaxb | jaxb-core | 4.0.5 | JAXB 코어 라이브러리 | compile |
| 223 | JAXB | JAXB Runtime | JAXB Runtime | org.glassfish.jaxb | jaxb-runtime | 4.0.5 | JAXB 런타임 라이브러리 | compile |
| 224 | JAXB | XML Processing | TXW2 | org.glassfish.jaxb | txw2 | 4.0.5 | Typed XML Writer | compile |
| 225 | Testing | Matcher Library | Hamcrest | org.hamcrest | hamcrest | 3.0 | Hamcrest 매처 라이브러리 | test |
| 226 | Testing | Matcher Library | Hamcrest Core | org.hamcrest | hamcrest-core | 3.0 | Hamcrest 코어 매처 | test |
| 227 | Monitoring | Metrics | HDR Histogram | org.hdrhistogram | HdrHistogram | 2.2.2 | 고성능 히스토그램 라이브러리 | compile |
| 228 | ORM | Hibernate | Hibernate Commons Annotations | org.hibernate.common | hibernate-commons-annotations | 6.0.6.Final | Hibernate 공통 어노테이션 | compile |
| 229 | ORM | Hibernate | Hibernate Core | org.hibernate.orm | hibernate-core | 6.4.4.Final | Hibernate ORM 코어 | compile |
| 230 | Validation | Hibernate Validator | Hibernate Validator | org.hibernate.validator | hibernate-validator | 8.0.2.Final | Hibernate 검증 프레임워크 | compile |
| 231 | Compression | Compression Library | Snappy | org.iq80.snappy | snappy | 0.4 | Snappy 압축 라이브러리 | compile |
| 232 | Logging | JBoss Logging | JBoss Logging | org.jboss.logging | jboss-logging | 3.6.1.Final | JBoss 로깅 프레임워크 | compile |
| 233 | XML Processing | XML Parser | JDOM2 | org.jdom | jdom2 | 2.0.6.1 | JDOM XML 파서 | compile |
| 234 | Code Quality | Static Analysis | JetBrains Annotations | org.jetbrains | annotations | 17.0.0 | JetBrains 어노테이션 | compile |
| 235 | Build Tools | Kotlin | Kotlin Maven Plugin | org.jetbrains.kotlin | kotlin-maven-plugin | 1.9.25 | Kotlin Maven 빌드 플러그인 | compile |
| 236 | Database | Code Generation | jOOQ Codegen Maven | org.jooq | jooq-codegen-maven | 3.19.24 | jOOQ 코드 생성 Maven 플러그인 | compile |
| 237 | Code Quality | Static Analysis | JSpecify | org.jspecify | jspecify | 1.0.0 | JSpecify 어노테이션 | compile |
| 238 | Testing | Test Framework | JUnit Jupiter | org.junit.jupiter | junit-jupiter | 5.12.2 | JUnit 5 Jupiter 집합체 | test |
| 239 | Testing | Test Framework | JUnit Jupiter API | org.junit.jupiter | junit-jupiter-api | 5.12.2 | JUnit 5 Jupiter API | test |
| 240 | Testing | Test Framework | JUnit Jupiter Engine | org.junit.jupiter | junit-jupiter-engine | 5.12.2 | JUnit 5 Jupiter 엔진 | test |
| 241 | Testing | Test Framework | JUnit Jupiter Params | org.junit.jupiter | junit-jupiter-params | 5.12.2 | JUnit 5 파라미터화 테스트 | test |
| 242 | Testing | Test Platform | JUnit Platform Commons | org.junit.platform | junit-platform-commons | 1.12.1 | JUnit 플랫폼 공통 라이브러리 | test |
| 243 | Testing | Test Platform | JUnit Platform Commons | org.junit.platform | junit-platform-commons | 1.12.2 | JUnit 플랫폼 공통 라이브러리 | test |
| 244 | Testing | Test Platform | JUnit Platform Engine | org.junit.platform | junit-platform-engine | 1.12.1 | JUnit 플랫폼 엔진 | test |
| 245 | Testing | Test Platform | JUnit Platform Engine | org.junit.platform | junit-platform-engine | 1.12.2 | JUnit 플랫폼 엔진 | test |
| 246 | Testing | Test Platform | JUnit Platform Launcher | org.junit.platform | junit-platform-launcher | 1.12.1 | JUnit 플랫폼 런처 | test |
| 247 | Testing | Test Platform | JUnit Platform Launcher | org.junit.platform | junit-platform-launcher | 1.12.2 | JUnit 플랫폼 런처 | test |
| 248 | Monitoring | Performance | Latency Utils | org.latencyutils | LatencyUtils | 2.0.3 | 지연시간 측정 유틸리티 | compile |
| 249 | Database Migration | Liquibase | Liquibase Maven Plugin | org.liquibase | liquibase-maven-plugin | 4.31.1 | Liquibase 마이그레이션 Maven 플러그인 | compile |
| 250 | Code Generation | Mapping | MapStruct | org.mapstruct | mapstruct | 1.6.3 | 매핑 코드 생성 라이브러리 | compile |
| 251 | Code Generation | Mapping | MapStruct Processor | org.mapstruct | mapstruct-processor | 1.6.3 | MapStruct 어노테이션 프로세서 | compile |
| 252 | Testing | Mocking Framework | Mockito Core | org.mockito | mockito-core | 5.17.0 | Mockito 모킹 프레임워크 | test |
| 253 | Testing | Mocking Framework | Mockito JUnit Jupiter | org.mockito | mockito-junit-jupiter | 5.17.0 | Mockito JUnit 5 통합 | test |
| 254 | Database | ORM Framework | MyBatis | org.mybatis | mybatis | 3.5.19 | MyBatis SQL 매핑 프레임워크 | compile |
| 255 | Database | Spring Integration | MyBatis Spring | org.mybatis | mybatis-spring | 3.0.5 | MyBatis Spring 통합 | compile |
| 256 | Spring Framework | Boot Autoconfigure | MyBatis Spring Boot Autoconfigure | org.mybatis.spring.boot | mybatis-spring-boot-autoconfigure | 3.0.5 | MyBatis Spring Boot 자동구성 | compile |
| 257 | Spring Framework | Boot Starter | MyBatis Spring Boot Starter | org.mybatis.spring.boot | mybatis-spring-boot-starter | 3.0.5 | MyBatis Spring Boot 스타터 | compile |
| 258 | Utility | Object Creation | Objenesis | org.objenesis | objenesis | 3.3 | 객체 인스턴스화 라이브러리 | compile |
| 259 | Testing | Test Framework | OpenTest4J | org.opentest4j | opentest4j | 1.3.0 | 오픈 테스트 연합 | test |
| 260 | Utility | Bytecode Manipulation | ASM | org.ow2.asm | asm | 9.6 | ASM 바이트코드 조작 | compile |
| 261 | Utility | Bytecode Manipulation | ASM | org.ow2.asm | asm | 9.7 | ASM 바이트코드 조작 | compile |
| 262 | Utility | Bytecode Manipulation | ASM | org.ow2.asm | asm | 9.7.1 | ASM 바이트코드 조작 | compile |
| 263 | Utility | Bytecode Manipulation | ASM Commons | org.ow2.asm | asm-commons | 9.7 | ASM 공통 유틸리티 | compile |
| 264 | Utility | Bytecode Manipulation | ASM Tree | org.ow2.asm | asm-tree | 9.7 | ASM 트리 API | compile |
| 265 | Database | JDBC Driver | PostgreSQL | org.postgresql | postgresql | 42.7.7 | PostgreSQL JDBC 드라이버 | compile |
| 266 | Code Generation | Boilerplate Reduction | Lombok | org.projectlombok | lombok | 1.18.36 | 자바 보일러플레이트 코드 제거 | compile |
| 267 | Testing | TestContainers | Duct Tape | org.rnorth.duct-tape | duct-tape | 1.0.8 | TestContainers 유틸리티 | test |
| 268 | Testing | JSON Testing | JSON Assert | org.skyscreamer | jsonassert | 1.5.3 | JSON 어서션 라이브러리 | test |
| 269 | Logging | SLF4J | JUL to SLF4J | org.slf4j | jul-to-slf4j | 2.0.17 | JUL을 SLF4J로 브리지 | compile |
| 270 | Logging | SLF4J | SLF4J API | org.slf4j | slf4j-api | 1.7.36 | SLF4J 로깅 API | compile |
| 271 | Logging | SLF4J | SLF4J API | org.slf4j | slf4j-api | 2.0.17 | SLF4J 로깅 API | compile |
| 272 | YAML Processing | YAML Parser | SnakeYAML Engine | org.snakeyaml | snakeyaml-engine | 2.7 | YAML 파싱 엔진 | compile |
| 273 | Build Tools | Plexus | Plexus Build API | org.sonatype.plexus | plexus-build-api | 0.0.7 | Plexus 빌드 API | compile |
| 274 | API Documentation | SpringDoc | SpringDoc OpenAPI Starter Common | org.springdoc | springdoc-openapi-starter-common | 2.8.8 | SpringDoc OpenAPI 공통 스타터 | compile |
| 275 | API Documentation | SpringDoc | SpringDoc OpenAPI Starter WebMVC API | org.springdoc | springdoc-openapi-starter-webmvc-api | 2.8.8 | SpringDoc WebMVC API 스타터 | compile |
| 276 | API Documentation | SpringDoc | SpringDoc OpenAPI Starter WebMVC UI | org.springdoc | springdoc-openapi-starter-webmvc-ui | 2.8.8 | SpringDoc WebMVC UI 스타터 | compile |
| 277 | Spring Framework | Boot Framework | Spring Boot | org.springframework.boot | spring-boot | 3.5.4 | Spring Boot 코어 프레임워크 | compile |
| 278 | Spring Framework | Boot Framework | Spring Boot Actuator | org.springframework.boot | spring-boot-actuator | 3.5.4 | Spring Boot 액추에이터 | compile |
| 279 | Spring Framework | Boot Framework | Spring Boot Actuator Autoconfigure | org.springframework.boot | spring-boot-actuator-autoconfigure | 3.5.4 | Spring Boot 액추에이터 자동구성 | compile |
| 280 | Spring Framework | Boot Framework | Spring Boot Autoconfigure | org.springframework.boot | spring-boot-autoconfigure | 3.5.4 | Spring Boot 자동구성 | compile |
| 281 | Spring Framework | Boot Framework | Spring Boot Buildpack Platform | org.springframework.boot | spring-boot-buildpack-platform | 3.5.4 | Spring Boot 빌드팩 플랫폼 | compile |
| 282 | Spring Framework | Boot Framework | Spring Boot DevTools | org.springframework.boot | spring-boot-devtools | 3.5.4 | Spring Boot 개발 도구 | compile |
| 283 | Spring Framework | Boot Framework | Spring Boot Loader Tools | org.springframework.boot | spring-boot-loader-tools | 3.5.4 | Spring Boot 로더 도구 | compile |
| 284 | Spring Framework | Boot Framework | Spring Boot Maven Plugin | org.springframework.boot | spring-boot-maven-plugin | 3.5.4 | Spring Boot Maven 플러그인 | compile |
| 285 | Spring Framework | Boot Starter | Spring Boot Starter | org.springframework.boot | spring-boot-starter | 3.5.4 | Spring Boot 기본 스타터 | compile |
| 286 | Spring Framework | Boot Starter | Spring Boot Starter Actuator | org.springframework.boot | spring-boot-starter-actuator | 3.5.4 | Spring Boot 액추에이터 스타터 | compile |
| 287 | Spring Framework | Boot Starter | Spring Boot Starter Cache | org.springframework.boot | spring-boot-starter-cache | 3.5.4 | Spring Boot 캐시 스타터 | compile |
| 288 | Spring Framework | Boot Starter | Spring Boot Starter Data JPA | org.springframework.boot | spring-boot-starter-data-jpa | 3.5.4 | Spring Boot JPA 스타터 | compile |
| 289 | Spring Framework | Boot Starter | Spring Boot Starter JDBC | org.springframework.boot | spring-boot-starter-jdbc | 3.5.4 | Spring Boot JDBC 스타터 | compile |
| 290 | Spring Framework | Boot Starter | Spring Boot Starter JSON | org.springframework.boot | spring-boot-starter-json | 3.5.4 | Spring Boot JSON 스타터 | compile |
| 291 | Spring Framework | Boot Starter | Spring Boot Starter Logging | org.springframework.boot | spring-boot-starter-logging | 3.5.4 | Spring Boot 로깅 스타터 | compile |
| 292 | Spring Framework | Boot Starter | Spring Boot Starter Security | org.springframework.boot | spring-boot-starter-security | 3.5.4 | Spring Boot 보안 스타터 | compile |
| 293 | Spring Framework | Boot Starter | Spring Boot Starter Test | org.springframework.boot | spring-boot-starter-test | 3.5.4 | Spring Boot 테스트 스타터 | test |
| 294 | Spring Framework | Boot Starter | Spring Boot Starter Tomcat | org.springframework.boot | spring-boot-starter-tomcat | 3.5.4 | Spring Boot Tomcat 스타터 | compile |
| 295 | Spring Framework | Boot Starter | Spring Boot Starter Validation | org.springframework.boot | spring-boot-starter-validation | 3.5.4 | Spring Boot 검증 스타터 | compile |
| 296 | Spring Framework | Boot Starter | Spring Boot Starter Web | org.springframework.boot | spring-boot-starter-web | 3.5.4 | Spring Boot 웹 스타터 | compile |
| 297 | Spring Framework | Boot Framework | Spring Boot Test | org.springframework.boot | spring-boot-test | 3.5.4 | Spring Boot 테스트 프레임워크 | test |
| 298 | Spring Framework | Boot Framework | Spring Boot Test Autoconfigure | org.springframework.boot | spring-boot-test-autoconfigure | 3.5.4 | Spring Boot 테스트 자동구성 | test |
| 299 | Spring Framework | Cloud Framework | Spring Cloud Commons | org.springframework.cloud | spring-cloud-commons | 4.2.1 | Spring Cloud 공통 라이브러리 | compile |
| 300 | Spring Framework | Cloud Framework | Spring Cloud Context | org.springframework.cloud | spring-cloud-context | 4.2.1 | Spring Cloud 컨텍스트 | compile |
| 301 | Spring Cloud | OpenFeign | Spring Cloud OpenFeign Core | org.springframework.cloud | spring-cloud-openfeign-core | 4.2.1 | Spring Cloud OpenFeign 코어 라이브러리 | compile |
| 302 | Spring Cloud | Starter | Spring Cloud Starter | org.springframework.cloud | spring-cloud-starter | 4.2.1 | Spring Cloud 스타터 | compile |
| 303 | Spring Cloud | OpenFeign | Spring Cloud Starter OpenFeign | org.springframework.cloud | spring-cloud-starter-openfeign | 4.1.4 | Spring Cloud OpenFeign 스타터 | compile |
| 304 | Spring Data | Data Commons | Spring Data Commons | org.springframework.data | spring-data-commons | 3.5.2 | Spring Data 공통 라이브러리 | compile |
| 305 | Spring Data | JPA | Spring Data JPA | org.springframework.data | spring-data-jpa | 3.5.2 | Spring Data JPA 라이브러리 | compile |
| 306 | Spring Security | Configuration | Spring Security Config | org.springframework.security | spring-security-config | 6.5.4 | Spring Security 설정 라이브러리 | compile |
| 307 | Spring Security | Core | Spring Security Core | org.springframework.security | spring-security-core | 6.5.4 | Spring Security 코어 라이브러리 | compile |
| 308 | Spring Security | Crypto | Spring Security Crypto | org.springframework.security | spring-security-crypto | 6.4.4 | Spring Security 암호화 라이브러리 | compile |
| 309 | Spring Security | Testing | Spring Security Test | org.springframework.security | spring-security-test | 6.4.4 | Spring Security 테스트 라이브러리 | test |
| 310 | Spring Security | Web | Spring Security Web | org.springframework.security | spring-security-web | 6.4.4 | Spring Security 웹 라이브러리 | compile |
| 311 | Spring Framework | AOP | Spring AOP | org.springframework | spring-aop | 6.2.9 | Spring AOP 라이브러리 | compile |
| 312 | Spring Framework | Aspects | Spring Aspects | org.springframework | spring-aspects | 6.2.9 | Spring AspectJ 통합 라이브러리 | compile |
| 313 | Spring Framework | Beans | Spring Beans | org.springframework | spring-beans | 6.2.9 | Spring Bean 관리 라이브러리 | compile |
| 314 | Spring Framework | Context | Spring Context | org.springframework | spring-context | 6.2.9 | Spring 컨텍스트 라이브러리 | compile |
| 315 | Spring Framework | Context Support | Spring Context Support | org.springframework | spring-context-support | 6.2.9 | Spring 컨텍스트 지원 라이브러리 | compile |
| 316 | Spring Framework | Core | Spring Core | org.springframework | spring-core | 6.2.9 | Spring 코어 라이브러리 | compile |
| 317 | Spring Framework | Expression | Spring Expression | org.springframework | spring-expression | 6.2.9 | Spring 표현식 언어 라이브러리 | compile |
| 318 | Spring Framework | Logging | Spring JCL | org.springframework | spring-jcl | 6.2.9 | Spring 공통 로깅 라이브러리 | compile |
| 319 | Spring Framework | JDBC | Spring JDBC | org.springframework | spring-jdbc | 6.2.9 | Spring JDBC 라이브러리 | compile |
| 320 | Spring Framework | ORM | Spring ORM | org.springframework | spring-orm | 6.2.9 | Spring ORM 통합 라이브러리 | compile |
| 321 | Spring Framework | Testing | Spring Test | org.springframework | spring-test | 6.2.9 | Spring 테스트 라이브러리 | test |
| 322 | Spring Framework | Transaction | Spring TX | org.springframework | spring-tx | 6.2.9 | Spring 트랜잭션 라이브러리 | compile |
| 323 | Spring Framework | Web | Spring Web | org.springframework | spring-web | 6.2.9 | Spring 웹 라이브러리 | compile |
| 324 | Spring Framework | Web MVC | Spring WebMVC | org.springframework | spring-webmvc | 6.2.9 | Spring Web MVC 라이브러리 | compile |
| 325 | Testing | TestContainers | Database Commons | org.testcontainers | database-commons | 1.21.2 | TestContainers 데이터베이스 공통 라이브러리 | test |
| 326 | Testing | TestContainers | JDBC | org.testcontainers | jdbc | 1.21.2 | TestContainers JDBC 라이브러리 | test |
| 327 | Testing | TestContainers | JUnit Jupiter | org.testcontainers | junit-jupiter | 1.21.2 | TestContainers JUnit Jupiter 통합 | test |
| 328 | Testing | TestContainers | PostgreSQL | org.testcontainers | postgresql | 1.21.2 | TestContainers PostgreSQL 컨테이너 | test |
| 329 | Testing | TestContainers | TestContainers Core | org.testcontainers | testcontainers | 1.21.2 | TestContainers 코어 라이브러리 | test |
| 330 | Configuration | TOML Parser | TOML-J | org.tomlj | tomlj | 1.0.0 | TOML 파싱 라이브러리 | compile |
| 331 | Compression | XZ Compression | XZ Utils | org.tukaani | xz | 1.9 | XZ 압축 라이브러리 | compile |
| 332 | Build Tools | Dependency Analysis | JDependency | org.vafer | jdependency | 2.10 | Java 의존성 분석 도구 | compile |
| 333 | Web Assets | Swagger UI | Swagger UI WebJar | org.webjars | swagger-ui | 5.21.0 | Swagger UI 웹 자산 | compile |
| 334 | Web Assets | WebJars | WebJars Locator Lite | org.webjars | webjars-locator-lite | 1.1.0 | WebJars 위치 탐지기 | compile |
| 335 | Testing | XML Testing | XMLUnit Core | org.xmlunit | xmlunit-core | 2.10.3 | XML 단위 테스트 라이브러리 | test |
| 336 | Configuration | YAML Parser | SnakeYAML | org.yaml | snakeyaml | 2.4 | YAML 파싱 라이브러리 | compile |

## Nexus Repository 구성 권장사항

### 1. Repository 그룹화 전략
- **Maven Central Proxy**: Maven Central Repository 프록시 캐시
- **Spring Releases**: Spring 공식 릴리즈 저장소
- **Spring Milestones**: Spring 마일스톤 및 RC 버전
- **Private Hosted**: 사내 개발 아티팩트 저장소
- **Snapshots**: 개발 중인 SNAPSHOT 버전 저장소
- **Public Group**: 모든 외부 저장소를 통합한 가상 저장소

### 2. 저장소별 세부 구성

#### Central Proxy Repository
```
Repository Type: proxy
Remote Storage: https://repo1.maven.org/maven2/
Blob Store: maven-central
Version Policy: Release
Layout Policy: Strict
```

#### Spring Proxy Repository
```
Repository Type: proxy
Remote Storage: https://repo.spring.io/libs-release/
Blob Store: spring-releases
Version Policy: Release
Layout Policy: Strict
```

#### Private Hosted Repository
```
Repository Type: hosted
Version Policy: Mixed
Deployment Policy: Allow Redeploy
Blob Store: private-hosted
```

### 3. 버전 관리 정책

#### Release 버전 관리
- **안정성 우선**: 운영 환경에는 안정화된 릴리즈 버전만 배포
- **버전 충돌 해결**: 동일 라이브러리의 다중 버전은 최신 안정 버전으로 통일
- **보안 패치**: CVE 발견 시 즉시 패치된 버전으로 업그레이드

#### SNAPSHOT 버전 관리
- **개발 환경 전용**: SNAPSHOT 버전은 개발/테스트 환경에서만 사용
- **자동 정리**: 30일 이상 된 SNAPSHOT 자동 삭제 정책 적용
- **빌드 재현성**: CI/CD 파이프라인에서 SNAPSHOT 의존성 최소화

### 4. 저장소 최적화

#### Blob Store 구성
- **SSD 스토리지**: 고성능 SSD 기반 블롭 스토리지 권장
- **용량 계획**: 최소 500GB, 확장 가능한 구조
- **압축 활성화**: 스토리지 효율성을 위한 압축 설정

#### Cleanup Policies
```yaml
# SNAPSHOT 정리 정책
snapshot-cleanup:
  criteria:
    - lastBlobUpdated: 30
    - lastDownloaded: 90
  format: maven2
  
# 릴리즈 정리 정책 (선택적)
release-cleanup:
  criteria:
    - lastDownloaded: 365
  format: maven2
```

#### 성능 최적화
- **메타데이터 캐시**: 1시간 캐시 유지
- **아티팩트 캐시**: 24시간 캐시 유지
- **네거티브 캐시**: 404 오류 1시간 캐시

### 5. 보안 정책

#### 접근 제어 (RBAC)
```yaml
roles:
  - name: nexus-admin
    privileges: [nx-all]
    
  - name: developer
    privileges: 
      - nx-repository-view-maven2-*-read
      - nx-repository-view-maven2-snapshots-*
      
  - name: ci-cd
    privileges:
      - nx-repository-view-maven2-*-read
      - nx-repository-view-maven2-private-hosted-*
```

#### 인증 통합
- **LDAP/AD 연동**: 기업 디렉토리 서비스와 연동
- **SSO 지원**: SAML/OAuth2 기반 Single Sign-On
- **API 토큰**: CI/CD 시스템용 API 토큰 관리

#### 취약점 관리
- **자동 스캔**: IQ Server 연동으로 알려진 취약점 자동 검사
- **정책 시행**: 고위험 취약점 포함 아티팩트 배포 차단
- **보고서**: 주간 보안 취약점 현황 보고

### 6. 백업 및 재해복구

#### 백업 전략
- **일일 백업**: 블롭 스토리지 및 데이터베이스 일일 백업
- **증분 백업**: 변경된 데이터만 백업하여 효율성 증대
- **오프사이트 보관**: 재해복구를 위한 원격지 백업 보관

#### 모니터링
- **디스크 사용량**: 80% 초과 시 알림
- **서비스 상태**: HTTP 헬스체크 및 JVM 메트릭
- **다운로드 통계**: 인기 아티팩트 및 사용 패턴 분석

### 7. 마이그레이션 가이드

#### 기존 Repository에서 마이그레이션
1. **현재 의존성 분석**: 기존 프로젝트의 의존성 목록 추출
2. **저장소 구성**: Nexus Repository 초기 설정
3. **아티팩트 업로드**: 필수 아티팩트 사전 캐시
4. **설정 변경**: Maven settings.xml 업데이트
5. **테스트 배포**: 개발 환경에서 우선 테스트
6. **단계적 적용**: 프로젝트별 순차 마이그레이션

#### Maven Settings 예시
```xml
<settings>
  <mirrors>
    <mirror>
      <id>nexus</id>
      <mirrorOf>*</mirrorOf>
      <url>http://nexus.company.com/repository/maven-public/</url>
    </mirror>
  </mirrors>
  
  <servers>
    <server>
      <id>nexus</id>
      <username>${env.NEXUS_USERNAME}</username>
      <password>${env.NEXUS_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

---
*생성일: 2025-10-08*  
*최종 업데이트: 2025-10-08*  
*총 의존성 수: 336개*  
*문서 버전: 2.0*  
*작성자: AxPortal Backend Team*  
*검토자: Infrastructure Team*