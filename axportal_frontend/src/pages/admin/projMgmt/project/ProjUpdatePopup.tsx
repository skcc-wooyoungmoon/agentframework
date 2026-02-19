import { useState } from 'react';

import { UIButton2, UIRadio2, UITypography } from '@/components/UI/atoms';
import {
  UIArticle,
  UIFormField,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { type ProjectDetailType, useUpdateProject } from '@/services/admin/projMgmt';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';

/**
 * 프로젝트 관리 > 프로젝트 상세 > (TAB) 기본 정보 > 프로젝트 수정 팝업
 */
export const ProjUpdatePopup = ({ projectInfo, onClose, onSuccess }: { projectInfo: ProjectDetailType; onClose: () => void; onSuccess?: () => void }) => {
  const { openConfirm, openAlert } = useModal();
  const { updateUser } = useUser();

  const [isPopupOpen, setIsPopupOpen] = useState(true);

  const [prjNm, setPrjNm] = useState(projectInfo.prjNm);
  const [dtlCtnt, setDtlCtnt] = useState(projectInfo.dtlCtnt);
  const [sstvInfInclYn, setSstvInfInclYn] = useState(projectInfo.sstvInfInclYn);
  const [sstvInfInclDesc, setSstvInfInclDesc] = useState(projectInfo.sstvInfInclDesc ?? '');

  // ================================
  // API 호출
  // ================================

  const updateProjectMutation = useUpdateProject(projectInfo.uuid, {
    onSuccess: async () => {
      onSuccess?.();
      openAlert({
        title: '완료',
        bodyType: 'text',
        message: '수정 사항이 저장되었습니다.',
        confirmText: '확인',
        onConfirm: () => {
          handleClose();
        },
      });

      // 상단 프로젝트목록 갱신용
      const updatedUser = await authServices.getMe();

      if (updatedUser) {
        updateUser(updatedUser);
      }
    },
  });

  // ================================
  // 이벤트 핸들러
  // ================================

  const handleClose = () => {
    setIsPopupOpen(false);
    onClose();
  };

  const handleCancel = async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: `화면을 나가시겠습니까?
                입력한 정보가 저장되지 않을 수 있습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      handleClose();
    }
  };

  // 프로젝트 저장
  const handleSave = () => {
    if (!prjNm.trim()) {
      openAlert({
        bodyType: 'text',
        message: '필수 항목을 입력해주세요.',
        confirmText: '확인',
      });
      return;
    }

    const hasChanged =
      projectInfo.prjNm !== prjNm ||
      projectInfo.dtlCtnt !== dtlCtnt ||
      projectInfo.sstvInfInclYn !== sstvInfInclYn ||
      (sstvInfInclYn === 'Y' && projectInfo.sstvInfInclDesc !== sstvInfInclDesc);

    if (!hasChanged) {
      openAlert({
        bodyType: 'text',
        title: '안내',
        message: '수정된 내용이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    updateProjectMutation.mutate({
      prjNm,
      dtlCtnt,
      sstvInfInclYn,
      sstvInfInclDesc: sstvInfInclYn === 'Y' ? sstvInfInclDesc : undefined,
    });
  };

  return (
    <UILayerPopup
      isOpen={isPopupOpen}
      onClose={handleClose} // Use real close handler
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
                <UIButton2 className='btn-tertiary-blue w-[80px]' onClick={handleSave} disabled={!prjNm.trim() || (sstvInfInclYn === 'Y' && !sstvInfInclDesc?.trim())}>
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
        <UIPopupHeader title='기본 정보 수정' description='프로젝트명 및 설명을 수정해주세요.' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          {/* 프로젝트명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                프로젝트명
              </UITypography>
              <div>
                <UIInput.Text value={prjNm} onChange={e => setPrjNm(e.target.value)} placeholder='프로젝트명 입력' />
              </div>
            </UIFormField>
          </UIArticle>

          {/* 설명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                설명
              </UITypography>
              <UITextArea2 value={dtlCtnt} onChange={e => setDtlCtnt(e.target.value)} placeholder='설명 입력' maxLength={100} />
            </UIFormField>
          </UIArticle>

          {/* 개인정보 관련 필드는 projectInfo.sstvInfInclYn가 'Y'가 아닐 때만 렌더링 */}
          {projectInfo.sstvInfInclYn !== 'Y' && (
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  개인정보 포함 여부
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='N' label='미포함' checked={sstvInfInclYn === 'N'} onChange={() => setSstvInfInclYn('N')} />
                  <UIRadio2 name='basic1' value='Y' label='포함' checked={sstvInfInclYn === 'Y'} onChange={() => setSstvInfInclYn('Y')} />
                </UIUnitGroup>
                {sstvInfInclYn === 'Y' && (
                  <div>
                    <UITextArea2 value={sstvInfInclDesc} onChange={e => setSstvInfInclDesc(e.target.value)} maxLength={100} placeholder='개인정보 포함 사유 입력' />
                  </div>
                )}
              </UIFormField>
            </UIArticle>
          )}
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
