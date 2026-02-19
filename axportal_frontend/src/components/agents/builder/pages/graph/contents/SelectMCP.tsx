import { CustomAccordionItem } from '@/components/agents/builder/common/Button/CustomAccordionItem';
import { SelectMCPPop } from '@/components/agents/builder/pages/modal/SelectMCPPop';
import { SelectMCPToolsPop } from '@/components/agents/builder/pages/modal/SelectMCPToolsPop';
import { useAuth } from '@/components/agents/builder/providers/Auth';
import { useModal } from '@/stores/common/modal';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { MCPSelection } from '../../../types/mcpTypes';

interface MCPProps {
  selectedMCPs: MCPSelection[];
  onChange?: (selectedMCPs: MCPSelection[]) => void;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  nodeId?: string;
  readOnly?: boolean;
}

export const SelectMCP = ({ selectedMCPs = [], onChange, asAccordionItem = false, title, nodeId = 'default', readOnly = false }: MCPProps) => {
  const [selectedMCPsState, setSelectedMCPsState] = useState<MCPSelection[]>(selectedMCPs);
  const { currentUser: _currentUser } = useAuth();
  const { openModal } = useModal();
  const navigate = useNavigate();
  const [mcpCache, setMcpCache] = useState<Record<string, MCPSelection[]>>({});
  const lastEmittedRef = useRef<string>('');
  const tempSelectedToolsRef = useRef<Record<string, any[]>>({});
  const getCurrentSelectedToolsRefs = useRef<Record<string, { getCurrentSelectedTools: () => any[] }>>({});

  const areEqual = useCallback((a: MCPSelection[] = [], b: MCPSelection[] = []) => {
    const normalize = (arr: MCPSelection[]) =>
      [...arr]
        .sort((x, y) => String((x as any).catalogId).localeCompare(String((y as any).catalogId)))
        .map(item => {
          const keys = Object.keys(item as any).sort();
          const obj: Record<string, any> = {};
          keys.forEach(k => {
            obj[k] = (item as any)[k];
          });
          return obj;
        });
    try {
      return JSON.stringify(normalize(a)) === JSON.stringify(normalize(b));
    } catch {
      return false;
    }
  }, []);

  useEffect(() => {
    const savedMCPs = mcpCache[nodeId];
    if (savedMCPs) {
      setSelectedMCPsState(savedMCPs);
    }
  }, [nodeId, mcpCache]);

  useEffect(() => {
    if (!areEqual(selectedMCPs, selectedMCPsState)) {
      setSelectedMCPsState(selectedMCPs);
    }
  }, [selectedMCPs, selectedMCPsState, areEqual]);

  const handleSelectMCP = useCallback(
    (mcpSelection: MCPSelection) => {
      setSelectedMCPsState(prevState => {
        const updatedMCPs = [...prevState];
        const existingIndex = updatedMCPs.findIndex(mcp => mcp.catalogId === mcpSelection.catalogId);

        if (existingIndex >= 0) {
          updatedMCPs[existingIndex] = mcpSelection;
        } else {
          updatedMCPs.push(mcpSelection);
        }

        setMcpCache((prev: Record<string, MCPSelection[]>) => ({
          ...prev,
          [nodeId]: updatedMCPs,
        }));

        if (onChange && !areEqual(updatedMCPs, selectedMCPs)) {
          const serialized = JSON.stringify(updatedMCPs);
          if (serialized !== lastEmittedRef.current) {
            lastEmittedRef.current = serialized;
            onChange(updatedMCPs);
          }
        }

        return updatedMCPs;
      });
    },
    [nodeId, onChange, areEqual, selectedMCPs]
  );

  const handleSelectMCPBatch = useCallback(
    (mcpSelections: MCPSelection[]) => {
      setSelectedMCPsState(prevState => {
        const updatedMCPs = [...prevState];
        mcpSelections.forEach(mcpSelection => {
          const existingIndex = updatedMCPs.findIndex(mcp => mcp.catalogId === mcpSelection.catalogId);
          if (existingIndex >= 0) {
            updatedMCPs[existingIndex] = mcpSelection;
          } else {
            updatedMCPs.push(mcpSelection);
          }
        });

        setMcpCache((prev: Record<string, MCPSelection[]>) => ({
          ...prev,
          [nodeId]: updatedMCPs,
        }));

        if (onChange && !areEqual(updatedMCPs, selectedMCPs)) {
          const serialized = JSON.stringify(updatedMCPs);
          if (serialized !== lastEmittedRef.current) {
            lastEmittedRef.current = serialized;
            onChange(updatedMCPs);
          }
        }

        return updatedMCPs;
      });
    },
    [nodeId, onChange, areEqual, selectedMCPs]
  );

  const handleMCPSelection = useCallback(
    (mcpSelection: MCPSelection) => {
      handleSelectMCP(mcpSelection);
    },
    [handleSelectMCP]
  );

  const handleMCPSelectionBatch = useCallback(
    (mcpSelections: MCPSelection[]) => {
      handleSelectMCPBatch(mcpSelections);
    },
    [handleSelectMCPBatch]
  );

  const handleRemoveMCP = (catalogId: string) => {
    const updatedMCPs = selectedMCPsState.filter(mcp => getCatalogId(mcp) !== catalogId);
    setSelectedMCPsState(updatedMCPs);
    setMcpCache((prev: Record<string, MCPSelection[]>) => ({
      ...prev,
      [nodeId]: updatedMCPs,
    }));
    if (onChange && !areEqual(updatedMCPs, selectedMCPs)) {
      const serialized = JSON.stringify(updatedMCPs);
      if (serialized !== lastEmittedRef.current) {
        lastEmittedRef.current = serialized;
        onChange(updatedMCPs);
      }
    }
  };

  const handleClickSearch = () => {
    if (readOnly) return;
    (window as any).currentNodeId = nodeId;

    openModal({
      title: 'MCP서버 선택',
      type: 'large',
      body: (
        <SelectMCPPop
          modalId={`select-mcp-pop_${nodeId}`}
          catalogNumber={0}
          selectedMCPs={selectedMCPsState}
          onSelectMCP={handleMCPSelection}
          onSelectMCPBatch={handleMCPSelectionBatch}
        />
      ),
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        if ((window as any).mcpApplyHandler) {
          (window as any).mcpApplyHandler();
        }
      },
    });
  };

  const getDisplayName = (mcp: MCPSelection, idx?: number) => {
    return mcp.catalogName || mcp.serverName || (mcp as any).server_name || (mcp as any).name || (typeof idx === 'number' ? `MCP ${idx + 1}` : 'MCP');
  };

  const getCatalogId = (mcp: MCPSelection): string => {
    return mcp.catalogId || (mcp as any).id || (mcp as any).mcp_id || '';
  };

  const handleMCPItemClick = (mcp: MCPSelection) => {
    if (readOnly) return;

    const catalogId = getCatalogId(mcp);
    const displayName = getDisplayName(mcp);
    const modalId = `select-mcp-tools-${catalogId}-${Date.now()}`;

    const initialTools = (mcp as any).tools || [];
    tempSelectedToolsRef.current[catalogId] = initialTools;

    const handleToolSelection = (selectedTools: any[]) => {
      const validTools = selectedTools.filter((tool: any) => {
        const hasRequiredFields = tool.inputSchema && tool.outputSchema;
        if ('isActive' in tool) {
          delete tool.isActive;
        }
        return hasRequiredFields;
      });

      tempSelectedToolsRef.current[catalogId] = validTools;
    };

    const handleGetCurrentSelectedToolsRef = (ref: { getCurrentSelectedTools: () => any[] }) => {
      getCurrentSelectedToolsRefs.current[catalogId] = ref;
    };

    openModal({
      title: `MCP서버 툴 리스트`,
      type: 'large',
      body: (
        <SelectMCPToolsPop
          catalogId={catalogId}
          catalogName={displayName}
          modalId={modalId}
          onSelectTools={handleToolSelection}
          onGetCurrentSelectedToolsRef={handleGetCurrentSelectedToolsRef}
          navigate={navigate}
          initialSelectedTools={initialTools}
        />
      ),
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        let finalSelectedTools: any[] = [];
        const getCurrentSelectedToolsRef = getCurrentSelectedToolsRefs.current[catalogId];
        if (getCurrentSelectedToolsRef) {
          finalSelectedTools = getCurrentSelectedToolsRef.getCurrentSelectedTools();
        } else {
          const tempTools = tempSelectedToolsRef.current[catalogId] || [];
          finalSelectedTools = tempTools.filter((tool: any) => {
            const hasRequiredFields = tool.inputSchema && tool.outputSchema;
            if ('isActive' in tool) {
              delete tool.isActive;
            }
            return hasRequiredFields;
          });
        }

        setSelectedMCPsState(prev => {
          const updated = prev.map(sel => {
            if (getCatalogId(sel) === catalogId) {
              return { ...sel, tools: finalSelectedTools ? [...finalSelectedTools] : [] };
            }
            return sel;
          });
          setMcpCache((prevCache: Record<string, MCPSelection[]>) => ({ ...prevCache, [nodeId]: updated }));

          if (onChange) {
            const prevMcp = prev.find(s => getCatalogId(s) === catalogId);
            const updatedMcp = updated.find(s => getCatalogId(s) === catalogId);
            const prevToolsIds = (prevMcp?.tools || [])
              .map((t: any) => t?.id || t?.name)
              .sort()
              .join(',');
            const updatedToolsIds = (updatedMcp?.tools || [])
              .map((t: any) => t?.id || t?.name)
              .sort()
              .join(',');
            const toolsChanged = prevToolsIds !== updatedToolsIds;

            if (toolsChanged || !areEqual(updated, selectedMCPs)) {
              const serialized = JSON.stringify(updated);
              if (serialized !== lastEmittedRef.current) {
                lastEmittedRef.current = serialized;
                onChange(updated);
              }
            }
          }
          return updated;
        });
      },
    });
  };

  const content = (
    <>
      <div className='w-full'>
        {selectedMCPsState && selectedMCPsState.length > 0 ? (
          <div className='space-y-2'>
            {selectedMCPsState.map((mcp, idx) => {
              const displayName = getDisplayName(mcp, idx);
              const catalogId = getCatalogId(mcp) || idx.toString();
              return (
                <div key={catalogId} className='flex items-center gap-2 p-3 bg-white rounded-lg border border-gray-300 hover:border-blue-400 transition-colors'>
                  <span
                    className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
                    title={displayName}
                    onClick={() => handleMCPItemClick(mcp)}
                  >
                    {displayName}
                  </span>
                  {!readOnly && (
                    <button onClick={() => handleRemoveMCP(catalogId)} className='btn-icon btn btn-sm btn-light text-primary btn-node-action ml-auto' title='삭제'>
                      {/* 🗑️ */}
                      <img alt='ico-system-24-outline-gray-trash' className='w-[20px] h-[20px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
                    </button>
                  )}
                </div>
              );
            })}
          </div>
        ) : (
          <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
            <div className='flex-1 items-center'>
              <div className='h-[36px] leading-[36px] text-sm text-gray-500'>MCP 서버를 선택해주세요</div>
            </div>
          </div>
        )}
      </div>

      {!readOnly && (
        <div className='flex justify-end'>
          <button onClick={handleClickSearch} className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200 mt-3'>
            검색
          </button>
        </div>
      )}
    </>
  );

  if (asAccordionItem) {
    const accordionTitle = (
      <>
        {title}
        {selectedMCPsState && selectedMCPsState.length > 0 && (
          <span className='ml-2 text-gray-500'>{selectedMCPsState.length === 1 ? getDisplayName(selectedMCPsState[0]) : `${selectedMCPsState.length}개 선택됨`}</span>
        )}
      </>
    );
    return (
      <CustomAccordionItem title={accordionTitle} defaultOpen={false}>
        {content}
      </CustomAccordionItem>
    );
  }

  return content;
};