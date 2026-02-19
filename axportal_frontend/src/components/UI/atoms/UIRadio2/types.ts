import type { InputHTMLAttributes, ReactNode } from 'react';

export interface UIRadioProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'className' | 'type' | 'onChange'> {
  /** 라디오 버튼과 함께 표시될 라벨 텍스트 */
  label?: string;
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 카드 내용 (card variant에서 사용) */
  children?: ReactNode;
  /** 값 변경 시 호출되는 콜백 함수 */
  onChange?: (checked: boolean, value: string) => void;
}
