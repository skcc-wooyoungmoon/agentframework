import { isChangeLLMAtom, selectedLLMRepoAtom } from '@/components/agents/builder/atoms/llmAtom';
import { CustomAccordionItem } from '@/components/agents/builder/common/Button/CustomAccordionItem';
import { LLMParametersPop } from '@/components/agents/builder/pages/modal/LLMParametersPop';
import { SelectLLMPop } from '@/components/agents/builder/pages/modal/SelectLLMPop';
import { useAuth } from '@/components/agents/builder/providers/Auth';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useModal } from '@/stores/common/modal';
import { useAtom } from 'jotai';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface llmProps {
  selectedServingName: string;
  selectedServingModel: string;
  onChange?: (selectedLLM: GetModelDeployResponse) => void;
  isReRanker?: boolean;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  nodeId?: string;
  nodeType?: string;
  readOnly?: boolean;
  onModelParametersChange?: (modelParameters: { params: Array<{ name: string; type: string; value: string }>; disabled_params: string[] }) => void;
  initialModelParameters?: { params: Array<{ name: string; type: string; value: string }>; disabled_params: string[] };
}

export const SelectLLM = ({
  selectedServingName,
  selectedServingModel,
  onChange,
  isReRanker,
  asAccordionItem = false,
  title,
  nodeId = 'default',
  nodeType,
  readOnly = false,
  onModelParametersChange,
  initialModelParameters,
}: llmProps) => {
  const [selectedLLM, setSelectedLLM] = useState<GetModelDeployResponse | null>(null);
  const { currentUser: _currentUser } = useAuth();
  const { openModal } = useModal();
  const navigate = useNavigate();
  const tempLLMRef = useRef<GetModelDeployResponse | null>(null);
  const [llmCache, setLlmCache] = useState<Record<string, any>>({});
  const [, setSelectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [, setChangeLLM] = useAtom(isChangeLLMAtom);
  const llmParametersConfirmRef = useRef<(() => void) | null>(null);

  const prevNodeIdRef = useRef<string>(nodeId);
  useEffect(() => {
    if (prevNodeIdRef.current !== nodeId) {
      prevNodeIdRef.current = nodeId;
      const savedLLM = llmCache[nodeId];
      if (savedLLM) {
        setSelectedLLM(savedLLM);
      }
    }
  }, [nodeId]);

  useEffect(() => {
    if (selectedServingName || selectedServingModel) {
      let servingIdValue = selectedServingModel || '';

      const existingLLM: Partial<GetModelDeployResponse> = {
        servingId: servingIdValue,
        name: selectedServingName || '',
        modelName: selectedServingName || '',
        type: isReRanker ? 'reranker' : 'language',
        status: 'Available',
      };

      setSelectedLLM(existingLLM as GetModelDeployResponse);
    }
  }, [selectedServingName, selectedServingModel, isReRanker]);

  const handleSelectLLM = (llm: GetModelDeployResponse) => {

    setSelectedLLM(llm);
    setLlmCache((prev: Record<string, any>) => ({
      ...prev,
      [nodeId]: llm,
    }));

    setSelectedLLMRepo(prev => ({
      ...prev,
      [nodeId]: {
        servingName: llm.name || llm.modelName || '',
        servingModel: llm.servingId || '',
      },
    }));

    setChangeLLM(true);

    if (onChange) {
      onChange(llm);
    }
  };

  const handleRemoveLLM = () => {
    setSelectedLLM(null);
    setLlmCache((prev: Record<string, any>) => ({
      ...prev,
      [nodeId]: null as any,
    }));

    setSelectedLLMRepo(prev => ({
      ...prev,
      [nodeId]: null,
    }));
    setChangeLLM(true);

    if (onChange) {
      onChange({
        name: '',
        modelName: '',
        servingId: '',
        type: '',
        status: '',
      } as GetModelDeployResponse);
    }
  };

  const handleClickLLMSettings = () => {
    if (readOnly) return;

    const modalId = `llm-parameters-${nodeId}-${Date.now()}`;
    const currentParams = initialModelParameters?.params || [];
    const currentDisabledParams = initialModelParameters?.disabled_params || [];

    const initialParameters = currentParams.map(param => ({
      key: param.name || '',
      type: param.type ? param.type.toLowerCase() : '',
      value: param.value || '',
    }));

    const initialInactiveParameters = currentDisabledParams.map(param => ({
      key: param,
    }));

    const handleParametersConfirm = (
      parameters: Array<{ key: string; type: string; value: string }>,
      inactiveParameters: Array<{ key: string }>
    ) => {
      const params = parameters
        .filter(p => p.key.trim() !== '' && p.type.trim() !== '' && p.value.trim() !== '')
        .map(p => ({
          name: p.key,
          type: p.type ? p.type.toLowerCase() : p.type,
          value: p.value,
        }));

      const disabled_params = inactiveParameters
        .filter(p => p.key.trim() !== '')
        .map(p => p.key);

      if (onModelParametersChange) {
        onModelParametersChange({
          params,
          disabled_params,
        });
      }
    };

    llmParametersConfirmRef.current = null;

    openModal({
      title: 'LLM 파라미터',
      type: 'medium',
      body: (
        <LLMParametersPop
          modalId={modalId}
          initialParameters={initialParameters}
          initialInactiveParameters={initialInactiveParameters}
          onConfirm={handleParametersConfirm}
          onConfirmRef={llmParametersConfirmRef}
        />
      ),
      showFooter: true,
      confirmText: '확인',
      onConfirm: () => {
        if (llmParametersConfirmRef.current) {
          llmParametersConfirmRef.current();
        }
      },
    });
  };

  const handleClickSearch = () => {
    if (readOnly) {
      return;
    }

    tempLLMRef.current = null;

    const handleLLMSelection = (llm: GetModelDeployResponse) => {
      tempLLMRef.current = llm;
    };

    const modalId = `select-llm-${nodeId}-${Date.now()}`;
    openModal({
      title: isReRanker ? 'Re-Rank 모델 선택' : 'LLM 선택',
      type: 'large',
      body: <SelectLLMPop modalId={modalId} isReRanker={isReRanker} onSelectLLM={handleLLMSelection} />,
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        if (tempLLMRef.current) {
          handleSelectLLM(tempLLMRef.current);
          tempLLMRef.current = null;
        }
      },
      onCancel: () => {
        setSelectedLLM(null);
        tempLLMRef.current = null;
      },
    });
  };

  const content = (
    <>
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {selectedLLM && selectedLLM.name && selectedLLM.name !== '' && (selectedLLM.name || selectedLLM.modelName) ? (
            <div className='flex items-center gap-2 flex-1'>
              <button
                className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
                title={selectedLLM.name || selectedLLM.modelName || '-'}
                onClick={e => {
                  e.stopPropagation();
                  if (selectedLLM.servingId) {
                    navigate(`/deploy/modelDeploy/${selectedLLM.servingId}`);
                  }
                }}
              >
                {selectedLLM.modelName || selectedLLM.name}
              </button>
              <button onClick={handleRemoveLLM} className='btn-icon btn btn-sm btn-light text-primary btn-node-action ml-auto' title='삭제'>
                <img alt='ico-system-24-outline-gray-trash' className='w-[20px] h-[20px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
              </button>
            </div>
          ) : (
            <div className='h-[36px] leading-[36px] text-sm text-gray-500'>{isReRanker ? 'Reranker를 선택해주세요' : 'LLM을 선택해주세요'}</div>
          )}
        </div>
      </div>
      {!readOnly && (
        <div className='flex gap-3 justify-end'>
          {nodeType === 'agent__generator' && (
            <button
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                handleClickLLMSettings();
              }}
              className='bg-[#E7EDF6] color-[#242A34] font-semibold py-2 px-3 rounded-lg border-0 mt-3'
            >
              LLM 설정
            </button>
          )}
          <button onClick={handleClickSearch} className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200  mt-3'>
            검색
          </button>
        </div>
      )}
    </>
  );

  if (asAccordionItem) {
    const accordionTitle = (
      <>
        {title}
        {selectedLLM && selectedLLM.modelName && <span className='ml-2 text-gray-500 font-medium'>{selectedLLM.modelName}</span>}
      </>
    );
    return (
      <CustomAccordionItem title={accordionTitle} defaultOpen={false}>
        {content}
      </CustomAccordionItem>
    );
  }

  return content;
};