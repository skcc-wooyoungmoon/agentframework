import { Button } from '@/components/common/auth';
import { UIDataCnt, UIIcon2, UIPagination, UITypography } from '@/components/UI/atoms';
import {
  UIAccordion,
  UIArticle,
  UIFormField,
  UIGroup,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  UITextArea2,
  UIUnitGroup,
  type UIStepperItem,
} from '@/components/UI/molecules';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIGrid, UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCopyFolder, useGetMigResourceEndpoints } from '@/services/deploy/mig/mig.services';
import { useGetSafetyFilterList } from '@/services/deploy/safetyFilter/safetyFilter.service';
import { useGetGuardRailList } from '@/services/prompt/guardRail/guardRail.services';
import { useModal } from '@/stores/common/modal/useModal';
import { MIG_DEPLOY_CATEGORY_MAP, type FieldData } from '@/stores/deploy/types';
import { useMigDeploy } from '@/stores/deploy/useMigDeploy';
import React, { useEffect, useMemo, useState } from 'react';

/**
 * 금지어 객체 배열을 쉼표로 구분된 문자열로 변환
 *
 * AG Grid에서 객체 배열을 직접 렌더링하면 React 에러가 발생하므로,
 * 미리 문자열로 변환하여 안전하게 표시
 *
 * @param stopWords - 금지어 객체 배열 (예: [{ id: 'uuid', stopWord: '@GMAIL.COM' }])
 * @returns 쉼표로 구분된 금지어 문자열 (예: '@GMAIL.COM, @NAVER.COM')
 */
const stringifyStopWords = (stopWords?: { id?: string; stopWord?: string }[]) => {
  if (!Array.isArray(stopWords)) return '';

  return stopWords
    .map(item => (typeof item?.stopWord === 'string' ? item.stopWord : ''))
    .filter(Boolean)
    .join(', ');
};

// 필드 쌍 컴포넌트 (dev/prod) - readonly 버전
interface FieldPairProps {
  fieldName: string;
  devValue: string;
  prodValue: string;
  isLongText?: boolean; // script 같은 긴 텍스트인지 여부
}

const FieldPairReadOnly = ({ fieldName, devValue, prodValue, isLongText = false }: FieldPairProps) => {
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
        <UITextArea2 value={prodValue} placeholder='직접 입력' style={{ height: isLongText ? '200px' : '80px' }} disabled={true} resizable={false} readOnly={true} />
      </UIUnitGroup>
    </UIFormField>
  );
};

// FieldData는 types.ts에서 import

// 리소스 필드 섹션 컴포넌트 (readonly)
interface ResourceFieldSectionProps {
  fields: FieldData[];
}

const ResourceFieldSectionReadOnly = ({ fields }: ResourceFieldSectionProps) => {
  return (
    <UIGroup direction='column' gap={16}>
      {fields.map(field => (
        <FieldPairReadOnly key={field.id} fieldName={field.fieldName} devValue={field.devValue} prodValue={field.prodValue} isLongText={field.fieldName === 'script'} />
      ))}
    </UIGroup>
  );
};

interface MigDeployStep4FinalCheckPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onPreviousStep: () => void;
  onSuccess?: () => void; // 이행 완료 후 콜백
}

interface MigDeployConfirmBodyProps {
  message: string;
}

function MigDeployConfirmBody({ message }: MigDeployConfirmBodyProps) {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
          운영 배포를 정말 진행하시겠습니까?
        </UITypography>
        <div className='box-fill mt-6'>
          <UIGroup gap={8} direction='row' align='start' vAlign='start'>
            <UIIcon2 className='ic-system-16-info-gray w-[16px] top-[2px] relative' />
            <UITypography variant='body-2' className='secondary-neutral-600 flex-1'>
              {message}
            </UITypography>
          </UIGroup>
        </div>
      </UIArticle>
    </section>
  );
}

