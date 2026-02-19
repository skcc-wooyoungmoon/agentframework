import React, { useEffect, useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import { UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetModelGardenList } from '@/services/model/garden/modelGarden.services';
import type { ModelGardenInfo } from '@/services/model/garden/types';

import { type ModelGardenInStepProps } from './ModelGardnIn';

/**
 * @author SGO1032948
 * @description 모델가든 self-hosting 모델 정보 입력
 *
 * MD_050101_P02
 */
export const MdGdnImpStep2Popup = ({ currentStep, onClose, onNextStep, onPreviousStep, info, onSetInfo }: ModelGardenInStepProps) => {
  const [filters, setFilters] = useState({
    page: 1,
    size: 12,
    search: '',
  });
  const { data, isFetched, refetch } = useGetModelGardenList(
    {
      page: filters.page,
      size: filters.size,
      dplyTyp: 'self-hosting',
      status: 'BEFORE',
      search: filters.search,
    },
    {
      enabled: false,
    },
    'IN_PROCESS'
  );

  useEffect(() => {
    refetch();
  }, []);

  useEffect(() => {
    refetch();
  }, [filters.page, filters.size]);

  const rowData = useMemo(() => {
    return data?.content.map((item, index) => ({
      ...item,
      no: index + 1 + (filters.page - 1) * filters.size,
    }));
  }, [data?.content]);

  useEffect(() => {
    if (info.id === '') {
      if (isFetched && rowData && rowData[0]) {
        onSetInfo(rowData[0]);
      }
    }
  }, [data, isFetched, rowData]);

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '버전',
        field: 'version',
        width: 140,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  const handleClose = () => {
    onClose();
  };

  const handleNext = () => {
    onNextStep();
  };

  const handleSelect = (params: ModelGardenInfo[]) => {
    onSetInfo(params[0]);
  };

  const handlePrevious = () => {
    onPreviousStep();
  };

  const isDisabled = useMemo(() => {
    return info.id === '';
  }, [info]);

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 2}
        onClose={handleClose}
        size='fullscreen'
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 반입' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIArticle>
                <UIStepper
                  items={[
                    { id: 'step1', step: 1, label: '반입 모델 선택' },
                    { id: 'step2', step: 2, label: '모델 정보 입력' },
                  ]}
                  currentStep={1}
                  direction='vertical'
                />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray' onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-aside-blue' disabled>
                    반입요청
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
          <UIPopupHeader title='반입 모델 선택' description='반입할 모델을 선택해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
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
                            refetch();
                          }
                        }}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  {isFetched && (
                    <UIGrid<ModelGardenInfo> type='single-select' rowData={rowData} columnDefs={columnDefs} selectedDataList={info.id ? [info] : []} onCheck={handleSelect} />
                  )}
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination
                    currentPage={filters.page}
                    totalPages={data?.totalPages || 0}
                    onPageChange={page => setFilters({ ...filters, page })}
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
                <UIButton2 className='btn-secondary-gray' onClick={handlePrevious}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={isDisabled}>
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
