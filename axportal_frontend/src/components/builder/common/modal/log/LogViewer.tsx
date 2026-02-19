import { type Log } from '@/components/builder/atoms/logAtom.ts';
import { useGetFineTuningTrainingEvents } from '@/services/model/fineTuning/modelFineTuning.service';
import { json } from '@codemirror/lang-json';
import type { Extension } from '@codemirror/state';
import CodeMirror, { type ReactCodeMirrorRef } from '@uiw/react-codemirror';
import { useEffect, useMemo, useRef, useState } from 'react';

export const TRAINING_LOG_FETCH_LIMIT = 300;

const LogViewer = ({
  currentLogList,
  minHeight,
  maxHeight,
  maxWidth,
  readOnly,
  id,
  modalId,
}: {
  currentLogList: Log[];
  minHeight: string;
  maxHeight: string;
  maxWidth: string;
  readOnly: boolean;
  id?: string;
  modalId?: string;
}) => {
  const editorRef = useRef<ReactCodeMirrorRef>(null);
  const [logs, setLogs] = useState<Log[]>(currentLogList);
  const [lastLogTime, setLastLogTime] = useState<string | null>(null);
  const [hasMoreLogs, setHasMoreLogs] = useState(true);
  const debounceTimeout = useRef<number | null>(null);
  const prevLogCountRef = useRef<number>(currentLogList.length);

  // useGetFineTuningTrainingEvents 훅 사용
  const { refetch: refetchEvents } = useGetFineTuningTrainingEvents(
    {
      trainingId: id || '',
      after: lastLogTime || undefined,
      limit: TRAINING_LOG_FETCH_LIMIT,
    },
    {
      enabled: false,
    }
  );

  const combinedLogs = useMemo(() => {
    return logs.length > 0 ? logs.map((logData, index) => (logData.time ? `${logData.time}: ${logData.log}` : `Event[${index}]: ${logData.log}`)).join('\n') : 'Log is empty';
  }, [logs]);

  const getLanguageExtension = (): Extension => {
    switch ('json') {
      case 'json':
        return json();
    }
  };

  // Scroll to bottom function
  const scrollToBottom = () => {
    const editorElement = editorRef.current?.view?.dom;
    if (editorElement) {
      const scrollContainer = editorElement.querySelector('.cm-scroller');
      if (scrollContainer) {
        scrollContainer.scrollTop = scrollContainer.scrollHeight;
      }
    }
  };

  // Sync local state with currentLogList changes and scroll to bottom
  useEffect(() => {
    const currentLogCount = currentLogList.length;
    const prevLogCount = prevLogCountRef.current;

    // If log count increased significantly, it's likely a refresh
    if (currentLogCount > prevLogCount + 50) {
      setLogs(currentLogList);
      setLastLogTime(null);
      setHasMoreLogs(true);

      // Scroll to bottom after a short delay to ensure content is rendered
      setTimeout(() => {
        scrollToBottom();
      }, 100);
    } else if (currentLogCount !== prevLogCount) {
      // For any log count change, update local state
      setLogs(currentLogList);
    }

    prevLogCountRef.current = currentLogCount;
  }, [currentLogList]);

  useEffect(() => {
    const handleScroll = (event: Event) => {
      if (debounceTimeout.current !== null) {
        clearTimeout(debounceTimeout.current);
      }

      debounceTimeout.current = window.setTimeout(async () => {
        const target = event.target as HTMLElement;
        const { scrollTop, scrollHeight, clientHeight } = target;

        if (scrollTop + clientHeight >= scrollHeight - 1) {
          // If you want to do something for specific modalId, plz add here
          if (hasMoreLogs)
            if (modalId == 'fine_tuning_log' && currentLogList.length == TRAINING_LOG_FETCH_LIMIT) {
              await fetchMoreFineTuningLogs();
            }
        }
      }, 100);
    };

    const fetchMoreFineTuningLogs = async () => {
      if (hasMoreLogs && id) {
        const afterTime = lastLogTime == null ? logs[logs.length - 1].time : lastLogTime;
        if (afterTime) {
          setLastLogTime(afterTime);
        }
      }
    };

    const observer = new MutationObserver(() => {
      const editorElement = editorRef.current?.view?.dom;
      if (editorElement) {
        const scrollContainer = editorElement.querySelector('.cm-scroller');
        if (scrollContainer) {
          scrollContainer.addEventListener('scroll', handleScroll);
          observer.disconnect();
        }
      }
    });

    observer.observe(document.body, { childList: true, subtree: true });

    return () => {
      // eslint-disable-next-line react-hooks/exhaustive-deps
      const editorElement = editorRef.current?.view?.dom;
      if (editorElement) {
        const scrollElement = editorElement.querySelector('.cm-scroller');
        if (scrollElement) {
          scrollElement.removeEventListener('scroll', handleScroll);
        }
      }
      if (debounceTimeout.current !== null) {
        clearTimeout(debounceTimeout.current);
      }
      observer.disconnect();
    };
  }, [currentLogList.length, hasMoreLogs, id, lastLogTime, logs, modalId]);

  // shouldFetchMore가 true가 되면 refetch 호출
  useEffect(() => {
    if (hasMoreLogs && id) {
      refetchEvents()
        .then((result: any) => {
          if (result.data?.data) {
            const newLogs = result.data.data;
            setLogs(prevLogs => [...prevLogs, ...newLogs]);
            if (result.data.last) {
              setLastLogTime(result.data.last);
            }
            if (newLogs.length < TRAINING_LOG_FETCH_LIMIT) {
              setHasMoreLogs(false);
            }
          }
        })
        .catch((error: any) => {
          console.error('Failed to fetch more logs:', error);
        });
    }
  }, [hasMoreLogs, id, refetchEvents]);

  return (
    <CodeMirror
      ref={editorRef}
      value={combinedLogs}
      style={{
        minHeight,
        maxHeight,
        maxWidth,
        overflow: 'auto',
      }}
      basicSetup={{
        lineNumbers: true,
        foldGutter: true,
        highlightActiveLine: true,
        indentOnInput: true,
        tabSize: 2,
        bracketMatching: true,
      }}
      readOnly={readOnly}
      extensions={[getLanguageExtension()]}
      theme='dark'
      onChange={() => {}}
    />
  );
};

export default LogViewer;
