import { ManagerInfoBox } from '@/components/common';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import type { GetMigMasWithMapResponseItem } from '@/services/deploy/mig/types';
import { MIG_DEPLOY_CATEGORY_MAP } from '@/stores/deploy/types';
interface MigBaseInfomationProps {
  data?: GetMigMasWithMapResponseItem;
}

export function MigBaseInfomation({ data }: MigBaseInfomationProps) {
  return (
    <>
      <section className='section-page'>
        {/* 페이지 바디 */}
        <UIPageBody>
          <UITypography variant='title-3' className='secondary-neutral-900 mt-8 mb-2'>
            기본 정보
          </UITypography>
          <UIArticle>
            <div className='border-t border-black'>
              <table className='tbl-v'>
                <colgroup>
                  <col style={{ width: '10%' }} />
                  <col style={{ width: '40%' }} />
                  <col style={{ width: '10%' }} />
                  <col style={{ width: '40%' }} />
                </colgroup>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        프로젝트명
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {data?.masGpoPrjNm}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        이행 분류
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {data?.masAsstG ? MIG_DEPLOY_CATEGORY_MAP[data.masAsstG as keyof typeof MIG_DEPLOY_CATEGORY_MAP] || data.masAsstG : '-'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        이행 대상
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {data?.masAsstNm}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </UIArticle>
          <UIArticle>
            <ManagerInfoBox
              type={(data?.masCreatedBy ?? '').length === 36 ? 'uuid' : 'memberId'}
              rowInfo={[{ personLabel: '요청자', dateLabel: '이행 요청일시' }]}
              people={[{ userId: data?.masCreatedBy || '', datetime: data?.masFstCreatedAt || '' }]}
            />
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                파일 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 파일명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {data?.masMigFileNm}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          파일 경로
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {data?.masMigFilePath}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </>
  );
}
