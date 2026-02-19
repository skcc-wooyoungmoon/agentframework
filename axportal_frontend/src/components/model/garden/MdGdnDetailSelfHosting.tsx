import { UIIcon2, UITextLabel, UITypography } from '@/components/UI';
import { UILabel, type UILabelIntent } from '@/components/UI/atoms';
import { MODEL_DEPLOY_PROVIDER } from '@/constants/deploy/modelDeploy.constants';
import { MODEL_GARDEN_STATUS } from '@/constants/model/garden.constants';
import type { MdGdnDetailInfoProps } from '@/pages/model/mdlGdn/MdGdnDetailPage';

export const MdGdnDetailSelfHosting = ({ modelInfo }: MdGdnDetailInfoProps) => {
  return (
    <tbody>
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            모델명
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.name}
          </UITypography>
        </td>

        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            상태
          </UITypography>
        </th>
        <td>
          <UILabel variant='badge' intent={MODEL_GARDEN_STATUS[modelInfo?.statusNm as keyof typeof MODEL_GARDEN_STATUS]?.value as UILabelIntent}>
            {MODEL_GARDEN_STATUS[modelInfo?.statusNm as keyof typeof MODEL_GARDEN_STATUS]?.label}
          </UILabel>
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
            {modelInfo?.description || ''}
          </UITypography>
        </td>
      </tr>
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            모델유형
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.type || ''}
          </UITypography>
        </td>

        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            크기
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {/* {stringUtils.formatBytesToGB(modelInfo?.size || 0)}GB */}
            {modelInfo?.size?.toString() ?? '0'}GB
          </UITypography>
        </td>
      </tr>
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            공급사
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            <div className='flex gap-1 items-center'>
              <UIIcon2 className={MODEL_DEPLOY_PROVIDER[modelInfo?.provider as keyof typeof MODEL_DEPLOY_PROVIDER]} />
              {modelInfo?.provider}
            </div>
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            태그
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            <div className='flex gap-1'>
              {modelInfo?.tags &&
                modelInfo?.tags.split(',').map(tag => (
                  <UITextLabel intent='tag' key={tag}>
                    {tag}
                  </UITextLabel>
                ))}
            </div>
          </UITypography>
        </td>
      </tr>
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            라이센스
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.license || ''}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            파라미터 수
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.param_size || modelInfo?.param_size === '0' ? `${modelInfo?.param_size}B` : ''}
          </UITypography>
        </td>
      </tr>
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            지원 언어
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.langauges &&
              modelInfo?.langauges
                .split(',')
                .map(lang => lang.trim())
                .join(', ')}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            반입 진행 현황
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {MODEL_GARDEN_STATUS[modelInfo?.statusNm as keyof typeof MODEL_GARDEN_STATUS]?.detail}
          </UITypography>
        </td>
      </tr>
    </tbody>
  );
};
