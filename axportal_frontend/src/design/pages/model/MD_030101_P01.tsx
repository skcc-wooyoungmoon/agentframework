import React, { useState } from 'react';

import { UIPagination, UIDataCnt } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';

import { UIButton2, UILabel, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';

export const MD_030101_P01: React.FC = () => {
  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '모델 선택' },
    { step: 2, label: '배포 정보 입력' },
    { step: 3, label: '자원 할당' },
    { step: 4, label: '학습 데이터세트 선택' }, // [251104_퍼블수정] : 데이터세트 선택 > 학습 데이터세트 선택  (텍스트 수정)
    { step: 5, label: '파라미터 설정' },
    { step: 6, label: '입력정보 확인' },
  ];

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 그리드 선택 상태 (라디오는 단일 선택)
  const [selectedId] = useState<string>('');

  // 검색 상태
  // const [searchText, setSearchText] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 모델 테이블 데이터
  const rowData = [
    {
      id: '1',
      no: 1,
      modelName: '고객군 분류',
      isValid: '유효',
      description: '텍스트 문서 분류',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      modelType: 'language',
    },
    {
      id: '2',
      no: 2,
      modelName: '2024년 2분기 대출 승인 예측용',
      isValid: '유효',
      description: '금융대출 파인튜닝',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      modelType: 'language',
    },
    {
      id: '3',
      no: 3,
      modelName: '민원 분류 분석',
      isValid: '유효',
      description: '고객 패턴 데이터',
      tags: ['Tag'],
      modelType: 'vision',
    },
    {
      id: '4',
      no: 4,
      modelName: '금융서류 분류',
      isValid: '유효',
      description: '텍스트 문서분류',
      tags: ['Tag1', 'Tag2'],
      modelType: 'language',
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
        headerName: '모델명',
        field: 'modelName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '유효',
        field: 'isValid' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              // case '이용불가':
              // return 'error';
              case '유효':
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value}
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
            >
              {params.value}
            </div>
          );
        }),
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 S
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 E
      {
        headerName: '모델유형',
        field: 'modelType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    [selectedId]
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
            <UIPopupHeader title='파인튜닝 등록' position='left' />
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
                    튜닝시작
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
          <UIPopupHeader title='모델 선택' description='' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='grid-header-left'>
                    <UIGroup gap={12} direction='row' vAlign={'center'}>
                      <div style={{ width: '102px' }}>
                        <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='grid-header-right'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='검색어 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='single-select'
                    rowData={rowData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {
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
                {/* <UIButton2 className='btn-secondary-gray'>이전</UIButton2> */}
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
