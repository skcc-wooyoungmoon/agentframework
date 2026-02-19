import { useCallback, useMemo } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import { UILabel, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIUnitGroup } from '@/components/UI/molecules';
import { type UserType, useUpdateUserStatus } from '@/services/admin/userMgmt';
import { useModal } from '@/stores/common/modal';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';

/**
 * 계정 상태 표시 맵
 */
const ACCOUNT_STATUS_MAP: Record<string, string> = {
  ACTIVE: '활성화',
  DORMANT: '비활성화',
  WITHDRAW: '탈퇴',
};

/**
 * 관리 > 사용자 관리 > 사용자 상세 >  (TAB) 기본 정보
 */
export const UserBasicDetailPage = ({ userInfo }: { userInfo: UserType }) => {
  const qc = useQueryClient();
  const { openAlert, openConfirm } = useModal();

  const { mutate: activateAccount, isPending } = useUpdateUserStatus(userInfo.memberId, {
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: ['GET', `/admin/users/${userInfo.memberId}`] });

      openAlert({
        title: '완료',
        message: '계정 활성화 처리가 완료되었습니다.',
        confirmText: '확인',
      });
    },
  });

  // 재직 여부 및 계정 상태 메모이제이션
  const isEmployed = useMemo(() => userInfo.retrJkwYn === '0', [userInfo.retrJkwYn]);
  const isDormant = useMemo(() => userInfo.dmcStatus === 'DORMANT', [userInfo.dmcStatus]);

  // 계정 활성화 확인 및 처리
  const handleActivate = useCallback(async () => {
    const confirmed = await openConfirm({
      title: '안내',
      message: '해당 계정을 활성화 처리하시겠습니까?',
      confirmText: '예',
      cancelText: '아니요',
    });

    if (confirmed) {
      activateAccount();
    }
  }, [openConfirm, activateAccount]);

  return (
    <>
      {/* 사용자 정보 */}
      <UIArticle>
        <div className='article-header mt-10'>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            사용자 정보
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
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {userInfo.jkwNm}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      인사 상태
                    </UITypography>
                  </th>
                  <td>
                    <UILabel variant='badge' intent={userInfo.retrJkwYn === '0' ? 'complete' : 'error'}>
                      {userInfo.retrJkwYn === '0' ? '재직' : '퇴사'}
                    </UILabel>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      부서
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {userInfo.deptNm}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      직급
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {userInfo.jkgpNm}
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      행번
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {userInfo.memberId}
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>

      {/* 접속 정보 */}
      <UIArticle>
        <div className='article-header'>
          <UIUnitGroup direction='row' align='space-between' gap={0}>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              접속 정보
            </UITypography>
            <div>
              {isEmployed && isDormant && (
                <Button auth={AUTH_KEY.ADMIN.USER_ACCOUNT_ACTIVATE} className='btn-option-outlined' onClick={handleActivate} disabled={isPending}>
                  계정 활성화
                </Button>
              )}
            </div>
          </UIUnitGroup>
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
                      마지막 접속 일시
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {userInfo.lstLoginAt}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      계정 상태
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {ACCOUNT_STATUS_MAP[userInfo.dmcStatus]}
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>
    </>
  );
};
