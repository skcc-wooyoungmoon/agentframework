import { CustomAccordionItem } from '@/components/builder/common/button/CustomAccordionItem';
import { SelectKnowledgePop } from '@/components/builder/pages/modal/SelectKnowledgePop.tsx';
import { useModal } from '@/stores/common/modal';

import { UIImage } from '@/components/UI/atoms/UIImage';
import { useGetExternalRepoDetailV2 } from '@/services/knowledge/knowledge.services';
import React, { useRef } from 'react';
import { useNavigate } from 'react-router-dom';

interface KnowledgeProps {
  selectedRepoId: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
  onKnowledgeUpdate?: (selectedKnowledge: any) => void;
}

export const SelectKnowledge = ({ selectedRepoId, asAccordionItem = false, title, readOnly = false, onKnowledgeUpdate }: KnowledgeProps) => {
  // console.log('🔍 selectedRepoId :: ', selectedRepoId);

  const { openModal } = useModal();
  const navigate = useNavigate();
  const selectedKnowledgeItemRef = useRef<any>({}); // 팝업에서 선택한 지식

  // selectedRepoId가 변하면 지식 상세 조회하여 selectedKnowledgeInfo 세팅
  const { data: repoDetail } = useGetExternalRepoDetailV2(selectedRepoId || '', {
    enabled: !!selectedRepoId, // selectedRepoId가 있을 때만 조회
  });

  const handleClickSearch = () => {
    if (readOnly) return; // 조회 모드에서는 검색 비활성화

    selectedKnowledgeItemRef.current = null;
    openModal({
      title: 'Knowledge 선택',
      type: 'large',
      body: (
        <SelectKnowledgePop
          selectedRepoId={selectedRepoId}
          onKnowledgeSelect={(selectedKnowledge: any) => {
            selectedKnowledgeItemRef.current = selectedKnowledge;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        // console.log('🔍 선택 지식 업데이트::', selectedKnowledgeItemRef.current);

        if (!selectedKnowledgeItemRef.current) return;

        onKnowledgeUpdate?.(selectedKnowledgeItemRef.current); // Node의 업데이트 함수 호출
      },
    });
  };

  const handleRemoveKnowledge = () => {
    onKnowledgeUpdate?.({});
  };

  const content = (
    <>
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {selectedRepoId && repoDetail?.name ? (
            <div className='h-[36px] flex items-center gap-2 flex-1 min-w-0'>
              <button className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] cursor-pointer text-sm truncate hover:bg-gray-200 transition-colors h-[28px]'
                title={repoDetail.name}
                onClick={() => selectedRepoId && navigate(`/data/dataCtlg/knowledge/detail/${repoDetail.knwId || selectedRepoId}`)}
              >
                {repoDetail.name}
              </button>
              <div className='h-[36px] flex items-center gap-2 flex-1 min-w-0'>
              {repoDetail.embedding_model_name && (
                <span className='rounded-lg bg-blue-100 px-3 py-1 text-blue-700 max-w-[180px] cursor-pointer text-sm truncate hover:bg-gray-200 transition-colors h-[28px]' title={repoDetail.embedding_model_name}>{repoDetail.embedding_model_name}</span>
                )}
              </div>
              <button
                onClick={handleRemoveKnowledge}
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
            <div className='h-[36px] leading-[36px] text-sm text-gray-500'>Knowledge를 선택해주세요</div>
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
        {selectedRepoId && repoDetail && <span className='ml-2 text-gray-500'>{repoDetail.name}</span>}
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
