# Agent Builder Module

에이전트 빌더 모듈은 SKT AI Platform과 연동하여 AI 에이전트를 생성하고 관리하는 기능을 제공합니다.

## 📁 모듈 구조

```
src/components/agents/builder/
├── index.ts                 # 메인 export 파일
├── README.md               # 이 파일
├── atoms/                  # 상태 관리 (Jotai)
│   ├── index.ts           # Atoms export
│   ├── AgentAtom.ts       # 에이전트 관련 상태
│   ├── toolsAtom.ts       # 도구 선택 상태
│   └── logAtom.ts         # 로그 상태
├── components/             # 재사용 가능한 컴포넌트
│   └── index.ts           # Components export
├── hooks/                  # 커스텀 훅
│   ├── index.ts           # Hooks export
│   ├── useNodeDataLoader.ts    # 노드 데이터 로딩
│   ├── useGraphActions.ts      # 그래프 액션
│   ├── useGraphHandlers.ts     # 그래프 이벤트 핸들러
│   ├── useModal.ts             # 모달 관리
│   ├── useFilter.ts            # 필터링
│   └── (useDebounceSearch.ts 제거됨)
├── pages/                  # 페이지 컴포넌트
│   ├── BuilderPage.tsx     # 빌더 메인 페이지
│   ├── GraphPage.tsx       # 그래프 페이지
│   ├── GraphPageById.tsx   # 특정 그래프 페이지
│   ├── modal/              # 모달 컴포넌트
│   ├── graph/              # 그래프 관련 컴포넌트
│   └── table/              # 테이블 컴포넌트
├── services/               # API 서비스
│   ├── index.ts           # Services export
│   ├── agent/             # 에이전트 API
│   ├── prompts/           # 프롬프트 API
│   ├── tools/             # 도구 API
│   ├── knowledge/         # 지식 API
│   └── common/            # 공통 API
├── types/                  # TypeScript 타입 정의
│   ├── Agents.ts          # 에이전트 관련 타입
│   └── InferencePrompts.ts # 프롬프트 관련 타입
├── utils/                  # 유틸리티 함수
│   └── DnDContext.tsx     # 드래그 앤 드롭 컨텍스트
└── providers/              # React Context Provider
```

## 🚀 주요 기능

### 1. 에이전트 빌더 캔버스

- ReactFlow 기반의 시각적 에이전트 빌더
- 드래그 앤 드롭으로 노드 추가/삭제
- 노드 간 연결 및 데이터 흐름 정의

### 2. 노드 타입

- **Generator**: LLM, 프롬프트, Few-shot, 도구 선택
- **Retriever**: 문서 검색 및 필터링
- **Memory**: 대화 기록 관리
- **Classifier**: 분류 및 카테고리화
- **Translator**: 다국어 번역
- **Custom**: 사용자 정의 노드

### 3. API 연동

- SKT AI Platform API와 연동
- 프롬프트 템플릿 관리
- 도구 및 지식베이스 연동
- 에이전트 배포 및 서빙

## 📦 사용법

```typescript
import {
  BuilderPage,
  GraphPage,
  useNodeDataLoader,
  useGraphActions
} from '@/components/agents/builder';

// 빌더 페이지 사용
<BuilderPage />

// 그래프 페이지 사용
<GraphPage />

// 커스텀 훅 사용
const { isLoading, error } = useNodeDataLoader({ nodes });
const { saveAgent } = useGraphActions();
```

## 🔧 개발 가이드

### 새로운 노드 타입 추가

1. `types/Agents.ts`에 노드 타입 정의
2. `pages/graph/node/`에 노드 컴포넌트 생성
3. `hooks/useNodeDataLoader.ts`에 데이터 로딩 로직 추가

### 새로운 API 서비스 추가

1. `services/` 디렉토리에 API 클라이언트 생성
2. `services/index.ts`에 export 추가
3. 필요한 타입을 `types/` 디렉토리에 정의

### 상태 관리

- Jotai를 사용한 전역 상태 관리
- `atoms/` 디렉토리에 상태 정의
- 컴포넌트에서 `useAtom` 훅으로 상태 접근

## 🐛 디버깅

### 콘솔 로그

- API 호출 시 응답 구조 확인
- 노드 데이터 로딩 상태 확인
- 에러 발생 시 상세 정보 출력

### 개발자 도구

- React DevTools로 상태 변화 추적
- Network 탭에서 API 호출 확인
- Console에서 에러 메시지 확인

## 📝 주의사항

1. **API 응답 구조**: SKT AI Platform API 응답 구조에 맞게 데이터 처리
2. **상태 동기화**: 노드 데이터와 전역 상태 간 동기화 유지
3. **에러 처리**: API 호출 실패 시 적절한 에러 처리
4. **성능 최적화**: 불필요한 리렌더링 방지

## 🔄 업데이트 히스토리

- **2024.01**: 모듈화 구조 개선
- **2024.01**: 프롬프트 템플릿 기능 추가
- **2024.01**: 도구 선택 기능 개선
- **2024.01**: 에러 처리 및 디버깅 개선

## 📊 기존 코드 vs 모듈화된 코드 비교

### 1. **파일 구조의 차이**

#### 기존 구조 (Before)

```
src/components/agents/builder/
├── index.ts                 # 단순한 export만
├── pages/
│   ├── BuilderPage.tsx
│   ├── GraphPage.tsx
│   └── ...
├── hooks/
│   ├── useNodeDataLoader.ts
│   ├── useGraphActions.ts
│   └── ... (개별 파일들)
├── atoms/
│   ├── AgentAtom.ts
│   ├── toolsAtom.ts
│   └── ... (개별 파일들)
└── services/
    ├── agent/
    ├── prompts/
    └── ... (개별 디렉토리들)
```

