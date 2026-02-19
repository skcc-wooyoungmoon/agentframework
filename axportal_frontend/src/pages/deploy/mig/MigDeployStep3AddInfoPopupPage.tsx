import { useEffect, useMemo, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIIcon2, UITypography } from '@/components/UI/atoms';
import {
  UIAccordion,
  UIArticle,
  UIFormField,
  UIGroup,
  UIList,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  UITextArea2,
  UIUnitGroup,
  type UIStepperItem,
} from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetMigResourceEndpoints } from '@/services/deploy/mig/mig.services';
import { useModal } from '@/stores/common/modal/useModal';
import type { FieldData } from '@/stores/deploy/types';
import { MIG_DEPLOY_CATEGORY_MAP } from '@/stores/deploy/types';
import { useMigDeploy } from '@/stores/deploy/useMigDeploy';

interface MigDeployStep3AddInfoPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onPreviousStep: () => void;
  onNextStep: () => void;
}

interface MigDeployValidationBodyProps {
  iconClassName?: string;
  message: string;
}

function MigDeployValidationBody({ iconClassName = 'ic-system-56-check', message }: MigDeployValidationBodyProps) {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UIGroup gap={16} direction='column' vAlign='center'>
          <UIIcon2 className={iconClassName} />
          <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
            {message}
          </UITypography>
        </UIGroup>
      </UIArticle>
    </section>
  );
}

// 필드 쌍 컴포넌트 (dev/prod)
interface FieldPairProps {
  fieldName: string;
  devValue: string;
  prodValue: string;
  onProdValueChange?: (value: string) => void;
  isLongText?: boolean; // script 같은 긴 텍스트인지 여부
}

const FieldPair = ({ fieldName, devValue, prodValue, onProdValueChange, isLongText = false }: FieldPairProps) => {
  return (
    <UIFormField gap={24} direction='row'>
      <UIUnitGroup gap={8} direction='column' style={{ flex: 1 }}>
        <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
          {fieldName}(개발)
        </UITypography>
        <UITextArea2 value={devValue} placeholder='-' style={{ height: isLongText ? '200px' : '80px' }} disabled={true} resizable={false} />
      </UIUnitGroup>
      <UIUnitGroup gap={8} direction='column' style={{ flex: 1 }}>
        <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
          {fieldName}(운영)
        </UITypography>
        <UITextArea2
          value={prodValue}
          placeholder='직접 입력'
          style={{ height: isLongText ? '200px' : '80px' }}
          disabled={false}
          resizable={false}
          onChange={e => onProdValueChange?.(e.target.value)}
        />
      </UIUnitGroup>
    </UIFormField>
  );
};

// FieldData는 types.ts에서 import

// 리소스 필드 섹션 컴포넌트
interface ResourceFieldSectionProps {
  fields: FieldData[];
  onProdValueChange?: (id: string, value: string) => void;
}

const ResourceFieldSection = ({ fields, onProdValueChange }: ResourceFieldSectionProps) => {
  return (
    <UIGroup direction='column' gap={16}>
      {fields.map(field => (
        <FieldPair
          key={field.id}
          fieldName={field.fieldName}
          devValue={field.devValue}
          prodValue={field.prodValue}
          onProdValueChange={value => onProdValueChange?.(field.id, value)}
          isLongText={field.fieldName === 'script'}
        />
      ))}
    </UIGroup>
  );
};

