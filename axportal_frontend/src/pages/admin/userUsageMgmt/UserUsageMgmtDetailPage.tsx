import { useAtomValue } from 'jotai';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UICode, UITypography } from '@/components/UI/atoms';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIFormField } from '@/components/UI/molecules';

import { selectedUserActivityAtom } from './index';
import { routeConfig } from '@/routes/route.config';
import { generateBreadcrumb } from '@/utils/common/breadcrumb.utils';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';

const normalizeMenuPath = (menuPath: string): string => {
  let normalized = menuPath?.trim() ?? '';
  if (!normalized) return '';

  if (/^https?:\/\//i.test(normalized)) {
    try {
      normalized = new URL(normalized).pathname;
    } catch (_error) {
      // URL 파싱 실패 시 원본 경로를 그대로 사용
      normalized = menuPath.trim();
    }
  }

  normalized = normalized.split(/[?#]/)[0];

  if (!normalized.startsWith('/')) {
    normalized = `/${normalized.replace(/^\/+/, '')}`;
  }

  normalized = normalized.replace(/\/{2,}/g, '/');

  return normalized;
};

const formatMenuPathLabel = (menuPath?: string | null): string => {
  if (!menuPath) return '-';
  if (menuPath.includes('>')) return menuPath;

  const normalizedPath = normalizeMenuPath(menuPath);
  if (!normalizedPath) return menuPath;

  if (normalizedPath === '/login') {
    return '로그인';
  }

  if (!normalizedPath) return menuPath;

  const breadcrumb = generateBreadcrumb(normalizedPath, routeConfig);
  if (!breadcrumb.length) return menuPath;

  return breadcrumb.join(' > ');
};

const formatJsonContent = (content: unknown) => {
  if (content === null || content === undefined) {
    return '';
  }

  if (typeof content === 'string') {
    const trimmed = content.trim();
    if (trimmed === '') {
      return '';
    }

    try {
      const parsed = JSON.parse(trimmed);
      return JSON.stringify(parsed, null, 2);
    } catch (_error) {
      return content;
    }
  }

  try {
    return JSON.stringify(content, null, 2);
  } catch (_error) {
    return '';
  }
};

export const UserUsageMgmtDetailPage = () => {
  const atomData = useAtomValue(selectedUserActivityAtom);
  

  const getSelectedData = () => {
    if (atomData) {
      return atomData;
    }
    
    try {
      const storedData = localStorage.getItem((STORAGE_KEYS.SEARCH_VALUES as any).USER_USAGE_HIST_DETAIL || 'USER_USAGE_HIST_DETAIL');
      if (storedData) {
        return JSON.parse(storedData);
      }
    } catch {
      // localStorage 데이터 파싱 실패 시 무시
    }
    
    return null;
  };
  
  const selectedData = getSelectedData();

  const formatDateTime = (dateString: string) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return dateString;

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
  };

  if (!selectedData) {
    return (
      <section className='section-page'>
        <UIPageHeader title='사용 이력 조회' description='' />
        <UIPageBody>
          <div className='flex-1 flex items-center justify-center h-[220px]'>
            <div className='flex flex-col justify-center items-center gap-3'>
              <span className='ico-nodata'>
                <UITypography variant='body-1' className='secondary-neutral-500'>
                  조회된 데이터가 없습니다.
                </UITypography>
              </span>
            </div>
          </div>
        </UIPageBody>
      </section>
    );
  }

  return (
    <section className='section-page'>
      <UIPageHeader title='사용 이력 조회' description='' />
      <UIPageBody>
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              사용자 정보
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
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData?.userName || '-'}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        프로젝트명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData?.projectName || '-'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        역할명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData?.roleName || '-'}
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
              접속 정보
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
                        요청 일시
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {formatDateTime(selectedData?.createdAt || '')}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        접속 환경
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData?.userAgent || '-'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        접속 IP
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData?.clientIp || '-'}
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
              요청 정보
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
                        메뉴 경로
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData ? formatMenuPathLabel(selectedData.menuPath) : '-'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        요청 경로
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {(() => {
                          const apiEndpoint = selectedData?.apiEndpoint;
                          if (!apiEndpoint) return '-';
                          
                          if (apiEndpoint.startsWith('Controller')) {
                            return 'Portal';
                          } else if (apiEndpoint.startsWith('/api')) {
                            return 'ADXP';
                          }
                          return '-';
                        })()}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th >
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        요청 메서드
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData?.action || '-'}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        API URL
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {selectedData?.apiEndpoint || '-'}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        <UIArticle>
          <UIFormField gap={8} direction='column'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
              Request
            </UITypography>
            <UICode
              value={formatJsonContent(selectedData?.requestContent)}
              language='json'
              theme='dark'
              width='100%'
              minHeight='300px'
              maxHeight='500px'
              readOnly={true}
              wordWrap={true}
            />
          </UIFormField>
        </UIArticle>

        <UIArticle>
          <UIFormField gap={8} direction='column'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
              Response
            </UITypography>
            <UICode
              value={formatJsonContent(selectedData?.responseContent)}
              language='json'
              theme='dark'
              width='100%'
              minHeight='300px'
              maxHeight='500px'
              readOnly={true}
              wordWrap={true}
            />
          </UIFormField>
        </UIArticle>

      </UIPageBody>
    </section>
  );
};
