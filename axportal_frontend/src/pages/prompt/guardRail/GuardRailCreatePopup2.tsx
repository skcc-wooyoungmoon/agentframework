import React, { useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import { UILabel } from '@/components/UI/atoms/UILabel';
import { UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { MODEL_DEPLOY_STATUS } from '@/constants/deploy/modelDeploy.constants';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { useUser } from '@/stores';

interface GuardRailCreatePopup2Props {
  isOpen?: boolean;
  onClose?: () => void;
  onPrevious?: () => void;
  onConfirm?: (selectedModels: GetModelDeployResponse[]) => void;
  name?: string;
  description?: string;
  promptId?: string;
  projectId?: string;
}

// 스테퍼 데이터
const stepperItems = [
  { step: 1, label: '기본 정보 입력' },
  { step: 2, label: '배포 모델 선택' },
];

const PAGE_SIZE = 12;

/**
 * 프롬프트 > 가드레일 > (TAB) 가드레일 관리 > 가드레일 생성 팝업2 (배포 모델 선택)
 */
export const GuardRailCreatePopup2 = ({ isOpen = true, onClose, onPrevious, onConfirm }: GuardRailCreatePopup2Props) => {
  // 검색 조건
  const [searchValues, setSearchValues] = useState({
    page: 1,
    searchKeyword: '',
  });

  // 입력 중인 검색어 (엔터 키를 누를 때만 searchKeyword에 반영)
  const [inputValue, setInputValue] = useState('');
  const [selectedModels, setSelectedModels] = useState<GetModelDeployResponse[]>([]);
  const { openAlert } = useModal();
  const { showCancelConfirm } = useCommonPopup();

  const {user} = useUser();
  const isPortalAdmin = user.activeProject.prjRoleSeq === '-199';

  // 배포 모델 목록 조회 (이용가능 상태만)
  const {
    data: modelList,
    refetch,
    isFetching,
  } = useGetModelDeployList({
    page: searchValues.page - 1,
    size: PAGE_SIZE,
    filter: 'status:Available,type:language',
    search: searchValues.searchKeyword,
  });

  // 검색어가 변경되면 API 재호출
  React.useEffect(() => {
    refetch();
  }, [searchValues.searchKeyword, searchValues.page]);

  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose?.();
      },
    });
  };

  const handlePrevious = () => {
    onPrevious?.();
  };

  // 검색어 엔터 키 핸들러 (엔터를 누를 때만 검색 실행)
  const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault(); // form submit 이벤트 방지
      setSearchValues(prev => ({
        ...prev,
        searchKeyword: inputValue,
        page: 1,
      }));
    }
  };

  // 모델 선택 핸들러
  const handleSelectModels = (datas: GetModelDeployResponse[]) => {
    setSelectedModels(datas);
  };

  // 만들기 버튼 핸들러
  const handleConfirm = async () => {
    // 1. 모델 선택 여부 검증
    if (selectedModels.length === 0) {
      await openAlert({
        title: '안내',
        message: '배포 모델을 선택해주세요.',
      });
      return;
    }

    // 2. 전체공유 모델 포함 여부 검증
    const hasPublicModel = selectedModels.some(model => model.publicStatus === '전체공유');

    if (hasPublicModel && !isPortalAdmin) {
      openAlert({
        title: '안내',
        message: '전체공유된 배포 모델이 포함되어 있는 경우 가드레일을 생성할 수 없습니다. 해당 모델 제외 후 다시 시도해주세요.',
      });
      return;
    }

    // 3. 검증 통과 시 가드레일 생성 진행
    onConfirm?.(selectedModels);
  };

  // API 응답 데이터를 가공한 행 데이터
  const rowData = useMemo(() => {
    return (
      modelList?.content.map((item, index) => ({
        id: item.servingId,
        ...item,
        no: (searchValues.page - 1) * PAGE_SIZE + index + 1,
        createdAt: dateUtils.formatDate(item.createdAt, 'datetime'),
        updatedAt: dateUtils.formatDate(item.updatedAt, 'datetime'),
      })) ?? []
    );
  }, [modelList, searchValues.page]);

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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '배포명',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const statusConfig = MODEL_DEPLOY_STATUS[params.value as keyof typeof MODEL_DEPLOY_STATUS];
          return (
            <UILabel variant='badge' intent={(statusConfig?.intent as any) || 'complete'}>
              {statusConfig?.label || params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '가드레일 적용 여부',
        field: 'guardrailApplied' as const,
        width: 152,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 392,
        cellStyle: { paddingLeft: '16px' },
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
      // {
      //   headerName: '운영배포 여부',
      //   field: 'production' as const,
      //   width: 120,
      //   cellStyle: { paddingLeft: '16px' },
      // },
      {
        headerName: '공개범위',
        field: 'publicStatus' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='가드레일 생성' position='left' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleConfirm} disabled={selectedModels.length === 0}>
                  만들기
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
        <UIPopupHeader title='배포 모델 선택' description='가드레일을 적용할 모델을 선택 후 만들기 버튼을 클릭해주세요.' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex items-center'>
                  <div style={{ width: '182px', paddingRight: '8px' }}>
                    <UIDataCnt count={modelList?.totalElements ?? 0} prefix='총' />
                  </div>
                </div>

                <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                  <div style={{ width: '360px', flexShrink: 0 }}>
                    <UIInput.Search
                      value={inputValue}
                      placeholder='배포명, 모델명, 설명 입력'
                      style={{
                        width: '100%',
                        boxSizing: 'border-box',
                      }}
                      onChange={e => {
                        setInputValue(e.target.value);
                      }}
                      onKeyDown={handleSearchKeyDown}
                    />
                  </div>
                </div>
              </UIListContentBox.Header>

              <UIListContentBox.Body>
                <UIGrid type='multi-select' rowData={rowData} columnDefs={columnDefs} selectedDataList={selectedModels} onCheck={handleSelectModels} loading={isFetching} />
              </UIListContentBox.Body>

              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={searchValues.page || 1}
                  hasNext={modelList?.hasNext}
                  totalPages={modelList?.totalPages || 1}
                  onPageChange={(newPage: number) => {
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
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handlePrevious}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
