import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIInput, UIStepper } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UILabel, UITextLabel } from '@/components/UI/atoms';
import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';

export const AD_120401_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const [searchValue, setSearchValue] = useState('');
  const [value, setValue] = useState('이름');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 데이터 테이블 데이터
  const datasetData = [
    {
      id: '1',
      no: 1,
      name: '장정현',
      department: 'AI UNIT',
      employeeStatus: '재직',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      no: 2,
      name: '장정현',
      department: 'AI UNIT',
      employeeStatus: '재직',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '3',
      no: 3,
      name: '장정현',
      department: 'AI UNIT',
      employeeStatus: '재직',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '4',
      no: 4,
      name: '장정현',
      department: 'AI UNIT',
      employeeStatus: '재직',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '5',
      no: 5,
      name: '장정현',
      department: 'AI UNIT',
      employeeStatus: '재직',
      lastModifiedDate: '2025.03.24 18:23:43',
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
        headerName: '계정 상태',
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const isActive = params.data.id !== '2'; // id가 '2'인 항목만 비활성화
          return <UITextLabel intent={isActive ? 'blue' : 'gray'}>{isActive ? '활성화' : '비활성화'}</UITextLabel>;
        }),
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name' as const,
        flex: 1,
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      {
        headerName: '부서',
        field: 'department' as const,
        width: 366,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '인사 상태',
        field: 'employeeStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            재직: 'complete',
            퇴사: 'error',
            // '휴직': 'warning',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '마지막 접속 일시',
        field: 'lastModifiedDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '사용자 선택' },
    { step: 2, label: '역할 할당' },
  ];

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
            <UIPopupHeader title='구성원 초대하기' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    완료
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
          <UIPopupHeader title='사용자 선택' description='프로젝트에 초대할 구성원을 선택해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex items-center'>
                    <div style={{ width: '182px', paddingRight: '8px' }}>
                      <UIDataCnt count={datasetData.length} prefix='총' unit='건' />
                    </div>
                  </div>

                  <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        options={[
                          { value: '전체', label: '전체' },
                          { value: '이름', label: '이름' },
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
                    type='multi-select'
                    rowData={datasetData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {}}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} disabled={true}>
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
