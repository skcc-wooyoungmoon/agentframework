import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';

/**
 * LG_010101_P03 - 로그아웃 안내 모달 페이지
 */
export const LayoutGuideModal = () => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            모달 내 아티클 타이틀
          </UITypography>
        </div>
        <div className='article-body'>
          <div>아티클 컨텐츠 영역</div>
        </div>
      </UIArticle>
    </section>
  );
};
