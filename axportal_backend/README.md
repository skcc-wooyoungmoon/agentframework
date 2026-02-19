# AxPortal Backend

## 프로젝트 개요

AxPortal Backend는 **Spring Boot 3.5.4** 기반의 **AI Platform 통합 포털 RESTful API 서버**입니다.  
다양한 AI 서비스 제공자(SKTAI, Lablup, Datumo 등)와 연동하여 통합된 AI 플랫폼 서비스를 제공합니다.

### 핵심 특징
- ✅ **멀티 외부 시스템 연동**: SKTAI, Lablup, Datumo, I-ONE, Shinhan, UDP, Elasticsearch 등 7개 주요 시스템 통합
- ✅ **19개 도메인 컨트롤러**: Agent, Auth, Data, Deploy, Eval, Home, Knowledge, Model, Prompt, Resource 등
- ✅ **OAuth2 & JWT 이중 인증**: 유연한 인증 시스템과 Swagger UI 자동 토큰 관리
- ✅ **표준화된 응답 포맷**: AxResponseEntity 기반 일관된 API 응답 구조
- ✅ **다중 환경 지원**: 6개 프로필 (local, elocal, edev, dev, staging, prod) 기반 환경별 설정
- ✅ **배치 및 스케줄링**: Spring Scheduler 기반 정기 작업 처리

## 기술 스택

### 핵심 프레임워크
- **Spring Boot**: 3.5.4
- **Spring Cloud**: 2025.0.0
- **Spring Security**: 6.5.4
- **Java**: 17
- **Maven**: 3.9.9 (Wrapper 사용)

### 주요 라이브러리
- **ORM/데이터베이스**
  - Spring Data JPA 3.5.4 - JPA 기반 데이터 액세스
  - MyBatis 3.0.4 - SQL 매핑 프레임워크
  - Hibernate 6.2.22.Final - JPA 구현체
  - HikariCP 6.3.1 - 고성능 커넥션 풀

- **외부 API 연동**
  - Spring Cloud OpenFeign 4.2.0 - 선언적 HTTP 클라이언트
  - Feign Form 13.5 - Multipart 지원
  - Feign OkHttp 13.6 - PATCH 메서드 지원

- **보안 & 인증**
  - Spring Security 6.5.4 - 보안 프레임워크
  - JWT (jjwt 0.12.6) - JSON Web Token
  - BouncyCastle 1.78.1 - 암호화 라이브러리

- **문서화 & 검증**
  - SpringDoc OpenAPI 2.8.8 - API 자동 문서화
  - Jakarta Validation - Bean 검증
  - Lombok 1.18.36 - 보일러플레이트 코드 제거
  - MapStruct 1.6.3 - 객체 매핑

- **인프라 & 모니터링**
  - Spring Boot Actuator - 애플리케이션 모니터링
  - Caffeine 3.1.8 - 고성능 캐시
  - Kubernetes Client 7.3.1 - K8s 연동
  - AWS S3 SDK 2.36.2 - S3 스토리지 연동

- **유틸리티**
  - Apache POI 5.4.1 - Excel 파일 생성
  - AspectJ 1.9.22 - AOP 지원
  - Spring Boot DevTools - 개발 생산성 향상

### 데이터베이스
- **PostgreSQL 42.7.7** - 외부 로컬/외부 개발환경 (elocal, edev)
- **Tibero 8.0.11** - 로컬/개발/스테이징/운영환경 (local, dev, staging, prod)

### 외부 시스템 연동 (7개 주요 시스템)
- **SKTAI Platform** - AI 모델, 에이전트, 데이터, 지식, 평가 등 19개 서브모듈
- **Lablup** - Backend.AI 플랫폼 연동
- **Datumo** - AI 평가 시스템 연동
- **I-ONE** - 사내 시스템 연동
- **Shinhan Bank** - 금융 시스템 연동
- **UDP** - 통합 데이터 플랫폼
- **Elasticsearch** - 검색 엔진

## 주요 기능

### 1. 멀티 외부 시스템 통합 연동

#### SKTAI Platform (19개 서브모듈)
프로젝트는 SKTAI AI Platform의 다양한 서비스와 연동하여 통합 AI 플랫폼 서비스를 제공합니다.

| 서브모듈 | 경로 | 주요 기능 |
|---------|------|----------|
| **Agent** | `/client/sktai/agent` | AI 에이전트 생성, 관리, 배포 |
| **Agent Gateway** | `/client/sktai/agentgateway` | 에이전트 추론 및 배치 처리 |
| **Auth** | `/client/sktai/auth` | 사용자 인증 및 권한 관리 |
| **Data** | `/client/sktai/data` | 데이터셋 관리 및 처리 |
| **Evaluation** | `/client/sktai/evaluation` | 모델 평가 및 성능 측정 |
| **External Knowledge** | `/client/sktai/externalKnowledge` | 외부 지식 연동 |
| **Fine-tuning** | `/client/sktai/finetuning` | 모델 파인튜닝 |
| **History** | `/client/sktai/history` | 사용 이력 조회 및 통계 |
| **Knowledge** | `/client/sktai/knowledge` | 지식베이스 관리 |
| **Lineage** | `/client/sktai/lineage` | 데이터 계보 관리 |
| **MCP** | `/client/sktai/mcp` | MCP 프로토콜 지원 |
| **Model** | `/client/sktai/model` | AI 모델 관리 |
| **Model Gateway** | `/client/sktai/modelgateway` | 모델 추론 게이트웨이 |
| **Resource** | `/client/sktai/resource` | 리소스 관리 및 스케일링 |
| **Resource Management** | `/client/sktai/resrcMgmt` | Prometheus 기반 자원 모니터링 |
| **Safety Filter** | `/client/sktai/safetyfilter` | 콘텐츠 안전 필터링 |
| **Serving** | `/client/sktai/serving` | 모델 서빙 관리 |

#### 기타 외부 시스템 연동 (6개)
- **Lablup** (`/client/lablup`) - Backend.AI 플랫폼, 배치 아티팩트 스캔
- **Datumo** (`/client/datumo`) - AI 평가 시스템
- **I-ONE** (`/client/ione`) - 사내 통합 시스템
- **Shinhan Bank** (`/client/shinhan`) - 금융 시스템 연동
- **UDP** (`/client/udp`) - 통합 데이터 플랫폼
- **Elasticsearch** (`/client/elastic`) - 검색 엔진 연동

### 2. 19개 도메인별 REST API 컨트롤러

