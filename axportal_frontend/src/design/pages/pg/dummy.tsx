import { DesignLayout } from '../../components/DesignLayout';

export const LogPage = () => {
  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'user-mgmt',
        label: '사용자 관리',
        icon: 'ico-lnb-menu-20-manage-user',
      }}
    >
      <></>
    </DesignLayout>
  );
};
