# 🚀 AXPORTAL FRONTEND 개발 가이드

> 관련해서 지속적으로 업데이트 예정

## 📋 목차

### [01] [개발 환경 설정](#1-개발-환경-설정)

- [1.1 필수 도구](#11-필수-도구)
- [1.2 프로젝트 설정](#12-프로젝트-설정)

### [02] [기술 스택](#2-기술-스택)

- [2.1 Core](#21-core)
- [2.2 상태 관리](#22-상태-관리)
- [2.3 UI & 스타일링](#23-ui--스타일링)
- [2.4 HTTP & 유틸리티](#24-http--유틸리티)

### [03] [프로젝트 구조](#3-프로젝트-구조)

- [3.1 전체 구조](#31-전체-구조)

### [04] [개발 컨벤션](#4-개발-컨벤션)

- [4.1 폴더 구조 명명 규칙](#41-폴더-구조-명명-규칙)
- [4.2 내보내기 규칙](#42-내보내기-규칙)
- [4.3 기타 명명 규칙](#43-기타-명명-규칙)

### [05] [라우팅](#5-라우팅)

- [5.1 라우트 구조](#51-라우트-구조)
- [5.2 인증 가드](#52-인증-가드)

### [06] [API 통신](#6-api-통신)

- [6.1 API 훅 사용법](#61-api-훅-사용법)
- [6.2 API 응답 구조](#62-api-응답-구조)
- [6.3 실제 사용 예시](#63-실제-사용-예시)
- [6.4 Timeout & Cache 설정](#64-timeout--cache-설정)

### [07] [코드 작성 가이드](#7-코드-작성-가이드)

- [7.1 컴포넌트 작성](#71-컴포넌트-작성)
- [7.2 페이지 작성](#72-페이지-작성)
- [7.3 커스텀 훅 작성](#73-커스텀-훅-작성)
- [7.4 상태 관리 작성](#74-상태-관리-작성)
- [7.5 라우팅 설정](#75-라우팅-설정)
- [7.6 유틸리티 함수 작성](#76-유틸리티-함수-작성)
- [7.7 설정 파일 작성](#77-설정-파일-작성)
- [7.8 상수 정의](#78-상수-정의)
- [7.9 Modal](#79-modal)
- [7.10 사용자 정보](#710-사용자-정보)
- [7.11 페이지네이션](#711-페이지네이션)
- [7.12 레이어 팝업](#712-레이어-팝업)
- [7.13 뒤로가기 상태 복원 (useBackRestoredState)](#713-뒤로가기-상태-복원-usebackrestoredstate)

### [08] [Git 정책](#8-Git-정책)

---

## 1. 개발 환경 설정

### 1.1 필수 도구

#### Node.js & pnpm

- Node.js 18+
- pnpm

```bash
## 확인 방법
node --version  # v18.0.0 이상
npm --version   # 9.0.0 이상
```

### 1.2 프로젝트 설정

#### pnpm 설치

```bash
npm i -g pnpm
```

#### 의존성 설치

```bash
# 프로젝트 클론
git clone <repository-url>
cd axportal_frontend

# 의존성 설치
pnpm install

# Storybook
pnpm storybook

# 개발 환경 실행
## 외부 로컬, 개발
pnpm elocal # 외부 로컬 환경 (api target : 로컬 백 서버)
pnpm edev # 외부 개발 환경 (api target : 개발 백 서버)

## 내부 로컬, 개발 (설정 진행 중)
pnpm local    # 내부 로컬 환경
pnpm dev      # 내부 개발 환경

# 빌드 환경 실행
pnpm build --mode edev  # 외부 개발 빌드
pnpm build --mode dev  # 내부 개발 빌드
pnpm preview  # 빌드 결과 미리보기
```

## 2. 기술 스택

### 2.1 Core

- **React 19.1.0**: 최신 React 버전
- **TypeScript 5.8.3**: 타입 안정성
- **Vite 7.0.0**: 빌드 도구
- **Node >=18.17.0**
- **npm >=9.0.0**

### 2.2 상태 관리

- **TanStack Query 5.81.5**: 서버 상태 관리
- **Jotai 2.12.5**: 클라이언트 상태 관리

### 2.3 UI & 스타일링

- **TailwindCSS 4.1.11**: 유틸리티 기반 CSS
- **Storybook 9.0.17**: 컴포넌트 문서화

### 2.4 HTTP & 유틸리티

- **Axios 1.10.0**: HTTP 클라이언트
- **React Router 7.6.3**: 라우팅

## 3. 프로젝트 구조

### 3.1 전체 구조

```
axportal_frontend/
├── src/
│   ├── components/         # 컴포넌트
│   ├── pages/              # 페이지 컴포넌트
│   ├── hooks/              # 커스텀 훅
│   ├── services/           # API 서비스
│   ├── stores/             # 상태 관리
│   ├── routes/             # 라우팅 설정
│   ├── utils/              # 유틸리티 함수
│   ├── configs/            # 설정 파일
│   ├── constants/          # 상수 정의
│   └── design/             # 퍼블리싱
├── public/                 # 정적 자산
├── docs/                   # 문서
└── k8s/                   # 쿠버네티스 설정
```

## 4. 개발 컨벤션

### 4.1 폴더 구조 명명 규칙

> **참고**: 업무 구분은 `업무구분.md` 파일을 참고하세요.

### 4.2 내보내기 규칙

#### 인덱스 파일 구조

> **각 폴더마다 `index.ts` 파일 필수**

**통합 내보내기:**

```typescript
// src/components/index.ts
export * from './UI';
export * from './common';
```

### 4.3 네이밍 컨벤션

#### 타입 명명 규칙

- **Props 타입**: `[컴포넌트명]Props` (예: `DataTableProps`)
- **데이터 타입**: `[명사]Type` (예: `UserType`, `MenuType`)
- **요청/응답 타입**: `[동사][명사]Request/Response` (예: `CreateUserRequest`, `GetUserResponse`)

#### 페이지 컴포넌트

- [페이지명]Page

```typescript
// ✅ 올바른 예
function HomePage() {
  /* ... */
}
export default HomePage;
```

#### 이벤트 핸들러

- 메소드 정의시, handle[이벤트명]

```typescript
// ✅ 올바른 예
const handleSubmit = () => {
  /* ... */
};
const handleUserClick = () => {
  /* ... */
};
const handleDataChange = () => {
  /* ... */
};
```

- Props 타입 정의시, on[이벤트명]

```typescript
function HomeButton({ onClick, onClose }: { onClick: () => void; onClose: () => void }) {
  /* ... */
}
```

#### 페이지 계층

- **페이지 명**: `[명사조합]Page` (예: `HomePage`, `DataCtlgPage`)
- **폴더 구조**: `pages/[화면업무Lv1]/[화면업무Lv2]/[명사조합]Page.tsx`
- **라우트 설정**: `routes/` 하위에 화면업무Lv1 별 route config 파일

#### 서비스 계층

- **서비스 명**: `use[복합명사]` (예: `useData`, `userAuthSvc`)
- **폴더 구조**: `services/[업무Lv1]/useData.tsx` (백엔드 업무 기준)
- **서비스 파일**: 백엔드 컨트롤러와 1:1 매핑으로 작성
  - 예: `services/agent/useAgent.ts`, `services/model/useModel.ts`
- **함수명**: 백엔드 컨트롤러 메서드와 1:1 매핑
  - `create[복합명사]` → `useCreate[복합명사]` (예: `createSample` → `useCreateSample`)
  - `get[복합명사]s` → `useGet[복합명사]s` (예: `getSamples` → `useGetSamples`)
  - `get[복합명사]ById` → `useGet[복합명사]ById` (예: `getSampleById` → `useGetSampleById`)
  - `update[복합명사]` → `useUpdate[복합명사]` (예: `updateSample` → `useUpdateSample`)
  - `delete[복합명사]` → `useDelete[복합명사]` (예: `deleteSample` → `useDeleteSample`)
  - `[동사][복합명사]` → `use[동사][복합명사]` → (예: `authUserToken` → `useAuthUserToken` )

#### 상태관리 계층

- **store/context**: `[복합명사]Store`, (예: `UserStore`, `MenuStore`)
- **폴더 구조**: `stores/[업무Lv1]/`

#### 유틸리티 계층

- **유틸리티명**: 복합명사, camelCase (예: `date`, `strConv`)
- **폴더 구조**: `utils/[업무Lv1]/[복합명사].ts`

#### 설정/상수 계층

- **설정파일**: `[기능].config.ts` (예: `menu.config.ts`)
- **상수**: `CONSTANT_CASE` (예: `API_BASE_URL` )
- **폴더 구조**: `configs/`, `constants/` (필요시)

## 5. 라우팅

### 5.1 라우트 구조

**라우트와 페이지의 일관된 구조:**

- `src/routes/data/` ↔ `src/pages/data/`
- `src/routes/model/` ↔ `src/pages/model/`
- `src/routes/agent/` ↔ `src/pages/agent/`

#### 업무별 라우트 그룹화

- 1 Depth : 수정시, 팀과 상의 필요

```typescript
// src/routes/route.config.tsx
export const routeConfig: RouteType[] = [
  {
    id: "PROTECTED",
    path: "/",
    element: <MainLayout />,
    loader: protectedLoader,
    children: [
      { id: "HOME", path: "home", children: homeRouteConfig },
      { id: "DATA", path: "data", children: dataRouteConfig },
      { id: "MODEL", path: "model", children: modelRouteConfig },
      { id: "AGENT", path: "agent", children: agentRouteConfig },
      // ... 기타 업무별 라우트
    ],
  },
];
```

- 2 Depth : 업무별 영역으로 해당 부분 수정

```typescript
// src/routes/model/model-route.config.tsx
export const modelRouteConfig: RouteType[] = [
  {
    id: "MODEL_LIST",
    path: "list",
    element: <ModelListPage />,
    label: "모델 목록",
  },
  {
    id: "MODEL_DETAIL",
    path: "detail/:id",
    element: <ModelDetailPage />,
    label: "모델 상세",
  },
  {
    id: "MODEL_CREATE",
    path: "create",
    element: <ModelCreatePage />,
    label: "모델 생성",
  },
];
```

### 5.2 인증 가드

#### 라우트 권한 처리

> 각 업무에 맞게 gaurds/ 아래 작성

```typescript
// src/routes/guards/authLoaders.ts
import { redirect } from 'react-router-dom';

export const protectedLoader = async () => {
  const token = localStorage.getItem('accessToken');
  if (!token) {
    return redirect('/login');
  }
  return null;
};

export const publicLoader = async () => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    return redirect('/home');
  }
  return null;
};
```

## 6. API 통신

> 백엔드 컨트롤러와 1:1 매핑으로 작성

> ✨ 참고 : [samples.services.ts](/src/services/samples/samples.services.ts)

> ✨ 참고 : [TestFetchPage.tsx](/src/pages/test/TestFetchPage.tsx)

**폴더 구조:**

```
services/
└── [업무Lv1별]/            # 업무별 서비스 (업무구분.md 참조)
    ├── [컨트롤러명].services.ts  # API 훅
    └── types.ts            # 타입 정의
```

> 참고

### 6.1 API 훅 사용법

> react-query을 기반한 useApi 이용

> react-query와 유사하게 사용 가능 및 동작

#### 기본 쿼리 훅 : GET 용

- 아래 이용시 react-query 기능 사용 가능

```typescript
ApiQueryOptions : react-query의 useQuery hook 옵션 사용을 위한 타입
```

- 예시

```typescript
import { useApiQuery } from '@/hooks/common/api/useApi';

// 🔍 GET 단일 조회
export const useGetSamplesById = (
  // 개발자 정의
  params: GetSampleByIdRequest, // request할 요소 // 선택
  options?: ApiQueryOptions<GetSampleByIdResponse> // react-query옵션 사용 가능 // 선택
) => {
  return useApiQuery<GetSampleByIdResponse>({
    queryKey: ['samples', params.id.toString()], // 선택
    url: '/samples/{id}', // 필수
    params, // 선택
    ...options, // 선택
  });
};

// 📄 GET 목록 조회 (페이지네이션)
export const useGetSamples = (
  // 개발자 정의
  params?: GetSamplesRequest, // request할 요소 // 선택
  options?: ApiQueryOptions<PaginatedDataType<GetSamplesResponse>> // useQuery 옵션 사용 가능 // 선택
) => {
  return useApiQuery<PaginatedDataType<GetSamplesResponse>>({
    queryKey: ['samples-list', params], // 선택
    url: '/samples', // 필수
    params, // 선택
    ...options, // 선택
  });
};
```

#### 뮤테이션 훅 : PUT, POST, DELETE 용

- 아래 이용시 react-query 기능 사용 가능

```typescript
ApiMutationOptions : react-query의 useMutation hook 옵션 사용을 위한 타입
```

- 예시

```typescript
// src/services/data/useDataService.ts
import { useApiMutation } from '@/hooks/common/api/useApi';

// ➕ POST 생성
export const useCreateSample = (
  // 개발자 정의
  options?: ApiMutationOptions<string, CreateSampleRequest> // useMutation 옵션 사용 가능 // 선택
) => {
  return useApiMutation<string, CreateSampleRequest>({
    method: 'POST', // 필수
    url: '/samples', // 필수
    ...options,
  });
};

// ✏️ PUT 수정
export const useUpdateSample = (
  options?: ApiMutationOptions<{}, UpdateSampleRequest> // useMutation 옵션 사용 가능 // 선택
) => {
  return useApiMutation<{}, UpdateSampleRequest>({
    method: 'PUT', // 필수
    url: '/samples/{id}', // 필수
    ...options, // 선택
  });
};

// 🗑️ DELETE 삭제
export const useDeleteSample = (
  options?: ApiMutationOptions<{}, DeleteSampleRequest> // useMutation 옵션 사용 가능 // 선택
) => {
  return useApiMutation<{}, DeleteSampleRequest>({
    method: 'DELETE', // 필수
    url: '/samples/{id}', // 필수
    ...options, // 선택
  });
};
```

### 6.2 API 응답 구조

#### 표준 응답 형식

```typescript
// src/hooks/common/api/types.ts
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  path: string;
}

export interface PaginatedDataType<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
```

### 6.3 사용 예시

#### 6.3.1 GET 단일 조회 예제

```typescript
import { useGetSamplesById } from '@/services/samples/samples.services';
import { useState } from "react";

export function MyComponent() {
  const [sampleId, setSampleId] = useState(1);

  const { data, isLoading, error, isSuccess, refetch } = useGetSamplesById(
    { id: sampleId },
    {
      enabled: sampleId > 0, // 조건부 실행
      staleTime: 5 * 60 * 1000, // 5분간 캐시 유지
    }
  );

  if (isLoading) return <div>로딩 중...</div>;
  if (error) return <div>에러: {error.message}</div>;

  return (
    <div>
      {isSuccess && data && (
        <div>
          <h2>{data.fullName}</h2>
          <p>{data.email}</p>
        </div>
      )}
      <button onClick={() => refetch()}>새로고침</button>
    </div>
  );
}
```

#### 6.3.2 GET 목록 조회 예제 (페이지네이션)

```typescript
import { useGetSamples } from '@/services/samples/samples.services';
import { useState } from "react";

export function ListComponent() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  const { data, isLoading, error } = useGetSamples({
    page,
    size,
  });

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  if (isLoading) return <div>로딩 중...</div>;
  if (error) return <div>에러 발생</div>;

  return (
    <div>
      {/* 데이터 목록 */}
      {data?.content.map((item) => (
        <div key={item.id}>{item.fullName}</div>
      ))}

      {/* 페이지네이션 */}
      <div>
        <button
          onClick={() => handlePageChange(page - 1)}
          disabled={page === 0}
        >
          이전
        </button>
        <span>
          페이지 {page + 1} / {data?.totalPages}
        </span>
        <button
          onClick={() => handlePageChange(page + 1)}
          disabled={page >= (data?.totalPages || 0) - 1}
        >
          다음
        </button>
      </div>
    </div>
  );
}
```

#### 6.3.3 POST 생성 예제

```typescript
import { usePostSample } from '@/services/samples/samples.services';
import { useModal } from "@/stores/common/modal/useModal";
import { useState } from "react";

export function CreateComponent() {
  const [formData, setFormData] = useState({
    username: "",
    fullName: "",
    email: "",
    phoneNumber: "",
    department: "",
    position: "",
    isActive: true,
  });

  const { openAlert } = useModal();

  const { mutate: createSample, isPending } = useCreateSample({
    onSuccess: () => {
      openAlert({
        title: "성공",
        message: "데이터가 생성되었습니다!",
      });
      // 폼 초기화
      setFormData({
        username: "",
        fullName: "",
        email: "",
        phoneNumber: "",
        department: "",
        position: "",
        isActive: true,
      });
    },
    onError: (error) => {
      openAlert({
        title: "에러",
        message: "데이터 생성에 실패했습니다.",
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    createSample(formData);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={formData.username}
        onChange={(e) => setFormData({ ...formData, username: e.target.value })}
        placeholder="사용자명"
        required
      />
      <input
        type="text"
        value={formData.fullName}
        onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
        placeholder="이름"
        required
      />
      <input
        type="email"
        value={formData.email}
        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        placeholder="이메일"
        required
      />

      <button type="submit" disabled={isPending}>
        {isPending ? "생성 중..." : "생성하기"}
      </button>
    </form>
  );
}
```

#### 6.3.4 PUT 수정 예제

```typescript
import {
  useGetSamplesById,
  useUpdateSample,
} from '@/services/samples/samples.services';
import { useEffect, useState } from "react";

export function EditComponent({ id }: { id: number }) {
  const [formData, setFormData] = useState({
    username: "",
    fullName: "",
    email: "",
    phoneNumber: "",
    department: "",
    position: "",
    isActive: true,
  });

  // 기존 데이터 조회
  const { data: originalData, isSuccess } = useGetSamplesById({ id });

  // 수정 요청
  const { mutate: updateSample, isPending } = useUpdateSample({
    onSuccess: () => {
      console.log("수정 성공!");
    },
  });

  // 원본 데이터 로드 시 폼에 설정
  useEffect(() => {
    if (isSuccess && originalData) {
      setFormData({
        username: originalData.username,
        fullName: originalData.fullName,
        email: originalData.email,
        phoneNumber: originalData.phoneNumber,
        department: originalData.department,
        position: originalData.position,
        isActive: originalData.isActive,
      });
    }
  }, [originalData, isSuccess]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateSample({
      id,
      ...formData,
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* 폼 필드들... */}
      <button type="submit" disabled={isPending}>
        {isPending ? "수정 중..." : "수정하기"}
      </button>
    </form>
  );
}
```

#### 6.3.5 DELETE 삭제 예제

```typescript
import { useDeleteSample } from '@/services/samples/samples.services';

export function DeleteComponent({ id }: { id: number }) {
  const { mutate: deleteSample, isPending } = useDeleteSample({
    onSuccess: () => {
      console.log("삭제 성공!");
    },
  });

  const handleDelete = () => {
    if (confirm("정말 삭제하시겠습니까?")) {
      deleteSample({ id });
    }
  };

  return (
    <button
      onClick={handleDelete}
      disabled={isPending}
      className="bg-red-500 text-white px-4 py-2 rounded"
    >
      {isPending ? "삭제 중..." : "삭제"}
    </button>
  );
}
```

### 6.4 Timeout & Cache 설정

#### Timeout 설정

**전역 설정:**

- 환경 변수: `.env` 파일에 `VITE_API_TIMEOUT` 설정 (기본값: 10000ms)
- 코드 위치: `src/configs/axios.config.ts`

**개별 요청 timeout:**

```typescript
// useApiQuery 사용 시
const { data } = useApiQuery({
  url: '/api/endpoint',
  timeout: 60 * 1000, // 60초
});

// useApiMutation 사용 시
const { mutate: createData } = useApiMutation({
  method: 'POST',
  url: '/api/endpoint',
  timeout: 60 * 1000, // 60초
});
```

#### Cache 설정

**전역 기본 설정:**

- `staleTime`: 5분 (데이터가 fresh로 유지되는 시간)
- `gcTime`: 10분 (캐시에서 제거되기까지의 시간)
- 위치: `src/providers/common/QueryProvider.tsx`
- 수정 금지

**개별 쿼리 Cache 설정:**

```typescript
// 캐시 비활성화
const { data } = useApiQuery({
  url: '/api/endpoint',
  disableCache: true, // staleTime=0, gcTime=0 자동 설정
});

// 캐시 개별 지정
const { data } = useApiQuery({
  url: '/api/endpoint',
  staleTime: 10 * 60 * 1000, // 10분
  gcTime: 30 * 60 * 1000, // 30분
});
```

**권장사항:**

- 일반 API: timeout 15~30초, cache 5분
- 파일 업로드: timeout 60초 이상
- 실시간 데이터: disableCache: true

## 7. 코드 작성 가이드

### 7.1 컴포넌트 작성

**폴더 구조:**

```
components/
├── UI/                       # 퍼블리싱 컴포넌트
└── [화면Lv1]/
    ├── 역할/
    │   ├── [컴포넌트명].tsx
    │   ├── types.tsx         # 타입
    │   └── index.ts          # 내보내기
```

**코드 작성**

```typescript
// src/components/data/dataTable/DataTable.tsx
import React from "react";
import type { DataTableProps } from "./types";

export function DataTable({ data, columns, onRowClick }: DataTableProps) {
  return <div className="data-table">{/* 테이블 구현 */}</div>;
}
```

**UI 폴더 정의 (퍼블리싱 용)**

- **폴더명**: `UI[컴포넌트명]/` (예: `UIButton/`, `UIIcon/`)
- **메인 파일**: `component.tsx` (컴포넌트 로직)
- **타입 파일**: `types.ts` (Props 인터페이스)
- **상수 파일**: `constants.ts` (테마, 크기 등 상수)
- **인덱스 파일**: `index.ts` (통합 내보내기)

### 7.2 페이지 작성

**폴더 구조:**

```
pages/
├── [화면 Lv1]/
│   ├── [페이지명]Page.tsx        # 페이지
│   ├── index.ts                  # 내보내기
│   └── [화면 Lv2]
│       ├── [페이지명]Page.tsx    # 페이지
│       └── index.ts              # 내보내기
```

**코드 작성**

```typescript
// src/pages/data/DataPage.tsx
import React from "react";
import { DataTable } from "@/components/data/dataTable";

export default function DataPage() {
  return (
    <div className="data-page">
      <h1>데이터 관리</h1>
      <DataTable data={[]} columns={[]} />
    </div>
  );
}

// src/pages/data/index.ts
export { default as DataPage } from "./DataPage";
```

### 7.3 커스텀 훅 작성

> state와 action을 포함했을 경우 Hook으로 정의

**폴더 구조:**

```
hooks/
├── [화면 Lv1]/
│   ├── [역할]/
│   │   ├── use[hook명].ts
│   │   ├── types.ts
│   │   └── index.ts
└── index.ts              # 통합 내보내기
```

**코드 작성**

```typescript
// src/hooks/data/useDataHandler.ts
import { useState, useCallback } from 'react';
import type { DataHandlerHook } from './types';

export const useDataHandler = (): DataHandlerHook => {
  const [data, setData] = useState<any[]>([]);

  const handleDataUpdate = useCallback((newData: any[]) => {
    setData(newData);
  }, []);

  return {
    data,
    handleDataUpdate,
  };
};
```

### 7.4 상태 관리 작성

> Jotai를 이용했을 경우 Hook으로 작성

**폴더 구조:**

```
stores/
├── [화면 Lv1]/
│   ├── [역할]/
│   │   ├── use[기능명]Stores.ts    # 인증 상태 훅
│   │   ├── types.ts                # 타입
│   │   └── index.ts                # 내보내기
```

**코드 예시**

```typescript
// src/stores/common/auth/useAuthStores.ts
import { atom } from 'jotai';

// 기본 상태 정의
export const userAtom = atom<User | null>(null);
export const isAuthenticatedAtom = atom<boolean>(false);

// 액션 함수
export const useAuthStores = () => {
  const [user, setUser] = useAtom(userAtom);
  const [isAuthenticated, setIsAuthenticated] = useAtom(isAuthenticatedAtom);

  const login = (userData: User) => {
    setUser(userData);
    setIsAuthenticated(true);
  };

  const logout = () => {
    setUser(null);
    setIsAuthenticated(false);
  };

  return {
    user,
    isAuthenticated,
    login,
    logout,
  };
};
```

### 7.5 라우팅 설정

**폴더 구조:**

```
routes/
├── [화면Lv2]/
│   └── [화면Lv2]-route.config.tsx   # 2Dpeth 라우트 설정
├── guards/                         # 라우트 권한 처리
│   └── [화면Lv1]Loader.tsx         # 권한 처리 Loader 정의
├── AppRouter.tsx                   # 메인 라우터
├── route.config.tsx                # 메인(1Depth) 라우트 설정
└── types.ts                        # 라우트 타입
```

**코드 작성**

```typescript
// src/routes/data/data-route.config.tsx
import type { RouteType } from "../types";
import { DataPage } from "@/pages/data";

export const dataRouteConfig: RouteType[] = [
  {
    id: "DATA_LIST",
    path: "list",
    element: <DataPage />,
    label: "데이터 목록",
  },
  {
    id: "DATA_DETAIL",
    path: "detail/:id",
    element: <DataDetailPage />,
    label: "데이터 상세",
  },
];

// src/routes/types.ts
export interface RouteType {
  id: string;
  path: string;
  element: React.ReactNode;
  label?: string;
  children?: RouteType[];
  loader?: () => Promise<any>;
}
```

### 7.6 유틸리티 함수 작성

**폴더 구조:**

```
utils/
├── common/               # 공통 유틸리티
├── [화면Lv1]/
│   ├── [기능별].utils.ts       # 기능별 유틸리티
│   └── index.ts          # 내보내기
└── index.ts              # 통합 내보내기
```

**코드 작성**

```typescript
// src/utils/common/date.utils.ts
export const formatDate = (date: Date | string): string => {
  const d = new Date(date);
  return d.toLocaleDateString('ko-KR');
};

export const formatDateTime = (date: Date | string): string => {
  const d = new Date(date);
  return d.toLocaleString('ko-KR');
};

// src/utils/common/index.ts
export * from './date';
export * from './validation';
export * from './storage';
```

### 7.7 설정 파일 작성

**폴더 구조:**

```
configs/
├── axios.config.ts        # Axios 설정
└── menu.config.ts         # 메뉴 설정
```

### 7.8 상수 정의

**폴더 구조:**

```
constants/
├── common/                 # 공통 상수
├── [화면Lv1]/
│   ├── [역할].constants.ts
│   └── index.ts            # 내보내기
└── index.ts                # 통합 내보내기
```

#### Default 상수 정의

**스토리지 키 상수** (`src/constants/common/storage.constants.ts`)

- localStorage/sessionStorage 사용시, KEY 값 정의 후 사용

```typescript
export const STORAGE_KEYS = {
  RECENT_MENU_ITEMS: 'RECENT_MENU_ITEMS',
  ACCESS_TOKEN: 'ACCESS_TOKEN',
  REFRESH_TOKEN: 'REFRESH_TOKEN',
  EXPIRES_AT: 'EXPIRES_AT',
};
```

### 7.9 Modal

## useModal 훅 가이드

`useModal` 훅은 React 애플리케이션에서 모달과 팝업을 쉽게 관리할 수 있게 해주는 커스텀 훅입니다. Promise 기반으로 동작하며, 타입 안전성을 보장합니다.

### 🚀 주요 기능

- **Promise 기반 모달**: `async/await`를 사용하여 모달 결과를 처리할 수 있습니다
- **타입 안전성**: TypeScript를 통한 완벽한 타입 지원
- **자동 상태 관리**: Jotai를 사용한 전역 모달 상태 관리
- **접근성**: ESC 키 지원, Body scroll lock, Focus trap 등
- **에러 처리**: 모달 열기 실패 시 에러 처리 및 로깅

### 📦 사용법

```typescript
import { useModal } from '@/stores/common/modal';

function MyComponent() {
  const { openModal, openAlert, openConfirm, closeAllModals } = useModal();

  // 모달 사용 예시
  const handleOpenModal = async () => {
    const result = await openModal({...});
    console.log('Modal result:', result);
  };
}
```

### 🎯 모달 타입별 사용법

#### 1. 모달 (UIModal)

**큰 모달 (large)**

```typescript
const handleOpenLargeModal = async () => {
  const confirmed = await openModal({
    type: 'large',
    title: '큰 모달',
    showFooter: false, // 푸터 숨기기 (기본값 : true)
    body: (
      <div>
        <p>더 많은 내용을 표시할 수 있습니다</p>
        <div className="mt-4 p-3 bg-gray-100 rounded">
          <h4>추가 정보</h4>
          <ul>
            <li>첫 번째 항목</li>
            <li>두 번째 항목</li>
          </ul>
        </div>
      </div>
    ),
  });
};
```

**작은 모달 (small)**

```typescript
const handleOpenSmallModal = async () => {
  try {
    const confirmed = await openModal({
      type: 'small',
      title: '작은 모달',
      body: <div>모달 내용</div>,
      cancelText: '취소', // 기본값: '취소'
      confirmText: '확인', // 기본값: '확인'
      onConfirm: () => console.log('확인됨'),
      onCancel: () => console.log('취소됨'),
      onClickCloseButton: () => console.log('X 버튼 클릭'),
    });

    if (confirmed) {
      console.log('사용자가 확인했습니다');
    } else {
      console.log('사용자가 취소했습니다');
    }
  } catch (error) {
    console.error('모달 열기 실패:', error);
  }
};
```

#### 2. 팝업 (UIPopup)

**Alert 팝업**

```typescript
const handleOpenAlert = async () => {
  const confirmed = await openAlert({
    message: '알림 메시지입니다.\n여러 줄로 표시할 수 있습니다.',
    confirmText: '알겠습니다',
    title: '알림', // 선택사항, 있으면 헤더가 표시됩니다
    onConfirm: () => console.log('Alert 확인됨'),
  });
};
```

**Confirm 팝업**

```typescript
const handleOpenConfirm = async () => {
  const confirmed = await openConfirm({
    message: '정말로 진행하시겠습니까?',
    title: '확인', // 선택사항
    confirmText: '진행',
    cancelText: '취소',
    onConfirm: () => console.log('확인됨'),
    onCancel: () => console.log('취소됨'),
  });

  if (confirmed) {
    // 사용자가 확인한 경우
    await performAction();
  }
};
```

### 🔧 사용 예시

> ✨ 참고 : [TestModalPage](/src/pages/test/TestModalPage.tsx)

#### 폼 제출 확인

```typescript
const handleSubmit = async () => {
  const confirmed = await openConfirm({
    message: '입력한 정보로 제출하시겠습니까?',
    confirmText: '제출',
    cancelText: '취소',
  });

  if (confirmed) {
    await submitForm();
  }
};
```

#### 에러 알림

```typescript
const handleError = async (error: Error) => {
  await openAlert({
    message: `오류가 발생했습니다: ${error.message}`,
    confirmText: '확인',
  });
};
```

### ⚠️ 주의사항

1. **Promise 처리**: 모든 모달 함수는 Promise를 반환하므로 `async/await` 또는 `.then()`을 사용해야 합니다
2. **에러 처리**: `try-catch` 블록으로 모달 열기 실패를 처리하는 것이 좋습니다
3. **메모리 누수 방지**: 컴포넌트가 언마운트되기 전에 열린 모달을 정리하는 것이 좋습니다
4. **접근성**: 모달이 열려있을 때는 ESC 키로 닫을 수 있고, Body scroll이 자동으로 잠깁니다

### 🎛️ 고급 버튼 제어

모달의 버튼 동작을 세밀하게 제어할 수 있는 기능입니다. 두 번째 인자로 제어 옵션을 전달하여 특정 버튼만 활성화하거나 커스텀 동작을 정의할 수 있습니다.

#### 제어 옵션 설명

````typescript
// 두 번째 인자: 제어 옵션
{
  modalId: string,    // 모달 식별자 (필수)
  confirm?: boolean,  // 확인 버튼 제어 여부 (기본값: false)
  cancel?: boolean,   // 취소 버튼 제어 여부 (기본값: false)
}


**제어 옵션 동작**

- `confirm: true`: 확인 버튼 클릭 시 `onConfirm` 콜백만 실행되고, 자동으로 모달이 닫히지 않음
- `cancel: true`: 취소 버튼 클릭 시 `onCancel` 콜백만 실행되고, 자동으로 모달이 닫히지 않음
- `modalId`: 특정 모달을 식별하기 위한 고유 ID (상수로 관리 권장)

**사용 시나리오**

1. **비동기 작업 처리**: 확인 버튼 클릭 시 API 호출 후 결과에 따라 모달 닫기
2. **유효성 검사**: 폼 데이터 검증 후 조건에 따라 모달 유지/닫기
3. **다단계 처리**: 확인 후 추가 모달 표시 또는 다른 액션 실행
4. **커스텀 피드백**: 처리 결과에 따른 메시지 표시 후 모달 닫기

#### 모달 버튼 제어

**확인 버튼만 제어하는 경우**

```typescript
import { MODAL_ID } from '@/constants/modal/modalId.constants';

const handleOpenControlledModal = async () => {
  try {
    const confirmed = await openModal(
      {
        type: 'small',
        title: '제어된 모달',
        body: <div>확인 버튼만 커스텀 동작합니다.</div>,
        onConfirm: () => {
          // 커스텀 확인 로직
          closeModal(MODAL_ID.CUSTOM_MODAL);
          console.log('커스텀 확인 처리');
        },
        onCancel: () => {
          console.log('일반 취소 처리');
        },
      },
      {
        modalId: MODAL_ID.CUSTOM_MODAL, // 모달 식별자
        confirm: true,  // 확인 버튼만 제어
      }
    );
  } catch (error) {
    console.error('모달 오류:', error);
  }
};
````

#### Confirm 팝업 버튼 제어

**확인 버튼만 제어하는 경우**

```typescript
import { MODAL_ID } from '@/constants/modal/modalId.constants';

const handleOpenControlledConfirm = async () => {
  try {
    const confirmed = await openConfirm(
      {
        message: '정말로 이 작업을 진행하시겠습니까?\n확인 버튼은 커스텀 동작을 수행합니다.',
        confirmText: '진행',
        cancelText: '취소',
        onConfirm: () => {
          // 커스텀 확인 로직 실행 후 모달 닫기
          closeModal(MODAL_ID.CUSTOM_CONFIRM);
          console.log('커스텀 확인 처리 완료');
        },
        onCancel: () => {
          console.log('일반 취소 처리');
        },
      },
      {
        modalId: MODAL_ID.CUSTOM_CONFIRM, // 모달 식별자
        confirm: true, // 확인 버튼만 제어
      }
    );
  } catch (error) {
    console.error('Confirm 오류:', error);
  }
};
```

### 커스텀 Footer 사용

모달의 기본 footer 대신 body 내부에 커스텀 footer를 직접 구현하는 방법입니다. 이는 버튼의 상태를 동적으로 제어하거나 복잡한 UI 로직이 필요할 때 사용합니다.

#### 기본 설정

```typescript
const { openModal } = useModal();

const handleOpenCustomFooterModal = () => {
  openModal(
    {
      type: 'medium',
      title: '커스텀 Footer 모달',
      body: <CustomBodyComponent />, // 커스텀 body 컴포넌트
      useCustomFooter: true, // 커스텀 footer 사용 설정
    },
    {
      modalId: MODAL_ID.CUSTOM_FOOTER_MODAL,
      confirm: true, // 확인 버튼 제어
    }
  );
};
```

#### 커스텀 Body 컴포넌트 구현

```typescript
import { UIModalContent } from '@/components/UI/organisms';
import { MODAL_ID } from '@/constants/modal/modalId.constants';

const CustomBodyComponent = ({ type, id }: { type: string; id: string }) => {
  const { openAlert, closeModal } = useModal();
  const [formData, setFormData] = useState({
    value: 'user',
    etcName: ''
  });

  // 폼 데이터 변경 핸들러
  const handleChange = useCallback((value: string) => {
    setFormData(prev => ({ ...prev, value: value as 'user' | 'etc' }));
  }, []);

  const handleEtcNameChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, etcName: e.target.value }));
  }, []);

  // 버튼 비활성화 조건
  const isButtonDisabled = formData.value === 'etc' && formData.etcName.length === 0;

  return (
    <div>
      {/* 모달 내용 */}
      <UIRadioGroup
        name='toolType'
        title='구분'
        options={[
          { label: '사용자', value: 'user' },
          { label: '기타', value: 'etc' },
        ]}
        value={formData.value}
        onChange={handleChange}
        direction='vertical'
        spacing='normal'
        required={true}
      />

      {formData.value === 'etc' && (
        <div className='pt-4'>
          <label className='form-label block mb-2'>
            <span className='inline-flex items-center'>
              <span>이름</span>
              <span className='inline-block w-1 h-1 ml-1 bg-[#D61111] rounded-full' />
            </span>
          </label>
          <UIInput.Text
            value={formData.etcName}
            maxLength={50}
            onChange={handleEtcNameChange}
            placeholder='이름 입력'
            required={true}
          />
        </div>
      )}

      {/* 🔑 커스텀 Footer 구현 */}
      <UIModalContent.Footer
        type='modal-medium'
        positiveButton={{
          text: '확인',
          disabled: isButtonDisabled, // 동적 비활성화
          onClick: () => {
            // 비동기 작업 처리
            console.log('API KEY 발급 진행', formData, type, id);

            // 성공 후 알림 및 모달 닫기
            openAlert({
              message: 'API Key 발급이 완료되었습니다.',
              onConfirm: () => {
                closeModal(MODAL_ID.API_KEY_ISSUE);
              },
            });
          },
        }}
      />
    </div>
  );
};
```

#### 사용 시나리오

**1. 동적 버튼 상태 제어**

```typescript
// 폼 유효성 검사에 따른 버튼 비활성화
const isButtonDisabled = formData.value === 'etc' && formData.etcName.length === 0;

<UIModalContent.Footer
  positiveButton={{
    text: '확인',
    disabled: isButtonDisabled, // 조건부 비활성화
    onClick: handleSubmit,
  }}
/>
```

**2. 복잡한 버튼 로직**

```typescript
<UIModalContent.Footer
  type='modal-medium'
  positiveButton={{
    text: isLoading ? '처리 중...' : '확인',
    disabled: isLoading || !isValid,
    onClick: handleComplexSubmit,
  }}
  negativeButton={{
    text: '취소',
    onClick: handleCancel,
  }}
/>
```

**3. 조건부 버튼 표시**

```typescript
<UIModalContent.Footer
  type='modal-medium'
  positiveButton={{
    text: '다음',
    onClick: handleNext,
  }}
  negativeButton={currentStep > 1 ? {
    text: '이전',
    onClick: handlePrevious,
  } : undefined}
/>
```

#### 장점

1. **세밀한 제어**: 버튼의 상태, 텍스트, 동작을 완전히 제어 가능
2. **동적 UI**: 폼 상태에 따른 실시간 버튼 상태 변경
3. **복잡한 로직**: 다단계 처리, 조건부 버튼 표시 등 복잡한 UI 로직 구현
4. **사용자 경험**: 로딩 상태, 유효성 검사 등으로 더 나은 UX 제공

#### 주의사항

1. **useCustomFooter: true** 설정 필수
2. **모달 제어 옵션**과 함께 사용하여 버튼 동작 제어
3. **UIModalContent.Footer** 컴포넌트 사용 권장
4. **접근성** 고려하여 적절한 버튼 상태 표시

### 7.10 사용자 정보

## useUserStores 사용 방법

`useUserStores`는 사용자 정보를 전역적으로 관리하는 Jotai 기반 스토어입니다.

### 기본 사용법

```tsx
import { useUserStores } from '@/stores/auth';

const MyComponent = () => {
  const { user, updateUser, clearUser, formattedUserInfo } = useUserStores();

  return (
    <div>
      <p>사용자: {formattedUserInfo}</p>
      <p>이메일: {user.email}</p>
      <p>프로젝트: {user.project.name}</p>
    </div>
  );
};
```

### 주요 기능

#### 1. 사용자 정보 조회

```tsx
const { user } = useUserStores();

// 사용자 기본 정보
console.log(user.id); // 사용자 ID
console.log(user.username); // 사용자명
console.log(user.email); // 이메일
console.log(user.first_name); // 이름
console.log(user.last_name); // 성
```

### 사용자 타입 구조

```tsx
type UserType = {
  id: string;
  username: string;
  email: string;
  first_name: string;
  last_name: string;
  roles: RoleType[];
  project: {
    id: string;
    name: string;
  };
  groups: any[];
};

type RoleType = {
  id: string;
  name: string;
  composite: boolean;
  clientRole: boolean;
  containerId: string;
};
```

### 7.11 페이지네이션

> 업데이트 예정

### 7.12 레이어 팝업

> ✨ 참고 : [TestLayerPopupPage](/src/pages/test/TestLayerPopupPage.tsx)

**Props 구조**

#### LayerPopupProps (훅에서 반환되는 Props)

```typescript
export type LayerPopupProps = {
  currentStep: number; // 현재 단계 (0: 닫힘, 1~: 열림)
  stepperItems?: UIStepperItem[]; // 스테퍼 아이템 (선택사항)
  onNextStep: () => void; // 다음 단계 핸들러
  onPreviousStep: () => void; // 이전 단계 핸들러
  onClose: () => void; // 닫기 핸들러
};
```

**사용 방법**

#### 1. 단일 레이어팝업 (기본 팝업)

```tsx
import { UILayerPopup } from '@/components/UI/organisms';

// 1. LayerPopupProps 정의
const SinglePopupComponent = ({ currentStep, onClose }: LayerPopupProps) => {
  return (
    <UILayerPopup
      {/* 1. open : step을 1로 정의 */}
      isOpen={currentStep === 1} // 🔑 핵심: step === 1일 때만 열림
      {/* 2. onClose 전달 */}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <h1 className='text-xl font-bold text-gray-900'>단일 팝업</h1>
          <div className='flex justify-start gap-2'>
            <UIButton onClick={onClose}>취소</UIButton>
            <UIButton onClick={onClose}>확인</UIButton>
          </div>
        </UIPopupAside>
      }
    >
      {/* 우측 콘텐츠 영역 */}
    </UILayerPopup>
  );
};
```

#### 2. 다수 레이어팝업 (스테퍼 포함)

```tsx
// 컴포넌트
import { UIStepper, UILayerPopup } from '@/components/UI/organisms';
import type { UIStepperItem } from '@/components/UI/molecules';

/* 1단계 팝업 */
// 1. LayerPopupProps 정의
const StepperPopupComponent1 = (
  currentStep,
  stepperItems = [],
  onClose,
  onNextStep,
  onPreviousStep,
): LayerPopupProps => {
  return (
    <UILayerPopup
      {/* 2. open : 해당 팝업의 step 입력  */}
      isOpen={currentStep === 1} // 🔑 1단계일 때만 열림
      {/* 3. onClose 전달 */}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <h1 className='text-xl font-bold text-gray-900'>1단계</h1>
          {/* 4. stepper props 전달 : currentStep, stepperItems */}
          <UIStepper
            currentStep={currentStep}
            items={stepperItems}
            direction='vertical'
            className='w-full'
          />
          <div className='flex justify-start gap-2'>
            <UIButton onClick={onClose}>취소</UIButton>
            <UIButton onClick={onNextStep}>다음</UIButton>
          </div>
        </UIPopupAside>
      }
    >
      {/* 1단계 내용 */}
    </UILayerPopup>
  );
}

/* 2단계 팝업 */
// 1. LayerPopupProps 정의
const StepperPopupComponent2 = (
  currentStep,
  stepperItems = [],
  onClose,
  onNextStep,
  onPreviousStep,
): LayerPopupProps => {
  return (
    <UILayerPopup
      {/* 2. open : 해당 팝업의 step 입력  */}
      isOpen={currentStep === 2} // 🔑 2단계일 때만 열림
      {/* 3. onClose 전달 */}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <h1 className='text-xl font-bold text-gray-900'>2단계</h1>
          {/* 4. stepper props 전달 : currentStep, stepperItems */}
          <UIStepper
            currentStep={currentStep}
            items={stepperItems}
            direction='vertical'
            className='w-full'
          />
          <div className='flex justify-start gap-2'>
            <UIButton onClick={onClose}>취소</UIButton>
            <UIButton onClick={onPreviousStep}>이전</UIButton>
            <UIButton onClick={onNextStep}>다음</UIButton>
          </div>
        </UIPopupAside>
      }
    >
      {/* 2단계 내용 */}
    </UILayerPopup>
  );
}

/* 3단계 팝업 */
// 1. LayerPopupProps 정의
const StepperPopupComponent3 = ({
  currentStep,
  stepperItems = [],
  onPreviousStep,
  onClose,
}: LayerPopupProps) => {
  return (
    <UILayerPopup
      {/* 2. open : 해당 팝업의 step 입력  */}
      isOpen={layerPopup.currentStep === 3} // 🔑 3단계일 때만 열림
      {/* 3. onClose 전달 */}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <h1 className='text-xl font-bold text-gray-900'>3단계</h1>
          {/* 4. stepper props 전달 : currentStep, stepperItems */}
          <UIStepper
            currentStep={currentStep}
            items={stepperItems}
            direction='vertical'
            className='w-full'
          />
          <div className='flex justify-start gap-2'>
            <UIButton onClick={onClose}>취소</UIButton>
            <UIButton onClick={layerPopup.onPreviousStep}>이전</UIButton>
            <UIButton onClick={onClose}>완료</UIButton>
          </div>
        </UIPopupAside>
      }
    >
      {/* 3단계 내용 */}
    </UILayerPopup>
  );
};
```

** 페이지 사용 예시 (TestLayerPopupPage 참고)**

```tsx
export function TestLayerPopupPage() {
  const layerPopupOne = useLayerPopup(); // 단일 팝업용
  const layerPopupStepper = useLayerPopup(); // 스테퍼 팝업용

  // 단일 레이어팝업 열기
  const handleOpenSinglePopup = () => {
    layerPopupOne.onOpen(); // step = 1
  };

  // 다수 레이어팝업 열기
  const handleOpenMultiplePopup = () => {
    layerPopupStepper.onOpen(); // step = 1
  };

  return (
    <div>
      {/* 단일 팝업 */}
      <TEST_DT_030101_P01 {...layerPopupOne} stepperItems={stepperItems} />

      {/* 다수 팝업들 */}
      <TEST_DT_030101_P02 {...layerPopupStepper} stepperItems={stepperItems} />
      <TEST_DT_030101_P03 {...layerPopupStepper} stepperItems={stepperItems} />
      <TEST_DT_030101_P04 {...layerPopupStepper} stepperItems={stepperItems} />
    </div>
  );
}
```

### 7.13 뒤로가기 상태 복원 (useBackRestoredState)

> ✨ 참고 : [DeployModelListPage.tsx](/src/pages/deploy/model/DeployModelListPage.tsx)

**목적**: 목록 페이지에서 뒤로가기 시 검색 조건 및 페이지네이션 상태를 자동 복원합니다.

#### 기본 사용법

```tsx
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  status: string;
  view: string;
}

export function MyListPage() {
  // 검색 조건 (sessionStorage에 자동 저장)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(
    STORAGE_KEYS.SEARCH_VALUES.MY_LIST, // 스토리지 키
    {
      page: 1,
      size: 12,
      searchKeyword: '',
      status: 'all',
      view: 'grid',
    }
  );

  // API 호출
  const { data, refetch } = useGetMyList({
    page: searchValues.page,
    size: searchValues.size,
    search: searchValues.searchKeyword,
    status: searchValues.status === 'all' ? undefined : searchValues.status,
  });

  // 검색 조건 변경 시 자동 refetch
  useEffect(() => {
    refetch();
  }, [searchValues.page, searchValues.size, searchValues.searchKeyword, searchValues.status, refetch]);

  // 조회 버튼 핸들러
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page: number) => {
    setSearchValues(prev => ({ ...prev, page }));
  };

  return (
    <>
      {/* 검색 영역 */}
      <UIInput.Search
        value={searchValues.searchKeyword}
        onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
        onKeyDown={e => {
          if (e.key === 'Enter') {
            handleSearch();
          }
        }}
      />
      <UIDropdown value={searchValues.status} onSelect={value => setSearchValues(prev => ({ ...prev, status: value }))} />

      {/* 목록 영역 */}
      <UIDropdown
        value={String(searchValues.size)}
        onSelect={(value: string) => {
          setSearchValues(prev => ({ ...prev, size: Number(value), page: 1 }));
        }}
      />
      <UIToggle checked={searchValues.view === 'card'} onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))} />

      {/* 페이지네이션 */}
      <UIPagination currentPage={searchValues.page} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} />
    </>
  );
}
```

#### 스토리지 키 관리

**상수 파일에 키 추가** (`src/constants/common/storage.constants.ts`)

```typescript
export const STORAGE_KEYS = {
  SEARCH_VALUES: {
    // ... 기존 키들
    MY_LIST: 'MY_LIST', // 새 키 추가
  },
} as const;
```

**페이지에서 사용**

```typescript
const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(
  STORAGE_KEYS.SEARCH_VALUES.MY_LIST, // 상수로 관리
  {
    /* 초기값 */
  }
);
```

## 8. Git 정책

- [Git 가이드 참고](docs/git-guide.md)

---

## 📚 참고 자료

- **업무 구분**: `업무구분.md` - 업무별 모듈 구조 및 명명 규칙
- **프로젝트 규칙**: `.cursor/rules/project_rule.mdc` - 상세한 프로젝트 규칙
- **약어 규칙**: `.cursor/rules/abbreviation-rule.mdc` - 공통 약어 정의

## 💡 개발 팁

> **💡 팁**: 이 가이드를 참고하여 일관된 코드 구조와 명명 규칙을 유지하세요.

> 새로운 기능 추가 시 기존 패턴을 따라주시고, 변경이 필요한 경우 전체 팀과 상의 후 진행해주세요.

> 문의 사항 : 배연호/김예리

### 주요 체크 포인트

1. **폴더 구조**: 업무별로 명확하게 분리되어 있는지 확인
2. **명명 규칙**: 컴포넌트, 함수, 변수명이 일관된 규칙을 따르는지 확인
3. **타입 정의**: TypeScript 타입이 적절히 정의되어 있는지 확인
4. **내보내기**: 각 폴더에 `index.ts` 파일이 있고 적절히 내보내고 있는지 확인
