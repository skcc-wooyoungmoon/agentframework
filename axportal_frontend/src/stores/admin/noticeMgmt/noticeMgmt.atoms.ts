import { atom, useAtom } from 'jotai';
import type { NoticeDetailData } from './types';

// 공지사항 유형 옵션
export interface NoticeTypeOption {
  value: string;
  label: string;
}

// 공지사항 유형 목록 atom
export const noticeTypeOptionsAtom = atom<NoticeTypeOption[]>([
  { value: '시스템 점검', label: '시스템 점검' },
  { value: '서비스 출시 및 오픈', label: '서비스 출시 및 오픈' },
  { value: '보안 안내', label: '보안 안내' },
  { value: '이용 가이드', label: '이용 가이드' },
  { value: '버전/기능 업데이트', label: '버전/기능 업데이트' },
  { value: '기타', label: '기타' }
]);

// 공지사항 검색 조건 타입
export interface NoticeSearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  modelType: string;
}

// 공지사항 검색 조건 atoms
export const noticeSearchValuesAtom = atom<NoticeSearchValues>({
  dateType: '수정일시',
  dateRange: { 
    startDate: '', 
    endDate: '' 
  },
  searchType: '제목',
  searchKeyword: '',
  status: '전체',
  modelType: '전체',
});

// 공지사항 서버 데이터
export const noticeServerDataAtom = atom<any[]>([]);

// 공지사항 상세 데이터 atoms
export const selectedNoticeDetailAtom = atom<NoticeDetailData | null>(null);

export const setSelectedNoticeDetailAtom = atom(
  null,
  (_get, set, noticeData: NoticeDetailData) => {
    set(selectedNoticeDetailAtom, noticeData);
  }
);

export const clearSelectedNoticeDetailAtom = atom(
  null,
  (_get, set) => {
    set(selectedNoticeDetailAtom, null);
  }
);

/**
 * 공지사항 관리 상태를 관리하는 커스텀 훅
 * @returns {Object} 공지사항 상태와 제어 메소드들을 포함한 객체
 */
export const useNoticeMgmt = () => {
  const [selectedNoticeDetail] = useAtom(selectedNoticeDetailAtom);
  const [noticeTypeOptions] = useAtom(noticeTypeOptionsAtom);

  return {
    selectedNoticeDetail,
    noticeTypeOptions,
  };
};