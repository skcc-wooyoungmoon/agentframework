import { useMemo } from 'react';

import { UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIUnitGroup } from '@/components/UI/molecules/UIUnitGroup';
import { useGetProjectInfo, useUpdateProjectToPublic } from '@/services/common/projectInfo.service';
import { useModal } from '@/stores/common/modal/useModal';
import { Button } from '../../auth';
import type { ProjectInfoBoxProps } from './types';

export const ProjectInfoBox = ({ assets, auth }: ProjectInfoBoxProps) => {
  const { openConfirm, openAlert } = useModal();

  const isEnabled = useMemo(() => {
    return assets && assets.length > 0 && assets.filter(asset => asset.type && asset.id).length > 0;
  }, [assets]);
  // 프로젝트 정보 조회
  const { data: projectInfo, refetch } = useGetProjectInfo({ assetUuid: assets?.[0]?.id || '' }, { enabled: isEnabled });
  const { mutate: updateProjectToPublic } = useUpdateProjectToPublic();

  const handleUpdateProjectToPublic = () => {
    openConfirm({
      title: '안내',
      message: '이 항목을 전체공유 하시겠어요?\n공유 후에는 다시 되돌릴 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        if (assets && assets.length > 0) {
          for (const asset of assets) {
            // type과 uuid가 모두 있는지 확인
            if (asset.type && asset.id) {
              updateProjectToPublic(
                { type: asset.type, id: asset.id },
                {
                  onSuccess: () => {
                    openAlert({
                      title: '완료',
                      message: '해당 에셋 전체공유 설정이 완료되었습니다.\nPublic 프로젝트에서 조회 및 사용하실 수 있습니다.',
                      confirmText: '확인',
                      onConfirm: () => {
                        refetch();
                      },
                    });
                  },
                }
              );
            }
          }
        }
      },
    });
  };

  return (
    <UIArticle>
      <div className='article-header'>
        <UIUnitGroup direction='row' align='space-between' gap={0}>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            프로젝트 정보
          </UITypography>
          <Button auth={auth} className='btn-option-outlined' onClick={handleUpdateProjectToPublic} disabled={!projectInfo || projectInfo?.lstPrjNm === 'Public'}>
            공개 설정
          </Button>
        </UIUnitGroup>
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
                    공개 범위
                  </UITypography>
                </th>
                <td>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    {projectInfo && projectInfo?.lstPrjNm === 'Public' ? `전체공유 | Public` : `내부공유 | ${projectInfo?.lstPrjNm || ''}`}
                  </UITypography>
                </td>
                <th>
                  <UITypography variant='body-2' className='secondary-neutral-900'>
                    권한 수정자
                  </UITypography>
                </th>
                <td>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    {projectInfo && projectInfo?.lstPrjNm === 'Public' ? `${projectInfo?.jkwNm} | ${projectInfo?.deptNm} | ${projectInfo?.fstPrjNm}` : ``}
                  </UITypography>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </UIArticle>
  );
};
