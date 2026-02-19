# SKTAI Model Gateway API 문서

## 개요

SKTAI Model Gateway는 다양한 AI 모델에 대한 통합된 인터페이스를 제공하는 게이트웨이 서비스입니다. OpenAI API와 호환되는 인터페이스를 통해 텍스트 생성, 이미지 생성, 오디오 처리, 임베딩 생성 등의 AI 기능을 제공합니다.

### 주요 기능

- **모델 관리**: 사용 가능한 AI 모델 목록 조회
- **텍스트 처리**: 채팅 완성, 텍스트 완성, 임베딩 생성
- **이미지 처리**: AI 기반 이미지 생성
- **오디오 처리**: 음성 합성(TTS), 음성 인식(STT), 음성 번역
- **검색 최적화**: 유사도 스코어링, 리랭킹
- **고급 기능**: 커스텀 모델 엔드포인트, 고급 응답 생성

## API 클라이언트

### SktaiModelGatewayClient
**AI 모델 게이트웨이 서비스**

Model Gateway는 OpenAI API와 호환되는 표준 인터페이스를 제공하여 다양한 AI 모델을 통합적으로 사용할 수 있게 합니다.

## API 엔드포인트

### 1. 모델 관리

#### GET /api/v1/gateway/models
- **기능**: 사용 가능한 AI 모델 목록 조회
- **설명**: SKTAI 플랫폼에서 제공하는 모든 AI 모델의 목록과 메타데이터를 조회합니다.
- **응답 타입**: `ModelListResponse`
- **응답 내용**: 모델 ID, 타입, 소유자, 생성일 등의 정보

### 2. 텍스트 처리

#### POST /api/v1/gateway/chat/completions
- **기능**: 채팅 완성 생성
- **설명**: 대화형 AI 모델을 사용하여 채팅 형태의 응답을 생성합니다.
- **요청 타입**: `ChatCompletionsRequest`
- **응답 타입**: `ChatCompletionsResponse`
- **특징**: 
  - 시스템 프롬프트, 사용자 메시지, 이전 대화 기록 지원
  - GPT-4, Claude 등의 모델 활용
  - 스트리밍 응답 지원

#### POST /api/v1/gateway/completions
- **기능**: 텍스트 완성 생성
- **설명**: 주어진 프롬프트를 기반으로 텍스트를 자동 완성합니다.
- **요청 타입**: `CompletionsRequest`
- **응답 타입**: `CompletionsResponse`
- **활용**: 창의적 글쓰기, 코드 생성, 텍스트 확장

#### POST /api/v1/gateway/embeddings
- **기능**: 텍스트 임베딩 생성
- **설명**: 입력 텍스트를 고차원 벡터로 변환합니다.
- **요청 타입**: `EmbeddingsRequest`
- **응답 타입**: `EmbeddingsResponse`
- **활용**: 유사도 검색, 클러스터링, 분류, 추천 시스템

### 3. 이미지 처리

#### POST /api/v1/gateway/images/generations
- **기능**: AI 이미지 생성
- **설명**: 텍스트 설명을 바탕으로 AI가 이미지를 생성합니다.
- **요청 타입**: `ImagesRequest`
- **응답 타입**: `ImagesResponse`
- **지원 모델**: DALL-E, Stable Diffusion 등
- **옵션**: 이미지 크기, 품질, 스타일 설정

### 4. 오디오 처리

#### POST /api/v1/gateway/audio/speech
- **기능**: 음성 합성 (Text-to-Speech)
- **설명**: 입력 텍스트를 자연스러운 음성으로 변환합니다.
- **요청 타입**: `AudioSpeechRequest`
- **응답 타입**: `AudioSpeechResponse`
- **Content-Type**: `application/json`, `audio/mpeg`
- **특징**:
  - 다양한 음성 캐릭터 지원
  - 속도 조절 가능
  - 여러 언어 지원

#### POST /api/v1/gateway/audio/transcriptions
- **기능**: 음성 인식 (Speech-to-Text)
- **설명**: 오디오 파일을 텍스트로 변환합니다.
- **요청 형식**: Multipart Form Data
- **파라미터**:
  - file (MultipartFile): 변환할 오디오 파일 **(필수)**
  - model (String): 사용할 음성 인식 모델 **(필수)**
  - prompt (String): 음성 인식 힌트 *(선택)*
  - language (String): 오디오 언어 (ISO 639-1 코드) *(선택)*
  - responseFormat (String): 응답 형식 (json, text, srt, verbose_json) *(선택)*
  - stream (String): 스트리밍 응답 여부 *(선택)*
- **응답 타입**: `AudioTranscriptionResponse`
- **특징**:
  - 다양한 언어 지원
  - 타임스탬프 정보 제공
  - 화자 분리 기능

