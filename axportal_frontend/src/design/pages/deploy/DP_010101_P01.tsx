import React, { useState } from 'react';

import { UIPagination, UIDataCnt } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIDropdown, UIFormField, UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';

export const DP_010101_P01: React.FC = () => {
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
  });

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '모델 선택' },
    { step: 2, label: '배포 정보 입력' },
    { step: 3, label: '자원 할당' },
  ];

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: string, _value: string) => {
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

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
      description: '텍스트 문서 분류',
      modelType: 'language',
      deployType: 'Serverless',
    },
    {
      id: '2',
      no: 2,
      modelName: '2024년 2분기 대출 승인 예측용',
      description: '금융대출 파인튜닝',
      modelType: 'language',
      deployType: 'Self Hosting',
    },
    {
      id: '3',
      no: 3,
      modelName: '민원 분류 분석',
      description: '고객 패턴 데이터',
      modelType: 'language',
      deployType: 'Serverless',
    },
    {
      id: '4',
      no: 4,
      modelName: '금융서류 분류',
      description: '텍스트 문서분류',
      modelType: 'Speech Recognition',
      deployType: 'Self Hosting',
    },
    {
      id: '5',
      no: 5,
      modelName: '대출 리스크 평가',
      description: '테스트 파인튜닝',
      modelType: 'embedding',
      deployType: 'Serverless',
    },
    {
      id: '6',
      no: 6,
      modelName: '고객 이탈 예측',
      description: '고객 이탈문서 데이터',
      modelType: 'language',
      deployType: 'Serverless',
    },
    {
      id: '7',
      no: 7,
      modelName: '금융 상담 질의응답',
      description: '텍스트 문서분류',
      modelType: 'language',
      deployType: 'Self Hosting',
    },
    {
      id: '8',
      no: 8,
      modelName: '상담 기록 요약',
      description: '음성인식 챗봇',
      modelType: 'language',
      deployType: 'Serverless',
    },
    {
      id: '9',
      no: 9,
      modelName: '준법 문서 검토',
      description: '사텍스트 문서분류',
      modelType: 'language',
      deployType: 'Self Hosting',
    },
    {
      id: '10',
      no: 10,
      modelName: '금융 상품 추천',
      description: '텍스트 문서분류',
      modelType: 'embedding',
      deployType: 'Serverless',
    },
    {
      id: '11',
      no: 11,
      modelName: '금융대출예측',
      description: '금융대출 파인튜닝',
      modelType: 'embedding',
      deployType: 'Serverless',
    },
    {
      id: '12',
      no: 12,
      modelName: '금융 상품 추천',
      description: '텍스트 문서분류',
      modelType: 'embedding',
      deployType: 'Serverless',
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
        width: 240,
        cellStyle: { paddingLeft: '16px' },
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
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
        headerName: '모델유형',
        field: 'modelType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포유형',
        field: 'deployType' as const,
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
            <UIPopupHeader title='모델 배포하기' position='left' />
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
          <UIPopupHeader title='모델 선택' description='배포할 모델을 선택해주세요.' position='right' />
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
                      <div>
                        <UIFormField gap={8} direction='row' vAlign={'center'}>
                          <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                            배포유형
                          </UITypography>
                          <div>
                            <div className='w-[180px]'>
                              <UIDropdown
                                value={'전체'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '아이디', label: '아이디' },
                                  { value: '이메일', label: '이메일' },
                                  { value: '부서', label: '부서' },
                                ]}
                                height={40}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
                          </div>
                        </UIFormField>
                      </div>
                      <div>
                        <UIFormField gap={8} direction='row' vAlign={'center'}>
                          <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                            모델유형
                          </UITypography>
                          <div>
                            <div className=' w-[180px]'>
                              <UIDropdown
                                value={'전체'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '아이디', label: '아이디' },
                                  { value: '이메일', label: '이메일' },
                                  { value: '부서', label: '부서' },
                                ]}
                                height={40}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
                          </div>
                        </UIFormField>
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
