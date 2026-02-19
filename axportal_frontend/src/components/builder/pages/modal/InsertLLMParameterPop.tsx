import { UIImage } from '@/components/UI/atoms/UIImage';
import React, { useEffect, useState } from 'react';

interface ModelParameter {
  keyName: string;
  type: string;
  value: string | null;
}

interface DisabledParameter {
  keyName: string;
}

interface InsertLLMParameterPopProps {
  initialModelParameters?: ModelParameter[];
  initialDisabledParameters?: DisabledParameter[];
  initialDefaultValue?: string;
  onSave?: (modelParams: ModelParameter[], disabledParams: DisabledParameter[], defaultValue?: string) => void;
}

export const InsertLLMParameterPop: React.FC<InsertLLMParameterPopProps> = ({ initialModelParameters = [], initialDisabledParameters = [], initialDefaultValue = '', onSave }) => {
  const [modelParameters, setModelParameters] = useState<ModelParameter[]>(initialModelParameters.length > 0 ? initialModelParameters : []);

  const [disabledParameters, setDisabledParameters] = useState<DisabledParameter[]>(initialDisabledParameters.length > 0 ? initialDisabledParameters : []);

  const [defaultValue, setDefaultValue] = useState<string>(initialDefaultValue);

  // initial 값이 변경될 때 state 업데이트 (모달을 다시 열었을 때 저장된 값 표시)
  useEffect(() => {
    // 항상 initialModelParameters를 사용 (빈 배열이어도)
    setModelParameters(initialModelParameters.length > 0 ? initialModelParameters : []);
  }, [initialModelParameters]);

  useEffect(() => {
    // 항상 initialDisabledParameters를 사용 (빈 배열이어도)
    setDisabledParameters(initialDisabledParameters.length > 0 ? initialDisabledParameters : []);
  }, [initialDisabledParameters]);

  useEffect(() => {
    setDefaultValue(initialDefaultValue);
  }, [initialDefaultValue]);

  const handleAddModelParameter = () => {
    setModelParameters([...modelParameters, { keyName: '', type: 'string', value: '' }]);
  };

  const handleRemoveModelParameter = (index: number) => {
    setModelParameters(modelParameters.filter((_, i) => i !== index));
  };

  const handleModelParameterChange = (index: number, field: keyof ModelParameter, value: string | null) => {
    const updated = [...modelParameters];
    updated[index] = { ...updated[index], [field]: value };

    // 타입이 null로 변경되면 value도 null로 설정
    if (field === 'type' && value === 'null') {
      updated[index].value = null;
    }
    // 타입이 null에서 다른 타입으로 변경되면 value를 빈 문자열로 설정
    if (field === 'type' && value !== 'null' && updated[index].value === null) {
      updated[index].value = '';
    }

    setModelParameters(updated);
  };

  const handleAddDisabledParameter = () => {
    setDisabledParameters([...disabledParameters, { keyName: '' }]);
  };

  const handleRemoveDisabledParameter = (index: number) => {
    setDisabledParameters(disabledParameters.filter((_, i) => i !== index));
  };

  const handleDisabledParameterChange = (index: number, value: string) => {
    const updated = [...disabledParameters];
    updated[index] = { ...updated[index], keyName: value };
    setDisabledParameters(updated);
  };

  // 전역 핸들러 등록 - 모달이 열릴 때
  useEffect(() => {
    const handleSave = () => {
      // 빈 행 제거
      const filteredModelParams = modelParameters.filter(p => p.keyName.trim() !== '');
      const filteredDisabledParams = disabledParameters.filter(p => p.keyName.trim() !== '');

      if (onSave) {
        onSave(filteredModelParams, filteredDisabledParams, defaultValue);
      }
    };

    (window as any).defaultValueApplyHandler = handleSave;

    return () => {
      delete (window as any).defaultValueApplyHandler;
    };
  }, [modelParameters, disabledParameters, defaultValue, onSave]);

  return (
    <section className='section-modal'>
      <div className='flex flex-col h-full'>
        {/* 안내 메시지 */}
        <div className='mb-6 p-4 bg-blue-50 border border-blue-200 rounded-lg'>
          <p className='text-sm text-gray-700'>외부 변수 값이 없는 경우, 아래 값이 기본값으로 사용됩니다.</p>
        </div>

        {/* Model Parameters Section */}
        <div className='mb-6'>
          <div className='flex items-center justify-between mb-3'>
            <h3 className='text-base font-semibold text-gray-800'>Model Parameters</h3>
            <button
              onClick={handleAddModelParameter}
              className='px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 cursor-pointer'
            >
              Add
            </button>
          </div>

          {/* Table Header */}
          <div className='grid grid-cols-[1fr_140px_1fr_40px] gap-2 mb-2 px-2'>
            <div className='text-sm font-medium text-gray-700'>Key Name</div>
            <div className='text-sm font-medium text-gray-700'>Type</div>
            <div className='text-sm font-medium text-gray-700'>Value</div>
            <div></div>
          </div>

          {/* Table Rows */}
          <div className='space-y-2'>
            {modelParameters.map((param, index) => (
              <div key={index} className='grid grid-cols-[1fr_140px_1fr_40px] gap-2 items-center'>
                <input
                  type='text'
                  value={param.keyName}
                  onChange={e => handleModelParameterChange(index, 'keyName', e.target.value)}
                  placeholder='Key Name'
                  className='border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                />
                <select
                  value={param.type}
                  onChange={e => handleModelParameterChange(index, 'type', e.target.value)}
                  className='border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                >
                  <option value='string'>string</option>
                  <option value='list'>list</option>
                  <option value='int'>int</option>
                  <option value='float'>float</option>
                  <option value='boolean'>boolean</option>
                  <option value='null'>null</option>
                </select>
                <input
                  type='text'
                  value={param.value ?? ''}
                  onChange={e => handleModelParameterChange(index, 'value', e.target.value)}
                  placeholder='Value'
                  disabled={param.type === 'null'}
                  className='border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                />
                <button
                  onClick={() => handleRemoveModelParameter(index)}
                  className='flex items-center justify-center w-10 h-10 text-gray-500 hover:text-red-600 transition-colors cursor-pointer'
                  style={{
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
                  title='삭제'
                >
                  <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='Delete' className='w-5 h-5' />
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Disabled Parameters Section */}
        <div className='mb-6'>
          <div className='flex items-center justify-between mb-3'>
            <h3 className='text-base font-semibold text-gray-800'>Disabled Parameters</h3>
            <button
              onClick={handleAddDisabledParameter}
              className='px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 cursor-pointer'
            >
              Add
            </button>
          </div>

          {/* Table Header */}
          <div className='grid grid-cols-[1fr_40px] gap-2 mb-2 px-2'>
            <div className='text-sm font-medium text-gray-700'>Key Name</div>
            <div></div>
          </div>

          {/* Table Rows */}
          <div className='space-y-2'>
            {disabledParameters.map((param, index) => (
              <div key={index} className='grid grid-cols-[1fr_40px] gap-2 items-center'>
                <input
                  type='text'
                  value={param.keyName}
                  onChange={e => handleDisabledParameterChange(index, e.target.value)}
                  placeholder='Key Name'
                  className='border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                />
                <button
                  onClick={() => handleRemoveDisabledParameter(index)}
                  className='flex items-center justify-center w-10 h-10 text-gray-500 hover:text-red-600 transition-colors cursor-pointer'
                  style={{
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
                  title='삭제'
                >
                  <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='Delete' className='w-5 h-5' />
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};
