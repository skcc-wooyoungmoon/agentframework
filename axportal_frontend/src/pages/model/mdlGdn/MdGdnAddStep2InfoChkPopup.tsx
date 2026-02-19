import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { MODEL_GARDEN_STATUS_TYPE } from '@/constants/model/garden.constants';
import { useCreateModelGarden } from '@/services/model/garden/modelGarden.services';

import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useModal } from '@/stores/common/modal';
import stringUtils from '@/utils/common/string.utils';
import type { ModelGardenSearchStepProps } from './MdGdnAdd';

/**
 * @author SGO1032948
 * @description Step2. 모델 정보 확인
 *
 * MD_050101_P06
 */
export const MdGdnAddStep2InfoChkPopup = ({ currentStep, onClose, onPreviousStep, data, onComplete }: ModelGardenSearchStepProps) => {
  const { mutate: createModelGarden } = useCreateModelGarden();
  const { openAlert } = useModal();
  const { showComplete } = useCommonPopup();
  const handleClose = () => {
    onClose();
  };
  const handleAdd = () => {
    createModelGarden(
      {
        serving_type: 'self-hosting',
        name: data?.name,
        description: data?.description,
        size: stringUtils.formatBytesToGB(data?.size) ?? '0',
        artifact_id: data?.id,
        revision_id: data?.revision_id,
        // version: data?.version,
        statusNm: MODEL_GARDEN_STATUS_TYPE.PENDING,
      },
      {
        onSuccess: ({ data: { id } }) => {
          showComplete({
            itemName: '반입할 모델 정보 조회에',
            onConfirm: () => {
              onComplete?.(id);
            },
          });
        },
        onError: e => {
          if (e.message.includes('G004')) {
            openAlert({
              title: '안내',
              message: '이미 동일한 모델명을 가진 모델이 목록에 있습니다.',
            });
          }
        },
      }
    );
  };
  const handlePrevious = () => {
    onPreviousStep();
  };

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 2}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 검색' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '추가할 모델 선택' },
                  { id: 'step2', step: 2, label: '모델 정보 확인' },
                ]}
                currentStep={2}
                direction='vertical'
              />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleAdd}>
                    추가
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}

        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='모델 정보 확인' description='선택한 모델 정보를 확인해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
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
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {data?.name}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            크기
                          </UITypography>
                        </th>
                        <td>{data?.size ? `${stringUtils.formatBytesToGB(data?.size)}GB` : '0GB'}</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handlePrevious}>
                  이전
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
