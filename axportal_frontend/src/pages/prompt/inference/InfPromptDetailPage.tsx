import React, { useEffect, useMemo, useRef, useState } from 'react';

import { useLocation, useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UIDataCnt } from '@/components/UI/atoms/UIDataCnt';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIToggle } from '@/components/UI/atoms/UIToggle';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { UIArticle } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UITextArea2 } from '@/components/UI/molecules/input';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIAccordion } from '@/components/UI/molecules/UIAccordion';
import { UIFormField } from '@/components/UI/molecules/UIFormField';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules/UIUnitGroup';
import { UIVersionCard, type UIVersionCardItem } from '@/components/UI/molecules/UIVersionCard';
import { AUTH_KEY } from '@/constants/auth';
import { useLayerPopup } from '@/hooks/common/layer';
import { InfPromptEditPopupPage } from '@/pages/prompt/inference/InfPromptEditPopupPage';
import { useGetAgentBuilderById } from '@/services/agent/builder/agentBuilder.services.ts';
import {
  useDeleteInfPromptByPromptId,
  useGetInfPromptById,
  useGetInfPromptLatestVerById,
  useGetInfPromptLineageRelations,
  useGetInfPromptMsgsById,
  useGetInfPromptTagById,
  useGetInfPromptVarsById,
  useGetInfPromptVerListById,
  useReleaseInfPrompt,
} from '@/services/prompt/inference/inferencePrompts.services';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';

import type { ColDef } from 'ag-grid-community';

type InfPromptVarUI = {
  id: string;
  label: string;
  variableEnabled: boolean;
  regexValue?: string;
  tokenLimitEnabled: boolean;
  tokenLimitValue?: string;
  showAddButton?: boolean;
};

const getPTypeLabel = (pType?: number) => {
  if (pType === 1) return '채팅';
  return '기타';
};

/**
 * 프롬프트 > 추론 프롬프트 > 추론 프롬프트 상세
 */
