import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules';

import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_120302 = () => {
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='역할 조회' description='' />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter pb-4'>
            {/* className='project-card bg-gray' bg-gray 배경이 회색일 경우 해당라인 클래스 추가  */}
            <div className='project-card'>
              <UIUnitGroup gap={8} direction='row' vAlign='center' className='mb-6'>
                <UIIcon2 className='ic-system-24-project' aria-hidden='true'></UIIcon2>
                <UITypography variant='title-4' className='secondary-neutral-700'>
                  대출 상품 추천
                </UITypography>
              </UIUnitGroup>
              <ul className='flex flex-col gap-4'>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    역활명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    사용자 피드백 관리자
                  </UITypography>
                </li>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    설명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    추천된 대출 상품에 대한 고객 피드백을 수집·분석하고, 개선 사항을 전달
                  </UITypography>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            {/* 탭 영역 */}
            <UITabs
              items={[
                { id: 'tab1', label: '기본 정보' },
                { id: 'tab2', label: '권한 정보' },
                { id: 'tab3', label: '구성원 정보' },
              ]}
              activeId='tab1'
              size='large'
              // onChange=''
            />
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                기본 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
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
                          역할 유형
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          사용자 피드백 관리자
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
              <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                담당자 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
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
                          [퇴사] 김신한 ㅣ Data기획Unit
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
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          [재직] 박신한 | AI Unit
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.24 18:23:43
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
              <UIButton2 className='btn-primary-gray' style={{ width: '80px' }}>
                삭제
              </UIButton2>
              <UIButton2 className='btn-primary-blue' style={{ width: '80px' }}>
                수정
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
