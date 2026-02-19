import { UILabel, UIButton2, UITypography, UIFileBox } from '../../../components/UI/atoms';
import { UIUnitGroup, UIPageHeader, UIPageBody, UIPageFooter, UIArticle, UIGroup, UIList } from '../../../components/UI/molecules';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_090102 = () => {
  // TODO: 탭 기능 구현 예정
  // const [activeTab, setActiveTab] = useState('basic');
  // const tabItems = [
  //   { id: 'basic', label: '기본 정보' },
  //   { id: 'role', label: '역할 정보' },
  //   { id: 'members', label: '구성원 정보' },
  // ];

  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'admin-notice',
        label: '공지사항 관리',
        icon: 'ico-lnb-menu-20-notice',
      }}
    >
      <section className='section-page'>
        <UIPageHeader title='공지사항 조회' description='' />
        <UIPageBody>
          <UIArticle>
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
                        제목
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        8월 5일(월) 정기 시스템 점검 예정 안내
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        유형
                      </UITypography>
                    </th>
                    {/* [251107_퍼블수정] : td 영역에 colSpan={3} 넣어주세요 */}
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        시스템점검
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        첨부파일
                      </UITypography>
                    </th>
                    {/* [251107_퍼블수정] : td 영역에 colSpan={3} 넣어주세요 */}
                    <td colSpan={3}>
                      <UIGroup gap={8} direction={'column'}>
                        <UIFileBox variant='link' fileName='2025-08-20_시스템점검_공지.pdf' />
                        <UIFileBox variant='link' fileName='2025-09-05_DB점검안내.docx' />
                      </UIGroup>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </UIArticle>

          {/* [251105_퍼블수정] 마크업 수정 */}
          <UIArticle className='pl-3 pt-2 pb-14 border-b border-[#DCE2ED]'>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              안정적인 서비스 운영을 위한 정기 점검이 다음과 같이 진행될 예정입니다.
            </UITypography>
            <UIGroup gap={0} direction='column' style={{ padding: '24px 0 48px 12px' }}>
              <UIList
                gap={4}
                direction='column'
                className='ui-list_bullet'
                data={[
                  {
                    dataItem: (
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        점검 일시: 2025년 8월 5일(월) 오전 2시 ~ 오전 5시
                      </UITypography>
                    ),
                  },
                ]}
              />
              <UIList
                gap={4}
                direction='column'
                className='ui-list_bullet'
                data={[
                  {
                    dataItem: (
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        점검 대상: 전체 포탈 서비스 (웹 UI, API 등)
                      </UITypography>
                    ),
                  },
                ]}
              />
              <UIList
                gap={4}
                direction='column'
                className='ui-list_bullet'
                data={[
                  {
                    dataItem: (
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        영향 범위: 점검 시간 동안 서비스 이용이 불가능합니다.
                      </UITypography>
                    ),
                  },
                ]}
              />
            </UIGroup>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              해당 시간에는 자동화 작업 및 모델 배포 기능도 일시적으로 중단되며, 작업 상황에 따라 점검 종료 시간이 변경될 수 있습니다. <br />
              보다 나은 서비스 제공을 위한 조치이오니 너그러운 양해 부탁드립니다.
            </UITypography>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                게시 정보
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
                          게시여부
                        </UITypography>
                      </th>
                      {/* [251107_퍼블수정] : td 영역에 colSpan={3} 넣어주세요 */}
                      <td colSpan={3}>
                        <UILabel variant='badge' intent='complete'>
                          게시
                        </UILabel>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          게시기간
                        </UITypography>
                      </th>
                      {/* [251107_퍼블수정] : td 영역에 colSpan={3} 넣어주세요 */}
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.03.01 00:00 - 2025.03.31 00:00
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
                          김신한 | AI Unit
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
                          김신한 | AI Unit
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.23 18:23:43
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
              <UIButton2 className='btn-primary-gray'>삭제</UIButton2>
              <UIButton2 className='btn-primary-blue'>수정</UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