| 도메인 | 경로 | 주요 기능 |
|--------|------|----------|
| **Admin** | `/controller/admin` | 관리자 기능 (프로젝트, 사용자 관리) |
| **Agent** | `/controller/agent` | 에이전트 관리 |
| **Auth** | `/controller/auth` | 인증/로그인/토큰 관리 |
| **Common** | `/controller/common` | 공통 기능 |
| **Data** | `/controller/data` | 데이터 관리 |
| **Deploy** | `/controller/deploy` | 에이전트/API 게이트웨이 배포 |
| **Elastic** | `/controller/elastic` | Elasticsearch 연동 |
| **Eval** | `/controller/eval` | 평가 관리 |
| **Home** | `/controller/home` | 홈/IDE 관리 |
| **Knowledge** | `/controller/knowledge` | 지식 관리 |
| **Lineage** | `/controller/lineage` | 데이터 계보 |
| **Log** | `/controller/log` | 로그 관리 |
| **Model** | `/controller/model` | 모델 관리 |
| **Notice** | `/controller/notice` | 공지사항 |
| **Prompt** | `/controller/prompt` | 프롬프트/워크플로우 관리 |
| **Resource** | `/controller/resource` | 리소스 관리 |
| **Sample** | `/controller/sample` | 샘플/테스트 컨트롤러 |

### 3. 보안 및 인증 시스템

#### OAuth2 & JWT 이중 인증
- **OAuth2 Password Flow**: 자동 토큰 획득 및 관리
- **HTTP Bearer Token (JWT)**: 수동 토큰 입력 방식
- **Swagger UI 통합**: 두 인증 방식 모두 지원
- **인증 테스트 API**: 8개 전용 엔드포인트로 인증 방식 테스트

#### Spring Security 설정
- **Role 기반 접근 제어**: USER, ADMIN 등 역할별 권한 관리
- **CORS 다중 도메인 지원**: 유연한 클라이언트 연동
- **JWT 토큰 검증**: 만료 시간, 서명 검증
- **비밀번호 암호화**: BCrypt 기반 안전한 비밀번호 저장

### 4. API 문서화 시스템 (SpringDoc OpenAPI 3)

#### Swagger UI 특징
- **자동 문서 생성**: 컨트롤러 어노테이션 기반 실시간 업데이트
- **대화형 API 테스트**: Try it out 기능으로 즉시 테스트
- **OAuth2 자동 인증**: Username/Password로 토큰 자동 획득
- **이중 인증 지원**: OAuth2PasswordBearer + HTTPBearer 동시 지원
- **Client Credentials in Body**: Request Body 방식 OAuth2 인증

#### OpenAPI 설정
- **글로벌 보안 스키마**: 모든 API에 자동 적용
- **스코프 정의**: read, write, admin 권한 관리
- **서버 정보**: 환경별 API 서버 URL 자동 설정

### 5. 예외 처리 및 응답 표준화

#### ErrorCode 기반 통합 예외 관리
- **계층화된 예외 구조**: CustomException → BusinessException, ValidationException
- **HTTP 상태 + 에러 코드**: 표준화된 에러 응답
- **상세 에러 정보**: 기본 메시지 + 커스텀 detail 메시지
- **GlobalExceptionHandler**: 통합 예외 처리 및 일관된 응답

#### AxResponseEntity 표준 응답
- **통합 응답 포맷**: success, message, data, error, timestamp 구조
- **페이징 지원**: PageResponse를 통한 효율적 대용량 데이터 처리
- **HTTP 상태 코드 자동 설정**: 성공/실패에 따른 자동 코드 매핑
- **개발자 친화적**: 명확한 에러 메시지와 디버깅 정보

### 6. 배치 및 스케줄링 시스템

#### Spring Scheduler 배치
- **IDE 정리 배치**: 주기적 IDE 리소스 정리
- **앱 기동 직후 실행**: ApplicationReadyEvent 기반 초기 실행
- **Cron 표현식**: 환경별 스케줄 설정

#### SKTAI Agent Gateway 배치 처리
- **배치 추론**: 대량 데이터 일괄 처리
- **설정 관리**: 배치 옵션 및 파라미터 지원

#### Lablup 배치 아티팩트 스캔
- **모델 스캔**: 배치 단위 아티팩트 모델 검색
- **배치 옵션**: 유연한 스캔 설정

#### 리소스 스케일링 (SKTAI Resource)
- **자동 스케일링**: CPU/메모리 임계값 기반 자동 확장/축소
- **수동 스케일링**: Scale Out/In, Scale Up/Down 지원
- **스케일링 정책**: 자동 스케일링 정책 설정/해제

### 7. 다중 환경 지원

#### 6개 프로필 환경
- **elocal** (기본): PostgreSQL, 외부 로컬, DevTools 활성화
- **local**: Tibero, 내부 로컬
- **edev**: PostgreSQL, 외부 개발
- **dev**: Tibero, 내부 개발
- **staging**: Tibero, 스테이징
- **prod**: Tibero, 운영

#### 환경별 설정 분리
- 데이터베이스 연결 정보
- 외부 API Base URL 및 인증 정보
- 로깅 레벨 및 출력 형식
- 캐시 및 보안 설정

### 8. DB 모니터링을 위한 SQL Comment 자동 삽입 시스템

#### 핵심 목표
모든 JPA/JDBC 쿼리에 자동으로 `/* ServiceImpl.method.Repository.method */` 형태의 주석을 추가하여 DB 모니터링 툴에서 SQL 출처 추적 가능

#### 시스템 아키텍처
```
Service Layer (AOP) 
    ↓ SQL 컨텍스트 설정
Repository Layer (AOP)
    ↓ Repository 정보 추가
SQL 실행 단계
    ├── SELECT: Hibernate StatementInspector
    └── DML: JDBC Connection Proxy → 실제 DB 전송
```

#### 핵심 컴포넌트

##### 1. SQL 컨텍스트 관리 (`SqlCommentContext`)
- **ThreadLocal 기반**: 스레드별 독립적 컨텍스트 관리
- **트랜잭션 동기화**: 트랜잭션 커밋 시점까지 컨텍스트 유지 (JPA Write Behind 지원)
- **주석 형식**: `ServiceImpl.method.Repository.method`
- **Thread-safe**: 멀티스레드 환경 안전

##### 2. AOP 인터셉터
- **Service AOP** (`ServiceSqlCommentAspect`): @Service 어노테이션 대상, Service 메서드 정보 수집
- **Repository AOP** (`RepositorySqlCommentAspect`): Spring Data Repository 대상, Repository 메서드 정보 수집
- **자동 컨텍스트 관리**: 메서드 시작/종료 시점에 자동 설정/정리

##### 3. Hibernate StatementInspector (`SqlCommentInterceptor`)
- **SELECT 쿼리 처리**: Hibernate 로그 출력용 주석 추가
- **자동 등록**: `JpaSqlCommentConfig`를 통한 Hibernate 속성 커스터마이징
- **중복 방지**: 기존 주석 제거 후 재추가

