import { Card, CardBody } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, type OutputKeyItem } from '@/components/builder/types/Agents';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { type NodeProps } from '@xyflow/react';
import { type FC, useCallback, useEffect, useRef, useState } from 'react';
import { NodeHeader } from '.';

interface NodeFormState {
  nodeName: string;
  description: string | null;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const NoteNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, syncNodeData } = useGraphActions();

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || null,
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
  const nodesUpdatedRef = useRef(false);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(nodeData.isRun, nodeData.isDone, nodeData.isError);
    setNodeStatus(status);
  }, [nodeData.isRun, nodeData.isDone, nodeData.isError]);

  const syncCurrentData = useCallback(() => {
    const newData = {
      ...data,
      // basic node info
      id: id,
      name: formState.nodeName,
      description: formState.description,
      // node schema
      input_keys: formState.inputKeys,
      output_keys: formState.outputKeys,
      // public node state
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [nodeData, syncNodeData, formState, data, id]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [syncCurrentData]);

  // Note 노드는 초기 렌더링 시 저장 필요(data 안에 id 세팅)
  useEffect(() => {
    syncCurrentData();
  }, []);

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
    handleFieldChange('description', value);
  }, []);

  const handleDelete = () => {
    removeNode(id);
  };

  return (
    <Card className={ABClassNames('agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus)}>
      <NodeHeader nodeId={id} type={type} data={nodeData} defaultValue={formState.nodeName} onClickDelete={handleDelete} onChange={handleNodeNameChange} />

      <CardBody className='p-4'>
        <div className='mb-4'>
          <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>

          <div className='relative'>
            <textarea
              className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
              rows={3}
              placeholder={'설명 입력'}
              value={formState.description || ''}
              onChange={e => handleDescriptionChange(e.target.value)}
              maxLength={100}
            />
            <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
              <span className='text-blue-500'>{formState.description?.length || 0}</span>/100
            </div>
          </div>
        </div>
      </CardBody>
    </Card>
  );
};
