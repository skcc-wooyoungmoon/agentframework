import { useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { UIGroup } from '@/components/UI/molecules';

export const EV_010201_P01: React.FC = () => {
  const [activeTab, setActiveTab] = useState('tab1');

  const tabItems = [
    { id: 'tab1', label: 'Manual Evaluation' },
    { id: 'tab2', label: 'Interacive Evaluation' },
  ];

  return (
    <section className='section-modal'>
      <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} className='mb-8' />

      {/* TAB1 : Evaluation Task */}
      {activeTab === 'tab1' && (
        <>
          <UIArticle>
            <div className='info-box'>
              <ul>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      특징
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      User Metric 활용, Q&A Dataset 기반으로 사람이 직접 평가
                    </UITypography>
                  </UIGroup>
                </li>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      사전 조건
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      Model/Agent 등록, User Metric 등록
                    </UITypography>
                  </UIGroup>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  사전 조건
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  평가 진행을 위해 사전에 준비 되어야 하는 내용입니다.
                </UITypography>
              </UIGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '152px' }} />
                    <col style={{ width: '870px' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Model / Agent 등록
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          사용자가 Target Model/Agent를 등록 및 지정합니다.
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Metric 준비
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          사용자가 필요에 따라 New User Metric을 등록하거나, 기본 Metric 중 선택합니다.
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Response Set 등록
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          필수 컬럼에는 Query, Response가 있습니다. * 단, User Metric 정의에 따라 Meta 컬럼이 필요합니다.
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
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  기본 에셋 정보
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  Datumo Eval에서 평가 진행을 위해 기본 제공하는 에셋입니다.
                </UITypography>
              </UIGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '152px' }} />
                    <col style={{ width: '870px' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          기본 Metric
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Safety, RAG Quality
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </>
      )}

      {/* TAB2 : RAG Checker Task */}
      {activeTab === 'tab2' && (
        <>
          <UIArticle>
            <div className='info-box'>
              <ul>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      특징
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      실시간 Q&A 기반으로 사람이 직접 평가
                    </UITypography>
                  </UIGroup>
                </li>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      사전 조건
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      Model/Agent 등록
                    </UITypography>
                  </UIGroup>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  사전 조건
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  평가 진행을 위해 사전에 준비 되어야 하는 내용입니다.
                </UITypography>
              </UIGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '152px' }} />
                    <col style={{ width: '870px' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Model / Agent 등록
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          사용자가 Target Model/Agent를 등록 및 지정합니다.
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Metric 준비
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Interactive Evaluation 평가의 경우 Metric 불필요하며 Task 등록시 평가 기준을 정의합니다.
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Response Set 등록
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Interactive Evaluation 평가의 경우 Response Set이 불필요합니다.
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
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  기본 에셋 정보
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  Interactive Evaluation 평가는 Metric과 Response set을 사용하지 않으므로 별도 기본 에셋이 없습니다.
                </UITypography>
              </UIGroup>
            </div>
          </UIArticle>
        </>
      )}
    </section>
  );
};
