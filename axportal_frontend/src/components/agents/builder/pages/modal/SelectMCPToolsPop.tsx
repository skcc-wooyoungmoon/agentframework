import { UIButton2, UIDataCnt, UIPagination, UIToggle, UITypography } from '@/components/UI';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIGrid } from '@/components/UI/organisms';
import { useGetAgentMcpByIdTools } from '@/services/agent/mcp/agentMcp.services';
import { useModal } from '@/stores/common/modal';
import { memo, useCallback, useEffect, useMemo, useRef, useState } from 'react';

interface SelectMCPToolsPopProps {
  catalogId: string;
  catalogName: string;
  modalId?: string;
  onSelectTools?: (selectedTools: any[]) => void;
  onGetCurrentSelectedToolsRef?: (ref: { getCurrentSelectedTools: () => any[] }) => void;
  onConfirm?: (selectedTools: any[]) => void;
  navigate?: (path: string) => void;
  initialSelectedTools?: any[];
}

export const SelectMCPToolsPop = ({ catalogId = '', catalogName: _catalogName, modalId: _modalId, onSelectTools, onGetCurrentSelectedToolsRef, navigate, initialSelectedTools = [] }: SelectMCPToolsPopProps) => {
  const { closeAllModals } = useModal();
  const [allActive, setAllActive] = useState(true);
  const [toolStates, setToolStates] = useState<Record<string, boolean>>({});
  const toolsDataRef = useRef<any[]>([]);
  const originalToolsDataRef = useRef<any[]>([]);
  const toolStatesRef = useRef<Record<string, boolean>>({});
  const { data: syncToolsData, isSuccess } = useGetAgentMcpByIdTools({ mcpId: catalogId }, { enabled: !!catalogId });

  useEffect(() => {
    toolStatesRef.current = toolStates;
  }, [toolStates]);

  const getCurrentSelectedTools = useCallback(() => {
    const originalTools = originalToolsDataRef.current;

    if (originalTools.length === 0) {
      return [];
    }

    const activeTools = originalTools.filter((tool: any) => {
      const toolId = tool.id || tool.name;
      return toolStatesRef.current[toolId] === true;
    });

    const validTools = activeTools.filter((tool: any) => {
      const hasRequiredFields = tool.inputSchema && tool.outputSchema;

      return hasRequiredFields;
    });

    return validTools;
  }, [initialSelectedTools]);

  useEffect(() => {
    if (onGetCurrentSelectedToolsRef) {
      onGetCurrentSelectedToolsRef({ getCurrentSelectedTools });
    }
  }, [onGetCurrentSelectedToolsRef, getCurrentSelectedTools]);

  const toolsData = useMemo(() => {
    if (!isSuccess || !syncToolsData) return [];

    let toolsArray = [];

    if (Array.isArray(syncToolsData)) {
      toolsArray = syncToolsData;
    }

    else if (syncToolsData && typeof syncToolsData === 'object' && Array.isArray((syncToolsData as any).tools)) {
      toolsArray = (syncToolsData as any).tools;
    }

    else if (syncToolsData && typeof syncToolsData === 'object' && Array.isArray((syncToolsData as any).data)) {
      toolsArray = (syncToolsData as any).data;
    }

    else {
      toolsArray = [];
    }

    return toolsArray.map((tool: any, index: number) => ({
      id: tool.id || tool.name,
      no: index + 1,
      name: tool.name,
      description: tool.description || '',
      isActive: true,
    }));
  }, [syncToolsData, isSuccess]);

  useEffect(() => {
    toolsDataRef.current = toolsData;
  }, [toolsData]);

  useEffect(() => {
    if (!isSuccess || !syncToolsData) {
      originalToolsDataRef.current = [];
      return;
    }

    let toolsArray = [];
    if (Array.isArray(syncToolsData)) {
      toolsArray = syncToolsData;
    } else if (syncToolsData && typeof syncToolsData === 'object' && Array.isArray((syncToolsData as any).tools)) {
      toolsArray = (syncToolsData as any).tools;
    } else if (syncToolsData && typeof syncToolsData === 'object' && Array.isArray((syncToolsData as any).data)) {
      toolsArray = (syncToolsData as any).data;
    }

    originalToolsDataRef.current = toolsArray.map((tool: any) => ({
      ...tool,
      id: tool.id || tool.name,
    }));
  }, [syncToolsData, isSuccess]);

  useEffect(() => {
    if (!toolsData || toolsData.length === 0) return;
    if (originalToolsDataRef.current.length === 0) return;

    const selectedSet = new Set((initialSelectedTools || []).map((t: any) => String(t?.id || t?.name)));
    const initStates: Record<string, boolean> = {};
    toolsData.forEach((tool: any) => {
      const key = String(tool.id);
      initStates[key] = selectedSet.has(key);
    });
    setToolStates(initStates);
    const newAllActive = toolsData.length > 0 && toolsData.every((tool: any) => initStates[tool.id] === true);
    setAllActive(newAllActive);
    if (onSelectTools) {
      let toolsToUse = originalToolsDataRef.current;
      if (toolsToUse.length === 0) {
        toolsToUse = initialSelectedTools || [];
      }
      const selectedTools = toolsToUse
        .filter((tool: any) => {
          const toolId = tool.id || tool.name;
          return initStates[toolId] === true;
        })
        .map((tool: any) => {
          const toolId = tool.id || tool.name;
          const savedTool = (initialSelectedTools || []).find((t: any) => (t?.id || t?.name) === toolId);
          if (tool.inputSchema && tool.outputSchema) {
            return tool;
          } else if (savedTool && savedTool.inputSchema && savedTool.outputSchema) {
            return {
              ...tool,
              ...savedTool,
              id: toolId,
            };
          }
          return null;
        })
        .filter((tool: any) => tool !== null && tool.inputSchema && tool.outputSchema); // 필수 필드가 있는 도구만 반환

      onSelectTools(selectedTools);
    }
  }, [toolsData, initialSelectedTools, onSelectTools, syncToolsData, isSuccess]);

  const handleToolSelection = (toolId: string, isActive: boolean) => {
    setToolStates(prev => {
      const updated = {
        ...prev,
        [toolId]: isActive,
      };
      const newAllActive =
        toolsData.length > 0 &&
        toolsData.every((tool: any) => {
          const state = updated[tool.id];
          return state === true;
        });
      setAllActive(newAllActive);
      if (onSelectTools) {
        let toolsToUse = originalToolsDataRef.current;
        if (toolsToUse.length === 0) {
          toolsToUse = initialSelectedTools || [];
        }

        const activeTools = toolsToUse.filter((tool: any) => {
          const toolId = tool.id || tool.name;
          return updated[toolId] === true;
        });

        const selectedTools = activeTools
          .map((tool: any) => {
            const toolId = tool.id || tool.name;
            const savedTool = (initialSelectedTools || []).find((t: any) => (t?.id || t?.name) === toolId);

            if (tool.inputSchema && tool.outputSchema) {
              return tool;
            } else if (savedTool && savedTool.inputSchema && savedTool.outputSchema) {
              return {
                ...tool,
                ...savedTool,
                id: toolId,
              };
            }
            return null;
          })
          .filter((tool: any) => tool !== null && tool.inputSchema && tool.outputSchema);

        onSelectTools(selectedTools);
      }
      return updated;
    });
  };

  const handleAllActiveToggle = () => {
    const newAllActive = !allActive;
    setAllActive(newAllActive);

    const newToolStates: Record<string, boolean> = {};
    toolsData.forEach((tool: any) => {
      newToolStates[tool.id] = newAllActive;
    });
    setToolStates(newToolStates);

    if (onSelectTools) {
      let toolsToUse = originalToolsDataRef.current;
      if (toolsToUse.length === 0) {
        toolsToUse = initialSelectedTools || [];
      }

      const activeTools = toolsToUse.filter((tool: any) => {
        const toolId = tool.id || tool.name;
        return newToolStates[toolId] === true;
      });

      const selectedTools = activeTools
        .map((tool: any) => {
          const toolId = tool.id || tool.name;
          const savedTool = (initialSelectedTools || []).find((t: any) => (t?.id || t?.name) === toolId);

          if (tool.inputSchema && tool.outputSchema) {
            return tool;
          } else if (savedTool && savedTool.inputSchema && savedTool.outputSchema) {
            return {
              ...tool,
              ...savedTool,
              id: toolId,
            };
          }

          return null;
        })
        .filter((tool: any) => tool !== null && tool.inputSchema && tool.outputSchema);
      onSelectTools(selectedTools);
    }
  };

  useEffect(() => {
    const newAllActive =
      toolsData.length > 0 &&
      toolsData.every((tool: any) => {
        const state = toolStates[tool.id];
        return state === true;
      });
    setAllActive(newAllActive);
  }, [toolsData, toolStates]);

  const handleMCPServerDetail = () => {
    closeAllModals();
    if (navigate) {
      navigate(`/agent/mcp/${catalogId}`);
    } else {
      window.location.href = `/agent/mcp/${catalogId}`;
    }
  };

  const columnDefs: any = useMemo(
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
        headerName: '툴 이름',
        field: 'name',
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 609,
        flex: 1,
        showTooltip: true,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
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
        headerName: '활성화',
        field: 'button',
        width: 85,
        cellRenderer: ({ data }: any) => {
          return <UIToggle checked={toolStates[data.id] ?? data.isActive} onChange={checked => handleToolSelection(data.id, checked)} />;
        },
      },
    ],
    [toolStates]
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={toolsData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                {isSuccess && toolsData.length > 0 && (
                  <UIUnitGroup gap={8} direction='row' vAlign='center'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      모든 도구 활성화
                    </UITypography>
                    <UIToggle checked={allActive} onChange={handleAllActiveToggle} />
                  </UIUnitGroup>
                )}
              </div>
            </div>
          </UIListContentBox.Header>

          {isSuccess && toolsData.length > 0 && (
            <UIListContentBox.Body>
              <UIGrid type='default' rowData={toolsData} columnDefs={columnDefs} />
            </UIListContentBox.Body>
          )}

          {isSuccess && toolsData.length === 0 && (
            <UIListContentBox.Body>
              <div className='flex items-center justify-center p-8'>
                <UITypography variant='body-1' className='secondary-neutral-600'>
                  등록된 도구가 없습니다.
                </UITypography>
              </div>
            </UIListContentBox.Body>
          )}

          <UIListContentBox.Footer className='ui-data-has-btn'>
            <UIButton2 className='btn-option-outlined' style={{ width: '122px' }} onClick={handleMCPServerDetail}>
              MCP서버 상세가기
            </UIButton2>
            <UIPagination currentPage={1} totalPages={1} onPageChange={() => { }} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
