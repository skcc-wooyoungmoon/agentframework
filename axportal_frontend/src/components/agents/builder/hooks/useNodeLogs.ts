import { useSetAtom } from 'jotai';
import { nodesAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { useStreamLogs } from '@/components/agents/builder/hooks/useStreamLogs.ts';
import { builderLogState } from '@/components/agents/builder/atoms/logAtom.ts';
import { useAtom } from 'jotai';
import { type CustomNode } from '@/components/agents/builder/types/Agents';

export const useNodeLogs = () => {
  const setNodes = useSetAtom(nodesAtom);
  const [globalLogs] = useAtom(builderLogState);
  const { streamLogsRef, getNodeSpecificLogs } = useStreamLogs();
  const updateNodeLogs = (currentTurn?: number, retryCount: number = 0) => {
    const currentStreamLogs = streamLogsRef.current;

    setNodes(prev => {
      return prev.map(node => {
        const nodeName = String(node.data?.name || node.id);
        let allLogs: any[] = [];

        if (globalLogs.length > 0) {
          let targetTurn: number | undefined = currentTurn;
          if (targetTurn === undefined) {
            const allTurns = globalLogs
              .map(log => (log as any).turn)
              .filter((turn): turn is number => typeof turn === 'number');
            targetTurn = allTurns.length > 0 ? Math.max(...allTurns) : undefined;
          }

          const nodeFilteredLogs = globalLogs.filter(log => {
            const matchesNode = (log as any).nodeName === nodeName ||
              (() => {
                const logStr = typeof log.log === 'string' ? log.log : String(log.log || '');
                return logStr.includes(`[${nodeName}]`) || logStr.includes(nodeName);
              })();

            if (!matchesNode) return false;

            if (targetTurn !== undefined) {
              const logTurn = (log as any).turn;
              return logTurn === targetTurn;
            }

            return true;
          });

          if (nodeFilteredLogs.length > 0) {
            allLogs = [...nodeFilteredLogs];
          }
        }

        if (allLogs.length === 0) {
          const filteredStreamLogs = currentTurn !== undefined
            ? currentStreamLogs.filter(log => {
              const logTurn = log.turn;
              return logTurn === currentTurn;
            })
            : currentStreamLogs;

          const nodeSpecificLogs = getNodeSpecificLogs(nodeName, filteredStreamLogs);
          const errorLogs = filteredStreamLogs.filter(log => log.type === 'error');
          allLogs = [...nodeSpecificLogs, ...errorLogs.map(log => ({
            time: log.timestamp,
            log: `🚨 ${log.errorType === 'python_error' ? 'Python Error' : 'System Error'}: ${log.content}`
          }))];

          if (allLogs.length === 0 && currentTurn !== undefined && retryCount < 3) {
          }
        } else {
          const filteredStreamLogs = currentTurn !== undefined
            ? currentStreamLogs.filter(log => {
              const logTurn = log.turn;
              return logTurn === currentTurn;
            })
            : currentStreamLogs;
          const errorLogs = filteredStreamLogs.filter(log => log.type === 'error');
          allLogs = [...allLogs, ...errorLogs.map(log => ({
            time: log.timestamp,
            log: `🚨 ${log.errorType === 'python_error' ? 'Python Error' : 'System Error'}: ${log.content}`
          }))];
        }

        return {
          ...node,
          data: {
            ...node.data,
            innerData: {
              ...node.data.innerData,
              logData: allLogs as any
            }
          }
        } as CustomNode;
      });
    });
  };

  return {
    updateNodeLogs,
  };
};