#### POST /api/v1/gateway/audio/translations
- **기능**: 음성 번역 (Speech Translation)
- **설명**: 다국어 오디오 파일을 영어 텍스트로 번역합니다.
- **요청 형식**: Multipart Form Data
- **파라미터**:
  - file (MultipartFile): 번역할 오디오 파일 **(필수)**
  - model (String): 사용할 음성 번역 모델 **(필수)**
  - prompt (String): 번역 힌트 *(선택)*
  - responseFormat (String): 응답 형식 *(선택)*
- **응답 타입**: `AudioTranscriptionResponse`
- **특징**: 음성 인식과 번역을 동시에 수행하는 원스톱 서비스

### 5. 검색 최적화

#### POST /api/v1/gateway/score
- **기능**: 텍스트 유사도 스코어링
- **설명**: 두 텍스트 간의 의미적 유사도를 측정합니다.
- **요청 타입**: `ScoreRequest`
- **응답 타입**: `ScoreResponse`
- **활용**: 검색 결과 평가, 문서 매칭, 중복 탐지

#### POST /api/v1/gateway/{api_version}/rerank
- **기능**: 검색 결과 리랭킹
- **설명**: 주어진 쿼리에 대해 여러 문서의 관련도를 재평가하여 순위를 재정렬합니다.
- **파라미터**:
  - apiVersion (String): API 버전 (예: "v1") **(필수)**
- **요청 타입**: `RerankRequest`
- **응답 타입**: `RerankResponse`
- **효과**: 검색 품질을 크게 향상시킬 수 있는 핵심 기능

### 6. 고급 기능

#### POST /api/v1/gateway/responses
- **기능**: 고급 응답 생성
- **설명**: 복잡한 추론과 구조화된 응답이 필요한 작업을 수행합니다.
- **요청 타입**: `ResponsesRequest`
- **응답 타입**: `ResponseGenerationResponse`
- **특징**: 일반 채팅보다 더 정교한 분석과 추론 능력 제공

#### POST /api/v1/gateway/custom/{api_path}
- **기능**: 커스텀 모델 엔드포인트
- **설명**: 사용자 정의 API 경로를 통해 특수 모델이나 실험적 기능에 접근합니다.
- **파라미터**:
  - apiPath (String): 커스텀 API 경로 (예: "stable-diffusion") **(필수)**
- **요청 타입**: `CustomRequest`
- **응답 타입**: `CustomEndpointResponse`
- **활용**: 표준 API로 제공되지 않는 고급 기능

## 데이터 타입 정의

### 요청 DTO 클래스
- `ChatCompletionsRequest`: 채팅 완성 요청
  - 메시지 배열, 모델 선택, 생성 파라미터 포함
- `CompletionsRequest`: 텍스트 완성 요청
  - 프롬프트, 모델, 생성 설정 포함
- `EmbeddingsRequest`: 임베딩 요청
  - 텍스트, 모델, 인코딩 형식 포함
- `ImagesRequest`: 이미지 생성 요청
  - 프롬프트, 모델, 크기, 품질 설정 포함
- `AudioSpeechRequest`: 음성 합성 요청
  - 텍스트, 모델, 음성, 속도 설정 포함
- `ScoreRequest`: 유사도 스코어링 요청
  - 비교할 텍스트들, 모델 포함
- `RerankRequest`: 리랭킹 요청
  - 쿼리, 문서 목록, 모델 포함
- `ResponsesRequest`: 고급 응답 생성 요청
  - 입력, 지시사항, 파라미터 포함
- `CustomRequest`: 커스텀 요청
  - 모델별 파라미터 포함

### 응답 DTO 클래스
- `ModelListResponse`: 모델 목록 응답
  - 사용 가능한 모델 정보 배열
- `ChatCompletionsResponse`: 채팅 완성 응답
  - 생성된 메시지, 사용량 정보, 메타데이터
- `CompletionsResponse`: 텍스트 완성 응답
  - 완성된 텍스트, 생성 정보
- `EmbeddingsResponse`: 임베딩 응답
  - 임베딩 벡터, 토큰 사용량 정보
- `ImagesResponse`: 이미지 생성 응답
  - 생성된 이미지 URL/데이터, 안전성 검사 결과
- `AudioSpeechResponse`: 음성 합성 응답
  - 생성된 오디오 파일 (바이너리 데이터)
- `AudioTranscriptionResponse`: 음성 인식/번역 응답
  - 인식된/번역된 텍스트, 메타데이터
- `ScoreResponse`: 유사도 스코어링 응답
  - 유사도 스코어, 메타데이터
- `RerankResponse`: 리랭킹 응답
  - 재정렬된 문서 목록, 관련도 스코어
