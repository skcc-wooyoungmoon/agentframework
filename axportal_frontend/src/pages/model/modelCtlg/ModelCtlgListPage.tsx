import React, { useMemo, useState } from 'react';

import { UIDataCnt, UILabel, UITextLabel, UIToggle } from '@/components/UI';
import { UIBox, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { ModelCtlgCreatePopupPage } from '@/pages/model/modelCtlg/ModelCtlgCreatePopupPage.tsx';
import { useDeleteModelCtlgBulk, useGetModelCtlgList, useGetModelTags, useGetModelTypes } from '@/services/model/ctlg/modelCtlg.services';
import type { ModelCtlgType, TagType } from '@/services/model/ctlg/types';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { useNavigate } from 'react-router-dom';
import { DeployModelCreatePopupPage } from '../../deploy/model/DeployModelCreatePopupPage';
import { ModelCtlgEditPage } from './ModelCtlgEditPage';

type SearchValues = {
  page: number;
  size: number;
  searchKeyword: string;
  deployType: string;
  modelType: string;
  isValid: string;
  tags: string;
  gridType: string;
};

/**
 * https://adxp.mobigen.com/api/v1/models/tags
 */

export const ModelCtlgListPage = () => {
  const navigate = useNavigate();
  const { openConfirm, openAlert } = useModal();
  const createModelLayerPopup = useLayerPopup();
  const deployModelLayerPopup = useLayerPopup();
  const updateModelLayerPopup = useLayerPopup();

  const [selectedModel, setSelectedModel] = useState<ModelCtlgType | undefined>(undefined);

  const { data: typeList, refetch: refetchModelTypes } = useGetModelTypes({ enabled: false });
  const { data: tagList, refetch: refetchModelTags } = useGetModelTags({ enabled: false });

  // 검색 조건
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.MODEL_CTLG_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    deployType: 'all',
    modelType: 'all',
    isValid: 'all',
    tags: 'all',
    gridType: 'grid',
  });

  // 필터 문자열 생성
  // TODO 백으로 옮기기
  const buildFilterString = () => {
    const filters: string[] = [];

    // 모델 유형 필터
    if (searchValues.modelType && searchValues.modelType !== 'all') {
      filters.push(`type:${searchValues.modelType}`);
    }

    // 서빙 타입 필터 (serving_type)
    if (searchValues.deployType && searchValues.deployType !== 'all') {
      filters.push(`serving_type:${searchValues.deployType}`);
    }

    // 유효성 필터
    if (searchValues.isValid && searchValues.isValid !== 'all') {
      filters.push(`is_valid:${searchValues.isValid === 'Y' ? 'true' : 'false'}`);
    }

    // 태그 필터
    if (searchValues.tags && searchValues.tags !== 'all') {
      filters.push(`tags[].name:${searchValues.tags}`);
    }

    return encodeURIComponent(filters.join(','));
  };

  // 목록 조회
  const {
    data: modelCtlgList,
    refetch,
    isFetching,
  } = useGetModelCtlgList(
    {
      page: searchValues.page - 1,
      size: searchValues.size,
      search: searchValues.searchKeyword,
      filter: buildFilterString(),
      queryKey: 'model-ctlg-list',
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  // list data 처리
  const listData = useMemo(() => {
    return (
      modelCtlgList?.content.map((item: any, index: number) => ({
        ...item,
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        name: item?.displayName || item.name,
      })) ?? []
    );
  }, [modelCtlgList?.content]);

  // 페이지/사이즈 변경 시 검색값 갱신 후 refetch (드롭다운·페이지네이션 공통)
  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '모델명',
        field: 'name',
        width: 272,
      },
      {
        headerName: '유효성',
        field: 'isValid',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          return (
            <UILabel variant='badge' intent={params.value ? 'complete' : 'error'}>
              {params.value ? '유효' : '유효하지않음'}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={params.value}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tags',
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        sortable: false,
        cellRenderer: (params: any) => {
          const tags: { name: string }[] = params.data.tags || [];

          const tagText = tags.join(', ');
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
                <div className='flex gap-1 flex-wrap'>
                  {tags.slice(0, 2).map((tag: { name: string }, index: number) => (
                    <UITextLabel key={index} intent='tag'>
                      {tag.name}
                    </UITextLabel>
                  ))}
                  {/* 2개 이상일 경우 ... 처리 */}
                  {tags.length > 2 && (
                    <UITypography variant='caption-2' className='secondary-neutral-550'>
                      {'...'}
                    </UITypography>
                  )}
                </div>
              </div>
            </div>
          );
        },
      },
      {
        headerName: '모델유형',
        field: 'type',
        width: 120,
      },
      {
        headerName: '배포유형',
        field: 'servingType',
        width: 120,
      },
      {
        headerName: '배포여부',
        field: 'deployStatus',
        width: 120,
        cellRenderer: (params: any) => {
          if (!params.value) return '미배포';
          return `${params.value
            .split(', ')
            .map((item: string) => (item === 'DEV' ? '개발' : item === 'PROD' ? '운영' : ''))
            .join('∙')} 배포`;
        },
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        valueFormatter: (params: any) => {
          return params.value ? dateUtils.formatDate(params.value, 'datetime') : '';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        valueFormatter: (params: any) => {
          return params.value ? dateUtils.formatDate(params.value, 'datetime') : '';
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    []
  );

  // checkbox
  const [selectedDataList, setSelectedDataList] = useState<ModelCtlgType[]>([]);
  const handleSelect = (modelList: (typeof listData)[number][]) => {
    // console.log('modelList', modelList);
    setSelectedDataList(modelList);
  };

  // 삭제
  const { mutate: deleteModelCtlgBulk } = useDeleteModelCtlgBulk();
  const deleteCallback = () => {
    openAlert({
      title: '완료',
      message: '모델이 삭제되었습니다.',
    });
    refetch();
  };
  const handleDeleteModelCtlgBulk = async (type: 'single' | 'bulk', id?: string) => {
    // TODO public base 모델 '삭제' 클릭시 "안내" "권한이 없습니다" 표시 필요
    if (type === 'bulk' && selectedDataList.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }
    if (
      (type === 'single' && id !== undefined && !!selectedDataList.find(item => item.id === id)?.deployStatus) ||
      (type === 'bulk' && selectedDataList.some(selectedItem => !!selectedItem.deployStatus))
    ) {
      openAlert({
        title: '안내',
        message: '배포된 모델은 삭제할 수 없습니다.',
      });
      return;
    }
    let deleteMessage = '삭제하시겠어요?\n삭제된 정보는 복구할 수 없습니다.';
    if (type === 'bulk' && selectedDataList.filter(item => item.servingType === 'self-hosting').length > 0) {
      deleteMessage = '삭제하시겠어요?\nSelf-hosting 모델은 삭제 후 재등록시 다시 처음부터 반입해야합니다.';
    } else if (type === 'single' && id !== undefined) {
      const deleteItem = listData.find(item => item.id === id);
      if (deleteItem && deleteItem.servingType === 'self-hosting') {
        deleteMessage = '삭제하시겠어요?\nSelf-hosting 모델은 삭제 후 재등록시 다시 처음부터 반입해야합니다.';
      }
    }

    const isOk = await openConfirm({
      title: '안내',
      message: deleteMessage,
    });
    if (isOk) {
      if (type === 'bulk') {
        deleteModelCtlgBulk(
          { items: selectedDataList.map(item => ({ type: item.servingType as 'serverless' | 'self-hosting', id: item.id })) },
          {
            onSuccess: () => {
              deleteCallback();
            },
          }
        );
      } else if (id !== undefined) {
        const deleteItem = listData.find(item => item.id === id);
        deleteModelCtlgBulk(
          { items: [{ type: deleteItem.servingType as 'serverless' | 'self-hosting', id }] },
          {
            onSuccess: () => {
              deleteCallback();
            },
          }
        );
      } else {
        // console.log('선택된 ID가 없습니다.');
      }
    }
  };

  // 모델추가버튼 클릭
  const handleOnModelAddClick = () => {
    createModelLayerPopup.onOpen();
  };

  const handleModelCtlgCreatePopupOnClose = () => {
    refetch();
    createModelLayerPopup.onClose();
  };

  // 모델 배포 버튼
  const handleOnModelDeploy = (step: number) => {
    deployModelLayerPopup.setCustomStep(step);
  };

  // 모델 수정
  const handleOnModelUpdate = (data: ModelCtlgType) => {
    setSelectedModel(data);
    updateModelLayerPopup.onOpen();
  };
  const handleUpdateSuccess = () => {
    updateModelLayerPopup.onClose();
    refetch();
  };

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '모델 배포',
          action: 'deploy',
          auth: AUTH_KEY.DEPLOY.MODEL_DEPLOY_CREATE,
          onClick: (data: ModelCtlgType) => {
            setSelectedModel(data);
            handleOnModelDeploy(2);
          },
        },
        {
          label: '파인튜닝 등록',
          action: 'finetuning',
          auth: AUTH_KEY.MODEL.FINE_TUNING_CREATE,
          onClick: (data: ModelCtlgType) => {
            setSelectedModel(data);
            // TODO 파인튜닝 등록 팝업 연결
            // handleOnModelFinetuning();
          },
          visible: (rowData: ModelCtlgType) => {
            return rowData.servingType === 'self-hosting';
          },
        },
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.MODEL.MODEL_CATALOG_UPDATE,
          onClick: (data: ModelCtlgType) => {
            handleOnModelUpdate(data);
          },
          visible: (rowData: ModelCtlgType) => {
            return rowData.servingType === 'serverless' || (rowData.servingType === 'self-hosting' && !!rowData.trainingId);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.MODEL.MODEL_CATALOG_DELETE,
          onClick: (data: ModelCtlgType) => {
            handleDeleteModelCtlgBulk('single', data.id);
          },
        },
      ],
    }),
    [selectedDataList, listData]
  );

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='모델 관리'
          description={[
            '내부로 반입된 모델의 상세정보와 개발망 배포 여부를 확인할 수 있습니다.',
            '모델을 클릭한 뒤, 업무 목적에 맞는 파인튜닝을 하거나 개발망에 배포해 활용해 보세요.',
          ]}
          actions={
            <>
              <Button
                auth={AUTH_KEY.MODEL.SERVERLESS_MODEL_REGISTER}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-add', children: '' }}
                onClick={handleOnModelAddClick}
              >
                Serverless 모델등록
              </Button>
              {/*<Button*/}
              {/*  className='btn-text-14-semibold-point'*/}
              {/*  auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_CREATE}*/}
              {/*  leftIcon={{ className: 'ic-system-24-add', children: '' }}*/}
              {/*  onClick={() => {*/}
              {/*    handleOnModelDeploy(1);*/}
              {/*  }}*/}
              {/*>*/}
              {/*  모델 배포*/}
              {/*</Button>*/}
            </>
          }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
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
                          <UIInput.Search
                            value={searchValues.searchKeyword}
                            onChange={e => {
                              setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                            }}
                            placeholder='모델명, 설명, 태그 입력'
                          />
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            모델유형
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={searchValues.modelType === 'all' ? 'all' : searchValues.modelType}
                            placeholder='조회 조건 선택'
                            options={
                              typeList
                                ? [{ value: 'all', label: '전체' }, ...typeList.types.map((type: string) => ({ value: type, label: type }))]
                                : []
                            }
                            refetchOnOpen={refetchModelTypes}
                            onSelect={value => setSearchValues(prev => ({ ...prev, modelType: value }))}
                          />
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            배포유형
                          </UITypography>
                        </th>
                        <td>
                          <UIUnitGroup gap={32} direction='row'>
                            <div style={{ width: '100%' }}>
                              <UIDropdown
                                value={searchValues.deployType === 'all' ? 'all' : searchValues.deployType}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: 'all', label: '전체' },
                                  { value: 'self-hosting', label: 'self-hosting' },
                                  { value: 'serverless', label: 'serverless' },
                                ]}
                                onSelect={value => setSearchValues(prev => ({ ...prev, deployType: value }))}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            유효성
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={searchValues.isValid}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: 'all', label: '전체' },
                              { value: 'Y', label: '유효' },
                              { value: 'N', label: '무효' },
                            ]}
                            onSelect={value => setSearchValues(prev => ({ ...prev, isValid: value }))}
                          />
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            태그
                          </UITypography>
                        </th>
                        <td>
                          <div>
                            <UIDropdown
                              value={searchValues.tags}
                              placeholder='조회 조건 선택'
                              options={
                                tagList
                                  ? [{ value: 'all', label: '전체' }, ...tagList.tags.map((tag: string) => ({ value: tag, label: tag }))]
                                  : []
                              }
                              refetchOnOpen={refetchModelTags}
                              onSelect={value => setSearchValues(prev => ({ ...prev, tags: value }))}
                            />
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <Button
                    className='btn-secondary-blue'
                    style={{ width: '100%' }}
                    onClick={() => {
                      refetch();
                    }}
                  >
                    조회
                  </Button>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>
          <UIArticle className='article-grid'>
            {/* 다중 선택 그리드 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIGroup gap={8} direction='row' align='start'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={modelCtlgList?.totalElements} />
                    </div>
                  </UIGroup>
                </div>
                <div className='flex items-center gap-2'>
                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(searchValues.size)}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => updatePageSizeAndRefetch({ page: 1, size: Number(value) })}
                      height={40}
                      variant='dataGroup'
                      disabled={!(listData.length > 0)}
                    />
                  </div>
                  <UIToggle
                    variant='dataView'
                    checked={searchValues.gridType === 'card'}
                    onChange={checked => setSearchValues(prev => ({ ...prev, gridType: checked ? 'card' : 'grid' }))}
                    disabled={!(listData.length > 0)}
                  />
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.gridType === 'grid' ? (
                  <UIGrid<ModelCtlgType>
                    type='multi-select'
                    loading={isFetching}
                    rowData={listData}
                    selectedDataList={selectedDataList}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(params: any) => {
                      navigate(`${params.data.id}`);
                    }}
                    onCheck={(modelList: ModelCtlgType[]) => {
                      handleSelect(modelList);
                    }}
                  />
                ) : (
                  <UICardList
                    rowData={listData}
                    flexType='none'
                    loading={isFetching}
                    card={(item: ModelCtlgType) => {
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.name}
                          caption={item.description}
                          data={item}
                          moreMenuConfig={moreMenuConfig}
                          onClick={() => {
                            navigate(`${item.id}`);
                          }}
                          statusArea={
                            <UILabel variant='badge' intent={item.isValid ? 'complete' : 'error'}>
                              {item.isValid ? '유효' : '무효'}
                            </UILabel>
                          }
                          checkbox={{
                            checked: selectedDataList.some(selectedItem => selectedItem.id === item.id),
                            onChange: (checked: boolean) => {
                              if (checked) setSelectedDataList([...selectedDataList, item]);
                              else setSelectedDataList(selectedDataList.filter(selectedItem => selectedItem.id !== item.id));
                            },
                          }}
                          rows={[
                            { label: '태그', value: item.tags.map((tag: TagType) => tag.name).join(', ') },
                            { label: '모델유형', value: item.type },
                            { label: '배포유형', value: item.servingType },
                            { label: '공개범위', value: item.isPrivate ? '내부공유' : '전체공유' },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.MODEL.MODEL_CATALOG_DELETE}
                  className='btn-option-outlined'
                  style={{ width: '40px' }}
                  disabled={!(listData.length > 0)}
                  onClick={() => handleDeleteModelCtlgBulk('bulk')}
                >
                  삭제
                </Button>
                <UIPagination
                  currentPage={searchValues.page}
                  hasNext={modelCtlgList?.hasNext}
                  totalPages={modelCtlgList?.totalPages || 1}
                  onPageChange={page => updatePageSizeAndRefetch({ page })}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>

      {createModelLayerPopup.currentStep > 0 && (
        <ModelCtlgCreatePopupPage
          currentStep={createModelLayerPopup.currentStep}
          onNextStep={createModelLayerPopup.onNextStep}
          onPreviousStep={createModelLayerPopup.onPreviousStep}
          onClose={handleModelCtlgCreatePopupOnClose}
        />
      )}
      {deployModelLayerPopup.currentStep > 0 && <DeployModelCreatePopupPage {...deployModelLayerPopup} defaultModel={selectedModel} />}

      {selectedModel && updateModelLayerPopup.currentStep > 0 && <ModelCtlgEditPage {...updateModelLayerPopup} id={selectedModel.id} onSuccess={handleUpdateSuccess} />}
    </>
  );
};
