import { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  userRole: string;
  userStatus: string;
  hrStatus: string;
}

export const DP_050101 = () => {
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    condition: false,
    menu: false,
    menu2: false,
    pageSize: false,
  });

  // 각 드롭다운 값 상태
  const [searchTypeValue, setSearchTypeValue] = useState('배포 요청일시');
  const [menuValue2, setMenuValue2] = useState('전체');

  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '', endDate: '' },
    searchType: '',
    searchKeyword: '',
    userRole: '전체',
    userStatus: '전체',
    hrStatus: '전체',
  });

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      type: '시스템',
      target: '서비스',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      type: '서비스',
      target: '서비스',
      modifiedDate: '2025.03.24 15:20:15',
    },
    {
      id: '3',
      type: '보안',
      target: '보안',
      modifiedDate: '2025.03.23 14:45:22',
    },
    {
      id: '4',
      type: '시스템',
      target: '시스템',
      modifiedDate: '2025.03.22 09:30:00',
    },
    {
      id: '5',
      type: '교육',
      target: '교육',
      modifiedDate: '2025.03.21 16:15:30',
    },
  ];

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');
  const [value, setValue] = useState('12개씩 보기');

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
        headerName: '배포 분류',
        field: 'type',
        minWidth: 622,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '배포 대상',
        field: 'target',
        width: 622,
      },
      {
        headerName: '배포 요청일시',
        field: 'modifiedDate',
        minWidth: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData]
  );

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
        <UIPageHeader
          title='운영 배포'
          description={[
            '포탈 개발 환경 내 에셋들을 운영으로 이행할 수 있으며, 이행한 이력을 확인할 수 있습니다.',
            '최종 운영 배포를 위해서 형상관리를 통해 이행이 필요하며 포탈 담당자에게 반드시 문의해주세요.',
          ]}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                운영 배포
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
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
                            조회 기간
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={searchTypeValue}
                                placeholder='조회 기간 선택'
                                options={[
                                  { value: 'val1', label: '배포 요청일시1' },
                                  { value: 'val2', label: '배포 요청일시2' },
                                  { value: 'val3', label: '배포 요청일시3' },
                                  { value: 'val4', label: '배포 요청일시4' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={(value: string) => {
                                  setSearchTypeValue(value);
                                  setDropdownStates(prev => ({ ...prev, searchType: false }));
                                }}
                              />
                            </div>
                            <div className='flex-1' style={{ zIndex: '10' }}>
                              <UIUnitGroup gap={8} direction='row' vAlign='center'>
                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueStart}
                                    onChange={e => {
                                      setDateValueStart(e.target.value);
                                    }}
                                  />
                                </div>

                                <UITypography variant='body-1' className='secondary-neutral-p w-[11px]'>
                                  ~
                                </UITypography>

                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueEnd}
                                    onChange={e => {
                                      setDateValueEnd(e.target.value);
                                    }}
                                  />
                                </div>
                              </UIUnitGroup>
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row' className='items-center'>
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValues.searchKeyword}
                                placeholder='배포 대상 입력'
                                onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                              />
                            </div>
                            <div className='flex flex-1 items-center'>
                              <UITypography variant='body-1' className='!w-[80px] secondary-neutral-800 text-body-1-sb'>
                                배포 분류
                              </UITypography>
                              <UIDropdown
                                value={menuValue2}
                                placeholder='배포 분류 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '세이프티 필터', label: '세이프티 필터' },
                                  { value: '모델', label: '모델' },
                                  { value: '지식', label: '지식' },
                                  { value: '에이전트', label: '에이전트' },
                                ]}
                                isOpen={dropdownStates.menu2}
                                onClick={() => handleDropdownToggle('menu2')}
                                onSelect={(value: string) => {
                                  setMenuValue2(value);
                                  setDropdownStates(prev => ({ ...prev, menu2: false }));
                                }}
                              />
                            </div>
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
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        options={[
                          { value: '1', label: '12개씩 보기' },
                          { value: '2', label: '36개씩 보기' },
                          { value: '3', label: '60개씩 보기' },
                        ]}
                        isOpen={dropdownStates.pageSize}
                        onClick={() => handleDropdownToggle('pageSize')}
                        onSelect={(value: string) => {
                          setValue(value);
                          setDropdownStates(prev => ({ ...prev, pageSize: false }));
                        }}
                        height={40}
                        variant='dataGroup'
                        width='w-40'
                      />
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} />
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
