import { isChangeToolAtom, isChangeToolsAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { selectedAtom, selectedListAtom } from '@/components/agents/builder/atoms/toolsAtom.ts';
import { CustomAccordionItem } from '@/components/agents/builder/common/Button/CustomAccordionItem';
import { useModal } from '@/stores/common/modal';
import { SelectToolsPop } from '@/components/agents/builder/pages/modal/SelectToolsPop.tsx';
import { type Tool } from '@/components/agents/builder/types/Tools.ts';
import { useAtom } from 'jotai';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface ToolsProps {
  toolId?: string;
  toolInfoList?: Tool[];
  nodeId: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  mode?: 'single' | 'multiple';
  readOnly?: boolean;
}

export const SelectTools = ({ toolId, toolInfoList, nodeId, asAccordionItem = false, title, mode = 'multiple', readOnly = false }: ToolsProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const [selectedToolRepo, setSelectedToolRepo] = useAtom(selectedAtom);
  const [selectedToolsRepo, setSelectedToolsRepo] = useAtom(selectedListAtom);
  const [isChangeTool, setChangeTool] = useAtom(isChangeToolAtom);
  const [isChangeTools, setChangeTools] = useAtom(isChangeToolsAtom);
  const [loadedTools, setLoadedTools] = useState<Tool[]>([]);

  const isMultiple = mode === 'multiple';
  const toolInfo = undefined;

  useEffect(() => {
    const currentSelected = selectedToolsRepo[nodeId];

    if (currentSelected !== undefined && currentSelected !== null) {
      const selectedIds = currentSelected
        .map(t => t.id)
        .sort()
        .join(',');
      // 🔥 loadedTools를 의존성에서 제거하여 무한 루프 방지
      setLoadedTools(prev => {
        const prevIds = prev
          .map(t => t.id)
          .sort()
          .join(',');
        if (selectedIds !== prevIds) {
          return [...currentSelected];
        }
        return prev;
      });
      return;
    }

    if (currentSelected === null) {
      // 🔥 loadedTools를 의존성에서 제거하여 무한 루프 방지
      setLoadedTools(prev => {
        if (prev.length > 0) {
          return [];
        }
        return prev;
      });
      return;
    }

    const toolInfoIds =
      toolInfoList
        ?.map(t => t.id)
        .sort()
        .join(',') || '';
    
    // 🔥 loadedTools를 의존성에서 제거하여 무한 루프 방지
    setLoadedTools(prev => {
      const prevIds = prev
        .map(t => t.id)
        .sort()
        .join(',');
      if (toolInfoIds !== prevIds) {
        if (toolInfoList && toolInfoList.length > 0) {
          return [...toolInfoList];
        } else if (prev.length > 0) {
          return [];
        }
      }
      return prev;
    });
  }, [toolInfoList, nodeId, selectedToolsRepo]);

  useEffect(() => {
    if (!isMultiple && toolInfo && !selectedToolRepo[nodeId]) {
      setSelectedToolRepo((prev: Record<string, Tool>) => ({
        ...prev,
        [nodeId]: toolInfo,
      }));
    }
  }, [toolId, nodeId, toolInfo, selectedToolRepo, setSelectedToolRepo, isMultiple]);

  useEffect(() => {
    if (isMultiple && loadedTools.length > 0 && selectedToolsRepo[nodeId] === undefined) {
      setSelectedToolsRepo((prev: Record<string, Tool[]>) => ({
        ...prev,
        [nodeId]: loadedTools,
      }));
    }
  }, [nodeId, loadedTools, selectedToolsRepo, setSelectedToolsRepo, isMultiple]);

  useEffect(() => {
    if (!isMultiple && isChangeTool) {
      setChangeTool(false);
    }
  }, [isChangeTool, setChangeTool, isMultiple]);

  useEffect(() => {
    if (isMultiple && isChangeTools) {
      setChangeTools(false);
    }
  }, [isChangeTools, setChangeTools, isMultiple]);

  const handleClickSearch = () => {
    if (readOnly) return;
    (window as any).currentNodeId = nodeId;
    (window as any).currentMode = isMultiple ? 'multiple' : 'single';
    if (!isMultiple) {
      const singleTool = selectedToolRepo[nodeId];
      (window as any).currentSelectedTools = singleTool ? [singleTool] : [];
    } else {
      (window as any).currentSelectedTools = selectedToolsRepo[nodeId] || loadedTools || [];
    }

    openModal({
      title: '도구 선택',
      type: 'large',
      body: <SelectToolsPop />,
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        if ((window as any).toolsApplyHandler) {
          (window as any).toolsApplyHandler();
        }
        setChangeTools(true);
        setChangeTool(true);
      },
    });
  };

  const handleRemoveTool = (toolId?: string) => {
    if (isMultiple) {
      if (toolId) {
        setSelectedToolsRepo((prevState: Record<string, Tool[]>) => {
          const newState = { ...prevState };
          if (newState[nodeId]) {
            newState[nodeId] = newState[nodeId].filter(tool => tool.id !== toolId);
            if (newState[nodeId].length === 0) {
              newState[nodeId] = null as any;
            }
          }
          return newState;
        });
        setLoadedTools(prev => {
          const filtered = prev.filter(tool => tool.id !== toolId);
          return filtered;
        });
        // 🔥 Tools 변경 플래그를 atom 업데이트 후에 설정
        setChangeTools(true);
      } else {
        setSelectedToolsRepo((prevState: Record<string, Tool[]>) => ({
          ...prevState,
          [nodeId]: null as any,
        }));

        setLoadedTools([]);
        // 🔥 Tools 변경 플래그를 atom 업데이트 후에 설정
        setChangeTools(true);
      }
    } else {
      setSelectedToolRepo((prevState: Record<string, Tool>) => ({
        ...prevState,
        [nodeId]: null as any,
      }));
      setChangeTool(true);
    }
  };

  const renderSingleTool = () => {
    if (!selectedToolRepo[nodeId]) return null;

    return (
      <div className='flex items-center gap-2 flex-1'>
        <button
          className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
          title={selectedToolRepo[nodeId].name}
          onClick={e => {
            e.stopPropagation();
            if (selectedToolRepo[nodeId]?.id) {
              navigate(`/agent/tools/${selectedToolRepo[nodeId].id}`);
            }
          }}
        >
          {selectedToolRepo[nodeId].name}
        </button>
      </div>
    );
  };

  const renderMultipleTools = () => {
    // 퓨샷과 동일한 로직: selectedToolsRepo[nodeId]가 있을 때만 렌더링
    // null 체크 추가 (삭제된 경우 null로 설정됨)
    const currentTools = selectedToolsRepo[nodeId];
    if (!currentTools || currentTools === null || currentTools.length === 0) {
      return null;
    }

    const toolsToRender = currentTools;

    return (
      <div className='space-y-2'>
        {toolsToRender.map((tool, index) => (
          <div key={`${tool.id}-${index}`} className='flex items-center gap-2 p-3 bg-white rounded-lg border border-gray-300 hover:border-blue-400 transition-colors'>
            <button
              className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
              title={tool.name}
              onClick={e => {
                e.stopPropagation();
                if (tool.id) {
                  navigate(`/agent/tools/${tool.id}`);
                }
              }}
            >
              {tool.name}
            </button>
            {!readOnly && (
              <button onClick={() => handleRemoveTool(tool.id)} className='btn-icon btn btn-sm btn-light text-primary btn-node-action ml-auto' title='삭제'>
                {/* 🗑️ */}
                <img alt='ico-system-24-outline-gray-trash' className='w-[20px] h-[20px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
              </button>
            )}
          </div>
        ))}
      </div>
    );
  };

  const content = (
    <>
      <div className='w-full'>
        {isMultiple ? (
          selectedToolsRepo[nodeId] && selectedToolsRepo[nodeId] !== null && selectedToolsRepo[nodeId].length > 0 ? (
            renderMultipleTools()
          ) : (
            // <div className='h-[36px] leading-[36px] text-sm text-gray-500 p-3 bg-gray-50 rounded-lg border border-gray-20'>도구를 선택해주세요</div>
            <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
              <div className='flex-1 items-center'>
                <div className='h-[36px] leading-[36px] text-sm text-gray-500'>도구를 선택해주세요</div>
              </div>
            </div>
          )
        ) : selectedToolRepo[nodeId] ? (
          <div className='flex items-center gap-2 p-3 bg-white rounded-lg border border-gray-300 hover:border-blue-400 transition-colors'>
            <div className='flex-1'>{renderSingleTool()}</div>
            {!readOnly && (
              <button onClick={() => handleRemoveTool()} className='text-red-500 p-2 hover:bg-red-100 rounded-lg ml-auto btn-bg-del' title='삭제'>
                🗑️
              </button>
            )}
          </div>
        ) : (
          // <div className='h-[36px] leading-[36px] text-sm text-gray-500 p-3 bg-gray-50 rounded-lg border border-gray-20'>도구를 선택해주세요</div>
          <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
            <div className='flex-1 items-center'>
              <div className='h-[36px] leading-[36px] text-sm text-gray-500'>도구를 선택해주세요</div>
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
        {isMultiple
          ? selectedToolsRepo[nodeId] &&
          selectedToolsRepo[nodeId].length > 0 && (
            <span className='ml-2 text-gray-500'>
              {selectedToolsRepo[nodeId].length === 1 ? selectedToolsRepo[nodeId][0].name : `${selectedToolsRepo[nodeId].length}개 선택됨`}
            </span>
          )
          : selectedToolRepo[nodeId] && <span className='ml-2 text-gray-500'>{selectedToolRepo[nodeId].name}</span>}
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

// 기존 컴포넌트들과의 호환성을 위한 별칭
export const SelectSingleTool = (props: Omit<ToolsProps, 'mode'>) => <SelectTools {...props} mode='single' />;

export const SelectMultipleTools = (props: Omit<ToolsProps, 'mode'>) => <SelectTools {...props} mode='multiple' />;