#### 모듈화된 구조 (After)

```
src/components/agents/builder/
├── index.ts                 # 체계적인 export 구조
├── README.md               # 문서화 추가
├── atoms/
│   ├── index.ts           # Atoms 통합 export
│   ├── AgentAtom.ts
│   ├── toolsAtom.ts
│   └── ...
├── hooks/
│   ├── index.ts           # Hooks 통합 export
│   ├── useNodeDataLoader.ts
│   ├── useGraphActions.ts
│   └── ...
├── components/
│   ├── index.ts           # Components 통합 export
│   └── ...
└── services/
    ├── index.ts           # Services 통합 export
    └── ...
```

### 2. **Export 방식의 차이**

#### 기존 코드 (Before)

```typescript
// index.ts
export { BuilderPage, BuilderList } from './pages/BuilderPage';
export { default as GraphPage } from './pages/GraphPage';

// 타입들 export
export type { Agent, CustomEdge, CustomNode } from './types/Agents';

// 유틸리티들 export
export { DnDProvider } from './utils/DnDContext';

// Atoms export
export { edgesAtom, keyTableAtom, nodesAtom } from './atoms/AgentAtom';
```

#### 모듈화된 코드 (After)

```typescript
// index.ts - 메인 export
export { BuilderPage, BuilderList } from './pages/BuilderPage';
export { default as GraphPage } from './pages/GraphPage';
export { default as GraphPageById } from './pages/GraphPageById';

// Types - 체계적으로 정리
export type {
  Agent,
  CustomEdge,
  CustomNode,
  CustomNodeInnerData,
  GeneratorDataSchema,
  KeyTableData,
  InputKeyItem,
} from './types/Agents';

// Hooks - 통합 export
export * from './hooks';

// Atoms & State Management - 통합 export
export * from './atoms';

// Utilities
export { DnDProvider } from './utils/DnDContext';

// Services - 통합 export
export * from './services';

// Components - 통합 export
export * from './components';
```

### 3. **개별 모듈의 Export 구조**

#### 기존: 개별 파일에서 직접 import

```typescript
// 다른 파일에서 사용할 때
import { useNodeDataLoader } from './hooks/useNodeDataLoader';
import { useGraphActions } from './hooks/useGraphActions';
import { edgesAtom, nodesAtom } from './atoms/AgentAtom';
import { selectedListAtom } from './atoms/toolsAtom';
import { logState } from './atoms/logAtom';
```

#### 모듈화: 통합된 import

```typescript
// 다른 파일에서 사용할 때
import {
  useNodeDataLoader,
  useGraphActions,
  edgesAtom,
  nodesAtom,
  selectedListAtom,
  logState,
} from '@/components/agents/builder';
```

### 4. **새로 추가된 파일들**

#### hooks/index.ts

```typescript
// ============================================================================
// Agent Builder Hooks - Exports
// ============================================================================

// Core Graph Hooks
export { useNodeDataLoader } from './useNodeDataLoader';
export { useGraphActions } from './useGraphActions';
export { useGraphHandlers } from './useGraphHandlers';

// UI & Interaction Hooks
export { useModal } from './useModal';
export { useFilter } from './useFilter';
// useDebounceSearch 제거됨
export { useNodeValidation } from './useNodeValidation';

// Quick Start Actions
export { QuickStartActions } from './QuickStartActions';
```

#### atoms/index.ts

```typescript
// ============================================================================
// Agent Builder Atoms - State Management Exports
// ============================================================================

// Core Agent Atoms
export * from './AgentAtom';
export * from './toolsAtom';
export * from './logAtom';
```

### 5. **주요 개선점**

#### 🚀 **개발자 경험 개선**

- **단일 import**: 모든 기능을 하나의 import로 가져올 수 있음
- **명확한 구조**: 각 기능이 어디에 있는지 명확히 알 수 있음
- **자동완성**: IDE에서 더 나은 자동완성 지원

#### 📚 **문서화**

- **README.md**: 모듈 사용법과 구조를 명확히 문서화
- **주석**: 각 export 섹션에 명확한 주석 추가
- **가이드**: 개발 가이드와 디버깅 방법 포함

#### 🔧 **유지보수성**

- **관심사 분리**: 각 디렉토리가 명확한 역할을 가짐
- **확장성**: 새로운 기능 추가 시 명확한 위치에 배치
- **재사용성**: 컴포넌트와 훅의 독립적인 사용 가능

#### 🐛 **디버깅 개선**

- **불필요한 로그 제거**: 프로덕션 환경에 적합
- **에러 로그 유지**: 디버깅에 필요한 로그는 유지
- **명확한 에러 메시지**: 더 나은 에러 추적 가능

### 6. **실제 사용 예시**

#### 기존 방식

```typescript
// 여러 파일에서 개별 import
import { useNodeDataLoader } from '@/components/agents/builder/hooks/useNodeDataLoader';
import {
  edgesAtom,
  nodesAtom,
} from '@/components/agents/builder/atoms/AgentAtom';
import { selectedListAtom } from '@/components/agents/builder/atoms/toolsAtom';
import { logState } from '@/components/agents/builder/atoms/logAtom';
```

#### 모듈화된 방식

```typescript
// 단일 import로 모든 기능 사용
import {
  useNodeDataLoader,
  edgesAtom,
  nodesAtom,
  selectedListAtom,
  logState,
} from '@/components/agents/builder';
```

이러한 모듈화를 통해 코드의 가독성, 유지보수성, 재사용성이 크게 향상되었습니다! 🚀
