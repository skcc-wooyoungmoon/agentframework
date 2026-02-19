import { useMemo, useState } from 'react';

import { useNavigate, useParams } from 'react-router-dom';

import { ManagerInfoBox } from '@/components/common/manager';
import { MdGdnDetailSelfHosting, MdGdnDetailServerless } from '@/components/model/garden';
import { UITypography } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { MODEL_GARDEN_BUTTON_STATUS, MODEL_GARDEN_STATUS_TYPE } from '@/constants/model/garden.constants';
import { usePermissionCheck } from '@/hooks/common/auth';
import { useLayerPopup } from '@/hooks/common/layer/useLayerPopup';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { PayReqWizard, type PayReqWizardProps } from '@/pages/common/PayReqWizrad';
import { useDeleteModelGarden, useGetModelGardenDetail, useUpdateModelGarden } from '@/services/model/garden/modelGarden.services';
import type { ModelGardenInfo } from '@/services/model/garden/types';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { MODAL_ID } from '@/constants/modal/modalId.constants';
import { MdGdnDoipResultPopup } from './MdGdnDoipResultPopup';
import { MdGdnEditPopup } from './MdGdnEditPopup';
import { ModelGardnIn } from './ModelGardnIn';

export type MdGdnDetailInfoProps = {
  modelInfo: ModelGardenInfo;
};

/**
 * @author SGO1032948
 * @description 모델가든 상세 페이지
 *
 * 	MD_050102
 * 	MD_050202
 */
