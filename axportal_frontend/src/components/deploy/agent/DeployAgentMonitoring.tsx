import { useEffect, useMemo, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

import { UIBox, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIBarChart, UILineChart } from '@/components/UI/molecules/chart';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import type { AgentBuilderDetailRes } from '@/services/agent/builder/types';
import { useGetApiEndpointStatistics } from '@/services/deploy/apigw/apigw.services';
import { useGetInferencePerformance } from '@/services/deploy/model/modelDeploy.services';
import { dateUtils } from '@/utils/common';
import type { ApexOptions } from 'apexcharts';

interface SearchValues {
  searchType: string;
  startDate: string;
  startTime: string;
  endDate: string;
  endTime: string;
  modelName: string;
}

interface DeployAgentMonitoringProps {
  appId?: string;
  targetType?: string;
  agentBuilder?: AgentBuilderDetailRes;
}

export function DeployAgentMonitoring({ appId, targetType, agentBuilder }: DeployAgentMonitoringProps) {
  // 날짜/시간을 API 형식으로 변환하는 함수
  const formatDateTime = (date: string, time: string) => {
    const cleanDate = date.replace(/\./g, ''); // yyyy.MM.dd -> yyyyMMdd
    const cleanTime = time.replace('시', '').padStart(2, '0'); // HH시 -> HH
    return `${cleanDate}${cleanTime}00`; // yyyyMMddHHmm (분은 00으로 고정)
  };

  // 오늘 날짜
  const today = useMemo(() => {
    const now = Date.now();
    const currentHour = parseInt(dateUtils.formatDate(now, 'time').slice(0, 2), 10);
    const nextHour = (currentHour + 1) % 24;
    const isNextDay = currentHour === 23;

    return {
      origin: now,
      date: isNextDay
        ? dateUtils.formatDate(dateUtils.addToDate(now, 1, 'days'), 'custom', { pattern: 'yyyy.MM.dd' })
        : dateUtils.formatDate(now, 'custom', { pattern: 'yyyy.MM.dd' }),
      time: nextHour.toString().padStart(2, '0'),
      endTime: nextHour.toString().padStart(2, '0'),
      endDate: isNextDay
        ? dateUtils.formatDate(dateUtils.addToDate(now, 1, 'days'), 'custom', { pattern: 'yyyy.MM.dd' })
        : dateUtils.formatDate(now, 'custom', { pattern: 'yyyy.MM.dd' }),
    };
  }, []);

  // 초기 모델 선택: serving_name이 있는 첫 번째 노드의 serving_name 사용
  const getInitialModelName = () => {
    if (agentBuilder?.nodes) {
      const firstModelNode = agentBuilder.nodes.find(node => node.data?.serving_name);
      return firstModelNode?.data?.serving_name || '';
    }
    return '';
  };

  const [filters, setFilters] = useState<SearchValues>({
    searchType: '1DAY',
    startDate: dateUtils.formatDate(dateUtils.addToDate(today.origin, -1, 'days'), 'custom', {
      pattern: 'yyyy.MM.dd',
    }),
    startTime: `${today.time}시`,
    endTime: `${today.endTime}시`,
    endDate: today.endDate,
    modelName: getInitialModelName(), // serving_name이 있는 첫 번째 노드의 serving_name 사용
  });

  // 날짜 비교를 위한 헬퍼 함수 (yyyy.MM.dd 형식)
  const compareDates = (date1: string, date2: string): number => {
    const d1 = new Date(date1.replace(/\./g, '-'));
    const d2 = new Date(date2.replace(/\./g, '-'));
    return d1.getTime() - d2.getTime();
  };

  // 조회 조건
  const updateFilters = (newFilters: Partial<SearchValues>) => {
    setFilters(prev => ({ ...prev, ...newFilters }));
  };

  // 에이전트
  const { data, refetch } = useGetApiEndpointStatistics({
    id: appId || '',
    startDate: formatDateTime(filters.startDate, filters.startTime),
    endDate: formatDateTime(filters.endDate, filters.endTime),
  });

  const callData = useMemo(() => {
    return {
      totalCount: data?.reduce((acc, item) => acc + item.totalCount, 0),
      succCount: data?.reduce((acc, item) => acc + item.succCount, 0),
      failCount: data?.reduce((acc, item) => acc + item.failCount, 0),
    };
  }, [data]);

  // 모델
  const modelOptions = useMemo(() => {
    return [
      ...(agentBuilder?.nodes ?? [])
        .filter(node => node.data?.serving_name) // serving_name이 있는 노드만 필터링
        .map(node => ({
          value: node.data?.serving_name || '',
          label: node.data?.serving_name || '',
        })),
    ];
  }, [agentBuilder?.nodes]);

  // agentBuilder가 로드되고 modelOptions가 변경될 때 초기 모델 설정
  useEffect(() => {
    if (agentBuilder?.nodes && modelOptions.length > 0 && !filters.modelName) {
      const firstModelValue = modelOptions[0]?.value;
      if (firstModelValue) {
        updateFilters({ modelName: firstModelValue });
      }
    }
  }, [agentBuilder?.nodes, modelOptions]);

  // 추론 성능 데이터 조회
  const { data: inferencePerformanceData, refetch: refetchInferencePerformance } = useGetInferencePerformance(
    {
      modelName: filters.modelName,
      startDate: formatDateTime(filters.startDate, filters.startTime),
      endDate: formatDateTime(filters.endDate, filters.endTime),
    },
    {
      enabled: !!filters.modelName && filters.modelName.trim() !== '',
    }
  );

  // 시간대별 데이터를 완전한 시간 범위로 채우는 함수
  const processChartData = useMemo(() => {
    // 실제 데이터와 매핑
    const chartData = data ?? [];

    // 카테고리 생성 (시간 표시용)
    const categories = chartData.map(item => {
      return `${item.hour}`;
    });

    return { chartData, categories };
  }, [data]);

  // 24 시간 옵션
  const timeOptions = useMemo(
    () =>
      Array.from({ length: 24 }, (_, index) => ({
        value: `${index.toString().padStart(2, '0')}시`,
        label: `${index.toString().padStart(2, '0')}시`,
      })),
    []
  );

  // 조회 조건 변경 핸들러
  const handleSearchTypeChange = (searchType: string) => {
    if (searchType === '1DAY') {
      updateFilters({
        searchType,
        startDate: dateUtils.formatDate(dateUtils.addToDate(today.origin, -1, 'days'), 'custom', {
          pattern: 'yyyy.MM.dd',
        }),
        endDate: today.endDate,
      });
    } else if (searchType === '2DAYS') {
      updateFilters({
        searchType,
        startDate: dateUtils.formatDate(dateUtils.addToDate(today.origin, -2, 'days'), 'custom', {
          pattern: 'yyyy.MM.dd',
        }),
        endDate: today.endDate,
      });
    } else if (searchType === '3DAYS') {
      updateFilters({
        searchType,
        startDate: dateUtils.formatDate(dateUtils.addToDate(today.origin, -3, 'days'), 'custom', {
          pattern: 'yyyy.MM.dd',
        }),
        endDate: today.endDate,
      });
    } else {
      // CUSTOM인 경우 searchType만 변경
      updateFilters({ searchType });
    }
  };

  // 조회 버튼 핸들러
  const handleSearch = () => {
    refetch();
    if (filters.modelName && filters.modelName.trim() !== '') {
      refetchInferencePerformance();
    }
  };

  // 호출 건수 차트 상태
  const timeCallCountChartState = useMemo(
    () => ({
      series: [
        {
          name: '호출건수',
          data:
            processChartData.chartData?.map(item => {
              const totalCount = item.totalCount || 0;
              return isNaN(totalCount) ? 0 : totalCount;
            }) || [],
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
          categories: processChartData.categories || [],
        },
      } as ApexOptions,
    }),
    [processChartData]
  );

  const averageResponseTimeChartState = useMemo(
    () => ({
      series: [
        {
          name: '평균 응답시간',
          data:
            processChartData.chartData?.map(item => {
              const responseTime = item.resMiliSec || 0;
              const responseTimeInSeconds = responseTime / 1000; // 밀리초를 초로 변환
              return isNaN(responseTimeInSeconds) ? 0 : Number(responseTimeInSeconds.toFixed(2));
            }) || [],
        },
      ],
      options: {
        chart: {
          width: '100%',
        },
        xaxis: {
          type: 'category',
          categories: processChartData.categories || [],
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
    }),
    [processChartData]
  );

  // 호출 성공률
  const successRateChartState = useMemo(
    () => ({
      series: [
        {
          name: '호출 성공률',
          data:
            processChartData.chartData?.map(item => {
              if (item.totalCount === 0) return 0;
              const successRate = (item.succCount / item.totalCount) * 100;
              return isNaN(successRate) ? 0 : Number(successRate.toFixed(2));
            }) || [],
        },
      ],
      options: {
        xaxis: {
          type: 'category',
          categories: processChartData.categories || [],
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
    }),
    [processChartData]
  );

  // timestamp를 시간 형식으로 변환하는 헬퍼 함수
  const formatTimestamp = (timestamp: number): string => {
    const date = new Date(timestamp * 1000); // Unix timestamp는 초 단위이므로 1000을 곱함
    const hours = date.getHours().toString().padStart(2, '0');
    return hours;
  };

  // Time To First Token 차트 데이터 변환
  const lineChartStateSmall1 = useMemo(() => {
    const timeSeriesData = inferencePerformanceData?.timeToFirstToken?.timeSeries || [];

    // timestamp를 시간 형식으로 변환하여 카테고리 생성
    const categories = timeSeriesData.map(item => formatTimestamp(item.timestamp));

    // value는 초 단위이므로 ms로 변환 (Average 값)
    const data = timeSeriesData.map(item => {
      const valueInMs = item.value * 1000; // 초를 밀리초로 변환
      return isNaN(valueInMs) ? 0 : Number(valueInMs.toFixed(2));
    });

    return {
      series: [
        {
          name: 'Time To First Token',
          data,
        },
      ],
      options: {
        chart: {
          width: '100%',
        },
        xaxis: {
          type: 'category',
          categories,
        },
        stroke: {
          width: 2,
          curve: 'smooth',
          colors: ['#B3D8FF'],
        },
        legend: {
          show: false,
        },
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
        colors: ['#B3D8FF'],
      } as ApexOptions,
    };
  }, [inferencePerformanceData]);

  // Time Per Output Token 차트 데이터 변환
  const lineChartStateSmall2 = useMemo(() => {
    const timeSeriesData = inferencePerformanceData?.timePerOutputToken?.timeSeries || [];

    // timestamp를 시간 형식으로 변환하여 카테고리 생성
    const categories = timeSeriesData.map(item => formatTimestamp(item.timestamp));

    // value는 초 단위이므로 ms로 변환 (Mean 값)
    const data = timeSeriesData.map(item => {
      const valueInMs = item.value * 1000; // 초를 밀리초로 변환
      return isNaN(valueInMs) ? 0 : Number(valueInMs.toFixed(3));
    });

    return {
      series: [
        {
          name: 'Time Per Output Token',
          data,
        },
      ],
      options: {
        chart: {
          width: '100%',
        },
        xaxis: {
          type: 'category',
          categories,
        },
        stroke: {
          width: 2,
          curve: 'smooth',
          colors: ['#DFCDF5'],
        },
        legend: {
          show: false,
        },
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
        colors: ['#DFCDF5'],
      } as ApexOptions,
    };
  }, [inferencePerformanceData]);

  // End-to-End Request Latency 차트 데이터 변환
  const lineChartStateSmall3 = useMemo(() => {
    const timeSeriesData = inferencePerformanceData?.endToEndLatency?.timeSeries || [];

    // timestamp를 시간 형식으로 변환하여 카테고리 생성
    const categories = timeSeriesData.map(item => formatTimestamp(item.timestamp));

    // value는 초 단위로 유지 (Average 값)
    const data = timeSeriesData.map(item => {
      return isNaN(item.value) ? 0 : Number(item.value.toFixed(2));
    });

    return {
      series: [
        {
          name: '응답 지연 시간',
          data,
        },
      ],
      options: {
        chart: {
          width: '100%',
        },
        xaxis: {
          type: 'category',
          categories,
        },
        stroke: {
          width: 2,
          curve: 'smooth',
          colors: ['#FFD8B3'],
        },
        legend: {
          show: false,
        },
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
        colors: ['#FFD8B3'],
      } as ApexOptions,
    };
  }, [inferencePerformanceData]);

  return (
    <>
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
                        조회조건
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UIUnitGroup gap={32} direction='row'>
                        <div style={{ width: '500px' }}>
                          <UIDropdown
                            value={filters.searchType}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '1DAY', label: '최근 24시간' },
                              { value: '2DAYS', label: '최근 48시간' },
                              { value: '3DAYS', label: '최근 72시간' },
                              { value: 'CUSTOM', label: '사용자 지정' },
                            ]}
                            onSelect={value => handleSearchTypeChange(value)}
                          />
                        </div>
                        <div className='flex-1'>
                          <UIUnitGroup gap={8} direction='row' vAlign='center'>
                            <div className='flex-1'>
                              <UIInput.Date
                                value={filters.startDate}
                                disabled={filters.searchType !== 'CUSTOM'}
                                onChange={e => {
                                  const newStartDate = e.target.value;
                                  // 시작일자가 종료일자보다 이후이면 종료일자를 시작일자와 동일하게 설정
                                  if (compareDates(newStartDate, filters.endDate) > 0) {
                                    setFilters(prev => ({ ...prev, startDate: newStartDate, endDate: newStartDate }));
                                  } else {
                                    setFilters(prev => ({ ...prev, startDate: newStartDate }));
                                  }
                                }}
                              />
                            </div>
                            <div className='w-[100px]'>
                              <UIDropdown
                                value={filters.startTime}
                                disabled={filters.searchType !== 'CUSTOM'}
                                placeholder='시간'
                                options={timeOptions}
                                onSelect={value => updateFilters({ startTime: value })}
                              />
                            </div>
                            <UITypography variant='body-1' className='secondary-neutral-p w-[28px] justify-center'>
                              ~
                            </UITypography>
                            <div className='flex-1'>
                              <UIInput.Date
                                value={filters.endDate}
                                disabled={filters.searchType !== 'CUSTOM'}
                                onChange={e => {
                                  const newEndDate = e.target.value;
                                  // 종료일자가 시작일자보다 이전이면 시작일자를 종료일자와 동일하게 설정
                                  if (compareDates(filters.startDate, newEndDate) > 0) {
                                    setFilters(prev => ({ ...prev, startDate: newEndDate, endDate: newEndDate }));
                                  } else {
                                    setFilters(prev => ({ ...prev, endDate: newEndDate }));
                                  }
                                }}
                              />
                            </div>
                            <div className='w-[100px]'>
                              <UIDropdown
                                value={filters.endTime}
                                disabled={filters.searchType !== 'CUSTOM'}
                                placeholder='시간'
                                options={timeOptions}
                                onSelect={value => updateFilters({ endTime: value })}
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
              <Button className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                조회
              </Button>
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
                    {callData.totalCount}
                  </UITypography>
                </UIGroup>
              </div>
              <div className='card-list'>
                <UIGroup direction='column' gap={6} vAlign='center'>
                  <UITypography variant='body-1' className='secondary-neutral-500'>
                    정상
                  </UITypography>
                  <UITypography variant='title-3' className='secondary-neutral-700'>
                    {callData.succCount}
                  </UITypography>
                </UIGroup>
              </div>
              <div className='card-list'>
                <UIGroup direction='column' gap={6} vAlign='center'>
                  <UITypography variant='body-1' className='secondary-neutral-500'>
                    오류
                  </UITypography>
                  <UITypography variant='title-3' className='semantic-deep-red'>
                    {callData.failCount}
                  </UITypography>
                </UIGroup>
              </div>
            </div>
          </div>
          <div className='chart-container mt-4'>
            <UIBarChart label='시간별 호출건수' x='시간 (ms)' y='요청수(개)' options={timeCallCountChartState.options} series={timeCallCountChartState.series} height={368} />
          </div>
          <div className='chart-container flex mt-4'>
            <UILineChart label='평균 응답시간' x='시간' y='초(s)' options={averageResponseTimeChartState.options} series={averageResponseTimeChartState.series} />
            <UILineChart label='성공률' x='시간' y='건수' options={successRateChartState.options} series={successRateChartState.series} />
          </div>
        </div>
      </UIArticle>

      {/* graph 타입이 아닌 경우 모델별 추론 성능 차트 표시 제외*/}
      {modelOptions.length > 0 && targetType !== 'external_graph' && (
        <UIArticle>
          <div className='mb-4'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              모델별 추론 성능
            </UITypography>
            <div className='flex mt-2'>
              <UIDropdown
                value={filters.modelName}
                placeholder='모델별 추론 성능 선택'
                options={modelOptions}
                onSelect={value => updateFilters({ modelName: value })}
                className='basis-[320px]'
              />
            </div>
          </div>
          <div className='article-body'>
            <div className='chart-container'>
              <UILineChart label='Time To First Token' x='추론 시각' y='소요 시간 (ms)' options={lineChartStateSmall1.options} series={lineChartStateSmall1.series} height={292} />
              <UILineChart
                label='Time Per Output Token'
                x='추론 시각'
                y='소요 시간 (ms)'
                options={lineChartStateSmall2.options}
                series={lineChartStateSmall2.series}
                height={292}
              />
              <UILineChart label='응답 지연 시간' x='추론 시각' y='소요 시간 (s)' options={lineChartStateSmall3.options} series={lineChartStateSmall3.series} height={292} />
            </div>
          </div>
        </UIArticle>
      )}
    </>
  );
}