export function MigDeployStep3AddInfoPopupPage({ isOpen, stepperItems = [], onClose, onPreviousStep, onNextStep }: MigDeployStep3AddInfoPopupPageProps) {
  const { openConfirm, openAlert, openModal } = useModal();
  const { migDeployData, updateMigDeployData, resetMigDeployData } = useMigDeploy();

  const firstUuid = migDeployData.uuidList[0];
  const uuid = typeof firstUuid === 'string' ? firstUuid : (firstUuid as any)?.id || String(firstUuid || '');

  // useGetMigResourceEndpoints 호출
  const { data: resourceEndpointsData } = useGetMigResourceEndpoints(
    {
      project_id: migDeployData.prjSeq || '',
      type: migDeployData.category || '',
      uuid: uuid,
    },
    {
      enabled: isOpen && migDeployData.uuidList.length > 0 && !!uuid && typeof uuid === 'string' && !!migDeployData.prjSeq && !!migDeployData.category,
    }
  );

  // 리소스별 필드 state 관리 (동적으로 생성)
  const [resourceFieldsState, setResourceFieldsState] = useState<Record<string, FieldData[]>>(migDeployData.resourceFieldsState || {});

  useEffect(() => {
    if (resourceEndpointsData && Object.keys(resourceEndpointsData).length > 0) {
      const newState: Record<string, FieldData[]> = {};

      Object.keys(resourceEndpointsData).forEach(resourceType => {
        const resourceList = resourceEndpointsData[resourceType];
        if (Array.isArray(resourceList) && resourceList.length > 0) {
          resourceList.forEach((resourceObj, idx) => {
            const resourceId = resourceObj.id || `${resourceType}-${idx + 1}`;
            const resourceKey = `${resourceType}-${resourceId}`; // 리소스 타입과 ID를 조합한 키

            // 리소스별로 필드를 분리하여 저장
            if (!newState[resourceKey]) {
              newState[resourceKey] = [];
            }

            // 리소스 타입별로 모든 필드 추출
            if (resourceType === 'KNOWLEDGE') {
              // KNOWLEDGE의 경우 index_name, script 등 모든 필드 추출
              Object.keys(resourceObj).forEach(key => {
                if (key !== 'id' && key !== 'type' && typeof resourceObj[key] === 'object' && resourceObj[key] !== null) {
                  const fieldValue = resourceObj[key] as any;
                  if (fieldValue && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    const devValue = fieldValue.dev;
                    const prodValue = fieldValue.prod;

                    // dev 값이 비어있지 않으면 표시 (문자열 또는 객체인 경우 처리)
                    const devValueStr =
                      typeof devValue === 'string' ? devValue : devValue && typeof devValue === 'object' ? JSON.stringify(devValue, null, 2) : String(devValue ?? '');

                    if (devValueStr.trim() !== '') {
                      const prodValueStr =
                        typeof prodValue === 'string' ? prodValue : prodValue && typeof prodValue === 'object' ? JSON.stringify(prodValue, null, 2) : String(prodValue ?? '');

                      newState[resourceKey].push({
                        id: `${resourceId}-${key}`,
                        fieldName: key,
                        devValue: devValueStr,
                        prodValue: prodValueStr,
                      });
                    }
                  }
                }
              });
            } else if (resourceType === 'VECTOR_DB') {
              // VECTOR_DB의 경우 connection_info 안의 모든 필드 추출
              const connectionInfo = resourceObj.connection_info;
              if (connectionInfo && typeof connectionInfo === 'object') {
                Object.keys(connectionInfo).forEach(key => {
                  const fieldValue = connectionInfo[key];
                  if (fieldValue && typeof fieldValue === 'object' && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    const devValue = (fieldValue as any).dev;
                    const prodValue = (fieldValue as any).prod;

                    // dev 값이 비어있지 않으면 표시 (문자열 또는 객체인 경우 처리)
                    const devValueStr =
                      typeof devValue === 'string' ? devValue : devValue && typeof devValue === 'object' ? JSON.stringify(devValue, null, 2) : String(devValue ?? '');

                    if (devValueStr.trim() !== '') {
                      const prodValueStr =
                        typeof prodValue === 'string' ? prodValue : prodValue && typeof prodValue === 'object' ? JSON.stringify(prodValue, null, 2) : String(prodValue ?? '');

                      newState[resourceKey].push({
                        id: `${resourceId}-${key}`,
                        fieldName: key,
                        devValue: devValueStr,
                        prodValue: prodValueStr,
                      });
                    }
                  }
                });
              }
            } else if (resourceType === 'SERVING_MODEL' || resourceType === 'MODEL') {
              // SERVING_MODEL은 기타 리소스 타입의 경우 모든 필드 추출
              Object.keys(resourceObj).forEach(key => {
                if (key !== 'id' && key !== 'type' && typeof resourceObj[key] === 'object' && resourceObj[key] !== null) {
                  const fieldValue = resourceObj[key] as any;
                  if (fieldValue && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    // 일반 필드 처리 (server_url 등)
                    const devValue = fieldValue.dev ?? '';
                    // dev 값이 비어있으면 노출하지 않음
                    newState[resourceKey].push({
                      id: `${resourceId}-${key}`,
                      fieldName: key,
                      devValue: devValue,
                      prodValue: fieldValue.prod || '',
                    });
                  }
                }
              });
            } else {
              // 기타 리소스 타입의 경우 모든 필드 추출
              Object.keys(resourceObj).forEach(key => {
                if (key !== 'id' && key !== 'type' && typeof resourceObj[key] === 'object' && resourceObj[key] !== null) {
                  const fieldValue = resourceObj[key] as any;

                  // api_param, auth_config 같은 중첩 구조 처리
                  if ((key === 'api_param' || key === 'auth_config') && typeof fieldValue === 'object') {
                    // api_param 내부의 headers, body, params 처리
                    // auth_config 내부의 username, password 처리
                    Object.keys(fieldValue).forEach(nestedKey => {
                      const nestedValue = fieldValue[nestedKey];
                      if (nestedValue && typeof nestedValue === 'object' && ('dev' in nestedValue || 'prod' in nestedValue)) {
                        const devValue = nestedValue.dev;
                        const prodValue = nestedValue.prod;

                        // dev 값이 비어있지 않으면 표시 (객체인 경우 JSON 문자열로 변환)
                        const devValueStr = typeof devValue === 'string' ? devValue : devValue && typeof devValue === 'object' ? JSON.stringify(devValue, null, 2) : '';

                        if (devValueStr.trim() !== '') {
                          newState[resourceKey].push({
                            id: `${resourceId}-${key}.${nestedKey}`,
                            fieldName: `${key}.${nestedKey}`,
                            devValue: devValueStr,
                            prodValue: typeof prodValue === 'string' ? prodValue : prodValue && typeof prodValue === 'object' ? JSON.stringify(prodValue, null, 2) : '',
                          });
                        }
                      }
                    });
                  } else if (key === 'agent_app_nodes' && typeof fieldValue === 'object' && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    // agent_app_nodes 처리: dev/prod가 배열인 경우
                    const devValue = fieldValue.dev;
                    const prodValue = fieldValue.prod;

                    // 빈 배열인 경우 표시하지 않음
                    if (Array.isArray(devValue) && devValue.length === 0) {
                      return;
                    }

                    // dev 값이 비어있지 않으면 표시 (배열인 경우 JSON 문자열로 변환)
                    const devValueStr =
                      typeof devValue === 'string'
                        ? devValue
                        : Array.isArray(devValue) && devValue.length > 0
                          ? JSON.stringify(devValue, null, 2)
                          : devValue && typeof devValue === 'object'
                            ? JSON.stringify(devValue, null, 2)
                            : '';

                    if (devValueStr.trim() !== '') {
                      const prodValueStr =
                        typeof prodValue === 'string'
                          ? prodValue
                          : Array.isArray(prodValue) && prodValue.length > 0
                            ? JSON.stringify(prodValue, null, 2)
                            : prodValue && typeof prodValue === 'object'
                              ? JSON.stringify(prodValue, null, 2)
                              : '';

                      newState[resourceKey].push({
                        id: `${resourceId}-${key}`,
                        fieldName: key,
                        devValue: devValueStr,
                        prodValue: prodValueStr,
                      });
                    }
                  } else if (fieldValue && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    // 일반 필드 처리 (server_url 등)
                    const devValue = fieldValue.dev ?? '';
                    // dev 값이 비어있으면 노출하지 않음
                    if (typeof devValue === 'string' && devValue.trim() !== '') {
                      newState[resourceKey].push({
                        id: `${resourceId}-${key}`,
                        fieldName: key,
                        devValue: devValue,
                        prodValue: fieldValue.prod || '',
                      });
                    }
                  }
                }
              });
            }
          });
        }
      });

      // 기존 state와 병합하여 사용자가 입력한 prodValue 값 유지
      setResourceFieldsState(prev => {
        const merged: Record<string, FieldData[]> = {};
        Object.keys(newState).forEach(resourceKey => {
          const newFields = newState[resourceKey];
          const prevFields = prev[resourceKey] || [];

          // 기존 필드와 매칭하여 prodValue 유지 (id로 매칭)
          merged[resourceKey] = newFields.map(newField => {
            const prevField = prevFields.find(p => p.id === newField.id);
            return prevField || newField;
          });
        });
        return merged;
      });
    }
  }, [resourceEndpointsData]);

  // 필드 값 변경 함수
  const updateField = (resourceKey: string, id: string, value: string) => {
    setResourceFieldsState(prev => {
      const currentFields = prev[resourceKey] || [];
      return {
        ...prev,
        [resourceKey]: currentFields.map(field => (field.id === id ? { ...field, prodValue: value } : field)),
      };
    });
  };

  // resourceFieldsState를 기반으로 아코디언 아이템 생성
  const createPromptAccordionItems = useMemo(() => {
    if (Object.keys(resourceFieldsState).length === 0) {
      return [];
    }

    return Object.keys(resourceFieldsState)
      .map(resourceKey => {
        const fields = resourceFieldsState[resourceKey];

        if (!fields || fields.length === 0) {
          return null;
        }

        // resourceKey에서 리소스 타입과 ID 추출 (예: "MCP-804516cd-a07b-402e-b9b2-a1a54e05be7c")
        const [resourceType, ...resourceIdParts] = resourceKey.split('-');
        const resourceId = resourceIdParts.length > 0 ? resourceIdParts.join('-') : '';

        // 리소스 ID가 있으면 제목에 표시 (같은 타입이 여러 개일 때 구분)
        const title =
          resourceId && Object.keys(resourceFieldsState).filter(key => key.startsWith(resourceType + '-')).length > 1
            ? `${resourceType} (${resourceId.substring(0, 8)}...)`
            : resourceType;

        return {
          title: title,
          content: <ResourceFieldSection fields={fields} onProdValueChange={(id, value) => updateField(resourceKey, id, value)} />,
          defaultOpen: false,
          showNoticeIcon: false,
        };
      })
      .filter(Boolean) as any[];
  }, [resourceFieldsState]);

  // category에 따른 필드명 결정
  const getItemNameLabel = () => {
    if (migDeployData.category === 'SERVING_MODEL' || migDeployData.category === 'AGENT_APP') {
      return '배포명';
    } else if (migDeployData.category === 'KNOWLEDGE') {
      return '지식명';
    } else if (migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS') {
      return '이름';
    }
    return '이름';
  };

  const handleClose = () => {
    resetMigDeployData();
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => {},
    });
  };

  const handleNextStep = () => {
    // 추출된 필드가 없으면 바로 다음 단계로 이동
    const hasExtractedFields = Object.keys(resourceFieldsState).length > 0 && Object.values(resourceFieldsState).some(fields => fields.length > 0);

    if (!hasExtractedFields) {
      // 추출된 필드가 없으면 바로 다음 단계로
      onNextStep();
      return;
    }

    // 필수 입력 체크: 모든 필드의 prodValue가 입력되었는지 확인
    const hasEmptyFields = Object.keys(resourceFieldsState).some(resourceKey => {
      const fields = resourceFieldsState[resourceKey];
      return fields.some(field => !field.prodValue || field.prodValue.trim() === '');
    });

    if (!hasEmptyFields) {
      // 모두 입력되었을 때 알림 표시
      openModal({
        type: '2xsmall',
        title: '안내',
        body: <MigDeployValidationBody message='운영 배포를 위한 최종 파일 생성이 완료되었습니다. 최종 정보를 확인 후, [배포] 버튼을 눌러주세요.' />,
        confirmText: '예',
        onConfirm: () => {
          // Step3에서 입력한 prod 값을 migDeployData에 저장
          updateMigDeployData({ resourceFieldsState });
          onNextStep();
        },
      });
      return;
    }

    // 필수 입력이 안 된 경우
    openAlert({
      title: '안내',
      message: '운영용 정보를 모두 입력해주세요.',
      confirmText: '확인',
    });
  };

  const handlePreviousStep = () => {
    onPreviousStep();
  };

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='운영 이행' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
               <UIStepper items={migDeployData.category === 'PROJECT' || migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS' ? [stepperItems[0], stepperItems[1], { ...stepperItems[3], step: 3 }] : stepperItems} currentStep={3} direction='vertical' /></UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                  취소
                </Button>
                <Button className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
                  이행
                </Button>
              </UIUnitGroup>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='운영용 정보 입력' description='외부 엔드포인트 기반 에셋이 운영 환경에서 정상적으로 동작하기 위해 운영용 정보를 입력해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {`선이행 이력이 있는 경우, 자동 입력된 운영 정보를 확인하실 수 있습니다. 필요한 경우 수정해주세요. `}
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {`입력한 값은 개발용 설정을 운영용으로 대체하여 최종 파일 생성에 반영됩니다.`}
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </UIUnitGroup>
              </div>
            </UIArticle>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  이행 정보
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
                            프로젝트명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {migDeployData.prjNm || ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            분류
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {MIG_DEPLOY_CATEGORY_MAP[migDeployData.category] || ''}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            {getItemNameLabel()}
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {(() => {
                              const nameValue = migDeployData.name;
                              return typeof nameValue === 'string' ? nameValue : (nameValue as any)?.name || String(nameValue || '');
                            })()}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle>
              <UIAccordion items={createPromptAccordionItems} variant='box' allowMultiple={true} />
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <Button className='btn-secondary-gray' onClick={handlePreviousStep}>
                  이전
                </Button>
                <Button className='btn-secondary-blue' onClick={handleNextStep}>
                  다음
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
}
