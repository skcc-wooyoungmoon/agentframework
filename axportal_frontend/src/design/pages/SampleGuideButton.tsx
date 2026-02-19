import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { DesignLayout } from '../components/DesignLayout';

export const SampleGuideButton = () => {
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page pub-guide'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='페이지 샘플'
          description='이 페이지는 테이블 컨텐츠의 간격 가이드 입니다.'
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
          {/* Primary 56px */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Button - Primary 56px</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>blue</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-primary-blue'>Button</UIButton2>
                    <UIButton2 className='btn-primary-blue' disabled>
                      Button
                    </UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>gray</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-primary-gray'>Button</UIButton2>
                    <UIButton2 className='btn-primary-gray' disabled>
                      Button
                    </UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>outline</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-primary-outline'>Button</UIButton2>
                    <UIButton2 className='btn-primary-outline' disabled>
                      Button
                    </UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>plus / minus</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-primary-plus'>{''}</UIButton2>
                    <UIButton2 className='btn-primary-plus' disabled>
                      {''}
                    </UIButton2>
                    <UIButton2 className='btn-primary-minus'>{''}</UIButton2>
                    <UIButton2 className='btn-primary-minus' disabled>
                      {''}
                    </UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          {/* Secondary 48px */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Button - Secondary 48px</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>blue / sky / gray / outline</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-secondary-blue'>Button</UIButton2>
                    <UIButton2 className='btn-secondary-sky-blue'>Button</UIButton2>
                    <UIButton2 className='btn-secondary-gray'>Button</UIButton2>
                    <UIButton2 className='btn-secondary-outline'>Button</UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>plus / minus</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-secondary-plus'>{''}</UIButton2>
                    <UIButton2 className='btn-secondary-minus'>{''}</UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          {/* Tertiary 40px */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Button - Tertiary 40px</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>blue / sky / gray / outline</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-tertiary-blue'>Button</UIButton2>
                    <UIButton2 className='btn-tertiary-sky-blue'>Button</UIButton2>
                    <UIButton2 className='btn-tertiary-gray'>Button</UIButton2>
                    <UIButton2 className='btn-tertiary-outline'>Button</UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>download</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-tertiary-outline download'>Button</UIButton2>
                    <UIButton2 className='btn-tertiary-outline download' disabled>
                      Button
                    </UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          {/* Option 32px */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Button - Option 32px</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>outlined</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-option-outlined'>Text</UIButton2>
                    <UIButton2 className='btn-option-outlined-sky-blue'>Text</UIButton2>
                    <UIButton2 className='btn-option-outlined-sky-blue' rightIcon={{ className: 'ic-system-20-copy-black', children: '' }}>
                      Text
                    </UIButton2>
                    <UIButton2 className='btn-option-outlined' disabled>
                      Text
                    </UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>filled</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-option-filled' leftIcon={{ className: 'ic-system-16-download', children: '' }}>
                      저장
                    </UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          {/* Text Button (14 / 16px) */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Button - Text 14 / 16px</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>normal</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-text-14'>Text</UIButton2>
                    <UIButton2 className='btn-text-16'>Text</UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>point</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-text-14-point'>Text</UIButton2>
                    <UIButton2 className='btn-text-16-point'>Text</UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>gray</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-text-14-gray'>Text</UIButton2>
                    <UIButton2 className='btn-text-16-gray'>Text</UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>with icon</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                      Text
                    </UIButton2>
                    <UIButton2 className='btn-text-16' rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }}>
                      Text
                    </UIButton2>
                    <UIButton2 className='btn-text-14' rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }}>
                      Text
                    </UIButton2>
                    <UIButton2 className='btn-text-14-gray' rightIcon={{ className: 'ic-system-12-arrow-right-gray', children: '' }}>
                      Text
                    </UIButton2>
                    <UIButton2 className='btn-text-14-point' rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }}>
                      Text
                    </UIButton2>
                  </dd>
                </dl>
                <dl className='guide'>
                  <dt className='guide-label'>underline</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-text-16-underline'>Text</UIButton2>
                    <UIButton2 className='btn-text-16-underline-point'>Text</UIButton2>
                    <UIButton2 className='btn-text-14-underline-point' rightIcon={{ className: 'ic-system-16-export', children: '' }}>
                      Text
                    </UIButton2>
                    <UIButton2 className='btn-text-16-underline-point' rightIcon={{ className: 'ic-system-24-outline-blue-export', children: '' }}>
                      Text
                    </UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          {/* icon Button (24) */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Icon Button</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>icon button 24</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-icon-24'>
                      <UIIcon2 className='ic-system-24-more' />
                    </UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          {/* Filter Chips */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Filter Chips</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>sort / dropdown</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-chip sort' leftIcon={{ className: 'ic-system-16-sort', children: '' }}>
                      {''}
                    </UIButton2>
                    <UIButton2 className='btn-chip dropdown' rightIcon={{ className: 'ic-system-10-dropdown', children: '' }}>
                      필터명
                    </UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          {/* Builder */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Builder</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>default</dt>
                  <dd className='guide-grid'>
                    <UIButton2 className='btn-builder'>Note</UIButton2>
                    <UIButton2 className='btn-builder' disabled>
                      Note
                    </UIButton2>
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <div className='btn-group direction-row align-center'>
              <UIButton2 className='btn-primary-blue'>확인</UIButton2>
              <UIButton2 className='btn-primary-gray'>취소</UIButton2>
            </div>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
