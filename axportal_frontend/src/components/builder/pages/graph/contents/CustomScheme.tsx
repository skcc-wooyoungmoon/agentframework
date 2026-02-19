import { UIImage } from '@/components/UI/atoms/UIImage';
import { useAtom } from 'jotai/index';
import { isEqual } from 'lodash';
import { useCallback, useEffect, useRef } from 'react';

import { keyTableAtom } from '@/components/builder/atoms/AgentAtom';
import { ABClassNames } from '@/components/builder/components/ui';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { useNodeValidation } from '@/components/builder/hooks/useNodeValidation.ts';
import { type CustomNodeInnerData, type InputKeyItem, type OutputKeyItem } from '@/components/builder/types/Agents';
import { useModal } from '@/stores/common/modal';
import KeyTables from '../controller/KeyTables';
import { CustomErrorMessage } from '../node/common/CustomErrorMessage';

interface CustomSchemeProps {
  id: string;
  inputKeys: InputKeyItem[];
  // eslint-disable-next-line no-unused-vars
  setInputKeys: (key: InputKeyItem[]) => void;
  inputValues: string[];
  // eslint-disable-next-line no-unused-vars
  setInputValues: (key: string[]) => void;
  innerData: CustomNodeInnerData;
  outputKeys: OutputKeyItem[];
  type?: string;
  disabledKeyIn?: boolean;
  disabledAddInput?: boolean;
}

