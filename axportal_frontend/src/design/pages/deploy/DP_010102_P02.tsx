import { useState } from 'react';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIInput, UIUnitGroup, UIList, UIFormField } from '@/components/UI/molecules';
import { UITypography } from '@/components/UI/atoms';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';

/**
 * DP_010102_P02
 */
export const DP_010102_P02: React.FC = () => {
  const [textValue, setLoginValue] = useState('');
  const [selectedValue2, setSelectedValue2] = useState<string>('');

  return (
    <section className='section-modal'>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          {/* [251105_퍼블수정] 속성값 수정 */}
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
            구분
          </UITypography>
          <UIUnitGroup gap={8} direction='column'>
            <UIRadio2
              name='basic2'
              label='사용자'
              value='option1'
              checked={selectedValue2 === 'option1'}
              onChange={(checked, value) => {
                if (checked) setSelectedValue2(value);
              }}
            />
            <UIRadio2
              name='basic2'
              label='기타'
              value='option2'
              checked={selectedValue2 === 'option2'}
              onChange={(checked, value) => {
                if (checked) setSelectedValue2(value);
              }}
            />
          </UIUnitGroup>
        </UIFormField>
      </UIArticle>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            이름
          </UITypography>
          <UIUnitGroup gap={8} direction='column' align='start'>
            <UIInput.Text
              value={textValue}
              placeholder='이름 입력'
              onChange={e => {
                setLoginValue(e.target.value);
              }}
            />
            <UIList
              gap={4}
              direction='column'
              className='ui-list_bullet'
              data={[
                {
                  dataItem: (
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {`발급할 API Key의 이름을 입력해주세요. (예시: 신한은행 광교영업점)`}
                    </UITypography>
                  ),
                },
              ]}
            />
          </UIUnitGroup>
        </UIFormField>
      </UIArticle>
    </section>
  );
};
