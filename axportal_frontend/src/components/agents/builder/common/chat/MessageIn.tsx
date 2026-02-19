import { useAtom } from 'jotai/index';

import { UIImage } from '@/components/UI/atoms/UIImage';
import { messagesAtom, regenerateAtom } from '@/components/agents/builder/atoms/messagesAtom.ts';
import { ChatType } from '@/components/agents/builder/types/Agents';

interface ChatMessageInProps {
  text: string;
  time: string;
  elapsedTime?: number;
  index: number;
  isRegenerating?: boolean;
}

const MessageIn = ({ text, time, elapsedTime, index, isRegenerating }: ChatMessageInProps) => {
  const [, setRegenerate] = useAtom(regenerateAtom);
  const [messages] = useAtom(messagesAtom);

  const handleRegenerate = () => {
    for (let i = index - 1; i >= 0; i--) {
      if (messages[i].type === ChatType.HUMAN || messages[i].role === 'user') {
        const userQuery = messages[i].content;
        setRegenerate({ trigger: true, query: userQuery, answerIndex: index, history: messages.slice(0, index) });
        break;
      }
    }
  };

  const formatElapsedTime = (milliseconds: number) => {
    const seconds = Math.floor(milliseconds / 1000);
    if (seconds < 60) {
      return `${seconds} Sec.`;
    }
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}m ${remainingSeconds}s`;
  };

  return (
    <div className='flex items-end gap-3.5 px-5'>
      <div className='flex flex-col gap-1.5 max-w-[80%]'>
        {isRegenerating ? (
          <div className='card flex max-w-[88px] flex-col items-center gap-2.5 rounded-tl-3xl rounded-tr-3xl rounded-br-3xl rounded-bl-sm bg-[#F3F6FB] p-4'>
            <div className='flex space-x-1'>
              <span className='inline-block h-[6px] w-[6px] animate-bounce rounded-full bg-gray-500' style={{ animationDelay: '0s' }}></span>
              <span className='inline-block h-[6px] w-[6px] animate-bounce rounded-full bg-gray-500' style={{ animationDelay: '0.15s' }}></span>
              <span className='inline-block h-[6px] w-[6px] animate-bounce rounded-full bg-gray-500' style={{ animationDelay: '0.3s' }}></span>
            </div>
            <span className='text-2xs font-medium text-gray-500'>재생성 중...</span>
          </div>
        ) : (
          <div
            className='card flex flex-col gap-2.5 rounded-bl-none bg-gray-100 p-3 text-2sm font-medium text-gray-700 shadow-none opacity-100 py-3 px-4 rounded-tl-3xl rounded-tr-3xl rounded-br-3xl rounded-bl-sm bg-[#F3F6FB] font-normal text-sm leading-5 tracking-[-0.01%] align-middle'
          >
            <div
              className='markdown-content break-words'
              style={{
                lineHeight: '1.6',
                maxWidth: '100%',
                wordWrap: 'break-word',
                overflowWrap: 'break-word',
                whiteSpace: 'pre-wrap',
              }}
            >
              {text}
            </div>
          </div>
        )}
        <div className='pl-3 align-middle font-normal text-xs leading-5 tracking-[-0.005%] align-middle text-[#576072]'>
          {!isRegenerating && (
            <>
              {time}
              {elapsedTime && ` (${formatElapsedTime(elapsedTime)})`}
            </>
          )}
          {isRegenerating && <span className='text-[#576072]'>재생성 중...</span>}
          <div className='flex items-center flex gap-[4px]'>
            <UIImage src='/assets/images/system/ico-system-20-reload.svg' alt='' />
            <button onClick={handleRegenerate} className='cursor-pointer text-primary hover:underline' disabled={isRegenerating}>
              <span className='font-semibold text-xs leading-5 tracking-[-0.00%] text-[#373E4D]'>{isRegenerating ? '재생성 중...' : '답변 재생성'}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export { MessageIn };