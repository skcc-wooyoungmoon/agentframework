import { useAtomValue } from 'jotai';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UILabel } from '@/components/UI/atoms/UILabel';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useBlockApiKey, useDeleteApiKey, useGetAdminApiKeyDetail, useRestoreApiKey } from '@/services/deploy/apikey/apikey.services';
import { selectedApiKeyAtom } from '@/stores/admin/apiKeyMgmt';
import { useModal } from '@/stores/common/modal';
import { API_KEY_QUOTA_TYPE_OPTIONS } from '../../../constants/deploy/apikey.constants';
import { dateUtils } from '../../../utils/common';
import { ApiKeyMgmtUpdatePage } from './ApiKeyMgmtUpdatePage';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useCopyHandler } from '@/hooks/common/util';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';

export const ApiKeyMgmtBasicPage = () => {
  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();
  const { handleCopy } = useCopyHandler();
  const selectedApiKey = useAtomValue(selectedApiKeyAtom);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);

  // id가 없을 경우 localStorage에서 가져오기
  const apiKeyId = selectedApiKey?.id || localStorage.getItem(STORAGE_KEYS.SEARCH_VALUES.API_KEY_MGMT_DETAIL_ID) || '';

  const { data: apiKeyData, refetch } = useGetAdminApiKeyDetail(
    { id: apiKeyId },
    {
      enabled: !!apiKeyId,
    }
  );

  useEffect(() => {
    if (apiKeyId) {
      refetch();
    }
  }, [apiKeyId, refetch]);

  useEffect(() => {
    const handleQuotaUpdated = () => {
      refetch();
    };

    window.addEventListener('quotaUpdated', handleQuotaUpdated);
    return () => {
      window.removeEventListener('quotaUpdated', handleQuotaUpdated);
    };
  }, [refetch]);

  const { mutate: deleteApiKey } = useDeleteApiKey(apiKeyId);

  const { mutate: blockApiKey } = useBlockApiKey(apiKeyId);

  const { mutate: restoreApiKey } = useRestoreApiKey(apiKeyId);

  const getTypeLabel = (type: string): string => {
    const typeMap: { [key: string]: string } = {
      USE: '사용자',
      ETC: '기타',
    };
    return typeMap[type.toUpperCase()] || type;
  };

  const handleCopyApiKey = async () => {
    const apiKey = apiKeyData?.apiKey;
    if (!apiKey) return;

    await handleCopy(apiKey);
  };

  const handleBlock = () => {
    if (!apiKeyId) {
      openAlert({
        title: '안내',
        message: 'API Key 정보를 찾을 수 없습니다.',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '해당 Key를 사용 차단 처리하시겠어요?\n사용 차단 처리한 API Key 는 더이상 사용이 불가합니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        blockApiKey({ apiKeyId: apiKeyId } as any, {
          onSuccess: () => {
            openAlert({
              title: '안내',
              message: 'API Key가 차단되었습니다.\n추후 사용을 원할 경우, 차단 해제 를 통해 다시 활성화 처리가 가능합니다.',
              confirmText: '확인',
              onConfirm: () => { 
                refetch();
              },
            });
          },
          onError: () => {
            openAlert({
              title: '실패',
              message: 'API Key 사용 차단에 실패했습니다.',
            });
          },
        });
      },
    });
  };

  const handleRestore = () => {
    if (!apiKeyId) {
      openAlert({
        title: '안내',
        message: 'API Key 정보를 찾을 수 없습니다.',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '해당 Key를 차단 해제 처리하시겠어요?\n차단 해제가 완료되면, 해당 API Key를 다시 사용하실 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        restoreApiKey({ apiKeyId: apiKeyId } as any, {
          onSuccess: () => {
            openAlert({
              title: '안내',
              message: 'API Key 사용 차단이 해제되었습니다.\n지금부터 해당 API Key를 다시 사용하실 수 있습니다.',
              confirmText: '확인',
              onConfirm: () => {
                refetch();
              },
            });
          },
          onError: () => {
            openAlert({
              title: '실패',
              message: 'API Key 차단 해제에 실패했습니다.',
            });
          },
        });
      },
    });
  };

  const handleDelete = () => {
    if (!apiKeyId) {
      openAlert({
        title: '안내',
        message: 'API Key 정보를 찾을 수 없습니다.',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제 처리한 API Key 는 더이상 사용하실 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteApiKey({} as any, {
          onSuccess: () => {
            openAlert({
              title: '안내',
              message: 'API Key가 삭제되었습니다.',
              confirmText: '확인',
              onConfirm: () => {
                navigate('/admin/api-key-mgmt', { replace: true });
              },
            });
          },
          onError: () => {
            openAlert({
              title: '실패',
              message: 'API Key 삭제에 실패했습니다.',
            });
          },
        });
      },
    });
  };

  return (
    <>
      {!apiKeyData && (

        /* 데이터 없을 경우 */
                <div className='flex-1 flex items-center justify-center h-[220px]'>
                <div className='flex flex-col justify-center items-center gap-3'>
                    <span className='ico-nodata'>
                        <UIIcon2 className='ic-system-80-default-nodata' />
                    </span>
                    <span className='text-body-1 secondary-neutral-500'>조회된 데이터가 없습니다.</span>
                </div>
                </div>
      )}

      {apiKeyData && (
        <>
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
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {apiKeyData?.name || '-'}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          프로젝트명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {apiKeyData?.projectName || '-'}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          구분
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {apiKeyData?.type ? getTypeLabel(apiKeyData.type) : '-'}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          연결 대상
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {(apiKeyData as any)?.permission || '-'}
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
              <UIUnitGroup gap={0} direction='row' align='space-between'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  API Key
                </UITypography>
                {(apiKeyData as any)?.expired ? (
                  <Button auth={AUTH_KEY.ADMIN.API_KEY_DEACTIVATE} className='btn-option-outline' onClick={handleRestore}>
                    차단 해제
                  </Button>
                ) : (
                  <Button auth={AUTH_KEY.ADMIN.API_KEY_DEACTIVATE} className='btn-option-outline' onClick={handleBlock}>
                    사용 차단
                  </Button>
                )}
              </UIUnitGroup>
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
                          API Key
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <div className='flex items-center gap-2 break-all'>
                            <span>{apiKeyData?.apiKey || '-'}</span>
                            <button onClick={handleCopyApiKey} className='cursor-pointer hover:opacity-70 flex-shrink-0'>
                              <UIIcon2 className='ic-system-20-copy-gray' style={{ display: 'block' }} />
                            </button>
                          </div>
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        {(apiKeyData as any)?.expired ? (
                          <UILabel variant='badge' intent='error'>
                            사용차단
                          </UILabel>
                        ) : (
                          <UILabel variant='badge' intent='complete'>
                            사용가능
                          </UILabel>
                        )}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UIUnitGroup gap={0} direction='row' align='space-between'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  Quota
                </UITypography>
                {!(apiKeyData as any)?.expired && (
                  <Button auth={AUTH_KEY.ADMIN.API_KEY_QUOTA_UPDATE} className='btn-option-outline' onClick={() => setIsUpdateModalOpen(true)}>
                    Quota 수정
                  </Button>
                )}
              </UIUnitGroup>
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
                          Quota
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {apiKeyData?.quota?.value || '0'}회 / {API_KEY_QUOTA_TYPE_OPTIONS[apiKeyData?.quota?.type as keyof typeof API_KEY_QUOTA_TYPE_OPTIONS]}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          호출 횟수
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {(apiKeyData as any)?.usedCount || '0'}회 / {API_KEY_QUOTA_TYPE_OPTIONS[apiKeyData?.quota?.type as keyof typeof API_KEY_QUOTA_TYPE_OPTIONS]}
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
                담당자 정보
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
                          생성자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {apiKeyData?.belongsTo?.name || '-'} ㅣ {apiKeyData?.belongsTo?.department || '-'}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {dateUtils.formatDate(apiKeyData?.createdAt, 'datetime')}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.ADMIN.API_KEY_DELETE} className='btn-primary-blue' onClick={handleDelete}>
                삭제
              </Button>
            </UIUnitGroup>
          </UIArticle>

          <ApiKeyMgmtUpdatePage isOpen={isUpdateModalOpen} onClose={() => setIsUpdateModalOpen(false)} apiKeyData={apiKeyData} />
        </>
      )}
    </>
  );
};
