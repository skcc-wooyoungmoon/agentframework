import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIGroup } from '@/components/UI/molecules';

export const POP_ALERT04 = () => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UIGroup gap={16} direction='column' vAlign='center'>
          <UIIcon2 className='ic-system-56-feedback' />
          <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
            운영 환경 내 일관된 프로젝트 및 역할 구조 유지를 위해, 선택한 프로젝트 정보와 해당 프로젝트 내 역할 정보가 함께 배포됩니다.
          </UITypography>
        </UIGroup>
      </UIArticle>
    </section>
  );
};