##### 4. JDBC Connection Proxy (`JdbcConnectionProxy`)
- **DML 쿼리 처리**: UPDATE/INSERT/DELETE 쿼리에 주석 강제 추가
- **실제 DB 전송**: PreparedStatement 생성 시점에 SQL 주석 삽입
- **Dynamic Proxy**: DataSource, Connection, PreparedStatement 래핑

#### 주석 예시

```sql
-- SELECT 쿼리
/* AuthServiceImpl.login.UserRepository.findByUsername */ 
SELECT u.user_id, u.username FROM users u WHERE u.username = ?

-- UPDATE 쿼리
/* AuthServiceImpl.login.UserRepository.save */ 
UPDATE users SET last_login = ? WHERE user_id = ?

-- INSERT 쿼리
/* UserServiceImpl.createUser.UserRepository.save */ 
INSERT INTO users (username, email, created_at) VALUES (?, ?, ?)

-- DELETE 쿼리
/* UserServiceImpl.deleteUser.UserRepository.deleteById */ 
DELETE FROM users WHERE user_id = ?
```

#### 적용 범위
- **개발 환경**: elocal, local, edev 프로필에서 활성화
- **운영 환경**: 성능 최적화를 위해 비활성화
- **모든 SQL 타입**: SELECT, UPDATE, INSERT, DELETE 100% 적용

#### 주요 특징
- ✅ **완벽한 호출 추적**: Service → Repository 경로 완벽 표시
- ✅ **DB 모니터링 도구 지원**: 실제 DB 서버에 주석 전송
- ✅ **성능 영향 최소화**: 개발 환경에서만 활성화
- ✅ **자동화**: 코드 수정 없이 AOP로 자동 적용
- ✅ **트랜잭션 안전**: Write Behind 등 지연 쓰기 완벽 지원

#### 로그 출력 예시
```
INFO  SqlCommentInterceptor : ✅ SQL 주석 교체 완료 - 타입: SELECT, 주석: AuthServiceImpl.login.UserRepository.findByUsername
INFO  JdbcConnectionProxy   : 🔄 DML 쿼리 JDBC 재확인 - 타입: UPDATE, 주석: AuthServiceImpl.login.UserRepository.save
INFO  JdbcConnectionProxy   : ✅ JDBC DML SQL 주석 강제 추가 완료 - 타입: UPDATE, 길이: 120 -> 180
```

#### 관련 문서
- 상세 구현 가이드: [`docs/이병관/SQLCOMMENT.md`](docs/이병관/SQLCOMMENT.md)

## 프로젝트 구조

