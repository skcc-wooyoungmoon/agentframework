import type { ApiMutationOptions, ApiQueryOptions } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import type {
  AlarmInfo,
  ApprovalUserReponse,
  ApprovalUserRequest,
  CancelAlarmRequest,
  CreateAlarmRequest,
  MarkAlarmReadRequest,
  ReadAlarmsRequest,
  ReadAlarmsResponse,
  ReadBulkAlarmRequest,
} from './types';

/**
 * @description 사용자의 읽은 알림 목록 조회
 */
export const useReadAlarms = ({ username }: ReadAlarmsRequest, options?: ApiQueryOptions<ReadAlarmsResponse>) => {
  return useApiQuery<ReadAlarmsResponse>({
    url: `/home/alarms/${username}/read`,
    ...options,
    refetchOnMount: 'always',
  });
};

/**
 * @description 알림 생성 (테스트용 - 현재 사용 중)
 */
export const useCreateAlarm = (options?: ApiMutationOptions<AlarmInfo, CreateAlarmRequest>) => {
  return useApiMutation<AlarmInfo, any>({
    method: 'POST',
    url: '/home/alarms',
    ...options,
  });
};

/**
 * @description 알림 수정 (테스트용)
 */
export const usePutMarkAlarm = (options?: ApiMutationOptions<AlarmInfo, MarkAlarmReadRequest>) => {
  return useApiMutation<AlarmInfo, MarkAlarmReadRequest>({
    method: 'PUT',
    url: '/home/alarms/{alarmId}/{username}/read',
    ...options,
  });
};

export const usePutCancelAlarm = (options?: ApiMutationOptions<AlarmInfo, CancelAlarmRequest>) => {
  return useApiMutation<AlarmInfo, CancelAlarmRequest>({
    method: 'PUT',
    url: '/home/alarms/{alarmId}/{username}/cancel',
    ...options,
  });
};

/**
 * @description 알림 일괄 읽음 처리
 */
export const usePutReadBulkAlarm = (options?: ApiMutationOptions<AlarmInfo, ReadBulkAlarmRequest>) => {
  return useApiMutation<AlarmInfo, ReadBulkAlarmRequest>({
    method: 'PUT',
    url: '/home/alarms/{username}/readBulk',
    ...options,
  });
};

/**
 * @description 알림 삭제 (테스트용)
 */
export const useDeleteAlarm = (options?: ApiMutationOptions<string, { alarmId: string }>) => {
  return useApiMutation<string, { alarmId: string }>({
    method: 'DELETE',
    url: '/home/alarms/{alarmId}',
    ...options,
  });
};

/****
 * @description 승인 사용자 정보 조회
 */
export const useGetApprovalUserInfo = ({ alarmId }: ApprovalUserRequest, options?: ApiQueryOptions<ApprovalUserReponse>) => {
  return useApiQuery<ApprovalUserReponse>({
    url: `/home/alarms/approvalUserInfo/${alarmId}`,
    ...options,
  });
};
