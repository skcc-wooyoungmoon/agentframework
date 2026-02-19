import { CustomAccordionItem } from '@/components/builder/common/button/CustomAccordionItem';
import { SelectToolsPop } from '@/components/builder/pages/modal/SelectToolsPop.tsx';
import { UIImage } from '@/components/UI/atoms/UIImage';
import type { GetAgentToolByIdResponse } from '@/services/agent/tool/types';
import { useModal } from '@/stores/common/modal';
import React, { useRef } from 'react';
import { useNavigate } from 'react-router-dom';

interface ToolsProps {
  toolIds: string[];
  nodeId: string;
  toolInfoList: GetAgentToolByIdResponse[] | null;
  isSingle?: boolean;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
  onToolsUpdate?: (selectedTools: any) => void;
  onToolInfoListUpdate?: (selectedToolInfoList: GetAgentToolByIdResponse[]) => void;
}

export const SelectTools = ({
  toolIds,
  nodeId,
  toolInfoList,
  isSingle = false,
  asAccordionItem = false,
  title,
  readOnly = false,
  onToolsUpdate,
  onToolInfoListUpdate,
}: ToolsProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const selectedToosItemRef = useRef<any>({}); // 팝업에서 선택한 툴

  const handleClickSearch = () => {
    if (readOnly) return; // 조회 모드에서는 검색 비활성화

    selectedToosItemRef.current = null;
    openModal({
      title: isSingle ? 'Tool 선택' : 'Tools 선택',
      type: 'large',
      body: (
        <SelectToolsPop
          isSingle={isSingle}
          toolIds={toolIds}
          nodeId={nodeId}
          onToolsSelect={(selectedTools: any) => {
            selectedToosItemRef.current = selectedTools;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        // console.log('🔍 선택 툴 업데이트::', selectedToosItemRef.current);

        if (!selectedToosItemRef.current) return;

        // selectedToosItemRef.current에서 id 필드만 뽑아서 string[] 형태로 전달
        const selectedTools = Array.isArray(selectedToosItemRef.current) ? selectedToosItemRef.current : [selectedToosItemRef.current];
        const toolIds = selectedTools.map((tool: any) => tool.id).filter((id: string) => id != null);

        // 상위 Node 업데이트
        onToolsUpdate?.(toolIds);
        onToolInfoListUpdate?.(selectedToosItemRef.current);
      },
    });
  };

  const handleRemoveTool = (toolId: string) => {
    // toolIds에서 toolId 제거 후 상위 Node를 업데이트
    const updatedToolIds = toolIds.filter(id => id !== toolId);
    const updatedToolInfoList = toolInfoList?.filter(tool => tool.id !== toolId) ?? [];

    // 상위 Node 업데이트
    onToolsUpdate?.(updatedToolIds);
    onToolInfoListUpdate?.(updatedToolInfoList);
  };

  const renderMultipleTools = () => {
    return (
      <div className='space-y-2'>
        {toolInfoList?.map(
          (tool: GetAgentToolByIdResponse, index: number) =>
            tool && (
              <div key={`${tool.id}-${index}`} className='flex items-center gap-2 p-3 bg-white rounded-lg border border-gray-300 hover:border-blue-400 transition-colors'>
                <button
                  className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer h-[28px] flex items-center text-sm'
                  title={tool.name}
                  onClick={() => tool.id && navigate(`/agent/tools/${tool.id}`)}
                >
                  {tool.name}
                </button>
                {tool.toolType && <span className='rounded-lg bg-blue-100 px-3 py-1 text-blue-700 text-sm max-w-[200px] truncate h-[28px] flex items-center'>{tool.toolType}</span>}
                <button
                  onClick={() => handleRemoveTool(tool.id)}
                  className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
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
                  title='삭제'
                >
                  <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
                </button>
              </div>
            )
        )}
      </div>
    );
  };

  const content = (
    <>
      <div className='w-full'>
        {toolInfoList && toolInfoList.length > 0 ? (
          renderMultipleTools()
        ) : (
          <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
            <div className='flex-1 items-center'>
              <div className='h-[36px] leading-[36px] text-sm text-gray-500'>{'Tool을 선택해주세요'}</div>
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
    const toolCount = toolInfoList?.length ?? 0;
    const accordionTitle = (
      <>
        {title}
        {toolCount > 0 && <span className='ml-2 text-gray-500'>{toolCount}개 선택됨</span>}
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
