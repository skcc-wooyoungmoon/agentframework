import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { DesignLayout } from '../components/DesignLayout';

export const SampleGuideTextField = () => {
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='컴포넌트 예시'
          description='텍스트필드 가이드입니다.'
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
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-1/6'>type</div>
                <div className='w-1/6'>Default</div>
                <div className='w-1/6'>Filled</div>
                <div className='w-1/6'>Error</div>
                <div className='w-1/6'>Disabled</div>
                <div className='w-1/6'>readOnly</div>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-1/6'>
                  <UITypography variant='title-4'>기본</UITypography>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield1-1' title='텍스트 필드 타이틀' placeholder='Placeholder Text' />
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield1-2' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='텍스트 입력완료' />
                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group error'>
                    <div className='textfield'>
                      <input type='text' id='textfield1-3' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='Value Text' />
                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                    </div>

                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='error-text'>오류 메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group disabled'>
                    <div className='textfield'>
                      <input type='text' id='textfield1-4' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='Value Text' disabled />
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group readOnly'>
                    <div className='textfield'>
                      <input type='text' id='textfield1-5' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='Value Text' readOnly />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-1/6'>
                  <UITypography variant='title-4'>마스킹</UITypography>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='password' id='textfield2-1' title='텍스트 필드 타이틀' placeholder='Placeholder Text' />
                      <button type='button' className='textfield-visibility'>
                        <UIIcon2 className='ic-system-24-outline-view' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='password' id='textfield2-2' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>

                      <button type='button' className='textfield-visibility'>
                        <UIIcon2 className='ic-system-24-outline-view' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group error'>
                    <div className='textfield'>
                      <input type='password' id='textfield2-3' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' />
                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                      <button type='button' className='textfield-visibility'>
                        <UIIcon2 className='ic-system-24-outline-view' />
                      </button>
                    </div>
                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='error-text'>오류 메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group disabled'>
                    <div className='textfield'>
                      <input type='password' id='textfield2-4' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' disabled />
                      <button type='button' className='textfield-visibility' disabled>
                        <UIIcon2 className='ic-system-24-outline-gray-view' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group readOnly'>
                    <div className='textfield'>
                      <input type='password' id='textfield2-5' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' readOnly />
                      <button type='button' className='textfield-visibility' disabled>
                        <UIIcon2 className='ic-system-24-outline-gray-view' />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-1/6'>
                  <UITypography variant='title-4'>마스킹 해제</UITypography>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield3-1' title='텍스트 필드 타이틀' placeholder='Placeholder Text' />
                      <button type='button' className='textfield-visibility'>
                        <UIIcon2 className='ic-system-24-outline-view-off' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield3-2' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>

                      <button type='button' className='textfield-visibility'>
                        <UIIcon2 className='ic-system-24-outline-view-off' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group error'>
                    <div className='textfield'>
                      <input type='text' id='textfield3-3' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>

                      <button type='button' className='textfield-visibility'>
                        <UIIcon2 className='ic-system-24-outline-view-off' />
                      </button>
                    </div>

                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='error-text'>오류 메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group disabled'>
                    <div className='textfield'>
                      <input type='text' id='textfield3-4' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' disabled />

                      <button type='button' className='textfield-visibility' disabled>
                        <UIIcon2 className='ic-system-24-outline-gray-view-off' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group readonly'>
                    <div className='textfield'>
                      <input type='text' id='textfield3-5' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='123456' readOnly />

                      <button type='button' className='textfield-visibility' disabled>
                        <UIIcon2 className='ic-system-24-outline-gray-view-off' />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-1/6'>
                  <UITypography variant='title-4'>검색</UITypography>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <UIIcon2 className='ic-system-24-outline-search' />
                      <input type='text' id='textfield5-1' title='텍스트 필드 타이틀' placeholder='검색어 입력' />
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <UIIcon2 className='ic-system-24-outline-search' />

                      <input type='text' id='textfield5-2' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group error'>
                    <div className='textfield'>
                      <UIIcon2 className='ic-system-24-outline-search' />

                      <input type='text' id='textfield5-3' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                    </div>

                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='error-text'>오류 메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group disabled'>
                    <div className='textfield'>
                      <UIIcon2 className='ic-system-24-outline-gray-search' />

                      <input type='text' id='textfield5-4' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' disabled />
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group readonly'>
                    <div className='textfield'>
                      <UIIcon2 className='ic-system-24-outline-gray-search' />

                      <input type='text' id='textfield5-5' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' readOnly />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-1/6'>
                  <UITypography variant='title-4'>날짜</UITypography>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield6-1' title='텍스트 필드 타이틀' placeholder='날짜 입력' />

                      <button type='button' className='textfield-date'>
                        <UIIcon2 className='ic-system-24-calender' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield6-2' title='텍스트 필드 타이틀' placeholder='날짜 입력' defaultValue='2025.09.05' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>

                      <button type='button' className='textfield-date'>
                        <UIIcon2 className='ic-system-24-calender' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group error'>
                    <div className='textfield'>
                      <input type='text' id='textfield6-3' title='텍스트 필드 타이틀' placeholder='날짜 입력' defaultValue='2025.09.05' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>

                      <button type='button' className='textfield-date'>
                        <UIIcon2 className='ic-system-24-calender' />
                      </button>
                    </div>

                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='error-text'>오류 메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group disabled'>
                    <div className='textfield'>
                      <input type='text' id='textfield6-4' title='텍스트 필드 타이틀' placeholder='날짜 입력' defaultValue='2025.09.05' disabled />

                      <button type='button' className='textfield-date' disabled>
                        <UIIcon2 className='ic-system-24-outline-gray-calender' />
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group readonly'>
                    <div className='textfield'>
                      <input type='text' id='textfield6-5' title='텍스트 필드 타이틀' placeholder='날짜 입력' defaultValue='2025.09.05' readOnly />

                      <button type='button' className='textfield-date' disabled>
                        <UIIcon2 className='ic-system-24-outline-gray-calender' />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-[50px]'>
                  <UITypography variant='title-4'>인증</UITypography>
                </div>
                <div className='w-1/5'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield7-1' title='텍스트 필드 타이틀' placeholder='Placeholder Text' />

                      <button type='button' className='btn-option-outlined'>
                        인증재요청
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/5'>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield7-2' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='Value Text' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>

                      <span className='textfield-timer'>02:59</span>

                      <button type='button' className='btn-option-outlined'>
                        인증재요청
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/5'>
                  <div className='form-group error'>
                    <div className='textfield'>
                      <input type='text' id='textfield7-3' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='Value Text' />

                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>

                      <span className='textfield-timer'>02:59</span>

                      <button type='button' className='btn-option-outlined'>
                        인증재요청
                      </button>
                    </div>

                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='error-text'>오류 메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/5'>
                  <div className='form-group disabled'>
                    <div className='textfield'>
                      <input type='text' id='textfield7-4' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='Value Text' disabled />

                      <UIIcon2 className='ic-system-24-outline-blue-check' />

                      <button type='button' className='btn-option-outlined' disabled>
                        인증재요청
                      </button>
                    </div>
                  </div>
                </div>
                <div className='w-1/5'>
                  <div className='form-group readonly'>
                    <div className='textfield'>
                      <input type='text' id='textfield7-5' title='텍스트 필드 타이틀' placeholder='Placeholder Text' defaultValue='Value Text' readOnly />

                      <UIIcon2 className='ic-system-24-outline-blue-check' />

                      <button type='button' className='btn-option-outlined' disabled>
                        인증재요청
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-body'>
              <div className='flex gap-4'>
                <div className='w-1/6'>
                  <UITypography variant='title-4'>Textarea</UITypography>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield multi-line'>
                      <textarea placeholder='계약서, 약관 등 금융문서 요약 전용' maxLength={100} rows={4} className=''></textarea>
                    </div>
                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='bullet-text'>메세지 노출</span>
                      </div>
                      <div className='count-text'>
                        <span className='current'>0자</span>/<span className='total'>0자</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield multi-line'>
                      <textarea placeholder='계약서, 약관 등 금융문서 요약 전용' maxLength={100} rows={4} className=''></textarea>
                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                    </div>
                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='bullet-text'>메세지 노출</span>
                      </div>
                      <div className='count-text'>
                        <span className='current'>0자</span>/<span className='total'>0자</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group'>
                    <div className='textfield multi-line'>
                      <textarea placeholder='계약서, 약관 등 금융문서 요약 전용' maxLength={100} rows={4} className=''></textarea>
                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                    </div>
                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='bullet-text'>메세지 노출</span>
                        <span className='error-text'>오류 메세지 노출</span>
                      </div>
                      <div className='count-text'>
                        <span className='current'>0자</span>/<span className='total'>0자</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group disabled'>
                    <div className='textfield multi-line'>
                      <textarea defaultValue='계약서, 약관 등 금융문서 요약 전용' maxLength={100} rows={4} className='' disabled></textarea>
                    </div>
                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='bullet-text'>메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div className='w-1/6'>
                  <div className='form-group readonly'>
                    <div className='textfield multi-line'>
                      <textarea defaultValue='계약서, 약관 등 금융문서 요약 전용' maxLength={100} rows={4} className='' readOnly></textarea>
                    </div>
                    <div className='form-message'>
                      <div className='desc-message'>
                        <span className='bullet-text'>메세지 노출</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