const CustomScheme = ({
  id,
  inputKeys,
  setInputKeys,
  inputValues,
  setInputValues,
  outputKeys,
  type = 'agent__generator',
  disabledKeyIn = false,
  disabledAddInput = false,
}: CustomSchemeProps) => {
  const { validateNode, getValidation } = useNodeValidation();
  const validation = getValidation(id, 'input');
  const { syncAllNodeKeyTable } = useGraphActions();
  const [keyTableList] = useAtom(keyTableAtom);
  const stableInputKeysRef = useRef(inputKeys);
  const { openModal } = useModal();

  const getKeyTableValue = useCallback(
    (keytableId: string): string => {
      const keyTableEntry = keyTableList.find(entry => entry.id === keytableId);
      return keyTableEntry?.key || '';
    },
    [keyTableList]
  );

  useEffect(() => {
    if (keyTableList.length > 0 && !isEqual(stableInputKeysRef.current, inputKeys)) {
      const initializedInputKeys = inputKeys.map(item => {
        if (item.keytable_id) {
          const value = getKeyTableValue(item.keytable_id);
          if (value) {
            return {
              ...item,
              keytable_id: item.keytable_id,
            };
          }
        }
        return item;
      });

      if (!isEqual(initializedInputKeys, inputKeys)) {
        setInputKeys(initializedInputKeys);
        stableInputKeysRef.current = initializedInputKeys;
      }
    }
  }, [keyTableList]);

  useEffect(() => {
    validateNode(id, type, inputKeys);
  }, [id, type, inputKeys, validateNode, keyTableList]);

  const dummyInputItem: InputKeyItem = {
    name: '',
    required: false,
    keytable_id: '',
    fixed_value: null,
  };

  // openModal, handleSelect, saveModal
  const handleOpenKeyTableModal = (index: number) => {
    syncAllNodeKeyTable();
    const tempValue = inputKeys[index]?.fixed_value || ''; // 직접 입력 값
    const tempKeyTableId = inputKeys[index]?.keytable_id || null; // 키테이블 ID
    // 🔥 키테이블 모드가 기본 (keytable_id가 있으면 키테이블 모드, 둘 다 없으면 키테이블 모드)
    // fixed_value만 있고 keytable_id가 없으면 직접입력 모드
    const isKeyTableMode = !!(tempKeyTableId && tempKeyTableId.trim() !== '') || (!tempKeyTableId && !tempValue); // 키테이블 모드 여부

    // 🔥 모달 상태를 저장할 객체 (클로저로 최신 상태 유지)
    const modalState = {
      isKeyTable: isKeyTableMode,
      selectedId: tempKeyTableId,
      tempValue: tempValue,
    };

    // console.log('🔍 키테이블 모달 상태 :::::::', modalState);

    // 🔥 상태 변경 핸들러 (객체 속성 직접 수정)
    const handleStateChange = (state: { isKeyTable: boolean; selectedId: string | null; tempValue: string }) => {
      modalState.isKeyTable = state.isKeyTable;
      modalState.selectedId = state.selectedId;
      modalState.tempValue = state.tempValue;
      // console.log('🔍 키테이블 모달 상태 변경:', modalState);
    };

    openModal({
      title: '키테이블',
      type: 'medium',
      body: (
        <KeyTables
          initTempValue={tempValue}
          initSelectedId={tempKeyTableId}
          initVisibleKeyTables={isKeyTableMode}
          disabledKeyIn={disabledKeyIn}
          onStateChange={handleStateChange}
        />
      ),
      showFooter: true,
      confirmText: '저장',
      onConfirm: () => {
        // saveModal
        const updatedInputKeys = [...inputKeys];
        // 🔥 최신 상태 가져오기
        const { isKeyTable, selectedId, tempValue: finalTempValue } = modalState;

        updatedInputKeys[index] = {
          ...updatedInputKeys[index],
          keytable_id: isKeyTable ? selectedId : '', // 테이블 선택 값 저장
          fixed_value: isKeyTable ? null : finalTempValue, // 직접 입력 값 저장
        };

        setInputKeys(updatedInputKeys);
      },
    });
  };

  const handleInputKeyChange = (index: number, keyType: 'name' | 'fixed_value', value: string) => {
    const updatedInputKeys = [...inputKeys];
    if (keyType === 'fixed_value') {
      updatedInputKeys[index] = {
        ...updatedInputKeys[index],
        fixed_value: value,
        keytable_id: '',
      };
    } else if (keyType === 'name') {
      updatedInputKeys[index] = {
        ...updatedInputKeys[index],
        name: value,
      };
    }
    setInputKeys(updatedInputKeys);

    const newInputValues = [...inputValues];
    newInputValues[index] = value;
    setInputValues(newInputValues);
  };

  const handleRemoveInput = (index: number) => {
    setInputKeys(inputKeys.filter((_, i) => i !== index));
  };

  // [입력 추가] ==> 더미데이터 추가
  const handleAddInput = () => {
    setInputKeys([...inputKeys, dummyInputItem]);
  };

  const renderInputField = (item: InputKeyItem, index: number) => {
    const inputErrors = validation?.errors.filter(error => error.details?.inputIndex === index) || [];
    const hasError = inputErrors.length > 0;
    const keyTableValue = item.keytable_id ? getKeyTableValue(item.keytable_id) : '';

    return (
      <div
        key={index}
        className={`relative ${
          // 필수 입력값이 비어있으면 하단 여백 추가 (에러 메시지 공간)
          hasError ? 'mb-5' : ''
        }`}
      >
        <div
          className={`flex items-center gap-2 rounded-lg bg-white p-2 border overflow-hidden ${
            // 필수 입력값이 비어있으면 빨간색 border
            hasError ? 'ag-border-red' : 'border-gray-300'
          }`}
        >
          {/* Key Input */}
          <div className={'flex-1 min-w-0'}>
            <input
              type='text'
              value={item.name || ''}
              onChange={e => handleInputKeyChange(index, 'name', e.target.value)}
              className={ABClassNames(
                'input nodrag',
                hasError ? 'w-full ag-border-red ring-red-500 focus:ag-border-red focus:ring-red-500' : 'w-full border-gray-300 focus:border-gray-300'
              )}
              placeholder={item.name || 'Key 입력'}
              disabled={item.required}
            />
          </div>

          {/* Value Input */}
          <div className='relative items-center flex-1 min-w-0 overflow-hidden'>
            {/* 🔥 keytable_id가 Key table에 있으면 표시 */}
            {keyTableValue ? (              
              <div
                className='w-full rounded bg-gray-50 px-3 py-2 text-sm border overflow-hidden'
                style={{
                  backgroundColor: '#F1F1F4',
                  color: '#5C5B75',
                  borderColor: '#E5E5E5',
                }}
              >
                <div className='flex items-center justify-between gap-2 w-[140px]'>
                  <div className='flex items-center gap-2 min-w-0 flex-1 overflow-hidden'>
                    <span
                      className={`badge badge-circle badge-${item.keytable_id?.endsWith('__global') || item.keytable_id?.endsWith('_global') ? 'success' : 'primary'}`}
                      style={{
                        width: 20,
                        height: 20,
                        verticalAlign: 'middle',
                        flexShrink: 0,
                      }}
                    >
                      {item.keytable_id?.endsWith('_global') ? 'G' : 'L'}
                    </span>
                    <span className='' style={{ textOverflow: 'ellipsis', whiteSpace: 'nowrap', overflow: 'hidden' }}>
                      {keyTableValue}
                    </span>
                  </div>
                  {/* 입력값 삭제 버튼 */}
                  <button
                    title=''
                    className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
                    style={{
                      width: 20,
                      height: 20,
                      verticalAlign: 'middle',
                      flexShrink: 0,
                    }}
                    onClick={() => {
                      const updatedInputKeys = [...inputKeys];
                      updatedInputKeys[index] = {
                        ...updatedInputKeys[index],
                        fixed_value: null,
                        keytable_id: '',
                      };
                      setInputKeys(updatedInputKeys);
                    }}
                  >
                    ×
                  </button>
                </div>
              </div>
            ) : (
              <input
                type='text'
                value={item.fixed_value || (item.keytable_id ? getKeyTableValue(item.keytable_id) : '')}
                onChange={e => {
                  const updatedInputKeys = [...inputKeys];
                  updatedInputKeys[index] = {
                    ...updatedInputKeys[index],
                    fixed_value: e.target.value,
                    keytable_id: '',
                  };
                  setInputKeys(updatedInputKeys);
                }}
                disabled
                className={`input w-full border-0 outline-none truncate ${
                  // query 필드이고 값이 비어있으면 빨간색 border
                  item.name === 'query' && !item.fixed_value && !item.keytable_id ? '' : ''
                }`}
                placeholder='Value 선택'
              />
            )}
          </div>

          <div className='flex gap-2 flex-shrink-0'>
            {/* Search Button */}
            <button
              type='button'
              onClick={() => handleOpenKeyTableModal(index)}
              className='btn-icon btn btn-sm btn-light text-primary flex-shrink-0'
              style={{
                backgroundColor: '#ffffff',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                padding: '6px',
                color: '#6b7280',
                cursor: 'pointer',
                fontSize: '14px',
                transition: 'all 0.2s ease',
                minWidth: '32px',
                width: '32px',
                height: '32px',
              }}
            >
              <UIImage src='/assets/images/system/ico-system-24-outline-gray-search.svg' alt='No data' className='w-20 h-20' />
            </button>

            {/* 입력 항목 Delete Button */}
            <button
              onClick={() => handleRemoveInput(index)}
              className='btn-icon btn btn-sm btn-light text-primary flex-shrink-0 btn-bg-del cursor-pointer'
              style={{
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                padding: '6px',
                color: '#ffffff',
                cursor: 'pointer',
                fontSize: '14px',
                transition: 'all 0.2s ease',
                minWidth: '32px',
                width: '32px',
                height: '32px',
              }}
              disabled={item.required || (type === 'agent__generator' && item.name === 'context' && index === 1)}
            >
              <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
            </button>
          </div>
        </div>

        {/* Error Messages */}
        {inputErrors.map((error, errorIndex) => (
          <div key={errorIndex} className='mt-1'>
            <CustomErrorMessage message={error.message} />
          </div>
        ))}
      </div>
    );
  };

  // CustomScheme 영역은 unfold 시 사라지는 영역으로 innerData.isToggle 처리 필요 없음
  return (
    <div className='bg-white p-3 shadow-sm px-5'>
      <div className='flex flex-col w-full'>
        {/* Inputs Section */}
        <div className='w-full'>
          <h4 className='mb-4 w-full text-left text-lg font-semibold'>{'입력'}</h4>
          <div className={`min-h-[100px]`}>
            <div className='flex w-full flex-col gap-3'>
              {inputKeys.map((item, index) => renderInputField(item, index))}
              {type !== 'agent__app' && (
                <div className='mt-2 flex justify-center'>
                  <button onClick={handleAddInput} disabled={disabledAddInput} className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'>
                    {'입력 추가'}
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Separator Line */}
        <div className='mx-4 w-px bg-gray-300' />

        {/* Outputs Section */}
        <div className='w-full flex-shrink-0'>
          <h4 className='mb-4 text-left text-lg font-semibold'>{'출력'}</h4>
          <div className='flex w-full flex-col gap-3'>
            {outputKeys.map((item, index) => (
              <div key={index} className='w-full h-[40px] leading-[40px] text-sm text-gray-500 px-3 bg-gray-50 rounded-lg border border-gray-200 flex items-center'>
                {item.name || 'output'}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export { CustomScheme };
