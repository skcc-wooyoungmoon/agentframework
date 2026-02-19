import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIButton2, UILabel, UITypography } from '@/components/UI/atoms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';

export const DP_020103 = () => {
  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '지식/학습 데이터 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='에이전트 배포 버전 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                버전 배포 정보
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
                          배포명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          신한 스마트콜봇
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          신한 스마트콜봇(RAG, 고객센터 챗봇 예제 사용), 금융상품 설명봇 참고용
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          빌더
                        </UITypography>
                      </th>
                      {/* [251107_퍼블수정] : 마크업 수정 */}
                      <td colSpan={3}>
                        <UIUnitGroup gap={16} direction='row' vAlign='center'>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            0509 신한 스마트콜봇 캔버스
                          </UITypography>
                          <UIButton2 className='btn-text-14-point' rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }}>
                            빌더 바로가기
                          </UIButton2>
                        </UIUnitGroup>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          운영 배포 여부
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          배포
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          버전
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          ver.4
                        </UITypography>
                      </td>
                    </tr>

                    <tr style={{ height: '68px' }}>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UILabel variant='badge' intent='stop'>
                          중지
                        </UILabel>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          할당된 자원
                          <br />
                          (CPU/Memory)
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2Cores / 8GB
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          복제 인스턴스 수
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          MIN2-MAX2
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 테이블 */}
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
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>

        {/* 페이지 footer */}
        {/* <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <UIButton2 className='btn-primary-gray'>삭제</UIButton2>
              <UIButton2 className='btn-primary-blue'>수정</UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter> */}
      </section>
    </DesignLayout>
  );
};
