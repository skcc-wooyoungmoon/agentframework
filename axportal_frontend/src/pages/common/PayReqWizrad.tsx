import React, { useState } from 'react';

import { UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { useSubmitPaymentApproval } from '@/services/common/payReq.service.ts';
import type { PaymentApprovalRequest } from '@/services/common/types.ts';
import { useDeleteProj } from '@/services/home';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';

export interface PayReqWizardProps {
  isOpen: boolean;
  onClose: () => void;
  onComplete?: () => void; // 결재 완료 콜백
  approvalInfo: {
    memberId: string;
    approvalType: string; // 업무코드
    approvalUniqueKey?: string; // 요청식별자 (중복방지 등 목적으로 각 업무에서 활용)
    approvalParamKey?: number; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalParamValue?: string; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalItemString: string; // 요청하는 대상/작업 이름 (알람 표시 목적)
    afterProcessParamString: string; // 후처리 변수
    approvalSummary?: string; // 결재사유 메세지
    preApprovalMessage?: string; // 자동완성 메시지
    apprivalTableInfo?: { key: string; value: string }[][];
  };
}

export const PayReqWizard: React.FC<PayReqWizardProps> = ({ onClose, onComplete, approvalInfo }) => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  // textarea 타입
  const [textareaValue, setTextareaValue] = useState(approvalInfo.preApprovalMessage || '');
  // 요청 사유가 입력되었는지 확인 (공백 제거 후 체크)
  const isRequestButtonEnabled = textareaValue.trim().length > 0;
  const { openAlert, openConfirm } = useModal();
  const { user, updateUser } = useUser();

  const approvalTypeMap: Record<string, { typeNm: string; approvalTarget: { prjNm: string; roleNm: string; prjSeq: number; roleSeq: number }[] }> = {
    '01': {
      typeNm: '프로젝트 생성',
      approvalTarget: [
        {
          prjNm: 'Public',
          roleNm: '포탈 관리자',
          prjSeq: -999,
          roleSeq: -199,
        },
      ],
    },
    '02': {
      typeNm: '프로젝트 참여',
      approvalTarget: [
        {
          prjNm: approvalInfo.approvalParamValue || '',
          roleNm: '프로젝트 관리자',
          prjSeq: approvalInfo.approvalParamKey || -999,
          roleSeq: -299,
        },
      ],
    },
    '03': {
      typeNm: '모델 점검 결과 승인 요청', // 2차 걸재용
      approvalTarget: [
        {
          prjNm: 'Public',
          roleNm: 'Tech운영 관리자',
          prjSeq: -999,
          roleSeq: -196,
        },
        {
          prjNm: 'Public',
          roleNm: '정보보호 관리자',
          prjSeq: -999,
          roleSeq: -197,
        },
      ],
    },
    '09': {
      typeNm: '모델 점검 결과 승인 요청', // 1차 결재용
      approvalTarget: [
        {
          prjNm: 'Public',
          roleNm: 'Tech운영 관리자',
          prjSeq: -999,
          roleSeq: -196,
        },
      ],
    },
    '04': {
      typeNm: '모델 배포',
      approvalTarget: [
        {
          prjNm: 'Public',
          roleNm: '포탈 관리자',
          prjSeq: -999,
          roleSeq: -199,
        },
      ],
    },
    '05': {
      typeNm: '에이전트 배포',
      approvalTarget: [
        {
          prjNm: 'Public',
          roleNm: '포탈 관리자',
          prjSeq: -999,
          roleSeq: -199,
        },
      ],
    },
    '06': {
      typeNm: '외부시스템 API Key 발급',
      approvalTarget: [
        {
          prjNm: 'Public',
          roleNm: '포탈 관리자',
          prjSeq: -999,
          roleSeq: -199,
        },
      ],
    },
    // '07': {
    //   typeNm: 'NAS Storage 상향 신청',
    //   approvalTarget: [
    //     {
    //       prjNm: 'Public',
    //       roleNm: '포탈 관리자',
    //       prjSeq: -999,
    //       roleSeq: -199,
    //     },
    //   ],
    // },
    '08': {
      typeNm: 'IDE 생성',
      approvalTarget: [
        {
          prjNm: 'Public',
          roleNm: '포탈 관리자',
          prjSeq: -999,
          roleSeq: -199,
        },
      ],
    },
  };

  // 프로젝트 생성 롤백을 위한 prjSeq 추출
  let prjSeqForDelete = 0;
  if (approvalInfo.approvalType === '01' && approvalInfo.afterProcessParamString) {
    try {
      const jsonParam = JSON.parse(approvalInfo.afterProcessParamString);
      prjSeqForDelete = jsonParam.prjSeq || 0;
    } catch {
      prjSeqForDelete = 0;
    }
  }

  // 프로젝트 삭제 API 훅 사용
  const { mutate: deleteProject } = useDeleteProj();
  const handleClose = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        // 프로젝트 생성 롤백
        if (approvalInfo.approvalType == '01' && prjSeqForDelete > 0) {
          deleteProject({ prjSeq: prjSeqForDelete });
        }

        setIsPopupOpen(false);
        onClose();
      },
    });
  };

  const handleRequestData = () => {
    approvalInfo.approvalSummary = textareaValue + '\n'; // 결재사유 추가

    // 프로젝트명이 없으면 제일 먼저 추가 (IDE 생성요청은 제외)
    if (!approvalInfo.apprivalTableInfo?.flat().some(row => row.key === '프로젝트명') && approvalInfo.approvalType !== '08') {
      approvalInfo.approvalSummary += `\n  • 프로젝트명 : ${user.activeProject.prjNm}`;
    }

    // 결재사유에 요청정보 추가
    approvalInfo.apprivalTableInfo?.flat().forEach(row => {
      approvalInfo.approvalSummary += `\n  • ${row.key} : ${row.value.length > 200 ? row.value.substring(0, 200) + '...' : row.value}`;
    });

    const requestData: PaymentApprovalRequest = {
      approvalInfo: approvalInfo,
      approvalTypeInfo: approvalTypeMap[approvalInfo.approvalType],
      displayInfo: {
        typeNm: approvalTypeMap[approvalInfo.approvalType].typeNm, // 업무구분
        jkwNm: user.userInfo.jkwNm, // 이름
        deptNm: user.userInfo.deptNm, // 부서
        prjNm: user.activeProject.prjNm, // 프로젝트명
        prjRoleNm: user.activeProject.prjRoleNm, // 역할
      },
    };
    // console.log('결재 요청 데이터:', requestData);
    // API 호출
    submitPaymentApproval(requestData);
  };

  // 결재 요청 API 훅
  const { mutate: submitPaymentApproval } = useSubmitPaymentApproval({
    onSuccess: async () => /* data */ {
      // console.log('결재 요청 성공:', data);

      // 성공 알림 표시
      openAlert({
        title: '완료',
        message: '간편결재 요청이 완료되었습니다.',
      });

      // 사용자 데이터를 갱신
      const updatedUser = await authServices.getMe();
      if (updatedUser) {
        updateUser(updatedUser);
      }

      // 완료 콜백 호출
      onComplete?.();

      // 화면 닫기
      setIsPopupOpen(false);
      onClose();
    },
    onError: /* error */ () => {
      // console.error('결재 요청 실패:', error);

      // 실패 알림 표시
      openAlert({
        title: '오류',
        message: '결재 요청 중 오류가 발생했습니다. 다시 시도해주세요.',
      });
    },
  });

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='간편결재 요청' position='left' />
            {/* 레이어 팝업 바디 */}
            {/* <UIPopupBody></UIPopupBody> */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={!isRequestButtonEnabled} onClick={handleRequestData}>
                    요청
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
          <UIPopupHeader title='간편결재 요청' description='요청 사유를 입력 후 결재를 요청해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-title-4-sb'>
                  요청 정보
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
                            업무 구분
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='text-blue-800 text-sb'>
                            {approvalTypeMap[approvalInfo.approvalType].typeNm}
                          </UITypography>
                        </td>
                      </tr>
                      {approvalInfo.apprivalTableInfo?.map((row, rowIndex) => (
                        <tr key={rowIndex}>
                          {row.map((cell, cellIndex) => (
                            <React.Fragment key={cellIndex}>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  {cell.key}
                                </UITypography>
                              </th>
                              <td>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  {cell.value}
                                </UITypography>
                              </td>
                            </React.Fragment>
                          ))}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-sb'>
                  요청자
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
                            이름
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {user.userInfo.jkwNm}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            부서
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {user.userInfo.deptNm}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            프로젝트명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {user.activeProject?.prjNm}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            역할명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {user.activeProject?.prjRoleNm}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            {/* 결재 대상 동적 렌더링 */}
            {approvalTypeMap[approvalInfo.approvalType].approvalTarget.map((target, index) => {
              const approvalTargets = approvalTypeMap[approvalInfo.approvalType].approvalTarget;
              const title = approvalTargets.length === 1 ? '결재자' : `${index + 1}차 결재자`;

              return (
                <UIArticle key={index}>
                  <div className='article-header'>
                    <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-title-4-sb'>
                      {title}
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
                                프로젝트명
                              </UITypography>
                            </th>
                            <td>
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {target.prjNm}
                              </UITypography>
                            </td>
                            <th>
                              <UITypography variant='body-2' className='secondary-neutral-900'>
                                역할명
                              </UITypography>
                            </th>
                            <td>
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {target.roleNm}
                              </UITypography>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </UIArticle>
              );
            })}

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography required={true} variant='title-4' className='secondary-neutral-800'>
                  요청 사유
                </UITypography>
                {/* readonly - 필요시 클래스 사용 */}
                <UITextArea2
                  value={textareaValue}
                  placeholder='결재 요청이 필요한 업무 항목과 사유를 함께 입력해주세요.
예시) ‘대출 상품 추천’ 프로젝트 참여 승인 요청 드립니다. 모델 개발 업무 수행 예정입니다.'
                  onChange={e => setTextareaValue(e.target.value)}
                  maxLength={500}
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <div className='box-fill'>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    결재 요청 시, 결재 대상인 역할을 보유한 모든 사용자에게 Swing 알림이 발송됩니다.
                  </UITypography>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
