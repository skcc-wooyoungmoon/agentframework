import React, { useEffect, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIDropdown, UIFormField, UIGroup, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';

import type { LayerPopupProps } from '@/hooks/common/layer';
import { useGetModelCtlgList, useGetModelTypes } from '@/services/model/ctlg/modelCtlg.services.ts';
import type { ModelCtlgType } from '@/services/model/ctlg/types.ts';

type SearchValues = {
  page: number;
  size: number;
  dateType: string;
  searchType: string;
  searchKeyword: string;
  deployType: string;
  modelType: string;
};

interface DeployModelStep1ModelSelectProps extends LayerPopupProps {
  selectedModel: ModelCtlgType | undefined;
  handleModelSelect: (model: ModelCtlgType | undefined) => void;
}

export const DeployModelStep1ModelSelect = ({ currentStep, stepperItems = [], onClose, onNextStep, selectedModel, handleModelSelect }: DeployModelStep1ModelSelectProps) => {
  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: 12,
    dateType: 'createdAt',
    searchType: 'name',
    searchKeyword: '',
    deployType: 'all',
    modelType: 'all',
  });

  const { data: modelTypes } = useGetModelTypes();

  const [searchText, setSearchText] = useState<string>('');

  const getSearchfilter = () => {
    let searchFilter = 'is_valid:true';
    if (searchValues.deployType && searchValues.deployType !== 'all') {
      searchFilter += `,serving_type:${searchValues.deployType}`;
    }
    if (searchValues.modelType && searchValues.modelType !== 'all') {
      searchFilter += `,type:${searchValues.modelType}`;
    }
    return searchFilter;
  };

  // 목록 조회
  const { data, refetch, isLoading, isRefetching } = useGetModelCtlgList({
    page: searchValues.page - 1,
    size: searchValues.size,
    search: searchValues.searchKeyword,
    filter: getSearchfilter(),
    queryKey: 'model-deploy',
  });

  useEffect(() => {
    refetch();
  }, [searchValues.deployType, searchValues.modelType, searchValues.page, searchValues.searchKeyword]);

  const handleDropdownSelect = (key: string, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value, page: 1 }));
  };

  const handleClose = () => {
    onClose();
  };

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, searchKeyword: searchText, page: 1 }));
  };

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
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
        width: 240,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description',
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
        headerName: '모델유형',
        field: 'type',
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포유형',
        field: 'servingType',
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    [searchValues.page, searchValues.size]
  );

  const rowData: ModelCtlgType[] = React.useMemo(() => {
    return (
      data?.content?.map((model, index) => ({
        ...model,
        no: (searchValues.page - 1) * searchValues.size + index + 1,
      })) || []
    );
  }, [data?.content]);

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 배포하기' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={currentStep} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={onClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    배포
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='모델 선택' description='배포할 모델을 선택해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='grid-header-left'>
                    <UIGroup gap={12} direction='row' vAlign={'center'}>
                      <div style={{ width: '102px' }}>
                        <UIDataCnt count={data?.totalElements || 0} />
                      </div>
                      <div>
                        <UIFormField gap={8} direction='row' vAlign={'center'}>
                          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                            배포유형
                          </UITypography>
                          <div>
                            <div className='w-[180px]'>
                              <UIDropdown
                                placeholder='조회 조건 선택'
                                value={searchValues.deployType}
                                options={[
                                  { value: 'all', label: '전체' },
                                  { value: 'self_hosting', label: 'self-hosting' },
                                  { value: 'serverless', label: 'serverless' },
                                ]}
                                height={40}
                                onSelect={value => handleDropdownSelect('deployType', value)}
                              />
                            </div>
                          </div>
                        </UIFormField>
                      </div>
                      <div>
                        <UIFormField gap={8} direction='row' vAlign={'center'}>
                          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                            모델유형
                          </UITypography>
                          <div>
                            <div className='w-[180px]'>
                              <UIDropdown
                                value={searchValues.modelType}
                                options={[{ value: 'all', label: '전체' }, ...(modelTypes?.types.map(type => ({ value: type, label: type })) || [])]}
                                height={40}
                                onSelect={value => handleDropdownSelect('modelType', value)}
                              />
                            </div>
                          </div>
                        </UIFormField>
                      </div>
                    </UIGroup>
                  </div>
                  <div className='grid-header-right'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchText}
                        onChange={e => {
                          setSearchText(e.target.value);
                        }}
                        placeholder='검색어 입력'
                        onKeyDown={e => {
                          if (e.key === 'Enter') {
                            handleSearch();
                          }
                        }}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid<ModelCtlgType>
                    type='single-select'
                    loading={isLoading || isRefetching}
                    rowData={rowData}
                    selectedDataList={selectedModel ? [selectedModel] : undefined}
                    checkKeyName={'id'}
                    columnDefs={columnDefs}
                    onCheck={(selectedModels: ModelCtlgType[]) => {
                      const selectedModel = selectedModels?.[0];
                      handleModelSelect(selectedModel);
                    }}
                  />
                </UIListContentBox.Body>

                <UIListContentBox.Footer>
                  <UIPagination
                    currentPage={searchValues.page || 1}
                    totalPages={data?.totalPages || 0}
                    onPageChange={newPage => {
                      setSearchValues(prev => ({ ...prev, page: newPage }));
                    }}
                    hasNext={data?.hasNext}
                    className='flex justify-center'
                  />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' onClick={onNextStep} disabled={!selectedModel}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
