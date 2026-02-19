# Maven Wrapper 사용 가이드

## 목차
1. [Maven Wrapper 개요](#1-maven-wrapper-개요)
2. [Maven Wrapper 설치](#2-maven-wrapper-설치)
3. [기본 사용법](#3-기본-사용법)
4. [주요 명령어](#4-주요-명령어)
5. [환경별 빌드](#5-환경별-빌드)
6. [트러블슈팅](#6-트러블슈팅)
7. [참고사항](#7-참고사항)

---

## 1. Maven Wrapper 개요

### 1.1 Maven Wrapper란?
Maven Wrapper(mvnw)는 프로젝트에 Maven을 포함시켜 시스템에 Maven이 설치되어 있지 않아도 빌드할 수 있게 해주는 도구입니다.

### 1.2 주요 장점
- **환경 독립성**: 시스템에 Maven이 설치되어 있지 않아도 빌드 가능
- **버전 일관성**: 모든 개발자가 동일한 Maven 버전 사용
- **자동 다운로드**: 필요시 Maven을 자동으로 다운로드
- **CI/CD 친화적**: 빌드 서버에서 별도 Maven 설치 불필요

### 1.3 프로젝트 파일 구성
```
axpotal_backend/
├── mvnw           # Unix/Linux/macOS용 실행 스크립트
├── mvnw.cmd       # Windows용 실행 스크립트
├── .mvn/
│   └── wrapper/
│       ├── maven-wrapper.jar        # Maven Wrapper JAR
│       └── maven-wrapper.properties # Wrapper 설정
└── pom.xml        # Maven 프로젝트 설정
```

---

## 2. Maven Wrapper 설치

### 2.1 기존 프로젝트에 Maven Wrapper 추가

#### 방법 1: Maven 명령어 사용
```bash
# Maven이 이미 설치된 경우
mvn wrapper:wrapper
```

#### 방법 2: 특정 Maven 버전 지정
```bash
mvn wrapper:wrapper -Dmaven=3.9.0
```

#### 방법 3: Maven 없이 설치
```bash
# 직접 다운로드 (Maven이 설치되지 않은 경우)
curl -LO https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
mkdir -p .mvn/wrapper
mv maven-wrapper-3.2.0.jar .mvn/wrapper/maven-wrapper.jar
```

### 2.2 실행 권한 설정 (Unix/Linux/macOS)
```bash
chmod +x mvnw
```

### 2.3 설정 파일 확인
```properties
# .mvn/wrapper/maven-wrapper.properties
distributionUrl=https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.9.0/apache-maven-3.9.0-bin.zip
wrapperUrl=https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
```

---

## 3. 기본 사용법

### 3.1 Windows 환경
```cmd
# 기본 빌드
.\mvnw.cmd clean compile

# 테스트 실행
.\mvnw.cmd test

# 패키지 생성
.\mvnw.cmd clean package

# Spring Boot 애플리케이션 실행
.\mvnw.cmd spring-boot:run
```

### 3.2 Unix/Linux/macOS 환경
```bash
# 기본 빌드
./mvnw clean compile

# 테스트 실행
./mvnw test

# 패키지 생성
./mvnw clean package

# Spring Boot 애플리케이션 실행
./mvnw spring-boot:run
```

### 3.3 IDE에서 사용
대부분의 IDE(IntelliJ IDEA, Eclipse, VS Code)에서 Maven Wrapper를 자동으로 인식하고 사용합니다.

**IntelliJ IDEA 설정:**
1. File → Settings → Build, Execution, Deployment → Build Tools → Maven
2. "Use Maven wrapper" 옵션 선택
3. Wrapper location: `프로젝트경로/.mvn/wrapper/maven-wrapper.properties`

---

## 4. 주요 명령어

### 4.1 기본 라이프사이클 명령어

```bash
# 1. 프로젝트 정리
./mvnw clean                    # target 디렉토리 삭제

# 2. 컴파일
./mvnw compile                  # 소스 코드 컴파일
./mvnw test-compile             # 테스트 코드 컴파일

# 3. 테스트
./mvnw test                     # 단위 테스트 실행
./mvnw integration-test         # 통합 테스트 실행

# 4. 패키징
./mvnw package                  # JAR/WAR 파일 생성
./mvnw package -DskipTests      # 테스트 생략하고 패키징

# 5. 설치
./mvnw install                  # 로컬 리포지토리에 설치

# 6. 배포
./mvnw deploy                   # 원격 리포지토리에 배포
```

### 4.2 Spring Boot 관련 명령어

```bash
# 애플리케이션 실행
./mvnw spring-boot:run

# 특정 프로파일로 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 특정 포트로 실행
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# JAR 파일 실행 (패키징 후)
java -jar target/aiplatform-1.0.0.jar

# Docker 이미지 빌드
./mvnw spring-boot:build-image
```

### 4.3 유용한 플러그인 명령어

```bash
# 종속성 관련
./mvnw dependency:tree          # 종속성 트리 출력
./mvnw dependency:analyze       # 종속성 분석
./mvnw dependency:resolve       # 종속성 다운로드

# 코드 품질
./mvnw checkstyle:check         # 코드 스타일 검사
./mvnw spotbugs:check           # 버그 패턴 검사
./mvnw jacoco:report            # 코드 커버리지 리포트

# 사이트 및 문서
./mvnw site                     # 프로젝트 사이트 생성
./mvnw javadoc:javadoc          # JavaDoc 생성

# 버전 관리
./mvnw versions:display-dependency-updates  # 종속성 업데이트 확인
./mvnw versions:display-plugin-updates      # 플러그인 업데이트 확인
```

---

## 5. 환경별 빌드

### 5.1 프로파일별 빌드

```bash
# 로컬 환경 빌드
./mvnw clean package -Plocal

# 개발 환경 빌드
./mvnw clean package -Pdev

# 운영 환경 빌드
./mvnw clean package -Pprod

# Spring Boot 프로파일 지정
./mvnw spring-boot:run -Dspring-boot.run.profiles=elocal
```

### 5.2 시스템 속성 전달

```bash
# JVM 옵션 전달
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1024m -Xms512m"

# 애플리케이션 인수 전달
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081 --spring.profiles.active=dev"

# Maven 속성 설정
./mvnw package -Dmaven.test.skip=true
./mvnw package -DskipTests=true
```

### 5.3 환경변수 활용

```bash
# Windows
set SPRING_PROFILES_ACTIVE=dev
.\mvnw.cmd spring-boot:run

# Unix/Linux/macOS
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run

# 또는 인라인으로
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

---

## 6. 트러블슈팅

### 6.1 일반적인 문제 및 해결방법

#### 문제 1: 권한 오류 (Unix/Linux/macOS)
```bash
# 증상: Permission denied
# 해결방법: 실행 권한 부여
chmod +x mvnw
```

#### 문제 2: Maven Wrapper 다운로드 실패
```bash
# 증상: Unable to download Maven Wrapper
# 해결방법 1: 프록시 설정 확인
./mvnw -Dhttp.proxyHost=proxy.company.com -Dhttp.proxyPort=8080

# 해결방법 2: 수동 다운로드
rm -rf ~/.m2/wrapper
./mvnw clean compile
```

#### 문제 3: 종속성 다운로드 실패
```bash
# 증상: Could not resolve dependencies
# 해결방법 1: 로컬 리포지토리 정리
rm -rf ~/.m2/repository
./mvnw clean compile

# 해결방법 2: 오프라인 모드 해제
./mvnw clean compile -o  # 오프라인 모드
./mvnw clean compile     # 온라인 모드
```

#### 문제 4: 메모리 부족 오류
```bash
# 증상: OutOfMemoryError
# 해결방법: JVM 메모리 증가
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
./mvnw clean package

# Windows에서
set MAVEN_OPTS=-Xmx2048m -XX:MaxPermSize=512m
.\mvnw.cmd clean package
```

### 6.2 로그 및 디버그

```bash
# 상세 로그 출력
./mvnw clean compile -X

# 디버그 정보 출력
./mvnw clean compile -e

# 조용한 모드 (오류만 출력)
./mvnw clean compile -q

# 특정 로거 레벨 설정
./mvnw clean compile -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

### 6.3 캐시 및 정리

```bash
# Maven 로컬 리포지토리 정리
./mvnw dependency:purge-local-repository

# 특정 아티팩트 정리
./mvnw dependency:purge-local-repository -DmanualInclude="com.skax:aiplatform"

# 워크스페이스 완전 정리
./mvnw clean
rm -rf ~/.m2/repository/com/skax
```

---

## 7. 참고사항

### 7.1 성능 최적화

#### 병렬 빌드
```bash
# CPU 코어 수만큼 병렬 빌드
./mvnw clean package -T 1C

# 특정 스레드 수로 병렬 빌드
./mvnw clean package -T 4
```

#### 오프라인 모드
```bash
# 종속성이 모두 다운로드된 후 오프라인 빌드
./mvnw clean package -o
```

#### 빌드 캐시 활용
```bash
# 증분 빌드 (변경된 부분만)
./mvnw compile

# 전체 정리 후 빌드
./mvnw clean compile
```

### 7.2 CI/CD 환경에서의 사용

#### GitHub Actions 예시
```yaml
name: Build with Maven Wrapper

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    
    - name: Build with Maven Wrapper
      run: ./mvnw clean compile
    
    - name: Run tests
      run: ./mvnw test
    
    - name: Package application
      run: ./mvnw package -DskipTests
```

### 7.3 보안 고려사항

#### Maven Wrapper 검증
```bash
# Maven Wrapper JAR 파일 체크섬 확인
shasum -a 256 .mvn/wrapper/maven-wrapper.jar

# 공식 체크섬과 비교
# https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar.sha256
```

#### 안전한 다운로드 URL
```properties
# .mvn/wrapper/maven-wrapper.properties
# HTTPS 사용 확인
distributionUrl=https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.9.0/apache-maven-3.9.0-bin.zip
```

### 7.4 개발팀 권장사항

#### 팀 내 표준화
1. **버전 통일**: 모든 팀원이 동일한 Maven 버전 사용
2. **설정 공유**: `.mvn/wrapper/maven-wrapper.properties` 파일 공유
3. **권한 설정**: Git에서 실행 권한 유지
4. **문서화**: 프로젝트별 빌드 명령어 문서화

#### Git 설정
```bash
# .gitattributes 파일에 추가
mvnw text eol=lf
*.cmd text eol=crlf

# .gitignore에서 제외 (필수 파일들)
!.mvn/wrapper/maven-wrapper.jar
!.mvn/wrapper/maven-wrapper.properties
!mvnw
!mvnw.cmd
```

---

## 빠른 참조

### 자주 사용하는 명령어
```bash
# 프로젝트 빌드 및 실행
./mvnw clean compile                    # 컴파일
./mvnw clean package                    # 패키징
./mvnw spring-boot:run                  # 애플리케이션 실행

# 테스트
./mvnw test                            # 전체 테스트
./mvnw test -Dtest=UserServiceTest     # 특정 테스트 클래스
./mvnw test -Dtest=UserServiceTest#createUser  # 특정 테스트 메서드

# 환경별 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=local   # 로컬
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev     # 개발
```

### 유용한 옵션
```bash
-DskipTests         # 테스트 생략
-Dmaven.test.skip   # 테스트 컴파일 및 실행 생략
-X                  # 디버그 모드
-q                  # 조용한 모드
-T 1C              # 병렬 빌드 (CPU 코어 수만큼)
-o                  # 오프라인 모드
```

---

> **참고**: 이 가이드는 AxportalBackend 프로젝트를 기준으로 작성되었습니다. 프로젝트 환경에 따라 일부 명령어나 설정이 다를 수 있습니다.