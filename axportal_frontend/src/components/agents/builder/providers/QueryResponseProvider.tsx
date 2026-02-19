import { useQuery } from '@tanstack/react-query';
import { atom } from 'jotai';
import { useAtom } from 'jotai';
import React, { createContext, useContext, useMemo, useState } from 'react';

// Atom definitions
type ActiveKnowledge = boolean | null;
const activeAtom = atom<ActiveKnowledge>(null);

// Types
interface PaginationState {
  pageIndex: number;
  pageSize: number;
  page: number;
  totalPages: number;
  totalItems: number;
  lastPage: number;
  last_page: number;
  total: number;
  limit: number;
  size: number;
  offset: number;
  search: string;
  sortColumn: string;
  sortType: string;
  filterStatus: string;
  filterType: string;
  filterCreator: string;
  links: any[];
  from_: number;
  to: number;
}

interface Response<T> {
  data: T[];
  payload?: {
    pagination: PaginationState;
  };
}

// interface _PaginationPayload {
//   pagination: PaginationState;
// }

interface QueryResponse<T> {
  data: T[];
  pagination?: PaginationState;
}

// interface APIResponse<T> {
//   data: T[];
//   payload: PaginationPayload;
// }

interface QueryResponseProviderProps<T> {
  children: React.ReactNode;

  // fetchData: (query: string, param: Record<string, any>) => Promise<APIResponse<T>>;
  // eslint-disable-next-line no-unused-vars
  fetchData: (query: string, param: Record<string, any>) => Promise<Response<Array<T>>>;

  fetchDataParam?: Record<string, any>;

  // fetchCallback?: (response: APIResponse<T>) => any;
  // eslint-disable-next-line no-unused-vars
  fetchCallback?: (response: any) => any;
  defaultFilters?: Record<string, string>;
  useUnderscoreFilter?: boolean;
  initSortColumn?: string;
  initSortType?: string;
  initSize?: number;
  pollingInterval?: number;
  // 서로 다른 리소스가 같은 화면에서 공존할 때 캐시 키를 분리하기 위한 식별자
  queryKey?: string;
}

interface QueryResponseContextValue<T> {
  isLoading: boolean;
  refetch: () => void;
  response?: Response<Array<T>> | undefined;
  pagination: PaginationState | undefined;
  page: number;
  size: number;
  // eslint-disable-next-line no-unused-vars
  setPage: (page: number) => void;
  // eslint-disable-next-line no-unused-vars
  setSize: (size: number) => void;
  // eslint-disable-next-line no-unused-vars
  setTotalPages: (totalPages: number) => void;
  // eslint-disable-next-line no-unused-vars
  handlePageChange: (page: number) => void;
  // eslint-disable-next-line no-unused-vars
  handleItemsPerPageChange: (itemsPerPage: number) => void;
  // eslint-disable-next-line no-unused-vars
  updateSearchTerm: (search: string) => void;
  // eslint-disable-next-line no-unused-vars
  updateFilterParam: (filters: Record<string, any>) => void;
  // eslint-disable-next-line no-unused-vars
  updateSortParam: (sortColumn: string, sortType: string) => void;
}

const QueryResponseContext = createContext<QueryResponseContextValue<any> | undefined>(undefined);

