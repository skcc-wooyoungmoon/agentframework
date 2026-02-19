import { DesignLayout } from '../components/DesignLayout';

export const ComponentSample: React.FC = () => {
  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 모델 카탈로그 > 모델 관리
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div>Component Sample Page</div>
      </DesignLayout>
    </>
  );
};
