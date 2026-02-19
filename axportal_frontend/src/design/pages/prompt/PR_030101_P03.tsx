import React, { useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIInput, UIStepper } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UILabel } from '../../../components/UI/atoms/UILabel';
import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';
import { useModal } from '@/stores/common/modal';

export const PR_030101_P03: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const [searchValue, setSearchValue] = useState('');
  const { openAlert } = useModal();

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 데이터 테이블 데이터 // [251128_퍼블수정] : 그리드 컬럼 속성 수정
  const datasetData = [
    {
      id: '1',
      no: 1,
      deployName: '대출 상품 설명서 전문 파일 묶음',
      modelName: 'GPT-4 Turbo',
      status: '이용가능',
      applicable: 'Y',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담사 답변 데이터',
      modelType: 'LLM',
      isProductionDeploy: '배포',
      publicRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      no: 2,
      deployName: '주택담보대출 상담 모델',
      modelName: 'Claude-3.5 Sonnet',
      status: '이용가능',
      applicable: 'Y',
      description: '주택담보대출, 신용대출 등 대출 상품 상담 내용',
      modelType: 'LLM',
      isProductionDeploy: '배포',
      publicRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '3',
      no: 3,
      deployName: '카드 상품 추천 모델',
      modelName: 'Gemini Pro',
      status: '이용가능',
      applicable: 'Y',
      description: '고객 프로필별 카드 상품 추천 이력 데이터',
      modelType: 'ML',
      isProductionDeploy: '배포',
      publicRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '4',
      no: 4,
      deployName: '보험 청구 처리 모델',
      modelName: 'LLaMA 2',
      status: '이용가능',
      applicable: 'Y',
      description: '보험 청구 접수부터 처리까지의 프로세스 데이터',
      modelType: 'LLM',
      isProductionDeploy: '배포',
      publicRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '5',
      no: 5,
      deployName: '투자상품 분석 모델',
      modelName: 'GPT-3.5 Turbo',
      status: '이용가능',
      applicable: 'Y',
      description: '펀드, 주식 등 투자상품의 과거 성과 및 위험도 데이터',
      modelType: 'ML',
      isProductionDeploy: '배포',
      publicRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      lastModifiedDate: '2025.03.24 18:23:43',
    },
  ];

  // 더보기 메뉴 설정 // [251105_퍼블수정] : 그리드 컬럼 속성 수정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '실행',
          action: 'run',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 실행을 시작합니다.`,
            });
          },
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '복사',
          action: 'copy',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 복사가 완료되었습니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

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
        headerName: '배포명',
        field: 'deployName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '실패':
                return 'error';
              case '이용가능':
                return 'complete';
              case '배포중':
                return 'progress';
              default:
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
      // 251128_퍼블수정 그리드 컬럼 속성 '가드레일 적용 여부' 영역 추가 S
      {
        headerName: '가드레일 적용 상태',
        field: 'applicable' as const,
        width: 152,
        cellStyle: { paddingLeft: '16px' },
      },
      // 251128_퍼블수정 그리드 컬럼 속성 '가드레일 적용 여부' 영역 추가 E
      {
        headerName: '설명',
        field: 'description' as const,
        width: 392,
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
        headerName: '모델유형',
        field: 'modelType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '운영 배포 여부',
        field: 'isProductionDeploy' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '공개범위',
        field: 'publicRange' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'lastModifiedDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '기본 정보 입력' },
    { step: 2, label: '배포 모델 선택' },
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
            <UIPopupHeader title='가드레일 생성' position='left' />
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
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }}>
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
          <UIPopupHeader title='배포 모델 선택' description='가드레일을 적용할 모델을 선택 후 만들기 버튼을 클릭해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex items-center'>
                    <div style={{ width: '182px', paddingRight: '8px' }}>
                      <UIDataCnt count={datasetData.length} prefix='총' unit='건' />
                    </div>
                    {/* <div className='flex items-center gap-2'>
                        <UITypography variant='body-1' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                        <div style={{ width: '180px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(value)}
                            options={[
                              { value: '전체', label: '전체' },
                              { value: '프로젝트명', label: '프로젝트명' },
                            ]}
                            onSelect={(value: string) => {setValue(value);
                            }}
                            onClick={() => {}}
                            height={40}
                            variant='dataGroup'
                          />
                        </div>
                      </div> */}
                  </div>

                  <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                    <div style={{ width: '360px', flexShrink: 0 }}>
                      <UIInput.Search
                        value={searchValue}
                        placeholder='배포명, 모델명, 설명 입력'
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='multi-select' rowData={datasetData} columnDefs={columnDefs} moreMenuConfig={moreMenuConfig} onClickRow={(_params: any) => {}} />
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
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }}>
                  이전
                </UIButton2>
                {/* <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
                  다음
                </UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
