import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';
import { UIGroup, UIList, UIUnitGroup } from '@/components/UI/molecules';

interface KnowledgeTestErrorAlertProps {
  detail?: string;
  errorCode?: string;
  errorType?: string;
}

/**
 * 지식 테스트 실패 안내 컴포넌트 (POP_ALERT03 기반)
 * 
 * @param detail - 에러 상세 메시지
 * @param errorCode - 에러 코드 (선택)
 * @param errorType - 에러 유형 (선택)
 */
export const KnowledgeTestErrorAlert: React.FC<KnowledgeTestErrorAlertProps> = ({
  detail,
  errorCode,
  errorType,
}) => {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UIGroup gap={24} direction='column'>
          <UITypography variant='body-1' className='secondary-neutral-600'>
            테스트를 실패하였습니다.
            <br />
            입력한 내용 오류 또는 API 호출 오류일 수 있습니다.
            <br />
            입력값 확인 후 다시 시도해 주세요.
          </UITypography>

          <div className='bg-gray-100 rounded-xl px-4 py-4 h-[132px]'>
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

                {detail && (
                  <UITypography variant='body-2' className='secondary-neutral-600' style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
                    {detail}
                  </UITypography>
                )}
              </UIGroup>
            </div>
          </div>
        </UIGroup>
      </UIArticle>
    </section>
  );
};

