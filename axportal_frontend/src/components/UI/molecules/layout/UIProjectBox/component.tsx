import { useEffect, useRef, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { UIUnitGroup } from '@/components/UI/molecules';
import { AUTH_KEY } from '@/constants/auth';
import { useUser } from '@/stores/auth';

import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import type { ProjectItemType, UIProjectBoxProps } from './types';

/**
 * UIProjectBox 컴포넌트
 *
 * 비활성화 상태일 때 사용 가능한 클래스:
 * - project-box--disabled: 전체 박스 비활성화 (클릭 불가, 회색 처리)
 *
 * 예시:
 * <UIProjectBox className="project-box--disabled" />
 */

const ProjectItem = ({ item, onProjectSelect }: { item: ProjectItemType; onProjectSelect: (e: React.MouseEvent<HTMLElement>, id?: string) => void }) => {
  const handleClick = (e: React.MouseEvent<HTMLElement>) => {
    onProjectSelect?.(e, item.id);
  };

  return item.id === '-999' ? (
    <li>
      <Button className={`flex-btn-group${item.selected ? ' active' : ''}`} onClick={handleClick}>
        <UIIcon2 className='ic-system-16-public' />
        <UITypography variant='body-2' className='text-body-3 primary-400 w-[246px] truncate'>
          {item.name}
        </UITypography>
      </Button>
    </li>
  ) : (
    <li>
      <Button className={`text-body-3 truncate${item.selected ? ' active' : ''}`} onClick={handleClick}>
        {item.name}
      </Button>
    </li>
  );
};

export function UIProjectBox({ projectList, className = '', onCreateProject, onJoinProject, onQuitProject, onProjectSelect, disabled = false }: UIProjectBoxProps) {
  const { user } = useUser();
  const navigate = useNavigate();

  const publicProject = user?.projectList?.find(p => p.prjSeq === '-999');
  const isPortalAdmin = Number(publicProject?.prjRoleSeq ?? NaN) === -199;

  const currentProject = user?.projectList.find(p => p.active);
  const isProjectAdmin = Number(currentProject?.prjRoleSeq ?? NaN) === -299;

  // // 드롭다운 상태 제어
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const projectBoxRef = useRef<HTMLDivElement>(null);

  const handleDropdown = () => {
    setIsDropdownOpen(value => !value);
  };

  // 드롭다운 이벤트 핸들러
  const handleCreateProject = (e: React.MouseEvent<HTMLButtonElement>) => {
    setIsDropdownOpen(false);
    onCreateProject?.(e);
  };
  const handleJoinProject = (e: React.MouseEvent<HTMLButtonElement>) => {
    setIsDropdownOpen(false);
    onJoinProject?.(e);
  };
  const handleQuitProject = (e: React.MouseEvent<HTMLButtonElement>) => {
    setIsDropdownOpen(false);
    onQuitProject?.(e);
  };
  const handleProjectSelect = (e: React.MouseEvent<HTMLElement>, id?: string) => {
    setIsDropdownOpen(false);
    onProjectSelect?.(e, id);
  };

  useEffect(() => {
    if (disabled) {
      setIsDropdownOpen(false);
    }
  }, [disabled]);

  // 외부 클릭 시 드롭다운 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (isDropdownOpen && dropdownRef.current && projectBoxRef.current) {
        const target = event.target as Node;
        // 드롭다운 영역이나 프로젝트 박스 영역 내부가 아닌 경우 닫기
        if (!dropdownRef.current.contains(target) && !projectBoxRef.current.contains(target)) {
          setIsDropdownOpen(false);
        }
      }
    };

    if (isDropdownOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isDropdownOpen]);
  return (
    <div ref={projectBoxRef} className={`project-box ${className} ${disabled ? 'project-box--disabled' : ''}`}>
      <div className='project-box-inner' onClick={disabled ? undefined : handleDropdown}>
        <div className='project-box-left'>
          <UIIcon2 className='ic-gnb-24-project-box' />
          {disabled && (
            <UITypography variant='body-2' className='text-[#7e889b]'>
              프로젝트 전환 불가
            </UITypography>
          )}
          {!disabled && (
            <UITypography variant='body-2' className='w-[210px] truncate'>
              {projectList?.find(project => project.selected)?.name}
            </UITypography>
          )}
        </div>
        {projectList?.find(project => project.selected)?.id === '-999' && <span className='project-box-label'>전체</span>}
      </div>
      {isDropdownOpen && (
        <div ref={dropdownRef} className={`project-dropdown project-dropdown--scroll ${className}`}>
          {/* 중간 영역 - 참여 그룹 목록 */}
          {/* 숨김 필요시 클래스 추가: project-dropdown--no-group */}
          <div className='participate-groups'>
            {/* 그룹 목록 (헤더 포함) */}
            <div className='group-list'>
              {/* 참여 그룹 헤더 */}
              <UIUnitGroup gap={20} direction='row' vAlign='center' align='space-between'>
                <UITypography variant='body-2' className='group-count-title text-sb'>
                  {/* 참여 그룹 */}
                  {projectList?.find(project => project.selected)?.name}
                </UITypography>
                {(isPortalAdmin || isProjectAdmin) && (
                  <Button
                    className='btn-option-outlined'
                    onClick={() => {
                      setIsDropdownOpen(false);
                      navigate('/admin/project-mgmt/' + currentProject?.prjUuid);
                    }}
                  >
                    설정
                  </Button>
                )}
              </UIUnitGroup>
              {/* [251126_퍼블수정] : 일반 텍스트로 변경 */}
              <UITypography variant='body-3' className='secondary-neutral-600'>
                {user.activeProject.prjRoleNm}
              </UITypography>
              {/* 
              [251126_퍼블수정] : 아이콘(숫자) > 일반 텍스트로 변경으로 주석처리
              <UIUnitGroup gap={5} direction='row' vAlign='center'>
                <UIIcon2 className='ic-system-16-user' />
                <UITypography variant='body-2' className='primary-600'>
                  {projectList?.find(project => project.selected)?.count}
                </UITypography>
              </UIUnitGroup> 
              */}
            </div>
          </div>
          {/* 참여그룹 목록(추가) */}
          {(projectList?.length ?? 0) > 1 && (
            <div className='project-group'>
              <div className='project-group-head'>
                <UIUnitGroup gap={4} direction='row' className='mb-2'>
                  <UITypography variant='body-2' className='btn-semibold text-sb'>
                    참여 프로젝트
                  </UITypography>
                  <UITypography variant='body-2' className='primary-800 text-sb'>
                    {projectList?.length}
                  </UITypography>
                </UIUnitGroup>
              </div>
              <div className='project-group-wrap'>
                <ul className='project-group-list'>
                  {projectList?.map(project => (
                    <ProjectItem key={project.id} item={project} onProjectSelect={handleProjectSelect} />
                  ))}
                </ul>
              </div>
            </div>
          )}

          {/* 하단 영역 - 생성/참여/탈퇴 */}
          {/* 운영환경은 다음 영역 보이지 않음 */}
          {env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD && (
            <div className='join-section'>
              <div className='join-buttons'>
                <Button auth={AUTH_KEY.HOME.PROJECT_CREATE} className='join-button project-create-btn' onClick={handleCreateProject}>
                  프로젝트 생성 +
                </Button>
                <Button className='join-button' onClick={handleJoinProject}>
                  프로젝트 참여
                </Button>
                {(projectList?.length ?? 0) > 1 && (
                  <Button className='join-button' onClick={handleQuitProject}>
                    프로젝트 탈퇴
                  </Button>
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
