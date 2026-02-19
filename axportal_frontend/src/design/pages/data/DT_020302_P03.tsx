import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIInput, UIStepper } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';

export const DT_020302_P03: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const [searchValue, setSearchValue] = useState('');
  const [value, setValue] = useState('전체');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 데이터 테이블 데이터
  const datasetData = [
    {
      id: '1',
      no: 1,
      name: '대출 상품 설명서 전문 파일 묶음',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담사 답변 데이터',
      depth: 'SOL-SAM',
    },
    {
      id: '2',
      no: 2,
      name: '대출 상품 설명서 전문 파일 묶음',
      description: '주택담보대출, 신용대출 등 대출 상품 상담 내용',
      depth: 'SOL-SAM',
    },
    {
      id: '3',
      no: 3,
      name: '대출 상품 설명서 전문 파일 묶음',
      description: '고객 프로필별 카드 상품 추천 이력 데이터',
      depth: 'SOL-SAM',
    },
    {
      id: '4',
      no: 4,
      name: '대출 상품 설명서 전문 파일 묶음',
      description: '보험 청구 접수부터 처리까지의 프로세스 데이터',
      depth: 'SOL-SAM',
    },
    {
      id: '5',
      no: 5,
      name: '대출 상품 설명서 전문 파일 묶음',
      description: '펀드, 주식 등 투자상품의 과거 성과 및 위험도 데이터',
      depth: 'SOL-SAM',
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
      // [251120_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '이름',
        field: 'name' as const,
        width: 272,
        sortable: false,
        cellStyle: { paddingLeft: '16px' },
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        sortable: false,
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
        headerName: '원천시스템',
        field: 'depth' as const,
        width: 120,
        sortable: false,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '지식 기본 설정' },
    { step: 2, label: '데이터 선택' },
    { step: 3, label: '선택 데이터 확인' },
    { step: 4, label: '청킹 설정' },
    { step: 5, label: '임베딩 설정' },
    { step: 6, label: '지식 등록' },
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
            <UIPopupHeader title='지식 데이터 추가' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
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
                    만들기
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
          {/* [251120_퍼블수정] 검수요청 현행화 수정 */}
          <UIPopupHeader title='데이터 선택' description='지식에 추가할 지식 데이터를 선택해 주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex items-center'>
                    <div style={{ width: '182px', paddingRight: '8px' }}>
                      {/* [251120_퍼블수정] 검수요청 현행화 수정 */}
                      <UIDataCnt count={datasetData.length} prefix='지식 데이터 총' unit='건' />
                    </div>
                    <div className='flex items-center gap-2'>
                      <UITypography variant='body-1' className='secondary-neutral-900'>
                        원천시스템
                      </UITypography>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(value)}
                          options={[
                            { value: '전체', label: '전체' },
                            { value: 'val1', label: 'S-basic' },
                            { value: 'val2', label: 'IT-Helpdesk' },
                            { value: 'val3', label: 'SOL_SAM' },
                            { value: 'val4', label: 'SWING' },
                            { value: 'val5', label: '법무윤리시스템' },
                            { value: 'val6', label: 'SPURT/통단' },
                            { value: 'val6', label: '고객상담센터/챗봇' },
                            { value: 'val7', label: 'KMS' },
                            { value: 'val8', label: '뉴스데이터' },
                          ]}
                          onSelect={(value: string) => {
                            setValue(value);
                          }}
                          onClick={() => {}}
                          height={40}
                          variant='dataGroup'
                        />
                      </div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                    <div style={{ width: '360px', flexShrink: 0 }}>
                      {/* 251128_퍼블수정 속성값 수정 */}
                      <UIInput.Search
                        value={searchValue}
                        placeholder='이름 입력'
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='multi-select' rowData={datasetData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
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
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                <UIButton2 className='btn-secondary-blue' disabled={true}>
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
