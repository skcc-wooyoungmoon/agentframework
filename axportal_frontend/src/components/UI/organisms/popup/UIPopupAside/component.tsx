import type { UIPopupAsideProps } from './types';

export const UIPopupAside: React.FC<UIPopupAsideProps> = ({ children, className = '' }) => {
  return (
    <div className={"w-full h-full flex flex-col p-10 space-y-8 bg-gray-100 " + className}>
      <section className='section-popup-aside'>{children}</section>
    </div>
  );
};
