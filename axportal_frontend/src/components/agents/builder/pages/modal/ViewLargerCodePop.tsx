import { DefaultButton } from '@/components/agents/builder/common/Button/DefaultButton';
import { UICode } from '@/components/UI/atoms/UICode';
import { useModal } from "@/stores/common/modal/useModal";
import { type FC, useState } from 'react';

type Props = {
  modalId: string;
  initialCode: string;
  onApply: (newCode: string) => void;
};

export const ViewLargerCodePop: FC<Props> = ({ modalId, initialCode, onApply }) => {
  const [editText, setEditText] = useState<string>(initialCode);

  const { closeModal } = useModal();

  const handleCodeChange = (value: string) => {
    setEditText(value);
  };

  const handleCancel = () => {
    closeModal(modalId);
  };

  const handleApplyBtn = () => {
    onApply(editText);
    handleCancel();
  };

  return (
    <div className='w-full'>
      <div className='grid'>
        <div className='flex w-full flex-col gap-1'>
          <UICode onChange={(value: string) => handleCodeChange(value)} value={editText} width='100%' minHeight='600px' maxHeight='800px' maxWidth='920px' />
        </div>
        <div className='mt-3 flex flex-wrap items-center justify-end gap-5'>
          <DefaultButton className='btn btn-secondary' onClick={handleCancel}>
            {'취소'}
          </DefaultButton>
          <DefaultButton className='btn btn-primary' onClick={handleApplyBtn}>
            {'적용'}
          </DefaultButton>
        </div>
      </div>
    </div>
  );
};
