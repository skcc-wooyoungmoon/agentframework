import { UIButton2, UITypography, UIPlaygroundCardBox } from '@/components/UI';
import { UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { DesignLayout } from '@/design/components/DesignLayout';
import { UIGroup } from '@/components/UI/molecules';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UILoading } from '@/components/UI/molecules/UILoading';
import { Fragment } from 'react/jsx-runtime';

export const LayoutGuideLoading = () => {
  // 드롭다운 핸들러

  return (
    <Fragment>
      {/* [참고] 로딩사용시 컨텐츠 영역 외부로 사용해주세요. */}
      <UILoading title='처리중입니다.' label='잠시만 기다려 주세요.' />

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
              <div className='article-body'>
                <div>아티클 컨텐츠 영역</div>
              </div>

              <div className='play-grid-wrap'>
                {/* Card 영역 */}
                <UIPlaygroundCardBox>
                  {/* PlaygroundCardContent */}
                  <div className='box-container'>
                    <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
                      <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
                        {/* 타이틀 & 버튼 */}
                        <div className='flex items-center justify-between'>
                          <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                            시스템 프롬프트
                          </UITypography>
                        </div>
                      </div>
                    </div>
                    {/* Content Box */}
                    <div className='py-5 px-8'>
                      <div>너는 사용자 질의에 따뜻하고 친절하게 답변하는 에이전트야. 사용자 질의에 친절하고 긍정적으로 답변해줘.</div>
                    </div>
                  </div>
                </UIPlaygroundCardBox>
                {/* Card 영역 */}
                <UIPlaygroundCardBox>
                  {/* PlaygroundCardContent */}
                  <div className='box-container'>
                    <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
                      <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
                        <div className='flex items-center justify-between'>
                          <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                            GIP/test-embedding-3-large-new
                          </UITypography>
                          <div className='flex items-center gap-1'>
                            <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                              삭제
                            </UIButton2>
                          </div>
                        </div>
                        <div className='flex items-center gap-2'>
                          <UIGroup gap={8} direction={'row'}>
                            <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                            <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                          </UIGroup>
                          <UIButton2 className='btn-text-14-gray' rightIcon={{ className: 'ic-system-12-arrow-right-gray', children: '' }}>
                            파라미터 설정
                          </UIButton2>
                        </div>
                      </div>
                    </div>
                    {/* Content Box */}
                    <div className='py-5 px-8'>
                      <div className='box-article'>
                        <div className='h-full rounded-xl overflow-y-auto custom-box-scroll text-[#242a34] font-pretendard leading-6 tracking-[-0.031em] pr-3'>
                          대한민국은 동아시아에 위치한 매력적인 나라로, 공식 명칭 "대한민국"이며 흔히 "한국"이라고도 불립니다. 수도는 서울이고, 약 5천만 명의 인구가 다양한 문화와
                          역사를 자랑하며 살고 있습니다. 한국은 드라마, 음악, 음식과 같은 대중문화로 세계적으로 유명하며, 특히 K-팝과 한식은 많은 사랑을 받고 있습니다. 또한 첨단
                          기술과 혁신적인 산업에서도 두각을 나타내고 있어, 세계적인 IT강국으로 손꼽히기도 합니다. 한국은 사계절이 뚜렷하고, 경이로운 자연경관과 고유한 전통문화가
                          어우러져 관광지로 서도 매력적인 나라입니다. 사람들이 친절하고 인심이 후하며, 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다.
                          방문하신다면 많은 것을 즐기고 배울 수 있을 것입니다. 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다. 방문하신다면 많은 것을
                          즐기고 배울 수 있을 것입니다. 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다. 방문하신다면 많은 것을 즐기고 배울 수 있을
                          것입니다.
                        </div>
                      </div>
                    </div>
                  </div>
                </UIPlaygroundCardBox>

                {/* Card 영역 */}
                <UIPlaygroundCardBox>
                  {/* PlaygroundCardContent */}
                  <div className='box-container'>
                    <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
                      <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
                        <div className='flex items-center justify-between'>
                          <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                            GIP/test-embedding-3-large-new
                          </UITypography>
                          <div className='flex items-center gap-1'>
                            <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                              삭제
                            </UIButton2>
                          </div>
                        </div>
                        <div className='flex items-center gap-2'>
                          <UIGroup gap={8} direction={'row'}>
                            <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                            <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                          </UIGroup>
                          <UIButton2 className='btn-text-14-gray' rightIcon={{ className: 'ic-system-12-arrow-right-gray', children: '' }}>
                            파라미터 설정
                          </UIButton2>
                        </div>
                      </div>
                    </div>
                    {/* Content Box */}
                    <div className='py-5 px-8'>
                      <div className='box-article'>
                        <div className='h-full rounded-xl overflow-y-auto custom-box-scroll text-[#242a34]  font-pretendard leading-6 tracking-[-0.031em] pr-3'>
                          <div className='card-none'>
                            <UIIcon2 className='ic-system-48-warning' />
                            <UITypography variant='title-3' className='secondary-neutral-500 text-sb'>
                              응답없음
                            </UITypography>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </UIPlaygroundCardBox>

                {/* Card 영역 */}
                <UIPlaygroundCardBox className='error' message='최대 토큰을 초과하였습니다. 토큰 값을 조절해 주세요.'>
                  {/* PlaygroundCardContent */}
                  <div className='box-container'>
                    <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
                      <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
                        <div className='flex items-center justify-between'>
                          <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                            GIP/test-embedding-3-large-new
                          </UITypography>
                          <div className='flex items-center gap-1'>
                            <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                              삭제
                            </UIButton2>
                          </div>
                        </div>
                        <div className='flex items-center gap-2'>
                          <UIGroup gap={8} direction={'row'}>
                            <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                            <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                          </UIGroup>
                          <UIButton2 className='btn-text-14-gray' rightIcon={{ className: 'ic-system-12-arrow-right-gray', children: '' }}>
                            파라미터 설정
                          </UIButton2>
                        </div>
                      </div>
                    </div>
                    {/* Content Box */}
                    <div className='py-5 px-8'>
                      <div className='box-article'>
                        <div className='h-full rounded-xl overflow-y-auto custom-box-scroll text-[#242a34] font-pretendard leading-6 tracking-[-0.031em] pr-3'>
                          <div className='card-none'>
                            {/* [개발참고] : 응답없을때 className='card-none' */}
                            <UIIcon2 className='ic-system-48-warning' />
                            <UITypography variant='title-3' className='secondary-neutral-500 text-sb'>
                              응답없음
                            </UITypography>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </UIPlaygroundCardBox>

                {/* Card 영역 */}
                <UIPlaygroundCardBox className='error' message='오류가 발생했습니다. 내용을 확인해주세요.'>
                  {/* PlaygroundCardContent */}
                  <div className='box-container'>
                    <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
                      <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
                        <div className='flex items-center justify-between'>
                          <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                            GIP/test-embedding-3-large-new
                          </UITypography>
                          <div className='flex items-center gap-1'>
                            <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                              삭제
                            </UIButton2>
                          </div>
                        </div>
                        <div className='flex items-center gap-2'>
                          <UIGroup gap={8} direction={'row'}>
                            <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                            <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                          </UIGroup>
                          <UIButton2 className='btn-text-14-gray' rightIcon={{ className: 'ic-system-12-arrow-right-gray', children: '' }}>
                            파라미터 설정
                          </UIButton2>
                        </div>
                      </div>
                    </div>
                    {/* Content Box */}
                    <div className='py-5 px-8'>
                      <div className='box-article'>
                        <div className='h-full rounded-xl overflow-y-auto custom-box-scroll text-[#242a34] font-pretendard leading-6 tracking-[-0.031em] pr-3'>
                          "detail": "400: API-KEY sk-0c9308ec7509f8f7f2ac02f0b5609810 is not allowed to access the GIP/gpt-4o with ProjectID 24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"
                        </div>
                      </div>
                    </div>
                  </div>
                </UIPlaygroundCardBox>

                {/* Card 영역 */}
                <UIPlaygroundCardBox>
                  {/* PlaygroundCardContent */}
                  <div className='box-container'>
                    <div className='response-none'>
                      <div className='response-none-title'>모델별 응답을 비교할 수 있습니다.</div>
                      <UIButton2 className='btn-option-outlined'>모델 추가</UIButton2>
                    </div>
                  </div>
                </UIPlaygroundCardBox>

                {/* Card 영역 */}
                <UIPlaygroundCardBox>
                  {/* PlaygroundCardContent */}
                  <div className='box-container'>
                    <div className='response-none'>
                      <div className='response-none-title'>모델별 응답을 비교할 수 있습니다.</div>
                      <UIButton2 className='btn-option-outlined'>모델 추가</UIButton2>
                    </div>
                  </div>
                </UIPlaygroundCardBox>
              </div>
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
    </Fragment>
  );
};
