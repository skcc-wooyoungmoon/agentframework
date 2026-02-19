import React, { useEffect, useMemo, useState } from 'react';

import { useLocation, useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIToggle } from '@/components/UI';
import { UIBox, UIButton2, UIDataCnt } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { InfPromptCreatePopup, InfPromptEditPopupPage } from '@/pages/prompt';
import {
  useDeleteInfPromptByPromptId,
  useGetInfPromptById,
  useGetInfPromptLatestVerById,
  useGetInfPromptList,
  useGetInfPromptMsgsById,
  useGetInfPromptTagById,
  useGetInfPromptTags,
  useGetInfPromptVarsById,
} from '@/services/prompt/inference/inferencePrompts.services';
import { useUser } from '@/stores'; // ============================================
import { useModal } from '@/stores/common/modal';

// ============================================
// 타입 정의
// ============================================

const ALL_TAG_VALUE = '__ALL__';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  selectedTag: string;
  view: string;
}

interface SearchQuery {
  searchKeyword: string;
  selectedTag: string;
}

// ============================================
// 헬퍼 함수: 태그 정규화
// ============================================

/**
 * 다양한 형식의 태그를 문자열 배열로 정규화
 *
 * 지원 형식:
 * - string: 그대로 사용
 * - JSON string: 파싱 후 tag/name 필드 추출
 * - object: tag/name 필드 추출
 *
 * @param tags - 원본 태그 데이터 (string[], object[], JSON string 등)
 * @returns 정규화된 태그 문자열 배열
 *
 * @example
 * normalizeTagLabels(['tag1', '{"tag":"tag2"}', {tag: 'tag3'}])
 * // ['tag1', 'tag2', 'tag3']
 */
const normalizeTagLabels = (tags: any): string[] => {
  if (!Array.isArray(tags)) return [];
  const labels: string[] = [];

  for (const t of tags) {
    if (t == null) continue;

    if (typeof t === 'string') {
      const s = t.trim();
      if (!s) continue;

      // JSON 형식의 태그 객체 파싱 시도
      try {
        const parsed = JSON.parse(s);
        if (parsed && typeof parsed === 'object') {
          const tagVal = parsed.tag ?? parsed.name;
          if (typeof tagVal === 'string' && tagVal.trim()) {
            labels.push(tagVal.trim());
            continue;
          }
        }
      } catch {
        // JSON 파싱 실패 시 원본 문자열을 그대로 사용 (의도된 동작)
        // 파싱 실패는 정상적인 경우일 수 있으므로 에러를 무시하고 계속 진행
        // 다음 줄에서 원본 문자열(s)을 labels에 추가함
      }

      labels.push(s);
    } else if (typeof t === 'object') {
      const tagVal = (t as any).tag ?? (t as any).name;
      if (typeof tagVal === 'string' && tagVal.trim()) {
        labels.push(tagVal.trim());
      }
    }
  }

  return labels;
};

/**
 * 프롬프트 > 추론 프롬프트 목록 페이지
 *
 * 주요 기능:
 * - 추론 프롬프트 목록 조회 (검색, 태그 필터, 페이징)
 * - 그리드/카드 뷰 전환
 * - 프롬프트 생성/수정/삭제
 * - 태그 정규화 및 표시
 */
