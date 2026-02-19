import React, { useState, useMemo, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSetAtom } from 'jotai';

import { UIGroup, UIDropdown, UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UIButton2, UIBox, UILabel } from '@/components/UI/atoms';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { useGetGpuNodeResources } from '@/services/admin/resrcMgmt';
import { selectedGpuNodeAtom } from '@/stores/admin/resrcMgmt';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { useApiQuery } from '@/hooks/common/api/useApi';
import { api } from '@/configs/axios.config';

interface SearchValues {
  searchType: string;
  nodeGroupType: string;
}

export const ResrcMgmtGpuNodePage = () => {
  const navigate = useNavigate();
  const setSelectedGpuNode = useSetAtom(selectedGpuNodeAtom);

  const { data: gpuNodeResourcesRaw } = useGetGpuNodeResources(undefined, {
    refetchOnMount: 'always',
    staleTime: 0,
  });

  const [agentGroupById, setAgentGroupById] = useState<Record<string, string>>({});
  // 에이전트별 상세 데이터 저장 (group, availableSlots, occupiedSlots 포함)
  // 사용 예시: agentDataById[normalizedId]?.availableSlots, agentDataById[normalizedId]?.occupiedSlots
  const [agentDataById, setAgentDataById] = useState<Record<string, { group: string; availableSlots?: any; occupiedSlots?: any }>>({});

  // API 데이터 처리 (배열 또는 객체 형태 모두 지원)
  const gpuNodeResources = useMemo(() => {
    // API 데이터가 배열 형태로 들어오는 경우
    if (gpuNodeResourcesRaw?.nodes && Array.isArray(gpuNodeResourcesRaw.nodes)) {
      // API 데이터만 사용하고 agentDataById의 availableSlots, occupiedSlots 병합
      const processedNodes = gpuNodeResourcesRaw.nodes.map((apiNode: any) => {
        const displayName = apiNode.display_name || '';
        const normalizedId = String(displayName).replace(/^agent-/, '');
        const agentData = agentDataById[normalizedId];
        
        return {
          ...apiNode,
          ...(agentData ? {
            availableSlots: agentData.availableSlots,
            occupiedSlots: agentData.occupiedSlots,
          } : {}),
        };
      });

      return {
        ...gpuNodeResourcesRaw,
        nodes: processedNodes,
      };
    }

    // API 데이터가 객체 형태로 들어오는 경우
    if (gpuNodeResourcesRaw?.nodes && typeof gpuNodeResourcesRaw.nodes === 'object' && !Array.isArray(gpuNodeResourcesRaw.nodes)) {
      const processedNodesObj: Record<string, any> = {};
      
      // API 데이터만 사용하고 agentDataById의 availableSlots, occupiedSlots 병합
      Object.entries(gpuNodeResourcesRaw.nodes).forEach(([key, node]: [string, any]) => {
        const normalizedId = String(node?.display_name || key).replace(/^agent-/, '');
        const agentData = agentDataById[normalizedId];
        
        processedNodesObj[key] = {
          ...node,
          ...(agentData ? {
            availableSlots: agentData.availableSlots,
            occupiedSlots: agentData.occupiedSlots,
          } : {}),
        };
      });

      return {
        ...gpuNodeResourcesRaw,
        nodes: processedNodesObj,
      };
    }

    // API 데이터가 없으면 빈 배열 반환
    return {
      nodes: [],
    };
  }, [gpuNodeResourcesRaw, agentDataById]);

  const [searchValues, setSearchValues] = useState<SearchValues>({
    searchType: '전체',
    nodeGroupType: '전체',
  });

  const [filteredNodes, setFilteredNodes] = useState<Array<[string, any]>>([]);
  const [isFiltered, setIsFiltered] = useState(false);

  const [searchValue1, setSearchValue1] = useState('');

  const { data: scalingGroupsResp } = useApiQuery<{
    success: boolean;
    message?: string;
    data?: {
      scalingGroups?: Array<{ name: string; [key: string]: any }>;
    };
    scalingGroups?: Array<{ name: string; [key: string]: any }>;
  }>({
    queryKey: ['resources-scaling-groups'],
    url: '/resources/scaling_groups',
    refetchOnMount: 'always',
    staleTime: 0,
    gcTime: 0,
  });

  useEffect(() => {
    const groups =
      (Array.isArray((scng => scng?.scalingGroups)(scalingGroupsResp)) &&
        (scalingGroupsResp as any).scalingGroups) ||
      (Array.isArray((scng => scng?.data?.scalingGroups)(scalingGroupsResp)) &&
        (scalingGroupsResp as any).data?.scalingGroups) ||
      [];

    const names = (groups as Array<{ name?: string }>)
      .map(g => g?.name)
      .filter((n): n is string => Boolean(n))


    if (names.length > 0) {
      (async () => {
        try {
          const results = await Promise.all(
            names.map(async (groupName) => {
              const res = await api.get('/resources/agents', {
                params: { scaling_group: groupName },
              });
              const items: any[] =
                res?.data?.data?.agent_list?.items && Array.isArray(res.data.data.agent_list.items)
                  ? res.data.data.agent_list.items
                  : [];
              return items.map((it) => ({ ...it, group: groupName }));
            })
          );
          const flat = ([] as any[]).concat(...results);
          
          const map: Record<string, string> = {};
          const dataMap: Record<string, { group: string; availableSlots?: any; occupiedSlots?: any }> = {};
          for (const it of flat) {
            if (it && typeof it.id === 'string' && it.group) {
              map[it.id] = String(it.group);
              // availableSlots와 occupiedSlots도 함께 저장 (없을 경우 undefined로 할당되어도 안전)
              dataMap[it.id] = {
                group: String(it.group),
                ...(it.availableSlots != null && { availableSlots: it.availableSlots }),
                ...(it.occupiedSlots != null && { occupiedSlots: it.occupiedSlots }),
              };
            }
          }
          setAgentGroupById(map);
          setAgentDataById(dataMap);
        } catch {
          // 에이전트 그룹 정보를 가져오지 못해도 기존 상태를 유지합니다.
        }
      })();
    }
  }, [scalingGroupsResp]);

  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

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

  const transformNodes = (nodesData: any): Array<[string, any]> => {
    if (!nodesData) return [];

    if (Array.isArray(nodesData)) {
      return nodesData.map((node: any, index: number) => {
        const derivedName = node?.display_name ?? node?.nodeName ?? node?.node_name ?? String(index);
        return [derivedName, node];
      });
    }

    return Object.entries(nodesData).map(([key, node]: [string, any]) => {
      const derivedName = node?.display_name ?? node?.nodeName ?? node?.node_name ?? key;
      return [derivedName, node];
    });
  };

  const getVisibleDisplayName = (value?: string) => {
    if (!value) {
      return '';
    }
    return value.startsWith('agent-i-') ? value.replace(/^agent-i-/, '') : value;
  };

  const isNodeOverloaded = (nodeData: any): boolean => {
    const cpuLimit = Number(nodeData?.cpu_limit) || 0;
    const memoryLimit = Number(nodeData?.memory_limit) || 0;
    const gpuLimit = Number(nodeData?.gpu_limit) || 0;

    const cpuUsage = Number(nodeData?.cpu_usage) || 0;
    const memoryUsage = Number(nodeData?.memory_usage) || 0;
    const gpuUsage = Number(nodeData?.gpu_usage) || 0;

    const cpuOver = cpuLimit > 0 && cpuUsage >= cpuLimit;
    const memoryOver = memoryLimit > 0 && memoryUsage >= memoryLimit;
    const gpuOver = gpuLimit > 0 && gpuUsage >= gpuLimit;

    return cpuOver || memoryOver || gpuOver;
  };

  const applyFilters = () => {
    if (!gpuNodeResources?.nodes) {
      setFilteredNodes([]);
      return;
    }

    const nodes = transformNodes(gpuNodeResources.nodes);

    const filtered = nodes.filter(([nodeName, nodeData]) => {
      const displayName = nodeData.display_name || nodeName;
      const normalizedId = String(displayName).replace(/^agent-/, '');
      const resolvedGroup = agentGroupById[normalizedId] ?? nodeData.service_group;
      
      // "upload" 그룹은 필터링
      if (resolvedGroup && resolvedGroup.toLowerCase() === 'upload') {
        return false;
      }
      if (nodeData.service_group && nodeData.service_group.toLowerCase() === 'upload') {
        return false;
      }
      
      const matchesSearch = searchValue1 === '' || displayName.toLowerCase().includes(searchValue1.toLowerCase());

      let matchesGroup = true;
      if (searchValues.nodeGroupType !== '전체') {
        matchesGroup = nodeData.service_group === searchValues.nodeGroupType || resolvedGroup === searchValues.nodeGroupType;
      }

      const isOverloaded = isNodeOverloaded(nodeData);

      let matchesStatus = true;
      if (searchValues.searchType === 'val2') {
        matchesStatus = !isOverloaded;
      } else if (searchValues.searchType === 'val3') {
        matchesStatus = isOverloaded;
      }

      return matchesSearch && matchesGroup && matchesStatus;
    });

    setFilteredNodes(filtered);
  };

  const handleSearch = () => {
    applyFilters();
    setIsFiltered(true);
  };

  React.useEffect(() => {
    if (gpuNodeResources?.nodes && !isFiltered) {
      const allNodes = transformNodes(gpuNodeResources.nodes);
      setFilteredNodes(allNodes);
    }
  }, [gpuNodeResources?.nodes, isFiltered]);

  const nodeGroupOptions = useMemo(() => {
    if (!gpuNodeResources?.nodes) {
      return [{ value: '전체', label: '전체' }];
    }

    // 배열 또는 객체 형태 모두 처리
    const nodes = Array.isArray(gpuNodeResources.nodes) 
      ? gpuNodeResources.nodes 
      : Object.values(gpuNodeResources.nodes);
    const groupsSet = new Set<string>();

    nodes.forEach((node: any) => {
      if (node.service_group && node.service_group.toLowerCase() !== 'upload') {
        groupsSet.add(node.service_group);
      }

      const displayName = node?.display_name ?? node?.nodeName ?? node?.node_name ?? '';
      const normalizedId = String(displayName).replace(/^agent-/, '');
      const resolvedGroup = agentGroupById[normalizedId] ?? node?.service_group;
      if (resolvedGroup && resolvedGroup.toLowerCase() !== 'upload') {
        groupsSet.add(resolvedGroup);
      }
    });

    const uniqueGroups = Array.from(groupsSet).filter(Boolean);

    return [
      { value: '전체', label: '전체' },
      ...uniqueGroups.map(group => ({
        value: group,
        label: group,
      })),
    ];
  }, [gpuNodeResources?.nodes, agentGroupById]);

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
                        검색
                      </UITypography>
                    </th>
                    <td>
                      <div className='flex-1'>
                        <UIInput.Search
                          value={searchValue1}
                          placeholder='노드명 입력'
                          onChange={e => {
                            setSearchValue1(e.target.value);
                          }}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        상태
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
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        노드 그룹명
                      </UITypography>
                    </th>
                    <td>
                      <UIDropdown
                        value={searchValues.nodeGroupType}
                        placeholder='노드 그룹 선택'
                        options={nodeGroupOptions}
                        isOpen={dropdownStates.publicRange}
                        onClick={() => handleDropdownToggle('publicRange')}
                        onSelect={value => handleDropdownSelect('nodeGroupType', value)}
                      />
                    </td>
                    <th></th>
                    <td></td>
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
        <div className='article-body'>
          {filteredNodes && filteredNodes.length > 0 ? (
            <>
              {Array.from({ length: Math.ceil(filteredNodes.length / 2) }, (_, rowIndex) => (
                <div key={rowIndex} className='chart-container grid-col2 mt-4'>
                  {filteredNodes.slice(rowIndex * 2, rowIndex * 2 + 2).map(([nodeName, nodeData]: [string, any], index: number) => {
                    const displayName: string = nodeData?.display_name ?? nodeName;
                    const visibleDisplayName = getVisibleDisplayName(displayName);
                    const normalizedId = String(displayName).replace(/^agent-/, '');
                    const resolvedGroup: string = agentGroupById[normalizedId] ?? (nodeData?.service_group as string);
                    
                    // 그래프의 각 자원별 value와 total로 과부하 판단
                    // occupiedSlots나 availableSlots가 없을 경우 0으로 처리
                    const cpuValue = nodeData.occupiedSlots?.cpu != null 
                      ? (typeof nodeData.occupiedSlots.cpu === 'string' ? parseFloat(nodeData.occupiedSlots.cpu) : Number(nodeData.occupiedSlots.cpu) || 0)
                      : 0;
                    const cpuTotal = nodeData.availableSlots?.cpu != null 
                      ? (typeof nodeData.availableSlots.cpu === 'string' ? parseFloat(nodeData.availableSlots.cpu) : Number(nodeData.availableSlots.cpu) || 0)
                      : 0;
                    const memValue = nodeData.occupiedSlots?.mem != null 
                      ? ((typeof nodeData.occupiedSlots.mem === 'string' ? parseFloat(nodeData.occupiedSlots.mem) : Number(nodeData.occupiedSlots.mem) || 0) / (1024 ** 3))
                      : 0;
                    const memTotal = nodeData.availableSlots?.mem != null 
                      ? ((typeof nodeData.availableSlots.mem === 'string' ? parseFloat(nodeData.availableSlots.mem) : Number(nodeData.availableSlots.mem) || 0) / (1024 ** 3))
                      : 0;
                    const gpuValue = nodeData.occupiedSlots != null 
                      ? (typeof nodeData.occupiedSlots?.['cuda.shares'] === 'string' 
                          ? parseFloat(nodeData.occupiedSlots['cuda.shares']) 
                          : (nodeData.occupiedSlots?.['cuda.shares'] ?? nodeData.occupiedSlots?.cuda_shares ?? 0))
                      : 0;
                    const gpuTotal = nodeData.availableSlots != null 
                      ? (typeof nodeData.availableSlots?.['cuda.shares'] === 'string' 
                          ? parseFloat(nodeData.availableSlots['cuda.shares']) 
                          : (nodeData.availableSlots?.['cuda.shares'] ?? nodeData.availableSlots?.cuda_shares ?? 0))
                      : 0;
                    
                    const cpuOver = cpuTotal > 0 && cpuValue >= cpuTotal;
                    const memoryOver = memTotal > 0 && memValue >= memTotal;
                    const gpuOver = gpuTotal > 0 && gpuValue >= gpuTotal;
                    const overloaded = cpuOver || memoryOver || gpuOver;
                    return (
                    <div
                      key={nodeName}
                      className={`chart-item flex-1${index === 0 ? ' !p-5' : ''}`}
                      onClick={() => {
                        const nodeStatus = overloaded ? 'overloaded' : 'normal';

                        const finalServiceGroup = resolvedGroup || nodeData.service_group;
                        const nodeDataWithResolvedGroup = {
                          ...nodeData,
                          service_group: finalServiceGroup,
                        };

                        setSelectedGpuNode({
                          nodeName: nodeData?.display_name ?? nodeName,
                          nodeStatus: nodeStatus,
                          nodeData: nodeDataWithResolvedGroup,
                        });
                        try {
                          sessionStorage.setItem(
                            'selectedGpuNode',
                            JSON.stringify({
                              nodeName: nodeData?.display_name ?? nodeName,
                              nodeStatus: nodeStatus,
                              nodeData: nodeDataWithResolvedGroup,
                            })
                          );
                        } catch {
                          // 세션 저장이 불가능한 환경에서도 상세 페이지 이동은 허용합니다.
                        }

                        navigate('/admin/resrc-mgmt/gpu-node-detail');
                      }}
                    >
                      <div className='chart-header flex !items-center'>
                        <UIGroup gap={8} direction='column' className='basis-[400px]'>
                          <UITypography variant='title-3' className='text-sb'>
                          {visibleDisplayName}
                          </UITypography>
                          <UITypography variant='body-1' className='secondary-neutral-700'>
                          {resolvedGroup ?? nodeData.service_group}
                          </UITypography>
                        </UIGroup>
                        <UILabel variant='badge' intent={overloaded ? 'error' : 'complete'}>
                          {overloaded ? '과부하' : '정상'}
                        </UILabel>
                      </div>
                      <div className='grid grid-cols-2 relative -top-[20px] justify-items-center chart-graph h-[480px] justify-center items-center !bg-transparent'>
                        <UICircleChart.Half 
                          type='CPU' 
                          value={parseFloat(cpuValue.toFixed(1))} 
                          total={parseFloat(cpuTotal.toFixed(1))}
                          usedLabel='할당량'
                          availableLabel='여유량'
                        />
                        <UICircleChart.Half
                          type='MemoryMB'
                          value={parseFloat(memValue.toFixed(1))}
                          total={parseFloat(memTotal.toFixed(1))}
                          usedLabel='할당량'
                          availableLabel='여유량'                          
                        />
                        <UICircleChart.Half 
                          type='GPU' 
                          value={parseFloat(gpuValue.toFixed(1))} 
                          total={parseFloat(gpuTotal.toFixed(1))}
                          usedLabel='할당량'
                          availableLabel='여유량'                          
                        />
                      </div>
                    </div>
                  )})}
                </div>
              ))}
            </>
          ) : (
            <div className='w-full flex items-center justify-center h-[115px] py-[146px] text-gray-500'>
                <div className='text-center'>
                  <UIIcon2 className='ic-system-80-default-nodata mb-3' />
                  <div className='text-base font-normal leading-6 text-[#7E889B]'>조회된 결과가 없습니다.</div>
                </div>
              </div>
          )}
        </div>
      </UIArticle>
    </>
  );
};
