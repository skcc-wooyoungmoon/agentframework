import { useMemo, useState } from 'react';

import { UIButton2, UICode, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { UIArticle, UIFormField, UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { MODEL_DEPLOY_PROVIDER } from '@/constants/deploy/modelDeploy.constants';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { usePutBackendAiModelDeploy, usePutModelDeploy } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useGetSafetyFilterList } from '@/services/deploy/safetyFilter/safetyFilter.service';

/**
 *
 * @author SGO1032948
 * @description 모델 배포 수정 팝업
 *
 * DP_010102_P01
 * @returns
 */
export const DeployModelEdit = ({ currentStep, onClose, data }: LayerPopupProps & { data: GetModelDeployResponse }) => {
  const [description, setDescription] = useState(data.description);
  const [envs] = useState<Record<string, any>>(data.envs || {});
  const { showEditComplete, showNoEditContent, showCancelConfirm } = useCommonPopup();

  // servingParams가 문자열인 경우 파싱
  const parsedServingParams = useMemo(() => {
    if (!data.servingParams) return null;
    try {
      return typeof data.servingParams === 'string' ? JSON.parse(data.servingParams) : data.servingParams;
    } catch (error) {
      console.error('servingParams 파싱 오류:', error);
      return null;
    }
  }, [data.servingParams]);

  // const [servingParams, setServingParams] = useState(parsedServingParams);

  const { mutate: putModelDeploy } = usePutModelDeploy();
  const { mutate: putBackendAiModelDeploy } = usePutBackendAiModelDeploy();

  // 세이프티 필터 조회 (컴포넌트 최상위에서 호출)
  const { data: safetyFilterInput } = useGetSafetyFilterList(
    {
      size: data?.safetyFilterInputGroups?.length ?? 0,
      filter: `group_id:${data?.safetyFilterInputGroups?.join('|')}`,
    },
    {
      enabled: !!data?.safetyFilterInputGroups && (data?.safetyFilterInputGroups?.length ?? 0) > 0,
    }
  );
  const { data: safetyFilterOutput } = useGetSafetyFilterList(
    {
      size: data?.safetyFilterOutputGroups?.length ?? 0,
      filter: `group_id:${data?.safetyFilterOutputGroups?.join('|')}`,
    },
    {
      enabled: !!data?.safetyFilterOutputGroups && (data?.safetyFilterOutputGroups?.length ?? 0) > 0,
    }
  );

  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  const handleSave = async () => {
    const done = () => {
      showEditComplete({
        onConfirm: () => {
          onClose();
        },
      });
    };

    // 변경사항 체크
    const descriptionChanged = data.description !== description;
    const envsChanged = JSON.stringify(data.envs || {}) !== JSON.stringify(envs);

    if (!descriptionChanged && !envsChanged) {
      showNoEditContent({});
      return;
    }

    if (data.servingType === 'self_hosting') {
      putBackendAiModelDeploy(
        {
          ...data,
          // servingId: data.servingId,
          description,
          envs: envs ?? {},
          // servingParams: JSON.stringify(servingParams),
        },
        {
          onSuccess: async () => {
            done();
          },
        }
      );
    } else {
      putModelDeploy(
        {
          ...data,
          description,
        },
        {
          onSuccess: async () => {
            done();
          },
        }
      );
    }
  };

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 배포 수정' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          <UIPopupBody>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  모델 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-[#121315]'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        {/* 모델명 */}
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            모델명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {data.modelName}
                          </UITypography>
                        </td>
                        {/* 설명 */}
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            설명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {data.modelDescription}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        {/* 표시이름 */}
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            표시이름
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {data.displayName}
                          </UITypography>
                        </td>
                        {/* 모델타입 */}
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            모델타입
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {data.type}
                          </UITypography>
                        </td>
                      </tr>
                      {/* 배포타입 */}
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            배포타입
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {data.servingType}
                          </UITypography>
                        </td>
                        {/* 공급사 */}
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            공급사
                          </UITypography>
                        </th>
                        <td>
                          <UIUnitGroup gap={4} direction='row' vAlign='center'>
                            <UIIcon2 className={MODEL_DEPLOY_PROVIDER[data.providerName as keyof typeof MODEL_DEPLOY_PROVIDER] || MODEL_DEPLOY_PROVIDER.Etc} />
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {data.providerName}
                            </UITypography>
                          </UIUnitGroup>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900'>
                배포 정보
              </UITypography>
            </UIArticle>
            {/* 배포명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  배포명
                </UITypography>
                <UIInput.Text readOnly value={data.name} />
              </UIFormField>
            </UIArticle>
            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={description} onChange={e => setDescription(e.target.value)} maxLength={100} />
              </UIFormField>
            </UIArticle>
            {/* 세이프티 필터 섹션 */}
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900'>
                세이프티 필터
              </UITypography>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  입력 필터
                </UITypography>
                <UIGroup
                  gap={8}
                  direction='row'
                  align='start'
                  style={{ height: '48px', background: '#F3F6FB', border: '1px solid #DCE2ED', borderRadius: '8px', padding: '12px 16px' }}
                >
                  {safetyFilterInput?.content?.map((filter, index) => (
                    <div key={index} style={{ border: '1px solid #DCE2ED', borderRadius: '4px', padding: '0 6px', color: '#576072' }}>
                      <UITypography variant='body-1' className='secondary-neutral-600'>
                        {filter.filterGroupName}
                      </UITypography>
                    </div>
                  ))}
                </UIGroup>
              </UIFormField>
            </UIArticle>
            {/* 출력 필터 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  출력 필터
                </UITypography>
                <UIGroup
                  gap={8}
                  direction='row'
                  align='start'
                  style={{ height: '48px', background: '#F3F6FB', border: '1px solid #DCE2ED', borderRadius: '8px', padding: '12px 16px' }}
                >
                  {safetyFilterOutput?.content?.map((filter, index) => (
                    <div key={index} style={{ border: '1px solid #DCE2ED', borderRadius: '4px', padding: '0 6px', color: '#576072' }}>
                      <UITypography variant='body-1' className='secondary-neutral-600'>
                        {filter.filterGroupName}
                      </UITypography>
                    </div>
                  ))}
                </UIGroup>
              </UIFormField>
            </UIArticle>

            {data.servingType === 'self_hosting' && (
              <>
                <UIArticle>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                      Advanced Setting
                    </UITypography>
                    {/* 소스코드 영역 */}
                    <UICode
                      value={JSON.stringify(parsedServingParams, null, 2)}
                      // onChange={value => {
                      //   setServingParams(JSON.parse(value));
                      // }}
                      language='json'
                      theme='dark'
                      width='100%'
                      minHeight='272px'
                      maxHeight='272px'
                      readOnly
                    />
                  </UIFormField>
                </UIArticle>
                <UIArticle>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                      envs
                    </UITypography>
                    {/* 소스코드 영역 */}
                    <UICode
                      value={JSON.stringify(envs, null, 2)}
                      // onChange={value => {
                      //   try {
                      //     const parsedValue = JSON.parse(value);
                      //     setEnvs(parsedValue);
                      //   } catch (error) {
                      //     // JSON이 아직 완성되지 않았거나 유효하지 않은 경우 무시
                      //     // 사용자가 계속 입력할 수 있도록 에러를 발생시키지 않음
                      //   }
                      // }}
                      language='json'
                      theme='dark'
                      width='100%'
                      minHeight='272px'
                      maxHeight='272px'
                      readOnly
                    />
                  </UIFormField>
                </UIArticle>
              </>
            )}
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
