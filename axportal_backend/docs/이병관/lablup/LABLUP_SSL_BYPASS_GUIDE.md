# Lablup Client SSL 우회 설정 가이드

## 📋 개요

개발계 환경에서 Lablup API와 통신할 때 발생할 수 있는 SSL 인증서 관련 문제들을 해결하기 위한 설정 가이드입니다.

## ⚠️ 보안 경고

**이 설정은 개발 환경에서만 사용해야 하며, 운영 환경에서는 절대 사용하지 마십시오!**

## 🔧 적용된 SSL 우회 설정

### 1. 환경별 SSL 우회 적용 범위

| 환경 | Profile | SSL 우회 적용 |
|------|---------|---------------|
| 외부로컬 | `elocal` | ✅ 적용 |
| 외부개발 | `edev` | ✅ 적용 |
| 로컬 | `local` | ✅ 적용 |
| 개발 | `dev` | ✅ 적용 |
| 스테이징 | `staging` | ❌ 표준 SSL |
| 운영 | `prod` | ❌ 표준 SSL |

### 2. 해결되는 SSL 문제들

- ✅ **자체 서명 인증서 (Self-signed certificates)**
- ✅ **만료된 인증서 (Expired certificates)**
- ✅ **호스트명 불일치 (Hostname mismatch)**
- ✅ **인증 기관 미신뢰 (Untrusted CA)**
- ✅ **인증서 체인 오류 (Certificate chain errors)**

### 3. 주요 설정 파일들

#### 3.1 LablupClientConfig.java
```java
@Bean
@Profile({"elocal", "edev", "local", "dev"})
public Client feignClientWithSSLBypass() throws Exception {
    // 모든 인증서를 신뢰하는 TrustManager
    // 호스트명 검증 완전 우회
    // SSL 컨텍스트 설정
}
```

#### 3.2 application 설정 파일들
```yaml
lablup:
  api:
    base-url: ${LABLUP_API_BASE_URL:https://lablup-api.example.com}
    ssl:
      trust-all: true
      verify-hostname: false
      enabled-protocols: TLSv1.2,TLSv1.3
```

## 🚀 사용 방법

### 1. 환경 변수 설정

개발계에서 Lablup API를 사용하려면 다음 환경 변수를 설정하세요:

```bash
# Lablup API 기본 설정
LABLUP_API_BASE_URL=https://your-lablup-api-server.com
LABLUP_CLIENT_ID=your-client-id
LABLUP_CLIENT_SECRET=your-client-secret
```

### 2. Profile 활성화

개발계 Profile을 활성화하여 SSL 우회 설정이 적용되도록 합니다:

```bash
# 외부로컬 환경
spring.profiles.active=elocal

# 외부개발 환경
spring.profiles.active=edev

# 로컬 환경
spring.profiles.active=local

# 개발 환경
spring.profiles.active=dev
```

### 3. 설정 검증

애플리케이션 시작 시 다음과 같은 로그를 확인하여 SSL 우회 설정이 올바르게 적용되었는지 확인할 수 있습니다:

```
=== Lablup SSL 설정 검증 시작 ===
활성 프로필: elocal
Lablup API Base URL: https://your-lablup-api-server.com
SSL Trust All 설정: true
SSL Hostname 검증: false
⚠️  SSL 검증 완전 우회 모드 활성화됨 - 개발 환경 전용
   - 모든 SSL 인증서를 신뢰합니다 (자체 서명, 만료된 인증서 포함)
   - 호스트명 검증을 건너뜁니다
   - 운영 환경에서는 절대 사용하지 마십시오!
=== Lablup SSL 설정 검증 완료 ===
```

## 🔍 문제 해결

### 1. SSL 우회가 적용되지 않는 경우

- Profile이 올바르게 설정되었는지 확인
- 환경 변수가 제대로 로드되었는지 확인
- 로그를 통해 SSL 설정 상태 확인

### 2. 여전히 SSL 오류가 발생하는 경우

```bash
# 1. JVM 시스템 속성으로 SSL 디버깅 활성화
-Djavax.net.debug=ssl:handshake

# 2. Spring Boot 로깅 레벨 조정
logging.level.com.skax.aiplatform.client.lablup=DEBUG
```

### 3. 인증서 관련 오류 메시지들

| 오류 메시지 | 해결 여부 |
|-------------|-----------|
| `PKIX path building failed` | ✅ 해결됨 |
| `unable to find valid certification path` | ✅ 해결됨 |
| `certificate has expired` | ✅ 해결됨 |
| `hostname in certificate didn't match` | ✅ 해결됨 |
| `SSLHandshakeException` | ✅ 해결됨 |

## 📝 참고사항

1. **운영 환경 주의사항**: 운영 환경에서는 반드시 유효한 SSL 인증서를 사용해야 합니다.

2. **보안 고려사항**: 개발계에서도 가능한 한 유효한 인증서를 사용하는 것을 권장합니다.

3. **성능 고려사항**: SSL 우회 설정은 성능에 미미한 영향을 줄 수 있습니다.

4. **로깅**: 개발 중에는 DEBUG 레벨 로깅을 활성화하여 SSL 통신 상태를 모니터링할 수 있습니다.

## 🔄 버전 정보

- **버전**: 1.0
- **작성일**: 2025-10-02
- **작성자**: ByounggwanLee
- **마지막 수정**: 2025-10-02