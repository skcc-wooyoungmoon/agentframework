import { keyTableAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Card, CardBody, CardFooter, DefaultButton } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { KeyTableFilter } from '@/components/agents/builder/pages/graph/common/KeyTableFilter';
import { BaseTable } from '@/components/agents/builder/pages/table/base/BaseTable.tsx';
import { createKeyTableColumns, keyTableColumnsConfig } from '@/components/agents/builder/pages/table/common/AgentColumn.tsx';
import { type CustomNode, type CustomNodeInnerData, type KeyTableData, NodeType, type OutputChatDataSchema } from '@/components/agents/builder/types/Agents';
import { type InputKeyItem, type OutputKeyItem, type UnionDataSchema } from '@/components/agents/builder/types/Agents.ts';
import keyTableData from '@/components/agents/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import React, { Fragment, useEffect, useMemo, useRef, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface Token {
  id: string;
  text: string;
}

export const UnionNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { nodes, removeNode, syncNodeData, toggleNodeView, syncAllNodeKeyTable } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const schemaData: UnionDataSchema = data as UnionDataSchema;
  const [keyTableList] = useAtom(keyTableAtom);

  const initialOutputKeys = useMemo(() => {
    const initOutputItems: OutputKeyItem[] = (schemaData.output_keys as OutputKeyItem[]) || [];
    return (schemaData.output_keys as OutputKeyItem[]) || initOutputItems;
  }, [schemaData.output_keys]);
  const [outputKeys, setOutputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);
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

  const [isValueInKeyTable, setIsValueInKeyTable] = useState<boolean>(false);
  const [tempValue, setTempValue] = useState<string>('');
  const [filteredKeyTableList, setFilteredKeyTableList] = useState<KeyTableData[]>(keyTableList);
  const [selectedRowIdInTable, setSelectedRowIdInTable] = useState<string | null>(null);
  const isSelectableRef = useRef<boolean>(false);

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
  const [description, setDescription] = useState((data.description as string) || (keyTableData['union']['field_default']['description'] as string));
  const nodesUpdatedRef = useRef(false);

  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  const initText = useMemo(() => {
    return ((data as OutputChatDataSchema).format_string as string) || '{{key}}';
  }, [data]);
  const { tokens: initTokens, keyMap: initKeyMap } = useMemo(() => {
    const newTokens: Token[] = [];
    const newKeyMap: Record<string, string> = {};
    const inputKeysData = (data as UnionDataSchema).input_keys || [];

    const parts = initText.split(/(\{\{.*?\}\})/g);

    parts.forEach(part => {
      const match = part.match(/^\{\{(\w+)\}\}$/);
      if (match) {
        const key = match[1];
        const tokenId = key;
        newTokens.push({ id: tokenId, text: `{{${key}}}` });

        const inputKeyItem = inputKeysData.find((item: InputKeyItem) => item.name === key);
        if (inputKeyItem && inputKeyItem.keytable_id) {
          newKeyMap[tokenId] = inputKeyItem.keytable_id;
        }
      } else {
        newTokens.push({ id: uuidv4(), text: part });
      }
    });

    return { tokens: newTokens, keyMap: newKeyMap };
  }, [initText, keyTableList]);

  const [text, setText] = useState(initText || '');
  const [, setInputIndex] = useState(0);
  const [tokens, setTokens] = useState<Token[]>(initTokens || []);
  const [keyMap, setKeyMap] = useState<Record<string, string>>({ ...initKeyMap });
  const [selectedKeys, setSelectedKeys] = useState<Record<string, string>>({});

  useEffect(() => {
    setFilteredKeyTableList(keyTableList);
  }, [keyTableList]);

  useEffect(() => {
    const newSelectedKeys: Record<string, string> = {};
    tokens.forEach(token => {
      const keyTableId = keyMap[token.id];
      if (keyTableId) {
        const findKeyTable = keyTableList.find(key => key.id === keyTableId);
        if (findKeyTable) {
          const keyName = findKeyTable.isGlobal ? findKeyTable.key : `${findKeyTable.nodeName}_${findKeyTable.key}`;
          newSelectedKeys[token.id] = keyName;
        }
      }
    });
    setSelectedKeys(newSelectedKeys);
  }, [tokens, keyMap, keyTableList]);

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const input = e.target.value;
    const newTokens: Token[] = [];
    const newKeyMap: Record<string, string> = {};
    const newSelectedKeys: Record<string, string> = {};

    const parts = input.split(/(\{\{.*?\}\})/g);

    parts.forEach(part => {
      const match = part.match(/^\{\{(\w+)\}\}$/);
      if (match) {
        const key = match[1];

        const existingToken = tokens.find(t => {
          const tokenMatch = t.text.match(/^\{\{(\w+)\}\}$/);
          return tokenMatch && tokenMatch[1] === key;
        });

        let tokenId: string;
        if (existingToken && keyMap[existingToken.id]) {
          tokenId = existingToken.id;
          newKeyMap[tokenId] = keyMap[existingToken.id];
          if (selectedKeys[existingToken.id]) {
            newSelectedKeys[tokenId] = selectedKeys[existingToken.id];
          }
        } else {
          tokenId = uuidv4();
          newKeyMap[tokenId] = key;
        }

        newTokens.push({ id: tokenId, text: `{{${key}}}` });
      } else {
        newTokens.push({ id: uuidv4(), text: part });
      }
    });

    setKeyMap(newKeyMap);
    setSelectedKeys(prev => ({ ...prev, ...newSelectedKeys }));
    setTokens(newTokens);
    setText(input);
  };
  const handleOpenKeyTableModal = (tokenId: string, _currentKey: string, index: number) => {
    syncAllNodeKeyTable();
    setInputIndex(index);

    const editingTokenId = tokenId;
    let tempKeyTableId: string | null = null;

    if (keyMap[editingTokenId]) {
      tempKeyTableId = keyMap[editingTokenId];
    }

    const latestKeyTableListAtOpen = [...keyTableList];

    const KeyTableModalBody = () => {
      const [localSelectedId, setLocalSelectedId] = useState<string | null>(tempKeyTableId);
      const [currentKeyTableList] = useAtom(keyTableAtom);


      const keyTableColumns = useMemo(
        () =>
          createKeyTableColumns(localSelectedId, (id: string) => {
            setLocalSelectedId(id);
            tempKeyTableId = id;
          }),
        [localSelectedId]
      );

      return (
        <div className='flex flex-col'>
          <div className='mt-4'>
            <div className='max-h-[400px] overflow-y-auto'>
              <BaseTable<KeyTableData>
                data={currentKeyTableList}
                columns={keyTableColumns}
                columnsWithWidth={keyTableColumnsConfig}
                maxHeight={'400px'}
                selectedRowId={localSelectedId}
                isSelectable={true}
                hideEmptyMessage={true}
                onRowClick={(oneKey: any) => {
                  setLocalSelectedId(oneKey.id);
                  tempKeyTableId = oneKey.id;
                }}
              />
            </div>
          </div>
        </div>
      );
    };

    openModal({
      title: '키테이블',
      type: 'medium',
      body: <KeyTableModalBody />,
      showFooter: true,
      cancelText: '취소',
      confirmText: '저장',
      onConfirm: () => {
        if (tempKeyTableId && editingTokenId) {
          const findKeyTable = latestKeyTableListAtOpen.find(key => key.id === tempKeyTableId);
          if (findKeyTable) {
            const keyName = findKeyTable.isGlobal ? findKeyTable.key : `${findKeyTable.nodeName}_${findKeyTable.key}`;
            const keyTableId = tempKeyTableId;

            setKeyMap(prev => ({
              ...prev,
              [editingTokenId]: keyTableId,
            }));

            setSelectedKeys(prev => ({
              ...prev,
              [editingTokenId]: keyName,
            }));

            setTokens(prevTokens => {
              const updatedTokens = prevTokens.map(token => (token.id === editingTokenId ? { ...token, text: `{{${keyTableId}}}` } : token));
              const newText = updatedTokens.map(token => token.text).join('');
              setText(newText);
              return updatedTokens;
            });
          }
        }
      },
    });
  };

  useEffect(() => {
    syncInputData();
  }, [nodeName, description, text, keyMap, outputKeys]);

  const containerRef = useAutoUpdateNodeInternals(id);

  const node = nodes.find(node => node.id === id);

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

    const newInputData: InputKeyItem[] = [];
    tokens.forEach(token => {
      const match = token.text.match(/^\{\{(\w+)\}\}$/);
      if (match) {
        const keyName = match[1];
        const keytableId = keyMap[token.id] || '';

        const exists = newInputData.find(item => item.name === keyName);
        if (!exists) {
          newInputData.push({
            name: keyName,
            required: true,
            description: '',
            keytable_id: keytableId,
            fixed_value: null,
          });
        }
      }
    });

    const newData = {
      ...data,
      type: NodeType.AgentUnion.name,
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

  const cancelModal = () => {
    setIsValueInKeyTable(false);
    setTempValue('');
    setSelectedRowIdInTable(null);
    isSelectableRef.current = false;
  };

  const saveModal = () => {
    if (selectedRowIdInTable) {
      const selectedKey = filteredKeyTableList.find(key => key.id === selectedRowIdInTable);
      if (selectedKey) {
        setTempValue(selectedKey.key);
      }
    }
    cancelModal();
  };

  const handleSelect = (selectedKey: KeyTableData) => {
    setSelectedRowIdInTable(selectedKey.id);
    isSelectableRef.current = true;
  };

  if (!node) return null;
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();
  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='gen_left'
          position={Position.Left}
          isConnectable={true}
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
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
        />
        <>
          {nodeData.isToggle && (
            <div>
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
                    value={description}
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
                    <span className='text-blue-500'>{description.length}</span>/100
                  </div>
                </div>
                <p className='font-bold mt-2'>{'출력 형식'}</p>
                <div className='mt-2 flex flex-wrap items-center gap-1'>
                  {tokens.map((token, index) => {
                    const match = token.text.match(/^\{\{(\w+)\}\}$/);
                    if (!match) return null;
                    const currentKey = selectedKeys[token.id] || 'select key';
                    const isSelected = currentKey !== 'select key';
                    return (
                      <DefaultButton
                        key={`${token.id}_${index}`}
                        color={isSelected ? 'primary' : 'success'}
                        className='mr-1 mb-1'
                        onClick={() => handleOpenKeyTableModal(token.id, currentKey, index)}
                      >
                        {isSelected ? currentKey : 'select key'}
                      </DefaultButton>
                    );
                  })}
                  {tokens.filter(t => t.text.match(/^\{\{(\w+)\}\}$/)).length === 0 && <span className='text-xs text-gray-500'>출력 키가 없습니다.</span>}
                </div>
              </div>
            </div>
          )}

          {!nodeData.isToggle && (
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
                      <span className='text-blue-500'>{description.length}</span>/100
                    </div>
                  </div>
                </div>
              </CardBody>

              <hr className='border-gray-200' />

              <div className='card-body p-4'>
                <div className='mb-4'>
                  <div className='py-4'>
                    <label className='fw-bold form-label mb-4 text-lg'>{'FormatString'}</label>
                    <textarea
                      className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                      rows={5}
                      value={text}
                      onChange={handleChange}
                    ></textarea>
                    <div className=''>
                      <label className='form-label font-light'>출력 문장에 사용할 변수를 {'{{key}}'} 형식으로 입력해주세요.</label>

                    </div>
                  </div>

                  <div className='mt-4 bg-gray-50 px-4 py-3 border-t border-gray-200'>
                    <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
                  </div>
                  <div className='bg-white p-3 shadow-sm'>
                    <div className='flex w-full'>
                      <div className='w-[350px] pr-2'>
                        <h4 className='mb-4 w-full text-center text-lg font-semibold'>{'입력'}</h4>
                        <div className='mt-4 min-h-[80px] p-1'>
                          {tokens.map((token, index) => {
                            const match = token.text.match(/^\{\{(\w+)\}\}$/);
                            if (match) {
                              const currentKey = selectedKeys[token.id] || 'select key';
                              const isSelected = currentKey !== 'select key';
                              return (
                                <Fragment key={index}>
                                  <DefaultButton
                                    key={token.id}
                                    color={isSelected ? 'primary' : 'success'}
                                    className='mr-1 mb-1'
                                    onClick={() => handleOpenKeyTableModal(token.id, currentKey, index)}
                                  >
                                    {isSelected ? currentKey : 'select key'}
                                  </DefaultButton>
                                  <br />
                                </Fragment>
                              );
                            }
                          })}
                        </div>
                      </div>
                      <div className='mx-4 w-px bg-gray-300' />
                      <div className='w-[180px] pl-2'>
                        <h4 className='mb-4 text-center text-lg font-semibold'>{'출력'}</h4>
                        <div className={`min-h-[100px] ${data.innerData?.isToggle ? 'hidden' : ''}`}>
                          <div className='flex w-full flex-col'>
                            {outputKeys.map((item, index) => (
                              <div key={index} className='w-full rounded-lg'>
                                <input
                                  type='text'
                                  className='w-full h-[38px] leading-[38px] text-sm text-gray-500 p-3 bg-gray-50 rounded-lg border border-gray-300'
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
            background: '#000000',
            top: '50%',
            transform: 'translateY(-50%)',
            right: -10,
            zIndex: 20,
          }}
        />
        <div>
          {isValueInKeyTable && (
            <div
              className='fixed left-1/2 top-1/2 z-[9999] -translate-x-1/2 -translate-y-1/2 transform rounded-lg border border-gray-300 bg-white shadow-lg'
              style={{ width: 480 }}
            >
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
                    columns={useMemo(
                      () =>
                        createKeyTableColumns(selectedRowIdInTable, (id: string) => {
                          setSelectedRowIdInTable(id);
                        }),
                      [selectedRowIdInTable]
                    )}
                    columnsWithWidth={keyTableColumnsConfig}
                    maxHeight={'400px'}
                    hideEmptyMessage={true}
                    selectedRowId={selectedRowIdInTable}
                    isSelectable={isSelectableRef.current}
                    onRowClick={handleSelect}
                  />
                </div>

                <div className='mr-2 flex w-full justify-end gap-2.5 p-2'>
                  <DefaultButton color={'light'} onClick={cancelModal}>
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
    </div>
  );
};
