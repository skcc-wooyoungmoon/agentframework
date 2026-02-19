import { UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { type RoleDetailType } from '@/services/admin/projMgmt';
import { getProjectRoleTypeLabel } from '@/services/admin/projMgmt/projMgmt.mappers';

/**
 * 프로젝트 관리 > 프로젝트 상세 >  (TAB) 역할 정보 > 역할 상세 > (TAB) 기본정보
 */
export const ProjRoleBasicDetail = ({ roleInfo }: { projectId: string; roleInfo: RoleDetailType }) => {
  return (
    <>
      {/* 기본 정보 */}
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
                <col style={{ width: '128px' }} />
                <col style={{ width: '46.5%' }} />
                <col style={{ width: '128px' }} />
                <col style={{ width: 'calc(50% - 128px)' }} />
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
                      {getProjectRoleTypeLabel(roleInfo.roleType)}
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
                <col style={{ width: '128px' }} />
                <col style={{ width: '46.5%' }} />
                <col style={{ width: '128px' }} />
                <col style={{ width: 'calc(50% - 128px)' }} />
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
                      {roleInfo.createdBy?.jkwNm && roleInfo.createdBy?.deptNm && (
                        <span>
                          {roleInfo.createdBy.jkwNm} | {roleInfo.createdBy.deptNm}
                        </span>
                      )}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      생성일시
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {roleInfo.fstCreatedAt}
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
                      {roleInfo.updatedBy?.jkwNm && roleInfo.updatedBy?.deptNm && (
                        <span>
                          {roleInfo.updatedBy.jkwNm} | {roleInfo.updatedBy.deptNm}
                        </span>
                      )}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      최종 수정일시
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {roleInfo.lstUpdatedAt}
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>
    </>
  );
};
