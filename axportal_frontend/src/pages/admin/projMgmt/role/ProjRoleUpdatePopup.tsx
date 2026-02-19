import { useAtomValue } from 'jotai';
import React, { useState } from 'react';
import { createPortal } from 'react-dom';

import { useParams } from 'react-router';

import { UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetProjectById, useUpdateProjectRole } from '@/services/admin/projMgmt';
import { projRoleListRefetchAtom } from '@/stores/admin/projMgmt/roles';
import { useModal } from '@/stores/common/modal';

interface ProjRoleBaseEditProps {
  onClose: () => void;
  roleData?: {
    name: string;
    description: string;
  };
  refetch: () => void;
}

/**
 * 프로젝트 관리 > 프로젝트 상세 >  (TAB) 역할 정보 > 역할 상세 > (TAB) 기본정보 - 수정 팝업
 */
export const ProjRoleUpdatePopup: React.FC<ProjRoleBaseEditProps> = ({ onClose, roleData, refetch }) => {
  const { projectId, roleId } = useParams();
  const refetchRoleList = useAtomValue(projRoleListRefetchAtom);
  const { data: projectData } = useGetProjectById(projectId!);

  const { openAlert, openConfirm } = useModal();
  const [isPopupOpen] = useState(true);

  const [roleName, setRoleName] = useState(roleData?.name || '');
  const [roleDescription, setRoleDescription] = useState(roleData?.description || '');

  const updateRoleMutation = useUpdateProjectRole(projectId!, roleId!, {
    onSuccess: () => {
      openAlert({
        bodyType: 'text',
        title: '완료',
        message: '수정 사항이 저장되었습니다.',
        confirmText: '확인',
        onConfirm: async () => {
          refetchRoleList?.();
          refetch();
          onClose();
        },
      });
    },
  });

  // 저장
  const handleSave = () => {
    if (!roleName.trim()) {
      openAlert({
        bodyType: 'text',
        message: '역할명은 필수입니다.',
        confirmText: '확인',
      });
      return;
    }

    // 수정된 내용이 있는지 확인
    const isNotNameChanged = roleName.trim() === roleData?.name;
    const isNotDescriptionChanged = roleDescription.trim() === roleData?.description;

    if (isNotNameChanged && isNotDescriptionChanged) {
      openAlert({
        bodyType: 'text',
        title: '안내',
        message: '수정된 내용이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    updateRoleMutation.mutate({
      roleNm: roleName.trim(),
      dtlCtnt: roleDescription.trim(),
    });
  };

  const handleCancel = async () => {
    const confirmed = await openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      onClose();
    }
  };

  const handleClose = async () => {
    await handleCancel();
  };

  return createPortal(
    <UILayerPopup
      isOpen={isPopupOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='기본 정보 수정' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray w-[80px]' onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue w-[80px]' onClick={handleSave} disabled={!roleName.trim()}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      {/* 콘텐츠 영역 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='기본 정보 수정' description='역할명, 설명 등 역할의 기본 정보를 수정해주세요.' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          {/* 프로젝트명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                프로젝트명
              </UITypography>
              <UIInput.Text title='프로젝트명' placeholder='프로젝트명 입력' value={projectData?.project.prjNm || ''} readOnly={true} />
            </UIFormField>
          </UIArticle>

          {/* 역할명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                역할명
              </UITypography>
              <UIInput.Text title='역할명' placeholder='역할명 입력' maxLength={50} value={roleName} onChange={e => setRoleName(e.target.value)} />
            </UIFormField>
          </UIArticle>

          {/* 역할 설명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                설명
              </UITypography>
              <UITextArea2 value={roleDescription} onChange={e => setRoleDescription(e.target.value)} placeholder='설명 입력' maxLength={100} />
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>,
    document.body
  );
};
