import { UIToast, UIToastTypeEnum, type UIToastType } from '@/components/UI/molecules/toast/UIToast';
import { useMemo } from 'react';
import toast from 'react-hot-toast';

const DEFAULT_DURATION = 2000; // 2초

interface UseToastOptions {
  /** 토스트 표시 시간 (ms) - 기본값: 2000 */
  duration?: number;
}

type ToastFn = {
  (message: string, options?: UseToastOptions): void;
  success: (message: string, options?: UseToastOptions) => void;
  error: (message: string, options?: UseToastOptions) => void;
  // warning: (message: string, options?: UseToastOptions) => void;
  // info: (message: string, options?: UseToastOptions) => void;
};

export const useToast = () => {
  const customToast = useMemo<ToastFn>(() => {
    const showToast = (message: string, type: UIToastType = UIToastTypeEnum.DEFAULT, options?: UseToastOptions) => {
      const { duration = DEFAULT_DURATION } = options ?? {};
      toast.custom(t => <UIToast id={t.id} message={message} type={type} visible={t.visible} />, { duration });
    };

    // 기본 호출: toast('메시지')
    const toastFn = (message: string, options?: UseToastOptions) => {
      showToast(message, UIToastTypeEnum.DEFAULT, options);
    };

    // 메서드 추가: toast.success('메시지'), toast.error('메시지') 등
    // Object.assign
    return Object.assign(toastFn, {
      success: (message: string, options?: UseToastOptions) => showToast(message, UIToastTypeEnum.SUCCESS, options),
      error: (message: string, options?: UseToastOptions) => showToast(message, UIToastTypeEnum.ERROR, options),
      // warning: (message: string, options?: UseToastOptions) => showToast(message, UIToastTypeEnum.WARNING, options),
      // info: (message: string, options?: UseToastOptions) => showToast(message, UIToastTypeEnum.INFO, options),
    });
  }, []);

  return { toast: customToast };
};
