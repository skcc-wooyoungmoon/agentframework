import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { UIDataCnt, UILabel } from '@/components/UI';
import { UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIPercentBar, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { useModal } from '@/stores/common/modal';
//import { DataImportPopup } from './DataImportPopup';
import { KnowledgeDetailEditPopup, type KnowledgeDetailEditData } from './KnowledgeDetailEditPopup';

import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useDeleteExternalKnowledge, useGetExternalRepoDetail, useGetExternalRepoProgress, useGetExternalReposFiles } from '@/services/knowledge/knowledge.services';

import { ManagerInfoBox } from '@/components/common';
import { Button } from '@/components/common/auth';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { AUTH_KEY } from '@/constants/auth';
import { useUser } from '@/stores/auth/useUser';

// 검색 조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}

/**
 * 지식 상세 페이지
 */
export const KnowledgeDetailPage: React.FC = () => {
  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();
  //  const [isDataImportPopupOpen, setIsDataImportPopupOpen] = useState(false);
  const [isEditPopupOpen, setIsEditPopupOpen] = useState(false);

  const { knwId } = useParams();
  const { user } = useUser();

  // Dropdown 값 관리
  const [searchType, setSearchType] = useState<'fileNm' | 'uuid'>('fileNm');

  // 검색 조건
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.KNOWLEDGE_MD_LIST, {
    page: 1,
    size: 6,
    searchKeyword: '',
  });

  /**
   * 데이터 삭제 -----------------------------------------------------------------------------------------------
   */
  const { mutate: deleteExternalKnowledge } = useDeleteExternalKnowledge();

  // 데이터 삭제
  const handleDeleteConfirm = async () => {
    if (!knowledgeData?.knwId && !knowledgeData?.expKnwId) {
      await openAlert({
        title: '오류',
        message: '삭제할 지식 정보를 찾을 수 없습니다.',
      });
      return;
    }

    await openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteExternalKnowledge(
          {
            items: [
              {
                knwId: knowledgeData.knwId,
                expKnwId: knowledgeData.expKnwId,
                ragChunkIndexNm: knowledgeData.ragChunkIndexNm,
              },
            ],
          },
          {
            onSuccess: async () => {
              await openAlert({
                title: '완료',
                message: '지식이 삭제되었습니다.',
                onConfirm: () => {
                  navigate('/data/dataCtlg', { replace: true }); // 목록 페이지로 이동 (지식 탭으로 이동)
                },
              });
            },
          }
        );
      },
    });
  };

  // useParams로 받은 knwId로 useGetExternalRepoDetail 호출 -----------------------------------------------------------------------------------------------
  const { data: repoDetail, refetch: refetchRepoDetail } = useGetExternalRepoDetail(knwId || '');

  // 2. 응답 시 repoDetail을 knowledgeData에 세팅
  const [knowledgeData, setKnowledgeData] = useState<any>(null);

  useEffect(() => {
    if (repoDetail) {
      const isCustomKnowledge = repoDetail.is_custom_knowledge === true;

      setKnowledgeData({
        ...repoDetail,
        // camelCase 변환
        knwId: knwId || repoDetail.knwId,
        expKnwId: isCustomKnowledge ? repoDetail.id : repoDetail.expKnwId,
        name: repoDetail.name,
        description: repoDetail.description,
        embedding: repoDetail.embedding_model_name,
        vectorDB: repoDetail.vector_db_name,
        ragChunkIndexNm: repoDetail.index_name,
        indexName: repoDetail.index_name,
        script: repoDetail.script,
        isCustomKnowledge: isCustomKnowledge,
        chunkNm: repoDetail.chunk_nm,
        dbLoadProgress: repoDetail.dbLoadProgress,
        dvlpSyncYn: repoDetail.dvlpSyncYn,
        prodSyncYn: repoDetail.prodSyncYn,
        kafkaConnectorStatus: repoDetail.kafkaConnectorStatus,
        dataPipelineLoadStatus: repoDetail.dataPipelineLoadStatus,
        dataPipelineSyncStatus: repoDetail.dataPipelineSyncStatus,
        // 상태 관련
        status: (repoDetail as any).is_active ? '활성화' : '비활성화',
        // ADXP 필드 created_by, created_at, updated_by, updated_at를 사용
        createdBy: repoDetail.created_by,
        updatedBy: repoDetail.updated_by,
        fstCreatedAt: repoDetail.created_at,
        lstUpdatedAt: repoDetail.updated_at,
        lstPrjSeq: repoDetail.lst_prj_seq,
        fstPrjSeq: repoDetail.fst_prj_seq,
      });
    }
  }, [repoDetail]);

  // 사용자 정의 지식 여부 확인 (knowledgeData가 있는 경우만 세팅, 기본값은 true)
  const isCustomKnowledge = knowledgeData ? (knowledgeData.isCustomKnowledge === true ? true : false) : true;

  // useGetExternalRepoProgress 호출 ----------------------------------------------------------------------------------------------------------------
  const shouldEnablePolling = useMemo(() => {
    return !isCustomKnowledge && !!knowledgeData?.knwId && knowledgeData?.dataPipelineLoadStatus === 'running' && knowledgeData?.dbLoadProgress < 100;
  }, [isCustomKnowledge, knowledgeData]);

  const { data: progressData } = useGetExternalRepoProgress(knwId || '', {
    enabled: shouldEnablePolling,
    refetchInterval: 5000,
    retry: false, // 에러 발생 시 재시도하지 않음
  });

  // progressData로 knowledgeData 업데이트
  useEffect(() => {
    if (progressData) {
      setKnowledgeData((prev: any) => ({
        ...prev,
        dataPipelineLoadStatus: progressData.dataPipelineLoadStatus,
        dbLoadProgress: progressData.dbLoadProgress,
      }));

      if (progressData.dbLoadProgress === 100) {
        refetchExternalReposFiles();
      }
    }
  }, [progressData]);

  // External Knowledge 지식데이터(MD) 목록 조회 - Backend API 연동 --------------------------------------------------------------------------------------
  const {
    data: externalReposFiles,
    refetch: refetchExternalReposFiles,
    isLoading,
  } = useGetExternalReposFiles(
    {
      indexName: knowledgeData?.ragChunkIndexNm,
      page: searchValues.page,
      countPerPage: searchValues.size,
      //search: searchValues.searchKeyword,
      ...(searchType === 'fileNm' ? { search: searchValues.searchKeyword } : { uuid: searchValues.searchKeyword }),
    },
    {
      enabled: !isCustomKnowledge, // 이 조건이 true일 때만 실행
    }
  );

  // 검색어 엔터 시 조회 핸들러
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    !isCustomKnowledge && refetchExternalReposFiles();
  };

  // MD파일 데이터 (나중에 실제 API 연동)
  const rowData = useMemo(() => {
    if (!externalReposFiles?.page?.content) {
      return [];
    }

    return externalReposFiles?.page?.content.map((item: any, index: number) => {
      return {
        // 그리드 표시용 필드
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        id: item?.topId,
        dataName: item?.topSource?.doc_nm, // MD파일명
        title: item?.topSource?.doc_origin_metadata?.title || '', // 타이틀
        attachName: item?.topSource?.doc_attach_yn.toUpperCase() === 'Y' ? item?.topSource?.doc_origin_metadata?.attach_nm : '', // 첨부파일 이름
        uuid: item?.topSource?.doc_uuid, // UUID
        type: item?.topSource?.doc_dataset_nm, // 지식데이터명

        // 상세 페이지용 필드 (camelCase 통일)
        docPathAnony: item?.docPathAnony,
        indexName: item?.topIndex,
      };
    });
  }, [externalReposFiles]);

  // 그리드 컬럼 정의
  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: () => ({
          textAlign: 'center' as const,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }),
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: 'MD파일명',
        field: 'dataName',
        width: 418,
        sortable: false,
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
        headerName: '타이틀',
        field: 'title',
        flex: 1,
        sortable: false,
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
        headerName: '첨부파일 이름',
        field: 'attachName',
        width: 280,
        sortable: false,
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
        headerName: 'UUID',
        field: 'uuid',
        width: 320,
        sortable: false,
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
        headerName: '지식 데이터명',
        field: 'type' as const,
        width: 180,
        sortable: false,
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
    ],
    []
  );

  // 색상 결정 로직
  const getPercentColor = () => {
    if (!knowledgeData) return '#545454';
    if (knowledgeData.dataPipelineLoadStatus === 'error') {
      return '#D61111';
    }
    if (knowledgeData.dataPipelineLoadStatus === 'complete') {
      return '#005DF9';
    }
    if (knowledgeData.dataPipelineLoadStatus === 'running') {
      if (knowledgeData.dbLoadProgress === 100) {
        return '#005DF9';
      }
      if (knowledgeData.dbLoadProgress === 0) {
        return '#8B95A9';
      }
      if (knowledgeData.dbLoadProgress >= 1 && knowledgeData.dbLoadProgress <= 99) {
        return '#545454';
      }
    }
    return '#545454';
  };

  // 지식 상태 라벨 렌더링
  const renderKnowledgeStatusLabel = (status: string | null | undefined): React.ReactElement => {
    return (
      <UILabel variant='badge' intent={status === '활성화' ? 'complete' : 'error'}>
        {status}
      </UILabel>
    );
  };

  // Kafka Connector 상태 라벨 렌더링
  // const renderKafkaStatusLabel = (status: string | null | undefined): React.ReactElement => {
  //   return (
  //     <UILabel variant='badge' intent={status === '정상' ? 'complete' : 'error'}>
  //       {status || '에러'}
  //     </UILabel>
  //   );
  // };

  // 동기화 상태 라벨 렌더링
  const renderSyncStatusLabel = (status: string | null | undefined): React.ReactElement | null => {
    if (!status || status.trim() === '') {
      return null;
    }

    return (
      <UILabel variant='badge' intent={status === '동기화중' ? 'progress' : status === '정상' ? 'complete' : 'error'}>
        {status || '에러'}
      </UILabel>
    );
  };

  // searchValues 변경 시 refetch
  useEffect(() => {
    !isCustomKnowledge === true && refetchExternalReposFiles();
  }, [searchValues.page, searchValues.size]);

  // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 수정 불가
  const checkPublicAssetPermission = (data: any, alertMessage: string = '지식/학습 데이터 편집에 대한 권한이 없습니다.') => {
    if (Number(data?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(data?.fstPrjSeq)) {
      openAlert({
        title: '안내',
        message: alertMessage,
        confirmText: '확인',
      });
      return false;
    }
    return true;
  };

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='지식 조회'
          description=''
        // actions={
        //   <>
        //     {!isCustomKnowledge && (!knowledgeData?.dataPipelineLoadStatus || knowledgeData?.dataPipelineLoadStatus === '') && (
        //       <Button
        //         auth={AUTH_KEY.DATA.DATASET_CREATE}
        //         className='btn-text-14-semibold-point'
        //         leftIcon={{ className: 'ic-system-24-add', children: '' }}
        //         onClick={() => {
        //           setIsDataImportPopupOpen(true);
        //         }}
        //       >
        //         데이터 가져오기
        //       </Button>
        //     )}
        //   </>
        // }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  지식 정보
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  지식 상태는 Script의 정합성에 의해 결정됩니다. 상태가 비활성화인 경우, Script와 지식 설정값이 올바르게 입력되었는지 지식 수정 화면에서 확인하세요.
                </UITypography>
              </UIGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251106_퍼블수정] width값 수정 */}
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {knowledgeData?.name || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          지식 상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {knowledgeData?.status && renderKnowledgeStatusLabel(knowledgeData?.status)}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {knowledgeData?.description || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          벡터 DB
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {knowledgeData?.vectorDB || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          임베딩 모델
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {knowledgeData?.embedding || ''}
                        </UITypography>
                      </td>
                    </tr>
                    {!isCustomKnowledge && (
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            청킹방법
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {knowledgeData?.chunkNm || ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            지식 유형
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            기본지식
                          </UITypography>
                        </td>
                      </tr>
                    )}
                    {isCustomKnowledge && (
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            인덱스명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {knowledgeData?.ragChunkIndexNm || knowledgeData?.indexName || ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            지식 유형
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            사용자 정의 지식
                          </UITypography>
                        </td>
                      </tr>
                    )}
                    {!isCustomKnowledge && (
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            인덱스명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {knowledgeData?.ragChunkIndexNm || ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            데이터 최초 적재 상태
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {knowledgeData?.dataPipelineLoadStatus === 'complete'
                              ? '완료'
                              : knowledgeData?.dataPipelineLoadStatus === 'fail' || knowledgeData?.dataPipelineLoadStatus === 'error'
                                ? '실패'
                                : knowledgeData?.dataPipelineLoadStatus === 'running'
                                  ? '진행중'
                                  : ''}
                          </UITypography>
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 데이터 최초 적재 현황 - 기본지식만 표시 */}
          {!isCustomKnowledge && (
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  데이터 최초 적재 현황
                </UITypography>
              </div>

              <div className='article-body'>
                {/* 데이터 카드영역 추가 */}
                <div className='card-default type-progress'>
                  <div className='card-list-wrapper'>
                    <div className='card-list'>
                      <div className='card-list-progress'>
                        {/*
                        default : 노말상태
                        fail : 실패한경우
                        success : 성공한경우
                      */}
                        <UIPercentBar value={knowledgeData?.dbLoadProgress || 0} height={8} status={knowledgeData?.dataPipelineLoadStatus} />
                      </div>
                    </div>
                    <div className='card-list'>
                      {/* 프로그래스바 - 로딩 상태 영역 추가 : card-loading 노출/비노출 처리필요 */}
                      {knowledgeData?.dataPipelineLoadStatus === 'running' && (
                        <div className='card-loading'>
                          <div className='loading-sm'></div>
                        </div>
                      )}
                      <UIGroup direction='column' gap={6} vAlign='center'>
                        <UITypography variant='body-1' className='secondary-neutral-500'>
                          진행률
                        </UITypography>
                        <UITypography variant='title-3' className='primary-800' style={{ color: getPercentColor() }}>
                          {knowledgeData?.dbLoadProgress || 0}%
                        </UITypography>
                      </UIGroup>
                    </div>
                  </div>
                </div>
              </div>
            </UIArticle>
          )}

          {/* 동기화 정보 - 기본지식만 표시 */}
          {!isCustomKnowledge && (
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  동기화 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    {/* [251106_퍼블수정] width값 수정 */}
                    <colgroup>
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                    </colgroup>
                    <tbody>
                      {(() => {
                        const isSyncEnabled = Number(knowledgeData?.dvlpSyncYn) === 1 || Number(knowledgeData?.prodSyncYn) === 1;
                        return (
                          <>
                            <tr>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  동기화 여부
                                </UITypography>
                              </th>
                              <td colSpan={isSyncEnabled ? (1 as any) : (3 as any)}>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  {isSyncEnabled ? 'Y' : 'N'}
                                </UITypography>
                              </td>
                              {isSyncEnabled && (
                                <>
                                  <th>
                                    <UITypography variant='body-2' className='secondary-neutral-900'>
                                      동기화 대상
                                    </UITypography>
                                  </th>
                                  <td>
                                    <UITypography variant='body-2' className='secondary-neutral-600'>
                                      {Number(knowledgeData?.dvlpSyncYn) === 1 && Number(knowledgeData?.prodSyncYn) === 1
                                        ? '개발계, 운영계'
                                        : Number(knowledgeData?.dvlpSyncYn) === 1
                                          ? '개발계'
                                          : '운영계'}
                                    </UITypography>
                                  </td>
                                </>
                              )}
                            </tr>
                            {isSyncEnabled && (
                              <tr>
                                {/* <th>
                                  <UITypography variant='body-2' className='secondary-neutral-900'>
                                    Kafka http
                                    <br /> 
                                    connector 상태
                                  </UITypography>
                                </th>
                                <td>
                                  <UITypography variant='body-2' className='secondary-neutral-600'>
                                    {renderKafkaStatusLabel(knowledgeData?.kafkaConnectorStatus)}
                                  </UITypography>
                                </td> */}
                                <th>
                                  <UITypography variant='body-2' className='secondary-neutral-900'>
                                    동기화 상태
                                  </UITypography>
                                </th>
                                <td colSpan={3}>
                                  <UITypography variant='body-2' className='secondary-neutral-600'>
                                    {renderSyncStatusLabel(knowledgeData?.dataPipelineSyncStatus)}
                                  </UITypography>
                                </td>
                              </tr>
                            )}
                          </>
                        );
                      })()}
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
          )}

          {/* 지식데이터 목록 - 기본지식만 표시 */}
          {!isCustomKnowledge && (
            <UIArticle className='article-grid'>
              <UIListContentBox.Header>
                <div className='flex items-center'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={externalReposFiles?.page?.totalElements || 0} prefix='지식데이터 총' />
                  </div>
                </div>
                <div className='flex items-center gap-2'>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={searchType}
                      options={[
                        { value: 'fileNm', label: '파일명' },
                        { value: 'uuid', label: 'UUID' },
                      ]}
                      onSelect={(value: string) => {
                        setSearchType(value as 'fileNm' | 'uuid');
                        setSearchValues(prev => ({ ...prev, searchKeyword: '' }));
                      }}
                      onClick={() => { }}
                      height={40}
                      variant='dataGroup'
                      disabled={false}
                    />
                  </div>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={searchValues.searchKeyword}
                      onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                      placeholder='검색어 입력'
                      onKeyDown={e => {
                        if (e.key === 'Enter') {
                          handleSearch();
                        }
                      }}
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <div className='article-body'>
                <UIListContainer>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='default'
                      loading={isLoading}
                      rowData={rowData}
                      columnDefs={columnDefs as any}
                      onClickRow={(params: any) => {
                        // 로우 데이터와 함께 지식파일 상세 페이지로 이동
                        navigate(`/data/dataCtlg/knowledge/file/${params.data.id}`, {
                          state: { fileData: params.data },
                        });
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination
                      currentPage={searchValues.page}
                      totalPages={externalReposFiles?.page?.totalPages || 1}
                      onPageChange={page => setSearchValues(prev => ({ ...prev, page }))}
                      className='flex justify-center'
                    />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </div>
            </UIArticle>
          )}

          {/* 담당자 정보 */}
          <ManagerInfoBox
            type='uuid'
            people={[
              { userId: knowledgeData?.createdBy || '', datetime: knowledgeData?.fstCreatedAt || '' },
              { userId: knowledgeData?.updatedBy || '', datetime: knowledgeData?.lstUpdatedAt || '' },
            ]}
          />

          {/* 프로젝트 정보 섹션 ADXP에서 사용하는 expKnwId 전달 필요 */}
          <ProjectInfoBox assets={[{ type: 'knowledge-external', id: knowledgeData?.expKnwId || '' }]} auth={AUTH_KEY.DATA.UNSTRUCTURED_CHANGE_PUBLIC} />
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button
                auth={AUTH_KEY.DATA.KNOWLEDGE_DELETE}
                className='btn-primary-gray'
                onClick={() => {
                  if (!checkPublicAssetPermission(knowledgeData, '지식/학습 데이터 삭제에 대한 권한이 없습니다.')) {
                    return;
                  }
                  handleDeleteConfirm();
                }}
              >
                삭제
              </Button>
              <Button
                auth={AUTH_KEY.DATA.KNOWLEDGE_SETTING}
                className='btn-primary-blue'
                onClick={() => {
                  if (!checkPublicAssetPermission(knowledgeData, '지식/학습 데이터 수정에 대한 권한이 없습니다.')) {
                    return;
                  }
                  setIsEditPopupOpen(true);
                }}
              >
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>

      {/* 데이터 가져오기 팝업 */}
      {/* <DataImportPopup
        isOpen={isDataImportPopupOpen}
        onClose={() => setIsDataImportPopupOpen(false)}
        knowledgeId={knowledgeData?.expKnwId}
        onComplete={async () => {
          await openAlert({
            title: '완료',
            message: '지식 데이터 추가를 완료하였습니다.',
          });
          setIsDataImportPopupOpen(false);
          // 진행률 조회 API 다시 호출
          refetchProgress();
        }}
      /> */}

      {/* 지식 수정 팝업 */}
      <KnowledgeDetailEditPopup
        isOpen={isEditPopupOpen}
        onClose={() => setIsEditPopupOpen(false)}
        initialData={
          knowledgeData
            ? {
              knwId: knwId,
              name: knowledgeData.name,
              description: knowledgeData.description,
              embeddingModel: knowledgeData.embedding,
              vectorDB: knowledgeData.vectorDB,
              indexName: knowledgeData.ragChunkIndexNm || knowledgeData.indexName,
              script: knowledgeData.script || '',
              isCustomKnowledge: isCustomKnowledge,
            }
            : undefined
        }
        onSave={async (updatedData: KnowledgeDetailEditData) => {
          // 수정된 데이터로 knowledgeData 업데이트
          if (updatedData && knowledgeData) {
            setKnowledgeData({
              ...knowledgeData,
              name: updatedData.name ?? knowledgeData.name,
              description: updatedData.description ?? knowledgeData.description,
              embedding: updatedData.embeddingModel ?? knowledgeData.embedding,
              vectorDB: updatedData.vectorDB ?? knowledgeData.vectorDB,
              ragChunkIndexNm: updatedData.indexName ?? knowledgeData.ragChunkIndexNm,
              indexName: updatedData.indexName ?? knowledgeData.indexName,
              script: updatedData.script ?? knowledgeData.script,
            });
          }
          await openAlert({
            title: '완료',
            message: '수정사항이 저장되었습니다.',
          });

          setIsEditPopupOpen(false);

          // 파일 목록은 indexName 기반으로 조회되지만:
          // - 이름/설명/스크립트 변경 시 파일 목록과 무관하므로 refetch 불필요
          // - 인덱스명 변경 시: 사용자 정의 지식은 파일 목록이 없고, 일반 지식은 인덱스명 수정 불가
          // 따라서 refetch() 불필요

          // 지식 상세 > 수정 후 최종수정자, 최종수정일시 업데이트 처리(refetchRepoDetail) 필요
          await refetchRepoDetail();
        }}
      />
    </>
  );
};
