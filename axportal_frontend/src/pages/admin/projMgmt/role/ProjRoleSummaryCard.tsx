import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIUnitGroup } from '@/components/UI/molecules';
import { type RoleDetailType } from '@/services/admin/projMgmt';

interface ProjRoleSummaryCardProps {
  projectNm?: string;
  roleInfo: RoleDetailType;
}

export const ProjRoleSummaryCard = ({ projectNm, roleInfo }: ProjRoleSummaryCardProps) => {
  return (
    <UIArticle className='article-filter pb-4'>
      <div className='project-card'>
        <UIUnitGroup gap={8} direction='row' vAlign='center' className='mb-6'>
          <UIIcon2 className='ic-system-24-project' aria-hidden='true' />
          <UITypography variant='title-4' className='secondary-neutral-700'>
            {projectNm || '-'}
          </UITypography>
        </UIUnitGroup>
        <ul className='flex flex-col gap-4'>
          <li>
            <UITypography variant='body-1' className='col-gray'>
              역할명
            </UITypography>
            <UITypography variant='title-4' className='secondary-neutral-700'>
              {roleInfo.roleNm}
            </UITypography>
          </li>
          <li>
            <UITypography variant='body-1' className='col-gray'>
              설명
            </UITypography>
            <UITypography variant='title-4' className='secondary-neutral-700'>
              {roleInfo.dtlCtnt}
            </UITypography>
          </li>
        </ul>
      </div>
    </UIArticle>
  );
};
