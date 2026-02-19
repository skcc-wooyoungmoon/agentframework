import React, { useCallback, useEffect, useMemo, useState, useRef } from 'react';
import type { ApexOptions } from 'apexcharts';
import { useAtomValue, useSetAtom } from 'jotai';
import { useNavigate } from 'react-router-dom';

import { UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIBox, UIButton2, UIDataCnt, UILabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILineChart } from '@/components/UI/molecules/chart';
import { selectedGpuNodeAtom } from '@/stores/admin/resrcMgmt';
import { useGetGpuNodeDetail } from '@/services/admin/resrcMgmt';
import { useModal } from '@/stores/common/modal';

interface SearchValues {
  searchType: string;
  dateRangeType: string;
}

export const ResrcMgmtGpuNodeDetailPage = () => {
  const nodeInfo = useAtomValue(selectedGpuNodeAtom);
  const setSelectedGpuNode = useSetAtom(selectedGpuNodeAtom);
  const navigate = useNavigate();

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

  const [searchValues, setSearchValues] = useState<SearchValues>({
    searchType: '',
    dateRangeType: '최근 24시간',
  });

  const [appliedSearchValues, setAppliedSearchValues] = useState<SearchValues>({
    searchType: '',
    dateRangeType: '최근 24시간',
  });

  const [cpuCurrentPage, setCpuCurrentPage] = useState(1);
  const [cpuPageSize, setCpuPageSize] = useState(12);

  const [memoryCurrentPage, setMemoryCurrentPage] = useState(1);
  const [memoryPageSize, setMemoryPageSize] = useState(12);

  const [gpuCurrentPage, setGpuCurrentPage] = useState(1);
  const [gpuPageSize, setGpuPageSize] = useState(12);

  const [searchValue1, setSearchValue1] = useState('');
  const [appliedSearchKeyword, setAppliedSearchKeyword] = useState('');

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

  const [queryParams, setQueryParams] = useState({
    fromDate: `${fromDateTime.date.replace(/\./g, '-')} ${currentDateTime.time}:00`,
    toDate: `${currentDateTime.date.replace(/\./g, '-')} ${currentDateTime.time}:00`,
  });

  // fromDate만 9시간 빼서 UTC로 변환 (한국 시간 → UTC)
  const KST_OFFSET = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
  const fromDateObj = new Date(queryParams.fromDate);
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

  const { data: gpuNodeDetail } = useGetGpuNodeDetail(
    {
      nodeName: nodeInfo?.nodeName || '',
      fromDate: formatDateTime(fromDateUTC),
      toDate: queryParams.toDate,
      workloadName: appliedSearchValues.searchType || undefined, // 워크로드명 전달
    },
    {
      enabled: !!nodeInfo?.nodeName,
      refetchOnMount: 'always',
      staleTime: 0,
      gcTime: 0,
    }
  );

  const matchesKeyword = useCallback(
    (value: string | undefined | null) => {
      if (!appliedSearchKeyword) return true;
      if (!value) return false;
      return value.toLowerCase().includes(appliedSearchKeyword.toLowerCase());
    },
    [appliedSearchKeyword]
  );

  const getFilteredWorkloadNames = useCallback(
    (allNames: string[]) => {
      if (appliedSearchKeyword) {
        const keyword = appliedSearchKeyword.toLowerCase();
        return allNames.filter(name => name.toLowerCase().includes(keyword));
      }
      // appliedSearchValues.searchType이 없으면 searchValues.searchType 사용
      const searchType = appliedSearchValues.searchType || searchValues.searchType;
      if (!searchType) {
        return []; // 전체 데이터를 반환하지 않고 빈 배열 반환
      }
      return allNames.includes(searchType) ? [searchType] : [];
    },
    [appliedSearchKeyword, appliedSearchValues.searchType, searchValues.searchType]
  );

  const workloadOptions = useMemo(() => {
    if (gpuNodeDetail?.workloads && gpuNodeDetail.workloads.length > 0) {
      const workloadOpts = gpuNodeDetail.workloads.map(workload => ({
        value: workload,
        label: workload,
      }));
      return workloadOpts;
    }
    return [];
  }, [gpuNodeDetail?.workloads]);

  // workloadOptions가 로드되면 첫 번째 워크로드로 자동 설정
  useEffect(() => {
    if (workloadOptions.length > 0 && !searchValues.searchType) {
      const firstWorkload = workloadOptions[0].value;
      setSearchValues(prev => ({ ...prev, searchType: firstWorkload }));
      setAppliedSearchValues(prev => ({ ...prev, searchType: firstWorkload }));
    }
  }, [workloadOptions]);

  const cpuRowData = useMemo(() => {
    if (!gpuNodeDetail?.sessionCpuQuotaGrid) return [];

    // gpuNodeDetail.workloads에 있는 워크로드만 필터링
    const availableWorkloads = gpuNodeDetail?.workloads && Array.isArray(gpuNodeDetail.workloads)
      ? gpuNodeDetail.workloads.filter(Boolean)
      : [];

    const filteredData = gpuNodeDetail.sessionCpuQuotaGrid.filter((item: any) => {
      // workloads 배열에 있는 항목만 표시
      if (availableWorkloads.length > 0 && !availableWorkloads.includes(item.session_id)) {
        return false;
      }
      
      if (appliedSearchKeyword) {
        return matchesKeyword(item.session_id);
      }
      // appliedSearchValues.searchType이 없으면 searchValues.searchType 사용
      const searchType = appliedSearchValues.searchType || searchValues.searchType;
      if (!searchType) {
        return false; // 전체 데이터를 반환하지 않고 빈 배열 반환
      }
      return item.session_id === searchType;
    });

    return filteredData.map((item, index) => ({
      id: String(index + 1),
      ...item,
    }));
  }, [gpuNodeDetail?.sessionCpuQuotaGrid, gpuNodeDetail?.workloads, appliedSearchValues.searchType, searchValues.searchType, appliedSearchKeyword, matchesKeyword]);

  const memoryRowData = useMemo(() => {
    if (!gpuNodeDetail?.sessionMemoryQuotaGrid) return [];

    // gpuNodeDetail.workloads에 있는 워크로드만 필터링
    const availableWorkloads = gpuNodeDetail?.workloads && Array.isArray(gpuNodeDetail.workloads)
      ? gpuNodeDetail.workloads.filter(Boolean)
      : [];

    const filteredData = gpuNodeDetail.sessionMemoryQuotaGrid.filter((item: any) => {
      // workloads 배열에 있는 항목만 표시
      if (availableWorkloads.length > 0 && !availableWorkloads.includes(item.session_id)) {
        return false;
      }
      
      if (appliedSearchKeyword) {
        return matchesKeyword(item.session_id);
      }
      // appliedSearchValues.searchType이 없으면 searchValues.searchType 사용
      const searchType = appliedSearchValues.searchType || searchValues.searchType;
      if (!searchType) {
        return false; // 전체 데이터를 반환하지 않고 빈 배열 반환
      }
      return item.session_id === searchType;
    });

    return filteredData.map((item, index) => ({
      id: String(index + 1),
      ...item,
    }));
  }, [gpuNodeDetail?.sessionMemoryQuotaGrid, gpuNodeDetail?.workloads, appliedSearchValues.searchType, searchValues.searchType, appliedSearchKeyword, matchesKeyword]);

  const gpuRowData = useMemo(() => {
    if (!gpuNodeDetail?.sessionGpuQuotaGrid) return [];

    // gpuNodeDetail.workloads에 있는 워크로드만 필터링
    const availableWorkloads = gpuNodeDetail?.workloads && Array.isArray(gpuNodeDetail.workloads)
      ? gpuNodeDetail.workloads.filter(Boolean)
      : [];

    const filteredData = gpuNodeDetail.sessionGpuQuotaGrid.filter((item: any) => {
      // workloads 배열에 있는 항목만 표시
      if (availableWorkloads.length > 0 && !availableWorkloads.includes(item.session_id)) {
        return false;
      }
      
      if (appliedSearchKeyword) {
        return matchesKeyword(item.session_id);
      }
      // appliedSearchValues.searchType이 없으면 searchValues.searchType 사용
      const searchType = appliedSearchValues.searchType || searchValues.searchType;
      if (!searchType) {
        return false; // 전체 데이터를 반환하지 않고 빈 배열 반환
      }
      return item.session_id === searchType;
    });

    return filteredData.map((item, index) => ({
      id: String(index + 1),
      ...item,
    }));
  }, [gpuNodeDetail?.sessionGpuQuotaGrid, gpuNodeDetail?.workloads, appliedSearchValues.searchType, searchValues.searchType, appliedSearchKeyword, matchesKeyword]);

  const paginatedCpuData = useMemo(() => {
    const startIndex = (cpuCurrentPage - 1) * cpuPageSize;
    const endIndex = startIndex + cpuPageSize;
    return cpuRowData.slice(startIndex, endIndex);
  }, [cpuRowData, cpuCurrentPage, cpuPageSize]);

  const cpuTotalPages = Math.max(1, Math.ceil(cpuRowData.length / cpuPageSize));

  const paginatedMemoryData = useMemo(() => {
    const startIndex = (memoryCurrentPage - 1) * memoryPageSize;
    const endIndex = startIndex + memoryPageSize;
    return memoryRowData.slice(startIndex, endIndex);
  }, [memoryRowData, memoryCurrentPage, memoryPageSize]);

  const memoryTotalPages = Math.max(1, Math.ceil(memoryRowData.length / memoryPageSize));

  const paginatedGpuData = useMemo(() => {
    const startIndex = (gpuCurrentPage - 1) * gpuPageSize;
    const endIndex = startIndex + gpuPageSize;
    return gpuRowData.slice(startIndex, endIndex);
  }, [gpuRowData, gpuCurrentPage, gpuPageSize]);

  const gpuTotalPages = Math.max(1, Math.ceil(gpuRowData.length / gpuPageSize));

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
        headerName: '워크로드명',
        field: 'session_id',
        minWidth: 430,
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
        field: 'allocation',
        width: 280,
      },
      {
        headerName: '사용량',
        field: 'usage',
        width: 280,
        valueFormatter: (params: any) => params.value?.toFixed(4) || '0',
      },
      {
        headerName: '할당량 대비 사용률',
        field: 'allocation_usage_rate',
        width: 300,
        valueFormatter: (params: any) => `${params.value?.toFixed(1) || '0'}%`,
      },
    ],
    []
  );

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
        headerName: '워크로드명',
        field: 'session_id',
        minWidth: 430,
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
        field: 'allocation',
        width: 280,
        valueFormatter: (params: any) => `${(Number(params.value ?? 0)).toFixed(4)} GB`,
      },
      {
        headerName: '사용량',
        field: 'usage',
        width: 280,
        valueFormatter: (params: any) => `${(Number(params.value ?? 0)).toFixed(4)} GB`,
      },
      {
        headerName: '할당량 대비 사용률',
        field: 'allocation_usage_rate',
        width: 300,
        valueFormatter: (params: any) => `${params.value?.toFixed(1) || '0'}%`,
      },
    ],
    []
  );

  const gpuColumnDefs: any = useMemo(
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
        headerName: '워크로드명',
        field: 'session_id',
        minWidth: 430,
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
        field: 'allocation',
        width: 280,
      },
      {
        headerName: '사용량',
        field: 'usage',
        width: 280,
        valueFormatter: (params: any) => params.value?.toFixed(4) || '0',
      },
      {
        headerName: '할당량 대비 사용률',
        field: 'allocation_usage_rate',
        width: 300,
        valueFormatter: (params: any) => `${params.value?.toFixed(1) || '0'}%`,
      },
    ],
    []
  );

  const handleFromDateChange = (newDate: string) => {
    const value = newDate.replace(/-/g, '.');

    // ref를 사용하여 최신 값 보장
    const currentEndDate = dateValueEndRef.current;
    const previousStartDate = dateValueStartRef.current;

    // 날짜만 먼저 비교
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
    const fromDateParts = value.split('.');
    const toDateParts = currentEndDate.split('.');
    
    if (fromDateParts.length === 3 && toDateParts.length === 3) {
      const fromYear = parseInt(fromDateParts[0], 10);
      const fromMonth = parseInt(fromDateParts[1], 10) - 1;
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

    // 날짜만 먼저 비교
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
    const fromDateParts = currentStartDate.split('.');
    const toDateParts = value.split('.');
    
    if (fromDateParts.length === 3 && toDateParts.length === 3) {
      const fromYear = parseInt(fromDateParts[0], 10);
      const fromMonth = parseInt(fromDateParts[1], 10) - 1;
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

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key as keyof typeof dropdownStates]: false }));

    if (key === 'dateRangeType') {
      const currentDateTime = getCurrentDateTime();

      let hoursAgo = 24;
      if (value === '최근 24시간') hoursAgo = 24;
      else if (value === '최근 48시간') hoursAgo = 48;
      else if (value === '최근 72시간') hoursAgo = 72;

      if (value === '사용자 지정') {
        setDateValueEnd(currentDateTime.date);
        setTimeValueEnd(currentDateTime.time);
      } else {
        setDateValueEnd(currentDateTime.date);
        setTimeValueEnd(currentDateTime.time);

        const fromDateTime = getFromDateTime(hoursAgo);
        setDateValueStart(fromDateTime.date);
        setTimeValueStart(fromDateTime.time);
      }
    }

    if (key === 'searchType') {
      setAppliedSearchKeyword('');
    }
  };

  const handleSearch = () => {
    const keyword = searchValue1.trim();
    setAppliedSearchKeyword(keyword);

    setAppliedSearchValues({ ...searchValues });

    setCpuCurrentPage(1);
    setMemoryCurrentPage(1);
    setGpuCurrentPage(1);

    const newFromDate = `${dateValueStart.replace(/\./g, '-')} ${timeValueStart}:00`;
    const newToDate = `${dateValueEnd.replace(/\./g, '-')} ${timeValueEnd}:00`;

    setQueryParams({
      fromDate: newFromDate,
      toDate: newToDate,
    });
  };


  // 10분 간격으로 데이터 샘플링 (성능 개선)
  const sampleDataByMinute = useCallback((dataArray: any[]) => {
    if (!dataArray || dataArray.length === 0) return [];
    if (dataArray.length <= 1) return dataArray;

    const sampled: any[] = [dataArray[0]]; // 첫 번째 포인트는 항상 포함
    const TEN_MINUTES_MS = 5 * 60 * 1000; // 10분 = 600000 밀리초 -> 1분간격으로 변경
    let lastTimestamp = dataArray[0][0]; // 첫 번째 타임스탬프

    for (let i = 1; i < dataArray.length; i++) {
      const currentTimestamp = dataArray[i][0];
      // 이전 포인트와의 시간 차이가 10분 이상이면 포함
      if (currentTimestamp - lastTimestamp >= TEN_MINUTES_MS) {
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

  const lineChartState1 = useMemo(() => {
    if (!gpuNodeDetail?.workloadCpuGraph) {
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
              formatter: (value: number) => value.toFixed(1),
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(1),
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

    const cpuData = gpuNodeDetail.workloadCpuGraph;
    
    // gpuNodeDetail.workloads에 있는 워크로드만 필터링
    const availableWorkloadNames = gpuNodeDetail?.workloads && Array.isArray(gpuNodeDetail.workloads)
      ? gpuNodeDetail.workloads.filter(Boolean)
      : [];
    
    // 그래프 데이터 중에서 availableWorkloadNames에 있는 것만 사용
    const filteredCpuData = availableWorkloadNames.length > 0
      ? Object.keys(cpuData).reduce((acc, key) => {
          if (availableWorkloadNames.includes(key)) {
            acc[key] = cpuData[key];
          }
          return acc;
        }, {} as typeof cpuData)
      : cpuData;
    
    const workloadNames = getFilteredWorkloadNames(Object.keys(filteredCpuData));

    if (workloadNames.length === 0) {
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
              formatter: (value: number) => value.toFixed(1),
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(1),
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

    // fromDate와 endDate를 밀리초 타임스탬프로 변환
    const fromTimestamp = new Date(queryParams.fromDate).getTime();
    const endTimestamp = new Date(queryParams.toDate).getTime();

    const series = workloadNames
      .filter(workloadName => filteredCpuData[workloadName])
      .map((workloadName: string) => {
        const values = filteredCpuData[workloadName] || [];

        // 1분 간격으로 샘플링하여 성능 개선
        const sampledValues = sampleDataByMinute(values);

        return {
          name: workloadName,
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
            enabled: true, // 줌 활성화
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
            formatter: (value: number) => value.toFixed(1),
          },
        },
        tooltip: {
          y: {
            formatter: (value: number) => value.toFixed(1),
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
  }, [gpuNodeDetail?.workloadCpuGraph, gpuNodeDetail?.workloads, appliedSearchValues.searchType, searchValues.searchType, getFilteredWorkloadNames, queryParams, sampleDataByMinute]);

  const lineChartState2 = useMemo(() => {
    if (!gpuNodeDetail?.workloadMemoryGraph) {
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
              formatter: (value: number) => value.toFixed(1),
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(1) + ' GB',
            },
          },
          colors: ['#71FFD0', '#4A86FF', '#FC4661', '#FFAB08', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
          stroke: {
            curve: 'smooth' as const,
            width: 2,
          },
        } as ApexOptions,
      };
    }

    const memoryData = gpuNodeDetail.workloadMemoryGraph;
    
    // gpuNodeDetail.workloads에 있는 워크로드만 필터링
    const availableWorkloadNames = gpuNodeDetail?.workloads && Array.isArray(gpuNodeDetail.workloads)
      ? gpuNodeDetail.workloads.filter(Boolean)
      : [];
    
    // 그래프 데이터 중에서 availableWorkloadNames에 있는 것만 사용
    const filteredMemoryData = availableWorkloadNames.length > 0
      ? Object.keys(memoryData).reduce((acc, key) => {
          if (availableWorkloadNames.includes(key)) {
            acc[key] = memoryData[key];
          }
          return acc;
        }, {} as typeof memoryData)
      : memoryData;
    
    const workloadNames = getFilteredWorkloadNames(Object.keys(filteredMemoryData));

    if (workloadNames.length === 0) {
      return {
        series: [],
        options: {
          xaxis: {
            type: 'category' as const,
            categories: [],
          },
          yaxis: {
            labels: {
              formatter: (value: number) => value.toFixed(1),
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(1) + ' GB',
            },
          },
          colors: ['#71FFD0', '#4A86FF', '#FC4661', '#FFAB08', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
          stroke: {
            curve: 'smooth' as const,
            width: 2,
          },
        } as ApexOptions,
      };
    }

    // fromDate와 endDate를 밀리초 타임스탬프로 변환
    const fromTimestamp = new Date(queryParams.fromDate).getTime();
    const endTimestamp = new Date(queryParams.toDate).getTime();

    const series = workloadNames
      .filter(workloadName => filteredMemoryData[workloadName])
      .map((workloadName: string) => {
        const values = filteredMemoryData[workloadName] || [];

        // 1분 간격으로 샘플링하여 성능 개선
        const sampledValues = sampleDataByMinute(values);

        return {
          name: workloadName,
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
            enabled: true, // 줌 활성화
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
            formatter: (value: number) => value.toFixed(1),
          },
        },
        tooltip: {
          y: {
            formatter: (value: number) => value.toFixed(1) + ' GB',
          },
          x: {
            format: 'yyyy-MM-dd HH:mm:ss',
          },
          shared: false, // 툴팁 공유 비활성화로 성능 개선
        },
        dataLabels: {
          enabled: false, // 데이터 레이블 비활성화로 성능 개선
        },
        colors: ['#71FFD0', '#4A86FF', '#FC4661', '#FFAB08', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
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
  }, [gpuNodeDetail?.workloadMemoryGraph, gpuNodeDetail?.workloads, appliedSearchValues.searchType, searchValues.searchType, getFilteredWorkloadNames, queryParams, sampleDataByMinute]);

  const lineChartState3 = useMemo(() => {
    if (!gpuNodeDetail?.workloadGpuGraph) {
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
              formatter: (value: number) => value.toFixed(1),
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(1),
            },
            x: {
              format: 'HH:mm:ss',
            },
          },
          colors: ['#FFAB08', '#8742FF', '#FC4661', '#4A86FF', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
          stroke: {
            curve: 'smooth' as const,
            width: 2,
          },
        } as ApexOptions,
      };
    }

    const gpuData = gpuNodeDetail.workloadGpuGraph;
    
    // gpuNodeDetail.workloads에 있는 워크로드만 필터링
    const availableWorkloadNames = gpuNodeDetail?.workloads && Array.isArray(gpuNodeDetail.workloads)
      ? gpuNodeDetail.workloads.filter(Boolean)
      : [];
    
    // 그래프 데이터 중에서 availableWorkloadNames에 있는 것만 사용
    const filteredGpuData = availableWorkloadNames.length > 0
      ? Object.keys(gpuData).reduce((acc, key) => {
          if (availableWorkloadNames.includes(key)) {
            acc[key] = gpuData[key];
          }
          return acc;
        }, {} as typeof gpuData)
      : gpuData;
    
    const workloadNames = getFilteredWorkloadNames(Object.keys(filteredGpuData));

    if (workloadNames.length === 0) {
      return {
        series: [],
        options: {
          xaxis: {
            type: 'category' as const,
            categories: [],
          },
          yaxis: {
            labels: {
              formatter: (value: number) => value.toFixed(1),
            },
          },
          tooltip: {
            y: {
              formatter: (value: number) => value.toFixed(1),
            },
          },
          colors: ['#FFAB08', '#8742FF', '#FC4661', '#4A86FF', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
          stroke: {
            curve: 'smooth' as const,
            width: 2,
          },
        } as ApexOptions,
      };
    }

    // fromDate와 endDate를 밀리초 타임스탬프로 변환
    const fromTimestamp = new Date(queryParams.fromDate).getTime();
    const endTimestamp = new Date(queryParams.toDate).getTime();

    const series = workloadNames
      .filter(workloadName => filteredGpuData[workloadName])
      .map((workloadName: string) => {
        const values = filteredGpuData[workloadName] || [];

        // 1분 간격으로 샘플링하여 성능 개선
        const sampledValues = sampleDataByMinute(values);

        return {
          name: workloadName,
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
            enabled: true, // 줌 활성화
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
            formatter: (value: number) => value.toFixed(1),
          },
        },
        tooltip: {
          y: {
            formatter: (value: number) => value.toFixed(1),
          },
          x: {
            format: 'yyyy-MM-dd HH:mm:ss',
          },
          shared: false, // 툴팁 공유 비활성화로 성능 개선
        },
        dataLabels: {
          enabled: false, // 데이터 레이블 비활성화로 성능 개선
        },
        colors: ['#FFAB08', '#8742FF', '#FC4661', '#4A86FF', '#9B59B6', '#E74C3C', '#3498DB', '#2ECC71', '#F39C12', '#1ABC9C', '#34495E', '#E67E22'],
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
  }, [gpuNodeDetail?.workloadGpuGraph, gpuNodeDetail?.workloads, appliedSearchValues.searchType, searchValues.searchType, getFilteredWorkloadNames, queryParams, sampleDataByMinute]);

  useEffect(() => {
    if (!nodeInfo) {
      const storedNode = sessionStorage.getItem('selectedGpuNode');
      if (storedNode) {
        try {
          const parsed = JSON.parse(storedNode);
          if (parsed?.nodeName) {
            setSelectedGpuNode(parsed);
            return;
          }
        } catch (error) {
          console.warn('Failed to parse stored GPU node info:', error);
        }
      }
      navigate('/admin/resrc-mgmt/gpu-node');
    } else {
      try {
        sessionStorage.setItem('selectedGpuNode', JSON.stringify(nodeInfo));
      } catch (error) {
        console.warn('Failed to store GPU node info:', error);
      }
    }
  }, [nodeInfo, navigate, setSelectedGpuNode]);

  if (!nodeInfo) {
    return null;
  }

  return (
    <div>
      <section className='section-page'>
        <UIPageHeader title='GPU 노드별 자원 현황 조회' description='' />

        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                노드 정보
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
                          노드명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {nodeInfo?.nodeData?.display_name || 'Unknown Node'}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          노드 그룹명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {nodeInfo?.nodeData?.service_group || 'Unknown Group'}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포 워크로드 수
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {gpuNodeDetail?.workload_count ?? 0}개
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent={nodeInfo?.nodeStatus === 'overloaded' ? 'error' : 'complete'}>
                            {nodeInfo?.nodeStatus === 'overloaded' ? '과부하' : '정상'}
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
                            placeholder='워크로드 입력'
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
                            워크로드명
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={searchValues.searchType}
                            placeholder='워크로드 선택'
                            options={workloadOptions}
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
                          <div className='flex-1'>
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
                          </div>
                        </td>
                        <td colSpan={3} className='pl-[32px]'>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1' style={{ zIndex: '10' }}>
                              <UIGroup gap={4} direction='row' vAlign='center'>
                                <UIUnitGroup gap={10} direction='row' vAlign='center'>
                                  <div className='flex-1'>
                                    <UIInput.Date
                                      key={`date-start-${dateInputKey}`}
                                      value={dateValueStart.replace(/\./g, '-')}
                                      onChange={e => {
                                        handleFromDateChange(e.target.value);
                                      }}
                                      disabled={searchValues.dateRangeType !== '사용자 지정'}
                                    />
                                  </div>
                                  <div className='w-[100px]'>
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
                                      value={dateValueEnd.replace(/\./g, '-')}
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
                          할당량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800'>
                          {gpuNodeDetail?.usageRates?.cpu_limit_usage_rate?.toFixed(1) ?? 0}%
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
                          할당량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800'>
                          {gpuNodeDetail?.usageRates?.memory_limit_usage_rate?.toFixed(1) ?? 0}%
                        </UITypography>
                      </UIGroup>
                    </div>
                  </div>
                </div>
                <div className='card-default flex-1'>
                  <div className='flex justify-between mb-[24px]'>
                    <div className='w-[240px] flex align-center items-start'>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                        GPU
                      </UITypography>
                    </div>
                  </div>
                  <div className='card-list-wrapper'>

                    <div className='card-list'>
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          할당량 대비 사용률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800'>
                          {gpuNodeDetail?.usageRates?.gpu_limit_usage_rate?.toFixed(1) ?? 0}%
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
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={cpuRowData.length} prefix='CPU Quota 총' unit='건' />
                      </div>
                    </div>
                    <div className='flex'>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(cpuPageSize)}
                          disabled={cpuRowData.length <= 12}
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
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={memoryRowData.length} prefix='Memory Quota 총' unit='건' />
                      </div>
                    </div>
                    <div className='flex'>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(memoryPageSize)}
                          disabled={memoryRowData.length <= 12}
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

          <UIArticle>
            <div className='chart-container'>
              <UILineChart label='GPU 사용량' x='시간(초)' y='사용량' options={lineChartState3.options} series={lineChartState3.series} />
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={gpuRowData.length} prefix='GPU Quota 총' unit='건' />
                      </div>
                    </div>
                    <div className='flex'>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(gpuPageSize)}
                          disabled={gpuRowData.length <= 12}
                          options={[
                            { value: '12', label: '12개씩 보기' },
                            { value: '36', label: '36개씩 보기' },
                            { value: '60', label: '60개씩 보기' },
                          ]}
                          onSelect={(value: string) => {
                            setGpuPageSize(Number(value));
                            setGpuCurrentPage(1);
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
                <UIGrid type='default' rowData={paginatedGpuData} columnDefs={gpuColumnDefs} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={gpuCurrentPage} totalPages={gpuTotalPages} onPageChange={(page: number) => setGpuCurrentPage(page)} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </div>
  );
};
