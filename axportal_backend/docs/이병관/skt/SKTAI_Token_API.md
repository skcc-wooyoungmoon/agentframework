# SKTAI Access Token API 사용 가이드

본 문서는 내부 JWT를 이용해 SKTAI 호출용 access_token을 조회/리프레시하는 엔드포인트 사용 방법을 설명합니다.

주의: 이 애플리케이션은 server.servlet.context-path가 "/api"로 설정되어 있습니다. 따라서 모든 API 호출 URL은 "/api" 접두어를 포함해야 합니다.

- 기본 URL 예시: http://localhost:8080/api
- 보안: Spring Security JWT 인증 필요 (Authorization: Bearer <INTERNAL_JWT>)
- 응답 래퍼: AxResponse

## 1) SKTAI Access Token 조회
- 메서드/경로: GET /auth/sktai/access-token
- 전체 URL: http://localhost:8080/api/auth/sktai/access-token
- 설명: 현재 인증된 사용자의 tokens 레코드에서 유효한 SKTAI access_token을 반환합니다.

요청 예시 (cURL)

```
curl -X GET \
  'http://localhost:8080/api/auth/sktai/access-token' \
  -H 'Authorization: Bearer <YOUR_INTERNAL_JWT>' \
  -H 'Accept: application/json'
```

성공 응답 예시 (200)

```
{
  "success": true,
  "message": "SKTAI Access Token 조회 성공",
  "data": { "access_token": "eyJhbGciOi..." },
  "timestamp": "2025-09-29T23:13:00",
  "path": "/auth/sktai/access-token"
}
```

## 2) SKTAI Access Token 리프레시
- 메서드/경로: GET /auth/sktai/access-token/refresh
- 전체 URL: http://localhost:8080/api/auth/sktai/access-token/refresh
- 설명: 저장된 refresh_token으로 SKTAI access_token을 갱신하고 DB/캐시를 업데이트합니다.

요청 예시 (cURL)

```
curl -X GET \
  'http://localhost:8080/api/auth/sktai/access-token/refresh' \
  -H 'Authorization: Bearer <YOUR_INTERNAL_JWT>' \
  -H 'Accept: application/json'
```

성공 응답 예시 (200)

```
{
  "success": true,
  "message": "SKTAI Access Token 리프레시 성공",
  "data": { "access_token": "eyJhbGciOi...<new>" },
  "timestamp": "2025-09-29T23:13:00",
  "path": "/auth/sktai/access-token/refresh"
}
```

## 에러 예시
- 401 Unauthorized: JWT 누락/유효하지 않음 또는 리프레시 토큰 만료
- 404 Not Found: 사용자 토큰 정보 없음 (tokens 레코드 없음 또는 무효)

## 테스트용 JWT 발급
- 로그인: POST http://localhost:8080/api/auth/login

```
curl -X POST \
  'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"username": "admin", "password": "aisnb"}'
```

응답의 data.access_token 값을 위 두 엔드포인트 Authorization 헤더에 사용하세요.
