import { useState } from 'react';
import { UICode } from '@/components/UI/atoms/UICode';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField } from '@/components/UI/molecules';
import { UITextArea2 } from '@/components/UI/molecules/input';

export const DT_020303_P06 = () => {
  // textarea 타입
  const [, setTextareaValue] = useState('');

  return (
    <section className='section-modal'>
      {/* 설명 입력 필드 */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
            내용
          </UITypography>
          {/* [251112_퍼블수정 속성값 수정] */}
          <UITextArea2
            value='(금융)거래관계의 설정·유지·이행·관리
수집·이용 목적 · 금융사고 조사, 분쟁해결, 민원처리
법령상 의무이행
법령상 의무이행'
            placeholder='설명 입력'
            onChange={e => setTextareaValue(e.target.value)}
          />
        </UIFormField>
      </UIArticle>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
            메타데이터
          </UITypography>
          {/* 소스코드 영역 */}
          <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='272px' maxHeight='272px' readOnly={true} />
        </UIFormField>
      </UIArticle>
    </section>
  );
};
