import { useState } from 'react';

import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UIBox, UIButton2 } from '@/components/UI/atoms';
import { UIGroup, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UIBarChart, UILineChart } from '@/components/UI/molecules/chart';
import type { ApexOptions } from 'apexcharts';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  agentType: string;
  startTime: string;
  endTime: string;
}

export const DP_020201 = () => {
  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    agentType: '전체',
    startTime: '',
    endTime: '',
  });

  const [activeTab, setActiveTab] = useState('agentTab3');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    agentType: false,
  });

  // 모델별 추론 성능 드롭다운 상태
  const [isModelPerformanceOpen, setIsModelPerformanceOpen] = useState(false);
  const [selectedModel, setSelectedModel] = useState('');

  const [dropdownStatesStart, setDropdownStatesStart] = useState({
    dateType: false,
    searchType: false,
    status: false,
    agentType: false,
  });

  const [dropdownStatesEnd, setDropdownStatesEnd] = useState({
    dateType: false,
    searchType: false,
    status: false,
    agentType: false,
  });

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');

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

  // 시작 시간 드롭다운 핸들러
  const handleDropdownToggleStart = (key: keyof typeof dropdownStatesStart) => {
    setDropdownStatesStart(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelectStart = (_value: string) => {
    setSearchValues(prev => ({ ...prev, startTime: _value }));
    setDropdownStatesStart(prev => ({ ...prev, searchType: false }));
  };

  // 끝 시간 드롭다운 핸들러
  const handleDropdownToggleEnd = (key: keyof typeof dropdownStatesEnd) => {
    setDropdownStatesEnd(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelectEnd = (value: string) => {
    setSearchValues(prev => ({ ...prev, endTime: value }));
    setDropdownStatesEnd(prev => ({ ...prev, searchType: false }));
  };

  // 모델별 추론 성능 드롭다운 핸들러
  const handleModelPerformanceToggle = () => {
    setIsModelPerformanceOpen(!isModelPerformanceOpen);
  };

  const handleModelPerformanceSelect = (value: string) => {
    setSelectedModel(value);
    setIsModelPerformanceOpen(false);
  };

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'agentTab1', label: '기본 정보' },
    { id: 'agentTab2', label: '시스템로그' },
    { id: 'agentTab3', label: '모니터링' },
  ];

  const [lineChartState3] = useState({
    series: [
      {
        name: 'name1',
        data: [31.0, 40.0, 28.0, 51.0, 42.0, 35.0],
      },
    ],
    options: {
      chart: {
        width: '100%',
      },
      xaxis: {
        type: 'category',
        categories: ['12:58:30', '12:59:00', '12:59:30', '13:00:00', '13:00:30', '13:01:00'],
      },
      stroke: {
        width: 2,
        curve: 'smooth',
      },
      legend: {
        show: false,
      },
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          shadeIntensity: 0,
          colorStops: [
            {
              offset: 16.3,
              color: 'rgba(255, 171, 8, 0.1)',
              opacity: 1,
            },
            {
              offset: 110.4,
              color: 'rgba(255, 171, 8, 0)',
              opacity: 1,
            },
          ],
        },
      },
      colors: ['#FFAB08'],
    } as ApexOptions,
  });

  const [barChartState] = useState({
    series: [
      {
        name: '호출건수',
        data: [30, 45, 60, 70, 40, 35, 30, 20, 15, 10, 100, 120, 30, 104, 234, 23, 23, 20],
      },
    ],
    options: {
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          shadeIntensity: 0,
          gradientToColors: ['rgba(157, 193, 255, 0.35)'],
          stops: [0, 100],
          colorStops: [
            {
              offset: 0,
              color: 'rgba(38, 112, 255, 0.6)',
              opacity: 1,
            },
            {
              offset: 100,
              color: 'rgba(157, 193, 255, 0.35)',
              opacity: 1,
            },
          ],
        },
      },
      stroke: {
        show: false,
        curve: 'smooth',
      },
      plotOptions: {
        bar: {
          borderRadius: 4,
          borderRadiusApplication: 'end',
          columnWidth: '96%',
        },
      },
      xaxis: {
        categories: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18'],
      },
    } as ApexOptions,
  });

  const [barChartStateSmall1] = useState({
    series: [
      {
        name: '호출건수',
        data: [0, 10, 30, 60, 90, 100, 150],
      },
    ],
    options: {
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          shadeIntensity: 0,
          gradientToColors: ['rgba(200, 245, 255, 0.35)'],
          stops: [0, 100],
          colorStops: [
            {
              offset: 0,
              color: 'rgba(200, 245, 255, 1)',
              opacity: 1,
            },
            {
              offset: 100,
              color: 'rgba(200, 245, 255, 0.35)',
              opacity: 1,
            },
          ],
        },
      },
      stroke: {
        show: false,
        curve: 'smooth',
      },
      plotOptions: {
        bar: {
          borderRadius: 4,
          borderRadiusApplication: 'end',
          columnWidth: '96%',
        },
      },
      xaxis: {
        categories: ['0', '5', '10', '15', '20', '30', '50'],
      },
    } as ApexOptions,
  });

  const [barChartStateSmall2] = useState({
    series: [
      {
        name: '호출건수',
        data: [0, 10, 30, 60, 90, 100, 150],
      },
    ],
    options: {
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          shadeIntensity: 0,
          gradientToColors: ['rgba(223, 205, 255, 0.35)'],
          stops: [0, 100],
          colorStops: [
            {
              offset: 0,
              color: 'rgba(223, 205, 255, 1)',
              opacity: 1,
            },
            {
              offset: 100,
              color: 'rgba(223, 205, 255, 0.35)',
              opacity: 1,
            },
          ],
        },
      },
      stroke: {
        show: false,
        curve: 'smooth',
      },
      plotOptions: {
        bar: {
          borderRadius: 4,
          borderRadiusApplication: 'end',
          columnWidth: '96%',
        },
      },
      xaxis: {
        categories: ['0', '5', '10', '15', '20', '30', '50'],
      },
    } as ApexOptions,
  });

  const [barChartStateSmall3] = useState({
    series: [
      {
        name: '호출건수',
        data: [0, 10, 30, 60, 90, 100, 150],
      },
    ],
    options: {
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          shadeIntensity: 0,
          gradientToColors: ['rgba(255, 216, 179, 0.35)'],
          stops: [0, 100],
          colorStops: [
            {
              offset: 0,
              color: 'rgba(255, 216, 179, 1)',
              opacity: 1,
            },
            {
              offset: 100,
              color: 'rgba(255, 216, 179, 0.35)',
              opacity: 1,
            },
          ],
        },
      },
      stroke: {
        show: false,
        curve: 'smooth',
      },
      plotOptions: {
        bar: {
          borderRadius: 4,
          borderRadiusApplication: 'end',
          columnWidth: '96%',
        },
      },
      xaxis: {
        categories: ['0', '5', '10', '15', '20', '30', '50'],
      },
    } as ApexOptions,
  });

  const [lineChartState4] = useState({
    series: [
      {
        name: 'name1',
        data: [31.0, 40.0, 28.0, 51.0, 42.0, 0.0, 0.0],
      },
    ],
    options: {
      xaxis: {
        type: 'category',
        categories: ['12:58', '12:59', '12:59', '13', '13', '13:01'],
      },
      stroke: {
        width: 2,
        curve: 'smooth',
      },
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          shadeIntensity: 0,
          colorStops: [
            {
              offset: 12.8,
              color: 'rgba(55, 216, 208, 0.1)',
              opacity: 1,
            },
            {
              offset: 110.83,
              color: 'rgba(55, 216, 208, 0)',
              opacity: 1,
            },
          ],
        },
      },
      colors: ['#37D8D0'],
    } as ApexOptions,
  });

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
        <UIPageHeader title='에이전트 배포 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          {/* 검색 영역 */}
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
                                value={searchValues.searchType}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '최근 24시간', label: '최근 24시간' },
                                  { value: '최근 48시간', label: '최근 48시간' },
                                  { value: '최근 72시간', label: '최근 72시간' },
                                  { value: '사용자 지정', label: '사용자 지정' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>

                            <div className='flex-1'>
                              <UIUnitGroup gap={8} direction='row' vAlign='center'>
                                <UIGroup gap={6} direction='row' vAlign='center'>
                                  <div className='flex-1' style={{ width: '194px' }}>
                                    <UIInput.Date
                                      value={dateValueStart}
                                      onChange={e => {
                                        setDateValueStart(e.target.value);
                                      }}
                                    />
                                  </div>

                                  <div className='w-[100px]'>
                                    <UIDropdown
                                      value={searchValues.startTime}
                                      placeholder='시간'
                                      options={[
                                        { value: '14시', label: '14시' },
                                        { value: '15시', label: '15시' },
                                        { value: '16시', label: '16시' },
                                      ]}
                                      isOpen={dropdownStatesStart.searchType}
                                      onClick={() => handleDropdownToggleStart('searchType')}
                                      onSelect={handleDropdownSelectStart}
                                    />
                                  </div>
                                </UIGroup>

                                <UITypography variant='body-1' className='secondary-neutral-p w-[11px] justify-center'>
                                  ~
                                </UITypography>

                                <UIGroup gap={6} direction='row' vAlign='center'>
                                  <div className='flex-1' style={{ width: '194px' }}>
                                    <UIInput.Date
                                      value={dateValueEnd}
                                      onChange={e => {
                                        setDateValueEnd(e.target.value);
                                      }}
                                    />
                                  </div>

                                  <div className='w-[100px]'>
                                    <UIDropdown
                                      value={searchValues.endTime}
                                      placeholder='시간'
                                      options={[
                                        { value: '14시', label: '14시' },
                                        { value: '15시', label: '15시' },
                                        { value: '16시', label: '16시' },
                                      ]}
                                      isOpen={dropdownStatesEnd.searchType}
                                      onClick={() => handleDropdownToggleEnd('searchType')}
                                      onSelect={handleDropdownSelectEnd}
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

          {/* Chart 영역 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                에이전트 트래픽
              </UITypography>
            </div>
            <div className='article-body'>
              {/* 데이터 카드영역 card-default (09/24 UI 구조 변경됨) */}
              <div className='card-default'>
                <div className='card-list-wrapper'>
                  <div className='card-list'>
                    <UIGroup direction='column' gap={6} vAlign='center'>
                      <UITypography variant='body-1' className='secondary-neutral-500'>
                        전체 호출
                      </UITypography>
                      <UITypography variant='title-3' className='primary-800'>
                        300
                      </UITypography>
                    </UIGroup>
                  </div>
                  <div className='card-list'>
                    <UIGroup direction='column' gap={6} vAlign='center'>
                      <UITypography variant='body-1' className='secondary-neutral-500'>
                        정상
                      </UITypography>
                      <UITypography variant='title-3' className='secondary-neutral-700'>
                        289
                      </UITypography>
                    </UIGroup>
                  </div>
                  <div className='card-list'>
                    <UIGroup direction='column' gap={6} vAlign='center'>
                      <UITypography variant='body-1' className='secondary-neutral-500'>
                        오류
                      </UITypography>
                      <UITypography variant='title-3' className='semantic-deep-red'>
                        11
                      </UITypography>
                    </UIGroup>
                  </div>
                </div>
              </div>
              <div className='chart-container mt-4'>
                <UIBarChart label='시간별 호출건수' x='시간 (ms)' y='요청수(개)' options={barChartState.options} series={[]} height={368} />
              </div>
              <div className='chart-container flex mt-4'>
                <UILineChart label='평균 응답시간' x='시간' y='건수' options={lineChartState3.options} series={[]} />
                <UILineChart label='성공률' x='시간' y='건수' options={lineChartState4.options} series={[]} />
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='mb-4'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                모델별 추론 성능
              </UITypography>
              <div className='flex mt-2'>
                <UIDropdown
                  value={selectedModel}
                  placeholder='모델별 추론 성능 선택'
                  options={[
                    { value: 'val1', label: 'GIP/gpt-4o' },
                    { value: 'val2', label: '문서요약기' },
                    { value: 'val3', label: '챗봇_모델' },
                  ]}
                  isOpen={isModelPerformanceOpen}
                  onClick={handleModelPerformanceToggle}
                  onSelect={handleModelPerformanceSelect}
                  className='basis-[320px]'
                />
              </div>
            </div>
            <div className='article-body'>
              <div className='chart-container'>
                {/* [251110_퍼블수정] : 그래프 - 데이터 없음처리시 series={[]}  */}
                <UIBarChart label='Time To First Token' x='시간 (ms)' y='요청수(개)' options={barChartStateSmall1.options} series={[]} height={292} />
                <UIBarChart label='Time Per Output Token' x='시간 (ms)' y='요청수(개)' options={barChartStateSmall2.options} series={[]} height={292} />
                <UIBarChart label='Time Per Output Token' x='시간 (ms)' y='요청수(개)' options={barChartStateSmall3.options} series={[]} height={292} />
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
