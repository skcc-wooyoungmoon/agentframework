import { useRef } from 'react';

import FocusTrap from '@/components/common/modal/FocusTrap';

import type { UIModalRootProps } from './types';

export default function UIModalRoot({ zIndex, children, trapFocus = true }: UIModalRootProps) {
  const modalRef = useRef<HTMLDivElement>(null);

  return (
    <>
      <FocusTrap modalRef={modalRef} zIndex={zIndex} trapFocus={trapFocus} />
      {/* 각 모달마다 독립적인 dim 배경 */}
      <div className='fixed inset-0 bg-black/60' style={{ zIndex: zIndex - 1 }} aria-hidden='true' />
      {/* 모달 컨테이너 */}
      <div ref={modalRef} role='dialog' aria-modal='true' className='fixed inset-0 flex items-center justify-center' style={{ zIndex: zIndex }} tabIndex={-1}>
        {children}
      </div>
    </>
  );
}
