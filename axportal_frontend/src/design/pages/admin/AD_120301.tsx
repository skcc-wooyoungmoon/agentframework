import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  publicRange: string;
}

export const AD_120301 = () => {
  const [, setActiveTab] = useState('tab2');
  const [value, setValue] = useState('12개씩 보기');
  const { openAlert } = useModal();
  const [, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    publicRange: '전체',
  });

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
    }),
    [openAlert]
  );

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      userName: '김신한',
      department: 'Data기획Unit',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      catogory: '지도학습',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      userName: '이영희',
      department: 'AI개발팀',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      catogory: '지도학습',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.23 14:15:32',
    },
    {
      id: '3',
      userName: '박철수',
      department: 'Data기획Unit',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      catogory: '지도학습',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.22 09:45:21',
    },
    {
      id: '4',
      userName: '최민수',
      department: 'AI개발팀',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      catogory: '지도학습',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.24 16:30:15',
    },
    {
      id: '5',
      userName: '정다은',
      department: 'Data분석팀',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      catogory: '지도학습',
      createdDate: '2025.03.20 11:20:43',
      modifiedDate: '2025.03.20 11:20:43',
    },
    {
      id: '6',
      userName: '홍길동',
      department: 'Data분석팀',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      catogory: '지도학습',
      createdDate: '2025.03.24 13:55:28',
      modifiedDate: '2025.03.24 13:55:28',
    },
    {
      id: '7',
      userName: '김미영',
      department: 'AI개발팀',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      catogory: '지도학습',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
  ];

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        field: 'userName' as any,
        width: 272,
      },
      {
        headerName: '유형',
        field: 'catogory',
        width: 120,
      },
      {
        headerName: '역할 설명',
        field: 'description',
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
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [rowData]
  );

  // 탭 아이템 정의
  const tabItems = [
    { id: 'tab1', label: '기본 정보' },
    { id: 'tab2', label: '역할 정보' },
    { id: 'tab3', label: '구성원 정보' },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'admin-users',
        label: '사용자 조회',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='프로젝트 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter pb-4'>
            {/* className='project-card bg-gray' bg-gray 배경이 회색일 경우 해당라인 클래스 추가  */}
            <div className='project-card bg-gray'>
              <ul className='flex flex-col gap-4'>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    프로젝트명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    슈퍼SOL 챗봇 개발
                  </UITypography>
                </li>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    설명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    슈퍼SOL에서 사용할 챗봇을 개발
                  </UITypography>
                </li>
              </ul>
            </div>
          </UIArticle>

          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabItems} activeId='tab2' size='large' onChange={setActiveTab} />
          </UIArticle>

          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 조건
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={'전체'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '1', label: '전체' },
                                  { value: '2', label: '아이템1' },
                                  { value: '3', label: '아이템2' },
                                  { value: '4', label: '아이템3' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValue}
                                onChange={e => {
                                  setSearchValue(e.target.value);
                                }}
                                placeholder='검색어 입력'
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            유형
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={'전체'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '1', label: '전체' },
                                  { value: '2', label: '아이템1' },
                                  { value: '3', label: '아이템2' },
                                  { value: '4', label: '아이템3' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
                            <div className='flex-1'></div> {/* < div 삭제하지마세요. 가로 사이즈 맞춤 빈여백 채우기 */}
                          </UIUnitGroup>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }}>
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                      </div>
                    </div>
                    <div className='flex items-center gap-2'>
                      <UIButton2 className='btn-tertiary-outline' onClick={() => {}}>
                        새 역할 만들기
                      </UIButton2>
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
                          disabled={true}
                        />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='multi-select'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {}}
                />
              </UIListContentBox.Body>
              {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                  삭제
                </UIButton2>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
