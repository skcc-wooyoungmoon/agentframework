import { UIIcon2 } from '../../../components/UI/atoms/UIIcon2';
import { UIButton2 } from '../../../components/UI/atoms/UIButton2';
import { UITypography } from '../../../components/UI/atoms/UITypography';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIGroup } from '@/components/UI/molecules';

/**
 * LG_010102 - 로그인 페이지
 *
 * 로그인 인증 수단을 선택하는 페이지입니다.
 * - 그라데이션 배경
 * - 신한은행 로고
 * - 몰리메이트 인증
 * - Swing 인증
 */
export default function LG_010102() {
  return (
    <div
      className='min-h-screen w-full flex flex-col items-center justify-center relative'
      style={{
        background: 'linear-gradient(180deg, rgba(205, 230, 241, 1) 0%, rgba(228, 237, 253, 1) 34.35%, rgba(255, 255, 255, 1) 94.71%)',
      }}
    >
      {/* 배경 장식 이미지 */}
      <div className='absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none' style={{ zIndex: 0 }}>
        <UIImage src='/assets/images/login/gradiant.png' alt='배경 장식' className='w-full h-full object-cover' loading='eager' />
      </div>

      {/* 메인 컨텐츠 */}
      <div className='absolute z-[10] flex flex-col items-center w-full' style={{ maxWidth: '496px' }}>
        {/* 회원가입 완료 텍스트 */}
        <div className='flex flex-col items-center mb-[24px]'>
          <UIIcon2 className='ic-system-72-feedback-check-blue mb-[24px]' />
          <div className='text-center'>
            <UIGroup direction='column' gap={0} vAlign='center'>
              <UITypography variant='headline-1' className='secondary-neutral-900 text-sb'>
                <UITypography variant='headline-1' className='primary-800'>
                  $김신한$
                </UITypography>
                님
              </UITypography>
              <UITypography variant='headline-1' className='secondary-neutral-900 text-sb'>
                회원가입 완료
              </UITypography>
            </UIGroup>
          </div>
        </div>

        {/* 로그인 박스 */}
        <div
          className='bg-white shadow-lg w-full flex flex-col items-center'
          style={{
            width: '560px',
            paddingTop: '42px',
            paddingBottom: '42px',
            borderRadius: '32px',
          }}
        >
          {/* 로그인 타이틀 */}
          <div className='text-center mb-[32px]'>
            <UITypography variant='title-2' className='text-gray-600 text-center text-sb'>
              {/* [251202_퍼블수정] : 문구 변경 */}
              대시보드에서 프로젝트 전환 박스를 클릭하면
              <br />
              다른 프로젝트에 참여하거나 프로젝트를 생성할 수 있어요.
            </UITypography>
          </div>

          {/* 인증 수단 선택 영역 */}
          <div className='w-full flex justify-center'>
            {/* [251202_퍼블수정] : projectBox > img-project-box 이미지 변경 */}
            <UIImage src='/assets/images/login/img-project-box.svg' alt='' className='w-[385px] h-[57.6px]' loading='eager' />
          </div>
        </div>

        <div className='pt-[40px]'>
          <UIButton2
            className='btn-primary-blue !h-[48px]'
            onClick={() => {
              // 확인 버튼 클릭 핸들러
            }}
          >
            확인
          </UIButton2>
        </div>
      </div>
    </div>
  );
}
