import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';
import { UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

export const HM_040101_P06: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 검색 상태
  const [value, setValue] = useState('12');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 모델 테이블 데이터
  const modelData = [
    {
      id: '1',
      no: 1,
      projectName: '슈퍼SOL 챗봇 개발',
      projectDescription: '슈퍼SOL에서 사용할 챗봇을 개발',
      participantCount: '100',
      createdBy: '김신한 | Data기획Unit',
    },
    {
      id: '2',
      no: 2,
      projectName: '슈퍼SOL 챗봇 개발',
      projectDescription: '슈퍼SOL에서 사용할 챗봇을 개발',
      participantCount: '100',
      createdBy: '김신한 | Data기획Unit',
    },
    {
      id: '3',
      no: 3,
      projectName: '슈퍼SOL 챗봇 개발',
      projectDescription: '슈퍼SOL에서 사용할 챗봇을 개발',
      participantCount: '100',
      createdBy: '김신한 | Data기획Unit',
    },
    {
      id: '4',
      no: 4,
      projectName: '슈퍼SOL 챗봇 개발',
      projectDescription: '슈퍼SOL에서 사용할 챗봇을 개발',
      participantCount: '100',
      createdBy: '김신한 | Data기획Unit',
    },
    {
      id: '5',
      no: 5,
      projectName: '슈퍼SOL 챗봇 개발',
      projectDescription: '슈퍼SOL에서 사용할 챗봇을 개발',
      participantCount: '100',
      createdBy: '김신한 | Data기획Unit',
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
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '프로젝트 설명',
        field: 'projectDescription' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '참여 인원',
        field: 'participantCount' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성자',
        field: 'createdBy' as const,
        width: 272,
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
            <UIPopupHeader title='프로젝트 탈퇴' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>{/* 빈 공간 */}</UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }}>
                    탈퇴
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
          <UIPopupHeader title='프로젝트 탈퇴' description='탈퇴를 원하는 프로젝트를 선택해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIDataCnt count={modelData.length} prefix='총' unit='건' />
                  </div>
                  <div className='flex items-center gap-2'>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        options={[
                          { value: '1', label: '12개씩 보기' },
                          { value: '2', label: '36개씩 보기' },
                          { value: '3', label: '60개씩 보기' },
                        ]}
                        onSelect={(value: string) => {setValue(value);
                        }}
                        onClick={() => {}}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='single-select'
                    rowData={modelData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle>
              <div className='box-fill'>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UITypography variant='body-2' className='secondary-neutral-700 text-sb'>
                    프로젝트 관리자로 참여 중인 프로젝트는 탈퇴할 수 없습니다.
                  </UITypography>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          {/* <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }}>
                  이전
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter> */}
        </section>
      </UILayerPopup>
    </>
  );
};
