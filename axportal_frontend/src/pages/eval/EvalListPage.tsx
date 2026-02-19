import { useEffect, useMemo, useState } from 'react';

import { UIButton2, UIDataCnt } from '@/components/UI';
import { UIBox, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UITabs } from '@/components/UI/organisms/UITabs';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { env } from '@/constants/common/env.constants';
import { authServices } from '@/services/auth/auth.non.services';
import { useGetEvalTaskList } from '@/services/eval/eval.service';
import { useUser } from '@/stores';
import { authUtils, dateUtils } from '@/utils/common';
import { useModal } from '@/stores/common/modal';
import { JudgeGuide } from '@/pages/eval/JudgeGuide.tsx';
import { HumanGuide } from '@/pages/eval/HumanGuide.tsx';
import { QuantitativeGuide } from '@/pages/eval/QuantitativeGuide.tsx';

export const EvalListPage = () => {
  const { user } = useUser();
  const { openModal } = useModal();

  const group = useMemo(() => {
    const project = user?.projectList?.find(item => item.active);
    return project?.adxpGroupPath || '';
  }, [user]);

  // 탭별 상태 관리 - 간단한 객체 방식
  const [tabStates, setTabStates] = useState({
    JUDGE: { currentPage: 1, pageSize: 12, searchValue: '' },
    HUMAN: { currentPage: 1, pageSize: 12, searchValue: '' },
    QUANTITATIVE: { currentPage: 1, pageSize: 12, searchValue: '' },
  });
  const [activeTab, setActiveTab] = useState<'JUDGE' | 'HUMAN' | 'QUANTITATIVE'>('JUDGE');

  // 검색어 입력 상태 (API 호출하지 않음)
  const [inputSearchValues, setInputSearchValues] = useState({
    JUDGE: '',
    HUMAN: '',
    QUANTITATIVE: '',
  });

  // 현재 활성 탭의 상태
  const currentTabState = tabStates[activeTab];

  // 모든 탭의 데이터를 미리 로드 (React Query가 자동으로 캐싱)
  const judgeQuery = useGetEvalTaskList(
    {
      group,
      category: 'JUDGE',
      page: tabStates.JUDGE.currentPage,
      pageSize: tabStates.JUDGE.pageSize,
      search: tabStates.JUDGE.searchValue || undefined,
    },
    { enabled: !!group && activeTab === 'JUDGE' }
  );

  const humanQuery = useGetEvalTaskList(
    {
      group,
      category: 'HUMAN',
      page: tabStates.HUMAN.currentPage,
      pageSize: tabStates.HUMAN.pageSize,
      search: tabStates.HUMAN.searchValue || undefined,
    },
    { enabled: !!group && activeTab === 'HUMAN' }
  );

  const quantitativeQuery = useGetEvalTaskList(
    {
      group,
      category: 'QUANTITATIVE',
      page: tabStates.QUANTITATIVE.currentPage,
      pageSize: tabStates.QUANTITATIVE.pageSize,
      search: tabStates.QUANTITATIVE.searchValue || undefined,
    },
    { enabled: !!group && activeTab === 'QUANTITATIVE' }
  );

  useEffect(() => {
    if (!group) return; // group이 없으면 API 호출하지 않음

    if (activeTab === 'JUDGE') {
      judgeQuery.refetch();
    } else if (activeTab === 'HUMAN') {
      humanQuery.refetch();
    } else if (activeTab === 'QUANTITATIVE') {
      quantitativeQuery.refetch();
    }
  }, [activeTab, tabStates, group]);

  const isFetching = useMemo(() => {
    if (activeTab === 'JUDGE') {
      return judgeQuery.isFetching;
    } else if (activeTab === 'HUMAN') {
      return humanQuery.isFetching;
    } else if (activeTab === 'QUANTITATIVE') {
      return quantitativeQuery.isFetching;
    }
    return false;
  }, [activeTab, judgeQuery.isFetching, humanQuery.isFetching, quantitativeQuery.isFetching]);

  // 현재 활성 탭의 데이터 선택
  const taskListResponse = useMemo(() => {
    switch (activeTab) {
      case 'JUDGE':
        return judgeQuery.data;
      case 'HUMAN':
        return humanQuery.data;
      case 'QUANTITATIVE':
        return quantitativeQuery.data;
      default:
        return null;
    }
  }, [activeTab, judgeQuery.data, humanQuery.data, quantitativeQuery.data]);

  // API 응답 데이터를 기존 형식으로 변환
  const rowData = useMemo(() => {
    if (!taskListResponse?.tasks) return [];

    return taskListResponse.tasks.map((task, index) => ({
      no: (currentTabState.currentPage - 1) * currentTabState.pageSize + index + 1,
      id: task.id,
      evalName: task.name,
      description: task.description,
      createdDate: dateUtils.formatDate(new Date(task.createdAt), 'datetime'),
      redirectUrl: task.redirectUrl, // 평가상세 이동을 위한 URL 추가
      isPublic: task.isPublic,
    }));
  }, [taskListResponse, currentTabState.currentPage, currentTabState.pageSize]);

  // 토큰 갱신 및 검증 공통 함수
  // useCallback 없이 일반 함수로 선언: sessionStorage에서 직접 값을 가져오므로
  // 클로저 문제가 없고, 함수가 호출될 때마다 항상 최신 토큰을 가져옵니다.
  const getValidToken = async (): Promise<string | null> => {
    // 호출 시점에 sessionStorage에서 최신 토큰 가져오기 (클로저 문제 없음)
    let token = authUtils.getAccessToken();

    // 토큰이 없거나 만료되었는지 확인
    if (!token || authUtils.isAccessTokenExpired()) {
      // 토큰이 만료되었거나 없으면 갱신 시도
      try {
        await authServices.refresh();
        token = authUtils.getAccessToken();
      } catch (refreshError) {
        authUtils.clearTokens();
        window.location.href = '/login';
        return null;
      }
    }

    if (!token) {
      alert('인증 토큰을 가져올 수 없습니다. 다시 로그인해주세요.');
      authUtils.clearTokens();
      window.location.href = '/login';
      return null;
    }

    return token;
  };

  // 그리드 컬럼 정의 (피그마 기반)
  const columnDefs: any = useMemo(
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
        field: 'evalName' as const,
        width: 240,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '평가상세',
        field: 'evalDetail',
        width: 120,
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        cellRenderer: (params: any) => {
          return (
            <Button
              auth={AUTH_KEY.EVAL.EVAL_DETAIL_MOVE}
              className='btn-text-14-underline-point'
              onClick={async () => {
                const redirectUrl = params.data?.redirectUrl;
                const isPublic: boolean = !!params.data?.isPublic;
                // Task의 redirectUrl로 이동
                if (redirectUrl) {
                  const token = await getValidToken();
                  if (!token) {
                    return; // getValidToken에서 이미 에러 처리 및 리다이렉트 완료
                  }
                  const targetUrl = `${env.VITE_DATUMO_URL}/verify?is-public=${isPublic}&token=${token}&redirect-url=${redirectUrl}`;
                  window.open(targetUrl, '_blank');
                }
              }}
            >
              평가상세 이동
            </Button>
          );
        },
      },
    ],
    [] // getValidToken은 일반 함수이므로 의존성 배열에 포함하지 않아도 됩니다.
    // 함수가 호출될 때마다 sessionStorage에서 최신 값을 가져오므로 클로저 문제가 없습니다.
  );

  // 탭 상태 업데이트 헬퍼 함수
  const updateTabState = (tabId: 'JUDGE' | 'HUMAN' | 'QUANTITATIVE', updates: Partial<typeof tabStates.JUDGE>) => {
    setTabStates(prev => ({
      ...prev,
      [tabId]: { ...prev[tabId], ...updates },
    }));
  };

  // 탭 변경 핸들러
  const handleTabChange = (tabId: string) => {
    setActiveTab(tabId as 'JUDGE' | 'HUMAN' | 'QUANTITATIVE');
  };

  // 검색 실행 핸들러
  const handleSearch = () => {
    const currentInputValue = inputSearchValues[activeTab];
    updateTabState(activeTab, { searchValue: currentInputValue, currentPage: 1 });
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page: number) => {
    updateTabState(activeTab, { currentPage: page });
  };

  // 페이지 크기 변경 핸들러
  const handlePageSizeChange = (pageSize: number) => {
    updateTabState(activeTab, { pageSize, currentPage: 1 });
  };

  // 검색어 변경 핸들러 (입력 상태만 업데이트, API 호출하지 않음)
  const handleSearchValueChange = (searchValue: string) => {
    setInputSearchValues(prev => ({
      ...prev,
      [activeTab]: searchValue,
    }));
  };

  const handleInteractiveEvelMove = async () => {
    const token = await getValidToken();
    if (!token) {
      return; // getValidToken에서 이미 에러 처리 및 리다이렉트 완료
    }

    const targetUrl = `${env.VITE_DATUMO_URL}/verify?token=${token}&redirect-url=/interactive-evaluation/list/`;
    window.open(targetUrl, '_blank');
  };

  const handleManualEvelMove = async () => {
    const token = await getValidToken();
    if (!token) {
      return; // getValidToken에서 이미 에러 처리 및 리다이렉트 완료
    }

    const targetUrl = `${env.VITE_DATUMO_URL}/verify?token=${token}&redirect-url=/manual-evaluation/list/`;
    window.open(targetUrl, '_blank');
  };

  const handleEvelDashBoardMove = async () => {
    const token = await getValidToken();
    if (!token) {
      return; // getValidToken에서 이미 에러 처리 및 리다이렉트 완료
    }

    const targetUrl = `${env.VITE_DATUMO_URL}/verify?token=${token}&redirect-url=/overview/`;
    window.open(targetUrl, '_blank');
  };

  const openGuideModal = (type: 'JUDGE' | 'HUMAN' | 'QUANTITATIVE') => {
    switch (type) {
      case 'JUDGE':
        openModal({
          title: '저지평가 가이드',
          type: 'large',
          body: <JudgeGuide />,
          showFooter: false,
        });
        return;
      case 'HUMAN':
        openModal({
          title: '정성평가 가이드',
          type: 'large',
          body: <HumanGuide />,
          showFooter: false,
        });
        return;
      case 'QUANTITATIVE':
        openModal({
          title: '정량평가 가이드',
          type: 'large',
          body: <QuantitativeGuide />,
          showFooter: false,
        });
        return;
    }
  };

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='평가'
          description='모델과 에이전트 평가 그룹 목록을 볼 수 있습니다.'
          actions={
            <>
              <Button
                auth={AUTH_KEY.EVAL.RED_TEAMING_MOVE}
                className='btn-text-18-semibold'
                leftIcon={{ className: 'ic-system-24-link', children: '' }}
                onClick={handleManualEvelMove}
              >
                수동 평가 이동
              </Button>
              <Button
                auth={AUTH_KEY.EVAL.USER_EVAL_MOVE}
                className='btn-text-18-semibold'
                leftIcon={{ className: 'ic-system-24-link', children: '' }}
                onClick={handleInteractiveEvelMove}
              >
                대화형 평가 이동
              </Button>
              <Button
                auth={AUTH_KEY.EVAL.EVAL_EXECUTE}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-add', children: '' }}
                onClick={handleEvelDashBoardMove}
              >
                평가하기
              </Button>
            </>
          }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            {/* 탭 영역 */}
            <UITabs
              items={[
                { id: 'JUDGE', label: '저지평가' },
                { id: 'HUMAN', label: '정성평가' },
                { id: 'QUANTITATIVE', label: '정량평가' },
              ]}
              activeId={activeTab}
              size='large'
              onChange={handleTabChange}
            />
          </UIArticle>

          {/* 필터 영역 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row' vAlign='center'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td className='!w-[1200px]'>
                          <UIInput.Search
                            value={inputSearchValues[activeTab]}
                            onChange={e => {
                              handleSearchValueChange(e.target.value);
                            }}
                            onKeyDown={e => {
                              if (e.key === 'Enter') {
                                handleSearch();
                              }
                            }}
                            placeholder='이름, 설명 입력'
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
            {/* 전체 데이터 목록 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIDataCnt count={taskListResponse?.totalDataCount || 0} prefix='총' />
                </div>
                <div className='flex items-center gap-2'>
                  <div>
                    <UIButton2
                      className='btn-tertiary-outline'
                      onClick={() => {
                        openGuideModal(activeTab);
                      }}
                    >
                      {activeTab === 'JUDGE' ? '저지평가 가이드' : activeTab === 'HUMAN' ? '정성평가 가이드' : activeTab === 'QUANTITATIVE' ? '정량평가 가이드' : ''}
                    </UIButton2>
                  </div>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(currentTabState.pageSize)}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => {
                        handlePageSizeChange(parseInt(value));
                      }}
                      // onClick={() => console.log('드롭다운 클릭')}
                      height={40}
                      variant='dataGroup'
                      disabled={!(rowData?.length > 0)}
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid rowData={rowData} loading={isFetching} columnDefs={columnDefs} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={currentTabState.currentPage}
                  totalPages={taskListResponse?.totalPageCount || 1}
                  onPageChange={handlePageChange}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </>
  );
};
