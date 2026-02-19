import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSetAtom } from 'jotai';

import { UICircleChart } from '@/components/UI/molecules/chart';
import { UIDataCnt, UIToggle } from '@/components/UI';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGroup, UIInput } from '@/components/UI/molecules';
import { UITypography } from '@/components/UI/atoms';
import { env } from '@/constants/common/env.constants';
import { useGetPortalResources } from '@/services/admin/resrcMgmt';
import { useGetClusterResources } from '@/services/deploy/agent/agentDeploy.services';
import { useGetScalingGroups } from '@/services/home/dashboard/dashboard.services';
import { useDeleteIde } from '@/services/home/webide/ide.services';
import { modelSessionsAtom } from '@/stores/admin/resrcMgmt';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useModal } from '@/stores/common/modal';

import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGrid } from '@/components/UI/molecules/grid';

export const ResrcMgmtPortalPage = () => {
  const navigate = useNavigate();
  const setModelSessions = useSetAtom(modelSessionsAtom);
  const { openConfirm } = useModal();

  const [searchType, setSearchType] = useState('userName');
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6;

  // 실제 API 호출에 사용할 검색 조건 (검색 버튼을 눌렀을 때만 업데이트)
  const [appliedSearchParams, setAppliedSearchParams] = useState<{
    searchType: string;
    searchValue?: string;
  }>({
    searchType: 'userName',
    searchValue: undefined,
  });

  const { data: portalResources, refetch: refetchPortalResources } = useGetPortalResources(
    {
      searchType: appliedSearchParams.searchType,
      searchValue: appliedSearchParams.searchValue,
    },
    {
      refetchOnMount: 'always',
      staleTime: 0,
    }
  );

  // 클러스터 리소스 조회 API 호출
  // 응답 데이터 구조: { node_resource, namespace_resource, task_policy, task_quota }
  const { data: clusterResources, refetch: refetchClusterResources } = useGetClusterResources(
    { nodeType: 'agent' },
    {
      refetchOnMount: 'always',
      staleTime: 0,
    }
  );

  // 리소스 조회
  const { data: scalingGroupData, refetch: refetchScalingGroups } = useGetScalingGroups(
    { isActive: true },
    {
      staleTime: 0, // 데이터를 항상 stale로 취급
      refetchOnMount: 'always', // 마운트 시 항상 refetch
    }
  );

  // 두 API를 함께 실행하는 함수
  const refetchResources = async () => {
    await Promise.all([refetchClusterResources(), refetchScalingGroups()]);
  };

  const onSearchClick = () => {
    // 검색 버튼 클릭 시에만 실제 검색 파라미터 업데이트
    setAppliedSearchParams({
      searchType: searchType,
      searchValue: searchValue || undefined,
    });
    // 페이지 초기화
    setCurrentPage(1);
    // API 호출
    setTimeout(() => {
      refetchPortalResources();
      refetchResources();
    }, 0);
  };

  const [parsedMetrics, setParsedMetrics] = useState<{
    agentCpuUsage: number;
    agentCpuRequests: number;
    agentCpuLimit: number;
    agentMemoryUsage: number;
    agentMemoryRequests: number;
    agentMemoryLimit: number;
    modelCpuUsage: number;
    modelCpuRequests: number;
    modelMemoryUsage: number;
    modelMemoryRequests: number;
    modelGpuUsage: number;
    modelGpuRequests: number;
    ideTotalCount: number;
    ideActiveCount: number;
    ideStoppedCount: number;
    ideErrorCount: number;
    ideResources: any[];
  }>({
    agentCpuUsage: 0,
    agentCpuRequests: 0,
    agentCpuLimit: 0,
    agentMemoryUsage: 0,
    agentMemoryRequests: 0,
    agentMemoryLimit: 0,
    modelCpuUsage: 0,
    modelCpuRequests: 0,
    modelMemoryUsage: 0,
    modelMemoryRequests: 0,
    modelGpuUsage: 0,
    modelGpuRequests: 0,
    ideTotalCount: 0,
    ideActiveCount: 0,
    ideStoppedCount: 0,
    ideErrorCount: 0,
    ideResources: [],
  });

  // 바이트를 기가바이트로 변환하고 소수점 1자리로 제한 (버림 처리)
  const bytesToGB = (bytes: number): number => {
    return Math.floor((bytes / 1024 ** 3) * 10) / 10;
  };

  // 소수점 1자리로 버림 처리 (부동소수점 오차 보정)
  const normalizeFloat = (num: number): number => {
    return Math.floor((num + 1e-9) * 10) / 10;
  };

  // 모델 배포 리소스 계산
  const { modelCpuQuota, modelMemQuota, modelGpuQuota, modelCpuUsed, modelMemUsed, modelGpuUsed } = useMemo(() => {
    let cpuQuota = 0;
    let memQuota = 0;
    let gpuQuota = 0;
    let cpuUsed = 0;
    let memUsed = 0;
    let gpuUsed = 0;

    // 모델 배포는 'INF'로 시작하는 그룹만 필터링
    const filterGroupName = 'INF';
    const scalingGroupList = scalingGroupData?.scalingGroups?.filter(group => ['E-LOCAL', 'E-DEV'].includes(env.VITE_RUN_MODE) || group?.name?.startsWith(filterGroupName)) || [];

    scalingGroupList.forEach(group => {
      // schedulable이 true인 에이전트만 필터링
      const schedulableAgents = group.agentList?.filter(agent => agent.schedulable) || [];

      // 각 에이전트의 availableSlots와 occupiedSlots를 합산
      const availableSlots: Record<string, number> = {};
      const occupiedSlots: Record<string, number> = {};

      schedulableAgents.forEach(agent => {
        // availableSlots 합산
        if (agent.availableSlots) {
          Object.entries(agent.availableSlots).forEach(([key, value]) => {
            const numValue = typeof value === 'string' ? parseFloat(value) : (value as number);
            if (!isNaN(numValue)) {
              availableSlots[key] = (availableSlots[key] || 0) + numValue;
            }
          });
        }

        // occupiedSlots 합산
        if (agent.occupiedSlots) {
          Object.entries(agent.occupiedSlots).forEach(([key, value]) => {
            const numValue = typeof value === 'string' ? parseFloat(value) : (value as number);
            if (!isNaN(numValue)) {
              occupiedSlots[key] = (occupiedSlots[key] || 0) + numValue;
            }
          });
        }
      });

      // CPU 설정
      const cpuQuotaStr = availableSlots.cpu;
      const cpuUsedStr = occupiedSlots.cpu;
      if (cpuQuotaStr) cpuQuota += cpuQuotaStr;
      if (cpuUsedStr) cpuUsed += cpuUsedStr;

      // Memory 설정 (바이트를 GB로 변환)
      const memQuotaStr = availableSlots.mem;
      const memUsedStr = occupiedSlots.mem;
      if (memQuotaStr) memQuota += bytesToGB(memQuotaStr);
      if (memUsedStr) memUsed += bytesToGB(memUsedStr);

      // GPU 설정 (cuda.shares 또는 cuda_shares)
      const gpuQuotaStr = availableSlots['cuda.shares'] || availableSlots.cuda_shares;
      const gpuUsedStr = occupiedSlots['cuda.shares'] || occupiedSlots.cuda_shares;
      if (gpuQuotaStr) gpuQuota += gpuQuotaStr;
      if (gpuUsedStr) gpuUsed += gpuUsedStr;
    });

    return {
      modelCpuQuota: normalizeFloat(cpuQuota),
      modelMemQuota: normalizeFloat(memQuota),
      modelGpuQuota: normalizeFloat(gpuQuota),
      modelCpuUsed: normalizeFloat(cpuUsed),
      modelMemUsed: normalizeFloat(memUsed),
      modelGpuUsed: normalizeFloat(gpuUsed),
    };
  }, [scalingGroupData]);

  const formatExpireAt = (expireAt?: string) => {
    if (!expireAt) return '';

    try {
      const date = new Date(expireAt);
      if (!Number.isNaN(date.getTime())) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
      }
    } catch (error) {
      // fallback to manual formatting below
    }

    const normalized = expireAt.replace('T', ' ').replace(/-/g, '.');
    return normalized.length >= 19 ? normalized.slice(0, 19) : normalized;
  };

  useEffect(() => {
    if (portalResources) {
      const response = portalResources;

      let newParsedMetrics: typeof parsedMetrics = {
        agentCpuUsage: 0,
        agentCpuLimit: 0,
        agentCpuRequests: 0,
        agentMemoryUsage: 0,
        agentMemoryLimit: 0,
        agentMemoryRequests: 0,
        modelCpuUsage: 0,
        modelCpuRequests: 0,
        modelMemoryUsage: 0,
        modelMemoryRequests: 0,
        modelGpuUsage: 0,
        modelGpuRequests: 0,
        ideTotalCount: 0,
        ideActiveCount: 0,
        ideStoppedCount: 0,
        ideErrorCount: 0,
        ideResources: [],
      };

      if (response.components?.Agent) {
        const agent = response.components.Agent;
        newParsedMetrics.agentCpuUsage = agent.cpu_usage || 0;
        newParsedMetrics.agentCpuRequests = agent.cpu_request || 0;
        newParsedMetrics.agentCpuLimit = agent.cpu_limit || 0;

        newParsedMetrics.agentMemoryUsage = agent.memory_usage || 0;
        newParsedMetrics.agentMemoryRequests = agent.memory_request || 0;
        newParsedMetrics.agentMemoryLimit = agent.memory_limit || 0;
      }

      if (response.components?.Model) {
        const model = response.components.Model;
        newParsedMetrics.modelCpuRequests = model.cpu_request || 0;
        newParsedMetrics.modelCpuUsage = model.cpu_usage || 0;
        newParsedMetrics.modelMemoryRequests = model.memory_request || 0;
        newParsedMetrics.modelMemoryUsage = model.memory_usage || 0;
        newParsedMetrics.modelGpuRequests = model.gpu_request || 0;
        newParsedMetrics.modelGpuUsage = model.gpu_usage || 0;

        if (model.sessions && Array.isArray(model.sessions)) {
          setModelSessions(model.sessions);
        }
      }

      if (response.ideResources) {
        newParsedMetrics.ideTotalCount = response.ideResources.length || 0;
        newParsedMetrics.ideActiveCount = response.ideResources.length || 0;
        newParsedMetrics.ideStoppedCount = 0;
        newParsedMetrics.ideErrorCount = 0;
        newParsedMetrics.ideResources = response.ideResources || [];
      }

      setParsedMetrics(newParsedMetrics);
    }
  }, [portalResources]);

  // 페이지네이션을 위한 데이터 필터링
  const paginatedIdeResources = useMemo(() => {
    if (!Array.isArray(parsedMetrics.ideResources) || parsedMetrics.ideResources.length === 0) {
      return [];
    }
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    return parsedMetrics.ideResources.slice(startIndex, endIndex);
  }, [parsedMetrics.ideResources, currentPage, itemsPerPage]);

  // UIGrid용 rowData 변환
  const rowData = useMemo(() => {
    return paginatedIdeResources.map((ide: any, index: number) => ({
      id: ide.id || `ide-${index}`,
      no: (currentPage - 1) * itemsPerPage + index + 1,
      username: ide.username || '',
      bankNum: ide.userId || '',
      ide: ide.imageType || '',
      cpu: ide.cpu ? `${parseFloat(ide.cpu).toFixed(1)}` : '',
      memory: ide.memory ? `${parseFloat(ide.memory).toFixed(1)}` : '',
      dwId: ide.dwAccountId || '',
      expireAt: formatExpireAt(ide.expireAt),
      rawData: ide,
      imageName: ide.imageName || '',
    }));
  }, [paginatedIdeResources, currentPage, itemsPerPage]);

  // UIGrid 컬럼 정의
  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '사용자명',
        field: 'username',
        width: 160,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '행번',
        field: 'bankNum',
        width: 160,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '도구명',
        field: 'ide',
        width: 200,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '이미지명',
        field: 'imageName',
        minWidth: 200,
        flex: 1,
        showTooltip: true,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '계정 ID',
        field: 'dwId',
        width: 170,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: 'CPU 할당량 (Core)',
        field: 'cpu',
        width: 160,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: 'Memory 할당량 (GiB)',
        field: 'memory',
        width: 160,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: 'IDE 만료 일시',
        field: 'expireAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상태',
        field: 'status',
        width: 80,
        cellStyle: {
          paddingLeft: '16px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        cellRenderer: React.memo(({ data }: any) => {
          const { mutate: deleteIde } = useDeleteIde({
            onSuccess: () => {
              refetchPortalResources();
            },
          });

          // 백엔드에서 status 필드가 제거됨 - 항상 활성 상태로 표시
          return (
            <div style={{ display: 'flex', alignItems: 'center', height: '100%' }}>
              <UIToggle
                checked={true}
                onChange={async _checked => {
                  // 토글을 끄면 IDE 삭제 확인 모달 표시
                  await openConfirm({
                    title: '안내',
                    message: '현재 선택한 사용자의 IDE를 강제 종료 하시겠습니까?',
                    confirmText: '예',
                    cancelText: '아니요',
                    onConfirm: () => {
                      // IDE 삭제 처리 로직
                      if (data?.rawData?.ideStatusId) {
                        deleteIde({ ideId: String(data.rawData.ideStatusId) });
                      }
                    },
                  });
                }}
              />
            </div>
          );
        }),
      },
    ],
    [refetchPortalResources]
  );

  const totalPages = useMemo(() => {
    return Math.ceil((parsedMetrics.ideResources?.length || 0) / itemsPerPage) || 1;
  }, [parsedMetrics.ideResources?.length, itemsPerPage]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const GridComponent = useMemo(
    () => <UIGrid type='default' rowData={rowData} loading={false} columnDefs={columnDefs as any} noDataMessage='조회된 결과가 없습니다.' />,
    [rowData, columnDefs]
  );
  return (
    <>
      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            배포 자원 현황
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='chart-container mt-4'>
            <div className='chart-item flex-1 h-[326px]' onClick={() => navigate('/admin/resrc-mgmt/portal-agent')}>
              <div className='chart-header mb-4'>
                <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
                  에이전트 배포
                </UITypography>
              </div>
              <div className='flex chart-graph h-[210px] gap-x-10 justify-center items-center'>
                <div className='w-[240px] flex items-center justify-center'>
                  <UICircleChart.Half
                    type='CPU'
                    value={parseFloat((clusterResources?.namespace_resource?.cpu_used || 0).toFixed(1))}
                    total={parseFloat((clusterResources?.namespace_resource?.cpu_quota || 0).toFixed(1))}
                    usedLabel='할당량'
                    availableLabel='여유량'
                  />
                </div>
                <div className='w-[240px] flex items-center justify-center'>
                  <UICircleChart.Half
                    type='Memory'
                    value={parseFloat((clusterResources?.namespace_resource?.mem_used || 0).toFixed(1))}
                    total={parseFloat((clusterResources?.namespace_resource?.mem_quota || 0).toFixed(1))}
                    usedLabel='할당량'
                    availableLabel='여유량'
                  />
                </div>
              </div>
            </div>
            <div className='chart-item flex-1' onClick={() => navigate('/admin/resrc-mgmt/portal-model')}>
              <div className='chart-header mb-4'>
                <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
                  모델 배포
                </UITypography>
              </div>
              <div className='flex chart-graph h-[210px] gap-x-5 justify-center'>
                <div className='w-[240px] flex items-center justify-center'>
                  <UICircleChart.Half type='CPU' value={modelCpuUsed} total={modelCpuQuota} usedLabel='할당량' availableLabel='여유량' />
                </div>
                <div className='w-[240px] flex items-center justify-center'>
                  <UICircleChart.Half type='MemoryMB' value={modelMemUsed} total={modelMemQuota} usedLabel='할당량' availableLabel='여유량' />
                </div>
                <div className='w-[240px] flex items-center justify-center'>
                  <UICircleChart.Half type='GPU' value={modelGpuUsed} total={modelGpuQuota} usedLabel='할당량' availableLabel='여유량' />
                </div>
              </div>
            </div>
          </div>
        </div>
      </UIArticle>

      <UIArticle className='article-grid'>
        <div className='article-header'>
          <UIGroup direction='column' gap={8}>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              IDE 자원 현황
            </UITypography>
          </UIGroup>
        </div>
        <div className='article-body'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='flex justify-between items-center w-full'>
                <div className='flex-shrink-0'>
                  <UIDataCnt count={parsedMetrics.ideResources?.length || 0} prefix='실행 중 IDE 총' unit='건' />
                </div>
                <div>
                  <UIGroup direction='row' gap={8}>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={searchType}
                        options={[
                          { value: 'userName', label: '사용자명' },
                          { value: 'BankNum', label: '행번' },
                          { value: 'dwAccountId', label: '계정ID' },
                        ]}
                        onSelect={value => setSearchType(value)}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>
                    <div className='w-[360px] h-[40px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          const value = e.target.value;
                          if (value.length <= 50) {
                            setSearchValue(value);
                          }
                        }}
                        onKeyDown={e => {
                          if (e.key === 'Enter') {
                            onSearchClick();
                          }
                        }}
                        placeholder='검색어 입력'
                      />
                    </div>
                  </UIGroup>
                </div>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>{GridComponent}</UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </div>
      </UIArticle>
    </>
  );
};
