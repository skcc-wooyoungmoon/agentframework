# Git Branch 운영 가이드

본 문서는 현 저장소의 브랜치 운영, 커밋, 머지 규칙을 명확히 하기 위한 가이드입니다.

---

## 📂 브랜치 구조

- **2depth 브랜치(dev-yyy)**: 업무 도메인별 상위 브랜치
- **3depth 브랜치(dev-yyy-xxx)**: 개인/기능 단위 개발 브랜치
  - `xxx` 형식: `{이니셜}, {기능명}`  
    예) `dev-cmm-hkh`

---

## 📌 2depth 업무명 규칙 (업무 도메인별)

| 업무명   | 브랜치명 예시 | 사용자                 |
| -------- | ------------- | ---------------------- |
| 공통     | dev-cmm       | 김예리, 황규희         |
| 관리     | dev-admin     | 민경민, 문규빈, 권두현 |
| 데이터   | dev-data      | 이혜리, 윤요섭         |
| 모델     | dev-model     | 김예리, TBD            |
| 에이전트 | dev-agent     | 황규희, TBD            |
| 배포     | dev-deploy    | TBD                    |
| 모니터링 | dev-mon       | TBD                    |
| 로그     | dev-log       | TBD                    |

---

## 🔄 브랜치 운영 규칙

1. **작업은 항상 3depth 브랜치에서 진행**
   - 예) `dev-cmm-hkh`
2. **머지 방향**
   - 3depth → 2depth: `git pull origin HEAD` 후 **Pull Request(PR)** 생성
     - 제목 : [업무] [관련 내용]
   - 2depth → main: **특정 승인자** 승인 필수
     - 제목 : [PR] [머지될 브랜치명] (예: [PR] dev-cmm )
     - 승인자: `문태진`, `민경민`
3. **main 브랜치 머지 권한**은 승인자만 보유

---

## 📝 커밋 규칙 

- `{commit type}: 메시지` 형식 사용  
  예) `feat: Response DTO class 생성`
- 메시지는 최대한 구제척으로 작성 요망

### Commit Type 목록

| 타입     | 설명                            |
| -------- | ------------------------------- |
| feat     | 새로운 기능 추가                |
| fix      | 버그 수정                       |
| docs     | 문서 수정                       |
| refactor | 코드 리팩토링 (기능 변화 없음)  |
| chore    | 빌드/환경 설정, 라이브러리 변경 |

---

## ⚠ 머지/충돌 처리 규칙

- **상위(모) 브랜치에만 머지**
- 머지 시에는 **Pull Request(PR)** 이용
- **conflict** 발생 시, 코드 작성자들과 협의 후 해결
- `revert`, `reset` 사용은 가급적 자제

---

## 🛠 명령어 예시

### 3depth → 2depth 머지

```bash
# 현재 3depth 브랜치에서
git pull origin HEAD
# GitHub에서 dev-cmm (업무별)으로 Pull Request 생성

# 승인자만 PR 생성 가능
# GitHub에서 main 브랜치로 Pull Request 생성 후 승인 요청


---
```
