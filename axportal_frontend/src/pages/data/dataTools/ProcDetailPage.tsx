import { useEffect } from 'react';
import { useParams } from 'react-router-dom';

import { UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useGetProcById } from '@/services/data/tool/dataToolProc.services';

export function ProcDetailPage() {
  const { id } = useParams<{ id: string }>();

  const { data: procData, isLoading, error } = useGetProcById(id || '');

  useEffect(() => {
    // console.log('gridData:', procData);
    // console.log('isLoading:', isLoading);
    if (error) {
      // console.log('error:', error);
    }
  }, [procData, isLoading, error]);

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='데이터도구 조회' description='' />

      {/* 페이지 바디 */}
      <UIPageBody>
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
              프로세서
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
                        이름
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {procData?.name || ''}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        설명
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {procData?.description || ''}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        유형
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {procData?.type || ''}
                      </UITypography>
                    </td>
                  </tr>
                  {procData?.type?.toLowerCase() === 'rule' && (
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Rule Pattern
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {procData?.rulePattern || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Rule Value
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {procData?.ruleValue || ''}
                        </UITypography>
                      </td>
                    </tr>
                  )}
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        데이터 유형
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {procData?.dataType || ''}
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
  );
}
