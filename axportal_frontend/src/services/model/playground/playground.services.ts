import { useApiMutation } from '@/hooks/common/api';
import type { ModelPlaygroundChatRequest, ModelPlaygroundChatResponse } from './types';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants.ts';

/**
 * 모델 플레이그라운드 채팅 완성 생성
 *
 * @returns useApiMutation 훅
 */
export const useCreateModelPlaygroundChat = () => {
  return useApiMutation<ModelPlaygroundChatResponse, ModelPlaygroundChatRequest>({
    mutationKey: [DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/model-playground/chat',
    method: 'POST',
    timeout: 2 * 60 * 1000 + 10 * 1000,
  });
};
