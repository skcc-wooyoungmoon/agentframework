import React, { useEffect, useRef, useState } from 'react';
import { useAtom } from 'jotai';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom';
import { type CustomNodeInnerData } from '@/components/agents/builder/types/Agents';
import { getNodeTitleByName } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface HeaderActionProps {
  type: string;
  data: CustomNodeInnerData;
  onClickLog?: () => void;
  onClickDelete?: () => void;
  defaultValue: string;
  onChange?: (field: string, value: string) => void;
  nodeId: string;
}

export const NodeHeader = ({ type, data, onClickDelete, defaultValue, onChange, onClickLog }: HeaderActionProps) => {
  const {
    stopPropagation,
    preventAndStop,
    autoResize
  } = useNodeHandler();

  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const [text, setText] = useState(defaultValue);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleLogButton = (e: React.MouseEvent) => {
    e.stopPropagation();
    e.preventDefault();
    if (!hasChatTested) {
      alert('채팅 테스트를 먼저 실행해주세요.\n채팅 테스트 후 빌더 로그를 확인할 수 있습니다.');
      return;
    }
    if (onClickLog) onClickLog();
  };

  const handleDeleteButton = (e: React.MouseEvent) => {
    e.stopPropagation();
    e.preventDefault();
    if (onClickDelete) {
      onClickDelete();
    }
  };

  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = textareaRef.current.scrollHeight + 'px';
    }
  }, [text]);

  const isolatedEventHandlers = {
    onMouseDown: stopPropagation,
    onMouseUp: stopPropagation,
    onselect: stopPropagation,
    onDragStart: preventAndStop,
    onDrag: preventAndStop,
  } as const;

  return (
    <>
      <div className='card-header flex min-w-[320px] flex-col gap-5' style={{ padding: '20px 20px 0 20px' }}>
        <div className='flex w-full items-start justify-between'>
          <div className='flex items-end flex-1 min-w-0'>
            <span className='text-lg font-bold text-blue-700 truncate'>{getNodeTitleByName(type)}</span>
            <button
              className='btn-icon btn btn-sm flex-shrink-0'
              style={{
                backgroundColor: 'transparent',
                border: hasChatTested && data.isRun ? '1px solid #bfdbfe' : 'none',
                padding: '6px',
                cursor: 'default',
                width: '36px',
                height: '36px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <img
                alt='bell-icon'
                className='w-[24px] h-[24px]'
                src='/assets/images/system/ico-system-24-outline-gray-alarm.svg'
                style={{
                  filter:
                    hasChatTested && data.isRun
                      ? 'brightness(0) saturate(100%) invert(36%) sepia(96%) saturate(2345%) hue-rotate(201deg) brightness(99%) contrast(101%)' // 파란색 #3E97FF
                      : 'opacity(0.5)',
                  transition: 'filter 0.3s ease',
                }}
              />
            </button>
            <button
              className='btn-icon btn btn-sm flex-shrink-0'
              onClick={handleLogButton}
              title={hasChatTested ? '빌더 로그' : '채팅 테스트를 먼저 실행해주세요'}
              disabled={!hasChatTested}
              style={{
                backgroundColor: 'transparent',
                border: 'none',
                padding: '6px',
                cursor: hasChatTested ? 'pointer' : 'not-allowed',
                width: '36px',
                height: '36px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <img
                alt='ico-system-24-outline-gray-log'
                className='w-[24px] h-[24px]'
                src='/assets/images/system/ico-system-24-outline-gray-log.svg'
                style={{ opacity: hasChatTested ? 1 : 0.5 }}
              />
            </button>
          </div>

          <div className='flex-shrink-0'>
            <button
              className='btn-icon btn btn-sm btn-light text-primary btn-bg-del'
              onClick={handleDeleteButton}
              style={{
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                padding: '6px',
                color: '#6b7280',
                cursor: 'pointer',
                fontSize: '14px',
                transition: 'all 0.2s ease',
              }}
              title='삭제'
            >
              <img alt='ico-system-24-outline-gray-trash' className='w-[24px] h-[24px]  ' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
            </button>
          </div>
        </div>
        <div className='mb-5 w-full'>
          <textarea
            ref={textareaRef}
            rows={1}
            className={['nodrag w-full resize-none rounded-lg border px-3 py-2 text-sm text-gray-700 border-gray-300 focus:border-gray-300'].filter(e => !!e).join(' ')}
            style={{
              maxHeight: '80px',
              overflow: 'hidden',
            }}
            placeholder={'노드 명'}
            value={text}
            onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => {
              const newValue = e.target.value;
              setText(newValue);
              if (onChange) {
                onChange(newValue, 'nodeName');
              }
              autoResize(e.target);
            }}
            onInput={(e: any) => {
              autoResize(e.target);
            }}
            {...isolatedEventHandlers}
          />
        </div>
      </div>
    </>
  );
};
