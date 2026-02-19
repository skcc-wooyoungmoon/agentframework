import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import { useAtom } from 'jotai';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import { UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetNotJoinPrivateProjList } from '@/services/home/proj/projBaseInfo.service';
import { useModal } from '@/stores/common/modal';
import { projJoinSelectedProjectAtom } from '@/stores/home/proj/projJoinWizard.atoms';
import { useCheckApprovalStatus } from '@/services/common/payReq.service.ts';
import { useUser } from '@/stores';

interface ProJoinStep1Props {
  onNextStep: () => void;
}

export const ProJoinStep1: React.FC<ProJoinStep1Props> = ({ onNextStep }) => {
  const [isPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const [searchText, setSearchText] = useState('');
  const [value, setValue] = useState('project');
  const { openAlert } = useModal();

  // 선택된 프로젝트 ID
  const [selectedProjectId, setSelectedProjectId] = useState<string | null>(null);
  // 최초 1회 자동 선택 여부 플래그
  const hasAutoSelectedRef = useRef(false);
  // Jotai atom 추가
  const [selectedProject, setSelectedProject] = useAtom(projJoinSelectedProjectAtom);

  // 검색 파라미터 상태 추가
  const [searchParams, setSearchParams] = useState({
    page: 1,
    size: 12,
    condition: 'project', // 'project' | 'name'
    keyword: '',
  });

  // 드롭다운 값을 검색 조건으로 매핑하는 함수
  const mapFilterType = useCallback((v: string) => {
    if (v === 'project' || v === '프로젝트명') return 'project';
    if (v === 'name' || v === '생성자') return 'name';
    return v || 'project';
  }, []);

  const username = sessionStorage.getItem('USERNAME') || '';
  // API 호출: useGetNotJoinPrivateProjList 사용
  const { data: notJoinProjListData, refetch } = useGetNotJoinPrivateProjList(
    {
      username: username,
      condition: searchParams.condition,
      keyword: searchParams.keyword,
      status: '',
    },
    {
      enabled: isPopupOpen, // 팝업이 열려있을 때만 API 호출
    }
  );

  // 전체 프로젝트 데이터 상태 관리
  const [allProjectData, setAllProjectData] = useState<any[]>([]);

  // API 응답이 오면 데이터 변환하여 allProjectData에 설정
  useEffect(() => {
    if (notJoinProjListData) {
      // 응답 데이터가 배열인지 확인하고 처리
      const projectsList = Array.isArray(notJoinProjListData) ? notJoinProjListData : [notJoinProjListData];
      // console.log('notJoinProjListData >>>> :', notJoinProjListData);
      // console.log('projectsList >>>> :', projectsList);

      // 데이터 형식 변환 (API 응답 -> 그리드 데이터)
      const transformedData = projectsList.map((project, index) => ({
        id: project.prjSeq || `proj-${index}`,
        no: index + 1,
        projectName: project.prjNm || '',
        description: project.dtlCtnt || '',
        participantCount: typeof project.memberCount === 'number' ? String(project.memberCount) : '0',
        manager: project.createrInfo || '',
        fstCreatedAt: project.fstCreatedAt || '',
        createrInfo: project.createrInfo || '',
      }));
      setAllProjectData(transformedData);
    }
  }, [notJoinProjListData]);

  // 검색 필터링된 데이터 (API 데이터를 클라이언트에서 필터링)
  const filteredProjectData = useMemo(() => {
    if (!searchParams.keyword) {
      return allProjectData;
    }

    const keyword = searchParams.keyword.toLowerCase();
    return allProjectData.filter(project => {
      if (searchParams.condition === 'project') {
        return project.projectName?.toLowerCase().includes(keyword);
      } else if (searchParams.condition === 'name') {
        return project.manager?.toLowerCase().includes(keyword);
      }
      return true;
    });
  }, [allProjectData, searchParams.keyword, searchParams.condition]);

  // 로컬 페이징 계산 (5개 단위)
  const { totalPages, projectData } = useMemo(() => {
    const size = searchParams.size ?? 12;
    const page = searchParams.page ?? 1;
    const total = Math.max(1, Math.ceil((filteredProjectData?.length || 0) / size));
    const start = (page - 1) * size;
    const end = start + size;
    const pagedData = (filteredProjectData || []).slice(start, end).map((item, index) => ({
      ...item,
      no: start + index + 1, // 페이지에 맞는 순번 부여
    }));
    return {
      totalPages: total,
      projectData: pagedData,
    };
  }, [filteredProjectData, searchParams.page, searchParams.size]);

  // 현재 선택된 프로젝트 데이터 배열 (UIGrid 선택 동기화를 위해)
  const selectedRowData = useMemo(() => {
    if (!selectedProjectId) return [] as any[];
    const found = projectData?.find(p => p.id === selectedProjectId);
    return found ? [found] : ([] as any[]);
  }, [selectedProjectId, projectData]);

  // 컴포넌트 마운트 시 atom에서 이전 선택값 복원
  useEffect(() => {
    if (selectedProject && selectedProject.id) {
      setSelectedProjectId(selectedProject.id);
      hasAutoSelectedRef.current = true; // 복원했으므로 자동 선택 방지
    }
  }, [selectedProject]);

  // 데이터 변경 시 현재 페이지 보정
  useEffect(() => {
    if (searchParams.page > totalPages) {
      setSearchParams(prev => ({ ...prev, page: Math.max(totalPages, 1) }));
    }
  }, [totalPages, searchParams.page]);

  // 최초 진입 시 그리드의 첫 번째 항목 자동 선택
  useEffect(() => {
    if (!hasAutoSelectedRef.current && projectData && projectData.length > 0) {
      setSelectedProjectId(projectData[0].id);
      hasAutoSelectedRef.current = true;
    }
  }, [projectData]);

  const isFormValid = () => {
    // 필수 입력 값 검증 - 프로젝트 선택 여부 확인
    return selectedProjectId !== null;
  };

  // 결재 상태 조회
  const { user } = useUser();
  // selectedProjectId가 변경될 때마다 approvalUniqueKey가 재계산됨
  const approvalUniqueKey = useMemo(() => {
    if (!selectedProjectId) return '';
    return '02' + user.userInfo.memberId + selectedProjectId;
  }, [selectedProjectId, user.userInfo.memberId]);

  const { refetch: refetchApprovalStatus } = useCheckApprovalStatus(
    {
      approvalUniqueKey,
    },
    {
      enabled: false, // 자동 실행 방지
    }
  );

  const handleNext = async () => {
    if (!isFormValid()) {
      openAlert({
        title: '선택 오류',
        message: '참여할 프로젝트를 선택해주세요.',
      });
      return;
    }
    // 선택한 프로젝트의 전체 데이터 찾기
    const selectedProject = allProjectData.find(project => project.id === selectedProjectId);
    if (selectedProject) {
      // Jotai atom에 선택된 프로젝트 전체 정보 저장
      setSelectedProject(selectedProject);

      // 결재진행상태 체크
      const { data: currentApprovalStatus } = await refetchApprovalStatus();

      if (currentApprovalStatus?.inProgress) {
        await openAlert({
          title: '안내',
          message: '동일한 프로젝트 참여 요청이 이미 진행 중입니다. 기존 요청 처리 완료 후 다시 시도해주세요.',
        });
        return;
      }

      onNextStep();
    } else {
      openAlert({
        title: '오류',
        message: '선택한 프로젝트 정보를 찾을 수 없습니다.',
      });
    }
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page: number) => {
    setSearchParams(prev => ({ ...prev, page }));
  };

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
        headerName: '프로젝트명',
        field: 'projectName' as const,
        width: 280,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 530,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '참여 인원',
        field: 'participantCount' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성자',
        field: 'manager' as const,
        width: 200,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  return (
    <>
      {/* 우측 Contents 영역 콘텐츠 - 퍼블리싱 구조 적용 */}
      <section className='section-popup-content'>
        <UIPopupHeader title='프로젝트 선택' description='원하는 프로젝트를 선택해주세요.' position='right' />
        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={filteredProjectData.length} prefix='총' unit='건' />
                  </div>
                </div>
                <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(value)}
                      options={[
                        { value: 'project', label: '프로젝트명' },
                        { value: 'name', label: '생성자' },
                      ]}
                      onSelect={(value: string) => {
                        setValue(value);
                      }}
                      onClick={() => console.log('드롭다운 클릭')}
                      height={40}
                      variant='dataGroup'
                    />
                  </div>
                  <div style={{ width: '360px', flexShrink: 0 }}>
                    <UIInput.Search
                      placeholder='검색어 입력'
                      value={searchText}
                      onChange={e => setSearchText(e.target.value)}
                      onKeyDown={e => {
                        if (e.key === 'Enter') {
                          const nextCondition = mapFilterType(value);
                          const nextKeyword = searchText;
                          setSearchParams(prev => ({
                            ...prev,
                            page: 1,
                            keyword: nextKeyword,
                            condition: nextCondition,
                          }));
                          setTimeout(() => {
                            try {
                              refetch();
                            } catch (err) {
                              // console.warn('refetch error', err);
                            }
                          }, 0);
                        }
                      }}
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='single-select'
                  rowData={projectData}
                  columnDefs={columnDefs}
                  selectedDataList={selectedRowData}
                  onClickRow={(params: any) => {
                    // console.log('++++++++++++++++++++++++++++++++++');
                    // console.log('프로젝트 선택:', params);
                    setSelectedProjectId(params.data.id);
                  }}
                  onCheck={(selectedRows: any[]) => {
                    // radio button 클릭 시에도 onClickRow와 동일한 로직 수행
                    if (selectedRows && selectedRows.length > 0) {
                      setSelectedProjectId(selectedRows[0].id);
                    }
                  }}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={searchParams.page} totalPages={totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPopupBody>
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={!selectedProjectId}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </>
  );
};
