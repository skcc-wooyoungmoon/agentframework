import { useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { UITypography } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useDeleteSafetyFilter, useGetSafetyFilterDetail } from '@/services/deploy/safetyFilter';
import { useModal } from '@/stores/common/modal';
import { SafetyFilterUpdatePopup } from './SafetyFilterUpdatePopup';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox';

export const SafetyFilterDetailPage = () => {
  const { id: filterGroupId } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { openAlert } = useModal();
  const { showDeleteConfirm } = useCommonPopup();

  const [isUpdatePopupOpen, setIsUpdatePopupOpen] = useState(false);

  // 상세 조회 API 호출
  const { data: safetyFilterData, refetch } = useGetSafetyFilterDetail(filterGroupId || '');

  // 삭제 API 호출
  const { mutate: deleteSafetyFilter } = useDeleteSafetyFilter({
    onSuccess: async () => {
      // 세이프티 필터 목록 캐시 무효화
      await queryClient.invalidateQueries({ queryKey: ['GET', '/safety-filter'] });

      openAlert({
        title: '성공',
        message: '세이프티 필터가 삭제되었습니다.',
        confirmText: '확인',
        onConfirm: () => {
          navigate('/deploy/safetyFilter', { replace: true });
        },
      });
    },
    onError: error => {
      return openAlert({
        title: '오류',
        message: error.error?.message || '세이프티 필터 삭제에 실패했습니다.',
        confirmText: '확인',
      });
    },
  });

  // 삭제 버튼 클릭
  const handleDelete = () => {
    showDeleteConfirm({
      onConfirm: () => {
        deleteSafetyFilter({ filterGroupIds: [filterGroupId!] });
      },
    });
  };

  // 수정 버튼 클릭
  const handleEdit = () => {
    setIsUpdatePopupOpen(true);
  };

  // 수정 팝업 닫기
  const handleUpdatePopupClose = () => {
    setIsUpdatePopupOpen(false);
  };

  // 수정 완료 시 데이터 갱신
  const handleUpdateSuccess = async () => {
    // 상세 조회 API 재호출로 최신 데이터 가져오기
    await refetch();
  };

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='세이프티 필터 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
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
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          분류
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {safetyFilterData?.filterGroupName || '-'}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          금지어
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {safetyFilterData?.stopWords && safetyFilterData.stopWords.length > 0 ? safetyFilterData.stopWords.map(word => word.stopWord).join(', ') : ''}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 담당자 정보 */}
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
                          {safetyFilterData?.createdBy && (
                            <span>
                              {safetyFilterData.createdBy.jkwNm} | {safetyFilterData.createdBy.deptNm}
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
                          {safetyFilterData?.createdAt ?? ''}
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
                          {safetyFilterData?.updatedBy && (
                            <span>
                              {safetyFilterData.updatedBy.jkwNm} | {safetyFilterData.updatedBy.deptNm}
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
                          {safetyFilterData?.updatedAt ?? ''}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 프로젝트 정보 */}
          <ProjectInfoBox assets={[{ type: 'safety-filter', id: safetyFilterData?.filterGroupId! }]} auth={AUTH_KEY.DEPLOY.SAFETY_FILTER_CHANGE_PUBLIC} />
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.DEPLOY.SAFETY_FILTER_DELETE} className='btn-primary-gray' onClick={handleDelete}>
                삭제
              </Button>
              <Button auth={AUTH_KEY.DEPLOY.SAFETY_FILTER_UPDATE} className='btn-primary-blue' onClick={handleEdit}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>

      {/* 수정 팝업 */}
      <SafetyFilterUpdatePopup
        isOpen={isUpdatePopupOpen}
        onClose={handleUpdatePopupClose}
        filterGroupId={filterGroupId!}
        safetyFilterData={safetyFilterData}
        onSave={handleUpdateSuccess}
      />
    </>
  );
};
