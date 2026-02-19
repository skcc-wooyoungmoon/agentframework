import React, { memo, useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIToggle } from '@/components/UI';
import { UIBox, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIDropdown, UIGridCard, UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import type { QnaPair } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import {
  useDeleteFewShotById,
  useGetFewShotById,
  useGetFewShotItemListById,
  useGetFewShotList,
  useGetFewShotTagList,
  useGetFewShotTagsByVerId,
  useGetLtstFewShotVerById,
} from '@/services/prompt/fewshot/fewShotPrompts.services';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { FewShotCreatePopupPage, FewShotEditPopupPage } from './';

interface SearchValues {
  page: number;
  size: number;
  filter: string;
  searchKeyword: string;
  view: string;
  selectedTag: string;
}

export function FewShotListPage() {
  const { openAlert, openConfirm } = useModal();

  const navigate = useNavigate();
  const layerCreatePopup = useLayerPopup();
  const layerEditPopup = useLayerPopup();

  // 편집할 FewShot 정보
  const [editingFewShotUuid, setEditingFewShotUuid] = useState<string | null>(null);

  // 편집할 FewShot 데이터 조회
  const {
    data: editingFewShotData,
    refetch: refetchEditingFewShotData,
    isLoading: isLoadingEditingFewShotData,
  } = useGetFewShotById(
    { uuid: editingFewShotUuid || '' },
    {
      enabled: !!editingFewShotUuid,
    }
  );

  // 편집할 FewShot의 최신 버전 조회
  const {
    data: editingLatestVersion,
    refetch: refetchEditingLatestVersion,
    isLoading: isLoadingEditingLatestVersion,
  } = useGetLtstFewShotVerById(
    { uuid: editingFewShotUuid || '' },
    {
      enabled: !!editingFewShotUuid,
    }
  );

  // 편집할 FewShot의 아이템 목록 조회
  const {
    data: editingFewShotItemList,
    refetch: refetchEditingFewShotItemList,
    isLoading: isLoadingEditingFewShotItemList,
  } = useGetFewShotItemListById(
    { verId: editingLatestVersion?.versionId || '' },
    {
      enabled: !!editingLatestVersion?.versionId,
    }
  );

  // 편집할 FewShot의 태그 조회
  const {
    data: editingFewShotTags,
    refetch: refetchEditingFewShotTags,
    isLoading: isLoadingEditingFewShotTags,
  } = useGetFewShotTagsByVerId(
    { verId: editingLatestVersion?.versionId || '' },
    {
      enabled: !!editingLatestVersion?.versionId,
    }
  );

  // 편집 팝업이 열릴 때 데이터 다시 조회
  useEffect(() => {
    if (layerEditPopup.currentStep > 0 && editingFewShotUuid) {
      // 데이터 다시 조회 (캐시된 데이터를 무시하고 최신 데이터 가져오기)
      refetchEditingFewShotData();
      refetchEditingLatestVersion();
    }
  }, [layerEditPopup.currentStep, editingFewShotUuid]);

  // 최신 버전이 변경되면 아이템 목록과 태그 다시 조회
  useEffect(() => {
    if (editingLatestVersion?.versionId) {
      refetchEditingFewShotItemList();
      refetchEditingFewShotTags();
    }
  }, [editingLatestVersion?.versionId]);

  // 데이터 로딩 상태 확인
  const isEditingDataLoading = isLoadingEditingFewShotData || isLoadingEditingLatestVersion || isLoadingEditingFewShotItemList || isLoadingEditingFewShotTags;

  // 편집할 FewShot의 Q&A 쌍 변환
  const editingQnaPairs = useMemo<QnaPair[]>(() => {
    if (!editingFewShotItemList || !Array.isArray(editingFewShotItemList)) {
      return [];
    }
    const newQaPairs: QnaPair[] = [];
    for (let i = 0; i < editingFewShotItemList.length; i += 2) {
      const questionItem = editingFewShotItemList[i];
      const answerItem = editingFewShotItemList[i + 1];

      if (questionItem && answerItem) {
        newQaPairs.push({
          id: `qna-${i}`,
          question: questionItem.item || '',
          answer: answerItem.item || '',
          questionError: false,
          answerError: false,
        });
      }
    }
    return newQaPairs;
  }, [editingFewShotItemList]);

  // 편집할 FewShot의 태그 변환
  const editingTags = useMemo<string[]>(() => {
    if (!editingFewShotTags || !Array.isArray(editingFewShotTags)) {
      return [];
    }
    return editingFewShotTags.map(tag => tag.tag).filter(Boolean);
  }, [editingFewShotTags]);

  // 검색 조건 (입력용)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.FEW_SHOT_LIST, {
    page: 1,
    size: 12,
    filter: '',
    searchKeyword: '',
    view: 'grid',
    selectedTag: '',
  });

  // 실제 검색에 사용할 값 (조회 버튼 클릭 시 업데이트)
  const [appliedSearchValues, setAppliedSearchValues] = useState<SearchValues>({
    page: searchValues.page,
    size: searchValues.size,
    filter: searchValues.filter,
    searchKeyword: searchValues.searchKeyword,
    view: searchValues.view,
    selectedTag: searchValues.selectedTag,
  });

  // 체크박스 상태 관리
  const [selectedDataList, setSelectedDataList] = useState<any[]>([]);
  const handleSelect = useCallback((datas: any[]) => {
    setSelectedDataList(datas);
  }, []);

  // selectedTag 변경 시 filter 동기화 (입력용 상태만)
  useEffect(() => {
    setSearchValues(prev => ({
      ...prev,
      filter: prev.selectedTag !== '' ? `tags:${prev.selectedTag}` : '',
    }));
  }, [searchValues.selectedTag]);

  // 태그 목록 (드롭다운 열 때만 refetch)
  const { data: tagList, refetch: refetchTags } = useGetFewShotTagList({ enabled: false });
  const tagOptions = useMemo(() => {
    if (!tagList?.tags) return [];
    return [{ value: '', label: '전체' }, ...tagList.tags.map((tag: string) => ({ value: tag, label: tag }))];
  }, [tagList]);

  // 목록 데이터 조회 (appliedSearchValues 사용)
  const { data, isSuccess, refetch, isLoading } = useGetFewShotList(
    {
      page: appliedSearchValues.page,
      size: appliedSearchValues.size,
      sort: 'created_at,desc',
      projectId: '', // TODO : projectId 추가
      filter: appliedSearchValues.filter,
      search: appliedSearchValues.searchKeyword,
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

  const [dataList, setDataList] = useState<any[]>([]);

  useEffect(() => {
    if (isSuccess && data) {
      setDataList(data.content || []);
    }
  }, [data, isSuccess]);

  // 그리드 컬럼 정의 (디자인 유지)
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        valueGetter: (params: any) => (appliedSearchValues.page - 1) * appliedSearchValues.size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name' as any,
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
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '버전',
        field: 'version' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
          const releaseVersion = params?.data?.releaseVersion;
          const latestVersion = params?.data?.latestVersion;
          return (
            <div className='flex items-center gap-[8px]'>
              {!!releaseVersion && <UITextLabel intent='blue'>Release Ver.{releaseVersion}</UITextLabel>}
              {!!latestVersion && <UITextLabel intent='gray'>Latest Ver.{latestVersion}</UITextLabel>}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '태그',
        field: 'tags' as any,
        width: 230,
        cellRenderer: React.memo((params: any) => {
          const tagValue = params.value;

          // tagValue가 배열이 아닌 경우 처리
          if (!tagValue || tagValue === '' || tagValue.length === 0) {
            return null;
          }
          // 배열인 경우 (여러 태그)
          if (Array.isArray(tagValue)) {
            return (
              <div className='flex items-center gap-[2px] flex-wrap'>
                {tagValue.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {tag}
                  </UITextLabel>
                ))}
                {/* 2개 초과 시 ... 표시 */}
                {tagValue.length > 2 && (
                  <UITypography variant='caption-2' className='secondary-neutral-550'>
                    {'...'}
                  </UITypography>
                )}
              </div>
            );
          }

          return null;
        }),
      },
      {
        headerName: '연결 에이전트',
        field: 'connectedAgentCount' as const,
        width: 230,
        valueGetter: (params: any) => {
          return params.data.connectedAgentCount || 0;
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.createdAt, 'datetime');
        },
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
      },
    ],
    [appliedSearchValues.page, appliedSearchValues.size]
  );

  /**
   * 퓨샷 삭제
   */
  const { mutate: deleteFewShot } = useDeleteFewShotById({
    onSuccess: () => { },
    onError: () => { },
  });

  /**
   * 데이터 삭제
   */
  const handleDeleteConfirm = async (ids: string[]) => {
    if (ids.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    } else {
      openConfirm({
        title: '안내',
        message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          handleDelete(ids);
        },
        onCancel: () => { },
      });
    }
  };

  /**
   * 데이터 삭제
   */
  const handleDelete = async (ids: string[]) => {
    let successCount = 0;
    let failCount = 0;

    // 순차적으로 삭제 처리
    for (const id of ids) {
      try {
        await new Promise<void>((resolve, reject) => {
          deleteFewShot(
            { uuid: id },
            {
              onSuccess: () => {
                successCount++;
                resolve();
              },
              onError: () => {
                failCount++;
                reject();
              },
            }
          );
        });
      } catch (error) {
        // console.error(`퓨샷 ${id} 삭제 중 오류:`, error);
        // 개별 삭제 실패는 계속 진행
      }
    }

    if (ids.length === 1) {
      // 단건 삭제
      if (successCount > 0) {
        openAlert({
          title: '완료',
          message: '퓨샷이 삭제되었습니다.',
        });
      }
    } else {
      // 다건 삭제
      if (failCount == 0) {
        openAlert({
          title: '완료',
          message: '퓨샷 삭제가 완료되었습니다.',
        });
      } else {
        openAlert({
          title: '안내',
          message: `퓨샷 삭제가 완료되었습니다.\n${successCount}건 성공, ${failCount}건 실패\n\n실패한 항목은 확인 후 다시 시도해주세요.`,
        });
      }
    }

    // 성공적으로 삭제된 경우에만 목록 새로고침
    if (successCount > 0) {
      // 체크박스 상태 초기화
      setSelectedDataList([]);
      refetch();
      refetchTags();
    }
  };

  // 총 페이지 (API 기준)
  const totalPages = isSuccess ? data?.totalPages || 1 : 1;

  const handlePageChange = (newPage: number) => {
    updatePageSizeAndRefetch({ page: newPage });
  };

  // 조회 버튼
  const handleSearch = () => {
    const newFilter = searchValues.selectedTag ? `tags:${searchValues.selectedTag}` : '';
    setAppliedSearchValues({
      page: 1,
      size: searchValues.size,
      filter: newFilter,
      searchKeyword: searchValues.searchKeyword,
      view: searchValues.view,
      selectedTag: searchValues.selectedTag,
    });
    setSearchValues(prev => ({
      ...prev,
      page: 1,
      filter: newFilter,
    }));

    if (env.VITE_NO_PRESSURE_MODE) {
      // NO_PRESSURE_MODE에서는 조회 버튼에서만 조회되도록 refetch를 여기서 수행
      setTimeout(() => refetch(), 0);
    }
  };

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.PROMPT.FEW_SHOT_UPDATE,
          onClick: (rowData: any) => {
            handleEditClick(rowData.uuid);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.PROMPT.FEW_SHOT_DELETE,
          onClick: (rowData: any) => {
            handleDeleteConfirm([rowData.uuid]);
          },
        },
      ],
      isActive: () => true, // 모든 퓨샷에 대해 활성화
    }),
    []
  );

  /**
   * 편집 팝업 열기
   */
  const handleEditClick = (fewShotUuid: string) => {
    setEditingFewShotUuid(fewShotUuid);
    layerEditPopup.onOpen();
  };

  /**
   * 상세 페이지 이동
   */
  const handleDetailClick = (uuid: string) => {
    navigate(`${uuid}`);
  };

  /**
   * 퓨샷 생성 팝업 호출
   */
  const handleFewshotCreatePopup = () => {
    layerCreatePopup.onOpen();
  };

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='퓨샷'
          description={[
            '생성형 AI 모델이 사용자가 작성한 Q&A를 참고해 답변할 수 있도록 Q&A세트(퓨샷)를 등록할 수 있습니다.',
            '퓨샷을 등록하고 활용해 생성형 AI 모델이 더 풍부한 답변을 생성할 수 있도록 해보세요.',
          ]}
          actions={
            <Button
              auth={AUTH_KEY.PROMPT.FEW_SHOT_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleFewshotCreatePopup}
            >
              퓨샷 등록
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
                              value={searchValues.searchKeyword}
                              placeholder='검색어 입력'
                              maxLength={50}
                              onChange={e => {
                                setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value.slice(0, 50) }));
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
                            value={searchValues.selectedTag}
                            placeholder='전체'
                            options={tagOptions}
                            refetchOnOpen={refetchTags}
                            onSelect={value => setSearchValues(prev => ({ ...prev, selectedTag: value }))}
                          />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <Button className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                    조회
                  </Button>
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
                        <UIDataCnt count={data?.totalElements ?? 0} prefix='총' unit='건' />
                      </div>
                    </div>
                    <div className='flex' style={{ gap: '12px' }}>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(searchValues.size)}
                          disabled={(data?.totalElements ?? 0) === 0}
                          options={[
                            { value: '12', label: '12개씩 보기' },
                            { value: '36', label: '36개씩 보기' },
                            { value: '60', label: '60개씩 보기' },
                          ]}
                          onSelect={(value: string) => updatePageSizeAndRefetch({ size: Number(value), page: 1 })}
                          height={40}
                          variant='dataGroup'
                        />
                      </div>
                      <div style={{ flexShrink: 0 }}>
                        <UIToggle
                          variant='dataView'
                          checked={searchValues.view === 'card'}
                          disabled={(data?.totalElements ?? 0) === 0}
                          onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                        />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.view === 'grid' ? (
                  <UIGrid<any>
                    type='multi-select'
                    loading={isLoading}
                    selectedDataList={selectedDataList}
                    rowData={dataList}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(params: any) => {
                      handleDetailClick(params.data.uuid);
                    }}
                    onCheck={handleSelect}
                  />
                ) : (
                  <UICardList
                    rowData={dataList}
                    flexType='none'
                    loading={isLoading}
                    card={(item: any) => (
                      <UIGridCard<any>
                        id={item.uuid}
                        title={item.name}
                        moreMenuConfig={moreMenuConfig}
                        data={item}
                        checkbox={{
                          checked: selectedDataList.some(data => data.uuid === item.uuid),
                          onChange: (checked: boolean) => {
                            if (checked) {
                              setSelectedDataList([...selectedDataList, item]);
                            } else {
                              setSelectedDataList(selectedDataList.filter(data => data.uuid !== item.uuid));
                            }
                          },
                        }}
                        onClick={() => handleDetailClick(item.uuid)}
                        statusArea={
                          <div className='flex gap-1 flex-wrap'>
                            {!!item.releaseVersion && <UITextLabel intent='blue'>Release Ver.{item.releaseVersion}</UITextLabel>}
                            {!!item.latestVersion && <UITextLabel intent='gray'>Latest Ver.{item.latestVersion}</UITextLabel>}
                          </div>
                        }
                        rows={[
                          { label: '태그', value: (item.tags || []).join(', ') },
                          { label: '연결 에이전트', value: String(item.connectedAgentCount) },
                          { label: '생성일시', value: dateUtils.formatDate(item.createdAt, 'datetime') },
                        ]}
                      />
                    )}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.PROMPT.FEW_SHOT_DELETE}
                  className='btn-option-outlined'
                  style={{ width: '40px' }}
                  disabled={(data?.totalElements ?? 0) === 0}
                  onClick={() => {
                    handleDeleteConfirm(selectedDataList.map(item => item.uuid));
                  }}
                >
                  삭제
                </Button>
                <UIPagination currentPage={appliedSearchValues.page} totalPages={totalPages} onPageChange={handlePageChange} hasNext={data?.hasNext} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
      <FewShotCreatePopupPage
        currentStep={layerCreatePopup.currentStep}
        onNextStep={layerCreatePopup.onNextStep}
        onPreviousStep={layerCreatePopup.onPreviousStep}
        onClose={layerCreatePopup.onClose}
        onCreateSuccess={() => {
          layerCreatePopup.onClose();
          refetch();
          refetchTags();
        }}
      />
      <FewShotEditPopupPage
        currentStep={layerEditPopup.currentStep}
        onNextStep={layerEditPopup.onNextStep}
        onPreviousStep={layerEditPopup.onPreviousStep}
        onClose={layerEditPopup.onClose}
        fewShotUuid={editingFewShotUuid || ''}
        fewShotName={isEditingDataLoading ? '' : editingFewShotData?.name || ''}
        items={isEditingDataLoading ? [] : editingQnaPairs}
        tags={isEditingDataLoading ? [] : editingTags}
        onUpdateSuccess={() => {
          // 업데이트 성공 시 관련 쿼리 무효화
          if (editingFewShotUuid) {
            refetchEditingFewShotData();
            refetchEditingLatestVersion();
            refetchEditingFewShotItemList();
            refetchEditingFewShotTags();
          }
          layerEditPopup.onClose();
          refetch();
          refetchTags();
        }}
      />
    </>
  );
}
