import { useEffect, useRef, useState } from 'react';

import type { ApexOptions } from 'apexcharts';

import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';

import { UIBox, UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UICircleChart, UILineChart } from '@/components/UI/molecules/chart';
import { UIInput } from '@/components/UI/molecules/input';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITooltip } from '@/components/UI/atoms/UITooltip';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  projectType: string;
  searchKeyword: string;
  status: string;
  agentType: string;
  startTime: string;
  endTime: string;
}

export const AD_100201 = () => {
  // 월별 피커 - default 상태
  const [selectedMonth, setSelectedMonth] = useState('2025.11');
  // 어느 쪽 날짜가 변경되었는지 추적
  const lastChangedRef = useRef<'start' | 'end'>('start');

  // 날짜에 일수를 더하거나 빼는 함수
  const addDaysToDate = (date: string, days: number) => {
    const dateObj = new Date(date);
    dateObj.setDate(dateObj.getDate() + days);
    return dateObj.toISOString().split('T')[0];
  };

  // 시작일이 변경되면 종료일을 7일 뒤로 자동 설정
  useEffect(() => {
    if (lastChangedRef.current === 'start') {
      const newEndDate = addDaysToDate(selectedMonth, 7);
      setSelectedMonth(newEndDate);
    }
  }, [selectedMonth]);

  // 종료일이 변경되면 시작일을 7일 이전으로 자동 설정
  useEffect(() => {
    if (lastChangedRef.current === 'end') {
      const newStartDate = addDaysToDate(selectedMonth, -7);
      setSelectedMonth(newStartDate);
    }
  }, [selectedMonth]);

  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '월별',
    projectType: '전체',
    searchKeyword: '',
    status: '전체',
    agentType: '전체',
    startTime: '',
    endTime: '',
  });

  const [activeTab, setActiveTab] = useState('agentTab2');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    projectType: false,
    status: false,
    agentType: false,
  });

  // date 타입
  const [dateValue, setDateValue] = useState('2025.06.29');

  // 월별 타입
  // const [selectedMonth, setSelectedMonth] = useState('');

  // 주별 타입 (시작, 끝)
  const [weekStartDate, setWeekStartDate] = useState('');
  const [weekEndDate, setWeekEndDate] = useState('');

  // 가장 많이 사용한 메뉴 차트 데이터 유무
  const [hasChartData] = useState(true);

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

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'agentTab1', label: '사용 이력' },
    { id: 'agentTab2', label: '사용 통계' },
  ];

  // API 호출 실패 예약 테이블 데이터
  // 데이터 있을 때 테스트용 (주석 해제하면 테이블 표시)
  // const apiFailureData: Array<{ time: string; action: string; code: string }> = [];

  // [251111_퍼블수정] horizontalBar 관련 변수는 커스텀 차트로 변경되면서 미사용

  // colorStops를 searchType에 따라 동적으로 생성
  const getColorStops = () => {
    switch (searchValues.searchType) {
      case '주별':
        return [
          {
            offset: 0,
            color: 'rgba(255, 171, 8, 0.8)',
            opacity: 1,
          },
          {
            offset: 100,
            color: 'rgba(255, 197, 84, 0.1)',
            opacity: 1,
          },
        ];
      case '일별':
        return [
          {
            offset: 0,
            color: 'rgba(129, 102, 210, 0.6)',
            opacity: 1,
          },
          {
            offset: 100,
            color: 'rgba(129, 102, 210, 0.1)',
            opacity: 1,
          },
        ];
      case '월별':
      default:
        return [
          {
            offset: 0,
            color: 'rgba(157, 193, 255, 0.35)',
            opacity: 1,
          },
          {
            offset: 100,
            color: 'rgba(38, 112, 255, 0.6)',
            opacity: 1,
          },
        ];
    }
  };

  const getLineColor = () => {
    switch (searchValues.searchType) {
      case '주별':
        return ['#FFAB08'];
      case '일별':
        return ['#8166D2'];
      case '월별':
      default:
        return ['#005DF9'];
    }
  };

  const lineChartOptions: ApexOptions = {
    fill: {
      type: 'gradient',
      gradient: {
        type: 'vertical',
        colorStops: getColorStops(),
      },
    },
    xaxis: {
      type: 'category',
      categories: ['02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13'],
    },
    colors: getLineColor(),
    stroke: {
      width: 2,
      curve: 'smooth',
    },
    // [251127_퍼블수정] 차트 너비 리사이징 수정 S
    responsive: [
      {
        breakpoint: 1900,
        options: {
          chart: {
            width: '1480px',
          },
        },
      },
    ],
    // [251127_퍼블수정] 차트 너비 리사이징 수정 E
  };

  const straightChartState2 = {
    series: [
      {
        name: '방문자 수',
        data: [220, 280, 310, 375, 388, 410, 270, 150, 200, 550, 300, 800],
      },
    ],
    options: lineChartOptions,
  };

  // [251111_퍼블수정] horizontalBarOptions는 커스텀 차트로 변경되면서 미사용

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
        <UIPageHeader title='사용자 이용 현황' description='포탈 전체 사용자의 사용 이력 및 통계를 확인하고 관리할 수 있습니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
          </UIArticle>

          {/* 검색 영역 */}
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
                                value={searchValues.searchType}
                                placeholder='조회 기간 선택'
                                options={[
                                  { value: '월별', label: '월별' },
                                  { value: '주별', label: '주별' },
                                  { value: '일별', label: '일별' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>

                            {/* 월별 조회 */}
                            {searchValues.searchType === '월별' && (
                              <div className='flex-1'>
                                <UIInput.Date value={selectedMonth} onChange={e => setSelectedMonth(e.target.value)} type='MONTH' placeholder='월별 조회' />
                                {/* <UIMonthPicker year={monthYear} month={selectedMonth} onYearChange={setMonthYear} onMonthSelect={setSelectedMonth} /> */}
                              </div>
                            )}

                            {/* 주별 조회 */}
                            {searchValues.searchType === '주별' && (
                              <div className='flex-1 flex gap-2'>
                                <div className='flex-1 date-weekly-first'>
                                  <UIInput.Date
                                    value={weekStartDate}
                                    onChange={e => {
                                      const newStartDate = e.target.value;
                                      setWeekStartDate(newStartDate);
                                    }}
                                    placeholder='주별 조회'
                                    dateType='date-weekly-first'
                                    highlightDays={{ after: 7 }}
                                  />
                                </div>
                                <div className='flex-1 date-weekly-last'>
                                  <UIInput.Date
                                    value={weekEndDate}
                                    onChange={e => {
                                      const newEndDate = e.target.value;
                                      setWeekEndDate(newEndDate);
                                    }}
                                    placeholder='주별 조회'
                                    dateType='date-weekly-first'
                                    highlightDays={{ before: 7 }}
                                  />
                                </div>
                              </div>
                            )}

                            {/* 일별 조회 */}
                            {searchValues.searchType === '일별' && (
                              <div className='flex-1'>
                                <UIInput.Date
                                  value={dateValue}
                                  onChange={e => {
                                    setDateValue(e.target.value);
                                  }}
                                  placeholder='일별 조회'
                                />
                              </div>
                            )}
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            프로젝트명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={searchValues.projectType}
                                placeholder='프로젝트명 선택'
                                options={[
                                  { value: '프로젝트명1', label: '프로젝트명1' },
                                  { value: '프로젝트명2', label: '프로젝트명2' },
                                  { value: '프로젝트명3', label: '프로젝트명3' },
                                ]}
                                isOpen={dropdownStates.projectType}
                                onClick={() => handleDropdownToggle('projectType')}
                                onSelect={value => handleDropdownSelect('projectType', value)}
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

          {/* Chart 영역 */}
          {/* [251105_퍼블수정] 마크업 수정  */}
          <UIArticle className='article-grid'>
            <div className='article-body'>
              {/* Chart 영역 구분 */}
              <div className='chart-container'>
                <UILineChart
                  type='straight'
                  label={
                    <div className='flex items-center'>
                      <UITypography variant='title-4' className='mr-1'>
                        프로젝트 방문자 수
                      </UITypography>
                      <UITypography variant='body-1' className='text-blue-800 text-sb'>
                        1000
                      </UITypography>
                      <UITypography variant='body-1' className='text-sb'>
                        명
                      </UITypography>
                      <UITooltip
                        trigger='click'
                        position='bottom-start'
                        type='notice'
                        title='프로젝트 방문자 수 안내'
                        items={['동일 사용자는 프로젝트별 최초 1회만 집계됩니다.']}
                        bulletType='dash'
                        showArrow={false}
                        showCloseButton={true}
                        className='ml-2 h-5'
                      >
                        <UIButton2>
                          <UIIcon2 className='ic-system-20-info cursor-pointer' />
                        </UIButton2>
                      </UITooltip>
                    </div>
                  }
                  x='최근 12개월'
                  y='방문자 수'
                  options={straightChartState2.options}
                  series={straightChartState2.series}
                />
              </div>

              {/* Chart 영역 구분 */}
              <div className='chart-container mt-4 min-h-[300px]'>
                {/* 
                  [참고] 모두 height : 300px 기준으로 맞춤. 차트는 height: 100% / auto 제어가 안됩니다.
                */}
                {/* 원형 차트 */}
                <div className='flex-[0_0_630px] h-full'>
                  {/* 원형차트 : height 값은 auto & 100% 로 제어안됨. ApexCharts는 무조건 고정값이 있어야합니다. */}
                  {/* [251107_퍼블수정] : 성공/실패 prop 추가 successValue={70} failValue={29} */}
                  <UICircleChart.Full label='API 호출 성공/실패율' value={70.3} total={100} dateRange='2025.08.01 ~ 2025.08.31' successValue={72345} failValue={2330} />
                  {/* <UICircleChart.Full label='API 호출 성공/실패율' value={0} total={0} dateRange='2025.08.01 ~ 2025.08.31' /> 데이터 없을경우 (테스트) */}
                </div>
                {/* 가장 많이 사용한 메뉴 차트 */}
                {/* [251125_퍼블수정] 폰트 스타일 수정 */}
                <div className='flex-1 h-full'>
                  <div className='chart-item h-full'>
                    <div className='chart-header mb-4 flex justify-between'>
                      <div className='flex items-center gap-4'>
                        <div className='chart-title'>
                          <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
                            가장 많이 사용한 메뉴
                          </UITypography>
                        </div>
                        <div className='chart-legend flex-1'>
                          <ul>
                            <li className='legend-item'>
                              <span className='legend-marker legend-marker-sky'></span>
                              <UITypography variant='body-2' className='secondary-neutral-600 text-gray-500'>
                                이용자 수
                              </UITypography>
                            </li>
                          </ul>
                        </div>
                      </div>
                      <div className='text-body-2 ml-auto text-[#8B95A9]'>2025.08.01 ~ 2025.08.31</div>
                    </div>

                    {/* 데이터 있을 경우 */}
                    {hasChartData ? (
                      <div className='horizontal-chart'>
                        <div className='item'>
                          <UITypography variant='body-1' className='label secondary-neutral-600'>
                            데이터 탐색
                          </UITypography>
                          <div className='progress'>
                            <span className='bar' style={{ '--target-width': `${5}%` } as any}></span>
                            <span className='value'>
                              <UITypography variant='body-2' className='secondary-neutral-600 '>
                                10
                              </UITypography>
                            </span>
                          </div>
                        </div>
                        <div className='item'>
                          <UITypography variant='body-1' className='label secondary-neutral-600'>
                            모델 관리
                          </UITypography>
                          <div className='progress'>
                            <span className='bar' style={{ '--target-width': `${20}%` } as any}></span>
                            <span className='value'>
                              <UITypography variant='body-2' className='secondary-neutral-600 '>
                                236
                              </UITypography>
                            </span>
                          </div>
                        </div>
                        <div className='item'>
                          <UITypography variant='body-1' className='label secondary-neutral-600'>
                            플레이 그라운드
                          </UITypography>
                          <div className='progress'>
                            <span className='bar' style={{ '--target-width': `${45}%` } as any}></span>
                            <span className='value'>
                              <UITypography variant='body-2' className='secondary-neutral-600 '>
                                1425
                              </UITypography>
                            </span>
                          </div>
                        </div>
                        <div className='item'>
                          <UITypography variant='body-1' className='label secondary-neutral-600'>
                            로그
                          </UITypography>
                          <div className='progress'>
                            <span className='bar' style={{ '--target-width': `${75}%` } as any}></span>
                            <span className='value'>
                              <UITypography variant='body-2' className='secondary-neutral-600 '>
                                3872
                              </UITypography>
                            </span>
                          </div>
                        </div>
                        <div className='item'>
                          <UITypography variant='body-1' className='label secondary-neutral-600'>
                            지식/학습 데이터 관리
                          </UITypography>
                          <div className='progress'>
                            <span className='bar' style={{ '--target-width': `${87}%` } as any}></span>
                            <span className='value'>
                              <UITypography variant='body-2' className='secondary-neutral-600 '>
                                3323
                              </UITypography>
                            </span>
                          </div>
                        </div>
                        <div className='item'>
                          <UITypography variant='body-1' className='label secondary-neutral-600'>
                            에이전트 배포 데이터관리
                          </UITypography>
                          <div className='progress'>
                            <span className='bar' style={{ '--target-width': `${100}%` } as any}></span>
                            <span className='value'>
                              <UITypography variant='body-2' className='secondary-neutral-600 '>
                                112350
                              </UITypography>
                            </span>
                          </div>
                        </div>
                      </div>
                    ) : (
                      /* 데이터 없을 경우 */
                      <div className='flex-1 flex items-center justify-center h-[220px]'>
                        <div className='flex flex-col justify-center items-center gap-3'>
                          <span className='ico-nodata'>
                            <UIIcon2 className='ic-system-80-default-nodata' />
                          </span>
                          <span className='text-body-1 secondary-neutral-500'>조회된 데이터가 없습니다.</span>
                        </div>
                      </div>
                    )}
                  </div>

                  {/* <UIHorizontalBarChart
                    label='가장 많이 사용한 메뉴'
                    options={horizontalBarChartState.options}
                    series={horizontalBarChartState.series}
                    height={243} // height 값은 auto & 100% 로 제어안됨. ApexCharts는 무조건 고정값이 있어야합니다.
                    dateRange='2025.08.01 ~ 2025.08.31'
                  /> */}
                </div>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
