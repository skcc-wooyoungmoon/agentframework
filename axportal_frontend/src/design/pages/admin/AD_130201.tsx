import { useState } from 'react';

import { DesignLayout } from '../../components/DesignLayout';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIIcon2, UITypography, UIButton2, UILabel } from '@/components/UI/atoms';
import { UIPageHeader, UIPageBody, UIPageFooter, UIUnitGroup } from '../../../components/UI/molecules';
import { UITabs } from '../../../components/UI/organisms';

export const AD_130201 = () => {
  const [activeTab, setActiveTab] = useState('Tab1');

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'Tab1', label: '기본 정보' },
    { id: 'Tab2', label: '모니터링' },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'admin-users',
        label: '사용자 조회',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      <div className='flex flex-col gap-12'>
        {/* 탭 내용 */}
        {activeTab === 'Tab1' && (
          <section className='section-page'>
            {/* 페이지 헤더 */}
            <UIPageHeader title='API Key 조회' description='' />

            {/* 페이지 바디 */}
            <UIPageBody>
              {/* 탭 영역 */}
              <UIArticle className='article-tabs'>
                <div className='flex'>
                  <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
                </div>
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
                      {/*  [251106_퍼블수정] 텍스트값 수정 */}
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
                              대출 상품 추천
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
                  <UIUnitGroup gap={0} direction='row' align='space-between'>
                    <UITypography variant='title-4' className='secondary-neutral-900'>
                      API Key
                    </UITypography>
                    {/* 2개 버튼 케이스별로 사용필요 (노출/비노출)
                    <UIButton2 className='btn-option-outline'>사용 차단</UIButton2> 
                    */}
                    <UIButton2 className='btn-option-outline'>차단 해제</UIButton2>
                  </UIUnitGroup>
                </div>
                <div className='article-body'>
                  {/*  [251106_퍼블수정] 테이블 속성값 수정 */}
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
                              API Key
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              <div className='flex align-center gap-2'>
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
                  <UIUnitGroup gap={0} direction='row' align='space-between'>
                    <UITypography variant='title-4' className='secondary-neutral-900'>
                      Quota
                    </UITypography>
                    <UIButton2 className='btn-option-outline'>Quota 수정</UIButton2>
                  </UIUnitGroup>
                </div>
                <div className='article-body'>
                  {/*  [251106_퍼블수정] 텍스트 수정 */}
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
                              Quota
                            </UITypography>
                          </th>
                          <td colSpan={3}>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              100회 / 시
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
                              10회 / 시
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
                              김신한 ㅣ Data기획Unit
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

            <UIPageFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='center'>
                  <UIButton2 className='btn-primary-blue' onClick={() => {}}>
                    삭제
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPageFooter>
          </section>
        )}
      </div>
    </DesignLayout>
  );
};
