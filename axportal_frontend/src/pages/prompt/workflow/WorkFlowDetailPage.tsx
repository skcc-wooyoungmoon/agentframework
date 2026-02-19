import { useEffect, useMemo, useRef, useState } from 'react';

import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIVersionCard } from '@/components/UI/molecules/UIVersionCard';
import { AUTH_KEY } from '@/constants/auth';
import { useLayerPopup } from '@/hooks/common/layer';
import { WorkFlowEditPopupPage } from '@/pages/prompt';
import {
  useDeleteWorkFlowByWorkFlowId,
  useGetWorkFlowLatestVerById,
  useGetWorkFlowVerById,
  useGetWorkFlowVerListById,
  useSetWorkFlowPublic,
} from '@/services/prompt/workFlow/workFlow.services';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';

export const WorkFlowDetailPage = () => {
  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();
  const { workFlowId } = useParams<{ workFlowId: string }>();
  const [selectedVersionId, setSelectedVersionId] = useState<string | null>(null);
  const [editPopupOpenSeq, setEditPopupOpenSeq] = useState(0);
  const prevworkFlowIdRef = useRef<string | null>(null);

  // WorfFlow ID를 통해 상세 데이터 호출
  const { data: workFlowVerList, refetch: refetchWorkFlowVerList } = useGetWorkFlowVerListById({ workFlowId: workFlowId || '' }, { enabled: !!workFlowId });
  const { data: workFlowLatestVer, refetch: refetchWorkFlowLatestVer } = useGetWorkFlowLatestVerById({ workFlowId: workFlowId || '' }, { enabled: !!workFlowId });

  // 선택된 버전의 상세 정보 조회
  const selectedVersionNo = selectedVersionId ? parseInt(selectedVersionId.replace('version-', '')) : null;

  // 선택된 버전의 데이터 가져오기
  const { data: selectedWorkFlowVer, refetch: refetchSelectedWorkFlowVer } = useGetWorkFlowVerById(
    { workFlowId: workFlowId || '', versionNo: selectedVersionNo || 0 },
    {
      enabled: false, // 수동으로 refetch 호출
    }
  );

  // 현재 표시할 워크플로우 정보 결정
  const workFlow = useMemo(() => {
    // 최신 버전을 선택한 경우
    if (selectedVersionNo === workFlowLatestVer?.versionNo) {
      return workFlowLatestVer;
    }
    // 다른 버전을 선택한 경우
    return selectedWorkFlowVer || workFlowLatestVer;
  }, [selectedVersionNo, selectedWorkFlowVer, workFlowLatestVer]);

  // 버전 클릭 핸들러
  const handleVersionClick = (versionNo: number) => {
    const versionId = `version-${versionNo}`;
    setSelectedVersionId(versionId);
  };

  // 버전 히스토리 데이터
  const versions = useMemo(() => {
    const list = workFlowVerList?.versions ?? [];
    const latestVersion = workFlowLatestVer?.versionNo;
    const currentSelectedVersionNo = selectedVersionNo || latestVersion;

    return list.map(item => {
      const isLatest = item.versionNo === latestVersion;
      const isSelected = item.versionNo === currentSelectedVersionNo;

      // 태그 배열 생성
      const tags: Array<{ label: string; intent: 'blue' | 'gray' | 'violet' }> = [];
      if (isLatest) {
        tags.push({ label: 'Latest', intent: 'gray' });
      }

      return {
        id: `version-${item.versionNo}`,
        version: `Ver.${item.versionNo}`,
        date: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
        tags: tags.length > 0 ? tags : undefined,
        isActive: isSelected,
      };
    });
  }, [workFlowVerList, workFlowLatestVer, selectedVersionNo]);

  const selectedVersion = useMemo(() => {
    const versionNo = selectedVersionNo || workFlowLatestVer?.versionNo;
    const isLatest = versionNo === workFlowLatestVer?.versionNo;
    return {
      version: versionNo ? `Ver.${versionNo}` : '-',
      isLatest,
    };
  }, [selectedVersionNo, workFlowLatestVer]);

  useEffect(() => {
    if (!selectedVersionId && workFlowLatestVer?.versionNo) {
      setSelectedVersionId(`version-${workFlowLatestVer.versionNo}`);
    }
  }, [workFlowLatestVer, selectedVersionId]);

  useEffect(() => {
    if (!selectedVersionId && versions.length) {
      // tags에 'Latest'가 있는 버전 찾기
      const latest = versions.find(v => v.tags?.some(tag => tag.label === 'Latest'));
      if (latest && latest.id) {
        setSelectedVersionId(latest.id);
      } else if (versions[0] && versions[0].id) {
        setSelectedVersionId(versions[0].id);
      }
    }
  }, [versions, selectedVersionId]);

  useEffect(() => {
    if (prevworkFlowIdRef.current && prevworkFlowIdRef.current !== workFlowId) {
      setSelectedVersionId(null);
    }
    prevworkFlowIdRef.current = workFlowId ?? null;
  }, [workFlowId]);

  // 버전 변경 시 데이터 가져오기
  useEffect(() => {
    if (workFlowId && selectedVersionNo) {
      // console.log('버전 데이터 요청:', { workFlowId, selectedVersionNo });
      refetchSelectedWorkFlowVer();
    }
  }, [workFlowId, selectedVersionNo, refetchSelectedWorkFlowVer]);

  /**
   *  워크플로우 삭제
   */
  const { mutate: deleteWorkFlow } = useDeleteWorkFlowByWorkFlowId({
    onSuccess: () => {
      // 삭제 성공 시에만 목록 페이지로 이동하면서 새로고침 신호 전달
      navigate('/prompt/workflow', { state: { shouldRefresh: true } });
    },
    onError: () => {
      // 삭제 실패 시에는 새로고침 없이 목록 페이지로 이동
      navigate('/prompt/workflow');
    },
  });

  /**
   * 워크플로우 공개설정
   */
  const { openAlert } = useModal();
  const { mutate: setWorkFlowPublic } = useSetWorkFlowPublic({
    onSuccess: async () => {
      // 공개설정 성공 시 현재 페이지 데이터 새로고침
      await Promise.all([refetchWorkFlowVerList(), refetchWorkFlowLatestVer()]);
      if (selectedVersionNo) {
        await refetchSelectedWorkFlowVer();
      }

      openAlert({
        title: '완료',
        message: '해당 에셋 전체 공유 처리가 완료되었습니다.\nPublic 프로젝트에서 조회 및 사용하실 수 있습니다.',
        confirmText: '확인',
      });
    },
  });

  /**
   * 워크플로우 삭제 핸들러
   */
  const { openConfirm } = useModal();
  const handleDelete = () => {
    if (!workFlowId) {
      return;
    }

    // 공개범위 체크: private 사용자가 public 워크플로우를 삭제하려는지 확인
    const isUserCurrentGroupPrivate = Number(user.activeProject.prjSeq) !== -999;
    const isWorkflowPublic = Number(workFlow?.projectSeq) === -999;

    if (isUserCurrentGroupPrivate && isWorkflowPublic) {
      openAlert({
        title: '안내',
        message: '워크플로우 삭제 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteWorkFlow({ workFlowId: workFlowId });

        openAlert({
          title: '완료',
          message: '워크플로우가 삭제되었습니다.',
        });
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  /**
   * 워크플로우 공개설정 핸들러
   */
  const handleSetPublic = () => {
    if (!workFlowId) {
      return;
    }

    openConfirm({
      title: '안내',
      message: '이 항목을 전체공유 하시겠어요?\n공유 후에는 다시 되돌릴 수 없습니다.',
      onConfirm: () => {
        setWorkFlowPublic({ workFlowId: workFlowId });
      },
    });
  };

  /**
   * 워크플로우 수정 성공 시 데이터 refetch
   */
  const handleEditSuccess = async () => {
    // 1. 먼저 기본 정보와 버전 정보를 refetch
    const [, latestVerResult] = await Promise.all([refetchWorkFlowVerList(), refetchWorkFlowLatestVer()]);

    // 2. 최신 버전 ID로 직접 업데이트
    const latestVersion = latestVerResult.data?.versionNo;
    if (latestVersion) {
      const latestVersionId = `version-${latestVersion}`;
      setSelectedVersionId(latestVersionId);

      // 3. 새 버전의 상세 데이터도 refetch (약간의 지연 후)
      // React Query가 selectedVersionNo 변경을 감지하여 자동으로 refetch함
      setTimeout(async () => {
        await Promise.all([refetchWorkFlowVerList(), refetchWorkFlowLatestVer()]);
      }, 100);
    }
  };

  /**
   * 워크플로우 수정 팝업 호출
   */
  const { user } = useUser();
  const handleWorkFlowEditPopup = () => {
    // console.log('@@@ workFlow : ', workFlow);

    if (user.functionAuthList.includes('A040402')) {
      // '수정' 버튼을 누를 때마다 팝업 컴포넌트를 새로 마운트하여 내부 상태를 초기화
      setEditPopupOpenSeq(prev => prev + 1);
      layerPopupOne.onOpen();
    } else {
      openAlert({
        title: '안내',
        message: '워크플로우 수정 권한이 없습니다.',
        confirmText: '확인',
      });
    }
  };

  // 태그 목록 처리
  const tagList = useMemo(() => {
    const fromWorkFlow = workFlow?.tags ?? [];
    const fromRaw = workFlow?.tagsRaw
      ? workFlow.tagsRaw
          .split(',')
          .map(s => s.trim())
          .filter(Boolean)
      : [];
    return Array.from(new Set([...fromWorkFlow, ...fromRaw]));
  }, [workFlow]);

  if (!workFlowId) {
    return null;
  }

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='워크플로우 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <div className='grid-layout'>
            {/* 왼쪽 : Content */}
            <div className='grid-article'>
              {/* 기본정보 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    기본정보
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
                              {workFlow?.workflowName ?? '-'}
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              버전
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {selectedVersion.isLatest && selectedVersionNo && <UITextLabel intent='gray'>Latest Ver.{selectedVersionNo}</UITextLabel>}
                              {!selectedVersion.isLatest && selectedVersion.version !== '-' && <UITextLabel intent='gray'>{selectedVersion.version}</UITextLabel>}
                              {selectedVersion.version === '-' && '-'}
                            </UITypography>
                          </td>
                        </tr>
                        <tr>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              태그
                            </UITypography>
                          </th>
                          <td colSpan={3}>
                            <UIUnitGroup gap={8} direction='row' align='start'>
                              {tagList.length > 0 &&
                                tagList.map((tag, index) => (
                                  <UITextLabel key={index} intent='tag'>
                                    {tag}
                                  </UITextLabel>
                                ))}
                            </UIUnitGroup>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              {/* 워크플로우 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    워크플로우
                  </UITypography>
                </div>
                <div className='article-body'>
                  <UIUnitGroup gap={8} direction='row' align='start'>
                    <div className='flex-1'>
                      <UITextArea2 value={workFlow?.xmlText || '워크플로우 XML이 없습니다.'} placeholder='' style={{ height: '394px' }} onChange={() => {}} readOnly />
                    </div>
                  </UIUnitGroup>
                </div>
              </UIArticle>

              {/* 담당자 정보 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    담당자 정보
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
                              생성자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {workFlow?.createdBy || '-'}
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              생성일시
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {workFlow?.createdAt ? dateUtils.formatDate(workFlow.createdAt, 'datetime') : '-'}
                            </UITypography>
                          </td>
                        </tr>
                        <tr>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              최종 수정자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {workFlow?.updatedBy || ''}
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              최종 수정일시
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {workFlow?.updatedAt ? dateUtils.formatDate(workFlow.updatedAt, 'datetime') : ''}
                            </UITypography>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              {/* 프로젝트 정보 */}
              <UIArticle>
                <div className='article-header'>
                  <UIUnitGroup direction='row' align='space-between' gap={0}>
                    <UITypography variant='title-4' className='secondary-neutral-900'>
                      프로젝트 정보
                    </UITypography>
                    <UIButton2 className='btn-option-outlined' onClick={handleSetPublic} disabled={Number(workFlow?.projectSeq) === -999}>
                      공개설정
                    </UIButton2>
                  </UIUnitGroup>
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
                              공개범위
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {(Number(workFlow?.projectSeq) === -999 ? '전체공유' : '내부공유') +
                                ' | ' +
                                user.projectList.find(prj => Number(prj.prjSeq) === Number(workFlow?.projectSeq))?.prjNm}
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              권한 수정자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {Number(workFlow?.projectSeq) !== -999 ? '' : workFlow?.projectScope || ''}
                            </UITypography>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              <div className='article-buton-group'>
                <Button auth={AUTH_KEY.PROMPT.WORKFLOW_DELETE} className='btn-primary-gray w-[80px]' onClick={handleDelete}>
                  삭제
                </Button>
                <Button auth={AUTH_KEY.PROMPT.WORKFLOW_UPDATE} className='btn-primary-blue w-[80px]' onClick={handleWorkFlowEditPopup}>
                  수정
                </Button>
              </div>
            </div>

            {/* 오른쪽 : 버전 히스토리 */}
            <div className='grid-right-sticky'>
              <UIVersionCard
                versions={versions}
                onVersionClick={version => {
                  if (version.id) {
                    const versionNo = parseInt(version.id.replace('version-', ''));
                    handleVersionClick(versionNo);
                  }
                }}
              />
            </div>
          </div>
        </UIPageBody>

        {/* 페이지 footer */}
      </section>

      {/* 워크플로우 수정 팝업 */}
      <WorkFlowEditPopupPage
        key={`${workFlowId}-${editPopupOpenSeq}`}
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        workFlowId={workFlowId}
        onEditSuccess={handleEditSuccess}
        initialName={workFlow?.workflowName || ''}
        initialXmlText={workFlow?.xmlText || ''}
        initialTags={tagList}
      />
    </>
  );
};
