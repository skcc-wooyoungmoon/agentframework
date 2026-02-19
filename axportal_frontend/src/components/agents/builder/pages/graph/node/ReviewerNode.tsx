import { isChangePromptAtom, selectedPromptIdRepoAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { selectedLLMRepoAtom, isChangeLLMAtom } from '@/components/agents/builder/atoms/llmAtom';
import { clearModelEventAtom, isClearModelAtom } from '@/components/agents/builder/atoms/clearModelAtom';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { SelectLLM } from '@/components/agents/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/agents/builder/pages/graph/contents/SelectPrompt.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem, type ReviewerDataSchema } from '@/components/agents/builder/types/Agents.ts';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useAtom } from 'jotai';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from './';

import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useModal } from '@/stores/common/modal/useModal';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import keyTableData from '@/components/agents/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

export const ReviewerNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData, syncAllNodeKeyTable } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  const initPromptId = useMemo(() => {
    const rData = data as ReviewerDataSchema;
    return rData.prompt_id;
  }, [data]);

  const innerData: CustomNodeInnerData = data.innerData ?? newInnerData;
  useNodeTracing(id, data.name as string, data, innerData);

  const isRun = useMemo(() => innerData?.isRun ?? false, [innerData?.isRun]);
  const isDone = useMemo(() => innerData?.isDone ?? false, [innerData?.isDone]);
  const isError = useMemo(() => innerData?.isError ?? false, [innerData?.isError]);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);

  useEffect(() => {
    setNodeStatus(getNodeStatus(isRun, isDone, isError));
  }, [isRun, isDone, isError]);

  const schemaData: ReviewerDataSchema = data as ReviewerDataSchema;

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || (keyTableData['agent__reviewer']['field_default']['description'] as string));
  const [servingName, setServingName] = useState((schemaData.serving_name as string) || '');
  const [servingModel, setServingModel] = useState((schemaData.serving_model as string) || '');
  const [promptId, setPromptId] = useState<string | null>(initPromptId);
  const [maxReviewAttempts, setMaxReviewAttempts] = useState<number>((data.max_review_attempts as number) || 3);

  const prevDataServingNameRef = useRef<string>('');
  const prevDataServingModelRef = useRef<string>('');
  const prevDataPromptIdRef = useRef<string | null>(null);

  useEffect(() => {
    const rData = data as ReviewerDataSchema;
    if (rData.prompt_id && rData.prompt_id !== prevDataPromptIdRef.current) {
      prevDataPromptIdRef.current = rData.prompt_id;
      if (rData.prompt_id !== promptId) {
        setPromptId(rData.prompt_id);
      }
    }
    if (rData.serving_name !== prevDataServingNameRef.current) {
      prevDataServingNameRef.current = rData.serving_name || '';
      setServingName(rData.serving_name || '');
    }

    if (rData.serving_model !== prevDataServingModelRef.current) {
      prevDataServingModelRef.current = rData.serving_model || '';
      setServingModel(rData.serving_model || '');
    }

    const currentLLM = selectedLLMRepo[id];
    const isLLMAtomEmpty = currentLLM === undefined || currentLLM === null;

    if (isLLMAtomEmpty && rData.serving_name && rData.serving_name.trim() !== '' && rData.serving_model && rData.serving_model.trim() !== '') {
      setSelectedLLMRepo((prev: Record<string, any>) => ({
        ...prev,
        [id]: {
          servingName: rData.serving_name,
          servingModel: rData.serving_model,
        },
      }));
    }
  }, [data?.serving_name, data?.serving_model, data?.prompt_id, id]);
  const initialInputKeys = useMemo(() => {
    const initInputItems: InputKeyItem[] = (schemaData.input_keys as InputKeyItem[]) || [];
    return (schemaData.input_keys as InputKeyItem[]) || initInputItems;
  }, [schemaData.input_keys]);
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);
  const [inputValues, setInputValues] = useState<string[]>(inputKeys.map(item => item.name));

  const initialOutputKeys = useMemo(() => {
    const initOutputItems: OutputKeyItem[] = (schemaData.output_keys as OutputKeyItem[]) || [];
    return (schemaData.output_keys as OutputKeyItem[]) || initOutputItems;
  }, [schemaData.output_keys]);
  const [outputKeys, setOutputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);
  const [, setOutputValues] = useState<string[]>(outputKeys.filter(item => item != null).map(item => item.name));

  const isInitialMountRef = useRef(true);
  const prevDataRef = useRef(data);

  useEffect(() => {
    if (isInitialMountRef.current) {
      isInitialMountRef.current = false;
      prevDataRef.current = data;
      return;
    }

    const dataChanged = prevDataRef.current !== data;
    if (!dataChanged) {
      return;
    }
    const newInputKeys = (schemaData.input_keys as InputKeyItem[]) || [];
    const newOutputKeys = (schemaData.output_keys as OutputKeyItem[]) || [];

    // 저장된 값이 있고 실제로 다를 때 복원 (keytable_id와 fixed_value 보존)
    if (newInputKeys.length > 0) {
      const keysChanged =
        newInputKeys.length !== inputKeys.length ||
        newInputKeys.some((key, idx) => {
          const existingKey = inputKeys[idx];
          return !existingKey || key.name !== existingKey.name;
        });

      if (keysChanged) {
        // keytable_id와 fixed_value 보존
        const preservedKeys = newInputKeys.map(newKey => {
          const existingKey = inputKeys.find(ek => ek.name === newKey.name);
          if (existingKey) {
            return {
              ...newKey,
              keytable_id: existingKey.keytable_id || newKey.keytable_id || '',
              fixed_value: existingKey.fixed_value !== undefined ? existingKey.fixed_value : newKey.fixed_value,
            };
          }
          return newKey;
        });
        setInputKeys(preservedKeys);
      }
    }

    if (newOutputKeys.length > 0) {
      const outputKeysChanged =
        newOutputKeys.length !== outputKeys.length ||
        newOutputKeys.some((key, idx) => {
          const existingKey = outputKeys[idx];
          return !existingKey || key.name !== existingKey.name;
        });

      if (outputKeysChanged) {
        setOutputKeys(newOutputKeys);
      }
    }

    prevDataRef.current = data;
  }, [data, schemaData.input_keys, schemaData.output_keys]);

  const [selectedPromptIdRepo, setSelectedPromptIdRepo] = useAtom(selectedPromptIdRepoAtom);
  const [isChangePrompt, setChangePrompt] = useAtom(isChangePromptAtom);

  const [selectedLLMRepo, setSelectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [isChangeLLM, setChangeLLM] = useAtom(isChangeLLMAtom);

  const [clearModelEvent] = useAtom(clearModelEventAtom);
  const [isClearModel] = useAtom(isClearModelAtom);

  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const nodesUpdatedRef = useRef(false);

  const prevInitPromptIdRef = useRef<string>('');
  useEffect(() => {
    if (initPromptId && initPromptId.trim() !== '') {
      const nodeId = data.id as string;
      if (prevInitPromptIdRef.current !== initPromptId) {
        const currentAtomValue = selectedPromptIdRepo[nodeId];
        if (currentAtomValue !== initPromptId) {
          setSelectedPromptIdRepo(prev => ({
            ...prev,
            [nodeId]: initPromptId,
          }));
        }

        prevInitPromptIdRef.current = initPromptId;
      }
    }
  }, [initPromptId, data.id, setSelectedPromptIdRepo]);

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
    syncAllNodeKeyTable();
  }, [inputKeys]);

  useEffect(() => {
    setOutputValues(outputKeys.map(item => (item == null ? '' : item.name)));
  }, [outputKeys]);

  const prevServingNameSyncRef = useRef<string>(servingName);
  const prevServingModelSyncRef = useRef<string>(servingModel);

  useEffect(() => {
    const servingNameChanged = prevServingNameSyncRef.current !== servingName;
    const servingModelChanged = prevServingModelSyncRef.current !== servingModel;

    if (servingNameChanged) {
      prevServingNameSyncRef.current = servingName;
    }
    if (servingModelChanged) {
      prevServingModelSyncRef.current = servingModel;
    }

    if (nodesUpdatedRef.current || servingNameChanged || servingModelChanged) {
      syncReviewerData();
      nodesUpdatedRef.current = false;
    }
  }, [nodeName, description, servingName, servingModel, promptId, maxReviewAttempts, inputKeys, outputKeys]);

  useEffect(() => {
    if (isChangePrompt) {
      setPromptId(selectedPromptIdRepo[id]);
      setChangePrompt(false);
      nodesUpdatedRef.current = true;
    }
  }, [isChangePrompt]);

  useEffect(() => {
    if (!isChangeLLM) return;

    const currentLLM = selectedLLMRepo[id];
    if (currentLLM) {
      const currentServingName = currentLLM.servingName || '';
      const currentServingModel = currentLLM.servingModel || '';

      if (currentServingName !== servingName || currentServingModel !== servingModel) {
        setServingName(currentServingName);
        setServingModel(currentServingModel);
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    } else if (currentLLM === null) {
      if (servingName || servingModel) {
        setServingName('');
        setServingModel('');
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    }
  }, [isChangeLLM]);

  const syncReviewerData = () => {
    const newInnerData = {
      ...innerData,
    };

    const currentPromptId = selectedPromptIdRepo[id];
    const currentLLM = selectedLLMRepo[id];

    const finalPromptId = currentPromptId === null ? '' : currentPromptId || promptId || '';
    const finalServingName = currentLLM?.servingName || servingName || '';
    const finalServingModel = currentLLM?.servingModel || servingModel || '';

    const newData = {
      ...data,
      type: NodeType.AgentReviewer.name,
      id: id,
      name: nodeName,
      description: description,
      serving_name: finalServingName,
      serving_model: finalServingModel,
      prompt_id: typeof finalPromptId === 'string' && finalPromptId.trim() !== '' ? finalPromptId : '',
      max_review_attempts: maxReviewAttempts,
      input_keys: inputKeys.map(key => ({
        ...key,
        fixed_value: key.fixed_value,
        keytable_id: key.keytable_id || '',
      })),
      output_keys: outputKeys,
      innerData: newInnerData,
      _debug_serving_info: {
        serving_name: finalServingName,
        serving_model: finalServingModel,
        is_serving_model_uuid: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(finalServingModel || ''),
        timestamp: new Date().toISOString(),
      },
    };

    syncNodeData(id, newData);
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

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  useEffect(() => {
    if (isClearModel && clearModelEvent) {
      const { nodeId, nodeType } = clearModelEvent;

      if (nodeId === id && (nodeType === 'Reviewer' || nodeType === 'agent__reviewer')) {
        setServingName('');
        setServingModel('');
        nodesUpdatedRef.current = true;
        syncReviewerData();
      }
    }
  }, [isClearModel, clearModelEvent, id, syncReviewerData]);

  const handleLLMChange = useCallback(
    (selectedLLM: any) => {
      const llmName = selectedLLM.name || selectedLLM.modelName || '';
      let servingIdValue = selectedLLM.servingId || '';

      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

      if (servingIdValue && !uuidRegex.test(servingIdValue)) {
        if (selectedLLM.modelId && uuidRegex.test(selectedLLM.modelId)) {
          servingIdValue = selectedLLM.modelId;
        } else if (selectedLLM.id && uuidRegex.test(selectedLLM.id)) {
          servingIdValue = selectedLLM.id;
        }
      } else {
        if (selectedLLM.modelId && uuidRegex.test(selectedLLM.modelId)) {
          servingIdValue = selectedLLM.modelId;
        } else if (selectedLLM.id && uuidRegex.test(selectedLLM.id)) {
          servingIdValue = selectedLLM.id;
        }
      }

      setServingName(llmName);
      setServingModel(servingIdValue);

      setSelectedLLMRepo((prev: Record<string, any>) => ({
        ...prev,
        [id]: {
          servingName: llmName,
          servingModel: servingIdValue,
        },
      }));

      nodesUpdatedRef.current = true;
    },
    [id, setSelectedLLMRepo]
  );

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
    nodesUpdatedRef.current = true;
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
    nodesUpdatedRef.current = true;
  };

  const handleMaxReviewAttemptsChange = (value: number) => {
    setMaxReviewAttempts(value);
    nodesUpdatedRef.current = true;
  };

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

  const containerRef = useAutoUpdateNodeInternals(id);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='gen_left'
          key={`gen_left_${innerData.isToggle}_${Date.now()}`}
          position={Position.Left}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            background: '#000000',
            top: '45%',
            transform: 'translateY(-50%)',
            left: -10,
            border: '2px solid white',
            zIndex: 20,
          }}
        />
        <NodeHeader
          nodeId={id}
          type={type}
          data={innerData}
          onClickDelete={onClickDelete}
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
        />

        <>
          {innerData.isToggle && (
            <div className='bg-white px-4 py-4 border-b border-gray-200'>
              <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
              <div className='relative'>
                <textarea
                  className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                  style={{
                    minHeight: '80px',
                    maxHeight: '100px',
                    height: 'auto',
                    overflow: 'hidden',
                  }}
                  placeholder={'설명 입력'}
                  value={description}
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
                  <span className='text-blue-500'>{description?.length || 0}</span>/100
                </div>
              </div>
              <hr className='border-gray-200' />
              <CardBody>
                <div className='w-full'>
                  <div className='h-[60px] leading-[36px] rounded-lg bg-green-100 px-4 py-2 relative'>
                    <div key={'condition-pass'} className='relative w-full'>
                      <h3 className='text-base font-bold text-gray-700'>{'PASS'}</h3>
                    </div>

                    <Handle
                      type='source'
                      position={Position.Right}
                      id='reviewer_pass'
                      style={{
                        position: 'absolute',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        width: 20,
                        height: 20,
                        right: -10,
                        background: '#10B981',
                        border: '2px solid white',
                        zIndex: 10,
                      }}
                    />
                  </div>

                  <div className='mt-4 h-[60px] leading-[36px] rounded-lg bg-red-100 px-4 py-2 relative'>
                    <div key={'condition-fail'} className='relative w-full'>
                      <h3 className='text-base font-bold text-gray-700'>{'FAIL'}</h3>
                    </div>

                    <Handle
                      type='source'
                      position={Position.Right}
                      id='reviewer_fail'
                      style={{
                        position: 'absolute',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        width: 20,
                        height: 20,
                        right: -10,
                        background: '#EF4444',
                        border: '2px solid white',
                        zIndex: 10,
                      }}
                    />
                  </div>
                </div>
              </CardBody>
            </div>
          )}

          {!innerData.isToggle && (
            <>
              <CardBody className='p-4'>
                <div className='mb-4'>
                  <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
                  <div className='relative'>
                    <textarea
                      className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                      rows={3}
                      placeholder={'설명'}
                      value={description}
                      maxLength={100}
                      onChange={e => {
                        const value = e.target.value;
                        if (value.length <= 100) {
                          handleDescriptionChange(value);
                        }
                      }}
                      onMouseDown={stopPropagation}
                      onMouseUp={stopPropagation}
                      onSelect={stopPropagation}
                      onDragStart={preventAndStop}
                      onDrag={preventAndStop}
                    ></textarea>
                    <div className='absolute bottom-2 right-3 text-xs text-gray-500'>{description?.length || 0}/100</div>
                  </div>
                </div>
              </CardBody>

              <hr className='border-gray-200' />

              <CardBody>
                <div className='bg-white'>
                  <div className='p-0'>
                    <div className='p-0 rounded-lg'></div>
                  </div>
                </div>

                <SelectLLM
                  selectedServingName={servingName}
                  selectedServingModel={servingModel}
                  onChange={handleLLMChange}
                  asAccordionItem={true}
                  title={
                    <>
                      {'LLM'}
                      <span className='ag-color-red'>*</span>
                    </>
                  }
                />

                <SelectPrompt
                  selectedPromptId={(() => {
                    const atomValue = selectedPromptIdRepo[id];
                    const localValue = promptId;
                    return atomValue || localValue;
                  })()}
                  nodeId={id}
                  nodeType={NodeType.AgentReviewer.name}
                  asAccordionItem={true}
                  title={'Prompt'}
                />

                <div className='mt-5 w-full'>
                  <div className='h-[60px] leading-[36px] rounded-lg bg-green-100 px-4 py-2 relative'>
                    <div key={'condition-pass'} className='relative w-full'>
                      <h3 className='text-base font-bold text-gray-700'>{'PASS'}</h3>
                    </div>

                    <Handle
                      type='source'
                      position={Position.Right}
                      id='reviewer_pass'
                      style={{
                        position: 'absolute',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        width: 20,
                        height: 20,
                        right: -30,
                        background: '#10B981',
                        border: '2px solid white',
                        zIndex: 10,
                      }}
                    />
                  </div>

                  <div className='mt-4 h-[60px] leading-[36px] rounded-lg bg-red-100 px-4 py-2 relative'>
                    <div key={'condition-fail'} className='relative w-full'>
                      <h3 className='text-base font-bold text-gray-700'>{'FAIL'}</h3>
                    </div>

                    <Handle
                      type='source'
                      position={Position.Right}
                      id='reviewer_fail'
                      style={{
                        position: 'absolute',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        width: 20,
                        height: 20,
                        right: -30,
                        background: '#EF4444',
                        border: '2px solid white',
                        zIndex: 10,
                      }}
                    />
                  </div>
                </div>
                <div className='mt-4 flex w-full justfiy-between items-center gap-4'>
                  <label className='fw-bold form-label text-base'>{'Max Review Attempts'}</label>
                  <div className='w-[100px]'>
                    <select
                      className='b-selectbox'
                      name='max_attempts'
                      value={maxReviewAttempts}
                      onChange={e => {
                        handleMaxReviewAttemptsChange(Number(e.target.value));
                      }}
                    >
                      {[1, 2, 3, 4, 5].map(num => (
                        <option key={num} value={num}>
                          {num}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </CardBody>

              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>
              <CustomScheme
                id={id}
                inputKeys={inputKeys}
                setInputKeys={setInputKeys}
                inputValues={inputValues}
                setInputValues={setInputValues}
                innerData={data.innerData}
                outputKeys={outputKeys}
                type={NodeType.AgentGenerator.name}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
