import { useState } from 'react';

import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { DesignLayout } from '../components/DesignLayout';

export const SampleGuideRadio = () => {
  const [selectedValue1, setSelectedValue1] = useState<string>('');
  const [selectedValue2, setSelectedValue2] = useState<string>('');
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page pub-guide'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='UIRadio2 컴포넌트 예시'
          description='라디오 버튼 가이드입니다.'
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
            <div className='guide-section'>
              <h2 className='guide-title'>레이블이 없는 기본 라디오 버튼</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UIRadio2
                      name='basic1'
                      value='option1'
                      checked={selectedValue1 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue1(value);
                      }}
                    />
                    <UIRadio2
                      name='basic1'
                      value='option2'
                      checked={selectedValue1 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue1(value);
                      }}
                    />
                    <UIRadio2 name='basic1' value='option3' disabled />
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>레이블이 있는 기본 라디오 버튼</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UIRadio2
                      name='basic2'
                      label='레이블1'
                      value='option1'
                      checked={selectedValue2 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue2(value);
                      }}
                    />
                    <UIRadio2
                      name='basic2'
                      label='레이블2'
                      value='option2'
                      checked={selectedValue2 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue2(value);
                      }}
                    />
                    <UIRadio2 name='basic2' label='레이블3' value='option3' disabled />
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
