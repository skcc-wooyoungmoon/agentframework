import { useMemo, useState } from 'react';
import type { ApexOptions } from 'apexcharts';

import { DesignLayout } from '../../components/DesignLayout';
import { UIGroup, UIDropdown, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UIButton2, UIBox, UIDataCnt, UILabel } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILineChart } from '@/components/UI/molecules/chart';

interface SearchValues {
  searchType: string;
}

export const AD_040402 = () => {
  const [value, setValue] = useState('12');
  const [searchValues, setSearchValues] = useState<SearchValues>({
    searchType: '전체',
  });

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      podsloadName: 'Backend-5b8c7678f8-rnk7n',
      allocation: '2',
      requestAmount: '500',
      actualUsage: '0.0000000',
      requestUsageRate: '0.0000000',
      allocationUsageRate: '0.0000000',
    },
    {
      id: '2',
      podsloadName: 'Backend-5b8c7678f8-rnk7n',
      allocation: '2',
      requestAmount: '500',
      actualUsage: '0.0000000',
      requestUsageRate: '0.00000007%',
      allocationUsageRate: '0.0000000',
    },
  ];

  // 컬럼 정의
  // 251128_퍼블수정 그리드 컬럼 속성 수정
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
        headerName: 'Pods명',
        field: 'podsloadName',
        minWidth: 282,
        flex: 1,
      },
      {
        headerName: '할당량',
        field: 'allocation',
        width: 252,
      },
      {
        headerName: '요청량',
        field: 'requestAmount',
        width: 282,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '실제 사용량',
        field: 'actualUsage',
        width: 252,
      },
      {
        headerName: '요청량 대비 사용률',
        field: 'requestUsageRate',
        width: 252,
      },
      {
        headerName: '할당량 대비 사용률',
        field: 'allocationUsageRate',
        width: 252,
      },
    ],
    [rowData]
  );

  // smooth 차트
  const [lineChartState1] = useState({
    series: [
      // 개발자 영역 : y축
      {
        name: 'name1',
        data: [31.0, 40.0, 28.0, 51.0, 42.0, 0.0, 0.0],
      },
      {
        name: 'series2',
        data: [11.0, 32.0, 45.4, 32.0, 34.0, 52.0, 41.0],
      },
    ],
    options: {
      xaxis: {
        // 개발자 영역 : X축
        type: 'category',
        categories: ['12:58:30', '12:59:00', '12:59:30', '13:00:00', '13:00:30', '13:01:00', '13:01:30', '13:02:00', '13:02:30', '13:03:00'],
      },
      stroke: {
        width: 2,
        curve: 'smooth',
      },
      legend: {
        show: false,
      },
      // 퍼블리셔 영역
      colors: ['#FC4661', '#FFAB08'],
    } as ApexOptions,
  });

  const [lineChartState2] = useState({
    series: [
      // 개발자 영역 : y축
      {
        name: 'name1',
        data: [31.0, 40.0, 28.0, 51.0, 42.0, 0.0, 0.0],
      },
      {
        name: 'series2',
        data: [11.0, 32.0, 45.4, 32.0, 34.0, 52.0, 41.0],
      },
    ],
    options: {
      xaxis: {
        // 개발자 영역 : X축
        type: 'category',
        categories: ['12:58:30', '12:59:00', '12:59:30', '13:00:00', '13:00:30', '13:01:00', '13:01:30', '13:02:00', '13:02:30', '13:03:00'],
      },
      stroke: {
        width: 2,
        curve: 'smooth',
      },
      legend: {
        show: false,
      },
      // 퍼블리셔 영역
      colors: ['#71FFD0', '#4A86FF'],
    } as ApexOptions,
  });

  // search 타입
  const [searchValue1, setSearchValue1] = useState('');

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key as keyof typeof dropdownStates]: false }));
  };

  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-tools',
        label: '에이전트의 도구',
        icon: 'ico-lnb-menu-20-agent-tools',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='솔루션 자원 현황 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                솔루션 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251106_퍼블수정] width값 수정 */}
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          솔루션명
                        </UITypography>
                      </th>
                      {/* [251107_퍼블수정] : UITypography컴포넌트로 감싸기 */}
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          API G/W
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          네임스페이스명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          ns-apigw-dev
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Pods 개수
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2개
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='complete'>
                            정상
                          </UILabel>
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
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
                            검색
                          </UITypography>
                        </th>
                        <td>
                          <UIInput.Search
                            value={searchValue1}
                            placeholder='워크로드명 입력'
                            onChange={e => {
                              setSearchValue1(e.target.value);
                            }}
                          />
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            Pods명
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchValues.searchType}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: 'val1', label: 'Pods명1' },
                                { value: 'val2', label: 'Pods명2' },
                                { value: 'val3', label: 'Pods명3' },
                              ]}
                              isOpen={dropdownStates.searchType}
                              onClick={() => handleDropdownToggle('searchType')}
                              onSelect={value => handleDropdownSelect('searchType', value)}
                            />
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 기간
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchValues.searchType}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: 'val1', label: '전체' },
                                { value: 'val2', label: '정상' },
                                { value: 'val3', label: '과부하' },
                              ]}
                              isOpen={dropdownStates.searchType}
                              onClick={() => handleDropdownToggle('searchType')}
                              onSelect={value => handleDropdownSelect('searchType', value)}
                            />
                          </div>
                        </td>
                        <td colSpan={3} className='pl-[32px]'>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1' style={{ zIndex: '10' }}>
                              <UIUnitGroup gap={8} direction='row' vAlign='center'>
                                <UIGroup gap={10} direction='row' vAlign='center'>
                                  <div className='flex-1'>
                                    <UIInput.Date
                                      value={dateValueStart}
                                      onChange={e => {
                                        setDateValueStart(e.target.value);
                                      }}
                                      disabled={true}
                                    />
                                  </div>

                                  <div className='w-[100px]'>
                                    <UIDropdown
                                      value={searchValues.searchType}
                                      placeholder='조회 조건 선택'
                                      options={[
                                        { value: 'val1', label: '전체' },
                                        { value: 'val2', label: '정상' },
                                        { value: 'val3', label: '과부하' },
                                      ]}
                                      isOpen={dropdownStates.searchType}
                                      onClick={() => handleDropdownToggle('searchType')}
                                      onSelect={value => handleDropdownSelect('searchType', value)}
                                      disabled={true}
                                    />
                                  </div>
                                </UIGroup>

                                <UITypography variant='body-1' className='secondary-neutral-p w-[11px] justify-center'>
                                  ~
                                </UITypography>

                                <UIGroup gap={10} direction='row' vAlign='center'>
                                  <div className='flex-1'>
                                    <UIInput.Date
                                      value={dateValueEnd}
                                      onChange={e => {
                                        setDateValueEnd(e.target.value);
                                      }}
                                      disabled={true}
                                    />
                                  </div>

                                  <div className='w-[100px]'>
                                    <UIDropdown
                                      value={searchValues.searchType}
                                      placeholder='조회 조건 선택'
                                      options={[
                                        { value: 'val1', label: '전체' },
                                        { value: 'val2', label: '정상' },
                                        { value: 'val3', label: '과부하' },
                                      ]}
                                      isOpen={dropdownStates.searchType}
                                      onClick={() => handleDropdownToggle('searchType')}
                                      onSelect={value => handleDropdownSelect('searchType', value)}
                                      disabled={true}
                                    />
                                  </div>
                                </UIGroup>
                              </UIUnitGroup>
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

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                평균 사용률
              </UITypography>
            </div>
            <div className='article-body'>
              <UIGroup gap={16} direction='row'>
                {/* 데이터 카드영역 [1] */}
                <div className='card-default flex-1'>
                  <div className='flex justify-between mb-[24px]'>
                    <div className='w-[240px] flex align-center items-start'>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                        CPU
                      </UITypography>
                    </div>
                  </div>
                  <div className='card-list-wrapper'>
                    <div className='card-list'>
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          할당량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='semantic-deep'>
                          0.0436%
                        </UITypography>
                      </UIGroup>
                    </div>
                    <div className='card-list'>
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          상한량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800'>
                          0.0581%
                        </UITypography>
                      </UIGroup>
                    </div>
                  </div>
                </div>
                {/* 데이터 카드영역 [2] */}
                <div className='card-default flex-1'>
                  <div className='flex justify-between mb-[24px]'>
                    <div className='w-[240px] flex align-center items-start'>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                        Memory
                      </UITypography>
                    </div>
                  </div>
                  <div className='card-list-wrapper'>
                    <div className='card-list'>
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          할당량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='semantic-deep'>
                          0.0436%
                        </UITypography>
                      </UIGroup>
                    </div>
                    <div className='card-list'>
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          상한량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800'>
                          0.0581%
                        </UITypography>
                      </UIGroup>
                    </div>
                  </div>
                </div>
              </UIGroup>
            </div>
          </UIArticle>

          <UIArticle>
            {/* Chart 영역 구분 */}
            <div className='chart-container'>
              {/* [251110_퍼블수정] : 그래프 - 데이터 없음처리시 series={[]} */}
              <UILineChart label='CPU 사용량' x='시간(초)' y='사용량' options={lineChartState1.options} series={[]} />
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={rowData.length} prefix='CPU Quota 총' unit='건' />
                      </div>
                    </div>
                    <div className='flex'>
                      <div style={{ width: '160px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(value)}
                          disabled={true}
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
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          <UIArticle>
            {/* Chart 영역 구분 */}
            <div className='chart-container'>
              {/* [251110_퍼블수정] : 그래프 - 데이터 없음처리시 series={[]} */}
              <UILineChart label='Memory 사용량' x='시간(초)' y='사용량' options={lineChartState2.options} series={[]} />
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={rowData.length} prefix='Memory Quota 총' unit='건' />
                      </div>
                    </div>
                    <div className='flex'>
                      <div style={{ width: '160px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(value)}
                          disabled={true}
                          options={[
                            { value: '12', label: '12개씩 보기' },
                            { value: '24', label: '24개씩 보기' },
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
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
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