export function MigDeployStep4FinalCheckPopupPage({ isOpen, stepperItems = [], onClose, onPreviousStep, onSuccess }: MigDeployStep4FinalCheckPopupPageProps) {
  const { openConfirm, openAlert, openModal } = useModal();
  const { migDeployData, resetMigDeployData } = useMigDeploy();

  // SAFETY_FILTER와 GUARDRAILS의 경우 uuidList를 |로 구분한 문자열로, 다른 경우는 첫 번째 UUID 사용
  const uuid = useMemo(() => {
      // 다른 카테고리: 첫 번째 UUID 사용
      const firstUuid = migDeployData.uuidList[0];
      return typeof firstUuid === 'string' ? firstUuid : (firstUuid as any)?.id || String(firstUuid || '');
  }, [migDeployData.category, migDeployData.uuidList]);

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
  const [resourceFieldsState, setResourceFieldsState] = useState<Record<string, FieldData[]>>({});

  // resourceEndpointsData가 변경되면 state 초기화
  // Map<String, List<Map<String, Object>>> 형식의 데이터를 FieldData[]로 변환
  // Step3에서 입력한 prod 값을 가져와서 사용
  useEffect(() => {
    if (resourceEndpointsData && Object.keys(resourceEndpointsData).length > 0) {
      const newState: Record<string, FieldData[]> = {};
      const savedFieldsState = migDeployData.resourceFieldsState || {};

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
                  const fieldValue = resourceObj[key] as { dev?: string; prod?: string };
                  if (fieldValue && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    const devValue = fieldValue.dev ?? '';
                    // dev 값이 비어있으면 노출하지 않음
                    if (typeof devValue === 'string' && devValue.trim() !== '') {
                      const fieldId = `${resourceId}-${key}`;
                      // Step3에서 입력한 prod 값이 있으면 사용, 없으면 API의 prod 값 사용
                      const savedField = savedFieldsState[resourceKey]?.find(f => f.id === fieldId);
                      newState[resourceKey].push({
                        id: fieldId,
                        fieldName: key,
                        devValue: devValue,
                        prodValue: savedField?.prodValue || fieldValue.prod || '',
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
                      const fieldId = `${resourceId}-${key}`;
                      // Step3에서 입력한 prod 값이 있으면 사용, 없으면 API의 prod 값 사용
                      const savedField = savedFieldsState[resourceKey]?.find(f => f.id === fieldId);
                      const prodValueStr =
                        typeof prodValue === 'string' ? prodValue : prodValue && typeof prodValue === 'object' ? JSON.stringify(prodValue, null, 2) : String(prodValue ?? '');

                      newState[resourceKey].push({
                        id: fieldId,
                        fieldName: key,
                        devValue: devValueStr,
                        prodValue: savedField?.prodValue || prodValueStr,
                      });
                    }
                  }
                });
              }
            } else if (resourceType === 'SERVING_MODEL' || resourceType === 'MODEL') {
              // SERVING_MODEL은 기타 리소스 타입의 경우 모든 필드 추출
              Object.keys(resourceObj).forEach(key => {
                if (key !== 'id' && key !== 'type' && typeof resourceObj[key] === 'object' && resourceObj[key] !== null) {
                  const fieldId = `${resourceId}-${key}`;
                  const savedField = savedFieldsState[resourceKey]?.find(f => f.id === fieldId);
                  const fieldValue = resourceObj[key] as any;
                  if (fieldValue && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    // 일반 필드 처리 (server_url 등)
                    const devValue = fieldValue.dev ?? '';
                    // dev 값이 비어있으면 노출하지 않음
                    newState[resourceKey].push({
                      id: `${resourceId}-${key}`,
                      fieldName: key,
                      devValue: devValue,
                      prodValue: savedField?.prodValue ?? '',
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
                          const fieldId = `${resourceId}-${key}.${nestedKey}`;
                          // Step3에서 입력한 prod 값이 있으면 사용, 없으면 API의 prod 값 사용
                          const savedField = savedFieldsState[resourceKey]?.find(f => f.id === fieldId);
                          const prodValueStr = typeof prodValue === 'string' ? prodValue : prodValue && typeof prodValue === 'object' ? JSON.stringify(prodValue, null, 2) : '';

                          newState[resourceKey].push({
                            id: fieldId,
                            fieldName: `${key}.${nestedKey}`,
                            devValue: devValueStr,
                            prodValue: savedField?.prodValue || prodValueStr,
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
                      const fieldId = `${resourceId}-${key}`;
                      // Step3에서 입력한 prod 값이 있으면 사용, 없으면 API의 prod 값 사용
                      const savedField = savedFieldsState[resourceKey]?.find(f => f.id === fieldId);
                      const prodValueStr =
                        typeof prodValue === 'string'
                          ? prodValue
                          : Array.isArray(prodValue) && prodValue.length > 0
                            ? JSON.stringify(prodValue, null, 2)
                            : prodValue && typeof prodValue === 'object'
                              ? JSON.stringify(prodValue, null, 2)
                              : '';

                      newState[resourceKey].push({
                        id: fieldId,
                        fieldName: key,
                        devValue: devValueStr,
                        prodValue: savedField?.prodValue || prodValueStr,
                      });
                    }
                  } else if (fieldValue && ('dev' in fieldValue || 'prod' in fieldValue)) {
                    // 일반 필드 처리 (server_url 등)
                    const devValue = fieldValue.dev ?? '';
                    // dev 값이 비어있으면 노출하지 않음
                    if (typeof devValue === 'string' && devValue.trim() !== '') {
                      const fieldId = `${resourceId}-${key}`;
                      // Step3에서 입력한 prod 값이 있으면 사용, 없으면 API의 prod 값 사용
                      const savedField = savedFieldsState[resourceKey]?.find(f => f.id === fieldId);
                      newState[resourceKey].push({
                        id: fieldId,
                        fieldName: key,
                        devValue: devValue,
                        prodValue: savedField?.prodValue || fieldValue.prod || '',
                      });
                    }
                  }
                }
              });
            }
          });
        }
      });

      setResourceFieldsState(newState);
    }
  }, [resourceEndpointsData, migDeployData.resourceFieldsState]);

  // resourceFieldsState를 기반으로 아코디언 아이템 생성
  const createResourceAccordionItems = useMemo(() => {
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
          content: <ResourceFieldSectionReadOnly fields={fields} />,
          defaultOpen: false,
          showNoticeIcon: false,
        };
      })
      .filter(Boolean) as any[];
  }, [resourceFieldsState]);

  // 모든 리소스에 대한 파일명과 파일 경로 생성 (1개 파일로 통합)
  const fileInfo = useMemo(() => {
    // PROJECT 카테고리인 경우 고정 값 사용
    if (migDeployData.category === 'PROJECT') {
      return {
        fileNames: 'project_migration_data.json',
        filePath: 'gapdat\\migration\\aiplatform',
      };
    }

    if (!resourceEndpointsData || Object.keys(resourceEndpointsData).length === 0) {
      return { fileNames: '', filePath: '' };
    }

    // SAFETY_FILTER도 단일 선택이므로 uuid 사용
    const folderUuid = uuid; // 폴더명
    const fileUuid = uuid; // 파일명

    // 파일명 형식: {uuid}.json
    const fileName = `${fileUuid}.json`;

    // 파일 경로: gapdat\\migration\\${project_id}\\${type}\\${folderUuid}
    const projectId = Number(migDeployData.prjSeq) < 0 ? 'public' : migDeployData.prjSeq;

    // 폴더명은 _로 구분된 UUID 사용
    const filePath = `gapdat\\migration\\aiplatform\\migration\\${projectId}\\${migDeployData.category}\\${folderUuid}`;

    return { fileNames: fileName, filePath };
  }, [resourceEndpointsData, migDeployData.prjSeq, migDeployData.category, migDeployData.uuidList, uuid]);

  // category에 따른 필드명 결정
  const getItemNameLabel = () => {
    if (migDeployData.category === 'SERVING_MODEL' || migDeployData.category === 'AGENT_APP') {
      return '배포명';
    } else if (migDeployData.category === 'KNOWLEDGE') {
      return '지식명';
    } else if (migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS') {
      return '이름';
    } else if (migDeployData.category === 'PROJECT') {
      return '프로젝트명';
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

  const handlePreviousStep = () => {
    if (migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS' || migDeployData.category === 'PROJECT') {
      onPreviousStep();
      onPreviousStep();
    } else {
      onPreviousStep();
    }
  };

  // Step2에서 선택한 세이프티 필터 ID 목록 가져오기
  const selectedFilterIds = useMemo(() => {
    if (migDeployData.category !== 'SAFETY_FILTER' || !migDeployData.uuidList || migDeployData.uuidList.length === 0) {
      return [];
    }
    // uuidList는 string[] 타입이므로 그대로 사용
    return migDeployData.uuidList.filter(Boolean);
  }, [migDeployData.category, migDeployData.uuidList]);

  // Step2에서 선택한 세이프티 필터 데이터 조회
  const { data: safetyFilterData } = useGetSafetyFilterList(
    {
      size: selectedFilterIds.length || 0,
      filter: selectedFilterIds.length > 0 ? `group_id:${selectedFilterIds.join('|')}` : undefined,
    },
    {
      enabled: isOpen && migDeployData.category === 'SAFETY_FILTER' && selectedFilterIds.length > 0,
    }
  );

  // Step2에서 선택한 가드레일 ID 목록 가져오기
  const selectedGuardRailIds = useMemo(() => {
    if (migDeployData.category !== 'GUARDRAILS' || !migDeployData.uuidList || migDeployData.uuidList.length === 0) {
      return [];
    }
    // uuidList는 string[] 타입이므로 그대로 사용
    return migDeployData.uuidList.filter(Boolean);
  }, [migDeployData.category, migDeployData.uuidList]);

  // Step2에서 선택한 가드레일 데이터 조회
  const { data: guardRailData } = useGetGuardRailList(
    {
      size: selectedGuardRailIds.length || 0,
      filter: selectedGuardRailIds.length > 0 ? `uuid:${selectedGuardRailIds.join('|')}` : undefined,
      project_id: migDeployData.prjSeq,
    },
    {
      enabled: isOpen && migDeployData.category === 'GUARDRAILS' && selectedGuardRailIds.length > 0,
    }
  );

  // Step2에서 선택한 세이프티 필터 데이터를 Step4 컬럼 형식으로 변환
  const safetyFilterRowData = useMemo(() => {
    if (migDeployData.category !== 'SAFETY_FILTER' || !safetyFilterData?.content) {
      return [];
    }
    return safetyFilterData.content.map((item: any) => ({
      id: item.filterGroupId || item.id,
      filterGroupName: item.filterGroupName || '',
      stopWordsText: stringifyStopWords(item.stopWords),
      publicStatus: item.isPublicAsset ? '전체공유' : '내부공유',
      createdAt: item.createdAt || '',
      updatedAt: item.updatedAt || item.createdAt || '',
    }));
  }, [migDeployData.category, safetyFilterData]);

  // Step2에서 선택한 가드레일 데이터를 Step4 컬럼 형식으로 변환
  const guardRailRowData = useMemo(() => {
    if (migDeployData.category !== 'GUARDRAILS' || !guardRailData?.content) {
      return [];
    }
    return guardRailData.content.map((item: any) => {
      return {
        id: item.uuid,
        name: item.name || '',
        description: item.description || '',
        publicStatus: item.isPublicAsset ? '전체공유' : '내부공유',
        createdAt: item.createdAt || '',
        updatedAt: item.updatedAt || item.createdAt || '',
      };
    });
  }, [migDeployData.category, guardRailData]);

  // 카테고리별 rowData 결정
  const rowData = useMemo(() => {
    if (migDeployData.category === 'SAFETY_FILTER') {
      return safetyFilterRowData;
    } else if (migDeployData.category === 'GUARDRAILS') {
      return guardRailRowData;
    }
    return [];
  }, [migDeployData.category, safetyFilterRowData, guardRailRowData]);

  // 세이프티 필터용 그리드 컬럼 정의
  const safetyFilterColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        headerName: '분류',
        field: 'filterGroupName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '금지어',
        field: 'stopWordsText',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '공개범위',
        field: 'publicStatus' as any,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // 가드레일용 그리드 컬럼 정의
  const guardRailColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        field: 'name' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '설명',
        field: 'description' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '공개범위',
        field: 'publicStatus' as any,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // 카테고리별 컬럼 정의 결정
  const columnDefs = useMemo(() => {
    if (migDeployData.category === 'SAFETY_FILTER') {
      return safetyFilterColumnDefs;
    } else if (migDeployData.category === 'GUARDRAILS') {
      return guardRailColumnDefs;
    }
    return safetyFilterColumnDefs;
  }, [migDeployData.category, safetyFilterColumnDefs, guardRailColumnDefs]);

  const { mutate: copyFolder } = useCopyFolder();

  const handleDeploy = () => {
    // 확인 모달 표시
    openModal({
      type: '2xsmall',
      title: '안내',
      body: <MigDeployConfirmBody message='운영 배포 전 포탈 담당자와 사전 협의가 반드시 필요합니다.' />,
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        // 실제 배포 로직 실행
        // PROJECT 카테고리의 경우 resourceEndpointsData가 없어도 배포 진행
        if (migDeployData.category !== 'PROJECT' && (!resourceEndpointsData || Object.keys(resourceEndpointsData).length === 0)) {
          return;
        }

        // Step3에서 입력한 prod 값을 반영한 resourceEndpoints 데이터 생성
        const updatedResourceEndpoints: Record<string, Array<Record<string, any>>> = { ...resourceEndpointsData };
        const savedFieldsState = migDeployData.resourceFieldsState || {};

        // 각 리소스 타입별로 prod 값 업데이트
        Object.keys(updatedResourceEndpoints).forEach(resourceType => {
          const resourceList = updatedResourceEndpoints[resourceType];
          if (Array.isArray(resourceList)) {
            resourceList.forEach((resourceObj, idx) => {
              const resourceId = resourceObj.id || `${resourceType}-${idx + 1}`;
              const resourceKey = `${resourceType}-${resourceId}`; // 리소스 타입과 ID를 조합한 키
              const savedFields = savedFieldsState[resourceKey] || [];

              // 리소스 타입별로 필드 업데이트
              if (resourceType === 'KNOWLEDGE') {
                // KNOWLEDGE의 경우 index_name, script 등 필드 업데이트
                Object.keys(resourceObj).forEach(key => {
                  if (key !== 'id' && key !== 'type' && typeof resourceObj[key] === 'object' && resourceObj[key] !== null) {
                    const savedField = savedFields.find(f => f.id === `${resourceId}-${key}`);
                    if (savedField && savedField.prodValue) {
                      (resourceObj[key] as { prod?: string }).prod = savedField.prodValue;
                    }
                  }
                });
              } else if (resourceType === 'VECTOR_DB') {
                // VECTOR_DB의 경우 connection_info 안의 필드 업데이트
                const connectionInfo = resourceObj.connection_info;
                if (connectionInfo && typeof connectionInfo === 'object') {
                  Object.keys(connectionInfo).forEach(key => {
                    const savedField = savedFields.find(f => f.id === `${resourceId}-${key}`);
                    if (savedField && savedField.prodValue) {
                      (connectionInfo[key] as { prod?: string }).prod = savedField.prodValue;
                    }
                  });
                }
              } else {
                // 기타 리소스 타입의 경우 필드 업데이트
                Object.keys(resourceObj).forEach(key => {
                  if (key !== 'id' && key !== 'type' && typeof resourceObj[key] === 'object' && resourceObj[key] !== null) {
                    const fieldValue = resourceObj[key] as any;

                    // api_param, auth_config 같은 중첩 구조 처리
                    if ((key === 'api_param' || key === 'auth_config') && typeof fieldValue === 'object') {
                      // api_param 내부의 headers, body, params 업데이트
                      // auth_config 내부의 username, password 업데이트
                      Object.keys(fieldValue).forEach(nestedKey => {
                        const nestedValue = fieldValue[nestedKey];
                        if (nestedValue && typeof nestedValue === 'object' && ('dev' in nestedValue || 'prod' in nestedValue)) {
                          const savedField = savedFields.find(f => f.id === `${resourceId}-${key}.${nestedKey}`);
                          if (savedField && savedField.prodValue) {
                            // prodValue가 JSON 문자열인 경우 파싱, 아니면 그대로 사용
                            try {
                              const parsedValue = JSON.parse(savedField.prodValue);
                              (nestedValue as any).prod = parsedValue;
                            } catch {
                              // JSON 파싱 실패 시 문자열로 저장
                              (nestedValue as any).prod = savedField.prodValue;
                            }
                          }
                        }
                      });
                    } else if (key === 'agent_app_nodes' && typeof fieldValue === 'object' && ('dev' in fieldValue || 'prod' in fieldValue)) {
                      // agent_app_nodes 업데이트: prodValue가 JSON 문자열인 경우 파싱
                      const savedField = savedFields.find(f => f.id === `${resourceId}-${key}`);
                      if (savedField && savedField.prodValue) {
                        try {
                          const parsedValue = JSON.parse(savedField.prodValue);
                          (fieldValue as any).prod = parsedValue;
                        } catch {
                          // JSON 파싱 실패 시 문자열로 저장
                          (fieldValue as any).prod = savedField.prodValue;
                        }
                      }
                    } else if (fieldValue && ('dev' in fieldValue || 'prod' in fieldValue)) {
                      // 일반 필드 업데이트 (server_url 등)
                      const savedField = savedFields.find(f => f.id === `${resourceId}-${key}`);
                      if (savedField && savedField.prodValue) {
                        (fieldValue as { prod?: string }).prod = savedField.prodValue;
                      }
                    }
                  }
                });
              }
            });
          }
        });

        // SAFETY_FILTER, GUARDRAILS, PROJECT의 경우 이름이 없으면 조회한 데이터에서 가져오기
        let finalAssetName = migDeployData.name as string;
        if ((migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS') && !finalAssetName) {
          if (migDeployData.category === 'SAFETY_FILTER' && safetyFilterRowData.length > 0) {
            finalAssetName = safetyFilterRowData[0]?.filterGroupName || '';
          } else if (migDeployData.category === 'GUARDRAILS' && guardRailRowData.length > 0) {
            finalAssetName = guardRailRowData[0]?.name || '';
          }
        } else if (migDeployData.category === 'PROJECT' && !finalAssetName) {
          // PROJECT의 경우 프로젝트명을 assetName으로 사용
          finalAssetName = migDeployData.prjNm as string;
        }

        // PROJECT 카테고리인 경우 resourceEndpoints가 없을 수 있으므로 빈 객체 또는 있는 데이터 사용
        const finalResourceEndpoints = migDeployData.category === 'PROJECT' ? updatedResourceEndpoints || {} : updatedResourceEndpoints;

        copyFolder(
          {
            project_id: migDeployData.prjSeq as string,
            type: migDeployData.category as string,
            id: migDeployData.uuidList[0] as string,
            projectName: migDeployData.prjNm as string,
            assetName: finalAssetName || '',
            resourceEndpoints: finalResourceEndpoints, // Step3에서 입력한 prod 값이 반영된 데이터
          },
          {
            onSuccess: () => {
              openAlert({
                title: '안내',
                message: '운영 이행이 완료되었습니다.',
                confirmText: '예',
                onConfirm: () => {
                  onSuccess?.(); // 리스트 재조회
                  onClose();
                },
              });
            },
          }
        );
      },
    });
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
               <UIStepper items={migDeployData.category === 'PROJECT' || migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS' ? [stepperItems[0], stepperItems[1], { ...stepperItems[3], step: 3 }] : stepperItems} currentStep={migDeployData.category === 'PROJECT' || migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS' ? 3 : 4} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                  취소
                </Button>
                <Button className='btn-tertiary-blue' style={{ width: 80 }} onClick={handleDeploy}>
                  이행
                </Button>
              </UIUnitGroup>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='최종 정보 확인' description='이행할 대상들의 최종 정보를 확인 후, 이행 버튼을 클릭해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
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
                      {migDeployData.category !== 'SAFETY_FILTER' && migDeployData.category !== 'GUARDRAILS' && migDeployData.category !== 'PROJECT' && (
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
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            {migDeployData.category === 'SAFETY_FILTER' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='세이프티 필터 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid type='default' rowData={rowData as any} columnDefs={columnDefs as any} />
                  </UIListContentBox.Body>
                  {rowData.length > 0 && (
                    <UIListContentBox.Footer>
                      <UIPagination currentPage={1} hasNext={false} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                    </UIListContentBox.Footer>
                  )}
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'GUARDRAILS' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='가드레일 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid type='default' rowData={rowData as any} columnDefs={columnDefs as any} />
                  </UIListContentBox.Body>
                  {rowData.length > 0 && (
                    <UIListContentBox.Footer>
                      <UIPagination currentPage={1} hasNext={false} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                    </UIListContentBox.Footer>
                  )}
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'PROJECT' && (
              <UIArticle className='article-grid'>
                <div className='box-fill'>
                  <UIGroup gap={8} direction='row' align='start' vAlign='start'>
                    <UIIcon2 className='ic-system-16-info-gray w-[16px] top-[2px] relative' />
                    <UITypography variant='body-2' className='secondary-neutral-600 flex-1'>
                      선택한 프로젝트 정보와 해당 프로젝트 내 역할 정보가 함께 배포됩니다.
                    </UITypography>
                  </UIGroup>
                </div>
              </UIArticle>
            )}

            {migDeployData.category !== 'SAFETY_FILTER' && migDeployData.category !== 'GUARDRAILS' && (
              <UIArticle>
                <UIAccordion items={createResourceAccordionItems} variant='box' allowMultiple={true} />
              </UIArticle>
            )}

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  파일 정보
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
                            최종 파일명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {fileInfo.fileNames || '-'}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            파일 경로
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {fileInfo.filePath || '-'}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <Button className='btn-secondary-gray' onClick={handlePreviousStep}>
                  이전
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
}
