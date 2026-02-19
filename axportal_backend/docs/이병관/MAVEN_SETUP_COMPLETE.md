# Maven 환경설정 완료 안내

이 파일은 AxportalBackend Project의 Maven 기반 환경설정이 정상적으로 완료되었음을 알리기 위해 자동 생성되었습니다.

## 프로젝트 정보
- **프로젝트명**: AxportalBackend Project
- **개발자**: ByounggwanLee
- **생성일**: 2025-08-01
- **빌드 도구**: Maven
- **Java 버전**: 17 이상
- **Spring Boot 버전**: 3.5.4
- **기본 환경**: 외부 로컬(elocal) ⭐

## 주요 환경설정
- `pom.xml`에 모든 필수 의존성 및 플러그인 설정 완료
- 환경별 프로파일(application-*.yml) 분리 적용 가능
- 표준 디렉토리 구조 및 패키지 규칙 적용
- **기본 프로필**: `elocal` (외부 로컬 환경)

## 빌드 및 실행 방법

### 1. 빌드
```bash
mvn clean install
```

### 2. 실행 (외부 로컬 환경 - 기본)
```bash
mvn spring-boot:run
```

### 3. 특정 환경 실행
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=elocal  # 외부 로컬 (기본)
mvn spring-boot:run -Dspring-boot.run.profiles=local   # 로컬
mvn spring-boot:run -Dspring-boot.run.profiles=edev    # 외부 개발
```

### 4. 테스트
```bash
mvn test
```

## VS Code 설정
- **F5 키**: 외부 로컬환경(elocal)으로 디버그 실행 (기본)
- **Ctrl+Shift+P** → **Tasks: Run Task**: 빌드 및 실행 작업 선택
- **기본 작업**: "Spring Boot: Run (External Local) - Default ⭐"

## 접속 정보 (외부 로컬 환경)
- **애플리케이션**: http://localhost:8080/api/v1
- **H2 콘솔**: http://localhost:8080/api/v1/h2-console
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **Health Check**: http://localhost:8080/api/v1/health

## 참고 사항
- 환경별 DB 및 설정은 `src/main/resources/application-*.yml` 파일에서 관리합니다.
- 추가 의존성은 반드시 코드 리뷰 후 반영합니다.
- 빌드/테스트 오류 발생 시, 로그를 확인하고 문제를 해결한 후 재시도하세요.
- **기본 개발환경**: 외부 로컬(elocal) 환경이 모든 실행의 기본값으로 설정되었습니다.

---

> **이 파일은 자동 생성되었으며, 삭제해도 프로젝트 동작에는 영향이 없습니다.**