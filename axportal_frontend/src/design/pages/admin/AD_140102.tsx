import { UIButton2, UITypography } from '../../../components/UI/atoms';
import { UIPageHeader, UIPageBody, UIPageFooter, UIUnitGroup } from '../../../components/UI/molecules';
import { UIArticle } from '../../../components/UI/molecules/UIArticle';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_140102 = () => {
  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'user-mgmt',
        label: '사용자 관리',
        icon: 'ico-lnb-menu-20-manage-user',
      }}
    >
      <section className='section-page'>
        <UIPageHeader title='이미지 조회' description='' />

        <UIPageBody>
          {/* 기본 정보 섹션 */}
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
                          도구명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Jupyter Notebook
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이미지명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Python_v3.12_RAG
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이미지 URL
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          assets/images/common/iimg-home-guide.svg
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          RAG 구축 실험용, LangChain·FAISS·BM25·Embeddings 포함
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
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.04 15:45:06
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
              <UIButton2 className='btn-primary-gray' onClick={() => {}}>
                삭제
              </UIButton2>
              <UIButton2 className='btn-primary-blue' onClick={() => {}}>
                수정
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
