import type { CSSProperties } from 'react';
import { useMemo, useState } from 'react';

import { UIBox, UIButton2, UITypography } from '@/components/UI/atoms';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UICircleChart, UILineChart } from '@/components/UI/molecules/chart';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { routeConfig } from '@/routes/route.config';
import { useGetCommonProjects, useGetUserActivityStats } from '@/services/admin/UserUsageMgmt/UserUsageMgmt.service';
import { useModal } from '@/stores/common/modal';
import { generateBreadcrumb } from '@/utils/common/breadcrumb.utils';
import type { ApexOptions } from 'apexcharts';
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

const normalizeMenuPath = (menuPath: string): string => {
  let normalized = menuPath?.trim() ?? '';
  if (!normalized) return '';

  if (/^https?:\/\//i.test(normalized)) {
    try {
      normalized = new URL(normalized).pathname;
    } catch {
      // URL 파싱 실패 시 원본 경로를 그대로 사용
    }
  }

  normalized = normalized.split(/[?#]/)[0];

  if (!normalized.startsWith('/')) {
    normalized = `/${normalized.replace(/^\/+/, '')}`;
  }

  normalized = normalized.replace(/\/{2,}/g, '/');

  return normalized;
};

const formatMenuPathLabel = (menuPath?: string | null): string => {
  if (!menuPath) return '-';
  if (menuPath.includes('>')) {
    const parts = menuPath.split('>').map(p => p.trim());
    return parts.slice(0, 2).join(' > ');
  }

  const normalizedPath = normalizeMenuPath(menuPath);
  if (!normalizedPath) return menuPath;

  if (normalizedPath === '/login') {
    return '로그인';
  }

  if (!normalizedPath) return menuPath;

  const breadcrumb = generateBreadcrumb(normalizedPath, routeConfig);
  if (!breadcrumb.length) return menuPath;

  return breadcrumb.slice(0, 2).join(' > ');
};

export const UserUsageMgmtStatsPage = () => {
  const { openAlert } = useModal();
  const showAlert = (message: string) => {
    openAlert({
      message: message,
      title: '안내',
      confirmText: '확인',
    });
  };

  const { data: projectsData } = useGetCommonProjects({
    enabled: true,
  });

  const projectOptions = useMemo(() => {
    const defaultOption = { value: 'ALL', label: '전체' };

    if (!projectsData || projectsData.length === 0) {
      return [defaultOption];
    }

    const projectList = projectsData.map(project => ({
      value: project.prjNm,
      label: project.prjNm,
    }));

    return [defaultOption, ...projectList];
  }, [projectsData]);

  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: {},
    searchType: 'day',
    projectType: 'ALL',
    searchKeyword: '',
    status: '전체',
    agentType: '전체',
    startTime: '',
    endTime: '',
  });

  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    projectType: false,
    status: false,
    agentType: false,
  });

  const getCurrentDate = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const getInitialDateValue = () => {
    return getCurrentDate().replace(/-/g, '.');
  };

  const normalizeDateValue = (value?: string) => {
    if (!value) return '';
    return value.includes('-') ? value.replace(/-/g, '.') : value;
  };

  const addDaysToDate = (dateStr: string, days: number) => {
    const normalized = normalizeDateValue(dateStr);
    if (!normalized) return '';

    const [year, month, day] = normalized.split('.').map(Number);
    if ([year, month, day].some(num => Number.isNaN(num))) {
      return normalized;
    }

    const date = new Date(year, (month ?? 1) - 1, day ?? 1);
    date.setDate(date.getDate() + days);

    const newYear = date.getFullYear();
    const newMonth = String(date.getMonth() + 1).padStart(2, '0');
    const newDay = String(date.getDate()).padStart(2, '0');

    return `${newYear}.${newMonth}.${newDay}`;
  };

  const toApiDate = (value: string, defaultDay?: string) => {
    if (!value) return '';
    const normalized = value.replace(/\./g, '-');
    if (defaultDay && normalized.length === 7) {
      return `${normalized}-${defaultDay}`;
    }
    return normalized;
  };

  const initialFullDate = getInitialDateValue();
  const initialMonthValue = initialFullDate.split('.').slice(0, 2).join('.');

  const [dateValueStart, setDateValueStart] = useState(initialFullDate);
  const [monthValue, setMonthValue] = useState(initialMonthValue);
  const [weekStartDate, setWeekStartDate] = useState(addDaysToDate(initialFullDate, -7)); // 일주일 전
  const [weekEndDate, setWeekEndDate] = useState(initialFullDate); // 오늘
  const [dayValue, setDayValue] = useState(initialFullDate);
  const [activeWeekRange, setActiveWeekRange] = useState<{ start: string; end: string } | null>(null);

  const [activeSearchType, setActiveSearchType] = useState('day');
  const [graphXlabel, setGraphXlabel] = useState('시간');

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

  const handleWeekStartChange = (value: string) => {
    const normalized = normalizeDateValue(value);
    setWeekStartDate(normalized);
    if (normalized) {
      setWeekEndDate(addDaysToDate(normalized, 7));
    } else {
      setWeekEndDate('');
    }
  };

  const handleWeekEndChange = (value: string) => {
    const normalized = normalizeDateValue(value);
    setWeekEndDate(normalized);
    if (normalized) {
      setWeekStartDate(addDaysToDate(normalized, -7));
    } else {
      setWeekStartDate('');
    }
  };

  const handleDayChange = (value: string) => {
    setDayValue(normalizeDateValue(value));
  };

  const generateMonthlyRange = (selectedMonth: string) => {
    const parts = selectedMonth.split('.');
    const year = Number(parts[0]);
    const month = Number(parts[1]);

    if (isNaN(year) || isNaN(month)) {
      const now = new Date();
      const endDate = new Date(now.getFullYear(), now.getMonth());
      const months = [];

      for (let i = 11; i >= 0; i--) {
        const date = new Date(endDate.getFullYear(), endDate.getMonth() - i);
        const monthStr = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}`;
        months.push(monthStr);
      }
      return months;
    }

    const endDate = new Date(year, month - 1);
    const months = [];

    for (let i = 11; i >= 0; i--) {
      const date = new Date(endDate.getFullYear(), endDate.getMonth() - i);
      const monthStr = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}`;
      months.push(monthStr);
    }

    return months;
  };

  const getDateRangeString = () => {
    if (!dateValueStart) return '';

    const formatDate = (date: Date) => {
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      return `${y}.${m}.${d}`;
    };

    if (activeSearchType === 'month') {
      const parts = dateValueStart.split('.');
      const year = Number(parts[0]);
      const month = Number(parts[1]);
      if (isNaN(year) || isNaN(month)) {
        return '';
      }
      const startDate = new Date(year, month - 1, 1);

      // 현재 날짜 확인
      const now = new Date();
      const currentYear = now.getFullYear();
      const currentMonth = now.getMonth() + 1;
      const currentDay = now.getDate();

      // 선택된 월이 현재 년/월과 같은 경우 오늘 날짜까지 표시
      let endDate: Date;
      if (year === currentYear && month === currentMonth) {
        endDate = new Date(year, month - 1, currentDay);
      } else {
        endDate = new Date(year, month, 0);
      }

      return `${formatDate(startDate)} - ${formatDate(endDate)} 기준`;
    }

    if (activeSearchType === 'week') {
      if (!activeWeekRange?.start || !activeWeekRange?.end) {
        return '';
      }

      const normStart = normalizeDateValue(activeWeekRange.start);
      const normEnd = normalizeDateValue(activeWeekRange.end);
      if (!normStart || !normEnd) {
        return '';
      }

      const [startY, startM, startD] = normStart.split('.').map(Number);
      const [endY, endM, endD] = normEnd.split('.').map(Number);
      if ([startY, startM, startD, endY, endM, endD].some(num => Number.isNaN(num))) {
        return '';
      }

      const startDate = new Date(startY, startM - 1, startD);
      const endDate = new Date(endY, endM - 1, endD);
      return `${formatDate(startDate)} - ${formatDate(endDate)} 기준`;
    }

    if (activeSearchType === 'day') {
      let year, month, day;

      if (dateValueStart.includes('-')) {
        [year, month, day] = dateValueStart.split('-').map(Number);
      } else {
        [year, month, day] = dateValueStart.split('.').map(Number);
      }

      if (isNaN(year) || isNaN(month) || isNaN(day)) {
        return '';
      }
      const selectedDate = new Date(year, month - 1, day);
      return `${formatDate(selectedDate)} 기준`;
    }

    return '';
  };

  const [statsParams, setStatsParams] = useState<{
    searchType: string;
    selectedDate?: string;
    projectType: string;
    startDate?: string;
    endDate?: string;
  }>(() => ({
    searchType: 'day',
    selectedDate: toApiDate(initialFullDate),
    projectType: 'ALL',
  }));

  const { data: statsData, refetch: refetchStats } = useGetUserActivityStats(statsParams, {
    enabled: true,
  });

  const chartCategories = useMemo(() => {


    if (activeSearchType === 'month') {
      setGraphXlabel('최근 12개월');
    } else if (activeSearchType === 'week') {
      setGraphXlabel('최근 7일');
    } else if (activeSearchType === 'day') {
      setGraphXlabel('최근 12일');
    }
    
    if (activeSearchType === 'week') {
      if (!activeWeekRange?.start || !activeWeekRange?.end) {
        return [];
      }

      const normStart = normalizeDateValue(activeWeekRange.start);
      const normEnd = normalizeDateValue(activeWeekRange.end);
      if (!normStart || !normEnd) return [];

      const [startY, startM, startD] = normStart.split('.').map(Number);
      const [endY, endM, endD] = normEnd.split('.').map(Number);
      if ([startY, startM, startD, endY, endM, endD].some(num => Number.isNaN(num))) {
        return [];
      }

      let startDate = new Date(startY, startM - 1, startD);
      let endDate = new Date(endY, endM - 1, endD);

      if (startDate > endDate) {
        const temp = startDate;
        startDate = endDate;
        endDate = temp;
      }

      const categories: string[] = [];
      const current = new Date(startDate);

      while (current <= endDate) {
        const monthStr = String(current.getMonth() + 1).padStart(2, '0');
        const dayStr = String(current.getDate()).padStart(2, '0');
        categories.push(`${monthStr}.${dayStr}`);
        current.setDate(current.getDate() + 1);
      }

      return categories;
    } else if (activeSearchType === 'day') {
      let year, month, day;

      if (dateValueStart.includes('-')) {
        [year, month, day] = dateValueStart.split('-').map(Number);
      } else {
        [year, month, day] = dateValueStart.split('.').map(Number);
      }

      if (isNaN(year) || isNaN(month) || isNaN(day)) {
        return [];
      }

      const endDate = new Date(year, month - 1, day);
      const categories = [];

      for (let i = 11; i >= 0; i--) {
        const date = new Date(endDate);
        date.setDate(endDate.getDate() - i);
        const monthStr = String(date.getMonth() + 1).padStart(2, '0');
        const dayStr = String(date.getDate()).padStart(2, '0');
        categories.push(`${monthStr}.${dayStr}`);
      }

      return categories;
    } else {
      const categories = generateMonthlyRange(dateValueStart).map(month => month.split('.')[1]);
      return categories;
    }
  }, [dateValueStart, activeSearchType]);

  const chartData = useMemo(() => {
    if (statsData?.loginSuccessCounts && statsData.loginSuccessCounts.length > 0) {
      const sortedData = chartCategories.map(category => {
        let matchingItem;

        if (activeSearchType === 'month') {
          matchingItem = statsData.loginSuccessCounts?.find((item: any) => {
            const itemMonth = item.month?.split('-')[1];
            return itemMonth === category;
          });
        } else if (activeSearchType === 'week') {
          matchingItem = statsData.loginSuccessCounts?.find((item: any) => {
            if (item['start-date']) {
              const [, month, day] = item['start-date'].split('-');
              const itemDate = `${month}.${day}`;
              return itemDate === category;
            } else if (item.week) {
              const [, month, day] = item.week.split('-');
              const itemDate = `${month}.${day}`;
              return itemDate === category;
            } else if (item.day) {
              const [, month, day] = item.day.split('-');
              const itemDate = `${month}.${day}`;
              return itemDate === category;
            } else if (item.date) {
              const [, month, day] = item.date.split('-');
              const itemDate = `${month}.${day}`;
              return itemDate === category;
            } else if (item.month) {
              const [, month] = item.month.split('-');
              const itemDate = `${month}.01`;
              return itemDate === category;
            }
            return false;
          });
        } else if (activeSearchType === 'day') {
          matchingItem = statsData.loginSuccessCounts?.find((item: any) => {
            if (item.day) {
              const [, month, day] = item.day.split('-');
              const itemDate = `${month}.${day}`;
              return itemDate === category;
            } else if (item.date) {
              const [, month, day] = item.date.split('-');
              const itemDate = `${month}.${day}`;
              return itemDate === category;
            } else if (item.month) {
              const [, month, day] = item.month.split('-');
              const itemDate = `${month}.${day}`;
              return itemDate === category;
            }
            return false;
          });
        }

        if (matchingItem) {
          const value = matchingItem.count || matchingItem.visitors || matchingItem.value || 0;
          return value;
        } else {
          return 0;
        }
      });

      const chartData = [
        {
          name: '방문자 수',
          data: sortedData,
          fill: {
            type: 'solid',
            opacity: 0.2,
          },
        },
      ];
      return chartData;
    }

    if (activeSearchType === 'week' || activeSearchType === 'day') {
      return [
        {
          name: '방문자 수',
          data: chartCategories.map(() => 0),
          fill: {
            type: 'solid',
            opacity: 0.2,
          },
        },
      ];
    }

    if (statsData?.totalLoginSuccessCount) {
      return [
        {
          name: '방문자 수',
          data: [statsData.totalLoginSuccessCount],
        },
      ];
    }

    return [
      {
        name: '방문자 수',
        data: [],
      },
    ];
  }, [statsData, chartCategories, activeSearchType]);

  const getYAxisMax = () => {
    if (statsData?.loginSuccessCounts && statsData.loginSuccessCounts.length > 0) {
      const maxValue = Math.max(...statsData.loginSuccessCounts.map(item => item.count || item.visitors || item.value || 0));
      const yAxisMax = Math.ceil(maxValue * 1.2);
      return yAxisMax > 0 ? yAxisMax : 100;
    }
    return 100;
  };


  // colorStops를 searchType에 따라 동적으로 생성
  const getColorStops = () => {
    switch (activeSearchType) {
      case 'week':
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
      case 'day':
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
      case 'month':
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
    switch (activeSearchType) {
      case 'week':
        return ['#FFAB08'];
      case 'day':
        return ['#8166D2'];
      case 'month':
      default:
        return ['#005DF9'];
    }
  };

  const chartOptions = useMemo((): ApexOptions => {
    const yAxisMax = getYAxisMax();

    return {
      chart: {
        type: 'area',
        height: 160,
        toolbar: { show: false },
        zoom: { enabled: false },
        selection: { enabled: false },
        animations: {
          enabled: true,
          speed: 800,
        },
      },
      stroke: {
        width: 2,
        curve: 'smooth',
      },
      colors: getLineColor(),
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          colorStops: getColorStops(),
        },
      },
      markers: {
        size: 0,
        hover: {
          size: 0,
        },
      },
      grid: {
        show: true,
        borderColor: '#F3F4F6',
      },
      xaxis: {
        categories: chartCategories,
        labels: {
          style: { fontSize: '10px' },
        },
      },
      yaxis: {
        labels: {
          style: { fontSize: '10px' },
          formatter: (value: number) => (Number.isInteger(value) ? value.toString() : value.toFixed(0)),
        },
        min: 0,
        max: yAxisMax,
      },
      dataLabels: {
        enabled: true,
        style: {
          fontSize: '10px',
          fontWeight: 'bold',
          colors: getLineColor(),
        },
        offsetY: -10,
        formatter: function (val: any) {
          return val > 0 ? val : '';
        },
      },
      legend: { show: false },
      tooltip: {
        enabled: true,
        shared: true,
        followCursor: false,
        intersect: false,
        inverseOrder: false,
        custom: undefined,
        fillSeriesColor: false,
        theme: 'light',
      },
    };
  }, [chartCategories, statsData, activeSearchType]);

  const handleSearch = () => {
    if (!searchValues.projectType) {
      showAlert('프로젝트를 선택해주세요.');
      return;
    }

    if (!searchValues.searchType || searchValues.searchType.trim() === '') {
      showAlert('조회기간을 선택해주세요.');
      return;
    }

    let selectedDateValue = '';
    let apiSelectedDate = '';
    let additionalParams: { startDate?: string; endDate?: string } = {};

    if (searchValues.searchType === 'month') {
      if (!monthValue) {
        showAlert('조회할 월을 선택해주세요.');
        return;
      }
      selectedDateValue = monthValue;
      apiSelectedDate = toApiDate(monthValue, '01');
      setActiveWeekRange(null);
    } else if (searchValues.searchType === 'week') {
      const normalizedStart = normalizeDateValue(weekStartDate);
      const normalizedEnd = normalizeDateValue(weekEndDate);

      if (!normalizedStart || !normalizedEnd) {
        showAlert('주별 조회 기간을 선택해주세요.');
        return;
      }

      setActiveWeekRange({ start: normalizedStart, end: normalizedEnd });

      selectedDateValue = normalizedEnd;
      apiSelectedDate = toApiDate(normalizedEnd);
      additionalParams = {
        startDate: toApiDate(normalizedStart),
        endDate: toApiDate(normalizedEnd),
      };
    } else {
      const normalizedDay = normalizeDateValue(dayValue);
      if (!normalizedDay) {
        showAlert('일별 조회 날짜를 선택해주세요.');
        return;
      }
      selectedDateValue = normalizedDay;
      apiSelectedDate = toApiDate(normalizedDay);
      setActiveWeekRange(null);
    }

    setDateValueStart(selectedDateValue);
    setActiveSearchType(searchValues.searchType);

    const params = {
      searchType: searchValues.searchType,
      selectedDate: apiSelectedDate,
      projectType: searchValues.projectType,
      ...additionalParams,
    };

    setStatsParams(params);
    refetchStats();
  };

  const topUsedMenuList = useMemo(() => {
    const menuData = statsData?.topUsedMenus;
    if (!menuData || !Array.isArray(menuData) || menuData.length === 0) {
      return [];
    }

    const maxCount = Math.max(...menuData.map((item: any) => item.count || 0));

    return menuData.map((item: any) => {
      const parsedLabel = formatMenuPathLabel(item.menu);
      let label = parsedLabel && parsedLabel !== '-' ? parsedLabel : item.menu || '알 수 없음';

      if (label.includes('>')) {
        const parts = label.split('>').map((part: string) => part.trim());
        label = parts[parts.length - 1] || parts[0] || '알 수 없음';
      }

      const count = item.count || 0;
      const percentage = maxCount > 0 ? Math.round((count / maxCount) * 100) : 0;

      return {
        label,
        count,
        percentage,
      };
    });
  }, [statsData?.topUsedMenus]);

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
                        조회 기간
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UIUnitGroup gap={32} direction='row'>
                        <div className='flex-1'>
                          <UIDropdown
                            placeholder='조회조건 선택'
                            value={searchValues.searchType}
                            options={[
                              { value: 'day', label: '일별' },
                              { value: 'week', label: '주별' },
                              { value: 'month', label: '월별' },
                            ]}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={value => handleDropdownSelect('searchType', value)}
                          />
                        </div>
                        <div className='flex-1'>
                          {searchValues.searchType === 'month' && (
                            <UIInput.Date type='MONTH' value={monthValue} onChange={e => setMonthValue(e.target.value)} placeholder='월별 조회' />
                          )}
                          {searchValues.searchType === 'week' && (
                            <UIUnitGroup gap={8} direction='row' vAlign='center'>
                              <div className='flex-1 date-weekly-first'>
                                <UIInput.Date
                                  value={weekStartDate}
                                  onChange={e => handleWeekStartChange(e.target.value)}
                                  placeholder='시작일'
                                  dateType='date-weekly-first'
                                  highlightDays={{ after: 7 }}
                                />
                              </div>
                              <UITypography variant='body-1' className='secondary-neutral-p w-[16px] text-center'>
                                ~
                              </UITypography>
                              <div className='flex-1 date-weekly-last'>
                                <UIInput.Date
                                  value={weekEndDate}
                                  onChange={e => handleWeekEndChange(e.target.value)}
                                  placeholder='종료일'
                                  dateType='date-weekly-last'
                                  highlightDays={{ before: 7 }}
                                />
                              </div>
                            </UIUnitGroup>
                          )}
                          {searchValues.searchType === 'day' && <UIInput.Date value={dayValue} onChange={e => handleDayChange(e.target.value)} placeholder='일별 조회' />}
                        </div>
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
                            placeholder='프로젝트 선택'
                            options={projectOptions}
                            isOpen={dropdownStates.projectType}
                            onClick={() => handleDropdownToggle('projectType')}
                            onSelect={value => handleDropdownSelect('projectType', value)}
                          />
                        </div>
                        <div className='flex-1'></div>
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
          <div className='chart-container' style={{ width: '100%', overflow: 'hidden', maxWidth: '100%' }}>
            <div style={{ width: '100%', overflow: 'hidden' }}>
              <UILineChart
                type='straight'
                width='100%'
                label={
                <div className='flex items-center'>
                  <UITypography variant='title-4' className='mr-1'>
                    프로젝트 방문자 수
                  </UITypography>
                  <UITypography variant='body-1' className='text-blue-800 text-sb'>
                   {statsData?.totalLoginSuccessCount ? statsData.totalLoginSuccessCount.toLocaleString() : '0'}
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
              x={graphXlabel}
              y='방문자 수'
              options={chartOptions}
              series={chartData}
            />
            </div>
          </div>

          <div className='chart-container mt-4 min-h-[300px]'>
            <div className='flex-[0_0_630px] h-full'>
              <UICircleChart.Full
                label='API 호출 성공/실패율'
                value={statsData?.apiSuccessRate || 0}
                total={100}
                dateRange={getDateRangeString()}
                successValue={statsData?.apiSuccessCount}
                failValue={statsData?.apiFailureCount}
              />
            </div>

            <div className='flex-1 h-full min-h-[312px]'>
              <div className='chart-item h-full min-h-[312px]'>
                <div className='chart-header mb-4 flex justify-between '>
                  <div className='flex items-center gap-4'>
                    <div className='chart-title'>
                      <UITypography variant='title-3' className='text-title-3'>
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
                  <div className='text-body-2 ml-auto text-[#8B95A9]'>{getDateRangeString()}</div>
                </div>

                {topUsedMenuList.length > 0 ? (
                  <div className='horizontal-chart flex-1 overflow-y-auto pr-2'>
                    {topUsedMenuList.map((item, index) => (
                      <div className='item' key={`top-menu-${item.label}-${index}`}>
                        <UITypography variant='body-1' className='label secondary-neutral-600'>
                          {item.label}
                        </UITypography>
                        <div className='progress'>
                          <span className='bar' style={{ '--target-width': `${item.percentage}%` } as CSSProperties}></span>
                          <span className='value'>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {item.count.toLocaleString()}
                            </UITypography>
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
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
            </div>
          </div>
        </div>
      </UIArticle>
    </>
  );
};
