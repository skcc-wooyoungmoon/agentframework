import React, { useCallback, useEffect, useState } from 'react';

import { useAtomValue, useSetAtom } from 'jotai';

import { ideCreWizardAtom, resetIdeCreWizardAtom } from '@/stores/home/webide/ideCreWizard.atoms';

import { IdeCreateStep1Popup } from './IdeCreateStep1Popup';
import { IdeCreateStep2Popup } from './IdeCreateStep2Popup';
import { IdeCreateStep3Popup } from './IdeCreateStep3Popup';
import { IdeCreateStep4Popup } from './IdeCreateStep4Popup';
import { useModal } from '@/stores/common/modal';
import { useCheckIdeCreateAvailable, useCreateIde } from '@/services/home';
import { useUser } from '@/stores';
import { PayReqWizard } from '@/pages/common/PayReqWizrad.tsx';

/** IdeCreateWizard Props 타입 */
interface IdeCreateWizardProps {
  /** 위자드 표시 여부 */
  isOpen: boolean;
  /** 위자드 닫기 핸들러 */
  onClose: () => void;
  /** 생성 완료 시 콜백 */
  onComplete?: () => void;
}

/**
 * IDE 생성 위자드 컨테이너 컴포넌트
 * Step1~Step4를 관리하며 네비게이션을 제어
 */
