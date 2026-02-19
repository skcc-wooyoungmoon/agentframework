export interface UIToastProps {
  /** 토스트 고유 ID */
  id?: string;
  type?: UIToastType;
  /** 토스트 메시지 */
  message: string;
  /** 애니메이션 상태 (react-hot-toast의 t.visible) */
  visible?: boolean;
}

export const UIToastTypeEnum = {
  DEFAULT: 'DEFAULT',
  SUCCESS: 'SUCCESS',
  ERROR: 'ERROR',
  WARNING: 'WARNING',
  INFO: 'INFO',
} as const;

export type UIToastType = (typeof UIToastTypeEnum)[keyof typeof UIToastTypeEnum];
