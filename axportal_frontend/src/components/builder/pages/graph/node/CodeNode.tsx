import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { UICode } from '@/components/UI/atoms/UICode';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position } from '@xyflow/react';
import { useAtom } from 'jotai';
import React, { useCallback, useEffect, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import { useAutoUpdateNodeInternals } from '../../../hooks/useAutoUpdateNodeInternals';

interface NodeFormState {
  nodeName: string;
  description: string;
  code: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const CodeNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();

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

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || (keyTableData['agent__coder']['field_default']['description'] as string),
    code: (data.code as string) || (keyTableData['agent__coder']['field_default']['code'] as string),
    inputKeys: (data.input_keys as InputKeyItem[]) || [],
    outputKeys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));

  const syncCurrentData = () => {
    const newData = {
      ...data,
      type: NodeType.AgentCoder.name,
      id: id,
      name: formState.nodeName,
      description: formState.description,
      code: formState.code,
      input_keys: formState.inputKeys,
      output_keys: formState.outputKeys,
      innerData: { ...nodeData },
    };

    syncNodeData(id, newData);
  };

  useEffect(() => {
    syncCurrentData();
  }, [formState]);

  // 노드 내부 콘텐츠 높이 변화 감지하여 연결선 재계산 (Generator/Categorizer와 동일한 방식)
  const containerRef = useAutoUpdateNodeInternals(id);

  const handleFieldChange = useCallback((field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
  }, []);

  const handleInputKeysChange = useCallback(
    (newInputKeys: InputKeyItem[]) => {
      handleFieldChange('inputKeys', newInputKeys);
    },
    [handleFieldChange]
  );

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

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleCodeChange = useCallback(
    (newCode: string) => {
      handleFieldChange('code', newCode);
    },
    [handleFieldChange]
  );

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
          id='code_left'
          key={`code_left_${nodeData.isToggle}_${Date.now()}`}
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
          onClickDelete={onClickDelete}
          onClickLog={handleHeaderClickLog}
          defaultValue={formState.nodeName}
          onChange={handleNodeNameChange}
        />

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
              {/* 코드 섹션 */}
              <CardBody>
                <div className='mb-4'>
                  <div className='flex items-center justify-between mb-3'>
                    <div className='flex items-center'>
                      <span className='text-lg font-bold text-gray-700'>코드</span>
                      <span className='ag-color-red'>*</span>
                    </div>
                    <button
                      className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'
                      onClick={() => {
                        openModal({
                          title: '코드 수정',
                          type: 'large',
                          body: (
                            <div className='flex h-full overflow-hidden'>
                              <UICode value={formState.code} onChange={handleCodeChange} language='python' theme='dark' width='100%' minHeight='500px' readOnly={false} />
                            </div>
                          ),
                          showFooter: true,
                          confirmText: '저장',
                          onConfirm: () => {
                            // console.log('🔍 코드 수정 모달 저장:', formState.code);
                          },
                        });
                      }}
                    >
                      코드 수정
                    </button>
                  </div>
                  <UICode value={formState.code} onChange={handleCodeChange} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='484px' readOnly={true} />
                </div>
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
                type={NodeType.AgentCoder.name}
              />
            </>
          )}
        </>
        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
        <Handle
          type='source'
          id='code_right'
          key={`code_right_${nodeData.isToggle}_${Date.now()}`}
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
