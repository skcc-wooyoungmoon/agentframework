// AG_010102_P24 페이지 - 노드 로그 (모달 컨텐츠용)
import { UICode } from '@/components/UI/atoms/UICode';

export const AG_010102_P24 = () => {
  return (
    <div className='flex h-full'>
      {/* 소스코드 영역 */}
      <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='500px' readOnly={false} />
    </div>
  );
};
