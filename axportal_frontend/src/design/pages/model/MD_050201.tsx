import React, { useMemo, useState } from 'react';

import { UIDataCnt } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';

import { UIBox, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { DesignLayout } from '@/design/components/DesignLayout';

export const MD_050201 = () => {
  // 뷰 상태 관리
  const [value, setValue] = useState('12');
  const [view] = useState('grid');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
  });

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

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 평가 데이터 샘플 (피그마 기반)
  const rowData = [
    {
      no: 1,
      id: '1',
      evalName: '요약 모델 평가',
      description: '요약 성능을 평가',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 2,
      id: '2',
      evalName: '대출 상품 RAG 평가',
      description: '대출상품에 대한 RAG 성능을 평가',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 3,
      id: '3',
      evalName: '추론 모델 성능 평가',
      description: '앱 이용법, 인증 절차 등을 담은 문서 기반 지식',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 4,
      id: '4',
      evalName: '추론 모델 성능 평가',
      description: 'ATM 한도, 수수료 안내 등 지식 문서 세트',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 5,
      id: '5',
      evalName: '추론 모델 성능 평가',
      description: '상담 로그 기반 Q&A 세트',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 6,
      id: '6',
      evalName: '추론 모델 성능 평가',
      description: '질문(차이점) → 명확한 구분 응답 포함',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 7,
      id: '7',
      evalName: '금융사기 예방 교육 세트',
      description: '보이스피싱 예방 안내 및 사례 중심 문서',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 8,
      id: '8',
      evalName: '신한카드 자주 묻는 질문 세트',
      description: '카드 관련 FAQ 문서, 응답 포함',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 9,
      id: '9',
      evalName: '금융사기 예방 교육 세트',
      description: '보이스피싱 예방 안내 및 사례 중심 문서',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      no: 10,
      id: '10',
      evalName: '대출 상품 RAG 평가',
      description: '대출상품에 대한 RAG 성능을 평가',
      version: 'embedding',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
  ];

  // 그리드 컬럼 정의 (피그마 기반)
  const columnDefs: any = useMemo(
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
        field: 'evalName' as const,
        width: 272,
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
        flex: 1,
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
        headerName: '모델 유형',
        field: 'version' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='모델 탐색' // [251111_퍼블수정] 타이틀명칭 변경 : 모델 가든 > 모델 탐색
          description={['Hugging Face에 등록된 모델을 검색하고 은행 내부로 가져올 수 있습니다.', '모델 검색과 반입 버튼을 통해 필요한 모델을 손쉽게 반입하세요.']}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIUnitGroup gap={16} direction='row' align='start'>
                <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                  모델 추가
                </UIButton2>
                <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-download', children: '' }}>
                  모델 반입
                </UIButton2>
              </UIUnitGroup>
            </>
          }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            {/* 탭 영역 */}
            <UITabs
              items={[
                { id: 'tab1', label: 'self-hosting' },
                { id: 'tab2', label: 'serverless' },
              ]}
              activeId='tab2'
              size='large'
              // onChange=''
            />
          </UIArticle>

          {/* 필터 영역 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        {/* [251120_퍼블수정] 플레이스홀더 수정 */}
                        <td>
                          <UIInput.Search
                            value={searchValue}
                            onChange={e => {
                              setSearchValue(e.target.value);
                            }}
                            placeholder='모델명, 설명 입력'
                          />
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            모델유형
                          </UITypography>
                        </th>
                        {/* [251120_퍼블수정] 속성값 수정 */}
                        <td>
                          <UIDropdown
                            value={'전체'}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '1', label: '전체' },
                              { value: '2', label: 'language' },
                              { value: '3', label: 'embedding' },
                              { value: '4', label: 'image' },
                              { value: '5', label: 'multimodal' },
                              { value: '6', label: 'reranker' },
                            ]}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={value => handleDropdownSelect('searchType', value)}
                          />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }}>
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          <UIArticle className='article-grid'>
            {/* 전체 데이터 목록 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIDataCnt count={rowData.length} prefix='총' unit='건' />
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
                      onSelect={(value: string) => {
                        setValue(value);
                      }}
                      onClick={() => {}}
                      height={40}
                      variant='dataGroup'
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                ) : (
                  <div className='p-4 text-center text-gray-500'>카드 뷰 준비 중...</div>
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
