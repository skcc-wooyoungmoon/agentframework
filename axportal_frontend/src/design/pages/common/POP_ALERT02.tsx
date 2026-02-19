import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIGroup } from '@/components/UI/molecules';

export const POP_ALERT02 = () => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UIGroup gap={16} direction='column' vAlign='center'>
          <UIIcon2 className='ic-system-56-check' />
          <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
            정합성 검증결과, 생성된 파일과 포탈 형상이 일치합니다. 운영 배포를 위한 최종파일을 생성할게요.
          </UITypography>
        </UIGroup>
      </UIArticle>
    </section>
  );
};