```
src/main/java/com/skax/aiplatform/
├── AxportalBackendApplication.java     # 메인 애플리케이션 클래스
├── UserDataInitializer.java            # 초기 사용자 데이터 로딩
├── batch/                               # 배치 작업
│   └── IdeDeleteBatch.java             # IDE 정리 배치 (@Scheduled)
├── client/                              # 외부 API Feign Client (7개 시스템)
│   ├── sktai/                          # SKTAI 플랫폼 (19개 서브모듈)
│   │   ├── agent/                      # 에이전트 관리
│   │   ├── agentgateway/              # 에이전트 게이트웨이 (배치 추론)
│   │   ├── auth/                       # 인증 관리
│   │   ├── data/                       # 데이터 관리
│   │   ├── evaluation/                 # 평가 관리
│   │   ├── externalKnowledge/         # 외부 지식 연동
│   │   ├── finetuning/                # 파인튜닝
│   │   ├── history/                    # 이력 관리
│   │   ├── knowledge/                  # 지식베이스
│   │   ├── lineage/                    # 데이터 계보
│   │   ├── mcp/                        # MCP 프로토콜
│   │   ├── model/                      # 모델 관리
│   │   ├── modelgateway/              # 모델 게이트웨이
│   │   ├── resource/                   # 리소스 관리 (스케일링)
│   │   ├── resrcMgmt/                 # 자원 모니터링 (Prometheus)
│   │   ├── safetyfilter/              # 안전 필터
│   │   ├── serving/                    # 모델 서빙
│   │   ├── common/                     # 공통 DTO
│   │   ├── config/                     # Feign 설정
│   │   └── intercept/                  # Feign 인터셉터
│   ├── lablup/                         # Lablup 연동 (Backend.AI)
│   ├── datumo/                         # Datumo 연동 (AI 평가)
│   ├── ione/                           # I-ONE 시스템 연동
│   ├── shinhan/                        # 신한은행 연동
│   ├── udp/                            # UDP 연동
│   ├── elastic/                        # Elasticsearch 연동
│   └── deepsecurity/                   # Deep Security 연동
├── controller/                         # REST 컨트롤러 (19개 도메인)
│   ├── admin/                          # 관리자 기능
│   ├── agent/                          # 에이전트 관리
│   ├── auth/                           # 인증/로그인/토큰
│   ├── common/                         # 공통 기능
│   ├── data/                           # 데이터 관리
│   ├── deploy/                         # 배포 관리
│   ├── elastic/                        # Elasticsearch
│   ├── eval/                           # 평가 관리
│   ├── home/                           # 홈/IDE
│   ├── knowledge/                      # 지식 관리
│   ├── lineage/                        # 데이터 계보
│   ├── log/                            # 로그 관리
│   ├── model/                          # 모델 관리
│   ├── notice/                         # 공지사항
│   ├── prompt/                         # 프롬프트/워크플로우
│   ├── resource/                       # 리소스 관리
│   ├── sample/                         # 샘플/테스트
│   ├── HealthController.java          # 헬스 체크
│   └── CorsTestController.java        # CORS 테스트
├── service/                            # 비즈니스 로직
│   ├── [도메인별 서비스 및 impl 하위 디렉토리]
│   └── common/                         # 공통 서비스
│       └── ApprovalAlarmAspect.java   # 승인 알람 AOP
├── repository/                         # 데이터 액세스
│   └── [도메인별 JPA Repository]
├── entity/                             # JPA 엔티티
│   ├── BaseEntity.java                # 공통 엔티티 (생성/수정 정보)
│   └── [도메인별 엔티티]
├── dto/                                # 데이터 전송 객체
│   ├── [도메인]/request/              # Request DTO (Req 접미사)
│   ├── [도메인]/response/             # Response DTO (Res 접미사)
│   └── [도메인]/                      # 기타 DTO
├── mapper/                             # MapStruct 매퍼
│   └── [도메인별 매퍼 인터페이스]
├── config/                             # 설정 클래스
│   ├── SecurityConfig.java            # Spring Security 설정
│   ├── OpenApiConfig.java             # OpenAPI/Swagger 설정
│   ├── JpaConfig.java                 # JPA 설정
│   ├── WebConfig.java                 # Web MVC & CORS 설정
│   ├── AopConfig.java                 # AOP 설정
│   ├── CacheConfig.java               # 캐시 설정
│   └── [기타 설정 클래스]
├── common/                             # 공통 기능
│   ├── constant/                       # 상수 정의
│   │   └── Constants.java             # 애플리케이션 상수
│   ├── exception/                      # 예외 처리
│   │   ├── ErrorCode.java             # 에러 코드 Enum
│   │   ├── CustomException.java       # 기본 예외 클래스
│   │   ├── BusinessException.java     # 비즈니스 예외
│   │   ├── ValidationException.java   # 검증 예외
│   │   └── GlobalExceptionHandler.java # 전역 예외 처리
│   ├── response/                       # 응답 포맷
│   │   ├── AxResponse.java            # 표준 응답 래퍼
│   │   ├── AxResponseEntity.java      # ResponseEntity + AxResponse 통합
│   │   └── PageResponse.java          # 페이징 응답 래퍼
│   ├── security/                       # 보안 관련
│   │   └── [JWT 필터, 인증 핸들러 등]
│   ├── sql/                            # SQL 주석 자동 삽입 시스템 🆕
│   │   ├── SqlCommentContext.java     # ThreadLocal 기반 컨텍스트 관리
│   │   ├── aspect/                     # AOP 인터셉터
│   │   │   ├── ServiceSqlCommentAspect.java      # Service AOP
│   │   │   └── RepositorySqlCommentAspect.java   # Repository AOP
│   │   ├── interceptor/                # SQL 인터셉터
│   │   │   ├── SqlCommentInterceptor.java        # Hibernate StatementInspector
│   │   │   ├── JdbcConnectionProxy.java          # JDBC Connection Proxy
│   │   │   └── RepositorySaveInterceptor.java    # Repository 저장 인터셉터
│   │   ├── listener/                   # JPA 리스너
│   │   │   └── EntitySqlListener.java            # 엔티티 SQL 리스너
│   │   └── config/                     # SQL 주석 설정
│   │       └── JpaSqlCommentConfig.java          # JPA SQL 주석 설정
│   └── util/                           # 유틸리티
│       └── [각종 유틸리티 클래스]
└── enums/                              # Enum 클래스
    └── [도메인별 Enum]

src/main/resources/
├── application.yml                     # 공통 설정
├── application-elocal.yml             # 외부Local 프로필 (기본)
├── application-local.yml              # Local 프로필
├── application-edev.yml               # 외부개발 프로필
├── application-dev.yml                # 개발 프로필
├── application-staging.yml            # 스테이징 프로필 (미존재)
├── application-prod.yml               # 운영 프로필
├── logback-spring.xml                 # 로깅 설정
├── messages_ko.properties             # 한글 메시지
├── data.sql                           # 초기 데이터 SQL
├── keys/                              # JWT 키 파일
│   ├── private_key.pem
│   └── public_key.pem
├── ssl/                               # SSL 인증서
├── mappers/                           # MyBatis 매퍼 XML
│   ├── common/
│   └── home/
└── libs/                              # 외부 라이브러리

.vscode/                               # VS Code 설정
├── launch.json                        # Run/Debug 설정 (elocal 기본)
├── tasks.json                         # Maven 작업 설정
├── settings.json                      # 워크스페이스 설정
└── launch.properties                  # 런타임 속성

docker/                                # Docker 관련
├── postgres/
│   └── init.sql                      # PostgreSQL 초기화 스크립트

k8s/                                   # Kubernetes 배포 매니페스트
├── 01-namespace.yaml
├── 02-deployment.yaml
├── 03-service.yaml
├── 04-ingressclass.yaml
├── 05-ingress.yaml
├── 06-configmap.yaml
├── 07-secret.yaml
└── 08-pvc.yaml

docs/                                  # 프로젝트 문서
├── AI.md                             # AI 관련 문서
├── 개발표준.md                        # 개발 표준
├── 명명규칙.md                        # 명명 규칙
├── 아키텍처정의서.md                  # 아키텍처 문서
├── 에러처리_개발가이드.md             # 에러 처리 가이드
└── 이병관/                            # 개발자별 문서
    ├── JWT_AUTHENTICATION.md
    ├── LOGGING.md
    ├── MULTIPART_IMPLEMENTATION.md
    └── [기타 기술 문서]
```

## Java 소스 코드 통계

아래는 프로젝트의 Java 소스 코드를 1-depth 디렉토리 기준으로 분석한 통계입니다.

| 디렉토리 | 주요 구현 내용 및 특징 | 파일 수 | 순수 코드 라인 수 | 주석 라인 수 |
|---------|-------------------|--------|---------------|------------|
| **batch** | Spring @Scheduled 기반 IDE 정리 배치 작업 | 1 | 35 | 4 |
| **client** | 7개 외부 시스템 Feign Client 통합 (SKTAI 19개 서브모듈 포함) | 827 | 약 78,900 | 약 10,500 |
| **common** | 공통 기능 (예외, 응답, 보안, SQL 주석 AOP, 유틸) | 41 | 약 3,900 | 약 520 |
| **config** | Spring 설정 (Security, JPA, OpenAPI, AOP, Cache 등) | 11 | 약 1,050 | 약 140 |
| **controller** | 19개 도메인 REST API 컨트롤러 (Admin, Agent, Auth 등) | 57 | 약 5,400 | 약 720 |
| **dto** | Request/Response DTO (도메인별 request/response 디렉토리 구조) | 331 | 약 31,600 | 약 4,200 |
| **entity** | JPA 엔티티 클래스 (BaseEntity 상속 구조) | 45 | 약 4,300 | 약 570 |
| **enums** | 도메인별 Enum 클래스 (상태, 타입, 코드 등) | 1 | 약 95 | 약 13 |
| **mapper** | MapStruct 기반 객체 매핑 인터페이스 | 30 | 약 2,850 | 약 380 |
| **repository** | Spring Data JPA Repository 인터페이스 | 30 | 약 2,850 | 약 380 |
| **service** | 비즈니스 로직 (도메인별 Service와 impl 구조) | 70 | 약 6,700 | 약 890 |
| **root** | 메인 애플리케이션 클래스 및 초기화 클래스 | 2 | 약 190 | 약 25 |
| **전체** | **총계** | **1,446** | **약 138,000** | **약 18,400** |

### 주요 특징
- ✅ **client 디렉토리 비중 최대**: 전체 파일의 57%를 차지하며, 외부 시스템 통합의 복잡도를 반영
- ✅ **DTO 계층 분리**: Request는 `request/` 디렉토리에 `Req` 접미사, Response는 `response/` 디렉토리에 `Res` 접미사 사용
- ✅ **주석 비율**: 전체 코드의 약 13%가 JavaDoc 및 OpenAPI 문서화 주석
- ✅ **Service/Repository 구조**: Service는 impl 하위 디렉토리로 구현체 분리, Repository는 JPA 인터페이스
- ✅ **AOP 기반 SQL 주석**: common/sql 디렉토리에서 DB 모니터링을 위한 SQL 주석 자동 삽입 구현

