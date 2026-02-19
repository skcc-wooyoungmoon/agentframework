import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { type NodeProps } from '@xyflow/react';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { useAtom } from 'jotai';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useModal } from '@/stores/common/modal/useModal';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  nodeName: string;
  description: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const NoteNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const { openModal } = useModal();

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || '',
    inputKeys: (data.input_keys as InputKeyItem[]) || [],
    outputKeys: (data.output_keys as OutputKeyItem[]) || [],
  });

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

  const syncCurrentData = useCallback(() => {
    const newInnerData = {
      ...nodeData,
    };

    const newData = {
      ...data,
      type: type,
      id: id,
      name: formState.nodeName,
      description: formState.description,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState.nodeName, formState.description]);

  useEffect(() => {
    syncCurrentData();
  }, []);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [formState.nodeName, formState.description, syncCurrentData]);

  const handleFieldChange = (field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
    nodesUpdatedRef.current = true;
  };

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('nodeName', value);
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    const truncatedValue = value.length > 100 ? value.substring(0, 100) : value;
    handleFieldChange('description', truncatedValue);
  }, []);

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

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
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
        )}
      </>

      <CardFooter>
        <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
      </CardFooter>
    </Card>
  );
};
