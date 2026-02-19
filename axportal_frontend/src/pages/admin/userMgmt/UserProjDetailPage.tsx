import { useState } from 'react';

import { useNavigate, useParams } from 'react-router';

import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPageBody, UIPageFooter, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UITabs } from '@/components/UI/organisms';
import { useGetUserById, useGetUserProjectDetail } from '@/services/admin/userMgmt';

import { UserProjRoleUpdatePopup } from './UserProjRoleUpdatePopup.tsx';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';

const tabItems = [
  { id: 'basic', label: '기본 정보' },
  { id: 'project', label: '프로젝트 정보' },
];

/**
 * 관리 > 사용자 관리 > 사용자 상세 >  (TAB) 프로젝트 정보(= 사용자가 참여한 프로젝트) > 프로젝트 상세
 */
export const UserProjDetailPage = () => {
  const navigate = useNavigate();

  const { userId, projectId } = useParams();

  const [isRoleEditOpen, setIsRoleEditOpen] = useState(false);

  // ================================
  // API 호출
  // ================================

  // 사용자 정보 조회
  const { data: userInfo } = useGetUserById({ userId: userId! });

  // 사용자 프로젝트 상세 정보 조회
  const { data: projectInfo, refetch } = useGetUserProjectDetail({
    userId: userId!,
    projectId: projectId!,
  });

  // ================================
  // 이벤트 핸들러
  // ================================

  // 탭 변경 처리
  const handleTabChange = (tabId: string) => {
    navigate(`/admin/user-mgmt/${userId}`, { state: { userInfo, activeTab: tabId } });
  };

  // ================================
  // 조건부 값
  // ================================

  // 수정 버튼 비활성화 여부 판단
  const getIsEditDisabled = () => {
    // 사용자 계정이 활성 상태가 아닌 경우 역할 수정 불가
    return userInfo?.dmcStatus !== 'ACTIVE';
  };

  // ================================
  // 렌더링
  // ================================

  return (
    <section className='section-page'>
      <UIPageHeader title='프로젝트 조회' />

      <UIPageBody>
        <UIArticle>
          <UITabs items={tabItems} activeId='project' size='large' onChange={handleTabChange} />
        </UIArticle>

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
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
                </colgroup>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        프로젝트명
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.project?.prjNm}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        설명
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.project?.dtlCtnt}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        생성일시
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.project?.fstCreatedAt}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        최종 수정일시
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.project?.lstUpdatedAt}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        {/* 역할 정보 섹션 */}
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              역할 정보
            </UITypography>
          </div>
          <div className='article-body'>
            <div className='border-t border-black'>
              <table className='tbl-v'>
                <colgroup>
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
                </colgroup>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        역할명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.role?.roleNm}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        설명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.role?.dtlCtnt}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        생성일시
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.role?.fstCreatedAt}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        최종 수정일시
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.role?.lstUpdatedAt}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        {/* 담당자 정보 섹션 */}
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
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
                </colgroup>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        최종 수정자
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {projectInfo?.userRole.updatedBy?.jkwNm && (
                          <span>
                            {projectInfo.userRole.updatedBy.jkwNm} | {projectInfo.userRole.updatedBy.deptNm}
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
                        {projectInfo?.userRole?.lstUpdatedAt}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        {/* 역할 수정 팝업 */}
        {isRoleEditOpen && projectInfo?.project && userInfo && (
          <UserProjRoleUpdatePopup
            userInfo={userInfo}
            projectInfo={projectInfo.project as any}
            onClose={() => setIsRoleEditOpen(false)}
            refetch={refetch}
            currentRoleId={projectInfo?.role?.uuid}
          />
        )}
      </UIPageBody>

      {/* 하단 버튼 영역 */}
      <UIPageFooter>
        <UIArticle>
          <UIUnitGroup gap={8} direction='row' align='center'>
            <Button auth={AUTH_KEY.ADMIN.USER_ROLE_UPDATE} className='btn-primary-blue' onClick={() => setIsRoleEditOpen(true)} disabled={getIsEditDisabled()}>
              수정
            </Button>
          </UIUnitGroup>
        </UIArticle>
      </UIPageFooter>
    </section>
  );
};
