import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'; // useParams import 추가

import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UIDataCnt, UILabel, UITextLabel } from '@/components/UI';
import { UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetTrainingDataList } from '@/services/data/storage/dataStorage.services';
import type { DatasourceFileItem, DownloadUploadAndSaveToEsRequest } from '@/services/data/types';
import { useModal } from '@/stores/common/modal';
import { useQueryClient } from '@tanstack/react-query';
import type { UUID } from 'crypto';
import { useUser } from '@/stores/auth/useUser';

import {
  useCustomDeleteDataset,
  useDeleteDataset,
  useDownloadUploadAndSaveToEs,
  useGetDatasetById,
  useGetDataSourceById,
  useGetDatasourceFiles,
} from '@/services/data/dataCtlgDataSet.services';
import { DataSetEditPopupPage } from './DataSetEditPopupPage';

export const DataSetDetailPage = () => {
  const layerPopupOne = useLayerPopup();
  // confirm, alert
  const { openConfirm } = useModal();
  // 공통 팝업 훅
  const { showDeleteComplete } = useCommonPopup();
  const queryClient = useQueryClient();
  const { datasetId } = useParams<{
    datasetId: UUID;
  }>();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [pagination, setPagination] = useState({ page: 1, size: 6 });
  const { user } = useUser();
  const { openAlert } = useModal();

  // API 호출 데이터세트 상세조회
  const {
    data: datasetData,
    isLoading: isLoadingDataset,
    //error: errorDataset,
    refetch: refetchDataset,
  } = useGetDatasetById(datasetId ? { datasetId } : undefined, {
    refetchOnMount: 'always', // 컴포넌트 마운트 시 항상 새로고침
  });

  // 쿼리파라미터에서 datasourceId 가져오기 (없으면 datasetData에서 가져오기)
  const dsIdFromUrl = searchParams.get('datasourceId');

  // datasourceId 결정: URL에 있으면 사용, 없으면 datasetData가 로드된 후 가져오기
  const dsId = useMemo(() => {
    return dsIdFromUrl || datasetData?.datasourceId || '';
  }, [dsIdFromUrl, datasetData?.datasourceId]);

  // 데이터 소스 상세조회 (파일리스트)
  const {
    data: dataSourceData,
    // isLoading: isLoadingDataSource,
    // error: errorDataSource,
    //refetch: refetchDataSource,
  } = useGetDataSourceById(dsId && dsId !== 'null' && dsId !== '' && dsId !== undefined ? { dataSourceId: dsId } : undefined);

  // 데이터 셋 삭제
  const { mutate: deleteDataset } = useDeleteDataset({
    onSuccess: () => {
      // console.log('데이터셋 삭제 성공');
      showDeleteComplete({
        itemName: '학습 데이터세트가',
        onConfirm: () => {
          navigate('/data/dataCtlg', { replace: true });
        },
      });
    },
    onError: /* error */ () => {
      // console.error('데이터셋 삭제 실패:', error);
    },
  });

  // 커스텀 데이터셋 삭제
  const { mutate: deleteCustomDataset } = useCustomDeleteDataset({
    onSuccess: () => {
      // console.log('커스텀 데이터셋 삭제 성공');

      showDeleteComplete({
        itemName: '학습 데이터세트 삭제가',
        onConfirm: () => {
          navigate('/data/dataCtlg', { replace: true });
        },
      });
    },
    onError: /* error */ () => {
      // console.error('커스텀 데이터셋 삭제 실패:', error);
    },
  });

  // 다운로드, S3 업로드 및 ES 저장
  const { mutate: downloadUploadAndSaveToEs } = useDownloadUploadAndSaveToEs({
    onSuccess: /* (data: any) */ () => {
      // 성공 모달 표시
      openConfirm({
        title: '완료',
        message: '데이터 탐색 메뉴에 등록을 완료하였습니다.',
        confirmText: '확인',
        cancelText: '',
        onConfirm: () => { },
        onCancel: () => { },
      });
    },
    onError: /* (error: any) */ () => {
      // 실패 모달 표시
      openConfirm({
        title: '실패',
        message: '데이터 탐색 메뉴에 등록을 실패하였습니다.',
        confirmText: '확인',
        cancelText: '',
        onConfirm: () => {
          // console.log('사용자가 실패 알림 확인');
        },
        onCancel: () => {
          // console.log('사용자가 실패 알림 확인');
        },
      });
    },
  });

  // custom 타입일 때는 API 호출하지 않음
  const isCustomType = datasetData?.type?.toUpperCase() === 'CUSTOM';

  // 데이터소스 파일 목록 조회 API (실제 datasourceId 사용)
  const {
    data: datasourceFilesData,
    isLoading: isLoadingFiles,
    error: errorFiles,
    refetch: refetchDatasourceFiles, // refetch 추가
  } = useGetDatasourceFiles(
    {
      datasourceId: dsId || '', // dsId가 있으면 전달, 없으면 빈 문자열
      page: pagination.page,
      size: pagination.size,
      sort: 'created_at:desc',
      filter: 'is_deleted:false',
    },
    {
      enabled: !!dsId && dsId !== 'null' && dsId !== '' && dsId !== undefined, // enabled 명시적으로 추가
    }
  );

  // custom 타입일 때 sourceFileName을 리스트 형태로 변환
  const customFilesData = useMemo(() => {
    if (!isCustomType || !datasetData?.sourceFileName) {
      return null;
    }

    // sourceFileName이 단일 파일명 문자열 (예: "custom.zip")
    const fileName = datasetData.sourceFileName;

    // DatasourceFileItem 형태로 변환
    const file: DatasourceFileItem = {
      id: datasetId || '',
      fileName: fileName,
      fileSize: 0,
      filePath: '',
      datasourceId: dsId || '',
      fileMetadata: {},
      knowledgeConfig: {},
      s3Etag: null,
      isDeleted: false,
      createdAt: datasetData.createdAt || '',
      updatedAt: datasetData.updatedAt || '',
      createdBy: datasetData.createdBy || '',
      updatedBy: datasetData.updatedBy || '',
    };

    return {
      content: [file],
      pageable: {
        page: 0,
        size: 20,
        sort: 'created_at:desc',
      },
      totalElements: 1,
      totalPages: 1,
      first: true,
      last: true,
      hasNext: false,
      hasPrevious: false,
    };
  }, [isCustomType, datasetData?.sourceFileName, datasetData?.createdAt, datasetData?.updatedAt, datasetData?.createdBy, datasetData?.updatedBy, dsId, datasetId]);

  // dsId가 변경될 때 수동으로 refetch
  useEffect(() => {
    if (dsId && dsId !== 'null' && dsId !== '' && dsId !== undefined && !isCustomType && !isCustomType) {
      refetchDatasourceFiles();
    }
  }, [dsId, refetchDatasourceFiles, isCustomType]);

  // 그리드에 표시할 데이터 결정 (custom 타입일 때는 customFilesData 사용)
  const displayFilesData = isCustomType ? customFilesData : datasourceFilesData;
  const displayIsLoadingFiles = isCustomType ? false : isLoadingFiles;
  const displayErrorFiles = isCustomType ? null : errorFiles;

  // 다운로드, S3 업로드 및 ES 저장 통합 핸들러
  const handleDownloadUploadAndSaveToEs = (datasourceFileId: string, fileData: any) => {
    // console.log('=== 다운로드, S3 업로드 및 ES 저장 통합 API 호출 시작 ===');

    // datasetData 존재 여부 확인
    if (!datasetData) {
      return;
    }

    // console.log('datasetData 존재 확인:', !!datasetData);

    // 데이터 세트 유형 처리 - 원본 영문 값 그대로 전송
    let datasetType = '';
    if (datasetData.type) {
      datasetType = datasetData.type; // 원본 영문 값 그대로 사용 (unsupervised_finetuning 등)
      // console.log('✅ datasetType 추출 성공:', datasetType);
    } else {
      // console.error('❌ datasetData.type이 없습니다!');
    }

    // 태그 처리 - 배열이면 join, 아니면 빈 문자열
    let tagsString = '';
    /* console.log('태그 디버깅:', {
      tags: datasetData.tags,
      type: typeof datasetData.tags,
      isArray: Array.isArray(datasetData.tags),
    }); */

    if (Array.isArray(datasetData.tags)) {
      // 태그 배열에서 name 속성을 추출하여 join
      tagsString = datasetData.tags.map((tag: any) => tag.name || tag).join(', ');
      // console.log('✅ 태그 배열 처리 성공:', tagsString);
    } else if (typeof datasetData.tags === 'string') {
      tagsString = datasetData.tags;
      // console.log('✅ 태그 문자열 처리 성공:', tagsString);
    } else {
      // console.error('❌ 태그 데이터가 없거나 예상과 다른 형식입니다:', datasetData.tags);
    }

    // console.log('처리된 데이터:');

    const requestData: DownloadUploadAndSaveToEsRequest = {
      download: true,
      uploadToS3: true,
      saveToEs: true,
      createdBy: '',
      datasetCat01: '학습',
      datasetCat02: datasetType,
      datasetCat03: 'category03_value',
      datasetCat04: 'category04_value',
      datasetCat05: 'category05_value',
      descCtnt: fileData.descCtnt || '',
      title: fileData.fileName,
      tags: tagsString,
      updatedBy: '',
      fstCreatedAt: '',
      lstUpdatedAt: '',
      ozonePath: '',
    };

    // console.log('전송할 requestData:', requestData);

    // console.log('isCustomType:', isCustomType);
    let dataId = '';

    if (isCustomType) {
      dataId = datasetId || '';
    } else {
      dataId = datasourceFileId || '';
    }
    downloadUploadAndSaveToEs({
      dataId: dataId,
      isCustomType: isCustomType,
      request: requestData,
    });
  };

  // DatasetDetail 페이지 새로고침 핸들러
  const handleDatasetDetailRefresh = () => {
    // console.log('DatasetDetail 페이지 새로고침 실행');

    refetchDataset(); // 데이터셋 상세 정보 새로고침

    //refetchDataSource(); // 데이터 소스 상세 정보 새로고침
  };

  // 데이터 셋 삭제 핸들러
  const handleDeleteDataset = async () => {
    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '삭제',
      cancelText: '취소',
      onConfirm: async () => {
        if (datasetData?.type.toUpperCase() === 'CUSTOM') {
          // console.log('커스텀 데이터셋 삭제');
          await new Promise<void>((resolve, reject) => {
            deleteCustomDataset(
              { datasetId: datasetId as UUID },
              {
                onSuccess: () => {
                  // console.log(`커스텀 데이터셋 ${datasetId} 삭제 성공`);
                  resolve();
                },
                onError: error => {
                  // console.error(`커스텀 데이터셋 ${datasetId} 삭제 실패:`, error);
                  reject(error);
                },
              }
            );
          });
        } else {
          // console.log('커스텀 외 데이터셋 삭제');
          await new Promise<void>((resolve, reject) => {
            deleteDataset(
              { datasetId: datasetId as UUID, dataSourceId: dataSourceData?.id as UUID },
              {
                onSuccess: () => {
                  // console.log(`데이터셋 ${datasetId} 삭제 성공`);
                  resolve();
                },
                onError: error => {
                  // console.error(`데이터셋 ${datasetId} 삭제 실패:`, error);
                  reject(error);
                },
              }
            );
          });
        }
      },
      onCancel: () => {
        // console.log('취소');
      },
    });
  };

  // 페이지네이션 핸들러
  const handlePageChange = (newPage: number) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  // Status 정의
  const STATUS_CONFIG = {
    completed: {
      label: '이용 가능',
      intent: 'complete' as const,
    },
    processing: {
      label: '진행중',
      intent: 'progress' as const,
    },
    failed: {
      label: '실패',
      intent: 'error' as const,
    },
    canceled: {
      label: '취소',
      intent: 'stop' as const,
    },
  } as const;

  // 데이터소스 타입에 따른 버튼 활성화/비활성화 상태
  const isFileDataSource = dataSourceData?.type === 'file';
  // const isS3DataSource = dataSourceData?.type === 's3';

  // 학습 데이터 목록 조회 (중복 체크용)
  const {
    data: trainingDataList,
    refetch: refetchTrainingData,
    isLoading: isLoadingTrainingData,
  } = useGetTrainingDataList({
    page: 1,
    countPerPage: 1000, // 충분히 큰 수로 설정하여 모든 데이터 조회
    cat01: '학습', // 카테고리1: 학습
    cat02: datasetData?.type, // 카테고리2: 데이터셋 유형 (supervised_finetuning, unsupervised_finetuning, dpo_finetuning, custom)
  });

  // 데이터 이름 중복 체크 함수
  const checkDuplicateTitle = (fileName: string, latestTrainingData?: any): boolean => {
    const dataToCheck = latestTrainingData || trainingDataList;

    // 학습 데이터 목록이 로드되지 않았거나 데이터셋 타입이 없는 경우 중복 없음으로 처리
    if (!dataToCheck?.content || !datasetData?.type) {
      // console.log('학습 데이터 목록이 없거나 데이터셋 타입이 없습니다. 중복 없음으로 처리합니다.');
      // console.log('dataToCheck:', dataToCheck);
      // console.log('datasetData?.type:', datasetData?.type);
      return false;
    }

    const existingTitles = dataToCheck.content.map((item: any) => item.title).filter(Boolean);
    const isDuplicate = existingTitles.includes(fileName);

    // console.log('=== 중복 체크 결과 ===');
    // console.log('🔍 체크할 파일명:', fileName);
    // console.log('📊 조회 카테고리 - cat01:', '학습', 'cat02:', datasetData?.type);
    // console.log('📋 기존 제목 목록:', existingTitles);
    // console.log('❌ 중복 여부:', isDuplicate);
    // console.log('=== 중복 체크 완료 ===');

    return isDuplicate;
  };

  // ProjectInfoBox asset 배열 생성
  // custom 데이터셋인 경우, datsaet id만 전송
  // custom 데이터셋이 아닌 경우, datasetid, datasource id 전송
  const projectInfoAssets = useMemo(() => {
    const assets: Array<{ type: string; id: string }> = [];
    if (isCustomType) {
      assets.push({ type: 'dataset', id: datasetId || '' });
    } else {
      assets.push({ type: 'dataset', id: datasetId || '' }, { type: 'datasource', id: dsId || '' });
    }
    return assets;
  }, [isCustomType, datasetId, dsId]);

  // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 수정 불가
  const checkPublicAssetPermission = (datasetData: any, alertMessage: string = '지식/학습 데이터 편집에 대한 권한이 없습니다.') => {
    if (Number(datasetData?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(datasetData?.fstPrjSeq)) {
      openAlert({
        title: '안내',
        message: alertMessage,
        confirmText: '확인',
      });
      return false;
    }
    return true;
  };

  // 그리드 컬럼 정의///////////////////////////////////////////////////////////////
  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        valueGetter: (params: any) => {
          return (pagination.page - 1) * pagination.size + params.node.rowIndex + 1;
        },
        cellStyle: () => ({
          textAlign: 'center' as const,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }),
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '데이터 이름',
        field: 'fileName',
        flex: 1,
        sortable: false,
      },
      {
        headerName: '데이터 탐색 등록',
        field: 'downloadUploadEs' as any,
        width: 150,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
        cellRenderer: (params: any) => {
          const isDisabled = isLoadingDataset || !datasetData || (!isFileDataSource && !isCustomType);

          return (
            <Button
              auth={AUTH_KEY.DATA.UNSTRUCTURED_REGISTER}
              className={isDisabled ? 'btn-text-14-underline-disabled' : 'btn-text-14-underline-point'}
              disabled={isDisabled}
              onClick={async () => {
                if (!checkPublicAssetPermission(datasetData, '지식/학습 데이터 편집에 대한 권한이 없습니다.')) {
                  return;
                }

                if (isDisabled) {
                  if (!isFileDataSource && !isCustomType) {
                    // console.log('S3 데이터소스는 비정형 등록을 사용할 수 없습니다.');
                  } else {
                    // console.log('데이터 세트 정보 로딩 중이거나 없음');
                  }
                  return;
                }

                // console.log('데이터저장소 등록 버튼 클릭:', params.data);

                // 먼저 확인 모달 표시
                openConfirm({
                  title: '안내',
                  message: '해당 학습 데이터를 데이터 탐색 메뉴에 등록하시겠어요?',
                  confirmText: '네',
                  cancelText: '아니오',
                  onConfirm: async () => {
                    // console.log('사용자가 확인 선택 - 이제 중복 체크 수행');

                    // 학습 데이터가 로딩 중이거나 데이터셋 타입이 없는 경우 중복 체크 건너뛰기
                    if (isLoadingTrainingData || !datasetData?.type) {
                      // console.log('학습 데이터 로딩 중이거나 데이터셋 타입이 없어 중복 체크를 건너뜁니다.');
                      handleDownloadUploadAndSaveToEs(params.data.id, params.data);
                      return;
                    }

                    // "예" 클릭 시마다 학습 데이터 목록 새로 조회 (캐시 무효화)
                    // console.log('🔄 "예" 클릭 - 학습 데이터 목록 새로 조회 (캐시 무효화)');

                    // 캐시 무효화
                    await queryClient.invalidateQueries({
                      queryKey: ['data-storage-training'],
                    });

                    // 강제로 새로고침
                    const refetchResult = await refetchTrainingData();
                    // console.log('🔄 학습 데이터 목록 새로고침 완료:', refetchResult);
                    /* console.log(
                      '🔄 새로고침된 데이터 내용:',
                      refetchResult.data?.content?.map(item => item.title)
                    ); */

                    // 중복 체크 수행 (새로고침된 데이터 사용)
                    const fileName = params.data.fileName;
                    // console.log('🔍 중복 체크 시작 - 파일명:', fileName);

                    const isDuplicate = checkDuplicateTitle(fileName, refetchResult.data);

                    if (isDuplicate) {
                      // console.log('❌ 중복 감지됨 - 중복 알림 모달 표시');
                      // 중복된 경우 알림 모달 표시
                      openConfirm({
                        title: '안내',
                        message: '이미 데이터 탐색 메뉴에 동일한 파일명을 가진 파일이 등록되어있습니다.',
                        confirmText: '확인',
                        cancelText: '',
                        onConfirm: () => {
                          // console.log('사용자가 중복 알림 확인');
                        },
                        onCancel: () => {
                          // console.log('사용자가 중복 알림 확인');
                        },
                      });
                      return;
                    }

                    // console.log('✅ 중복 없음 - 비정형 등록 실행');
                    handleDownloadUploadAndSaveToEs(params.data.id, params.data);
                  },
                  onCancel: () => {
                    // console.log('사용자가 취소');
                  },
                });
              }}
            >
              {isLoadingDataset ? '로딩 중...' : '데이터 탐색 등록'}
            </Button>
          );
        },
      },
    ],
    [isLoadingDataset, datasetData, isFileDataSource, isCustomType, trainingDataList, openConfirm, datasetData?.type, isLoadingTrainingData, pagination.page, pagination.size]
  );

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='학습 데이터세트 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                학습 데이터세트 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '152px' }} />
                    <col style={{ width: '624px' }} />
                    <col style={{ width: '152px' }} />
                    <col style={{ width: '624px' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {datasetData?.name || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        {(() => {
                          const status = datasetData?.status as keyof typeof STATUS_CONFIG;
                          const config = STATUS_CONFIG[status] || {
                            label: status,
                            intent: 'complete' as const,
                          };
                          return (
                            <UILabel variant='badge' intent={config.intent}>
                              {config.label}
                            </UILabel>
                          );
                        })()}
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {datasetData?.description || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          데이터세트 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {datasetData?.type === 'supervised_finetuning'
                            ? '지도학습'
                            : datasetData?.type === 'unsupervised_finetuning'
                              ? '비지도학습'
                              : datasetData?.type === 'dpo_finetuning'
                                ? 'DPO'
                                : datasetData?.type === 'custom'
                                  ? 'Custom'
                                  : datasetData?.type}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          태그
                        </UITypography>
                      </th>
                      <td>
                        <UIUnitGroup gap={8} direction='row' align='start'>
                          {datasetData?.tags?.map((tag: any) => (
                            <UITextLabel key={tag.name} intent='tag'>
                              {tag.name}
                            </UITextLabel>
                          ))}
                        </UIUnitGroup>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          <UIArticle className='article-grid'>
            <UIListContentBox.Header>
              <div className='flex-shrink-0'>
                <UIGroup gap={8} direction='row' align='start'>
                  <div style={{ width: '168px' }}>
                    <UIDataCnt count={displayFilesData?.totalElements || 0} prefix='학습 데이터 총' />
                  </div>
                </UIGroup>
              </div>
            </UIListContentBox.Header>
            <div className='article-body'>
              {/* 다중 선택 그리드 */}
              <UIListContainer>
                <UIListContentBox.Body>
                  {displayErrorFiles ? (
                    <div className='flex items-center justify-center h-32'>
                      <UITypography variant='body-2' className='text-red-600'>
                        파일 목록을 불러오는 중 오류가 발생했습니다.
                      </UITypography>
                    </div>
                  ) : (
                    <UIGrid
                      type='default'
                      loading={displayIsLoadingFiles}
                      rowData={displayFilesData?.content || []}
                      columnDefs={columnDefs as any}
                    /* onClickRow={(params: any) => {
                      console.log('다중 onClickRow', params);
                    }} */
                    />
                  )}
                </UIListContentBox.Body>
                {!displayIsLoadingFiles && !displayErrorFiles && (
                  <UIListContentBox.Footer>
                    <UIPagination
                      currentPage={displayFilesData?.pageable.page ? displayFilesData.pageable.page + 1 : 1}
                      hasNext={displayFilesData?.hasNext}
                      totalPages={displayFilesData?.totalPages || 1}
                      onPageChange={handlePageChange}
                      className='flex justify-center'
                    />
                  </UIListContentBox.Footer>
                )}
              </UIListContainer>
            </div>
          </UIArticle>

          {/* 담당자 정보 섹션 */}
          <ManagerInfoBox
            type='uuid'
            people={[
              { userId: datasetData?.createdBy || '', datetime: datasetData?.createdAt ?? '' },
              { userId: datasetData?.updatedBy || '', datetime: datasetData?.updatedAt ?? '' },
            ]}
          />
          {/* 프로젝트 정보 섹션 */}
          <ProjectInfoBox assets={projectInfoAssets} auth={AUTH_KEY.DATA.UNSTRUCTURED_CHANGE_PUBLIC} />
        </UIPageBody>
        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button
                auth={AUTH_KEY.DATA.DATASET_DELETE}
                className='btn-primary-gray'
                onClick={() => {
                  if (!checkPublicAssetPermission(datasetData, '지식/학습 데이터 삭제에 대한 권한이 없습니다.')) {
                    return;
                  }
                  handleDeleteDataset();
                }}
              >
                삭제
              </Button>
              <Button
                auth={AUTH_KEY.DATA.DATASET_UPDATE}
                className='btn-primary-blue'
                onClick={() => {
                  if (!checkPublicAssetPermission(datasetData, '지식/학습 데이터 수정에 대한 권한이 없습니다.')) {
                    return;
                  }
                  layerPopupOne.onOpen();
                }}
              >
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
      <DataSetEditPopupPage
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        mode='ValueBind' // 편집 모드
        initialData={{
          // 초기 데이터 전달
          name: datasetData?.name || '',
          description: datasetData?.description || '',
          type: datasetData?.type || '',
          tags: datasetData?.tags || [],
          projectId: datasetData?.projectId || '',
          datasetId: datasetId || '',
          // ... 기타 필요한 데이터
        }}
        onDatasetDetailRefresh={handleDatasetDetailRefresh} // DatasetDetail 새로고침 콜백만 전달
      />
    </>
  );
};
