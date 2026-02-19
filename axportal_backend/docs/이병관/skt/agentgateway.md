# SKTAI Agent Gateway API 클라이언트 문서

## 개요
SKTAI Agent Gateway API는 대화형 AI 에이전트의 관리, 대화 처리, 스레드 관리, 실행 제어 등의 기능을 제공하는 RESTful API입니다. 이 문서는 Agent Gateway 관련 Feign Client의 구현 현황과 API 명세를 정리합니다.

**문서 버전**: 8.0  
**최종 업데이트**: 2025-08-16  
**기준 소스**: src/main/java/com/skax/aiplatform/client/sktai/agentgateway/

## SKTAI Agent Gateway 클라이언트 목록

### 1. SktaiAgentGatewayClient
**패키지**: `com.skax.aiplatform.client.sktai.agentgateway`  
**기능**: Agent Gateway 통합 관리 - 에이전트, 스레드, 실행, 대화 처리

#### Agent Management APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| getAgents | 에이전트 목록 조회 | GET | /api/v1/agents | limit(Integer), order(String), after(String), before(String), projectId(String) | AgentListResponse |
| createAgent | 새 에이전트 생성 | POST | /api/v1/agents | AgentCreateRequest | AgentResponse |
| getAgent | 에이전트 상세 조회 | GET | /api/v1/agents/{agent_id} | agentId(String) | AgentResponse |
| updateAgent | 에이전트 수정 | PUT | /api/v1/agents/{agent_id} | agentId(String), AgentUpdateRequest | AgentResponse |
| deleteAgent | 에이전트 삭제 | DELETE | /api/v1/agents/{agent_id} | agentId(String) | void |

#### Chat & Communication APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| chat | 에이전트와 대화 | POST | /api/v1/chat | ChatRequest | ChatResponse |
| chatStream | 에이전트와 스트리밍 대화 | POST | /api/v1/chat/stream | ChatStreamRequest | Flux&lt;ChatStreamResponse&gt; |

#### Thread Management APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| createThread | 새 스레드 생성 | POST | /api/v1/threads | ThreadCreateRequest | ThreadResponse |
| getThread | 스레드 상세 조회 | GET | /api/v1/threads/{thread_id} | threadId(String) | ThreadResponse |
| updateThread | 스레드 수정 | PUT | /api/v1/threads/{thread_id} | threadId(String), ThreadUpdateRequest | ThreadResponse |
| deleteThread | 스레드 삭제 | DELETE | /api/v1/threads/{thread_id} | threadId(String) | void |

#### Message Management APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| createMessage | 스레드에 메시지 추가 | POST | /api/v1/threads/{thread_id}/messages | threadId(String), MessageCreateRequest | MessageResponse |
| getMessages | 스레드의 메시지 목록 조회 | GET | /api/v1/threads/{thread_id}/messages | threadId(String), limit(Integer), order(String), after(String), before(String) | MessageListResponse |
| getMessage | 특정 메시지 조회 | GET | /api/v1/threads/{thread_id}/messages/{message_id} | threadId(String), messageId(String) | MessageResponse |
| updateMessage | 메시지 수정 | PUT | /api/v1/threads/{thread_id}/messages/{message_id} | threadId(String), messageId(String), MessageUpdateRequest | MessageResponse |

#### Run Management APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| createRun | 스레드에서 실행 시작 | POST | /api/v1/threads/{thread_id}/runs | threadId(String), RunCreateRequest | RunResponse |
| getRuns | 스레드의 실행 목록 조회 | GET | /api/v1/threads/{thread_id}/runs | threadId(String), limit(Integer), order(String), after(String), before(String) | RunListResponse |
| getRun | 특정 실행 조회 | GET | /api/v1/threads/{thread_id}/runs/{run_id} | threadId(String), runId(String) | RunResponse |
| updateRun | 실행 상태 수정 | PUT | /api/v1/threads/{thread_id}/runs/{run_id} | threadId(String), runId(String), RunUpdateRequest | RunResponse |
| cancelRun | 실행 취소 | POST | /api/v1/threads/{thread_id}/runs/{run_id}/cancel | threadId(String), runId(String) | RunResponse |

#### Stream Management APIs
| 메서드명 | 기능 | HTTP 메서드 | 엔드포인트 | 파라미터 | 응답 타입 |
|---------|------|------------|-----------|----------|----------|
| createRunStream | 스트리밍 실행 시작 | POST | /api/v1/threads/{thread_id}/runs/stream | threadId(String), RunStreamCreateRequest | Flux&lt;RunStreamResponse&gt; |
| submitToolOutputs | 도구 출력 제출 | POST | /api/v1/threads/{thread_id}/runs/{run_id}/submit_tool_outputs | threadId(String), runId(String), ToolOutputsRequest | RunResponse |
| submitToolOutputsStream | 도구 출력 스트리밍 제출 | POST | /api/v1/threads/{thread_id}/runs/{run_id}/submit_tool_outputs/stream | threadId(String), runId(String), ToolOutputsStreamRequest | Flux&lt;RunStreamResponse&gt; |

**총 22개 API**

## 총 API 수
- **총 1개 클라이언트**
- **총 22개 API**

### 기능별 API 수
- Agent Management: 5개 API
- Chat & Communication: 2개 API
- Thread Management: 4개 API
- Message Management: 4개 API
- Run Management: 5개 API
- Stream Management: 3개 API

## 주요 기능
1. **에이전트 관리**: AI 에이전트 생성, 조회, 수정, 삭제
2. **대화 처리**: 텍스트 기반 대화 및 실시간 스트리밍
3. **스레드 관리**: 대화 컨텍스트를 위한 스레드 생성 및 관리
4. **메시지 관리**: 스레드 내 메시지 추가, 조회, 수정
5. **실행 제어**: 에이전트 실행 시작, 모니터링, 취소
6. **도구 연동**: 외부 도구 및 API 통합 지원
7. **스트리밍**: 실시간 응답 스트리밍

## 공통 설정
- **Base URL**: `${sktai.api.base-url}`
- **Configuration**: SktaiClientConfig
- **Authentication**: Bearer Token
- **Content-Type**: application/json

## 에러 처리
모든 API는 다음과 같은 공통 에러 응답을 따릅니다:
- **400**: 잘못된 요청 데이터
- **401**: 인증 실패
- **403**: 권한 부족
- **404**: 리소스를 찾을 수 없음
- **409**: 중복 또는 충돌
- **422**: 유효성 검증 실패
- **500**: 서버 내부 오류

## 주요 DTO 클래스

### Request DTO
- **Agent**: AgentCreateRequest, AgentUpdateRequest
- **Chat**: ChatRequest, ChatStreamRequest
- **Thread**: ThreadCreateRequest, ThreadUpdateRequest
- **Message**: MessageCreateRequest, MessageUpdateRequest
- **Run**: RunCreateRequest, RunUpdateRequest, RunStreamCreateRequest
- **Tool**: ToolOutputsRequest, ToolOutputsStreamRequest

### Response DTO
- **Agent**: AgentResponse, AgentListResponse
- **Chat**: ChatResponse, ChatStreamResponse
- **Thread**: ThreadResponse
- **Message**: MessageResponse, MessageListResponse
- **Run**: RunResponse, RunListResponse, RunStreamResponse

### 특별 기능
- **Reactive Streams**: Flux를 사용한 실시간 스트리밍 지원
- **Tool Integration**: 외부 도구 실행 결과 처리
- **Context Management**: 대화 컨텍스트 유지 및 관리
