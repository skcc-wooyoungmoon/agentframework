import React, { useEffect, useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIToggle } from '@/components/UI';
import { UIBox, UIButton2, UIDataCnt, UILabel, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useCustomDeleteDataset, useDeleteDataset, useGetDatasets } from '@/services/data/dataCtlgDataSet.services';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import type { UUID } from 'crypto';
import { DataSetEditPopupPage } from './DataSetEditPopupPage';

// 조회조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  dataType: string;
  status: string;
  view: string;
}

export const DataSetListPage = () => {
  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();
  // confirm, alert
  const { openConfirm, openAlert } = useModal();
  // 공통 팝업 훅
  const { showDeleteComplete, showTaskPartialComplete } = useCommonPopup();
  // 검색 조건
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.DATA_SET_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    dataType: '전체',
    status: '전체',
    view: 'grid',
  });
  const { user } = useUser();
  const [selectedDataList, setSelectedDataList] = useState<any[]>([]); // 전체 객체 배열 추가

  // 팝업 넘길 때 선택된 데이터셋 ID
  const [selectedDatasetId, setSelectedDatasetId] = useState<string>('');

  // requestParams 설정
  const requestParams = useMemo(() => {
    const statusFilter = searchValues.status !== '전체' && searchValues.status !== 'all' ? `status:${searchValues.status}` : '';
    const typeFilter = searchValues.dataType !== '전체' && searchValues.dataType !== 'all' ? `type:${searchValues.dataType}` : '';
    const combinedFilter = [statusFilter, typeFilter].filter(Boolean).join(',');
    return {
      page: searchValues.page,
      size: searchValues.size,
      sort: 'updated_at,desc',
      search: searchValues.searchKeyword,
      filter: combinedFilter || undefined,
    };
  }, [searchValues.page, searchValues.size, searchValues.searchKeyword, searchValues.status, searchValues.dataType]);

  // API 호출 (NO_PRESSURE_MODE면 자동 실행 안 함, 조회 버튼/useEffect의 refetch로만 호출)
  const { data: datasetData, refetch, isLoading } = useGetDatasets(requestParams, {
    enabled: !env.VITE_NO_PRESSURE_MODE,
  });

  // API 응답 데이터를 useMemo로 저장
  const dataList = useMemo(() => {
    if (!datasetData?.content) {
      return [];
    }

    // map()을 사용하여 새로운 배열 생성
    return datasetData.content.map((item: any, index: number) => {
      return {
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        id: item.id,
        datasourceId: item.datasourceId,
        name: item.name,
        type: item.type,
        status: item.status,
        description: item.description,
        publicStatus: item.publicStatus,
        tags: item.tags,
        createdAt: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
        updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '',
        lstPrjSeq: item.lstPrjSeq,
        fstPrjSeq: item.fstPrjSeq,
      };
    });
  }, [datasetData]);

  // 데이터셋 생성 완료 이벤트 수신
  useEffect(() => {
    const handleDatasetCreated = () => {
      // console.log('데이터셋 생성 완료 이벤트 수신 - 목록 새로고침');
      refetch();
    };

    window.addEventListener('dataset-created', handleDatasetCreated);

    return () => {
      window.removeEventListener('dataset-created', handleDatasetCreated);
    };
  }, [refetch]);

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  // DatasetList 페이지 새로고침 콜백
  const handleDatasetListRefresh = () => {
    // console.log('DatasetList 페이지 새로고침 실행');
    refetch(); // 데이터셋 목록 새로고침
  };

  // 검색 핸들러
  const handleSearch = () => {
    updatePageSizeAndRefetch({ page: 1 });
  };

  const handlePageChange = (newPage: number) => {
    updatePageSizeAndRefetch({ page: newPage });
  };

  // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 수정 불가
  const checkPublicAssetPermission = (rowData: any, alertMessage: string = '지식/학습 데이터 편집에 대한 권한이 없습니다.') => {
    if (Number(rowData?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(rowData?.fstPrjSeq)) {
      openAlert({
        title: '안내',
        message: alertMessage,
        confirmText: '확인',
      });
      return false;
    }
    return true;
  };

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.DATA.DATASET_UPDATE,
          onClick: (rowData: any) => {
            if (!checkPublicAssetPermission(rowData, '지식/학습 데이터 편집에 대한 권한이 없습니다.')) {
              return;
            }
            setSelectedDatasetId(rowData.id);
            layerPopupOne.onOpen();
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.DATA.DATASET_DELETE,
          onClick: (rowData: any) => {
            // console.log('테스트 삭제:', rowData);
            if (!checkPublicAssetPermission(rowData, '지식/학습 데이터 편집에 대한 권한이 없습니다.')) {
              return;
            }
            handleDeleteSelected(false, [{ datasetId: rowData.id, dataSourceId: rowData.datasourceId, type: rowData.type }]);
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
  };

  // 상세 페이지로 이동
  const handleDetailPage = (params: any) => {

    if (params.type.toUpperCase() === 'CUSTOM') {
      navigate(`dataset/${params.id}`);
    } else {
      navigate(`dataset/${params.id}?datasourceId=${params.datasourceId}`);
    }
  };

  // 데이터 셋 삭제
  const { mutate: deleteDataset } = useDeleteDataset();

  // 커스텀 데이터셋 삭제
  const { mutate: deleteCustomDataset } = useCustomDeleteDataset();

  // 선택된 항목 삭제 핸들러
  const handleDeleteSelected = async (isMultiple: boolean, idToDelete?: { datasetId: string; dataSourceId: string; type: string }[]) => {
    // 객체 배열로 변경: { datasetId, dataSourceId } 형태
    let itemsToDelete: { datasetId: string; dataSourceId: string; type: string }[] = [];

    if (isMultiple) {
      //console.log('Multiple selectedDataList', selectedDataList);
      itemsToDelete = selectedDataList.map(item => ({ datasetId: item.id, dataSourceId: item.datasourceId, type: item.type }));
      //console.log('itemsToDelete', itemsToDelete);
    } else if (!isMultiple) {
      // console.log('Single idToDelete', idToDelete);
      itemsToDelete = idToDelete || [];
    }

    //console.log('삭제할 항목:', itemsToDelete);
    if (itemsToDelete.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
        confirmText: '확인',
      });
      return;
    }
    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '삭제',
      cancelText: '취소',
      onConfirm: async () => {
        // 삭제 로직 실행
        //console.log('삭제 실행');
        //console.log('삭제할 항목들:', itemsToDelete);
        let successCount = 0;
        let failCount = 0;

        // 순차적으로 삭제 처리
        for (const item of itemsToDelete) {
          if (item.type.toUpperCase() === 'CUSTOM') {
            // console.log('커스텀 데이터셋 삭제');
            await new Promise<void>((resolve, reject) => {
              deleteCustomDataset(
                { datasetId: item.datasetId as UUID },
                {
                  onSuccess: () => {
                    // console.log(`커스텀 데이터셋 ${item.datasetId} 삭제 성공`);
                    successCount++;
                    resolve();
                  },
                  onError: error => {
                    // console.error(`커스텀 데이터셋 ${item.datasetId} 삭제 실패:`, error);
                    failCount++;
                    reject(error);
                  },
                }
              );
            });
          } else {
            //console.log('커스텀 외 데이터셋 삭제');
            //console.log('item', item);
            await new Promise<void>((resolve, reject) => {
              deleteDataset(
                { datasetId: item.datasetId as UUID, dataSourceId: item.dataSourceId as UUID },
                {
                  onSuccess: () => {
                    //console.log(`데이터셋 ${item.datasetId} 삭제 성공`);
                    successCount++;
                    resolve();
                  },
                  onError: error => {
                    // console.error(`데이터셋 ${item.datasetId} 삭제 실패:`, error);
                    failCount++;
                    reject(error);
                  },
                }
              );
            });
          }
        }
        // 삭제 결과 알림
        if (failCount > 0) {
          showTaskPartialComplete({
            taskName: '학습 데이터세트',
            successCount: successCount,
            failureCount: failCount,
            onConfirm: () => {
              refetch();
            },
          });
          // console.log(`삭제 완료 ${successCount}건, 실패 ${failCount}건`);
        } else {
          showDeleteComplete({
            itemName: '학습 데이터세트가',
            onConfirm: () => {
              refetch();
            },
          });
          // console.log(`삭제 완료 ${successCount}건`);
        }

        // 성공적으로 삭제된 경우에만 목록 새로고침
        if (successCount > 0) {
          refetch();
        }

        // 매개변수로 전달된 경우가 아니라면 selectedIds 초기화
        if (isMultiple) {
          setSelectedDataList([]); // 추가: selectedDataList도 초기화
          //setSelectedIds([]);
        }
      },
      onCancel: () => {
        // console.log('취소');
      },
    });
  };

  // dropdown 정의/////////////////////////////////////////////////////////////

  // 유형 정의
  const typeOptions = [
    { value: 'all', label: '전체' },
    { value: 'supervised_finetuning', label: '지도학습' },
    { value: 'unsupervised_finetuning', label: '비지도학습' },
    { value: 'dpo_finetuning', label: 'DPO' },
    { value: 'custom', label: 'Custom' },
  ];

  // 상태 정의
  const statusOptions = [
    { value: 'all', label: '전체' },
    { value: 'processing', label: '진행중' },
    { value: 'failed', label: '실패' },
    { value: 'canceled', label: '취소' },
    { value: 'completed', label: '이용 가능' },
  ];

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

  // 컬럼 정의 ///////////////////////////////////////////////////////////////
  const columnDefs: any = useMemo(
    () =>
      [
        {
          headerName: 'NO',
          field: 'no' as any,
          width: 56,
          minWidth: 56,
          maxWidth: 56,
          cellClass: 'text-center',
          headerClass: 'text-center',
          cellStyle: {
            textAlign: 'center',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          } as any,
          sortable: false,
          suppressHeaderMenuButton: true,
          suppressSizeToFit: true,
          //valueGetter: (params: any) => (searchValues.page - 1) * searchValues.size + params.node.rowIndex + 1,
        },
        {
          headerName: '이름',
          field: 'name' as any,
          width: 190,
        },
        {
          headerName: '상태',
          field: 'status' as any,
          width: 120,
          cellRenderer: React.memo((params: any) => {
            const status = params.value as keyof typeof STATUS_CONFIG;
            const config = STATUS_CONFIG[status] || {
              label: status,
              intent: 'complete' as const,
            };
            return (
              <UILabel variant='badge' intent={config.intent}>
                {config.label}
              </UILabel>
            );
          }),
        },
        {
          headerName: '설명',
          field: 'description',
          minWidth: 300,
          flex: 1,
          showTooltip: true,
          cellRenderer: React.memo((params: any) => {
            return (
              <div
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
              >
                {params.value}
              </div>
            );
          }),
        },
        {
          headerName: '공개범위',
          field: 'publicStatus',
          width: 120,
        },

        {
          headerName: '태그',
          field: 'tags',
          width: 180,
          cellStyle: { paddingLeft: '16px' },
          cellRenderer: (params: any) => {
            const tags = params.value; // 태그 배열

            if (!Array.isArray(tags) || tags.length === 0) {
              return null;
            }

            const tagText = tags.map((tag: any) => tag.name).join(', ');
            const tagTextArray = tags.map((tag: any) => tag.name);
            return (
              <div
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
                title={tagText}
              >
                <div className='flex gap-1 flex-wrap'>
                  {tagTextArray.slice(0, 2).map((tag: any, index: number) => (
                    <UITextLabel key={index} intent='tag'>
                      {tag}
                    </UITextLabel>
                  ))}
                  {/* 2개 이상일 경우 ... 처리 */}
                  {tagTextArray.length > 2 && (
                    <UITypography variant='caption-2' className='secondary-neutral-550'>
                      {'...'}
                    </UITypography>
                  )}
                </div>
              </div>
            );
          },
        },
        {
          headerName: '유형',
          field: 'type',
          width: 120,
          cellRenderer: (params: any) => {
            return typeOptions.find(option => option.value === params.value)?.label || params.value;
          },
        },
        {
          headerName: '생성일시',
          field: 'createdAt' as any,
          width: 180,
          cellStyle: {
            paddingLeft: '16px',
          },
          // valueGetter: (params: any) => {
          //   return dateUtils.formatDate(params.data.createdAt, 'datetime');
          // },
        },
        {
          headerName: '최종 수정일시',
          field: 'updatedAt' as any,
          width: 180,
          cellStyle: {
            paddingLeft: '16px',
          },
          // valueGetter: (params: any) => {
          //   return dateUtils.formatDate(params.data.updatedAt, 'datetime');
          // },
        },
        {
          headerName: '',
          field: 'more', // 더보기 컬럼 필드명 (고정)
          width: 56,
        },
      ] as any,
    []
  );

  return (
    <>
      <UIArticle className='article-filter'>
        <UIBox className='box-filter'>
          <UIGroup gap={40} direction='row'>
            <div style={{ width: 'calc(100% - 168px)' }}>
              <table className='tbl_type_b'>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        검색
                      </UITypography>
                    </th>
                    <td>
                      <div className='flex-1' style={{ zIndex: '10' }}>
                        <UIInput.Search
                          value={searchValues.searchKeyword}
                          onChange={e => {
                            setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                          }}
                          placeholder='이름, 설명 입력'
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        유형
                      </UITypography>
                    </th>
                    <td>
                      <div>
                        <UIDropdown value={searchValues.dataType} placeholder='조회 조건 선택' options={typeOptions} onSelect={value => handleDropdownSelect('dataType', value)} />
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        상태
                      </UITypography>
                    </th>
                    <td>
                      <div>
                        <UIDropdown value={searchValues.status} placeholder='조회 조건 선택' options={statusOptions} onSelect={value => handleDropdownSelect('status', value)} />
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ width: '128px' }}>
              <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                조회
              </UIButton2>
            </div>
          </UIGroup>
        </UIBox>
      </UIArticle>

      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='w-full'>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={datasetData?.totalElements ?? 0} prefix='총' unit='건' />
                    </div>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(searchValues.size)}
                        disabled={(datasetData?.totalElements ?? 0) === 0}
                        options={[
                          { value: '12', label: '12개씩보기' },
                          { value: '36', label: '36개씩보기' },
                          { value: '60', label: '60개씩보기' },
                        ]}
                        onSelect={(value: string) => updatePageSizeAndRefetch({ size: Number(value), page: 1 })}
                        //onClick={() => console.log('onClick')}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>
                    <UIToggle
                      variant='dataView'
                      checked={searchValues.view === 'card'}
                      disabled={(datasetData?.totalElements ?? 0) === 0}
                      onChange={checked => {
                        setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }));
                        //setSelectedIds([]); // 선택된 항목들 초기화
                      }}
                    />
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            {searchValues.view === 'grid' ? (
              <UIGrid
                type='multi-select'
                loading={isLoading}
                rowData={dataList}
                columnDefs={columnDefs}
                moreMenuConfig={moreMenuConfig}
                selectedDataList={selectedDataList} // 추가: 체크박스 상태 유지용
                checkKeyName={'id'}
                onClickRow={(params: any) => {
                  handleDetailPage(params.data);
                  //console.log('params.data', params.data);
                }}
                onCheck={(checkedRows: any[]) => {
                  setSelectedDataList(checkedRows);
                }}
              />
            ) : (
              <UICardList
                rowData={dataList}
                flexType='none'
                loading={isLoading}
                card={(item: any) => {
                  const status = item.status as keyof typeof STATUS_CONFIG;
                  const config = STATUS_CONFIG[status] || {
                    label: status,
                    intent: 'complete' as const,
                  };
                  return (
                    <UIGridCard
                      id={item.id}
                      title={item.name}
                      caption={item.description}
                      data={item}
                      moreMenuConfig={moreMenuConfig}
                      statusArea={
                        <UILabel variant='badge' intent={config.intent}>
                          {config.label}
                        </UILabel>
                      }
                      checkbox={{
                        checked: selectedDataList.some(selectedItem => selectedItem.id === item.id), // 수정: selectedDataList 사용
                        onChange: (checked: boolean /* , value: string */) => {
                          // console.log('checked', checked, value);
                          if (checked) {
                            setSelectedDataList([...selectedDataList, item]);
                          } else {
                            setSelectedDataList(selectedDataList.filter((row: any) => row.id !== item.id));
                          }
                        },
                      }}
                      onClick={() => handleDetailPage(item)}
                      rows={[
                        { label: '태그', value: Array.isArray(item.tags) ? item.tags.map((tag: any) => tag.name) : [] },
                        { label: '유형', value: typeOptions.find(option => option.value === item.type)?.label || item.type },
                        { label: '생성일시', value: item.createdAt || '' },
                        // { label: '최종수정일시', value: dateUtils.formatDate(item.updatedAt, 'datetime') },
                      ]}
                    />
                  );
                }}
              />
            )}
          </UIListContentBox.Body>
          {/* [참고] classname 관련
               - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
               - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
             */}
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <Button
              className='btn-option-outlined'
              auth={AUTH_KEY.DATA.DATASET_DELETE}
              style={{ width: '40px' }}
              disabled={(datasetData?.totalElements ?? 0) === 0}
              onClick={() => {
                if ((datasetData?.totalElements ?? 0) > 0) {
                  handleDeleteSelected(
                    true,
                    selectedDataList.map(item => ({ datasetId: item.id, dataSourceId: item.datasourceId, type: item.type }))
                  );
                }
              }}
            >
              삭제
            </Button>
            <UIPagination currentPage={searchValues.page} hasNext={datasetData?.hasNext} totalPages={datasetData?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
      <DataSetEditPopupPage
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        mode='ApiCall'
        datasetId={selectedDatasetId} // 실제로는 선택된 아이템의 ID를 전달
        onDatasetListRefresh={handleDatasetListRefresh} // DatasetList 새로고침 콜백만 전달
      />
    </>
  );
};