> **참고**: 라인 수는 공백 라인을 포함한 물리적 라인 수이며, 전체 프로젝트 총 라인 수는 약 200,690줄입니다.

## 환경별 프로필

### 1. elocal (외부 로컬) ⭐ **기본 환경**
- **데이터베이스**: PostgreSQL (AWS RDS)
- **포트**: 8080
- **DevTools**: 활성화 (JMX 비활성화로 충돌 방지)
- **로깅**: DEBUG 레벨
- **보안**: 완화된 설정
- **캐시**: 비활성화
- **용도**: 외부 로컬 개발 및 테스트 (기본 프로필)
- **특징**: Spring Boot DevTools로 자동 재시작 및 라이브 리로드 지원

### 2. local (내부 로컬)
- **데이터베이스**: Tibero
- **포트**: 8080
- **용도**: 내부 로컬 테스트 환경

### 3. edev (외부 개발)
- **데이터베이스**: PostgreSQL
- **연결**: jdbc:postgresql://localhost:5432/axportal_edev
- **포트**: 8080
- **용도**: 외부 개발 환경
- **특징**: Docker Compose 활용 가능

### 4. dev (내부 개발)
- **데이터베이스**: Tibero
- **포트**: 8080
- **용도**: 내부 개발 환경

### 5. staging (스테이징)
- **데이터베이스**: Tibero
- **로깅**: INFO 레벨
- **보안**: 운영과 동일
- **캐시**: 활성화
- **용도**: 운영 배포 전 테스트 환경

### 6. prod (운영)
- **데이터베이스**: Tibero
- **로깅**: WARN 레벨
- **보안**: 강화된 설정
- **캐시**: 활성화
- **모니터링**: 전체 활성화
- **용도**: 실제 운영 환경

## 실행 방법

### 1. Maven Wrapper를 사용한 개발 환경 실행

**Windows:**
```cmd
# 프로젝트 컴파일
.\mvnw.cmd clean compile

# 테스트 실행
.\mvnw.cmd test

# 패키지 빌드
.\mvnw.cmd clean package

# Spring Boot 실행 (외부 로컬 환경 - 기본)
.\mvnw.cmd spring-boot:run

# 특정 프로필로 실행
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=elocal
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=edev
```

**Linux/Mac:**
```bash
# 실행 권한 부여 (최초 1회)
chmod +x mvnw

# 프로젝트 컴파일
./mvnw clean compile

# 테스트 실행
./mvnw test

# 패키지 빌드
./mvnw clean package

# Spring Boot 실행 (외부 로컬 환경 - 기본)
./mvnw spring-boot:run

# 특정 프로필로 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=elocal
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
./mvnw spring-boot:run -Dspring-boot.run.profiles=edev
```

### 2. 편의 스크립트 사용

**Windows:**
```cmd
# 대화형 실행 스크립트
run.bat
```

**Linux/Mac:**
```bash
# 실행 권한 부여 (최초 1회)
chmod +x run.sh

# 대화형 실행 스크립트
./run.sh
```

### 3. JAR 파일 직접 실행

```bash
# JAR 파일 빌드
.\mvnw.cmd clean package  # Windows
./mvnw clean package     # Linux/Mac

# JAR 파일로 실행
java -jar target/aiplatform-1.0.0.jar

# 프로필별 실행
java -jar target/aiplatform-1.0.0.jar --spring.profiles.active=elocal
java -jar target/aiplatform-1.0.0.jar --spring.profiles.active=local
java -jar target/aiplatform-1.0.0.jar --spring.profiles.active=edev
java -jar target/aiplatform-1.0.0.jar --spring.profiles.active=prod
```

### 4. VS Code 작업(Task) 사용

VS Code에서 `Ctrl+Shift+P` → `Tasks: Run Task` 선택 후:
- **Spring Boot: Run (External Local) - Default** - 기본 외부 로컬 환경 실행 ⭐
- **Maven: Clean Compile** - 프로젝트 컴파일
- **Maven: Clean Package** - JAR 파일 빌드  
- **Maven: Test** - 테스트 실행
- **Spring Boot: Run (Local)** - 로컬 환경으로 실행
- **Spring Boot: Run (External Local)** - 외부 로컬 환경으로 실행
- **Spring Boot: Run (External Dev)** - 외부 개발 환경으로 실행

### 5. VS Code Run/Debug 사용

VS Code에서 `F5` 키를 누르거나 Run/Debug 패널에서:
- **Spring Boot App (External Local)** - 외부 로컬 환경 디버그 ⭐ (기본)
- **Spring Boot App (Local)** - 로컬 환경 디버그
- **Spring Boot App (External Dev)** - 외부 개발 환경 디버그  
- **Debug HealthController** - HealthController 디버그용

> 📌 **기본 설정**: Run 및 Debug 실행 시 **외부Local환경(elocal)**이 기본으로 설정되어 있습니다.

## API 문서

### Swagger UI
- **로컬 (elocal)**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### 🔐 OAuth2 & JWT 이중 인증 시스템

AxPortal Backend API는 **OAuth2PasswordBearer**와 **HTTPBearer** 두 가지 인증 방식을 지원합니다. Swagger UI에서 두 방식 모두 테스트할 수 있습니다.

#### 🔑 인증 방식 비교

| 특징 | OAuth2PasswordBearer | HTTPBearer (JWT) |
|------|---------------------|------------------|
| **인증 타입** | OAuth2 Password Flow | HTTP Bearer Token |
| **토큰 획득** | 자동 (Username/Password) | 수동 (로그인 API 호출) |
| **사용 편의성** | ⭐⭐⭐⭐⭐ 매우 쉬움 | ⭐⭐⭐ 보통 |
| **토큰 관리** | 자동 처리 | 수동 관리 |
| **적합한 상황** | 빠른 개발 테스트 | 정확한 토큰 테스트 |

#### 🚀 OAuth2PasswordBearer 사용법 (권장)

1. **Swagger UI 접속**: http://localhost:8080/swagger-ui.html
2. **Authorize 클릭**: 상단의 🔓 **Authorize** 버튼 클릭
3. **OAuth2 인증**: 
   ```
   Username: admin
   Password: aisnb
   ```
   또는 다른 유효한 사용자 계정
4. **자동 토큰 획득**: Authorize 버튼으로 자동 인증 완료
5. **API 테스트**: 🔐 표시된 OAuth2 보호 API들 테스트

