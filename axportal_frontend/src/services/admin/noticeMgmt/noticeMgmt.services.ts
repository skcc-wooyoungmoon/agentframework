import type { ApiMutationOptions, ApiQueryOptions } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';

import type { createRequest, noticeRespose, } from '../../../pages/admin/noticeMgmt/type';
import { api } from '@/configs/axios.config';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';

/**
 * @description 공지사항 단건 조회
 */
export const useGetNoticeById = ({ notiId }: { notiId: string }, options?: ApiQueryOptions<noticeRespose>) => {
  return useApiQuery<noticeRespose>({
    queryKey: ['notices', notiId],
    url: `/admin/notices/${notiId}`,
    enabled: !!notiId, // notiId가 있을 때만 API 호출
    disableCache: true,
    ...options,
  });
};

/**
 * @description 공지사항 목록 조회 (서버 사이드 필터링)
 */
export const useGetNotices = (
  params?: {
    page?: number;
    size?: number;
    searchKeyword?: string;
    searchType?: string;
    dateType?: string;
    startDate?: string;
    endDate?: string;
    status?: string;
    type?: string;
  },
  options?: ApiQueryOptions<noticeRespose[]>
) => {
  return useApiQuery<noticeRespose[]>({
    queryKey: ['notices-list', DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/admin/notices',
    params: params,
    ...options,
  });
};

/**
 * @description 공지사항 등록
 */
export const usePostNotice = (options?: ApiMutationOptions<noticeRespose, createRequest>) => {
  return useApiMutation<noticeRespose, createRequest>({
    method: 'POST',
    url: '/admin/notices',
    ...options,
  });
};

/**
 * @description 공지사항 수정
 */
export const usePutNotice = (options?: ApiMutationOptions<noticeRespose, createRequest & { notiId: string }>) => {
  return useApiMutation<noticeRespose, createRequest & { notiId: string }>({
    method: 'PUT',
    url: '/admin/notices/{notiId}',
    ...options,
  });
};

/**
 * @description 공지사항 삭제
 */
export const useDeleteNotice = (options?: ApiMutationOptions<string, { notiId: string }>) => {
  return useApiMutation<string, { notiId: string }>({
    method: 'DELETE',
    url: '/admin/notices/{notiId}',
    ...options,
  });
};

/**
 * @description 공지사항 등록 (파일 포함) - 직접 API 호출
 */
export const postNoticeWithFiles = async (
  noticeData: {
    title: string;
    msg: string;
    type: string;
    useYn: string;
    expFrom?: string;
    expTo?: string;
  },
  files: File[],
  options?: {
    onSuccess?: (response: noticeRespose) => void;
    onError?: (error: any) => void;
  }
) => {
  try {
    // FormData 구성
    const formData = new FormData();

    // notice 데이터를 JSON 문자열로 변환하여 추가
    formData.append('notice', JSON.stringify(noticeData));

    // 파일들을 추가
    files.forEach(file => {
      formData.append('files', file);
    });

    const response = await api.post('/admin/notices/with-files', formData);

    if (options?.onSuccess) {
      options.onSuccess(response.data);
    }

    return response.data;
  } catch (error) {
    if (options?.onError) {
      options.onError(error);
    }
    throw error;
  }
};

/**
 * @description 공지사항 수정 (파일 포함) - 직접 API 호출
 */
export const putNoticeWithFiles = async (
  notiId: string,
  noticeData: {
    title: string;
    msg: string;
    type: string;
    useYn: string;
    expFrom?: string;
    expTo?: string;
  },
  files: File[],
  deleteFileIds?: number[],
  options?: {
    onSuccess?: (response: noticeRespose) => void;
    onError?: (error: any) => void;
  }
) => {
  try {
    // FormData 구성
    const formData = new FormData();

    // notice 데이터를 JSON 문자열로 변환하여 추가
    formData.append('notice', JSON.stringify(noticeData));

    // 삭제할 파일 ID들을 추가
    if (deleteFileIds && deleteFileIds.length > 0) {
      formData.append('deleteFileIds', JSON.stringify(deleteFileIds));
    }

    // 새로 업로드할 파일들을 추가
    files.forEach(file => {
      formData.append('newFiles', file);
    });

    const response = await api.put(`/admin/notices/${notiId}/with-files`, formData);

    if (options?.onSuccess) {
      options.onSuccess(response.data);
    }

    return response.data;
  } catch (error) {
    if (options?.onError) {
      options.onError(error);
    }
    throw error;
  }
};

/**
 * @description 파일 업로드 전용
 */
export const uploadFiles = async (files: File[]): Promise<string[]> => {
  try {
    const formData = new FormData();
    files.forEach(file => {
      formData.append(`files`, file);
    });

    const response = await api.post('/admin/files/upload', formData);

    return response.data.data.fileUrls || [];
  } catch (error) {
    // console.error('파일 업로드 실패:', error);
    throw new Error('파일 업로드에 실패했습니다.');
  }
};

/**
 * @description 파일 다운로드 (진행률 콜백 지원)
 */
export const downloadFileWithProgress = async (noticeId: string, fileId: number, onProgress?: (progress: number) => void): Promise<void> => {
  try {
    const response = await api.get(`/admin/notices/${noticeId}/files/${fileId}`, {
      responseType: 'blob',
      onDownloadProgress: progressEvent => {
        if (progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress?.(progress);
        }
      },
    });

    // Content-Disposition 헤더에서 파일명 추출
    const disposition = response.headers['content-disposition'] || '';
    const filename = decodeURIComponent(/filename\*=UTF-8''([^;]+)/.exec(disposition)?.[1] ?? 'download.bin');

    // Blob URL 생성 및 다운로드
    const url = URL.createObjectURL(response.data);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();

    // 메모리 정리
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  } catch (error) {
    throw new Error('파일 다운로드에 실패했습니다.');
  }
};
