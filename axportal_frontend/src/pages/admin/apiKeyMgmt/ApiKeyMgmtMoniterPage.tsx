import { useAtom, useAtomValue } from 'jotai';
import { useEffect, useMemo, useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

import { UIBox, UIButton2, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIBarChart, UILineChart } from '@/components/UI/molecules/chart';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetApiKeyStatic } from '@/services/deploy/apikey/apikey.services';
import { apiKeyMonitorSearchAtom, selectedApiKeyAtom } from '@/stores/admin/apiKeyMgmt';
import type { ApexOptions } from 'apexcharts';

const getInitialDates = () => {
  const now = new Date();
  const yesterday = new Date(now);
  yesterday.setDate(yesterday.getDate() - 1);

  const formatDate = (date: Date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  return {
    startDate: formatDate(yesterday),
    endDate: formatDate(now),
  };
};

// 현재 시간을 시간 단위로 올림 처리
const getCurrentTimeCeiled = () => {
  const now = new Date();
  const currentHour = now.getHours();
  // 시간 단위로 올림 처리 (예: 14:30 → 15시)
  const ceiledHour = currentHour === 23 ? 24 : currentHour + 1;
  return `${String(ceiledHour).padStart(2, '0')}시`;
};

export const ApiKeyMgmtMoniterPage = () => {
  const queryClient = useQueryClient();

  const [searchValues, setSearchValues] = useAtom(apiKeyMonitorSearchAtom);
  const selectedApiKey = useAtomValue(selectedApiKeyAtom);

  const initialDates = useMemo(() => getInitialDates(), []);

  const [dateValueStart, setDateValueStart] = useState(initialDates.startDate);
  const [dateValueEnd, setDateValueEnd] = useState(initialDates.endDate);

  const [isCalendarDisabled, setIsCalendarDisabled] = useState(true);

  const [chartKey, setChartKey] = useState(Date.now());

  const [dropdownStates, setDropdownStates] = useState({
    searchType: false,
    projectType: false,
  });

  const [dropdownStatesStart, setDropdownStatesStart] = useState({
    searchType: false,
  });

  const [dropdownStatesEnd, setDropdownStatesEnd] = useState({
    searchType: false,
  });

  const formatDateTime = (date: string, time: string): string => {
    const dateStr = date.replace(/\./g, '');
    const hour = time.replace('시', '').padStart(2, '0');
    return `${dateStr}${hour}00`;
  };

  const {
    data: staticData,
    refetch: refetchStatic,
    isLoading,
    isFetching,
  } = useGetApiKeyStatic(
    {
      id: selectedApiKey?.id || '',
      startDate: formatDateTime(dateValueStart, searchValues.startTime),
      endDate: formatDateTime(dateValueEnd, searchValues.endTime),
    },
    {
      enabled: !!selectedApiKey?.id,
      staleTime: 0,
      gcTime: 0,
      refetchOnMount: 'always',
    }
  );

  useEffect(() => {
    if (staticData && Array.isArray(staticData) && staticData.length > 0) {
      setChartKey(Date.now());
    }
  }, [staticData, isLoading, isFetching]);

  const handleSearch = async () => {
    if (selectedApiKey?.id) {
      await queryClient.invalidateQueries({
        queryKey: ['apikey-static'],
        exact: false,
      });
      refetchStatic();
    }
  };

  // 페이지 오픈 시 필터 초기화
  useEffect(() => {
    const initialDates = getInitialDates();
    const currentTime = getCurrentTimeCeiled();

    // 필터 초기값 설정
    setSearchValues({
      dateType: '생성일시',
      dateRange: { startDate: initialDates.startDate, endDate: initialDates.endDate },
      searchType: '최근 24시간',
      projectType: '전체',
      searchKeyword: '',
      status: '전체',
      agentType: '전체',
      startTime: currentTime,
      endTime: currentTime,
    });

    // 날짜 초기값 설정
    setDateValueStart(initialDates.startDate);
    setDateValueEnd(initialDates.endDate);
    setIsCalendarDisabled(true);
  }, []);

  useEffect(() => {
    const calculateDate = (daysAgo: number): string => {
      const now = new Date();
      const pastDate = new Date(now);
      pastDate.setDate(pastDate.getDate() - daysAgo);

      const year = pastDate.getFullYear();
      const month = String(pastDate.getMonth() + 1).padStart(2, '0');
      const day = String(pastDate.getDate()).padStart(2, '0');
      return `${year}.${month}.${day}`;
    };

    if (searchValues.searchType === '최근 24시간') {
      setDateValueStart(calculateDate(1));
      setIsCalendarDisabled(true);
    } else if (searchValues.searchType === '최근 48시간') {
      setDateValueStart(calculateDate(2));
      setIsCalendarDisabled(true);
    } else if (searchValues.searchType === '최근 72시간') {
      setDateValueStart(calculateDate(3));
      setIsCalendarDisabled(true);
    } else if (searchValues.searchType === '사용자 지정') {
      setIsCalendarDisabled(false);
    }
  }, [searchValues.searchType, setSearchValues]);

  const callCountData = useMemo(() => {
    let data: number[] = [];
    let categories: string[] = [];

    if (staticData && Array.isArray(staticData) && staticData.length > 0) {
      const dataLength = Math.min(staticData.length, 24);

      data = staticData.slice(0, 24).map(item => item?.totalCount || 0);
      categories = Array.from({ length: dataLength }, (_, i) => `${String(i + 1).padStart(2, '0')}시`);

      while (data.length < 24) {
        data.push(0);
      }
      while (categories.length < 24) {
        categories.push(`${String(categories.length + 1).padStart(2, '0')}시`);
      }
    } else {
      data = Array(24).fill(0);
      categories = Array.from({ length: 24 }, (_, i) => `${String(i + 1).padStart(2, '0')}시`);
    }

    const chartOptions: ApexOptions = {
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
        type: 'category',
        categories: categories,
      },
    };

    return {
      series: [
        {
          name: '호출 건수',
          data: data,
        },
      ],
      options: chartOptions,
    };
  }, [staticData]);

  const responseTimeData = useMemo(() => {
    let data: number[] = [];
    let categories: string[] = [];

    if (staticData && Array.isArray(staticData) && staticData.length > 0) {
      const dataLength = Math.min(staticData.length, 24);

      data = staticData.slice(0, 24).map(item => item?.resMiliSec || 0);
      categories = Array.from({ length: dataLength }, (_, i) => `${String(i + 1).padStart(2, '0')}시`);

      while (data.length < 24) {
        data.push(0);
      }
      while (categories.length < 24) {
        categories.push(`${String(categories.length + 1).padStart(2, '0')}시`);
      }
    } else {
      data = Array(24).fill(0);
      categories = Array.from({ length: 24 }, (_, i) => `${String(i + 1).padStart(2, '0')}시`);
    }

    return {
      series: [
        {
          name: '응답시간',
          data: data,
        },
      ],
      options: {
        xaxis: {
          type: 'category',
          categories: categories,
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
    };
  }, [staticData]);

  const successRateData = useMemo(() => {
    let data: number[] = [];
    let categories: string[] = [];

    if (staticData && Array.isArray(staticData) && staticData.length > 0) {
      const dataLength = Math.min(staticData.length, 24);

      data = staticData.slice(0, 24).map(item => {
        if (!item) return 0;

        const total = (item.succCount || 0) + (item.failCount || 0);
        if (total === 0) return 0;
        return Math.round(((item.succCount || 0) / total) * 100 * 100) / 100;
      });

      categories = Array.from({ length: dataLength }, (_, i) => `${String(i + 1).padStart(2, '0')}시`);

      while (data.length < 24) {
        data.push(0);
      }
      while (categories.length < 24) {
        categories.push(`${String(categories.length + 1).padStart(2, '0')}시`);
      }
    } else {
      data = Array(24).fill(0);
      categories = Array.from({ length: 24 }, (_, i) => `${String(i + 1).padStart(2, '0')}시`);
    }

    return {
      series: [
        {
          name: '성공률',
          data: data,
        },
      ],
      options: {
        xaxis: {
          type: 'category',
          categories: categories,
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
    };
  }, [staticData]);

  const calculatePastDate = (daysAgo: number): string => {
    const now = new Date();
    const pastDate = new Date(now);
    pastDate.setDate(pastDate.getDate() - daysAgo);

    const year = pastDate.getFullYear();
    const month = String(pastDate.getMonth() + 1).padStart(2, '0');
    const day = String(pastDate.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  const aggregatedStats = useMemo(() => {
    if (!staticData || !Array.isArray(staticData)) {
      return { totalCalls: 0, successCalls: 0, errorCalls: 0 };
    }

    const totalCalls = staticData.reduce((sum, item) => sum + (item.totalCount || 0), 0);
    const successCalls = staticData.reduce((sum, item) => sum + (item.succCount || 0), 0);
    const errorCalls = staticData.reduce((sum, item) => sum + (item.failCount || 0), 0);

    return { totalCalls, successCalls, errorCalls };
  }, [staticData]);

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof typeof searchValues, value: string) => {
    if (key === 'searchType') {
      if (value === '최근 24시간') {
        setDateValueStart(calculatePastDate(1));
        setIsCalendarDisabled(true);
      } else if (value === '최근 48시간') {
        setDateValueStart(calculatePastDate(2));
        setIsCalendarDisabled(true);
      } else if (value === '최근 72시간') {
        setDateValueStart(calculatePastDate(3));
        setIsCalendarDisabled(true);
      } else if (value === '사용자 지정') {
        setIsCalendarDisabled(false);
      }
    }

    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key as keyof typeof dropdownStates]: false }));
  };

  const handleDropdownToggleStart = (key: keyof typeof dropdownStatesStart) => {
    setDropdownStatesStart(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelectStart = (value: string) => {
    if (dateValueStart === dateValueEnd) {
      const startHour = parseInt(value.replace('시', ''));
      const endHour = parseInt(searchValues.endTime.replace('시', ''));
      
      if (startHour > endHour) {
        setSearchValues(prev => ({ ...prev, startTime: value, endTime: value }));
        setDropdownStatesStart(prev => ({ ...prev, searchType: false }));
        return;
      }
    }
    
    setSearchValues(prev => ({ ...prev, startTime: value }));
    setDropdownStatesStart(prev => ({ ...prev, searchType: false }));
  };

  const handleDropdownToggleEnd = (key: keyof typeof dropdownStatesEnd) => {
    setDropdownStatesEnd(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelectEnd = (value: string) => {
    if (dateValueStart === dateValueEnd) {
      const startHour = parseInt(searchValues.startTime.replace('시', ''));
      const endHour = parseInt(value.replace('시', ''));
      
      if (endHour < startHour) {
        setSearchValues(prev => ({ ...prev, startTime: value, endTime: value }));
        setDropdownStatesEnd(prev => ({ ...prev, searchType: false }));
        return;
      }
    }
    
    setSearchValues(prev => ({ ...prev, endTime: value }));
    setDropdownStatesEnd(prev => ({ ...prev, searchType: false }));
  };

  return (
    <>
      <UIArticle className='article-filter'>
        <UIBox className='box-filter'>
          <UIGroup gap={40} direction='row'>
            <div style={{ width: 'calc(100% - 168px)' }}>
              <table className='tbl_type_b'>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        조회기간
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UIUnitGroup gap={32} direction='row'>
                        <div style={{ width: '500px' }}>
                          <UIDropdown
                            value={searchValues.searchType}
                            placeholder='조회조건 선택'
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
                            <div className='flex-1'>
                              <UIInput.Date
                                value={dateValueStart}
                                onChange={e => {
                                  const value = e.target.value.replace(/-/g, '.');
                                  
                                  setDateValueStart(value);
                                  
                                  if (dateValueEnd) {
                                    const startStr = value.replace(/\./g, '');
                                    const endStr = dateValueEnd.replace(/\./g, '');
                                    
                                    if (startStr > endStr) {
                                      setDateValueEnd(value);
                                    }
                                  }
                                }}
                                disabled={isCalendarDisabled}
                              />
                            </div>

                            <div className='w-[100px]'>
                              <UIDropdown
                                value={searchValues.startTime}
                                placeholder='시간'
                                options={[
                                  { value: '01시', label: '01시' },
                                  { value: '02시', label: '02시' },
                                  { value: '03시', label: '03시' },
                                  { value: '04시', label: '04시' },
                                  { value: '05시', label: '05시' },
                                  { value: '06시', label: '06시' },
                                  { value: '07시', label: '07시' },
                                  { value: '08시', label: '08시' },
                                  { value: '09시', label: '09시' },
                                  { value: '10시', label: '10시' },
                                  { value: '11시', label: '11시' },
                                  { value: '12시', label: '12시' },
                                  { value: '13시', label: '13시' },
                                  { value: '14시', label: '14시' },
                                  { value: '15시', label: '15시' },
                                  { value: '16시', label: '16시' },
                                  { value: '17시', label: '17시' },
                                  { value: '18시', label: '18시' },
                                  { value: '19시', label: '19시' },
                                  { value: '20시', label: '20시' },
                                  { value: '21시', label: '21시' },
                                  { value: '22시', label: '22시' },
                                  { value: '23시', label: '23시' },
                                  { value: '24시', label: '24시' },
                                ]}
                                isOpen={dropdownStatesStart.searchType}
                                onClick={() => handleDropdownToggleStart('searchType')}
                                onSelect={handleDropdownSelectStart}
                                disabled={isCalendarDisabled}
                              />
                            </div>

                            <UITypography variant='body-1' className='secondary-neutral-p w-[28px] justify-center'>
                              ~
                            </UITypography>
                            <div className='flex-1'>
                              <UIInput.Date
                                value={dateValueEnd}
                                onChange={e => {
                                  const value = e.target.value.replace(/-/g, '.');
                                  
                                  setDateValueEnd(value);
                                  
                                  if (dateValueStart) {
                                    const startStr = dateValueStart.replace(/\./g, '');
                                    const endStr = value.replace(/\./g, '');
                                    
                                    if (endStr < startStr) {
                                      setDateValueStart(value);
                                    }
                                  }
                                }}
                                disabled={isCalendarDisabled}
                              />
                            </div>

                            <div className='w-[100px]'>
                              <UIDropdown
                                value={searchValues.endTime}
                                placeholder='시간'
                                options={[
                                  { value: '01시', label: '01시' },
                                  { value: '02시', label: '02시' },
                                  { value: '03시', label: '03시' },
                                  { value: '04시', label: '04시' },
                                  { value: '05시', label: '05시' },
                                  { value: '06시', label: '06시' },
                                  { value: '07시', label: '07시' },
                                  { value: '08시', label: '08시' },
                                  { value: '09시', label: '09시' },
                                  { value: '10시', label: '10시' },
                                  { value: '11시', label: '11시' },
                                  { value: '12시', label: '12시' },
                                  { value: '13시', label: '13시' },
                                  { value: '14시', label: '14시' },
                                  { value: '15시', label: '15시' },
                                  { value: '16시', label: '16시' },
                                  { value: '17시', label: '17시' },
                                  { value: '18시', label: '18시' },
                                  { value: '19시', label: '19시' },
                                  { value: '20시', label: '20시' },
                                  { value: '21시', label: '21시' },
                                  { value: '22시', label: '22시' },
                                  { value: '23시', label: '23시' },
                                  { value: '24시', label: '24시' },
                                ]}
                                isOpen={dropdownStatesEnd.searchType}
                                onClick={() => handleDropdownToggleEnd('searchType')}
                                onSelect={handleDropdownSelectEnd}
                                disabled={isCalendarDisabled}
                              />
                            </div>
                          </UIUnitGroup>
                        </div>
                      </UIUnitGroup>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ width: '128px' }}>
              <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                조회
              </UIButton2>
            </div>
          </UIGroup>
        </UIBox>
      </UIArticle>

      <UIArticle className='article-grid'>
        <div className='article-body'>
          <div className='card-default'>
            <div className='card-list-wrapper'>
              <div className='card-list'>
                <UIGroup direction='column' gap={6} vAlign='center'>
                  <UITypography variant='body-1' className='secondary-neutral-500'>
                    전체 호출
                  </UITypography>
                  <UITypography variant='title-3' className='primary-800'>
                    {aggregatedStats.totalCalls}
                  </UITypography>
                </UIGroup>
              </div>
              <div className='card-list'>
                <UIGroup direction='column' gap={6} vAlign='center'>
                  <UITypography variant='body-1' className='secondary-neutral-500'>
                    정상
                  </UITypography>
                  <UITypography variant='title-3' className='secondary-neutral-700'>
                    {aggregatedStats.successCalls}
                  </UITypography>
                </UIGroup>
              </div>
              <div className='card-list'>
                <UIGroup direction='column' gap={6} vAlign='center'>
                  <UITypography variant='body-1' className='secondary-neutral-500'>
                    오류
                  </UITypography>
                  <UITypography variant='title-3' className='semantic-deep-red'>
                    {aggregatedStats.errorCalls}
                  </UITypography>
                </UIGroup>
              </div>
            </div>
          </div>

          <div className='chart-container mt-4'>
            <UIBarChart
              key={`bar-chart-${chartKey}`}
              label='시간별 호출 건수'
              x='시간'
              y='호출 건수(개)'
              options={callCountData.options}
              series={callCountData.series}
              height={368}
            />
          </div>
          <div className='chart-container flex mt-4' style={{ gap: '16px' }}>
            <div style={{ flex: 1, minWidth: 0, overflow: 'hidden' }}>
              <UILineChart
                key={`line-chart-response-${chartKey}`}
                label='평균 응답시간'
                x='시간'
                y='응답시간(ms)'
                options={responseTimeData.options}
                series={responseTimeData.series}
                width='100%'
              />
            </div>
            <div style={{ flex: 1, minWidth: 0, overflow: 'hidden' }}>
              <UILineChart 
                key={`line-chart-success-${chartKey}`} 
                label='호출 성공률' 
                x='시간' 
                y='성공률(%)' 
                options={successRateData.options} 
                series={successRateData.series}
                width='100%'
              />
            </div>
          </div>
        </div>
      </UIArticle>
    </>
  );
};
