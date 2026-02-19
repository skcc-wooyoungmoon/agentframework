import { UIIcon2 } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';

export default function LG_010101_P02() {
  return (
    <section className='section-modal'>
      <UIArticle className='flex flex-col justify-center items-center'>
        {/* 피드백 아이콘 - 72px */}
        <UIIcon2 className='ic-system-72-feedback' />
        <div className='article-header' style={{ marginTop: '24px', marginBottom: '4px' }}>
          <UITypography variant='title-2' className='secondary-neutral-900 text-sb'>
            <UITypography variant='title-2' className='primary-800 text-sb'>
              9분 58초
            </UITypography>
            후 자동 로그아웃 예정입니다.
          </UITypography>
        </div>
        <div className='article-body' style={{ textAlign: 'center' }}>
          <UITypography variant='body-1' className='secondary-neutral-600'>
            [확인] 버튼을 클릭하면 로그인 시간을 <br /> 초기화할 수 있어요.
          </UITypography>
        </div>
      </UIArticle>
    </section>
  );
}
