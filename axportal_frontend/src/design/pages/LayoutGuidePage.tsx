import { UIButton2, UITypography } from '@/components/UI';
import { UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { DesignLayout } from '@/design/components/DesignLayout';
import { UIGroup } from '@/components/UI/molecules';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';

export const LayoutGuidePage = () => {
  // 드롭다운 핸들러

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='페이지 샘플'
          description={'이 페이지는 테이블 컨텐츠의 간격 가이드 입니다.<br>여기부터 줄바꿈입니다.'}
          actions={
            <>
              <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                데이터 만들기
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                아티클 헤더 (서브 타이틀)
              </UITypography>
            </div>
          </UIArticle>

          <UIArticle>
            {/* 타이틀 & 버튼 */}
            <div className='flex items-center justify-between'>
              <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                시스템 프롬프트
              </UITypography>
              <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                시스템 프롬프트
              </UITypography>
            </div>
          </UIArticle>

          <UIArticle>
            <UIGroup gap={8} direction={'row'}>
              <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
              <UITextLabel intent='gray'>Lastest.1</UITextLabel>
            </UIGroup>
            <UIButton2 className='btn-text-14-gray' rightIcon={{ className: 'ic-system-12-arrow-right-gray', children: '' }}>
              파라미터 설정
            </UIButton2>
          </UIArticle>
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <UIButton2 className='btn-primary-gray'>취소</UIButton2>
              <UIButton2 className='btn-primary-blue'>확인</UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
