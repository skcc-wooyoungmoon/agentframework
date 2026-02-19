import type { UIAlarmItemProps } from '@/components/UI/molecules/UIAlarmItem';

export interface AlarmItem extends Omit<UIAlarmItemProps, 'className'> {}

export interface AlarmGroup {
  /** 날짜 문자열 */
  date: string;
  /** 알람 아이템 목록 */
  items: AlarmItem[];
}

export interface UIAlarmGroupProps {
  /** 알람 그룹 데이터 배열 (children이 있으면 선택사항) */
  alarmData?: AlarmGroup[];
  /** 커스텀 자식 컴포넌트 */
  children?: React.ReactNode;
  /** 알람 아이템 클릭 이벤트 핸들러 */
  onItemClick?: (item: AlarmItem, event: React.MouseEvent<HTMLDivElement>) => void;
  /** 추가 CSS 클래스 */
  className?: string;
}
