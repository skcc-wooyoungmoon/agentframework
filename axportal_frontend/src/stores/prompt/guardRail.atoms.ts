import { atom } from 'jotai';

import type { GetGuardRailPromptListResponse } from '@/services/prompt/guardRail/types';

// 선택된 가드레일 프롬프트 데이터
export const selectedGuardRailPromptAtom = atom<GetGuardRailPromptListResponse | null>(null);

