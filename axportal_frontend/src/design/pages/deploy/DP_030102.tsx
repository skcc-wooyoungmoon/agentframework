import { useState } from 'react';
import { UIButton2, UITypography, UITooltip } from '@/components/UI/atoms';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UILabel } from '@/components/UI';

// import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UIArticle, UIFormField } from '@/components/UI/molecules';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '../../../components/UI/organisms/UITabs';

import { DesignLayout } from '../../components/DesignLayout';

export const DP_030102 = () => {
  const [, setActiveTab] = useState('tab1');
  // 탭 아이템 정의
  const tabItems = [
    { id: 'tab1', label: '기본 정보' },
    { id: 'tab2', label: '모니터링' },
  ];
  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '지식/학습 데이터 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='API Key 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabItems} activeId='tab1' size='large' onChange={setActiveTab} />
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                기본 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/*  [251105_퍼블수정] width값 수정 */}
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
                          김신한
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          프로젝트명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          대출상품분석
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
                          사용자
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          연결 대상
                          {/* [251202_퍼블수정] 연결 대상 문구 수정 */}
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          금융 Q&A 응답 모델
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
                  {/*  [251105_퍼블수정] width값 수정 */}
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
                          API Key
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <div className='flex align-center items-center gap-2'>
                            https://aip.sktai.io/api/v1/model_gateway/d8826b97-7373-4dba-973a-6d5228025aaa
                            <a href='#none'>
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
                        <UILabel variant='badge' intent='complete'>
                          사용 가능
                        </UILabel>
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
                    title=''
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
                  {/*  [251105_퍼블수정] width값 수정 */}
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
                          Quota
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          20회 / 일
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
                          10회 / 일
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
                  {/*  [251105_퍼블수정] width값 수정 */}
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
                          김신한
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.03.24 18:23:43
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
        {/* 페이지 footer */}
        {/* <UIPageFooter></UIPageFooter> */}
      </section>
    </DesignLayout>
  );
};
