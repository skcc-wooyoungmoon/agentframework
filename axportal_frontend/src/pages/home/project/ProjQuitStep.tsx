import React, { useEffect, useMemo, useState } from 'react';

import { UIDataCnt, UIPagination, UIRadio2 } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { useGetJoinPrivateProjList, usePutQuitProjInfo } from '@/services/home/proj/projBaseInfo.service';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';

interface ProjQuitStepProps {
  onClose?: () => void;
}

export const ProjQuitStep: React.FC<ProjQuitStepProps> = ({ onClose }) => {
  // 검색 상태
  const [value, setValue] = useState('12');

  // 선택된 프로젝트 ID
  const [selectedProjectId, setSelectedProjectId] = useState<string | null>(null);

  // 페이지네이션 상태
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(12); // 동적으로 변경 가능하도록 state로 관리

  // 탈퇴 가능한 프로젝트가 있는지 여부
  const [hasValidProjects, setHasValidProjects] = useState<boolean>(false);

  const { openAlert, openConfirm } = useModal();

  // API 호출: useGetJoinPrivateProjList 사용
  const { data: projListData, isLoading } = useGetJoinPrivateProjList({
    refetchOnMount: true, // 컴포넌트가 마운트될 때마다 재조회
  });

  // 프로젝트 데이터 상태 관리
  const [allProjectData, setAllProjectData] = useState<any[]>([]);

  // API 응답이 오면 데이터 변환하여 allProjectData에 설정
  useEffect(() => {
    // console.log('!! isLoading >>>> :', isLoading);
    if (!isLoading && projListData) {
      //
      // 응답 데이터가 배열인지 확인하고 처리
      const projectsList = Array.isArray(projListData) ? projListData : [projListData];
      // console.log('!! projListData >>>> :', projListData);
      // 데이터 형식 변환 (API 응답 -> 그리드 데이터)
      const transformedData = projectsList.map((project, index) => ({
        id: project.prjSeq || `proj-${index}`,
        no: index + 1,
        projectName: project.prjNm || '프로젝트명 없음',
        projectDescription: project.dtlCtnt || '설명 없음',
        participantCount: project.memberCount?.toString() || '0',
        createdBy: project.createrInfo || '정보 없음',
        // include roleId from API so it is available on selectedProject
        roleId: project.roleSeq,
        isManager: project.isManager || false, // 관리자 여부 추가
      }));

      // 데이터 조회 결과가 0건인 경우 알림 표시
      if (transformedData.length === 0) {
        setHasValidProjects(false);
        openAlert({
          title: '안내',
          message: '탈퇴 가능한 프로젝트가 존재하지 않습니다.',
          confirmText: '확인',
          onConfirm: () => {
            onClose?.();
          },
        });
        return;
      }

      // 기존 정렬 순서를 유지하되, roleSeq == '-299'인 항목을 후순위로 이동
      const sortedData = [...transformedData.filter(project => project.roleId !== '-299'), ...transformedData.filter(project => project.roleId === '-299')];

      setAllProjectData(sortedData);
      setHasValidProjects(true);
    }
  }, [projListData, isLoading]);

  // 데이터 조회 시 첫번째 항목 자동 선택 (roleId가 '-299'가 아닌 경우에만)
  useEffect(() => {
    if (allProjectData.length > 0 && currentPage === 1) {
      // 선택 가능한 첫번째 항목 찾기 (roleId !== '-299')
      const firstSelectableItem = allProjectData.find(project => project.roleId !== '-299');

      // 선택 가능한 항목이 있으면 자동 선택
      if (firstSelectableItem) {
        setSelectedProjectId(firstSelectableItem.id);
      }
    }
  }, [allProjectData, currentPage]);

  // 현재 페이지에 표시할 프로젝트 데이터 (페이지네이션 적용)
  const projectData = useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = Math.min(startIndex + itemsPerPage, allProjectData.length);

    // 페이지 번호에 맞게 슬라이스하고, 번호를 새로 매기기
    return allProjectData.slice(startIndex, endIndex).map((item, index) => ({
      ...item,
      no: startIndex + index + 1, // 페이지에 맞는 순번 부여
    }));
  }, [allProjectData, currentPage, itemsPerPage]);

  const handleClose = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        onClose?.();
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  const isFormValid = () => {
    // 필수 입력 값 검증 - 프로젝트 선택 여부 확인
    return selectedProjectId !== null;
  };

  const handleQuit = async () => {
    if (!isFormValid()) {
      openAlert({
        title: '선택 오류',
        message: '탈퇴할 프로젝트를 선택해주세요.',
      });
      return;
    }

    // 선택된 프로젝트가 관리자인 경우 탈퇴 불가
    const selectedProject = allProjectData.find(project => project.id === selectedProjectId);
    // console.log('selectedProject +++ >>>> :', selectedProject);
    // if (selectedProject?.roleId === '-299' || selectedProject?.isManager) {
    //   openAlert({
    //     title: '탈퇴 불가',
    //     message: '프로젝트 관리자로 참여 중인 프로젝트는 탈퇴할 수 없습니다.',
    //   });
    //   return;
    // }

    // 탈퇴 확인 대화상자 표시

    openConfirm({
      message: '프로젝트를 탈퇴하시겠어요?\n탈퇴 후 재 참여를 원할 시, 프로젝트 관리자의 간편결재 승인이 필요합니다.',
      title: '안내',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        // console.log('프로젝트 탈퇴:', selectedProjectId);
        const username = sessionStorage.getItem('USERNAME') || '';
        const projectData = {
          username: username,
          project: selectedProject,
        };
        // console.log('>>>>>>>>>>> projectData >>>> :', projectData);
        quitProject(projectData);
        // Close and notify parent so the layer can be reopened later
        onClose?.();
      },
    });
  };

  const { updateUser } = useUser();
  const { mutate: quitProject } = usePutQuitProjInfo({
    onSuccess: async () => /* data */ {
      // console.log('프로젝트 탈퇴 성공:', data);

      // 생성된 프로젝트 ID 저장
      // 부모 컴포넌트에 알림
      // 성공 알림 표시
      openAlert({
        title: '성공',
        message: '프로젝트에 성공적으로 탈퇴가 되었습니다.',
      });

      try {
        const updatedUser = await authServices.getMe();
        if (updatedUser) {
          updateUser(updatedUser);
        }
      } catch (error) {
        // console.error('사용자 데이터 갱신 실패:', error);
      }
    },
    onError: /* error */ () => {
      // console.error('프로젝트 참여 실패:', error);
      // 실패 알림 표시
      openAlert({
        title: '오류',
        message: '프로젝트 참여 중 오류가 발생했습니다. 다시 시도해주세요.',
      });
    },
  });

  // 페이지 변경 핸들러
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    // 페이지 변경 시 선택된 프로젝트 초기화
    setSelectedProjectId(null);
  };

  // 총 페이지 수 계산
  const totalPages = Math.ceil(allProjectData.length / itemsPerPage);

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: '',
        field: 'selection',
        width: 50,
        minWidth: 50,
        maxWidth: 50,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellRenderer: (params: any) => {
          // const isDisabled = params.data.roleId === '-299';
          return (
            <UIRadio2 name='projectSelection' checked={selectedProjectId === params.data.id} onChange={() => setSelectedProjectId(params.data.id)} /*disabled={isDisabled} */ />
          );
        },
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
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '프로젝트 설명',
        field: 'projectDescription' as const,
        flex: 1,
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
        field: 'createdBy' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    [selectedProjectId]
  );

  // 탈퇴 가능한 프로젝트가 없으면 팝업을 렌더링하지 않음
  if (hasValidProjects === false) {
    return null;
  }

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={true}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='프로젝트 탈퇴' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>{/* 빈 공간 */}</UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleQuit} disabled={!selectedProjectId}>
                    탈퇴
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}

        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='프로젝트 탈퇴' description='탈퇴를 원하는 프로젝트를 선택해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIDataCnt count={allProjectData.length} prefix='총' unit='건' />
                  </div>
                  <div className='flex items-center gap-2'>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        options={[
                          { value: '12', label: '12개씩 보기' },
                          { value: '20', label: '20개씩 보기' },
                        ]}
                        onSelect={(selectedValue: string) => {
                          setValue(selectedValue);
                          setItemsPerPage(parseInt(selectedValue));
                          setCurrentPage(1); // 페이지 수 변경 시 첫 페이지로 이동
                        }}
                        onClick={() => console.log('드롭다운 클릭')}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    rowData={projectData}
                    columnDefs={columnDefs}
                    onClickRow={(params: any) => {
                      // console.log('프로젝트 선택:', params);
                      // console.log('선택된 프로젝트 데이터:', params.data);
                      // console.log('roleId:', params.data.roleId, 'isManager:', params.data.isManager);
                      // roleId가 '-299'인 경우 선택 불가
                      if (params.data.roleId === '-299') {
                        return;
                      }
                      // 선택된 프로젝트 ID 저장
                      setSelectedProjectId(params.data.id);
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={currentPage} totalPages={totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            {/*<UIArticle>*/}
            {/*  <div className='box-fill'>*/}
            {/*    <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>*/}
            {/*      <UIIcon2 className='ic-system-16-info-gray' />*/}
            {/*      <UITypography variant='body-2' className='secondary-neutral-700 text-sb'>*/}
            {/*        프로젝트 관리자로 참여 중인 프로젝트는 탈퇴할 수 없습니다.*/}
            {/*      </UITypography>*/}
            {/*    </div>*/}
            {/*  </div>*/}
            {/*</UIArticle>*/}
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
