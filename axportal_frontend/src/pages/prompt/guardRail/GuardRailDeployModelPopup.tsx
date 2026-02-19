import React, { useEffect, useMemo, useRef, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UILabel } from '@/components/UI/atoms';
import { UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { MODEL_DEPLOY_STATUS } from '@/constants/deploy/modelDeploy.constants';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { useUser } from '@/stores';

/**
 * 배포 모델 설정 팝업 Props
 */
interface GuardRailDeployModelPopupProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (selectedModels: GetModelDeployResponse[]) => void;
  selectedList?: any[];
}

// 페이지당 항목 수
const PAGE_SIZE = 12;

/**
 * 프롬프트 > 가드레일 > (TAB) 가드레일 관리 > 배포 모델 설정 팝업
 * 가드레일에 적용할 배포 모델을 설정하는 팝업
 */
export const GuardRailDeployModelPopup: React.FC<GuardRailDeployModelPopupProps> = ({ isOpen, onClose, onSave, selectedList }) => {
  const lastSyncedSignatureRef = useRef<string>('');

  const {user} = useUser();
  const isPortalAdmin = user.activeProject.prjRoleSeq === '-199';

  const { showCancelConfirm } = useCommonPopup();
  const { openAlert } = useModal();

  // 검색 조건
  const [searchValues, setSearchValues] = useState({
    page: 1,
    searchKeyword: '',
  });

  const [item, setItem] = useState<any[]>(selectedList || []);

  const selectedDeployModelNames = useMemo(() => item.map(model => model?.name).filter((name): name is string => typeof name === 'string' && name.length > 0), [item]);

  // 입력 중인 검색어 (엔터 키를 누를 때만 searchKeyword에 반영)
  const [inputValue, setInputValue] = useState('');

  // 모든 페이지에 걸친 선택 항목을 Map으로 관리 (servingId -> 전체 모델 정보)
  const [allSelectedModelsMap, setAllSelectedModelsMap] = useState<Map<string, GetModelDeployResponse>>(new Map());

  // selectedList 내용 변화를 문자열 시그니처로 만들어 부모 재렌더 시 불필요한 초기화를 막음
  const selectedListSignature = useMemo(() => {
    if (!selectedList || selectedList.length === 0) {
      return '';
    }

    return selectedList
      .map(model => model.name)
      .filter(Boolean)
      .sort()
      .join('|');
  }, [selectedList]);

  // 배포 모델 목록 조회 (이용가능 상태만)
  const {
    data: modelList,
    refetch,
    isFetching,
  } = useGetModelDeployList({
    page: searchValues.page - 1,
    size: PAGE_SIZE,
    filter: 'status:Available,type:language',
    search: searchValues.searchKeyword,
    queryKey: 'guardrail-model',
    deployModelNames: selectedDeployModelNames,
  });

  // selectedList가 실제로 변경됐을 때만 초기 선택 상태 동기화
  useEffect(() => {
    if (!isOpen) {
      return;
    }

    // 동일한 시그니처면 동기화 스킵 (부모의 동일 배열 재생성 대응)
    if (lastSyncedSignatureRef.current === selectedListSignature) {
      return;
    }

    lastSyncedSignatureRef.current = selectedListSignature;

    if (selectedList && selectedList.length > 0) {
      const initialMap = new Map<string, GetModelDeployResponse>();

      selectedList.forEach((model: any) => {
        initialMap.set(model.servingId, model);
      });

      setAllSelectedModelsMap(initialMap);
      setItem(selectedList);
    } else {
      // selectedList가 비어있으면 모든 선택 해제
      setAllSelectedModelsMap(new Map());
      setItem([]);
    }
  }, [selectedList, selectedListSignature, isOpen]);

  // 팝업이 닫히면 내부 상태 초기화 (다음 오픈을 위해)
  useEffect(() => {
    if (isOpen) {
      return;
    }

    lastSyncedSignatureRef.current = '';
    setAllSelectedModelsMap(new Map());
    setItem([]);
  }, [isOpen]);

  // 페이지 번호가 바뀌면 목록 재조회
  useEffect(() => {
    refetch();
  }, [searchValues.page]);

  // API 응답 데이터를 가공한 행 데이터
  const rowData = useMemo(() => {
    return (
      modelList?.content.map((item, index) => ({
        id: item.servingId,
        ...item,
        no: (searchValues.page - 1) * PAGE_SIZE + index + 1,
        createdAt: dateUtils.formatDate(item.createdAt, 'datetime'),
        updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : dateUtils.formatDate(item.createdAt, 'datetime'),
      })) ?? []
    );
  }, [modelList]);

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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '배포명',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const statusConfig = MODEL_DEPLOY_STATUS[params.value as keyof typeof MODEL_DEPLOY_STATUS];
          return (
            <UILabel variant='badge' intent={(statusConfig?.intent as any) || 'complete'}>
              {statusConfig?.label || params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '가드레일 적용 여부',
        field: 'guardrailApplied' as const,
        width: 152,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 392,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '모델유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포유형',
        field: 'servingType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      // {
      //   headerName: '운영배포 여부',
      //   field: 'production' as const,
      //   width: 120,
      //   cellStyle: { paddingLeft: '16px' },
      // },
      {
        headerName: '공개범위',
        field: 'publicStatus' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  // 검색어가 변경되면 API 재호출 (엔터 키로 searchKeyword 변경 시 자동 호출)
  useEffect(() => {
    refetch();
  }, [searchValues.searchKeyword, refetch]);

  // 저장 버튼 비활성화 여부 계산 (선택된 모델이 0개이면 비활성화)
  const isSaveDisabled = useMemo(() => {
    return allSelectedModelsMap.size === 0;
  }, [allSelectedModelsMap]);

  // 검색어 엔터 키 핸들러 (엔터를 누를 때만 검색 상태 업데이트)
  const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault(); // form submit 이벤트 방지
      setSearchValues(prev => ({
        ...prev,
        searchKeyword: inputValue,
        page: 1,
      }));
      // refetch는 useEffect에서 자동으로 호출됨
    }
  };

  // 모델 선택 핸들러 (현재 페이지의 선택 변경을 Map에 반영)
  const handleSelectModels = (newSelectedForCurrentPage: GetModelDeployResponse[]) => {
    const newMap = new Map(allSelectedModelsMap);

    // 현재 페이지의 기존 선택 항목 제거
    rowData.forEach(item => {
      // name을 기준으로 비교 (unique함)
      const isSelected = newSelectedForCurrentPage.find(s => s.name === item.name);

      if (!isSelected) {
        // 선택 해제
        newMap.delete(item.servingId);
      }
    });

    // 현재 페이지의 새 선택 항목 추가
    newSelectedForCurrentPage.forEach(item => {
      newMap.set(item.servingId, item);
    });

    setAllSelectedModelsMap(newMap);
    // Map을 배열로 변환하여 UIGrid에 표시되는 선택 상태 업데이트
    setItem(Array.from(newMap.values()));
  };

  // 저장 버튼 클릭 핸들러
  const handleSave = () => {
    const selectedModels = Array.from(allSelectedModelsMap.values());

    // 초기 선택 목록과 현재 선택 목록 비교 (servingId 기준)
    const initialServingIds = new Set((selectedList || []).map(model => model.servingId));

    const currentServingIds = new Set(selectedModels.map(model => model.servingId));

    // 개수가 같고 모든 servingId가 일치하는지 확인
    const hasNoChanges = initialServingIds.size === currentServingIds.size && [...initialServingIds].every(id => currentServingIds.has(id));

    if (hasNoChanges) {
      openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
      });
      return;
    }

    // 전체공유 모델 포함 여부 검증
    const hasPublicModel = selectedModels.some(model => model.publicStatus === '전체공유');

    if (hasPublicModel && !isPortalAdmin) {
      openAlert({
        title: '안내',
        message: '전체공유된 배포 모델이 포함되어 있는 경우 가드레일을 생성할 수 없습니다. 해당 모델 제외 후 다시 시도해주세요.',
      });
      return;
    }

    onSave(selectedModels);
  };

  // 닫기 버튼 클릭 핸들러 (취소 확인)
  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='배포 모델 설정' position='left' />
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={isSaveDisabled}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='배포 모델 설정' description='가드레일을 적용할 모델을 수정할 수 있습니다.' position='right' />
        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div style={{ width: '182px', paddingRight: '8px' }}>
                  <UIDataCnt count={modelList?.totalElements ?? 0} prefix='총' />
                </div>

                <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                  <div style={{ width: '360px', flexShrink: 0 }}>
                    <UIInput.Search
                      value={inputValue}
                      placeholder='배포명, 모델명, 설명 입력'
                      style={{
                        width: '100%',
                        boxSizing: 'border-box',
                      }}
                      onChange={e => {
                        setInputValue(e.target.value);
                      }}
                      onKeyDown={handleSearchKeyDown}
                    />
                  </div>
                </div>
              </UIListContentBox.Header>

              <UIListContentBox.Body>
                <UIGrid
                  type='multi-select'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  selectedDataList={item}
                  onCheck={handleSelectModels}
                  checkKeyName={'name'}
                  loading={isFetching}
                />
              </UIListContentBox.Body>

              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={searchValues.page || 1}
                  hasNext={modelList?.hasNext}
                  totalPages={modelList?.totalPages || 1}
                  onPageChange={(newPage: number) => {
                    setSearchValues(prev => ({ ...prev, page: newPage }));
                  }}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
