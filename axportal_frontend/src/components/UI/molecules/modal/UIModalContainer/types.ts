import type { UIModalType } from '@/components/UI/organisms/modal';

export type UIBaseModalContainerProps = {
  type: UIModalType;
  children: React.ReactNode;
  className?: string;
};
