import { useEffect, useMemo } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { MODEL_GARDEN_STATUS_TYPE } from '@/constants/model/garden.constants';
import { MODEL_INPUT_MAX_LENGTH } from '@/constants/model/model.constants';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetModelProviders, useGetModelTypes } from '@/services/model/ctlg/modelCtlg.services';
import { useCreateModelGarden } from '@/services/model/garden/modelGarden.services';
import { useModal } from '@/stores/common/modal';
import { validationUtils } from '@/utils/common';

import type { ModelGardenInStepProps } from './ModelGardnIn';

/**
 * @author SGO1032948
 * @description 모델가든 Step3. 모델 정보 입력
 *
 * MD_050101_P03
 */
export const MdGdnImpStep2SvlsPopup = ({ currentStep, onClose, onPreviousStep, info, onSetInfo, onComplete }: ModelGardenInStepProps) => {
  const { data: typeList } = useGetModelTypes();
  const { data: providerList } = useGetModelProviders();
  const { openAlert } = useModal();
  const { showComplete } = useCommonPopup();

  useEffect(() => {
    if (typeList && providerList) {
      onSetInfo({
        type: typeList.types[0],
        provider: providerList.content[0].name,
        providerId: providerList.content[0].id,
      });
    }
  }, [typeList, providerList]);

  const inButtonDisabled = useMemo(() => {
    return (
      info.name === '' ||
      info.name.length > MODEL_INPUT_MAX_LENGTH.name ||
      (info.param_size.length !== 0 && info.param_size.length > MODEL_INPUT_MAX_LENGTH.param_size) ||
      info.url === '' ||
      info.url.length > MODEL_INPUT_MAX_LENGTH.url ||
      info.identifier === '' ||
      info.identifier.length > MODEL_INPUT_MAX_LENGTH.identifier
    );
  }, [info, MODEL_INPUT_MAX_LENGTH]);

  const { mutate: createModelGarden } = useCreateModelGarden();

  const handleClose = () => {
    onClose();
  };

  const handleInRequest = () => {
    createModelGarden(
      {
        serving_type: 'serverless',
        name: info.name,
        description: info.description,
        type: info.type,
        provider: info.provider,
        providerId: info.providerId,
        param_size: Number(info.param_size),
        url: info.url,
        identifier: info.identifier,
        statusNm: MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED, // 반입 완료 상태
      },
      {
        onSuccess: ({ data: { id } }) => {
          showComplete({
            itemName: '모델 반입을',
            onConfirm: () => {
              onComplete?.(id);
            },
          });
        },
        onError: e => {
          if (e.message.includes('G004')) {
            openAlert({
              title: '안내',
              message: '이미 동일한 모델명을 가진 모델이 반입되어 있습니다.',
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
      <UILayerPopup
        isOpen={currentStep === 2}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 반입' description='' position='left' />

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray' onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-aside-blue' disabled={inButtonDisabled} onClick={handleInRequest}>
                    반입요청
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='모델 정보 입력' description='' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  모델명
                </UITypography>
                <UIInput.Text
                  value={info.name}
                  maxLength={MODEL_INPUT_MAX_LENGTH.name}
                  placeholder='모델명 입력'
                  onChange={e => {
                    onSetInfo({ name: e.target.value });
                  }}
                  onBlur={e => onSetInfo({ identifier: e.target.value })}
                />
              </UIUnitGroup>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800'>
                  설명
                </UITypography>
                <UITextArea2
                  value={info.description}
                  maxLength={MODEL_INPUT_MAX_LENGTH.description}
                  placeholder='설명 입력'
                  onChange={e => onSetInfo({ description: e.target.value })}
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  모델 유형
                </UITypography>
                <UIDropdown
                  value={info.type}
                  options={typeList?.types.map(type => ({ value: type, label: type })) ?? []}
                  onSelect={value => {
                    onSetInfo({ type: value });
                  }}
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  공급사
                </UITypography>
                <UIDropdown
                  value={info.provider}
                  options={providerList?.content.map(({ id, name }) => ({ value: id, label: name })) ?? []}
                  onSelect={value => {
                    const provider = providerList?.content.find(provider => provider.id === value);
                    onSetInfo({ provider: provider?.name ?? '', providerId: provider?.id ?? '' });
                  }}
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-title-4-sb'>
                  파라미터 수(B)
                </UITypography>
                <UIInput.Text
                  value={info.param_size}
                  maxLength={MODEL_INPUT_MAX_LENGTH.param_size}
                  inputMode='decimal'
                  placeholder='파라미터 수 입력'
                  onChange={e => {
                    if (validationUtils.isValidData('decimal', e.target.value)) {
                      onSetInfo({ param_size: e.target.value });
                    }
                  }}
                />
              </UIUnitGroup>
            </UIArticle>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  URL
                </UITypography>
                <UIInput.Text value={info.url} maxLength={MODEL_INPUT_MAX_LENGTH.url} placeholder='URL 입력' onChange={e => onSetInfo({ url: e.target.value })} />
              </UIUnitGroup>
            </UIArticle>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  identifier
                </UITypography>
                <UIInput.Text readOnly value={info.identifier} placeholder='모델명 입력시 자동으로 입력됩니다.' />
              </UIUnitGroup>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <UIButton2 className='btn-secondary-gray' onClick={handlePrevious}>
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
