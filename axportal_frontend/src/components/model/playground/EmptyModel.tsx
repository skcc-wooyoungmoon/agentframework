import { UIButton2, UIPlaygroundCardBox, UITypography } from '@/components/UI';

interface EmptyModelProps {
  openModal: () => void;
}

export const EmptyModel = ({ openModal }: EmptyModelProps) => {
  return (
    <UIPlaygroundCardBox>
      {/* PlaygroundCardContent */}
      <div className='box-container'>
        <div className='response-none'>
          <UITypography variant='body-2' className='secondary-neutral-800'>
            모델별 응답을 비교할 수 있습니다.
          </UITypography>
          <UIButton2 className='btn-option-outlined' onClick={openModal}>
            모델 추가
          </UIButton2>
        </div>
      </div>
    </UIPlaygroundCardBox>
  );
};
