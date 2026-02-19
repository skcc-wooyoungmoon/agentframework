import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIGroup } from '@/components/UI/molecules';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import dateUtils from '@/utils/common/date.utils.ts';

export const DashboardModelSection = () => {
  const navigate = useNavigate();

  const { data: modelDeployList } = useGetModelDeployList({
    page: 0,
    size: 5,
    sort: 'created_at,desc',
    filter: 'status:Available',
    queryKey: 'dashboard',
  });

  const modelList = useMemo(() => {
    const tempDeployList: GetModelDeployResponse[] = modelDeployList?.content || [];

    return [
      ...tempDeployList.map(item => ({
        id: item.servingId,
        name: item.name,
        status: item.status,
        type: item.servingType,
        createdAt: dateUtils.toKoreanTime(item.createdAt),
        displayCreateDate: dateUtils.formatDate(item.createdAt, 'custom', {
          pattern: 'yyyy.MM.dd HH:mm',
          useKoreanLocale: true,
        }),
        modelType: 'deploy',
        displayModelType: '모델 배포',
      })),
    ];
  }, [modelDeployList]);

  const handleModelClick = (model: { id: string }) => {
    navigate(`/deploy/modelDeploy/${model.id}`);
  };

  return (
    <div className='box-group-item'>
      <div className='item-header'>
        <div className='left'>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            사용 가능한 모델
          </UITypography>
        </div>
      </div>
      <div className='item-cont mt-4'>
        {modelList.length > 0 ? (
          <ul className='recent-data-list'>
            {modelList.map(model => (
              <li key={model.id} className='item'>
                <div className='item-left'>
                  <UIGroup direction='column' gap={4}>
                    <UIButton2 onClick={() => handleModelClick(model)} className='cursor-pointer'>
                      <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                        {model.name}
                      </UITypography>
                    </UIButton2>
                    <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                      <UITypography variant='body-2' className='text-gray-500'>
                        {model.displayModelType}
                      </UITypography>
                      <UITypography variant='body-2' className='text-gray-500'>
                        {model.displayCreateDate}
                      </UITypography>
                    </UIGroup>
                  </UIGroup>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <div className='no-date'>
            <UIImage src='/assets/images/system/ico-system-80-default-nodata.svg' alt='No data' className='w-20 h-20' />
            <UITypography variant='body-1' className='text-gray-500'>
              조회 가능한 모델이 없습니다.
            </UITypography>
          </div>
        )}
      </div>
    </div>
  );
};
