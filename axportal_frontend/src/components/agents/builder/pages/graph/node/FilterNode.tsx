import { isChangePromptAtom, selectedPromptIdRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { selectedLLMRepoAtom } from '@/components/agents/builder/atoms/llmAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Accordion } from '@/components/agents/builder/common/accordion/Accordion.tsx';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useModal } from '@/stores/common/modal/useModal';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/agents/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/agents/builder/pages/graph/contents/SelectPrompt.tsx';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { type CustomNode, type CustomNodeInnerData, type FilterDataSchema, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  nodeName: string;
  description: string;
  servingName: string;
  servingModel: string;
  prompt: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const FilterNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const [selectedPromptIdRepo] = useAtom(selectedPromptIdRepoAtom);
  const [isChangePrompt, setChangePrompt] = useAtom(isChangePromptAtom);
  const [selectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const { openModal } = useModal();

  const [inputValues, setInputValues] = useState<string[]>((data.input_keys as InputKeyItem[] | undefined)?.map(item => item.name) || []);

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
  const schemaData: FilterDataSchema = data as FilterDataSchema;

  const initialPromptId = (schemaData?.context_refiner?.llm_chain?.prompt as string) || '';
  const servingNameFromData = (data.serving_name as string) || '';
  const servingNameFromSchema = (schemaData?.context_refiner?.llm_chain?.llm_config?.serving_name as string) || '';
  const servingModelFromData = ((data as any)?.serving_model as string) || '';
  const servingModelFromSchema = ((schemaData?.context_refiner?.llm_chain?.llm_config as any)?.serving_model as string) || '';

  const finalServingName = servingNameFromData || servingNameFromSchema;
  const finalServingModel = servingModelFromData || servingModelFromSchema || '';

  const [promptId, setPromptId] = useState<string | null>(initialPromptId);
  const [servingName, setServingName] = useState(finalServingName);
  const [servingModel, setServingModel] = useState(finalServingModel);

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || '',
    servingName: finalServingName,
    servingModel: finalServingModel,
    prompt: initialPromptId,
    inputKeys: (data.input_keys as InputKeyItem[]) || [],
    outputKeys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const nodesUpdatedRef = useRef(false);
  const initialSyncDone = useRef(false);

  const handleFieldChange = useCallback(
    (field: keyof NodeFormState, value: any) => {
      setFormState(prev => ({
        ...prev,
        [field]: value,
      }));
      nodesUpdatedRef.current = true;
    },
    [nodesUpdatedRef]
  );

  const syncCurrentData = useCallback(() => {
    const currentPromptId = selectedPromptIdRepo[id];
    const currentLLM = selectedLLMRepo[id];

    const finalPrompt = currentPromptId === null ? '' : currentPromptId || formState.prompt || '';
    const finalServingName = currentLLM?.servingName || servingName || '';
    const finalServingModel = currentLLM?.servingModel || servingModel || '';

    const existingContextRefiner = ((data as any)?.context_refiner as any) || (schemaData?.context_refiner as any) || {};
    const existingLlmChain = existingContextRefiner?.llm_chain || {};
    const existingLlmConfig = existingLlmChain?.llm_config || {};

    const normalizedInputKeys = (formState.inputKeys || []).map(key => ({
      ...key,
      keytable_id: key.keytable_id ?? '',
    }));

    const newContextRefiner = {
      ...existingContextRefiner,
      llm_chain: {
        ...existingLlmChain,
        llm_config: {
          ...existingLlmConfig,
          api_key: existingLlmConfig?.api_key ?? '',
          serving_name: finalServingName,
          serving_model: finalServingModel,
        },
        prompt: typeof finalPrompt === 'string' && finalPrompt.trim() !== '' ? finalPrompt : '',
      },
    };

    const newData = {
      ...data,
      id,
      type: NodeType.RetrieverFilter.name,
      name: formState.nodeName,
      description: formState.description,
      input_keys: normalizedInputKeys,
      output_keys: formState.outputKeys || [],
      serving_name: finalServingName,
      serving_model: finalServingModel,
      context_refiner: newContextRefiner,
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [data, id, formState, nodeData, schemaData, servingName, servingModel, syncNodeData, selectedPromptIdRepo, selectedLLMRepo]);

  useEffect(() => {
    if (!initialSyncDone.current) {
      syncCurrentData();
      initialSyncDone.current = true;
      nodesUpdatedRef.current = false;
      return;
    }

    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [syncCurrentData]);

  useEffect(() => {
    const currentServingName = (data.serving_name as string) || (schemaData?.context_refiner?.llm_chain?.llm_config?.serving_name as string) || '';
    const currentServingModel = ((data as any)?.serving_model as string) || ((schemaData?.context_refiner?.llm_chain?.llm_config as any)?.serving_model as string) || '';

    if (servingName !== currentServingName || servingModel !== currentServingModel) {
      setServingName(currentServingName);
      setServingModel(currentServingModel);
      setFormState(prev => ({
        ...prev,
        servingName: currentServingName,
        servingModel: currentServingModel,
      }));
    }
  }, [data, schemaData, servingName, servingModel]);

  useEffect(() => {
    const currentPrompt = (schemaData?.context_refiner?.llm_chain?.prompt as string) || '';
    if ((promptId || '') !== currentPrompt) {
      setPromptId(currentPrompt || null);
      setFormState(prev => ({
        ...prev,
        prompt: currentPrompt,
      }));
    }
  }, [schemaData, promptId]);

  useEffect(() => {
    if (isChangePrompt) {
      const nextPromptId = selectedPromptIdRepo[id];
      handleFieldChange('prompt', nextPromptId || '');
      setPromptId(nextPromptId ?? null);
      setChangePrompt(false);
    }
  }, [isChangePrompt, id, selectedPromptIdRepo, setChangePrompt, handleFieldChange]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);
  const containerRef = useAutoUpdateNodeInternals(id);

  const handleInputKeysChange = useCallback(
    (newInputKeys: InputKeyItem[]) => {
      handleFieldChange('inputKeys', newInputKeys);
      setInputValues(newInputKeys.map(item => item.name));
    },
    [handleFieldChange]
  );

  useEffect(() => {
    setInputValues((formState.inputKeys || []).map(item => item.name));
  }, [formState.inputKeys]);

  const handleNodeNameChange = useCallback(
    (value: string) => {
      handleFieldChange('nodeName', value);
    },
    [handleFieldChange]
  );

  const handleDescriptionChange = useCallback(
    (value: string) => {
      handleFieldChange('description', value);
    },
    [handleFieldChange]
  );

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

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const handleDelete = () => {
    removeNode(id);
  };

  const handleLLMChange = useCallback(
    (selectedLLM: any) => {
      const llmName = selectedLLM?.name || selectedLLM?.modelName || selectedLLM?.model_name || '';
      const llmServingId = selectedLLM?.servingId || selectedLLM?.serving_id || selectedLLM?.servingModel || '';

      handleFieldChange('servingName', llmName);
      handleFieldChange('servingModel', llmServingId);
      setServingName(llmName);
      setServingModel(llmServingId);
    },
    [handleFieldChange]
  );
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='filter_left'
          key={`filter_left_${nodeData.isToggle}`}
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
        <NodeHeader
          nodeId={id}
          type={type}
          data={nodeData}
          defaultValue={formState.nodeName}
          onClickDelete={handleDelete}
          onClickLog={handleHeaderClickLog}
          onChange={handleNodeNameChange}
        />
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
                    overflow: 'hidden',
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
              </CardBody>

              <hr className='border-gray-200' />
              <CardBody>
                <Accordion>
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
                  <SelectPrompt selectedPromptId={promptId} nodeId={id} nodeType={NodeType.RetrieverFilter.name} asAccordionItem={true} title='프롬프트' />
                </Accordion>
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
                type={NodeType.RetrieverFilter.name}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id='filter_right'
          key={`filter_right_${nodeData.isToggle}`}
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
