import { useApiQuery, type ApiQueryOptions } from '@/hooks/common/api';

import type { GetScalingGroupsRequest, GetScalingGroupsResponse } from './types';

/**
 * 스케일링 그룹 목록 조회
 * @description Lablup Backend.AI 시스템의 스케일링 그룹 목록을 조회합니다.
 * 각 그룹의 설정, 드라이버 옵션, 스케줄러 정보 및 자원 할당량을 확인할 수 있습니다.
 *
 * @param request 스케일링 그룹 목록 조회 요청 파라미터
 * @param options React Query 옵션 (staleTime, refetchOnMount 등)
 * @returns 스케일링 그룹 목록 조회 결과
 */
export const useGetScalingGroups = (request?: GetScalingGroupsRequest, options?: ApiQueryOptions<GetScalingGroupsResponse>) => {
  return useApiQuery<GetScalingGroupsResponse>({
    queryKey: ['scaling-groups', request?.isActive?.toString() ?? 'all'],
    url: '/resources/scaling_groups',
    params: request?.isActive !== undefined ? { is_active: request.isActive.toString() } : undefined,
    ...options,
  });
};
