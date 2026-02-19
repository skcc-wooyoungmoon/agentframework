import { useEffect, useMemo, useRef } from 'react';

import { useGetManagerInfoBulk } from '@/services/common/userInfo.service';

import { useEnvCheck } from '@/hooks/common/util';
import { dateUtils } from '../../../../utils/common';
import { UITypography } from '../../../UI/atoms';
import { UIArticle } from '../../../UI/molecules/UIArticle';

interface ManagerInfoBoxProps {
  title?: string;
  type?: 'memberId' | 'uuid';

  // 기존 호환성을 위한 props
  people?: Array<{ userId: string; datetime: string }>;
  rowInfo?: Array<{ personLabel?: string; dateLabel?: string }>;
}

export const ManagerInfoBox = ({
  title = '담당자 정보',
  type = 'memberId',
  people,
  rowInfo = [
    { personLabel: '생성자', dateLabel: '생성일시' },
    { personLabel: '최종 수정자', dateLabel: '최종 수정일시' },
  ],
}: ManagerInfoBoxProps) => {
  const { isProd } = useEnvCheck();
  const isValid = useMemo(() => {
    return people && people.length > 0 && people.filter(person => person.userId != null && person.userId !== '').length > 0;
  }, [people]);

  const savedDatetimesRef = useRef<string[]>([]);

  const { data: managerInfo, refetch } = useGetManagerInfoBulk(
    {
      type,
      values: people?.map(person => person.userId) || [],
    },
    {
      enabled: isValid, // 유효한 userId가 있을 때만 API 호출
      refetchOnMount: true, // 컴포넌트가 마운트될 때마다 재조회
    }
  );

  // isValid가 true이고, datetime가 변경되면 refetch
  useEffect(() => {
    if (isValid) {
      const currentDatetimes = people?.map(person => person.datetime) || [];
      const prevDatetimes = savedDatetimesRef.current;
      const hasChanged = prevDatetimes.length !== currentDatetimes.length || prevDatetimes.some((datetime, index) => datetime !== currentDatetimes[index]);

      if (hasChanged) {
        savedDatetimesRef.current = currentDatetimes;
        refetch();
      }
    }
  }, [isValid, people, refetch]);

  return (
    <UIArticle>
      <div className='article-header'>
        <UITypography variant='title-4' className='secondary-neutral-900'>
          {title}
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
              {rowInfo
                ? rowInfo.map((row, index) => (
                    <tr key={index} className='border-b border-gray-200'>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900 whitespace-break-spaces'>
                          {row.personLabel || ''}
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {/* TODO : YERI null일 경우 처리 변경 */}
                          {managerInfo?.[index]?.jkwNm ? `${managerInfo[index].jkwNm} | ${managerInfo[index].deptNm}` : isProd ? '시스템 | 시스템관리팀' : ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900 whitespace-break-spaces'>
                          {row.dateLabel || ''}
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {people?.[index]?.datetime ? dateUtils.formatDate(people?.[index]?.datetime, 'datetime') : ''}
                        </UITypography>
                      </td>
                    </tr>
                  ))
                : null}
            </tbody>
          </table>
        </div>
      </div>
    </UIArticle>
  );
};
