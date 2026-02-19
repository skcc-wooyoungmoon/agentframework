import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography, UIIcon2 } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIStepper, type UIStepperItem } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '@/design/components/DesignLayout';

const modelData = [
  {
    id: '1',
    no: 1,
    projectName: '비정형 데이터 자산화 플랫폼 구축',
    projectId: '123',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
    roleName: '프로젝트 관리자',
  },
  {
    id: '2',
    no: 2,
    projectName: '비정형 데이터 자산화 플랫폼 구축',
    projectId: '123',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
    roleName: '프로젝트 관리자',
  },
  {
    id: '3',
    no: 3,
    projectName: '비정형 데이터 자산화 플랫폼 구축',
    projectId: '123',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
    roleName: '프로젝트 관리자',
  },
  {
    id: '4',
    no: 4,
    projectName: '비정형 데이터 자산화 플랫폼 구축',
    projectId: '123',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
    roleName: '프로젝트 관리자',
  },
  {
    id: '5',
    no: 5,
    projectName: '비정형 데이터 자산화 플랫폼 구축',
    projectId: '123',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
    roleName: '프로젝트 관리자',
  },
  {
    id: '6',
    no: 6,
    projectName: '비정형 데이터 자산화 플랫폼 구축',
    projectId: '123',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
    roleName: '프로젝트 관리자',
  },
];

export const HM_060101_P03: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 그리드 선택 상태 (라디오는 단일 선택)
  const [selectedId, _] = useState<string>('');

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '프로젝트 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '도구 및 이미지 선택',
      step: 2,
    },
    {
      id: 'step3',
      label: 'DW 계정 선택',
      step: 3,
    },
    {
      id: 'step4',
      label: '자원 선택',
      step: 4,
    },
  ];

  // 검색 상태

  const handleClose = () => {
    setIsPopupOpen(false);
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
        width: 272,
        showTooltip: true,
      },
    ],
    [selectedId]
  );

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'data', label: '데이터' }}
        initialSubMenu={{
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-data-storage',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              IDE 이동
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              Ingestion Tool 만들기 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='IDE 생성' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
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
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='프로젝트 선택' description='IDE 환경에서 이용할 프로젝트를 선택해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={modelData.length} prefix='총' unit='건' />
                    </div>
                  </div>
                  <div className='flex-shrink-0'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='프로젝트명, 설명, 역할명 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='multi-select' rowData={modelData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
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
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={() => {}}>
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
