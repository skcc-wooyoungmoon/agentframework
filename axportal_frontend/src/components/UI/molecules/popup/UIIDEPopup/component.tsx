import { UIButton2, UIIcon2 } from '@/components/UI';
import { UIUnitGroup } from '@/components/UI/molecules';
import type { UIIDEPopupProps } from '@/components/UI/molecules/popup/UIIDEPopup/types.ts';
import { useGetAccessToken } from '@/services/home';
import { useCopyHandler } from '@/hooks/common/util';

export const UIIDEPopup: React.FC<UIIDEPopupProps> = ({ isOpen = false, onClose, onMoveClick, className = '' }) => {
  if (!isOpen) return null;

  // 토큰 조회
  const { refetch: fetchToken } = useGetAccessToken({
    enabled: false,
  });
  const { handleCopy } = useCopyHandler();

  const handleCopyToken = (tokenType: 'access' | 'refresh') => async () => {
    const result = await fetchToken();

    await handleCopy(result.data?.[tokenType === 'access' ? 'access_token' : 'refresh_token'] ?? '');
  };

  return (
    <>
      <div className={'ide-modal' + className}>
        <button onClick={onClose} className='btn-close' aria-label='팝업 닫기'>
          <UIIcon2 className='ic-system-24-outline-small-close text-gray-600' />
        </button>
        <div className='ide-modal-content'>
          <UIUnitGroup direction='column' gap={8} className='w-full'>
            <UIButton2 className='btn-option-outlined-sky-blue w-full' onClick={onMoveClick}>
              이동하기
            </UIButton2>
            <UIButton2 className='btn-option-outlined w-full' onClick={handleCopyToken('access')} rightIcon={{ className: 'ic-system-20-copy-black', children: '' }}>
              Access Token
            </UIButton2>
            <UIButton2 className='btn-option-outlined w-full' onClick={handleCopyToken('refresh')} rightIcon={{ className: 'ic-system-20-copy-black', children: '' }}>
              Refresh Token
            </UIButton2>
          </UIUnitGroup>
        </div>
      </div>
    </>
  );
};

UIIDEPopup.displayName = 'UIIDEPopup';
