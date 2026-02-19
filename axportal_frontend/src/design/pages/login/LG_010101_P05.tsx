import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { useState } from 'react';

/**
 * LG_010101_P05 - swinge 인증
 */
export default function LG_010101_P05() {
  const [textValue, setLoginValue] = useState('');
  const [passwordValue, setPasswordValue] = useState('');

  return (
    <section className='section-modal'>
      <UIArticle>
        <UIUnitGroup gap={16} direction='column'>
          <div>
            <UIInput.Text
              value={textValue}
              placeholder='아이디 입력'
              onChange={e => {
                setLoginValue(e.target.value);
              }}
            />
          </div>
          <div>
          <UIInput.Password 
            value={passwordValue}
            placeholder='비밀번호 입력'
            onChange={e => setPasswordValue(e.target.value)}
            error='아이디 또는 비밀번호를 다시 확인해 주세요.'
          />
          </div>
        </UIUnitGroup>
      </UIArticle>
    </section>
  );
}
