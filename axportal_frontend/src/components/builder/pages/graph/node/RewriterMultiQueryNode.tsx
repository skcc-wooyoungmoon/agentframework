import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/builder/pages/graph/contents/SelectPrompt.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem, type RewriterMultiQueryDataSchema } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../../atoms/logAtom';

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

export const RewriterMultiQueryNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const schemaData: RewriterMultiQueryDataSchema = data as RewriterMultiQueryDataSchema;

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || (keyTableData['retriever__rewriter_multiquery']['field_default']['description'] as string),
    prompt: schemaData?.query_rewriter?.llm_chain?.prompt || '',
    servingName: schemaData?.query_rewriter?.llm_chain?.llm_config?.serving_name || '',
    servingModel: (schemaData?.query_rewriter?.llm_chain?.llm_config?.api_key as string) || '',
    hasOriginalQuery: schemaData?.query_rewriter?.include_ori_query || false,
    inputKeys: (data.input_keys as InputKeyItem[]) || [],
    outputKeys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isToggle: false,
  };

  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(nodeData.isRun, nodeData.isDone, nodeData.isError);
    setNodeStatus(status);
  }, [nodeData.isRun, nodeData.isDone, nodeData.isError]);

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  const syncCurrentData = useCallback(() => {
    const finalPrompt = formState.prompt || '';
    const finalServingName = formState.servingName || '';
    const finalServingModel = formState.servingModel || '';

    const newData = {
      ...data,
      // basic node info
      id: id,
      name: formState.nodeName,
      description: formState.description,
      // node schema
      input_keys: formState.inputKeys,
      output_keys: formState.outputKeys,
      // 🚨 중요: Lineage 생성을 위해 최상위 레벨에도 serving 정보 저장
      serving_name: finalServingName,
      serving_model: finalServingModel,
      prompt_id: finalPrompt,
      // rewriter node state
      retriever_id: null,
      query_rewriter: {
        include_ori_query: formState.hasOriginalQuery, // 🔥 질의 포함 스위치 버튼 상태 저장
        llm_chain: {
          llm_config: {
            api_key: finalServingModel,
            serving_name: finalServingName,
          },
          prompt: typeof finalPrompt === 'string' && finalPrompt.trim() !== '' ? finalPrompt : '', // kind에 따라 제공되는 값 확인 필요
        },
      },
      // public node state
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState]);

  useEffect(() => {
    syncCurrentData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formState]);

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  const handleFieldChange = (field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleInputKeysChange = useCallback((newInputKeys: InputKeyItem[]) => {
    handleFieldChange('inputKeys', newInputKeys);
  }, []);

  const handleToggleOriginalQuery = () => {
    setFormState(prev => {
      return {
        ...prev,
        hasOriginalQuery: !prev.hasOriginalQuery,
      };
    });
  };

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('nodeName', value);
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
  }, []);

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const handleDelete = () => {
    removeNode(id);
  };

  const handleLLMChange = useCallback((selectedLLM: any) => {
    handleFieldChange('servingName', selectedLLM?.name || '');
    handleFieldChange('servingModel', selectedLLM?.id || ''); // lineage 정보에 저장할 serving_model 값
  }, []);

  const containerRef = useAutoUpdateNodeInternals(id);

  const handleHeaderClickLog = () => {
    if (data.innerData.logData) {
      setLogData(
        data.innerData.logData.map(item => ({
          log: item,
        }))
      );
      openModal({
        type: 'large',
        title: '로그',
        body: <LogModal id={'builder_log'} />,
        showFooter: false,
      });
    }
  };

  return (
    <div ref={containerRef}>
      <Card className={ABClassNames('agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus)}>
        <Handle
          type='target'
          id={`multiquery_left_${id}`}
          position={Position.Left}
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
          onClickLog={handleHeaderClickLog}
          onClickDelete={handleDelete}
          onChange={handleNodeNameChange}
        />

        <CardBody className='p-4'>
          <div className='mb-4'>
            <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
            <div className='relative'>
              <textarea
                className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                rows={3}
                placeholder={'설명 입력'}
                value={formState.description}
                onChange={e => handleDescriptionChange(e.target.value)}
                maxLength={100}
              />
              <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                <span className='text-blue-500'>{formState.description.length}</span>/100
              </div>
            </div>
          </div>
        </CardBody>

        {!nodeData.isToggle && (
          <>
            <div className='border-t border-gray-200'>
              <CardBody className='p-4'>
                <SelectLLM
                  selectedServingName={formState.servingName}
                  selectedServingModel={formState.servingModel}
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
                  selectedPromptId={formState.prompt}
                  nodeId={id}
                  nodeType={NodeType.RewriterMultiQuery.name}
                  asAccordionItem={true}
                  title={'Prompt'}
                  onPromptUpdate={selectedPrompt => {
                    handleFieldChange('prompt', selectedPrompt?.id || '');
                  }}
                />

                <hr className='border-gray-200' />

                <div className='flex flex-1 items-center justify-start pl-2 py-5'>
                  <div className='flex items-center space-x-4'>
                    <label className='fw-bold form-label'>사용자의 기존 질의 포함</label>
                    <button
                      type='button'
                      className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
                        formState.hasOriginalQuery ? 'bg-blue-600' : 'bg-gray-200'
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
              </CardBody>
            </div>

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
              type={NodeType.RewriterMultiQuery.name}
            />
          </>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id={`multiquery_right_${id}`}
          position={Position.Right}
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
