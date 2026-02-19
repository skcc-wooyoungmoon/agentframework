import { useEffect, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input/UIInput/component';
import { UITextArea2 } from '@/components/UI/molecules/input/UITextArea2/component';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useUpdateAgentApp } from '@/services/deploy/agent/agentDeploy.services';
import { useModal } from '@/stores/common/modal';

interface DeployAgentEditPopupPageProps {
  appId: string;
  name: string;
  description: string;
  isOpen: boolean;
  onClose: () => void;
  onUpdateSuccess: () => void;
}

export function DeployAgentEditPopupPage({ appId, name, description, isOpen, onClose, onUpdateSuccess }: DeployAgentEditPopupPageProps) {
  const { openConfirm, openAlert } = useModal();

  const [deployName, setDeployName] = useState(name || ''); // 배포명
  const [datasetDescription, setDatasetDescription] = useState(description || ''); // 설명

  // props가 변경될 때 state 업데이트 (팝업이 열릴 때)
  useEffect(() => {
    if (isOpen) {
      setDeployName(name || '');
      setDatasetDescription(description || '');
    }
  }, [isOpen, name, description]);

  /**
   * 닫기 버튼 클릭
   */
  const handleClose = () => {
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        onClose();
      },
      onCancel: () => {},
    });
  };

  /**
   * 에이전트 앱 수정 요청
   */
  const { mutate: updateAgentApp, isPending } = useUpdateAgentApp({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '수정사항이 저장되었습니다.',
        onConfirm: () => {
          onUpdateSuccess?.();
          handleClose();
        },
      });
    },
    onError: (error: any) => {
      // console.error('에이전트 배포 수정 실패:', error);
      openAlert({
        title: '실패',
        message: error?.response?.data?.message || error?.message || '에이전트 배포 수정에 실패하였습니다.',
      });
    },
  });

  /**
   * 필수값 검증 함수
   */
  const isFormValid = () => {
    // 설명은 필수가 아니므로 항상 true 반환 (배포명은 disabled이므로 검증 불필요)
    return true;
  };

  /**
   * 변경사항 감지 함수
   */
  const hasChanges = () => {
    // 설명 변경 확인 (배포명은 disabled이므로 변경 불가)
    if (datasetDescription.trim() !== (description || '').trim()) {
      return true;
    }
    return false;
  };

  /**
   * 저장 버튼 클릭 시 호출
   */
  const handleSave = async () => {
    // 변경사항이 없으면 alert만 표시
    if (!hasChanges()) {
      await openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
      });
      return;
    }

    updateAgentApp({
      appId: appId,
      name: deployName,
      description: datasetDescription,
    });
  };

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader
              title={
                <>
                  에이전트 배포
                  <br />
                  정보 수정
                </>
              }
              description=''
              position='left'
            />

            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_UPDATE} className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button
                    auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_UPDATE}
                    className='btn-tertiary-blue'
                    style={{ width: '80px' }}
                    disabled={isPending || !isFormValid()}
                    onClick={handleSave}
                  >
                    저장
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 역할명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  배포명
                </UITypography>
                <UIInput.Text placeholder='배포명 입력' value={deployName} onChange={e => setDeployName(e.target.value)} disabled />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={datasetDescription} placeholder='설명 입력' onChange={e => setDatasetDescription(e.target.value.slice(0, 100))} maxLength={100} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
}
