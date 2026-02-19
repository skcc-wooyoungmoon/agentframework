import type { UIHomeGuidePopupProps } from './types';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';

export const UIHomeGuidePopup: React.FC<UIHomeGuidePopupProps> = ({ isOpen = false, onClose }) => {
  if (!isOpen) return null;

  const handleClose = () => {
    onClose?.();
  };

  return (
    <div className='fixed inset-0 z-[9999]'>
      <div className='home-guide'></div>
      <UIButton2 className='btn-primary-blue home-guide-btn' onClick={handleClose}>
        닫기
      </UIButton2>
    </div>
  );
};

UIHomeGuidePopup.displayName = 'UIHomeGuidePopup';
