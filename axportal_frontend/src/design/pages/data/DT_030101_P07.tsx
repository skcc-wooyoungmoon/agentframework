import { UICode } from '@/components/UI/atoms/UICode';

export const DT_030101_P07: React.FC = () => {
  return (
    <div className='flex h-full'>
      {/* 소스코드 영역 */}
      <UICode value={'여기는 에디터 화면입니다. 테스트 테스트 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='392px' maxHeight='392px' readOnly={false} />
    </div>
  );
};
