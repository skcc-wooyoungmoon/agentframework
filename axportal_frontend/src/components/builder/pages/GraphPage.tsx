import type { Agent } from '@/components/builder/types/Agents';
import { DnDProvider } from '@/components/builder/utils/DnDContext';
import { useModal } from '@/stores/common/modal';
import { ReactFlowProvider } from '@xyflow/react';
import { useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import Graph from './graph/Graph';
import GraphSidebar from './graph/GraphSidebar';

const GraphPage = () => {
  const { openAlert } = useModal();
  const location = useLocation();
  const currentGraph: Agent = location.state?.data || {};
  const isReadOnly = location.state?.isReadOnly || false;
  const hasShownAlertRef = useRef(false);

  // console.log('GraphPage location ★★★ : ', location);

  useEffect(() => {
    if (!isReadOnly || hasShownAlertRef.current) {
      return;
    }
    hasShownAlertRef.current = true;

    const message = [
      '현재 진입하신 화면은 조회 전용 모드입니다. 편집이나 수정이 불가능합니다.',
      '조회 모드에서 가능한 기능',
      '• 그래프 확대/축소 및 이동',
      '• 노드와 연결선 구조 확인',
      '• 그래프 분석 및 상태 점검',
    ].join('\n');

    openAlert({
      title: '안내',
      message,
    });
  }, [isReadOnly, openAlert]);

  return (
    <ReactFlowProvider>
      <DnDProvider>
        <style>
          {`
                .graph-wrap {
                  margin: -40px -48px !important;
                  width: calc(100% + 96px) !important;
                  height: calc(100vh - 70px) !important;
                  overflow: hidden;
                }
                
                .react-flow__viewport {
                  overflow: visible !important;
                }
                
                .react-flow__renderer {
                  overflow: visible !important;
                }
              `}
        </style>
        <div className='graph-wrap' style={{ width: '100%', height: 'calc(100vh - 70px)', position: 'relative' }}>
          {/* 노드 사이드바 */}
          <div
            className='graph-side-bar'
            style={{
              position: 'absolute',
              top: '93px',
              left: '50px',
              width: '250px',
              height: 'auto', // 내용에 맞게 자동 조정
              maxHeight: 'calc(100vh - 176px)', // 최대 높이 제한 (top 증가분만큼 조정)
              zIndex: 1,
              backgroundColor: 'rgba(255, 255, 255, 0.95)',
              borderRadius: '12px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
              border: '1px solid rgba(0, 0, 0, 0.1)',
              backdropFilter: 'blur(10px)',
              transition: 'all 0.3s ease',
              display: 'flex',
              flexDirection: 'column',
            }}
          >
            <GraphSidebar readOnly={isReadOnly} />
          </div>
          <Graph data={currentGraph} readOnly={isReadOnly} />
        </div>
      </DnDProvider>
    </ReactFlowProvider>
  );
};

export default GraphPage;
