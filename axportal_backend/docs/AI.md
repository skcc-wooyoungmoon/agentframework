# AI 명령어를 통한 프로그램 생성 프롬프트

## 사전작업 ##
1. 디렉토리 생성
2. .github에 coplot-instructions.md 파일 생성

## 프로젝트 생성 ##
1. 생성 프롬프트
``` ai prompt
프로젝트 생성해줘
```
2. Gradle/Maven Wrapper설치
``` ai prompt
maven[gradle] wrapper 를 생성해줘
```
3. 실행환경변경
``` ai prompt
4. 로깅 및 요청 추적 자동생성
``` ai prompt
구조화된 로깅 및 요청 추적 자동생성
```
5. Jwt인증 및 SpringSecutrity추가
```
springsecurity와 jwt인증 추가
```
6. CORS 설정
``` ai prompt
CORS를 허용하도록 설정해줘
```
7. 샘플 생성
``` ai prompt
Sample 생성해줘
```
8. SktAx FeignClient 및 DTO생성
   - 생성요청
``` ai prompt
- URL(https://aip-stg.sktai.io/api/v1/common/auth/openapi.json)를 참조
  - config, intercept는 /client/sktax/config에 생성하여 공통으로 사용하며, 필요시 수정
  - Feign Client Interface는 접속 엔드포인트별로 Group화하여 /client/sktax/auth 디렉토리에 생성
  - dto생성
    - file명은 openapi.json의 명세을 기반으로 생성
    - file명에 Request, Req, request, req가 포함된 경우 /client/sktax/auth/dto/request에 생성
    - file명에 Response, Res, response, res가 포함된 경우 /client/sktax/auth/dto/response에 생성
    - 이외는 /client/sktax/auth/dto/ 기본 디렉토리에 생성
    - class내부에 inner class는 별도 독립 class로 생성(단, enum class는 예외)
  - 작업시 주의사항
    - Swagger UI에서 제공하는 예시를 참고하여 DTO를 작성하세요.
    - 요청 파라미터 및 응답 모델의 필드명은 OpenAPI 명세와 일치해야 합니다.
    - OpenAPI 명세에 정의된 필드 타입을 정확히 반영해야 합니다.
  - 수행전 coplot-instructions.md과 충돌내용 있으면 확인 요청
- 작업완료 후 openapi.json 명세와 비교 누락된 부분은 재생성
```
  - 수행점검
``` ai prompt
다시한번 openapi.json 명세와 비교 누락된 부분은 재생성
```
   - 수행완료 후 재점검
``` ai prompt
생성된 파일이 OpenAPI 명세와 일치하는지 확인
  - 작업시 주의사항
    - Swagger UI에서 제공하는 예시를 참고하여 DTO를 작성하세요.
    - 요청 파라미터 및 응답 모델의 필드명은 OpenAPI 명세와 일치해야 합니다.
    - OpenAPI 명세에 정의된 필드 타입을 정확히 반영해야 합니다.
```

9. FeignClient 분석
``` ai prompt
sktax.auth.*client에서 사용하는 Dto CRUD mextrix 표로 보여줘
```
## 문서생성 ##

1. README
``` ai prompt
현재 프로젝트 기반으로 README.md 현행화
``` 

2. 개발표준
``` ai prompt
# 프로젝트 문서를 아래내용을 참조하여, 개발표준.md를 현행화 해주세요.

1. 프로젝트 개요 및 AI 코딩 접근 방식
   - 이 Spring Boot 프로젝트의 핵심 비즈니스 목적과 해결하려는 문제는 무엇인가요? 
   - 특히, 프로젝트 개발에 AI 코딩(예: GitHub Copilot, ChatGPT 등)이 어떤 방식으로, 어느 정도 활용되었는지 명확히 설명해 주세요. 
   - AI가 생성한 코드와 수동으로 작성된 코드 간의 역할 분담 및 통합 전략에 대해서도 기술해 주세요.

2. 아키텍처 및 모듈 구조
   - 프로젝트의 전체적인 아키텍처 다이어그램을 제공하고, 각 Spring Boot 모듈(서비스, 라이브러리 등)의 역할과 책임을 명확히 설명해 주세요. 
   - 특히, AI가 생성한 코드가 주로 어느 계층(Controller, Service, Repository, DTO 등)에 위치하며, 각 모듈 간의 데이터 흐름과 상호작용 방식을 구체적으로 보여주세요.

3. 핵심 기능 설명 및 AI 코드 연관성
   - 이 프로젝트의 주요 핵심 기능 5가지를 식별하고, 각 기능별로 비즈니스 로직의 흐름을 상세히 설명해 주세요. 
   - 각 기능 구현에 AI가 생성한 코드가 어떤 특정 부분에서 기여했으며, 해당 AI 생성 코드가 어떤 역할을 수행하는지 구체적인 코드 예시와 함께 설명해 주세요.

4. AI 생성 코드 품질 및 검토 가이드라인
   - AI가 생성한 코드에 대한 코드 품질 기준 및 검토 가이드라인을 제시해 주세요. 예를 들어, 가독성, 성능 최적화, 보안 취약점 점검, 비즈니스 로직의 정확성 검증 등 AI 생성 코드에 대해 특별히 주의해야 할 사항은 무엇인가요? 
   - 코드 리뷰 과정에서 AI 생성 코드를 어떻게 다루어야 하는지에 대한 절차도 포함해 주세요."

5. 데이터 모델 및 엔티티 관계
   - 프로젝트에서 사용되는 주요 데이터 모델(JPA 엔티티 등)과 그들 간의 관계를 ERD(Entity Relationship Diagram)와 함께 설명해 주세요. 
   - 각 엔티티의 핵심 필드와 제약 조건을 명시하고, AI가 데이터베이스 스키마 또는 쿼리 생성에 기여한 부분이 있다면 언급해 주세요.

6. 개발 환경 및 필수 도구
   - 프로젝트 개발을 위한 **필수 개발 환경(Java 버전, Spring Boot 버전, IDE, 빌드 도구 등)**을 명확히 명시하고, 
   - 프로젝트 빌드 및 실행을 위한 상세한 절차를 제공해 주세요. 
   - AI 코딩에 사용된 특정 플러그인이나 도구 설정이 있다면 함께 설명해 주세요."

7. 종속성 관리 및 외부 연동
   - 프로젝트의 주요 외부 라이브러리 및 프레임워크 종속성(pom.xml 또는 build.gradle 기반)을 설명하고, 각 종속성의 역할에 대해 간략히 설명해 주세요. 
   - 외부 서비스(REST API, 메시지 큐 등)와의 연동 방식 및 사용된 Feign Client 또는 WebClient 설정에 대해 기술해 주세요.

8. 테스트 전략 및 AI 코드 테스트
   - 이 프로젝트의 **테스트 전략(단위 테스트, 통합 테스트, 인수 테스트)**을 설명하고, 
   - 각 테스트 유형별로 AI가 생성한 코드에 대한 테스트는 어떻게 수행되었는지 구체적인 사례를 들어 설명해 주세요. 
   - 코드 커버리지 목표 및 CI/CD 파이프라인에서의 테스트 자동화 여부도 명시해 주세요."

9. 배포 및 운영 가이드
   - 프로젝트를 운영 환경에 배포하기 위한 상세한 절차를 제공하고, 애플리케이션의 주요 설정(application.yml 등) 및 환경 변수에 대해 설명해 주세요. 
   - 운영 중 발생할 수 있는 잠재적 문제(로그 분석, 성능 병목, AI 모델 관련 오류 등)와 그 해결 방안을 포함한 운영 가이드를 작성해 주세요.

10. AI 코딩 시 주의사항 및 향후 개선 방향
   - 이 프로젝트 개발 과정에서 AI 코딩으로 인해 발생했던 주요 문제점이나 어려움은 무엇이었나요? (예: 비효율적인 코드, 보안 취약성, 유지보수의 어려움 등) 
   - 이러한 문제들을 어떻게 해결했으며, 
   - 향후 프로젝트의 유지보수, 확장, 또는 새로운 기능 개발 시 AI 코딩을 활용하는 데 있어 개발자들이 특별히 주의해야 할 사항과 개선 방향에 대해 제안해 주세요.
``` 

3. 명명규칙
``` ai prompt
현재 프로젝트 기준으로 아래내용을 중심으로 명명규칙.md를 작성해줘
- Rest Controller Method, Mepping에 대해 Get,Post,Patch,Put, Delete등 상세하게
- Get의 경우 Get방식에 전체/단건/단수조건/복수조건 검색등에 대해 상세
- Controller, DTO, Service, Entity, Respository간에 명명규칙 관계에 대해
- Controller, DTO, Service, Feign DTO, FeignClient간에 명명규칙 관계에 대해
- [업무그룹]별로 디렉토리를 생성 후 파일생성
- 좋은예시/나쁜예시 포함
- DTO는 Request와 Response 분리하여 Suffix로 Req, Res로 표기 하도록
- FeigneClient는 /client/skai/[업무그룹]/ 
- FeignClient DTO는 /client/skai/[업무그룹]/dto( Request, Response 분리)
- 목차를 만들고 링크 포함해서
```

4. 아키텍처 정의서
``` ai 프롬프트
현재 개발중인  프로젝트의 아키텍처 정의서를 작성하려고 합니다.
아래 항목들을 포함해서, 아키텍처 정의서.md로 작성해 주세요.

프로젝트 개요
기술 스택
전체 시스템 구조
레이어드 아키텍처
주요 모듈 및 컴포넌트
데이터베이스 설계
API 설계 예시
보안 및 인증
배포 및 운영 환경
그 외 참고할 만한 사항
항목별로 자세하게 설명해 주세요.
```

## 기타 ##
** 소스파일 변경 **
``` ai prompt
소스파일을 타켓파일로 변경해줘
```
** 파일 삭제 **
``` ai prompt
파일을 삭제해줘
```
** maven 컴파일 **
.\mvnw.cmd clean compile
```
mvnw로 컴파일해줘
```

** 삭제파일 복구되는 문제 **
git commit & push 후 "Developer: Reload window"실행 파일 생면
git restore .
git clean -fd
```
현재 git 변경된 파일 원복해줘
```