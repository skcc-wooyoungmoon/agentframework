export interface UIAlarmItemProps {
  /** 알람 아이템 고유 ID */
  id: string;
  /** 알람 제목 */
  title: string;
  /** 알람 상세 내용 */
  description: string;
  /** 알람 시간 */
  time: string;
  /** 읽음 여부 */
  isRead?: boolean;
  /** 제목 스타일 타입 */
  type?: 'dot' | 'normal';
  /** 클릭 이벤트 핸들러 */
  onClick?: (item: { id: string; title: string; description: string; time: string; isRead?: boolean }, event: React.MouseEvent<HTMLDivElement>) => void;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 우측 액션 버튼 (옵션) */
  actionButton?: React.ReactNode;
}
