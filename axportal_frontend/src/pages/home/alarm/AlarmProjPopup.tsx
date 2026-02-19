import React from 'react';

import { UILabel, UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetApprovalUserInfo } from '@/services/home/alarm';

interface AlarmProjPopupProps {
  alarmItem?: {
    id: string;
    title: string;
    description: string;
    time: string;
    isRead?: boolean;
  } | null;
  projectId?: string;
}

export const AlarmProjPopup: React.FC<AlarmProjPopupProps> = ({ alarmItem }) => {
  // API 호출 - UserInfo
  const userInfoQuery = useGetApprovalUserInfo({
    alarmId: alarmItem?.id || '',
  });
  const { data: approvalUserInfo } = userInfoQuery;
  const statusText = approvalUserInfo?.apiSpclV === 'APPROVAL' ? '승인' : '반려';

  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
            결재 정보
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '248px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '248px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      결재 결과
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      <UILabel variant='badge' intent={statusText === '승인' ? 'complete' : 'error'}>
                        {statusText || ''}
                      </UILabel>
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      결재자
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {approvalUserInfo?.jkwNm || ''} ㅣ {approvalUserInfo?.deptNm || ''}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      결재 일자
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {approvalUserInfo?.fstCreatedAt || ''}
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>

      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
            요청 정보
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '248px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '248px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      업무 구분
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UITypography variant='body-2' className='secondary-neutral-800 text-sb'>
                      {approvalUserInfo?.payApprovalInfo?.displayInfo?.typeNm}
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      이름
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {approvalUserInfo?.payApprovalInfo?.displayInfo?.jkwNm}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      부서
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {approvalUserInfo?.payApprovalInfo?.displayInfo?.deptNm}
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      프로젝트명
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {approvalUserInfo?.payApprovalInfo?.displayInfo?.prjNm}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      역할
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {approvalUserInfo?.payApprovalInfo?.displayInfo?.prjRoleNm}
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      요청 사유
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {approvalUserInfo?.payApprovalInfo?.approvalInfo?.approvalSummary}
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>
    </section>
  );
};
