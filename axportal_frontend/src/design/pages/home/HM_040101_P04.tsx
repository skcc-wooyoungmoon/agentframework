import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';

export const HM_040101_P04: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const [searchValue, setSearchValue] = useState('');
  const [value, setValue] = useState('전체');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 프로젝트 테이블 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      projectName: '슈퍼SOL 챗봇 개발',
      description: '슈퍼SOL에서 사용할 챗봇을 개발하는 프로젝트입니다.',
      participantCount: '5',
      manager: '김신한 | Data기획Unit',
    },
    {
      id: '2',
      no: 2,
      projectName: '이상징후 탐지 시스템',
      description: '금융거래에서 이상징후를 탐지하는 AI 시스템 개발',
      participantCount: '3',
      manager: '홍길동 | Data기획Unit',
    },
    {
      id: '3',
      no: 3,
      projectName: '문서 자동요약 서비스',
      description: '계약서 및 금융문서 자동요약 AI 서비스 구축',
      participantCount: '4',
      manager: '홍길동 | Data기획Unit',
    },
    {
      id: '4',
      no: 4,
      projectName: '고객상담 챗봇',
      description: '은행상품 관련 고객상담을 위한 챗봇 시스템',
      participantCount: '6',
      manager: '홍길동 | Data기획Unit',
    },
    {
      id: '5',
      no: 5,
      projectName: 'AI 투자자문 서비스',
      description: 'AI 기반 투자자문 및 포트폴리오 관리 시스템',
      participantCount: '7',
      manager: '홍길동 | Data기획Unit',
    },
    {
      id: '6',
      no: 6,
      projectName: 'AI 투자자문 서비스',
      description: 'AI 기반 투자자문 및 포트폴리오 관리 시스템',
      participantCount: '7',
      manager: '홍길동 | Data기획Unit',
    },
  ];

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
        width: 280,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 530,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '참여 인원', // [251104_퍼블수정] : 참여인원 > 참여 (띄어쓰기) 인원
        field: 'participantCount' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성자', // [251104_퍼블수정] : 담당자 > 생성자
        field: 'manager' as const,
        width: 200,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델 카탈로그',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델 카탈로그
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              파인튜닝 등록 진행 중...
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
            <UIPopupHeader title='프로젝트 참여' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '프로젝트 선택' },
                  { id: 'step2', step: 2, label: '프로젝트 정보 확인' },
                ]}
                currentStep={1}
                direction='vertical'
              />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    참여
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
          <UIPopupHeader title='프로젝트 선택' description='원하는 프로젝트를 선택해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                    </div>
                  </div>

                  <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        options={[
                          { value: '전체', label: '전체' },
                          { value: '프로젝트명', label: '프로젝트명' },
                          { value: '설명', label: '설명' },
                          { value: '참여인원', label: '참여인원' },
                          { value: '담당자', label: '담당자' },
                        ]}
                        onSelect={(value: string) => {setValue(value);
                        }}
                        onClick={() => {}}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>
                    <div style={{ width: '360px', flexShrink: 0 }}>
                      <UIInput.Search
                        value={searchValue}
                        placeholder='검색어 입력'
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='single-select'
                    rowData={projectData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
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
