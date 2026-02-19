import { useNavigate } from 'react-router-dom';

import { UIIcon2, UITypography } from '@/components/UI';
import { UIArticle, UIGroup, UIPageBody } from '@/components/UI/molecules';
import authUtils from '@/utils/common/auth.utils';
import { Fragment } from 'react/jsx-runtime';
import { UIButton2 } from '../../components/UI/atoms/UIButton2/component';

/**
 * @author SGO1032948
 * @description 404 페이지
 * CM_040101
 */
export const NotFoundPage = ({
  type = '404',
  title = '요청하신 페이지를 찾을 수 없습니다.',
  description = '요청하신 주소가 잘못되었거나, 삭제된 페이지입니다.\n입력하신 URL을 다시 확인해주세요.',
}: {
  type?: '404' | '500' | 'MIG';
  title?: string;
  description?: string;
}) => {
  // TODO 에러 타입별 확인 필요
  const navigate = useNavigate();

  const handleGoToDashboard = () => {
    navigate('/', { replace: true });
  };

  return type === 'MIG' ? (
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
  ) : (
    <section className='section-page min-h-screen flex flex-col justify-center'>
      <UIPageBody>
        {/* 전체 페이지 Wrap */}
        <div className='flex flex-col items-center w-full'>
          {/* 에러페이지 컨텐츠 */}
          <UIArticle className='w-full flex flex-col items-center'>
            <UIIcon2 className='ic-system-180-error' style={{ width: '180px' }} />
            <UIGroup gap={16} direction={'column'} className='mt-[32px]' vAlign='center'>
              <UITypography variant='title-2' className='secondary-neutral-800 text-center'>
                {title}
              </UITypography>
              <UITypography variant='body-1' className='secondary-neutral-500 text-center'>
                {description.split('\n').map((line, idx) => (
                  <Fragment key={idx}>
                    {line}
                    <br />
                  </Fragment>
                ))}
              </UITypography>
            </UIGroup>
          </UIArticle>
          <UIArticle>
            <UIGroup gap={16} direction={'row'} align='center'>
              {authUtils.isAuthenticated() ? (
                <UIButton2 className='btn-secondary-blue !min-w-[200px]' onClick={handleGoToDashboard}>
                  대시보드 이동
                </UIButton2>
              ) : (
                <UIButton2 className='btn-secondary-blue !min-w-[200px]' onClick={handleGoToDashboard}>
                  로그인 이동
                </UIButton2>
              )}
            </UIGroup>
          </UIArticle>
        </div>
      </UIPageBody>
    </section>
  );
};
