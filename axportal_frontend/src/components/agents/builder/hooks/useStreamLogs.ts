import { useState, useRef } from 'react';
import { useAtom } from 'jotai';
import { builderLogState } from '@/components/agents/builder/atoms/logAtom';
import { nodesAtom, selectedKnowledgeNameRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';

export interface StreamLogEntry {
  timestamp?: string;
  request_time?: string;
  node_name?: string;
  node_type?: string;
  node_id?: string;
  progress?: string;
  updates?: any;
  llm?: any;
  content?: string;
  status?: string;
  errorType?: string;
  chunkIndex?: number;
  turn?: number;
  [key: string]: any;
}

export const useStreamLogs = () => {
  const [streamLogs, setStreamLogs] = useState<StreamLogEntry[]>([]);
  const streamLogsRef = useRef<StreamLogEntry[]>([]);
  const [, setBuilderLogState] = useAtom(builderLogState);
  const [nodes] = useAtom(nodesAtom);
  const [selectedKnowledgeNameRepo] = useAtom(selectedKnowledgeNameRepoAtom);

  const clearStreamLogs = () => {
    setStreamLogs([]);
    streamLogsRef.current = [];
  };

  const addStreamLog = (logEntry: StreamLogEntry) => {
    const newLogEntry = {
      ...logEntry,
      timestamp: logEntry.timestamp || new Date().toISOString(),
    };

    setStreamLogs(prev => [...prev, newLogEntry]);
    streamLogsRef.current = [...streamLogsRef.current, newLogEntry];
  };

  const generateBuilderLogs = (_userInput: string, _response: string, _elapsedTime: number, turn?: number) => {
    try {
      const currentStreamLogs = streamLogsRef.current;
      const filteredLogs: any[] = [];

      currentStreamLogs.forEach((log) => {
        const nodeId = log.node_name || log.node_id || 'unknown';
        const nodeType = log.node_type || '';
        const currentTurn = typeof log.turn === 'number' ? log.turn : typeof turn === 'number' ? turn : undefined;

        if (nodeId === 'unknown') {
          return;
        }

        if (log.progress && log.progress.trim()) {
          if (nodeType === 'union') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🔗 Union 처리\n━━━━━━━━━━━━━━━━\n${log.progress}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'union',
              turn: currentTurn,
            });
          } else if (nodeType !== 'union') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🔄 진행 상태\n${log.progress}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'progress',
              turn: currentTurn,
            });
          }
        }

        if (log.tool_calls) {
          filteredLogs.push({
            time: log.timestamp || log.request_time || new Date().toISOString(),
            log: `[${nodeId}] 🔧 Tool 호출\n${JSON.stringify(log.tool_calls, null, 2)}`,
            nodeName: nodeId,
            nodeType: nodeType,
            type: 'tool_calls',
            turn: currentTurn,
          });
        }

        if (log.tool_result) {
          filteredLogs.push({
            time: log.timestamp || log.request_time || new Date().toISOString(),
            log: `[${nodeId}] ✅ Tool 결과\n${JSON.stringify(log.tool_result, null, 2)}`,
            nodeName: nodeId,
            nodeType: nodeType,
            type: 'tool_result',
            turn: currentTurn,
          });
        }

        if (log.llm && log.llm.content && log.llm.content.trim()) {
          filteredLogs.push({
            time: log.timestamp || log.request_time || new Date().toISOString(),
            log: `[${nodeId}] 🤖 LLM 출력 (스트리밍)\n${log.llm.content}`,
            nodeName: nodeId,
            nodeType: nodeType,
            type: 'llm_streaming',
            turn: currentTurn,
          });
        }

        if ((log.updates && Object.keys(log.updates).length > 0) || (nodeType && nodeId !== 'unknown')) {
          if (nodeType === 'input__basic') {
            const userInput = log.updates?.additional_kwargs?.user_input
              || log.updates?.additional_kwargs?.latest_user_input
              || log.updates?.additional_kwargs?.current_user_input || '';
            if (userInput) {
              filteredLogs.push({
                time: log.timestamp || log.request_time || new Date().toISOString(),
                log: `[${nodeId}] 👤 사용자 입력 처리 완료\n━━━━━━━━━━━━━━━━\n입력 내용: "${userInput}"\n추가 정보: ${JSON.stringify(log.updates?.additional_kwargs || {}, null, 2)}`,
                nodeName: nodeId,
                nodeType: nodeType,
                type: 'user_input',
                turn: currentTurn,
              });
            } else {
              filteredLogs.push({
                time: log.timestamp || log.request_time || new Date().toISOString(),
                log: `[${nodeId}] 👤 사용자 입력 처리\n━━━━━━━━━━━━━━━━\nInput 노드가 실행되었습니다.`,
                nodeName: nodeId,
                nodeType: nodeType,
                type: 'user_input',
                turn: currentTurn,
              });
            }
          } else if (nodeType === 'agent__generator') {
            if (log.updates?.messages && Array.isArray(log.updates.messages)) {
              const content = log.updates.messages.map((m: any) => m.content || '').filter(Boolean).join('\n');
              if (content && content.trim()) {
                filteredLogs.push({
                  time: log.timestamp || log.request_time || new Date().toISOString(),
                  log: `[${nodeId}] 💬 AI 응답 생성 완료\n━━━━━━━━━━━━━━━━\n${content}\n\n메시지 상세:\n${JSON.stringify(log.updates.messages, null, 2)}`,
                  nodeName: nodeId,
                  nodeType: nodeType,
                  type: 'llm_content',
                  turn: currentTurn,
                });
              }
            } else if (log.updates?.additional_kwargs) {
              filteredLogs.push({
                time: log.timestamp || log.request_time || new Date().toISOString(),
                log: `[${nodeId}] 📝 Generator 업데이트\n${JSON.stringify(log.updates, null, 2)}`,
                nodeName: nodeId,
                nodeType: nodeType,
                type: 'generator_update',
                turn: currentTurn,
              });
            } else {
              filteredLogs.push({
                time: log.timestamp || log.request_time || new Date().toISOString(),
                log: `[${nodeId}] 🤖 Generator 처리\n━━━━━━━━━━━━━━━━\nGenerator 노드가 실행되었습니다.`,
                nodeName: nodeId,
                nodeType: nodeType,
                type: 'generator_update',
                turn: currentTurn,
              });
            }
          } else if (nodeType === 'output__chat' || nodeType === 'output__formatter' || nodeType === 'output__selector') {
            const content = log.updates?.content || '';
            if (content) {
              filteredLogs.push({
                time: log.timestamp || log.request_time || new Date().toISOString(),
                log: `[${nodeId}] 🎯 최종 출력 완료\n━━━━━━━━━━━━━━━━\n출력 내용: "${content}"\n\n업데이트 상세:\n${JSON.stringify(log.updates, null, 2)}`,
                nodeName: nodeId,
                nodeType: nodeType,
                type: 'output',
                turn: currentTurn,
              });
            }
          } else if (nodeType === 'retriever__knowledge') {
            const nodeData = nodes.find((n: any) => n.id === nodeId);
            const knowledgeInfo: string[] = [];

            const DELETED_STATE = '__DELETED__';
            const data = nodeData?.data as any;

            const traceKnowledgeName = (log.updates as any)?.knowledge_name
              || (log.updates as any)?._knowledgeName
              || (log.tool_result as any)?.knowledge_name
              || (log.tool_result as any)?._knowledgeName
              || (log as any)?.knowledge_name
              || (log as any)?._knowledgeName
              || (log.updates as any)?.knowledge_retriever?.name
              || (log.tool_result as any)?.knowledge_retriever?.name;

            const atomKnowledgeName = selectedKnowledgeNameRepo[nodeId] === DELETED_STATE ? '' : selectedKnowledgeNameRepo[nodeId];
            const knowledgeRetriever = data?.knowledge_retriever as { name?: string; repo_id?: string; knowledge_info?: any } | undefined;

            let knowledgeName = atomKnowledgeName
              || traceKnowledgeName
              || data?._knowledgeName
              || data?.knowledge_name
              || data?.innerData?.knowledgeName
              || knowledgeRetriever?.name
              || knowledgeRetriever?.knowledge_info?.name
              || (data?.knowledge_retriever as any)?.knowledge_info?.name
              || '';

            if (data) {
              const repoId = data.repo_id
                || data.schemaData?.repo_id
                || data.knowledge_retriever?.repo_id
                || (data.knowledge_retriever as any)?.knowledge_info?.repo_id;

              const repoKind = data.repo_kind
                || data.schemaData?.repo_kind
                || data.knowledge_retriever?.repo_kind
                || (data.knowledge_retriever as any)?.knowledge_info?.repo_kind;

              const retrieverId = data.retriever_id
                || data.schemaData?.knowledge_retriever?.retriever_id
                || data.knowledge_retriever?.retriever_id;

              const retrieverName = data.knowledge_retriever?.name
                || data.schemaData?.knowledge_retriever?.name;

              if (repoId) {
                knowledgeInfo.push(`지식 베이스 ID: ${repoId}`);
              }
              if (repoKind) {
                knowledgeInfo.push(`지식 베이스 종류: ${repoKind}`);
              }
              if (retrieverId) {
                knowledgeInfo.push(`리트리버 ID: ${retrieverId}`);
              }
              if (retrieverName) {
                knowledgeInfo.push(`리트리버 이름: ${retrieverName}`);
              }
            }

            if (!knowledgeName || knowledgeName.trim() === '') {
              return;
            }

            const contentObj = typeof log.content === 'object' && log.content !== null ? log.content as { context?: string; docs?: any[] } : null;
            const context = log.updates?.context || log.updates?.additional_kwargs?.context || contentObj?.context || '';
            const docs = log.updates?.docs || log.updates?.additional_kwargs?.docs || contentObj?.docs || [];
            const query = log.updates?.query || log.updates?.additional_kwargs?.query || '';
            const rewrittenQueries = log.updates?.rewritten_queries || log.updates?.additional_kwargs?.rewritten_queries || [];

            let logMessage = `[${nodeId}] 🔍 지식 검색 처리`;

            if (knowledgeName && knowledgeName.trim() !== '') {
              logMessage += `: ${knowledgeName}`;
            }

            logMessage += `\n━━━━━━━━━━━━━━━━\n`;

            if (knowledgeName && knowledgeName.trim() !== '') {
              logMessage += `📚 사용 지식: ${knowledgeName}\n`;
            }

            logMessage += `노드 타입: ${nodeType}`;

            if (knowledgeInfo.length > 0) {
              logMessage += `\n${knowledgeInfo.join(', ')}`;
            }
            logMessage += `\n`;

            if (query) {
              logMessage += `검색 질의: "${query}"\n`;
            }

            if (rewrittenQueries && Array.isArray(rewrittenQueries) && rewrittenQueries.length > 0) {
              logMessage += `재작성된 질의: ${JSON.stringify(rewrittenQueries, null, 2)}\n`;
            }

            if (context) {
              logMessage += `\n📄 검색 결과 (Context):\n${typeof context === 'string' ? context : JSON.stringify(context, null, 2)}\n`;
            }

            if (docs && Array.isArray(docs) && docs.length > 0) {
              logMessage += `\n📚 검색된 문서 수: ${docs.length}개\n`;
              docs.slice(0, 3).forEach((doc: any, index: number) => {
                const docContent = typeof doc === 'string' ? doc : (doc.content || doc.text || JSON.stringify(doc));
                logMessage += `\n문서 ${index + 1}:\n${docContent.substring(0, 200)}${docContent.length > 200 ? '...' : ''}\n`;
              });
              if (docs.length > 3) {
                logMessage += `\n... 외 ${docs.length - 3}개 문서\n`;
              }
            }

            if (log.progress && log.progress.trim()) {
              logMessage += `\n진행 상태: ${log.progress}\n`;
            }

            if (log.updates && Object.keys(log.updates).length > 0) {
              logMessage += `\n전체 업데이트:\n${JSON.stringify(log.updates, null, 2)}`;
            } else if (!context && (!docs || docs.length === 0)) {
              logMessage += `\n⚠️ 검색 결과가 없습니다. (updates가 비어있거나 지식 검색이 실패했을 수 있습니다.)`;
            }

            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: logMessage,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'retriever',
              turn: currentTurn,
            });
          } else if (nodeType === 'condition') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🔀 조건 분기 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Condition 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'condition',
              turn: currentTurn,
            });
          } else if (nodeType === 'agent__reviewer') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] ✅ 검토 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Reviewer 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'reviewer',
              turn: currentTurn,
            });
          } else if (nodeType === 'agent__categorizer') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 📂 카테고리 분류 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Categorizer 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'categorizer',
              turn: currentTurn,
            });
          } else if (nodeType === 'union') {
            const progressInfo = log.progress ? `진행 상태: ${log.progress}\n` : '';
            const updatesInfo = log.updates && Object.keys(log.updates).length > 0
              ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}`
              : 'Union 노드가 실행되었습니다.';

            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🔗 Union 처리\n━━━━━━━━━━━━━━━━\n${progressInfo}${updatesInfo}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'union',
              turn: currentTurn,
            });
          } else if (nodeType === 'merger') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🔀 Merger 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Merger 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'merger',
              turn: currentTurn,
            });
          } else if (nodeType === 'retriever__reranker') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 📊 ReRanker 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'ReRanker 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'reranker',
              turn: currentTurn,
            });
          } else if (nodeType === 'retriever__compressor') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 📦 문서 압축 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Doc Compressor 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'compressor',
              turn: currentTurn,
            });
          } else if (nodeType === 'retriever__filter') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🔍 문서 필터링 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Doc Filter 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'filter',
              turn: currentTurn,
            });
          } else if (nodeType === 'agent__app') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🤖 AgentApp 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'AgentApp 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'agent_app',
              turn: currentTurn,
            });
          } else if (nodeType === 'agent__tool') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 🔧 Tool 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Tool 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'tool',
              turn: currentTurn,
            });
          } else if (nodeType === 'agent__coder') {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 💻 Coder 처리\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : 'Coder 노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'coder',
              turn: currentTurn,
            });
          } else {
            filteredLogs.push({
              time: log.timestamp || log.request_time || new Date().toISOString(),
              log: `[${nodeId}] 📝 업데이트 (${nodeType})\n━━━━━━━━━━━━━━━━\n${log.updates && Object.keys(log.updates).length > 0 ? `업데이트:\n${JSON.stringify(log.updates, null, 2)}` : '노드가 실행되었습니다.'}`,
              nodeName: nodeId,
              nodeType: nodeType,
              type: 'updates',
              turn: currentTurn,
            });
          }
        }

        if (log.final_result && typeof log.final_result === 'string' && log.final_result.length > 10) {
          filteredLogs.push({
            time: log.timestamp || log.request_time || new Date().toISOString(),
            log: `[${nodeId}] 🎯 최종 결과\n${log.final_result}`,
            nodeName: nodeId,
            nodeType: nodeType,
            type: 'final_result',
            turn: currentTurn,
          });
        }
      });

      const progressLogs: any[] = [];
      const llmStreamingMap = new Map<string, any>();
      const otherLogs: any[] = [];

      filteredLogs.forEach((log, index) => {
        if (log.type === 'progress') {
          progressLogs.push({ ...log, _index: index });
        } else if (log.type === 'llm_streaming') {
          const key = `${log.nodeName}_llm_streaming`;
          llmStreamingMap.set(key, { ...log, _index: index });
        } else if (log.type === 'union') {
          otherLogs.push({ ...log, _index: index });
        } else {
          const existing = otherLogs.find(l => l.nodeName === log.nodeName && l.type === log.type);
          if (!existing) {
            otherLogs.push({ ...log, _index: index });
          }
        }
      });

      const allLogs = [
        ...progressLogs,
        ...Array.from(llmStreamingMap.values()),
        ...otherLogs
      ];

      const logs = allLogs.sort((a, b) => {
        if (a.time && b.time) {
          const timeA = new Date(a.time).getTime();
          const timeB = new Date(b.time).getTime();
          if (timeA !== timeB) return timeA - timeB;
        }
        return (a._index || 0) - (b._index || 0);
      });

      setBuilderLogState(prev => {
        if (turn !== undefined && typeof turn === 'number') {
          const prevLogsWithoutCurrentTurn = prev.filter((log: any) => {
            const logTurn = log.turn;
            return logTurn === undefined || logTurn !== turn;
          });
          const newLogs = [...prevLogsWithoutCurrentTurn, ...logs];

          return newLogs;
        } else {
          const newLogs = [...prev, ...logs];

          return newLogs;
        }
      });
    } catch (error) {
      setBuilderLogState([{
        time: new Date().toISOString(),
        log: `[ERROR] 로그 생성 중 오류 발생: ${error instanceof Error ? error.message : String(error)}`
      }]);
    }
  };

  const getNodeSpecificLogs = (nodeName: string,/* nodeType: string, */providedStreamLogs?: StreamLogEntry[]) => {
    const currentStreamLogs = providedStreamLogs || streamLogsRef.current;

    if (!currentStreamLogs || currentStreamLogs.length === 0) {
      return [];
    }

    const filteredLogs = currentStreamLogs.filter(log => {
      if (!log) return false;
      return log.node_name === nodeName;
    });

    const formattedLogs: any[] = [];

    filteredLogs.forEach(log => {
      let logMessage = '';
      let shouldInclude = false;

      if (log.progress && log.progress.trim()) {
        logMessage = `🔄 ${log.progress}`;
        shouldInclude = true;
      } else if (log.tool_calls) {
        logMessage = `🔧 Tool 호출: ${JSON.stringify(log.tool_calls, null, 2)}`;
        shouldInclude = true;
      } else if (log.tool_result) {
        logMessage = `✅ Tool 결과: ${JSON.stringify(log.tool_result, null, 2)}`;
        shouldInclude = true;
      } else if (log.updates && Object.keys(log.updates).length > 0) {
        if (log.updates.additional_kwargs?.user_input) {
          logMessage = `👤 사용자 입력 처리 완료\n입력: ${log.updates.additional_kwargs.user_input}`;
          shouldInclude = true;
        } else if (log.updates.messages) {
          let content = '';
          if (Array.isArray(log.updates.messages)) {
            content = log.updates.messages.map((m: any) => m.content || '').filter(Boolean).join('\n');
          } else if (log.updates.messages.content) {
            content = log.updates.messages.content;
          }

          if (content && content.trim()) {
            logMessage = `💬 AI 응답 생성 완료\n내용: ${content}`;
            shouldInclude = true;
          }
        } else if (log.updates.content) {
          logMessage = `🎯 최종 출력 완료\n출력: ${log.updates.content}`;
          shouldInclude = true;
        } else {
          logMessage = `📝 업데이트\n데이터: ${JSON.stringify(log.updates, null, 2)}`;
          shouldInclude = true;
        }
      }

      if (shouldInclude) {
        formattedLogs.push({
          time: log.request_time || log.timestamp || new Date().toISOString(),
          log: logMessage
        });
      }
    });

    return formattedLogs;
  };

  return {
    streamLogs,
    streamLogsRef,
    clearStreamLogs,
    addStreamLog,
    generateBuilderLogs,
    getNodeSpecificLogs,
  };
};