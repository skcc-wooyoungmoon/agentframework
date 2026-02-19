import { CustomAccordionItem } from '@/components/builder/common/button/CustomAccordionItem';
import { SelectPromptPop } from '@/components/builder/pages/modal/SelectPromptPop.tsx';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useGetInfPromptById } from '@/services/prompt/inference/inferencePrompts.services';
import { useModal } from '@/stores/common/modal';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface PromptProps {
  selectedPromptId: string | null;
  nodeId: string;
  nodeType?: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
  onPromptUpdate?: (selectedPrompt: any) => void;
}

export const SelectPrompt = ({ selectedPromptId, nodeType, asAccordionItem = false, title, readOnly = false, onPromptUpdate }: PromptProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const selectedPromptItemRef = useRef<any>({}); // 팝업에서 선택한 지식

  // id로 상세 조회
  const { data: infPrompt } = useGetInfPromptById(
    { promptUuid: selectedPromptId || '' },
    {
      enabled: !!selectedPromptId, // selectedRepoId가 있을 때만 조회
    }
  );

  // infPrompt 데이터를 state로 관리하여 업데이트 보장
  const [displayPrompt, setDisplayPrompt] = useState<any>(null);

  // infPrompt가 변경될 때 displayPrompt 업데이트
  useEffect(() => {
    if (infPrompt) {
      setDisplayPrompt(infPrompt);
    } else if (!selectedPromptId) {
      setDisplayPrompt(null);
    }
  }, [infPrompt, selectedPromptId]);

  const handleClickSearch = () => {
    if (readOnly) return; // 조회 모드에서는 검색 비활성화

    selectedPromptItemRef.current = null;
    openModal({
      title: 'Prompt 선택',
      type: 'large',
      body: (
        <SelectPromptPop
          selectedPromptId={selectedPromptId}
          nodeType={nodeType}
          onPromptSelect={(selectedPrompt: any) => {
            selectedPromptItemRef.current = selectedPrompt;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        // console.log('🔍 선택 프롬프트 업데이트::', selectedPromptItemRef.current);

        if (!selectedPromptItemRef.current) return;

        onPromptUpdate?.(selectedPromptItemRef.current); // 상위노드의 업데이트 함수 호출
      },
    });
  };

  const handleRemovePrompt = () => {
    onPromptUpdate?.(null); // 상위 Node의 formState.prompt를 업데이트
  };

  const content = (
    <>
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {selectedPromptId && displayPrompt?.name ? (
            <div className='h-[36px] flex items-center gap-2 flex-1'>
              <button
                className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[400px] text-sm truncate hover:bg-gray-200 transition-colors cursor-pointer'
                title={displayPrompt.name}
                onClick={() => selectedPromptId && navigate(`/prompt/inferPrompt/${selectedPromptId}`)}
              >
                {displayPrompt.name}
              </button>
              <button
                onClick={handleRemovePrompt}
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
          ) : (
            <div className='h-[36px] leading-[36px] text-sm text-gray-500'>Prompt를 선택해주세요</div>
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
        {selectedPromptId && displayPrompt && <span className='ml-2 text-gray-500 font-medium'>{displayPrompt.name}</span>}
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
