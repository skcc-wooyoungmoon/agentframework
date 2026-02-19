import type { PromptData } from '@/services/model/playground/types.ts';
import { useGetInfPromptMsgsById, useGetInfPromptVerListById } from '@/services/prompt/inference/inferencePrompts.services';

interface UsePromptDataReturn {
  promptDataList: PromptData[];
  isLoading: boolean;
  error?: any;
}

/**
 * 여러 프롬프트의 데이터를 가져오는 커스텀 훅
 * @param promptUuids 프롬프트 UUID 배열
 * @returns 프롬프트 데이터 리스트와 로딩 상태
 */
export const usePromptData = (promptUuids: string[]): UsePromptDataReturn => {
  // 최대 10개의 프롬프트만 처리하도록 제한 (Hook 개수 고정)
  const maxPrompts = 10;
  const limitedPromptUuids = promptUuids.slice(0, maxPrompts);

  // 각 프롬프트에 대해 버전 데이터를 가져오는 훅들 (고정된 개수)
  const versionQueries = Array.from({ length: maxPrompts }, (_, index) => {
    const promptUuid = limitedPromptUuids[index];
    return useGetInfPromptVerListById({ promptUuid: promptUuid || '' }, { enabled: !!promptUuid });
  });

  // 각 프롬프트의 릴리즈 버전을 찾기 (없으면 첫 번째 항목 반환)
  const releaseVersions = versionQueries.map(query => {
    const versions = query.data?.versions;
    if (!versions || versions.length === 0) return undefined;

    const releaseVersion = versions.find((version: any) => version.release === true);
    return releaseVersion || versions[0]; // release가 true인 버전이 없으면 첫 번째 항목 반환
  });

  // 각 릴리즈 버전에 대해 메시지 데이터를 가져오는 훅들 (고정된 개수)
  const messageQueries = Array.from({ length: maxPrompts }, (_, index) => {
    const releaseVersion = releaseVersions[index];
    return useGetInfPromptMsgsById({ versionUuid: releaseVersion?.versionUuid || '' }, { enabled: !!releaseVersion?.versionUuid });
  });

  // 프롬프트 데이터 리스트 생성 (실제 선택된 프롬프트만)
  const promptDataList: PromptData[] = limitedPromptUuids.map((promptUuid, index) => {
    const versionQuery = versionQueries[index];
    const releaseVersion = releaseVersions[index];
    const messageQuery = messageQueries[index];

    return {
      promptUuid,
      versionUuid: releaseVersion?.versionUuid,
      messages: messageQuery?.data?.messages || [],
      isLoading: versionQuery?.isLoading || messageQuery?.isLoading || false,
      error: versionQuery?.error || messageQuery?.error,
    };
  });

  // 전체 로딩 상태
  const isLoading = versionQueries.some(query => query.isLoading) || messageQueries.some(query => query.isLoading);

  // 에러 상태
  const error = versionQueries.find(query => query.error)?.error || messageQueries.find(query => query.error)?.error;

  return {
    promptDataList,
    isLoading,
    error,
  };
};
