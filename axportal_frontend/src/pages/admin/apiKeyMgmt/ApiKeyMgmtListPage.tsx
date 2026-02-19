import type { ColDef } from 'ag-grid-community';
import { useSetAtom } from 'jotai';
import { memo, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UILabel } from '@/components/UI/atoms/UILabel';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { API_KEY_TYPE_OPTIONS } from '@/constants/deploy/apikey.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal';

import { UIInput } from '@/components/UI/molecules/input';
import { useGetAdminApiKeyList } from '@/services/deploy/apikey/apikey.services';
import { useGetCommonProjects } from '@/services/admin/resrcMgmt';
import { selectedApiKeyAtom } from '@/stores/admin/apiKeyMgmt';

interface SearchValues {
  page: number;
  size: number;
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchKeyword: string;
  projectName: string;
}

export const ApiKeyMgmtListPage = () => {
  const navigate = useNavigate();
  const setSelectedApiKey = useSetAtom(selectedApiKeyAtom);
  const { openAlert } = useModal();
  const { user } = useUser();

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.API_KEY_MGMT_LIST, {
    page: 1,
    size: 12,
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchKeyword: '',
    projectName: '전체',
  });

  const [appliedFilters, setAppliedFilters] = useState<{
    searchKeyword: string;
    projectName: string;
  }>({
    searchKeyword: searchValues.searchKeyword,
    projectName: searchValues.projectName,
  });

  const { data: apiKeyList, isLoading, refetch, isRefetching } = useGetAdminApiKeyList({});

  const isPortalAdmin = useMemo(() => {
    return user?.projectList?.some(project => project?.prjSeq === '-999' && project?.prjRoleSeq === '-199');
  }, [user?.projectList]);

  const { data: commonProjectList } = useGetCommonProjects({
    enabled: isPortalAdmin,
    refetchOnMount: 'always',
    staleTime: 0,
  });

  const [dropdownStates, setDropdownStates] = useState({
    projectName: false,
  });

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
    }),
    [openAlert]
  );

  const projectNameOptions = useMemo(() => {
    const options: { value: string; label: string }[] = [{ value: '전체', label: '전체' }];

    if (isPortalAdmin) {
      if (commonProjectList && Array.isArray(commonProjectList)) {
        commonProjectList.forEach(project => {
          if (project?.prjNm) {
            options.push({ value: project.prjNm, label: project.prjNm });
          }
        });
      }
    } else {
      const projectList = user?.projectList ?? [];
      const targetProjects = projectList.filter(project => project?.prjRoleSeq === '-299');

      targetProjects.forEach(project => {
        if (project?.prjNm) {
          options.push({ value: project.prjNm, label: project.prjNm });
        }
      });
    }

    return options;
  }, [isPortalAdmin, commonProjectList, user?.projectList]);

  const paginatedData = useMemo(() => {
    if (!apiKeyList?.content) return { rowData: [], totalPages: 0, totalElements: 0 };

    let filteredData = apiKeyList.content;

    if (appliedFilters.searchKeyword.trim()) {
      const keyword = appliedFilters.searchKeyword.trim().toLowerCase();
      filteredData = filteredData.filter(item => item.name?.toLowerCase().includes(keyword) || item.permission?.toLowerCase().includes(keyword));
    }

    if (appliedFilters.projectName !== '전체') {
      filteredData = filteredData.filter(item => item.projectName === appliedFilters.projectName);
    }

    const startIndex = (searchValues.page - 1) * searchValues.size;
    const endIndex = startIndex + searchValues.size;
    const paginatedContent = filteredData.slice(startIndex, endIndex);

    const rowData = paginatedContent.map((apiKey, index) => ({
      ...apiKey,
      no: startIndex + index + 1,
    }));

    const totalElements = filteredData.length;
    const totalPages = Math.ceil(totalElements / searchValues.size) || 1;

    return { rowData, totalPages, totalElements };
  }, [apiKeyList?.content, appliedFilters, searchValues.page, searchValues.size]);

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handlePageChange = (page: number) => {
    setSearchValues(prev => ({ ...prev, page: page }));
  };

  const handlePageSizeChange = (newPageSize: string) => {
    setSearchValues(prev => ({ ...prev, size: Number(newPageSize), page: 1 }));
  };

  const handleSearch = () => {
    setAppliedFilters({
      searchKeyword: searchValues.searchKeyword,
      projectName: searchValues.projectName,
    });
    setSearchValues(prev => ({ ...prev, page: 1 }));
    if (searchValues.searchKeyword === '' && searchValues.projectName === '전체') {
      refetch();
    }
  };

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
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
      },
      {
        headerName: '상태',
        field: 'expired',
        width: 120,
        cellRenderer: memo((params: any) => {
          const isExpired = params.value === true;
          return isExpired ? (
            <UILabel variant='badge' intent='error'>
              사용차단
            </UILabel>
          ) : (
            <UILabel variant='badge' intent='complete'>
              사용가능
            </UILabel>
          );
        }),
      },
      {
        headerName: '프로젝트명',
        field: 'projectName',
        width: 360,
        showTooltip: true,
        cellRenderer: memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value || ''}
            </div>
          );
        }),
      },
      {
        headerName: '구분',
        field: 'type',
        width: 320,
        showTooltip: true,
        cellRenderer: memo((params: any) => {
          const type = API_KEY_TYPE_OPTIONS.find(option => option.value === params.value?.toUpperCase());
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {type?.label || ''}
            </div>
          );
        }),
      },
      {
        headerName: '연결 대상',
        field: 'permission',
        flex: 1,
        showTooltip: true,
        cellRenderer: memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value || ''}
            </div>
          );
        }),
      },
    ],
    []
  );

  return (
    <section className='section-page'>
      <UIPageHeader
        title='API Key 관리'
        description={['발급 및 등록된 모델과 에이전트의 API Key를 확인할 수 있습니다.', 'API Key별로 사용량(Quota)을 조정하고, 필요에 따라 사용 차단 또는 재활성화를 설정하세요.']}
      />

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
                        <div className='flex-1'>
                          <UIInput.Search
                            value={searchValues.searchKeyword}
                            onChange={e => {
                              setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                            }}
                            onKeyDown={e => {
                              if (e.key === 'Enter') {
                                handleSearch();
                              }
                            }}
                            placeholder='이름, 연결 대상 입력'
                          />
                        </div>
                      </td>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          프로젝트명
                        </UITypography>
                      </th>
                      <td>
                        <div className='flex-1'>
                          <UIDropdown
                            value={searchValues.projectName}
                            placeholder='프로젝트명 선택'
                            options={projectNameOptions}
                            isOpen={dropdownStates.projectName}
                            onClick={() => handleDropdownToggle('projectName')}
                            onSelect={value => {
                              setSearchValues(prev => ({ ...prev, projectName: value }));
                              setDropdownStates(prev => ({ ...prev, projectName: false }));
                            }}
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
              <div className='flex-shrink-0'>
                <UIGroup gap={8} direction='row' align='start'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={paginatedData.totalElements} prefix='총' />
                  </div>
                </UIGroup>
              </div>
              <div className='flex items-center gap-2'>
                <div style={{ width: '180px', flexShrink: 0 }}>
                  <UIDropdown
                    value={`${String(searchValues.size)}개씩 보기`}
                    disabled={paginatedData.totalElements === 0}
                    options={[
                      { value: '12', label: '12개씩 보기' },
                      { value: '36', label: '36개씩 보기' },
                      { value: '60', label: '60개씩 보기' },
                    ]}
                    onSelect={handlePageSizeChange}
                    height={40}
                    variant='dataGroup'
                    width='w-40'
                  />
                </div>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body className='article-body'>
              <UIGrid
                type='default'
                loading={isLoading || isRefetching}
                rowData={paginatedData.rowData}
                columnDefs={columnDefs}
                moreMenuConfig={moreMenuConfig}
                onClickRow={(params: any) => {
                  const apiKeyId = params.data?.id;
                  if (apiKeyId) {
                    // 로컬스토리지에 id 저장
                    localStorage.setItem(STORAGE_KEYS.SEARCH_VALUES.API_KEY_MGMT_DETAIL_ID, String(apiKeyId));
                  }
                  setSelectedApiKey(params.data);
                  navigate('/admin/api-key-mgmt/detail');
                }}
              />
            </UIListContentBox.Body>

            <UIListContentBox.Footer>
              <UIPagination currentPage={searchValues.page} totalPages={paginatedData.totalPages} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
