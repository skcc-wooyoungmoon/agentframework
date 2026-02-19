import { useState } from 'react';
import { UITabs } from '@/components/UI/organisms';
import { UIArticle, UIGroup } from '@/components/UI/molecules';
import { UITypography } from '@/components/UI';

export const QuantitativeGuide = () => {
  const [activeTab, setActiveTab] = useState('tab1');

  const tabItems = [
    { id: 'tab1', label: 'Harness Task' },
    { id: 'tab2', label: 'Reference-based' },
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
                      공개용 벤치마크 데이터로 평가 (LM Harness 평가 프레임 워크)
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
                          기본 System Metric 중 선택합니다.
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
                          공개용 벤치마크 Dataset을 사용하므로, Response Set이 필요 없습니다.
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
                          시스템 Metric
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          KMMLU, HRM8K, KOBEST ... 중 선택
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
                      공개용 정량 지표로 평가
                    </UITypography>
                  </UIGroup>
                </li>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      사전 조건
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      Model/Agent 등록, Response Set 준비
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
                          기본 System Metric 중 선택합니다.
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
                          필수 컬럼에는 Query, Response, Expected Response가 있습니다.
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
                          시스템 Metric
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          BLUE, TER, METEOR ... 중 선택
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
    </section>
  );
};
