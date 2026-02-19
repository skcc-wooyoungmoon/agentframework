import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIModalContainer, UIModalContent } from '../../../molecules/modal';

import type { UIPopupProps } from './types';

export function UIPopup({
  type,
  onClose,
  showHeader = false,
  title = '',
  bodyType = 'text',
  message = '',
  image = {
    url: '',
    alt: '이미지 영역',
    size: 56,
  },
  showFooter = false,
  contentAlign,
  cancelText = '취소',
  onCancel,
  confirmText = '확인',
  onConfirm,
}: UIPopupProps) {
  // 취소 버튼
  const handleCancelButton = () => {
    onCancel?.();
  };

  // 확인 버튼
  const handleConfirmButton = () => {
    onConfirm?.();
  };
  return (
    <UIModalContainer type={type}>
      {showHeader && <UIModalContent.Header type={type} title={title} onClose={onClose} />}
      <UIModalContent.Body type={type} showHeader={showHeader} showFooter={showFooter} contentAlign={contentAlign}>
        {/* 이미지/텍스트 */}
        {bodyType === 'text' ? (
          // <div className="mb-4">
          message.split('\n').map((line, index) => (
            <p key={index} className='text-gray-700 text-sm leading-5'>
              {line}
            </p>
          ))
        ) : (
          // </div>
          <div className='mb-4 rounded-full bg-gray-200 flex items-center justify-center' style={{ width: `${image.size}px`, height: `${image.size}px` }}>
            <UIImage
              src={image.url}
              alt={image.alt}
              className='object-cover rounded-full'
              style={{
                width: `${image.size - 8}px`,
                height: `${image.size - 8}px`,
              }}
            />
          </div>
        )}
      </UIModalContent.Body>
      <UIModalContent.Footer
        type={type}
        negativeButton={
          type === 'alert'
            ? undefined
            : {
                text: cancelText,
                onClick: handleCancelButton,
              }
        }
        positiveButton={{
          text: confirmText,
          onClick: handleConfirmButton,
        }}
      />
    </UIModalContainer>
  );
}