export const MdlGdnDetailPage = () => {
  const navigate = useNavigate();
  const { openConfirm, openModal } = useModal();
  const { showDeleteComplete } = useCommonPopup();
  const { id } = useParams();
  const { user } = useUser();
  const { checkPermissionAndShowAlert } = usePermissionCheck();
  const modelInPopup = useLayerPopup();

  // 모델 가든 아이디가 없으면 404 페이지로 이동
  if (!id || id === '' || id === undefined) {
    navigate('/not-found');
  }

  // API
  // 모델 가든 등록 정보 상세 조회
  const { data: modelGardenDetail, refetch } = useGetModelGardenDetail(id ?? '');

  // 모델 가든 등록 정보 업데이트
  const { mutate: updateModelGarden } = useUpdateModelGarden();

  // 배포 유형 체크
  const isSelftHosting = useMemo(() => {
    return modelGardenDetail?.serving_type === 'self-hosting';
  }, [modelGardenDetail?.type]);

  const modelInfo = useMemo(() => {
    if (
      modelGardenDetail?.serving_type === 'self-hosting' &&
      !(
        modelGardenDetail?.statusNm === MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED_UNREGISTERED ||
        modelGardenDetail?.statusNm === MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED_REGISTERED
      )
    ) {
      // 반입완료 상태가 아니면
      return {
        ...modelGardenDetail,
        type: '',
        provider: '',
        providerId: '',
        param_size: '',
        license: '',
        tags: '',
        langauges: '',
      };
    } else {
      // 반입완료 상태가 아니면 모델 정보 반환
      return modelGardenDetail;
    }
  }, [modelGardenDetail]);

  // 삭제
  const { mutate: deleteModelGarden } = useDeleteModelGarden();

  // 삭제 버튼
  const handleDelete = () => {
    checkPermissionAndShowAlert(() => {
      // 공통 업데이트 처리
      const _updateHandler = () => {
        deleteModelGarden(
          { id: id ?? '' },
          {
            onSuccess: () => {
              showDeleteComplete({
                itemName: '모델 탐색이',
                onConfirm: () => {
                  navigate('/model/modelGarden', { replace: true });
                },
              });
            },
          }
        );
      };
      // 공통 처리
      openConfirm({
        title: '안내',
        message: '삭제하시겠어요?\n삭제가 완료되면 모델이 반입 전 상태로 돌아갑니다.',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          _updateHandler();
        },
      });
    });
  };

  ////////////////////// self-hosting //////////////////////
  // Action 버튼
  const handleActionButtonClick = () => {
    checkPermissionAndShowAlert(() => {
      switch (MODEL_GARDEN_BUTTON_STATUS[modelGardenDetail?.statusNm as keyof typeof MODEL_GARDEN_BUTTON_STATUS]?.action) {
        case 'REQ_IN':
          // 반입 요청
          handleReqIn();
          break;
        case 'RE_IMPORT':
          // 반입 재시도
          handleReImport();
          break;
        case 'REQ_VUL_APPROVAL':
          // 취약점 점검 요청
          handleDoipResultPopup();
          // handleReqVulApproval();
          break;
        default:
          break;
      }
    });
  };

  const handleReqIn = () => {
    checkPermissionAndShowAlert(() => {
      modelInPopup.setCustomStep(3);
    });
  };

  // 반입 재시도 핸들러
  const handleReImport = () => {
    openConfirm({
      title: '안내',
      message: `모델 반입 재시도를 하시겠어요?\n반입 진행현황이 '반입요청 완료'로 변경됩니다.`,
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        modelInPopup.setCustomStep(3);
      },
    });
  };
  const handleCreateComplete = () => {
    refetch();
  };

  // 취약점점검 결재요청 관련
  const uniqueKey = useMemo(() => `${user.userInfo.memberId}-${id}`, [user.userInfo.memberId, id]);
  const payReqWizardPopup = useLayerPopup();
  const [approvalInfo, setApprovalInfo] = useState<PayReqWizardProps['approvalInfo']>({
    memberId: '',
    approvalType: '', // 취약점 점검 요청
    approvalUniqueKey: uniqueKey,
    afterProcessParamString: '',
    approvalItemString: '',
    approvalSummary: '',
  });

  // PayReqWizard 닫기 핸들러
  const handlePayReqWizardClose = () => {
    payReqWizardPopup.onClose();
  };

  // 취약점점검 결재요청 완료 핸들러
  const handleReqVulApprovalComplete = () => {
    updateModelGarden(
      {
        id: id ?? '',
        statusNm: 'VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS', // 취약점점검 검토 결재 요청
      },
      {
        onSuccess: () => {
          refetch();
        },
      }
    );
  };

  // 취약점점검 결재요청 핸들러
  // const handleReqVulApproval = () => {
  //   openConfirm({
  //     title: '안내',
  //     message: '최종 반입 결재요청 하시겠어요?\n결재 승인 이후, 모델 반입과 등록이 완료됩니다.',
  //     confirmText: '예',
  //     cancelText: '아니요',
  //     onConfirm: () => {
  //       // 결재 시작
  //       payReqWizardPopup.onOpen();
  //       setApprovalInfo({
  //         memberId: user.userInfo.memberId,
  //         approvalType: '03', // 취약점 점검 요청
  //         approvalUniqueKey: uniqueKey, // 요청 식별자
  //         approvalItemString: `${modelGardenDetail?.name}`, // 요청하는 대상/작업 이름 (알람 표시 목적)
  //         afterProcessParamString: JSON.stringify({
  //           id: id ?? '',
  //         }),
  //         preApprovalMessage: ``,
  //       });
  //     },
  //   });
  // };
  ////////////////////// serverless //////////////////////
  // 수정 팝업
  const layerPopupEdit = useLayerPopup();

  // 수정 버튼
  // const handleUpdateButtonClick = () => {
  //   checkPermissionAndShowAlert(() => {
  //     layerPopupEdit.onOpen();
  //   });
  // };

  // 수정 성공시 콜백
  const handleUpdateSuccess = () => {
    refetch();
  };

  ////////////////////// 결과 확인 팝업 //////////////////////
  const isShowDoipResultPopup = useMemo(() => {
    return (
      isSelftHosting && modelGardenDetail?.statusNm !== MODEL_GARDEN_STATUS_TYPE.PENDING && modelGardenDetail?.statusNm !== MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED_UNREGISTERED
    );
  }, [isSelftHosting, modelGardenDetail?.statusNm]);

  const handleDoipResultPopup = () => {
    openModal(
      {
        type: 'large',
        title: '모델 검사 결과',
        useCustomFooter: true,
        body: <MdGdnDoipResultPopup id={id ?? ''} setApprovalInfo={setApprovalInfo} openReqVulApprovalPopup={payReqWizardPopup.onOpen} />,
      },
      {
        modalId: MODAL_ID.MODEL_GARDEN_DOIP_RESULT_POPUP,
      }
    );
  };

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title={`${isSelftHosting ? 'self-hosting' : 'serverless'} 모델 조회`}
          description=''
          actions={
            isShowDoipResultPopup ? (
              <Button className='btn-tertiary-outline line-only-blue' onClick={handleDoipResultPopup}>
                모델 검사 결과
              </Button>
            ) : (
              <></>
            )
          }
        />

        {modelGardenDetail && (
          <>
            {/* 페이지 바디 */}
            <UIPageBody>
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
                      {isSelftHosting ? <MdGdnDetailSelfHosting modelInfo={modelGardenDetail} /> : <MdGdnDetailServerless modelInfo={modelGardenDetail} />}
                    </table>
                  </div>
                </div>
              </UIArticle>

              {isSelftHosting ? (
                <ManagerInfoBox
                  type='memberId'
                  rowInfo={[
                    { personLabel: '반입요청자', dateLabel: '반입요청일시' },
                    { personLabel: '최종 반입요청자', dateLabel: '최종 반입요청일시' },
                  ]}
                  people={[
                    { userId: modelInfo?.doipMn ?? '', datetime: modelInfo?.doipAt ?? '' },
                    { userId: modelInfo?.chkMn ?? '', datetime: modelInfo?.chkAt ?? '' },
                  ]}
                />
              ) : (
                <ManagerInfoBox
                  type='memberId'
                  people={[
                    { userId: modelInfo?.created_by ?? '', datetime: modelInfo?.created_at ?? '' },
                    { userId: modelInfo?.updated_by ?? '', datetime: modelInfo?.updated_at ?? '' },
                  ]}
                />
              )}
            </UIPageBody>
            {/* 페이지 footer */}
            <UIPageFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='center'>
                  <Button
                    auth={isSelftHosting ? AUTH_KEY.MODEL.SELF_HOSTING_MODEL_DELETE : AUTH_KEY.MODEL.SERVERLESS_MODEL_DELETE}
                    className='btn-primary-gray'
                    onClick={handleDelete}
                    disabled={
                      !(
                        modelGardenDetail?.statusNm === MODEL_GARDEN_STATUS_TYPE.PENDING || // 반입전
                        modelGardenDetail?.statusNm === MODEL_GARDEN_STATUS_TYPE.IMPORT_FAILED || // 반입실패
                        modelGardenDetail?.statusNm === MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED || // 반입완료
                        modelGardenDetail?.statusNm === MODEL_GARDEN_STATUS_TYPE.VULNERABILITY_CHECK_APPROVAL_REJECTED || // 취약점점검 결재반려
                        modelGardenDetail?.statusNm === MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED_UNREGISTERED // 반입완료 + 카탈로그 삭제 상태
                      )
                    }
                  >
                    삭제
                  </Button>

                  {isSelftHosting ? (
                    <>
                      <Button
                        className='btn-primary-blue'
                        // 반입 완료를 제외하고 비활성화
                        disabled={MODEL_GARDEN_BUTTON_STATUS[modelInfo?.statusNm as keyof typeof MODEL_GARDEN_BUTTON_STATUS]?.disabled}
                        onClick={handleActionButtonClick}
                      >
                        {MODEL_GARDEN_BUTTON_STATUS[modelInfo?.statusNm as keyof typeof MODEL_GARDEN_BUTTON_STATUS]?.label}
                      </Button>
                    </>
                  ) : (
                    // <Button auth={AUTH_KEY.MODEL.SERVERLESS_MODEL_DETAIL_UPDATE} className='btn-primary-blue' onClick={handleUpdateButtonClick}>
                    //   수정
                    // </Button>
                    <></>
                  )}
                </UIUnitGroup>
              </UIArticle>
            </UIPageFooter>
          </>
        )}
      </section>
      {modelInPopup.currentStep > 0 && <ModelGardnIn {...modelInPopup} selectedData={modelInfo} onComplete={handleCreateComplete} />}

      {!isSelftHosting && modelInfo && layerPopupEdit.currentStep === 1 && (
        <MdGdnEditPopup {...layerPopupEdit} onSubmit={() => checkPermissionAndShowAlert(() => handleUpdateSuccess())} modelGardenDetail={modelInfo} />
      )}

      {/* 취약점점검 결재요청 PayReqWizard */}
      {payReqWizardPopup.currentStep === 1 && (
        <PayReqWizard isOpen={payReqWizardPopup.currentStep === 1} onClose={handlePayReqWizardClose} onComplete={handleReqVulApprovalComplete} approvalInfo={approvalInfo} />
      )}
    </>
  );
};
