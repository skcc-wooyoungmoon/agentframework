// noinspection DuplicatedCode

import { tempSelectListAtom, selectedListAtom, selectedAtom } from '@/components/agents/builder/atoms/toolsAtom.ts';
import { type Tool } from '@/components/agents/builder/types/Tools.ts';
import { useAtom, useSetAtom } from 'jotai/react';
import { type FC, useEffect, useState } from 'react';
import React, { useMemo } from 'react';
import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetAgentToolList } from '@/services/agent/tool/agentTool.services';

const InferenceToolsList: FC = () => {
  const [tempSelectList, setTempSelectList] = useAtom<Tool[]>(tempSelectListAtom);
  const setSelectedToolsRepo = useSetAtom(selectedListAtom);
  const setSelectedToolRepo = useSetAtom(selectedAtom);
  const [page, setPage] = useState(1);
  const [size] = useState(6);
  const [searchTerm, setSearchTerm] = useState('');

  const currentMode = (window as any).currentMode || 'multiple';
  const isSingleMode = currentMode === 'single';

  const { data, isSuccess, isLoading, refetch } = useGetAgentToolList(
    {
      page: page,
      size,
      sort: 'created_at,desc',
      search: searchTerm || '',
    },
    {
      enabled: true,
    }
  );

  const [dataList, setDataList] = useState<Tool[]>([]);

  useEffect(() => {
    const nodeId = (window as any).currentNodeId;

    if (nodeId) {
      const currentSelected = (window as any).currentSelectedTools || [];
      setTempSelectList(currentSelected);
    }
  }, [setTempSelectList]);

  useEffect(() => {
    const handleApply = () => {
      const nodeId = (window as any).currentNodeId || 'default';
      const mode = (window as any).currentMode || 'multiple';
      if (mode === 'single') {
        const singleTool = tempSelectList.length > 0 ? tempSelectList[0] : null;
        setSelectedToolRepo((prev: Record<string, Tool>) => {
          const newState = { ...prev };
          if (singleTool) {
            newState[nodeId] = singleTool;
          } else {
            newState[nodeId] = null as any;
          }
          return newState;
        });
      } else {
        setSelectedToolsRepo((prev: Record<string, Tool[]>) => ({
          ...prev,
          [nodeId]: tempSelectList.length > 0 ? [...tempSelectList] : null as any,
        }));
      }
    };

    (window as any).toolsApplyHandler = handleApply;

    return () => {
      delete (window as any).toolsApplyHandler;
    };
  }, [tempSelectList, setSelectedToolsRepo, setSelectedToolRepo]);

  useEffect(() => {
    refetch();
  }, [page, searchTerm, refetch]);

  useEffect(() => {
    if (isSuccess && data) {
      const transformedData = (data.content || []).map((item: any) => {
        const tool: any = {
          id: item.id,
          name: item.name,
          description: item.description || '',
          tool_type: item.toolType || item.tool_type || 'custom_code',
          display_name: item.displayName || item.display_name || item.name || '',
          code: item.code || '',
          created_at: item.createdAt || item.created_at || '',
          updated_at: item.updatedAt || item.updated_at || '',
          created_by: item.createdBy?.id || item.created_by || '',
          updated_by: item.updatedBy?.id || item.updated_by || '',
          project_id: item.projectId || item.project_id || '',
        };

        if (item.inputKeys || item.input_keys) tool.input_keys = item.inputKeys || item.input_keys;
        if (item.serverUrl || item.server_url) tool.server_url = item.serverUrl || item.server_url;
        if (item.method) tool.method = item.method;

        return tool;
      });
      setDataList(transformedData);
    }
  }, [data, isSuccess]);

  const safeToolsData = Array.isArray(dataList) ? dataList : [];

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const updateSearchTerm = (term: string) => {
    setSearchTerm(term);
    setPage(1);
  };

  const handleGridSelection = (datas: any[]) => {
    if (isSingleMode) {
      const lastSelected = datas.length > 0 ? datas[datas.length - 1] : null;
      const singleSelection = lastSelected?.originalData ? [lastSelected.originalData] : [];
      setTempSelectList(singleSelection);
      return;
    }
    const selectedTools = datas
      .map(d => d.originalData || d)
      .filter(Boolean)
      .filter((tool, index, self) =>
        index === self.findIndex(t => t.id === tool.id)
      );

    setTempSelectList(selectedTools);
  };

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
        headerName: '이름',
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
              {params.value || '-'}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 464,
        flex: 1,
        showTooltip: true,
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
              {params.value || '-'}
            </div>
          );
        }),
      },
      {
        headerName: '도구 유형',
        field: 'tool_type' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );
  const gridData = useMemo(
    () =>
      safeToolsData.map((item, index) => ({
        id: item.id,
        no: (page - 1) * size + index + 1,
        name: item.name,
        description: item.description || '-',
        tool_type: item.tool_type || '-',
        originalData: item,
      })),
    [safeToolsData, page, size]
  );

  const selectedGridData = useMemo(() => {
    const result = tempSelectList.length > 0
      ? gridData.filter(item => tempSelectList.some(selected => selected.id === item.originalData?.id))
      : [];

    return result;
  }, [tempSelectList, gridData]);

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={data?.totalElements || safeToolsData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchTerm}
                    placeholder='이름 입력'
                    onChange={e => {
                      updateSearchTerm(e.target.value);
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type={isSingleMode ? 'single-select' : 'multi-select'}
              rowData={gridData}
              columnDefs={columnDefs}
              selectedDataList={selectedGridData}
              onCheck={handleGridSelection}
              loading={isLoading}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={page} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};

export const SelectToolsPop: FC = () => {
  return <InferenceToolsList />;
};