export const IdeCreateWizard: React.FC<IdeCreateWizardProps> = ({ isOpen, onClose, onComplete }) => {
  // 현재 스텝 (1~4)
  const [currentStep, setCurrentStep] = useState(1);
  const [isPayReqWizardOpen, setIsPayReqWizardOpen] = useState(false);
  const [approvalInfo, setApprovalInfo] = useState<{
    memberId: string;
    approvalType: string;
    approvalUniqueKey?: string;
    approvalParamKey?: number;
    approvalParamValue?: string;
    approvalItemString: string;
    afterProcessParamString: string;
    approvalSummary?: string;
  }>({
    memberId: '',
    approvalType: '',
    approvalItemString: '',
    afterProcessParamString: '',
  });

  // 위자드 데이터 및 초기화 함수
  const wizardData = useAtomValue(ideCreWizardAtom);
  const resetWizardData = useSetAtom(resetIdeCreWizardAtom);

  /**
   * 팝업이 열릴 때마다 데이터 및 스텝 초기화
   */
  useEffect(() => {
    if (isOpen) {
      setCurrentStep(1);
      setIsPayReqWizardOpen(false);
      resetWizardData();
    }
  }, [isOpen, resetWizardData]);

  /**
   * 위자드 닫기 및 초기화
   */
  const handleClose = useCallback(() => {
    setCurrentStep(1);
    setIsPayReqWizardOpen(false);
    resetWizardData();
    onClose();
  }, [onClose, resetWizardData]);

  /**
   * 간편결재 위자드 닫기
   */
  const handlePayReqWizardClose = useCallback(() => {
    setIsPayReqWizardOpen(false);
    onClose();
  }, [onClose]);

  /**
   * 다음 스텝으로 이동
   */
  const handleNext = useCallback(() => {
    setCurrentStep(prev => Math.min(prev + 1, 4));
  }, []);

  /**
   * 이전 스텝으로 이동
   */
  const handlePrev = useCallback(() => {
    setCurrentStep(prev => Math.max(prev - 1, 1));
  }, []);

  /**
   * 생성 완료 핸들러
   */
  // ✅ useCreateIde hook 사용
  const { mutateAsync: createIde } = useCreateIde();
  const { refetch: checkCreateAvailable } = useCheckIdeCreateAvailable({ ideType: wizardData.selectedImageType }, { enabled: false });
  const { openAlert } = useModal();
  const { user } = useUser();
  const handleCreate = useCallback(async () => {
    // 팝업 1,2,3,4 에서 선택한 모든 값 출력
    // console.log('=== IDE 생성 데이터 확인 ===');
    // console.log('Step 1 (프로젝트):', wizardData.selectedProjectIds);
    // console.log('Step 2 (이미지):', wizardData.selectedImageId);
    // console.log('Step 2 (이미지타입):', wizardData.selectedImageType);
    // console.log('Step 3 (DW계정 사용여부):', wizardData.dwAccountUsage);
    // if (wizardData.dwAccountUsage === 'use') {
    //   console.log('Step 3 (DW계정 유형):', wizardData.dwAccountType === '1' ? '사용자 계정' : '서비스 계정');
    //   if (wizardData.dwAccountType === '1') {
    //     console.log('Step 3 (선택된 DW계정):', wizardData.selectedDwAccountId);
    //   } else {
    //     console.log('Step 3 (서비스 계정 입력값):', wizardData.selectedDwAccountId);
    //   }
    // }
    // console.log('Step 4 (자원 프리셋):', wizardData.resourcePreset);
    // console.log('Step 4 (CPU):', wizardData.cpuValue);
    // console.log('Step 4 (Memory):', wizardData.memoryValue);
    // console.log('============================');

    // 생성 가능한지 검증
    const { data: isAvailable } = await checkCreateAvailable();
    if (isAvailable === false) {
      openAlert({
        title: '알림',
        message: '생성 가능한 IDE 개수를 초과했습니다.',
        confirmText: '확인',
      });
      return;
    }

    const publicProject = user.projectList.find(el => el.prjSeq == '-999');
    const isPortalAdmin = publicProject?.prjRoleSeq === '-199';

    // 기본프리셋 생성요청이거나 포탈관리자의 생성요청은 즉시 처리
    if (wizardData.resourcePreset === 'preset-small' || isPortalAdmin) {
      await createIde({
        prjSeq: wizardData.selectedProjectIds,
        userId: user?.userInfo?.memberId || '',
        imgUuid: wizardData.selectedImageId,
        ideType: wizardData.selectedImageType,
        dwAccountUsed: wizardData.dwAccountUsage === 'use',
        dwAccount: wizardData.selectedDwAccountId,
        cpu: Number(wizardData.cpuValue),
        memory: Number(wizardData.memoryValue),
      });

      // console.log('IDE 생성 완료');

      openAlert({
        title: '완료',
        message: 'IDE가 생성이 완료되었습니다.\n지금부터 7일간 선택하신 버전과 설정으로 개발환경을\n사용하실 수 있습니다.',
        confirmText: '확인',
        onConfirm: () => {
          // 성공 시 상위 컴포넌트에 알림
          onComplete?.();
          // 팝업 닫기
          onClose?.();
        },
      });
    } else {
      // 이외 간편결재 요청
      const presetName = wizardData.resourcePreset === 'preset-medium' ? 'Medium' : 'Large';

      const newApprovalInfo = {
        memberId: user.userInfo.memberId,
        approvalType: '08', // IDE 생성
        approvalUniqueKey: '08' + user.userInfo.memberId + wizardData.selectedImageId,
        approvalItemString: presetName,
        afterProcessParamString: JSON.stringify({
          prjSeq: wizardData.selectedProjectIds,
          userId: user?.userInfo?.memberId || '',
          imgUuid: wizardData.selectedImageId,
          ideType: wizardData.selectedImageType,
          dwAccountUsed: wizardData.dwAccountUsage === 'use',
          dwAccount: wizardData.selectedDwAccountId,
          cpu: Number(wizardData.cpuValue),
          memory: Number(wizardData.memoryValue),
        }),
        apprivalTableInfo: [
          [
            {
              key: '자원 프리셋',
              value: `${wizardData.resourcePreset === 'preset-medium' ? 'Medium' : 'Large'} (CPU : ${wizardData.cpuValue}Core, Memory : ${wizardData.memoryValue}GiB)`,
            },
          ],
        ],
      };

      setApprovalInfo(newApprovalInfo);
      setIsPayReqWizardOpen(true);
    }
  }, [onComplete, wizardData, user, createIde, openAlert]);

  // 위자드가 닫혀있으면 렌더링하지 않음
  if (!isOpen) {
    return null;
  }

  return (
    <>
      {!isPayReqWizardOpen && currentStep === 1 && <IdeCreateStep1Popup isOpen={true} onClose={handleClose} onNext={handleNext} />}
      {!isPayReqWizardOpen && currentStep === 2 && <IdeCreateStep2Popup isOpen={true} onClose={handleClose} onPrev={handlePrev} onNext={handleNext} />}
      {!isPayReqWizardOpen && currentStep === 3 && <IdeCreateStep3Popup isOpen={true} onClose={handleClose} onPrev={handlePrev} onNext={handleNext} />}
      {!isPayReqWizardOpen && currentStep === 4 && <IdeCreateStep4Popup isOpen={true} onClose={handleClose} onPrev={handlePrev} onCreate={handleCreate} />}
      {isPayReqWizardOpen && <PayReqWizard isOpen={isPayReqWizardOpen} onClose={handlePayReqWizardClose} onComplete={onComplete} approvalInfo={approvalInfo} />}
    </>
  );
};
