import { UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules';
import { type ProjectDetailType } from '@/services/admin/projMgmt';

interface ProjSummaryCardProps {
  projectInfo: ProjectDetailType;
}

export const ProjSummaryCard = ({ projectInfo }: ProjSummaryCardProps) => {
  return (
    <UIArticle className='article-filter pb-4'>
      <div className='project-card bg-gray'>
        <ul className='flex flex-col gap-4'>
          <li>
            <UITypography variant='body-1' className='col-gray'>
              프로젝트명
            </UITypography>
            <UITypography variant='title-4' className='secondary-neutral-700'>
              {projectInfo.prjNm}
            </UITypography>
          </li>
          <li>
            <UITypography variant='body-1' className='col-gray'>
              설명
            </UITypography>
            <UITypography variant='title-4' className='secondary-neutral-700'>
              {projectInfo.dtlCtnt}
            </UITypography>
          </li>
        </ul>
      </div>
    </UIArticle>
  );
};
