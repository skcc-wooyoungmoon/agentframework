import { UICode } from '@/components/UI/atoms/UICode';

export const DT_020303_P01 = () => {

  return (
    <div className='flex h-full'>
      {/* 소스코드 영역 */}
      <UICode value={'여기는 에디터 화면입니다. 테스트 테스트 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='472px' maxHeight='472px' readOnly={true} />
    </div>
  );
};
