import { useEffect, useState } from 'react';

import { createPortal } from 'react-dom';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import {
  UIFormField,
  UIGroup,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useUpdateProjectUserRoles } from '@/services/admin/projMgmt/projMgmt.services';
import type {
  GetProjectUsersResponse,
  ProjectRoleUserType,
  ProjectUserRoleChangeRequest,
  ProjectUserRoleChangeResponse
} from '@/services/admin/projMgmt/projMgmt.types';
import { useModal } from '@/stores/common/modal';

type PopupUserSource = ProjectRoleUserType | GetProjectUsersResponse;

type PopupUser = PopupUserSource & { roleUuid?: string; roleNm?: string; no?: number; portalAdmin?: boolean };

interface ProjRoleUserUpdatePopupProps {
  projectId: string;
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => Promise<void> | void;
  users: PopupUser[];
  roleOptions: Array<{ value: string; label: string }>;
  roleId?: string;
}

/**
 * 프로젝트 역할 구성원 역할 변경 팝업
 * 선택된 사용자들의 역할을 일괄 변경합니다.
 */
export const ProjRoleUserUpdatePopup = ({ projectId, isOpen, onClose, onSuccess, users, roleOptions, roleId }: ProjRoleUserUpdatePopupProps) => {
  const { openConfirm, openAlert } = useModal();

  const [selectedRoles, setSelectedRoles] = useState<Record<string, string>>({});
  const [openDropdownUserId, setOpenDropdownUserId] = useState<string | null>(null);

  // API 호출
  const { mutateAsync: updateProjectUserRoles, isPending } = useUpdateProjectUserRoles(projectId);

  useEffect(() => {
    if (!isOpen) {
      setOpenDropdownUserId(null);
      return;
    }

    setSelectedRoles(prev => {
      const fallbackRoleValue = (() => {
        if (roleId) {
          const matched = roleOptions.find(option => option.value === roleId);
          if (matched) {
            return matched.value;
          }
        }
        return roleOptions[0]?.value ?? '';
      })();

      const next: Record<string, string> = {};

      users.forEach(user => {
        const previousValue = prev[user.uuid];
        if (previousValue && roleOptions.some(option => option.value === previousValue)) {
          next[user.uuid] = previousValue;
          return;
        }

        const resolveUserRoleCandidate = () => {
          if (user.roleUuid) {
            return user.roleUuid;
          }

          if (user.roleNm) {
            const matchedByName = roleOptions.find(option => option.label === user.roleNm);
            if (matchedByName) {
              return matchedByName.value;
            }
          }

          return roleId;
        };

        const userRoleCandidate = resolveUserRoleCandidate();

        const matchedRole = userRoleCandidate && roleOptions.some(option => option.value === userRoleCandidate) ? userRoleCandidate : fallbackRoleValue;
        next[user.uuid] = matchedRole;
      });

      return next;
    });
  }, [isOpen, users, roleOptions, roleId]);

  const hasRoleOptions = roleOptions.length > 0;
  const hasUsers = users.length > 0;
  const isConfirmDisabled = isPending || !hasRoleOptions || (hasUsers && users.some(user => !selectedRoles[user.uuid]));

  const handleCancel = async () => {
    if (isPending) {
      return;
    }

    // 항상 확인 모달 표시 (변경사항 여부와 관계없이)
    const confirmed = await openConfirm({
      title: '안내',
      message: `화면을 나가시겠어요?
      입력한 정보가 저장되지 않을 수 있습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      onClose();
    }
  };

  const handleSave = async () => {
    if (isPending) {
      return;
    }

    const payload = users.reduce<ProjectUserRoleChangeRequest['users']>((acc, user) => {
      const nextRole = selectedRoles[user.uuid];

      if (!nextRole || !user.uuid) {
        return acc;
      }

      acc.push({
        userUuid: user.uuid,
        roleUuid: nextRole,
      });

      return acc;
    }, []);

    if (payload.length === 0) {
      await openAlert({
        title: '안내',
        message: '전송할 구성원 역할 정보가 없습니다.',
      });
      return;
    }

    const response = await updateProjectUserRoles({ users: payload });
    const result: ProjectUserRoleChangeResponse | undefined = response.data;

    const total = payload.length;
    const successCount = result?.successCount ?? total;
    const failureCount = result?.failureCount ?? Math.max(total - successCount, 0);

    if (failureCount > 0) {
      if (successCount > 0) {
        if (onSuccess) {
          await onSuccess();
        }
      }

      await openAlert({
        title: '실패',
        message: '구성원 역할 변경에 실패하였습니다.',
      });
      return;
    }

    await openAlert({
      title: '완료',
      message: '구성원 역할 변경이 완료되었습니다.',
    });

    if (onSuccess) {
      await onSuccess();
    }

    onClose();
  };

  if (!isOpen) {
    return null;
  }

  const handleLayerClose = () => {
    if (isPending) {
      return;
    }
    void handleCancel();
  };

  return createPortal(
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleLayerClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='구성원 역할 변경' description='' position='left' />

          {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel} disabled={isPending}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={isConfirmDisabled} onClick={handleSave}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 - 구성원 역할 변경 */}
      <section className='section-popup-content'>
        <UIPopupHeader title='구성원 역할 변경' description='역할을 변경 후, 저장 버튼을 눌러주세요.' position='right' />
        <UIPopupBody>
          {hasUsers ? (
            users.map(user => {
              const dropdownValue = selectedRoles[user.uuid] ?? '';
              const isOpenDropdown = openDropdownUserId === user.uuid;

              return (
                <UIArticle key={user.uuid}>
                  <UIFormField gap={8} direction='column'>
                    <UIGroup gap={0} direction='row'>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                        {user.jkwNm}
                      </UITypography>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                        {user.deptNm}
                      </UITypography>
                    </UIGroup>

                    <UIDropdown
                      value={dropdownValue}
                      options={roleOptions}
                      isOpen={isOpenDropdown}
                      onClick={() => {
                        setOpenDropdownUserId(prev => (prev === user.uuid ? null : user.uuid));
                      }}
                      onSelect={(value: string) => {
                        setSelectedRoles(prev => ({
                          ...prev,
                          [user.uuid]: value,
                        }));
                        setOpenDropdownUserId(null);
                      }}
                      disabled={!hasRoleOptions}
                    />
                  </UIFormField>
                </UIArticle>
              );
            })
          ) : (
            <UIArticle>
              <UITypography variant='body-2' className='text-gray-500'>
                표시할 구성원이 없습니다.
              </UITypography>
            </UIArticle>
          )}
        </UIPopupBody>
      </section>
    </UILayerPopup>,
    document.body
  );
};
