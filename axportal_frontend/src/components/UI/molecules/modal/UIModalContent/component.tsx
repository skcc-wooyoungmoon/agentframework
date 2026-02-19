import { UIIcon2 } from '@/components/UI/atoms';

import { Button } from '@/components/common/auth/Button';
import type { UIModalBodyProps, UIModalFooterProps, UIModalHeaderProps } from './types';

function Header({ type, title, onClose, showCloseButton = false, onClickCloseButton }: UIModalHeaderProps) {
  // 닫기 버튼 클릭 시 이벤트
  const handleCloseButton = () => {
    onClickCloseButton?.();
    onClose?.();
  };
  const isSmallModal = ['alert', 'confirm', '2xsmall'].includes(type);
  return (
    <div className={`flex items-center justify-between ${isSmallModal ? 'h-[66px] px-6' : 'h-[88px] px-10 pt-8 pb-6'} flex-shrink-0`}>
      {/* 타이틀 */}
      <h2
        className='text-gray-900'
        style={{
          fontSize: isSmallModal ? '18px' : '22px',
          lineHeight: isSmallModal ? '26px' : '32px',
          fontWeight: 700,
        }}
      >
        {title}
      </h2>

      {/* 닫기 버튼 */}
      {showCloseButton && (
        <button onClick={handleCloseButton} title='닫기' className='cursor-pointer'>
          {/*  UIIcon2 변경 */}
          <UIIcon2 className='ic-system-24-outline-large-close' />
        </button>
      )}
    </div>
  );
}

function Body({ type, children = <></>, showHeader = false, useCustomFooter = false, contentAlign = 'left', showFooter = false }: UIModalBodyProps) {
  return (
    <div
      className={`${
        ['alert', 'confirm'].includes(type)
          ? `px-6 ${showHeader ? '' : 'pt-6'} text-${contentAlign}`
          : type === '2xsmall'
            ? 'flex-1 overflow-y-auto px-6'
            : 'flex-1 overflow-y-auto px-10'
      } ${!showFooter ? (['alert', 'confirm'].includes(type) ? '' : useCustomFooter ? '' : 'pb-10') : ''}`}
    >
      <div>{children}</div>
    </div>
  );
}

function Footer({ type, negativeButton, positiveButton }: UIModalFooterProps) {
  const isSmallModal = ['alert', 'confirm', '2xsmall', 'xsmall'].includes(type);
  const isXSmall = type === 'xsmall';
  const paddingX = isXSmall ? 'px-10' : isSmallModal ? 'px-6' : 'px-10';

  return (
    <div className={`flex items-center gap-2 justify-center ${isSmallModal ? 'h-[96px]' : 'h-[128px] pt-8 pb-10'} ${paddingX} flex-shrink-0`}>
      <div className={`flex gap-2 ${isSmallModal ? 'w-full' : ''}`}>
        {negativeButton && negativeButton.text?.trim().length > 0 && negativeButton.text != undefined && (
          <Button
            auth={negativeButton.auth}
            // size={buttonSize === 'small' ? 'medium' : undefined}
            // intent='negative'
            onClick={negativeButton.onClick}
            disabled={negativeButton.disabled}
            className={'btn-modal-gray' + (isSmallModal ? ' flex-1' : '')}
            // className={getButtonClassName()}
          >
            {negativeButton.text}
          </Button>
        )}
        {positiveButton && positiveButton.text?.trim().length > 0 && positiveButton.text != undefined && (
          <Button
            auth={positiveButton.auth}
            // variant={variant}
            // size={buttonSize === 'small' ? 'medium' : undefined}
            // intent='positive'
            onClick={positiveButton.onClick}
            disabled={positiveButton.disabled}
            className={'btn-modal-blue' + (isSmallModal ? ' flex-1' : '')}
            // className={getButtonClassName()}
          >
            {positiveButton.text}
          </Button>
        )}
      </div>
    </div>
  );
}
const UIModalContent = {
  Header,
  Body,
  Footer,
};

export { UIModalContent };
