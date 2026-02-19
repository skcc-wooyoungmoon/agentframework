import { keyTableAtom } from '@/components/builder/atoms/AgentAtom';
import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, type InputNodeDataSchema, NodeType } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import { useEffect, useMemo, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import KeyTables from '../controller/KeyTables';

export const OutputSelectorNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { nodes, removeNode, syncNodeData, toggleNodeView } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

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

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || (keyTableData['output__keys']['field_default']['description'] as string));
  const [keyTableList] = useAtom(keyTableAtom);
  const { syncAllNodeKeyTable } = useGraphActions();

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  const dummyItem: InputKeyItem = {
    name: '',
    required: false,
    keytable_id: '',
    fixed_value: null,
  };
  const initialInputKeys = useMemo(() => {
    const initInputItems: InputKeyItem[] = [dummyItem];
    return ((data as InputNodeDataSchema).input_keys as InputKeyItem[]) || initInputItems;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [(data as InputNodeDataSchema).input_keys, dummyItem]);
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);

  // keyTableList에서 keytable_id로 키 정보를 가져오는 함수
  const getKeyTableValue = (keytableId: string): { key: string; nodeName: string; isGlobal: boolean } | null => {
    const keyTableEntry = keyTableList.find(entry => entry.id === keytableId);
    if (keyTableEntry) {
      return {
        key: keyTableEntry.key,
        nodeName: keyTableEntry.nodeName || '',
        isGlobal: keyTableEntry.isGlobal || false,
      };
    }
    return null;
  };

  // keyTableList 변경 시: 삭제된 노드의 키테이블이면 name만 비우기, 업데이트된 경우 name 업데이트
  useEffect(() => {
    if (keyTableList.length >= 0 && inputKeys.length > 0) {
      const updatedInputKeys = inputKeys.map(item => {
        if (item.keytable_id && item.keytable_id.trim() !== '') {
          const keyTableInfo = getKeyTableValue(item.keytable_id);
          if (!keyTableInfo) {
            // 키테이블에서 제거된 항목(삭제된 노드): name만 비우기
            return { ...item, name: '' };
          } else {
            // 키테이블 정보가 업데이트된 경우: name 업데이트
            const keyName = keyTableInfo.isGlobal ? keyTableInfo.key : `${keyTableInfo.nodeName}_${keyTableInfo.key}`;
            if (item.name !== keyName) {
              return { ...item, name: keyName };
            }
          }
        }
        return item;
      });

      if (JSON.stringify(updatedInputKeys) !== JSON.stringify(inputKeys)) {
        setInputKeys(updatedInputKeys);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [keyTableList]);

  const syncOutputData = () => {
    const newInnerData = {
      ...nodeData,
    };

    const newData = {
      ...data,
      type: NodeType.OutputSelector.name,
      id: id,
      name: nodeName,
      description: description,
      input_keys: inputKeys,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  };

  useEffect(() => {
    syncOutputData();
    // eslint-disable-next-line
  }, [nodeName, description, inputKeys]);

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  // 노드 내부 콘텐츠 높이 변화 감지하여 연결선 재계산
  const containerRef = useAutoUpdateNodeInternals(id);

  const node = nodes.find(node => node.id === id);
  if (!node) return null;

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleNodeNameChange = (value: string) => {
    // console.log('nodeNameChange : ', value);
    setNodeName(value);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleDescriptionChange = (val: string) => {
    setDescription(val);
  };

  const handleAddInput = () => {
    setInputKeys([...inputKeys, dummyItem]);
  };

  const handleRemoveInput = (index: number) => {
    setInputKeys(inputKeys.filter((_, i) => i !== index));
  };

  const handleOpenKeyTableModal = (index: number) => {
    // console.log('handleOpenKeyTableModal data : ', index);
    syncAllNodeKeyTable();

    const initialKeyTableId = inputKeys[index]?.keytable_id || null;

    const modalState = {
      selectedId: initialKeyTableId,
      tempValue: '',
    };

    // 🔥 상태 변경 핸들러 (객체 속성 직접 수정)
    const handleStateChange = (state: { isKeyTable: boolean; selectedId: string | null; tempValue: string }) => {
      modalState.selectedId = state.selectedId;
      modalState.tempValue = state.tempValue;
    };

    openModal({
      title: '키테이블',
      type: 'medium',
      body: <KeyTables initTempValue={''} initSelectedId={initialKeyTableId} initVisibleKeyTables={true} disabledKeyIn={true} onStateChange={handleStateChange} />,
      showFooter: true,
      confirmText: '저장',
      onConfirm: () => {
        // console.log('🎯 키테이블 모달 저장:', index, modalState);

        if (modalState.selectedId) {
          const findKeyTable = keyTableList.find(key => key.id === modalState.selectedId);
          if (findKeyTable) {
            const keyName = findKeyTable.isGlobal ? findKeyTable.key : `${findKeyTable.nodeName}_${findKeyTable.key}`;
            const updatedInputKeys = [...inputKeys];
            updatedInputKeys[index] = {
              ...updatedInputKeys[index],
              name: keyName,
              keytable_id: modalState.selectedId,
            };
            setInputKeys(updatedInputKeys);
          }
        }
      },
    });
  };

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
          position={Position.Left}
          style={{
            width: 20,
            height: 20,
            top: '50%',
            transform: 'translateY(-50%)',
            left: -10,
            background: '#000000',
            border: '2px solid white',
            zIndex: 20,
          }}
        />
        <NodeHeader
          nodeId={id}
          type={type}
          data={nodeData}
          onClickLog={handleHeaderClickLog}
          onClickDelete={onClickDelete}
          defaultValue={nodeName}
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

          {!nodeData.isToggle && (
            <>
              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <div className='card-body gap-5 px-4 mt-3 pb-3'>
                <div className='mx-auto flex gap-3 w-full flex-col items-center'>
                  {inputKeys.map((_item, index) => (
                    <div key={index} className='w-full'>
                      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
                        <div className='relative flex w-full items-center'>
                          {inputKeys[index].keytable_id && inputKeys[index].name !== '' && inputKeys[index].name !== 'dummy' && inputKeys[index].keytable_id !== '' ? (
                            // Key table이 선택된 경우 - 선택된 값만 표시
                            <div
                              className='w-full rounded bg-gray-50 px-3 py-2 text-sm'
                              style={{
                                backgroundColor: '#F1F1F4',
                                color: '#5C5B75',
                              }}
                            >
                              <div className='flex items-center justify-between'>
                                <div className='flex items-center gap-2'>
                                  <span
                                    className={`badge badge-circle badge-${inputKeys[index].keytable_id?.endsWith('_global') ? 'success' : 'primary'}`}
                                    style={{
                                      width: 20,
                                      height: 20,
                                      verticalAlign: 'middle',
                                    }}
                                  >
                                    {inputKeys[index].keytable_id?.endsWith('_global') ? 'G' : 'L'}
                                  </span>
                                  <span className='truncate'>{inputKeys[index].name}</span>
                                </div>
                                <button
                                  title=''
                                  className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
                                  style={{
                                    width: 20,
                                    height: 20,
                                    verticalAlign: 'middle',
                                    flexShrink: 0,
                                  }}
                                  onClick={() => {
                                    const updatedInputKeys = [...inputKeys];
                                    updatedInputKeys[index] = {
                                      ...updatedInputKeys[index],
                                      name: '',
                                      required: false,
                                      fixed_value: null,
                                      keytable_id: '',
                                    };
                                    setInputKeys(updatedInputKeys);
                                  }}
                                >
                                  ×
                                </button>
                              </div>
                            </div>
                          ) : (
                            // Key table이 선택되지 않은 경우 - input 필드 표시
                            <input type='text' value={''} className='input w-full border-0 outline-none' readOnly={true} placeholder='Key' />
                          )}
                        </div>
                        {/* 검색 버튼 - 항상 노출 */}
                        <button
                          onClick={() => handleOpenKeyTableModal(index)}
                          className='btn-icon btn btn-sm btn-light text-primary flex-shrink-0'
                          style={{
                            backgroundColor: '#ffffff',
                            border: '1px solid #d1d5db',
                            borderRadius: '6px',
                            padding: '6px',
                            color: '#6b7280',
                            cursor: 'pointer',
                            fontSize: '14px',
                            transition: 'all 0.2s ease',
                            minWidth: '32px',
                            width: '32px',
                            height: '32px',
                          }}
                        >
                          <UIImage src='/assets/images/system/ico-system-24-outline-gray-search.svg' alt='No data' className='w-20 h-20' />
                        </button>

                        <button
                          onClick={() => handleRemoveInput(index)}
                          className='btn-icon btn btn-sm btn-light text-primary flex-shrink-0 btn-bg-del cursor-pointer'
                          style={{
                            border: '1px solid #d1d5db',
                            borderRadius: '6px',
                            padding: '6px',
                            color: '#6b7280',
                            cursor: 'pointer',
                            fontSize: '14px',
                            transition: 'all 0.2s ease',
                            minWidth: '32px',
                            width: '32px',
                            height: '32px',
                          }}
                        >
                          <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
                        </button>
                      </div>
                    </div>
                  ))}

                  <div className='mt-2 flex justify-center'>
                    <button onClick={handleAddInput} className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'>
                      출력 추가
                    </button>
                  </div>
                </div>
              </div>
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
