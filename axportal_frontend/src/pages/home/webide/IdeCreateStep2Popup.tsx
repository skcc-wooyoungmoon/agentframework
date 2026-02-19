import React, { useMemo, useState } from 'react';

import { useAtom } from 'jotai';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import type { UIStepperItem } from '@/components/UI/molecules';
import { UIArticle, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetIdeImages } from '@/services/home/webide/ide.services';
import { ideCreWizardAtom } from '@/stores/home/webide/ideCreWizard.atoms';

/** IdeCreateStep2Popup Props 타입 */
interface IdeCreateStep2PopupProps {
  /** 팝업 표시 여부 */
  isOpen: boolean;
  /** 팝업 닫기 핸들러 */
  onClose: () => void;
  /** 이전 버튼 클릭 핸들러 */
  onPrev: () => void;
  /** 다음 버튼 클릭 핸들러 */
  onNext: () => void;
}

/** 스테퍼 아이템 */
const stepperItems: UIStepperItem[] = [
  { id: 'step1', label: '프로젝트 선택', step: 1 },
  { id: 'step2', label: '도구 및 이미지 선택', step: 2 },
  { id: 'step3', label: 'DW 계정 선택', step: 3 },
  { id: 'step4', label: '자원 선택', step: 4 },
];

/**
 * IDE 생성 Step2 - 도구 및 이미지 선택 팝업
 */
export const IdeCreateStep2Popup: React.FC<IdeCreateStep2PopupProps> = ({ isOpen, onClose, onPrev, onNext }) => {
  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  // 위자드 데이터 상태
  const [wizardData, setWizardData] = useAtom(ideCreWizardAtom);
  const selectedId = wizardData.selectedImageId;
  const setSelectedId = (id: string, type: string) => {
    setWizardData(prev => ({ ...prev, selectedImageId: id, selectedImageType: type }));
  };

  // 검색어 상태
  const [searchValue, setSearchValue] = useState('');
  // 입력값 상태
  const [inputValue, setInputValue] = useState('');

  // 현재 페이지
  const [currentPage, setCurrentPage] = useState(1);

  // 페이지당 아이템 수
  const pageSize = 12;

  // IDE 이미지 목록 조회
  const { data: ideImageData } = useGetIdeImages({ enabled: isOpen });

  // 필터링된 아이템 목록
  const filteredItems = useMemo(() => {
    if (!ideImageData?.images) return [];

    return ideImageData.images
      .map((img, index) => ({
        id: img.id,
        no: index + 1,
        imageType: img.type,
        imageName: img.name,
        description: img.desc,
      }))
      .filter(
        item =>
          item.imageType.toLowerCase().includes(searchValue.toLowerCase()) ||
          item.imageName.toLowerCase().includes(searchValue.toLowerCase()) ||
          item.description.toLowerCase().includes(searchValue.toLowerCase())
      );
  }, [ideImageData, searchValue]);

  // 선택된 아이템 목록 (UIGrid 전달용)
  const selectedDataList = useMemo(() => {
    const selected = filteredItems.find(item => item.id === selectedId);
    return selected ? [selected] : [];
  }, [filteredItems, selectedId]);

  // 현재 페이지의 아이템 목록
  const pagedItems = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return filteredItems.slice(startIndex, startIndex + pageSize);
  }, [filteredItems, currentPage]);

  // 전체 페이지 수
  const totalPages = Math.ceil(filteredItems.length / pageSize) || 1;

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
        headerName: '도구명',
        field: 'imageType' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value == 'JUPYTER' ? 'Jupyter Notebook' : 'VS Code'}
            </div>
          );
        }),
      },
      {
        headerName: '이미지명',
        field: 'imageName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
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
    ],
    []
  );

  /**
   * 체크박스 선택 핸들러
   */
  const handleCheck = (selectedRows: any[]) => {
    setSelectedId(selectedRows[0]?.id || '', selectedRows[0]?.imageType || '');
  };

  /**
   * 취소 버튼 클릭 핸들러
   */
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: onClose,
    });
  };

  /**
   * 이전 버튼 클릭 핸들러
   */
  const handlePrev = () => {
    onPrev();
  };

  /**
   * 다음 버튼 클릭 핸들러
   */
  const handleNext = () => {
    if (selectedId) {
      onNext();
    }
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          {/* 좌측 헤더 */}
          <UIPopupHeader title='IDE 생성' position='left' />
          {/* 좌측 바디 - 스테퍼 */}
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          {/* 좌측 푸터 */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled>
                  생성
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 */}
      <section className='section-popup-content'>
        {/* 우측 헤더 */}
        <UIPopupHeader title='도구 및 이미지 선택' description='사용할 도구 및 이미지를 선택해주세요.' position='right' />

        {/* 우측 바디 */}
        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={filteredItems.length} prefix='총' unit='건' />
                  </div>
                </div>
                <div className='flex-shrink-0'>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={inputValue}
                      onChange={e => setInputValue(e.target.value)}
                      onKeyDown={e => {
                        if (e.key === 'Enter') {
                          setSearchValue(inputValue);
                          setCurrentPage(1); // 검색 시 첫 페이지로 이동
                        }
                      }}
                      placeholder='도구명, 이미지명, 설명 입력'
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='single-select' rowData={pagedItems} columnDefs={columnDefs} onCheck={handleCheck} selectedDataList={selectedDataList} checkKeyName='id' />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPopupBody>

        {/* 우측 푸터 */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handlePrev}>
                이전
              </UIButton2>
              <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={!selectedId}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
