import { CustomAccordionItem } from '@/components/builder/common/button/CustomAccordionItem';
import { SelectMCPsPop } from '@/components/builder/pages/modal/SelectMCPsPop';
import { SelectMCPToolsPop } from '@/components/builder/pages/modal/SelectMCPToolsPop';
import { UIImage } from '@/components/UI/atoms/UIImage';
import type { generateMCPCatalog } from '@/services/agent/mcp/types';
import { useModal } from '@/stores/common/modal';
import { useRef } from 'react';

interface MCPsProps {
  nodeId: string;
  mcpInfoList: generateMCPCatalog[];
  asAccordionItem?: boolean;
  title?: string;
  onMCPUpdate?: (selectedMCPs: generateMCPCatalog[]) => void;
}

export const SelectMCPs = ({ mcpInfoList, asAccordionItem = false, title, onMCPUpdate }: MCPsProps) => {
  // console.log('🔍 SelectMCPs::: mcpInfoList:::::', mcpInfoList);

  const { openModal } = useModal();
  const selectedMCPsItemRef = useRef<generateMCPCatalog[] | null>(null); // 팝업에서 선택한 MCP들
  const selectedMCPsToolsItemRef = useRef<any[] | null>(null); // 팝업에서 선택한 MCP들의 툴들

  const handleRemoveMCP = (mcpId: string) => {
    // mcpInfoList에서 mcpId를 제거한 리스트르 상위 노드의 업데이트 함수로 전달
    const newMCPs = mcpInfoList.filter((mcp: any) => mcp.id !== mcpId);
    onMCPUpdate?.(newMCPs as unknown as generateMCPCatalog[]);
  };

  const handleClickSearch = () => {
    selectedMCPsItemRef.current = null; // 기존 선택값을 초기 상태로 반영

    openModal({
      title: 'MCP 서버 선택',
      type: 'large',
      body: (
        <SelectMCPsPop
          selectedMCPs={mcpInfoList}
          onSelectMCP={(mcpSelections: generateMCPCatalog[]) => {
            // 팝업에서 선택한 MCP들을 ref에 저장
            selectedMCPsItemRef.current = mcpSelections;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        // console.log('🔍 selectedMCPsItemRef.current::::::::', selectedMCPsItemRef.current);
        if (!selectedMCPsItemRef.current || selectedMCPsItemRef.current.length === 0) return;

        // 팝업에서 선택한 MCP들을 상위 노드의 업데이트 함수로 전달
        onMCPUpdate?.(selectedMCPsItemRef.current as unknown as generateMCPCatalog[]);
      },
    });
  };

  const handleMCPItemClick = (mcp: generateMCPCatalog) => {
    const mcpId = mcp.id;
    selectedMCPsToolsItemRef.current = null;

    // tools 업데이트
    openModal({
      title: 'MCP서버 툴 리스트',
      type: 'large',
      body: (
        <SelectMCPToolsPop
          mcp={mcp as any}
          onApply={(filteredTools: any[]) => {
            selectedMCPsToolsItemRef.current = filteredTools;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        // console.log('🔍 selectedMCPsToolsItemRef.current::::::::', selectedMCPsToolsItemRef.current);
        // mcpInfoList에서 mcpId에 해당하는 아이템의 tools 필드를 selectedMCPsToolsItemRef.current로 업데이트
        const newMCPs = mcpInfoList.map((mcp: any) => {
          if (mcp.id === mcpId) {
            return { ...mcp, tools: selectedMCPsToolsItemRef.current };
          }
          return mcp;
        });
        onMCPUpdate?.(newMCPs as unknown as generateMCPCatalog[]);
      },
      onClose: () => {
        selectedMCPsToolsItemRef.current = null;
      },
    });
  };

  const content = (
    <>
      <div className='w-full'>
        {mcpInfoList && mcpInfoList.length > 0 ? (
          <div className='space-y-2'>
            {mcpInfoList.map((mcp: any) => {
              const displayName = mcp.name;
              const mcpId = mcp.id;
              return (
                <div key={mcpId} className='flex items-center gap-2 p-3 bg-white rounded-lg border border-gray-300 hover:border-blue-400 transition-colors'>
                  <button
                    className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[400px] text-sm truncate hover:bg-gray-200 transition-colors cursor-pointer'
                    title={displayName}
                    onClick={() => handleMCPItemClick(mcp as generateMCPCatalog)}
                  >
                    {displayName}
                  </button>
                  <button
                    onClick={() => handleRemoveMCP(mcpId)}
                    className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
                    title='삭제'
                    style={{
                      backgroundColor: '#ffffff',
                      border: '1px solid #d1d5db',
                      borderRadius: '6px',
                      padding: '6px',
                      color: '#6b7280',
                      cursor: 'pointer',
                      fontSize: '14px',
                      transition: 'all 0.2s ease',
                      minWidth: '32px',
                      width: '32px',
                      height: '32px',
                    }}
                  >
                    <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
                  </button>
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

      <div className='flex justify-end'>
        <button
          onClick={handleClickSearch}
          className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200 mt-3 cursor-pointer'
        >
          검색
        </button>
      </div>
    </>
  );

  if (asAccordionItem) {
    const accordionTitle = (
      <>
        {title}
        {mcpInfoList && mcpInfoList.length > 0 && <span className='ml-2 text-gray-500'>{mcpInfoList.length === 1 ? mcpInfoList[0]?.name : `${mcpInfoList.length}개 선택됨`}</span>}
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
