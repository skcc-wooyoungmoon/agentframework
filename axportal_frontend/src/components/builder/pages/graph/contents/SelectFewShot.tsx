import { CustomAccordionItem } from '@/components/builder/common/button/CustomAccordionItem';
import { SelectFewShotPop } from '@/components/builder/pages/modal/SelectFewShotPop.tsx';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useGetFewShotById } from '@/services/prompt/fewshot/fewShotPrompts.services';
import { useModal } from '@/stores/common/modal';
import React, { useRef } from 'react';
import { useNavigate } from 'react-router-dom';
interface FewShotProps {
  selectedFewShotId: string | null;
  nodeId: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
  onFewShotUpdate?: (selectedFewShot: any) => void;
}

export const SelectFewShot = ({ selectedFewShotId, nodeId, asAccordionItem = false, title, readOnly = false, onFewShotUpdate }: FewShotProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const selectedFewShotItemRef = useRef<any>({}); // 팝업에서 선택한 퓨샷

  const { data: fewShotInfo } = useGetFewShotById(
    { uuid: selectedFewShotId || '' },
    {
      enabled: !!selectedFewShotId,
    }
  );

  const handleClickSearch = () => {
    if (readOnly) return; // 조회 모드에서는 검색 비활성화

    selectedFewShotItemRef.current = null;
    openModal({
      title: 'Few-shot 선택',
      type: 'large',
      body: (
        <SelectFewShotPop
          selectedFewShotId={selectedFewShotId}
          nodeId={nodeId}
          onFewShowSelect={(selectedFewShot: any) => {
            selectedFewShotItemRef.current = selectedFewShot;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        // console.log('🔍 선택 퓨샷 업데이트::', selectedFewShotItemRef.current);

        if (!selectedFewShotItemRef.current) return;

        onFewShotUpdate?.(selectedFewShotItemRef.current); // 상위노드의 업데이트 함수 호출
      },
    });
  };

  const handleRemoveFewShot = () => {
    onFewShotUpdate?.(null); // 상위 Node를 업데이트
  };

  const content = (
    <>
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {selectedFewShotId && fewShotInfo?.name ? (
            <div className='h-[36px] flex items-center gap-2 flex-1'>
              <button
                className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[400px] text-sm truncate hover:bg-gray-200 transition-colors cursor-pointer'
                title={fewShotInfo.name}
                onClick={() => selectedFewShotId && navigate(`/prompt/fewShot/${selectedFewShotId}`)}
              >
                {fewShotInfo.name}
              </button>
              <button
                onClick={handleRemoveFewShot}
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
            <div className='h-[36px] leading-[36px] text-sm text-gray-500'>Few-shot을 선택해주세요</div>
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
        {selectedFewShotId && fewShotInfo?.name && <span className='ml-2 text-gray-500 font-medium'>{fewShotInfo.name}</span>}
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
