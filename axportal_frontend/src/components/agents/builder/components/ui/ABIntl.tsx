/**
 * react-intl 완전 대체 - 순수 React 기반 다국어 지원
 * react-intl 의존성 완전 제거
 */
import React, { createContext, useState } from 'react';

// Agent Builder 전용 메시지 정의
const agentBuilderMessages = {
  ko: {
    // 공통
    'common.save': '저장',
    'common.cancel': '취소',
    'common.delete': '삭제',
    'common.edit': '편집',
    'common.search': '검색',
    'common.reset': '초기화',
    'common.apply': '적용',
    'common.close': '닫기',
    'common.confirm': '확인',

    // 그래프
    'graph.save': '그래프 저장',
    'graph.load': '그래프 불러오기',
    'graph.saveSuccess': '그래프가 성공적으로 저장되었습니다.',
    'graph.saveError': '그래프 저장 중 오류가 발생했습니다.',
    'graph.loadError': '그래프 불러오기 중 오류가 발생했습니다.',

    // 노드
    'node.llm': 'LLM',
    'node.prompt': '프롬프트',
    'node.tools': '도구',
    'node.knowledge': '지식베이스',
    'node.fewshot': '퓨샷',
    'node.categorizer': '분류기',
    'node.condition': '조건',

    // 채팅
    'chat.title': '채팅',
    'chat.placeholder': '메시지를 입력하세요...',
    'chat.send': '전송',
    'chat.clear': '대화 초기화',

    // 테이블
    'table.noData': '데이터가 없습니다.',
    'table.loading': '로딩 중...',
    'table.search': '테이블 검색',
    'table.pagination.showing': '총 {total}개 중 {start}-{end}개 표시',

    // 모달
    'modal.selectPrompt': '프롬프트 선택',
    'modal.selectTools': '도구 선택',
    'modal.selectKnowledge': '지식베이스 선택',
    'modal.selectFewShot': '퓨샷 선택',

    // 토스트 메시지
    'toast.success': '성공',
    'toast.error': '오류',
    'toast.warning': '경고',
    'toast.info': '정보',

    // 에러 메시지
    'error.canvasLoad': '캔버스 로드 중 오류가 발생했습니다',
    'error.agentIdMissing': 'Agent ID가 없습니다',
    'error.invalidPath': '올바른 경로로 접근해주세요.',
    'error.loading': '에러가 발생했습니다: {message}',
    'error.agentNotFound': '에이전트를 찾을 수 없습니다.',
    'error.agentLoadFailed': '에이전트 정보를 불러오는데 실패했습니다.',

    // 버튼 텍스트
    'button.retry': '다시 시도',
    'button.goBack': '뒤로 가기',
    'button.refresh': '페이지 새로고침',

    // 상태 메시지
    'status.loading': '로딩 중...',
    'status.builderCanvas': '빌더 캔버스',
    'status.agent': '에이전트: {name}',
    'status.description': '설명: {description}',
    'status.nodeCount': '노드 수: {count}',
    'status.edgeCount': '엣지 수: {count}',

    // 노드 목록
    'nodeList.title': '노드 목록',
    'nodeList.input': '입력 노드',
    'nodeList.output': '출력 노드',
    'nodeList.generator': '생성기 노드',
    'nodeList.tools': '도구 노드',

    // 기본값
    'default.noName': '이름 없음',
    'default.noDescription': '설명 없음',
  },
  en: {
    // 공통
    'common.save': 'Save',
    'common.cancel': 'Cancel',
    'common.delete': 'Delete',
    'common.edit': 'Edit',
    'common.search': 'Search',
    'common.reset': 'Reset',
    'common.apply': 'Apply',
    'common.close': 'Close',
    'common.confirm': 'Confirm',

    // 그래프
    'graph.save': 'Save Graph',
    'graph.load': 'Load Graph',
    'graph.saveSuccess': 'Graph saved successfully.',
    'graph.saveError': 'Error occurred while saving graph.',
    'graph.loadError': 'Error occurred while loading graph.',

    // 노드
    'node.llm': 'LLM',
    'node.prompt': 'Prompt',
    'node.tools': 'Tools',
    'node.knowledge': 'Knowledge',
    'node.fewshot': 'Few-shot',
    'node.categorizer': 'Categorizer',
    'node.condition': 'Condition',

    // 채팅
    'chat.title': 'Chat',
    'chat.placeholder': 'Type a message...',
    'chat.send': 'Send',
    'chat.clear': 'Clear Chat',

    // 테이블
    'table.noData': 'No data available.',
    'table.loading': 'Loading...',
    'table.search': 'Search table',
    'table.pagination.showing': 'Showing {start}-{end} of {total} entries',

    // 모달
    'modal.selectPrompt': 'Select Prompt',
    'modal.selectTools': 'Select Tools',
    'modal.selectKnowledge': 'Select Knowledge',
    'modal.selectFewShot': 'Select Few-shot',

    // 토스트 메시지
    'toast.success': 'Success',
    'toast.error': 'Error',
    'toast.warning': 'Warning',
    'toast.info': 'Info',

    // 에러 메시지
    'error.canvasLoad': 'An error occurred while loading the canvas',
    'error.agentIdMissing': 'Agent ID is missing',
    'error.invalidPath': 'Please access through the correct path.',
    'error.loading': 'An error occurred: {message}',
    'error.agentNotFound': 'Agent not found.',
    'error.agentLoadFailed': 'Failed to load agent information.',

    // 버튼 텍스트
    'button.retry': 'Retry',
    'button.goBack': 'Go Back',
    'button.refresh': 'Refresh Page',

    // 상태 메시지
    'status.loading': 'Loading...',
    'status.builderCanvas': 'Builder Canvas',
    'status.agent': 'Agent: {name}',
    'status.description': 'Description: {description}',
    'status.nodeCount': 'Node Count: {count}',
    'status.edgeCount': 'Edge Count: {count}',

    // 노드 목록
    'nodeList.title': 'Node List',
    'nodeList.input': 'Input Node',
    'nodeList.output': 'Output Node',
    'nodeList.generator': 'Generator Node',
    'nodeList.tools': 'Tools Node',

    // 기본값
    'default.noName': 'No Name',
    'default.noDescription': 'No Description',
  },
};

// Agent Builder Intl Context
const ABIntlContext = createContext<{
  locale: string;
  setLocale: (locale: string) => void;
  messages: Record<string, string>;
} | null>(null);

interface ABIntlProviderProps {
  children: React.ReactNode;
  defaultLocale?: string;
}

export const ABIntlProvider: React.FC<ABIntlProviderProps> = ({ children, defaultLocale = 'ko' }) => {
  const [locale, setLocale] = useState(defaultLocale);

  const messages = agentBuilderMessages[locale as keyof typeof agentBuilderMessages] || agentBuilderMessages.ko;

  return <ABIntlContext.Provider value={{ locale, setLocale, messages }}>{children}</ABIntlContext.Provider>;
};