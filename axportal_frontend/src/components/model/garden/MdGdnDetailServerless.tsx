import { UIIcon2, UITypography } from '@/components/UI';
import { MODEL_DEPLOY_PROVIDER } from '@/constants/deploy/modelDeploy.constants';
import type { MdGdnDetailInfoProps } from '@/pages/model/mdlGdn/MdGdnDetailPage';

export const MdGdnDetailServerless = ({ modelInfo }: MdGdnDetailInfoProps) => {
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
            모델유형
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.type}
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
            {modelInfo?.description}
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
              <UIIcon2 className={MODEL_DEPLOY_PROVIDER[modelInfo?.provider as keyof typeof MODEL_DEPLOY_PROVIDER] || MODEL_DEPLOY_PROVIDER.Etc} />
              {modelInfo?.provider}
            </div>
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            파라미터 수(B)
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
            URL
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.url}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            identifier
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {modelInfo?.identifier}
          </UITypography>
        </td>
      </tr>
    </tbody>
  );
};
