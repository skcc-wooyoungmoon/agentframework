import React, { useEffect, useState } from 'react';

import { UIButton2, UIDataCnt, UIPagination } from '@/components/UI/atoms';
import { UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetModelGardenList } from '@/services/model/garden/modelGarden.services.ts';
import type { ModelGardenInfo } from '@/services/model/garden/types.ts';

interface ModelCtlgStep1PickPopupProps {
  isOpen: boolean;
  onNextStep: () => void;
  onClose: () => void;
  stepperItems: UIStepperItem[];
  selectedModelGarden: ModelGardenInfo | undefined;
  setSelectedModelGarden: (data: ModelGardenInfo | undefined) => void;
}

export const ModelCtlgStep1PickPopup: React.FC<ModelCtlgStep1PickPopupProps> = ({
  isOpen,
  onNextStep,
  onClose,
  stepperItems,
  selectedModelGarden,
  setSelectedModelGarden,
}: ModelCtlgStep1PickPopupProps) => {
  // 검색 상태
  const [searchText, setSearchText] = useState('');

  const [searchValues, setSearchValues] = useState({
    page: 1,
    size: 12,
    search: '',
  });

  const {
    data: modelGardenList,
    refetch,
    isFetching,
  } = useGetModelGardenList({
    page: searchValues.page,
    size: searchValues.size,
    dplyTyp: 'serverless',
    search: searchValues.search,
    status: 'IMPORT_COMPLETED',
  });

  useEffect(() => {
    refetch();
  }, [searchValues]);

  const handleClose = () => {
    onClose();
  };

  const handleNextStep = () => {
    onNextStep();
  };

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, search: searchText }));
  };

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
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => {
          const page = searchValues.page || 1;
          const size = searchValues.size || 12;
          return (page - 1) * size + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '모델명',
        field: 'name' as const,
        width: 240,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'created_at' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'updated_at' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    [searchValues]
  );

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 등록' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    등록
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
          <UIPopupHeader
            title='모델 선택'
            description='모델 탐색에 반입이 완료된 serverless 모델 리스트에서 선택한 프로젝트의 모델 관리 메뉴에 등록할 모델을 선택해주세요.'
            position='right'
          />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={modelGardenList?.totalElements || 0} prefix='반입완료 모델 총' />
                    </div>
                  </div>
                  <div className='flex-shrink-0'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchText}
                        onChange={e => {
                          setSearchText(e.target.value);
                        }}
                        placeholder='모델명, 설명 입력'
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
                  <UIGrid
                    type='single-select'
                    loading={isFetching}
                    rowData={modelGardenList?.content || []}
                    columnDefs={columnDefs}
                    selectedDataList={selectedModelGarden ? [selectedModelGarden] : []}
                    onCheck={(selectedList: any[]) => {
                      // console.log('모델 선택:', selectedList);
                      setSelectedModelGarden(selectedList?.[0] || undefined);
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination
                    currentPage={searchValues?.page || 1}
                    totalPages={modelGardenList?.totalPages || 0}
                    onPageChange={newPage => {
                      setSearchValues(prev => ({ ...prev, page: newPage }));
                    }}
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
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNextStep} disabled={!selectedModelGarden}>
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
