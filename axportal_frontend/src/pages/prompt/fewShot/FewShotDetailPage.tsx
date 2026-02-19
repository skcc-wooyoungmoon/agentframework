import { useEffect, useMemo, useState } from 'react';

import { useQueryClient } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox';
import { UIDataCnt } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { UIArticle, UIFormField, UIGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules/UIUnitGroup';
import { UIVersionCard } from '@/components/UI/molecules/UIVersionCard';
import type { UIVersionCardItem } from '@/components/UI/molecules/UIVersionCard/types';
import type { QnaPair } from '@/components/UI/organisms';
import { UIQnaFewshot } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useGetAgentBuilderById } from '@/services/agent/builder/agentBuilder.services';
import {
  useDeleteFewShotById,
  useGetFewShotById,
  useGetFewShotItemListById,
  useGetFewShotLineageRelations,
  useGetFewShotTagsByVerId,
  useGetFewShotVerListById,
  useGetLtstFewShotVerById,
  useUpdateFewShot,
} from '@/services/prompt/fewshot/fewShotPrompts.services';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import { FewShotEditPopupPage } from './FewShotEditPopupPage';

export const FewShotDetailPage = () => {
  const { id } = useParams<{ id: string }>(); // 퓨샷 고유 아이디
  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();
  const queryClient = useQueryClient();

  const { openAlert, openConfirm } = useModal();
  const [qaPairs, setQaPairs] = useState<QnaPair[]>([]); // Q&A 쌍 상태

  /**
   * 퓨샷 데이터 조회
   */
  const { data: fewShotData, refetch: refetchFewShot } = useGetFewShotById({ uuid: id || '' });

  /**
   * 버전 히스토리 조회
   */
  const { data: versionHistory, refetch: refetchVersionHistory } = useGetFewShotVerListById({
    uuid: id || '',
  });

  /**
   * 최신 버전 조회
   */
  const { data: latestVersion, refetch: refetchLatestVersion } = useGetLtstFewShotVerById(
    {
      uuid: id || '',
    },
    {
      enabled: !!id,
      retry: 3,
      retryDelay: 1000,
    }
  );

  /**
   * 선택된 버전 (versionHistory의 첫 번째 항목을 초기값으로 설정)
   */
  const [selectedVersion, setSelectedVersion] = useState<string>('');

  /**
   * 연결된 에이전트 페이지네이션 상태
   */
  const [currentPage, setCurrentPage] = useState<number>(1);
  const pageSize = 6;

  /**
   * 선택된 에이전트 ID 상태
   */
  const [selectedAgentId, setSelectedAgentId] = useState<string>('');

  /**
   * 선택된 에이전트 데이터 조회
   */
  const { data: selectedAgentData } = useGetAgentBuilderById(selectedAgentId);

  /**
   * 첫 번째 버전 정보
   */
  const firstVersion = useMemo(() => {
    return versionHistory?.find(item => item.version === 1);
  }, [versionHistory]);

  /**
   * 담당자 정보를 위한 people 배열 생성
   * versionHistory와 latestVersion이 모두 로드되었을 때만 생성
   */
  const managerPeople = useMemo(() => {
    // 두 데이터가 모두 로드되었는지 확인
    if (!versionHistory || !latestVersion || !firstVersion) {
      return undefined;
    }

    // createdBy가 하나라도 있으면 배열 반환 (빈 문자열도 포함)
    // 두 데이터가 모두 로드되었고, firstVersion이 있으면 항상 반환
    return [
      {
        userId: firstVersion.createdBy || '',
        datetime: firstVersion.createdAt || '',
      },
      {
        userId: latestVersion.createdBy || '',
        datetime: latestVersion.createdAt || '',
      },
    ];
  }, [firstVersion, firstVersion?.createdBy, firstVersion?.createdAt, latestVersion, latestVersion?.createdBy, latestVersion?.createdAt]);

  /**
   * versionHistory가 로드되면 첫 번째 항목을 selectedVersion으로 설정
   */
  useEffect(() => {
    if (versionHistory && versionHistory.length > 0 && !selectedVersion) {
      setSelectedVersion(versionHistory[0].versionId);
    }
  }, [versionHistory]);

  /**
   * 데이터 삭제
   */
  const handleDeleteConfirm = async (id: string) => {
    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteFewShot({ uuid: id });
      },
      onCancel: () => {},
    });
  };

  /**
   * 퓨샷 삭제
   */
  const { mutate: deleteFewShot } = useDeleteFewShotById({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '퓨샷이 삭제되었습니다.',
        onConfirm: () => {
          navigate('/prompt/fewShot', { replace: true });
        },
      });
    },
  });

  /**
   * 퓨샷 아이템 리스트 조회
   */
  const { data: fewShotItemList, refetch: refetchFewShotItemList } = useGetFewShotItemListById(
    {
      verId: selectedVersion || '',
    },
    {
      enabled: !!id && !!selectedVersion && selectedVersion !== '',
      retry: 3,
      retryDelay: 1000,
    }
  );

  /**
   * 선택된 버전의 태그 조회
   */
  const { data: fewShotTags, refetch: refetchFewShotTags } = useGetFewShotTagsByVerId(
    {
      verId: selectedVersion || '',
    },
    {
      enabled: !!id && !!selectedVersion && selectedVersion !== '',
      retry: 3,
      retryDelay: 1000,
    }
  );

  /**
   * selectedVersion이 변경될 때마다 API 재호출
   */
  useEffect(() => {
    if (selectedVersion && selectedVersion !== '') {
      refetchFewShotItemList();
      refetchFewShotTags();
    }
  }, [selectedVersion, refetchFewShotItemList, refetchFewShotTags]);

  /**
   * 퓨샷 업데이트
   */
  const updateFewShotMutation = useUpdateFewShot({
    onSuccess: async () => {
      // 캐시 무효화 후 버전 관련 데이터 새로고침
      // fewShot 관련 모든 쿼리 무효화 (해당 uuid와 관련된 모든 쿼리)
      if (id) {
        // prefix 매칭으로 해당 uuid와 관련된 모든 쿼리 무효화
        await queryClient.invalidateQueries({
          queryKey: ['GET'],
          predicate: query => {
            const key = query.queryKey;
            return Array.isArray(key) && key.length > 1 && typeof key[1] === 'string' && (key[1].includes(`fewShot/${id}`) || key[1].includes(`fewShot/versions/${id}`));
          },
        });
      }

      // 강제로 최신 데이터 가져오기
      const [, , latestVerResult] = await Promise.all([refetchFewShot(), refetchVersionHistory(), refetchLatestVersion()]);

      // 최신 버전 ID로 직접 업데이트
      const latestVersionId = latestVerResult.data?.versionId;
      if (latestVersionId) {
        setSelectedVersion(latestVersionId);
      }
    },
    onError: /* error */ () => {
      // console.error('퓨샷 업데이트 실패:', error);
    },
  });

  /**
   * 연결된 에이전트 목록 조회
   */
  const { data: lineageRelations, refetch: refetchLineageRelations } = useGetFewShotLineageRelations(
    {
      fewShotUuid: id || '',
      page: currentPage,
      size: pageSize,
    },
    {
      enabled: !!id,
      retry: 3,
      retryDelay: 1000,
    }
  );

  /**
   * 연결된 에이전트 컬럼 정의
   */
  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
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
        valueGetter: (params: any) => (currentPage - 1) * pageSize + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 378,
        flex: 1,
      },
      {
        headerName: '배포 여부',
        field: 'deployed',
        width: 120,
        cellRenderer: (params: any) => {
          return params.data.deployed ? '배포' : '미배포';
        },
      },
      {
        headerName: '생성일',
        field: 'createdAt',
        width: 180,
        valueGetter: (params: any) => {
          if (!params.data.createdAt) return '';
          // UTC 시간을 그대로 포맷만 적용 (타임존 변환 없이)
          const date = new Date(params.data.createdAt);
          const year = date.getUTCFullYear();
          const month = String(date.getUTCMonth() + 1).padStart(2, '0');
          const day = String(date.getUTCDate()).padStart(2, '0');
          const hours = String(date.getUTCHours()).padStart(2, '0');
          const minutes = String(date.getUTCMinutes()).padStart(2, '0');
          const seconds = String(date.getUTCSeconds()).padStart(2, '0');
          return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        valueGetter: (params: any) => {
          if (params.data.updatedAt) {
            // UTC 시간을 그대로 포맷만 적용 (타임존 변환 없이)
            const date = new Date(params.data.updatedAt);
            const year = date.getUTCFullYear();
            const month = String(date.getUTCMonth() + 1).padStart(2, '0');
            const day = String(date.getUTCDate()).padStart(2, '0');
            const hours = String(date.getUTCHours()).padStart(2, '0');
            const minutes = String(date.getUTCMinutes()).padStart(2, '0');
            const seconds = String(date.getUTCSeconds()).padStart(2, '0');
            return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
          }
          if (params.data.createdAt) {
            // UTC 시간을 그대로 포맷만 적용 (타임존 변환 없이)
            const date = new Date(params.data.createdAt);
            const year = date.getUTCFullYear();
            const month = String(date.getUTCMonth() + 1).padStart(2, '0');
            const day = String(date.getUTCDate()).padStart(2, '0');
            const hours = String(date.getUTCHours()).padStart(2, '0');
            const minutes = String(date.getUTCMinutes()).padStart(2, '0');
            const seconds = String(date.getUTCSeconds()).padStart(2, '0');
            return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
          }
          return '';
        },
      },
    ],
    [lineageRelations, currentPage, pageSize]
  );

  const handleBuilderClick = (agentId: string) => {
    if (!agentId) {
      // console.warn('에이전트 ID가 없습니다.');
      return;
    }

    // 선택된 에이전트 ID 설정 (useGetAgentBuilderById가 자동으로 조회)
    setSelectedAgentId(agentId);
  };

  /**
   * 선택된 에이전트 데이터가 로드되면 페이지 이동
   */
  useEffect(() => {
    if (selectedAgentData && selectedAgentId) {
      navigate(`/agent/builder/graph`, {
        state: {
          isReadOnly: true, // 조회모드로 링크
          data: {
            id: selectedAgentData.id,
            name: selectedAgentData.name,
            description: selectedAgentData.description,
            project_id: selectedAgentData.id,
            nodes: selectedAgentData.nodes || [],
            edges: selectedAgentData.edges || [],
          },
        },
      });
      // 이동 후 상태 초기화
      setSelectedAgentId('');
    }
  }, [selectedAgentData, selectedAgentId, navigate]);

  /**
   * 퓨샷 아이템 리스트 변경 시 Q&A 쌍 업데이트
   */
  useEffect(() => {
    if (fewShotItemList && Array.isArray(fewShotItemList)) {
      const newQaPairs: QnaPair[] = [];
      for (let i = 0; i < fewShotItemList.length; i += 2) {
        const questionItem = fewShotItemList[i];
        const answerItem = fewShotItemList[i + 1];

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
      setQaPairs(newQaPairs);
    }
  }, [fewShotItemList]);

  /**
   * 퓨샷 수정 팝업 열기
   */
  const handleFewshotEditPopup = () => {
    layerPopupOne.onOpen();
  };

  /**
   * 버전 클릭
   * @param version 선택된 버전
   */
  const handleVersionClick = (version: UIVersionCardItem) => {
    if (version.id) {
      setSelectedVersion(version.id);
    }
  };

  /**
   * 버전 릴리즈 버튼 클릭
   */
  const handleRelease = async () => {
    if (!id || !fewShotData) {
      // console.error('ID 또는 데이터가 없습니다.');
      return;
    }

    await openConfirm({
      message: '해당 버전을 배포하시겠습니까?',
      confirmText: '진행',
      cancelText: '취소',
      onConfirm: () => {
        updateFewShotMutation.mutate({
          uuid: id,
          newName: fewShotData.name,
          items: qaPairs.map(pair => ({
            itemQuery: pair.question,
            itemAnswer: pair.answer,
          })),
          release: true, // 릴리즈 상태로 변경
          tags: fewShotData.tags.map(tag => ({ tag })),
        });
      },
      onCancel: () => {},
    });
  };

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='퓨샷 조회'
          description=''
          actions={(() => {
            const releaseVersionItem = versionHistory?.find(item => item.version === fewShotData?.releaseVersion || '');
            const isCurrentVersionReleased = releaseVersionItem?.versionId === selectedVersion;
            return (
              <Button
                auth={AUTH_KEY.PROMPT.FEW_SHOT_UPDATE}
                className='btn-model-detail'
                onClick={isCurrentVersionReleased ? undefined : handleRelease}
                disabled={isCurrentVersionReleased}
              >
                릴리즈
              </Button>
            );
          })()}
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <div className='grid-layout'>
            {/* 왼쪽 영역 */}
            <div className='grid-article'>
              {/* 기본 정보 섹션 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    기본정보
                  </UITypography>
                </div>
                <div className='article-body'>
                  <div className='border-t border-black'>
                    <table className='tbl-v'>
                      <colgroup>
                        <col style={{ width: '10%' }} />
                        <col style={{ width: '40%' }} />
                        <col style={{ width: '10%' }} />
                        <col style={{ width: '40%' }} />
                      </colgroup>
                      <tbody>
                        <tr className='border-b border-gray-200'>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              이름
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {fewShotData?.name || ''}
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              버전
                            </UITypography>
                          </th>
                          <td>
                            <div className='flex gap-2'>
                              {fewShotData?.releaseVersion == null ? (
                                <UITextLabel intent='gray'>Latest Ver.{latestVersion?.version}</UITextLabel>
                              ) : (
                                <UIGroup gap={8} direction='row'>
                                  <UITextLabel intent='gray'>Latest Ver.{latestVersion?.version}</UITextLabel>
                                  <UITextLabel intent='blue'>Release Ver.{fewShotData?.releaseVersion || ''}</UITextLabel>
                                </UIGroup>
                              )}
                            </div>
                          </td>
                        </tr>
                        <tr className='border-b border-gray-200'>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              태그
                            </UITypography>
                          </th>
                          <td>
                            <UIUnitGroup gap={3} direction='row' align='start'>
                              {fewShotTags?.map(tag => (
                                <UITextLabel intent='tag'>{tag.tag}</UITextLabel>
                              ))}
                            </UIUnitGroup>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              {/* 프롬프트 섹션 */}
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UIQnaFewshot qnaPairs={qaPairs} label='' required={false} showAddButton={false} showDeleteButton={false} />
                </UIFormField>
              </UIArticle>

              {/* 연결된 에이전트 섹션 */}
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex justify-between items-center w-full'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={lineageRelations?.totalElements || 0} prefix='연결된 에이전트 총' />
                        </div>
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='default'
                      rowData={lineageRelations?.content || []}
                      columnDefs={columnDefs as any}
                      onClickRow={(params: any) => {
                        if (params.data.id) {
                          handleBuilderClick(params.data.id);
                        }
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination
                      currentPage={currentPage}
                      totalPages={lineageRelations?.totalPages || 1}
                      onPageChange={(page: number) => {
                        setCurrentPage(page);
                      }}
                      className='flex justify-center'
                    />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>

              {/* 담당자 정보 섹션 */}
              {managerPeople && <ManagerInfoBox type='uuid' people={managerPeople} />}

              {/* 프로젝트 정보 섹션 */}
              <ProjectInfoBox assets={[{ type: 'few-shot', id: id || '' }]} auth={AUTH_KEY.PROMPT.FEW_SHOT_CHANGE_PUBLIC} />

              <div className='article-buton-group'>
                <UIUnitGroup gap={8} direction='row' align='center'>
                  <Button
                    auth={AUTH_KEY.PROMPT.FEW_SHOT_DELETE}
                    className='btn-primary-gray'
                    onClick={() => {
                      if (id) {
                        handleDeleteConfirm(id);
                      }
                    }}
                  >
                    삭제
                  </Button>
                  <Button auth={AUTH_KEY.PROMPT.FEW_SHOT_UPDATE} className='btn-primary-blue' onClick={handleFewshotEditPopup}>
                    수정
                  </Button>
                </UIUnitGroup>
              </div>
            </div>

            {/* 오른쪽영역 */}
            <div className='grid-right-sticky'>
              <UIVersionCard
                versions={
                  versionHistory?.map(item => {
                    const isLatest = latestVersion && item.version === latestVersion.version;
                    const isRelease = item.release;
                    const isSelected = selectedVersion === item.versionId;

                    // 태그 배열 생성
                    const tags: Array<{ label: string; intent: 'blue' | 'gray' | 'violet' }> = [];
                    if (isLatest) {
                      tags.push({ label: 'Latest', intent: 'gray' });
                    }
                    if (isRelease) {
                      tags.push({ label: 'Release', intent: 'blue' });
                    }

                    return {
                      id: item.versionId,
                      version: `Ver.${item.version}`,
                      date: item?.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
                      tags: tags.length > 0 ? tags : undefined,
                      isActive: isSelected,
                    };
                  }) || []
                }
                onVersionClick={handleVersionClick}
              />
            </div>
          </div>
        </UIPageBody>
      </section>
      <FewShotEditPopupPage
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        fewShotUuid={id || ''}
        fewShotName={fewShotData?.name || ''}
        items={qaPairs || []}
        tags={fewShotData?.tags || []}
        onUpdateSuccess={async () => {
          // 1. 캐시 무효화 후 기본 정보와 버전 정보를 refetch
          // fewShot 관련 모든 쿼리 무효화 (해당 uuid와 관련된 모든 쿼리)
          if (id) {
            // prefix 매칭으로 해당 uuid와 관련된 모든 쿼리 무효화
            await queryClient.invalidateQueries({
              queryKey: ['GET'],
              predicate: query => {
                const key = query.queryKey;
                return Array.isArray(key) && key.length > 1 && typeof key[1] === 'string' && (key[1].includes(`fewShot/${id}`) || key[1].includes(`fewShot/versions/${id}`));
              },
            });
          }

          // 강제로 최신 데이터 가져오기
          const [, , latestVerResult] = await Promise.all([refetchFewShot(), refetchVersionHistory(), refetchLatestVersion()]);

          // 2. 최신 버전 ID로 직접 업데이트
          const latestVersionId = latestVerResult.data?.versionId;
          if (latestVersionId) {
            setSelectedVersion(latestVersionId);
          }

          // 3. 나머지 데이터 refetch
          refetchLineageRelations();
        }}
      />
    </>
  );
};
