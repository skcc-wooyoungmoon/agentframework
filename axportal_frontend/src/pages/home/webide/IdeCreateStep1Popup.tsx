import React, { useEffect, useMemo, useState } from 'react';

import { useAtom } from 'jotai';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import type { UIStepperItem } from '@/components/UI/molecules';
import {
  UIArticle,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useUser } from '@/stores';
import { ideCreWizardAtom } from '@/stores/home/webide/ideCreWizard.atoms';

/** 프로젝트 아이템 타입 */
interface ProjectItem {
  id: number;
  no: number;
  projectName: string;
  description: string;
  roleName: string;
}

/** IdeCreateStep1Popup Props 타입 */
interface IdeCreateStep1PopupProps {
  /** 팝업 표시 여부 */
  isOpen: boolean;
  /** 팝업 닫기 핸들러 */
  onClose: () => void;
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
 * IDE 생성 Step1 - 프로젝트 선택 팝업
 */
export const IdeCreateStep1Popup: React.FC<IdeCreateStep1PopupProps> = ({ isOpen, onClose, onNext }) => {
  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  // 위자드 데이터 상태
  const [wizardData, setWizardData] = useAtom(ideCreWizardAtom);
  const selectedIds = wizardData.selectedProjectIds;
  const setSelectedIds = (ids: number[]) => {
    setWizardData(prev => ({ ...prev, selectedProjectIds: ids }));
  };

  // 검색어 상태
  const [searchValue, setSearchValue] = useState('');
  // 입력값 상태
  const [inputValue, setInputValue] = useState('');

  // 현재 페이지
  const [currentPage, setCurrentPage] = useState(1);

  const { user } = useUser();

  const projectList = useMemo(() => {
    return user.projectList
      .filter(el => Number(el.prjSeq) !== -999)
      .map((el, index) => ({
        no: index + 1,
        id: Number(el.prjSeq),
        projectName: el.prjNm,
        projectId: el.prjSeq,
        description: el.prjDesc,
        roleName: el.prjRoleNm,
      }));
  }, [user.projectList]);

  // 검색 필터링된 리스트
  const filteredProjectList = useMemo(() => {
    if (!searchValue) return projectList;
    const lowerSearchValue = searchValue.toLowerCase();
    return projectList.filter(
      item =>
        item.projectName?.toLowerCase().includes(lowerSearchValue) ||
        item.description?.toLowerCase().includes(lowerSearchValue) ||
        item.roleName?.toLowerCase().includes(lowerSearchValue)
    );
  }, [projectList, searchValue]);

  // 페이징 처리된 리스트
  const pageSize = 12;
  const totalCount = filteredProjectList.length;
  const totalPages = Math.max(1, Math.ceil(totalCount / pageSize));

  const displayList = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return filteredProjectList.slice(startIndex, startIndex + pageSize);
  }, [filteredProjectList, currentPage]);

  // 검색어 변경 시 페이지 초기화
  useEffect(() => {
    setCurrentPage(1);
  }, [searchValue]);

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
        headerName: '프로젝트명',
        field: 'projectName' as const,
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
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '프로젝트 ID',
        field: 'projectId' as const,
        width: 120,
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
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description' as const,
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
        headerName: '역할명',
        field: 'roleName' as const,
        minWidth: 272,
        showTooltip: true,
      },
    ],
    []
  );

  /**
   * 현재 페이지에서 선택된 행들만 추려서 UIGrid에 전달 (객체 동일성 보장)
   */
  const selectedRowsInCurrentPage = useMemo(() => {
    const idSet = new Set(selectedIds || []);
    return (displayList || []).filter((row: any) => idSet.has(row?.id));
  }, [displayList, selectedIds]);

  /**
   * 체크박스 선택 핸들러: 선택된 "ID"를 상태에 저장하여 페이지 이동 후에도 유지
   */
  const handleCheck = React.useCallback(
    (rows: ProjectItem[]) => {
      try {
        const getId = (item: ProjectItem) => item.id;
        // 현재 페이지에 표시된 ID 집합
        const currentPageIdSet = new Set((displayList || []).map((r: any) => getId(r)));
        // 현재 페이지 외의 이전 선택 ID는 보존
        const prevKeptIds = (selectedIds || []).filter((id: any) => !currentPageIdSet.has(id));
        // 이번 페이지에서 체크된 행의 ID
        const checkedIds = (Array.isArray(rows) ? rows : []).map(getId);
        // 병합 + 중복 제거
        const nextIds: number[] = Array.from(new Set([...prevKeptIds, ...checkedIds]));
        setSelectedIds(nextIds);
      } catch (e) {
        // console.warn('handleCheck error', e);
      }
    },
    [displayList, selectedIds]
  );

  /**
   * 취소 버튼 클릭 핸들러
   */
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: onClose,
    });
  };

  /**
   * 다음 버튼 클릭 핸들러
   */
  const handleNext = () => {
    // if (selectedIds.length > 0) {
    //   onNext();
    // }
    onNext();
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
              <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
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
        <UIPopupHeader title='프로젝트 선택' description='IDE 환경에서 이용할 프로젝트를 선택해주세요.' position='right' />

        {/* 우측 바디 */}
        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={totalCount} prefix='총' unit='건' />
                  </div>
                </div>
                <div className='flex-shrink-0'>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={inputValue}
                      onChange={e => setInputValue(e.target.value)}
                      onKeyDown={e => {
                        if (e.key === 'Enter') setSearchValue(inputValue);
                      }}
                      placeholder='프로젝트명, 설명, 역할명 입력'
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='multi-select'
                  rowData={displayList}
                  columnDefs={columnDefs}
                  selectedDataList={selectedRowsInCurrentPage}
                  onCheck={handleCheck}
                  {...({ getRowId: (params: any) => params.data.id } as any)}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
          <UIArticle>
            <div className='box-fill'>
              <UIUnitGroup gap={6} direction='column' vAlign='start'>
                <div className='flex items-center gap-2'>
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                    선택한 프로젝트와 프로젝트 내에서의 역할을 기반으로 IDE 환경 로그인을 진행하며, SDK 사용이 가능합니다.
                  </UITypography>
                </div>
                <div className='flex items-center gap-2'>
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                    IDE 환경에서는 프로젝트명이 아닌 프로젝트 ID로만 표시됩니다. IDE 실행 전 대시보드에서 선택한 프로젝트의 ID를 확인한 후 이용해 주세요.
                  </UITypography>
                </div>
              </UIUnitGroup>
            </div>
          </UIArticle>
        </UIPopupBody>

        {/* 우측 푸터 */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={false /*selectedIds.length === 0*/}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
