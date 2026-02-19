import type { GetModelDeployResponse } from '@/services/deploy/model/types';

import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';
import { MODEL_DEPLOY_PROVIDER } from '@/constants/deploy/modelDeploy.constants';

interface ServerlessModelInfoTableProps {
  data: GetModelDeployResponse;
}

/**
 * Serverless 타입 모델 정보 테이블
 *
 * 레이아웃:
 * - 각 필드가 한 행씩 차지 (단일 컬럼)
 * - 모델명
 * - 설명
 * - 표시이름
 * - 모델유형
 * - 배포유형
 * - 공급사
 */
export function ServerlessModelInfoTable({ data }: ServerlessModelInfoTableProps) {
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
    //           {/* 모델명 */}
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
    //                 설명
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.modelDescription}
    //               </UITypography>
    //             </td>
    //           </tr>
    //           {/* 설명 */}
    //           {/* <tr>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 설명
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.modelDescription}
    //               </UITypography>
    //             </td>
    //           </tr> */}
    //           {/* 표시이름 */}
    //           <tr>
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
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 모델유형
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.type}
    //               </UITypography>
    //             </td>
    //           </tr>
    //           {/* 모델유형 */}
    //           {/* <tr>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 모델유형
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.type}
    //               </UITypography>
    //             </td>
    //           </tr> */}
    //           {/* 배포유형 */}
    //           <tr>
    //             <th>
    //               <UITypography variant='body-2' className='secondary-neutral-900'>
    //                 배포유형
    //               </UITypography>
    //             </th>
    //             <td>
    //               <UITypography variant='body-2' className='secondary-neutral-600'>
    //                 {data.servingType}
    //               </UITypography>
    //             </td>
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
    //           </tr>
    //           {/* 공급사 */}
    //           {/* <tr>
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
    //           </tr> */}
    //         </tbody>
    //       </table>
    //     </div>
    //   </div>
    // </UIArticle>

    <tbody>
      {/* 모델명 */}
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
            설명
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.modelDescription}
          </UITypography>
        </td>
      </tr>
      {/* 설명 */}
      {/* <tr>
      <th>
        <UITypography variant='body-2' className='secondary-neutral-900'>
          설명
        </UITypography>
      </th>
      <td>
        <UITypography variant='body-2' className='secondary-neutral-600'>
          {data.modelDescription}
        </UITypography>
      </td>
    </tr> */}
      {/* 표시이름 */}
      <tr>
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
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            모델유형
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.type}
          </UITypography>
        </td>
      </tr>
      {/* 모델유형 */}
      {/* <tr>
      <th>
        <UITypography variant='body-2' className='secondary-neutral-900'>
          모델유형
        </UITypography>
      </th>
      <td>
        <UITypography variant='body-2' className='secondary-neutral-600'>
          {data.type}
        </UITypography>
      </td>
    </tr> */}
      {/* 배포유형 */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            배포유형
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.servingType}
          </UITypography>
        </td>
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
      </tr>
      {/* 공급사 */}
      {/* <tr>
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
    </tr> */}
    </tbody>
  );
}
