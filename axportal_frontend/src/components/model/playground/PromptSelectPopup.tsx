import React, { useState } from 'react';

import { UIDataCnt, UIPagination, UITypography } from '@/components/UI';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetInfPromptList } from '@/services/prompt/inference/inferencePrompts.services';

interface PromptSelectPopupProps {
  onPromptSelect?: (promptUuids: string[]) => void;
}

export const PromptSelectPopup: React.FC<PromptSelectPopupProps> = ({ onPromptSelect }) => {
  const [searchText, setSearchText] = useState('');
  const [selectedItems, setSelectedItems] = useState<string[]>([]);
  const [selectedDataList, setSelectedDataList] = useState<any[]>([]);

  // 실제 API 요청에 사용될 파라미터
  const [searchValues, setSearchValues] = useState({
    page: 1,
    size: 6,
    sort: 'created_at,desc',
    search: '',
  });

  const { data, isFetching } = useGetInfPromptList(searchValues);

  // API 데이터를 그리드에 맞게 변환
  const projectData = React.useMemo(() => {
    if (!data?.content) return [];
    // console.log('data?.content', data?.content);

    return data.content.map((item: any, index: number) => ({
      id: item.uuid,
      no: (searchValues.page - 1) * searchValues.size + index + 1,
      name: item.name,
      latestVersion: item.latestVersion,
      releaseVersion: item.releaseVersion,
      type: item.ptype === 1 ? '채팅' : '기타',
      tags: item.tags || [],
      connectedAgent: '0', // API에서 제공되지 않는 필드
      // createdDate: item.created_at ? dateUtils.formatDate(item.created_at, 'datetime') : '',
      // updatedDate: item.updated_at ? dateUtils.formatDate(item.updated_at, 'datetime') : '',
      createdAt: item.createdAt,
      updatedAt: item.updatedAt,
      publicStatus: item.publicStatus,
    }));
  }, [data?.content]);

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
      },
      {
        headerName: '이름',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '버전',
        field: 'version' as const,
        width: 204,
        cellStyle: { paddingLeft: '16px' },
        // cellRenderer: React.memo((params: any) => {
        //   return (
        //     <div className='flex items-center gap-1'>
        //       {params.data?.releaseVersion && (
        //         <UILabel variant='line' intent='blue' showIcon={false}>
        //           Release Ver.{params.data?.releaseVersion}
        //         </UILabel>
        //       )}
        //       {params.data?.latestVersion && (
        //         <UILabel variant='line' intent='gray-2-outline' showIcon={false}>
        //           Latest Ver.{params.data?.latestVersion}
        //         </UILabel>
        //       )}
        //     </div>
        //   );
        // }),

        cellRenderer: React.memo((params: any) => {
          return (
            <div className='flex items-center gap-1'>
              {params.data?.releaseVersion && <UITextLabel intent='blue'>Release Ver.{params.data?.releaseVersion}</UITextLabel>}
              {params.data?.latestVersion && <UITextLabel intent='gray'>Lastest Ver.{params.data?.latestVersion}</UITextLabel>}
            </div>
          );
        }),
      },
      {
        headerName: '유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '공개범위',
        field: 'publicStatus' as const,
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
          const tags = params.value || [];
          if (!tags || tags.length === 0) {
            return null;
          }
          const tagText = tags.map((item: { tag: string }) => item.tag).join(', ');
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
                {tags.slice(0, 2).map((item: { tag: string }, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {item.tag}
                  </UITextLabel>
                ))}
                {/* 2개 이상일 경우 ... 처리 */}
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
        field: 'createdAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    [selectedItems, projectData]
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={data?.totalElements} />
                </div>
              </div>
              <div>
                <div className='w-[360px] h-[40px]'>
                  <UIInput.Search
                    value={searchText}
                    placeholder='검색어 입력'
                    onChange={e => {
                      setSearchText(e.target.value);
                    }}
                    onKeyDown={e => {
                      if (e.key === 'Enter') {
                        setSearchValues(prev => ({ ...prev, search: searchText }));
                      }
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='multi-select'
              loading={isFetching}
              rowData={projectData}
              columnDefs={columnDefs}
              selectedDataList={selectedDataList}
              // onClickRow={(params: any) => {
              //   console.log('프로젝트 선택:', params);
              // }}
              onCheck={(selectedItems: any[]) => {
                // console.log('다중 선택:', selectedItems);
                setSelectedDataList(selectedItems);

                const selectedIds = selectedItems?.map(item => item.id) || [];
                setSelectedItems(selectedIds);
                if (onPromptSelect) {
                  onPromptSelect(selectedIds);
                }
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={searchValues.page || 1}
              totalPages={data?.totalPages || 1}
              onPageChange={(page: number) => {
                setSearchValues(prev => ({
                  ...prev,
                  page: page,
                }));
              }}
              className='flex justify-center'
              hasNext={data?.hasNext}
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
