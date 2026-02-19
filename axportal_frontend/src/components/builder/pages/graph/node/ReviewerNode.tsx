import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { SelectLLM } from '@/components/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/builder/pages/graph/contents/SelectPrompt.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem, type ReviewerDataSchema } from '@/components/builder/types/Agents.ts';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import React, { useEffect, useMemo, useState } from 'react';
import { NodeFooter, NodeHeader } from './';

import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { useModal } from '@/stores/common/modal/useModal';
import { useAtom } from 'jotai';
import { logState } from '../../..';

export const ReviewerNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  // console.log('🔍 ReviewerNode!!!!!!!!!!!!!:', data, id, type);

  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  const innerData: CustomNodeInnerData = data.innerData ?? newInnerData;
  const schemaData: ReviewerDataSchema = data as ReviewerDataSchema;

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(innerData.isRun, innerData.isDone, innerData.isError);
    setNodeStatus(status);
  }, [innerData.isRun, innerData.isDone, innerData.isError]);

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || (keyTableData['agent__reviewer']['field_default']['description'] as string));
  const [servingName, setServingName] = useState((schemaData.serving_name as string) || '');
  const [servingModel, setServingModel] = useState((schemaData.serving_model as string) || '');
  const [promptId, setPromptId] = useState<string | null>((data.prompt_id as string) || '');
  const [maxReviewAttempts, setMaxReviewAttempts] = useState<number>((data.max_review_attempts as number) || 3);

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
  const [outputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);
  const [, setOutputValues] = useState<string[]>(outputKeys.filter(item => item != null).map(item => item.name));

  const { syncAllNodeKeyTable } = useGraphActions();

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
    syncAllNodeKeyTable();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inputKeys]);

  useEffect(() => {
    setOutputValues(outputKeys.map(item => (item == null ? '' : item.name)));
  }, [outputKeys]);

  useEffect(() => {
    syncReviewerData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [nodeName, description, servingName, servingModel, promptId, maxReviewAttempts, inputKeys, outputKeys]);

  const syncReviewerData = () => {
    const newInnerData = {
      ...innerData,
    };

    const newData = {
      ...data,
      type: NodeType.AgentReviewer.name,
      id: id,
      name: nodeName,
      description: description,
      serving_name: servingName,
      serving_model: servingModel,
      prompt_id: promptId,
      max_review_attempts: maxReviewAttempts,
      input_keys: inputKeys.map(key => ({
        ...key,
        fixed_value: key.fixed_value,
        keytable_id: key.keytable_id || '',
      })),
      output_keys: outputKeys,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  };

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleLLMChange = (selectedLLM: any) => {
    setServingName(selectedLLM?.name || '');
    setServingModel(selectedLLM?.id || ''); // lineage 정보에 저장할 serving_model 값
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
  };

  const handleMaxReviewAttemptsChange = (value: number) => {
    setMaxReviewAttempts(value);
  };

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

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
          id='gen_left'
          key={`gen_left_${innerData.isToggle}_${Date.now()}`}
          position={Position.Left}
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
          onClickLog={handleHeaderClickLog}
          defaultValue={nodeName}
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
                value={description}
                onChange={e => handleDescriptionChange(e.target.value)}
                maxLength={100}
              />
              <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                <span className='text-blue-500'>{description?.length || 0}</span>/100
              </div>
            </div>
          </div>
        </CardBody>
        <div className='border-t border-gray-200'>
          <CardBody>
            {!innerData.isToggle && (
              <>
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
                  selectedPromptId={promptId}
                  nodeId={id}
                  nodeType={NodeType.AgentReviewer.name}
                  asAccordionItem={true}
                  title={'Prompt'}
                  onPromptUpdate={selectedPrompt => {
                    setPromptId(selectedPrompt?.id || '');
                  }}
                />
              </>
            )}

            {/* </Accordion> */}
            <div className='mt-5 w-full'>
              <div className='h-[60px] leading-[36px] rounded-lg bg-agent-green px-4 py-2 relative'>
                <div key={'condition-pass'} className='relative w-full'>
                  <h3 className='text-base font-bold text-gray-700'>PASS</h3>
                </div>

                <Handle
                  type='source'
                  position={Position.Right}
                  id='handle-condition-pass'
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

              <div className='mt-4 h-[60px] leading-[36px] rounded-lg bg-agent-red px-4 py-2 relative'>
                <div key={'condition-fail'} className='relative w-full'>
                  <h3 className='text-base font-bold text-gray-700'>FAIL</h3>
                </div>

                <Handle
                  type='source'
                  position={Position.Right}
                  id='handle-condition-fail'
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

            {!innerData.isToggle && (
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
            )}
          </CardBody>
        </div>

        {!innerData.isToggle && (
          <>
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
              type={NodeType.AgentReviewer.name}
            />
          </>
        )}
        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
