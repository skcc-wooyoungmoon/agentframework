import React, { useCallback, useMemo, useState } from 'react';

import { UIButton2, UIIcon2, UIPagination, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIDataCnt } from '@/components/UI/atoms/UIDataCnt';
import { UIArticle, UIFormField, UIGroup, UIInput, UIList, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIModalContent } from '@/components/UI/molecules/modal';
import { MODAL_ID } from '@/constants/modal/modalId.constants';
import { useCreateApiKey, useGetApiKeyList } from '@/services/deploy/apikey/apikey.services';
import { useModal } from '@/stores/common/modal';

import { API_KEY_QUOTA_TYPE_OPTIONS } from '../../../../constants/deploy/apikey.constants';

import { useLayerPopup } from '@/hooks/common/layer';
import { useCopyHandler } from '@/hooks/common/util';
import { PayReqWizard } from '@/pages/common/PayReqWizrad';
import { useCheckApprovalStatus } from '@/services/common/payReq.service';
import type { GetCheckApiEndpointResponse } from '@/services/deploy/apigw/types';
import type { ApiKeyScope, CreateApiKeyRequest } from '@/services/deploy/apikey/types';
import { useUser } from '@/stores';
import type { ColDef } from 'ag-grid-community';

// Body 컴포넌트를 함수 외부로 이동
/**
 *
 * @author SGO1032948
 * @description 사용자 API Key 발급 모달 컴포넌트
 * DP_010102_P02
 */
const Body = ({
  id,
  scope,
  // hasUserApiKey,
  uniqueKey,
  handleSubmit,
}: {
  id: string;
  scope: ApiKeyScope;
  name: string;
  uniqueKey: string;
  // hasUserApiKey: boolean;
  handleSubmit: (type: 'USE' | 'ETC', request?: CreateApiKeyRequest) => void;
} & ApiKeyListProps) => {
  console.log('scope', scope);
  const { openAlert, closeModal } = useModal();

  //// 데이터
  const [_data, _setData] = useState<{ value: 'USE' | 'ETC'; etcName: string }>({ value: 'USE', etcName: '' });

  //// 핸들러
  const handleChange = useCallback(
    (value: string) => {
      _setData(prev => ({ ...prev, value: value as 'USE' | 'ETC' }));
    },
    [_setData]
  );

  const handleEtcNameChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      _setData(prev => ({ ...prev, etcName: e.target.value }));
    },
    [_setData]
  );

  //// API
  const { mutate: createApiKey } = useCreateApiKey();

  // 결재 상태 조회
  const { data: approvalStatus } = useCheckApprovalStatus(
    {
      approvalUniqueKey: uniqueKey,
    },
    {
      enabled: !!uniqueKey,
      refetchOnMount: 'always',
    }
  );
  //// 발급 처리
  const handleCreateApiKey = () => {
    // 사용자 API Key 중복 체크
    // if (_data.value === 'USE' && hasUserApiKey) {
    //   openAlert({
    //     title: '안내',
    //     message: '이미 발급된 API Key가 존재합니다.',
    //   });
    //   return;
    // }
    // 기타 API Key 결재 중복 체크
    if (_data.value === 'ETC') {
      // 결재 중복 체크
      if (approvalStatus?.inProgress) {
        openAlert({
          title: '안내',
          message: `동일한 ${scope === 'model' ? '모델' : '에이전트'}에 대한 외부시스템 API Key 발급 요청이 이미 진행 중입니다. 
                    기존 요청 처리 완료 후 다시 시도해주세요.`,
        });
        return;
      }
      closeModal(MODAL_ID.API_KEY_ISSUE);
      // 결재
      handleSubmit(_data.value, {
        name: _data.etcName,
        type: 'ETC',
        scope,
        uuid: id,
      });
    } else {
      // 사용자 API Key 발급
      createApiKey(
        {
          name: _data.etcName,
          type: 'USE',
          scope,
          uuid: id,
        },
        {
          onSuccess: () => {
            openAlert({
              title: '안내',
              message: 'API Key 발급이 완료되었습니다.',
              onConfirm: () => {
                handleSubmit(_data.value);
                closeModal(MODAL_ID.API_KEY_ISSUE);
              },
            });
          },
        }
      );
    }
  };

  return (
    <section className='section-modal'>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            구분
          </UITypography>
          <UIUnitGroup gap={8} direction='column'>
            <UIRadio2
              name='basic2'
              label='사용자'
              value='USE'
              checked={_data.value === 'USE'}
              onChange={(_, value) => {
                handleChange(value);
              }}
            />
            <UIRadio2
              name='basic2'
              label='기타'
              value='ETC'
              checked={_data.value === 'ETC'}
              onChange={(_, value) => {
                handleChange(value);
              }}
            />
          </UIUnitGroup>
        </UIFormField>
      </UIArticle>
      {_data.value === 'ETC' && (
        <UIArticle>
          <UIFormField gap={8} direction='column'>
            <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
              이름
            </UITypography>
            <UIUnitGroup gap={8} direction='column' align='start'>
              <UIInput.Text
                value={_data.etcName}
                placeholder='이름 입력'
                maxLength={50}
                onChange={e => {
                  handleEtcNameChange(e);
                }}
              />
              <UIList
                gap={4}
                direction='column'
                className='ui-list_bullet'
                data={[
                  {
                    dataItem: (
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {`발급할 API Key의 이름을 입력해주세요. (예시: 우리은행 광교영업점)`}
                      </UITypography>
                    ),
                  },
                ]}
              />
            </UIUnitGroup>
          </UIFormField>
        </UIArticle>
      )}
      <UIModalContent.Footer
        type={'modal-medium'}
        positiveButton={{
          text: '확인',
          disabled: _data.value === 'ETC' && (_data.etcName.length === 0 || _data.etcName.length > 50),
          onClick: () => {
            handleCreateApiKey();
          },
        }}
      />
    </section>
  );
};

