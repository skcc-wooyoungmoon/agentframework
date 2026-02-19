import { UIIcon2, UILabel, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import { COMMON_DEPLOY_API_GW_STATUS } from '@/constants/deploy/commonDeploy.constants';
import { MODEL_DEPLOY_STATUS } from '@/constants/deploy/modelDeploy.constants';
import { useCopyHandler } from '@/hooks/common/util';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import type { SafetyFilter } from '@/services/deploy/safetyFilter';
import type { DeployApiGwStatus, DeployModelSafetyFilterList } from './DeployModelInformation';

interface SelfHostingDeployInfoTableProps extends DeployModelSafetyFilterList, DeployApiGwStatus {
  data: GetModelDeployResponse & { replicas: string };
  endpoint: string;
}

/**
 * Self Hosting 타입 배포 정보 테이블
 *
 * 레이아웃:
 * - 배포명 | 운영계 배포 여부
 * - 설명 (전체 너비)
 * - A.X 배포상태 | API Gateway 배포 상태
 * - Inflight Quantization | 복제 인스턴스 수
 * - 프레임워크 | Endpoint
 * - 입력 세이프티 필터 | 출력 세이프티 필터
 */
export function SelfHostingDeployInfoTable({ data, endpoint, inputFilters, outputFilters, apigwStatus }: SelfHostingDeployInfoTableProps) {
  const { handleCopy } = useCopyHandler();

  return (
    <tbody>
      {/* 배포명 | 운영계 배포 여부 */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            배포명
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.name}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            운영계 배포 여부
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? (data.production ? '배포' : '미배포') : '배포'}
          </UITypography>
        </td>
      </tr>
      {/* 설명 (전체 너비) */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            설명
          </UITypography>
        </th>
        <td colSpan={3}>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.description}
          </UITypography>
        </td>
      </tr>
      {/* A.X 배포상태 | API Gateway 배포 상태 */}
      <tr style={{ height: '68px' }}>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            A.X 배포상태
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            <UILabel variant='badge' intent={(MODEL_DEPLOY_STATUS[data.status as keyof typeof MODEL_DEPLOY_STATUS].intent as UILabelIntent) || 'gray'}>
              {MODEL_DEPLOY_STATUS[data.status as keyof typeof MODEL_DEPLOY_STATUS].label || data.status}
            </UILabel>
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            API Gateway
            <br />
            배포 상태
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            <UILabel variant='badge' intent={COMMON_DEPLOY_API_GW_STATUS[apigwStatus.status as keyof typeof COMMON_DEPLOY_API_GW_STATUS].intent as UILabelIntent}>
              {COMMON_DEPLOY_API_GW_STATUS[apigwStatus.status as keyof typeof COMMON_DEPLOY_API_GW_STATUS].label || apigwStatus.status}
            </UILabel>
          </UITypography>
        </td>
      </tr>
      {/* Inflight Quantization | 복제 인스턴스 수 */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            Inflight Quantization
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.quantization?.inflightQuantization ? 'Y' : 'N'}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            복제 인스턴스 수
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.replicas}
          </UITypography>
        </td>
      </tr>
      {/* 프레임워크 | Endpoint */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            프레임워크
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {data.runtime}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            Endpoint
          </UITypography>
        </th>
        <td colSpan={3}>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            <div className='flex align-center gap-2'>
              <span>{endpoint}</span>
              <a href='#none'>
                <UIIcon2 className='ic-system-20-copy-gray' style={{ display: 'block' }} onClick={() => handleCopy(endpoint)} />
              </a>
            </div>
          </UITypography>
        </td>
      </tr>
      {/* 입력 세이프티 필터 | 출력 세이프티 필터 */}
      <tr>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            입력 세이프티 필터
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {inputFilters?.map((filter: SafetyFilter) => filter.filterGroupName).join(', ') || ''}
          </UITypography>
        </td>
        <th>
          <UITypography variant='body-2' className='secondary-neutral-900'>
            출력 세이프티 필터
          </UITypography>
        </th>
        <td>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            {outputFilters?.map((filter: SafetyFilter) => filter.filterGroupName).join(', ') || ''}
          </UITypography>
        </td>
      </tr>
    </tbody>
  );
}
