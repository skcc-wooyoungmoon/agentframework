import React, { useState } from 'react';
import { UIProfileBadge } from '../../atoms/UIProfileBadge';
import { UIProfileDropdown } from '../UIProfileDropdown';
import type { UIProfileBadgeGroupProps } from './types';

export const UIProfileBadgeGroup: React.FC<UIProfileBadgeGroupProps> = ({ users, maxVisible = 3, showDropdown = true, maxDropdownDisplay = 5, className = '' }) => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const visibleUsers = users.slice(0, maxVisible);
  const remainingCount = users.length - maxVisible;

  const handleClick = () => {
    if (showDropdown) {
      setIsDropdownOpen(!isDropdownOpen);
    }
  };

  return (
    <div className={'relative inline-flex flex-shrink-0' + className}>
      <button type='button' onClick={handleClick} className='flex items-center cursor-pointer'>
        <div className='flex items-center'>
          {visibleUsers.map((user, index) => (
            <div
              key={user.id}
              className='relative'
              style={{
                marginLeft: index === 0 ? '0' : '-2px',
                zIndex: visibleUsers.length - index,
              }}
            >
              <div className='ring-2 ring-white rounded-full relative'>
                <UIProfileBadge name={user.name} bgColor={user.bgColor} textColor='#FFFFFF' size='medium' />
                {/* 관리자일 경우 빨간 알림 표시 */}
                {user.isAdmin && <span className='admin-badge'></span>}
              </div>
            </div>
          ))}
        </div>

        {remainingCount > 0 && <span className='text-body-1 text-gray-600 ml-2'>+{remainingCount}명</span>}
      </button>

      {showDropdown && <UIProfileDropdown users={users} isOpen={isDropdownOpen} onClose={() => setIsDropdownOpen(false)} maxDisplay={maxDropdownDisplay} />}
    </div>
  );
};
