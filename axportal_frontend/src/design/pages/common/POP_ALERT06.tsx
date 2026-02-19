import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';

export const POP_ALERT06 = () => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UITypography variant='body-1' className='secondary-neutral-600'>
          화면을 나가시겠어요?
          <br />
          입력한 정보가 저장되지 않을 수 있습니다.
        </UITypography>
      </UIArticle>
    </section>
  );
};
