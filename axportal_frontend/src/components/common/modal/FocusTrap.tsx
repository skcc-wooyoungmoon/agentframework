import { useEffect } from 'react';

type FocusTrapProps = {
  modalRef: React.RefObject<HTMLDivElement | null>;
  zIndex: number;
  trapFocus?: boolean;
};

export default function FocusTrap({ modalRef, zIndex, trapFocus = true }: FocusTrapProps) {
  useEffect(() => {
    if (!trapFocus || !modalRef.current) return;

    const modal = modalRef.current;

    // 현재 모달이 가장 상위 모달인지 확인하는 함수
    const isTopModal = (): boolean => {
      const allModals = Array.from(document.querySelectorAll<HTMLElement>('[role="dialog"][aria-modal="true"]'));
      if (allModals.length === 0) return true;

      let maxZIndex = 0;
      allModals.forEach(m => {
        const z = parseInt(window.getComputedStyle(m).zIndex || '0', 10);
        if (z > maxZIndex) maxZIndex = z;
      });

      const currentZIndex = parseInt(window.getComputedStyle(modal).zIndex || '0', 10);
      return currentZIndex === maxZIndex;
    };

    // 모달 내부의 포커스 가능한 요소들을 찾는 함수
    const getFocusableElements = (): HTMLElement[] => {
      const focusableSelectors = [
        'a[href]',
        'button:not([disabled])',
        'textarea:not([disabled])',
        'input:not([disabled])',
        'select:not([disabled])',
        '[tabindex]:not([tabindex="-1"])',
      ].join(', ');

      return Array.from(modal.querySelectorAll<HTMLElement>(focusableSelectors)).filter(element => {
        // 화면에 보이고 실제로 포커스 가능한 요소만 필터링
        const style = window.getComputedStyle(element);
        return style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0' && !element.hasAttribute('disabled') && !element.hasAttribute('aria-hidden');
      });
    };

    // 포커스를 설정하는 함수
    const setFocus = () => {
      const focusableElements = getFocusableElements();
      if (focusableElements.length > 0) {
        // 가장 하단으로 포커스 이동
        focusableElements[focusableElements.length - 1].focus();
      } else {
        modal.focus();
      }
    };

    // 이전 포커스 저장
    // const prevActive = document.activeElement as HTMLElement | null;

    // 가장 상위 모달일 때만 포커스 설정
    if (isTopModal()) {
      // 약간의 지연을 두어 모달이 완전히 렌더링된 후 포커스 설정
      setTimeout(() => {
        if (isTopModal()) {
          setFocus();
        }
      }, 0);

      // Tab 키 핸들러
      const handleKeyDown = (e: KeyboardEvent) => {
        // 가장 상위 모달이 아니면 이벤트 무시
        if (!isTopModal()) return;

        if (e.key !== 'Tab') return;

        const focusableElements = getFocusableElements();
        if (focusableElements.length === 0) {
          e.preventDefault();
          return;
        }

        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];
        const currentElement = document.activeElement as HTMLElement;

        // Shift+Tab: 역방향
        if (e.shiftKey) {
          if (currentElement === firstElement || !focusableElements.includes(currentElement)) {
            e.preventDefault();
            lastElement.focus();
          }
        } else {
          // Tab: 정방향
          if (currentElement === lastElement || !focusableElements.includes(currentElement)) {
            e.preventDefault();
            firstElement.focus();
          }
        }
      };

      // 키보드 이벤트 리스너 등록
      modal.addEventListener('keydown', handleKeyDown);

      // cleanup: 이전 포커스 복원 및 이벤트 리스너 제거
      return () => {
        modal.removeEventListener('keydown', handleKeyDown);
        // 상위 모달이 닫힐 때 다음 상위 모달에 포커스 설정
        setTimeout(() => {
          const nextTopModal = Array.from(document.querySelectorAll<HTMLElement>('[role="dialog"][aria-modal="true"]')).reduce<HTMLElement | null>((top, m) => {
            const z = parseInt(window.getComputedStyle(m).zIndex || '0', 10);
            const topZ = top ? parseInt(window.getComputedStyle(top).zIndex || '0', 10) : 0;
            return z > topZ ? m : top;
          }, null);

          if (nextTopModal) {
            const focusableSelectors = [
              'a[href]',
              'button:not([disabled])',
              'textarea:not([disabled])',
              'input:not([disabled])',
              'select:not([disabled])',
              '[tabindex]:not([tabindex="-1"])',
            ].join(', ');

            const focusableElements = Array.from(nextTopModal.querySelectorAll<HTMLElement>(focusableSelectors)).filter(element => {
              const style = window.getComputedStyle(element);
              return (
                style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0' && !element.hasAttribute('disabled') && !element.hasAttribute('aria-hidden')
              );
            });

            if (focusableElements.length > 0) {
              focusableElements[focusableElements.length - 1].focus();
            } else {
              nextTopModal.focus();
            }
          }
          // 모달이 모두 닫혔을 때는 포커스 복원하지 않음
          // preActive?.focus();
        }, 0);
      };
    }
  }, [trapFocus, zIndex, modalRef]);

  return null;
}
