// import { type AgentApp } from '@/services/deploy/AgentServing.ts';
import type { GetAgentAppResponse } from '@/services/deploy/agent/types';

type AgentApp = GetAgentAppResponse & {
  deployment_version?: number;
};
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import React, { type FC, useState, useEffect } from 'react';
import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useAtom } from 'jotai';
import {
  selectedAgentAppIdRepoAtom,
  selectedAgentAppNameRepoAtom,
  selectedAgentAppVersionRepoAtom,
  tempSelectedAgentAppAtom,
  isChangeAgentAppAtom,
} from '@/components/agents/builder/atoms/AgentAtom.ts';

type Props = {
  readOnly?: boolean;
  onRowClick?: (id: string, name: string) => void;
  selectedRowId?: string;
  projectId?: string;
  modalId: string;
  nodeId: string;
};

const convertToAgentApp = (response: GetAgentAppResponse): AgentApp => ({
  ...response,
  deployment_version: response.deploymentVersion, // deploymentVersion을 deployment_version으로 매핑
});

const AgentAppList: FC<Props> = ({ readOnly = false, nodeId }) => {
  const [page, setPage] = useState(1);
  const [size] = useState(6);
  const [searchTerm, setSearchTerm] = useState('');

  const [selectedAgentAppIdRepo, setSelectedAgentAppIdRepo] = useAtom(selectedAgentAppIdRepoAtom);
  const [, setSelectedAgentAppNameRepo] = useAtom(selectedAgentAppNameRepoAtom);
  const [, setSelectedAgentAppVersionRepo] = useAtom(selectedAgentAppVersionRepoAtom);
  const [tempSelectedAgentApp, setTempSelectedAgentApp] = useAtom(tempSelectedAgentAppAtom);
  const [, setChangeAgentApp] = useAtom(isChangeAgentAppAtom);

  const currentSelectedAgentAppId = selectedAgentAppIdRepo[nodeId];

  const { data, isSuccess, isLoading, error } = useGetAgentAppList({
    page,
    size,
    targetType: 'all',
    sort: '',
    filter: '',
    search: searchTerm,
  });

  const [dataList, setDataList] = useState<GetAgentAppResponse[]>([]);

  useEffect(() => {
    try {
      if (isSuccess && data) {
        setDataList((data.content as unknown as GetAgentAppResponse[]) || []);
      } else if (error) {
        setDataList([]);
      }
    } catch (error) {
      setDataList([]);
    }
  }, [data, isSuccess, error]);

  useEffect(() => {
    if (currentSelectedAgentAppId && dataList.length > 0) {
      const currentSelected = dataList.find(item => item.id === currentSelectedAgentAppId);
      if (currentSelected) {
        const agentApp = convertToAgentApp(currentSelected);
        setTempSelectedAgentApp(agentApp);
      }
    }
  }, [currentSelectedAgentAppId, dataList]);

  useEffect(() => {
    const applyHandler = () => {
      if (tempSelectedAgentApp && nodeId) {
        setSelectedAgentAppIdRepo(prev => ({
          ...prev,
          [nodeId]: tempSelectedAgentApp.id,
        }));

        setSelectedAgentAppNameRepo(prev => ({
          ...prev,
          [nodeId]: tempSelectedAgentApp.name || tempSelectedAgentApp.id,
        }));

        setSelectedAgentAppVersionRepo(prev => ({
          ...prev,
          [nodeId]: tempSelectedAgentApp.deployment_version || tempSelectedAgentApp.deploymentVersion,
        }));

        setChangeAgentApp(true);
      }
    };

    (window as any).agentAppApplyHandler = applyHandler;

    return () => {
      delete (window as any).agentAppApplyHandler;
    };
  }, [tempSelectedAgentApp, nodeId, setSelectedAgentAppIdRepo, setSelectedAgentAppNameRepo, setSelectedAgentAppVersionRepo, setChangeAgentApp]);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const updateSearchTerm = (term: string) => {
    setSearchTerm(term);
    setPage(1);
  };

  const convertedData: AgentApp[] = dataList.map(convertToAgentApp);

  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        headerName: '배포명',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '빌더명',
        field: 'builderName' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '설명',
        field: 'description',
        width: 280,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value || '-'}
            </div>
          );
        }),
      },
      {
        headerName: '버전',
        field: 'deployment_version',
        width: 100,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );
  const gridData = React.useMemo(
    () =>
      convertedData.map((item, index) => ({
        ...item,
        no: (page - 1) * size + index + 1,
      })),
    [convertedData, page, size]
  );

  const selectedGridData = React.useMemo(() => {
    const result = tempSelectedAgentApp ? gridData.filter(item => item.id === tempSelectedAgentApp.id) : [];
    return result;
  }, [tempSelectedAgentApp, gridData]);

  return (
    <>
      <section className='section-modal'>
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='flex justify-between items-center w-full'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
                  </div>
                </div>
                {!readOnly && (
                  <div>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchTerm}
                        placeholder='배포명 입력'
                        onChange={e => {
                          setSearchTerm(e.target.value);
                        }}
                        onKeyDown={e => {
                          if (e.key === 'Enter') {
                            updateSearchTerm(searchTerm);
                          }
                        }}
                      />
                    </div>
                  </div>
                )}
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='single-select'
                rowData={gridData}
                columnDefs={columnDefs}
                selectedDataList={selectedGridData}
                loading={isLoading}
                onCheck={(selectedItems: any[]) => {
                  if (selectedItems && selectedItems.length > 0) {
                    const selectedItem = selectedItems[0];
                    if (tempSelectedAgentApp?.id === selectedItem.id) {
                      return;
                    }

                    setTempSelectedAgentApp(selectedItem);
                  }
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination currentPage={page} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </section>
    </>
  );
};

type AgentAppModalParamProps = {
  modalId: string;
  nodeId: string;
  nodeType?: string;
};

export const SelectAgentAppPop: FC<AgentAppModalParamProps & { readOnly?: boolean }> = ({ modalId, nodeId, readOnly = false }) => {
  return <AgentAppList projectId={''} modalId={modalId} nodeId={nodeId} readOnly={readOnly} />;
};
