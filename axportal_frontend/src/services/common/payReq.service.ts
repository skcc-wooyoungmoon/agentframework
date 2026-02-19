import { type ApiMutationOptions, type ApiQueryOptions, useApiQuery } from '@/hooks/common/api';
import { useApiMutation } from '@/hooks/common/api/useApi';

import type { ApproverRes, CheckApprovalStatusRequest, CheckApprovalStatusResponse, GetProjUserList, PaymentApprovalRequest, PaymentApprovalResponse, ProjUserInfo } from './types';

/**
 * @description 사용자의 읽은 알림 목록 조회
 */
export const useUserGetMe = (options?: ApiQueryOptions<ApproverRes>) => {
  const username = sessionStorage.getItem('USERNAME') || '';
  return useApiQuery<ApproverRes>({
    queryKey: ['alarms', 'read', username],
    url: `/home/alarms/${username}/read`,
    enabled: !!username, // username이 있을 때만 API 호출
    ...options,
  });
};

/**
 * @description 결재자 선택을 위한 사용자 목록 조회
 */
export const useGetApproverList = (params?: GetProjUserList, options?: ApiQueryOptions<ProjUserInfo>) => {
  const mergedOptions: ApiQueryOptions<ProjUserInfo> = {
    staleTime: 0 as any,
    refetchOnMount: true as any,
    ...options,
  };

  return useApiQuery<ProjUserInfo>({
    url: '/home/project/join-user-list', // ProjCreStep2MemSel과 동일한 API 사용
    params,
    ...mergedOptions,
  });
};

export const useSubmitPaymentApproval = (options?: ApiMutationOptions<PaymentApprovalResponse, PaymentApprovalRequest>) => {
  return useApiMutation<PaymentApprovalResponse, PaymentApprovalRequest>({
    method: 'POST',
    url: '/common/payreq',
    ...options,
  });
};

export const useCheckApprovalStatus = (params: CheckApprovalStatusRequest, options?: ApiQueryOptions<CheckApprovalStatusResponse>) => {
  return useApiQuery<CheckApprovalStatusResponse>({
    url: '/common/approvalStatus',
    params,
    ...options,
    disableCache: true,
  });
};
