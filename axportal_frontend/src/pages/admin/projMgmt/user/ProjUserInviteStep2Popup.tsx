import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import {
  UIArticle,
  UIDropdown,
  UIFormField,
  UIGroup,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  type UIStepperItem,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetProjectRoles } from '@/services/admin/projMgmt';
import type { ProjectUserType } from '@/services/admin/projMgmt/projMgmt.types';
import { useModal } from '@/stores/common/modal';

type UserRoleAssignment = {
  user: ProjectUserType;
  roleUuid: string;
};

type ProjUserInviteStep2RoleAssignPopupProps = {
  isOpen: boolean;
  onClose: () => void;
  // eslint-disable-next-line no-unused-vars
  onPrevious: (currentUserRoles: Record<string, string>) => void;
  // eslint-disable-next-line no-unused-vars
  onComplete: (assignments: UserRoleAssignment[]) => void;
  projectId: string;
  selectedUsers: ProjectUserType[];
  initialUserRoles?: Record<string, string>;
  isSubmitting?: boolean;
};

const STEPPER_ITEMS: UIStepperItem[] = [
  {
    id: 'step1',
    label: '사용자 선택',
    step: 1,
  },
  {
    id: 'step2',
    label: '역할 할당',
    step: 2,
  },
];

const buildInitialRoleKey = (roles?: Record<string, string>) => JSON.stringify(roles ?? {});

/**
 * 프로젝트 관리 > 프로젝트 상세 > 구성원 정보(TAB) > 구성원 초대 - 역할 할당 팝업
 */
export const ProjUserInviteStep2Popup: React.FC<ProjUserInviteStep2RoleAssignPopupProps> = ({
  isOpen,
  onClose,
  onPrevious,
  onComplete,
  projectId,
  selectedUsers,
  initialUserRoles,
  isSubmitting = false,
}) => {
  const { openConfirm } = useModal();
  const { data: rolesData } = useGetProjectRoles(projectId, {
    page: 1,
    size: 100,
  });

  const resolveUserKey = useCallback((user: ProjectUserType) => user.uuid || user.memberId, []);
  const initialUserRolesKey = useMemo(() => buildInitialRoleKey(initialUserRoles), [initialUserRoles]);

  const [userRoles, setUserRoles] = useState<Record<string, string>>({});
  const [dropdownStates, setDropdownStates] = useState<Record<string, boolean>>({});

  useEffect(() => {
    if (!isOpen) return;
  }, [isOpen, initialUserRoles]);

  useEffect(() => {
    if (!isOpen) {
      setUserRoles({});
      setDropdownStates({});
    }
  }, [isOpen]);

  const testerRole = useMemo(() => {
    return rolesData?.content?.find(role => role.roleNm?.toLowerCase() === '테스터' || role.roleNm?.toLowerCase() === 'tester');
  }, [rolesData]);

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    setDropdownStates(prev => {
      const next: Record<string, boolean> = {};
      selectedUsers.forEach(user => {
        const key = resolveUserKey(user);
        if (key && prev[key]) {
          next[key] = prev[key];
        }
      });
      return next;
    });
  }, [isOpen, selectedUsers, resolveUserKey]);

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    setUserRoles(prev => {
      const next: Record<string, string> = {};

      selectedUsers.forEach(user => {
        const key = resolveUserKey(user);
        if (!key) {
          return;
        }

        const initialRole = initialUserRoles?.[key];
        const prevRole = prev[key];

        if (initialRole) {
          next[key] = initialRole;
          return;
        }

        if (prevRole) {
          next[key] = prevRole;
          return;
        }

        if (testerRole) {
          next[key] = testerRole.uuid;
        }
      });

      const prevKeys = Object.keys(prev);
      const nextKeys = Object.keys(next);

      if (prevKeys.length === nextKeys.length && prevKeys.every(key => prev[key] === next[key])) {
        return prev;
      }

      return next;
    });
  }, [isOpen, initialUserRolesKey, testerRole, selectedUsers, resolveUserKey]);

  const roleOptions = useMemo(
    () =>
      rolesData?.content?.map(role => ({
        value: role.uuid,
        label: role.roleNm,
      })) ?? [],
    [rolesData]
  );

  const toggleDropdown = (userKey: string) => {
    setDropdownStates(prev => ({
      ...prev,
      [userKey]: !prev[userKey],
    }));
  };

  const handleRoleSelect = (userKey: string, roleUuid: string) => {
    setUserRoles(prev => ({
      ...prev,
      [userKey]: roleUuid,
    }));
    setDropdownStates(prev => ({
      ...prev,
      [userKey]: false,
    }));
  };

  const isCompleteEnabled = selectedUsers.every(user => {
    const key = resolveUserKey(user);
    return key ? Boolean(userRoles[key]) : false;
  });

  const handleCancel = useCallback(async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: `화면을 나가시겠어요?
                입력한 정보가 저장되지 않을 수 있습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      onClose();
    }
  }, [openConfirm, onClose]);

  const handleComplete = () => {
    if (!isCompleteEnabled || isSubmitting) {
      return;
    }

    const assignments: UserRoleAssignment[] = selectedUsers
      .map(user => {
        const key = resolveUserKey(user);
        if (!key) {
          return null;
        }

        const roleUuid = userRoles[key];
        if (!roleUuid) {
          return null;
        }

        return { user, roleUuid };
      })
      .filter((assignment): assignment is UserRoleAssignment => assignment !== null);

    onComplete(assignments);
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleCancel}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='구성원 초대하기' position='left' />

          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={2} items={STEPPER_ITEMS} direction='vertical' />
            </UIArticle>
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleComplete} disabled={!isCompleteEnabled || isSubmitting}>
                  완료
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 - 2단계: 역할 할당 */}
      <section className='section-popup-content'>
        {/* 팝업 헤더 */}
        <UIPopupHeader title='역할 할당' description='사용자 역할을 수정하고 싶은 경우 변경할 수 있습니다. 기본 역할은 테스터로 할당됩니다.' position='right' />

        {/* 팝업 내용 */}
        <UIPopupBody>
          {selectedUsers.map(user => {
            const key = resolveUserKey(user);
            if (!key) return null;

            return (
              <UIArticle key={key}>
                <UIFormField gap={8} direction='column'>
                  <UIGroup gap={0} direction='row'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                      {user.jkwNm}
                    </UITypography>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' divider={true} required={true}>
                      {user.deptNm}
                    </UITypography>
                  </UIGroup>

                  <UIDropdown
                    value={userRoles[key] || ''}
                    options={roleOptions}
                    isOpen={dropdownStates[key] || false}
                    onClick={() => toggleDropdown(key)}
                    onSelect={(value: string) => handleRoleSelect(key, value)}
                    placeholder='역할을 선택해주세요'
                  />
                </UIFormField>
              </UIArticle>
            );
          })}
        </UIPopupBody>

        {/* 하단 버튼 */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={() => onPrevious(userRoles)}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
