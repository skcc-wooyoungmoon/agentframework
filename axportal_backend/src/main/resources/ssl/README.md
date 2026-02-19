# HTTPS/SSL 설정 가이드

## 개발 환경

개발 환경에서는 모든 SSL 인증서를 신뢰하도록 설정되어 있습니다.
이는 자체 서명된 인증서나 테스트 환경에서의 편의를 위한 설정입니다.

```yaml
spring:
  profiles:
    active: dev
```

## 운영 환경

운영 환경에서는 적절한 SSL 인증서 검증을 수행합니다.

### Trust Store 설정

1. Trust Store 파일을 `src/main/resources/ssl/` 디렉토리에 배치합니다.
2. application.yml에서 Trust Store 설정을 활성화합니다:

```yaml
spring:
  profiles:
    active: prod

feign:
  client:
    ssl:
      trust-store: classpath:ssl/truststore.jks
      trust-store-password: ${SSL_TRUSTSTORE_PASSWORD}
      trust-store-type: JKS
```

### 환경 변수 설정

운영 환경에서는 다음 환경 변수를 설정해야 합니다:

```bash
export SSL_TRUSTSTORE_PASSWORD=your_truststore_password
```

### Trust Store 생성 방법

서버의 SSL 인증서를 Trust Store에 추가하는 방법:

```bash
# 서버에서 인증서 다운로드
openssl s_client -connect aip-stg.sktai.io:443 -showcerts </dev/null 2>/dev/null | openssl x509 -outform PEM > server.crt

# Trust Store에 인증서 추가
keytool -import -alias aip-stg-sktai -file server.crt -keystore truststore.jks -storepass changeit
```

## 로깅 설정

HTTPS 연결 관련 문제를 디버깅하기 위해 다음 로깅을 활성화할 수 있습니다:

```yaml
logging:
  level:
    javax.net.ssl: DEBUG
    com.skax.aiplatform.feign: DEBUG
    feign: DEBUG
```

## 문제 해결

### SSL Handshake 실패
- Trust Store에 서버 인증서가 포함되어 있는지 확인
- 인증서 만료일 확인
- SSL 프로토콜 버전 확인

### 연결 타임아웃
- 네트워크 연결 상태 확인
- 방화벽 설정 확인
- Feign 타임아웃 설정 조정

### 인증서 검증 오류
- 인증서 체인 확인
- CA 인증서 Trust Store 포함 여부 확인
- 호스트명 검증 설정 확인
