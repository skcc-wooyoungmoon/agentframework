import { useAtom } from 'jotai';
import React, { useEffect, useMemo, useState } from 'react';

import { UIDataCnt } from '@/components/UI';
import { UIButton2, UILabel, UIPagination } from '@/components/UI/atoms';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIArticle, UIFormField, UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetModelCtlgList } from '@/services/model/ctlg/modelCtlg.services';
import type { ModelCtlgType } from '@/services/model/ctlg/types';
import {
  fineTuningModelCurrentPageAtom,
  fineTuningModelSearchTextAtom,
  fineTuningSelectedModelAtom,
  fineTuningSelectedModelIdAtom,
  resetAllFineTuningDataAtom,
} from '@/stores/model/fineTuning/fineTuning.atoms';
import { dateUtils } from '@/utils/common';

interface LayerPopupProps {
  currentStep: number;
  stepperItems: UIStepperItem[];
  onNextStep: () => void;
  onClose: () => void;
}

const size = 12;

export const ModelFineTuningCreate01StepPopupPage: React.FC<LayerPopupProps> = ({ currentStep, stepperItems, onClose, onNextStep }) => {
  // useModal 훅
  const { showCancelConfirm } = useCommonPopup();

  // Jotai 상태 관리
  const [selectedId, setSelectedId] = useAtom(fineTuningSelectedModelIdAtom);
  const [selectedModel, setSelectedModel] = useAtom(fineTuningSelectedModelAtom);
  const [searchText, setSearchText] = useAtom(fineTuningModelSearchTextAtom);
  const [currentPage, setCurrentPage] = useAtom(fineTuningModelCurrentPageAtom);
  const [, resetAllData] = useAtom(resetAllFineTuningDataAtom);

  const [isInit, setIsInit] = useState(true);

  // React Query 훅 사용
  const {
    data: modelsData,
    refetch,
    isFetching,
  } = useGetModelCtlgList(
    {
      page: currentPage - 1, // 서버는 0부터 시작
      size: size,
      search: searchText || undefined,
      // filter: searchFilter !== '모델명' ? searchFilter : undefined,
      filter: 'serving_type:self-hosting,is_valid:true,type:language',
      queryKey: 'create-funetuing',
    },
    {
      enabled: true, // 자동으로 데이터 가져오기
    }
  );

  useEffect(() => {
    refetch();
  }, [currentPage]);

  // 파인튜닝 등록 진입 시 selectedModel이 없으면 최초 1번만 첫 번째 모델 자동 선택
  useEffect(() => {
    if (isInit && !selectedModel && modelsData?.content && modelsData.content.length > 0) {
      setIsInit(false);
      setSelectedId(modelsData.content[0].id);
      setSelectedModel(modelsData.content[0]);
    }
  }, [modelsData]);

  useEffect(() => {
    if (selectedModel) {
      setIsInit(false);
    }
  }, []);

  const rowData = useMemo(() => {
    if (!modelsData?.content || modelsData?.content.length === 0) {
      return [];
    }
    return modelsData.content.map((item, index) => ({
      no: (currentPage - 1) * size + index + 1,
      ...item,
    }));
  }, [modelsData]);

  // 취소 핸들러 - 확인 알러트 후 상태 초기화
  const handleCancel = () => {

    showCancelConfirm({
      onConfirm: () => {
        // 모든 상태값 초기화 (Jotai 공통 함수 사용)
        resetAllData();
      },
    });
  };

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
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '모델명',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
        valueFormatter: (params: any) => {
          return params.data.displayName || params.value;
        },
      },
      {
        headerName: '유효성',
        field: 'isValid' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
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
        field: 'description' as const,
        flex: 1,
        minWidth: 392,
        showTooltip: true,
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
        headerName: '모델유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포유형',
        field: 'servingType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포여부',
        field: 'deployStatus' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return params.value === 'DEV' ? '개발 배포' : '미배포';
        },
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueFormatter: (params: any) => {
          const value = params.data.createdAt;
          return value ? dateUtils.formatDate(value, 'datetime') : '';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueFormatter: (params: any) => {
          const value = params.data.updatedAt;
          return value ? dateUtils.formatDate(value, 'datetime') : '';
        },
      },
    ],
    []
  );

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='파인튜닝 생성' position='left' />
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
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='모델 선택' description='' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='grid-header-left'>
                    <UIGroup gap={12} direction='row' vAlign={'center'}>
                      <div style={{ width: '102px' }}>
                        <UIDataCnt count={modelsData?.totalElements} />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='grid-header-right'>
                    <UIFormField gap={8} direction='row' vAlign={'center'}>
                      <div className='w-[360px]'>
                        <UIInput.Search
                          value={searchText}
                          onChange={e => setSearchText(e.target.value)}
                          placeholder='검색어 입력'
                          onKeyDown={e => e.key === 'Enter' && refetch()}
                        />
                      </div>
                    </UIFormField>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='single-select'
                    loading={isFetching}
                    rowData={rowData}
                    columnDefs={columnDefs}
                    selectedDataList={selectedModel ? [selectedModel] : []}
                    checkKeyName={'id'}
                    onClickRow={(_params: any) => {
                      // 행 클릭 시 동작
                    }}
                    onCheck={(datas: ModelCtlgType[]) => {
                      console.log('datas', datas);
                      if (datas.length > 0) {
                        setSelectedModel(datas[0]);
                        setSelectedId(datas[0].id);
                      } else {
                        setSelectedModel(null);
                        setSelectedId('');
                      }
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination
                    currentPage={currentPage}
                    totalPages={modelsData?.totalPages || 1}
                    onPageChange={(page: number) => setCurrentPage(page)}
                    className='flex justify-center'
                    hasNext={modelsData?.hasNext}
                  />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' onClick={onNextStep} disabled={!selectedId}>
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
