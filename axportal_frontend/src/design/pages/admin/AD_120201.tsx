import { UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_120201 = () => {
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='프로젝트 조회' description='' />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter pb-4'>
            {/* className='project-card bg-gray' bg-gray 배경이 회색일 경우 해당라인 클래스 추가  */}
            <div className='project-card bg-gray'>
              <ul className='flex flex-col gap-4'>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    프로젝트명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    슈퍼SOL 챗봇 개발
                  </UITypography>
                </li>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    설명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    슈퍼SOL에서 사용할 챗봇을 개발
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
                { id: 'tab2', label: '역할 정보' },
                { id: 'tab3', label: '구성원 정보' },
              ]}
              activeId='tab1'
              size='large'
              // onChange=''
            />
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
                          개인정보 포함 여부
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          포함
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          개인정보 포함 사유
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          고객 본인 확인을 위해 주민등록번호가 포함됩니다.
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
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          김신한 ㅣ Data기획Unit
                        </UITypography>
                      </td>
                      {/* [251106_퍼블수정] 테이블속성 텍스트 수정 */}
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
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
          <div className='flex justify-center gap-[8px]'>
            <UIButton2 className='btn-primary-gray w-[80px]'>프로젝트 종료</UIButton2>
            <UIButton2 className='btn-primary-blue w-[80px]'>수정</UIButton2>
          </div>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
