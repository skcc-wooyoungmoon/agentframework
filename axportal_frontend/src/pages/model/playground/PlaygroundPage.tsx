import { UIPageHeader } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { Model } from '@/components/model/playground/Model.tsx';
import { Prompt } from '@/components/model/playground/Prompt.tsx';
import { useModelSelectModal } from '@/hooks/model/useModelSelectModal';
import { usePromptData } from '@/hooks/model/usePromptData.ts';
import { usePromptSelectModal } from '@/hooks/model/usePromptSelectModal';
import type { ModelParameters, ModelPlaygroundChatResponse, PlaygroundModel, PromptData } from '@/services/model/playground/types';
import type { InfPromptMessageType } from '@/services/prompt/inference/types';
import { useCallback, useEffect, useRef, useState } from 'react';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { useCreateModelPlaygroundChat } from '@/services/model/playground/playground.services';
import { useModal } from '@/stores/common/modal';

export const PlaygroundPage = () => {
  const { openAlert } = useModal();

  const prevPromptDataListRef = useRef<PromptData[]>([]);

  const [selectedPromptUuids, setSelectedPromptUuids] = useState<string[]>([]);

  const [selectedModels, setSelectedModels] = useState<PlaygroundModel[]>([]);

  // 프롬프트 입력 텍스트 상태 관리
  const [systemPromptText, setSystemPromptText] = useState<string>('');
  const [userPromptText, setUserPromptText] = useState<string>('');

  // 플레이그라운드 실행 결과 상태 관리
  const [playgroundResults, setPlaygroundResults] = useState<Record<string, ModelPlaygroundChatResponse | null>>({});
  const [playgroundErrors, setPlaygroundErrors] = useState<Record<string, string>>({});
  const [isExecuting, setIsExecuting] = useState<Record<string, boolean>>({});

  // 프롬프트 데이터 가져오기
  const { promptDataList } = usePromptData(selectedPromptUuids);

  // 플레이그라운드 채팅 API 훅
  const { mutateAsync: createPlaygroundChat } = useCreateModelPlaygroundChat();

  // 프롬프트 선택 모달 훅
  const { openPromptSelectModal } = usePromptSelectModal({
    onPromptSelect: (promptUuids: string[]) => {
      setSelectedPromptUuids(() => promptUuids);
    },
  });

  // 모델 선택 모달 훅
  const { openModelSelectModal } = useModelSelectModal({
    onModelSelect: (selectedModels: PlaygroundModel[]) => {
      setSelectedModels(prev => [...prev, ...selectedModels]);
    },
  });

  const getPromptDataHash = (promptDataList: PromptData[]) => {
    return promptDataList
      .filter(p => p.messages && p.messages.length > 0) // messages가 있는 것만
      .map(p => `${p.promptUuid}-${p.messages?.length || 0}`) // 직접 undefined 체크
      .join('|');
  };

  // promptDataList가 변경될 때 system과 user 프롬프트를 분리하여 설정
  useEffect(() => {
    const currentHash = getPromptDataHash(promptDataList);
    const prevHash = getPromptDataHash(prevPromptDataListRef.current);
    if (promptDataList && promptDataList.length > 0 && currentHash !== prevHash) {
      prevPromptDataListRef.current = promptDataList;

      let systemPrompt = '';
      let userPrompt = '';

      // 모든 프롬프트 데이터에서 메시지를 수집
      promptDataList.forEach(promptData => {
        if (promptData.messages) {
          promptData.messages.forEach((message: InfPromptMessageType) => {
            if (message.mtype === 1) {
              systemPrompt += (systemPrompt ? '\n\n' : '') + message.message;
            } else if (message.mtype === 2) {
              userPrompt += (userPrompt ? '\n\n' : '') + message.message;
            }
          });
        }
      });

      setSystemPromptText(systemPrompt);
      setUserPromptText(userPrompt);
    } else {
      // 프롬프트가 선택되지 않았을 때 초기화
      // setSystemPromptText('');
      // setUserPromptText('');
    }
  }, [promptDataList]);

  // 모델 삭제 함수
  const handleDeleteModel = (instanceId: string) => {
    setSelectedModels(prev => {
      return prev.filter(model => model.instanceId !== instanceId);
    });

    // 삭제된 모델의 실행 결과, 에러, 실행 상태도 함께 제거
    setPlaygroundResults(prev => {
      const newResults = { ...prev };
      delete newResults[instanceId];
      return newResults;
    });
    setPlaygroundErrors(prev => {
      const newErrors = { ...prev };
      delete newErrors[instanceId];
      return newErrors;
    });
    setIsExecuting(prev => {
      const newExecuting = { ...prev };
      delete newExecuting[instanceId];
      return newExecuting;
    });
  };

  // 모델 파라미터 업데이트 함수
  const handleUpdateModelParameters = (instanceId: string, parameters: ModelParameters) => {
    setSelectedModels(prev => prev.map(model => (model.instanceId === instanceId ? { ...model, parameters } : model)));
  };

  // 개별 모델 실행 함수
  const handleExecuteSingleModel = async (model: PlaygroundModel) => {
    if (!systemPromptText.trim()) {
      await openAlert({
        message: '시스템 프롬프트를 입력해주세요.',
      });
      return;
    }

    if (!userPromptText.trim()) {
      await openAlert({
        message: '유저 프롬프트를 입력해주세요.',
      });
      return;
    }

    // 해당 모델의 실행 상태를 true로 설정
    setIsExecuting(prev => ({
      ...prev,
      [model.instanceId]: true,
    }));

    // 해당 모델의 이전 결과 및 에러 초기화
    setPlaygroundResults(prev => {
      const newResults = { ...prev };
      delete newResults[model.instanceId];
      return newResults;
    });
    setPlaygroundErrors(prev => {
      const newErrors = { ...prev };
      delete newErrors[model.instanceId];
      return newErrors;
    });

    try {
      const requestData = {
        model: model.name,
        systemPrompt: systemPromptText || undefined,
        userPrompt: userPromptText,
        maxTokens: model.parameters?.maxTokensChecked ? model.parameters?.maxTokens : undefined,
        temperature: model.parameters?.temperatureChecked ? model.parameters?.temperature : undefined,
        topP: model.parameters?.topPChecked ? model.parameters?.topP : undefined,
        frequencyPenalty: model.parameters?.frequencyPenaltyChecked ? model.parameters?.frequencyPenalty : undefined,
        presencePenalty: model.parameters?.presencePenaltyChecked ? model.parameters?.presencePenalty : undefined,
        stream: false,
        servingId: model.servingId,
      };

      const response = await createPlaygroundChat(requestData);

      // 응답에 error 필드가 있으면 에러로 처리
      if (response.data?.error && typeof response.data.error === 'string') {
        setPlaygroundErrors(prev => ({
          ...prev,
          [model.instanceId]: response.data.error!,
        }));
      } else {
        // 에러 상태 제거
        setPlaygroundErrors(prev => {
          const { [model.instanceId]: _, ...rest } = prev;
          return rest;
        });
      }

      setPlaygroundResults(prev => ({
        ...prev,
        [model.instanceId]: response.data,
      }));
    } catch (error) {
      const errorMessage = (error as any)?.message || '알 수 없는 오류가 발생했습니다.';

      setPlaygroundErrors(prev => ({
        ...prev,
        [model.instanceId]: errorMessage,
      }));

      // 결과 상태 제거
      setPlaygroundResults(prev => {
        const newResults = { ...prev };
        delete newResults[model.instanceId];
        return newResults;
      });
    } finally {
      // 해당 모델의 실행 상태를 false로 설정
      setIsExecuting(prev => ({
        ...prev,
        [model.instanceId]: false,
      }));
    }
  };

  // 플레이그라운드 실행 함수
  const handleExecutePlayground = useCallback(async () => {
    if (selectedModels.length === 0) {
      await openAlert({
        message: '실행할 모델을 선택해주세요.',
      });
      return;
    }

    if (!systemPromptText.trim()) {
      await openAlert({
        message: '시스템 프롬프트를 입력해주세요.',
      });
      return;
    }

    if (!userPromptText.trim()) {
      await openAlert({
        message: '유저 프롬프트를 입력해주세요.',
      });
      return;
    }

    // 각 모델별로 실행 상태 초기화
    const initialExecutingState: Record<string, boolean> = {};
    selectedModels.forEach(model => {
      initialExecutingState[model.instanceId] = true;
    });
    setIsExecuting(initialExecutingState);
    setPlaygroundResults({});
    setPlaygroundErrors({});

    // 각 모델별로 병렬 실행
    const promises = selectedModels.map(async model => {
      try {
        const requestData = {
          model: model.name,
          systemPrompt: systemPromptText || undefined,
          userPrompt: userPromptText,
          maxTokens: model.parameters?.maxTokensChecked ? model.parameters?.maxTokens : undefined,
          temperature: model.parameters?.temperatureChecked ? model.parameters?.temperature : undefined,
          topP: model.parameters?.topPChecked ? model.parameters?.topP : undefined,
          frequencyPenalty: model.parameters?.frequencyPenaltyChecked ? model.parameters?.frequencyPenalty : undefined,
          presencePenalty: model.parameters?.presencePenaltyChecked ? model.parameters?.presencePenalty : undefined,
          stream: false,
          servingId: model.servingId,
        };

        const response = await createPlaygroundChat(requestData);

        // 응답에 error 필드가 있으면 에러로 처리
        if (response.data?.error && typeof response.data.error === 'string') {
          setPlaygroundErrors(prev => ({
            ...prev,
            [model.instanceId]: response.data.error!,
          }));
        } else {
          // 에러 상태 제거
          setPlaygroundErrors(prev => {
            const { [model.instanceId]: _, ...rest } = prev;
            return rest;
          });
        }

        setPlaygroundResults(prev => ({
          ...prev,
          [model.instanceId]: response.data,
        }));
      } catch (error) {
        // console.error(`Model ${model.instanceId} execution error:`, error);

        const errorMessage = (error as any)?.message || '알 수 없는 오류가 발생했습니다.';

        setPlaygroundErrors(prev => ({
          ...prev,
          [model.instanceId]: errorMessage,
        }));

        // 결과 상태 제거
        setPlaygroundResults(prev => {
          const newResults = { ...prev };
          delete newResults[model.instanceId];
          return newResults;
        });
      } finally {
        // 각 모델의 실행 상태를 false로 설정
        setIsExecuting(prev => ({
          ...prev,
          [model.instanceId]: false,
        }));
      }
    });

    await Promise.all(promises);
  }, [selectedModels, systemPromptText, userPromptText, createPlaygroundChat, openAlert, setIsExecuting, setPlaygroundResults, setPlaygroundErrors]);

  // Ctrl+Enter 키 조합으로 플레이그라운드 실행
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      // Ctrl+Enter 또는 Cmd+Enter 조합 감지
      if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
        // 실행 중이 아닐 때만 실행
        if (!Object.values(isExecuting).some(executing => executing)) {
          event.preventDefault();
          handleExecutePlayground();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isExecuting, handleExecutePlayground]);

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='플레이 그라운드'
          description={[
            '시스템 프롬프트와 유저 프롬프트를 자유롭게 구성하고, 개발망에 배포된 모델로 응답을 바로 확인할 수 있습니다.',
            '등록한 프롬프트와 다양한 모델을 조합해 응답 품질을 비교해 보세요.',
          ]}
          actions={
            <>
              <Button className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-prompt', children: '' }} onClick={openModelSelectModal}>
                모델 추가
              </Button>
              <Button className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-prompt', children: '' }} onClick={openPromptSelectModal}>
                추론 프롬프트
              </Button>
              <Button
                auth={AUTH_KEY.MODEL.PLAYGROUND_USE}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-page', children: '' }}
                onClick={handleExecutePlayground}
                disabled={Object.values(isExecuting).some(executing => executing)}
              >
                {Object.values(isExecuting).some(executing => executing) ? '실행 중...' : '실행하기'}
              </Button>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='play-wrap'>
            {/* 시스템 프롬프트 : 왼쪽 1개 고정영역 */}
            <div className='play-grid-fix'>
              {/* Card 영역 */}
              <Prompt type='system' value={systemPromptText} onChange={setSystemPromptText} />
              {/* Card 영역 */}
              <Prompt type='user' value={userPromptText} onChange={setUserPromptText} />
            </div>

            {/* GIP : 오른쪽 2개 column */}
            <div className='play-grid-wrap'>
              {selectedModels.map(model => (
                <Model
                  key={model.instanceId}
                  model={model}
                  onDelete={handleDeleteModel}
                  onUpdateParameters={handleUpdateModelParameters}
                  onExecute={handleExecuteSingleModel}
                  result={playgroundResults[model.instanceId]}
                  error={playgroundErrors[model.instanceId]}
                  isExecuting={isExecuting[model.instanceId] || false}
                />
              ))}
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </>
  );
};
