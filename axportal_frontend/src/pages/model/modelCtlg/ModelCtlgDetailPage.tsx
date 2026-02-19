import { useMemo } from 'react';

import { useAtom } from 'jotai';
import { useNavigate, useParams } from 'react-router-dom';

import { ManagerInfoBox } from '@/components/common';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UIDataCnt, UILabel, UIPagination } from '@/components/UI';
import { UIIcon2, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { MODEL_DEPLOY_PROVIDER } from '@/constants/deploy/modelDeploy.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { ModelFineTuningCreateWizard } from '@/pages/model/fineTuning/ModelFineTuningCreateWizard.tsx';
import { ModelCtlgEditPage } from '@/pages/model/modelCtlg/ModelCtlgEditPage';
import { useDeleteModelCtlgBulk, useGetModelCtlgById } from '@/services/model/ctlg/modelCtlg.services';
import type { TagType } from '@/services/model/ctlg/types.ts';
import { useGetFineTuningTrainingById } from '@/services/model/fineTuning/modelFineTuning.service.ts';
import { useModal } from '@/stores/common/modal';
import { fineTuningWizardIsOpenAtom } from '@/stores/model/fineTuning/fineTuning.atoms.ts';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { DeployModelCreatePopupPage } from '../../deploy/model/DeployModelCreatePopupPage';

export const ModelCtlgDetailPage = () => {
  const navigate = useNavigate();
  const { openConfirm, openAlert } = useModal();
  const deployModelLayerPopup = useLayerPopup();
  // 모델 카드 ID
  const { id } = useParams();

  // 파인튜닝 생성팝업
  const [isWizardOpen, setIsWizardOpen] = useAtom(fineTuningWizardIsOpenAtom);

  // 모델 카드 상세 조회
  const { data: modelCtlg, isLoading, refetch } = useGetModelCtlgById(id ?? '');

  const { data: fineTuningDetail } = useGetFineTuningTrainingById({ id: modelCtlg?.trainingId || '', isDataSet: true }, { enabled: !!modelCtlg?.trainingId });
  const rowData = useMemo(() => {
    if (!fineTuningDetail || !fineTuningDetail?.datasetDetails || fineTuningDetail?.datasetDetails?.length === 0) {
      return [];
    }
    return fineTuningDetail?.datasetDetails;
  }, [fineTuningDetail]);

  // 모델 카드 삭제
  const { mutate: deleteModelCtlgBulk } = useDeleteModelCtlgBulk();
  const handleDeleteModelCtlgBulk = async () => {
    if (id !== undefined && modelCtlg?.deployStatus !== null) {
      openAlert({
        title: '안내',
        message: '배포된 모델은 삭제할 수 없습니다.',
      });
      return;
    }
    let deleteMessage = '삭제하시겠어요?\n삭제된 정보는 복구할 수 없습니다.';
    if (modelCtlg?.servingType === 'self-hosting') {
      deleteMessage = '삭제하시겠어요?\nSelf-hosting 모델은 삭제 후 재등록시 다시 처음부터 반입해야합니다.';
    }

    const isOk = await openConfirm({
      title: '안내',
      message: deleteMessage,
    });
    if (isOk) {
      deleteModelCtlgBulk(
        {
          items: [{ type: modelCtlg?.servingType as 'serverless' | 'self-hosting', id: id ?? '' }],
        },
        {
          onSuccess: async () => {
            const confirmed = await openAlert({
              title: '완료',
              message: '모델이 삭제되었습니다.',
            });
            if (confirmed) {
              navigate('/model/modelCtlg', { replace: true });
            }
          },
        }
      );
    }
  };
  const handleOnModelDeploy = () => {
    deployModelLayerPopup.setCustomStep(2);
  };

  const handleOnFineTuningCreate = () => {
    setIsWizardOpen(true);
  };

  // 배포 유형
  const isServerless = useMemo(() => {
    return modelCtlg?.servingType === 'serverless';
  }, [modelCtlg?.servingType]);

  // 수정 버튼
  const handleUpdateModelCtlg = () => {
    onOpen();
  };

  const { currentStep, onNextStep, onPreviousStep, onOpen, onClose } = useLayerPopup();

  const handleUpdateSuccess = () => {
    onClose();
    refetch();
  };

  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as any,
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 230,
        flex: 1,
        cellRenderer: (params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '100%',
              }}
            >
              {params.value}
            </div>
          );
        },
      },
      {
        headerName: '유형',
        field: 'type',
        width: 120,
        valueGetter: (params: any) => {
          const type = params?.data?.type;
          return type === 'supervised_finetuning' ? '지도학습' : type === 'unsupervised_finetuning' ? '비지도학습' : type || '';
        },
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const tags = params.value; // 태그 배열

          if (!Array.isArray(tags) || tags.length === 0) {
            return null;
          }

          const tagText = tags.map((tag: any) => tag.name).join(', ');
          const tagTextArray = tags.map((tag: any) => tag.name);
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {tagTextArray.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
    ],
    [rowData]
  );

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='모델 조회'
          description=''
          actions={
            <>
              <UIGroup gap={8} direction='row' align='center'>
                {!isServerless && (
                  <Button auth={AUTH_KEY.MODEL.FINE_TUNING_CREATE} className='btn-tertiary-outline line-only-blue' onClick={handleOnFineTuningCreate}>
                    파인튜닝
                  </Button>
                )}
                <Button
                  auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_CREATE}
                  className='btn-tertiary-outline line-only-blue'
                  disabled={modelCtlg?.isValid !== true}
                  onClick={handleOnModelDeploy}
                >
                  모델 배포
                </Button>
              </UIGroup>
            </>
          }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                모델 정보
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
                          모델명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelCtlg?.name}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          유효성
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {!isLoading && (
                            <UILabel variant='badge' intent={modelCtlg?.isValid ? 'complete' : 'error'}>
                              {modelCtlg?.isValid === true ? '유효' : '유효하지않음'}
                            </UILabel>
                          )}
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
                          {modelCtlg?.description}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          표시이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelCtlg?.displayName}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          모델 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelCtlg?.type}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          공급사
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UIUnitGroup gap={4} direction='row' vAlign='center'>
                            <UIIcon2 className={MODEL_DEPLOY_PROVIDER[modelCtlg?.providerName as keyof typeof MODEL_DEPLOY_PROVIDER] || MODEL_DEPLOY_PROVIDER.Etc} />
                            {modelCtlg?.providerName}
                          </UIUnitGroup>
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          태그
                        </UITypography>
                      </th>
                      <td>
                        {modelCtlg && modelCtlg.tags?.length > 0 && (
                          <UIUnitGroup gap={8} direction='row' align='start'>
                            {modelCtlg?.tags.map((tag: TagType, index: number) => (
                              <UITextLabel intent='tag' key={index}>
                                {tag.name}
                              </UITextLabel>
                            ))}
                          </UIUnitGroup>
                        )}
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          파라미터 수
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelCtlg?.size ? `${modelCtlg?.size}B` : ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포여부
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {isLoading ? '' : modelCtlg?.deployStatus === 'DEV' ? '개발 배포' : '미배포'}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelCtlg?.servingType}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          {isServerless ? 'URL' : '지원언어'}
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {isServerless ? modelCtlg?.url : modelCtlg?.languages.map(({ name }) => name).join(', ')}
                        </UITypography>
                      </td>
                    </tr>
                    {isServerless ? (
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Identifier
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {modelCtlg?.identifier}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            API Key
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {modelCtlg?.key}
                          </UITypography>
                        </td>
                      </tr>
                    ) : (
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            라이센스
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {modelCtlg?.license}
                          </UITypography>
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {fineTuningDetail && (
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  파인튜닝 정보
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
                            파인튜닝 이름
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {fineTuningDetail.name}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            파인튜닝 설명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {fineTuningDetail.description}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
          )}

          {/* 데이터 그리드 섹션 */}
          {fineTuningDetail && (
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={16} direction='column'>
                      <div className='flex justify-between w-full items-center'>
                        <div className='flex-shrink-0'>
                          <div style={{ width: '168px', paddingRight: '8px' }}>
                            <UIDataCnt count={rowData?.length || 0} prefix='데이터세트 총' />
                          </div>
                        </div>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          )}

          <ManagerInfoBox
            type='uuid'
            people={[
              { userId: modelCtlg?.createdBy || '', datetime: modelCtlg?.createdAt || '' },
              { userId: modelCtlg?.updatedBy || '', datetime: modelCtlg?.updatedAt || '' },
            ]}
          />

          {(isServerless || (!isServerless && modelCtlg?.trainingId)) && (
            <ProjectInfoBox assets={[{ type: 'model-ctlg', id: modelCtlg?.id || '' }]} auth={AUTH_KEY.MODEL.MODEL_CATALOG_CHANGE_PUBLIC} />
          )}
        </UIPageBody>
        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.MODEL.MODEL_CATALOG_DELETE} className='btn-primary-gray' onClick={handleDeleteModelCtlgBulk}>
                삭제
              </Button>
              {(isServerless || (!isServerless && modelCtlg?.trainingId)) && (
                <Button auth={AUTH_KEY.MODEL.MODEL_CATALOG_UPDATE} className='btn-primary-blue' onClick={handleUpdateModelCtlg}>
                  수정
                </Button>
              )}
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>

      {modelCtlg && currentStep > 0 && (
        <ModelCtlgEditPage currentStep={currentStep} onClose={onClose} onSuccess={handleUpdateSuccess} onNextStep={onNextStep} onPreviousStep={onPreviousStep} id={id || ''} />
      )}
      {deployModelLayerPopup.currentStep > 0 && <DeployModelCreatePopupPage {...deployModelLayerPopup} defaultModel={modelCtlg} />}

      {/* 파인튜닝 생성 Wizard */}
      {isWizardOpen && <ModelFineTuningCreateWizard />}
    </>
  );
};
