import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIGroup } from '@/components/UI/molecules';

export const POP_ALERT05 = () => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
          운영 배포를 정말 진행하시겠습니까?
        </UITypography>
        <div className='box-fill mt-6'>
          <UIGroup gap={8} direction='row' align='start' vAlign='start'>
            <UIIcon2 className='ic-system-16-info-gray w-[16px] top-[2px] relative' />
            <UITypography variant='body-2' className='secondary-neutral-600 flex-1'>
              배포할 모델이 세이프티 필터를 참조하는 경우, 포탈 운영 환경에서 해당 세이프티 필터가 정상 동작하는 지 꼭 확인해주세요!
            </UITypography>
          </UIGroup>
        </div>
      </UIArticle>
    </section>
  );
};
