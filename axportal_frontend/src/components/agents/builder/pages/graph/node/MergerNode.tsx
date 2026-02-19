// noinspection RegExpRedundantEscape

import { keyTableAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { DefaultButton } from '@/components/agents/builder/common/Button/DefaultButton';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { DefaultTooltip } from '@/components/agents/builder/common/tooltip/Tooltip';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { KeyTableFilter } from '@/components/agents/builder/pages/graph/common/KeyTableFilter';
import { BaseTable } from '@/components/agents/builder/pages/table/base/BaseTable.tsx';
import { createKeyTableColumns, keyTableColumnsConfig } from '@/components/agents/builder/pages/table/common/AgentColumn.tsx';
import {
  type CustomNode,
  type CustomNodeInnerData,
  type InputKeyItem,
  type KeyTableData,
  type MergerDataSchema,
  NodeType,
  type OutputChatDataSchema,
  type OutputKeyItem,
} from '@/components/agents/builder/types/Agents';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import React, { Fragment, useEffect, useMemo, useRef, useState } from 'react';

import { v4 as uuidv4 } from 'uuid';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface TokenButtonProps {
  id: string;
  label: string;
  onClick: () => void;
}

interface Token {
  id: string;
  text: string;
}

const TokenButton: React.FC<TokenButtonProps> = ({ id, label, onClick }) => {
  const compareSelectKey = 'select key';
  return (
    <button
      key={`${id}_${label}`}
      className={`mb-1 mr-1 rounded ${label === compareSelectKey ? 'bg-blue-500 hover:bg-blue-600' : 'bg-blue-500 hover:bg-blue-600'} px-2 py-1 text-white`}
      onClick={onClick}
    >
      {label}
    </button>
  );
};

export const MergerNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { nodes, removeNode, syncNodeData, toggleNodeView, syncAllNodeKeyTable } = useGraphActions();
  const schemaData: MergerDataSchema = data as MergerDataSchema;

  const initialOutputKeys = useMemo(() => {
    const initOutputItems: OutputKeyItem[] = (schemaData.output_keys as OutputKeyItem[]) || [];
    return (schemaData.output_keys as OutputKeyItem[]) || initOutputItems;
  }, [schemaData.output_keys]);
  const [outputKeys, setOutputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);
  const [inputKeys, setInputKeys] = useState<string[]>([]);

  const isInitialMountRef = useRef(true);
  const prevDataRef = useRef(data);

  useEffect(() => {
    if (isInitialMountRef.current) {
      isInitialMountRef.current = false;
      prevDataRef.current = data;
      return;
    }
    const dataChanged = prevDataRef.current !== data;
    if (!dataChanged) {
      return;
    }

    const newOutputKeys = (schemaData.output_keys as OutputKeyItem[]) || [];
    const hasEmptyOutputKeys = outputKeys.length === 0;
    if (newOutputKeys.length > 0 && hasEmptyOutputKeys) {
      setOutputKeys(newOutputKeys);
    }

    prevDataRef.current = data;
  }, [data, schemaData.output_keys]);

  const changeOutPutName = (index: number, value: string) => {
    const newOutputKeys = [...outputKeys];
    newOutputKeys[index].name = value;
    syncNodeData(id, {
      ...data,
      output_keys: newOutputKeys,
    });
  };

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

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || '');
  const nodesUpdatedRef = useRef(false);

  const [keyTableList] = useAtom(keyTableAtom);
  const [filteredKeyTableList, setFilteredKeyTableList] = useState<KeyTableData[]>(keyTableList);
  const keyTableColumns = useMemo(() => createKeyTableColumns(), []);
  const isSelectableRef = useRef(true);
  const [selectedRowIdInTable, setSelectedRowIdInTable] = useState<string | null>(null);
  const [isValueInKeyTable, setIsValueInKeyTable] = useState<boolean>(false);
  const [tempValue, setTempValue] = useState<string | null>(null);
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const initText = useMemo(() => {
    return ((data as OutputChatDataSchema).format_string as string) || '';
  }, [data]);
  const { tokens: initTokens, keyMap: initKeyMap } = useMemo(() => {
    const newTokens: Token[] = [];
    const newKeyMap: Record<string, string> = {};

    const parts = initText.split(/(\{\{.*?\}\})/g);

    parts.forEach(part => {
      const match = part.match(/^\{\{(\w+)\}\}$/);
      if (match) {
        const key = match[1];
        newTokens.push({ id: key, text: `{{${key}}}` });
        newKeyMap[key] = key;
      } else {
        newTokens.push({ id: uuidv4(), text: part });
      }
    });

    return { tokens: newTokens, keyMap: newKeyMap };
  }, [initText, keyTableList]);

  const [text, setText] = useState(initText || '');
  const [_inputIdex, setInputIndex] = useState(0);
  const [tokens, setTokens] = useState<Token[]>(initTokens || []);
  const [keyMap, setKeyMap] = useState<Record<string, string>>({
    ...initKeyMap,
  });

  const [currentEditingTokenId, setCurrentEditingTokenId] = useState<string | null>(null);

  useEffect(() => {
    setFilteredKeyTableList(keyTableList);
  }, [keyTableList]);

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const input = e.target.value;
    const newTokens: Token[] = [];
    const newKeyMap: Record<string, string> = {};

    const parts = input.split(/(\{\{.*?\}\})/g);

    parts.forEach(part => {
      const match = part.match(/^\{\{(\w+)\}\}$/);
      if (match) {
        const key = match[1];
        const id = uuidv4();
        newTokens.push({ id, text: `{{${key}}}` });
        newKeyMap[id] = key;
      } else {
        newTokens.push({ id: uuidv4(), text: part });
      }
    });

    setKeyMap(newKeyMap);
    setTokens(newTokens);
    setText(input);
  };
  const openKeyTableModal = (tokenId: string, _currentKey: string, index: number) => {
    syncAllNodeKeyTable();
    setInputIndex(index);

    setSelectedRowIdInTable(null);
    setIsValueInKeyTable(true);
    setCurrentEditingTokenId(tokenId);
  };

  const handleSelect = (oneKey: KeyTableData) => {
    setSelectedRowIdInTable(oneKey.id);
    setTempValue(oneKey.key);
  };

  const cancelModal = () => {
    setTempValue(null);
    setIsValueInKeyTable(false);
    setCurrentEditingTokenId(null);
  };

  const saveModal = () => {
    if (currentEditingTokenId && selectedRowIdInTable) {
      setKeyMap(prev => ({
        ...prev,
        [currentEditingTokenId]: selectedRowIdInTable,
      }));

      setTokens(prevTokens => prevTokens.map(token => (token.id === currentEditingTokenId ? { ...token, text: `{{${selectedRowIdInTable}}}` } : token)));

      const newText = tokens.map(token => (token.id === currentEditingTokenId ? `{{${selectedRowIdInTable}}}` : token.text)).join('');
      setText(newText);
    }
    cancelModal();
  };

  useEffect(() => {
    const newInputKeys: string[] = [];
    Object.values(keyMap).map(key => newInputKeys.push(key));
    setInputKeys(newInputKeys);
  }, [keyMap]);

  useEffect(() => {
    syncInputData();
  }, [nodeName, description, text, inputKeys, outputKeys]);

  const node = nodes.find(node => node.id === id);
  if (!node) return null;

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

  const syncInputData = () => {
    const newInnerData = {
      ...nodeData,
    };

    const newInputData = inputKeys.map(key => ({
      name: key,
      required: true,
      description: '',
      keytable_id: key,
      fixed_value: null,
    })) as InputKeyItem[];

    const newData = {
      ...data,
      type: NodeType.AgentMerger.name,
      id: id,
      name: nodeName,
      description: description,
      format_string: text,
      input_keys: newInputData,
      output_keys: outputKeys,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  };

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
    nodesUpdatedRef.current = true;
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleDescriptionChange = (val: string) => {
    setDescription(val);
    nodesUpdatedRef.current = true;
  };
  const { stopPropagation, preventAndStop } = useNodeHandler()
  return (
    <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
      <Handle
        type='target'
        position={Position.Left}
        isConnectable={true}
        style={{
          width: 20,
          height: 20,
          top: nodeData.isToggle ? 'calc(50% + 20px)' : 'calc(50% + 400px)',
          transform: 'translateY(-50%)',
          left: -10,
          background: '#000000',
          border: '2px solid white',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          zIndex: 10,
        }}
      />
      <NodeHeader nodeId={id} type={type} data={nodeData} onClickDelete={onClickDelete} defaultValue={nodeName} onChange={handleNodeNameChange} onClickLog={handleHeaderClickLog} />
      <>
        {nodeData.isToggle && (
          <div>
            <div className='bg-gray-100 px-4 py-2 pl-8 text-sm text-gray-700'>
              <p className='font-bold'>{'설명'}</p>
              <DefaultTooltip title={description || '설명이 없습니다.'} placement='top'>
                <span className='block max-w-xl truncate'>{description || '설명이 없습니다.'}</span>
              </DefaultTooltip>
              <p className='font-bold'>{'설명'}</p>
              <textarea className='w-full resize-none border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none' rows={5} value={text} readOnly></textarea>
            </div>
          </div>
        )}

        {!nodeData.isToggle && (
          <>
            <CardBody>
              <div className='mb-4 w-auto'>
                <label className='fw-bold form-label mb-4 text-lg'>{'설명'}</label>
                <textarea
                  className='nodrag textarea w-full resize-none'
                  rows={4}
                  placeholder={'설명'}
                  value={description}
                  onChange={e => {
                    handleDescriptionChange(e.target.value);
                  }}
                  onMouseDown={stopPropagation}
                  onMouseUp={stopPropagation}
                  onSelect={stopPropagation}
                  onDragStart={preventAndStop}
                  onDrag={preventAndStop}
                ></textarea>
              </div>
            </CardBody>

            <hr className='border-gray-200' />

            <div className='card-body grid gap-5'>
              <div className='mb-4 w-auto'>
                <label className='fw-bold form-label mb-4 text-lg'>{'FormatString'}</label>
                <textarea
                  className='nodrag textarea w-full resize-none'
                  rows={5}
                  value={text}
                  onChange={handleChange}
                  onMouseDown={stopPropagation}
                  onMouseUp={stopPropagation}
                  onSelect={stopPropagation}
                  onDragStart={preventAndStop}
                  onDrag={preventAndStop}
                ></textarea>
                <label className='form-label font-light'>Format a result string with keys; {'{{key}}'} </label>

                <div className='mt-4 bg-gray-50 px-4 py-3 border-t border-gray-200'>
                  <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
                </div>

                <div className='bg-white p-3 shadow-sm'>
                  <div className='flex w-full min-w-[530px]'>
                    <div className='w-[350px] pr-2'>
                      <h4 className='mb-4 w-full text-center text-lg font-semibold'>{'입력'}</h4>
                      <div className='mt-4 min-h-[80px] p-1'>
                        {tokens.map((token, index) => {
                          const match = token.text.match(/^\{\{(\w+)\}\}$/);
                          if (match) {
                            let currentKey = keyMap[token.id] || 'select key';

                            const keyMapValue = keyMap[token.id];
                            if (keyMapValue) {
                              const findKeyTable = keyTableList.find(key => {
                                return key.id === keyMapValue;
                              });
                              if (findKeyTable) {
                                currentKey = `${findKeyTable.nodeName}_${findKeyTable.key}`;
                              } else {
                                currentKey = 'select key';
                              }
                            }

                            return (
                              <Fragment key={index}>
                                <TokenButton key={token.id} id={token.id} label={currentKey} onClick={() => openKeyTableModal(token.id, currentKey, index)} />
                                <br />
                              </Fragment>
                            );
                          }
                        })}
                      </div>
                    </div>
                    <div className='mx-4 w-px bg-gray-300' />
                    <div className='w-[180px] pl-2'>
                      <h4 className='mb-4 text-center text-lg font-semibold'>{'결과'}</h4>
                      <div className={`min-h-[100px] ${data.innerData?.isToggle ? 'hidden' : ''}`}>
                        <div className='flex w-full flex-col'>
                          {outputKeys.map((item, index) => (
                            <div key={index} className='w-full rounded-lg bg-white p-2 shadow-sm'>
                              <input
                                type='text'
                                className='input'
                                placeholder={item == null ? '' : item.name}
                                value={item.name}
                                onChange={e => {
                                  changeOutPutName(index, e.target.value);
                                }}
                              />
                            </div>
                          ))}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </>
        )}
      </>

      <CardFooter>
        <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
      </CardFooter>
      <Handle
        type='source'
        id='gen_right'
        position={Position.Right}
        isConnectable={true}
        style={{
          width: 20,
          height: 20,
          top: nodeData.isToggle ? 'calc(50% + 20px)' : 'calc(50% + 400px)',
          transform: 'translateY(-50%)',
          right: -8,
          background: '#000000',
          border: '2px solid white',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          zIndex: 10,
        }}
      />
      <div>
        {isValueInKeyTable && (
          <div className='fixed left-1/2 top-1/2 z-[9999] -translate-x-1/2 -translate-y-1/2 transform rounded-lg border border-gray-300 bg-white shadow-lg' style={{ width: 480 }}>
            <div className='w-full items-center'>
              <div className={'flex w-full justify-between px-5 pt-5'}>
                <div className=''>
                  <span className='text'>Value : </span>
                  <span className='text'>{tempValue}</span>
                </div>
              </div>
              <div className='max-h-[400px] overflow-y-auto'>
                <div className='ml-4 mt-4 flex w-full justify-start'>
                  <h1 className='text-lg font-bold text-gray-800'>{'Key Tables'}</h1>
                </div>
                <div className='mt-2 px-4'>
                  <KeyTableFilter keyTableList={keyTableList} onChange={setFilteredKeyTableList} />
                </div>
                <BaseTable<KeyTableData>
                  data={filteredKeyTableList}
                  columns={keyTableColumns}
                  columnsWithWidth={keyTableColumnsConfig}
                  maxHeight={'400px'}
                  selectedRowId={selectedRowIdInTable}
                  isSelectable={isSelectableRef.current}
                  hideEmptyMessage={true}
                  onRowClick={handleSelect}
                />
              </div>

              <div className='mr-2 flex w-full justify-end gap-2.5 p-2'>
                <DefaultButton color={'primary'} onClick={cancelModal}>
                  <span className='text-sm'>Cancel</span>
                </DefaultButton>
                <DefaultButton color={'primary'} onClick={saveModal}>
                  <span className='text-sm'>Save</span>
                </DefaultButton>
              </div>
            </div>
          </div>
        )}
      </div>
    </Card>
  );
};
