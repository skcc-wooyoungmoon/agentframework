import { DesignLayout } from '../../components/DesignLayout';

export const AG_010102_P19: React.FC = () => {
  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'agent', label: '에이전트' }}
        initialSubMenu={{
          id: 'agent-dev',
          label: '에이전트 개발',
          icon: 'ico-lnb-menu-20-agent',
        }}
      >
        <div>
          {/* 에이전트 개발 페이지 */}
        </div>
      </DesignLayout>
    </>
  );
};
