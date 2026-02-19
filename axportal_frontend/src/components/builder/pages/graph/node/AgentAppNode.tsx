import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { type AgentAppDataSchema, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/builder/types/Agents.ts';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { env } from '@/constants/common/env.constants';
import { useGetAgentAppById } from '@/services/deploy/agent/agentDeploy.services';
import type { InputKey, OutputKey } from '@/services/deploy/agent/types';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import React, { useEffect, useState } from 'react';
import { logState } from '../../..';
import { SelectAgentApp } from '../contents/SelectAgentApp';
import { NodeFooter, NodeHeader } from './';

export const AgentAppNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const schemaData: AgentAppDataSchema = data as AgentAppDataSchema;
  const innerData: CustomNodeInnerData = data.innerData ?? {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(innerData.isRun, innerData.isDone, innerData.isError);
    setNodeStatus(status);
  }, [innerData.isRun, innerData.isDone, innerData.isError]);

  // 선택된 AgentApp ID를 가져오기
  const [agentAppId, setAgentAppId] = useState<string | null>(schemaData?.agent_app_id);
  const [nodeName, setNodeName] = useState(schemaData.name as string);
  const [description, setDescription] = useState((schemaData.description as string) || '');
  const [apiKey] = useState<string>(env.VITE_API_KEY || '');

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  //[input keys handle]
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>((schemaData.input_keys as InputKeyItem[]) || []);
  const [inputValues, setInputValues] = useState<string[]>(inputKeys.map(item => item.name));

  //[output keys handle]
  const [outputKeys, setOutputKeys] = useState<OutputKeyItem[]>((schemaData.output_keys as OutputKeyItem[]) || []);

  // 임시로 비활성화 (검색 버튼을 눌렀을 때만 SelectAgentApp에서 호출)
  const { data: agentAppData } = useGetAgentAppById(
    { appId: agentAppId || '' },
    {
      enabled: !!agentAppId,
    }
  );

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
    syncGenData();
  }, [inputKeys]);

  useEffect(() => {
    syncGenData();
  }, [nodeName, description, outputKeys, apiKey, agentAppId]);

  // AgentApp 데이터가 로드되었을 때 input_keys와 output_keys를 업데이트
  useEffect(() => {
    const fetchAgentAppSchemaData = async () => {
      if (agentAppId && agentAppId !== '') {
        // AgentApp의 input_keys와 output_keys로 업데이트
        if (agentAppData?.inputKeys) {
          const updatedInputKeys = agentAppData.inputKeys.map((key: InputKey) => ({
            name: key.name,
            required: key.required || false,
            keytable_id: (schemaData.input_keys as InputKeyItem[]).find(item => item.name === key.name)?.keytable_id || null,
            fixed_value: (schemaData.input_keys as InputKeyItem[]).find(item => item.name === key.name)?.fixed_value || null,
          }));
          setInputKeys(updatedInputKeys);
        }

        if (agentAppData?.outputKeys) {
          const updatedOutputKeys = agentAppData.outputKeys.map((key: OutputKey) => ({
            name: key.name,
            keytable_id: `${key.name}__${id}`,
          }));
          setOutputKeys(updatedOutputKeys);
        }
      } else {
        // AgentApp이 선택되지 않았을 때는 기본값으로 초기화
        setInputKeys([]);
        setOutputKeys([]);
      }
    };

    fetchAgentAppSchemaData();
  }, [agentAppId, id, agentAppData]);

  const syncGenData = () => {
    const newData = {
      ...data,
      type: NodeType.AgentApp.name,
      id: id,
      name: nodeName,
      description: description,
      agent_app_id: agentAppId || '', // null이면 빈 문자열로 설정
      input_keys: inputKeys.map(key => ({
        ...key,
        fixed_value: key.fixed_value,
        keytable_id: key.keytable_id || '',
      })),
      api_key: apiKey,
      output_keys: outputKeys,
      innerData: innerData,
    };

    syncNodeData(id, newData);
  };

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
  };

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

  // 노드 내부 콘텐츠 높이 변화 감지하여 연결선 재계산
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
            top: '50%',
            transform: 'translateY(-50%)',
            left: -10,
            border: '2px solid white',
            background: '#000000',
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
                <span className='text-blue-500'>{description.length}</span>/100
              </div>
            </div>
          </div>
        </CardBody>

        {!innerData.isToggle && (
          <>
            <div className='border-t border-gray-200'>
              <CardBody>
                <SelectAgentApp
                  selectedAgentAppId={agentAppId}
                  onAgentAppSelect={selectedAgentApp => {
                    setAgentAppId(selectedAgentApp?.id || null);
                  }}
                  nodeId={id}
                  nodeType={NodeType.AgentApp.name}
                  asAccordionItem={true}
                  title={
                    <>
                      {'Agent'}
                      <span className='ag-color-red'>*</span>
                    </>
                  }
                />
              </CardBody>
            </div>

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
              type={NodeType.AgentApp.name}
            />
          </>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle as boolean} />
        </CardFooter>
        <Handle
          type='source'
          id='gen_right'
          key={`gen_right_${innerData.isToggle}_${Date.now()}`}
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
