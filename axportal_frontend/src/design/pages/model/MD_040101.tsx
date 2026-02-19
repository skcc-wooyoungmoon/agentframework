import { DesignLayout } from '../../components/DesignLayout';
import { UIPageHeader, UIPageBody, UIGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIButton2, UIPlaygroundCardBox } from '@/components/UI';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UITypography } from '../../../components/UI/atoms/UITypography';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UITextArea2 } from '@/components/UI/molecules/input';
import { useEffect, useState } from 'react';

export const MD_040101 = () => {
  // 박스영역 스크롤 fade 효과 처리
  const handleScroll = (scrollElement: HTMLElement, boxArticleElement: HTMLElement) => {
    const hasScroll = scrollElement.scrollHeight > scrollElement.clientHeight;
    const isAtTop = scrollElement.scrollTop <= 1;
    const isAtBottom = scrollElement.scrollTop + scrollElement.clientHeight >= scrollElement.scrollHeight - 1;

    // box-article 요소에 클래스 적용 (스크롤 끝지점에서 변경)
    if (boxArticleElement && hasScroll) {
      if (isAtBottom) {
        boxArticleElement.classList.remove('fade-bottom');
        boxArticleElement.classList.add('fade-top');
      } else if (isAtTop) {
        boxArticleElement.classList.remove('fade-top');
        boxArticleElement.classList.add('fade-bottom');
      }
    }
  };

  useEffect(() => {
    const boxArticles = document.querySelectorAll('.box-article');

    boxArticles.forEach(boxArticle => {
      const scrollElement = boxArticle.querySelector('.custom-box-scroll') as HTMLElement;

      if (scrollElement) {
        const handleScrollEvent = () => handleScroll(scrollElement, boxArticle as HTMLElement);

        scrollElement.addEventListener('scroll', handleScrollEvent);

        // 초기 상태 설정
        handleScroll(scrollElement, boxArticle as HTMLElement);

        // 클린업을 위해 요소에 핸들러 저장
        (scrollElement as any)._handleScrollEvent = handleScrollEvent;
      }
    });

    return () => {
      boxArticles.forEach(boxArticle => {
        const scrollElement = boxArticle.querySelector('.custom-box-scroll');
        if (scrollElement && (scrollElement as any)._handleScrollEvent) {
          scrollElement.removeEventListener('scroll', (scrollElement as any)._handleScrollEvent);
          delete (scrollElement as any)._handleScrollEvent;
        }
      });
    };
  }, []);

  // textarea 타입
  const [textareaValue1, setTextareaValue1] = useState('너는 사용자 질의에 따뜻하고 친절하게 답변하는 에이전트야. 사용자 질의에 친절하고 긍정적으로 답변해줘.');
  const [textareaValue2, setTextareaValue2] = useState('너는 사용자 질의에 따뜻하고 친절하게 답변하는 에이전트야. 사용자 질의에 친절하고 긍정적으로 답변해줘.');

  return (
    <DesignLayout
      initialMenu={{ id: 'model', label: '모델' }}
      initialSubMenu={{
        id: 'fine-tuning',
        label: '파인튜닝',
        icon: 'ico-lnb-menu-20-fine-tuning',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='플레이 그라운드'
          description={[
            '시스템 프롬프트와 유저 프롬프트를 자유롭게 구성하고 개발망에 배포된 모델로 응답을 바로 확인하실 수 있습니다.',
            '등록한 프롬프트와 다양한 모델을 조합해 응답 품질을 비교해 보세요.',
          ]}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                모델 추가
              </UIButton2>
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-prompt', children: '' }}>
                추론 프롬프트
              </UIButton2>
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-page', children: '' }}>
                실행하기
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='play-wrap'>
            {/* 시스템 프롬프트 : 왼쪽 1개 고정영역 */}
            <div className='play-grid-fix'>
              {/* Card 영역 */}
              <UIPlaygroundCardBox>
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
                    <UITextArea2 value={textareaValue1} onChange={e => setTextareaValue1(e.target.value)} className='area-none-line' resizable={false} enableScrollFade={true} />
                    {/* [251215_퍼블수정]: area-none-line 클래스 변경 / enableScrollFade={true} 꼭 넣어주세요 */}
                  </div>
                </div>
              </UIPlaygroundCardBox>
              {/* Card 영역 */}
              <UIPlaygroundCardBox>
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
                    <UITextArea2 value={textareaValue2} onChange={e => setTextareaValue2(e.target.value)} className='area-none-line' resizable={false} enableScrollFade={true} />
                    {/* [251215_퍼블수정]: area-none-line 클래스 변경 / enableScrollFade={true} 꼭 넣어주세요 */}
                  </div>
                </div>
              </UIPlaygroundCardBox>
            </div>

            {/* GIP : 오른쪽 2개 column */}
            <div className='play-grid-wrap'>
              <UIPlaygroundCardBox>
                {/* PlaygroundCardContent */}
                <div className='box-container'>
                  <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
                    <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
                      <div className='flex items-center justify-between'>
                        <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                          GIP/test-embedding-3-large-newlarge-newlarge-newlarge-new
                        </UITypography>
                        {/* [251222_퍼블수정]: 버튼 영역 추가 (삭제, 실행)  div 태그 className='flex items-center gap-3' 클래스명 수정 */}
                        <div className='flex items-center gap-3'>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                            삭제
                          </UIButton2>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-page', children: '' }}>
                            실행
                          </UIButton2>
                        </div>
                      </div>
                      <div className='flex items-center gap-2'>
                        <UIGroup gap={8} direction={'row'}>
                          {/* <UITextLabel intent='blue'>Release Ver.1</UITextLabel> */}
                          <UITextLabel intent='gray'>전체공유</UITextLabel>
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
                      <div className='h-full rounded-xl overflow-y-auto custom-box-scroll  leading-6 tracking-[-0.031em] pr-3'>
                        <UITypography variant='body-1' className='secondary-neutral-800'>
                          대한민국은 동아시아에 위치한 매력적인 나라로, 공식 명칭 "대한민국"이며 흔히 "한국"이라고도 불립니다. 수도는 서울이고, 약 5천만 명의 인구가 다양한 문화와
                          역사를 자랑하며 살고 있습니다. 한국은 드라마, 음악, 음식과 같은 대중문화로 세계적으로 유명하며, 특히 K-팝과 한식은 많은 사랑을 받고 있습니다. 또한 첨단
                          기술과 혁신적인 산업에서도 두각을 나타내고 있어, 세계적인 IT강국으로 손꼽히기도 합니다. 한국은 사계절이 뚜렷하고, 경이로운 자연경관과 고유한 전통문화가
                          어우러져 관광지로 서도 매력적인 나라입니다. 사람들이 친절하고 인심이 후하며, 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다.
                          방문하신다면 많은 것을 즐기고 배울 수 있을 것입니다. 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다. 방문하신다면 많은 것을
                          즐기고 배울 수 있을 것입니다. 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다. 방문하신다면 많은 것을 즐기고 배울 수 있을
                          것입니다.
                        </UITypography>
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
                          GIP/test-embedding-3-large-newlarge-newlarge-newlarge-newGIP/test-embedding-3-large-newlarge-newlarge-newlarge-new
                        </UITypography>
                        <div className='flex items-center gap-3'>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                            삭제
                          </UIButton2>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-page', children: '' }}>
                            실행
                          </UIButton2>
                        </div>
                      </div>
                      <div className='flex items-center gap-2'>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='gray'>전체공유</UITextLabel>
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
                      <div className='h-full rounded-xl overflow-y-auto custom-box-scroll pr-3'>
                        <UITypography variant='body-1' className='secondary-neutral-800'>
                          대한민국은 동아시아에 위치한 매력적인 나라로, 공식 명칭 "대한민국"이며 흔히 "한국"이라고도 불립니다. 수도는 서울이고, 약 5천만 명의 인구가 다양한 문화와
                          역사를 자랑하며 살고 있습니다. 한국은 드라마, 음악, 음식과 같은 대중문화로 세계적으로 유명하며, 특히 K-팝과 한식은 많은 사랑을 받고 있습니다. 또한 첨단
                          기술과 혁신적인 산업에서도 두각을 나타내고 있어, 세계적인 IT강국으로 손꼽히기도 합니다. 한국은 사계절이 뚜렷하고, 경이로운 자연경관과 고유한 전통문화가
                          어우러져 관광지로 서도 매력적인 나라입니다. 사람들이 친절하고 인심이 후하며, 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다.
                          방문하신다면 많은 것을 즐기고 배울 수 있을 것입니다. 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다. 방문하신다면 많은 것을
                          즐기고 배울 수 있을 것입니다. 다양한 역사 유적지와 현대적인 도시를 동시에 경험할 수 있는 곳이기도 합니다. 방문하신다면 많은 것을 즐기고 배울 수 있을
                          것입니다.
                        </UITypography>
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
                        <div className='flex items-center gap-3'>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                            삭제
                          </UIButton2>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-page', children: '' }}>
                            실행
                          </UIButton2>
                        </div>
                      </div>
                      <div className='flex items-center gap-2'>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='gray'>전체공유</UITextLabel>
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
                      <div className='h-full rounded-xl overflow-y-auto custom-box-scroll text-[#242a34] leading-6 tracking-[-0.031em] pr-3'>
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
                        <div className='flex items-center gap-3'>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }}>
                            삭제
                          </UIButton2>
                          <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-page', children: '' }}>
                            실행
                          </UIButton2>
                        </div>
                      </div>
                      <div className='flex items-center gap-2'>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='gray'>전체공유</UITextLabel>
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
                      <div className='h-full rounded-xl overflow-y-auto custom-box-scroll pr-3'>
                        <UITypography variant='body-1' className='secondary-neutral-800'>
                          "detail": "400: API-KEY sk-0c9308ec7509f8f7f2ac02f0b5609810 is not allowed to access the GIP/gpt-4o with ProjectID 24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"
                        </UITypography>
                      </div>
                    </div>
                  </div>
                </div>
              </UIPlaygroundCardBox>

              {/* Card 영역 */}
              {/* 
              [251215_퍼블수정] : 모델 추가 - 영역 삭제 (상단 > 오른쪽) 버튼으로 이동함
              <UIPlaygroundCardBox>
                <div className='box-container'>
                  <div className='response-none'>
                    <UITypography variant='body-2' className='secondary-neutral-800'>
                      모델별 응답을 비교할 수 있습니다.
                    </UITypography>
                    <UIButton2 className='btn-option-outlined'>모델 추가</UIButton2>
                  </div>
                </div>
              </UIPlaygroundCardBox> 
              */}
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
