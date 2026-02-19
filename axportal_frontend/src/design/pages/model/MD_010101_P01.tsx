import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';

export const MD_010101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 모델 테이블 데이터
  const modelData = [
    {
      id: '1',
      no: 1,
      modelName: '문서요약기_계약서형',
      description: '이 데이터 파일은 신용점수와 연소득에 대한 정보를 포함하고 있습니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '2',
      no: 2,
      modelName: '이상징후탐지기_거래형',
      description: '이 파일은 신용점수와 연소득에 대한 다양한 사례를 제공합니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '3',
      no: 3,
      modelName: '상담챗봇_은행상품형',
      description: '이 데이터는 신용점수와 연소득의 관계를 보여주는 예시를 포함하고 있습니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '4',
      no: 4,
      modelName: '이상징후탐지기_거래형',
      description: '이 파일은 신용점수와 연소득의 다양한 조합을 보여줍니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '5',
      no: 5,
      modelName: '문서요약기_계약서형',
      description: '이 데이터는 신용점수와 연소득에 대한 정보를 제공합니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '6',
      no: 6,
      modelName: '이상징후탐지기_거래형',
      description: '이 파일은 신용점수와 연소득의 예시를 포함하고 있습니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '7',
      no: 7,
      modelName: '문서요약기_계약서형',
      description: '이 데이터는 신용점수와 연소득에 대한 정보를 포함하고 있습니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '8',
      no: 8,
      modelName: '이상징후탐지기_거래형',
      description: '이 파일은 신용점수와 연소득의 다양한 예시를 제공합니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '9',
      no: 9,
      modelName: '문서요약기_계약서형',
      description: '이 데이터는 신용점수와 연소득의 관계를 보여주는 예시를 포함하고 있습니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '10',
      no: 10,
      modelName: '이상징후탐지기_거래형',
      description: '이 파일은 신용점수와 연소득에 대한 정보를 제공합니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '11',
      no: 11,
      modelName: '문서요약기_계약서형',
      description: '이 데이터는 신용점수와 연소득의 관계를 보여주는 예시를 포함하고 있습니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
    },
    {
      id: '12',
      no: 12,
      modelName: '이상징후탐지기_거래형',
      description: '이 파일은 신용점수와 연소득에 대한 정보를 제공합니다.',
      modeltype: 'language',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '모델명',
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
        headerName: '모델 유형',
        field: 'modeltype' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
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
            <UIPopupHeader title='모델 등록' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '모델 선택' },
                  { id: 'step2', step: 2, label: '모델 정보 확인' },
                  { id: 'step3', step: 3, label: '추가 정보 입력' },
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
                    등록
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
          <UIPopupHeader
            title='모델 선택'
            description='모델 탐색에 반입이 완료된 serverless 모델 리스트에서 선택한 프로젝트의 모델 관리 메뉴에 등록할 모델을 선택해주세요.'
            position='right'
          />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={modelData.length} prefix='반입완료 모델 총' unit='건' />
                    </div>
                  </div>
                  <div className='flex-shrink-0'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='모델명, 설명 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={modelData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
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
