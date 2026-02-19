import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIGroup } from '@/components/UI/molecules';
import { useGetExternalRepos } from '@/services/knowledge/knowledge.services.ts';
import dateUtils from '@/utils/common/date.utils.ts';

export const DashboardKnowledgeSection = () => {
  const navigate = useNavigate();

  const { data: externalReposData } = useGetExternalRepos({
    page: 1,
    size: 5,
    sort: 'created_at,desc',
    filter: 'is_active:true',
  });

  const datasetList = useMemo(() => {
    const tempExternalReposList = externalReposData?.data || [];

    return [
      ...tempExternalReposList.map(item => ({
        id: item.id,
        name: item.name,
        status: item.is_active,
        type: '',
        createdAt: dateUtils.toKoreanTime(item.created_at),
        displayCreateDate: dateUtils.formatDate(item.created_at, 'custom', {
          pattern: 'yyyy.MM.dd HH:mm',
          useKoreanLocale: true,
        }),
        displayType: '지식',
        dataType: 'knowledge',
      })),
    ];
  }, [externalReposData]);

  const handleDataClick = (data: any) => {
    const knowledgeData: any = externalReposData?.data.find(item => item.id === data.id);
    const knwId = knowledgeData?.knw_id || knowledgeData?.id;
    if (knwId) {
      navigate(`/data/dataCtlg/knowledge/detail/${knwId}`);
    }
  };

  return (
    <div className='box-group-item'>
      <div className='item-header'>
        <div className='left'>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            사용 가능한 지식 데이터
          </UITypography>
        </div>
      </div>
      <div className='item-cont mt-4'>
        {datasetList.length > 0 ? (
          <ul className='recent-data-list'>
            {datasetList.map(data => (
              <li key={data.id} className='item'>
                <div className='item-left'>
                  <UIGroup direction='column' gap={4}>
                    <UIButton2 onClick={() => handleDataClick(data)} className='cursor-pointer'>
                      <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                        {data.name}
                      </UITypography>
                    </UIButton2>
                    <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                      <UITypography variant='body-2' className='text-gray-500'>
                        {data.displayType}
                      </UITypography>
                      <UITypography variant='body-2' className='text-gray-500'>
                        {data.displayCreateDate}
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
              조회 가능한 데이터가 없습니다.
            </UITypography>
          </div>
        )}
      </div>
    </div>
  );
};
