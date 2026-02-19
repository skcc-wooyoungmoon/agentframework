import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useState } from 'react';
import { DesignLayout } from '../components/DesignLayout';

export function UITestInputPage() {
  // text 타입
  const [textValue, setTextValue] = useState('');

  // password 타입
  const [passwordValue, setPasswordValue] = useState('');

  // date 타입
  const [dateValue, setDateValue] = useState('');

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // auth 타입
  const [authValue, setAuthValue] = useState('');

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');

  // tags 타입
  const [tags, setTags] = useState<string[]>([
    '테스트테스트테스',
    '테스트테스트테스',
    '테스트테스트테스',
    '테스트테스트테스',
    '테스트테스트테스',
    '테스트테스트테스',
    '테스트테스트테스',
  ]);

  // maxLength 테스트용
  const [maxLengthTextValue, setMaxLengthTextValue] = useState('');
  const [maxLengthPasswordValue, setMaxLengthPasswordValue] = useState('');
  const [maxLengthDateValue, setMaxLengthDateValue] = useState('');
  const [maxLengthSearchValue, setMaxLengthSearchValue] = useState('');
  const [maxLengthAuthValue, setMaxLengthAuthValue] = useState('');
  const [maxLengthTextareaValue, setMaxLengthTextareaValue] = useState('');

  // error 테스트용
  const [errorTextValue, setErrorTextValue] = useState('');
  const [errorPasswordValue, setErrorPasswordValue] = useState('');
  const [errorDateValue, setErrorDateValue] = useState('');
  const [errorSearchValue, setErrorSearchValue] = useState('');
  const [errorAuthValue, setErrorAuthValue] = useState('');
  const [errorTextareaValue, setErrorTextareaValue] = useState('');

  // disabled 테스트용
  const [disabledTextValue, setDisabledTextValue] = useState('입력 불가 상태');
  const [disabledPasswordValue, setDisabledPasswordValue] = useState('입력 불가 상태');
  const [disabledDateValue, setDisabledDateValue] = useState('입력 불가 상태');
  const [disabledSearchValue, setDisabledSearchValue] = useState('입력 불가 상태');
  const [disabledAuthValue, setDisabledAuthValue] = useState('입력 불가 상태');

  // readOnly 테스트용
  const [readOnlyTextValue, setReadOnlyTextValue] = useState('읽기 상태');
  const [readOnlyPasswordValue, setReadOnlyPasswordValue] = useState('읽기 상태');
  const [readOnlyDateValue, setReadOnlyDateValue] = useState('읽기 상태');
  const [readOnlySearchValue, setReadOnlySearchValue] = useState('읽기 상태');
  const [readOnlyAuthValue, setReadOnlyAuthValue] = useState('읽기 상태');

  // date Month 테스트
  const [selectedMonth, setSelectedMonth] = useState('2025.08');

  return (
    <DesignLayout>
      <section className='section-page pub-guide'>
        <UIPageHeader
          title='UIInput 컴포넌트 예시'
          description='입력 필드 가이드입니다.'
          actions={
            <>
              <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                데이터 만들기
              </UIButton2>
            </>
          }
        />

        <UIPageBody>
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Input 타입 비교</h2>
              <div className='guide-content'>
                <div className='flex gap-5 w-fit h-fit'>
                  {/* 종류 */}
                  <div className='flex flex-col gap-2'>
                    <div className='form-group'>
                      <div>종류</div>
                    </div>
                    <div className='h-[76px]'>
                      <div className='h-full flex '>text</div>
                    </div>
                    <div className='h-[76px] '>
                      <div className='h-full flex  '>password</div>
                    </div>
                    <div className='h-[76px]'>
                      <div className='h-full flex '>date</div>
                    </div>
                    <div className='h-[76px]'>
                      <div className='h-full flex  '>search</div>
                    </div>
                    <div className='h-[76px]'>
                      <div className='h-full flex  '>인증</div>
                    </div>
                    <div className='h-[150px]'>
                      <div className='h-full flex  '>textarea</div>
                    </div>
                  </div>

                  {/* default */}
                  <div className='flex flex-col gap-2'>
                    <div>default</div>
                    <div className='h-[76px]'>
                      <UIInput.Text
                        value={textValue}
                        onChange={e => {
                          setTextValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Password value={passwordValue} onChange={e => setPasswordValue(e.target.value)} />
                    </div>

                    <div className='h-[76px]'>
                      <UIInput.Date
                        value={dateValue}
                        onChange={e => {
                          setDateValue(e.target.value);
                        }}
                      />
                    </div>

                    <div className='h-[76px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Auth
                        value={authValue}
                        onChange={e => {
                          setAuthValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[150px]'>
                      <UITextArea2 value={textareaValue} onChange={e => setTextareaValue(e.target.value)} />
                    </div>
                  </div>

                  {/* maxLength */}
                  <div className='flex flex-col gap-2'>
                    <div>maxLength</div>
                    <div className='h-[76px]'>
                      <UIInput.Text
                        value={maxLengthTextValue}
                        maxLength={10}
                        onChange={e => {
                          setMaxLengthTextValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Password
                        value={maxLengthPasswordValue}
                        maxLength={10}
                        onChange={e => {
                          setMaxLengthPasswordValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Date
                        value={maxLengthDateValue}
                        maxLength={10}
                        onChange={e => {
                          setMaxLengthDateValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Search
                        value={maxLengthSearchValue}
                        maxLength={5}
                        onChange={e => {
                          setMaxLengthSearchValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Auth
                        status='processing'
                        timer='02:59'
                        value={maxLengthAuthValue}
                        onChange={e => {
                          setMaxLengthAuthValue(e.target.value);
                        }}
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UITextArea2 value={maxLengthTextareaValue} onChange={e => setMaxLengthTextareaValue(e.target.value)} maxLength={10} />
                    </div>
                  </div>

                  {/* 에러 */}
                  <div className='flex flex-col gap-2'>
                    <div>error</div>
                    <div className='h-[76px]'>
                      <UIInput.Text
                        value={errorTextValue}
                        onChange={e => {
                          setErrorTextValue(e.target.value);
                        }}
                        error='valid error (오류메시지를 입력해주세요)'
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Password
                        value={errorPasswordValue}
                        onChange={e => {
                          setErrorPasswordValue(e.target.value);
                        }}
                        error='10자까지만 입력해주세요'
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Date
                        value={errorDateValue}
                        onChange={e => {
                          setErrorDateValue(e.target.value);
                        }}
                        error='오류메시지'
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Search
                        value={errorSearchValue}
                        onChange={e => {
                          setErrorSearchValue(e.target.value);
                        }}
                        error='오류오류오류오류오류오류오류오류오류오류오류오류오류오류오류오류오류오류'
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Auth
                        value={errorAuthValue}
                        status='processing'
                        timer='02:59'
                        onChange={e => {
                          setErrorAuthValue(e.target.value);
                        }}
                        error='인증번호가 틀렸습니다. 재요청해주세요.'
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UITextArea2 value={errorTextareaValue} onChange={e => setErrorTextareaValue(e.target.value)} maxLength={10} error='오류메시지' />
                    </div>
                  </div>

                  {/* disabled */}
                  <div className='flex flex-col gap-2'>
                    <div>disabled</div>
                    <div className='h-[76px]'>
                      <UIInput.Text
                        value={disabledTextValue}
                        maxLength={10}
                        onChange={e => {
                          setDisabledTextValue(e.target.value);
                        }}
                        disabled
                      />
                    </div>

                    <div className='h-[76px]'>
                      <UIInput.Password
                        value={disabledPasswordValue}
                        maxLength={10}
                        onChange={e => {
                          setDisabledPasswordValue(e.target.value);
                        }}
                        disabled
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Date
                        value={disabledDateValue}
                        maxLength={10}
                        onChange={e => {
                          setDisabledDateValue(e.target.value);
                        }}
                        disabled
                      />
                    </div>

                    <div className='h-[76px]'>
                      <UIInput.Search
                        value={disabledSearchValue}
                        maxLength={10}
                        onChange={e => {
                          setDisabledSearchValue(e.target.value);
                        }}
                        disabled
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Auth
                        status='done'
                        value={disabledAuthValue}
                        onChange={e => {
                          setDisabledAuthValue(e.target.value);
                        }}
                        disabled
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UITextArea2 value={'입력 불가'} disabled maxLength={10} />
                    </div>
                  </div>

                  {/* readOnly */}
                  <div className='flex flex-col gap-2'>
                    <div>readOnly</div>
                    <div className='h-[76px]'>
                      <UIInput.Text
                        value={readOnlyTextValue}
                        maxLength={10}
                        onChange={e => {
                          setReadOnlyTextValue(e.target.value);
                        }}
                        readOnly
                      />
                    </div>

                    <div className='h-[76px]'>
                      <UIInput.Password
                        value={readOnlyPasswordValue}
                        maxLength={10}
                        onChange={e => {
                          setReadOnlyPasswordValue(e.target.value);
                        }}
                        readOnly
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Date
                        value={readOnlyDateValue}
                        maxLength={10}
                        onChange={e => {
                          setReadOnlyDateValue(e.target.value);
                        }}
                        readOnly
                      />
                    </div>

                    <div className='h-[76px]'>
                      <UIInput.Search
                        value={readOnlySearchValue}
                        maxLength={10}
                        onChange={e => {
                          setReadOnlySearchValue(e.target.value);
                        }}
                        readOnly
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UIInput.Auth
                        status='done'
                        value={readOnlyAuthValue}
                        onChange={e => {
                          setReadOnlyAuthValue(e.target.value);
                        }}
                        readOnly
                      />
                    </div>
                    <div className='h-[76px]'>
                      <UITextArea2 value={'읽기 상태'} readOnly maxLength={10} />
                    </div>
                  </div>
                  <div className='flex flex-col gap-2'>
                    <div>etc</div>
                    <div className='h-[76px]'>
                      <UITextArea2 value={textareaValue} onChange={e => setTextareaValue(e.target.value)} hint='힌트 메시지 출력' />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Tags Input</h2>
              <div className='guide-content'>
                <div className='flex flex-col gap-4'>
                  <UIInput.Tags
                    tags={tags}
                    onChange={value => {
                      setTags(value);
                    }}
                  />
                  <UIInput.Tags
                    tags={[
                      '테스트테스트테스',
                      '테스트테스트테스',
                      '테스트테스트테스',
                      '테스트테스트테스',
                      '테스트테스트테스',
                      '테스트테스트테스',
                      '테스트테스트테스',
                      '테스트테스트테스',
                    ]}
                    readOnly
                  />

                  <UIInput.Tags tags={['태그1', '태그2테스트']} disabled />
                </div>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>Month & Date Input</h2>
              <div className='guide-content'>
                <div className='mt-5'>
                  <UIUnitGroup gap={8} direction='row'>
                    <div className='flex-1'>
                      <UIInput.Date type='MONTH' value={selectedMonth} onChange={e => setSelectedMonth(e.target.value)} placeholder='월별 조회' />
                    </div>
                    <div className='flex-1 date-weekly-first'>
                      <UIInput.Date value={dateValue} onChange={e => setDateValue(e.target.value)} placeholder='주별 조회' />
                    </div>
                    <div className='flex-1 date-weekly-last'>
                      <UIInput.Date value={dateValue} onChange={e => setDateValue(e.target.value)} placeholder='주별 조회' />
                    </div>
                  </UIUnitGroup>
                </div>
              </div>
            </div>
          </UIArticle>

          <div className='py-[150px]'></div>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
}
