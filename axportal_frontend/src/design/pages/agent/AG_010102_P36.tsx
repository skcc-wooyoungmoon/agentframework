// AG_010102_P36 페이지
import { useState } from 'react';
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

export const AG_010102_P36 = () => {
  const [parameters, setParameters] = useState<Parameter[]>([
    { key: '', type: '', value: '' },
    { key: '', type: '', value: '' },
    { key: '', type: '', value: '' },
  ]);
  const [inactiveParameters, setInactiveParameters] = useState<InactiveParameter[]>([{ key: '' }, { key: '' }, { key: '' }]);
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

  return (
    <section className='section-modal'>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup gap={4} direction='column'>
            <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={false}>
              모델 파라미터
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              설정한 지식의 정상 여부를 테스트하기 위해 쿼리를 입력 해주세요.
            </UITypography>
          </UIGroup>
          <UIGroup gap={8} direction='column' align='start'>
            {parameters.map((param, index) => (
              <UIGroup key={index} gap={8} direction='row' align='start'>
                <div className='flex-1'>
                  <UIInput.Text value={param.key} onChange={e => handleParameterChange(index, 'key', e.target.value)} placeholder='Key 이름 입력' />
                </div>
                <div className='flex-1'>
                  <UIDropdown
                    value={param.type || 'Type 선택'}
                    placeholder='조회 조건 선택'
                    options={[
                      { value: 'Float', label: 'Float' },
                      { value: 'Int', label: 'Int' },
                      { value: 'Bool', label: 'Bool' },
                      { value: 'List', label: 'List' },
                      { value: 'Null', label: 'Null' },
                    ]}
                    isOpen={dropdownStates['type_' + index] || false}
                    onClick={() => handleDropdownToggle('type_' + index)}
                    onSelect={value => handleDropdownSelect(index, value)}
                  />
                </div>
                <div className='flex-1'>
                  <UIInput.Text value={param.value} onChange={e => handleParameterChange(index, 'value', e.target.value)} placeholder='Value 입력' />
                </div>
                <UIButton2 className='ic-system-48-delete cursor-pointer'></UIButton2>
              </UIGroup>
            ))}
            <UIButton2 className='btn-secondary-outline-blue w-[121px]'>파라미터 추가</UIButton2>
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
                  <UIInput.Text value={param.key} onChange={e => handleInactiveParameterChange(index, e.target.value)} placeholder='Key 이름 입력' />
                </div>
                <UIButton2 className='ic-system-48-delete cursor-pointer'></UIButton2>
              </UIGroup>
            ))}
            <UIButton2 className='btn-secondary-outline-blue w-[121px]'>파라미터 추가</UIButton2>
          </UIGroup>
        </UIFormField>
      </UIArticle>
    </section>
  );
};
