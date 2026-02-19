import { agentAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { useAtom } from 'jotai/index';
import React, { useEffect, useState } from 'react';

interface UpdateDescriptionPopProps {
  modalId?: string;
  onConfirm?: (name: string, description: string) => void;
  onValidityChange?: (isValid: boolean) => void;
}

export const UpdateDescriptionPop: React.FC<UpdateDescriptionPopProps> = ({ onConfirm, onValidityChange }) => {
  const [agent] = useAtom(agentAtom);

  // @ts-ignore
  const [name, setName] = useState((agent.name || '').substring(0, 50));
  const [description, setDescription] = useState((agent?.description || '').substring(0, 100));

  const isNameValid = name.trim().length > 0;

  useEffect(() => {
    if (onConfirm) {
      onConfirm(name, description);
    }
    if (onValidityChange) {
      onValidityChange(isNameValid);
    }
  }, []);

  useEffect(() => {
    if (onConfirm) {
      onConfirm(name, description);
    }
  }, [name, description, onConfirm]);

  useEffect(() => {
    if (onValidityChange) {
      onValidityChange(isNameValid);
    }
  }, [isNameValid, onValidityChange]);

  return (
    <div className='mt-7'>
      <div className='grid gap-5'>
        <div className='mb-4 flex flex-row items-center gap-2'>
          <label className={`form-label flex items-center gap-1 max-w-56`}>
            {'Agent 이름'}
            <span className='ag-color-red ml-0.5'>*</span>
          </label>
          <div className={'w-full'}>
            <input
              type='text'
              placeholder='에이전트 이름 입력'
              className={['textarea w-full rounded-lg border border-gray-300 px-3 py-2 pr-10 shadow-sm', {
                'ag-border-red dark:border-red-400': !isNameValid,
              }].filter(e => !!e).join(' ')}
              value={name}
              onInput={e => {
                const input = e.target as HTMLInputElement;
                const limitedValue = input.value.substring(0, 50);
                input.value = limitedValue;
                setName(limitedValue);
              }}
              onPaste={e => {
                e.preventDefault();
                const pastedText = e.clipboardData.getData('text');
                const limitedValue = pastedText.substring(0, 50);
                setName(limitedValue);
              }}
              maxLength={50}
            />
            {!isNameValid && (
              <div className='mt-1 flex items-center gap-2 text-sm ag-color-red'>
                <i className='ki-filled ki-information-1' aria-hidden='true' />
                <span>{'Agent 이름을 입력해주세요.'}</span>
              </div>
            )}
            <div className='mt-1 text-xs text-gray-500'>{name.length}/50</div>
          </div>
        </div>

        <div className='mb-4 flex flex-row items-center gap-2'>
          <label className='form-label flex w-1/3 max-w-56 items-center text-sm font-medium text-gray-700'>{'설명'}</label>
          <div className={'relative w-full'}>
            <textarea
              placeholder='에이전트 설명 입력'
              rows={3}
              className={['textarea w-full rounded-lg border border-gray-300 px-3 py-2 pr-10 shadow-sm'].filter(e => !!e).join(' ')}
              value={description}
              onInput={e => {
                const textarea = e.target as HTMLTextAreaElement;
                const limitedValue = textarea.value.substring(0, 100);
                textarea.value = limitedValue;
                setDescription(limitedValue);
              }}
              onPaste={e => {
                e.preventDefault();
                const pastedText = e.clipboardData.getData('text');
                const limitedValue = pastedText.substring(0, 100);
                setDescription(limitedValue);
              }}
              maxLength={100}
            ></textarea>
            <div className='mt-1 text-xs text-gray-500'>{description.length}/100</div>
          </div>
        </div>
      </div>
    </div>
  );
};
