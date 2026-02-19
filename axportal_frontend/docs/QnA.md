# Q&A

## 1. AgentBuild 화면의 Tab 구조 설계

### 상황

- AgentBuild 화면에 Agent, Model에 대한 Tab이 있는 경우

### 구현 방식

#### 1) 독립적인 Tab 구조 (50:50 비율)

- **AgentController**와 **ModelController**에서 각각 Method를 호출하여 화면 구현
- 각 Tab이 독립적으로 동작

#### 2) 종속적인 구조 (Agent 중심)

- Agent 화면에서 Model 정보가 일부 필요한 경우
- **AgentController**가 주도적으로 데이터를 조합하여 반환

### 아키텍처 흐름

```
AgentController
    ↓
AgentService
    ↓
AgentRepository
    ↓
ModelService (추가 호출)
    ↓
ModelRepository
```

### 개발 원칙

- 호출 후 데이터를 조합해서 반환하는 형태로 개발
- 단일 Controller에서 필요한 모든 데이터를 수집하여 클라이언트에 전달
