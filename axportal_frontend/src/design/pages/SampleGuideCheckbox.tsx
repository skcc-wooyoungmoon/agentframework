import { useState } from 'react';

import { DesignLayout } from '../components/DesignLayout';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';

import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UICheckbox2 } from '@/components/UI/atoms/UICheckbox2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

export const SampleGuideCheckbox = () => {
  const [selectedValue1, setSelectedValue1] = useState<string>('');
  const [selectedValue2, setSelectedValue2] = useState<string>('');
  const [selectedValue3, setSelectedValue3] = useState<string>('');
  const [selectedValue4, setSelectedValue4] = useState<string>('');
  const [selectedValue5, setSelectedValue5] = useState<string>('');
  const [selectedValue6, setSelectedValue6] = useState<string>('');
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page pub-guide'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='UICheckbox2 컴포넌트 예시'
          description='체크박스 가이드입니다.'
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
              <h2 className='guide-title'>레이블이 없는 기본 체크박스</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UICheckbox2
                      name='basic1'
                      value='option1'
                      checked={selectedValue1 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue1(value);
                      }}
                    />
                    <UICheckbox2
                      name='basic1'
                      value='option2'
                      checked={selectedValue1 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue1(value);
                      }}
                    />
                    <UICheckbox2 name='basic1' value='option3' disabled />
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>레이블이 있는 기본 체크박스</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UICheckbox2
                      name='basic2'
                      label='레이블1'
                      value='option1'
                      checked={selectedValue2 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue2(value);
                      }}
                    />
                    <UICheckbox2
                      name='basic2'
                      label='레이블2'
                      value='option2'
                      checked={selectedValue2 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue2(value);
                      }}
                    />
                    <UICheckbox2 name='basic2' label='레이블3' value='option3' disabled />
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>레이블이 없는 circle 체크박스</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UICheckbox2
                      name='basic3'
                      value='option1'
                      className='chk circle'
                      checked={selectedValue3 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue3(value);
                      }}
                    />
                    <UICheckbox2
                      name='basic3'
                      value='option2'
                      className='chk circle'
                      checked={selectedValue3 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue3(value);
                      }}
                    />
                    <UICheckbox2 name='basic3' value='option3' className='chk circle' disabled />
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>레이블이 있는 circle 체크박스</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UICheckbox2
                      name='basic4'
                      value='option1'
                      label='레이블1'
                      className='chk circle'
                      checked={selectedValue4 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue4(value);
                      }}
                    />
                    <UICheckbox2
                      name='basic4'
                      value='option2'
                      label='레이블2'
                      className='chk circle'
                      checked={selectedValue4 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue4(value);
                      }}
                    />
                    <UICheckbox2 name='basic4' value='option3' label='레이블3' className='chk circle' disabled />
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>레이블이 없는 box 체크박스</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UICheckbox2
                      name='basic5'
                      value='option1'
                      className='chk box'
                      checked={selectedValue5 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue5(value);
                      }}
                    />
                    <UICheckbox2
                      name='basic5'
                      value='option2'
                      className='chk box'
                      checked={selectedValue5 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue5(value);
                      }}
                    />
                    <UICheckbox2 name='basic5' value='option3' className='chk box' disabled />
                  </dd>
                </dl>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>레이블이 있는 box 체크박스</h2>
              <div className='guide-content'>
                <dl className='guide'>
                  <dt className='guide-label'>기본 상태</dt>
                  <dd className='guide-grid'>
                    <UICheckbox2
                      name='basic6'
                      value='option1'
                      label='레이블1'
                      className='chk box'
                      checked={selectedValue6 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue6(value);
                      }}
                    />
                    <UICheckbox2
                      name='basic6'
                      value='option2'
                      label='레이블2'
                      className='chk box'
                      checked={selectedValue6 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue6(value);
                      }}
                    />
                    <UICheckbox2 name='basic6' value='option3' label='레이블3' className='chk box' disabled />
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
