import { useState } from 'react';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIInput } from '@/components/UI/molecules/input';

export default function LG_010101_P06() {
  const [value, setValue] = useState('');
  const [authStatus] = useState<'init' | 'processing' | 'done'>('processing');
  const [authTimer] = useState<string>('02:59');

  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='article-body'>
          <UIInput.Auth
            value={value}
            status={authStatus}
            timer={authTimer}
            placeholder='인증번호 6자리 입력'
            onChange={(e) => setValue(e.target.value)}
            authButtonDisabled={authStatus === 'processing' && authTimer !== '00:00'}
            error=''
          />
        </div>
      </UIArticle>
    </section>
  );
}
