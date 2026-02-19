import { useEffect, useMemo, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { MODEL_GARDEN_STATUS_TYPE } from '@/constants/model/garden.constants';
import { MODEL_INPUT_MAX_LENGTH } from '@/constants/model/model.constants';
import { useGetModelProviders, useGetModelTypes } from '@/services/model/ctlg/modelCtlg.services';
import { validationUtils } from '@/utils/common';

import { useUpdateModelGarden } from '../../../services/model/garden/modelGarden.services';
import { useModal } from '../../../stores/common/modal';

import type { ModelGardenInStepProps } from './ModelGardnIn';

// 스테퍼 데이터
const stepperItems = [
  {
    id: 'step1',
    label: '반입 모델 선택',
    step: 1,
  },
  {
    id: 'step2',
    label: '모델 정보 입력',
    step: 2,
  },
];

/**
 * @author SGO1032948
 * @description Step3. 모델 정보 입력
 *
 * MD_050101_P07
 */
export const MdGdnImpStep3Popup = ({ currentStep, onClose, onPreviousStep, info, onSetInfo, onComplete }: ModelGardenInStepProps) => {
  const { openAlert } = useModal();

  const { data: typeList } = useGetModelTypes();
  const { data: providerList } = useGetModelProviders();

  useEffect(() => {
    if (typeList && providerList) {
      onSetInfo({
        type: typeList.types[0],
        provider: providerList.content[0].name,
        providerId: providerList.content[0].id,
      });
    }
  }, [typeList, providerList]);

  const [tags, setTags] = useState<string[]>([]);
  const [langauges, setLangauges] = useState<string[]>([]);

  const { mutate: updateModelGarden } = useUpdateModelGarden();

  const inButtonDisabled = useMemo(() => {
    return (
      (!!info &&
        (info.name === '' ||
          (info.name?.length ?? 0) > MODEL_INPUT_MAX_LENGTH.name ||
          (info.param_size && info.param_size.length !== 0 && info.param_size.length > MODEL_INPUT_MAX_LENGTH.param_size) ||
          info.url === '' ||
          (info.url?.length ?? 0) > MODEL_INPUT_MAX_LENGTH.url)) ||
      info.license === '' ||
      info.license?.length > MODEL_INPUT_MAX_LENGTH.license
    );
  }, [info, MODEL_INPUT_MAX_LENGTH]);

  const handleClose = () => {
    onClose();
  };

  const handleInRequest = () => {
    updateModelGarden(
      {
        ...info,
        statusNm: MODEL_GARDEN_STATUS_TYPE.IMPORT_REQUEST, // 반입 요청
        tags: tags.join(',') ?? '',
        langauges: langauges.join(',') ?? '',
        providerId: info.providerId,
      },
      {
        onSuccess: ({ data: { id } }) => {
          openAlert({
            title: '안내',
            message: '모델 반입 요청이 완료되었습니다.\n백신검사, 내부망 반입 및 취약점 점검이 순서대로 진행되며 취약점 점검 완료시 결재 요청이 가능합니다.',
            onConfirm: () => {
              onComplete?.(id); // 완료 콜백 호출
            },
          });
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
        isOpen={currentStep === 3}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 반입' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray' onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-aside-blue' onClick={handleInRequest} disabled={inButtonDisabled}>
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
          <UIPopupHeader title='모델 정보 입력' description='입력한 정보는 모델 관리 목록에서 모델 등록 시 자동 입력됩니다.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  모델명
                </UITypography>
                <UIInput.Text value={info.name} maxLength={MODEL_INPUT_MAX_LENGTH.name} placeholder='이름 입력' readOnly />
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
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-title-4-sb'>
                  모델 크기(GB)
                </UITypography>
                <UIInput.Text
                  value={info.size ?? ''}
                  onChange={e => {
                    onSetInfo({ size: e.target.value });
                  }}
                  placeholder='모델 크기 입력'
                  readOnly
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UIDropdown
                  label='공급사'
                  required={true}
                  value={info.provider}
                  options={providerList?.content.map(provider => ({ value: provider.id, label: provider.name })) ?? []}
                  onSelect={value => {
                    const provider = providerList?.content.find(provider => provider.id === value);
                    // console.log('💫 ModelGardenInStep3Popup provider', provider, value);
                    onSetInfo({ provider: provider?.name ?? '', providerId: provider?.id ?? '' });
                  }}
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UIDropdown
                  label='모델 유형'
                  required={true}
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
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  라이센스
                </UITypography>
                <UIInput.Text
                  required={true}
                  value={info.license}
                  maxLength={MODEL_INPUT_MAX_LENGTH.license}
                  onChange={e => {
                    onSetInfo({ license: e.target.value });
                  }}
                  placeholder='라이센스 입력'
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

            {/* 태그 입력 필드 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} label='태그' />
            </UIArticle>

            {/* 지원 언어 입력 필드 */}
            <UIArticle>
              <UIInput.Tags tags={langauges} onChange={setLangauges} label='지원 언어' placeholder='지원 언어 입력' />
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
