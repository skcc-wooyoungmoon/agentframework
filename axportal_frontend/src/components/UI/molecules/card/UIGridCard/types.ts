import type { UIButtonProps } from '@/components/UI/atoms/UIButton2';
import type { UICheckboxProps } from '@/components/UI/atoms/UICheckbox2';
import type { UIMoreMenuConfig } from '../../grid';
/** 체크박스 그룹 옵션 */
export interface UIGridCardRows {
  /** 그리드 컬럼명 */
  label: string;
  /** 그리드 값 (문자열 또는 배열) */
  value?: string | string[];
}

export interface UIGridCardProps<TData = any> {
  id?: string;
  data?: TData;
  onClick?: (e: React.MouseEvent<HTMLDivElement>) => void;
  /** 진행률 */
  progressValue?: number;
  /** 체크박스 */
  checkbox?: UICheckboxProps;
  /** 상태 영역 */
  statusArea?: React.ReactNode;
  /** 타이틀 */
  title: React.ReactNode;
  /** 캡션. */
  caption?: React.ReactNode;
  /** rows data */
  rows: UIGridCardRows[];
  /** 더보기 버튼 클릭 이벤트 */
  moreButton?: UIButtonProps;
  /** 너비 */
  width?: string | number;

  moreMenuConfig?: UIMoreMenuConfig<TData> | null;
}
