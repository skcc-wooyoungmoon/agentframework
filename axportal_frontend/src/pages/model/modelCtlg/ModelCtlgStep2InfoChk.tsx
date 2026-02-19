import React from 'react';
import { UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { MODEL_DEPLOY_PROVIDER } from '@/constants/deploy/modelDeploy.constants';
import { useGetModelGardenDetail } from '@/services/model/garden/modelGarden.services.ts';
import type { ModelGardenInfo } from '@/services/model/garden/types.ts';
import { ManagerInfoBox } from '@/components/common';

interface ModelCtlgStep2InfoChkProps {
  isOpen: boolean;
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
  stepperItems: UIStepperItem[];
  selectedModelGarden: ModelGardenInfo | undefined;
}

export const ModelCtlgStep2InfoChk: React.FC<ModelCtlgStep2InfoChkProps> = ({
  isOpen,
  onNextStep,
  onPreviousStep,
  onClose,
  stepperItems,
  selectedModelGarden,
}: ModelCtlgStep2InfoChkProps) => {
  const { data: modelGardenDetail } = useGetModelGardenDetail(selectedModelGarden?.id ?? '', {
    enabled: !!selectedModelGarden?.id,
  });
  // console.log('modelGardenDetail', modelGardenDetail);

  const handleClose = () => {
    onClose();
  };

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 등록' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper items={stepperItems} currentStep={2} direction='vertical' />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    등록
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='모델 정보 확인' description='선택한 모델 정보를 확인해주세요' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 모델 테이블 필드 */}
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-base font-semibold'>
                  모델 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            모델명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600 text-body-2-r'>
                            {modelGardenDetail?.name}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            공급사
                          </UITypography>
                        </th>
                        <td>
                          <UIGroup gap={4} direction='row' vAlign='center'>
                            <UIIcon2 className={MODEL_DEPLOY_PROVIDER[modelGardenDetail?.provider as keyof typeof MODEL_DEPLOY_PROVIDER] || MODEL_DEPLOY_PROVIDER.Etc} />
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {modelGardenDetail?.provider}
                            </UITypography>
                          </UIGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            설명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {modelGardenDetail?.description}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            URL
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {modelGardenDetail?.url}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Identifier
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {modelGardenDetail?.identifier}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
            {/* 담당자 정보 테이블 필드 */}
            <ManagerInfoBox
              type='memberId'
              people={[
                { userId: modelGardenDetail?.created_by ?? '', datetime: modelGardenDetail?.created_at ?? '' },
                { userId: modelGardenDetail?.updated_by ?? '', datetime: modelGardenDetail?.updated_at ?? '' },
              ]}
            />
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={onNextStep}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
