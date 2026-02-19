export type UITextLabelIntent =
  | 'blue' //릴리즈 버전
  | 'gray' //마지막 버전
  | 'tag' //태그
  | 'violet' //보라색
  | 'red'; //빨간색

export interface UITextLabelProps {
  /** 라벨 색상 테마 */
  intent?: UITextLabelIntent;
  children?: React.ReactNode;
  /** 추가 CSS 클래스 */
  className?: string;
}
