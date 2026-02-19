import { useState } from 'react';

import { UIButton2, UIIcon2 } from '@/components/UI/atoms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

import type { UIFilterProps } from './types';

export function UIFilter({
  position = 'right',
  width = 'w-[448px]',
  children,
  className = '',
  isVisible = true,
  onClose,
  title = '채팅 테스트',
  onChatReset,
  showDropdown = false,
  dropdownOptions = [
    { value: '1', label: 'ver.1' },
    { value: '2', label: 'ver.2' },
    { value: '3', label: 'ver.3' },
  ],
  defaultDropdownValue = 'ver.1',
  onDropdownChange,
}: UIFilterProps) {
  if (!isVisible) {
    return null;
  }

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
  });

  // 선택된 값 상태 관리
  const [selectedVersion, setSelectedVersion] = useState(defaultDropdownValue);

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: string, value: string) => {
    // console.log(`Selected ${key}: ${value}`);
    if (key === 'searchType') {
      // value에서 label로 변환
      const selectedOption = dropdownOptions.find(option => option.value === value);
      const selectedLabel = selectedOption?.label || value;
      setSelectedVersion(selectedLabel);

      // 외부 콜백 호출
      onDropdownChange?.(value);
    }
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  return (
    <>
      {/* Dim 배경 */}
      <div className='fixed inset-0 bg-dimmed min-w-[1920px] min-h-screen z-[9000]' aria-hidden='true' />

      {/* 사이드 패널 */}
      <div className='fixed top-0 right-0 bottom-0' style={{ zIndex: 9900 }}>
        <aside
          className={`${width} bg-white shadow-sm border-gray-200 flex-shrink-0 overflow-y-auto h-full ${
            position === 'left' ? 'border-r order-first' : 'border-l order-last'
          } ${className}`}
        >
          <div className='h-full flex flex-col'>
            {/* 필터 헤더 */}
            <div className='filter-header p-6 bg-white' style={{ height: '76px' }}>
              <div className='flex items-center justify-between h-full'>
                <h3 className='text-xl font-semibold text-gray-900'>{title}</h3>
                <div className='flex flex-end items-center gap-4'>
                  {onChatReset && (
                    <UIButton2 className='btn-text-16-underline-point' onClick={onChatReset}>
                      채팅 초기화
                    </UIButton2>
                  )}
                  {onClose && (
                    <UIButton2 onClick={onClose} className='cursor-pointer h-[24px]' title='닫기'>
                      <UIIcon2 className='ic-system-24-outline-large-close' />
                    </UIButton2>
                  )}
                </div>
              </div>
            </div>
            {/* 버전 셀렉트 - 조건부 렌더링 */}
            {showDropdown && (
              <div className='ml-[24px] w-[100px]'>
                <UIDropdown
                  value={selectedVersion}
                  placeholder='조회 조건 선택'
                  options={dropdownOptions}
                  height={40}
                  fontSize='14px'
                  isOpen={dropdownStates.searchType}
                  onClick={() => handleDropdownToggle('searchType')}
                  onSelect={value => handleDropdownSelect('searchType', value)}
                />
              </div>
            )}

            {/* 필터 콘텐츠 */}
            <div className='flex-1 overflow-y-auto'>{children}</div>
          </div>
        </aside>
      </div>
    </>
  );
}
