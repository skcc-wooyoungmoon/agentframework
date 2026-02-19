import React, { useEffect, useMemo, useRef, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetGuardRailPromptList, useGetGuardRailPromptTags } from '@/services/prompt/guardRail/guardRail.services';
import { dateUtils } from '@/utils/common';
import { useUser } from '@/stores';

interface GuardRailPromptPickPopupProps {
  onConfirm?: (selectedPrompt: { id: string; name: string }) => void;
}

const PAGE_SIZE = 6;

/**
 * 프롬프트 > 가드레일 > (TAB) 가드레일 관리 > 가드레일 생성 팝업1 (프롬프트 선택)
 */
export const GuardRailPromptPickPopup: React.FC<GuardRailPromptPickPopupProps> = ({ onConfirm }) => {
  // onConfirm의 최신 버전을 ref로 관리
  const onConfirmRef = useRef(onConfirm);

  const { user } = useUser();

  const [searchValue, setSearchValue] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const [currentPage, setCurrentPage] = useState(1);

  const [selectedTag, setSelectedTag] = useState<string>('전체');
  const [manuallySelectedId, setManuallySelectedId] = useState<string | null>(null);
  const [isTagDropdownOpen, setIsTagDropdownOpen] = useState(false);

  useEffect(() => {
    onConfirmRef.current = onConfirm;
  }, [onConfirm]);

  // API 호출 - 자동 조회 모드
  const { data, isFetching, refetch } = useGetGuardRailPromptList({
    project_id: user.activeProject.prjUuid,
    page: currentPage,
    size: PAGE_SIZE,
    search: searchQuery,
    tag: selectedTag !== '전체' ? selectedTag : '',
  });

  // 태그 목록 조회
  const { data: tagsData } = useGetGuardRailPromptTags();

  // 태그, 페이지, 검색어 변경 시 재조회
  useEffect(() => {
    refetch();
  }, [selectedTag, currentPage, searchQuery, refetch]);

  // 태그 드롭다운 토글 핸들러
  const handleTagDropdownToggle = () => {
    setIsTagDropdownOpen(prev => !prev);
  };

  // 태그 옵션 생성 (전체 포함)
  const tagOptions = useMemo(() => {
    const options = [{ value: '전체', label: '전체' }];
    if (tagsData && Array.isArray(tagsData)) {
      tagsData.forEach(tag => {
        options.push({ value: tag, label: tag });
      });
    }
    return options;
  }, [tagsData]);

  // API 데이터를 그리드 형식으로 변환
  const promptData = useMemo(() => {
    return (data?.content || []).map((item, index) => {
      const createdAt = item.createdAt;
      const updatedAt = item.updatedAt ? dateUtils.formatDate(new Date(item.updatedAt).getTime() + 9 * 60 * 60 * 1000, 'datetime') : item.createdAt;

      return {
        id: item.uuid,
        no: (currentPage - 1) * PAGE_SIZE + index + 1,
        name: item.name,
        // tags가 객체 배열인 경우와 문자열 배열인 경우를 모두 처리
        tags: Array.isArray(item.tags) ? item.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag.tag)) : [],
        createdAt: createdAt,
        updatedAt: updatedAt,
      };
    });
  }, [data?.content, currentPage]);

  // 로딩 중에는 6개의 스켈레톤 행을 전달하여 높이를 고정
  const gridRowData = useMemo(() => {
    if (isFetching) {
      return Array.from({ length: 6 }, (_, index) => ({
        __skeleton: true,
        id: `skeleton-${index}`,
      }));
    }
    return promptData;
  }, [isFetching, promptData]);

  // selectedId 계산: 수동 선택이 있으면 그것을 사용, 없으면 첫 번째 항목 자동 선택
  const selectedId = useMemo(() => {
    if (manuallySelectedId !== null) {
      return manuallySelectedId;
    }

    return promptData.length > 0 ? promptData[0].id : '';
  }, [manuallySelectedId, promptData]);

  // promptData 변경 시 수동 선택 리셋 (페이지 변경 시 다시 첫 번째 항목 자동 선택)
  useEffect(() => {
    setManuallySelectedId(null);
  }, [promptData]);

  // 선택된 항목 정보를 부모 컴포넌트로 전달
  useEffect(() => {
    if (selectedId && promptData.length > 0) {
      const selectedItem = promptData.find(item => item.id === selectedId);

      if (selectedItem) {
        onConfirmRef.current?.({
          id: selectedItem.id,
          name: selectedItem.name,
        });
      }
    }
  }, [selectedId, promptData]);

  const columnDefs = useMemo(
    () =>
      [
        {
          headerName: 'No',
          field: 'no' as const,
          width: 60,
          cellStyle: { paddingLeft: '16px' } as any,
        },
        {
          headerName: '이름',
          field: 'name' as const,
          flex: 1,
          minWidth: 200,
          cellStyle: { paddingLeft: '16px' } as any,
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
          headerName: '태그',
          field: 'tags' as const,
          flex: 1,
          minWidth: 250,
          cellStyle: { paddingLeft: '16px' },
          cellRenderer: (params: any) => {
            if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
              return null;
            }

            console.log('params.value.length', params.value.length);
            const tagText = params.value.join(', ');

            return (
              <div title={tagText}>
                <div className='flex gap-1'>
                  {params.value.slice(0, 2).map((tag: string, index: number) => (
                    <UITextLabel key={index} intent='tag' className='nowrap'>
                      {tag}
                    </UITextLabel>
                  ))}
                  {params.value.length > 2 && (
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
          cellStyle: { paddingLeft: '16px' } as any,
        },
        
      ] as any,
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex items-center w-full mb-2'>
              <div className='flex-shrink-0'>
                <div style={{ width: '102px', paddingRight: '12px' }}>
                  <UIDataCnt count={data?.totalElements || 0} prefix='총' />
                </div>
              </div>
              <div className='flex gap-2 items-center'>
                <UITypography variant='body-1' className='secondary-neutral-900'>
                  태그
                </UITypography>
                <div className='w-[180px]'>
                  <UIDropdown
                    value={selectedTag}
                    placeholder='태그 선택'
                    options={tagOptions}
                    isOpen={isTagDropdownOpen}
                    height={40}
                    onClick={handleTagDropdownToggle}
                    onSelect={(value: string) => {
                      setSelectedTag(value);
                      setCurrentPage(1);
                      setIsTagDropdownOpen(false);
                    }}
                  />
                </div>
              </div>
              <div className='w-[360px] h-[40px] ml-auto'>
                <UIInput.Search
                  value={searchValue}
                  placeholder='이름 입력'
                  onChange={e => {
                    setSearchValue(e.target.value);
                  }}
                  onKeyDown={e => {
                    if (e.key === 'Enter') {
                      setSearchQuery(searchValue);
                      setCurrentPage(1);
                    }
                  }}
                />
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='single-select'
              loading={false}
              rowData={gridRowData}
              columnDefs={columnDefs}
              selectedDataList={promptData.filter(item => item.id === selectedId)}
              checkKeyName='id'
              onClickRow={(params: any) => {
                setManuallySelectedId(params.data.id);
              }}
              onCheck={(selectedData: any[]) => {
                if (selectedData.length > 0) {
                  const selected = selectedData[0];
                  setManuallySelectedId(selected.id);
                }
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={currentPage}
              hasNext={data?.hasNext}
              totalPages={data?.totalPages || 1}
              onPageChange={page => setCurrentPage(page)}
              className='flex justify-center mt-5'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
