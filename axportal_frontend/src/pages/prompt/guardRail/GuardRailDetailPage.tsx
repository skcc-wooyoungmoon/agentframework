import React, { useMemo, useState } from 'react';

import { useQueryClient } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox';
import { UIDataCnt, UIPagination, UITextLabel } from '@/components/UI';
import { UILabel, UITypography } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useDeleteGuardRail, useGetGuardRailById, useGetGuardRailPromptById, useUpdateGuardRail } from '@/services/prompt/guardRail/guardRail.services';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import { GuardRailDeployModelPopup } from './GuardRailDeployModelPopup';
import { GuardRailUpdatePopup } from './GuardRailUpdatePopup';

const PAGE_SIZE = 3;

const DEPLOY_PAGE_SIZE = 1000;

/**
 * 프롬프트 > 가드레일 > (TAB) 가드레일 관리 > 가드레일 상세
 */
export const GuardRailDetailPage = () => {
  // ===========================
  // Router & Context Hooks
  // ===========================
  const { id } = useParams<{ id: string }>();

  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { openAlert, openConfirm } = useModal();

  const { user } = useUser();

  // ===========================
  // 상태 변수
  // ===========================
  const [isUpdatePopupOpen, setIsUpdatePopupOpen] = useState(false);
  const [isDeployModelPopupOpen, setIsDeployModelPopupOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);

  // ===========================
  // API 조회
  // ===========================

  // 가드레일 상세 조회
  const { data: guardRailDetailData, isLoading: isGuardRailDetailLoading } = useGetGuardRailById(id!);

  const guardRailPromptId = guardRailDetailData?.promptId;

  // 가드레일 프롬프트 상세 조회 (promptId가 필요)
  const { data: guardRailPromptData } = useGetGuardRailPromptById({ id: guardRailPromptId ?? '' }, { enabled: !!guardRailPromptId });

  const llms = guardRailDetailData?.llms || [];

  // 배포 모델 이름 계산 (ID로 매번 찌르는 방식이 아닌, 한번만 찌른 후 name으로 필터링)
  const deployModelNames = useMemo(
    () =>
      llms
        .map(llm => llm.servingName)
        .filter(Boolean)
        .join('|'),
    [llms]
  );

  const filteredDeployModelNames = deployModelNames ? encodeURIComponent(`name[]:${deployModelNames}`) : undefined;
  const shouldFetchDeployModels = !!filteredDeployModelNames;

  // 배포 모델 정보 조회 (deployModelNames가 필요)
  const { data: deployModels, isLoading: isDeployModelListLoading } = useGetModelDeployList(
    {
      page: 0,
      size: DEPLOY_PAGE_SIZE,
      ...(filteredDeployModelNames ? { filter: filteredDeployModelNames } : {}),
      queryKey: filteredDeployModelNames ? `guardrail-detail-${filteredDeployModelNames}` : 'guardrail-detail',
    },
    {
      enabled: shouldFetchDeployModels,
    }
  );

  const isDeployGridLoading = isGuardRailDetailLoading || isDeployModelListLoading;

  // ===========================
  // 그리드 계산
  // ===========================

  // 배포 모델 데이터를 그리드에 맞게 변환
  const transformedDeployModelData = useMemo(() => {
    const deployModelList = deployModels?.content || [];

    return deployModelList.map((model: any) => ({
      no: 0, // columnDefs의 valueGetter에서 계산됨
      deployName: model.name || '',
      modelName: model.modelName || '',
      status: model.status || '',
      description: model.description || '',
      modelType: model.type || '',
      deployType: model.servingType || '',
      production: '', // 정의 안됨
      publicStatus: model.publicStatus || '',
      createdAt: model.createdAt ? dateUtils.formatDate(model.createdAt, 'datetime') : '',
      updatedAt: model.updatedAt ? dateUtils.formatDate(model.updatedAt, 'datetime') : dateUtils.formatDate(model.createdAt, 'datetime'),
    }));
  }, [deployModels]);

  // 페이지네이션을 고려한 그리드 데이터
  const paginatedDeployModelData = useMemo(() => {
    const startIdx = (currentPage - 1) * PAGE_SIZE;
    const endIdx = startIdx + PAGE_SIZE;

    return transformedDeployModelData.slice(startIdx, endIdx);
  }, [transformedDeployModelData, currentPage]);

  // 배포 모델 총 개수 및 페이지 수
  const totalDeployModelCount = transformedDeployModelData.length;
  const totalDeployModelPages = Math.ceil(totalDeployModelCount / PAGE_SIZE);

  // ===========================
  // API 변경 (Mutations)
  // ===========================

  // 가드레일 삭제 API
  const { mutate: deleteGuardRail } = useDeleteGuardRail({
    onSuccess: async () => {
      // 삭제 성공 시
      await queryClient.invalidateQueries({ queryKey: ['GET', '/guardrails'] });

      openAlert({
        title: '완료',
        message: '가드레일이 삭제되었습니다.',
        onConfirm: () => {
          // 목록 페이지로 이동
          navigate('/prompt/guardrail');
        },
      });
    },

    onError: () => {
      openAlert({
        title: '실패',
        message: '가드레일 삭제에 실패했습니다.',
      });
    },
  });

  // 배포 모델 설정 저장 API
  const { mutate: updateGuardRail } = useUpdateGuardRail({
    onSuccess: response => {
      if (response.data.data.result) {
        openAlert({
          title: '완료',
          message: '배포 모델이 저장되었습니다.',
          onConfirm: () => {
            handleCloseDeployModelPopup();
            queryClient.invalidateQueries({ queryKey: ['GET', `/guardrails/${id}`] });
          },
        });
      } else {
        openAlert({
          title: '실패',
          message: '배포 모델 저장에 실패했습니다.',
        });
      }
    },
  });

  // ===========================
  // 이벤트 핸들러
  // ===========================

  // 팝업 열고 닫기
  const handleOpenUpdatePopup = () => {
    setIsUpdatePopupOpen(true);
  };

  const handleCloseUpdatePopup = () => {
    setIsUpdatePopupOpen(false);
  };

  const handleOpenDeployModelPopup = () => {
    setIsDeployModelPopupOpen(true);
  };

  const handleCloseDeployModelPopup = () => {
    setIsDeployModelPopupOpen(false);
  };

  // 삭제 핸들러
  const handleDelete = () => {
    openConfirm({
      title: '삭제 확인',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      onConfirm: () => {
        deleteGuardRail({ guardrailIds: [id!] });
      },
    });
  };

  // 저장 핸들러
  const handleSaveDeployModels = (selectedModels: GetModelDeployResponse[]) => {
    // GetModelDeployResponse[] → llms[] 변환 (servingName)
    const updatedLlms = selectedModels.map(model => ({
      servingName: model.name, // name → serving_name
    }));

    // 가드레일 전체 정보와 함께 PUT 요청
    updateGuardRail({
      id: guardRailDetailData?.uuid || '',
      name: guardRailDetailData?.name || '',
      description: guardRailDetailData?.description || '',
      projectId: user?.activeProject?.prjUuid || '',
      promptId: guardRailDetailData?.promptId || '',
      llms: updatedLlms,
      tags: [],
    });
  };

  // ===========================
  // 그리드 정의
  // ===========================
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
        valueGetter: (params: any) => {
          return (currentPage - 1) * PAGE_SIZE + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '배포명',
        field: 'deployName' as const,
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
          // 상태 값을 한글로 변환
          const getStatusLabel = (status: string) => {
            const statusMap: Record<string, string> = {
              Available: '이용가능',
              Running: '실행중',
              배포중: '배포중',
              실패: '실패',
            };
            return statusMap[status] || status;
          };

          // 상태 값에 따른 intent 반환
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '실패':
                return 'error';
              case '이용가능':
              case 'Running':
                return 'complete';
              case '배포중':
                return 'progress';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {getStatusLabel(params.value)}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 390,
        flex: 1,
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
        field: 'modelType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포유형',
        field: 'deployType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      // {
      //   headerName: '운영 배포 여부',
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
    [currentPage, PAGE_SIZE]
  );

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='가드레일 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                기본 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'auto' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailDetailData?.name}
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
                          {guardRailDetailData?.description}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                가드레일 프롬프트 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '128px' }} />
                    <col style={{ width: '656px' }} />
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'auto' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailPromptData?.name}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          가드레일 프롬프트
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600' style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
                          {guardRailPromptData?.message}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          태그
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailPromptData?.tags && guardRailPromptData.tags.length > 0 && (
                            <div className='flex gap-2'>
                              {guardRailPromptData.tags.map((tag, idx) => (
                                <UITextLabel key={idx} intent='tag'>
                                  {tag.tag}
                                </UITextLabel>
                              ))}
                            </div>
                          )}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailPromptData?.createdAt ?? ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailPromptData?.updatedAt ?? guardRailPromptData?.createdAt}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 그리드 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex items-center'>
                  <div style={{ width: '182px', paddingRight: '8px' }}>
                    <UIDataCnt count={totalDeployModelCount} prefix='배포 모델 총' unit='건' />
                  </div>
                </div>
                <div style={{ marginLeft: 'auto' }}>
                  <Button auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE} className='btn-option-outlined' onClick={handleOpenDeployModelPopup}>
                    배포 모델 설정
                  </Button>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={paginatedDeployModelData} columnDefs={columnDefs} loading={isDeployGridLoading} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={currentPage || 1}
                  hasNext={deployModels?.hasNext}
                  totalPages={totalDeployModelPages || 1}
                  onPageChange={setCurrentPage}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 담당자 정보 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                담당자 정보
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
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailDetailData?.createdBy?.jkwNm && guardRailDetailData?.createdBy?.deptNm
                            ? `${guardRailDetailData.createdBy.jkwNm} | ${guardRailDetailData.createdBy.deptNm}`
                            : ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailDetailData?.createdAt ?? ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailDetailData?.updatedBy?.jkwNm && guardRailDetailData?.updatedBy?.deptNm
                            ? `${guardRailDetailData.updatedBy.jkwNm} | ${guardRailDetailData.updatedBy.deptNm}`
                            : ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailDetailData?.updatedAt ?? guardRailDetailData?.createdAt}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 프로젝트 정보 섹션 */}
          <ProjectInfoBox assets={[{ type: 'guardrails', id: guardRailDetailData?.uuid! }]} auth={AUTH_KEY.PROMPT.GUARDRAIL_CHANGE_PUBLIC} />
        </UIPageBody>
        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.PROMPT.GUARDRAIL_DELETE} className='btn-primary-gray' onClick={handleDelete}>
                삭제
              </Button>
              <Button auth={AUTH_KEY.PROMPT.GUARDRAIL_UPDATE} className='btn-primary-blue' onClick={handleOpenUpdatePopup}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>

      {/* 수정 팝업 */}
      {isUpdatePopupOpen && (
        <GuardRailUpdatePopup
          isOpen={isUpdatePopupOpen}
          onClose={handleCloseUpdatePopup}
          initialData={{
            id: guardRailDetailData?.uuid || '',
            projectId: user?.activeProject?.prjUuid || '',
            name: guardRailDetailData?.name || '',
            description: guardRailDetailData?.description || '',
            promptName: guardRailPromptData?.name || '',
            promptId: guardRailDetailData?.promptId || '',
            llms: guardRailDetailData?.llms || [],
            tags: [],
          }}
        />
      )}

      {/* 배포 모델 설정 팝업 */}
      {isDeployModelPopupOpen && (
        <GuardRailDeployModelPopup
          isOpen={isDeployModelPopupOpen}
          onClose={handleCloseDeployModelPopup}
          onSave={handleSaveDeployModels}
          selectedList={deployModels?.content?.filter(model => guardRailDetailData?.llms?.some(llm => llm.servingName === model.name))}
        />
      )}
    </>
  );
};
