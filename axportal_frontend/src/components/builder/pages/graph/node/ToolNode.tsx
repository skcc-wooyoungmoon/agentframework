import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { useGetAgentToolById } from '@/services/agent/tool/agentTool.services';
import type { GetAgentToolByIdResponse } from '@/services/agent/tool/types';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import { SelectTools } from '../contents/SelectTools';

interface NodeFormState {
  type: string;
  id: string;
  name: string;
  description: string;
  tool_id: string;
  tool_info: GetAgentToolByIdResponse | null;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
}

export const ToolNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  // console.log('🔍 ToolNode data :::::: ', data, id);

  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const [isChangeTool, setChangeTool] = useState(false);
  const [formState, setFormState] = useState<NodeFormState>({
    type: NodeType.Tool.name,
    id: id,
    name: data.name as string,
    description: (data.description as string) || (keyTableData['tool']['field_default']['description'] as string),
    tool_id: data.tool_id && typeof data.tool_id === 'string' ? data.tool_id : '',
    tool_info: null,
    input_keys: (data.input_keys as InputKeyItem[]) || [],
    output_keys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.input_keys.map(item => item.name));

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  const tmpId = useRef<string>('');

  // isSingle이 true인 경우 tool 조회
  // useGetAgentToolById
  const { data: toolInfo } = useGetAgentToolById(
    { toolId: formState.tool_id || '' },
    {
      enabled: !!formState.tool_id,
    }
  );

  useEffect(() => {
    if (toolInfo) {
      handleFieldChange('tool_info', toolInfo as GetAgentToolByIdResponse);

      if (tmpId.current === toolInfo?.id) {
        // toolInfo의 inputKeys를 formState의 input_keys에 업데이트
        setFormState(prev => ({
          ...prev,
          input_keys: convertToolInputKeys(toolInfo?.inputKeys ?? []),
        }));
        tmpId.current = '';
      }
    }
  }, [toolInfo]);

  useEffect(() => {
    if (isChangeTool && toolInfo !== null) {
      const convertInputKeys = convertToolInputKeys(toolInfo?.inputKeys ?? []);
      setFormState(prev => {
        return {
          ...prev,
          tool_id: formState.tool_info?.id ?? '',
          input_keys: convertInputKeys,
        };
      });

      setInputValues(convertInputKeys.map(item => item.name));
      syncCurrentData();
      setChangeTool(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isChangeTool]);

  const convertToolInputKeys = (toolInputKeys: any[]) => {
    const result: InputKeyItem[] = [];

    toolInputKeys?.forEach(key => {
      result.push({
        name: key?.key,
        required: key?.required,
        keytable_id: null,
        fixed_value: key.defaultValue,
      });
    });

    return result;
  };

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(nodeData.isRun, nodeData.isDone, nodeData.isError);
    setNodeStatus(status);
  }, [nodeData.isRun, nodeData.isDone, nodeData.isError]);

  const syncCurrentData = () => {
    const newData = {
      ...data,
      type: NodeType.Tool.name,
      id: id,
      name: formState.name,
      description: formState.description,
      // node schema
      input_keys: formState.input_keys,
      output_keys: formState.output_keys,
      // node state
      tool_id: formState.tool_id,
      tool_info: formState.tool_info,
      // public node state
      innerData: {
        ...nodeData,
      },
    };
    syncNodeData(id, newData);
  };

  useEffect(() => {
    syncCurrentData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formState]);

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  const handleFieldChange = (field: keyof NodeFormState, value: any) => {
    // input_keys 변경 시에는 setChangeTool을 호출하지 않음 (직접 변경이므로)
    if (field === 'tool_id') {
      setChangeTool(true);
    }

    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleInputKeysChange = useCallback((newInputKeys: InputKeyItem[]) => {
    handleFieldChange('input_keys', newInputKeys);
  }, []);

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('name', value);
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
          id='tool_left'
          key={`tool_left_${nodeData.isToggle}`}
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
          onClickLog={handleHeaderClickLog}
          defaultValue={formState.name}
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
                <SelectTools
                  toolIds={formState.tool_id ? [formState.tool_id] : []}
                  nodeId={id}
                  toolInfoList={formState.tool_info ? [formState.tool_info] : null}
                  asAccordionItem={true}
                  isSingle={true}
                  title={
                    <>
                      {'Tool'}
                      <span className='ag-color-red'>*</span>
                    </>
                  }
                  onToolsUpdate={selectedToolIds => {
                    handleFieldChange('tool_id', selectedToolIds?.[0] || '');
                    tmpId.current = selectedToolIds?.[0] || '';
                  }}
                  onToolInfoListUpdate={selectedToolInfo => {
                    handleFieldChange('tool_info', selectedToolInfo[0] ?? null);
                  }}
                />
              </CardBody>
            </div>

            <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
              <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
            </div>

            <CustomScheme
              id={id}
              inputKeys={formState.input_keys}
              setInputKeys={handleInputKeysChange}
              inputValues={inputValues}
              setInputValues={setInputValues}
              innerData={data.innerData}
              outputKeys={formState.output_keys}
              type={formState.type}
              disabledAddInput={true}
            />
          </>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id='tool_right'
          key={`tool_right_${nodeData.isToggle}`}
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
