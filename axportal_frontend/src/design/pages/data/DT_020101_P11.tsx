// DT_020101_P11 페이지
import { UITypography } from '@/components/UI';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIArticle, UIFormField, UIGroup, UIInput, UITextArea2 } from '@/components/UI/molecules';

export const DT_020101_P11 = () => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup gap={4} direction='column'>
            <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
              Query
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              설정한 지식의 정상 여부를 테스트하기 위해 쿼리를 입력 해주세요.
            </UITypography>
          </UIGroup>
          <UIInput.Text value={''} placeholder='Query 입력' />
        </UIFormField>
      </UIArticle>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            Retrieval options
          </UITypography>
          {/* 소스코드 영역 */}
          <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='500px' readOnly={false} />
        </UIFormField>
      </UIArticle>
      {/* 테스트 결과 필드 */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
            테스트 결과
          </UITypography>
          <UITextArea2 value='테스트 성공시 테스트 결과가 여기에 노출됩니다.' placeholder='' readOnly={true} />
        </UIFormField>
      </UIArticle>
    </section>
  );
};
