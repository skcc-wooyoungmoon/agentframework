import type { ApiMutationOptions, ApiQueryOptions } from '@/hooks/common/api';
import { useApiMutation, useApiQueries, useApiQuery } from '@/hooks/common/api/useApi';

import type {
  ClusterResource,
  CreateFineTuningRequest,
  // Dataset,
  DeleteFineTuningTrainingRequest,
  FineTuningTraining,
  GetFineTuningTrainingByIdRequest,
  GetFineTuningTrainingEventsRequest,
  GetFineTuningTrainingsRequest,
  PaginatedResponse,
  TaskPolicyResource,
  TrainingEventsRead,
  UpdateFineTuningRequest,
  UpdateFineTuningStatusRequest,
} from './types';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants.ts';

// 타입들은 types.ts에서 import하여 사용

/**
 * @description 클러스터 리소스 목록 조회 API
 */
export const useGetClusterResources = (options?: ApiQueryOptions<ClusterResource[]>) => {
  return useApiQuery<ClusterResource[]>({
    queryKey: ['cluster-resources'],
    url: '/resources/cluster',
    ...options,
  });
};

/**
 * @description 태스크 정책 리소스 목록 조회 API
 */
export const useGetTaskPolicyResources = (options?: ApiQueryOptions<TaskPolicyResource[]>) => {
  return useApiQuery<TaskPolicyResource[]>({
    queryKey: ['task-policy-resources'],
    url: '/resources/task_policy',
    ...options,
  });
};

/**
 * @description FineTuning 목록 조회
 */
export const useGetFineTuningTrainings = (params?: GetFineTuningTrainingsRequest, options?: ApiQueryOptions<PaginatedResponse<FineTuningTraining>>) => {
  return useApiQuery<PaginatedResponse<FineTuningTraining>>({
    queryKey: ['fine-tuning-trainings', DONT_SHOW_LOADING_KEYS.GRID_DATA, params?.queryKey || ''],
    url: '/finetuning/trainings',
    params: params,
    ...options,
  });
};

/**
 * @description FineTuning 단건 조회
 */
export const useGetFineTuningTrainingById = (params: GetFineTuningTrainingByIdRequest, options?: ApiQueryOptions<FineTuningTraining>) => {
  return useApiQuery<FineTuningTraining>({
    queryKey: ['fine-tuning-training', JSON.stringify(params)],
    url: `/finetuning/trainings/${params.id}`,
    enabled: !!params.id,
    params: { isDataSet: params.isDataSet ? 'Y' : 'N', isMetric: params.isMetric ? 'Y' : 'N' },
    ...options,
  });
};

/**
 * @description FineTuning 생성
 */
export const useCreateFineTuningTraining = (options?: ApiMutationOptions<FineTuningTraining, CreateFineTuningRequest>) => {
  return useApiMutation<FineTuningTraining, CreateFineTuningRequest>({
    method: 'POST',
    url: '/finetuning/trainings',
    ...options,
  });
};

/**
 * @description FineTuning 수정
 */
export const useUpdateFineTuningTraining = (options?: ApiMutationOptions<FineTuningTraining, UpdateFineTuningRequest>) => {
  return useApiMutation<FineTuningTraining, UpdateFineTuningRequest>({
    method: 'PUT',
    url: '/finetuning/trainings/{id}',
    ...options,
  });
};

/**
 * @description FineTuning Status 변경 (status만 업데이트)
 */
export const useUpdateFineTuningStatus = (options?: ApiMutationOptions<FineTuningTraining, UpdateFineTuningStatusRequest>) => {
  return useApiMutation<FineTuningTraining, UpdateFineTuningStatusRequest>({
    method: 'PUT',
    url: '/finetuning/trainings/status/{id}',
    timeout: 120 * 1000, // 300초 타임아웃
    ...options,
  });
};

/**
 * @description FineTuning 삭제
 */
export const useDeleteFineTuningTraining = (options?: ApiMutationOptions<string, DeleteFineTuningTrainingRequest>) => {
  return useApiMutation<string, DeleteFineTuningTrainingRequest>({
    method: 'DELETE',
    url: '/finetuning/trainings/{id}',
    ...options,
  });
};

/**
 * @description 데이터셋 목록 조회 API
 */
// export const useGetDatasets = (options?: ApiQueryOptions<Dataset[]>) => {
//   return useApiQuery<Dataset[]>({
//     queryKey: ['datasets'],
//     url: '/dataCtlg/datasets',
//     ...options,
//   });
// };

/**
 * @description FineTuning 다건 조회 (useApiQueries 사용)
 */
export const useGetFineTuningTrainingsByIds = (params: GetFineTuningTrainingByIdRequest[], options?: ApiQueryOptions<FineTuningTraining>) => {
  return useApiQueries<FineTuningTraining>(
    params.map(param => ({
      url: `/finetuning/trainings/${param.id}`,
      queryKey: ['fine-tuning-training', JSON.stringify(param)],
      params: { isDataSet: param.isDataSet ? 'Y' : 'N', isMetric: param.isMetric ? 'Y' : 'N' },
      options: {
        enabled: !!param.id,
        ...options,
      },
    }))
  );
};

/**
 * @description FineTuning 이벤트 조회
 */
export const useGetFineTuningTrainingEvents = (params: GetFineTuningTrainingEventsRequest, options?: ApiQueryOptions<TrainingEventsRead>) => {
  return useApiQuery<TrainingEventsRead>({
    queryKey: ['fine-tuning-training-events', params.trainingId, params.last || ''],
    url: `/finetuning/trainings/${params.trainingId}/events`,
    params: params.last ? { last: params.last } : undefined,
    enabled: !!params.trainingId,
    ...options,
  });
};
