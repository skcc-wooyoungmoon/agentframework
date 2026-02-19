// src/pages/home/project/ProjJoinWizard.tsx
import React, { useCallback, useState } from 'react';

import { useAtom } from 'jotai';

import { UIButton2 } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { PayReqWizard } from '@/pages/common/PayReqWizrad.tsx';
import { ProJoinStep1, ProJoinStep2 } from '@/pages/home';
import { usePutJoinProjInfo } from '@/services/home/proj/projBaseInfo.service';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import { projJoinSelectedProjectAtom, resetAllProjJoinDataAtom } from '@/stores/home/proj/projJoinWizard.atoms';
import { authServices } from '@/services/auth/auth.non.services.ts';

interface ProjJoinWizardProps {
  onClose?: () => void;
}

export const ProjJoinWizard: React.FC<ProjJoinWizardProps> = ({ onClose }) => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [currentStep, setCurrentStep] = useState(1); // 기본값을 1로 설정
  const [isPayReqWizardOpen, setIsPayReqWizardOpen] = useState(false);

  const [, resetAllData] = useAtom(resetAllProjJoinDataAtom);
  const [selectedProject] = useAtom(projJoinSelectedProjectAtom);
  const { openConfirm, openAlert } = useModal();
  const { user } = useUser();

  // 스테퍼 항목
  const stepperItems = [
    { id: 'step1', step: 1, label: '프로젝트 선택' },
    { id: 'step2', step: 2, label: '프로젝트 정보 확인' },
  ];

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

  // PayReqWizard 닫기 핸들러
  const handlePayReqWizardClose = () => {
    setIsPayReqWizardOpen(false);
    // 부모 컴포넌트에 알림
    if (onClose) {
      onClose();
    }
  };

  // 팝업 닫기 핸들러
  const handleClose = useCallback(async () => {
    // 취소 확인 대화상자 표시
    const confirmed = await openConfirm({
      title: '취소',
      bodyType: 'text',
      message: '화면을 나가시겠어요?  \n입력한 정보가 저장되지 않을 수 있습니다.',
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      resetAllData(); // 모든 상태 초기화
      setIsPopupOpen(false);
      // 부모 컴포넌트에 알림
      if (onClose) {
        onClose();
      }
    }
  }, [openConfirm, resetAllData, onClose]);

  // 다음 단계로 이동
  const handleNextStep = () => {
    setCurrentStep(prev => Math.min(prev + 1, stepperItems.length));
  };

  // 이전 단계로 이동
  const handlePreviousStep = () => {
    setCurrentStep(prev => Math.max(prev - 1, 1));
  };

  // 프로젝트 참여 핸들러
  const handleJoin = async () => {
    // console.log('=== 프로젝트 생성 확인 ===');
    // console.log('=== 프로젝트 참여 확인 ===');
    // console.log('선택된 프로젝트:', selectedProject);

    if (!selectedProject) {
      // console.error('선택된 프로젝트가 없습니다.');
      openAlert({
        title: '알림',
        message: '프로젝트를 먼저 선택해주세요.',
      });
      return;
    }

    // 참여 확인 대화상자 표시
    const confirmed = await openConfirm({
      title: '안내',
      bodyType: 'text',
      message: '프로젝트에 참여하시겠습니까?',
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      const publicProject = user.projectList.find(project => project.prjSeq === '-999');

      // 포탈 관리자는 즉시 참여
      if (publicProject?.prjRoleSeq === '-199') {
        // 프로젝트 참여 API 호출
        const projectData = {
          username: user.userInfo.memberId,
          project: {
            id: selectedProject.id,
          },
        };
        joinProject(projectData);

        return;
      }

      const newApprovalInfo = {
        memberId: user.userInfo.memberId,
        approvalType: '02', // 프로젝트 참여
        approvalUniqueKey: '02' + user.userInfo.memberId + selectedProject.id,
        approvalParamKey: Number(selectedProject.id),
        approvalParamValue: selectedProject.projectName,
        approvalItemString: selectedProject.projectName,
        apprivalTableInfo: [
          [
            { key: '프로젝트명', value: selectedProject.projectName },
            { key: '설명', value: selectedProject.description },
          ],
        ],
        afterProcessParamString: JSON.stringify({ prjSeq: Number(selectedProject.id), username: user.userInfo.memberId }),
      };
      setApprovalInfo(newApprovalInfo);

      setIsPopupOpen(false);
      setIsPayReqWizardOpen(true);
    }
  };

  const { updateUser } = useUser();
  const { mutate: joinProject } = usePutJoinProjInfo({
    onSuccess: /* async data */ async () => {
      // console.log('프로젝트 참여 성공:', data);

      openAlert({
        title: '안내',
        message: '프로젝트 참여를 완료했습니다.',
      });

      try {
        const updatedUser = await authServices.getMe();
        if (updatedUser) {
          updateUser(updatedUser);
        }
      } catch (error) {
        // console.error('사용자 데이터 갱신 실패:', error);
      }

      setIsPopupOpen(false);
    },
    onError: /* error */ () => {
      // console.error('프로젝트 참여 실패:', error);
      // 실패 알림 표시
      openAlert({
        title: '오류',
        message: '프로젝트 참여 중 오류가 발생했습니다. 다시 시도해주세요.',
      });
    },
  });

  // 단계별 컴포넌트 렌더링
  const renderStepContent = useCallback(() => {
    switch (currentStep) {
      case 1:
        return <ProJoinStep1 onNextStep={handleNextStep} />;
      case 2:
        return <ProJoinStep2 onPreviousStep={handlePreviousStep} />;
      default:
        return null;
    }
  }, [currentStep, handleNextStep, handlePreviousStep]);

  // 디버깅을 위한 콘솔 로그
  /* console.log('ProjJoinWizard 렌더링', {
    currentStep,
    selectedProject,
    isPopupOpen,
  }); */

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
            <UIPopupHeader title='프로젝트 참여' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIStepper items={stepperItems} currentStep={currentStep} direction='vertical' />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleJoin} disabled={currentStep !== 2}>
                    참여
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
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