export const InfPromptDetailPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const layerPopupOne = useLayerPopup();
  const { openConfirm } = useModal();
  const { promptUuid } = useParams<{ promptUuid: string }>();
  const [selectedVersionId, setSelectedVersionId] = useState<string | null>(null);
  const [editPopupOpenSeq, setEditPopupOpenSeq] = useState(0);
  const currentVersionUuid = selectedVersionId ?? '';
  const prevPromptUuidRef = useRef<string | null>(null);
  const [systemPrompt, setSystemPrompt] = useState('');
  const [userPrompt, setUserPrompt] = useState('');

  // 변수 속성 설정 데이터
  const [variableSettingsItems, setVariableSettingsItems] = useState<InfPromptVarUI[]>([]);

  // Prompt UUID를 통해 상세 데이터 호출
  const { data: infPrompt, refetch: refetchInfPrompt } = useGetInfPromptById({ promptUuid: promptUuid || '' }, { enabled: !!promptUuid, refetchOnMount: 'always' });
  const { data: infPromptVerList, refetch: refetchInfPromptVerList } = useGetInfPromptVerListById(
    { promptUuid: promptUuid || '' },
    { enabled: !!promptUuid, refetchOnMount: 'always' }
  );
  const { data: infPromptLatestVer, refetch: refetchInfPromptLatestVer } = useGetInfPromptLatestVerById(
    { promptUuid: promptUuid || '' },
    { enabled: !!promptUuid, refetchOnMount: 'always' }
  );

  // Version UUID를 통해 상세 데이터 호출
  const { data: infPromptMsgs, refetch: refetchInfPromptMsgs } = useGetInfPromptMsgsById(
    { versionUuid: currentVersionUuid },
    { enabled: !!currentVersionUuid, refetchOnMount: 'always' }
  );

  const { data: infPromptTag, refetch: refetchInfPromptTag } = useGetInfPromptTagById(
    { versionUuid: currentVersionUuid },
    { enabled: !!currentVersionUuid, refetchOnMount: 'always' }
  );

  const { data: infPromptVars, refetch: refetchInfPromptVars } = useGetInfPromptVarsById(
    { versionUuid: currentVersionUuid },
    { enabled: !!currentVersionUuid, refetchOnMount: 'always' }
  );

  /**
   * 선택된 에이전트 ID 상태
   */
  const [selectedAgentId, setSelectedAgentId] = useState<string>('');

  /**
   * 선택된 에이전트 데이터 조회
   */
  const { data: selectedAgentData } = useGetAgentBuilderById(selectedAgentId, { refetchOnMount: 'always' });

  // TODO: 템플릿 정보가 API에 추가되면 빌트인 템플릿 데이터 사용
  // const { data: builtinData } = useGetInfPromptBuiltin();

  // 버전 히스토리 데이터
  const versions: UIVersionCardItem[] = useMemo(() => {
    const list = infPromptVerList?.versions ?? [];
    const latestId = infPromptLatestVer?.versionUuid;
    const isSelected = selectedVersionId;

    return list.map(item => {
      const isLatest = item.versionUuid === latestId;
      const isRelease = item.release;
      const isSelectedVersion = item.versionUuid === isSelected;

      // 태그 배열 생성
      const tags: Array<{ label: string; intent: 'blue' | 'gray' | 'violet' }> = [];
      if (isLatest) {
        tags.push({ label: 'Latest', intent: 'gray' });
      }
      if (isRelease) {
        tags.push({ label: 'Release', intent: 'blue' });
      }

      return {
        id: item.versionUuid,
        version: `Ver.${item.version}`,
        date: item.createdAt || '-',
        tags: tags.length > 0 ? tags : undefined,
        isActive: isSelectedVersion,
        createdBy: item.createdBy,
        createdAt: item.createdAt,
      };
    });
  }, [infPromptVerList, infPromptLatestVer, selectedVersionId]);

  const selectedVersion = useMemo(() => {
    const selected = versions.find(v => v.id === selectedVersionId);
    if (!selected) return null;

    // UIVersionCardItem을 VersionItem 형식으로 변환 (기존 코드 호환성)
    return {
      id: selected.id || '',
      version: selected.version.replace('Ver.', ''),
      createdDate: selected.date,
      isLatest: selected.tags?.some(tag => tag.label === 'Latest') || false,
      isRelease: selected.tags?.some(tag => tag.label === 'Release') || false,
    };
  }, [versions, selectedVersionId]);

  // 최신 버전이 있고 선택된 버전이 없으면 자동으로 최신 버전 선택
  useEffect(() => {
    if (infPromptLatestVer?.versionUuid && !selectedVersionId) {
      setSelectedVersionId(infPromptLatestVer.versionUuid);
    }
  }, [infPromptLatestVer?.versionUuid]);

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
    if (prevPromptUuidRef.current && prevPromptUuidRef.current !== promptUuid) {
      setSelectedVersionId(null);
    }
    prevPromptUuidRef.current = promptUuid ?? null;
  }, [promptUuid]);

  // 수정 후 페이지 재진입 시 최신 버전 자동 선택
  useEffect(() => {
    const state = location.state as { shouldRefresh?: boolean } | null;
    if (state?.shouldRefresh && infPromptLatestVer?.versionUuid) {
      setSelectedVersionId(infPromptLatestVer.versionUuid);
      // state 초기화 (뒤로가기 시 중복 실행 방지)
      window.history.replaceState({}, document.title);
    }
  }, [location.state, infPromptLatestVer?.versionUuid]);

  // 초기화 로직
  useEffect(() => {
    if (!currentVersionUuid) {
      setSystemPrompt('');
      setUserPrompt('');
      setVariableSettingsItems([]);
    }
  }, [currentVersionUuid]);

  useEffect(() => {
    const msgs = infPromptMsgs?.messages ?? [];
    let nextSystem = '';
    let sysOrder = -Infinity;
    let nextUser = '';
    let usrOrder = -Infinity;

    for (const m of msgs) {
      if (!m?.message) continue;
      if (m.mtype === 1 && (m.order ?? -Infinity) >= sysOrder) {
        sysOrder = m.order ?? -Infinity;
        nextSystem = m.message;
      } else if (m.mtype === 2 && (m.order ?? -Infinity) >= usrOrder) {
        usrOrder = m.order ?? -Infinity;
        nextUser = m.message;
      }
    }
    setSystemPrompt(prev => (prev === nextSystem ? prev : nextSystem));
    setUserPrompt(prev => (prev === nextUser ? prev : nextUser));
  }, [infPromptMsgs]);

  useEffect(() => {
    const vars = infPromptVars?.variables ?? [];
    if (!vars.length) {
      setVariableSettingsItems([]);
      return;
    }

    const next: InfPromptVarUI[] = vars.map((v, idx) => ({
      id: v.variableId || `${v.variable ?? 'var'}-${idx}`,
      label: v.variable ?? '-',
      variableEnabled: !!v.validationFlag, // 변수 설정 토글
      regexValue: v.validation ?? '', // 정규표현식 입력
      tokenLimitEnabled: !!v.tokenLimitFlag, // 토큰 제한 토글
      tokenLimitValue: v.tokenLimit ? String(v.tokenLimit) : '',
      showAddButton: idx === vars.length - 1, // 마지막 항목만 “프롬프트 추가” 버튼 노출(기존 UX 유지)
    }));
    setVariableSettingsItems(next);
  }, [infPromptVars]);

  /**
   *  추론 프롬프트 삭제
   */
  const { openAlert } = useModal();
  const { mutate: deleteInfPrompt } = useDeleteInfPromptByPromptId({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '추론 프롬프트가 삭제되었습니다.',
        onConfirm: () => {
          // 삭제 성공 시에만 목록 페이지로 이동하면서 새로고침 신호 전달 (CreatePopup과 동일한 패턴)
          navigate('/prompt/inferPrompt', { state: { shouldRefresh: true } });
        },
      });
    },
    onError: () => {
      // console.error('추론 프롬프트 삭제 실패');
      // 삭제 실패 시에는 새로고침 없이 목록 페이지로 이동
      navigate('/prompt/inferPrompt');
    },
  });

  /**
   * 추론 프롬프트 릴리즈
   */
  const { mutate: releaseInfPrompt } = useReleaseInfPrompt({
    onSuccess: async () => {
      // console.log('추론 프롬프트 릴리즈 성공');
      // 릴리즈 성공 시 데이터 새로고침
      await Promise.all([refetchInfPrompt(), refetchInfPromptVerList(), refetchInfPromptLatestVer(), refetchInfPromptMsgs(), refetchInfPromptTag(), refetchInfPromptVars()]);
      // console.log('릴리즈 후 데이터 새로고침 완료');
    },
    onError: () => {
      // console.error('추론 프롬프트 릴리즈 실패');
    },
  });

  /**
   * 추론 프롬프트 삭제 핸들러
   */
  const handleDelete = () => {
    if (!promptUuid) {
      return;
    }

    // built-in 프롬프트는 편집 불가
    if (tagList.includes('built-in')) {
      openAlert({
        title: '안내',
        message: '추론프롬프트 편집 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 삭제 불가
    if (Number(infPrompt?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(infPrompt?.fstPrjSeq)) {
      openAlert({
        title: '안내',
        message: '추론프롬프트 편집 권한이 없습니다.',
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
        deleteInfPrompt({ promptUuid });
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  /**
   * 추론 프롬프트 릴리즈 핸들러
   */
  const handleRelease = () => {
    if (!promptUuid || !currentVersionUuid) {
      // console.error('릴리즈할 프롬프트 UUID 또는 버전 UUID가 없습니다.');
      return;
    }

    // built-in 프롬프트는 편집 불가
    if (tagList.includes('built-in')) {
      openAlert({
        title: '안내',
        message: '추론프롬프트 편집 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '해당 버전을 배포하시겠어요?',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        // 현재 선택된 버전의 데이터를 가져와서 릴리즈 요청 데이터 생성
        const messages =
          infPromptMsgs?.messages?.map(msg => ({
            mtype: msg.mtype,
            message: msg.message,
            order: msg.order,
          })) || [];

        const tags = infPromptTag?.tags?.map(tag => ({ tag: tag.tag })) || [];

        const variables =
          infPromptVars?.variables?.map(variable => ({
            variable: variable.variable,
            validation: variable.validation,
            validationFlag: variable.validationFlag,
            tokenLimitFlag: variable.tokenLimitFlag,
            tokenLimit: variable.tokenLimit,
          })) || [];

        const releaseData = {
          promptUuid: promptUuid,
          newName: infPrompt?.name || '',
          desc: '',
          messages: messages,
          release: true, // 릴리즈는 항상 true
          tags: tags,
          variables: variables,
        };

        // console.log('릴리즈 요청 데이터:', releaseData);
        releaseInfPrompt(releaseData);
      },
      onCancel: () => {
        // console.log('릴리즈 취소됨');
      },
    });
  };

  /**
   * 추론 프롬프트 수정 성공 시 데이터 refetch
   */
  const handleEditSuccess = async () => {
    // 1. 기본 정보와 버전 정보 refetch
    await Promise.all([refetchInfPrompt(), refetchInfPromptVerList(), refetchInfPromptLatestVer()]);

    // 2. 최신 버전 정보를 refetch한 후 새로운 최신 버전을 선택
    const latestVersionResult = await refetchInfPromptLatestVer();
    const newLatestVersionUuid = latestVersionResult.data?.versionUuid;

    if (newLatestVersionUuid) {
      // 새로운 최신 버전을 명시적으로 선택
      setSelectedVersionId(newLatestVersionUuid);

      // 3. 선택된 버전의 상세 데이터 refetch (약간의 지연 후 selectedVersionId 변경 감지 대기)
      setTimeout(async () => {
        await Promise.all([refetchInfPromptMsgs(), refetchInfPromptTag(), refetchInfPromptVars()]);
      }, 100);
    }
  };

  /**
   * 추론 프롬프트 생성 팝업 호출
   */
  const { user } = useUser();
  const handleInfPromptEditPopup = () => {
    // built-in 프롬프트는 편집 불가
    if (tagList.includes('built-in')) {
      openAlert({
        title: '안내',
        message: '추론프롬프트 편집 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 수정 불가
    if (Number(infPrompt?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(infPrompt?.fstPrjSeq)) {
      openAlert({
        title: '안내',
        message: '추론프롬프트 편집 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    // '수정' 버튼을 누를 때마다 팝업 컴포넌트를 새로 마운트하여 내부 상태를 초기화
    setEditPopupOpenSeq(prev => prev + 1);
    layerPopupOne.onOpen();
  };

  // 연결된 에이전트 페이지네이션 상태
  const [currentPage, setCurrentPage] = useState<number>(1);
  const pageSize = 6;

  // 연결된 에이전트 목록 조회
  const { data: lineageRelations } = useGetInfPromptLineageRelations(
    {
      promptUuid: promptUuid || '',
      page: currentPage,
      size: pageSize,
    },
    {
      enabled: !!promptUuid,
      retry: 3,
      retryDelay: 1000,
      refetchOnMount: 'always',
    }
  );

  // 연결된 에이전트 데이터 변환
  const agentData = useMemo(
    () =>
      (lineageRelations?.content ?? []).map((item: any, idx: number) => ({
        id: item.id,
        no: (currentPage - 1) * pageSize + idx + 1,
        name: item.name,
        description: item.description,
        isDeployed: item.deployed ? '배포' : '미배포',
        publicRange: item.publicRange || '전체공유',
        createdDate: item.createdAt,
        modifiedDate: item.updatedAt,
      })),
    [lineageRelations?.content, currentPage, pageSize]
  );

  // 그리드 컬럼 정의 (디자인과 동일 구조)
  const columnDefs: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (currentPage - 1) * pageSize + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 250,
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 270,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={params.value}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '배포 여부',
        field: 'isDeployed',
        width: 131,
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
      },
    ],
    [currentPage]
  );

  const handleBuilderClick = (agentId: string) => {
    if (!agentId) {
      // console.warn('에이전트 ID가 없습니다.');
      return;
    }

    // 선택된 에이전트 ID 설정 (useGetAgentBuilderById가 자동으로 조회)
    setSelectedAgentId(agentId);
  };

  /**
   * 선택된 에이전트 데이터가 로드되면 페이지 이동
   */
  useEffect(() => {
    if (selectedAgentData && selectedAgentId) {
      navigate(`/agent/builder/graph`, {
        state: {
          isReadOnly: true, // 조회모드로 링크
          data: {
            id: selectedAgentData.id,
            name: selectedAgentData.name,
            description: selectedAgentData.description,
            project_id: selectedAgentData.id,
            nodes: selectedAgentData.nodes || [],
            edges: selectedAgentData.edges || [],
          },
        },
      });
      // 이동 후 상태 초기화
      setSelectedAgentId('');
    }
  }, [selectedAgentData, selectedAgentId, navigate]);

  // 프롬프트 아코디언 데이터
  const promptAccordionItems = useMemo(
    () => [
      {
        title: '시스템 프롬프트',
        content: systemPrompt || '',
        defaultOpen: true,
        showNoticeIcon: false,
      },
      {
        title: '유저 프롬프트',
        content: userPrompt || '',
        defaultOpen: true,
        showNoticeIcon: false,
      },
    ],
    [systemPrompt, userPrompt]
  );

  // 버전 태그 우선, 없으면 프롬프트 태그 사용
  const tagList = useMemo(() => {
    const fromVersion = (infPromptTag?.tags ?? []).map(t => t.tag).filter(Boolean);

    // 프롬프트의 tags가 ["dev,prd"] 형태일 수 있어 split 처리
    const fromPrompt = (infPrompt?.tags ?? [])
      .flatMap(s => (typeof s === 'string' ? s.split(',') : []))
      .map(s => s.trim())
      .filter(Boolean);

    const merged = fromVersion.length ? fromVersion : fromPrompt;
    return Array.from(new Set(merged)); // 중복 제거
  }, [infPromptTag, infPrompt]);

  if (!promptUuid) {
    // console.error('URL 파라미터 id가 없습니다.');
    return null;
  }

  return (
    <section className='section-page'>
      <UIPageHeader
        title='추론 프롬프트 조회'
        description=''
        actions={
          <>
            <Button auth={AUTH_KEY.PROMPT.INFERENCE_PROMPT_UPDATE} className='btn-model-detail' onClick={handleRelease} disabled={selectedVersion?.isRelease}>
              릴리즈
            </Button>
          </>
        }
      />

      <UIPageBody>
        <div className='grid-layout'>
          {/* 왼쪽 영역 */}
          <div className='grid-article'>
            {/* 기본 정보 섹션 */}
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  기본 정보
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
                            {infPrompt?.name ?? '-'}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            버전
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex gap-2'>
                            <UIUnitGroup gap={8} direction='row' align='start'>
                              {selectedVersion?.isRelease && <UITextLabel intent='blue'>Release Ver.{selectedVersion?.version}</UITextLabel>}
                              {selectedVersion?.isLatest ? (
                                <UITextLabel intent='gray'>Latest Ver.{selectedVersion?.version}</UITextLabel>
                              ) : (
                                <UITextLabel intent='gray'>Ver.{selectedVersion?.version}</UITextLabel>
                              )}
                            </UIUnitGroup>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            유형
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {getPTypeLabel(infPrompt?.ptype)}
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
                          {tagList.length > 0 ? (
                            <UIUnitGroup gap={8} direction='row' align='start'>
                              {tagList.map((tag, index) => (
                                <UITextLabel key={index} intent='tag'>
                                  {tag}
                                </UITextLabel>
                              ))}
                            </UIUnitGroup>
                          ) : (
                            <>-</>
                          )}
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            {/* 프롬프트 섹션 */}
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  프롬프트
                </UITypography>
              </div>
              <div className='article-body'>
                <UIAccordion items={promptAccordionItems} variant='box' allowMultiple={true} />
              </div>
            </UIArticle>

            {/* 변수 속성 설정 섹션 */}
            {variableSettingsItems.length > 0 && (
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    변수 속성 설정
                  </UITypography>
                </div>
                <div className='article-body'>
                  <UIFormField gap={8} direction='column'>
                    <div className='variable-settings-card'>
                      {variableSettingsItems.map(item => (
                        <div key={item.id} className='variable-setting-item'>
                          {/* 라벨 */}
                          <div className='flex items-center gap-2 mb-2 h-6'>
                            <div className='w-1 h-6 flex items-center'>
                              <div className='w-1 h-1 bg-gray-500 rounded-full'></div>
                            </div>
                            <UITypography variant='body-1' className='secondary-neutral-600 text-sb'>
                              {item.label}
                            </UITypography>
                          </div>

                          {/* 컨텐츠 영역 - 2열 그리드 */}
                          <div className='grid grid-cols-2 gap-3'>
                            {/* 변수 설정 박스 */}
                            <div className='bg-white border border-gray-200 rounded-[18px] overflow-hidden'>
                              <div className='px-8 pt-8 pb-6'>
                                <div className='flex items-center gap-3'>
                                  <UIToggle
                                    checked={!!item.variableEnabled}
                                    onChange={val => setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, variableEnabled: val } : v)))}
                                    size='small'
                                    disabled={true}
                                  />
                                  <UITypography variant='title-4' className='secondary-neutral-800'>
                                    변수 설정
                                  </UITypography>
                                </div>
                              </div>
                              <div className='w-full px-8'>
                                <UITextArea2
                                  className='w-full px-0 bg-white resize-none focus:outline-none'
                                  value={String(item.regexValue)}
                                  placeholder=''
                                  onChange={e => setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, regexValue: e.target.value } : v)))}
                                  noBorder={true}
                                  readOnly={true}
                                />
                              </div>
                            </div>

                            {/* 토큰 제한 박스 */}
                            <div className='bg-white border border-gray-200 rounded-[18px] overflow-hidden'>
                              <div className='px-8 pt-8 pb-6'>
                                <div className='flex items-center gap-3'>
                                  <UIToggle
                                    checked={!!item.tokenLimitEnabled}
                                    onChange={val => setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, tokenLimitEnabled: val } : v)))}
                                    size='small'
                                    disabled={true}
                                  />
                                  <UITypography variant='title-4' className='secondary-neutral-800'>
                                    토큰 제한
                                  </UITypography>
                                </div>
                              </div>
                              <div className='w-full px-8'>
                                <UITextArea2
                                  className='w-full px-0 bg-white resize-none focus:outline-none'
                                  value={item.tokenLimitValue ?? ''}
                                  placeholder=''
                                  onChange={e => setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, tokenLimitValue: e.target.value } : v)))}
                                  noBorder={true}
                                  readOnly={true}
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </UIFormField>
                </div>
              </UIArticle>
            )}

            {/* 연결된 에이전트 섹션 */}
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={lineageRelations?.totalElements || 0} prefix=' 연결된 에이전트 총' unit='건' />
                  </div>
                </UIListContentBox.Header>

                <UIListContentBox.Body>
                  <UIGrid
                    type='default'
                    rowData={agentData}
                    columnDefs={columnDefs as any}
                    onClickRow={(params: any) => {
                      if (params.data.id) {
                        handleBuilderClick(params.data.id);
                      }
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination
                    currentPage={currentPage}
                    totalPages={lineageRelations?.totalPages || 1}
                    onPageChange={(page: number) => {
                      setCurrentPage(page);
                    }}
                    className='flex justify-center'
                  />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            {/* 담당자 정보 섹션 */}
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
                            {versions ? versions[versions.length - 1]?.createdBy : ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            생성일시
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {versions ? versions[versions.length - 1]?.createdAt : ''}
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
                            {versions ? versions[0]?.createdBy : ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            최종 수정일시
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {versions ? versions[0]?.createdAt : ''}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            {/* 프로젝트 정보 섹션 */}
            <ProjectInfoBox assets={[{ type: 'infer-prompts', id: promptUuid || '' }]} auth={AUTH_KEY.PROMPT.INFERENCE_PROMPT_CHANGE_PUBLIC} />

            <div className='article-buton-group'>
              <UIUnitGroup gap={8} direction='row' align='center'>
                <Button auth={AUTH_KEY.PROMPT.INFERENCE_PROMPT_DELETE} className='btn-primary-gray' onClick={handleDelete}>
                  삭제
                </Button>
                <Button auth={AUTH_KEY.PROMPT.INFERENCE_PROMPT_UPDATE} className='btn-primary-blue' onClick={handleInfPromptEditPopup}>
                  수정
                </Button>
              </UIUnitGroup>
            </div>
          </div>

          {/* 오른쪽 영역 */}
          <div className='grid-right-sticky'>
            <UIVersionCard
              versions={versions}
              onVersionClick={version => {
                if (version.id) {
                  setSelectedVersionId(version.id);
                }
              }}
            />
          </div>
        </div>
      </UIPageBody>

      {/* 수정 팝업 */}
      <InfPromptEditPopupPage
        key={`${promptUuid}-${editPopupOpenSeq}`}
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        promptUuid={promptUuid}
        onEditSuccess={handleEditSuccess}
        initialName={infPrompt?.name || ''}
        initialTemplate='none'
        initialSystemPrompt={systemPrompt}
        initialUserPrompt={userPrompt}
        initialTags={tagList}
        initialRelease={selectedVersion?.isRelease || false}
        initialVariables={variableSettingsItems.map(item => ({ ...item }))}
      />
    </section>
  );
};
