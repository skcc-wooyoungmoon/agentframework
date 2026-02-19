import React, { useState, useMemo } from 'react';

import { UIDataCnt } from '@/components/UI';
import { UIButton2, UITypography, UIPagination } from '@/components/UI/atoms';
import { UIInput, UIDropdown, UIArticle, UIPopupFooter, UIPopupHeader, UIPopupBody, UIUnitGroup, UIFormField, UIGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const AD_010502_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  const [modelName, setModelName] = useState('대출 상품 추천');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [value, setValue] = useState('역할명');

  // 샘플 데이터
  const sampleData = [
    {
      id: '1',
      dataName: '사용자 피드백 관리자',
      type: '추천된 대출 상품에 대한 고객 피드백을 수집·분석하고, 개선 사항을 전달',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      dataName: '데이터 품질 관리자',
      type: '데이터의 정확성과 일관성을 검증하고, 품질 기준에 맞는 데이터 관리',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.23 15:42:17',
    },
    {
      id: '3',
      dataName: '시스템 모니터링 관리자',
      type: '시스템 성능 및 안정성 모니터링, 장애 대응 및 예방 관리',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.22 09:15:28',
    },
    {
      id: '4',
      dataName: '콘텐츠 검수 관리자',
      type: '게시되는 콘텐츠의 적절성 검토 및 가이드라인 준수 확인',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.21 14:33:55',
    },
    {
      id: '5',
      dataName: '보안 정책 관리자',
      type: '보안 정책 수립 및 실행, 접근 권한 관리 및 보안 감사',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.20 11:27:42',
    },
    {
      id: '6',
      dataName: '교육 프로그램 관리자',
      type: '사용자 교육 과정 기획 및 운영, 교육 자료 제작 및 배포',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.19 16:51:33',
    },
    {
      id: '7',
      dataName: '프로젝트 관리자',
      type: '프로젝트 계획 수립 및 진행 관리, 팀 협업 및 일정 조율',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.18 10:08:19',
    },
    {
      id: '8',
      dataName: '고객 지원 관리자',
      type: '고객 문의 처리 및 지원, 서비스 개선을 위한 고객 의견 수집',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.17 13:44:26',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as keyof (typeof sampleData)[0],
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '역할명',
        field: 'dataName' as keyof (typeof sampleData)[0],
        width: 262,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'type' as keyof (typeof sampleData)[0],
        flex: 1,
        minWidth: 420,
        showTooltip: true,
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
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as keyof (typeof sampleData)[0],
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'user-management',
          label: '사용자 관리',
          icon: 'ico-lnb-menu-20-user-management',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              사용자 역할 수정
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              사용자 역할 수정 진행 중...
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
            <UIPopupHeader title='사용자 역할 수정' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 좌측 스테퍼나 네비게이션 콘텐츠 영역 */}</UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='사용자 역할 수정' description='사용자에게 할당하고 싶은 역할을 선택 후 저장 버튼을 클릭 해주세요.' position='right' />
          <UIPopupBody>
            {/* 프로젝트명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  프로젝트명
                </UITypography>
                <UIInput.Text value={modelName} onChange={e => setModelName(e.target.value)} disabled={true} />
              </UIFormField>
            </UIArticle>

            {/* 그리드 영역 */}
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '102px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={sampleData.length} prefix='총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                      <div>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div>
                            <UIDropdown
                              value={String(value)}
                              disabled={false}
                              options={[
                                { value: '1', label: '역할명' },
                                { value: '2', label: '설명' },
                              ]}
                              height={40}
                              width='w-[160px]'
                              variant='dataGroup'
                              onSelect={(value: string) => {setValue(value);
                              }}
                              onClick={() => {}}
                            />
                          </div>
                          <div style={{ width: '360px' }}>
                            <UIInput.Search value={searchKeyword} onChange={e => setSearchKeyword(e.target.value)} placeholder='검색어 입력' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='single-select'
                    rowData={sampleData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
