import type { ApiMutationOptions, ApiQueryOptions, PaginatedDataType } from '@/hooks/common/api';
import { useApiMutation, useApiQuery } from '@/hooks/common/api/useApi';
import { useMutation } from '@tanstack/react-query';
import { api } from '@/configs/axios.config';
import type { ErrorResponse } from '@/hooks/common/api';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';

import type {
  DeleteDatasetTagsRequest,
  GetDatasetByIdRequest,
  GetDatasetByIdResponse,
  GetDatasetsRequest,
  GetDatasetsResponse,
  GetDataSourceByIdRequest,
  GetDataSourceByIdResponse,
  GetDatasourceFilesRequest,
  GetDatasourceFilesResponse,
  UpdateDatasetRequest,
  UpdateDatasetTagsRequest,
  UploadFilesResponse,
  DownloadUploadAndSaveToEsRequest,
  DownloadUploadAndSaveToEsResponse,
  CreateS3TrainingDatasetRequest,
  CreateS3TrainingDatasetResponse,
  UploadDatasetFileRequest,
  UploadDatasetFileResponse,
} from './types';
import type { UUID } from 'crypto';

// 데이터셋 목록 조회
export const useGetDatasets = (params?: GetDatasetsRequest, options?: ApiQueryOptions<PaginatedDataType<GetDatasetsResponse>>) => {
  return useApiQuery<PaginatedDataType<GetDatasetsResponse>>({
    queryKey: ['datasets', DONT_SHOW_LOADING_KEYS.GRID_DATA],
    url: '/data-catalog/datasets', // baseURL + /data-ctlg/datasets = http://localhost:8080/data-ctlg/datasets
    params,
    ...options,
    disableCache: true,
  });
};

// 데이터셋 상세 조회
export const useGetDatasetById = (request?: GetDatasetByIdRequest, options?: ApiQueryOptions<GetDatasetByIdResponse>) => {
  return useApiQuery<GetDatasetByIdResponse>({
    queryKey: ['dataset', request?.datasetId || 'none'],
    url: `/data-catalog/datasets/${request?.datasetId}`,
    enabled: !!request?.datasetId, // request?.datasetId가 존재하면 쿼리 실행
    ...options,
  });
};

// 데이터소스 상세 조회
export const useGetDataSourceById = (request?: GetDataSourceByIdRequest, options?: ApiQueryOptions<GetDataSourceByIdResponse>) => {
  return useApiQuery<GetDataSourceByIdResponse>({
    queryKey: ['datasource', request?.dataSourceId || 'none'],
    url: `/data-catalog/datasources/${request?.dataSourceId}`,
    enabled: !!request?.dataSourceId, // request?.dataSourceId가 존재하면 쿼리 실행
    ...options,
  });
};

// 데이터셋 삭제
export const useDeleteDataset = (options?: ApiMutationOptions<string, { datasetId: UUID; dataSourceId: UUID }>) => {
  return useApiMutation<string, { datasetId: UUID; dataSourceId: UUID }>({
    method: 'DELETE',
    url: `/data-catalog/datasets/{datasetId}?dataSourceId={dataSourceId}`, // Query Parameter로 추가
    ...options,
  });
};

// 커스텀 데이터셋 삭제
export const useCustomDeleteDataset = (options?: ApiMutationOptions<string, { datasetId: UUID }>) => {
  return useApiMutation<string, { datasetId: UUID }>({
    method: 'DELETE',
    url: `/data-catalog/datasets/{datasetId}/custom`,
    ...options,
  });
};

// 데이터셋 수정
export const useUpdateDataset = (
  //options?: ApiMutationOptions< string,{ datasetId: UUID; description: string; projectId: UUID }>
  options?: ApiMutationOptions<string, UpdateDatasetRequest>
) => {
  return useApiMutation<string, UpdateDatasetRequest>({
    method: 'PUT',
    url: `/data-catalog/datasets/{datasetId}`,
    ...options,
  });
};

