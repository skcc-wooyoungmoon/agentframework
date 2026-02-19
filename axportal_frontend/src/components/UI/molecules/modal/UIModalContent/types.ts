import type { UIModalType } from '@/components/UI/organisms/modal';
import type { AuthInfo } from '@/constants/auth';

export type UIModalHeaderProps = {
  type: UIModalType;
  title: string;
  onClose: () => void;
  showCloseButton?: boolean;
  onClickCloseButton?: () => void;
};
export type UIModalBodyProps = {
  type: string;
  children?: React.ReactNode;
  useCustomFooter?: boolean;
  showHeader?: boolean;
  contentAlign?: 'left' | 'center' | 'right'; // TODO: 추후 수정
  showFooter?: boolean;
};

export type UIModalFooterProps = {
  type: string;
  /** 부정 버튼 (취소/이전) - 왼쪽에 위치 */
  negativeButton?: {
    text: string;
    auth?: AuthInfo;
    onClick?: () => void;
    disabled?: boolean;
  };
  /** 긍정 버튼 (확인/다음) - 오른쪽에 위치 */
  positiveButton?: {
    text: string;
    auth?: AuthInfo;
    onClick?: () => void;
    disabled?: boolean;
  };
};