#### 🔧 HTTPBearer (JWT) 사용법

1. **JWT 토큰 획득**: 먼저 로그인 API 호출
   ```bash
   POST /auth/login
   {
     "username": "admin",
     "password": "aisnb"
   }
   ```

2. **Swagger UI에서 인증 설정**:
   - **Authorize 클릭**: 상단의 🔓 **Authorize** 버튼 클릭
   - **HTTPBearer 섹션**: JWT 토큰 입력
   - ⚠️ **주의**: `Bearer ` 접두사는 제외하고 토큰만 입력
   - ✅ **올바른 예시**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
   - ❌ **잘못된 예시**: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

3. **Authorize 클릭**: 인증 완료
4. **API 테스트**: 🔑 표시된 JWT 보호 API들 테스트

#### 🧪 인증 테스트 전용 API

두 인증 방식을 테스트하기 위한 전용 API들이 제공됩니다:

| 엔드포인트 | 인증 방식 | 설명 |
|-----------|----------|------|
| `GET /auth/test/public` | ❌ 없음 | 공개 API (인증 불필요) |
| `GET /auth/test/oauth2-protected` | 🔐 OAuth2 전용 | OAuth2PasswordBearer만 지원 |
| `GET /auth/test/jwt-protected` | 🔑 JWT 전용 | HTTPBearer만 지원 |
| `GET /auth/test/dual-auth` | 🔐🔑 이중 지원 | 두 방식 모두 지원 |
| `GET /auth/test/user-info` | 🔐🔑 이중 지원 | 사용자 정보 조회 |
| `GET /auth/test/admin` | 🔐🔑 이중 지원 | 관리자 권한 필요 |
| `GET /auth/test/token-info` | 🔐🔑 이중 지원 | 토큰 정보 분석 |
| `GET /auth/test/auth-comparison` | ❌ 없음 | 인증 방식 비교 도움말 |

#### 🔍 인증 상태 확인

- **🔒 닫힌 자물쇠**: OAuth2 인증 완료
- **🔓 열린 자물쇠**: OAuth2 인증 필요
- **🔑 키 아이콘**: JWT 인증 완료
- **🔐 자물쇠 + 키**: 이중 인증 지원

#### 💡 문제 해결 가이드

**토큰 만료 시:**
1. OAuth2: 자동 갱신 (재인증 불필요)
2. JWT: Refresh Token 사용 (`POST /auth/refresh`) 또는 재로그인

**인증 실패 시:**
1. 토큰 형식 확인 (Bearer 접두사 제외)
2. 토큰 만료 시간 확인
3. 적절한 권한(Role) 보유 확인

### 주요 엔드포인트

#### 시스템 API
- `GET /health` - 헬스 체크
- `GET /info` - 시스템 정보
- `GET /actuator/health` - Spring Actuator 헬스 체크

#### 인증 API (`/auth`)
- `POST /auth/login` - 로그인 (JSON/Form-data 듀얼 지원)
- `POST /auth/refresh` - 토큰 갱신
- `GET /auth/users` - 사용자 관리
- `GET /auth/test/*` - 인증 테스트 API (8개)

#### 도메인별 주요 API
- **Admin** (`/admin`) - 관리자 기능 (프로젝트, 사용자 관리)
- **Agent** (`/agent`) - 에이전트 관리
- **Data** (`/data`) - 데이터 관리
- **Deploy** (`/agentDeploy`, `/api-gw`) - 배포 관리
- **Eval** (`/eval`) - 평가 관리
- **Home** (`/home`) - 홈/IDE 관리
- **Knowledge** (`/knowledge`) - 지식 관리
- **Model** (`/model`) - 모델 관리
- **Notice** (`/notices`) - 공지사항
- **Prompt** (`/workflow`, `/prompt`) - 프롬프트/워크플로우
- **Resource** (`/resources`) - 리소스 관리
- **Sample** (`/samples`, `/api/sample/*`) - 샘플/테스트

## SKTAI 외부 API 연동 현황

### 전체 구현 현황 (19개 서브모듈)

| 서브모듈 | 클라이언트 | 서비스 | DTO | 상태 |
|---------|----------|--------|-----|------|
| **Agent** | ✅ | ✅ | ✅ | 완료 |
| **Agent Gateway** | ✅ | ✅ | ✅ | 완료 |
| **Auth** | ✅ | ✅ | ✅ | 완료 |
| **Data** | ✅ | ✅ | ✅ | 완료 |
| **Evaluation** | ✅ | ✅ | ✅ | 완료 |
| **External Knowledge** | ✅ | ✅ | ✅ | 완료 |
| **Fine-tuning** | ✅ | ✅ | ✅ | 완료 |
| **History** | ✅ | ✅ | ✅ | 완료 |
| **Knowledge** | ✅ | ✅ | ✅ | 완료 |
| **Lineage** | ✅ | ✅ | ✅ | 완료 |
| **MCP** | ✅ | ✅ | ✅ | 완료 |
| **Model** | ✅ | ✅ | ✅ | 완료 |
| **Model Gateway** | ✅ | ✅ | ✅ | 완료 |
| **Resource** | ✅ | ✅ | ✅ | 완료 |
| **Resource Management** | ✅ | ✅ | ✅ | 완료 |
| **Safety Filter** | ✅ | ✅ | ✅ | 완료 |
| **Serving** | ✅ | ✅ | ✅ | 완료 |

### 주요 구현 내용

#### 1. History API (완전 구현)
**Feign Client**: `HistoryManagementClient`

**주요 기능:**
- 모델 사용 히스토리 목록 조회 (`GET /api/v1/history/model/list`)
- 모델 통계 조회 - 테스트 환경 (`GET /api/v1/history/model/stats/test`)
- 모델 통계 조회 - 운영 환경 (`GET /api/v1/history/model/stats`)
- 에이전트 사용 히스토리 목록 조회 (`GET /api/v1/history/agent/list`)
- 에이전트 통계 조회 - 테스트 환경 (`GET /api/v1/history/agent/stats/test`)
- 에이전트 통계 조회 - 운영 환경 (`GET /api/v1/history/agent/stats`)
- 문서 지능 통계 조회 (`GET /api/v1/history/doc-intelligence/stats`)

**DTO 구조:**
- Request: 페이징 및 필터링 파라미터
- Response: `ModelHistoryRes`, `ModelStatsRes`, `AgentHistoryRes`, `AgentStatsRes`, `DocIntelligenceStatsRes`
- 공통: `Payload`, `Pagination`, `PaginationLinks` (페이징 지원)
- 오류: `ValidationError`, `HTTPValidationError`

**특징:**
- OpenAPI 3.1.0 명세 100% 준수
- 페이징 및 필터링 완벽 지원
- 동적 데이터 구조 지원 (`additionalProperties: true`)

