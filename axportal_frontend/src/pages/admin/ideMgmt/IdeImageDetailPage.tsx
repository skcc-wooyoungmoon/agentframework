import { useNavigate, useParams } from 'react-router-dom';
import { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPageBody, UIPageFooter, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { IMAGE_TYPE_LABEL, useDeleteImage, useGetImageDetail } from '@/services/admin/ideMgmt';
import { ManagerInfoBox } from '@/components/common';
import { IdeImageUpdatePopup } from './IdeImageUpdatePopup';

/**
 * 관리 > IDE 관리 > 이미지 관리 (TAB) > 이미지 상세
 */
export const IdeImageDetailPage = () => {
  const { imageId } = useParams<{ imageId: string }>();
  const navigate = useNavigate();
  const { showDeleteConfirm, showDeleteComplete } = useCommonPopup();
  const { mutate: deleteImage } = useDeleteImage();

  // 수정 팝업 상태
  const [isUpdatePopupOpen, setIsUpdatePopupOpen] = useState(false);

  // API 호출
  const { data: imageDetail, refetch } = useGetImageDetail(imageId!);

  // 삭제 핸들러
  const handleDelete = () => {
    if (!imageId) return;

    showDeleteConfirm({
      onConfirm: () => {
        deleteImage(
          { uuids: [imageId] },
          {
            onSuccess: () => {
              showDeleteComplete({
                itemName: '이미지가',
                onConfirm: () => {
                  // 목록 화면으로 이동
                  navigate(-1);
                },
              });
            },
          }
        );
      },
    });
  };

  // 수정 핸들러
  const handleEdit = () => {
    setIsUpdatePopupOpen(true);
  };

  // 수정 완료 핸들러
  const handleUpdateSave = () => {
    setIsUpdatePopupOpen(false);
    refetch();
  };

  return (
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
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {imageDetail?.imgG ? IMAGE_TYPE_LABEL[imageDetail.imgG] : ''}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        이미지명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {imageDetail?.imgNm || ''}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        이미지 URL
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {imageDetail?.imgUrl || ''}
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
                        {imageDetail?.dtlCtnt || ''}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        {/* 담당자 정보 섹션 */}
        <ManagerInfoBox
          type='memberId'
          people={[
            { userId: imageDetail?.createdBy || '', datetime: imageDetail?.createdAt || '' },
            { userId: imageDetail?.updatedBy || '', datetime: imageDetail?.updatedAt || '' },
          ]}
        />
      </UIPageBody>

      <UIPageFooter>
        <UIArticle>
          <UIUnitGroup gap={8} direction='row' align='center'>
            <UIButton2 className='btn-primary-gray' onClick={handleDelete}>
              삭제
            </UIButton2>
            <UIButton2 className='btn-primary-blue' onClick={handleEdit}>
              수정
            </UIButton2>
          </UIUnitGroup>
        </UIArticle>
      </UIPageFooter>

      {/* 이미지 수정 팝업 */}
      <IdeImageUpdatePopup
        isOpen={isUpdatePopupOpen}
        onClose={() => setIsUpdatePopupOpen(false)}
        onSave={handleUpdateSave}
        initialData={
          imageDetail
            ? {
                toolName: IMAGE_TYPE_LABEL[imageDetail.imgG],
                imageName: imageDetail.imgNm,
                imageUrl: imageDetail.imgUrl,
                description: imageDetail.dtlCtnt,
              }
            : undefined
        }
        imageUuid={imageId}
      />
    </section>
  );
};
