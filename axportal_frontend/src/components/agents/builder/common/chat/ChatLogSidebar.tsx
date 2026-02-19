import { messagesAtom, tracingMessagesAtom } from '@/components/agents/builder/atoms/messagesAtom.ts';
import { nodesAtom, edgesAtom, selectedKnowledgeNameRepoAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { stringUtils } from '@/utils/common';
import { useAtom } from 'jotai';
import { type FC, useMemo, useState, useEffect, useRef } from 'react';
import { StructuredLogView } from './StructuredLogView';

interface ChatLogSidebarProps {
  isVisible: boolean;
  onClose: () => void;
  agentId?: string;
  hasChatTested?: boolean;
}

const ChatLogSidebar: FC<ChatLogSidebarProps> = ({ isVisible, onClose, hasChatTested = false }) => {
  const [messages] = useAtom(messagesAtom);
  const [tracingMessages] = useAtom(tracingMessagesAtom);
  const [nodes] = useAtom(nodesAtom);
  const [edges] = useAtom(edgesAtom);
  const [selectedKnowledgeNameRepo] = useAtom(selectedKnowledgeNameRepoAtom);
  const [showRaw, setShowRaw] = useState(false);
  const logContainerRef = useRef<HTMLDivElement>(null);
  const prevLogsLengthRef = useRef<number>(0);

  const selectedKnowledgeNameRepoStr = JSON.stringify(selectedKnowledgeNameRepo);
  const selectedKnowledgeNameRepoParsed = useMemo(() => {
    return JSON.parse(selectedKnowledgeNameRepoStr) as Record<string, string>;
  }, [selectedKnowledgeNameRepoStr]);

  const nodesMap = useMemo(() => {
    const map = new Map<string, { id: string; name: string; data: any }>();
    nodes.forEach(node => {
      const nodeName = node.data?.name ? String(node.data.name) : node.id;
      map.set(node.id, {
        id: node.id,
        name: nodeName,
        data: node.data || {},
      });
      if (node.data?.name && String(node.data.name) !== node.id) {
        map.set(String(node.data.name), {
          id: node.id,
          name: String(node.data.name),
          data: node.data || {},
        });
      }
    });
    return map;
  }, [nodes]);

  const formattedLogs = useMemo(() => {
    if (tracingMessages && tracingMessages.length > 0) {
      const findNodeInfo = (nodeId: string) => {
        let nodeInfo = nodesMap.get(nodeId);
        if (!nodeInfo) {
          const possibleNodeName = nodeId.replace(/^retriever__(knowledge|rewriter_hyde|rewriter_multiquery)_?/, '');
          if (possibleNodeName) {
            nodeInfo = nodesMap.get(possibleNodeName);
          }
        }
        if (!nodeInfo) {
          const foundNode = nodes.find((n: any) => {
            const nodeName = n.data?.name ? String(n.data.name) : '';
            return n.id === nodeId || nodeName === nodeId || nodeId.includes(n.id) || nodeId.includes(nodeName);
          });
          if (foundNode) {
            return {
              id: foundNode.id,
              name: foundNode.data?.name ? String(foundNode.data.name) : foundNode.id,
              data: foundNode.data || {},
            };
          }
        }
        return nodeInfo || { id: nodeId, name: nodeId, data: {} };
      };

      const getKnowledgeName = (nodeId: string, nodeName: string, nodeData: any, updates: any, toolResult: any) => {
        const DELETED_STATE = '__DELETED__';
        const atomKnowledgeName =
          selectedKnowledgeNameRepoParsed[nodeId] === DELETED_STATE ? '' : selectedKnowledgeNameRepoParsed[nodeId] || selectedKnowledgeNameRepoParsed[nodeName] || '';

        const traceKnowledgeName =
          (updates as any)?.knowledge_name ||
          (updates as any)?._knowledgeName ||
          (toolResult as any)?.knowledge_name ||
          (toolResult as any)?._knowledgeName ||
          (updates as any)?.knowledge_retriever?.name ||
          (toolResult as any)?.knowledge_retriever?.name;

        const knowledgeRetriever = nodeData?.knowledge_retriever as { name?: string; repo_id?: string; knowledge_info?: any } | undefined;

        return (
          atomKnowledgeName ||
          traceKnowledgeName ||
          nodeData?._knowledgeName ||
          nodeData?.knowledge_name ||
          nodeData?.innerData?.knowledgeName ||
          nodeData?.schemaData?.knowledge_retriever?.name ||
          knowledgeRetriever?.name ||
          knowledgeRetriever?.knowledge_info?.name ||
          (nodeData?.knowledge_retriever as any)?.knowledge_info?.name ||
          ''
        );
      };

      const logs: Array<{ time: string; log: string; type: string; index: number; nodeId?: string; nodeX?: number; nodeY?: number; executionOrder?: number }> = [];
      const processedNodes = new Set<string>();
      const nodeExecutionOrderMap = new Map<string, number>();
      tracingMessages.forEach((trace, index) => {
        const nodeId = trace.node_name || trace.nodeName || trace.node_id || trace.nodeId || 'unknown';
        if (nodeId !== 'unknown' && !nodeExecutionOrderMap.has(nodeId)) {
          nodeExecutionOrderMap.set(nodeId, index);
        }
      });

      tracingMessages.forEach((trace, index) => {
        const nodeId = trace.node_name || trace.nodeName || trace.node_id || trace.nodeId || 'unknown';
        const nodeType = trace.node_type || trace.nodeType || '';
        const timeStamp = `[${(index * 0.1).toFixed(1)}s]`;
        const turn = typeof trace.turn === 'number' && trace.turn > 0 ? trace.turn : undefined;
        const turnLabel = turn ? `#${turn} ` : '';
        const turnKey = turn || '0';

        if ((trace.updates?.user_input || trace.log?.user_input) && !processedNodes.has(`user_${turnKey}`)) {
          const userInput = trace.updates?.user_input ?? trace.log?.user_input;
          const inputNode = nodes.find((n: any) => n.type === 'input__basic');
          const inputX = inputNode?.position?.x ?? -1000;
          const inputY = inputNode?.position?.y ?? 0;

          logs.push({
            time: timeStamp,
            log: `${turnLabel}👤 사용자 입력: ${userInput}`,
            type: 'user',
            index: index - 0.01,
            nodeX: inputX,
            nodeY: inputY,
            executionOrder: 0,
          });
          processedNodes.add(`user_${turnKey}`);
        }

        const callback = trace.callback || trace.event || '';
        const isChainEvent =
          callback === 'on_chain_start' ||
          callback === 'on_chain_end' ||
          callback === 'chain_start' ||
          callback === 'chain_end' ||
          callback === 'on_chain_error' ||
          callback === 'chain_error';
        const isChainStart = callback === 'on_chain_start' || callback === 'chain_start';
        const hasUpdates = trace.updates && Object.keys(trace.updates).length > 0;
        const hasToolResult = trace.tool_result && Object.keys(trace.tool_result).length > 0;
        const hasLlmContent = trace.llm?.content || trace.log?.llm?.content;
        const hasProgress = trace.progress && trace.progress.trim();

        if (isChainStart && !hasUpdates && !hasToolResult && !hasLlmContent && !hasProgress) {
          const nodeInfo = findNodeInfo(nodeId);
          const nodeName = nodeInfo.name;
          const actualNode = nodes.find((n: any) => n.id === nodeInfo.id);
          const nodeX = actualNode?.position?.x ?? 0;
          const nodeY = actualNode?.position?.y ?? 0;

          if (nodeType === 'input__basic') {
            const inputKey = `input_start_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(inputKey) && !(trace.updates?.user_input || trace.log?.user_input)) {
              logs.push({
                time: timeStamp,
                log: `${turnLabel}📥 Input 노드 [${nodeName}] 실행 시작...`,
                type: 'input',
                index,
                nodeId,
                nodeX,
                nodeY,
                executionOrder: nodeExecutionOrderMap.get(nodeId) ?? index,
              });
              processedNodes.add(inputKey);
            }
            return;
          }
          const NODE_TYPE_LABELS: Record<string, string> = {
            'agent__generator': 'Generator',
            'agent__reviewer': 'Reviewer',
            'agent__categorizer': 'Categorizer',
            'agent__coder': 'Code',
            'agent__app': 'AgentApp',
            'condition': 'Condition',
            'retriever__knowledge': 'Retriever',
            'retriever__rewriter_hyde': 'Rewriter HyDE',
            'retriever__rewriter_multiquery': 'Rewriter MultiQuery',
            'tool': 'Tool',
            'union': 'Union',
            'merger': 'Merger',
          };

          const nodeTypeLabel = NODE_TYPE_LABELS[nodeType] ?? nodeType ?? '노드';

          if (nodeType === 'condition') return;

          const startKey = `start_${nodeId}_${turnKey}_${index}`;
          if (!processedNodes.has(startKey)) {
            const nodeTypeToLogType: Record<string, string> = {
              'agent__generator': 'generator',
              'agent__reviewer': 'reviewer',
              'agent__categorizer': 'categorizer',
              'condition': 'condition',
              'retriever__knowledge': 'retriever',
              'union': 'union',
              'output__chat': 'output',
              'output__keys': 'output',
              'output__selector': 'output',
              'output__formatter': 'output',
            };

            const logType = nodeTypeToLogType[nodeType] ?? 'structured';

            logs.push({
              time: timeStamp,
              log: `${turnLabel}🔄 ${nodeTypeLabel} 노드 [${nodeName}] 실행 시작...`,
              type: logType,
              index,
              nodeId,
              nodeX,
              nodeY,
              executionOrder: nodeExecutionOrderMap.get(nodeId) ?? index,
            });
            processedNodes.add(startKey);
          }
        }

        const isGeneratorWithLlmContent = nodeType === 'agent__generator' && hasLlmContent;

        if (isGeneratorWithLlmContent) {
          const nodeInfo = findNodeInfo(nodeId);
          const nodeName = nodeInfo.name;
          const nodeData = nodeInfo.data;
          const actualNode = nodes.find((n: any) => n.id === nodeInfo.id);
          const nodeX = actualNode?.position?.x ?? 0;
          const nodeY = actualNode?.position?.y ?? 0;

          const promptId = nodeData?.prompt_id || '';
          const fewshotId = nodeData?.fewshot_id || '';
          const servingModel = nodeData?.serving_model || '';
          const servingName = nodeData?.serving_name || '';

          const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
          const isUuid = servingModel && uuidRegex.test(servingModel);
          const displayServingModel = isUuid && servingName ? servingName : servingName || servingModel || '';

          const toolIds = nodeData?.tool_ids || [];
          const mcpSelections = nodeData?.mcp_selections || [];
          const knowledgeName = getKnowledgeName(nodeId, nodeName, nodeData, trace.updates || {}, trace.tool_result || {});

          const content = trace.llm?.content || trace.log?.llm?.content ||
            trace.updates?.content || trace.tool_result?.content ||
            trace.updates?.messages?.[(trace.updates.messages?.length || 1) - 1]?.content || '';

          const existingLogIndex = logs.findIndex((log) => {
            if (log.nodeId === nodeId && log.type === 'generator') {
              const indexDiff = Math.abs((log.index || 0) - index);
              return indexDiff <= 10;
            }
            return false;
          });

          if (existingLogIndex >= 0) {
            const existingLog = logs[existingLogIndex];

            if (content) {
              const contentText = typeof content === 'string' ? content : JSON.stringify(content);
              const contentMatch = existingLog.log.match(/💬 생성된 응답:\n([\s\S]*)$/);
              if (contentMatch) {
                const existingContent = contentMatch[1];
                if (contentText.length > existingContent.length || contentText !== existingContent) {
                  const updatedLog = existingLog.log.replace(/💬 생성된 응답:\n[\s\S]*$/, `💬 생성된 응답:\n${contentText}`);
                  logs[existingLogIndex] = {
                    ...existingLog,
                    log: updatedLog,
                    time: timeStamp,
                    index,
                  };
                }
              } else {
                logs[existingLogIndex] = {
                  ...existingLog,
                  log: `${existingLog.log}\n\n💬 생성된 응답:\n${contentText}`,
                  time: timeStamp,
                  index,
                };
              }
            }
          } else if (existingLogIndex < 0 && content) {
            const logEntries = [
              { show: promptId, emoji: '📝', label: '프롬프트 ID', value: promptId },
              { show: fewshotId, emoji: '📚', label: '퓨샷 ID', value: fewshotId },
              { show: displayServingModel, emoji: '🤖', label: '서빙 모델', value: displayServingModel },
              { show: knowledgeName, emoji: '📚', label: '사용 지식', value: knowledgeName },
              { show: toolIds?.length, emoji: '🔧', label: '도구 수', value: `${toolIds?.length}개` },
              { show: mcpSelections?.length, emoji: '🔌', label: 'MCP 수', value: `${mcpSelections?.length}개` },
            ];

            const detailLines = logEntries
              .filter(({ show }) => show)
              .map(({ emoji, label, value }) => `${emoji} ${label}: ${value}`)
              .join('\n');
            const contentText = typeof content === 'string' ? content : JSON.stringify(content);

            const generatorLog = [
              `${turnLabel}🤖 Generator 노드 [${nodeName}]`,
              '━━━━━━━━━━━━━━━━',
              detailLines,
              '',
              `💬 생성된 응답:`,
              contentText,
            ].filter(line => line !== undefined).join('\n');


            const generatorKey = `generator_llm_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(generatorKey)) {
              logs.push({
                time: timeStamp,
                log: generatorLog,
                type: 'generator',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(generatorKey);
            }
          }
        }

        if ((hasUpdates || hasToolResult || isChainEvent || hasLlmContent || hasProgress) && !isGeneratorWithLlmContent) {
          const nodeInfo = findNodeInfo(nodeId);
          const nodeName = nodeInfo.name;
          const nodeData = nodeInfo.data;
          const actualNode = nodes.find((n: any) => n.id === nodeInfo.id);
          const nodeX = actualNode?.position?.x ?? 0;
          const nodeY = actualNode?.position?.y ?? 0;

          const updates = trace.updates || {};
          const toolResult = trace.tool_result || {};

          if (nodeType === 'input__basic') {
            const inputKey = `input_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(inputKey)) {
              if (!(trace.updates?.user_input || trace.log?.user_input)) {
                const inputData = updates.input || updates;
                if (Object.keys(inputData).length > 0) {
                  const existingLogIndex = logs.findIndex((log) =>
                    log.nodeId === nodeId &&
                    log.type === 'input' &&
                    Math.abs((log.index || 0) - index) <= 5
                  );

                  const inputLog = `${turnLabel}📥 Input 노드 [${nodeName}]\n━━━━━━━━━━━━━━━━\n${JSON.stringify(inputData, null, 2)}`;

                  if (existingLogIndex >= 0) {
                    logs[existingLogIndex] = {
                      ...logs[existingLogIndex],
                      log: inputLog,
                      time: timeStamp,
                      index,
                    };
                  } else {
                    logs.push({
                      time: timeStamp,
                      log: inputLog,
                      type: 'input',
                      index,
                      nodeId,
                      nodeX,
                      nodeY,
                    });
                  }
                  processedNodes.add(inputKey);
                }
              }
            }
          }
          else if (nodeType === 'retriever__rewriter_hyde') {
            const rewriterKey = `rewriter_hyde_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(rewriterKey)) {
              const servingModel = nodeData?.serving_model || '';
              const servingName = nodeData?.serving_name || '';
              const prompt = nodeData?.query_rewriter?.llm_chain?.prompt || '';

              const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
              const isUuid = servingModel && uuidRegex.test(servingModel);
              const displayServingModel = isUuid && servingName ? servingName : servingName || servingModel || '';
              const rewrittenQuery = (updates as any)?.query || (toolResult as any)?.query || (updates as any)?.rewritten_query || '';
              const originalQuery = (updates as any)?.original_query || (toolResult as any)?.original_query || '';

              let rewriterLog = `${turnLabel}✍️ Rewriter HyDE 노드 [${nodeName}]`;
              rewriterLog += `\n━━━━━━━━━━━━━━━━\n`;

              if (displayServingModel) {
                rewriterLog += `서빙 모델: ${displayServingModel}\n`;
              }
              if (prompt) {
                const promptPreview = prompt.length > 100 ? prompt.substring(0, 100) + '...' : prompt;
                rewriterLog += `프롬프트: ${promptPreview}\n`;
              }
              if (originalQuery) {
                rewriterLog += `원본 질의: ${originalQuery}\n`;
              }
              if (rewrittenQuery) {
                rewriterLog += `\n재작성된 질의:\n${rewrittenQuery}`;
              }

              logs.push({
                time: timeStamp,
                log: rewriterLog,
                type: 'rewriter',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(rewriterKey);
            }
          }
          else if (nodeType === 'retriever__knowledge') {
            const retrieverKey = `retriever_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(retrieverKey)) {
              const knowledgeName = getKnowledgeName(nodeId, nodeName, nodeData, updates, toolResult);
              const query = (updates as any)?.query || (toolResult as any)?.query || '';
              const context = (updates as any)?.context || (toolResult as any)?.context || '';
              const retrievedDocs = (updates as any)?.retrieved_docs || (toolResult as any)?.retrieved_docs || [];

              let retrieverLog = `${turnLabel}🔍 Retriever 노드 [${nodeName}]`;
              retrieverLog += `\n━━━━━━━━━━━━━━━━\n`;

              if (knowledgeName) {
                retrieverLog += `📚 사용 지식: ${knowledgeName}\n`;
              }
              if (query) {
                retrieverLog += `검색 질의: ${query}\n`;
              }
              if (Array.isArray(retrievedDocs) && retrievedDocs.length > 0) {
                retrieverLog += `검색된 문서 수: ${retrievedDocs.length}개\n`;
              }
              if (context) {
                const contextText = typeof context === 'string' ? context.substring(0, 500) : JSON.stringify(context).substring(0, 500);
                retrieverLog += `\n📄 검색된 정보:\n${contextText}${typeof context === 'string' && context.length > 500 ? '...' : ''}`;
              }

              logs.push({
                time: timeStamp,
                log: retrieverLog,
                type: 'retriever',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(retrieverKey);
            }
          } else if (nodeType === 'agent__generator' && (hasUpdates || hasToolResult)) {
            const promptId = nodeData?.prompt_id || '';
            const fewshotId = nodeData?.fewshot_id || '';
            const servingModel = nodeData?.serving_model || '';
            const servingName = nodeData?.serving_name || '';

            const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
            const isUuid = servingModel && uuidRegex.test(servingModel);
            const displayServingModel = isUuid && servingName ? servingName : servingName || servingModel || '';

            const toolIds = nodeData?.tool_ids || [];
            const mcpSelections = nodeData?.mcp_selections || [];
            const knowledgeName = getKnowledgeName(nodeId, nodeName, nodeData, updates, toolResult);

            const content = (updates as any)?.content || (toolResult as any)?.content || (updates as any)?.messages?.[(updates.messages?.length || 1) - 1]?.content || '';

            const existingLogIndex = logs.findIndex((log) => {
              if (log.nodeId === nodeId && log.type === 'generator') {
                const indexDiff = Math.abs((log.index || 0) - index);
                return indexDiff <= 10;
              }
              return false;
            });

            if (existingLogIndex >= 0) {
              const existingLog = logs[existingLogIndex];

              if (content) {
                const contentText = typeof content === 'string' ? content : JSON.stringify(content);
                const contentMatch = existingLog.log.match(/💬 생성된 응답:\n([\s\S]*)$/);

                if (contentMatch) {
                  const existingContent = contentMatch[1];
                  if (contentText.length > existingContent.length || contentText !== existingContent) {
                    const updatedLog = existingLog.log.replace(/💬 생성된 응답:\n[\s\S]*$/, `💬 생성된 응답:\n${contentText}`);
                    logs[existingLogIndex] = {
                      ...existingLog,
                      log: updatedLog,
                      time: timeStamp,
                      index,
                    };
                  }
                } else {
                  logs[existingLogIndex] = {
                    ...existingLog,
                    log: `${existingLog.log}\n\n💬 생성된 응답:\n${contentText}`,
                    time: timeStamp,
                    index,
                  };
                }
              }
            } else if (existingLogIndex < 0) {
              const generatorKey = `generator_${nodeId}_${turnKey}_${index}`;
              if (!processedNodes.has(generatorKey)) {
                let generatorLog = `${turnLabel}🤖 Generator 노드 [${nodeName}]`;
                generatorLog += `\n━━━━━━━━━━━━━━━━\n`;
                if (promptId) {
                  generatorLog += `📝 프롬프트 ID: ${promptId}\n`;
                }
                if (fewshotId) {
                  generatorLog += `📚 퓨샷 ID: ${fewshotId}\n`;
                }
                if (displayServingModel) {
                  generatorLog += `🤖 서빙 모델: ${displayServingModel}\n`;
                }
                if (knowledgeName) {
                  generatorLog += `📚 사용 지식: ${knowledgeName}\n`;
                }
                if (Array.isArray(toolIds) && toolIds.length > 0) {
                  generatorLog += `🔧 도구 수: ${toolIds.length}개\n`;
                }
                if (Array.isArray(mcpSelections) && mcpSelections.length > 0) {
                  generatorLog += `🔌 MCP 수: ${mcpSelections.length}개\n`;
                }
                if (content) {
                  const contentText = typeof content === 'string' ? content : JSON.stringify(content);
                  generatorLog += `\n💬 생성된 응답:\n${contentText}`;
                }

                logs.push({
                  time: timeStamp,
                  log: generatorLog,
                  type: 'generator',
                  index,
                  nodeId,
                  nodeX,
                  nodeY,
                });
                processedNodes.add(generatorKey);
              }
            }
          } else if (nodeType === 'condition') {
            const conditionKey = `condition_${nodeId}_${turnKey}`;
            if (!processedNodes.has(conditionKey)) {
              const conditions = nodeData?.conditions || [];
              let selectedCondition = (updates as any)?.selected || (toolResult as any)?.selected || '';
              if (!selectedCondition && (updates as any)?.additional_kwargs) {
                const additionalKwargs = (updates as any).additional_kwargs;
                const selectedKeys = Object.keys(additionalKwargs).filter(key => key.startsWith('selected_'));
                if (selectedKeys.length > 0) {
                  selectedCondition = additionalKwargs[selectedKeys[selectedKeys.length - 1]];
                }
              }
              const existingLogIndex = logs.findIndex((log) =>
                log.nodeId === nodeId &&
                log.type === 'condition' &&
                Math.abs((log.index || 0) - index) <= 5
              );

              let conditionLog = `${turnLabel}🔀 Condition 노드 [${nodeName}]`;
              conditionLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (Array.isArray(conditions) && conditions.length > 0) {
                conditionLog += `조건 수: ${conditions.length}개\n`;
                conditions.forEach((cond: any) => {
                  if (cond.id && cond.expression) {
                    conditionLog += `  - ${cond.id}: ${cond.expression}\n`;
                  }
                });
              }
              if (selectedCondition) {
                conditionLog += `\n선택된 조건: ${selectedCondition}`;
              }

              if (existingLogIndex >= 0) {
                logs[existingLogIndex] = {
                  ...logs[existingLogIndex],
                  log: conditionLog,
                  time: timeStamp,
                  index,
                };
              } else {
                logs.push({
                  time: timeStamp,
                  log: conditionLog,
                  type: 'condition',
                  index,
                  nodeId,
                  nodeX,
                  nodeY,
                });
              }
              processedNodes.add(conditionKey);
            }
          } else if (nodeType === 'agent__categorizer') {
            const categorizerKey = `categorizer_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(categorizerKey)) {
              const promptId = nodeData?.prompt_id || '';
              const servingModel = nodeData?.serving_model || '';
              const servingName = nodeData?.serving_name || '';

              const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
              const isUuid = servingModel && uuidRegex.test(servingModel);
              const displayServingModel = isUuid && servingName ? servingName : servingName || servingModel || '';

              const selected = (updates as any)?.selected || (toolResult as any)?.selected || '';

              let categorizerLog = `${turnLabel}📂 Categorizer 노드 [${nodeName}]`;
              categorizerLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (promptId) {
                categorizerLog += `📝 프롬프트 ID: ${promptId}\n`;
              }
              if (displayServingModel) {
                categorizerLog += `🤖 서빙 모델: ${displayServingModel}\n`;
              }
              if (selected) {
                categorizerLog += `선택된 카테고리: ${selected}`;
              }

              logs.push({
                time: timeStamp,
                log: categorizerLog,
                type: 'categorizer',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(categorizerKey);
            }
          } else if (nodeType === 'union') {
            const unionKey = `union_${nodeId}_${turnKey}`;
            if (!processedNodes.has(unionKey)) {
              const formatString = nodeData?.format_string || '';
              const mergedContent = (updates as any)?.content || (toolResult as any)?.content || '';

              let unionLog = `${turnLabel}🔗 Union 노드 [${nodeName}]`;
              unionLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (formatString) {
                unionLog += `Format String: ${formatString}\n`;
              }
              if (mergedContent) {
                const contentText = typeof mergedContent === 'string' ? mergedContent.substring(0, 500) : JSON.stringify(mergedContent).substring(0, 500);
                unionLog += `\n병합된 내용:\n${contentText}${typeof mergedContent === 'string' && mergedContent.length > 500 ? '...' : ''}`;
              }

              logs.push({
                time: timeStamp,
                log: unionLog,
                type: 'union',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(unionKey);
            }
          } else if (nodeType === 'agent__reviewer') {
            const reviewerKey = `reviewer_${nodeId}_${turnKey}`;
            if (!processedNodes.has(reviewerKey)) {
              const existingLogIndex = logs.findIndex((log) =>
                log.nodeId === nodeId &&
                log.type === 'reviewer' &&
                Math.abs((log.index || 0) - index) <= 5
              );

              const promptId = nodeData?.prompt_id || '';
              const servingModel = nodeData?.serving_model || '';

              let reviewerLog = `${turnLabel}✅ Reviewer 노드 [${nodeName}]`;
              reviewerLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (promptId) {
                reviewerLog += `📝 프롬프트 ID: ${promptId}\n`;
              }
              if (servingModel) {
                reviewerLog += `🤖 서빙 모델: ${servingModel}\n`;
              }

              if (existingLogIndex >= 0) {
                logs[existingLogIndex] = {
                  ...logs[existingLogIndex],
                  log: reviewerLog,
                  time: timeStamp,
                  index,
                };
              } else {
                logs.push({
                  time: timeStamp,
                  log: reviewerLog,
                  type: 'reviewer',
                  index,
                  nodeId,
                  nodeX,
                  nodeY,
                });
              }
              processedNodes.add(reviewerKey);
            }
          } else if (nodeType === 'merger') {
            const mergerKey = `merger_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(mergerKey)) {
              const mergedContent = (updates as any)?.content || (toolResult as any)?.content || '';

              let mergerLog = `${turnLabel}🔀 Merger 노드 [${nodeName}]`;
              mergerLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (mergedContent) {
                const contentText = typeof mergedContent === 'string' ? mergedContent.substring(0, 500) : JSON.stringify(mergedContent).substring(0, 500);
                mergerLog += `병합된 내용:\n${contentText}${typeof mergedContent === 'string' && mergedContent.length > 500 ? '...' : ''}`;
              }

              logs.push({
                time: timeStamp,
                log: mergerLog,
                type: 'merger',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(mergerKey);
            }
          } else if (nodeType === 'agent__coder') {
            const coderKey = `coder_${nodeId}_${turnKey}`;
            if (!processedNodes.has(coderKey)) {
              const existingLogIndex = logs.findIndex((log) =>
                log.nodeId === nodeId &&
                log.type === 'coder' &&
                Math.abs((log.index || 0) - index) <= 5
              );

              const codeFunction = nodeData?.code_function || '';
              const executionResult = (updates as any)?.result || (toolResult as any)?.result || '';

              let coderLog = `${turnLabel}💻 Code 노드 [${nodeName}]`;
              coderLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (codeFunction) {
                const codePreview = codeFunction.length > 200 ? codeFunction.substring(0, 200) + '...' : codeFunction;
                coderLog += `코드 함수:\n${codePreview}\n`;
              }
              if (executionResult) {
                const resultText = typeof executionResult === 'string' ? executionResult.substring(0, 500) : JSON.stringify(executionResult).substring(0, 500);
                coderLog += `\n실행 결과:\n${resultText}${typeof executionResult === 'string' && executionResult.length > 500 ? '...' : ''}`;
              }

              if (existingLogIndex >= 0) {
                logs[existingLogIndex] = {
                  ...logs[existingLogIndex],
                  log: coderLog,
                  time: timeStamp,
                  index,
                };
              } else {
                logs.push({
                  time: timeStamp,
                  log: coderLog,
                  type: 'coder',
                  index,
                  nodeId,
                  nodeX,
                  nodeY,
                });
              }
              processedNodes.add(coderKey);
            }
          } else if (nodeType === 'tool') {
            const toolKey = `tool_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(toolKey)) {
              const toolName = nodeData?.tool_name || nodeData?.name || '';
              const toolResult_data = (updates as any)?.result || (toolResult as any)?.result || '';

              let toolLog = `${turnLabel}🔧 Tool 노드 [${nodeName}]`;
              toolLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (toolName) {
                toolLog += `도구 이름: ${toolName}\n`;
              }
              if (toolResult_data) {
                const resultText = typeof toolResult_data === 'string' ? toolResult_data.substring(0, 500) : JSON.stringify(toolResult_data).substring(0, 500);
                toolLog += `\n도구 실행 결과:\n${resultText}${typeof toolResult_data === 'string' && toolResult_data.length > 500 ? '...' : ''}`;
              }

              logs.push({
                time: timeStamp,
                log: toolLog,
                type: 'tool',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(toolKey);
            }
          } else if (nodeType === 'agent__app') {
            const agentAppKey = `agent_app_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(agentAppKey)) {
              const appId = nodeData?.agent_app_id || '';
              const appName = nodeData?.agent_app_name || nodeData?.name || '';
              const appResult = (updates as any)?.result || (toolResult as any)?.result || '';

              let agentAppLog = `${turnLabel}🤖 AgentApp 노드 [${nodeName}]`;
              agentAppLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (appId) {
                agentAppLog += `AgentApp ID: ${appId}\n`;
              }
              if (appName) {
                agentAppLog += `AgentApp 이름: ${appName}\n`;
              }
              if (appResult) {
                const resultText = typeof appResult === 'string' ? appResult.substring(0, 500) : JSON.stringify(appResult).substring(0, 500);
                agentAppLog += `\n실행 결과:\n${resultText}${typeof appResult === 'string' && appResult.length > 500 ? '...' : ''}`;
              }

              logs.push({
                time: timeStamp,
                log: agentAppLog,
                type: 'agent_app',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(agentAppKey);
            }
          } else if (nodeType === 'retriever__rewriter_multiquery') {
            const rewriterMultiQueryKey = `rewriter_multiquery_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(rewriterMultiQueryKey)) {
              const servingModel = nodeData?.serving_model || '';
              const servingName = nodeData?.serving_name || '';
              const prompt = nodeData?.query_rewriter?.llm_chain?.prompt || '';

              const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
              const isUuid = servingModel && uuidRegex.test(servingModel);
              const displayServingModel = isUuid && servingName ? servingName : servingName || servingModel || '';

              const rewrittenQueries = (updates as any)?.queries || (toolResult as any)?.queries || [];
              const originalQuery = (updates as any)?.original_query || (toolResult as any)?.original_query || '';

              let rewriterLog = `${turnLabel}✍️ Rewriter MultiQuery 노드 [${nodeName}]`;
              rewriterLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (displayServingModel) {
                rewriterLog += `서빙 모델: ${displayServingModel}\n`;
              }
              if (prompt) {
                const promptPreview = prompt.length > 100 ? prompt.substring(0, 100) + '...' : prompt;
                rewriterLog += `프롬프트: ${promptPreview}\n`;
              }
              if (originalQuery) {
                rewriterLog += `원본 질의: ${originalQuery}\n`;
              }
              if (Array.isArray(rewrittenQueries) && rewrittenQueries.length > 0) {
                rewriterLog += `\n재작성된 질의 수: ${rewrittenQueries.length}개\n`;
                rewrittenQueries.slice(0, 5).forEach((q: string, idx: number) => {
                  rewriterLog += `  ${idx + 1}. ${q}\n`;
                });
                if (rewrittenQueries.length > 5) {
                  rewriterLog += `  ... (${rewrittenQueries.length - 5}개 더)`;
                }
              }

              logs.push({
                time: timeStamp,
                log: rewriterLog,
                type: 'rewriter',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(rewriterMultiQueryKey);
            }
          } else if (nodeType === 'retriever__doc_reranker') {
            const rerankerKey = `reranker_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(rerankerKey)) {
              const rankedDocs = (updates as any)?.ranked_docs || (toolResult as any)?.ranked_docs || [];

              let rerankerLog = `${turnLabel}📊 ReRanker 노드 [${nodeName}]`;
              rerankerLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (Array.isArray(rankedDocs) && rankedDocs.length > 0) {
                rerankerLog += `재랭킹된 문서 수: ${rankedDocs.length}개`;
              }

              logs.push({
                time: timeStamp,
                log: rerankerLog,
                type: 'reranker',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(rerankerKey);
            }
          } else if (nodeType === 'retriever__doc_compressor') {
            const compressorKey = `compressor_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(compressorKey)) {
              const compressedDocs = (updates as any)?.compressed_docs || (toolResult as any)?.compressed_docs || [];

              let compressorLog = `${turnLabel}📦 Compressor 노드 [${nodeName}]`;
              compressorLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (Array.isArray(compressedDocs) && compressedDocs.length > 0) {
                compressorLog += `압축된 문서 수: ${compressedDocs.length}개`;
              }

              logs.push({
                time: timeStamp,
                log: compressorLog,
                type: 'compressor',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(compressorKey);
            }
          } else if (nodeType === 'retriever__doc_filter') {
            const filterKey = `filter_${nodeId}_${turnKey}_${index}`;
            if (!processedNodes.has(filterKey)) {
              const filteredDocs = (updates as any)?.filtered_docs || (toolResult as any)?.filtered_docs || [];

              let filterLog = `${turnLabel}🔍 Filter 노드 [${nodeName}]`;
              filterLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (Array.isArray(filteredDocs) && filteredDocs.length > 0) {
                filterLog += `필터링된 문서 수: ${filteredDocs.length}개`;
              }

              logs.push({
                time: timeStamp,
                log: filterLog,
                type: 'filter',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(filterKey);
            }
          } else if (nodeType === 'output__chat' || nodeType === 'output__keys' || nodeType === 'output__selector' || nodeType === 'output__formatter') {
            const outputKey = `output_${nodeId}_${turnKey}`;
            if (!processedNodes.has(outputKey)) {
              const formatString = nodeData?.format_string || '';

              let outputLog = `${turnLabel}📤 Output 노드 [${nodeName}]`;
              outputLog += `\n━━━━━━━━━━━━━━━━\n`;
              if (formatString) {
                outputLog += `Format String: ${formatString}\n`;
              }

              logs.push({
                time: timeStamp,
                log: outputLog,
                type: 'output',
                index,
                nodeId,
                nodeX,
                nodeY,
              });
              processedNodes.add(outputKey);
            }
          }
        }
      });

      const finalResultByTurn = new Map<number | string, { content: string; index: number; nodeId: string; turn: number | undefined }>();

      tracingMessages.forEach((trace, index) => {
        if (trace.final_result !== undefined) {
          const nodeId = trace.node_name || trace.nodeName || trace.node_id || trace.nodeId || 'unknown';
          const turn = typeof trace.turn === 'number' && trace.turn > 0 ? trace.turn : undefined;
          const turnKey = turn || '0';
          const finalResultValue = typeof trace.final_result === 'string' ? trace.final_result : String(trace.final_result || '');
          
          // 🔥 디버깅: final_result 값 확인
          console.log('[ChatLogSidebar] final_result:', {
            index,
            nodeId,
            turn,
            finalResultValue,
            length: finalResultValue.length,
          });
          
          // 스트리밍으로 들어오는 final_result는 누적되는 것이 아니라 가장 긴/최신 값을 사용
          if (finalResultByTurn.has(turnKey)) {
            const existing = finalResultByTurn.get(turnKey)!;
            // 더 긴 값이거나 더 최신 인덱스면 교체
            if (finalResultValue.length > existing.content.length || index > existing.index) {
              console.log('[ChatLogSidebar] Replacing with longer/newer value:', {
                oldLength: existing.content.length,
                newLength: finalResultValue.length,
                oldIndex: existing.index,
                newIndex: index,
              });
              existing.content = finalResultValue;
              existing.index = index;
              existing.nodeId = nodeId;
            }
          } else {
            finalResultByTurn.set(turnKey, {
              content: finalResultValue,
              index,
              nodeId,
              turn,
            });
          }
        }
      });

      let finalResultContent = '';
      let finalResultIndex = -1;
      let finalResultNodeId = 'unknown';
      let finalResultTurn = undefined;

      if (finalResultByTurn.size > 0) {
        const sortedTurns = Array.from(finalResultByTurn.keys()).sort((a, b) => {
          if (typeof a === 'number' && typeof b === 'number') return b - a;
          if (typeof a === 'number') return -1;
          if (typeof b === 'number') return 1;
          return 0;
        });
        const latestTurnKey = sortedTurns[0];
        const latestFinalResult = finalResultByTurn.get(latestTurnKey)!;
        finalResultContent = latestFinalResult.content;
        finalResultIndex = latestFinalResult.index;
        finalResultNodeId = latestFinalResult.nodeId;
        finalResultTurn = latestFinalResult.turn;
      }

      // 🔥 final_result가 너무 짧으면 (1-2글자) output__chat의 content를 우선 사용
      if (finalResultContent && finalResultContent.trim().length <= 2) {
        console.log('[ChatLogSidebar] final_result too short, checking output__chat content:', finalResultContent);
        finalResultContent = '';
      }

      if (!finalResultContent) {
        const outputContentByTurn = new Map<number | string, { content: string; index: number; nodeId: string; turn: number | undefined }>();

        tracingMessages.forEach((trace, index) => {
          const nodeType = trace.node_type || trace.nodeType || '';
          if (nodeType === 'output__chat' || nodeType === 'output__keys') {
            const nodeId = trace.node_name || trace.nodeName || trace.node_id || trace.nodeId || 'unknown';
            const turn = typeof trace.turn === 'number' && trace.turn > 0 ? trace.turn : undefined;
            const turnKey = turn || '0';

            // content 추출: updates.content, log.content, 또는 log에서 직접 파싱
            let content = '';
            
            if (trace.updates?.content) {
              content = typeof trace.updates.content === 'string' ? trace.updates.content : JSON.stringify(trace.updates.content);
            } else if (trace.log?.content) {
              content = typeof trace.log.content === 'string' ? trace.log.content : JSON.stringify(trace.log.content);
            } else if (typeof trace.log === 'string') {
              // log가 JSON 문자열인 경우 파싱 시도
              // log 형식: "#4 [output__chat_1] 📤 출력 처리\n{...}" 또는 직접 JSON
              try {
                // 먼저 직접 파싱 시도
                const parsed = JSON.parse(trace.log);
                if (parsed.content) {
                  content = typeof parsed.content === 'string' ? parsed.content : JSON.stringify(parsed.content);
                }
              } catch {
                // 파싱 실패 시, JSON 부분만 추출 시도
                try {
                  // "{...}" 부분 찾기
                  const jsonMatch = trace.log.match(/\{[\s\S]*\}/);
                  if (jsonMatch) {
                    const parsed = JSON.parse(jsonMatch[0]);
                    if (parsed.content) {
                      content = typeof parsed.content === 'string' ? parsed.content : JSON.stringify(parsed.content);
                    }
                  }
                } catch {
                  // 파싱 실패 시 무시
                  console.warn('[ChatLogSidebar] Failed to parse log content:', trace.log?.substring(0, 100));
                }
              }
            }

            if (content && (!outputContentByTurn.has(turnKey) || index > outputContentByTurn.get(turnKey)!.index)) {
              outputContentByTurn.set(turnKey, {
                content,
                index,
                nodeId,
                turn,
              });
            }
          }
        });
        if (outputContentByTurn.size > 0) {
          const sortedTurns = Array.from(outputContentByTurn.keys()).sort((a, b) => {
            if (typeof a === 'number' && typeof b === 'number') return b - a;
            if (typeof a === 'number') return -1;
            if (typeof b === 'number') return 1;
            return 0;
          });
          const latestTurnKey = sortedTurns[0];
          const latestOutputContent = outputContentByTurn.get(latestTurnKey)!;
          
          // 🔥 output__chat의 content가 더 길면 우선 사용
          if (!finalResultContent || latestOutputContent.content.length > finalResultContent.length) {
            console.log('[ChatLogSidebar] Using output__chat content:', {
              finalResultLength: finalResultContent.length,
              outputContentLength: latestOutputContent.content.length,
            });
            finalResultContent = latestOutputContent.content;
            finalResultIndex = latestOutputContent.index;
            finalResultNodeId = latestOutputContent.nodeId;
            finalResultTurn = latestOutputContent.turn;
          }
        }
      }

      const trimmedContent = finalResultContent?.trim() || '';
      if (trimmedContent && trimmedContent !== '#' && finalResultIndex >= 0) {
        const turnLabel = finalResultTurn ? `#${finalResultTurn} ` : '';
        const outputNode = nodes.find((n: any) => n.type === 'output__chat' || n.type === 'output__keys');
        const outputX = outputNode?.position?.x ?? 10000;
        const nodeX = outputX + 100;
        const nodeY = outputNode?.position?.y ?? 0;
        for (let i = logs.length - 1; i >= 0; i--) {
          const log = logs[i];
          if (log.type === 'output') {
            const logTurn = log.log.match(/#(\d+)\s/)?.[1];
            const finalResultTurnStr = finalResultTurn?.toString();
            if (!finalResultTurn || !logTurn || logTurn === finalResultTurnStr) {
              logs.splice(i, 1);
            }
          }
        }

        logs.push({
          time: `[${(finalResultIndex * 0.1).toFixed(1)}s]`,
          log: `${turnLabel}💬 최종 답변\n━━━━━━━━━━━━━━━━\n${trimmedContent}`,
          type: 'final_result',
          index: finalResultIndex,
          nodeId: finalResultNodeId,
          nodeX,
          nodeY,
        });
      }
      const nodeFirstAppearanceMap = new Map<string, number>();
      tracingMessages.forEach((trace, index) => {
        const nodeId = trace.node_name || trace.nodeName || trace.node_id || trace.nodeId || 'unknown';
        if (nodeId !== 'unknown' && !nodeFirstAppearanceMap.has(nodeId)) {
          nodeFirstAppearanceMap.set(nodeId, index);
        }
      });
      const calculateNodeExecutionOrder = () => {
        const nodeOrderMap = new Map<string, number>();
        const visited = new Set<string>();
        let order = 0;
        const inputNode = nodes.find((n: any) => n.type === 'input__basic');
        if (inputNode) {
          const queue: string[] = [inputNode.id];
          visited.add(inputNode.id);
          const nodeOrder = order++;
          nodeOrderMap.set(inputNode.id, nodeOrder);
          const inputNodeName = inputNode.data?.name;
          if (inputNodeName && String(inputNodeName) !== inputNode.id) {
            nodeOrderMap.set(String(inputNodeName), nodeOrder);
          }
          while (queue.length > 0) {
            const currentNodeId = queue.shift()!;
            const currentOrder = nodeOrderMap.get(currentNodeId) || 0;
            const outgoingEdges = edges.filter((e: any) => e.source === currentNodeId);
            outgoingEdges.forEach((edge: any) => {
              const targetNodeId = edge.target;
              if (!visited.has(targetNodeId)) {
                visited.add(targetNodeId);
                const targetOrder = currentOrder + 1;
                nodeOrderMap.set(targetNodeId, targetOrder);
                const targetNode = nodes.find((n: any) => n.id === targetNodeId);
                if (targetNode) {
                  const targetNodeName = targetNode.data?.name;
                  if (targetNodeName && String(targetNodeName) !== targetNodeId) {
                    nodeOrderMap.set(String(targetNodeName), targetOrder);
                  }
                }
                queue.push(targetNodeId);
              } else {
                const existingOrder = nodeOrderMap.get(targetNodeId) || 0;
                if (currentOrder + 1 < existingOrder) {
                  const newOrder = currentOrder + 1;
                  nodeOrderMap.set(targetNodeId, newOrder);
                  const targetNode = nodes.find((n: any) => n.id === targetNodeId);
                  if (targetNode) {
                    const targetNodeName = targetNode.data?.name;
                    if (targetNodeName && String(targetNodeName) !== targetNodeId) {
                      nodeOrderMap.set(String(targetNodeName), newOrder);
                    }
                  }
                }
              }
            });
          }
        }

        nodes.forEach((node: any) => {
          if (!visited.has(node.id)) {
            const nodeOrder = 999 + order++;
            nodeOrderMap.set(node.id, nodeOrder);
            const nodeName = node.data?.name;
            if (nodeName && String(nodeName) !== node.id) {
              nodeOrderMap.set(String(nodeName), nodeOrder);
            }
          }
        });

        return nodeOrderMap;
      };

      const nodeOrderMap = calculateNodeExecutionOrder();

      logs.forEach(log => {
        if (log.executionOrder === undefined) {
          const nodeId = log.nodeId || '';
          log.executionOrder = nodeExecutionOrderMap.get(nodeId) ?? log.index ?? 9999;
        }
      });

      return logs.sort((a, b) => {
        if (a.type === 'user' && b.type !== 'user') return -1;
        if (a.type !== 'user' && b.type === 'user') return 1;
        if (a.type === 'final_result' && b.type !== 'final_result') return 1;
        if (a.type !== 'final_result' && b.type === 'final_result') return -1;
        const nodeIdA = a.nodeId || '';
        const nodeIdB = b.nodeId || '';
        const nodeInfoA = findNodeInfo(nodeIdA);
        const nodeInfoB = findNodeInfo(nodeIdB);
        const actualNodeIdA = nodeInfoA.id || nodeIdA;
        const actualNodeIdB = nodeInfoB.id || nodeIdB;

        const orderA = nodeOrderMap.get(actualNodeIdA) ?? nodeOrderMap.get(nodeIdA) ?? 9999;
        const orderB = nodeOrderMap.get(actualNodeIdB) ?? nodeOrderMap.get(nodeIdB) ?? 9999;

        if (orderA !== orderB) {
          return orderA - orderB;
        }
        const execOrderA = a.executionOrder ?? 9999;
        const execOrderB = b.executionOrder ?? 9999;

        if (execOrderA !== execOrderB) {
          return execOrderA - execOrderB;
        }
        if (actualNodeIdA !== actualNodeIdB) {
          const nodeNameA = nodeInfoA.name || nodeIdA;
          const nodeNameB = nodeInfoB.name || nodeIdB;
          if (nodeNameA !== nodeNameB) {
            return nodeNameA.localeCompare(nodeNameB);
          }
        }
        const indexA = a.index ?? 9999;
        const indexB = b.index ?? 9999;

        if (indexA !== indexB) {
          return indexA - indexB;
        }
        const typeOrder: Record<string, number> = {
          user: 1,
          input: 2,
          rewriter: 2.5,
          retriever: 3,
          categorizer: 3.2,
          condition: 3.5,
          generator: 4,
          reviewer: 4.2,
          coder: 4.5,
          tool: 4.7,
          agent_app: 4.8,
          union: 5,
          merger: 5.2,
          reranker: 5.5,
          compressor: 5.7,
          filter: 5.8,
          output: 6,
          final_result: 7
        };
        const typeOrderA = typeOrder[a.type] || 99;
        const typeOrderB = typeOrder[b.type] || 99;

        if (typeOrderA !== typeOrderB) {
          return typeOrderA - typeOrderB;
        }
        return 0;
      });
    }

    if (messages.length === 0) {
      return [];
    }

    const stepLogs: Array<{ time: string; log: string; type: string }> = [];

    const userMessages = messages.filter(m => m.role === 'user' || m.type === 'human');
    const aiMessages = messages.filter(m => m.role === 'assistant' || m.type === 'ai');

    userMessages.forEach((message, index) => {
      stepLogs.push({
        time: `[${(index * 2).toFixed(1)}s]`,
        log: `[사용자] ${message.content}`,
        type: 'user',
      });
    });

    aiMessages.forEach((message, index) => {
      stepLogs.push({
        time: `[${(index * 2 + 1).toFixed(1)}s]`,
        log: `[AI 응답] ${message.content}`,
        type: 'llm_content',
      });
    });

    return stepLogs;
  }, [messages, tracingMessages, nodesMap, selectedKnowledgeNameRepoParsed, nodes]);

  useEffect(() => {
    if (formattedLogs.length > prevLogsLengthRef.current) {
      prevLogsLengthRef.current = formattedLogs.length;
      if (logContainerRef.current) {
        const container = logContainerRef.current;
        container.scrollTo({
          top: container.scrollHeight,
          behavior: 'smooth',
        });
      }
    }
  }, [formattedLogs.length]);

  if (!isVisible) {
    return <div style={{ display: 'none' }} />;
  }
  if (!hasChatTested) {
    return (
      <div className='absolute right-[525px] top-16 z-40 h-[91%] w-96 bg-white border-l border-gray-300 shadow-lg'>
        <div className='bg-gray-100 border-b border-gray-300 px-4 py-2 flex items-center justify-between'>
          <h3 className='text-sm font-semibold text-gray-800'>빌더 로그</h3>
          <button onClick={onClose} className='text-gray-500 hover:text-gray-700 text-lg'>
            ×
          </button>
        </div>
        <div className='flex items-center justify-center h-full bg-gray-50'>
          <div className='text-center text-gray-500'>
            <div className='text-4xl mb-4'>📝</div>
            <p className='text-lg font-medium mb-2'>채팅 테스트를 먼저 실행해주세요</p>
            <p className='text-sm text-gray-400'>채팅 테스트 후 빌더 로그를 확인할 수 있습니다</p>
          </div>
        </div>
      </div>
    );
  }

  const totalTime = formattedLogs.length > 0 ? formattedLogs[formattedLogs.length - 1].time.replace('[', '').replace(']', '') : '0.00s';

  return (
    <div className='absolute right-[525px] top-16 z-40 h-[91%] w-96 bg-white border-l border-gray-300 shadow-lg flex flex-col'>
      <div className='bg-gray-100 border-b border-gray-300 px-4 py-2 flex items-center justify-between'>
        <h3 className='text-sm font-semibold text-gray-800'>채팅 테스트 로그</h3>
        <button onClick={onClose} className='text-gray-500 hover:text-gray-700 text-lg'>
          ×
        </button>
      </div>

      <div className='bg-white border-b border-gray-300 px-4 py-2 flex items-center justify-end'>
        <button className='text-sm px-3 py-1 bg-blue-500 hover:bg-blue-600 text-white rounded transition-colors' onClick={() => setShowRaw(!showRaw)}>
          {showRaw ? 'Structured 보기' : 'Raw 보기'}
        </button>
      </div>

      <div className='flex-1 overflow-hidden'>
        {tracingMessages.length > 0 || messages.length > 0 ? (
          !showRaw ? (
            <div ref={logContainerRef} className='h-full overflow-y-auto'>
              <StructuredLogView logs={formattedLogs} totalTime={totalTime} />
            </div>
          ) : (
            <div ref={logContainerRef} className='h-full overflow-y-auto bg-white p-4'>
              <div className='space-y-3'>
                {formattedLogs.map((log, index) => (
                  <div key={index} className='border-l-4 border-gray-300 pl-3 py-2 bg-gray-50'>
                    <div className='text-xs text-gray-500 mb-1'>
                      {log.time} {log.type || 'default'}
                    </div>
                    <div className='bg-gray-100 p-2 rounded'>
                      <div className='text-xs font-semibold text-gray-700 mb-1'>event metadata</div>
                      <div className='font-mono text-xs text-gray-600 mb-2'>run_id: {stringUtils.secureRandomString(9)}</div>
                      <div className='text-xs font-semibold text-gray-700 mb-1'>event data</div>
                      <div className='font-mono text-xs text-gray-800 whitespace-pre-wrap'>
                        {JSON.stringify(
                          {
                            log: log.log,
                            type: log.type,
                            time: log.time,
                          },
                          null,
                          2
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )
        ) : (
          <div className='flex items-center justify-center h-full text-gray-500'>채팅 로그가 없습니다.</div>
        )}
      </div>
    </div>
  );
};

export { ChatLogSidebar };