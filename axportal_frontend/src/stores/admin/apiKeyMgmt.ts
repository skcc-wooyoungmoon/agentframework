import { atom } from 'jotai';
import type { AdminApiKeyInfo } from '@/services/deploy/apikey/types';

// 선택된 API Key 데이터를 저장하는 atom (Jotai만 사용)
export const selectedApiKeyAtom = atom<AdminApiKeyInfo | null>(null);

// API Key 모니터링 검색 조건을 저장하는 atom
export interface ApiKeyMonitorSearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  projectType: string;
  searchKeyword: string;
  status: string;
  agentType: string;
  startTime: string;
  endTime: string;
}

// 오늘 날짜를 YYYY.MM.DD 형식으로 반환
const getTodayDate = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${year}.${month}.${day}`;
};

// 현재 시간을 시간 단위로 올림 처리
const getCurrentTimeCeiled = () => {
  const now = new Date();
  const currentHour = now.getHours();
  // 시간 단위로 올림 처리 (예: 14:30 → 15시)
  const ceiledHour = currentHour === 23 ? 24 : currentHour + 1;
  return `${String(ceiledHour).padStart(2, '0')}시`;
};

export const apiKeyMonitorSearchAtom = atom<ApiKeyMonitorSearchValues>({
  dateType: '생성일시',    
  dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
  searchType: '최근 24시간',
  projectType: '전체',
  searchKeyword: '',
  status: '전체',
  agentType: '전체',
  startTime: getCurrentTimeCeiled(),
  endTime: getCurrentTimeCeiled(),
});

export const apiKeyMonitorDateAtom = atom<string>(getTodayDate());