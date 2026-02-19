import { useAtom } from 'jotai';
import { useEffect, useState } from 'react';
import { logState, type Log } from '../../..';
import LogViewer from './LogViewer';
interface LogModalProps {
  id?: string;
  trainingData?: any;
}

export const LogModal = ({ id }: LogModalProps) => {
  const [logDataList] = useAtom(logState);
  const [currentLogList, setCurrentLogList] = useState<Log[]>([]);

  useEffect(() => {
    setCurrentLogList(logDataList);
  }, [logDataList]);

  return (
    <div className='builder-log-modal w-full'>
      <div className='max-w-[960px] mx-auto'>
        <LogViewer currentLogList={currentLogList} minHeight='300px' maxHeight='600px' maxWidth='1120px' readOnly={false} id={id} />
      </div>
    </div>
  );
};
