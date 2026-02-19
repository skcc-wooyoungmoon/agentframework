import { useCallback } from 'react';
import { useAtom } from 'jotai';
import { useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { nodesAtom, keyTableAtom, selectedLLMRepoAtom } from '@/components/agents/builder/atoms';
import { useGraphActions } from '@/components/agents/builder/hooks';
import { useToast } from '@/hooks/common/toast/useToast';
import { useModal } from '@/stores/common/modal/useModal';
import { useDeployAgent } from '@/stores/deploy/useDeployAgent';
import { validateGraphForSave } from '@/components/agents/builder/pages/graph/utils';
import type { Agent } from '@/components/agents/builder/types/Agents';

interface UseGraphSaveProps {
  data: Agent | null;
  setUnsavedChanges: (value: boolean) => void;
  setDeployStep: (step: number) => void;
  setIsChatVisible: React.Dispatch<React.SetStateAction<boolean>>;
  isChatVisible?: boolean;
}

interface UseGraphSaveReturn {
  handleSave: (skipValidation?: boolean, silent?: boolean) => Promise<boolean>;
  handleChat: () => Promise<void>;
  handleDeploy: () => Promise<void>;
  handleDescription: () => Promise<void>;
}

export const useGraphSave = ({ data, setUnsavedChanges, setDeployStep, setIsChatVisible, isChatVisible = false }: UseGraphSaveProps): UseGraphSaveReturn => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { openAlert } = useModal();
  const { toast } = useToast();
  const { updateDeployData } = useDeployAgent();

  const [nodes] = useAtom(nodesAtom);
  const [keyTableList] = useAtom(keyTableAtom);
  const [selectedLLMRepo] = useAtom(selectedLLMRepoAtom);

  const { saveAgent } = useGraphActions();
  const handleSave = useCallback(
    async (skipValidation = false, silent = false): Promise<boolean> => {
      if (skipValidation) {
        const success = await saveAgent(keyTableList);
        if (success) {
          setUnsavedChanges(false);

          if (data?.id) {
            queryClient.invalidateQueries({ queryKey: ['agent-builder', data.id], refetchType: 'none' });
            queryClient.invalidateQueries({ queryKey: ['agent-builder-list'], refetchType: 'none' });

            queryClient.refetchQueries({
              queryKey: ['agent-builder', data.id],
              type: 'active',
            });
          }

          if (!silent) {
            toast.success('저장이 완료되었습니다.');
          }
          return true;
        }
        return false;
      }

      const { isValid, errors } = validateGraphForSave(nodes, selectedLLMRepo);

      if (!isValid) {
        openAlert({
          title: '안내',
          message: `필수 항목을 모두 입력해주세요.\n\n${errors.join('\n')}`,
          confirmText: '확인',
        });
        return false;
      }

      const success = await saveAgent(keyTableList);

      if (success) {
        setUnsavedChanges(false);

        if (data?.id) {
          queryClient.invalidateQueries({ queryKey: ['agent-builder', data.id], refetchType: 'none' });
          queryClient.invalidateQueries({ queryKey: ['agent-builder-list'], refetchType: 'none' });

          queryClient.refetchQueries({
            queryKey: ['agent-builder', data.id],
            type: 'active',
          });
        }

        toast.success('저장이 완료되었습니다.');
        return true;
      }
      return false;
    },
    [nodes, keyTableList, selectedLLMRepo, data?.id, saveAgent, setUnsavedChanges, queryClient, toast, openAlert]
  );

  const handleChat = useCallback(async (): Promise<void> => {
    const saveSuccess = await handleSave();

    if (saveSuccess) {
      // 채팅창이 이미 열려있으면 닫히지 않게 함 (항상 열린 상태로 유지)
      if (!isChatVisible) {
        setIsChatVisible(true);
      }
    }
  }, [handleSave, setIsChatVisible, isChatVisible]);

  const handleDeploy = useCallback(async (): Promise<void> => {
    const graphId = data?.id || '';

    try {
      const saveSuccess = await saveAgent(keyTableList);

      if (saveSuccess) {
        toast.success('저장이 완료되었습니다.');
        updateDeployData({
          targetId: graphId,
          targetType: 'agent_graph',
        });

        setDeployStep(2);
      }
    } catch (error) {
      openAlert({
        title: '안내',
        message: '저장에 실패했습니다. 배포를 진행할 수 없습니다.',
        confirmText: '확인',
      });
    }
  }, [data?.id, keyTableList, saveAgent, updateDeployData, setDeployStep, toast, openAlert]);

  const handleDescription = useCallback(async (): Promise<void> => {
    if (data?.id) {
      const saveSuccess = await handleSave();

      if (saveSuccess) {
        navigate(`/test/secret/${data.id}`);
      }
    } else {
      openAlert({
        title: '안내',
        message: '에이전트 ID가 없습니다.',
        confirmText: '확인',
      });
    }
  }, [data?.id, handleSave, navigate, openAlert]);

  return {
    handleSave,
    handleChat,
    handleDeploy,
    handleDescription,
  };
};
