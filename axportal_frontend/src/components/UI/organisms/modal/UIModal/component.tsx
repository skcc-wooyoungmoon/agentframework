import { UIModalContainer, UIModalContent } from '../../../molecules/modal';

import type { UIModalProps } from './types';

export function UIModal({
  type,
  onClose,
  title,
  onClickCloseButton,
  body,
  useCustomFooter = false,
  showFooter = true,
  cancelText = '',
  onCancel,
  cancelDisabled = false,
  confirmText = '확인',
  onConfirm,
  confirmDisabled = false,
}: UIModalProps) {
  // // 모달 형식 정의
  // const modalType = useMemo(
  //   () => (type === "small" ? "modal-small" : "modal-large"),
  //   [type]
  // );

  // 취소버튼
  const handleCancelButton = () => {
    onCancel?.();
  };

  // 확인버튼
  const handleConfirmButton = () => {
    onConfirm?.();
  };

  // 내부 footer 처리
  const innerShowFooter = useCustomFooter ? false : showFooter;

  return (
    <UIModalContainer type={type}>
      <UIModalContent.Header type={type} title={title} showCloseButton={true} onClose={onClose} onClickCloseButton={onClickCloseButton} />
      <UIModalContent.Body type={type} showFooter={innerShowFooter} useCustomFooter={useCustomFooter}>
        {body}
      </UIModalContent.Body>
      {innerShowFooter && (
        <UIModalContent.Footer
          type={type}
          negativeButton={{
            text: cancelText,
            onClick: handleCancelButton,
            disabled: cancelDisabled,
          }}
          positiveButton={{
            text: confirmText,
            onClick: handleConfirmButton,
            disabled: confirmDisabled,
          }}
        />
      )}
    </UIModalContainer>
  );
}
