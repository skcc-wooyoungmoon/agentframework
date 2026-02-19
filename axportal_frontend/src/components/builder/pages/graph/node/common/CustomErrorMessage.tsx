import type { FC } from 'react';

export const CustomErrorMessage: FC<{ message: string; className?: string }> = ({ message, className }) => (
  <div className={`ml-2 flex items-start text-sm ag-color-red ${className}`}>
    <i className='ki-filled ki-information-1' aria-hidden='true' />
    <span>{message}</span>
  </div>
);
