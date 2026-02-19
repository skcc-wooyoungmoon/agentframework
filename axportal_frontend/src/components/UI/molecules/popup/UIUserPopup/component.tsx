import { authServices } from '@/services/auth/auth.non.services';
import { UIButton2 } from '../../../atoms/UIButton2';
import { UIIcon2 } from '../../../atoms';
import type { UIUserPopupProps } from './types';

export const UIUserPopup: React.FC<UIUserPopupProps> = ({ isOpen = false, userName = '', userTeam = '', userInitial = '', onClose, onLogout, className = '' }) => {
  if (!isOpen) return null;

  const getInitials = () => {
    if (userInitial) return userInitial;
    if (userName) {
      const names = userName.split(' ');
      if (names.length === 1) {
        return userName.slice(0, 2);
      }
      return names
        .map(n => n[0])
        .join('')
        .slice(0, 2);
    }
    return '사용자';
  };

  return (
    <div
      className={`absolute bg-white rounded-lg border border-gray-300 shadow-lg z-50 ${className}`}
      style={{
        width: '240px',
        height: '210px',
        top: '62px',
        right: '24px',
        borderColor: '#DCE2ED',
      }}
    >
      <button onClick={onClose} className='absolute top-3.5 right-4 p-0 w-6 h-6 flex items-center justify-center cursor-pointer' aria-label='팝업 닫기'>
        <UIIcon2 className='ic-system-24-outline-small-close text-gray-600' />
      </button>

      <div className='flex flex-col items-center px-6 pt-5 pb-6'>
        <div className='w-12 h-12 rounded-full flex items-center justify-center text-white font-medium text-lg mb-4' style={{ backgroundColor: '#0046FF' }}>
          {getInitials()}
        </div>

        <div className='text-center mb-4'>
          <button
            className='text-lg font-semibold text-black mb-0.5'
            onClick={() => {
              authServices
                .getMe()
                .then(/* userData => {
                  // console.log('사용자 정보:', userData);
                  // 여기서 받아온 사용자 정보를 활용할 수 있습니다
                } */)
                .catch(/* error => {
                  // console.error('사용자 정보 조회 실패:', error);
                } */);
            }}
          >
            {userName || '사용자'}
          </button>
          <p className='text-base text-gray-600 truncate w-[192px]' style={{ color: '#576072' }}>
            {userTeam || '소속 정보 없음'}
          </p>
        </div>

        <UIButton2 onClick={onLogout} className='btn-tertiary-sky-blue w-full !h-8 !text-sm'>
          로그아웃
        </UIButton2>
      </div>
    </div>
  );
};

UIUserPopup.displayName = 'UIUserPopup';
