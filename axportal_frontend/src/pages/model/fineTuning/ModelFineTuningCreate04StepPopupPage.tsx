import { useAtom } from 'jotai';
import React, { useEffect, useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UILabel } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetDatasets } from '@/services/data/dataCtlgDataSet.services.ts';
import {
  fineTuningDatasetCurrentPageAtom,
  fineTuningDatasetSearchTextAtom,
  fineTuningLearningTypeAtom,
  fineTuningSelectedDatasetIdsAtom,
  resetAllFineTuningDataAtom,
} from '@/stores/model/fineTuning/fineTuning.atoms';
import dateUtils from '@/utils/common/date.utils.ts';

interface LayerPopupProps {
  currentStep: number;
  stepperItems?: UIStepperItem[];
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
}

const typeOptions = [
  { value: 'all', label: '전체' },
  { value: 'supervised_finetuning', label: '지도학습' },
  { value: 'unsupervised_finetuning', label: '비지도학습' },
  { value: 'dpo_finetuning', label: 'DPO' },
  { value: 'custom', label: 'Custom' },
];

export const ModelFineTuningCreate04StepPopupPage: React.FC<LayerPopupProps> = ({ currentStep, stepperItems = [], onClose, onNextStep, onPreviousStep }) => {
  // useModal 훅
  const { showCancelConfirm } = useCommonPopup();

  // Jotai 상태 관리
  const [selectedIds, setSelectedIds] = useAtom(fineTuningSelectedDatasetIdsAtom);
  const [searchText, setSearchText] = useAtom(fineTuningDatasetSearchTextAtom);
  const [currentPage, setCurrentPage] = useAtom(fineTuningDatasetCurrentPageAtom);
  const [, resetAllData] = useAtom(resetAllFineTuningDataAtom);

  const [learningType] = useAtom(fineTuningLearningTypeAtom);

  // 로컬 상태
  const [itemsPerPage] = useState(12); // 페이지당 5개 항목 표시

  const handleNext = () => {
    onNextStep();
  };

  // 취소 핸들러 - 확인 알러트 후 상태 초기화
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        // 모든 상태값 초기화 (Jotai 공통 함수 사용)
        resetAllData();
      },
    });
  };

  const {
    data: datasetsData,
    refetch,
    isFetching,
  } = useGetDatasets({
    page: currentPage,
    size: itemsPerPage,
    search: searchText,
    filter: `status:completed,type:` + (learningType === 'dpo' ? 'dpo_finetuning' : learningType === 'supervised' ? 'supervised_finetuning' : 'unsupervised_finetuning'),
  });

  useEffect(() => {
    refetch();
  }, [currentPage]);

  const rowData = useMemo(() => {
    if (!datasetsData?.content || datasetsData.content.length === 0) return [];
    return datasetsData.content.map((item: any, index: number) => ({
      ...item,
      no: (currentPage - 1) * itemsPerPage + index + 1,
    }));
  }, [datasetsData]);

  // Status 정의 (DataSetListPage와 동일)
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

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
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
      },
      {
        headerName: '이름',
        field: 'name' as const,
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const status = params.value as keyof typeof STATUS_CONFIG;
          const config = STATUS_CONFIG[status] || {
            label: params.value,
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
        field: 'description' as const,
        flex: 1,
        minWidth: 329,
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
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const tags = params.value; // 태그 배열

          if (!Array.isArray(tags) || tags.length === 0) {
            return null;
          }

          const tagText = tags.map((tag: any) => tag.name).join(', ');
          const tagTextArray = tags.map((tag: any) => tag.name);

          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {tagTextArray.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      {
        headerName: '유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return typeOptions.find(option => option.value === params.value)?.label || params.value;
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any) => {
          return params.data.createdAt ? dateUtils.formatDate(params.data.createdAt, 'datetime') : '';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any) => {
          return params.data.updatedAt ? dateUtils.formatDate(params.data.updatedAt, 'datetime') : '';
        },
      },
    ],
    []
  );

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 4}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='파인튜닝 등록' position='left' />
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
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    튜닝시작
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='학습 데이터세트 선택' description='' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={16} direction='column'>
                      <div className='flex justify-between w-full items-center'>
                        <div className='w-full'>
                          <UIGroup gap={12} direction='row' align='start'>
                            <div style={{ width: '102px', display: 'flex', alignItems: 'center' }}>
                              <UIDataCnt count={datasetsData?.totalElements} />
                            </div>

                            <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                              <div style={{ width: '360px', flexShrink: 0 }}>
                                {/* 검색 입력 */}
                                <UIInput.Search
                                  value={searchText}
                                  onChange={e => {
                                    setSearchText(e.target.value);
                                  }}
                                  placeholder='검색어 입력'
                                  onKeyDown={e => {
                                    if (e.key === 'Enter') {
                                      refetch();
                                    }
                                  }}
                                />
                              </div>
                            </div>
                          </UIGroup>
                        </div>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='multi-select'
                    loading={isFetching}
                    rowData={rowData}
                    columnDefs={columnDefs}
                    selectedDataList={selectedIds}
                    checkKeyName={'id'}
                    onClickRow={(params: any) => {
                      const columnId = params?.colDef?.field;
                      if (columnId !== 'checkbox') {
                        // console.log('데이터세트 선택 onClickRow', params);
                      }
                    }}
                    onCheck={(selectedDataList: any[]) => {
                      setSelectedIds(selectedDataList);
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination
                    currentPage={currentPage}
                    totalPages={datasetsData?.totalPages || 1}
                    onPageChange={(page: number) => setCurrentPage(page)}
                    className='flex justify-center'
                    hasNext={datasetsData?.hasNext}
                  />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' disabled={selectedIds.length === 0} style={{ width: '80px' }} onClick={handleNext}>
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
