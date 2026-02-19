import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';

export const POP_ALERT07 = () => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UITypography variant='body-1' className='secondary-neutral-600'>
          벡터 DB 운영 배포는 포탈 관리자만 가능합니다.
        </UITypography>
      </UIArticle>
    </section>
  );
};
