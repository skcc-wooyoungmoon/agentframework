import type { GetModelDeployResponse } from '@/services/deploy/model/types';

import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';
import { MODEL_DEPLOY_PROVIDER } from '@/constants/deploy/modelDeploy.constants';

interface SelfHostingModelInfoTableProps {
  data: GetModelDeployResponse;
}

/**
 * Self Hosting 타입 모델 정보 테이블
 *
 * 레이아웃:
 * - 첫 번째 행: 모델명 | 표시이름
 * - 두 번째 행: 설명 (전체 너비)
 * - 세 번째 행: 공급사 | 모델타입
 * - 네 번째 행: 배포타입 (전체 너비)
 */
export function SelfHostingModelInfoTable({ data }: SelfHostingModelInfoTableProps) {
  return (
    // <UIArticle>
    //   <div className='article-header'>
    //     <UITypography variant='title-4' className='secondary-neutral-900'>
    //       모델 정보
    //     </UITypography>
    //   </div>
    //   <div className='article-body'>
    //     <div className='border-t border-black'>
    //       <table className='tbl-v'>
    //         <colgroup>
    //           <col style={{ width: '152px' }} />
    //           <col style={{ width: '624px' }} />
    //           <col style={{ width: '152px' }} />
    //           <col style={{ width: '624px' }} />
    //         </colgroup>
    //         <tbody>
    //           {/* 첫 번째 행: 모델명 | 표시이름 */}
    //           <tr>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 모델명
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.modelName}
    //               </UITypography>
    //             </td>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 표시이름
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.displayName}
    //               </UITypography>
    //             </td>
    //           </tr>
    //           {/* 두 번째 행: 설명 (전체 너비) */}
    //           <tr>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 설명
    //               </UITypography>
    //             </th>
    //             <td colSpan={3}>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.modelDescription}
    //               </UITypography>
    //             </td>
    //           </tr>
    //           {/* 세 번째 행: 공급사 | 모델타입 */}
    //           <tr>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 공급사
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UIUnitGroup gap={4} direction='row' vAlign='center'>
    //                 <UIIcon2 className={MODEL_DEPLOY_PROVIDER[data.providerName as keyof typeof MODEL_DEPLOY_PROVIDER] || MODEL_DEPLOY_PROVIDER.Etc} />
    //                 <UITypography variant='body-2' className='secondary-neutral-600'>
    //                   {data.providerName}
    //                 </UITypography>
    //               </UIUnitGroup>
    //             </td>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 모델타입
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.type}
    //               </UITypography>
    //             </td>
    //           </tr>
    //           {/* 네 번째 행: 배포타입 (전체 너비) */}
    //           <tr>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 배포타입
    //               </UITypography>
    //             </th>
    //             <td colSpan={3}>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.servingType}
    //               </UITypography>
    //             </td>
    //           </tr>
    //         </tbody>
    //       </table>
    //     </div>
    //   </div>
    // </UIArticle>

    <tbody>
      {/* 첫 번째 행: 모델명 | 표시이름 */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            모델명
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.modelName}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            표시이름
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.displayName}
          </UITypography>
        </td>
      </tr>
      {/* 두 번째 행: 설명 (전체 너비) */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            설명
          </UITypography>
        </th>
        <td colSpan={3}>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.modelDescription}
          </UITypography>
        </td>
      </tr>
      {/* 세 번째 행: 공급사 | 모델타입 */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            공급사
          </UITypography>
        </th>
        <td>
          <UIUnitGroup gap={4} direction='row' vAlign='center'>
            <UIIcon2 className={MODEL_DEPLOY_PROVIDER[data.providerName as keyof typeof MODEL_DEPLOY_PROVIDER] || MODEL_DEPLOY_PROVIDER.Etc} />
            <UITypography variant='body-2' className='secondary-neutral-600'>
              {data.providerName}
            </UITypography>
          </UIUnitGroup>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            모델타입
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.type}
          </UITypography>
        </td>
      </tr>
      {/* 네 번째 행: 배포타입 (전체 너비) */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            배포타입
          </UITypography>
        </th>
        <td colSpan={3}>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.servingType}
          </UITypography>
        </td>
      </tr>
    </tbody>
  );
}
