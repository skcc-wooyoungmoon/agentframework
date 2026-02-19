import { Button } from '@/components/common/auth';
import { useEffect, useRef } from 'react';
import type { UIMoreMenuConfig } from '../UIGrid/types';
import type { UIMoreMenuItem, UIMoreMenuType } from './types';

/**
 * 더보기 메뉴 컴포넌트
 * TODO 스타일 필요
 */
export function UIMoreMenuPopup<TData>({
  type = 'grid',
  isOpen,
  y,
  data,
  menuConfig,
  onClose,
}: {
  type?: 'grid' | 'card';
  menuConfig: UIMoreMenuConfig<TData>;
  onClose: () => void;
} & Omit<UIMoreMenuType<TData>, 'x'>) {
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen, onClose]);

  if (!data || !isOpen) return null;

  const handleMenuClick = (item: UIMoreMenuItem<TData>) => {
    item.onClick(data); // 선택된 데이터 전달
    onClose();
  };

  // visible 조건에 따라 메뉴 아이템 필터링
  const visibleItems = menuConfig.items.filter(item => (item.visible ? item.visible(data) : true));

  // TODO 팝업 스타일 필요
  return (
    <div
      ref={menuRef}
      className={`${type === 'grid' ? 'more-menu-popup' : 'more-menu-card'}`}
      style={
        type === 'grid'
          ? {
              right: 16,
              top: y,
              transform: 'translate(0, -4px)',
            }
          : {}
      }
    >
      <>
        {visibleItems.map((item, index) => (
          <Button key={item.label || index} className='btn-text-14' auth={item.auth} onClick={() => !item.disabled && handleMenuClick(item)} disabled={item.disabled}>
            {item.label}
          </Button>
        ))}
      </>
    </div>
  );
}
