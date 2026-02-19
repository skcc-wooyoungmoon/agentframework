import type { AuthInfo } from '@/constants/auth';

export interface ProjectInfoBoxProps {
  assets?: Array<{ type: string; id: string }>;
  auth?: AuthInfo;
}
