import { DesignLayout } from '../../components/DesignLayout';

// AG_010102_P03 페이지 - 빌더 헤더 타입
export const AG_010102_P03 = () => {

  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-builder',
        label: '빌더',
        icon: 'ico-lnb-menu-20-agent-builder',
      }}
      contentsBgColor='#EFF5FF'
      enableHorizontalScroll={true}
    >
      <div>
        {/* 빌더 헤더 타입 */}
      </div>
    </DesignLayout>
  );
};
