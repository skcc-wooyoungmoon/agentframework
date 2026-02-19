import { Button } from '@/components/common/auth';
import { UIIcon2, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal/useModal';
import type { MigDeployCategory } from '@/stores/deploy/types';
import { MIG_DEPLOY_CATEGORY_MAP } from '@/stores/deploy/types';
import { useMigDeploy } from '@/stores/deploy/useMigDeploy';
import { useEffect } from 'react';

interface MigDeployStep1TypeSelectPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onNextStep: () => void;
}

interface MigDeployConfirmBodyProps {
  message: string;
}

function MigDeployConfirmBody({ message }: MigDeployConfirmBodyProps) {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
          운영 배포를 정말 진행하시겠습니까?
        </UITypography>
        <div className='box-fill mt-6'>
          <UIGroup gap={8} direction='row' align='start' vAlign='start'>
            <UIIcon2 className='ic-system-16-info-gray w-[16px] top-[2px] relative' />
            <UITypography variant='body-2' className='secondary-neutral-600 flex-1'>
              {message}
            </UITypography>
          </UIGroup>
        </div>
      </UIArticle>
    </section>
  );
}

export function MigDeployStep1TypeSelectPopupPage({ isOpen, stepperItems = [], onClose, onNextStep }: MigDeployStep1TypeSelectPopupPageProps) {
  const { user } = useUser();
  const { openConfirm, openModal } = useModal();

  const { migDeployData, updateMigDeployData, resetMigDeployData } = useMigDeploy();

  // 팝업이 열릴 때 프로젝트 정보 초기화
  useEffect(() => {
    if (isOpen && user.activeProject) {
      updateMigDeployData({
        prjSeq: user.activeProject.prjSeq || migDeployData.prjSeq || '',
        prjNm: user.activeProject.prjNm || migDeployData.prjNm || '',
      });
    }
  }, [isOpen, user.activeProject]);

  const handleCategoryChange = (category: MigDeployCategory) => {
    updateMigDeployData({
      prjSeq: user.activeProject?.prjSeq || migDeployData.prjSeq || '',
      prjNm: user.activeProject?.prjNm || migDeployData.prjNm || '',
      category,
      categoryName: MIG_DEPLOY_CATEGORY_MAP[category],
    });
  };

  const handleClose = () => {
    resetMigDeployData();
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => {},
    });
  };

  const handleNextStep = () => {
    if (migDeployData.category === 'SERVING_MODEL') {
      openModal({
        type: '2xsmall',
        title: '안내',
        body: <MigDeployConfirmBody message='이행할 모델이 세이프티 필터를 참조하는 경우, 포탈 운영 환경에서 해당 세이프티 필터가 정상 동작 하는지 꼭 확인해주세요!' />,
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          onNextStep();
        },
      });
    } else if (migDeployData.category === 'KNOWLEDGE') {
      openModal({
        type: '2xsmall',
        title: '안내',
        body: <MigDeployConfirmBody message='이행할 지식이 참조하는 벡터 DB와 임베딩 모델이 포탈 운영 환경에서 정상 동작 하는지 꼭 확인해주세요!' />,
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          onNextStep();
        },
      });
    } else if (migDeployData.category === 'VECTOR_DB') {
      openModal({
        type: '2xsmall',
        title: '안내',
        body: <MigDeployConfirmBody message='이행할 벡터 DB가 참조하는 임베딩 모델이 포탈 운영 환경에서 정상 동작 하는지 꼭 확인해주세요!' />,
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          onNextStep();
        },
      });
    } else if (migDeployData.category === 'AGENT_APP') {
      openModal({
        type: '2xsmall',
        title: '안내',
        body: (
          <MigDeployConfirmBody message='이행할 에이전트가 참조하는 세이프티 필터, 지식, LLM을 참조 및 사용하는 경우, 포탈 운영 환경에서 해당 에셋들이 정상 동작 하는지 꼭 확인해주세요!' />
        ),
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          onNextStep();
        },
      });
    } else if (migDeployData.category === 'PROJECT') {
      openModal({
        type: '2xsmall',
        title: '안내',
        body: <MigDeployConfirmBody message='현재 위치한 프로젝트 정보가 배포되니 반드시 선택한 프로젝트를 꼭 확인해주세요!' />,
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          onNextStep();
        },
      });
    } else {
      // 세이프티 필터, 가드레일의 경우 그냥 NEXT
      onNextStep();
    }
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
            <UIPopupHeader title='운영 이행' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
               <UIStepper items={migDeployData.category === 'PROJECT' || migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS' ? [stepperItems[0], stepperItems[1], { ...stepperItems[3], step: 3 }] : stepperItems} currentStep={1} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
                    이행
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='분류 선택' description='운영 이행을 원하는 분류를 선택해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <div className='inline-flex items-center'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    분류
                  </UITypography>
                </div>
                <div>
                  <UIUnitGroup gap={12} direction='column' align='start'>
                    <UIRadio2
                      name='category'
                      value='PROJECT'
                      label='프로젝트'
                      checked={migDeployData.category === 'PROJECT'}
                      onChange={(checked, value) => {
                        if (checked) handleCategoryChange(value as MigDeployCategory);
                      }}
                    />
                    <UIRadio2
                      name='category'
                      value='SAFETY_FILTER'
                      label='세이프티 필터'
                      checked={migDeployData.category === 'SAFETY_FILTER'}
                      onChange={(checked, value) => {
                        if (checked) handleCategoryChange(value as MigDeployCategory);
                      }}
                    />
                    <UIRadio2
                      name='category'
                      value='GUARDRAILS'
                      label='가드레일'
                      checked={migDeployData.category === 'GUARDRAILS'}
                      onChange={(checked, value) => {
                        if (checked) handleCategoryChange(value as MigDeployCategory);
                      }}
                    />
                    <UIRadio2
                      name='category'
                      value='KNOWLEDGE'
                      label='지식'
                      checked={migDeployData.category === 'KNOWLEDGE'}
                      onChange={(checked, value) => {
                        if (checked) handleCategoryChange(value as MigDeployCategory);
                      }}
                    />
                    <UIRadio2
                      name='category'
                      value='SERVING_MODEL'
                      label='모델'
                      checked={migDeployData.category === 'SERVING_MODEL'}
                      onChange={(checked, value) => {
                        if (checked) handleCategoryChange(value as MigDeployCategory);
                      }}
                    />
                    <UIRadio2
                      name='category'
                      value='VECTOR_DB'
                      label='벡터 DB'
                      checked={migDeployData.category === 'VECTOR_DB'}
                      onChange={(checked, value) => {
                        if (checked) handleCategoryChange(value as MigDeployCategory);
                      }}
                    />
                    <UIRadio2
                      name='category'
                      value='AGENT_APP'
                      label='에이전트'
                      checked={migDeployData.category === 'AGENT_APP'}
                      onChange={(checked, value) => {
                        if (checked) handleCategoryChange(value as MigDeployCategory);
                      }}
                    />
                  </UIUnitGroup>
                </div>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                    <UIIcon2 className='ic-system-16-info-gray' />
                    <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                      운영 환경에서 정상 작동을 위해 아래 항목을 반드시 사전에 운영 이행해 주세요.
                    </UITypography>
                  </div>
                  <div style={{ paddingLeft: '22px' }}>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'모델의 경우 : 참조 세이프티 필터'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'가드레일의 경우 : 참조 모델'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'지식의 경우 : 참조 벡터 DB, 참조 임베딩 모델'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'에이전트의 경우 : 참조 세이프티 필터, 참조 가드레일, 참조 지식, 참조 LLM'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                    </UIUnitGroup>
                  </div>
                </UIUnitGroup>
              </div>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-secondary-blue' onClick={handleNextStep}>
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
