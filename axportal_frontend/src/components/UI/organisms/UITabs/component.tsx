import { UITabList } from '../../molecules/UITabList';

import type { UITabsProps } from './types';

/**
 * Tabs 컴포넌트 (Atomic Design: organism)
 * - 전체 탭 시스템을 관리하는 최상위 컴포넌트
 * - 상태 관리, 이벤트 처리, TabList와 TabPanel 조합
 * - 완전한 탭 기능을 제공하는 독립적인 컴포넌트
 */
export function UITabs({ items, activeId, size = 'large', variant = 'default', onChange, children, className = '' }: UITabsProps) {
  const handleTabChange = (tabId: string) => {
    if (onChange) {
      onChange(tabId);
    }
  };

  return (
    <div className={`w-full ${className}`}>
      {/* TabList (Molecule) */}
      <UITabList items={items} activeId={activeId} size={size} variant={variant} onTabClick={handleTabChange} />

      {/* TabPanel 영역 (선택사항) */}
      {children && (
        <div className='mt-4' role='tabpanel'>
          {children}
        </div>
      )}
    </div>
  );
}
