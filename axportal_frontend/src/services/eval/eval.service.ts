import { useApiQuery, type ApiQueryOptions, type PaginatedDataType } from '@/hooks/common/api';
import type { TaskListRequest, TaskListResponse } from './types';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants.ts';

/**
 * 평가 Task 목록 조회
 * @param request Task 목록 조회 요청 파라미터
 * @param options API 쿼리 옵션
 * @returns 평가 Task 목록 조회 결과
 */
export const useGetEvalTaskList = (
    request: TaskListRequest,
    options?: ApiQueryOptions<TaskListResponse>
) => {
    return useApiQuery<TaskListResponse, TaskListRequest>({
        queryKey: ['eval-tasks', DONT_SHOW_LOADING_KEYS.GRID_DATA],
        url: '/eval/tasks',
        params: request,
        ...options,
    });
};

/**
 * 평가 Task 목록 조회 (페이지네이션 포함)
 * @param request Task 목록 조회 요청 파라미터
 * @param options API 쿼리 옵션
 * @returns 페이지네이션된 평가 Task 목록 조회 결과
 */
export const useGetEvalTaskListPaginated = (
    request: TaskListRequest,
    options?: ApiQueryOptions<PaginatedDataType<TaskListResponse>>
) => {
    return useApiQuery<PaginatedDataType<TaskListResponse>, TaskListRequest>({
        queryKey: ['eval-tasks-paginated', JSON.stringify(request)],
        url: '/eval/tasks',
        params: request,
        ...options,
    });
};
