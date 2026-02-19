import { builderLogState } from '@/components/agents/builder/atoms/logAtom.ts';
import { nodesAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { tracingMessagesAtom } from '@/components/agents/builder/atoms/messagesAtom.ts';
import { useAtom } from 'jotai';
import { type FC, useMemo } from 'react';
import { formatLogData } from '@/components/agents/builder/utils/logDataFormatter.ts';

interface LogModalProps {
  id?: string;
  nodeId?: string;
}

const LogModal: FC<LogModalProps> = ({ nodeId: targetNodeId }) => {
  const [builderLogs] = useAtom(builderLogState);
  const [nodes] = useAtom(nodesAtom);
  const [tracingMessages] = useAtom(tracingMessagesAtom);

  const logsToDisplay = useMemo(() => {
    let rawLogs: any[] = [];

    if (targetNodeId) {
      const targetNode = nodes.find(node => {
        const nodeName = String(node.data?.name || node.id);
        return nodeName === targetNodeId || node.id === targetNodeId;
      });


      if (targetNode?.data?.innerData?.logData) {
        const nodeLogData = formatLogData(targetNode.data.innerData.logData);
        if (nodeLogData.length > 0) {
          rawLogs = nodeLogData;
        }
      }
    }

    if (rawLogs.length === 0 && targetNodeId && tracingMessages.length > 0) {
      const allTurns = tracingMessages
        .map(trace => trace.turn)
        .filter((turn): turn is number => typeof turn === 'number');
      const latestTurn = allTurns.length > 0 ? Math.max(...allTurns) : undefined;

      const filteredTracingLogs = tracingMessages.filter(trace => {
        const nodeId = trace.node_name || trace.nodeName || trace.node_id || trace.nodeId || '';
        const matchesNode = nodeId === targetNodeId;

        if (!matchesNode) return false;

        if (latestTurn !== undefined) {
          const traceTurn = trace.turn;
          return traceTurn === latestTurn;
        }

        return true;
      });

      if (filteredTracingLogs.length > 0) {
        const formattedTracingLogs: Array<{ time: string; log: string; type: string }> = [];

        const processedOutputNodes = new Set<string>();
        const getKnowledgeInfo = (nodeData: any) => {
          const data = (nodeData?.data || {}) as any;
          const repoId = data.repo_id || data.schemaData?.repo_id || data.knowledge_retriever?.repo_id || (data.knowledge_retriever as any)?.knowledge_info?.repo_id || data.knowledge_id || '';
          const repoKind = data.repo_kind || data.schemaData?.repo_kind || data.knowledge_retriever?.repo_kind || (data.knowledge_retriever as any)?.knowledge_info?.repo_kind || '';
          const knowledgeRetriever = data.knowledge_retriever as { name?: string; repo_id?: string } | undefined;
          const knowledgeName = data._knowledgeName || data.knowledge_name || knowledgeRetriever?.name || '알 수 없는 지식';
          return { repoId, repoKind, knowledgeName };
        };

        filteredTracingLogs.forEach((trace, index) => {
          const nodeId = trace.node_name || trace.nodeName || trace.node_id || trace.nodeId || 'unknown';
          const nodeType = trace.node_type || trace.nodeType || '';
          const timeStamp = `[${(index * 0.1).toFixed(1)}s]`;
          const turn = typeof trace.turn === 'number' && trace.turn > 0 ? trace.turn : undefined;
          const turnLabel = turn ? `#${turn} ` : '';

          if (trace.progress && trace.progress.trim()) {
            if (nodeType === 'union') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🔗 Union 처리\n━━━━━━━━━━━━━━━━\n${trace.progress}`,
                type: 'union',
              });
            } else {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🔄 진행: ${trace.progress}`,
                type: 'progress',
              });
            }
          }

          if (trace.llm && trace.llm.content && trace.llm.content.trim()) {
            formattedTracingLogs.push({
              time: timeStamp,
              log: `${turnLabel}[${nodeId}] 🤖 LLM (스트리밍): ${trace.llm.content}`,
              type: 'llm_streaming',
            });
          }

          if (trace.tool_calls) {
            formattedTracingLogs.push({
              time: timeStamp,
              log: `${turnLabel}[${nodeId}] 🔧 Tool 호출\n${JSON.stringify(trace.tool_calls, null, 2)}`,
              type: 'tool_calls',
            });
          }

          if (trace.tool_result) {
            formattedTracingLogs.push({
              time: timeStamp,
              log: `${turnLabel}[${nodeId}] ✅ Tool 결과\n${JSON.stringify(trace.tool_result, null, 2)}`,
              type: 'tool_result',
            });
          }

          if (trace.updates && Object.keys(trace.updates).length > 0) {
            if (nodeType === 'input__basic') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 📝 입력 데이터 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'structured',
              });
            } else if (nodeType === 'retriever__rewriter_hyde') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] ✍️ Rewriter HyDE 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'rewriter',
              });
            } else if (nodeType === 'agent__generator') {
              let usedKnowledgeInfo = '';
              const currentTurn = turn;
              for (let i = 0; i < index; i++) {
                const prevTrace = filteredTracingLogs[i];
                const prevNodeType = prevTrace.node_type || prevTrace.nodeType || '';
                const prevTurn = typeof prevTrace.turn === 'number' && prevTrace.turn > 0 ? prevTrace.turn : undefined;

                if (prevTurn === currentTurn && prevNodeType === 'retriever__knowledge') {
                  const prevNodeId = prevTrace.node_name || prevTrace.nodeName || prevTrace.node_id || prevTrace.nodeId || '';
                  const prevNodeData = nodes.find(n => {
                    const nName = n.data?.name || n.id;
                    return nName === prevNodeId || n.id === prevNodeId;
                  });

                  if (prevNodeData) {
                    const { repoId, repoKind, knowledgeName } = getKnowledgeInfo(prevNodeData);
                    if (knowledgeName) {
                      usedKnowledgeInfo = `\n━━━━━━━━━━━━━━━━\n📚 사용된 지식: ${knowledgeName}`;
                      if (repoId) {
                        usedKnowledgeInfo += ` (ID: ${String(repoId).substring(0, 8)}...)`;
                      }
                      if (repoKind) {
                        usedKnowledgeInfo += `\n지식 베이스 종류: ${repoKind}`;
                      }
                      usedKnowledgeInfo += `\n━━━━━━━━━━━━━━━━`;
                      break;
                    }
                  }
                }
              }

              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🤖 Generator 처리${usedKnowledgeInfo}\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'generator',
              });
            } else if (nodeType === 'output__chat' || nodeType === 'output__keys' || nodeType === 'output__selector' || nodeType === 'output__formatter') {
              const turnKey = turn || '0';
              const outputKey = `output_${nodeId}_${turnKey}`;
              if (!processedOutputNodes.has(outputKey)) {
                formattedTracingLogs.push({
                  time: timeStamp,
                  log: `${turnLabel}[${nodeId}] 📤 출력 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                  type: 'output',
                });
                processedOutputNodes.add(outputKey);
              }
            } else if (nodeType === 'retriever__rewriter_hyde') {
              const rewrittenQuery = (trace.updates as any)?.query || (trace.tool_result as any)?.query || (trace.updates as any)?.rewritten_query || '';
              const originalQuery = (trace.updates as any)?.original_query || (trace.tool_result as any)?.original_query || '';

              let rewriterLog = `${turnLabel}[${nodeId}] ✍️ Rewriter HyDE 처리\n━━━━━━━━━━━━━━━━\n`;

              if (originalQuery) {
                rewriterLog += `원본 질의: ${originalQuery}\n`;
              }
              if (rewrittenQuery) {
                rewriterLog += `재작성된 질의: ${rewrittenQuery}\n`;
              }
              rewriterLog += `\n📋 전체 업데이트 정보:\n${JSON.stringify(trace.updates, null, 2)}`;

              formattedTracingLogs.push({
                time: timeStamp,
                log: rewriterLog,
                type: 'rewriter',
              });
            } else if (nodeType === 'retriever__knowledge') {
              const nodeData = nodes.find(n => {
                const nName = n.data?.name || n.id;
                return nName === nodeId || n.id === nodeId;
              });
              const data = (nodeData?.data || {}) as any;

              const repoId = data.repo_id || data.schemaData?.repo_id || data.knowledge_retriever?.repo_id || (data.knowledge_retriever as any)?.knowledge_info?.repo_id || data.knowledge_id || '';
              const repoKind = data.repo_kind || data.schemaData?.repo_kind || data.knowledge_retriever?.repo_kind || (data.knowledge_retriever as any)?.knowledge_info?.repo_kind || '';
              const knowledgeRetriever = data.knowledge_retriever as { name?: string; repo_id?: string } | undefined;
              const knowledgeName = data._knowledgeName || data.knowledge_name || knowledgeRetriever?.name || '알 수 없는 지식';

              const updates = trace.updates || {};
              const toolResult = trace.tool_result || {};
              const output = updates.output || toolResult.output || {};
              const result = updates.result || toolResult.result || {};

              const retrievedDocs =
                updates.retrieved_docs ||
                updates.data_chunks ||
                output.retrieved_docs ||
                output.data_chunks ||
                result.retrieved_docs ||
                result.data_chunks ||
                toolResult.retrieved_docs ||
                toolResult.data_chunks ||
                [];

              const retrievedChunks =
                updates.retrieved_chunks ||
                output.retrieved_chunks ||
                result.retrieved_chunks ||
                toolResult.retrieved_chunks ||
                [];

              const dataChunks =
                updates.data_chunks ||
                output.data_chunks ||
                result.data_chunks ||
                toolResult.data_chunks ||
                [];

              const docCount = Array.isArray(retrievedDocs) ? retrievedDocs.length : 0;
              const chunkCount = Array.isArray(retrievedChunks) ? retrievedChunks.length : (Array.isArray(dataChunks) ? dataChunks.length : 0);

              // 지식 사용 정보 구성
              let knowledgeInfo = `📚 사용된 지식: ${knowledgeName}`;
              if (repoId) {
                knowledgeInfo += ` (ID: ${String(repoId).substring(0, 8)}...)`;
              }
              const knowledgeDetails: string[] = [];
              if (repoId) knowledgeDetails.push(`지식 베이스 ID: ${repoId}`);
              if (repoKind) knowledgeDetails.push(`지식 베이스 종류: ${repoKind}`);
              if (knowledgeDetails.length > 0) {
                knowledgeInfo += `\n${knowledgeDetails.join(', ')}`;
              }
              knowledgeInfo += `\n━━━━━━━━━━━━━━━━`;

              if (docCount > 0 || chunkCount > 0) {
                knowledgeInfo += `\n📊 검색 결과:`;
                if (docCount > 0) knowledgeInfo += `\n  - 검색된 문서: ${docCount}개`;
                if (chunkCount > 0) knowledgeInfo += `\n  - 검색된 청크: ${chunkCount}개`;
              } else {
                knowledgeInfo += `\n⚠️ 검색된 문서/청크가 없습니다.`;
              }
              knowledgeInfo += `\n\n📋 전체 업데이트 정보:\n${JSON.stringify(trace.updates, null, 2)}`;

              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🔍 지식 검색 처리\n${knowledgeInfo}`,
                type: 'retriever',
              });
            }
            // Condition 노드
            else if (nodeType === 'condition') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🔀 조건 분기 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'condition',
              });
            }
            // Reviewer 노드
            else if (nodeType === 'agent__reviewer') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] ✅ 검토 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'reviewer',
              });
            }
            // Categorizer 노드
            else if (nodeType === 'agent__categorizer') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 📂 카테고리 분류 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'categorizer',
              });
            }
            // Union 노드
            else if (nodeType === 'union' || nodeType === 'union_1') {
              const progressInfo = trace.progress ? `진행 상태: ${trace.progress}\n` : '';
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🔗 Union 처리\n━━━━━━━━━━━━━━━━\n${progressInfo}업데이트:\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'union',
              });
            }
            // Merger 노드
            else if (nodeType === 'merger') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🔀 Merger 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'merger',
              });
            }
            // ReRanker 노드
            else if (nodeType === 'retriever__reranker') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 📊 ReRanker 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'reranker',
              });
            }
            // Doc Compressor 노드
            else if (nodeType === 'retriever__compressor') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 📦 문서 압축 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'compressor',
              });
            }
            // Doc Filter 노드
            else if (nodeType === 'retriever__filter') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 🔍 문서 필터링 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'filter',
              });
            }
            // Coder 노드
            else if (nodeType === 'agent__coder') {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 💻 Coder 처리\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'coder',
              });
            }
            // 기타 모든 노드
            else {
              formattedTracingLogs.push({
                time: timeStamp,
                log: `${turnLabel}[${nodeId}] 📝 업데이트 (${nodeType || 'unknown'})\n${JSON.stringify(trace.updates, null, 2)}`,
                type: 'structured',
              });
            }
          }

          // Final result
          if (trace.final_result !== undefined) {
            const finalContent = typeof trace.final_result === 'string'
              ? trace.final_result
              : JSON.stringify(trace.final_result, null, 2);
            formattedTracingLogs.push({
              time: timeStamp,
              log: `${turnLabel}[${nodeId}] 🎯 최종 출력\n━━━━━━━━━━━━━━━━\n출력: ${finalContent}`,
              type: 'final_result',
            });
          }
        });

        // 변환된 로그를 formatLogData 형식으로 변환
        rawLogs = formattedTracingLogs.map(log => ({
          time: log.time,
          log: log.log,
        }));
      }
    }

    // 🔥 3. tracingMessages도 없으면 builderLogState에서 필터링
    if (rawLogs.length === 0) {
      if (!targetNodeId) {
        // 🔥 최신 turn의 로그만 사용 (재생성 시에도 최신 로그만 표시)
        const allTurns = builderLogs
          .map(log => (log as any).turn)
          .filter((turn): turn is number => typeof turn === 'number');
        const latestTurn = allTurns.length > 0 ? Math.max(...allTurns) : undefined;

        if (latestTurn !== undefined) {
          rawLogs = builderLogs.filter(log => {
            const logTurn = (log as any).turn;
            // 🔥 재생성 시: turn이 정확히 일치하는 로그만 사용 (undefined는 제외)
            return logTurn === latestTurn;
          });
        } else {
          rawLogs = builderLogs;
        }
      } else {
        // 🔥 최신 turn 찾기
        const allTurns = builderLogs
          .map(log => (log as any).turn)
          .filter((turn): turn is number => typeof turn === 'number');
        const latestTurn = allTurns.length > 0 ? Math.max(...allTurns) : undefined;

        const filteredLogs = builderLogs.filter(log => {
          // nodeName 필드가 있으면 정확히 매칭
          const matchesNode = (log as any).nodeName === targetNodeId ||
            (() => {
              // nodeName 필드가 없으면 log 문자열에서 검색
              let logStr = '';
              if (typeof log.log === 'string') {
                logStr = log.log;
              } else if (typeof log.log === 'object' && log.log !== null) {
                try {
                  logStr = JSON.stringify(log.log);
                } catch (e) {
                  logStr = String(log.log || '');
                }
              } else {
                logStr = String(log.log || '');
              }
              const nodeMatch = logStr.match(/\[([^\]]+)\]/);
              const logNodeId = nodeMatch ? nodeMatch[1] : '';
              return logNodeId === targetNodeId;
            })();

          if (!matchesNode) return false;

          // 🔥 최신 turn의 로그만 사용 (재생성 시에도 최신 로그만 표시)
          if (latestTurn !== undefined) {
            const logTurn = (log as any).turn;
            // 🔥 재생성 시: turn이 정확히 일치하는 로그만 사용 (undefined는 제외)
            return logTurn === latestTurn;
          }

          return true;
        });

        // 필터링된 로그가 있으면 사용, 없으면 전체 로그 사용 (디버깅용)
        rawLogs = filteredLogs.length > 0 ? filteredLogs : builderLogs;

      }
    }

    // 🔥 모든 로그를 formatLogData로 정규화하여 타입 통일
    const formattedLogs = formatLogData(rawLogs);

    return formattedLogs;
  }, [builderLogs, targetNodeId, nodes, tracingMessages]);

  // 로그 데이터를 깔끔한 JSON 형식으로 변환
  const formattedLogsForDisplay = useMemo(() => {
    return logsToDisplay.map((log, index) => {
      const result: any = {
        index: index + 1,
        time: log.time,
      };

      // log 필드 처리
      if (typeof log.log === 'string') {
        // 이미 포맷팅된 문자열인 경우 (tracingMessages에서 온 경우)
        // JSON 파싱 시도
        try {
          const parsed = JSON.parse(log.log);
          result.log = parsed;
        } catch (e) {
          // JSON 파싱 실패 시 원본 문자열을 log 필드에 저장
          result.log = log.log;
        }
      } else if (typeof log.log === 'object' && log.log !== null) {
        // 이미 객체인 경우
        result.log = log.log;
      } else {
        result.log = log.log || '';
      }

      return result;
    });
  }, [logsToDisplay]);

  return (
    <div className='builder-log-modal w-full'>
      <div className='max-w-[960px] mx-auto'>
        <div className='max-h-[600px] min-h-[300px] overflow-y-auto bg-[#000000] text-[#d1d5db] rounded-xl p-4'>
          {logsToDisplay.length > 0 ? (
            <>
              <pre className='font-mono text-xs whitespace-pre-wrap break-words text-gray-200 overflow-x-auto'>
                {JSON.stringify(formattedLogsForDisplay, null, 2)}
              </pre>
            </>
          ) : (
            <div className='flex h-full items-center justify-center text-sm text-gray-400 min-h-[300px]'>
              로그가 비어있습니다.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export { LogModal };