#### 2. Resource API (스케일링 기능)
**Feign Client**: `SktaiResourceClient`

**주요 기능:**
- 리소스 스케일링 (`POST /api/v1/resources/{resourceId}/scale`)
- 자동 스케일링 정책 설정
- Scale Out/In, Scale Up/Down 지원
- CPU/메모리 임계값 기반 트리거

**DTO:**
- Request: `ResourceScalingRequest` (scalingAction, targetCapacity, trigger 등)
- Response: `ResourceAllocationResponse`

#### 3. Agent Gateway API (배치 처리)
**Feign Client**: `SktaiAgentGatewayClient`

**주요 기능:**
- 배치 추론 처리 (`POST /api/v1/agents/{agentId}/batch`)
- 대량 데이터 일괄 처리

**DTO:**
- Request: `BatchRequest` (inputs, config, kwargs)
- Response: `BatchResponse`

#### 4. Data API (데이터 관리)
**Feign Client**: `SktaiDataClient`

**주요 기능:**
- 데이터셋 생성, 조회, 수정, 삭제
- 데이터 소스 관리
- Few-shot 데이터 관리

**DTO:**
- 구체적 타입 정의 (DataSourceCreate, DatasetResponse 등)
- Generic DTO 패턴 (`SktaiResponse<T>`)

#### 5. Knowledge API (지식베이스 관리)
**Feign Client**: `SktaiKnowledgeClient`

**주요 기능:**
- Repository 생성, 조회, 수정, 삭제
- Collection 관리
- Document 관리

**DTO:**
- Request: `RepoCreate`, `CollectionCreate`, `DocumentCreate`
- Response: `RepoCreateResponse`, `RepoWithCollection`

### 기타 외부 시스템 연동 상태

#### Lablup (Backend.AI)
- **클라이언트**: `LablupArtifactClient`, `LablupBackendAiClient`
- **주요 기능**: 배치 아티팩트 스캔, 세션 관리, 컴퓨트 관리
- **인증**: HMAC SHA256 기반 요청 서명
- **상태**: ✅ 완료

#### Datumo (AI 평가)
- **클라이언트**: `DatumoClient`
- **주요 기능**: AI 모델 평가 및 벤치마크
- **상태**: ✅ 완료

#### I-ONE (사내 시스템)
- **클라이언트**: `IoneSystemClient`
- **주요 기능**: 사내 시스템 연동
- **상태**: ✅ 완료

#### Shinhan Bank (금융 시스템)
- **클라이언트**: `ShinhanClient`
- **주요 기능**: 금융 시스템 연동
- **상태**: ✅ 완료

#### UDP (통합 데이터 플랫폼)
- **클라이언트**: `UdpClient`
- **주요 기능**: 데이터 플랫폼 연동
- **상태**: ✅ 완료

#### Elasticsearch
- **클라이언트**: `ElasticClient`
- **주요 기능**: 검색 엔진 연동
- **상태**: ✅ 완료

### 공통 Feign 설정

#### SktaiClientConfig
- **Base URL**: `${sktai.api.base-url}` (통일된 설정)
- **타임아웃**: 연결 5초, 읽기 30초
- **재시도**: 최대 5회, 지수 백오프
- **SSL/TLS**: HTTPS 통신 보안 설정
- **인터셉터**: `SktaiRequestInterceptor` (공통 헤더 자동 적용)
- **에러 디코더**: `SktaiErrorDecoder` (외부 API 오류 변환)

#### 개발 가이드라인
- **타입 안전성 우선**: Object 대신 구체적 DTO 사용
- **Generic DTO 활용**: `SktaiResponse<T>` 패턴
- **DTO 클래스 분리**: inner class 금지
- **OpenAPI 문서화**: `@Schema` 어노테이션 필수
- **상세 로깅**: 요청/응답 로깅 (민감정보 제외)
- **Service 래핑**: Feign Client를 Service에서 래핑하여 비즈니스 로직 처리

## 테스트 실행

**Windows:**
```cmd
# 전체 테스트 실행
.\mvnw.cmd test

# 특정 테스트 클래스 실행
.\mvnw.cmd test -Dtest=HealthControllerTest

# 테스트 커버리지 포함 실행
.\mvnw.cmd clean test jacoco:report
```

**Linux/Mac:**
```bash
# 전체 테스트 실행
./mvnw test

# 특정 테스트 클래스 실행
./mvnw test -Dtest=HealthControllerTest

# 테스트 커버리지 포함 실행
./mvnw clean test jacoco:report
```

## 빌드 및 배포

### 1. Maven Wrapper 빌드

**Windows:**
```cmd
# 기본 빌드
.\mvnw.cmd clean package

# 프로필별 빌드
.\mvnw.cmd clean package -P local
.\mvnw.cmd clean package -P dev
.\mvnw.cmd clean package -P prod

# 테스트 스킵 빌드
.\mvnw.cmd clean package -DskipTests

# 컴파일만 수행
.\mvnw.cmd clean compile
```

**Linux/Mac:**
```bash
# 기본 빌드
./mvnw clean package

# 프로필별 빌드
./mvnw clean package -P local
./mvnw clean package -P dev
./mvnw clean package -P prod

# 테스트 스킵 빌드
./mvnw clean package -DskipTests

# 컴파일만 수행
./mvnw clean compile
```

### 2. 배포 파일
- **JAR**: `target/aiplatform-1.0.0.jar`
- **설정 파일**: `src/main/resources/application-{profile}.yml`

## 데이터베이스 설정

### H2 (로컬 개발)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:axportal
    username: sa
    password: 
```

### PostgreSQL (외부 개발)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/axportal_edev
    username: axportal_user
    password: axportal_password
```

### Tibero (운영)
```yaml
spring:
  datasource:
    url: jdbc:tibero:thin:@prod-db-server:8629:axportal_prod
    username: axportal_prod
    password: ${DB_PASSWORD}
```

## 환경 변수

### 운영 환경 필수 환경 변수
```bash
DB_PASSWORD=운영DB비밀번호
JWT_SECRET=JWT시크릿키
```

### SKTAX API 설정
```bash
SKTAX_BASE_URL=${sktai.api.base-url}
SKTAX_API_KEY=API키
```

### 선택적 환경 변수
```bash
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
LOG_LEVEL=INFO
```

## 보안 설정

### JWT 토큰
- **만료 시간**: 24시간 (운영환경: 1시간)
- **리프레시 토큰**: 7일 (운영환경: 24시간)
- **알고리즘**: HS256

### CORS 설정
- **허용 도메인**: `localhost:*`, `*.skax.com`
- **허용 메서드**: GET, POST, PUT, PATCH, DELETE, OPTIONS
- **자격 증명**: 허용

