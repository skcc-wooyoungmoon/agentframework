// import { CustomErrorMessage } from "@/app/common/components/common/CustomErrorMessage.tsx";
import React, { useCallback, useEffect, useState } from 'react';

import { useAtom } from 'jotai';

import { nodesAtom } from '@/components/builder/atoms/AgentAtom';
import { ABClassNames } from '@/components/builder/components/ui';
import { useNodeValidation } from '@/components/builder/hooks';
import { type CustomNodeInnerData } from '@/components/builder/types/Agents';
import { getNodeTitleByName } from '@/components/builder/utils/GraphUtils.ts';
import { CustomErrorMessage } from './CustomErrorMessage';

interface HeaderActionProps {
  type: string;
  data: CustomNodeInnerData;
  onClickLog?: () => void;
  onClickDelete?: () => void;
  defaultValue: string;
  onChange?: (value: string) => void;
  nodeId: string;
}

export const NodeHeader = ({ type, data, onClickLog, onClickDelete, defaultValue, onChange, nodeId }: HeaderActionProps) => {
  const [nodes] = useAtom(nodesAtom);
  const [text, setText] = useState(defaultValue);
  const { updateValidation, getValidation, clearValidation } = useNodeValidation();

  const validation = getValidation(nodeId);
  const isDuplicate = validation?.errors.some(e => e.type === 'DUPLICATE_NAME');

  const checkDuplicateName = useCallback(() => {
    if (!nodeId || !text.trim()) {
      updateValidation(nodeId, true, []);
      return false;
    }

    const sameTypeNodes = nodes.filter(node => node.type === type && node.id !== nodeId);
    const duplicateExists = sameTypeNodes.some(node => node.data?.name === text);

    updateValidation(
      nodeId,
      !duplicateExists,
      duplicateExists
        ? [
            {
              type: 'DUPLICATE_NAME',
              message: '노드 이름이 중복되었습니다.',
            },
          ]
        : []
    );

    return duplicateExists;
  }, [nodeId, text, nodes, type, updateValidation, data]);

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      checkDuplicateName();
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [text, checkDuplicateName]);

  const handleLogButton = () => {
    if (onClickLog) onClickLog();
  };

  const handleDeleteButton = () => {
    if (onClickDelete) {
      onClickDelete();
    }
  };

  const handleTextChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    setText(newValue);

    if (onChange) {
      onChange(newValue);
    }
  };

  useEffect(() => {
    return () => {
      clearValidation(nodeId);
    };
  }, [nodeId, clearValidation]);

  return (
    <div className='card-header flex min-w-[320px] flex-col gap-5' style={{ padding: '20px 20px 0 20px' }}>
      {/* 상단: 타이틀과 버튼들 */}
      <div className='flex w-full items-start justify-between'>
        {/* 타이틀 */}
        <div className='flex items-end flex-1 min-w-0'>
          <span className='text-lg font-bold text-blue-700 truncate'>{getNodeTitleByName(type)}</span>
          {type !== 'note' && (
            <>
              {/* 종 아이콘 - 로그 버튼 방식: hasChatTested && data.isRun 체크하여 색상 변경 */}
              <button
                className='btn-icon btn btn-sm flex-shrink-0'
                style={{
                  backgroundColor: 'transparent',
                  border: 'none',
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
                    filter: data.isRun
                      ? 'brightness(0) saturate(100%) invert(36%) sepia(96%) saturate(2345%) hue-rotate(201deg) brightness(99%) contrast(101%)' // 파란색 #3E97FF
                      : 'opacity(0.5)', // 회색
                    transition: 'filter 0.3s ease',
                  }}
                />
              </button>
              {/* 로그 버튼 */}
              <button
                className='btn-icon btn btn-sm flex-shrink-0'
                onClick={handleLogButton}
                title={'빌더 로그'}
                style={{
                  backgroundColor: 'transparent',
                  border: 'none',
                  padding: '6px',
                  cursor: data.isRun ? 'pointer' : '',
                  width: '36px',
                  height: '36px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                {/* 📝 */}
                <img
                  alt='ico-system-24-outline-gray-log'
                  className='w-[24px] h-[24px]'
                  src='/assets/images/system/ico-system-24-outline-gray-log.svg'
                  style={{ opacity: data.isRun ? 1 : 0.5 }}
                />
              </button>
            </>
          )}
        </div>

        {/* 삭제 버튼 */}
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

      {/* 하단: Input 필드 */}
      <div className='mb-5 w-full'>
        <input
          type='text'
          className={ABClassNames(
            'w-full rounded-lg border px-3 py-2 text-sm text-gray-700 nodrag',
            isDuplicate ? 'border-red-500 ring-red-500 focus:border-red-500 focus:ring-red-500' : 'border-gray-300 focus:border-gray-300'
          )}
          placeholder={'노드 명'}
          value={text}
          onChange={handleTextChange}
        />
        {validation?.errors.map((error, index) => (
          <div key={index}>
            <CustomErrorMessage message={error.message} className='mt-1' />
          </div>
        ))}
      </div>
    </div>
  );
};
