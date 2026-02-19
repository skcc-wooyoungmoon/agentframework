import React, { useEffect, useMemo, useState } from 'react';

import { useLocation, useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIToggle } from '@/components/UI';
import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { WorkFlowCreatePopupPage, WorkFlowEditPopupPage } from '@/pages/prompt';
import { useDeleteWorkFlowsByIds, useGetWorkFlowList, useGetWorkFlowTags } from '@/services/prompt/workFlow/workFlow.services';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  selectedTag: string;
  view: string;
}

export const WorkFlowListPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const layerPopupOne = useLayerPopup(); // 생성 팝업용
  const layerPopupTwo = useLayerPopup(); // 수정 팝업용
  const { openAlert, openConfirm } = useModal();

  // 수정할 워크플로우 정보
  const [editingWorkflow, setEditingWorkflow] = useState<{
    workflowId: string;
    workflowName: string;
    xmlText: string;
    tags: string[];
  } | null>(null);

  // 검색 조건 (실제 API 요청용)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.WORKFLOW_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    selectedTag: '',
    view: 'grid',
  });

  // 임시 검색 조건 (입력 중인 값)
  const [tempSearchKeyword, setTempSearchKeyword] = useState(searchValues.searchKeyword);
  const [tempSelectedTag, setTempSelectedTag] = useState(searchValues.selectedTag);

  const [sort] = useState<string | undefined>('created_at,desc');
  const { user } = useUser();
  const { data, refetch, isLoading } = useGetWorkFlowList({
    project_id: user.adxpProject.prjUuid,
    page: searchValues.page,
    size: searchValues.size,
    sort,
    search: searchValues.searchKeyword.trim(),
    tag: searchValues.selectedTag !== '' ? searchValues.selectedTag : '',
  });

  // 태그 목록 (드롭다운 열 때만 refetch)
  const { data: tagList, refetch: refetchTags } = useGetWorkFlowTags(
    { project_id: user.adxpProject.prjUuid },
    { enabled: false }
  );

  // 태그 옵션 (데이터 없으면 [] → refetchOnOpen 동작)
  const tagOptions = useMemo(() => {
    if (!tagList?.length) return [];
    return [{ value: '', label: '전체' }, ...tagList.map((tag: string) => ({ value: tag, label: tag }))];
  }, [tagList]);

  // 조회 버튼 클릭 핸들러 - 임시 값을 실제 검색 조건에 반영
  const handleSearch = () => {
    setSearchValues({
      page: 1,
      searchKeyword: tempSearchKeyword,
      selectedTag: tempSelectedTag,
    });

    refetch();
  };

  // 실제 검색 조건이 변경되면 임시 값도 동기화 (뒤로가기 등)
  useEffect(() => {
    setTempSearchKeyword(searchValues.searchKeyword);
    setTempSelectedTag(searchValues.selectedTag);
  }, [searchValues.searchKeyword, searchValues.selectedTag]);

  useEffect(() => {
    refetch();
  }, [searchValues.page, searchValues.size, searchValues.searchKeyword, searchValues.selectedTag]);

  // 삭제 후 페이지 이동 시 자동 새로고침
  useEffect(() => {
    const state = location.state as { shouldRefresh?: boolean } | null;
    if (state?.shouldRefresh) {
      refetch(); // 현재 requestParams로 다시 조회
      refetchTags(); // 태그 목록도 새로고침
      // state 초기화 (뒤로가기 시 중복 실행 방지)
      window.history.replaceState({}, document.title);
    }
  }, [location.state, refetch, refetchTags]);

  /**
   *  워크플로우 일괄 삭제
   */
  const deleteWorkFlowsMutation = useDeleteWorkFlowsByIds();

  /**
   * 데이터 삭제
   */
  const handleDelete = () => {
    const idsToDelete = searchValues.view === 'grid' ? selectedIds : selectedCardIds;

    // console.log('handleDelete - idsToDelete 원본:', idsToDelete);
    // console.log('handleDelete - idsToDelete 타입:', typeof idsToDelete[0]);

    if (idsToDelete.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 워크플로우를 선택해주세요.',
      });
      return;
    }

    // 공개범위 체크: private 사용자가 public 워크플로우를 삭제하려는지 확인
    const isUserCurrentGroupPrivate = Number(user.activeProject.prjSeq) !== -999;

    if (isUserCurrentGroupPrivate) {
      // 선택된 워크플로우 중 public 워크플로우가 있는지 확인
      const selectedWorkflows = data?.content?.filter(item => idsToDelete.includes(String(item.workflowId)));
      const hasPublicWorkflow = selectedWorkflows?.some(workflow => Number(workflow.projectSeq) === -999);

      if (hasPublicWorkflow) {
        openAlert({
          title: '안내',
          message: '워크플로우 삭제 권한이 없습니다.',
          confirmText: '확인',
        });
        return;
      }
    }

    openConfirm({
      title: '삭제 확인',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      onConfirm: async () => {
        try {
          // ID를 문자열로 확실히 변환
          const stringIds = idsToDelete.map(id => {
            // 객체인 경우 id 속성 추출
            if (typeof id === 'object' && id !== null) {
              return String((id as any).id || (id as any).workflowId || id);
            }
            return String(id);
          });
          // console.log('삭제할 IDs (변환 후):', stringIds);

          // 일괄 삭제 API 호출 (배열로 전달)
          const result = await deleteWorkFlowsMutation.mutateAsync({ ids: stringIds });

          // 결과 메시지 표시
          if (result.data.successCount > 0) {
            openAlert({
              title: '완료',
              message: '워크플로우가 삭제되었습니다.',
            });

            // 목록 새로고침 및 선택 항목 초기화
            refetch();
            refetchTags(); // 태그 목록도 새로고침
            setSelectedIds([]);
            setSelectedCardIds([]);
          } else {
            openAlert({
              title: '오류',
              message: '워크플로우 삭제에 실패했습니다.',
            });
          }
        } catch (error) {
          // console.error('워크플로우 삭제 오류:', error);
          openAlert({
            title: '오류',
            message: '워크플로우 삭제 중 오류가 발생했습니다.',
          });
        }
      },
    });
  };

  /**
   * 워크플로우 생성 팝업 호출
   */
  const handleWorkFlowCreatePopup = () => {
    layerPopupOne.onOpen();
  };

  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'edit',
          auth: AUTH_KEY.PROMPT.WORKFLOW_UPDATE,
          onClick: (rowData: any) => {
            const workFlowRowData = data?.content?.find(item => item.workflowId === rowData.workflowId);

            // 수정 팝업 열기
            setEditingWorkflow({
              workflowId: String(workFlowRowData?.workflowId || rowData.id),
              workflowName: workFlowRowData?.workflowName || rowData.name || '',
              xmlText: workFlowRowData?.xmlText || '',
              tags: workFlowRowData?.tags || [],
            });
            layerPopupTwo.onOpen();
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.PROMPT.WORKFLOW_DELETE,
          onClick: (rowData: any) => {
            const workFlowRowData = data?.content?.find(item => item.workflowId === rowData.workflowId);
            const workflowId = String(rowData.workflowId || rowData.id);
            const isUserCurrentGroupPrivate = Number(user.activeProject.prjSeq) !== -999;
            const isWorkflowPublic = Number(workFlowRowData?.projectSeq) === -999;

            if (!user.functionAuthList.includes('A040402') || (isUserCurrentGroupPrivate && isWorkflowPublic)) {
              openAlert({
                title: '안내',
                message: '워크플로우 삭제 권한이 없습니다.',
                confirmText: '확인',
              });

              return;
            }

            openConfirm({
              title: '삭제 확인',
              message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
              onConfirm: async () => {
                try {
                  const result = await deleteWorkFlowsMutation.mutateAsync({ ids: [workflowId] });
                  if (result.data.successCount > 0) {
                    openAlert({
                      title: '완료',
                      message: '워크플로우가 삭제되었습니다.',
                    });
                    refetch();
                    refetchTags(); // 태그 목록도 새로고침
                    setSelectedIds([]);
                    setSelectedCardIds([]);
                  } else {
                    openAlert({
                      title: '오류',
                      message: '워크플로우 삭제에 실패했습니다.',
                    });
                  }
                } catch (error) {
                  // console.error('워크플로우 삭제 오류:', error);
                  openAlert({
                    title: '오류',
                    message: '워크플로우 삭제 중 오류가 발생했습니다.',
                  });
                }
              },
            });
          },
        },
      ],
      isActive: () => true,
    }),
    [navigate, openConfirm, openAlert, deleteWorkFlowsMutation, refetch, setSelectedIds, setSelectedCardIds, layerPopupTwo]
  );

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
      },
      {
        headerName: '이름',
        field: 'workflowName' as any,
        minWidth: 301,
        flex: 1,
      },
      {
        headerName: '공개범위',
        field: 'publicRange' as any,
        width: 301,
      },
      {
        headerName: '버전',
        field: 'versionNo' as any,
        width: 301,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          return params.value ? (
            <div className='flex items-center gap-1'>
              <UITextLabel intent='gray'>Latest Ver.{params.value}</UITextLabel>
            </div>
          ) : null;
        }),
      },
      {
        headerName: '태그',
        field: 'tags' as any,
        width: 301,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const tagValue = params.value;

          // 배열인 경우
          if (Array.isArray(tagValue)) {
            return (
              <div className='flex items-center gap-1'>
                {tagValue.map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            );
          }

          // 문자열인 경우
          if (typeof tagValue === 'string' && tagValue.includes(',')) {
            const tags = tagValue.split(',').map((tag: string) => tag.trim());
            return (
              <div className='flex items-center gap-1'>
                {tags.map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            );
          }
          // 단일 태그인 경우
          return <UITextLabel intent='tag'>{tagValue}</UITextLabel>;
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          return params.value ? dateUtils.formatDate(params.value, 'datetime') : '-';
        }),
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    []
  );

  // 그리드용 rowData 변환
  const rowData = useMemo(() => {
    if (!data?.content) return [];
    return data.content.map(
      (
        item: {
          workflowId: string;
          workflowName: string;
          versionNo: number;
          projectSeq: string;
          tags?: string[];
          tagsRaw?: string;
          isActive: string;
          updatedAt?: string | null;
          createdAt: string;
          createdBy?: string;
        },
        index: number
      ) => {
        // 태그 파싱
        let parsedTags: string[] = [];
        if (item.tags && Array.isArray(item.tags) && item.tags.length > 0) {
          parsedTags = item.tags;
        } else if (item.tagsRaw) {
          parsedTags = item.tagsRaw.split(',').filter((tag: string) => tag.trim());
        }

        return {
          no: (searchValues.page - 1) * searchValues.size + index + 1,
          id: String(item.workflowId),
          workflowId: String(item.workflowId),
          workflowName: item.workflowName || '이름 없음',
          publicRange: Number(item.projectSeq) === -999 ? '전체공유' : '내부공유',
          versionNo: item.versionNo,
          tags: parsedTags,
          createdAt: item.createdAt,
          updatedAt: item.updatedAt,
          isActive: item.isActive,
          createdBy: item.createdBy,
          more: 'more',
        };
      }
    );
  }, [data?.content, searchValues.page, searchValues.size]);

  // 카드용 rowData 변환
  const rowCardData = useMemo(() => {
    if (!data?.content) return [];
    return data.content.map(
      (
        item: {
          workflowId: string;
          workflowName: string;
          versionNo: number;
          tags?: string[];
          tagsRaw?: string;
          isActive: string;
          updatedAt?: string | null;
          createdAt: string;
          createdBy?: string;
        },
        index: number
      ) => {
        // 태그 파싱
        let parsedTags: string[] = [];
        if (item.tags && Array.isArray(item.tags) && item.tags.length > 0) {
          parsedTags = item.tags;
        } else if (item.tagsRaw) {
          parsedTags = item.tagsRaw.split(',').filter((tag: string) => tag.trim());
        }

        const tagName = parsedTags.length > 0 ? parsedTags.join(', ') : '';

        return {
          no: (searchValues.page - 1) * searchValues.size + index + 1,
          id: String(item.workflowId),
          workflowId: String(item.workflowId),
          modelName: item.workflowName || '이름 없음',
          description: '',
          tagName: tagName,
          permission: 'Public', // 퍼블 구조에 맞춰 추가 (API 데이터에 따라 수정 필요)
          creationDate: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '-',
          statusLabels: item.versionNo ? [{ text: `Latest Ver.${item.versionNo}`, intent: 'gray' }] : [],
          more: 'more',
          isActive: item.isActive === 'Y',
        };
      }
    );
  }, [data?.content, searchValues.page, searchValues.size]);

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='워크플로우'
          description={['업무 절차에 기반한 워크플로우(XML)를 등록할 수 있습니다.', '등록한 워크플로우를 추론 프롬프트에서 불러와 프롬프트를 더욱 완성도 있게 보완해 보세요.']}
          actions={
            <Button
              auth={AUTH_KEY.PROMPT.WORKFLOW_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleWorkFlowCreatePopup}
            >
              워크플로우 등록
            </Button>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
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
                          <div>
                            <UIInput.Search
                              value={tempSearchKeyword}
                              placeholder='검색어 입력'
                              onChange={e => {
                                setTempSearchKeyword(e.target.value);
                              }}
                              onKeyDown={e => {
                                if (e.key === 'Enter') {
                                  handleSearch();
                                }
                              }}
                            />
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            태그
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={tempSelectedTag || ''}
                            placeholder='전체'
                            options={tagOptions}
                            refetchOnOpen={refetchTags}
                            onSelect={value => setTempSelectedTag(value)}
                          />
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
                        <UIDataCnt count={data?.totalElements || 0} prefix='총' />
                      </div>
                    </div>
                    <div className='flex' style={{ gap: '12px' }}>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(searchValues.size)}
                          options={[
                            { value: '12', label: '12개씩 보기' },
                            { value: '36', label: '36개씩 보기' },
                            { value: '60', label: '60개씩 보기' },
                          ]}
                          onSelect={(value: string) => {
                            setSearchValues(prev => ({ ...prev, size: Number(value), page: 1 }));
                          }}
                          height={40}
                          variant='dataGroup'
                        />
                      </div>
                      <div className='h-[40px]' style={{ flexShrink: 0 }}>
                        <UIToggle
                          variant='dataView'
                          checked={searchValues.view === 'card'}
                          onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                        />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.view === 'grid' ? (
                  <UIGrid
                    type='multi-select'
                    rowData={rowData}
                    columnDefs={columnDefs}
                    loading={isLoading}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(params: any) => {
                      navigate(`/prompt/workflow/${params.data.workflowId}`);
                    }}
                    onCheck={(selectedRows: any[]) => {
                      // 선택된 row에서 id만 추출
                      const ids = selectedRows.map(row => String(row.id || row.workflowId));
                      // console.log('Grid 선택된 IDs:', ids);
                      setSelectedIds(ids);
                    }}
                  />
                ) : (
                  <UICardList
                    rowData={rowCardData}
                    flexType='none'
                    loading={isLoading}
                    card={(item: any) => {
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.modelName}
                          caption={item.description}
                          data={item}
                          moreMenuConfig={moreMenuConfig}
                          onClick={() => {
                            navigate(`/prompt/workflow/${item.workflowId}`);
                          }}
                          statusArea={
                            <UIGroup gap={8} direction='row'>
                              {item.statusLabels?.map((label: { text: string; intent: string }, index: number) => (
                                <UITextLabel key={index} intent={label.intent as any}>
                                  {label.text}
                                </UITextLabel>
                              ))}
                            </UIGroup>
                          }
                          checkbox={{
                            checked: selectedCardIds.includes(String(item.id)),
                            onChange: (checked: boolean) => {
                              const stringId = String(item.id);
                              if (checked) {
                                setSelectedCardIds([...selectedCardIds, stringId]);
                              } else {
                                setSelectedCardIds(selectedCardIds.filter(id => id !== stringId));
                              }
                            },
                          }}
                          rows={[
                            { label: '태그', value: item.tagName },
                            { label: '생성일시', value: item.creationDate },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button auth={AUTH_KEY.PROMPT.WORKFLOW_DELETE} className='btn-option-outlined' style={{ width: '40px' }} onClick={handleDelete}>
                  삭제
                </Button>
                <UIPagination
                  currentPage={searchValues.page}
                  totalPages={data?.totalPages || 0}
                  onPageChange={(newPage: number) => {
                    setSearchValues(prev => ({ ...prev, page: newPage }));
                  }}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>

      {/* 워크플로우 생성 팝업 */}
      <WorkFlowCreatePopupPage
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        onCreateSuccess={() => {
          refetch(); // 목록 새로고침
          refetchTags(); // 태그 목록도 새로고침
        }}
      />

      {/* 워크플로우 수정 팝업 */}
      {editingWorkflow && (
        <WorkFlowEditPopupPage
          currentStep={layerPopupTwo.currentStep}
          onNextStep={layerPopupTwo.onNextStep}
          onPreviousStep={layerPopupTwo.onPreviousStep}
          onClose={() => {
            layerPopupTwo.onClose();
            setEditingWorkflow(null); // 수정 데이터 초기화
          }}
          workFlowId={editingWorkflow.workflowId}
          initialName={editingWorkflow.workflowName}
          initialXmlText={editingWorkflow.xmlText}
          initialTags={editingWorkflow.tags}
          onEditSuccess={() => {
            refetch(); // 목록 새로고침
            refetchTags(); // 태그 목록도 새로고침
            setEditingWorkflow(null); // 수정 데이터 초기화
          }}
        />
      )}
    </>
  );
};
