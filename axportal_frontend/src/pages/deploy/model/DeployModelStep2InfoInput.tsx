import { UIButton2, UICode, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UINotice } from '@/components/UI/atoms/UINotice';
import { UIArticle, UIDropdown, UIFormField, UIGroup, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useGetDockerImgUrl } from '@/services/deploy/model/modelDeploy.services.ts';
import type { InfoInputDataType } from '@/services/deploy/model/types';
import type { ModelCtlgType } from '@/services/model/ctlg/types.ts';
import { useModal } from '@/stores/common/modal';
import { type Dispatch, type SetStateAction, useMemo, useState } from 'react';
import { SafetyFilterListPopup } from '../safetyFilter';

interface DeployModelStep2InfoInputProps extends LayerPopupProps {
  selectedModel: ModelCtlgType | undefined;
  infoInputData: InfoInputDataType;
  setInfoInputData: Dispatch<SetStateAction<InfoInputDataType>>;
  onClickCreateDeployModel: (isDirectDeploy?: boolean) => void;
  advancedSettingInitialize: () => void;
  envsInitialize: () => void;
}

export const DeployModelStep2InfoInput = ({
  currentStep,
  stepperItems = [],
  onClose,
  onNextStep,
  onPreviousStep,
  selectedModel,
  infoInputData,
  setInfoInputData,
  onClickCreateDeployModel,
  advancedSettingInitialize,
  envsInitialize,
}: DeployModelStep2InfoInputProps) => {
  const [isDirectDeploy, setIsDirectDeploy] = useState(false);
  const [selectedFrame, setSelectedFrame] = useState(infoInputData.selectedFrame);
  const [selectedFrameVer, setSelectedFrameVer] = useState(infoInputData.selectedFrameVer);
  const [codeKey, setCodeKey] = useState(true);
  const [envCodeKey, setEnvCodeKey] = useState(true);

  const { openModal } = useModal();

  const { data: dockerImgUrlList } = useGetDockerImgUrl({ sysUV: selectedFrame === 'vLLM' ? 'vLLM' : 'SGLang' });

  const dockerImgUrlOption = useMemo(() => {
    if (!dockerImgUrlList) {
      return [];
    }

    return dockerImgUrlList.map(item => ({ value: item.imgUrl, label: item.imgUrl }));
  }, [dockerImgUrlList]);

  const handleAdvancedSettingInitialize = () => {
    advancedSettingInitialize();
    // 초기화 시 key를 변경하여 컴포넌트 리마운트
    setCodeKey(prev => !prev);
  };

  const handleEnvInitialize = () => {
    envsInitialize();
    setEnvCodeKey(prev => !prev);
  };

  const handleClose = () => {
    onClose();
  };

  const handleFrameworkChange = (checked: boolean, value: string) => {
    if (checked) {
      setSelectedFrame(value);
      setSelectedFrameVer('');

      // 상태 업데이트
      setInfoInputData(prev => ({
        ...prev,
        selectedFrame: value,
        selectedFrameVer: '',
      }));
    }
  };

  const handleFrameworkVersionChange = (value: string) => {
    setSelectedFrameVer(value);

    // 상태 업데이트
    setInfoInputData(prev => ({
      ...prev,
      selectedFrameVer: value,
    }));
  };

  const validateRequiredFields = () => {
    if (!selectedModel) {
      return true;
    } else if (!infoInputData.name) {
      return true;
    } else if (selectedModel?.servingType === 'self-hosting' && (!infoInputData.selectedFrame || !infoInputData.selectedFrameVer)) {
      return true;
    }

    // 배포명 유효성 검증: 영문, 숫자, '-', '_'만 허용, 영문 또는 숫자로 시작/끝나야 함
    const nameValue = infoInputData.name.trim();
    if (nameValue.length < 4) return true;
    if (nameValue) {
      // 정규식 체크: 영문 또는 숫자로 시작하고 끝나야 함
      // 1자일 때: ^[a-zA-Z0-9]$
      // 2자 이상: ^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$
      const namePattern = /^(?:[a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9])$/;
      if (!namePattern.test(nameValue)) {
        return true; // 유효성 검증 실패
      }
    }

    return false;
  };

  const handleCreateDeployModel = () => {
    onClickCreateDeployModel(true);
  };

  const handelInputSafetyFilterPopup = () => {
    const selectedRowsRef = { current: [] as any[] };

    openModal({
      type: 'large',
      title: '세이프티 필터',
      body: (
        <SafetyFilterListPopup
          onConfirm={rows => {
            selectedRowsRef.current = rows;
          }}
          selectedList={infoInputData.inputFilter || []}
        />
      ),
      confirmText: '저장',
      onConfirm: () => {
        // console.log('선택된 세이프티 필터:', selectedRowsRef.current);
        setInfoInputData(prev => ({
          ...prev,
          inputFilter: selectedRowsRef.current,
        }));
      },
    });
  };

  const handelOutputSafetyFilterPopup = () => {
    const selectedRowsRef = { current: [] as any[] };

    openModal({
      type: 'large',
      title: '세이프티 필터',
      body: (
        <SafetyFilterListPopup
          onConfirm={rows => {
            selectedRowsRef.current = rows;
          }}
          selectedList={infoInputData.outputFilter || []}
        />
      ),
      confirmText: '저장',
      onConfirm: () => {
        setInfoInputData(prev => ({
          ...prev,
          outputFilter: selectedRowsRef.current,
        }));
      },
    });
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
            <UIPopupHeader title='모델 배포하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2
                    className='btn-tertiary-blue'
                    style={{ width: '80px' }}
                    disabled={(() => {
                      if (selectedModel?.servingType === 'self-hosting') {
                        return true;
                      }
                      return validateRequiredFields();
                    })()}
                    onClick={() => onClickCreateDeployModel(true)}
                  >
                    배포
                  </UIButton2>
                </UIUnitGroup>
                <div className='mt-20'>
                  {/* // TODO YERI 우회 */}
                  {/* {env.VITE_RUN_MODE !== 'PROD' && */}
                  {!isDirectDeploy ? (
                    <UIButton2 className='w-10 h-10' onClick={() => setIsDirectDeploy(true)}></UIButton2>
                  ) : (
                    <UIButton2 className='btn-secondary-blue' disabled={validateRequiredFields()} onClick={() => onClickCreateDeployModel(true)}>
                      바로 배포
                    </UIButton2>
                  )}
                </div>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader
            title='배포 정보 입력'
            description='배포 정보를 입력 해주세요. 이미 등록된 배포명이나, 삭제 처리 중인 배포명과 동일한 이름은 중복으로 인식되어 등록할 수 없습니다.'
            position='right'
          />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 배포명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    배포명
                  </UITypography>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    영문 및 숫자와 -, _ 만 사용할 수 있으며 영문 또는 숫자로 시작하고 끝나도록 입력되어야합니다.
                  </UITypography>
                </UIGroup>
                <div>
                  <UIInput.Text
                    value={infoInputData.name}
                    maxLength={24}
                    onChange={e => {
                      const inputValue = e.target.value;

                      // 빈 문자열 허용 (삭제 중일 때)
                      if (inputValue === '') {
                        setInfoInputData(prev => ({ ...prev, name: '' }));
                        return;
                      }

                      // 허용된 문자만 입력: 영문, 숫자, '-', '_'만 허용
                      const allowedPattern = /^[a-zA-Z0-9_-]*$/;
                      if (!allowedPattern.test(inputValue)) {
                        return; // 허용되지 않는 문자가 있으면 입력 차단
                      }

                      // 입력이 영문 또는 숫자로 시작해야 함
                      if (!/^[a-zA-Z0-9]/.test(inputValue)) {
                        return; // 영문 또는 숫자로 시작하지 않으면 입력 차단
                      }

                      setInfoInputData(prev => ({ ...prev, name: inputValue }));
                    }}
                    placeholder='배포명 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  설명
                </UITypography>
                <UITextArea2
                  value={infoInputData.description}
                  onChange={e => setInfoInputData(prev => ({ ...prev, description: e.target.value }))}
                  placeholder='설명 입력'
                  maxLength={100}
                />
              </UIFormField>
            </UIArticle>

            {selectedModel?.servingType === 'self-hosting' && (
              <UIArticle>
                <div className='article-body'>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                      프레임워크
                    </UITypography>
                    <UIUnitGroup gap={12} direction='column' align='start'>
                      <UIRadio2 name='basic1' label='vLLM' value='vLLM' checked={selectedFrame === 'vLLM'} onChange={handleFrameworkChange} />
                      <UIRadio2 name='basic1' label='SGLang' value='SGLang' checked={selectedFrame === 'SGLang'} onChange={handleFrameworkChange} />
                    </UIUnitGroup>
                  </UIFormField>
                </div>
              </UIArticle>
            )}

            {selectedModel?.servingType === 'self-hosting' && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  {/*<UIGroup gap={0} direction='row'>*/}
                  {/*  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>*/}
                  {/*    vLLM 버전*/}
                  {/*  </UITypography>*/}
                  {/*</UIGroup>*/}
                  <UIDropdown value={selectedFrameVer} options={dockerImgUrlOption} onClick={() => { }} onSelect={(value: string) => handleFrameworkVersionChange(value)} />
                </UIFormField>
              </UIArticle>
            )}

            <UIArticle>
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-3' className='secondary-neutral-900'>
                  세이프티 필터
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  아래에서 별도로 필터를 선택하지 않은 경우, 기본 필터링 기능만 자동으로 적용됩니다.
                </UITypography>
              </UIGroup>
            </UIArticle>

            {/* 입력 필터 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  입력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text value={infoInputData.inputFilter?.map(item => item.filterGroupName)?.join(',')} placeholder='입력 필터 입력' />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-outline' style={{ minWidth: '64px' }} onClick={handelInputSafetyFilterPopup}>
                      선택
                    </UIButton2>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 출력 필터 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  출력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text value={infoInputData.outputFilter?.map(item => item.filterGroupName)?.join(',')} placeholder='출력 필터 입력' />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-outline' style={{ minWidth: '64px' }} onClick={handelOutputSafetyFilterPopup}>
                      선택
                    </UIButton2>
                  </div>
                </UIUnitGroup>
                <UINotice variant='info' message='스트리밍 출력에 대해서는 출력 필터 지원이 불가능합니다.' bulletType='circle' />
              </UIFormField>
            </UIArticle>

            {selectedModel?.servingType === 'self-hosting' && (
              <>
                <UIArticle>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                      Advanced Setting
                    </UITypography>

                    <UIUnitGroup direction='row' align='space-between' gap={0}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        아래에서 별도로 Advanced setting을 수정하지 않을 경우, 기본 Advanced setting값으로 적용됩니다.
                      </UITypography>
                      <UIButton2 className='btn-option-outlined' onClick={handleAdvancedSettingInitialize}>
                        초기화
                      </UIButton2>
                    </UIUnitGroup>
                    <UICode
                      key={codeKey.toString()}
                      value={JSON.stringify(infoInputData?.advancedValue, null, 2)}
                      onChange={value => {
                        try {
                          const parsedValue = JSON.parse(value);
                          setInfoInputData(prev => ({ ...prev, advancedValue: parsedValue }));
                        } catch (error) {
                          // JSON이 아직 완성되지 않았거나 유효하지 않은 경우 무시
                          // 사용자가 계속 입력할 수 있도록 에러를 발생시키지 않음
                        }
                      }}
                      language='json'
                      theme='dark'
                      width='100%'
                      minHeight='272px'
                      maxHeight='272px'
                    // readOnly={!infoInputData?.advancedChecked}
                    />
                  </UIFormField>
                </UIArticle>

                <UIArticle>
                  <UIFormField gap={8} direction='column'>
                    <UIUnitGroup direction='row' align='space-between' gap={0}>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                        envs
                      </UITypography>
                      <UIButton2 className='btn-option-outlined' onClick={handleEnvInitialize}>
                        초기화
                      </UIButton2>
                    </UIUnitGroup>

                    {/* 소스코드 영역 */}

                    <UICode
                      key={envCodeKey.toString()}
                      value={JSON.stringify(infoInputData?.envs ?? {}, null, 2)}
                      onChange={value => {
                        try {
                          const parsedValue = JSON.parse(value);
                          setInfoInputData(prev => ({ ...prev, envs: parsedValue }));
                        } catch (error) {
                          // JSON이 아직 완성되지 않았거나 유효하지 않은 경우 무시
                          // 사용자가 계속 입력할 수 있도록 에러를 발생시키지 않음
                        }
                      }}
                      language='json'
                      theme='dark'
                      width='100%'
                      minHeight='272px'
                      maxHeight='272px'
                    // readOnly={!infoInputData?.advancedChecked}
                    />
                  </UIFormField>
                </UIArticle>
              </>
            )}
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' onClick={onPreviousStep}>
                  이전
                </UIButton2>
                {selectedModel?.servingType === 'self-hosting' && (
                  <UIButton2 className='btn-secondary-blue' disabled={validateRequiredFields()} onClick={onNextStep}>
                    다음
                  </UIButton2>
                )}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
