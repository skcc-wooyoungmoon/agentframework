// noinspection RegExpRedundantEscape

import { edgesAtom, keyTableAtom, nodesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Card, CardBody, CardFooter, DefaultButton } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { BaseTable } from '@/components/agents/builder/pages/table/base/BaseTable.tsx';
import { createKeyTableColumns, keyTableColumnsConfig } from '@/components/agents/builder/pages/table/common/AgentColumn.tsx';
import { type CustomNode, type CustomNodeInnerData, type KeyTableData, type OutputChatDataSchema } from '@/components/agents/builder/types/Agents';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import React, { useEffect, useMemo, useRef, useState } from 'react';

import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

export const OutputFormatterNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { nodes, removeNode, syncNodeData, toggleNodeView } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isToggle: false,
  };
  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;

  useNodeTracing(id, data.name as string, data, nodeData);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const currentInnerData = data.innerData ?? newInnerData;
    setNodeStatus(getNodeStatus(currentInnerData.isRun, currentInnerData.isDone, currentInnerData.isError));
  }, [data.innerData?.isRun, data.innerData?.isDone, data.innerData?.isError, data.innerData]);

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || '');
  const [selectedKey, setSelectedKey] = useState<string>((data as any).selected_key || 'select key');

  const nodesUpdatedRef = useRef(false);

  const { syncAllNodeKeyTable } = useGraphActions();
  const [keyTableList] = useAtom(keyTableAtom);
  const [edges] = useAtom(edgesAtom);
  const [allNodes] = useAtom(nodesAtom);
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const [text, setText] = useState<string>((data as OutputChatDataSchema).format_string || '{{key}}');
  const [isManuallyEdited, setIsManuallyEdited] = useState(false);
  const previousEdgesRef = useRef<string>('');
  const initialMountRef = useRef(true);

  useEffect(() => {
    const incoming = (data as any).selected_key;
    if (incoming && incoming !== selectedKey) {
      setSelectedKey(incoming);
    }
  }, [(data as any).selected_key]);

  useEffect(() => {
    const currentText = text || (data as OutputChatDataSchema).format_string || '';
    if (!currentText) {
      return;
    }

    const tokenRegex = /\{\{([^}]+)\}\}/g;
    const tokens = Array.from(currentText.matchAll(tokenRegex), (match: RegExpMatchArray) => match[1]);

    if (tokens.length === 0) {
      return;
    }

    const missingTokens: string[] = [];
    tokens.forEach(token => {
      const keyTableItem = keyTableList.find(item => item.id === token);
      if (!keyTableItem) {
        missingTokens.push(token);
      }
    });

    if (missingTokens.length > 0) {
      const incomingEdges = edges.filter(edge => edge.target === id);
      const sourceNodes = incomingEdges
        .map(edge => allNodes.find(node => node.id === edge.source))
        .filter((node): node is CustomNode => node !== undefined);

      sourceNodes.forEach(sourceNode => {
        const outputKeys = Array.isArray((sourceNode.data as any)?.output_keys)
          ? (sourceNode.data as any).output_keys
          : [];

        outputKeys.forEach((key: any) => {
          if (key && key.keytable_id && missingTokens.includes(key.keytable_id)) {
            const existingItem = keyTableList.find(item => item.id === key.keytable_id);
            if (!existingItem) {
              syncAllNodeKeyTable();
            }
          }
        });
      });
    }
  }, [text, keyTableList, edges, allNodes, id, data, syncAllNodeKeyTable]);

  useEffect(() => {
    const incomingEdges = edges.filter(edge => edge.target === id);

    const currentEdgesKey = incomingEdges
      .map(edge => `${edge.source}:${edge.sourceHandle || ''}`)
      .sort()
      .join('|');

    previousEdgesRef.current = currentEdgesKey;
    if (!initialMountRef.current && isManuallyEdited) return;
    const currentText = text || (data as OutputChatDataSchema).format_string || '';
    const isEmpty = !currentText || currentText.trim() === '' || currentText.trim() === ' ';

    if (initialMountRef.current && isEmpty) {
      if (incomingEdges.length === 0) {
        setText('{{key}}');
        nodesUpdatedRef.current = true;
        initialMountRef.current = false;
        return;
      }
    }

    if (initialMountRef.current) {
      initialMountRef.current = false;
    }

    if (incomingEdges.length === 0) return;

    const sourceNodes = incomingEdges
      .map(edge => {
        const node = allNodes.find(n => n.id === edge.source);
        return node ? { node, edge } : null;
      })
      .filter((item): item is { node: CustomNode; edge: any } => item !== null);

    if (sourceNodes.length === 0) return;

    let targetSourceNode: CustomNode | null = null;
    const reviewerPassSource = sourceNodes.find(({ edge }) => {
      const sourceHandle = edge.sourceHandle || edge.data?.sourceHandle || edge.data?.source_handle || '';
      return sourceHandle === 'reviewer_pass' || sourceHandle.includes('reviewer_pass');
    });

    if (reviewerPassSource) {
      targetSourceNode = reviewerPassSource.node;
    } else {
      targetSourceNode = sourceNodes[0].node;
    }

    if (!targetSourceNode) return;

    let targetOutputKeys: string[] = [];

    if (targetSourceNode.type === 'agent__reviewer') {
      const reviewerIncomingEdges = edges.filter(edge => edge.target === targetSourceNode!.id);
      const generatorNode = reviewerIncomingEdges
        .map(edge => allNodes.find(node => node.id === edge.source))
        .find(node => node && node.type === 'agent__generator');

      if (generatorNode) {
        const generatorOutputKeys = Array.isArray((generatorNode.data as any)?.output_keys)
          ? (generatorNode.data as any).output_keys
          : [];

        const contentKey = generatorOutputKeys.find((key: any) => {
          const keyName = key?.name || key?.key || '';
          return keyName === 'content' || keyName.includes('content');
        });

        if (contentKey && contentKey.keytable_id) {
          targetOutputKeys = [contentKey.keytable_id];
        }
      }
    } else if (targetSourceNode.type === 'union' || targetSourceNode.type === 'merger') {
      const outputKeys = Array.isArray((targetSourceNode.data as any)?.output_keys)
        ? (targetSourceNode.data as any).output_keys
        : [];

      if (outputKeys.length > 0 && outputKeys[0]?.keytable_id) {
        targetOutputKeys = [outputKeys[0].keytable_id];
      }
    } else {
      const outputKeys = Array.isArray((targetSourceNode.data as any)?.output_keys)
        ? (targetSourceNode.data as any).output_keys
        : [];

      if (outputKeys.length > 0 && outputKeys[0]?.keytable_id) {
        targetOutputKeys = [outputKeys[0].keytable_id];
      }
    }

    if (isEmpty) {
      if (targetOutputKeys.length > 0) {
        const desiredFormatString = `{{${targetOutputKeys[0]}}}`;
        setText(desiredFormatString);
        nodesUpdatedRef.current = true;
      } else {
        setText('{{key}}');
        nodesUpdatedRef.current = true;
      }
    }
  }, [edges, allNodes, id, data, text, isManuallyEdited]);


  const syncOutputData = () => {
    const newInnerData = {
      ...nodeData,
    };

    const newData = {
      ...data,
      type: type,
      id: id,
      name: nodeName,
      description: description,
      format_string: text || ' ',
      selected_key: selectedKey,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  };

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncOutputData();
      nodesUpdatedRef.current = false;
    }
  }, [text, nodeName, description, selectedKey]);

  const handleOpenKeyTableModal = (targetKey: string, keyIndex: number, lineIndex: number) => {
    syncAllNodeKeyTable();

    const editingKeyIndex = keyIndex;
    const editingLineIndex = lineIndex;

    const currentKey = keyTableList.find(key => key.id === targetKey);
    let tempKeyTableId: string | null = currentKey ? currentKey.id : null;

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
        if (tempKeyTableId) {
          const currentText = text || (data as OutputChatDataSchema).format_string || '{{key}}';
          const lines = currentText.split('\n');

          const targetLine = lines[editingLineIndex];
          if (targetLine) {
            const regex = /\{\{([^}]+)\}\}/g;
            let count = 0;
            const newLine = targetLine.replace(regex, (match) => {
              if (count === editingKeyIndex) {
                count++;
                return `{{${tempKeyTableId}}}`;
              }
              count++;
              return match;
            });
            lines[editingLineIndex] = newLine;
          }

          const newText = lines.join('\n');

          setText(newText);
          setSelectedKey(tempKeyTableId);
          setIsManuallyEdited(true);
          const updatedData = {
            ...data,
            type: type,
            id: id,
            name: nodeName,
            description: description,
            format_string: newText || ' ',
            selected_key: tempKeyTableId,
            innerData: nodeData,
          };

          syncNodeData(id, updatedData);
          nodesUpdatedRef.current = true;
        }
      },
    });
  };

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

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

  if (!node) return null;
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();
  return (
    <div ref={containerRef}>
      <Card className={[
        'agent-card w-full min-w-[500px] max-w-[500px]',
        nodeStatus].filter(e => !!e).join(' ')}
      >
        <Handle
          type='target'
          id='output_formatter_left'
          key={`output_formatter_left_${nodeData.isToggle}_${Date.now()}`}
          isConnectable={true}
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
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
        />
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
                  value={description}
                  onChange={e => {
                    const value = e.target.value;
                    if (value.length <= 100) {
                      setDescription(value);
                      nodesUpdatedRef.current = true;
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
                          setDescription(value);
                          nodesUpdatedRef.current = true;
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

              <div className='border-t border-gray-200'>
                <CardBody className='p-4'>
                  <div className='mb-4 w-full pt-5 flex flex-col gap-3'>
                    <label className='fw-bold form-label text-lg mb-2'>{'출력 형식'}</label>
                    <textarea
                      className={`w-full resize-none border rounded-lg p-3 focus:outline-none ${'border-gray-300 focus:border-gray-400'}`}
                      rows={5}
                      value={text}
                      onChange={e => {
                        setText(e.target.value);
                        setIsManuallyEdited(true);
                        nodesUpdatedRef.current = true;
                      }}
                      placeholder='출력 형식 입력 (예: {{query}}에 대한 답변입니다)'
                      onMouseDown={stopPropagation}
                      onMouseUp={stopPropagation}
                      onSelect={stopPropagation}
                      onDragStart={preventAndStop}
                      onDrag={preventAndStop}
                    ></textarea>

                    <label className='form-label font-light'>출력 문장에 사용할 변수를 {'{{key}}'} 형식으로 입력해주세요.</label>
                    <div className='mt-4'>
                      <div className='w-full min-h-[80px] border border-gray-300 rounded-lg p-3 bg-gray-50 relative'>
                        <div className='text-gray-700 flex flex-col items-start gap-2'>
                          {!text || text.trim() === '' ? (
                            <span className='text-gray-400'>여기에 텍스트가 표시됩니다...</span>
                          ) : (
                            (() => {
                              const lines = text.split('\n');

                              return lines.map((line, lineIdx) => {
                                if (!line.trim()) {
                                  return <div key={lineIdx} className='h-4' />;
                                }

                                const parts: (string | { type: 'button'; key: string; index: number })[] = [];
                                const regex = /\{\{([^}]+)\}\}/g;
                                let lastIndex = 0;
                                let match;
                                let buttonIndex = 0;

                                while ((match = regex.exec(line)) !== null) {
                                  if (match.index > lastIndex) {
                                    parts.push(line.substring(lastIndex, match.index));
                                  }

                                  const keyContent = match[1];
                                  const hasKorean = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/.test(keyContent);

                                  if (hasKorean) {
                                    parts.push(`{{${keyContent}}}`);
                                  } else {
                                    parts.push({
                                      type: 'button',
                                      key: keyContent,
                                      index: buttonIndex++,
                                    });
                                  }

                                  lastIndex = regex.lastIndex;
                                }

                                if (lastIndex < line.length) {
                                  parts.push(line.substring(lastIndex));
                                }

                                return (
                                  <div key={lineIdx} className='flex flex-wrap items-center gap-1'>
                                    {parts.map((part, idx) => {
                                      if (typeof part === 'string') {
                                        return <span key={idx}>{part}</span>;
                                      } else {
                                        const keyTableItem = keyTableList.find(item => item.id === part.key);

                                        let isValidToken = keyTableItem !== undefined;
                                        let displayName = part.key;

                                        if (!isValidToken) {
                                          const incomingEdges = edges.filter(edge => edge.target === id);
                                          for (const edge of incomingEdges) {
                                            const sourceNode = allNodes.find(node => node.id === edge.source);
                                            if (sourceNode) {
                                              const outputKeys = Array.isArray((sourceNode.data as any)?.output_keys)
                                                ? (sourceNode.data as any).output_keys
                                                : [];
                                              const matchingKey = outputKeys.find((key: any) => key && key.keytable_id === part.key);
                                              if (matchingKey) {
                                                isValidToken = true;
                                                displayName = matchingKey.name || part.key;
                                                break;
                                              }
                                            }
                                          }
                                        } else if (keyTableItem) {
                                          displayName = keyTableItem.isGlobal
                                            ? keyTableItem.key
                                            : `${keyTableItem.nodeName}__${keyTableItem.key}`;
                                        }

                                        const isKeyTableId = isValidToken;

                                        return (
                                          <DefaultButton
                                            key={idx}
                                            color={isKeyTableId ? 'primary' : 'success'}
                                            onClick={() => {
                                              handleOpenKeyTableModal(part.key, part.index, lineIdx);
                                            }}
                                          >
                                            {isKeyTableId ? displayName : 'select key'}
                                          </DefaultButton>
                                        );
                                      }
                                    })}
                                  </div>
                                );
                              });
                            })()
                          )}
                        </div>
                      </div>
                    </div>
                  </div>
                </CardBody>
              </div>
            </>
          )}
        </>

        <CardFooter className='p-0'>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
