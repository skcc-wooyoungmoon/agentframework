import React from 'react';
// import toast, { Toaster } from "react-hot-toast";
import type { UIToasterProps } from './types';

export const UIToaster: React.FC<UIToasterProps> = () => {
  // return <Toaster position={position} />;
  return null;
};

/**
 * 커스텀 토스트 메시지를 표시하는 훅
 * @param options - Toaster 옵션 (duration 등)
 * @returns 토스트 함수
 *
 * @example
 * const showToast = useToaster();
 * showToast("복사가 완료되었습니다.");
//  */
// export const useToaster = (options: UseToasterOptions = {}) => {
//   const { duration = 2000 } = options;

//   const showToast = useCallback(
//     (message: string) => {
//       toast.custom(
//         t => (
//           <div className={`${t.visible ? 'animate-custom-enter' : 'animate-custom-leave'} toast`}>
//             <div className='toast-content'>
//               <UIUnitGroup gap={8} direction='row' vAlign='center' align='center'>
//                 <UIIcon2 className='ic-system-24-complete' aria-hidden='true' />
//                 <UITypography variant='body-1' className='text-white'>
//                   {message}
//                 </UITypography>
//               </UIUnitGroup>
//             </div>
//           </div>
//         ),
//         {
//           duration,
//         }
//       );
//     },
//     [duration]
//   );

//   return showToast;
// };
