import { CustomAccordionItem } from '@/components/builder/common/button/CustomAccordionItem';
import { SelectAgentAppPop } from '@/components/builder/pages/modal/SelectAgentAppPop';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useGetAgentAppById } from '@/services/deploy/agent/agentDeploy.services';
import { useModal } from '@/stores/common/modal';
import React, { useRef } from 'react';
import { useNavigate } from 'react-router-dom';
interface AgentAppProps {
  selectedAgentAppId: string | null;
  nodeId: string;
  nodeType?: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
  onAgentAppSelect?: (selectedAgentApp: any) => void;
}

export const SelectAgentApp = ({ selectedAgentAppId, nodeId, asAccordionItem = false, title, readOnly = false, onAgentAppSelect }: AgentAppProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const selectedAgentItemRef = useRef<any>({}); // 팝업에서 선택한 에이전트

  const { data: agentAppInfo } = useGetAgentAppById(
    { appId: selectedAgentAppId || '' },
    {
      enabled: !!selectedAgentAppId,
    }
  );

  const handleClickSearch = () => {
    if (readOnly) return; // 조회 모드에서는 검색 비활성화

    selectedAgentItemRef.current = null;
    openModal({
      title: 'Agent App 선택',
      type: 'large',
      body: (
        <SelectAgentAppPop
          selectedAgentAppId={selectedAgentAppId}
          nodeId={nodeId}
          onAgentAppSelect={(selectedAgentApp: any) => {
            selectedAgentItemRef.current = selectedAgentApp;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        // console.log('🔍 선택 에이전트 업데이트::', selectedAgentItemRef.current);

        if (!selectedAgentItemRef.current) return;

        onAgentAppSelect?.(selectedAgentItemRef.current); // 상위노드의 업데이트 함수 호출
      },
    });
  };

  const handleRemoveAgentApp = () => {
    onAgentAppSelect?.(null); // 상위노드의 업데이트 함수 호출
  };

  const content = (
    <>
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {selectedAgentAppId && agentAppInfo?.name ? (
            <div className='h-[36px] flex items-center gap-2 flex-1'>
              <button
                className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
                title={agentAppInfo.name}
                onClick={() => selectedAgentAppId && navigate(`/deploy/agentDeploy/${selectedAgentAppId}`)}
              >
                {agentAppInfo.name}
              </button>
              <button
                onClick={handleRemoveAgentApp}
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
          ) : (
            <div className='h-[36px] leading-[36px] text-sm text-gray-500'>Agent App을 선택해주세요</div>
          )}
        </div>
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
        {selectedAgentAppId && agentAppInfo?.name && <span className='ml-2 text-gray-500'>{agentAppInfo.name}</span>}
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
