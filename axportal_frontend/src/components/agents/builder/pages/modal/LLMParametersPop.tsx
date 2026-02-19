import React, { useState } from 'react';
import { UITypography } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup, UIInput, UIDropdown } from '@/components/UI/molecules';

interface Parameter {
  key: string;
  type: string;
  value: string;
}

interface InactiveParameter {
  key: string;
}

interface LLMParametersPopProps {
  modalId?: string;
  initialParameters?: Parameter[];
  initialInactiveParameters?: InactiveParameter[];
  onConfirm?: (parameters: Parameter[], inactiveParameters: InactiveParameter[]) => void;
  onConfirmRef?: React.MutableRefObject<(() => void) | null> | { current: (() => void) | null };
}

export const LLMParametersPop: React.FC<LLMParametersPopProps> = ({
  modalId: _modalId,
  initialParameters = [],
  initialInactiveParameters = [],
  onConfirm,
  onConfirmRef,
}) => {
  const [parameters, setParameters] = useState<Parameter[]>(
    initialParameters.length > 0
      ? initialParameters
      : [{ key: '', type: '', value: '' }]
  );
  const [inactiveParameters, setInactiveParameters] = useState<InactiveParameter[]>(
    initialInactiveParameters.length > 0
      ? initialInactiveParameters
      : []
  );
  const [dropdownStates, setDropdownStates] = useState<Record<string, boolean>>({});

  const handleParameterChange = (index: number, field: keyof Parameter, value: string) => {
    const newParameters = [...parameters];
    newParameters[index][field] = value;
    setParameters(newParameters);
  };

  const handleInactiveParameterChange = (index: number, value: string) => {
    const newInactiveParameters = [...inactiveParameters];
    newInactiveParameters[index].key = value;
    setInactiveParameters(newInactiveParameters);
  };

  const handleDropdownToggle = (key: string) => {
    setDropdownStates(prev => ({ ...prev, [key]: !prev[key] }));
  };

  const handleDropdownSelect = (index: number, value: string) => {
    handleParameterChange(index, 'type', value);
    setDropdownStates(prev => ({ ...prev, ['type_' + index]: false }));
  };

  const handleAddParameter = () => {
    setParameters([...parameters, { key: '', type: '', value: '' }]);
  };

  const handleRemoveParameter = (index: number) => {
    const newParameters = parameters.filter((_, i) => i !== index);
    setParameters(newParameters.length > 0 ? newParameters : [{ key: '', type: '', value: '' }]);
  };

  const handleAddInactiveParameter = () => {
    setInactiveParameters([...inactiveParameters, { key: '' }]);
  };

  const handleRemoveInactiveParameter = (index: number) => {
    const newInactiveParameters = inactiveParameters.filter((_, i) => i !== index);
    setInactiveParameters(newInactiveParameters);
  };

  const handleConfirm = () => {
    if (onConfirm) {
      const validParameters = parameters.filter(p => p.key.trim() !== '' && p.type.trim() !== '' && p.value.trim() !== '');
      const validInactiveParameters = inactiveParameters.filter(p => p.key.trim() !== '');
      onConfirm(validParameters, validInactiveParameters);
    }
  };

  React.useEffect(() => {
    if (onConfirmRef) {
      onConfirmRef.current = handleConfirm;
    }
    return () => {
      if (onConfirmRef) {
        onConfirmRef.current = null;
      }
    };
  }, [parameters, inactiveParameters, onConfirm, onConfirmRef]);

  return (
    <section className='section-modal'>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup gap={4} direction='column'>
            <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={false}>
              모델 파라미터
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              모델 파라미터를 설정해주세요.
            </UITypography>
          </UIGroup>
          <UIGroup gap={8} direction='column' align='start'>
            {parameters.map((param, index) => (
              <UIGroup key={index} gap={8} direction='row' align='start'>
                <div className='flex-1'>
                  <UIInput.Text
                    value={param.key}
                    onChange={e => handleParameterChange(index, 'key', e.target.value)}
                    placeholder='Key 이름 입력'
                  />
                </div>
                <div className='flex-1'>
                  <UIDropdown
                    value={param.type || 'Type 선택'}
                    placeholder='Type 선택'
                    options={[
                      { value: 'float', label: 'Float' },
                      { value: 'int', label: 'Int' },
                      { value: 'bool', label: 'Bool' },
                      { value: 'list', label: 'List' },
                      { value: 'null', label: 'Null' },
                      { value: 'string', label: 'String' },
                      { value: 'json', label: 'JSON' },
                      { value: 'object', label: 'Object' },
                    ]}
                    isOpen={dropdownStates['type_' + index] || false}
                    onClick={() => handleDropdownToggle('type_' + index)}
                    onSelect={value => handleDropdownSelect(index, value)}
                  />
                </div>
                <div className='flex-1'>
                  <UIInput.Text
                    value={param.value}
                    onChange={e => handleParameterChange(index, 'value', e.target.value)}
                    placeholder='Value 입력'
                  />
                </div>
                <UIButton2
                  className='ic-system-48-delete cursor-pointer'
                  onClick={() => handleRemoveParameter(index)}
                />
              </UIGroup>
            ))}
            <UIButton2 className='btn-secondary-outline-blue w-[121px]' onClick={handleAddParameter}>
              파라미터 추가
            </UIButton2>
          </UIGroup>
        </UIFormField>
      </UIArticle>

      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup gap={4} direction='column'>
            <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={false}>
              비활성 파라미터
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              비활성 파라미터가 필요한 경우, [파라미터 추가]버튼을 클릭하여 설정해주세요.
            </UITypography>
          </UIGroup>
          <UIGroup gap={8} direction='column' align='start'>
            {inactiveParameters.map((param, index) => (
              <UIGroup key={index} gap={8} direction='row' align='start'>
                <div className='flex-1'>
                  <UIInput.Text
                    value={param.key}
                    onChange={e => handleInactiveParameterChange(index, e.target.value)}
                    placeholder='Key 이름 입력'
                  />
                </div>
                <UIButton2
                  className='ic-system-48-delete cursor-pointer'
                  onClick={() => handleRemoveInactiveParameter(index)}
                />
              </UIGroup>
            ))}
            <UIButton2 className='btn-secondary-outline-blue w-[121px]' onClick={handleAddInactiveParameter}>
              파라미터 추가
            </UIButton2>
          </UIGroup>
        </UIFormField>
      </UIArticle>
    </section>
  );
};

