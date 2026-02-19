import { useState } from 'react';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIInput } from '@/components/UI/molecules/input';
import { UICheckbox2 } from '@/components/UI/atoms/UICheckbox2';

export default function LG_010101_P01() {
  const [selectedValue, setSelectedValue] = useState<string>('');

  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='article-body'>
          <UIInput.Auth value={'SGO1032959'} status='processing' timer='02:59' onChange={() => {}} error='행번으로 검색된 핸드폰 정보가 없습니다.' />
          <div style={{ marginTop: '16px' }}>
            <UICheckbox2
              name='basic6'
              value='option1'
              label='직원번호 저장'
              className='chk box items-start'
              checked={selectedValue === 'option1'}
              onChange={(checked, value) => {
                if (checked) setSelectedValue(value);
              }}
            />
          </div>
        </div>
      </UIArticle>
    </section>
  );
}