type ApiKeyListProps = {
  scope: ApiKeyScope;
  id: string;
  name: string;
  apiGwStatus?: GetCheckApiEndpointResponse;
};

/**
 * @author SGO1032948
 * @description 사용자 API Key 목록 조회
 */
export function ApiKeyList({ scope, id, name, apiGwStatus }: ApiKeyListProps) {
  const uuid = `${scope}-${id}`;
  const { openModal, openAlert } = useModal();
  const { user } = useUser();
  // 목록 조회
  const {
    data: apiKeyList,
    refetch,
    isLoading,
  } = useGetApiKeyList({
    uuid,
  });
  // const hasUserApiKey = useMemo(() => apiKeyList?.content.some(item => item.type === 'USE'), [apiKeyList]);
  //// 사용자 API Key 목록 조회
  // 검색 조건
  const [searchValues, setSearchValues] = useState({
    page: 1,
    size: 6,
    searchKeyword: '',
    status: '',
  });

  //// 결재
  const uniqueKey = useMemo(() => `${user.userInfo.memberId}-${id}`, [user.userInfo.memberId, id]);
  // PayReqWizard 닫기 핸들러
  const payReqWizardPopup = useLayerPopup();
  const handlePayReqWizardClose = () => {
    payReqWizardPopup.onClose();
  };
  // PayReqWizard에 전달할 프로젝트 정보를 별도로 보관
  const [approvalInfo, setApprovalInfo] = useState<{
    memberId: string;
    approvalType: string; // 업무코드
    approvalUniqueKey?: string; // 요청식별자 (중복방지 등 목적으로 각 업무에서 활용)
    approvalParamKey?: number; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalParamValue?: string; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalItemString: string; // 요청하는 대상/작업 이름 (알람 표시 목적)
    afterProcessParamString: string; // 후처리 변수
    approvalSummary?: string; // 결재사유 메세지
    apprivalTableInfo?: { key: string; value: string }[][];
  }>({
    memberId: '',
    approvalType: '',
    approvalUniqueKey: uniqueKey,
    afterProcessParamString: '',
    approvalItemString: '',
  });

  //// 완료 처리
  const handleSubmit = (keyType: 'USE' | 'ETC', request?: CreateApiKeyRequest) => {
    if (keyType === 'USE') {
      refetch();
    } else {
      /// 결재 시작
      payReqWizardPopup.onOpen();
      setApprovalInfo({
        memberId: user.userInfo.memberId,
        approvalType: '06', // 외부시스템 API Key 발급
        approvalUniqueKey: `${user.userInfo.memberId}-${id}`, // 요청 식별자
        approvalItemString: `${request?.name ?? ''}`, // 요청하는 대상/작업 이름 (알람 표시 목적) <- 발급명
        afterProcessParamString: JSON.stringify(request),
        apprivalTableInfo: [
          [
            { key: 'API Key 이름', value: request?.name ?? '' },
            { key: '연결 대상', value: name },
          ],
        ],
      });
    }
  };

  const handleOpen = () => {
    if (apiGwStatus?.status !== 'SUCCESS') {
      openAlert({
        title: '안내',
        message: 'API Gateway 배포가 완료된 후 API Key를 발급할 수 있습니다.',
      });
      return;
    }
    openModal(
      {
        type: 'medium',
        title: 'API Key 발급',
        body: <Body id={id} scope={scope} name={name} uniqueKey={uniqueKey} handleSubmit={handleSubmit} />,
        useCustomFooter: true,
      },
      {
        modalId: MODAL_ID.API_KEY_ISSUE,
        confirm: true,
      }
    );
  };

  const { handleCopy } = useCopyHandler();

  const apiKeyColumns: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        headerName: '이름',
        field: 'name',
        width: 272,
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
        headerName: '구분',
        field: 'type',
        width: 230,
        valueFormatter: (params: { value: string }) => {
          return params.value === 'USE' ? '사용자' : '기타';
        },
      },
      {
        headerName: 'Key',
        field: 'apiKey',
        flex: 1,
        showTooltip: false,
        cellRenderer: React.memo((params: any) => {
          return (
            <div className='flex align-center gap-1'>
              {params.value}
              <a href='#none' onClick={() => handleCopy(params.value)}>
                <UIIcon2 className='ic-system-20-copy-gray' style={{ display: 'block' }} />
              </a>
            </div>
          );
        }),
      },
      {
        headerName: 'Quota',
        field: 'quota',
        width: 180,
        valueFormatter: (params: any) => {
          // object인 경우 문자열로 변환, 이미 문자열인 경우 그대로 반환
          if (typeof params.value === 'object' && params.value !== null) {
            return `${params.value.value}회 / ${API_KEY_QUOTA_TYPE_OPTIONS[params.value.type as keyof typeof API_KEY_QUOTA_TYPE_OPTIONS]}`;
          }
          return params.value || '';
        },
      },
      {
        headerName: '호출 횟수',
        field: 'usedCount',
        width: 180,
        valueFormatter: (params: any) => {
          // object인 경우 처리, 이미 문자열인 경우 그대로 반환
          if (typeof params.value === 'object' && params.value !== null) {
            return `${params.value}회`;
          }
          return params.value || '';
        },
      },
    ],
    []
  );

  // 클라이언트 사이드 페이징 처리
  const paginatedData = useMemo(() => {
    if (!apiKeyList?.content) return { rowData: [], totalPages: 0, totalElements: 0 };

    const allData = apiKeyList.content;
    const startIndex = (searchValues.page - 1) * searchValues.size;
    const endIndex = startIndex + searchValues.size;
    const paginatedContent = allData.slice(startIndex, endIndex);

    const rowData = paginatedContent.map((item, index) => ({
      no: startIndex + index + 1,
      ...item,
      quota: `${item.quota.value}회 / ${API_KEY_QUOTA_TYPE_OPTIONS[item.quota.type as keyof typeof API_KEY_QUOTA_TYPE_OPTIONS]}`,
      usedCount: `${item.usedCount}회 / ${API_KEY_QUOTA_TYPE_OPTIONS[item.quota.type as keyof typeof API_KEY_QUOTA_TYPE_OPTIONS]}`,
    }));

    const totalElements = allData.length;
    const totalPages = Math.ceil(totalElements / searchValues.size);

    return { rowData, totalPages, totalElements };
  }, [apiKeyList?.content, searchValues.page, searchValues.size]);

  return (
    <>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='w-full'>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='w-full'>
                    <UIGroup gap={12} direction='row' align='start'>
                      <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                        <UIDataCnt count={paginatedData.totalElements} prefix='API Key 총' />
                      </div>
                    </UIGroup>
                  </div>
                  <div>
                    <UIButton2 className='btn-tertiary-outline' onClick={handleOpen}>
                      발급
                    </UIButton2>
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='default' rowData={paginatedData.rowData} columnDefs={apiKeyColumns} loading={isLoading} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              className='flex justify-center'
              currentPage={searchValues.page}
              totalPages={paginatedData.totalPages || 1}
              onPageChange={(page: number) => {
                setSearchValues(prev => ({ ...prev, page }));
              }}
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
      {/* 프로젝트 생성 성공 후 PayReqWizard 표시 */}
      {payReqWizardPopup.currentStep === 1 && (
        <PayReqWizard
          isOpen={payReqWizardPopup.currentStep === 1}
          onClose={handlePayReqWizardClose}
          approvalInfo={approvalInfo} // 저장된 프로젝트 정보 전달
        />
      )}
    </>
  );
}
