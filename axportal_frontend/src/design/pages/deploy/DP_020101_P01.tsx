import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '@/design/components/DesignLayout';

// 모델 테이블 데이터
const modelData = [
  {
    id: '1',
    no: 1,
    modelName: '대출 상담 자동화 에이전트',
    description: '재질문 분류 문서 기반 응답 자동 생성',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '2',
    no: 2,
    modelName: '금융 약관 번역',
    description: '금융 문서의 한→영 자연어 번역 수행 (Categorizer 사용)',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '3',
    no: 3,
    modelName: 'Q&A 응답',
    description: '금융 상품 및 제도에 대한 질문 응답 ',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '4',
    no: 4,
    modelName: '대출 금리 산정 방식 매뉴얼 응답',
    description: '',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '5',
    no: 5,
    modelName: '금융 문서 자동 분류',
    description: '',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '6',
    no: 6,
    modelName: 'FAQ 정답 재정렬',
    description: '약관, 계약서 등 내부 문서를 항목별로 자동 분류.',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '7',
    no: 7,
    modelName: '약관 요약 에이전트',
    description: '',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '8',
    no: 8,
    modelName: '금융 문서 자동 분류기',
    description: 'DOC Filter 사용, 핵심 항목만 추출',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '9',
    no: 9,
    modelName: '고객센터 챗봇',
    description: '질문 분류 후 시나리오형 응답 흐름 에이전트',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '10',
    no: 10,
    modelName: '신용평가 방식 안내',
    description: 'retriever, Categorizer 사용 테스트',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '11',
    no: 11,
    modelName: '업무 보고서 요약',
    description: 'Doc Compressor 사용 테스트',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    id: '12',
    no: 12,
    modelName: '예적금 상품 비교 안내 에이전트',
    description: '사용자의 조건(기간, 금액 등)에 따라 은행 예·적금 상품을 비교 설명',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
];

export const DP_020101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '에이전트 선택' },
    { step: 2, label: '배포 정보  입력' },
    { step: 3, label: '자원 할당' },
  ];

  // 그리드 선택 상태 (라디오는 단일 선택)
  const [selectedId, _] = useState<string>('');

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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'modelName' as const,
        width: 240,
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
        headerName: '설명',
        field: 'description' as const,
        minWidth: 392,
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
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
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
              데이터 도구
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
            <UIPopupHeader title='에이전트 배포하기' position='left' />
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
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }}>
                    배포
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
          <UIPopupHeader title='에이전트 선택' description='배포할 에이전트를 선택해 주세요.' position='right' />

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
                        placeholder='검색어 입력'
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
