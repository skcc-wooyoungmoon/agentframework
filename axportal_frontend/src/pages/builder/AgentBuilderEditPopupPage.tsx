import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useUpdateAgentInfo } from '@/services/agent/builder2/agentBuilder.services';
import { useModal } from '@/stores/common/modal';
import { useEffect, useRef, useState } from 'react';

interface AgentBuilderEditPopupPageProps {
  agentId?: string;
  agentName?: string;
  agentDescription?: string;
  isOpen: boolean;
  onClose: () => void;
  onUpdateSuccess: () => void;
}

export function AgentBuilderEditPopupPage({ agentId, agentName, agentDescription, isOpen, onClose, onUpdateSuccess }: AgentBuilderEditPopupPageProps) {
  const [editName, setEditName] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const { openAlert, openConfirm } = useModal();

  const originalValuesRef = useRef({ name: '', description: '' });

  const { mutate: updateAgentInfo, isPending } = useUpdateAgentInfo({
    onSuccess: async () => {
      openAlert({
        title: '완료',
        message: '수정사항이 저장되었습니다.',
        onConfirm: () => {
          onUpdateSuccess();
          onClose();
        },
      });
    },
  });

  // 팝업이 열릴 때마다 props로 상태 초기화
  useEffect(() => {
    if (isOpen) {
      const initialName = (agentName ?? '').trim();
      const initialDescription = (agentDescription ?? '').trim();

      setEditName(initialName);
      setEditDescription(initialDescription);
      originalValuesRef.current = {
        name: initialName,
        description: initialDescription,
      };

      // UIInput(Text)의 내부 이벤트로 즉시 값이 비워지는 케이스가 있어 한 틱 뒤에 한 번 더 보강
      requestAnimationFrame(() => {
        setEditName(prev => (prev === initialName ? prev : initialName));
        setEditDescription(prev => (prev === initialDescription ? prev : initialDescription));
      });
    }
  }, [isOpen, agentName, agentDescription]);

  const hasChanges = () => {
    const trimmedName = editName.trim();
    const trimmedDescription = editDescription.trim();

    return trimmedName !== originalValuesRef.current.name || trimmedDescription !== originalValuesRef.current.description;
  };

  const handleCloseWithConfirm = async () => {
    await openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        onClose();
      },
    });
  };

  const handleSave = async () => {
    const trimmedName = editName.trim();
    const trimmedDescription = editDescription.trim();

    if (!hasChanges()) {
      openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
      });
      return;
    }

    updateAgentInfo({ id: agentId!, name: trimmedName, description: trimmedDescription });
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleCloseWithConfirm}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='에이전트 정보 수정' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCloseWithConfirm}>
                  취소
                </Button>
                <Button
                  auth={AUTH_KEY.AGENT.AGENT_BUILDER_UPDATE}
                  className='btn-tertiary-blue'
                  style={{ width: '80px' }}
                  disabled={isPending || editName.trim() === ''}
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
      <section className='section-popup-content'>
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                이름
              </UITypography>
              <UIInput.Text value={editName} onChange={e => setEditName(e.target.value)} placeholder='이름 입력' maxLength={50} />
            </UIFormField>
          </UIArticle>

          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                설명
              </UITypography>
              <UITextArea2 value={editDescription} onChange={e => setEditDescription(e.target.value)} maxLength={100} />
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
}
