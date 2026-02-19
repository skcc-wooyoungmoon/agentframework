import { UICode } from '@/components/UI/atoms/UICode';
import { useModal } from '@/stores/common/modal/useModal';
import { type FC } from 'react';

type Props = {
  modalId: string;
  code: string;
  isLoading: boolean;
  agentName?: string;
};

export const ExportCodePop: FC<Props> = ({ modalId, code, isLoading, agentName = 'agent' }) => {
  const { closeModal, openAlert } = useModal();

  const handleClose = () => {
    closeModal(modalId);
  };

  const handleDownload = () => {
    if (!code || code.trim() === '') {
      openAlert({
        title: '안내',
        message: '다운로드할 코드가 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    // 파일명 생성 (에이전트 이름 또는 ID 사용)
    const sanitizedName = agentName.replace(/[^a-zA-Z0-9가-힣_-]/g, '_');
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, -5);
    const filename = `${sanitizedName}_${timestamp}.py`;

    // Blob 생성
    const blob = new Blob([code], { type: 'text/x-python' });
    const url = URL.createObjectURL(blob);

    // 다운로드 링크 생성 및 클릭
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();

    // 정리
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  return (
    <div className='w-full'>
      <div className='flex w-full flex-col gap-1'>
        {isLoading ? (
          <div className='flex items-center justify-center' style={{ minHeight: '510px' }}>
            <span className='text-gray-500'>코드를 불러오는 중...</span>
          </div>
        ) : (
          <UICode
            value={code}
            onChange={() => {}} // 읽기 전용이므로 onChange는 빈 함수
            language='python'
            theme='dark'
            width='100%'
            minHeight='510px'
            maxHeight='510px'
            readOnly={true}
          />
        )}
      </div>
      <div className='mt-3 flex flex-wrap items-center justify-end gap-5'>
        <button
          className='px-4 py-2 bg-blue-500 text-white text-sm rounded hover:bg-blue-600 transition-colors flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed'
          onClick={handleDownload}
          disabled={!code || code.trim() === '' || isLoading}
        >
          <svg xmlns='http://www.w3.org/2000/svg' className='h-4 w-4' fill='none' viewBox='0 0 24 24' stroke='currentColor'>
            <path strokeLinecap='round' strokeLinejoin='round' strokeWidth={2} d='M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4' />
          </svg>
          다운로드
        </button>
        <button className='px-4 py-2 bg-gray-200 text-gray-700 text-sm rounded hover:bg-gray-300 transition-colors' onClick={handleClose}>
          닫기
        </button>
      </div>
    </div>
  );
};