const QueryResponseProvider = <T,>({
  children,
  fetchData,
  fetchDataParam = {},
  fetchCallback,
  defaultFilters = {},
  useUnderscoreFilter = false,
  initSortColumn,
  initSortType = 'desc',
  pollingInterval: _pollingInterval,
  queryKey = 'data-list',
}: QueryResponseProviderProps<T>) => {
  // const [page, setPage] = useAtom(pageAtom);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(12);
  const [search, setSearch] = useState('');
  const [filters, setFilters] = useState(defaultFilters);
  const [sortColumn, setSortColumn] = useState(initSortColumn != null ? initSortColumn : '');
  const [sortType, setSortType] = useState(initSortColumn != null ? initSortType : '');
  const [, setTotalPages] = useState<number>(1);
  const [isActive] = useAtom(activeAtom);

  const query = useMemo(() => {
    const paginationQuery = `page=${page}&size=${size}`;
    const searchQuery = search ? `&search=${search}` : '';
    const filterQuery = Object.entries(filters)
      .flatMap(([key, value]) => {
        if (Array.isArray(value)) {
          // List type processing (ex. 'tags: [{ name: selectedTag }]' -> 'tags[].name:selectedTag')
          return value.map(item => {
            if (typeof item === 'object' && item !== null) {
              return Object.entries(item)
                .map(([subKey, subValue]) => (subValue ? `${key}[].${subKey}:${subValue}` : ''))
                .filter(Boolean)
                .join(',');
            }
            return item ? `${key}[]:${item}` : '';
          });
        } else if (typeof value === 'object' && value !== null) {
          // Dict type processing (ex. 'inf_params: { max_tokens: selectedMaxTokens }' -> 'inf_params.max_tokens:selectedMaxTokens')
          return Object.entries(value)
            .map(([subKey, subValue]) => (subValue ? `${key}.${subKey}:${subValue}` : ''))
            .filter(Boolean)
            .join(',');
        } else {
          // String type processing (ex. 'names: selectedName' -> 'names:selectedName')
          return value ? `${key}:${value}` : '';
        }
      })
      .filter(Boolean)
      .join(',');
    const sortQuery = sortColumn && sortType ? `&sort=${sortColumn},${sortType}` : '';
    const isActiveQuery = isActive != null && (isActive ? '&is_active=true' : '&is_active=false');

    const filterQueryParam = filterQuery ? `&${useUnderscoreFilter ? '_filter' : 'filter'}=${filterQuery}` : '';

    return `${paginationQuery}${searchQuery}${filterQueryParam}${sortQuery}${isActive == null ? '' : isActiveQuery}`;
  }, [page, size, search, filters, sortColumn, sortType, isActive, useUnderscoreFilter]);

  const {
    isFetching,
    refetch,
    data,
    error: _error,
  } = useQuery<QueryResponse<T>, Error>({
    // 리소스별로 queryKey를 분리하여 서로 다른 fetcher가 섞이지 않도록 함
    queryKey: [queryKey, query, filters, fetchDataParam?.projectId ?? null],
    queryFn: async () => {
      // console.log(`🔍 QueryResponseProvider [${queryKey}] - queryFn 호출됨, query:`, query);
      // console.log(`🔍 QueryResponseProvider [${queryKey}] - fetchDataParam:`, fetchDataParam);
      try {
        const result = await fetchData(query, fetchDataParam);
        // console.log(`🔍 QueryResponseProvider [${queryKey}] - fetchData 결과:`, result);
        fetchCallback?.(result);
        return {
          data: result.data || [],
          pagination: result.payload?.pagination,
        } as QueryResponse<T>;
      } catch (error) {
        // console.error(`🔍 QueryResponseProvider [${queryKey}] - fetchData 에러:`, error);
        // 에러가 발생해도 기본값 반환하여 컴포넌트가 계속 렌더링되도록 함
        return {
          data: [],
          pagination: {
            page: 1,
            totalPages: 1,
            totalItems: 0,
            lastPage: 1,
            last_page: 1,
            total: 0,
            limit: 10,
            size: 1,
            offset: 0,
            search: '',
            links: [],
          },
        } as unknown as QueryResponse<T>;
      }
    },
    gcTime: 5 * 60 * 1000, // 5분 캐시
    staleTime: 2 * 60 * 1000, // 2분간 fresh 상태 유지
    placeholderData: previousData => previousData,
    refetchOnWindowFocus: false,
    refetchOnMount: false, // 마운트 시 자동 리페치 비활성화
    refetchOnReconnect: false, // 재연결 시 자동 리페치 비활성화
    refetchInterval: false, // 폴링 비활성화
    retry: 1, // 재시도 횟수 제한
    retryDelay: 1000, // 재시도 간격
    enabled: false, // 자동 실행 비활성화 (수동으로만 호출)
  });

  const handlePageChange = (page: number) => {
    setPage(page);
  };

  const handleItemsPerPageChange = (itemsPerPage: number) => {
    setPage(1);
    setSize(itemsPerPage);
  };

  const updateSearchTerm = (newSearchTerm: string) => {
    setSearch(newSearchTerm);
    setPage(1);
  };

  const updateFilterParam = (newFilters: Record<string, string>) => {
    setFilters({ ...filters, ...newFilters });
    setPage(1);
  };

  const updateSortParam = (newSortColumn: string, newSortType: string) => {
    setSortColumn(newSortColumn);
    setSortType(newSortType);
  };

  return (
    <QueryResponseContext.Provider
      value={{
        isLoading: isFetching,
        refetch,
        response: data as Response<Array<T>> | undefined,
        pagination: data?.pagination,
        page,
        size,
        setPage,
        setSize,
        setTotalPages,
        handlePageChange,
        handleItemsPerPageChange,
        updateSearchTerm,
        updateFilterParam,
        updateSortParam,
      }}
    >
      {children}
    </QueryResponseContext.Provider>
  );
};

const useQueryResponse = <T,>() => {
  const context = useContext(QueryResponseContext);
  if (!context) {
    throw new Error('useQueryResponse must be used within a QueryResponseProvider');
  }
  return context as QueryResponseContextValue<T>;
};

export {
  QueryResponseProvider,
  // eslint-disable-next-line react-refresh/only-export-components
  useQueryResponse,
};
