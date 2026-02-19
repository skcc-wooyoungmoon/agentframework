import { useEffect } from 'react';

import { useModal } from '@/stores/common/modal/useModal';

import { UITypography } from '@/components/UI';
import { UIArticle, UIGroup, UIList, UIUnitGroup } from '@/components/UI/molecules';

export const ErrorHandler = () => {
  const { openModal } = useModal();

  useEffect(() => {
    const handleApiError = (event: CustomEvent) => {
      // openAlert({
      //   title: '에러',
      //   message: `
      //   ${event.detail.message}
      //   ${event.detail.details ?? ''}
      //   ${env.VITE_RUN_MODE !== 'prod' ? `code : ${event.detail.code}` : ''}
      //   `,
      //   onConfirm: () => {
      //     // TODO 에러 발생시 페이지 이동 정의 필요
      //   },
      // });
      // event.detail은 ErrorInfoType 또는 string일 수 있음
      const errorData = typeof event.detail === 'object' && event.detail !== null ? event.detail : { message: event.detail };

      const errorCode = errorData.code || '';
      const errorType = errorData.hscode || '';
      const errorMessage = errorData.message || '서비스 오류가 발생했습니다.';
      const errorDetails = errorData.details || '';

      openModal({
        type: '2xsmall',
        title: '오류',
        body: (
          <section className='section-modal'>
            <UIArticle>
              <UIGroup gap={24} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-600 whitespace-pre-wrap'>
                  {errorMessage}
                </UITypography>

                <div className={`bg-gray-100 rounded-xl px-4 py-4 ${errorDetails ? 'h-[132px]' : 'h-full'}`}>
                  <div className='h-full overflow-y-auto custom-box-scroll'>
                    <UIGroup gap={16} direction='column' align='start'>
                      {(errorCode || errorType) && (
                        <UIUnitGroup gap={8} direction='column' align='start' style={{ padding: '0 8px' }}>
                          {errorCode && (
                            <UIList
                              gap={4}
                              direction='column'
                              className='ui-list_bullet'
                              data={[
                                {
                                  dataItem: (
                                    <UITypography variant='body-2' className='secondary-neutral-600'>
                                      오류코드 : {errorCode}
                                    </UITypography>
                                  ),
                                },
                              ]}
                            />
                          )}
                          {errorType && (
                            <UIList
                              gap={4}
                              direction='column'
                              className='ui-list_bullet'
                              data={[
                                {
                                  dataItem: (
                                    <UITypography variant='body-2' className='secondary-neutral-600'>
                                      오류유형 : {errorType}
                                    </UITypography>
                                  ),
                                },
                              ]}
                            />
                          )}
                        </UIUnitGroup>
                      )}

                      {errorDetails && (
                        <UITypography variant='body-2' className='secondary-neutral-600 whitespace-pre-wrap'>
                          {errorDetails}
                        </UITypography>
                      )}
                    </UIGroup>
                  </div>
                </div>
              </UIGroup>
            </UIArticle>
          </section>
        ),
      });
    };
    window.addEventListener('api-error', handleApiError as EventListener);
    return () => window.removeEventListener('api-error', handleApiError as EventListener);
  }, [openModal]);

  return null; // UI 렌더링 안함
};
