import type { UIButtonProps } from '@/components/UI/atoms/UIButton2';
import type { AuthInfo } from '@/constants/auth';

export interface ButtonProps extends UIButtonProps {
  auth?: AuthInfo;
}
