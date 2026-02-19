import { useEffect, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useModal } from '@/stores/common/modal';
import { useDeployAgent } from '@/stores/deploy/useDeployAgent';
import { SafetyFilterListPopup } from '../safetyFilter';

interface DeployAgentStep2InfoInputPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onNextStep: () => void;
  currentStep?: number;
}

export function DeployAgentStep2InfoInputPopupPage({ isOpen, stepperItems = [], onClose, onNextStep, currentStep }: DeployAgentStep2InfoInputPopupPageProps) {
  const { openModal, openConfirm } = useModal();

  // 세이프티필터 (선택된 필터 객체 배열)
  const [inputFilter, setInputFilter] = useState<any[]>([]);
  const [outputFilter, setOutputFilter] = useState<any[]>([]);

  // 모달 상태 디버깅
  const { deployData, updateDeployData, resetDeployData } = useDeployAgent();

  const [deployName, setDeployName] = useState(deployData.name || '');
  const [datasetDescription, setDatasetDescription] = useState(deployData.description || '');
  const [_, setErrorName] = useState<boolean>(false);
  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false); // save 버튼 클릭 여부

  // 팝업이 열릴 때 deployData에서 최신 값으로 동기화
  useEffect(() => {
    if (isOpen) {
      // deployData가 초기화된 상태(빈 값)이면 기본값으로 초기화
      if (deployData.targetId !== '') {
        // deployData에서 값 복원
        setDeployName(deployData.name || '');
        setDatasetDescription(deployData.description || '');
      }
    }
  }, [isOpen, deployData.targetId, deployData.name, deployData.description]);

  // 다음 버튼을 한 번 누른 후에는 실시간으로 유효성 검사
  useEffect(() => {
    if (hasAttemptedSave) {
      const isNameEmpty = deployName.trim() === '';
      setErrorName(isNameEmpty);
    }
  }, [hasAttemptedSave, deployName]);

  // 필터나 이름/설명이 변경될 때마다 deployData 자동 업데이트
  useEffect(() => {
    const safetyFilterOptions = {
      safety_filter_input: inputFilter.length > 0,
      safety_filter_output: outputFilter.length > 0,
      safety_filter_input_groups: inputFilter.map((filter: any) => filter.filterGroupId || filter.id || filter.uuid).filter(Boolean),
      safety_filter_output_groups: outputFilter.map((filter: any) => filter.filterGroupId || filter.id || filter.uuid).filter(Boolean),
    };

    updateDeployData({
      name: deployName,
      description: datasetDescription,
      servingType: 'standalone',
      versionDescription: datasetDescription,
      safetyFilterOptions,
    });
  }, [inputFilter, outputFilter]);

  /**
   * 닫기 버튼 클릭
   */
  const handleClose = () => {
    setDeployName('');
    setDatasetDescription('');
    setInputFilter([]);
    setOutputFilter([]);
    resetDeployData();
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        setErrorName(false);
        setHasAttemptedSave(false);
        handleClose();
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  const handleNextStep = () => {
    // save 버튼 클릭 표시
    setHasAttemptedSave(true);

    // 유효성 검사
    const isNameEmpty = deployName.trim() === '';

    // 에러가 있으면 저장하지 않음
    if (isNameEmpty) {
      // console.log('필수 항목이 누락되었습니다. 저장할 수 없습니다.');
      return;
    }

    // safetyFilterOptions 구성
    const safetyFilterOptions = {
      safety_filter_input: inputFilter.length > 0,
      safety_filter_output: outputFilter.length > 0,
      safety_filter_input_groups: inputFilter.map((filter: any) => filter.filterGroupId || filter.id || filter.uuid).filter(Boolean),
      safety_filter_output_groups: outputFilter.map((filter: any) => filter.filterGroupId || filter.id || filter.uuid).filter(Boolean),
    };

    updateDeployData({
      name: deployName,
      description: datasetDescription,
      servingType: 'standalone',
      versionDescription: datasetDescription, // 설명을 버전 설명으로도 사용
      safetyFilterOptions,
    });
    onNextStep();
  };

  // const handlePreviousStep = () => {
  //   onPreviousStep();
  // };

  /**
   * 세이프티 필터 팝업 오픈 이벤트
   */
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
          selectedList={inputFilter}
        />
      ),
      confirmText: '저장',
      onConfirm: () => {
        // 선택된 필터 객체 배열을 그대로 저장
        // useEffect에서 자동으로 deployData 업데이트됨
        setInputFilter(selectedRowsRef.current);
      },
    });
  };

  /**
   * 세이프티 필터 팝업 오픈 이벤트
   */
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
          selectedList={outputFilter}
        />
      ),
      confirmText: '저장',
      onConfirm: () => {
        // 선택된 필터 객체 배열을 그대로 저장
        // useEffect에서 자동으로 deployData 업데이트됨
        setOutputFilter(selectedRowsRef.current);
      },
    });
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
            <UIPopupHeader title='에이전트 배포하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={currentStep ?? 2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_CREATE} className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true} onClick={handleNextStep}>
                    배포하기
                  </Button>
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
          <UIPopupHeader title='배포 정보 입력' description='' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 역할명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  배포명
                </UITypography>
                <UIInput.Text
                  value={deployName}
                  disabled={deployData.name !== ''}
                  placeholder='이름 입력'
                  required={true}
                  maxLength={50}
                  onChange={e => setDeployName(e.target.value.slice(0, 50))}
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
                  value={datasetDescription}
                  placeholder='설명 입력'
                  required={true}
                  maxLength={100}
                  onChange={e => setDatasetDescription(e.target.value.slice(0, 100))}
                  // disabled={deployData.description !== ''} // 기존 방식: description이 있으면 disabled
                />
              </UIFormField>
            </UIArticle>

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

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  입력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text
                      value={inputFilter?.map((item: any) => item.filterGroupName)?.join(', ') || ''}
                      placeholder='입력 필터 입력'
                      onChange={e => {
                        if (e.target.value.trim() === '') {
                          setInputFilter([]);
                        }
                      }}
                    />
                  </div>
                  <div>
                    <Button className='btn-secondary-outline !min-w-[64px]' onClick={handelInputSafetyFilterPopup}>
                      선택
                    </Button>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  출력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text
                      value={outputFilter?.map((item: any) => item.filterGroupName)?.join(', ') || ''}
                      placeholder='출력 필터 입력'
                      onChange={e => {
                        if (e.target.value.trim() === '') {
                          setOutputFilter([]);
                        }
                      }}
                    />
                  </div>
                  <div>
                    <Button className='btn-secondary-outline !min-w-[64px]' onClick={handelOutputSafetyFilterPopup}>
                      선택
                    </Button>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                {/* <Button className='btn-secondary-gray' onClick={handlePreviousStep}>
                  이전
                </Button> */}
                <Button className='btn-secondary-blue' onClick={handleNextStep} disabled={deployName.trim() === ''}>
                  다음
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
}