## 모니터링

### Spring Actuator
- `/actuator/health` - 헬스 체크
- `/actuator/info` - 애플리케이션 정보
- `/actuator/metrics` - 메트릭 정보

### 로깅
- **로그 레벨**: DEBUG (개발), INFO (운영)
- **로그 파일**: 콘솔 출력 (Docker 환경)

## 개발 가이드라인

### 1. 코딩 컨벤션
- **Java**: Google Java Style Guide 준수
- **네이밍**: camelCase (변수, 메서드), PascalCase (클래스)
- **패키지**: 소문자, 점(.) 구분

### 2. 커밋 메시지
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 리팩토링
test: 테스트 추가/수정
chore: 빌드 설정 등
```

### 3. 브랜치 전략
- **main**: 운영 배포 브랜치
- **develop**: 개발 통합 브랜치
- **feature/기능명**: 기능 개발 브랜치
- **hotfix/수정명**: 긴급 수정 브랜치

## 개발 도구 설정

### VS Code 확장 추천
- **Extension Pack for Java** - Java 개발 도구
- **Spring Boot Extension Pack** - Spring Boot 개발 도구
- **REST Client** - API 테스트 도구

### IntelliJ IDEA 플러그인 추천
- **Lombok Plugin** - Lombok 지원
- **MapStruct Support** - MapStruct 지원

## 라이센스

MIT License - 자세한 내용은 [LICENSE](LICENSE) 파일 참조

## 작성자

**ByounggwanLee**
- Email: developer@skax.com
- GitHub: https://github.com/byounggwanlee

## 버전 히스토리

### v1.2.0 (2025-11-01) - README 현행화 ✅
- **프로젝트 구조 업데이트**: 실제 구현된 19개 도메인 컨트롤러 및 7개 외부 시스템 반영
- **SKTAI 19개 서브모듈 현황**: 전체 구현 완료 상태 문서화
- **외부 시스템 연동 현황**: Lablup, Datumo, I-ONE, Shinhan, UDP, Elasticsearch 완료
- **배치 및 스케줄링**: IdeDeleteBatch, Agent Gateway, Lablup Artifact, Resource Scaling 문서화
- **기술 스택 상세화**: 의존성 버전 및 용도 명확화
- **환경별 프로필**: 6개 환경 설정 상세 설명
- **API 문서**: 도메인별 주요 엔드포인트 추가
- **SQL Comment 자동 삽입 시스템**: AOP 기반 DB 모니터링용 SQL 주석 자동화 🆕
  - ThreadLocal 기반 컨텍스트 관리
  - Service/Repository AOP 인터셉터
  - Hibernate StatementInspector + JDBC Connection Proxy
  - 트랜잭션 동기화 및 Write Behind 지원
  - 개발 환경에서만 활성화 (elocal, local, edev)

### v1.1.0 (2025-10-09) - 인증 및 예외 처리 강화
- **Swagger UI OAuth2 인증 시스템 강화**
  - OAuth2PasswordBearer와 HTTPBearer 이중 인증 지원
  - Client Credentials Location을 Request Body로 설정
  - 자동 토큰 관리 및 글로벌 보안 요구사항 적용
  - 듀얼 로그인 엔드포인트 (JSON + Form-data) 구현
- **예외 처리 시스템 개선**
  - BusinessException에 커스텀 메시지 detail 지원 추가
  - AxResponseEntity에 상세 정보 포함 error 메서드 확장
  - GlobalExceptionHandler에서 ErrorCode 기본 메시지와 커스텀 detail 분리
  - 개발자 친화적 에러 응답 구조 완성
- **API 문서화 개선**
  - OpenAPI 3 설정에 OAuth2 플로우 스코프 정의
  - Swagger UI에서 OAuth2 자동 인증 기능 완성
  - SpringDoc 설정 최적화 (persist-authorization, scopes 등)

### v1.0.0 (2025-08-02) - 초기 릴리스
- Spring Boot 3.5.4 기반 프로젝트 구조 생성
- JWT 인증 기반 보안 시스템 구현
- 다중 데이터베이스 환경 지원 (H2, PostgreSQL, Tibero)
- OpenAPI 3 문서화 설정
- 전역 예외 처리 구현
- 헬스 체크 API 구현
- SKTAI API 연동 구조 구현
- 히스토리 관리 API 클라이언트 완성 (7개 엔드포인트)
- 13개 SKTAI 서비스별 Feign Client 구조 생성

## 추후 개발 계획

### Phase 1 - 성능 최적화 및 모니터링 강화 (Q1 2026)
- [ ] **캐싱 전략 고도화**: Redis 통합 및 분산 캐시
- [ ] **성능 모니터링**: Prometheus + Grafana 대시보드 구축
- [ ] **로깅 시스템 개선**: ELK 스택 통합
- [ ] **API 응답 시간 최적화**: 쿼리 튜닝 및 인덱스 최적화
- [ ] **배치 처리 성능 향상**: 병렬 처리 및 파티셔닝

### Phase 2 - 기능 확장 (Q2 2026)
- [ ] **실시간 알림 시스템**: WebSocket 기반 실시간 알림
- [ ] **파일 업로드/다운로드 최적화**: 청크 업로드 및 S3 직접 연동
- [ ] **사용자 활동 추적**: Audit Log 시스템 구축
- [ ] **다국어 지원 확장**: 영어, 일본어 메시지 추가
- [ ] **API Rate Limiting**: Spring Cloud Gateway를 통한 요청 제한

### Phase 3 - 보안 및 안정성 강화 (Q3 2026)
- [ ] **API 버전 관리**: v2 API 도입
- [ ] **보안 강화**: OAuth2 Refresh Token, 2FA 지원
- [ ] **재해 복구**: 백업 및 복구 자동화
- [ ] **서킷 브레이커**: Resilience4j 통합
- [ ] **보안 취약점 스캔**: 정기적 보안 점검 자동화

### Phase 4 - 고급 기능 (Q4 2026)
- [ ] **AI 기반 추천 시스템**: 사용자 행동 기반 추천
- [ ] **GraphQL API**: REST API와 병행 제공
- [ ] **gRPC 지원**: 내부 서비스 간 고성능 통신
- [ ] **이벤트 기반 아키텍처**: Kafka 통합
- [ ] **마이크로서비스 분리**: 도메인별 서비스 분리

### 장기 로드맵 (2027년 이후)
- **컨테이너 오케스트레이션**: Kubernetes 기반 완전 자동화 배포
- **서버리스 아키텍처**: AWS Lambda 기반 일부 기능 전환
- **ML Ops 통합**: MLflow 연동으로 모델 라이프사이클 관리
- **글로벌 확장**: 리전별 배포 및 CDN 활용
