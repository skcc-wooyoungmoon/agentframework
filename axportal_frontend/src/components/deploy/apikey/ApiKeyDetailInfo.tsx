import { ManagerInfoBox } from '@/components/common';
import { UIButton2, UIIcon2, UILabel, UITooltip, UITypography } from '@/components/UI';
import { UIFormField } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { API_KEY_QUOTA_TYPE_OPTIONS, API_KEY_TYPE_OPTIONS } from '@/constants/deploy/apikey.constants';
import { useCopyHandler } from '@/hooks/common/util';
import { useGetApiKeyDetail } from '@/services/deploy/apikey/apikey.services';

/**
 * @author SGO1032948
 * @description API Key 상세
 *
 * DP_030102
 */
export const ApiKeyDetailInfo = ({ apiKeyId }: { apiKeyId: string }) => {
  const { data } = useGetApiKeyDetail({ id: apiKeyId || '' });
  const { handleCopy } = useCopyHandler();

  const handleCopyApiKey = async () => {
    await handleCopy(data?.apiKey ?? '');
  };
  return (
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
                      {data?.name}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      프로젝트명
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {data?.projectName}
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
                      {API_KEY_TYPE_OPTIONS.find(option => option.value === data?.type)?.label}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      연결 대상
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {data?.permission}
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
            API Key
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '624px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '624px' }} />
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
                      <div className='flex align-center items-center gap-2'>
                        <span>{data?.apiKey}</span>
                        <a href='#' onClick={handleCopyApiKey}>
                          <UIIcon2 className='ic-system-20-copy-gray' style={{ display: 'block' }} />
                        </a>
                      </div>
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      상태
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      <div className='flex align-center items-center gap-2'>
                        <UILabel variant='badge' intent={data?.expired ? 'error' : 'complete'}>
                          {data?.expired ? '사용차단' : '사용가능'}
                        </UILabel>
                      </div>
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
          <UIFormField gap={8} direction='column'>
            <div className='inline-flex items-center'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                Quota
              </UITypography>
              <UITooltip
                trigger='click'
                position='bottom-start'
                type='notice'
                items={['Quota 수정을 원할 경우 포탈 관리자 또는 프로젝트 관리자에게 문의해주세요.']}
                bulletType='default'
                showArrow={false}
                showCloseButton={true}
                className='tooltip-wrap ml-1'
              >
                <UIButton2 className='btn-ic'>
                  <UIIcon2 className='ic-system-20-info' />
                </UIButton2>
              </UITooltip>
            </div>
          </UIFormField>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '624px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '624px' }} />
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
                      {data?.quota?.value}회 / {API_KEY_QUOTA_TYPE_OPTIONS[data?.quota?.type as keyof typeof API_KEY_QUOTA_TYPE_OPTIONS]}
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
                      {data?.usedCount}회 / {API_KEY_QUOTA_TYPE_OPTIONS[data?.quota?.type as keyof typeof API_KEY_QUOTA_TYPE_OPTIONS]}
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>
      <ManagerInfoBox
        type='memberId'
        people={[{ userId: data?.belongsTo?.id || '', datetime: data?.createdAt || '' }]}
        rowInfo={[{ personLabel: '생성자', dateLabel: '생성일시' }]}
      />
    </>
  );
};
