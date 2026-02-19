/**
 * CM_040101 - 에러 페이지
 */

import { UITypography, UIIcon2, UIButton2 } from '@/components/UI/atoms';
import { UIGroup } from '@/components/UI/molecules';
import { UIPageBody } from '../../../components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

export function CM_040101() {
  return (
    <section className='section-page min-h-screen flex flex-col justify-center'>
      <UIPageBody>
        {/* 전체 페이지 Wrap */}
        <div className='flex flex-col items-center w-full'>
          {/* 에러페이지 컨텐츠 */}
          <UIArticle className='w-full flex flex-col items-center'>
            <UIIcon2 className='ic-system-180-error' style={{ width: '180px' }} />
            <UIGroup gap={16} direction={'column'} className='mt-[32px]' vAlign='center'>
              <UITypography variant='title-2' className='secondary-neutral-800 text-center'>
                일시적인 오류가 발생했습니다.
              </UITypography>
              <UITypography variant='body-1' className='secondary-neutral-500 text-center'>
                서버와의 통신 중 문제가 발생했습니다. <br />
                잠시 후 다시 시도해주세요.
              </UITypography>
            </UIGroup>
          </UIArticle>
          <UIArticle>
            <UIGroup gap={16} direction={'row'} align='center'>
              <UIButton2 className='btn-secondary-blue !min-w-[200px]'>대시보드 이동</UIButton2>
              <UIButton2 className='btn-secondary-blue !min-w-[200px]'>대시보드 이동</UIButton2>
              <UIButton2 className='btn-secondary-blue !min-w-[200px]'>새로고침</UIButton2>
            </UIGroup>
          </UIArticle>
        </div>
      </UIPageBody>
    </section>
  );
}
