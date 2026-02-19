/**
 * CM_TEST - 테스트
 */

import { UITypography, UIIcon2 } from '@/components/UI/atoms';
import { UIGroup } from '@/components/UI/molecules';
import { UIPageBody } from '../../../components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

export function CM_TEST() {
  return (
    <section className='section-page min-h-screen flex flex-col justify-center'>
      <UIPageBody>
        {/* 전체 페이지 Wrap */}
        <div className='flex flex-col items-center w-full'>
          <UIArticle className='w-full flex flex-col items-center'>
            <UIIcon2 className='ic-system-180-open' style={{ width: '180px' }} />
            <UIGroup gap={16} direction={'column'} className='mt-[32px]' vAlign='center'>
              <UITypography variant='title-2' className='secondary-neutral-800 text-center'>
                현재 서비스 오픈 준비 중입니다.
              </UITypography>
              <UITypography variant='body-1' className='secondary-neutral-500 text-center'>
                2025년 12월 15일부터 정상 이용이 가능하오니 양해 부탁드립니다.
              </UITypography>
            </UIGroup>
          </UIArticle>
        </div>
      </UIPageBody>
    </section>
  );
}