// 데이터셋 태그 수정
export const useUpdateDatasetTags = (
  // options?: ApiMutationOptions<string,{ datasetId: UUID; tags: { name: string }[] }>
  options?: ApiMutationOptions<string, UpdateDatasetTagsRequest>
) => {
  return useApiMutation<string, UpdateDatasetTagsRequest>({
    method: 'PUT',
    url: `/data-catalog/datasets/{datasetId}/tags`,
    ...options,
  });
};

// 데이터셋 태그 삭제
export const useDeleteDatasetTags = (options?: ApiMutationOptions<string, DeleteDatasetTagsRequest>) => {
  return useApiMutation<string, DeleteDatasetTagsRequest>({
    //return useApiMutation<string, DeleteDatasetTagsRequest>({
    method: 'DELETE',
    url: `/data-catalog/datasets/{datasetId}/tags`,
    ...options,
  });
};

// 데이터소스 파일 업로드
export const useUploadDatasetFiles = (options?: ApiMutationOptions<UploadFilesResponse, FormData>) => {
  return useApiMutation<UploadFilesResponse, FormData>({
    method: 'POST',
    url: '/data-catalog/datasources/upload/files',
    timeout: 300 * 1000, // 300초 타임아웃
    ...options,
  });
};

// 데이터소스 파일 목록 조회
export const useGetDatasourceFiles = (params?: GetDatasourceFilesRequest, options?: ApiQueryOptions<GetDatasourceFilesResponse>) => {
  return useApiQuery<GetDatasourceFilesResponse>({
    queryKey: ['datasourceFiles', params ? JSON.stringify(params) : ''],
    url: `/data-catalog/datasources/${params?.datasourceId}/files`,
    params,
    enabled: !!params?.datasourceId,
    ...options,
  });
};

// 비정형 등록 버튼 (데이터소스 파일 다운로드, S3 업로드 및 ES 메타 저장)
// export const useDownloadUploadAndSaveToEs = (options?: any) => {
//   return useMutation<DownloadUploadAndSaveToEsResponse, ErrorResponse, { datasourceFileId: string; request: DownloadUploadAndSaveToEsRequest }>({
//     mutationFn: async ({ datasourceFileId, request }) => {
//       const response = await api.post(`/data-catalog/datasources/files/${datasourceFileId}/udp-register`, request);
//       return response.data;
//     },
//     ...options,
//   });
// };

// 데이터 저장소 등록 버튼 (데이터소스 파일 다운로드, S3 업로드 및 ES 메타 저장)
export const useDownloadUploadAndSaveToEs = (options?: any) => {
  return useMutation<DownloadUploadAndSaveToEsResponse, ErrorResponse, { dataId: string; isCustomType: boolean; request: DownloadUploadAndSaveToEsRequest }>({
    mutationFn: async ({ dataId, isCustomType, request }) => {
      const response = await api.post(`/data-catalog/datasets/files/${dataId}/ozone-register?isCustomType=${isCustomType}`, request);
      return response.data;
    },
    ...options,
  });
};

// 학습 데이터셋 만들기 - custom 이 아닐 경우
export const useCreateS3TrainingDataset = (options?: any) => {
  return useMutation<CreateS3TrainingDatasetResponse, ErrorResponse, CreateS3TrainingDatasetRequest>({
    mutationFn: async (request: CreateS3TrainingDatasetRequest) => {
      const response = await api.post('/data-catalog/create-train-data/not-custom', request, {
        timeout: 300000,
      });
      return response.data;
    },
    ...options,
  });
};

