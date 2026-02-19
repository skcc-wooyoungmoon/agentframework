import type { ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiQuery } from '@/hooks/common/api/useApi';
import { api } from '@/configs/axios.config';

import type { GetNoticeByIdRequest, GetNoticeByIdResponse, GetNoticesRequest, GetNoticesResponse } from './types';

// 공지사항 목록 조회
export const useGetNotices = (params?: GetNoticesRequest, options?: ApiQueryOptions<PaginatedDataType<GetNoticesResponse>>) => {
  return useApiQuery<PaginatedDataType<GetNoticesResponse>>({
    queryKey: ['notices', params ? JSON.stringify(params) : ''],
    url: '/notices', // 백엔드 API와 일치
    params,
    ...options,
  });
};

// 공지사항 상세 조회
export const useGetNoticeById = (request?: GetNoticeByIdRequest, options?: ApiQueryOptions<GetNoticeByIdResponse>) => {
  return useApiQuery<GetNoticeByIdResponse>({
    queryKey: ['notice', request?.noticeId || 'none'],
    url: `/notices/${request?.noticeId}`,
    enabled: !!request?.noticeId, // request?.noticeId가 존재하면 쿼리 실행
    ...options,
  });
};

// 공지사항 첨부파일 다운로드 (진행률 콜백 지원)
export const downloadNoticeFile = async (
  noticeId: number,
  fileId: number,
  onProgress?: (progress: number) => void
): Promise<void> => {
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
