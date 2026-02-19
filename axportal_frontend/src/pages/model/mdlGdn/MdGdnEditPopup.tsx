import { useMemo, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { MODEL_INPUT_MAX_LENGTH } from '@/constants/model/model.constants';
import { usePermissionCheck } from '@/hooks/common/auth';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetModelProviders, useGetModelTypes } from '@/services/model/ctlg/modelCtlg.services';
import { useUpdateModelGarden } from '@/services/model/garden/modelGarden.services';
import type { ModelGardenInfo, UpdateModelGardenRequest } from '@/services/model/garden/types';
import { useModal } from '@/stores/common/modal/useModal';
import { validationUtils } from '@/utils/common';

/**
 *
 * @author SGO1032948
 * @description 모델가든 수정 팝업
 *
 * MD_050202_P01
 */
export const MdGdnEditPopup = ({ onClose, currentStep, onSubmit, modelGardenDetail }: LayerPopupProps & { onSubmit: () => void; modelGardenDetail: ModelGardenInfo }) => {
  const { data } = useGetModelTypes();
  const { data: providerData } = useGetModelProviders();
  const { checkPermissionAndShowAlert } = usePermissionCheck();
  const { openAlert } = useModal();
  const { showCancelConfirm, showNoEditContent } = useCommonPopup();

  // 수정 API
  const { mutate: updateModelGarden } = useUpdateModelGarden();

  // 수정 Request
  const initialModelGarden = useMemo(() => {
    return {
      id: modelGardenDetail.id,
      name: modelGardenDetail.name,
      description: modelGardenDetail.description,
      type: modelGardenDetail.type,
      provider: modelGardenDetail.provider,
      providerId: modelGardenDetail.providerId,
      param_size: modelGardenDetail.param_size,
      url: modelGardenDetail.url,
      identifier: modelGardenDetail.identifier,
    };
  }, [modelGardenDetail]);

  const [newModelGarden, setNewModelGarden] = useState<UpdateModelGardenRequest>(initialModelGarden);

  const isDisabled = useMemo(() => {
    return !!(
      newModelGarden &&
      // 이름
      (newModelGarden.name?.length === 0 ||
        (newModelGarden.name?.length && newModelGarden.name?.length > MODEL_INPUT_MAX_LENGTH.name) ||
        // 파라미터 수(B)
        (newModelGarden.param_size?.length !== 0 && newModelGarden.param_size?.length && newModelGarden.param_size?.length > MODEL_INPUT_MAX_LENGTH.param_size) ||
        // URL
        newModelGarden.url?.length === 0 ||
        (newModelGarden.url?.length && newModelGarden.url?.length > MODEL_INPUT_MAX_LENGTH.url))
    );
  }, [newModelGarden]);

  // 취소 버튼
  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  // 저장 버튼
  const handleSave = () => {
    // 수정사항이 없을 경우
    if (
      newModelGarden.name === initialModelGarden.name &&
      newModelGarden.description === initialModelGarden.description &&
      newModelGarden.type === initialModelGarden.type &&
      newModelGarden.provider === initialModelGarden.provider &&
      newModelGarden.param_size === initialModelGarden.param_size &&
      newModelGarden.url === initialModelGarden.url &&
      newModelGarden.identifier === initialModelGarden.identifier
    ) {
      showNoEditContent({});
      return;
    }
    // 수정
    checkPermissionAndShowAlert(() => {
      updateModelGarden(
        {
          ...newModelGarden,
        },
        {
          onSuccess: () => {
            openAlert({
              title: '완료',
              message: '수정사항이 저장되었습니다. 해당 모델을 Private 모델 관리 메뉴에 등록한 사용자에게 알림이 전송됩니다.',
              onConfirm: () => {
                onSubmit();
                onClose();
              },
            });
          },
        }
      );
    });
  };

  return (
    <>
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='serverless 모델 수정' description='' position='right' />
            <UIPopupBody></UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={isDisabled} onClick={handleSave}>
                    저장
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
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  모델명
                </UITypography>
                <UIInput.Text
                  value={newModelGarden.name}
                  maxLength={MODEL_INPUT_MAX_LENGTH.name}
                  onChange={e => setNewModelGarden({ ...newModelGarden, name: e.target.value })}
                  onBlur={() => {
                    setNewModelGarden({ ...newModelGarden, identifier: newModelGarden.name });
                  }}
                  placeholder='이름 입력'
                />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  설명
                </UITypography>
                <UITextArea2
                  value={newModelGarden.description ?? ''}
                  maxLength={MODEL_INPUT_MAX_LENGTH.description}
                  onChange={e => setNewModelGarden({ ...newModelGarden, description: e.target.value })}
                  placeholder='설명 입력'
                />
              </UIFormField>
            </UIArticle>

            {/* 모델 유형 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  모델 유형
                </UITypography>
                <UIDropdown
                  required={true}
                  value={newModelGarden.type ?? ''}
                  options={data?.types.map(item => ({ value: item, label: item })) ?? []}
                  onSelect={value => {
                    setNewModelGarden({ ...newModelGarden, type: value });
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 공급사 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  공급사
                </UITypography>
                <UIDropdown
                  required={true}
                  value={newModelGarden.provider ?? ''}
                  options={providerData?.content.map(item => ({ value: item.name, label: item.name })) ?? []}
                  onSelect={value => {
                    setNewModelGarden({ ...newModelGarden, provider: value });
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 파라미터 수(B) 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  파라미터 수(B)
                </UITypography>
                <UIInput.Text
                  value={newModelGarden.param_size ?? ''}
                  maxLength={MODEL_INPUT_MAX_LENGTH.param_size}
                  inputMode='decimal'
                  onChange={e => validationUtils.isValidData('decimal', e.target.value) && setNewModelGarden({ ...newModelGarden, param_size: e.target.value })}
                  placeholder='파라미터 수 입력'
                />
              </UIFormField>
            </UIArticle>

            {/* URL 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  URL
                </UITypography>
                <UIInput.Text
                  value={newModelGarden.url ?? ''}
                  maxLength={MODEL_INPUT_MAX_LENGTH.url}
                  onChange={e => setNewModelGarden({ ...newModelGarden, url: e.target.value })}
                  placeholder='URL 입력'
                />
              </UIFormField>
            </UIArticle>

            {/* identifier 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  identifier
                </UITypography>
                <UIInput.Text
                  readOnly
                  value={newModelGarden.identifier ?? ''}
                  maxLength={MODEL_INPUT_MAX_LENGTH.identifier}
                  onChange={e => setNewModelGarden({ ...newModelGarden, identifier: e.target.value })}
                  placeholder='모델명 입력시 자동으로 입력됩니다.'
                />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
