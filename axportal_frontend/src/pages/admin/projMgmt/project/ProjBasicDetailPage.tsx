import { useState } from 'react';

import { useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPageFooter } from '@/components/UI/molecules';
import { AUTH_KEY } from '@/constants/auth';
import { ProjUpdatePopup } from '@/pages/admin/projMgmt/project/ProjUpdatePopup';
import { type ProjectDetailType, useDeleteProject } from '@/services/admin/projMgmt';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';

const PUBLIC_PROJECT_SEQ = -999;

/**
 * 프로젝트 관리 > 프로젝트 상세 > (TAB) 기본 정보
 */
export const ProjBasicDetailPage = ({ projectInfo, onProjectUpdated }: { projectInfo: ProjectDetailType; onProjectUpdated?: () => void }) => {
  const navigate = useNavigate();
  const { openConfirm, openAlert } = useModal();
  const queryClient = useQueryClient();

  const { updateUser } = useUser();

  const [isEditOpen, setIsEditOpen] = useState(false);

  // 삭제 API 호출
  const deleteProjectMutation = useDeleteProject(projectInfo.uuid);

  // 수정 팝업 열기
  const handleEdit = () => {
    setIsEditOpen(true);
  };

  // 수정 팝업 닫기
  const handleEditClose = () => {
    setIsEditOpen(false);
  };

  // 프로젝트 종료
  const handleCompleteProject = async () => {
    const confirmed = await openConfirm({
      title: '안내',
      bodyType: 'text',
      message: `프로젝트를 종료하시겠습니까?
                삭제한 정보는 복구할 수 없습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      deleteProjectMutation.mutate(undefined, {
        onSuccess: async () => {
          await queryClient.invalidateQueries({ queryKey: ['GET', '/admin/projects'] });

          openAlert({
            bodyType: 'text',
            title: '안내',
            message: `프로젝트가 종료되었습니다.
                      이후에는 내용을 확인하거나 변경할 수 없습니다.`,
            confirmText: '확인',
            onConfirm: () => {
              navigate('/admin/project-mgmt', { replace: true });
            },
          });

          // 상단 프로젝트목록 갱신용
          const updatedUser = await authServices.getMe();

          if (updatedUser) {
            updateUser(updatedUser);
          }
        },
      });
    }
  };

  return (
    <>
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
                {/* 개인정보 포함 여부 */}
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      개인정보 포함 여부
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {projectInfo?.sstvInfInclYn === 'Y' ? '포함' : '미포함'}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      개인정보 포함 사유
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {projectInfo?.sstvInfInclDesc}
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
                      {projectInfo.createdBy.jkwNm} | {projectInfo.createdBy.deptNm}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      생성일시
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {projectInfo.fstCreatedAt}
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
                      {projectInfo.updatedBy.jkwNm !== '' && projectInfo.updatedBy.jkwNm + ' | ' + projectInfo.updatedBy.deptNm}
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      최종 수정일시
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {projectInfo.lstUpdatedAt}
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>

      {/* 하단 버튼 그룹 */}
      {projectInfo.prjSeq !== PUBLIC_PROJECT_SEQ && (
        <UIPageFooter>
          <div className='flex justify-center gap-[8px] mt-10'>
            <Button auth={AUTH_KEY.ADMIN.PROJECT_DELETE} className='btn-primary-gray w-[80px]' onClick={handleCompleteProject}>
              프로젝트 종료
            </Button>
            <Button auth={AUTH_KEY.ADMIN.PROJECT_BASIC_INFO_UPDATE} className='btn-primary-blue w-[80px]' onClick={handleEdit}>
              수정
            </Button>
          </div>
        </UIPageFooter>
      )}

      {/* 수정 팝업 */}
      {isEditOpen && <ProjUpdatePopup projectInfo={projectInfo} onClose={handleEditClose} onSuccess={onProjectUpdated} />}
    </>
  );
};