export const InfPromptListPage = () => {
  // ============================================
  // Router & Hook
  // ============================================
  const navigate = useNavigate();
  const location = useLocation();
  const layerPopupOne = useLayerPopup(); // 생성 팝업
  const layerEditPopup = useLayerPopup(); // 수정 팝업
  const { openAlert, openConfirm } = useModal();

  // ============================================
  // State: 수정 팝업
  // ============================================
  // 수정할 프롬프트 ID
  const [editingPromptUuid, setEditingPromptUuid] = useState<string | null>(null);

  // ============================================
  // State: 검색 및 필터
  // ============================================
  // 검색 조건 (사용자가 입력한 값)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.INFERENCE_PROMPT_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    selectedTag: ALL_TAG_VALUE,
    view: 'grid',
  });

  // 실제 API 호출에 사용되는 검색 쿼리 (엔터 키 또는 조회 버튼 클릭 시에만 업데이트)
  const [searchQuery, setSearchQuery] = useState<SearchQuery>({
    searchKeyword: '',
    selectedTag: '',
  });

  // 페이지 마운트 시 검색어 초기화
  useEffect(() => {
    setSearchValues({
      searchKeyword: '',
      selectedTag: ALL_TAG_VALUE,
    });
    setSearchQuery({
      searchKeyword: '',
      selectedTag: '',
    });
  }, []);

  useEffect(() => {
    if (searchValues.selectedTag === '') {
      setSearchValues({ selectedTag: ALL_TAG_VALUE });
    }
  }, [searchValues.selectedTag, setSearchValues]);

  const [sort] = useState<string | undefined>('created_at,desc');

  const normalizedSearchKeyword = searchQuery.searchKeyword.trim();
  const normalizedSelectedTag = searchQuery.selectedTag === ALL_TAG_VALUE || searchQuery.selectedTag === '' ? undefined : searchQuery.selectedTag;

  // ============================================
  // State: 뷰 모드 및 선택
  // ============================================

  const [selectedIds, setSelectedIds] = useState<string[]>([]);

  // ============================================
  // API: 데이터 조회
  // ============================================
  // 태그 필터 생성 (tags:태그명 형식)
  const tagFilter = normalizedSelectedTag ? `tags:${normalizedSelectedTag}` : undefined;
  const { user } = useUser();

  const { data, refetch, isLoading } = useGetInfPromptList(
    {
      project_id: user.adxpProject.prjUuid,
      page: searchValues.page,
      size: searchValues.size,
      sort,
      filter: tagFilter,
      search: normalizedSearchKeyword || undefined,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  /**
   * 태그 목록 조회 (드롭다운 열 때만 refetch)
   * 백엔드 응답 구조: { data: { tags: string[] | null, total: number | null } }
   */
  const { data: tagList, refetch: refetchTags } = useGetInfPromptTags({ enabled: false });

  // ============================================
  // API: 수정 팝업용 데이터 조회
  // ============================================
  // 수정할 프롬프트 상세 정보 조회
  const { data: editingPromptData, refetch: refetchEditingPromptData } = useGetInfPromptById(
    { promptUuid: editingPromptUuid || '' },
    {
      enabled: !!editingPromptUuid,
    }
  );

  // 수정할 프롬프트의 최신 버전 조회
  const { data: editingLatestVersion, refetch: refetchEditingLatestVersion } = useGetInfPromptLatestVerById(
    { promptUuid: editingPromptUuid || '' },
    {
      enabled: !!editingPromptUuid,
    }
  );

  // 수정할 프롬프트의 메시지 조회
  const { data: editingPromptMsgs, refetch: refetchEditingPromptMsgs } = useGetInfPromptMsgsById(
    { versionUuid: editingLatestVersion?.versionUuid || '' },
    {
      enabled: !!editingLatestVersion?.versionUuid,
    }
  );

  // 수정할 프롬프트의 태그 조회
  const { data: editingPromptTags, refetch: refetchEditingPromptTags } = useGetInfPromptTagById(
    { versionUuid: editingLatestVersion?.versionUuid || '' },
    {
      enabled: !!editingLatestVersion?.versionUuid,
    }
  );

  // 수정할 프롬프트의 변수 조회
  const { data: editingPromptVars, refetch: refetchEditingPromptVars } = useGetInfPromptVarsById(
    { versionUuid: editingLatestVersion?.versionUuid || '' },
    {
      enabled: !!editingLatestVersion?.versionUuid,
    }
  );

  // 수정 팝업이 열릴 때 데이터 다시 조회
  useEffect(() => {
    if (layerEditPopup.currentStep > 0 && editingPromptUuid) {
      refetchEditingPromptData();
      refetchEditingLatestVersion();
    }
  }, [layerEditPopup.currentStep, editingPromptUuid]);

  // 최신 버전이 변경되면 메시지, 태그, 변수 다시 조회
  useEffect(() => {
    if (editingLatestVersion?.versionUuid) {
      refetchEditingPromptMsgs();
      refetchEditingPromptTags();
      refetchEditingPromptVars();
    }
  }, [editingLatestVersion?.versionUuid]);

  // ============================================
  // API: 데이터 변경 (삭제)
  // ============================================
  const { mutate: deleteInfPrompt } = useDeleteInfPromptByPromptId({
    onSuccess: () => {
      // 삭제 성공
    },
    onError: () => {
      // 삭제 실패 시 처리
    },
  });

  // ============================================
  // Event Handler: 검색
  // ============================================

  /**
   * 검색/조회 버튼 클릭 핸들러
   * 검색 쿼리를 업데이트하고 첫 페이지로 이동
   */
  const handleSearch = () => {
    // 검색 쿼리 업데이트 (이것이 API 호출을 트리거함)
    setSearchQuery({
      searchKeyword: searchValues.searchKeyword,
      selectedTag: searchValues.selectedTag === ALL_TAG_VALUE ? '' : searchValues.selectedTag,
    });

    // 페이지를 첫 페이지로 리셋 (리셋 후 useEffect에서 API 호출)
    setSearchValues({
      page: 1,
    });

    if (env.VITE_NO_PRESSURE_MODE) {
      // NO_PRESSURE_MODE에서는 조회 버튼에서만 조회되도록 refetch를 여기서 수행
      setTimeout(() => refetch(), 0);
    }
  };

  /**
   * 검색 입력 필드에서 엔터 키 입력 시 검색 실행
   */
  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };

  // ============================================
  // Event Handler: 행 선택
  // ============================================

  /**
   * 그리드/카드 행 선택/해제 핸들러
   * 선택된 ID들을 Set으로 관리한 후 배열로 변환
   */
  const handleRowSelection = (uuid: string, isChecked: boolean) => {
    setSelectedIds(prev => {
      const newSelectedIds = new Set(prev);
      if (isChecked) {
        newSelectedIds.add(uuid);
      } else {
        newSelectedIds.delete(uuid);
      }
      return Array.from(newSelectedIds);
    });
  };

  // ============================================
  // Event Handler: 삭제
  // ============================================

  /**
   * 프롬프트 삭제 핸들러
   * 여러 개의 프롬프트를 순차적으로 삭제
   * 개별 삭제 실패 시에도 계속 진행
   *
   * @param ids - 삭제할 프롬프트 UUID 배열
   */
  const handleDelete = async (ids: string[]) => {
    // 선택 항목 없음 체크
    if (ids.length === 0) {
      openAlert({
        message: '삭제할 항목을 선택해 주세요.',
        title: '안내',
      });
      return;
    }

    // 삭제 확인 컨펌
    const confirmed = await openConfirm({
      title: '안내',
      message: '삭제 하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '삭제',
      cancelText: '취소',
    });

    if (!confirmed) {
      return;
    }

    let successCount = 0;
    let failCount = 0;

    // 순차적으로 삭제 처리
    for (const id of ids) {
      try {
        await new Promise<void>((resolve, reject) => {
          deleteInfPrompt(
            { promptUuid: id },
            {
              onSuccess: () => {
                successCount++;
                resolve();
              },
              onError: () => {
                // console.error(`추론 프롬프트 ${id} 삭제 실패`);
                failCount++;
                reject(new Error(`추론 프롬프트 ${id} 삭제 실패`));
              },
            }
          );
        });
      } catch {
        // 개별 삭제 실패는 계속 진행 (의도된 동작)
        // onError 콜백에서 이미 failCount가 증가했으므로 에러를 무시하고 다음 항목 삭제 계속
        // 루프는 계속 진행되어 나머지 항목들의 삭제를 시도함
      }
    }

    // 삭제 결과 처리
    if (failCount > 0) {
      await openAlert({
        title: '부분 실패',
        message: `총 ${ids.length}개 중 ${successCount}개 삭제, ${failCount}개 실패했습니다.`,
        confirmText: '확인',
      });
    } else if (successCount > 0) {
      await openAlert({
        title: '완료',
        message: '프롬프트가 삭제되었습니다.',
        confirmText: '확인',
      });
    }

    // 성공적으로 삭제된 경우에만 목록 새로고침
    if (successCount > 0) {
      setSelectedIds([]); // 선택 해제
      refetch();
    }
  };

  // ============================================
  // Event Handler: 팝업 호출
  // ============================================

  /**
   * 프롬프트 생성 팝업 열기
   */
  const handleInfPromptCreatePopup = () => {
    layerPopupOne.onOpen();
  };

  // ============================================
  // Computed: 태그 옵션 (전체 옵션 포함, 데이터 없으면 [] → refetchOnOpen 동작)
  // ============================================
  const tagOptions = useMemo(() => {
    if (!tagList?.tags) return [];
    return [{ value: ALL_TAG_VALUE, label: '전체' }, ...tagList.tags.map((tag: string) => ({ value: tag, label: tag }))];
  }, [tagList]);

  // ============================================
  // Computed: 변수 데이터 변환 (수정 팝업용)
  // ============================================
  const initialVariables = useMemo(() => {
    const vars = editingPromptVars?.variables ?? [];
    if (!vars.length) return [];

    return vars.map((v, idx) => ({
      id: v.variableId || `${v.variable ?? 'var'}-${idx}`,
      label: v.variable ?? '-',
      variableEnabled: !!v.validationFlag,
      regexValue: v.validation ?? '',
      tokenLimitEnabled: !!v.tokenLimitFlag,
      tokenLimitValue: v.tokenLimit ? String(v.tokenLimit) : '',
      showAddButton: false, // 팝업에서는 추가 버튼 미표시
    }));
  }, [editingPromptVars]);

  // ============================================
  // Computed: 그리드 뷰 데이터 변환
  // ============================================
  /**
   * API 응답 데이터를 그리드 표시용으로 변환
   */
  const gridRowData = useMemo(() => {
    if (!data?.content) return [] as any[];

    return data.content.map((item: any, index: number) => ({
      id: item.uuid,
      no: (searchValues.page - 1) * searchValues.size + index + 1,
      name: item.name,
      releaseVersion: item.releaseVersion ?? '',
      latestVersion: item.latestVersion ?? '',
      ptype: item.ptype,
      tags: item.tags ?? [],
      publicStatus: item.publicStatus,
      fstPrjSeq: item.fstPrjSeq,
      lstPrjSeq: item.lstPrjSeq,
      connectedAgent: item.connectedAgentCount,
      createdAt: item.createdAt ?? '',
      more: 'more',
    }));
  }, [data?.content, searchValues.page, searchValues.size]);

  // ============================================
  // Computed: 카드 뷰 데이터 변환
  // ============================================
  /**
   * API 응답 데이터를 카드 표시용으로 변환
   * 태그를 문자열 배열로 변환하고 레이블 생성
   */
  const cardRowData = useMemo(() => {
    if (!data?.content) return [] as any[];

    return data.content.map((item: any) => {
      const tagNames = normalizeTagLabels(item.tags ?? []);

      return {
        id: item.uuid,
        title: item.name || '이름 없음',
        releaseVersion: item.releaseVersion ?? '',
        latestVersion: item.latestVersion ?? '',
        tags: tagNames,
        tagsLabel: tagNames.length > 0 ? tagNames.join(', ') : '',
        connectedAgent: item.connectedAgentCount,
        createdAt: item.createdAt ?? '-',
      };
    });
  }, [data?.content]);

  // ============================================
  // Computed: 그리드 컬럼 정의
  // ============================================
  /**
   * ag-grid 컬럼 설정
   * 각 컬럼의 너비, 렌더러, 정렬 등을 정의
   */
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as any,
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '이름',
        field: 'name' as any,
        flex: 1,
      },
      {
        headerName: '버전',
        field: 'releaseVersion' as const,
        width: 238,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const row = params.data || {};
          return (
            <div className='flex items-center gap-[8px]'>
              {row?.releaseVersion ? <UITextLabel intent='blue'>{`Release Ver.${row.releaseVersion}`}</UITextLabel> : null}
              {row?.latestVersion ? <UITextLabel intent='gray'>{`Latest.${row.latestVersion}`}</UITextLabel> : null}
            </div>
          );
        },
      },
      {
        headerName: '유형',
        field: 'ptype' as any,
        width: 120,
        cellRenderer: (params: any) => {
          const map: Record<string, string> = {
            '1': '채팅',
            '2': '시스템',
            '3': '사용자',
          };
          return map[String(params.value)] ?? '-';
        },
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const tags = normalizeTagLabels(params.value);
          if (!tags.length) {
            return null;
          }
          const tagText = tags.join(', ');
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={tagText}
            >
              <div className='flex gap-1 flex-wrap'>
                {tags.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {tag}
                  </UITextLabel>
                ))}
                {/* 4개 초과 시 ... 표시 */}
                {tags.length > 2 && (
                  <UITypography variant='caption-2' className='secondary-neutral-550'>
                    {'...'}
                  </UITypography>
                )}
              </div>
            </div>
          );
        },
      },
      {
        headerName: '연결 에이전트',
        field: 'connectedAgent' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
      },
    ],
    []
  );

  // ============================================
  // Computed: 더보기 메뉴 설정
  // ============================================
  /**
   * 그리드 행의 더보기(more) 버튼 메뉴 설정
   * 수정/삭제 액션 제공
   */
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.PROMPT.INFERENCE_PROMPT_UPDATE,
          onClick: (rowData: any) => {
            // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 수정 불가
            if (Number(rowData.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(rowData.fstPrjSeq)) {
              openAlert({
                title: '안내',
                message: '추론프롬프트 편집 권한이 없습니다.',
                confirmText: '확인',
              });
              return;
            }

            // 수정 팝업 열기
            setEditingPromptUuid(rowData.id);
            layerEditPopup.onOpen();
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.PROMPT.INFERENCE_PROMPT_DELETE,
          onClick: (rowData: any) => {
            // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 수정 불가
            if (Number(rowData.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(rowData.fstPrjSeq)) {
              openAlert({
                title: '안내',
                message: '추론프롬프트 편집 권한이 없습니다.',
                confirmText: '확인',
              });
              return;
            }

            handleDelete([rowData.id]);
          },
        },
      ],
    }),
    [handleDelete, layerEditPopup]
  );

  // ============================================
  // Side Effect: 삭제 후 자동 새로고침
  // ============================================
  /**
   * 삭제 후 이 페이지로 돌아올 때 목록 새로고침
   * location.state.shouldRefresh 플래그 사용
   */
  useEffect(() => {
    const state = location.state as { shouldRefresh?: boolean } | null;
    if (state?.shouldRefresh) {
      // 현재 검색 조건으로 다시 조회
      setSearchQuery({
        searchKeyword: searchValues.searchKeyword,
        selectedTag: searchValues.selectedTag === ALL_TAG_VALUE ? '' : searchValues.selectedTag,
      });
      refetch();
      refetchTags(); // 태그 목록도 새로고침
      // state 초기화 (뒤로가기 시 중복 실행 방지)
      window.history.replaceState({}, document.title);
    }
  }, [location.state, searchValues.searchKeyword, searchValues.selectedTag, refetch, refetchTags]);

  // ============================================
  // Render
  // ============================================
  return (
    <section className='section-page'>
      <UIPageHeader
        title='추론 프롬프트'
        description={['생성된 AI 모델의 입력으로 사용할 프롬프트를 등록하고 버전별로 관리할 수 있습니다.', '제공된 프롬프트 템플릿을 사용해서 손쉽게 프롬프트를 생성해 보세요.']}
        actions={
          <>
            <Button
              auth={AUTH_KEY.PROMPT.INFERENCE_PROMPT_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleInfPromptCreatePopup}
            >
              프롬프트 등록
            </Button>
          </>
        }
      />

      <UIPageBody>
        {/* ──────────────────────── 필터 영역 ──────────────────────── */}
        <UIArticle className='article-filter'>
          <UIBox className='box-filter'>
            <UIGroup gap={40} direction='row'>
              <div style={{ width: 'calc(100% - 168px)' }}>
                <table className='tbl_type_b'>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          검색
                        </UITypography>
                      </th>
                      <td>
                        <div className='flex-1'>
                          <UIInput.Search
                            value={searchValues.searchKeyword}
                            onChange={e =>
                              setSearchValues(prev => ({
                                ...prev,
                                searchKeyword: e.target.value,
                              }))
                            }
                            onKeyDown={handleKeyDown}
                            placeholder='검색어 입력'
                          />
                        </div>
                      </td>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          태그
                        </UITypography>
                      </th>
                      <td>
                        <div className='flex-1'>
                          <UIDropdown
                            value={searchValues.selectedTag === '' ? ALL_TAG_VALUE : searchValues.selectedTag}
                            placeholder='태그 선택'
                            options={tagOptions}
                            refetchOnOpen={refetchTags}
                            onSelect={value => setSearchValues({ selectedTag: value })}
                          />
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div style={{ width: '128px' }}>
                <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                  조회
                </UIButton2>
              </div>
            </UIGroup>
          </UIBox>
        </UIArticle>

        {/* ──────────────────────── 리스트 영역 ──────────────────────── */}
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='w-full'>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
                      </div>
                    </div>
                    <div className='flex items-center gap-2'>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(searchValues.size)}
                          options={[
                            { value: '12', label: '12개씩 보기' },
                            { value: '36', label: '36개씩 보기' },
                            { value: '60', label: '60개씩 보기' },
                          ]}
                          onSelect={(val: string) => updatePageSizeAndRefetch({ size: parseInt(val, 10) || 12, page: 1 })}
                          height={40}
                          variant='dataGroup'
                          disabled={gridRowData.length === 0}
                        />
                      </div>
                      <UIToggle
                        variant='dataView'
                        checked={searchValues.view === 'card'}
                        onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                        disabled={gridRowData.length === 0}
                      />
                    </div>
                  </div>
                </UIUnitGroup>
              </div>
            </UIListContentBox.Header>

            <UIListContentBox.Body>
              {searchValues.view === 'grid' ? (
                <UIGrid
                  type='multi-select'
                  loading={isLoading}
                  rowData={gridRowData}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={(params: any) => {
                    const id = params?.data?.id ?? params?.id;
                    if (id) navigate(`/prompt/inferPrompt/${id}`);
                  }}
                  onCheck={(rows: any[]) => {
                    setSelectedIds((rows || []).map((r: any) => r.id));
                  }}
                />
              ) : (
                <UICardList
                  rowData={cardRowData}
                  flexType='none'
                  loading={isLoading}
                  card={(item: any) => (
                    <UIGridCard
                      id={item.id}
                      title={item.title}
                      data={item}
                      moreMenuConfig={moreMenuConfig}
                      onClick={() => navigate(`/prompt/inferPrompt/${item.id}`)}
                      statusArea={
                        <UIGroup gap={8} direction='row'>
                          {item.releaseVersion ? <UITextLabel intent='blue'>{`Release Ver.${item.releaseVersion}`}</UITextLabel> : null}
                          {item.latestVersion ? <UITextLabel intent='gray'>{`Latest.${item.latestVersion}`}</UITextLabel> : null}
                        </UIGroup>
                      }
                      checkbox={{
                        checked: selectedIds.includes(item.id),
                        onChange: (checked: boolean) => handleRowSelection(item.id, checked),
                      }}
                      rows={[
                        { label: '태그', value: item.tagsLabel },
                        { label: '연결 에이전트', value: item.connectedAgent },
                        { label: '생성일시', value: item.createdAt },
                      ]}
                    />
                  )}
                />
              )}
            </UIListContentBox.Body>

            <UIListContentBox.Footer className='ui-data-has-btn'>
              <Button
                auth={AUTH_KEY.PROMPT.INFERENCE_PROMPT_DELETE}
                className='btn-option-outlined'
                style={{ width: '40px' }}
                onClick={() => handleDelete(selectedIds)}
                disabled={gridRowData.length === 0}
              >
                삭제
              </Button>
              <UIPagination
                currentPage={searchValues.page}
                hasNext={data?.hasNext}
                totalPages={data?.totalPages || 1}
                onPageChange={(newPage: number) => updatePageSizeAndRefetch({ page: newPage })}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>

      {/* ──────────────────────── 프롬프트 등록 팝업 ──────────────────────── */}
      <InfPromptCreatePopup
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        onCreateSuccess={() => {
          refetch();
          refetchTags();
        }}
      />

      {/* ──────────────────────── 프롬프트 수정 팝업 ──────────────────────── */}
      <InfPromptEditPopupPage
        currentStep={layerEditPopup.currentStep}
        onNextStep={layerEditPopup.onNextStep}
        onPreviousStep={layerEditPopup.onPreviousStep}
        onClose={() => {
          layerEditPopup.onClose();
          setEditingPromptUuid(null);
        }}
        promptUuid={editingPromptUuid || ''}
        onEditSuccess={() => {
          // 캐시 무효화는 InfPromptEditPopupPage에서 처리
          // 목록 데이터만 즉시 갱신
          refetch();
          refetchTags();
          layerEditPopup.onClose();
          setEditingPromptUuid(null);
        }}
        initialName={editingPromptData?.name || ''}
        initialTemplate='none'
        initialSystemPrompt={editingPromptMsgs?.messages?.find((msg: any) => msg.mtype === 1)?.message || ''}
        initialUserPrompt={editingPromptMsgs?.messages?.find((msg: any) => msg.mtype === 2)?.message || ''}
        initialTags={editingPromptTags?.tags?.map((tag: any) => tag.tag) || []}
        initialRelease={!!editingPromptData?.releaseVersion}
        initialVariables={initialVariables}
      />
    </section>
  );
};
