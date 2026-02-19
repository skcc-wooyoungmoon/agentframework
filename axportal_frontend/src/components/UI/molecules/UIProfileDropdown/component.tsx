import React, { useEffect, useRef } from 'react';
import { UIProfileBadge } from '../../atoms/UIProfileBadge';
import type { UIProfileDropdownProps } from './types';
import { UITypography } from '../../atoms/UITypography';

// maxDisplay = 5
export const UIProfileDropdown: React.FC<UIProfileDropdownProps> = ({ users, isOpen, onClose, className = '', maxDisplay = 5 }) => {
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
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

  if (!isOpen) return null;

  const shouldScroll = users.length > maxDisplay;
  // const dropdownHeight = shouldScroll ? maxDisplay * 52 + 48 : users.length * 52 + 48;

  const getContentHeight = () => {
    if (users.length === 1) return ' h-[42px]';
    if (users.length === 2) return ' h-[94px]';
    if (users.length === 3) return ' h-[150px]';
    if (users.length === 4) return ' h-[198px]';
    if (shouldScroll) return ' h-[250px]';
    return '';
  };

  const getMaxHeight = () => {
    if (users.length > 5) return '290px';
    // if (users.length === 1) return '108px'; // 60 + 48(padding)
    // if (users.length === 2) return '168px'; // 120 + 48
    // if (users.length === 3) return '198px'; // 150 + 48
    // if (users.length === 4) return '288px'; // 240 + 48
    // if (users.length === 5) return '298px'; // 250 + 48
    // return '250px';
  };

  return (
    <div
      ref={dropdownRef}
      className={'py-6 pr-2 absolute top-full right-0 mt-2 bg-white border border-gray-200 rounded-lg shadow-lg z-50 ' + className}
      style={{
        width: '355px',
        maxWidth: '355px',
        height: 'auto',
        maxHeight: getMaxHeight(),
        // maxHeight: dropdownHeight + 'px',
        boxShadow: '0px 2px 6px rgba(22, 37, 66, 0.1)',
      }}
    >
      <div className={'px-4 pr-2 overflow-y-auto custom-box-scroll leading-6 tracking-[-0.031em]' + getContentHeight()}>
        <div className='flex flex-col gap-3'>
          {users.map(user => (
            <div key={user.id} className='flex items-center gap-3'>
              <div className='relative'>
                <UIProfileBadge name={user.name} bgColor={user.bgColor} textColor='#FFFFFF' size='small' />
                {/* 관리자일 경우 빨간 알림 표시 */}

                {user.isAdmin && <span className='admin-badge'></span>}
              </div>
              <div className='user-profile'>
                <span className='user-profile-item'>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    {user.fullName}
                  </UITypography>
                </span>
                <span className='user-profile-item'>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    |
                  </UITypography>
                </span>
                <span className='user-profile-item'>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    {user.department}
                    {/* {user.fullName.includes('(나)') ? ' (나)' : ''} */}
                  </UITypography>
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
