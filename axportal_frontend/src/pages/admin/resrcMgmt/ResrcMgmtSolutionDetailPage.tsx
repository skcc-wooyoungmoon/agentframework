import React, { useMemo, useState, useCallback, useEffect, useRef } from 'react';
import { useAtomValue, useSetAtom } from 'jotai';
import type { ApexOptions } from 'apexcharts';

import { UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIBox, UIButton2, UIDataCnt, UILabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILineChart } from '@/components/UI/molecules/chart';
import { selectedSolutionAtom } from '@/stores/admin/resrcMgmt';
import { useGetSolutionDetail, useGetSolutionInfo } from '@/services/admin/resrcMgmt';

import { useModal } from '@/stores/common/modal';

interface SearchValues {
  searchType: string;
  dateRangeType: string;
}

export const ResrcMgmtSolutionDetailPage = () => {
  const solutionInfo = useAtomValue(selectedSolutionAtom);
  const setSelectedSolution = useSetAtom(selectedSolutionAtom);

  const SOLUTION_STORAGE_KEY = 'selectedSolutionInfo';

  useEffect(() => {
    if (solutionInfo?.namespace) {
      try {
        localStorage.setItem(SOLUTION_STORAGE_KEY, JSON.stringify(solutionInfo));
      } catch {
        // localStorage 저장 실패 시 조용히 무시 (브라우저 설정 또는 용량 제한)
      }
    } else {
      try {
        const stored = localStorage.getItem(SOLUTION_STORAGE_KEY);
        if (stored) {
          const parsed = JSON.parse(stored);
          if (parsed?.namespace) {
            setSelectedSolution(parsed);
          }
        }
      } catch {
        // localStorage 조회 실패 시 조용히 무시 (손상된 데이터 또는 파싱 오류)
      }
    }
  }, [solutionInfo, setSelectedSolution]);

  const { openAlert } = useModal();
  const showAlert = useCallback(
    (message: string) => {
      openAlert({
        message,
        title: '안내',
        confirmText: '확인',
      });
    },
    [openAlert]
  );

  const [cpuUsageRate, setCpuUsageRate] = useState('0.0000');
  const [memoryRequestUsageRate, setMemoryRequestUsageRate] = useState('0.0000');
  const [cpuLimitUsageRate, setCpuLimitUsageRate] = useState('0.0000');
  const [memoryLimitUsageRate, setMemoryLimitUsageRate] = useState('0.0000');

  const [searchValues, setSearchValues] = useState<SearchValues>({
    searchType: '전체',
    dateRangeType: '최근 24시간',
  });

  const [cpuCurrentPage, setCpuCurrentPage] = useState(1);
  const [cpuPageSize, setCpuPageSize] = useState(12);

  const cpuColumnDefs: any = useMemo(
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
        headerName: 'Pod명',
        field: 'podName',
        width: 282,
        flex: 1,
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
        headerName: '할당량',
        field: 'cpuLimits',
        width: 252,
      },
      {
        headerName: '요청량',
        field: 'cpuRequests',
        width: 282,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '실제 사용량',
        field: 'cpuUsage',
        width: 252,
      },
      {
        headerName: '요청량 대비 사용률',
        field: 'cpuRequestUsageRate',
        width: 252,
      },
      {
        headerName: '할당량 대비 사용률',
        field: 'cpuLimitUsageRate',
        width: 252,
      },
    ],
    []
  );

  const [searchValue1, setSearchValue1] = useState('');
  const [appliedSearchValue, setAppliedSearchValue] = useState('');
  const [appliedPodName, setAppliedPodName] = useState('전체');

  const timeOptions = useMemo(() => {
    return Array.from({ length: 24 }, (_, i) => {
      const hour = String(i).padStart(2, '0');
      return { value: `${hour}:00`, label: `${hour}시` };
    });
  }, []);

  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
    dateRangeType: false,
    timeStart: false,
    timeEnd: false,
  });

  const getCurrentDateTime = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    let hours = now.getHours();
    if (now.getMinutes() > 0) {
      hours = Math.min(hours + 1, 23);
    }
    const hoursStr = String(hours).padStart(2, '0');

    return {
      date: `${year}.${month}.${day}`,
      time: `${hoursStr}:00`,
    };
  };

  const getFromDateTime = (hoursAgo: number) => {
    const now = new Date();
    now.setHours(now.getHours() - hoursAgo);
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    let hours = now.getHours();
    if (now.getMinutes() > 0) {
      hours = Math.min(hours + 1, 23);
    }
    const hoursStr = String(hours).padStart(2, '0');

    return {
      date: `${year}.${month}.${day}`,
      time: `${hoursStr}:00`,
    };
  };

  const currentDateTime = getCurrentDateTime();
  const fromDateTime = getFromDateTime(24);

  // 입력용 날짜/시간 (pending)
  const [dateValueStart, setDateValueStart] = useState(fromDateTime.date);
  const [dateValueEnd, setDateValueEnd] = useState(currentDateTime.date);
  const [timeValueStart, setTimeValueStart] = useState(currentDateTime.time);
  const [timeValueEnd, setTimeValueEnd] = useState(currentDateTime.time);
  
  // 날짜 input 강제 리렌더링을 위한 key
  const [dateInputKey, setDateInputKey] = useState(0);

  // 최신 값을 추적하기 위한 ref
  const dateValueStartRef = useRef(dateValueStart);
  const dateValueEndRef = useRef(dateValueEnd);
  const timeValueStartRef = useRef(timeValueStart);
  const timeValueEndRef = useRef(timeValueEnd);

  // state 변경 시 ref도 업데이트
  useEffect(() => {
    dateValueStartRef.current = dateValueStart;
  }, [dateValueStart]);

  useEffect(() => {
    dateValueEndRef.current = dateValueEnd;
  }, [dateValueEnd]);

  useEffect(() => {
    timeValueStartRef.current = timeValueStart;
  }, [timeValueStart]);

  useEffect(() => {
    timeValueEndRef.current = timeValueEnd;
  }, [timeValueEnd]);

  // 실제 API 호출에 사용할 날짜/시간 (active)
  const [activeDateStart, setActiveDateStart] = useState(fromDateTime.date);
  const [activeDateEnd, setActiveDateEnd] = useState(currentDateTime.date);
  const [activeTimeStart, setActiveTimeStart] = useState(currentDateTime.time);
  const [activeTimeEnd, setActiveTimeEnd] = useState(currentDateTime.time);

  const fromDate = `${activeDateStart.replace(/\./g, '-')} ${activeTimeStart}:00`;
  const endDate = `${activeDateEnd.replace(/\./g, '-')} ${activeTimeEnd}:00`;

  // fromDate만 9시간 빼서 UTC로 변환 (한국 시간 → UTC)
  const KST_OFFSET = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
  const fromDateObj = new Date(fromDate);
  const fromDateUTC = new Date(fromDateObj.getTime() - KST_OFFSET);
  
  // UTC 날짜를 문자열 형식으로 변환 (YYYY-MM-DD HH:mm:ss)
  const formatDateTime = (date: Date): string => {
    const year = date.getUTCFullYear();
    const month = String(date.getUTCMonth() + 1).padStart(2, '0');
    const day = String(date.getUTCDate()).padStart(2, '0');
    const hours = String(date.getUTCHours()).padStart(2, '0');
    const minutes = String(date.getUTCMinutes()).padStart(2, '0');
    const seconds = String(date.getUTCSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  };

  const apiParams = {
    fromDateTime: formatDateTime(fromDateUTC),
    toDateTime: endDate,
    namespace: solutionInfo?.namespace || '',
    podName: appliedPodName && appliedPodName !== '전체' ? appliedPodName : '',
  };

  const { data: solutionInfoData, refetch: refetchSolutionInfo } = useGetSolutionInfo({
    fromDate: apiParams.fromDateTime,
    toDate: apiParams.toDateTime,
    nameSpace: apiParams.namespace,
    podName: apiParams.podName,
  }, {
    enabled: false,
  });

  const { data: solutionDetail, refetch } = useGetSolutionDetail(
    {
      fromDate: apiParams.fromDateTime,
      toDate: apiParams.toDateTime,
      nameSpace: apiParams.namespace,
      podName: apiParams.podName,
    },
    {
      enabled: false,
    }
  );

  useEffect(() => {
    if (solutionDetail?.usageRates) {
      setCpuUsageRate(solutionDetail.usageRates.cpuRequestUsageRate.toFixed(1));
      setCpuLimitUsageRate(solutionDetail.usageRates.cpuLimitUsageRate.toFixed(1));
      setMemoryRequestUsageRate(solutionDetail.usageRates.memoryRequestUsageRate.toFixed(1));
      setMemoryLimitUsageRate(solutionDetail.usageRates.memoryLimitUsageRate.toFixed(1));
    }
  }, [solutionDetail]);

  const cpuGridData = useMemo(() => {
    if (!solutionDetail?.podCpuGrid) return [];

    let filteredData = solutionDetail.podCpuGrid;

    // 드롭다운 선택 시 pod 필터링 (조회 버튼 클릭 시 적용)
    if (appliedPodName && appliedPodName !== '전체') {
      filteredData = filteredData.filter((item: any) => item.podName === appliedPodName);
    }

    // 검색 입력 시 클라이언트 사이드 라이크 검색 (조회 버튼/엔터키 입력 시 적용)
    if (appliedSearchValue.trim()) {
      const keyword = appliedSearchValue.trim().toLowerCase();
      filteredData = filteredData.filter((item: any) => 
        item.podName?.toLowerCase().includes(keyword)
      );
    }

    return filteredData.map((item: any, index: number) => ({
      id: String(index + 1),
      podName: item.podName,
      cpuLimits: item.cpuLimits,
      cpuRequests: item.cpuRequests,
      cpuUsage: item.cpuUsage.toFixed(4),
      cpuRequestUsageRate: `${item.cpuRequestUsageRate}%`,
      cpuLimitUsageRate: `${item.cpuLimitUsageRate}%`,
    }));
  }, [solutionDetail?.podCpuGrid, appliedPodName, appliedSearchValue]);

  const paginatedCpuData = useMemo(() => {
    const startIndex = (cpuCurrentPage - 1) * cpuPageSize;
    const endIndex = startIndex + cpuPageSize;
    return cpuGridData.slice(startIndex, endIndex);
  }, [cpuGridData, cpuCurrentPage, cpuPageSize]);

  const cpuTotalPages = Math.max(1, Math.ceil(cpuGridData.length / cpuPageSize));

  const memoryGridData = useMemo(() => {
    if (!solutionDetail?.podMemoryGrid) return [];

    let filteredData = solutionDetail.podMemoryGrid;

    // 드롭다운 선택 시 pod 필터링 (조회 버튼 클릭 시 적용)
    if (appliedPodName && appliedPodName !== '전체') {
      filteredData = filteredData.filter((item: any) => item.podName === appliedPodName);
    }

    // 검색 입력 시 클라이언트 사이드 라이크 검색 (조회 버튼/엔터키 입력 시 적용)
    if (appliedSearchValue.trim()) {
      const keyword = appliedSearchValue.trim().toLowerCase();
      filteredData = filteredData.filter((item: any) => 
        item.podName?.toLowerCase().includes(keyword)
      );
    }

    return filteredData.map((item: any, index: number) => ({
      id: String(index + 1),
      podName: item.podName,
      memoryLimits: item.memoryLimits,
      memoryRequests: item.memoryRequests,
      memoryUsage: item.memoryUsage,
      memoryRequestUsageRate: `${item.memoryRequestUsageRate}%`,
      memoryLimitUsageRate: `${item.memoryLimitUsageRate}%`,
    }));
  }, [solutionDetail?.podMemoryGrid, appliedPodName, appliedSearchValue]);

  const [memoryCurrentPage, setMemoryCurrentPage] = useState(1);
  const [memoryPageSize, setMemoryPageSize] = useState(12);

  const paginatedMemoryData = useMemo(() => {
    const startIndex = (memoryCurrentPage - 1) * memoryPageSize;
    const endIndex = startIndex + memoryPageSize;
    return memoryGridData.slice(startIndex, endIndex);
  }, [memoryGridData, memoryCurrentPage, memoryPageSize]);

  const memoryTotalPages = Math.max(1, Math.ceil(memoryGridData.length / memoryPageSize));

  const memoryColumnDefs: any = useMemo(
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
        headerName: 'Pod명',
        field: 'podName',
        width: 282,
        flex: 1,
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
        headerName: '할당량',
        field: 'memoryLimits',
        width: 252,
        valueFormatter: (params: any) => `${((Number(params.value ?? 0)) / 1024).toFixed(4)} GiB`,
      },
      {
        headerName: '요청량',
        field: 'memoryRequests',
        width: 282,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueFormatter: (params: any) => `${((Number(params.value ?? 0)) / 1024).toFixed(4)} GiB`,
      },
      {
        headerName: '실제 사용량',
        field: 'memoryUsage',
        width: 252,
        valueFormatter: (params: any) => `${((Number(params.value ?? 0)) / 1024).toFixed(4)} GiB`,
      },
      {
        headerName: '요청량 대비 사용률',
        field: 'memoryRequestUsageRate',
        width: 252,
      },
      {
        headerName: '할당량 대비 사용률',
        field: 'memoryLimitUsageRate',
        width: 252,
      },
    ],
    []
  );


  // 10분 간격으로 데이터 샘플링 (성능 개선)
  const sampleDataByMinute = useCallback((dataArray: any[]) => {
    if (!dataArray || dataArray.length === 0) return [];
    if (dataArray.length <= 1) return dataArray;

    const sampled: any[] = [dataArray[0]]; // 첫 번째 포인트는 항상 포함
    const FIVE_MINUTES_MS = 5 * 60 * 1000; 
    let lastTimestamp = dataArray[0][0]; // 첫 번째 타임스탬프

    for (let i = 1; i < dataArray.length; i++) {
      const currentTimestamp = dataArray[i][0];
      // 이전 포인트와의 시간 차이가 5분 이상이면 포함
      if (currentTimestamp - lastTimestamp >= FIVE_MINUTES_MS) {
        sampled.push(dataArray[i]);
        lastTimestamp = currentTimestamp;
      }
    }

    // 마지막 포인트도 항상 포함 (데이터의 끝을 보여주기 위해)
    if (sampled.length > 0 && sampled[sampled.length - 1] !== dataArray[dataArray.length - 1]) {
      sampled.push(dataArray[dataArray.length - 1]);
    }

    return sampled;
  }, []);

  const podOptions = useMemo(() => {
    const options = [{ value: '전체', label: '전체' }];

    if (solutionInfoData?.podNames && Array.isArray(solutionInfoData.podNames)) {
      solutionInfoData.podNames.forEach(item => {
        if (item.pod) {
          options.push({ value: item.pod, label: item.pod });
        }
      });
    }

    return options;
  }, [solutionInfoData?.podNames]);

  const handleFromDateChange = (newDate: string) => {
    const value = newDate.replace(/-/g, '.');

    // ref를 사용하여 최신 값 보장
    const currentEndDate = dateValueEndRef.current;
    const previousStartDate = dateValueStartRef.current;

    // 날짜만 먼저 비교 (UserUsageMgmtHistPage 방식)
    const startStr = value.replace(/\./g, '');
    const endStr = currentEndDate.replace(/\./g, '');

    if (startStr > endStr) {
      // fromDate가 toDate보다 클 경우, 두 날짜를 모두 입력값으로 설정
      setDateValueStart(value);
      setDateValueEnd(value);
      dateValueStartRef.current = value;
      dateValueEndRef.current = value;
      return;
    }

    // 날짜만으로 3일 초과 체크 (날짜 차이 계산)
    // 날짜 문자열을 직접 파싱하여 정확한 날짜 차이 계산
    const fromDateParts = value.split('.');
    const toDateParts = currentEndDate.split('.');
    
    if (fromDateParts.length === 3 && toDateParts.length === 3) {
      const fromYear = parseInt(fromDateParts[0], 10);
      const fromMonth = parseInt(fromDateParts[1], 10) - 1; // 월은 0부터 시작
      const fromDay = parseInt(fromDateParts[2], 10);
      
      const toYear = parseInt(toDateParts[0], 10);
      const toMonth = parseInt(toDateParts[1], 10) - 1;
      const toDay = parseInt(toDateParts[2], 10);
      
      const fromDateObj = new Date(fromYear, fromMonth, fromDay, 0, 0, 0, 0);
      const toDateObj = new Date(toYear, toMonth, toDay, 0, 0, 0, 0);
      
      const dateDiffInMs = toDateObj.getTime() - fromDateObj.getTime();
      const dateDiffInDays = dateDiffInMs / (1000 * 60 * 60 * 24);

      // 날짜 차이가 3일을 초과하면 알림 후 이전 값으로 복원
      if (dateDiffInDays > 3) {
        showAlert('조회 기간은 3일을 초과할 수 없습니다.');
        // 이전 값으로 강제 복원 및 input 리렌더링
        setDateValueStart(previousStartDate);
        setDateInputKey(prev => prev + 1);
        return;
      }
    }

    // 날짜와 시간을 조합하여 3일 초과 검증 (시간까지 고려)
    const fromDateStr = value.replace(/\./g, '-');
    const toDateStr = currentEndDate.replace(/\./g, '-');
    const [fromHours, fromMinutes] = timeValueStartRef.current.split(':').map(Number);
    const [toHours, toMinutes] = timeValueEndRef.current.split(':').map(Number);

    const fromDateTime = new Date(fromDateStr);
    fromDateTime.setHours(fromHours || 0, fromMinutes || 0, 0, 0);

    const toDateTime = new Date(toDateStr);
    toDateTime.setHours(toHours || 0, toMinutes || 0, 0, 0);

    // 3일 초과 검증 (정확히 3일을 초과하는지 체크)
    const diffInMs = toDateTime.getTime() - fromDateTime.getTime();
    
    // 3일을 초과하는 경우 (3일보다 큰 경우)
    // 3일 = 72시간 = 259200000ms
    if (diffInMs > 259200000) {
      showAlert('조회 기간은 3일을 초과할 수 없습니다.');
      // 이전 값으로 강제 복원 및 input 리렌더링
      setDateValueStart(previousStartDate);
      setDateInputKey(prev => prev + 1);
      return;
    }

    setDateValueStart(value);
    dateValueStartRef.current = value;
  };

  const handleToDateChange = (newDate: string) => {
    const value = newDate.replace(/-/g, '.');

    // ref를 사용하여 최신 값 보장
    const currentStartDate = dateValueStartRef.current;
    const previousEndDate = dateValueEndRef.current;

    // 날짜만 먼저 비교 (UserUsageMgmtHistPage 방식)
    const startStr = currentStartDate.replace(/\./g, '');
    const endStr = value.replace(/\./g, '');

    if (endStr < startStr) {
      // toDate가 fromDate보다 작을 경우, 두 날짜를 모두 입력값으로 설정
      setDateValueStart(value);
      setDateValueEnd(value);
      dateValueStartRef.current = value;
      dateValueEndRef.current = value;
      return;
    }

    // 날짜만으로 3일 초과 체크 (날짜 차이 계산)
    // 날짜 문자열을 직접 파싱하여 정확한 날짜 차이 계산
    const fromDateParts = currentStartDate.split('.');
    const toDateParts = value.split('.');
    
    if (fromDateParts.length === 3 && toDateParts.length === 3) {
      const fromYear = parseInt(fromDateParts[0], 10);
      const fromMonth = parseInt(fromDateParts[1], 10) - 1; // 월은 0부터 시작
      const fromDay = parseInt(fromDateParts[2], 10);
      
      const toYear = parseInt(toDateParts[0], 10);
      const toMonth = parseInt(toDateParts[1], 10) - 1;
      const toDay = parseInt(toDateParts[2], 10);
      
      const fromDateObj = new Date(fromYear, fromMonth, fromDay, 0, 0, 0, 0);
      const toDateObj = new Date(toYear, toMonth, toDay, 0, 0, 0, 0);
      
      const dateDiffInMs = toDateObj.getTime() - fromDateObj.getTime();
      const dateDiffInDays = dateDiffInMs / (1000 * 60 * 60 * 24);

      // 날짜 차이가 3일을 초과하면 알림 후 이전 값으로 복원
      if (dateDiffInDays > 3) {
        showAlert('조회 기간은 3일을 초과할 수 없습니다.');
        // 이전 값으로 강제 복원 및 input 리렌더링
        setDateValueEnd(previousEndDate);
        setDateInputKey(prev => prev + 1);
        return;
      }
    }

    // 날짜와 시간을 조합하여 3일 초과 검증 (시간까지 고려)
    const fromDateStr = currentStartDate.replace(/\./g, '-');
    const toDateStr = value.replace(/\./g, '-');
    const [fromHours, fromMinutes] = timeValueStartRef.current.split(':').map(Number);
    const [toHours, toMinutes] = timeValueEndRef.current.split(':').map(Number);

    const fromDateTime = new Date(fromDateStr);
    fromDateTime.setHours(fromHours || 0, fromMinutes || 0, 0, 0);

    const toDateTime = new Date(toDateStr);
    toDateTime.setHours(toHours || 0, toMinutes || 0, 0, 0);

    // 3일 초과 검증 (정확히 3일을 초과하는지 체크)
    const diffInMs = toDateTime.getTime() - fromDateTime.getTime();
    
    // 3일을 초과하는 경우 (3일보다 큰 경우)
    // 3일 = 72시간 = 259200000ms
    if (diffInMs > 259200000) {
      showAlert('조회 기간은 3일을 초과할 수 없습니다.');
      // 이전 값으로 강제 복원 및 input 리렌더링
      setDateValueEnd(previousEndDate);
      setDateInputKey(prev => prev + 1);
      return;
    }

    setDateValueEnd(value);
    dateValueEndRef.current = value;
  };

  const handleFromTimeChange = (newTime: string) => {
    // ref를 사용하여 최신 값 보장
    const currentStartDate = dateValueStartRef.current;
    const currentEndDate = dateValueEndRef.current;
    const currentEndTime = timeValueEndRef.current;
    const previousStartTime = timeValueStartRef.current;

    // 날짜와 시간을 조합하여 비교
    const fromDateStr = currentStartDate.replace(/\./g, '-');
    const toDateStr = currentEndDate.replace(/\./g, '-');
    const [fromHours, fromMinutes] = newTime.split(':').map(Number);
    const [toHours, toMinutes] = currentEndTime.split(':').map(Number);

    const fromDateTime = new Date(fromDateStr);
    fromDateTime.setHours(fromHours || 0, fromMinutes || 0, 0, 0);

    const toDateTime = new Date(toDateStr);
    toDateTime.setHours(toHours || 0, toMinutes || 0, 0, 0);

    // fromDateTime이 toDateTime보다 크면 toDate와 toTime을 fromDate와 fromTime과 동일하게 설정
    if (fromDateTime > toDateTime) {
      setTimeValueStart(newTime);
      setDateValueEnd(currentStartDate);
      setTimeValueEnd(newTime);
      timeValueStartRef.current = newTime;
      dateValueEndRef.current = currentStartDate;
      timeValueEndRef.current = newTime;
      return;
    }

    // 3일 초과 검증
    const diffInMs = toDateTime.getTime() - fromDateTime.getTime();
    const diffInDays = diffInMs / (1000 * 60 * 60 * 24);
    if (diffInDays > 3) {
      showAlert('조회 기간은 3일을 초과할 수 없습니다.');
      // 이전 값으로 강제 복원
      setTimeValueStart(previousStartTime);
      return;
    }

    setTimeValueStart(newTime);
    timeValueStartRef.current = newTime;
  };

  const handleToTimeChange = (newTime: string) => {
    // ref를 사용하여 최신 값 보장
    const currentStartDate = dateValueStartRef.current;
    const currentEndDate = dateValueEndRef.current;
    const currentStartTime = timeValueStartRef.current;
    const previousEndTime = timeValueEndRef.current;

    // 날짜와 시간을 조합하여 비교
    const fromDateStr = currentStartDate.replace(/\./g, '-');
    const toDateStr = currentEndDate.replace(/\./g, '-');
    const [fromHours, fromMinutes] = currentStartTime.split(':').map(Number);
    const [toHours, toMinutes] = newTime.split(':').map(Number);

    const fromDateTime = new Date(fromDateStr);
    fromDateTime.setHours(fromHours || 0, fromMinutes || 0, 0, 0);

    const toDateTime = new Date(toDateStr);
    toDateTime.setHours(toHours || 0, toMinutes || 0, 0, 0);

    // 현재 시간 초과 검증 (가장 먼저 체크)
    // const now = new Date();
    // if (toDateTime > now) {
    //   showAlert('종료 일시는 현재 시간을 초과할 수 없습니다.');
    //   return;
    // }

    // toDateTime이 fromDateTime보다 작으면 fromDate와 fromTime을 toDate와 toTime과 동일하게 설정
    if (toDateTime < fromDateTime) {
      setTimeValueEnd(newTime);
      setDateValueStart(currentEndDate);
      setTimeValueStart(newTime);
      timeValueEndRef.current = newTime;
      dateValueStartRef.current = currentEndDate;
      timeValueStartRef.current = newTime;
      return;
    }

    // 3일 초과 검증
    const diffInMs = toDateTime.getTime() - fromDateTime.getTime();
    const diffInDays = diffInMs / (1000 * 60 * 60 * 24);
    if (diffInDays > 3) {
      showAlert('조회 기간은 3일을 초과할 수 없습니다.');
      // 이전 값으로 강제 복원
      setTimeValueEnd(previousEndTime);
      return;
    }

    setTimeValueEnd(newTime);
    timeValueEndRef.current = newTime;
  };

  const handleSearch = () => {
    const keyword = searchValue1.trim();

    // 페이지 초기화
    setCpuCurrentPage(1);
    setMemoryCurrentPage(1);

    // 날짜/시간을 active state로 업데이트 (조회 버튼 클릭 시에만 적용)
    setActiveDateStart(dateValueStart);
    setActiveDateEnd(dateValueEnd);
    setActiveTimeStart(timeValueStart);
    setActiveTimeEnd(timeValueEnd);

    if (keyword) {
      setSearchValues(prev => ({ ...prev, searchType: '전체' }));
      // 검색 입력 시 클라이언트 사이드 필터링만 사용 (서버 API 호출 안 함)
      setAppliedSearchValue(keyword);
      setAppliedPodName('전체');
    } else {
      // 검색어가 없으면 드롭다운 선택값으로 서버 사이드 필터링
      setAppliedSearchValue('');
      setAppliedPodName(searchValues.searchType || '전체');
    }

    // API 호출
    setTimeout(() => {
      refetchSolutionInfo();
      triggerRefetch();
    }, 0);
  };

  useEffect(() => {
    if (solutionInfo?.namespace) {
      // 초기 로드 시 active 날짜/시간으로 API 호출
      setActiveDateStart(dateValueStart);
      setActiveDateEnd(dateValueEnd);
      setActiveTimeStart(timeValueStart);
      setActiveTimeEnd(timeValueEnd);
      setTimeout(() => {
        refetchSolutionInfo();
        refetch();
      }, 0);
    }
  }, [solutionInfo?.namespace]);

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const triggerRefetch = useCallback(() => {
    setTimeout(() => {
      refetch();
    }, 0);
  }, [refetch]);

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key as keyof typeof dropdownStates]: false }));

    if (key === 'searchType') {
      // 드롭다운 선택 시 즉시 API 호출하지 않음 (조회 버튼 클릭 시에만 적용)
      setSearchValue1(''); // 드롭다운 선택 시 검색 입력값 초기화
      setAppliedSearchValue(''); // 적용된 검색값도 초기화
    }

    if (key === 'dateRangeType') {
      const currentDateTime = getCurrentDateTime();

      let hoursAgo = 24;
      if (value === '최근 24시간') hoursAgo = 24;
      else if (value === '최근 48시간') hoursAgo = 48;
      else if (value === '최근 72시간') hoursAgo = 72;

      if (value === '사용자 지정') {
        setDateValueEnd(currentDateTime.date);
        setTimeValueEnd(currentDateTime.time);
        dateValueEndRef.current = currentDateTime.date;
        timeValueEndRef.current = currentDateTime.time;
      } else {
        setDateValueEnd(currentDateTime.date);
        setTimeValueEnd(currentDateTime.time);

        const fromDateTime = getFromDateTime(hoursAgo);
        setDateValueStart(fromDateTime.date);
        setTimeValueStart(fromDateTime.time);
        dateValueEndRef.current = currentDateTime.date;
        timeValueEndRef.current = currentDateTime.time;
        dateValueStartRef.current = fromDateTime.date;
        timeValueStartRef.current = fromDateTime.time;
      }
    }
  };

  const lineChartState1 = useMemo(() => {
    if (!solutionDetail?.cpuGraph) {
      return {
        series: [],
        options: {
          xaxis: {
            type: 'datetime' as const,
            min: undefined,
            max: undefined,
          },
          yaxis: {
            labels: {
              formatter: (value: number) => {
                return value.toFixed(4);
              },
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(4),
            },
            x: {
              format: 'HH:mm:ss',
            },
          },
          colors: ['#FC4661', '#FFAB08', '#4A86FF', '#71FFD0', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
          stroke: {
            curve: 'smooth' as const,
            width: 2,
          },
        } as ApexOptions,
      };
    }

    const cpuData = solutionDetail.cpuGraph;

    // solutionInfoData.podNames에 있는 pod만 필터링
    const availablePodNames = solutionInfoData?.podNames 
      ? solutionInfoData.podNames.map(item => item.pod).filter(Boolean)
      : [];
    
    // 그래프 데이터 중에서 availablePodNames에 있는 것만 사용
    const filteredCpuData = availablePodNames.length > 0
      ? Object.keys(cpuData).reduce((acc, key) => {
          if (availablePodNames.includes(key)) {
            acc[key] = cpuData[key];
          }
          return acc;
        }, {} as typeof cpuData)
      : cpuData;

    let podNames: string[] = [];
    
    // 검색 입력 시 클라이언트 사이드 라이크 검색 (조회 버튼/엔터키 입력 시 적용)
    if (appliedSearchValue.trim()) {
      const keyword = appliedSearchValue.trim().toLowerCase();
      podNames = Object.keys(filteredCpuData).filter(podName => 
        podName.toLowerCase().includes(keyword)
      );
    } else {
      // 드롭다운 선택 시 서버 사이드 필터링된 데이터 사용 (조회 버튼 클릭 시 적용)
      podNames = appliedPodName === '전체' ? Object.keys(filteredCpuData) : [appliedPodName];
    }

    // fromDate와 endDate를 밀리초 타임스탬프로 변환
    // fromDate 형식: "2024-01-01 14:00:00" (한국 시간)
    // 실제 데이터는 밀리초 타임스탬프이므로, fromDate와 endDate도 밀리초로 변환
    const fromTimestamp = new Date(fromDate).getTime();
    const endTimestamp = new Date(endDate).getTime();

    const series = podNames
      .filter(podName => filteredCpuData[podName])
      .map((podName: string) => {
        const values = filteredCpuData[podName] || [];

        // 10분 간격으로 샘플링하여 성능 개선
        const sampledValues = sampleDataByMinute(values);

        return {
          name: podName,
          data: sampledValues.map((v: any) => [v[0], parseFloat(v[1])]),
        };
      });

    return {
      series,
      options: {
        chart: {
          animations: {
            enabled: false, // 애니메이션 비활성화로 성능 개선
          },
          toolbar: {
            show: false, // 툴바 비활성화
          },
          zoom: {
            enabled: true, // 줌 비활성화
          },
        },
        xaxis: {
          type: 'datetime' as const,
          min: fromTimestamp,
          max: endTimestamp,
          labels: {
            format: 'MM/dd HH:mm',
            datetimeUTC: false,
            rotate: 0, // 레이블 회전 비활성화
          },
        },
        yaxis: {
          labels: {
            formatter: (value: number) => {
              return value.toFixed(4);
            },
          },
        },
        tooltip: {
          y: {
            formatter: (value: number) => value.toFixed(4),
          },
          x: {
            format: 'yyyy-MM-dd HH:mm:ss',
          },
          shared: false, // 툴팁 공유 비활성화로 성능 개선
        },
        dataLabels: {
          enabled: false, // 데이터 레이블 비활성화로 성능 개선
        },
        colors: ['#FC4661', '#FFAB08', '#4A86FF', '#71FFD0', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
        stroke: {
          curve: 'straight' as const, // 'smooth'보다 빠른 렌더링
          width: 1,
        },
        legend: {
          show: true,
          position: 'bottom' as const, // 범례 위치 최적화
        },
      } as ApexOptions,
    };
  }, [solutionDetail?.cpuGraph, solutionInfoData?.podNames, appliedPodName, appliedSearchValue, fromDate, endDate, sampleDataByMinute]);

  const lineChartState2 = useMemo(() => {
    if (!solutionDetail?.memoryGraph) {
      return {
        series: [],
        options: {
          xaxis: {
            type: 'datetime' as const,
            min: undefined,
            max: undefined,
          },
          yaxis: {
            labels: {
              formatter: (value: number) => {
                return value.toFixed(4);
              },
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(4) + ' GiB',
            },
            x: {
              format: 'HH:mm:ss',
            },
          },
          colors: ['#71FFD0', '#4A86FF', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22', '#FC4661', '#FFAB08'],
          stroke: {
            curve: 'smooth' as const,
            width: 2,
          },
        } as ApexOptions,
      };
    }

    const memoryData = solutionDetail.memoryGraph;

    // solutionInfoData.podNames에 있는 pod만 필터링
    const availablePodNames = solutionInfoData?.podNames 
      ? solutionInfoData.podNames.map(item => item.pod).filter(Boolean)
      : [];
    
    // 그래프 데이터 중에서 availablePodNames에 있는 것만 사용
    const filteredMemoryData = availablePodNames.length > 0
      ? Object.keys(memoryData).reduce((acc, key) => {
          if (availablePodNames.includes(key)) {
            acc[key] = memoryData[key];
          }
          return acc;
        }, {} as typeof memoryData)
      : memoryData;

    let podNames: string[] = [];
    
    // 검색 입력 시 클라이언트 사이드 라이크 검색 (조회 버튼/엔터키 입력 시 적용)
    if (appliedSearchValue.trim()) {
      const keyword = appliedSearchValue.trim().toLowerCase();
      podNames = Object.keys(filteredMemoryData).filter(podName => 
        podName.toLowerCase().includes(keyword)
      );
    } else {
      // 드롭다운 선택 시 서버 사이드 필터링된 데이터 사용 (조회 버튼 클릭 시 적용)
      podNames = appliedPodName === '전체' ? Object.keys(filteredMemoryData) : [appliedPodName];
    }

    // fromDate와 endDate를 밀리초 타임스탬프로 변환
    // fromDate 형식: "2024-01-01 14:00:00" (한국 시간)
    // 실제 데이터는 밀리초 타임스탬프이므로, fromDate와 endDate도 밀리초로 변환
    const fromTimestamp = new Date(fromDate).getTime();
    const endTimestamp = new Date(endDate).getTime();

    const series = podNames
      .filter(podName => filteredMemoryData[podName])
      .map((podName: string) => {
        const values = filteredMemoryData[podName] || [];

        // 10분 간격으로 샘플링하여 성능 개선
        const sampledValues = sampleDataByMinute(values);

        return {
          name: podName,
          data: sampledValues.map((v: any) => [v[0], parseFloat(v[1])]),
        };
      });

    return {
      series,
      options: {
        chart: {
          animations: {
            enabled: false, // 애니메이션 비활성화로 성능 개선
          },
          toolbar: {
            show: false, // 툴바 비활성화
          },
          zoom: {
            enabled: true, 
          },
        },
        xaxis: {
          type: 'datetime' as const,
          min: fromTimestamp,
          max: endTimestamp,
          labels: {
            format: 'MM/dd HH:mm',
            datetimeUTC: false,
            rotate: 0, // 레이블 회전 비활성화
          },
        },
        yaxis: {
          labels: {
            formatter: (value: number) => value.toFixed(4),
          },
        },
        tooltip: {
          y: {
            formatter: (value: number) => value.toFixed(4) + ' GiB',
          },
          x: {
            format: 'yyyy-MM-dd HH:mm:ss',
          },
          shared: false, // 툴팁 공유 비활성화로 성능 개선
        },
        dataLabels: {
          enabled: false, // 데이터 레이블 비활성화로 성능 개선
        },
        colors: ['#71FFD0', '#4A86FF', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22', '#FC4661', '#FFAB08'],
        stroke: {
          curve: 'straight' as const, // 'smooth'보다 빠른 렌더링
          width: 1,
        },
        legend: {
          show: true,
          position: 'bottom' as const, // 범례 위치 최적화
        },
      } as ApexOptions,
    };
  }, [solutionDetail?.memoryGraph, solutionInfoData?.podNames, appliedPodName, appliedSearchValue, fromDate, endDate, sampleDataByMinute]);

  return (
    <div>
      <section className='section-page'>
        <UIPageHeader title='솔루션 자원 현황 조회' description='' />

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
                  <colgroup>
                    <col style={{ width: '128px' }} />
                    <col style={{ width: '656px' }} />
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'auto' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          솔루션명
                        </UITypography>
                      </th>
                      <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {solutionInfo?.solutionName || 'Unknown Solution'}
                        </UITypography>
                        </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          네임스페이스명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {solutionInfo?.namespace || '-'}
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
                          {solutionInfoData?.podCount || 0}개
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent={solutionInfo?.solutionStatus === 'overloaded' ? 'overload' : 'complete'}>
                            {solutionInfo?.solutionStatus === 'overloaded' ? '과부하' : '정상'}
                          </UILabel>
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
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
                            검색
                          </UITypography>
                        </th>
                        <td>
                          <UIInput.Search
                            value={searchValue1}
                            placeholder='Pods명 입력'
                            onChange={e => {
                              setSearchValue1(e.target.value);
                            }}
                            onKeyDown={e => {
                              if (e.key === 'Enter') {
                                handleSearch();
                              }
                            }}
                          />
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            Pods명
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={searchValues.searchType}
                            placeholder='Pod 선택'
                            options={podOptions}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={value => handleDropdownSelect('searchType', value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 기간
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={searchValues.dateRangeType}
                            placeholder='조회 기간 선택'
                            options={[
                              { value: '최근 24시간', label: '최근 24시간' },
                              { value: '최근 48시간', label: '최근 48시간' },
                              { value: '최근 72시간', label: '최근 72시간' },
                              { value: '사용자 지정', label: '사용자 지정' },
                            ]}
                            isOpen={dropdownStates.dateRangeType}
                            onClick={() => handleDropdownToggle('dateRangeType')}
                            onSelect={value => handleDropdownSelect('dateRangeType', value)}
                          />
                        </td>
                        <td colSpan={3} className='pl-[32px]'>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1' style={{ zIndex: '10' }}>
                              <UIGroup gap={4} direction='row' vAlign='center'>
                                <UIUnitGroup gap={10} direction='row' vAlign='center'>
                                  <div className='flex-1'>
                                    <UIInput.Date
                                      key={`date-start-${dateInputKey}`}
                                      value={dateValueStart}
                                      onChange={e => {
                                        handleFromDateChange(e.target.value);
                                      }}
                                      disabled={searchValues.dateRangeType !== '사용자 지정'}
                                    />
                                  </div>
                                  <div style={{ width: 'calc(35%)' }}>
                                    <UIDropdown
                                      value={timeValueStart}
                                      placeholder='시간 선택'
                                      options={timeOptions}
                                      isOpen={dropdownStates.timeStart}
                                      onClick={() => handleDropdownToggle('timeStart')}
                                      onSelect={(value: string) => {
                                        handleFromTimeChange(value);
                                        setDropdownStates(prev => ({ ...prev, timeStart: false }));
                                      }}
                                      disabled={searchValues.dateRangeType !== '사용자 지정'}
                                    />
                                  </div>
                                </UIUnitGroup>

                                <UITypography variant='body-1' className='secondary-neutral-p'>
                                  ~
                                </UITypography>

                                <UIUnitGroup gap={10} direction='row' vAlign='center'>
                                  <div className='flex-1'>
                                    <UIInput.Date
                                      key={`date-end-${dateInputKey}`}
                                      value={dateValueEnd}
                                      onChange={e => {
                                        handleToDateChange(e.target.value);
                                      }}
                                      disabled={searchValues.dateRangeType !== '사용자 지정'}
                                    />
                                  </div>
                                  <div className='w-[100px]'>
                                    <UIDropdown
                                      value={timeValueEnd}
                                      placeholder='시간 선택'
                                      options={timeOptions}
                                      isOpen={dropdownStates.timeEnd}
                                      onClick={() => handleDropdownToggle('timeEnd')}
                                      onSelect={(value: string) => {
                                        handleToTimeChange(value);
                                        setDropdownStates(prev => ({ ...prev, timeEnd: false }));
                                      }}
                                      disabled={searchValues.dateRangeType !== '사용자 지정'}
                                    />
                                  </div>
                                </UIUnitGroup>
                              </UIGroup>
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

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                평균 사용률
              </UITypography>
            </div>
            <div className='article-body'>
              <UIGroup gap={16} direction='row'>
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
                          요청량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='semantic-deep'>
                          {cpuUsageRate}%
                        </UITypography>
                      </UIGroup>
                    </div>
                    <div className='card-list'>
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          상한량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800'>
                          {cpuLimitUsageRate}%
                        </UITypography>
                      </UIGroup>
                    </div>
                  </div>
                </div>
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
                          요청량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='semantic-deep'>
                          {memoryRequestUsageRate}%
                        </UITypography>
                      </UIGroup>
                    </div>
                    <div className='card-list'>
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          상한량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800'>
                          {memoryLimitUsageRate}%
                        </UITypography>
                      </UIGroup>
                    </div>
                  </div>
                </div>
              </UIGroup>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='chart-container'>
              <UILineChart label='CPU 사용량' x='시간(초)' y='사용량' options={lineChartState1.options} series={lineChartState1.series} />
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '200px', paddingRight: '8px' }}>
                        <UIDataCnt count={cpuGridData.length} prefix='CPU Quota 총' unit='건' />
                      </div>
                    </div>
                    <div className='flex'>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(cpuPageSize)}
                          disabled={cpuGridData.length <= 12}
                          options={[
                            { value: '12', label: '12개씩 보기' },
                            { value: '36', label: '36개씩 보기' },
                            { value: '60', label: '60개씩 보기' },
                          ]}
                          onSelect={(value: string) => {
                            setCpuPageSize(Number(value));
                            setCpuCurrentPage(1);
                          }}
                          height={40}
                          variant='dataGroup'
                        />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={paginatedCpuData} columnDefs={cpuColumnDefs} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={cpuCurrentPage} totalPages={cpuTotalPages} onPageChange={(page: number) => setCpuCurrentPage(page)} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          <UIArticle>
            <div className='chart-container'>
              <UILineChart label='Memory 사용량' x='시간(초)' y='사용량' options={lineChartState2.options} series={lineChartState2.series} />
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '200px', paddingRight: '8px' }}>
                        <UIDataCnt count={memoryGridData.length} prefix='Memory Quota 총' unit='건' />
                      </div>
                    </div>
                    <div className='flex'>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(memoryPageSize)}
                          disabled={memoryGridData.length <= 12}
                          options={[
                            { value: '12', label: '12개씩 보기' },
                            { value: '36', label: '36개씩 보기' },
                            { value: '60', label: '60개씩 보기' },
                          ]}
                          onSelect={(value: string) => {
                            setMemoryPageSize(Number(value));
                            setMemoryCurrentPage(1);
                          }}
                          height={40}
                          variant='dataGroup'
                        />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={paginatedMemoryData} columnDefs={memoryColumnDefs} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={memoryCurrentPage}
                  totalPages={memoryTotalPages}
                  onPageChange={(page: number) => setMemoryCurrentPage(page)}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </div>
  );
};
