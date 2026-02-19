import { useToast } from '../toast/useToast';

/**
 * @author SGO1032948
 * @description 복사 핸들러
 */
export const useCopyHandler = () => {
  const { toast } = useToast();
  const handleCopy = async (text: string) => {
    await navigator.clipboard.writeText(text);
    toast.success('복사가 완료되었습니다.');
  };
  return { handleCopy };
};
