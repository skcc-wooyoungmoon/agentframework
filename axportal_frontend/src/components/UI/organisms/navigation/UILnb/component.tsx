import { useState } from 'react';

import { UIIcon2 } from '../../../atoms';

import type { UILnbProps } from './types';
// Default menu items for 1depth

export function UILnb({
  menuItems = [], // 메뉴
  recentMenuItems, // 최근 이용 메뉴
  active, // 활성 메뉴
  setActive, // 활성 메뉴 제어
  setRecentMenuItem, // 최근 이용 메뉴 클릭 시 핸들러
  onMenuClick, // 1depth 메뉴 클릭 시 핸들러
  onSubMenuClick, // 2depth 메뉴 클릭 시 핸들러
  onRecentMenuClick, // 최근 이용 메뉴 클릭 시 핸들러
  is2DepthOpen: initialIs2DepthOpen = true, // 2depth 메뉴 열림 여부
  onToggle2Depth, // 2depth 메뉴 토글 핸들러
  className = '',
}: UILnbProps) {
  const [is2DepthOpen, setIs2DepthOpen] = useState(initialIs2DepthOpen);
  return (
    <aside className={`relative flex bg-white flex-shrink-0 ${is2DepthOpen ? 'w-[272px] border-r border-gray-200' : 'w-[72px] border-r border-transparent'} ${className}`}>
      {/* 1depth 영역 */}
      <div
        className='w-[72px] min-w-[72px] max-w-[72px] flex-shrink-0 bg-white border-r border-gray-200'
        style={{ width: '72px', minWidth: '72px', maxWidth: '72px', padding: '0' }}
      >
        <div className='flex flex-col h-full lnb-menu-wrap'>
          {menuItems.map(item => (
            <button
              key={item.id}
              onClick={() => {
                // 1depth 메뉴 클릭 시 핸들러 호출
                onMenuClick?.();
                setActive.menu(item);
              }}
              className={`relative flex flex-col items-center justify-center text-center transition-colors cursor-pointer min-h-[76px] ${item.id === 'playground' ? 'h-[90px]' : 'h-[76px]'} ${
                active.menu?.id === item.id ? 'active' : 'bg-white'
              }`}
            >
              {item.icon && (
                <div className='w-8 h-8 mb-1'>
                  <img
                    src={`/assets/images/lnb-menu/${active.menu?.id === item.id ? item.icon.replace('default', 'accent') : item.icon}.svg`}
                    alt={item.label}
                    className='w-full h-full'
                  />
                </div>
              )}
              <span
                className={`text-[13px] font-semibold ${active.menu?.id === item.id ? 'text-blue-700' : 'text-gray-400'} ${
                  item.id === 'playground' ? 'leading-[17px]' : 'leading-[20px]'
                }`}
              >
                {item.id === 'playground' ? (
                  <>
                    플레이
                    <br />
                    그라운드
                  </>
                ) : (
                  item.label
                )}
              </span>
            </button>
          ))}
        </div>
      </div>

      {/* 2depth 영역 */}
      {is2DepthOpen && (
        <div className='w-[200px] bg-white'>
          <div className='flex flex-col h-full pt-[16px]'>
            {/* 2depth 헤더 */}
            <div className='px-4 py-[10px]'>
              <h3 className='text-[13px] leading-[20px] font-semibold text-[#576072]'>{menuItems.find(item => item.id === active.menu?.id)?.label || '메뉴'}</h3>
            </div>

            {/* 2depth 메뉴 목록 */}
            <div className='flex-1'>
              <div className='py-[4px] px-[8px]'>
                {menuItems
                  .find(item => item.id === active.menu?.id)
                  ?.children?.map((child, index) => (
                    <div
                      key={child.id}
                      className={`w-full px-1 py-[6px] text-left flex items-center justify-between cursor-pointer transition-colors ${
                        active.subMenu?.id === child.id ? 'text-[#373E4D]' : 'text-[#8B95A9]'
                      }`}
                      style={{
                        marginBottom: index < (menuItems.find(item => item.id === active.menu?.id)?.children?.length || 0) - 1 ? '12px' : '0',
                      }}
                    >
                      <button
                        onClick={() => {
                          // 2depth 메뉴 클릭 시 핸들러 호출
                          setActive.subMenu(child);
                          onSubMenuClick?.();
                        }}
                        className='flex items-center flex-1 bg-transparent border-0 p-0 text-left cursor-pointer transition-colors'
                      >
                        {child.icon && (
                          <div className='mr-[8px] flex items-center'>
                            <UIIcon2 className={(active.subMenu?.id === child.id ? `${child.icon}-on` : child.icon).replace(/^ico-/, 'ic-')} />
                          </div>
                        )}
                        <span className='text-[13px] leading-[20px] font-semibold'>{child.label}</span>
                      </button>
                      {child.href && (
                        <a href={child.href} target='_blank' rel='noopener noreferrer' className='ml-2 flex items-center'>
                          <UIIcon2 className='ic-system-16-link' />
                        </a>
                      )}
                    </div>
                  )) || <div className='px-2 py-1 text-[13px] text-gray-500'>하위 메뉴가 없습니다.</div>}
              </div>

              {/* 최근 이용 메뉴 섹션 */}
              {recentMenuItems && recentMenuItems.length > 0 && (
                <div className='mt-[24px] pt-[24px] border-t border-gray-200 px-[12px]'>
                  <h4 className='text-[13px] leading-[20px] py-[10px] font-semibold text-[#576072] text-sb'>최근 이용 메뉴</h4>
                  <div className='mt-2.5'>
                    {recentMenuItems.map((item, index) => (
                      <button
                        key={item.id}
                        onClick={() => {
                          setRecentMenuItem?.(item);
                          onRecentMenuClick?.();
                        }}
                        className='w-full h-[32px] text-left text-gray-500 flex items-center cursor-pointer transition-colors hover:text-gray-700'
                        style={{
                          marginBottom: index < recentMenuItems.length - 1 ? '12px' : '0',
                        }}
                      >
                        {item.icon && (
                          <div className='inline-flex mr-2'>
                            <UIIcon2 className={item.icon.replace(/^ico-/, 'ic-')} />
                          </div>
                        )}
                        <span className='text-[13px] leading-[20px] text-[#8B95A9] text-sb'>{item.label}</span>
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* 토글 버튼 - aside 오른쪽에 떠있는 위치 */}
      <button
        type='button'
        onClick={() => {
          setIs2DepthOpen(prev => !prev);
          onToggle2Depth?.();
        }}
        className='!absolute top-24 -right-3 z-1 bg-transparent cursor-pointer flex items-center justify-center w-6 h-6'
        title={is2DepthOpen ? '2depth 메뉴 닫기' : '2depth 메뉴 열기'}
      >
        <i className={is2DepthOpen ? 'ic-lnb-24-toggle-close' : 'ic-lnb-24-toggle-open'}></i>
      </button>
    </aside>
  );
}
