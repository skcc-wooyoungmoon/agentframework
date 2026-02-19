import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/builder/pages/graph/contents/SelectPrompt.tsx';
import { type CompressorDataSchema, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';

interface NodeFormState {
  nodeName: string;
  description: string;
  servingName: string;
  servingModel: string;
  prompt: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const CompressorNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isToggle: false,
  };

  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;
  const schemaData: CompressorDataSchema = data as CompressorDataSchema;

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(nodeData.isRun, nodeData.isDone, nodeData.isError);
    setNodeStatus(status);
  }, [nodeData.isRun, nodeData.isDone, nodeData.isError]);

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || (keyTableData['retriever__doc_compressor']['field_default']['description'] as string),
    servingName: (schemaData?.context_refiner?.llm_chain?.llm_config?.serving_name as string) || '',
    servingModel: (schemaData?.context_refiner?.llm_chain?.llm_config?.api_key as string) || '',
    prompt: (schemaData?.context_refiner?.llm_chain?.prompt as string) || '',
    inputKeys: (data.input_keys as InputKeyItem[]) || [],
    outputKeys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));

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
      // 🚨 중요: Lineage 생성을 위해 최상위 레벨에도 serving, prompt 정보 저장
      serving_name: finalServingName,
      serving_model: finalServingModel,
      prompt_id: finalPrompt,
      //node state
      retriever_id: null,
      kind: 'contextual_compression',
      context_refiner: {
        llm_chain: {
          llm_config: {
            api_key: finalServingModel,
            serving_name: finalServingName,
          },
          prompt: typeof finalPrompt === 'string' && finalPrompt.trim() !== '' ? finalPrompt : '',
        },
      },
      // public node state
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState]);

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

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const handleDelete = () => {
    removeNode(id);
  };

  const handleLLMChange = (selectedLLM: any) => {
    handleFieldChange('servingName', selectedLLM?.name || '');
    handleFieldChange('servingModel', selectedLLM?.id || ''); // lineage 정보에 저장할 serving_model 값
  };

  // 🔄 formState 변경 시 자동 동기화
  useEffect(() => {
    syncCurrentData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formState]);

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('nodeName', value);
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
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
          id='compressor_left'
          key={`compressor_left_${nodeData.isToggle}`}
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
                  nodeType={NodeType.RetrieverCompressor.name}
                  asAccordionItem={true}
                  title={'Prompt'}
                  onPromptUpdate={selectedPrompt => {
                    handleFieldChange('prompt', selectedPrompt?.id || '');
                  }}
                />
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
              type={NodeType.RetrieverCompressor.name}
            />
          </>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id='compressor_right'
          key={`compressor_right_${nodeData.isToggle}`}
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
