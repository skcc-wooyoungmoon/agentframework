import React, { useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import { UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetModelGardenAvailable } from '@/services/model/garden/modelGarden.services';
import type { ModelGardenArtifactInfo } from '@/services/model/garden/types';

import stringUtils from '@/utils/common/string.utils';
import type { ModelGardenSearchStepProps } from './MdGdnAdd';

/**
 *
 * @author SGO1032948
 * @description Step1. 모델 검색 및 선택
 *
 * MD_050101_P05
 */
export const MdGdnAddStep1AddPickPopup = ({ currentStep, onNextStep, onClose, data, setData }: ModelGardenSearchStepProps) => {
  const [filters, setFilters] = useState({
    page: 1,
    size: 12,
    search: '',
  });

  const {
    data: artifactList,
    refetch,
    isLoading,
  } = useGetModelGardenAvailable(
    {
      search: filters.search,
    },
    {
      enabled: false,
    }
  );

  // 전체 데이터에서 페이지네이션된 데이터 계산
  const paginatedData = useMemo(() => {
    if (!artifactList?.artifacts) return [];

    const startIndex = (filters.page - 1) * filters.size;
    const endIndex = startIndex + filters.size;

    return artifactList.artifacts.slice(startIndex, endIndex);
  }, [artifactList?.artifacts, filters.page, filters.size]);

  const rowData = useMemo(() => {
    return paginatedData.map((item, index) => ({
      ...item,
      no: (filters.page - 1) * filters.size + index + 1,
    }));
  }, [paginatedData, filters.page, filters.size]);

  // 총 페이지 수 계산
  const totalPages = useMemo(() => {
    if (!artifactList?.artifacts) return 1;
    return Math.ceil(artifactList.artifacts.length / filters.size);
  }, [artifactList?.artifacts, filters.size]);

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
      },
      {
        headerName: '모델명',
        field: 'name',
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '크기',
        field: 'size',
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        valueFormatter: (params: any) => {
          return `${stringUtils.formatBytesToGB(params.value)}GB`;
        },
      },
    ],
    []
  );

  const handleClose = () => {
    onClose();
  };

  const handleSearch = () => {
    // 검색 시 첫 페이지로 이동
    setFilters(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  const handlePageChange = (page: number) => {
    setFilters(prev => ({ ...prev, page }));
  };

  const handleNext = () => {
    onNextStep();
  };

  return (
    <>
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 검색' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '모델 검색 및 선택' },
                  { id: 'step2', step: 2, label: '모델 정보 확인' },
                ]}
                currentStep={1}
                direction='vertical'
              />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    추가
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
          <UIPopupHeader title='모델 검색 및 선택' description='Reservoir의 self-hosting 모델 목록 중에서 모델 탐색에 추가할 모델을 검색하여 선택해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={artifactList?.artifacts.length || 0} prefix='총' />
                    </div>
                  </div>

                  <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                    <div style={{ width: '360px', flexShrink: 0 }}>
                      <UIInput.Search
                        value={filters.search}
                        placeholder='모델명 입력'
                        onChange={e => {
                          setFilters({ ...filters, search: e.target.value });
                        }}
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
                  <UIGrid<ModelGardenArtifactInfo>
                    type='single-select'
                    loading={isLoading}
                    rowData={rowData}
                    noDataMessage='키워드를 통해 검색시 결과가 노출됩니다.'
                    columnDefs={columnDefs}
                    selectedDataList={data ? [data] : []}
                    onCheck={(selectedList: ModelGardenArtifactInfo[]) => {
                      setData?.(selectedList[0]);
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={filters.page} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={!data}>
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
