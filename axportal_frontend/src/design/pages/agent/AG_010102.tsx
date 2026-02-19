import { UIPageHeader } from '../../../components/UI/molecules/UIPageHeader';
import { DesignLayout } from '../../components/DesignLayout';

// AG_010102 페이지 - 빌더 헤더 타입
export const AG_010102 = () => {
  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-builder',
        label: '빌더',
        icon: 'ico-lnb-menu-20-agent-builder',
      }}
      contentsBgColor='#EFF5FF'
    >
      <div className='flex flex-col h-full'>
        <UIPageHeader title='빌더 캔버스' />

        {/* 워크플로우 빌더 메인 영역 */}
        <div className='flex flex-1 overflow-hidden mt-12'>
          {/* 좌측 사이드바 */}
          {/* <UIWorkflowSidebar selectedNodeId={selectedNodeId} onNodeClick={handleNodeClick} /> */}

          {/* 빌더 콘텐츠 영역 - 추후 구현 예정 */}
          <div className='flex-1 bg-[#EFF5FF]'>{/* 콘텐츠 영역은 나중에 구현 */}</div>
        </div>
      </div>
    </DesignLayout>
  );
};
