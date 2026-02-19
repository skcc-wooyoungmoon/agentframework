import type { AuthInfo } from '@/constants/auth';

export type UIMoreMenuItem<TData> = {
  label: string; // 라벨
  action: string; // 액션
  auth?: AuthInfo; // 권한
  disabled?: boolean; // 비활성 여부
  visible?: (rowData: TData) => boolean; // 표시 여부를 결정하는 함수
  onClick: (rowData: TData) => void; // 클릭 이벤트
};

export type UIMoreMenuType<TData> = {
  isOpen: boolean; // 더보기 팝업 오픈여부
  x?: number; // 더보기 팝업 좌표 x
  y?: number; // 더보기 팝업 좌표 y
  data: TData | null; // 데이터
};