// 학습 데이터셋 만들기 - custom 일 경우 (파일 업로드)
export const useUploadDatasetFile = (options?: any) => {
  return useMutation<UploadDatasetFileResponse, ErrorResponse, { file: File; requestData: UploadDatasetFileRequest }>({
    mutationFn: async ({ file, requestData }) => {
      const formData = new FormData();

      // 백엔드 컨트롤러의 @RequestPart 파라미터들과 정확히 매칭
      formData.append('file', file); // @RequestPart("file") MultipartFile
      formData.append('project_id', requestData.projectId || ''); // @RequestPart("project_id") String
      formData.append('name', requestData.name); // @RequestPart("name") String
      formData.append('type', 'file'); // @RequestPart("type") String - 데이터 소스 타입 ('file')
      formData.append('dataset_type', requestData.type); // @RequestPart("dataset_type") String - 데이터셋 타입 ('custom')
      formData.append('created_by', requestData.createdBy || ''); // @RequestPart("created_by") String
      formData.append('payload', requestData.payload || ''); // @RequestPart("payload") String
      formData.append('status', requestData.status || ''); // @RequestPart("status") String
      formData.append('tags', requestData.tags || ''); // @RequestPart("tags") String (optional)
      formData.append('updated_by', requestData.updatedBy || ''); // @RequestPart("updated_by") String
      formData.append('description', requestData.description || ''); // @RequestPart("description") String (optional)

      // 입력 파라미터 전체 로그
      const formDataObj: Record<string, any> = {};
      for (let [key, value] of formData.entries()) {
        formDataObj[key] = value instanceof File ? { name: value.name, size: value.size, type: value.type } : value;
      }
      // console.log('=== Custom 데이터셋 파일 업로드 입력 파라미터 ===', JSON.stringify(formDataObj, null, 2));

      // multipart/form-data로 전송 (Content-Type 헤더는 브라우저가 자동 설정)
      const response = await api.post('/data-catalog/create-train-data/custom', formData, {
        timeout: 300000,
      });

      // 컨트롤러 응답 전체 로그
      // console.log('=== Custom 데이터셋 파일 업로드 응답 ===', JSON.stringify(response.data, null, 2));

      return response.data;
    },
    ...options,
  });
};

// 학습 데이터셋 만들기 - custom 일 경우 (데이터저장소)
export const useCreateCustomDatasetFromStorage = (options?: any) => {
  return useMutation<UploadDatasetFileResponse, ErrorResponse, { fileName: string; sourceType: 's3' | 'file'; requestData: UploadDatasetFileRequest }>({
    mutationFn: async ({ fileName, sourceType, requestData }) => {
      const formData = new FormData();

      // 백엔드 컨트롤러의 @RequestPart 파라미터들과 정확히 매칭
      // sourceType이 's3'인 경우 file_name 파라미터 추가 (필수, 반드시 .zip 확장자)
      if (sourceType === 's3') {
        // 파일명이 .zip 확장자를 가지는지 확인하고, 없으면 추가
        const fileNameWithZip = fileName.endsWith('.zip') ? fileName : `${fileName}.zip`;
        formData.append('file_name', fileNameWithZip); // @RequestPart("file_name") String
      }
      // sourceType이 'file'인 경우 file 파라미터는 useUploadDatasetFile에서 처리

      formData.append('project_id', requestData.projectId || ''); // @RequestPart("project_id") String
      formData.append('name', requestData.name); // @RequestPart("name") String
      formData.append('type', sourceType); // @RequestPart("type") String - 데이터 소스 타입 ('s3' 또는 'file')
      formData.append('dataset_type', requestData.type); // @RequestPart("dataset_type") String - 데이터셋 타입 ('custom')
      formData.append('created_by', requestData.createdBy || ''); // @RequestPart("created_by") String
      formData.append('payload', requestData.payload || ''); // @RequestPart("payload") String
      formData.append('status', requestData.status || ''); // @RequestPart("status") String
      formData.append('tags', requestData.tags || ''); // @RequestPart("tags") String (optional)
      formData.append('updated_by', requestData.updatedBy || ''); // @RequestPart("updated_by") String
      formData.append('description', requestData.description || ''); // @RequestPart("description") String (optional)

      // 입력 파라미터 전체 로그
      const formDataObj: Record<string, any> = {};
      for (let [key, value] of formData.entries()) {
        formDataObj[key] = value instanceof File ? { name: value.name, size: value.size, type: value.type } : value;
      }
      // console.log('=== Custom 데이터셋 데이터저장소 입력 파라미터 ===', JSON.stringify(formDataObj, null, 2));

      // multipart/form-data로 전송 (Content-Type 헤더는 브라우저가 자동 설정)
      const response = await api.post('/data-catalog/create-train-data/custom', formData, {
        timeout: 300000,
      });

      // 컨트롤러 응답 전체 로그
      // console.log('=== Custom 데이터셋 데이터저장소 응답 ===', JSON.stringify(response.data, null, 2));

      return response.data;
    },
    ...options,
  });
};
