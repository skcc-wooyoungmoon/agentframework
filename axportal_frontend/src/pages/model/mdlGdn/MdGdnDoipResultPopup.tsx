import { UILabel, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { UIModalContent } from '@/components/UI/molecules/modal/UIModalContent';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { MODAL_ID } from '@/constants/modal/modalId.constants';
import { MODEL_GARDEN_BUTTON_STATUS, MODEL_GARDEN_STATUS_TYPE, VULNERABILITY_CHECK_STATUS } from '@/constants/model/garden.constants';
import { type PayReqWizardProps } from '@/pages/common/PayReqWizrad';
import { useGetManagerInfoBulk } from '@/services/common/userInfo.service';
import { useGetVaccineCheckResult } from '@/services/model/garden/modelGarden.services';
import { useUser } from '@/stores/auth';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { useMemo } from 'react';

/**
 * @author SGO1032948
 * @description 모델 검사 결과 팝업
 *
 * MD_050102_P01
 */
export const MdGdnDoipResultPopup = ({
  id,
  setApprovalInfo,
  openReqVulApprovalPopup,
}: {
  id: string;
  setApprovalInfo?: (approvalInfo: PayReqWizardProps['approvalInfo']) => void;
  openReqVulApprovalPopup: () => void;
}) => {
  const { closeModal, openConfirm } = useModal();
  const { user } = useUser();

  const { data, isFetching } = useGetVaccineCheckResult(id);
  const { data: checkByInfo, isFetching: isFetchingCheckByInfo } = useGetManagerInfoBulk(
    {
      type: 'memberId',
      values: [data?.checkBy ?? ''],
    },
    {
      enabled: data?.checkBy != null && data?.checkBy !== '', // 유효한 userId가 있을 때만 API 호출
    }
  );

  const isLoading = useMemo(() => {
    return isFetching || isFetchingCheckByInfo;
  }, [isFetching, isFetchingCheckByInfo]);

  // Python dict 문자열을 JSON으로 파싱하는 헬퍼 함수
  const parsePythonDict = (str: string) => {
    try {
      // "Verification result:" 이후의 dict 부분 추출
      const dictStart = str.indexOf('{');
      if (dictStart < 0) return null;

      const dictStr = str.slice(dictStart);

      // Python dict을 JSON으로 변환
      const jsonStr = dictStr
        .replaceAll(/'/g, '"') // 작은따옴표 → 큰따옴표
        .replaceAll(/\bTrue\b/g, 'true') // True → true
        .replaceAll(/\bFalse\b/g, 'false') // False → false
        .replaceAll(/\bNone\b/g, 'null'); // None → null

      return JSON.parse(jsonStr);
    } catch (e) {
      return null;
    }
  };

  // 외부 백신검사 결과 파싱
  const fistChkDtl: any = useMemo(() => {
    if (!data?.fistChkDtl) return null;
    // JSON 형식 시도
    try {
      return JSON.parse(data.fistChkDtl);
    } catch {
      // Python dict 형식 시도
      return parsePythonDict(data.fistChkDtl);
    }
  }, [data?.fistChkDtl]);

  // 외부망 백신 요약 정보 계산
  const fistChkSummary = useMemo(() => {
    if (!fistChkDtl?.verifiers) return null;

    const verifiers = fistChkDtl?.verifiers;
    const verifierList = Object.values(verifiers) as any[];

    // 모든 verifier가 success인지 확인
    const allSuccess = verifierList.every((v: any) => v.success === true);

    // 검사한 파일 수 (sophos + v3)
    const scannedCount = (verifiers.sophos?.scanned_count || 0) + (verifiers.v3?.scanned_count || 0);

    // 감염 파일 수 (모든 verifier의 합계)
    const infectedCount = verifierList.reduce((sum: number, v: any) => sum + (v.infected_count || 0), 0);

    return {
      success: allSuccess,
      scannedCount,
      infectedCount,
    };
  }, [fistChkDtl]);

  // 내부망 백신검사 결과 파싱
  const secndChkDtl: any = useMemo(() => {
    if (!data?.secndChkDtl) return null;
    try {
      return JSON.parse(data.secndChkDtl);
    } catch (e) {
      return data.secndChkDtl;
    }
  }, [data?.secndChkDtl]);

  // 취약점검 결과 파싱
  const vanbBrSmry: any = useMemo(() => {
    if (!data?.vanbBrSmry) return null;
    try {
      return JSON.parse(data.vanbBrSmry);
    } catch (e) {
      return data.vanbBrSmry;
    }
  }, [data?.vanbBrSmry]);

  const handleClosePopup = () => {
    closeModal(MODAL_ID.MODEL_GARDEN_DOIP_RESULT_POPUP);
  };

  // 취약점점검 결재요청 핸들러
  const handleReqVulApproval = () => {
    const request =
      vanbBrSmry?.total_vulnerabilities > 0
        ? // vanbBrSmry?.total_vulnerabilities > 0
          {
            memberId: user.userInfo.memberId,
            approvalType: '03', // 취약점 점검 요청
            approvalUniqueKey: `${id}-${data?.modelName}`, // 요청 식별자
            approvalItemString: `${data?.modelName}`, // 요청하는 대상/작업 이름 (알람 표시 목적)
            afterProcessParamString: JSON.stringify({
              id: id ?? '',
            }),
            apprivalTableInfo: [
              [
                { key: '모델명', value: data?.modelName ?? '' },
                { key: '라이센스', value: data?.license ?? '미입력' },
              ],
              [{ key: '백신 검사 결과', value: secndChkDtl?.message }],
              [{ key: '취약점점검 결과', value: JSON.stringify(vanbBrSmry) }],
            ],
            //             preApprovalMessage: `- 모델명: ${data?.modelName}
            //             - 라이센스: ${data?.license ?? '미입력'}
            // - 취약점검 결과: ${JSON.stringify(vanbBrSmry)}
            //                   `.slice(0, 500),
          }
        : {
            memberId: user.userInfo.memberId,
            approvalType: '09', // 취약점 점검 요청
            approvalUniqueKey: `${id}-${data?.modelName}`, // 요청 식별자
            approvalItemString: `${data?.modelName}`, // 요청하는 대상/작업 이름 (알람 표시 목적)
            afterProcessParamString: JSON.stringify({
              id: id ?? '',
            }),
            apprivalTableInfo: [
              [
                { key: '모델명', value: data?.modelName ?? '' },
                { key: '라이센스', value: data?.license ?? '미입력' },
              ],
            ],
            //             preApprovalMessage: `- 모델명: ${data?.modelName}
            // - 라이센스: ${data?.license ?? '미입력'}
            //                   `.slice(0, 500),
          };
    openConfirm({
      title: '안내',
      message: '최종 반입 결재요청 하시겠어요?\n결재 승인 이후, 모델 반입과 등록이 완료됩니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        // 결재 시작
        handleClosePopup();
        openReqVulApprovalPopup();
        setApprovalInfo?.(request);
      },
    });
  };

  return (
    <>
      {!isLoading && (
        <>
          <section className='section-modal'>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  외부망 백신점검 결과 요약
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            백신 검사 결과
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {fistChkDtl && (
                              <UILabel variant='badge' intent={fistChkSummary?.success ? 'complete' : 'error'}>
                                {fistChkSummary?.success ? '적합' : '부적합'}
                              </UILabel>
                            )}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            검사한 파일 수
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {fistChkSummary?.scannedCount}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            감염 파일 수
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {fistChkSummary?.infectedCount}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  내부망 백신점검 결과 요약
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            백신 검사 결과
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {secndChkDtl && (
                              <UILabel variant='badge' intent={secndChkDtl?.success ? 'complete' : 'error'}>
                                {secndChkDtl?.success ? '적합' : '취약'}
                              </UILabel>
                            )}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            백신 검사 내용
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {secndChkDtl?.message}
                          </UITypography>
                        </td>
                        {/* <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        검사한 파일 수
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        25
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        감염 파일 수
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        0
                      </UITypography>
                    </td> */}
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  취약점검 결과 요약
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            취약점검 결과
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vanbBrSmry && (
                              <UILabel variant='badge' intent={vanbBrSmry?.total_vulnerabilities > 0 ? 'error' : 'complete'}>
                                {vanbBrSmry?.total_vulnerabilities > 0 ? '취약' : '적합'}
                              </UILabel>
                            )}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            발견된 취약점 수
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vanbBrSmry?.total_vulnerabilities}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            취약점 심각도 별 파일 수
                          </UITypography>
                        </th>
                        <td>
                          <pre className='text-sm font-normal text-secondary-neutral-600 overflow-auto text-[#576072]'>
                            {JSON.stringify(vanbBrSmry?.vulnerabilities_by_severity)}
                          </pre>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            취약점 내용
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {JSON.stringify(vanbBrSmry?.vulnerabilities)}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  모델 점검 결과 상세
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '100%' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            백신 검사 결과
                          </UITypography>
                        </th>
                        <td>
                          <pre className='text-sm font-normal text-secondary-neutral-600 whitespace-pre-wrap text-[#576072]'>
                            {fistChkDtl ? JSON.stringify(fistChkDtl, null, 2) : ''}
                          </pre>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            취약점검 결과
                          </UITypography>
                        </th>
                        <td>
                          <pre className='text-sm font-normal text-secondary-neutral-600 overflow-x-auto whitespace-pre-wrap text-[#576072]'>
                            {vanbBrSmry ? JSON.stringify(vanbBrSmry, null, 2) : ''}
                          </pre>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  최종 반입 결재 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                      <col style={{ width: '152px' }} />
                      <col style={{ width: '350px' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            결재 결과
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {Object.keys(VULNERABILITY_CHECK_STATUS).includes(data?.checkStatus as keyof typeof VULNERABILITY_CHECK_STATUS) && (
                              <UILabel variant='badge' intent={VULNERABILITY_CHECK_STATUS[data?.checkStatus as keyof typeof VULNERABILITY_CHECK_STATUS]?.value as UILabelIntent}>
                                {VULNERABILITY_CHECK_STATUS[data?.checkStatus as keyof typeof VULNERABILITY_CHECK_STATUS]?.label}
                              </UILabel>
                            )}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            결재 요청자
                          </UITypography>
                        </th>
                        <td>
                          {Object.keys(VULNERABILITY_CHECK_STATUS).includes(data?.checkStatus as keyof typeof VULNERABILITY_CHECK_STATUS) && (
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {checkByInfo?.[0] ? `${checkByInfo?.[0]?.jkwNm} | ${checkByInfo?.[0]?.deptNm}` : ''}
                            </UITypography>
                          )}
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            결재 요청일시
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {dateUtils.formatDate(data?.checkAt, 'datetime')}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIModalContent.Footer
              type={'modal-large'}
              positiveButton={{
                disabled: MODEL_GARDEN_BUTTON_STATUS[data?.checkStatus as keyof typeof MODEL_GARDEN_BUTTON_STATUS]?.disabled,
                text: MODEL_GARDEN_BUTTON_STATUS[data?.checkStatus as keyof typeof MODEL_GARDEN_BUTTON_STATUS]?.label,
                onClick: () => {
                  handleReqVulApproval();
                },
              }}
              negativeButton={{
                text: '삭제',
                auth: AUTH_KEY.MODEL.SELF_HOSTING_MODEL_DELETE,
                disabled: !(
                  data?.checkStatus === MODEL_GARDEN_STATUS_TYPE.PENDING || // 반입전
                  data?.checkStatus === MODEL_GARDEN_STATUS_TYPE.IMPORT_FAILED || // 반입실패
                  data?.checkStatus === MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED || // 반입완료
                  data?.checkStatus === MODEL_GARDEN_STATUS_TYPE.VULNERABILITY_CHECK_APPROVAL_REJECTED || // 취약점점검 결재반려
                  data?.checkStatus === MODEL_GARDEN_STATUS_TYPE.IMPORT_COMPLETED_UNREGISTERED // 반입완료 + 카탈로그 삭제 상태
                ),
                onClick: () => {
                  handleClosePopup();
                },
              }}
            />
          </section>
        </>
      )}
    </>
  );
};