- `ResponseGenerationResponse`: 고급 응답 생성 응답
  - 구조화된 응답, 추론 과정 정보
- `CustomEndpointResponse`: 커스텀 엔드포인트 응답
  - 모델별 응답 데이터

## 인증 및 권한

모든 API는 Bearer Token 인증을 사용합니다.

```
Authorization: Bearer {access_token}
```

## 에러 처리

표준 HTTP 상태 코드를 사용하며, OpenAI API와 호환되는 에러 형식을 제공합니다.

- `200`: 성공
- `400`: 잘못된 요청 데이터
- `401`: 인증 실패
- `403`: 접근 권한 없음
- `404`: API 경로를 찾을 수 없음
- `422`: 입력값 검증 실패
- `500`: 서버 내부 오류

## 사용 예시

### 채팅 완성 예시
```java
ChatCompletionsRequest request = ChatCompletionsRequest.builder()
    .model("gpt-4")
    .messages(Arrays.asList(
        ChatMessage.builder()
            .role("system")
            .content("당신은 친절한 AI 어시스턴트입니다.")
            .build(),
        ChatMessage.builder()
            .role("user")
            .content("안녕하세요!")
            .build()
    ))
    .maxTokens(150)
    .temperature(0.7)
    .build();

ChatCompletionsResponse response = modelGatewayClient.createChatCompletion(request);
```

### 이미지 생성 예시
```java
ImagesRequest request = ImagesRequest.builder()
    .model("dall-e-3")
    .prompt("a beautiful sunset over a mountain lake")
    .size("1024x1024")
    .quality("standard")
    .n(1)
    .build();

ImagesResponse response = modelGatewayClient.generateImages(request);
```

### 음성 합성 예시
```java
AudioSpeechRequest request = AudioSpeechRequest.builder()
    .model("tts-1")
    .input("안녕하세요, SKTAI입니다.")
    .voice("alloy")
    .speed(1.0)
    .build();

AudioSpeechResponse response = modelGatewayClient.createSpeech(request);
```

### 임베딩 생성 예시
```java
EmbeddingsRequest request = EmbeddingsRequest.builder()
    .model("text-embedding-ada-002")
    .input(Arrays.asList("Hello world", "안녕하세요"))
    .encodingFormat("float")
    .build();

EmbeddingsResponse response = modelGatewayClient.createEmbeddings(request);
```

### 음성 인식 예시
```java
MultipartFile audioFile = // 오디오 파일
AudioTranscriptionResponse response = modelGatewayClient.transcribeAudio(
    audioFile,
    "whisper-1",
    "Customer support call",  // 힌트
    "ko",                    // 한국어
    "json",                  // JSON 형식
    null                     // 스트리밍 없음
);
```

## OpenAI 호환성

Model Gateway는 OpenAI API와 높은 호환성을 제공하여 기존 OpenAI API 기반 애플리케이션을 쉽게 마이그레이션할 수 있습니다.

### 지원하는 OpenAI API 엔드포인트
- `/v1/models` → `/api/v1/gateway/models`
- `/v1/chat/completions` → `/api/v1/gateway/chat/completions`
- `/v1/completions` → `/api/v1/gateway/completions`
- `/v1/embeddings` → `/api/v1/gateway/embeddings`
- `/v1/images/generations` → `/api/v1/gateway/images/generations`
- `/v1/audio/speech` → `/api/v1/gateway/audio/speech`
- `/v1/audio/transcriptions` → `/api/v1/gateway/audio/transcriptions`
- `/v1/audio/translations` → `/api/v1/gateway/audio/translations`

### 추가 기능
- 검색 최적화: 유사도 스코어링, 리랭킹
- 고급 응답 생성: 복잡한 추론 작업
- 커스텀 엔드포인트: 실험적 기능 접근

## 성능 및 최적화

### 스트리밍 지원
- 채팅 완성과 텍스트 완성에서 실시간 스트리밍 응답 지원
- 긴 응답에 대한 사용자 경험 개선

### 배치 처리
- 임베딩 생성 시 여러 텍스트 동시 처리
- 효율적인 리소스 사용

### 캐싱
- 자주 사용되는 요청에 대한 캐싱 지원
- 응답 시간 단축 및 비용 절약

## 제한사항

### 요청 크기 제한
- 텍스트 요청: 최대 32K 토큰
- 이미지 업로드: 최대 20MB
- 오디오 파일: 최대 25MB

### 속도 제한 (Rate Limiting)
- 사용자별/API키별 요청 속도 제한
- 공정한 리소스 사용 보장

### 모델별 제한
- 각 모델별로 다른 입력/출력 제한
- 모델 문서 참조 필요
