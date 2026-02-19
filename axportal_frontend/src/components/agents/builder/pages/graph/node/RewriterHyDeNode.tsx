import { isChangePromptAtom, selectedPromptIdRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { selectedLLMRepoAtom, isChangeLLMAtom } from '@/components/agents/builder/atoms/llmAtom';
import { clearModelEventAtom, isClearModelAtom } from '@/components/agents/builder/atoms/clearModelAtom';
import { Accordion } from '@/components/agents/builder/common/accordion/Accordion.tsx';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/agents/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/agents/builder/pages/graph/contents/SelectPrompt.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem, type RewriterHyDEDataSchema } from '@/components/agents/builder/types/Agents';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useModal } from '@/stores/common/modal/useModal';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  nodeName: string;
  description: string;
  prompt: string;
  servingName: string;
  servingModel: string;
  hasOriginalQuery: boolean;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const RewriterHyDeNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const schemaData: RewriterHyDEDataSchema = data as RewriterHyDEDataSchema;
  
  const [selectedPromptIdRepoHyde, setSelectedPromptIdRepoHyde] = useAtom(selectedPromptIdRepoAtom);

  const [formState, setFormState] = useState<NodeFormState>(() => {
    const servingNameFromData = (data.serving_name as string) || '';
    const servingNameFromSchema =
      (typeof schemaData.query_rewriter !== 'undefined' && schemaData.query_rewriter !== null ? schemaData.query_rewriter.llm_chain.llm_config.serving_name : '') || '';
    const servingModelFromData = (data.serving_model as string) || '';
    const servingModelFromSchema =
      (typeof schemaData.query_rewriter !== 'undefined' && schemaData.query_rewriter !== null
        ? ((schemaData.query_rewriter.llm_chain.llm_config as any).serving_model as string) || ''
        : '') || '';

    const finalServingName = servingNameFromData || servingNameFromSchema;
    const finalServingModel = servingModelFromData || servingModelFromSchema;
    
    // 프롬프트 ID 가져오기 (query_rewriter.llm_chain.prompt에 저장된 ID)
    const promptFromSchema = typeof schemaData.query_rewriter !== 'undefined' && schemaData.query_rewriter !== null 
      ? schemaData.query_rewriter.llm_chain.prompt 
      : '';
    const promptId = typeof promptFromSchema === 'string' && promptFromSchema.trim() !== '' ? promptFromSchema : '';

    return {
      nodeName: data.name as string,
      description: (data.description as string) || '',
      prompt: promptId,
      servingName: finalServingName,
      servingModel: finalServingModel,
      hasOriginalQuery: typeof schemaData.query_rewriter !== 'undefined' && schemaData.query_rewriter !== null ? schemaData.query_rewriter.include_ori_query : false,
      inputKeys: (data.input_keys as InputKeyItem[]) || [],
      outputKeys: (data.output_keys as OutputKeyItem[]) || [],
    };
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;

  useNodeTracing(id, data.name as string, data, nodeData);

  const isRun = useMemo(() => nodeData?.isRun ?? false, [nodeData?.isRun]);
  const isDone = useMemo(() => nodeData?.isDone ?? false, [nodeData?.isDone]);
  const isError = useMemo(() => nodeData?.isError ?? false, [nodeData?.isError]);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    setNodeStatus(getNodeStatus(isRun, isDone, isError));
  }, [isRun, isDone, isError]);
  const nodesUpdatedRef = useRef(false);
  const initialSyncDone = useRef(false);

  const [isChangePrompt, setChangePrompt] = useAtom(isChangePromptAtom);
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const { openModal } = useModal();

  const [selectedLLMRepo, setSelectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [isChangeLLM, setChangeLLM] = useAtom(isChangeLLMAtom);

  const [clearModelEvent] = useAtom(clearModelEventAtom);
  const [isClearModel] = useAtom(isClearModelAtom);

  const syncCurrentDataHyde = useCallback(() => {
    const currentPromptId = selectedPromptIdRepoHyde[id];
    const currentLLM = selectedLLMRepo[id];

    const finalPrompt = currentPromptId === null ? '' : (currentPromptId || formState.prompt || '');
    const finalServingName = currentLLM?.servingName || formState.servingName || '';
    const finalServingModel = currentLLM?.servingModel || formState.servingModel || '';

    const newData = {
      ...data,
      id: id,
      name: formState.nodeName,
      description: formState.description,
      input_keys: formState.inputKeys,
      output_keys: formState.outputKeys,
      serving_name: finalServingName,
      serving_model: finalServingModel,
      _debug_serving_info: {
        serving_name: finalServingName,
        serving_model: finalServingModel,
        is_serving_model_uuid: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(finalServingModel || ''),
        timestamp: new Date().toISOString(),
      },
      retriever_id: null,
      query_rewriter: {
        include_ori_query: formState.hasOriginalQuery,
        llm_chain: {
          llm_config: {
            api_key: '',
            serving_name: finalServingName,
            serving_model: finalServingModel,
          },
          prompt: typeof finalPrompt === 'string' && finalPrompt.trim() !== '' ? finalPrompt : '',
        },
      },
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState, selectedPromptIdRepoHyde, selectedLLMRepo]);

  useEffect(() => {
    if (!initialSyncDone.current) {
      syncCurrentDataHyde();
      initialSyncDone.current = true;
    }
  }, [syncCurrentDataHyde, formState.servingName, formState.servingModel]);

  useEffect(() => {
    const currentServingName = (data.serving_name as string) || (schemaData.query_rewriter?.llm_chain?.llm_config?.serving_name as string) || '';
    const currentServingModel = (data.serving_model as string) || ((schemaData.query_rewriter?.llm_chain?.llm_config as any)?.serving_model as string) || '';

    if (formState.servingName !== currentServingName || formState.servingModel !== currentServingModel) {
      setFormState(prev => ({
        ...prev,
        servingName: currentServingName,
        servingModel: currentServingModel,
      }));
    }

    // data에서 모델 정보가 비어있으면 selectedLLMRepo에서도 삭제 (모델 사용중지 후 반영)
    const currentLLM = selectedLLMRepo[id];
    const hasDataModel = currentServingName && currentServingName.trim() !== '' && currentServingModel && currentServingModel.trim() !== '';

    if (!hasDataModel && currentLLM) {
      setSelectedLLMRepo((prev: Record<string, any>) => {
        const { [id]: _removed, ...rest } = prev;
        return rest;
      });
    }
  }, [data.serving_name, data.serving_model, schemaData.query_rewriter, id, selectedLLMRepo]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentDataHyde();
      nodesUpdatedRef.current = false;
    }
  }, [formState]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  const handleFieldChange = (field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
    nodesUpdatedRef.current = true;
  };

  // GeneratorNode 패턴: data와 atom 비교해서 formState 업데이트
  useEffect(() => {
    const promptFromSchema = typeof schemaData.query_rewriter !== 'undefined' && schemaData.query_rewriter !== null 
      ? schemaData.query_rewriter.llm_chain.prompt 
      : '';
    const promptIdFromData = typeof promptFromSchema === 'string' && promptFromSchema.trim() !== '' ? promptFromSchema : '';
    const atomPromptId = selectedPromptIdRepoHyde[id];
    
    const finalPromptId = promptIdFromData || (atomPromptId && atomPromptId !== null && atomPromptId.trim() !== '' ? atomPromptId : '');
    
    if (finalPromptId && finalPromptId !== formState.prompt) {
      handleFieldChange('prompt', finalPromptId);
    } else if (!finalPromptId && formState.prompt) {
      handleFieldChange('prompt', '');
    } else if (atomPromptId === null && formState.prompt) {
      handleFieldChange('prompt', '');
    }
  }, [data, id, schemaData.query_rewriter, selectedPromptIdRepoHyde, formState.prompt]);

  // GeneratorNode 패턴: data에서 atom으로 동기화 (캐시 지운 후 프롬프트 정보 로드)
  // SelectPrompt가 작동하려면 atom에 프롬프트 ID가 반드시 있어야 함
  const isInitialMount = useRef(true);
  useEffect(() => {
    // 초기 마운트 시에만 data에서 atom으로 동기화
    if (!isInitialMount.current) {
      return;
    }
    
    const promptFromSchema = typeof schemaData.query_rewriter !== 'undefined' && schemaData.query_rewriter !== null 
      ? schemaData.query_rewriter.llm_chain.prompt 
      : '';
    const promptIdFromData = typeof promptFromSchema === 'string' && promptFromSchema.trim() !== '' ? promptFromSchema : '';
    const currentAtomValue = selectedPromptIdRepoHyde[id];
    
    // 초기 로드 시에만 data에서 atom으로 설정
    if (promptIdFromData && promptIdFromData.trim() !== '') {
      if (currentAtomValue !== promptIdFromData) {
        setSelectedPromptIdRepoHyde(prev => ({
          ...prev,
          [id]: promptIdFromData,
        }));
      }
    } else {
      if (currentAtomValue !== null && currentAtomValue !== undefined) {
        setSelectedPromptIdRepoHyde(prev => ({
          ...prev,
          [id]: null,
        }));
      }
    }
    
    isInitialMount.current = false;
  }, []);

  useEffect(() => {
    if (isChangePrompt) {
      const currentPromptId = selectedPromptIdRepoHyde[id];
      // null인 경우 삭제된 것이므로 빈 문자열로 설정
      if (currentPromptId === null || typeof currentPromptId === 'undefined') {
        handleFieldChange('prompt', '');
      } else {
        handleFieldChange('prompt', currentPromptId);
      }
      setChangePrompt(false);
      nodesUpdatedRef.current = true;
    }
  }, [id, isChangePrompt, selectedPromptIdRepoHyde, setChangePrompt]);

  useEffect(() => {
    if (!isChangeLLM) return;

    const currentLLM = selectedLLMRepo[id];
    if (currentLLM) {
      const currentServingName = currentLLM.servingName || '';
      const currentServingModel = currentLLM.servingModel || '';

      if (currentServingName !== formState.servingName || currentServingModel !== formState.servingModel) {
        handleFieldChange('servingName', currentServingName);
        handleFieldChange('servingModel', currentServingModel);
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    } else if (currentLLM === null) {
      if (formState.servingName || formState.servingModel) {
        handleFieldChange('servingName', '');
        handleFieldChange('servingModel', '');
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    }
  }, [isChangeLLM]);

  const handleInputKeysChange = useCallback((newInputKeys: InputKeyItem[]) => {
    handleFieldChange('inputKeys', newInputKeys);
  }, []);

  const handleToggleOriginalQuery = () => {
    setFormState(prev => {
      const newValue = !prev.hasOriginalQuery;
      return {
        ...prev,
        hasOriginalQuery: newValue,
      };
    });
    nodesUpdatedRef.current = true;
  };

  const handleLLMChange = useCallback(
    (selectedLLM: any) => {
      const llmName = selectedLLM.name || selectedLLM.modelName || '';
      const servingId = selectedLLM.servingId || '';

      handleFieldChange('servingName', llmName);
      handleFieldChange('servingModel', servingId);
      nodesUpdatedRef.current = true;
    },
    [handleFieldChange]
  );

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('nodeName', value);
    nodesUpdatedRef.current = true;
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
    nodesUpdatedRef.current = true;
  }, []);

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  useEffect(() => {
    if (isClearModel && clearModelEvent) {
      const { nodeId, nodeType } = clearModelEvent;

      if (nodeId === id && (nodeType === 'Hyde' || nodeType === 'retriever__rewriter_hyde')) {
        handleFieldChange('servingName', '');
        handleFieldChange('servingModel', '');
        nodesUpdatedRef.current = true;
        syncCurrentDataHyde();
      }
    }
  }, [isClearModel, clearModelEvent, id, syncCurrentDataHyde, handleFieldChange]);

  const handleDelete = () => {
    removeNode(id);
  };

  const handleHeaderClickLog = () => {
    const nodeName = (data as any).name || data.innerData?.name || id;

    if (hasChatTested) {
      openModal({
        type: 'large',
        title: '로그',
        body: <LogModal id={'builder_log'} nodeId={String(nodeName)} />,
        showFooter: false,
      });
    }
  };

  const containerRef = useAutoUpdateNodeInternals(id);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id={`hyde_left_${id}`}
          position={Position.Left}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            background: '#000000',
            top: '50%',
            transform: 'translateY(-50%)',
            left: -10,
            border: '2px solid white',
            zIndex: 20,
          }}
        />
        <NodeHeader nodeId={id} type={type} data={nodeData} defaultValue={formState.nodeName} onClickDelete={handleDelete} onChange={handleNodeNameChange} onClickLog={handleHeaderClickLog} />

        <>
          {nodeData.isToggle && (
            <div className='bg-white px-4 py-4 border-b border-gray-200'>
              <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
              <div className='relative'>
                <textarea
                  className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                  style={{
                    minHeight: '80px',
                    maxHeight: '100px',
                    height: 'auto',
                    overflow: 'hidden'
                  }}
                  placeholder={'설명 입력'}
                  value={formState.description}
                  onChange={e => {
                    const value = e.target.value;
                    if (value.length <= 100) {
                      handleDescriptionChange(value);
                    }
                    autoResize(e.target);
                  }}
                  onInput={(e: any) => {
                    autoResize(e.target);
                  }}
                  maxLength={100}
                  onMouseDown={stopPropagation}
                  onMouseUp={stopPropagation}
                  onSelect={stopPropagation}
                  onDragStart={preventAndStop}
                  onDrag={preventAndStop}
                />
                <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                  <span className='text-blue-500'>{formState.description.length}</span>/100
                </div>
              </div>
            </div>
          )}

          {!nodeData.isToggle && (
            <>
              <CardBody className='p-4'>
                <div className='mb-4'>
                  <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
                  <div className='relative'>
                    <textarea
                      className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                      rows={3}
                      placeholder={'설명 입력'}
                      value={formState.description}
                      onChange={e => {
                        const value = e.target.value;
                        if (value.length <= 100) {
                          handleDescriptionChange(value);
                        }
                      }}
                      maxLength={100}
                      onMouseDown={stopPropagation}
                      onMouseUp={stopPropagation}
                      onSelect={stopPropagation}
                      onDragStart={preventAndStop}
                      onDrag={preventAndStop}
                    />
                    <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                      <span className='text-blue-500'>{formState.description.length}</span>/100
                    </div>
                  </div>
                </div>

                <div className='border-gray-200 bg-white'>
                  <div className=''>
                    <div className='p-0 rounded-lg'>
                      <SelectLLM
                        selectedServingName={formState.servingName}
                        selectedServingModel={formState.servingModel}
                        onChange={handleLLMChange}
                        nodeId={id}
                        asAccordionItem={true}
                        title={
                          <>
                            {'LLM'}
                            <span className='ag-color-red'>*</span>
                          </>
                        }
                      />
                    </div>
                  </div>
                </div>

                <Accordion>
                  <SelectPrompt 
                    selectedPromptId={(() => {
                      const atomValue = selectedPromptIdRepoHyde[id];
                      const localValue = formState.prompt;
                      const dataValue = typeof schemaData.query_rewriter !== 'undefined' && schemaData.query_rewriter !== null 
                        ? schemaData.query_rewriter.llm_chain.prompt 
                        : '';
                      // 삭제 시(atomValue === null)에는 dataValue/localValue로 fallback 하지 않고 즉시 비워서
                      // "삭제 직후 잠깐 Prompt ID가 보이는" 현상을 방지
                      if (atomValue === null) return '';

                      return atomValue || localValue || dataValue || '';
                    })()} 
                    nodeId={id} 
                    nodeType={NodeType.RewriterHyDE.name} 
                    asAccordionItem={true} 
                    title={'Prompt'} 
                  />
                </Accordion>

                <hr className='border-gray-200' />

                <div className='flex flex-1 items-center justify-start pl-2 py-5'>
                  <div className='flex items-center space-x-4'>
                    <label className='fw-bold form-label'>사용자의 기존 질의 포함</label>

                    <button
                      type='button'
                      className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${formState.hasOriginalQuery ? 'bg-blue-600' : 'bg-gray-200'
                        }`}
                      onClick={handleToggleOriginalQuery}
                      aria-pressed={formState.hasOriginalQuery}
                    >
                      <span
                        className='inline-block h-4 w-4 transform rounded-full bg-white shadow-lg transition-transform duration-200 ease-in-out'
                        style={{
                          transform: formState.hasOriginalQuery ? 'translateX(20px)' : 'translateX(4px)',
                        }}
                      />
                    </button>
                  </div>
                </div>

                <hr className='border-gray-200' />
              </CardBody>

              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <CustomScheme
                id={id}
                inputKeys={formState.inputKeys}
                setInputKeys={handleInputKeysChange}
                inputValues={inputValues}
                setInputValues={setInputValues}
                innerData={data.innerData}
                outputKeys={formState.outputKeys}
                type={NodeType.RewriterHyDE.name}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id={`hyde_right_${id}`}
          position={Position.Right}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            background: '#000000',
            top: '50%',
            transform: 'translateY(-50%)',
            right: -10,
            border: '2px solid white',
            zIndex: 20,
          }}
        />
      </Card>
    </div>
  );
};
