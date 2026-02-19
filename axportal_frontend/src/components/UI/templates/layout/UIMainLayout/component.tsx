import { Outlet } from 'react-router-dom';

import { UIContents, UIHeader, UILnb, type UIContentsProps, type UIHeaderProps, type UILnbProps } from '../../../organisms';

export function UIMainLayout({
  type = 'default',
  headerProps,
  lnbProps,
  contentsProps,
  children = <></>,
}: {
  type?: 'default' | 'design';
  headerProps: UIHeaderProps;
  lnbProps: UILnbProps;
  contentsProps?: UIContentsProps;
  children?: React.ReactNode;
}) {
  return (
    <div className='h-screen bg-gray-50 flex flex-col min-w-[1920px]'>
      <UIHeader {...headerProps} />
      <div className='flex flex-1 overflow-hidden'>
        <UILnb {...lnbProps} />
        <UIContents {...contentsProps}>{type === 'default' ? <Outlet /> : children}</UIContents>
      </div>
    </div>
  );
}
