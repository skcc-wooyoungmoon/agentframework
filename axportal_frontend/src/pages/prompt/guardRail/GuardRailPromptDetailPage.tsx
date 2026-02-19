import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAtomValue } from 'jotai';
import { useQueryClient } from '@tanstack/react-query';

import { UITypography } from '@/components/UI/atoms';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { selectedGuardRailPromptAtom } from '@/stores/prompt';
import { useModal } from '@/stores/common/modal';
import { useDeleteGuardRailPrompt, useGetGuardRailPromptById } from '@/services/prompt/guardRail/guardRail.services';
import { GuardRailPromptUpdatePage } from './GuardRailPromptUpdatePage';
import type { GetGuardRailPromptByIdResponse } from '@/services/prompt/guardRail/types';
import { useUser } from '@/stores';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';

export const GuardRailPromptDetailPage: React.FC = () => {
  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();
  const { user } = useUser();
  const queryClient = useQueryClient();
  
  const isAuthorized = (user?.functionAuthList?.includes('A040304') ?? false) && user?.activeProject?.prjRoleSeq === '-199' && user?.activeProject?.prjSeq === '-999';

  // 조타이에서 선택된 가드레일 프롬프트의 ID 가져오기 + 새로고침 대비 로컬스토리지 복구
  const selectedData = useAtomValue(selectedGuardRailPromptAtom);
  const STORAGE_KEY = 'selectedGuardRailPromptId';
  const initialId = selectedData?.uuid || '';
  const [effectiveGuardRailId, setEffectiveGuardRailId] = useState<string>(initialId);

  // guardRailId 저장/복구
  useEffect(() => {
    if (initialId) {
      try {
        localStorage.setItem(STORAGE_KEY, initialId);
        setEffectiveGuardRailId(initialId);
      } catch (e) {
        console.warn('Failed to store guardRailId:', e);
      }
    } else {
      try {
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored) {
          setEffectiveGuardRailId(stored);
        }
      } catch (e) {
        console.warn('Failed to restore guardRailId:', e);
      }
    }
  }, [initialId]);

  // 수정 모달 상태
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);

  // 상세 데이터 API 호출
  const { data: fetchedGuardRailData, refetch } = useGetGuardRailPromptById(
    { id: effectiveGuardRailId },
    {
      enabled: !!effectiveGuardRailId,
      staleTime: 0, // 데이터가 즉시 stale 상태가 됨
      gcTime: 0, // 캐시를 즉시 삭제
      refetchOnMount: 'always', // 마운트 시 항상 재조회
    }
  );

  // 로컬 상태로 데이터 관리 (수정 시 부분 업데이트를 위함)
  const [guardRailData, setGuardRailData] = useState<GetGuardRailPromptByIdResponse | null>(null);

  // API에서 데이터를 가져오면 로컬 상태 업데이트
  useEffect(() => {
    if (fetchedGuardRailData) {
      setGuardRailData(fetchedGuardRailData);
    }
  }, [fetchedGuardRailData]);

  // 삭제 mutation
  const { mutate: deleteGuardRailPrompt } = useDeleteGuardRailPrompt({
    onSuccess: () => {
      openAlert({
        title: '안내',
        message: '삭제되었습니다.',
        onConfirm: () => {
          navigate('/prompt/guardrail');
        },
      });
    },
    onError: () => {
      openAlert({
        title: '안내',
        message: '실패하였습니다.',
      });
    },
  });

  // 삭제 버튼 클릭 핸들러
  const handleDelete = () => {
    if (!isAuthorized) {
      openAlert({
        title: '안내',
        message: '가드레일 프롬프트 삭제 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    const deleteId = guardRailData?.uuid || effectiveGuardRailId;

    if (!deleteId) {
      openAlert({
        title: '안내',
        message: '삭제할 데이터를 찾을 수 없습니다.',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteGuardRailPrompt({ id: deleteId });
      },
    });
  };

  // 수정 버튼 클릭 핸들러
  const handleEdit = () => {
    if (!isAuthorized) {
      openAlert({
        title: '안내',
        message: '가드레일 프롬프트 수정 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    setIsUpdateModalOpen(true);
  };

  // 수정 성공 후 콜백 - 캐시 강제 갱신
  const handleUpdateSuccess = async (updatedData: { name: string; message: string; tags: string[] }) => {
    // 관련 쿼리 캐시 무효화
    await queryClient.invalidateQueries({
      queryKey: ['guardRail', 'prompts'],
    });
    // 태그 목록 캐시도 무효화 (새로운 태그가 추가되었을 수 있음)
    await queryClient.invalidateQueries({
      queryKey: ['guardRail', 'prompts', 'tags'],
    });
    
    // 상세 조회 쿼리 강제 재조회
    await refetch();
    
    // 로컬 상태도 업데이트 (즉시 반영)
    if (guardRailData) {
      setGuardRailData({
        ...guardRailData,
        name: updatedData.name,
        message: updatedData.message,
        tags: updatedData.tags.map(tag => ({ tag })),
        updatedAt: new Date().toISOString(),
      });
    }
  };

  const formatKoreanDateTime = (dateString?: string | null) => {
    if (!dateString || !dateString.trim()) {
      return '';
    }

    const trimmed = dateString.trim();

    const normalizedDate = trimmed.replace(
      /^(\d{4})\.(\d{2})\.(\d{2})/,
      (_match, year, month, day) => `${year}-${month}-${day}`
    );
    let normalizedDateTime = normalizedDate.replace(' ', 'T');

    if (/T\d{1,2}:\d{2}$/.test(normalizedDateTime)) {
      normalizedDateTime += ':00';
    }

    let date = new Date(`${normalizedDateTime}Z`);

    if (isNaN(date.getTime())) {
      date = new Date(`${normalizedDateTime.replace(/\./g, '-') }Z`);
    }

    if (isNaN(date.getTime())) {
      return '-';
    }

    const kstDate = new Date(date.getTime() + 9 * 60 * 60 * 1000);

    const year = kstDate.getUTCFullYear();
    const month = String(kstDate.getUTCMonth() + 1).padStart(2, '0');
    const day = String(kstDate.getUTCDate()).padStart(2, '0');
    const hours = String(kstDate.getUTCHours()).padStart(2, '0');
    const minutes = String(kstDate.getUTCMinutes()).padStart(2, '0');
    const seconds = String(kstDate.getUTCSeconds()).padStart(2, '0');

    return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
  };

  const formatUserWithDept = (name?: string | null, dept?: string | null) => {
    if (name) {
      return `${name}${dept ? ` | ${dept}` : ''}`;
    }
    return dept || '';
  };

  const creatorInfo = formatUserWithDept(guardRailData?.created_by_name, guardRailData?.created_by_depts);
  const lastEditorInfo = formatUserWithDept(guardRailData?.updated_by_name, guardRailData?.updated_by_depts) || creatorInfo;
  const createdDateText = formatKoreanDateTime(guardRailData?.createdAt);
  const lastEditedDateText = formatKoreanDateTime(guardRailData?.updatedAt) || createdDateText;

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='가드레일 프롬프트 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 기본 정보 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                기본 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '152px' }} />
                    <col style={{ width: 'auto' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {guardRailData?.name || '-'}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          가드레일 프롬프트
                        </UITypography>
                      </th>
                        <td colSpan={3}>
                          <UITypography
                            variant='body-2'
                            className='secondary-neutral-600'
                            style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }} >
                            {guardRailData?.message || '-'}
                          </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          태그
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <div className='flex items-center gap-1'>
                            {guardRailData?.tags && guardRailData.tags.length > 0
                              ? guardRailData.tags.map((tag, index) => {
                                  const tagText = typeof tag === 'string' ? tag : (tag as any).tag;
                                  return (
                                    <UITextLabel key={index} intent='tag'>
                                      {tagText}
                                    </UITextLabel>
                                  );
                                })
                              : '-'}
                          </div>
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 담당자 정보 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                담당자 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '152px' }} />
                    <col style={{ width: 'calc(100% - 66%)' }} />
                    <col style={{ width: '152px' }} />
                    <col style={{ width: 'auto' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {creatorInfo}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {createdDateText}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {lastEditorInfo}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {lastEditedDateText}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button
                auth={AUTH_KEY.PROMPT.GUARDRAIL_PROMPT_DELETE}
                className='btn-primary-gray' onClick={handleDelete}>
                삭제
              </Button>
              <Button 
               auth={AUTH_KEY.PROMPT.GUARDRAIL_PROMPT_UPDATE}
               className='btn-primary-blue' onClick={handleEdit}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>

      {/* 수정 모달 */}
      <GuardRailPromptUpdatePage isOpen={isUpdateModalOpen} onClose={() => setIsUpdateModalOpen(false)} guardRailData={guardRailData} onUpdateSuccess={handleUpdateSuccess} />
    </>
  );
};
