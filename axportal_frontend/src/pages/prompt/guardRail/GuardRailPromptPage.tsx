import { UIBox, UIButton2, UIDataCnt, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useDeleteGuardRailPrompt, useGetGuardRailPromptList, useGetGuardRailPromptTags } from '@/services/prompt/guardRail';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import { selectedGuardRailPromptAtom } from '@/stores/prompt';
import { dateUtils } from '@/utils/common';
import { useSetAtom } from 'jotai';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { GuardRailPromptCreatePage } from './GuardRailPromptCreatePage';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';

interface SearchValues {
  page: number;
  size: number;
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  publicRange: string;
}

export const GuardRailPromptPage = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [selectedItems, setSelectedItems] = useState<any[]>([]);
  const navigate = useNavigate();

  // 프로젝트 ID (하드코딩)

  // 모달 훅
  const { openAlert, openConfirm, closeAllModals } = useModal();

  // 삭제 관련 에러에서 전역 에러 핸들러 알러트를 막기 위한 이벤트 리스너
  useEffect(() => {
    const handleApiError = (event: Event) => {
      const customEvent = event as CustomEvent;
      const errorData = typeof customEvent.detail === 'object' && customEvent.detail !== null ? customEvent.detail : { message: customEvent.detail };

      // axios 인터셉터에서 설정한 path 정보 확인
      // normalizeError에서 error.config?.url을 path로 설정함
      const errorPath = (errorData as any).path || '';

      // 가드레일 프롬프트 삭제 API 에러인 경우 이벤트 완전히 차단
      // URL 패턴: /guardrails/prompts/{id} (DELETE 메서드)
      if (errorPath.includes('/guardrails/prompts/') && errorPath.match(/\/guardrails\/prompts\/[^/]+$/)) {
        // 이벤트 차단하여 전역 에러 핸들러가 실행되지 않도록 함
        event.preventDefault();
        event.stopImmediatePropagation();
        event.stopPropagation();

        // 플래그 설정하여 onError에서 알러트 표시하도록 함
        (window as any).__guardRailPromptDeleteError = true;

        return false;
      }
    };

    // 이벤트를 capture phase에서 먼저 등록하여 전역 에러 핸들러보다 먼저 실행되도록 함
    window.addEventListener('api-error', handleApiError, true);

    return () => {
      window.removeEventListener('api-error', handleApiError, true);
    };
  }, []);
  const { user } = useUser();
  const isAuthorized = (user?.functionAuthList?.includes('A040304') ?? false) && user?.activeProject?.prjRoleSeq === '-199' && user?.activeProject?.prjSeq === '-999';
  console.log(user.activeProject.prjRoleSeq);

  const setSelectedGuardRailPrompt = useSetAtom(selectedGuardRailPromptAtom);
  const projectId = user.adxpProject.prjUuid;
  // 태그 목록 (드롭다운 열 때만 refetch)
  const { data: tagList, refetch: refetchTags } = useGetGuardRailPromptTags({ enabled: false });

  // 태그 목록을 드롭다운 옵션으로 변환 (데이터 없으면 [] → refetchOnOpen 동작)
  const tagOptions = useMemo(() => {
    if (!tagList || !Array.isArray(tagList)) return [];
    return [{ value: '전체', label: '전체' }, ...tagList.map((tag: string) => ({ value: tag, label: tag }))];
  }, [tagList]);

  // 생성 버튼 클릭 핸들러
  const handleCreateClick = () => {
    if (!isAuthorized) {
      openAlert({
        title: '안내',
        message: '가드레일 프롬프트 생성 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    setIsCreateModalOpen(true);
  };

  // 생성 모달 닫기 핸들러
  const handleCloseCreateModal = () => {
    setIsCreateModalOpen(false);
  };

  // 생성 완료 핸들러
  const handleCreateSubmit = (data: any) => {
    // GuardRailPromptCreatePage에서 직접 API 호출하므로
    // 여기서는 성공 시 목록만 새로고침
    if (data?.success) {
      refetch();
      refetchTags();
    }
  };

  // 검색 조건 (사용자 입력용)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.GUARDRAIL_PROMPT_LIST, {
    page: 1,
    size: 12,
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '전체',
    searchKeyword: '',
    status: '전체',
    publicRange: '전체',
  });

  // 적용된 검색 조건 (실제 API 호출에 사용)
  const [appliedSearchValues, setAppliedSearchValues] = useState<SearchValues>(searchValues);

  // 가드레일 프롬프트 목록 조회
  const {
    data: guardRailData,
    refetch,
    isFetching,
  } = useGetGuardRailPromptList(
    {
      project_id: projectId,
      page: appliedSearchValues.page,
      size: appliedSearchValues.size,
      search: appliedSearchValues.searchKeyword,
      tag: appliedSearchValues.searchType !== '전체' ? appliedSearchValues.searchType : '',
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setAppliedSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues({ [key]: value });
  };

  const handlePageChange = (page: number) => {
    updatePageSizeAndRefetch({ page });
  };

  const handlePageSizeChange = (size: string) => {
    updatePageSizeAndRefetch({ size: Number(size), page: 1 });
  };

  // 검색 핸들러
  const handleSearch = () => {
    setAppliedSearchValues({ ...searchValues, page: 1 });
    setSearchValues({ page: 1 });

    if (env.VITE_NO_PRESSURE_MODE) {
      // NO_PRESSURE_MODE에서는 조회 버튼에서만 조회되도록 refetch를 여기서 수행
      setTimeout(() => refetch(), 0);
    }
  };

  // 삭제 mutation
  const { mutate: deleteGuardRailPrompt } = useDeleteGuardRailPrompt({
    onSuccess: () => {
      // 목록 새로고침
      refetch();
      // 태그 목록도 새로고침 (삭제된 항목의 태그가 더 이상 사용되지 않을 수 있음)
      refetchTags();
    },
    onError: () => {
      // 이벤트 리스너에서 플래그를 설정했는지 확인
      if ((window as any).__guardRailPromptDeleteError) {
        // 전역 에러 핸들러가 이미 모달을 열었을 수 있으므로 모든 모달 닫기
        closeAllModals();

        // 커스텀 알러트 표시
        setTimeout(() => {
          openAlert({
            title: '안내',
            message: '가드레일 프롬프트 삭제에 실패했습니다.',
          });
          // 플래그 정리
          delete (window as any).__guardRailPromptDeleteError;
        }, 0);
      }
    },
  });

  // 단일 삭제 핸들러 (더보기 메뉴)
  const handleDelete = (rowData: any) => {
    if (!isAuthorized) {
      openAlert({
        title: '안내',
        message: '가드레일 프롬프트 삭제 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제된 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteGuardRailPrompt({ id: rowData.uuid });
      },
    });
  };

  // 선택된 항목 일괄 삭제 핸들러 (하단 삭제 버튼)
  const handleBulkDelete = () => {
    if (!isAuthorized) {
      openAlert({
        title: '안내',
        message: '가드레일 프롬프트 삭제 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    if (selectedItems.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: `삭제하시겠어요?\n삭제된 정보는 복구할 수 없습니다.`,
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        // 각 항목에 대해 삭제 API 호출
        let deletedCount = 0;
        const totalCount = selectedItems.length;

        selectedItems.forEach(item => {
          deleteGuardRailPrompt(
            { id: item.uuid },
            {
              onSuccess: () => {
                deletedCount++;
                // 모든 항목 삭제 완료 시
                if (deletedCount === totalCount) {
                  openAlert({
                    title: '완료',
                    message: `가드레일 프롬프트가 삭제되었습니다.`,
                    onConfirm: () => {
                      setSelectedItems([]);
                      refetch();
                      // 태그 목록도 새로고침 (삭제된 항목의 태그가 더 이상 사용되지 않을 수 있음)
                      refetchTags();
                    },
                  });
                }
              },
            }
          );
        });
      },
    });
  };

  // 그리드 row 클릭 핸들러
  const handleRowClick = (event: any) => {
    const rowData = event.data;
    // 조타이에 선택된 row 데이터 저장
    setSelectedGuardRailPrompt(rowData);
    // 상세 페이지로 이동
    navigate('/prompt/guardrail/guardrail-prompt-detail');
  };

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '삭제',
          auth: AUTH_KEY.PROMPT.GUARDRAIL_PROMPT_DELETE,
          action: 'delete',
          onClick: (rowData: any) => {
            handleDelete(rowData);
          },
        },
      ],
      isActive: () => true,
    }),
    [handleDelete]
  );

  // API 데이터를 rowData로 변환
  const rowData = useMemo(() => {
    return (
      guardRailData?.content?.map((item, index) => ({
        ...item,
        no: (appliedSearchValues.page - 1) * appliedSearchValues.size + index + 1,
        createdDate: item.createdAt ? dateUtils.formatDate(new Date(item.createdAt).getTime() + 9 * 60 * 60 * 1000, 'datetime') : '',
        updatedDate: item.updatedAt ? dateUtils.formatDate(new Date(item.updatedAt).getTime() + 9 * 60 * 60 * 1000, 'datetime') : '',
        more: 'more',
      })) || []
    );
  }, [guardRailData, appliedSearchValues.page, appliedSearchValues.size]);

  // 그리드 컬럼 정의
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
        valueGetter: (params: any) => params.data?.no || '',
      },
      {
        headerName: '이름',
        field: 'name',
        flex: 1,
      },
      {
        headerName: '태그',
        field: 'tags',
        width: 230,
        sortable: true,
        comparator: (valueA: any, valueB: any) => {
          // 태그 배열의 0번째 문자열로 비교
          const getFirstTag = (tags: any): string => {
            if (!tags || !Array.isArray(tags) || tags.length === 0) {
              return '';
            }
            const firstTag = tags[0];
            return typeof firstTag === 'string' ? firstTag : firstTag?.tag || '';
          };

          const tagA = getFirstTag(valueA).toLowerCase();
          const tagB = getFirstTag(valueB).toLowerCase();

          if (tagA < tagB) return -1;
          if (tagA > tagB) return 1;
          return 0;
        },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const normalizedTags = params.value.map((tag: any) => (typeof tag === 'string' ? tag : tag?.tag)).filter(Boolean);

          if (normalizedTags.length === 0) {
            return null;
          }

          const maxVisible = 2;
          const visibleTags = normalizedTags.slice(0, maxVisible);
          const titleText = normalizedTags.join(', ');

          return (
            <div
              className='w-full'
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={titleText}
            >
              <div className='flex gap-1 flex-wrap items-center max-w-full'>
                {visibleTags.map((tag: string, index: number) => (
                  <UITextLabel key={`${tag}-${index}`} intent='tag'>
                    {tag}
                  </UITextLabel>
                ))}
                {normalizedTags.length > maxVisible && (
                  <UITypography variant='caption-2' className='secondary-neutral-550'>
                    ...
                  </UITypography>
                )}
              </div>
            </div>
          );
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        minWidth: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      /*
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        minWidth: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueFormatter: (params: any) => {
          const updatedValue = params.value;
          const createdValue = params.data?.createdAt;
          const source = updatedValue ?? createdValue;

          if (!source) return '';

          const date = new Date(source);
          if (Number.isNaN(date.getTime())) return source;

          const targetDate = updatedValue !== undefined && updatedValue !== null ? new Date(date.getTime() + 9 * 60 * 60 * 1000) : date;

          const year = targetDate.getFullYear();
          const month = String(targetDate.getMonth() + 1).padStart(2, '0');
          const day = String(targetDate.getDate()).padStart(2, '0');
          const hours = String(targetDate.getHours()).padStart(2, '0');
          const minutes = String(targetDate.getMinutes()).padStart(2, '0');
          const seconds = String(targetDate.getSeconds()).padStart(2, '0');

          return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
        },
      },
      */
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [rowData]
  );

  return (
    <>
      {/* 가드레일 생성 모달 */}
      {isCreateModalOpen && <GuardRailPromptCreatePage open={isCreateModalOpen} onClose={handleCloseCreateModal} onSubmit={handleCreateSubmit} projectId={projectId} />}

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
                          onChange={e => {
                            setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                          }}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              e.preventDefault();
                              handleSearch();
                            }
                          }}
                          placeholder='이름 입력'
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        태그
                      </UITypography>
                    </th>
                    <td>
                      <div>
                        <UIDropdown
                          value={searchValues.searchType}
                          placeholder='태그 선택'
                          options={tagOptions}
                          refetchOnOpen={refetchTags}
                          onSelect={value => handleDropdownSelect('searchType', value)}
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

      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <UIUnitGroup gap={16} direction='column'>
              <div className='flex justify-between w-full items-center'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={guardRailData?.totalElements || 0} prefix='총' unit='건' />
                  </div>
                </div>
                <div className='flex items-center gap-2'>
                  <Button auth={AUTH_KEY.PROMPT.GUARDRAIL_PROMPT_CREATE} className='btn-tertiary-outline' onClick={handleCreateClick}>
                    가드레일 프롬프트 생성
                  </Button>

                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={`${searchValues.size}개씩 보기`}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={handlePageSizeChange}
                      height={40}
                      variant='dataGroup'
                      disabled={(guardRailData?.totalElements || 0) === 0}
                    />
                  </div>
                </div>
              </div>
            </UIUnitGroup>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='multi-select'
              rowData={rowData}
              loading={isFetching}
              columnDefs={columnDefs}
              moreMenuConfig={moreMenuConfig}
              onClickRow={handleRowClick}
              onCheck={(selectedIds: any[]) => {
                setSelectedItems(selectedIds);
              }}
            />
          </UIListContentBox.Body>
          {/* [참고] classname 관련
              - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
              - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
            */}
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <Button
              auth={AUTH_KEY.PROMPT.GUARDRAIL_PROMPT_DELETE}
              className='btn-option-outlined'
              style={{ width: '40px' }}
              onClick={handleBulkDelete}
              disabled={(guardRailData?.totalElements || 0) === 0}
            >
              삭제
            </Button>
            <UIPagination
              currentPage={appliedSearchValues.page}
              hasNext={guardRailData?.hasNext}
              totalPages={guardRailData?.totalPages || 1}
              onPageChange={handlePageChange}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
};
