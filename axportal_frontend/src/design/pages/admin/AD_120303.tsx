import { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography, UIIcon2 } from '@/components/UI/atoms';
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

export const AD_120303 = () => {
  const [, setActiveTab] = useState('dataset');
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

  // date 타입
  // const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  // const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 드롭
  const [value, setValue] = useState('12개씩 보기');

  // 스켈레톤 로딩 상태
  const [loading, setLoading] = useState(false);

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
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
      agentName: '기준금리 조회',
      authorityName: '원천 데이터 조회',
      authorityDetail: '원천 데이터 목록 조회,원천 데이터 상세 조회',
    },
    {
      id: '2',
      agentName: '환율 정보 조회',
      authorityName: '원천 데이터 조회',
      authorityDetail: '원천 데이터 목록 조회,원천 데이터 상세 조회',
    },
    {
      id: '3',
      agentName: '금융용어 쉬운 설명',
      authorityName: '원천 데이터 조회',
      authorityDetail: '원천 데이터 목록 조회,원천 데이터 상세 조회',
    },
    {
      id: '4',
      agentName: '예/적금 상품 추천',
      authorityName: '원천 데이터 조회',
      authorityDetail: '원천 데이터 목록 조회,원천 데이터 상세 조회',
    },
    {
      id: '5',
      agentName: '금융 정책 대상 여부 판단',
      authorityName: '원천 데이터 조회',
      authorityDetail: '원천 데이터 목록 조회,원천 데이터 상세 조회',
    },
    {
      id: '6',
      agentName: '대출 이자 계산기',
      authorityName: '원천 데이터 조회',
      authorityDetail: '원천 데이터 목록 조회,원천 데이터 상세 조회',
    },
    {
      id: '7',
      agentName: 'ATM/지점 위치 찾기',
      authorityName: '원천 데이터 조회',
      authorityDetail: '원천 데이터 목록 조회,원천 데이터 상세 조회',
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
        headerName: '메뉴명',
        field: 'agentName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '권한명',
        field: 'authorityName' as any,
        width: 272,
      },
      {
        headerName: '상세 권한',
        field: 'authorityDetail' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData]
  );

  // 탭 아이템 정의
  const tabItems = [
    { id: 'dataset', label: '기본 정보' },
    { id: 'rightsInformation', label: '권한 정보' },
    { id: 'memberInformation', label: '구성원 정보' },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'admin-user',
        label: '사용자 관리',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='역할 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter pb-4'>
            {/* className='project-card bg-gray' bg-gray 배경이 회색일 경우 해당라인 클래스 추가  */}
            <div className='project-card'>
              <UIUnitGroup gap={8} direction='row' vAlign='center' className='mb-6'>
                <UIIcon2 className='ic-system-24-project' aria-hidden='true'></UIIcon2>
                <UITypography variant='title-4' className='secondary-neutral-700'>
                  대출 상품 추천
                </UITypography>
              </UIUnitGroup>
              <ul className='flex flex-col gap-4'>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    역활명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    사용자 피드백 관리자
                  </UITypography>
                </li>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    설명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    추천된 대출 상품에 대한 고객 피드백을 수집·분석하고, 개선 사항을 전달
                  </UITypography>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabItems} activeId='rightsInformation' size='large' onChange={setActiveTab} />
          </UIArticle>

          {/* [251105_퍼블수정] 검색영역 수정 */}
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
                            메뉴명
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
                  <UIButton2
                    className='btn-secondary-blue'
                    style={{ width: '100%' }}
                    onClick={() => {
                      setLoading(true);
                      // 실제로는 API 호출 후 setLoading(false) 처리
                      setTimeout(() => setLoading(false), 2000);
                    }}
                  >
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex' style={{ gap: '12px' }}>
                        <div className=''>
                          <UIButton2 className='btn-tertiary-outline'>권한 설정하기</UIButton2>
                        </div>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(value)}
                            options={[
                              { value: '12', label: '12개씩 보기' },
                              { value: '20', label: '20개씩 보기' },
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
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='default'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  loading={loading}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {}}
                />
              </UIListContentBox.Body>
              {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
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
