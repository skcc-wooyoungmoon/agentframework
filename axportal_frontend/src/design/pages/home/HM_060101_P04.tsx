import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
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
    projectName: 'Jupyter Notebook',
    imageName: 'Python_v3.12_RAG',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
  },
  {
    id: '2',
    no: 2,
    projectName: 'Jupyter Notebook',
    imageName: 'Python_v3.12_RAG',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
  },
  {
    id: '3',
    no: 3,
    projectName: 'Jupyter Notebook',
    imageName: 'Python_v3.12_RAG',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
  },
  {
    id: '4',
    no: 4,
    projectName: 'Jupyter Notebook',
    imageName: 'Python_v3.12_RAG',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
  },
  {
    id: '5',
    no: 5,
    projectName: 'Jupyter Notebook',
    imageName: 'Python_v3.12_RAG',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
  },
  {
    id: '6',
    no: 6,
    projectName: 'Jupyter Notebook',
    imageName: 'Python_v3.12_RAG',
    description: '비정형 데이터 자산화 플랫폼 구축 프로젝트입니다. 프로젝트 참가 전 공개에셋 참고 부탁드립니다.',
  },
];

export const HM_060101_P04: React.FC = () => {
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
        headerName: '도구명',
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
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
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
          <UIPopupHeader title='도구 및 이미지 선택' description='사용할 도구 및 이미지를 선택해주세요.' position='right' />

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
                        placeholder='도구명, 이미지명, 설명 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={modelData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={() => {}}>
                  이전
                </UIButton2>
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
