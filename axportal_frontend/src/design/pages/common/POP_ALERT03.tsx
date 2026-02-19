import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';
import { UIGroup, UIList, UIUnitGroup } from '@/components/UI/molecules';

export const POP_ALERT03 = () => {
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
                <UIUnitGroup gap={8} direction='column' align='start' style={{ padding: '0 8px' }}>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            오류코드 : C001
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            오류유형 : Bad Request
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </UIUnitGroup>

                <UITypography variant='body-2' className='secondary-neutral-600'>
                  오류메세지는 최대 2줄까지 노출됩니다. 넘어갈 경우 세로 스크롤 발생합니다. 오류메세지는 최대 2줄까지 노출 오류메세지는 최대 2줄까지 노출됩니다. 넘어갈 경우 세로
                </UITypography>
              </UIGroup>
            </div>
          </div>
        </UIGroup>
      </UIArticle>
    </section>
  );
};
