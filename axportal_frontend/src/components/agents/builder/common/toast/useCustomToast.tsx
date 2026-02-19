import { useABToast } from '../../components/ui/ABToast';

type Position = 'top-right' | 'top-left' | 'top-center' | 'bottom-right' | 'bottom-left' | 'bottom-center';

type Status = 'success' | 'error' | 'warning' | 'info';

interface ToastProps {
  text: string;
  position?: Position;
  status?: Status;
  timer?: number;
  title?: string;
}

const useCustomToast = () => {
  const { showToast: abShowToast } = useABToast();

  const showToast = ({ text, title: _title = 'Toast Message', position: _position = 'top-right', status = 'info', timer: _timer = 3000 }: ToastProps) => {
    // ABToast를 사용하도록 변경
    abShowToast(text, status);
  };

  return { showToast };
};

export { useCustomToast };
