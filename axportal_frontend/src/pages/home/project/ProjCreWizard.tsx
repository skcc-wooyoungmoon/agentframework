// src/pages/home/project/ProjCreWizard.tsx
import React, { useState } from 'react';

import { useAtom } from 'jotai';

import { UIButton2 } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { PayReqWizard } from '@/pages/common/PayReqWizrad.tsx';
import { ProjCreStep1BaseInfo, ProjCreStep2MemSel } from '@/pages/home';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { useCreateProjBaseInfo, useDeleteProj } from '@/services/home/proj/projBaseInfo.service';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import { projCreatedSeqAtom, projCreBaseInfoAtom, projCreSelectedMembersAtom, resetAllProjCreDataAtom } from '@/stores/home/proj/projCreWizard.atoms';

interface ProjCreWizardProps {
  onClose?: () => void;
}

export const ProjCreWizard: React.FC<ProjCreWizardProps> = ({ onClose }) => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [currentStep, setCurrentStep] = useState(1); // 기본값을 1로 설정
  const [, resetAllData] = useAtom(resetAllProjCreDataAtom);
  const [isPayReqWizardOpen, setIsPayReqWizardOpen] = useState(false);
  // PayReqWizard에 전달할 프로젝트 정보를 별도로 보관
  const [approvalInfo, setApprovalInfo] = useState<{
    memberId: string;
    approvalType: string; // 업무코드
    approvalUniqueKey?: string; // 요청식별자 (중복방지 등 목적으로 각 업무에서 활용)
    approvalParamKey?: number; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalParamValue?: string; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalItemString: string; // 요청하는 대상/작업 이름 (알람 표시 목적)
    afterProcessParamString: string; // 후처리 변수
    approvalSummary?: string; // 결재사유 메세지
    apprivalTableInfo?: { key: string; value: string }[][];
  }>({
    memberId: '',
    approvalType: '',
    afterProcessParamString: '',
    approvalItemString: '',
  });

  const [baseInfo] = useAtom(projCreBaseInfoAtom);
  const [selectedMembers] = useAtom(projCreSelectedMembersAtom);

  const [createdPrjSeq, setCreatedPrjSeq] = useAtom(projCreatedSeqAtom);
  const { openConfirm, openAlert } = useModal();
  const { user, updateUser } = useUser();

  // 프로젝트 삭제 API 훅 사용
  const { mutate: deleteProject } = useDeleteProj();

  // 스테퍼 항목
  const stepperItems = [
    { id: 'step1', step: 1, label: '기본 정보 입력' },
    { id: 'step2', step: 2, label: '구성원 선택' },
  ];

  // 팝업 닫기 핸들러
  const handleClose = async () => {
    // 취소 확인 대화상자 표시
    const confirmed = await openConfirm({
      title: '안내',
      bodyType: 'text',
      message: '화면을 나가시겠어요?  \n입력한 정보가 저장되지 않을 수 있습니다.',
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      // 생성된 프로젝트가 있으면 삭제 (0보다 큰 경우에만 실행)
      if (createdPrjSeq != null && createdPrjSeq > 0) {
        deleteProject({ prjSeq: createdPrjSeq });
      }

      setIsPopupOpen(false);
      resetAllData(); // 모든 상태 초기화
      // 부모 컴포넌트에 알림
      if (onClose) {
        onClose();
      }
    }
  };

  // 다음 단계로 이동
  const handleNextStep = () => {
    setCurrentStep(prev => Math.min(prev + 1, stepperItems.length));
  };

  // 이전 단계로 이동
  const handlePreviousStep = () => {
    setCurrentStep(prev => Math.max(prev - 1, 1));
  };

  // PayReqWizard 닫기 핸들러
  const handlePayReqWizardClose = () => {
    setIsPayReqWizardOpen(false);
    // 부모 컴포넌트에 알림
    if (onClose) {
      onClose();
    }
  };

  const handleComfirm = async () => {
    // console.log('=== 프로젝트 생성 확인 ===');
    // console.log('기본 정보:', baseInfo);
    // console.log('선택된 멤버 ID:', selectedMembers);

    const publicProject = user.projectList.find(project => project.prjSeq === '-999');

    // 포탈 관리자는 즉시 생성
    if (publicProject?.prjRoleSeq === '-199') {
      openConfirm({
        title: '안내',
        message: '프로젝트를 생성하시겠어요?',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          // 프로젝트 생성 API 호출
          const username = user.userInfo.memberId;
          const projectData = {
            ...baseInfo,
            username: username,
            is_portal_admin: 'Y', // 포탈 관리자는 프로젝트 즉시 생성하기 위한 플래그
            member_ids: selectedMembers,
          };

          createProject(projectData);
        },
        onCancel: () => {
          // console.log('취소');
        },
      });
    } else {
      // 프로젝트 생성 API 호출
      const username = user.userInfo.memberId;
      const projectData = {
        ...baseInfo,
        username: username,
        member_ids: selectedMembers,
        is_portal_admin: 'N',
      };

      createProject(projectData);
    }
  };

  // 프로젝트 생성 API 훅 사용
  const { mutate: createProject } = useCreateProjBaseInfo({
    onSuccess: async data => {
      // console.log('프로젝트 생성 성공:', data);
      // console.log('프로젝트 ID :', data.data.projectId);
      // 생성된 프로젝트 ID 저장
      setCreatedPrjSeq(data.data.prjSeq);

      const publicProject = user.projectList.find(project => project.prjSeq === '-999');

      // 포탈 관리자는 결재없이 즉시 생성
      if (publicProject?.prjRoleSeq === '-199') {
        openAlert({
          title: '완료',
          message: '프로젝트 생성이 완료되었습니다.',
        });

        // 상단 프로젝트목록 갱신용
        try {
          const updatedUser = await authServices.getMe();
          if (updatedUser) {
            updateUser(updatedUser);
          }
        } catch {
          // console.error('사용자 데이터 갱신 실패:', error);
        }

        resetAllData(); // 모든 상태 초기화 (이제 savedProjectInfo는 영향받지 않음)
        setIsPopupOpen(false);
        if (onClose) {
          onClose();
        }
      } else {
        setIsPopupOpen(false);

        // PayReqWizard에 전달할 프로젝트 정보를 API 응답 데이터로 저장
        setApprovalInfo({
          memberId: user.userInfo.memberId,
          approvalType: '01', // 프로젝트 생성
          approvalItemString: data.data.name,
          afterProcessParamString: JSON.stringify({
            prjSeq: data.data.prjSeq,
          }),
          apprivalTableInfo: [
            [
              { key: '프로젝트명', value: data.data.name },
              { key: '설명', value: data.data.description },
            ],
          ],
        });

        // PayReqWizard 화면 표시
        setIsPayReqWizardOpen(true);
        resetAllData(); // 모든 상태 초기화 (이제 savedProjectInfo는 영향받지 않음)
      }
    },
  });

  // 단계별 컴포넌트 렌더링
  const renderStepContent = () => {
    switch (currentStep) {
      case 1:
        return <ProjCreStep1BaseInfo onNextStep={handleNextStep} />;
      case 2:
        return <ProjCreStep2MemSel currentStep={currentStep} stepperItems={stepperItems} onComfirmStep={handleComfirm} onPreviousStep={handlePreviousStep} />;
      default:
        return null;
    }
  };

  return (
    <>
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='프로젝트 생성' description='' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper items={stepperItems} currentStep={currentStep} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIUnitGroup gap={8} direction='row'>
                <UIButton2 className='btn-aside-gray' onClick={handleClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-aside-blue' onClick={handleComfirm} disabled={currentStep !== 2}>
                  생성
                </UIButton2>
              </UIUnitGroup>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {renderStepContent()}
      </UILayerPopup>

      {/* 프로젝트 생성 성공 후 PayReqWizard 표시 */}
      {isPayReqWizardOpen && (
        <PayReqWizard
          isOpen={isPayReqWizardOpen}
          onClose={handlePayReqWizardClose}
          approvalInfo={approvalInfo} // 저장된 프로젝트 정보 전달
        />
      )}
    </>
  );
};
