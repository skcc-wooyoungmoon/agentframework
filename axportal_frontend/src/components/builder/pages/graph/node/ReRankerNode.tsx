import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/builder/pages/graph/contents/SelectLLM.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem, type ReRankerDataSchema } from '@/components/builder/types/Agents';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import { type FC, useCallback, useEffect, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';

interface NodeFormState {
  nodeName: string;
  description: string;
  servingName: string;
  servingModel: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const ReRankerNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const [formState, setFormState] = useState<NodeFormState>(() => {
    const contextRefiner = (data as ReRankerDataSchema).context_refiner;
    return {
      nodeName: data.name as string,
      description: (data.description as string) || '',
      servingName: contextRefiner?.rerank_cnf?.model_info?.serving_name ?? '',
      servingModel: contextRefiner?.rerank_cnf?.model_info?.api_key ?? '',
      inputKeys: (data.input_keys as InputKeyItem[]) || [],
      outputKeys: (data.output_keys as OutputKeyItem[]) || [],
    };
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
    // null이면 삭제된 것이므로 빈 문자열, undefined면 로컬 상태 사용
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
      // rewriter node state
      retriever_id: null,
      kind: 'rerank',
      context_refiner: {
        rerank_cnf: {
          model_info: {
            serving_name: finalServingName,
            api_key: finalServingModel,
          },
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

  const handleLLMChange = (selectedLLM: any) => {
    handleFieldChange('servingName', selectedLLM?.name || '');
    handleFieldChange('servingModel', selectedLLM?.id || ''); // lineage 정보에 저장할 serving_model 값
  };

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
          id={`rerank_left_${id}`}
          position={Position.Left}
          // 접힘/펼침 상태에 따라 동적 조정
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
                <div className='mb-0 w-auto'>                  
                  <SelectLLM
                    selectedServingName={formState.servingName}
                    selectedServingModel={formState.servingModel}
                    onChange={handleLLMChange}
                    isReRanker={true}
                    asAccordionItem={true}
                    title={
                      <>
                        {'Re-Ranker 모델'}
                        <span className='ag-color-red'>*</span>
                      </>
                    }
                  />                  
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
              type={NodeType.RetrieverReRanker.name}
            />
          </>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id={`rerank_right_${id}`}
          position={Position.Right}
          style={{
            width: 20,
            height: 20,
            background: '#000000',
            top: '50%',
            transform: 'translateY(-50%)',
            right: -10,
            border: '2px solid white',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            zIndex: 20,
          }}
        />
      </Card>
    </div>
  );
};
