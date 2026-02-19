import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules';

export const LogoutAutoNoti = () => {
  // console.log('LogoutAutoNoti 컴포넌트 렌더링');

  return (
    <section className='section-modal'>
      <UIArticle className='flex flex-col justify-center items-center'>
        {/* 피드백 아이콘 - 72px */}
        <UIIcon2 className='ic-system-72-feedback' />
        <div className='article-header' style={{ marginTop: '24px', marginBottom: '4px' }}>
          <UITypography variant='title-2' className='secondary-neutral-900 text-sb'>
            다시 로그인해 주세요.
          </UITypography>
        </div>
        <div className='article-body' style={{ textAlign: 'center' }}>
          <UITypography variant='body-1' className='secondary-neutral-600'>
            장시간 미사용으로 로그아웃 되었습니다.
          </UITypography>
        </div>
      </UIArticle>
    </section>
  );
};
