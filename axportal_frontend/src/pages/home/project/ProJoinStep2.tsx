// src/pages/home/project/ProJoinStep2.tsx
import React, { useEffect, useState } from 'react';

import { useAtom } from 'jotai';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetNotJoinPrivateProjDetail } from '@/services/home/proj/projBaseInfo.service';
import { projJoinSelectedProjectAtom } from '@/stores/home/proj/projJoinWizard.atoms';

// 날짜/시간을 'YYYY.MM.DD HH.mm.SS' 형태로 포맷팅
const formatDateTime = (value?: string | number | Date): string => {
  if (!value) return '';
  const date = value instanceof Date ? value : new Date(value);
  if (isNaN(date.getTime())) return '';

  const pad = (n: number) => n.toString().padStart(2, '0');
  const yyyy = date.getFullYear();
  const MM = pad(date.getMonth() + 1);
  const DD = pad(date.getDate());
  const HH = pad(date.getHours());
  const mm = pad(date.getMinutes());
  const SS = pad(date.getSeconds());

  // 요구 포맷: YYYY.MM.DD HH.mm.SS
  return `${yyyy}.${MM}.${DD} ${HH}:${mm}:${SS}`;
};

interface ProJoinStep2Props {
  onPreviousStep: () => void;
}

export const ProJoinStep2: React.FC<ProJoinStep2Props> = ({ onPreviousStep }) => {
  const [selectedProject] = useAtom(projJoinSelectedProjectAtom);
  // 포맷팅된 프로젝트 관리자 문자열
  const [formattedManagers, setFormattedManagers] = useState<string[]>([]);

  // 디버깅을 위한 콘솔 로그
  // console.log('ProJoinStep2 렌더링', { selectedProject });

  // selectedProject가 있을 경우에만 API 호출
  const { data: projectDetailData } = useGetNotJoinPrivateProjDetail(selectedProject?.id || '', {
    enabled: !!selectedProject?.id, // selectedProject.id가 존재할 때만 API 호출
  });

  useEffect(() => {
    if (projectDetailData) {
      // projectDetailData가 배열인지 확인
      if (Array.isArray(projectDetailData)) {
        const managersArray: string[] = [];

        projectDetailData.forEach((item /* , index */) => {
          // console.log(`아이템 ${index}:`, item);
          managersArray.push(item.projMgmteInfo);
        });
        const formatted = managersArray.join(',\n');
        setFormattedManagers([formatted]);
      }
      /*
      // 특정 속성이 있는지 확인하고 출력
      if (projectDetailData.projMgmteInfo) {
        setProjectManagers(Array.isArray(projectDetailData.projMgmteInfo)
          ? projectDetailData.projMgmteInfo
          : [projectDetailData.projMgmteInfo]);
      } */
    } else {
      setFormattedManagers([]);
    }
  }, [projectDetailData]);

  return (
    <>
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='프로젝트 정보 확인' description='선택하신 프로젝트 정보를 확인 후, 참여하고 싶으신 경우 참여 버튼을 눌러주세요.' position='right' />
        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
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
                    <col style={{ width: '624px' }} />
                    <col style={{ width: '152px' }} />
                    <col style={{ width: '624px' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          프로젝트명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {selectedProject?.projectName}
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
                          {selectedProject?.description}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          참여인원
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {selectedProject?.participantCount}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
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
                    <col style={{ width: '624px' }} />
                    <col style={{ width: '152px' }} />
                    <col style={{ width: '624px' }} />
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
                          {selectedProject?.createrInfo || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성 일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {formatDateTime(selectedProject?.fstCreatedAt)}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          프로젝트 관리자
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {formattedManagers.length > 0 &&
                            formattedManagers[0].split(',').map((manager, index, array) => (
                              <React.Fragment key={index}>
                                {manager.trim()}
                                {index < array.length - 1 && <br />}
                              </React.Fragment>
                            ))}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPopupBody>
        {/* 레이어 팝업 footer */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </>
  );
};
