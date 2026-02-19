import React, { type ReactNode } from 'react';

import { useSearchParams } from 'react-router-dom';

import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIPageBody } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';

import { DesignLayout } from '../components/DesignLayout';

import { AG_010102_P17 } from './agent/AG_010102_P17';
import { AG_010102_P18 } from './agent/AG_010102_P18';
import { AG_010102_P20 } from './agent/AG_010102_P20';
import { AG_010102_P21 } from './agent/AG_010102_P21';
import { AG_010102_P22 } from './agent/AG_010102_P22';
import { AG_010102_P23 } from './agent/AG_010102_P23';
import { AG_010102_P24 } from './agent/AG_010102_P24';
import { AG_010102_P25 } from './agent/AG_010102_P25';
import { AG_010102_P26 } from './agent/AG_010102_P26';
import { AG_010102_P32 } from './agent/AG_010102_P32';
import { AG_010102_P33 } from './agent/AG_010102_P33';
import { AG_010102_P34 } from './agent/AG_010102_P34';
import { AG_010102_P35 } from './agent/AG_010102_P35';
import { AG_010102_P36 } from './agent/AG_010102_P36';
import { AG_030101_P03 } from './agent/AG_030101_P03';
import { CM_020101_P02 } from './common/CM_020101_P02';
import { CM_020101_P03 } from './common/CM_020101_P03';
import { POP_ALERT } from './common/POP_ALERT';
import { POP_ALERT02 } from './common/POP_ALERT02';
import { POP_ALERT03 } from './common/POP_ALERT03';
import { POP_ALERT04 } from './common/POP_ALERT04';
import { POP_ALERT05 } from './common/POP_ALERT05';
import { POP_ALERT06 } from './common/POP_ALERT06';
import { POP_ALERT07 } from './common/POP_ALERT07';
import { DT_010102_P01 } from './data/DT_010102_P01';
import { DT_020101_P08 } from './data/DT_020101_P08';
import { DT_020101_P11 } from './data/DT_020101_P11';
import { DT_020302_P06 } from './data/DT_020302_P06';
import { DT_020303_P01 } from './data/DT_020303_P01';
import { DT_020303_P03 } from './data/DT_020303_P03';
import { DT_020303_P06 } from './data/DT_020303_P06';
import { DT_030101_P04 } from './data/DT_030101_P04';
import { DP_010102_P02 } from './deploy/DP_010102_P02';
import { DP_040102_P02 } from './deploy/DP_040102_P02';
import { HM_010101_P04 } from './home/HM_010101_P04';
import LG_010101_P02 from './login/LG_010101_P02';
import LG_010101_P03 from './login/LG_010101_P03';
import LG_010101_P06 from './login/LG_010101_P06';
import { MD_030101_P07 } from './model/MD_030101_P07';
import { MD_030102_P02 } from './model/MD_030102_P02';
import { MD_030103_P01 } from './model/MD_030103_P01';
import { MD_040101_P01 } from './model/MD_040101_P01';
import { MD_040101_P02 } from './model/MD_040101_P02';
import { MD_040101_P03 } from './model/MD_040101_P03';
import { PR_010102_P03 } from './prompt/PR_010102_P03';
import { PR_030101_P02 } from './prompt/PR_030101_P02';
import { EV_010101_P01 } from './eval/EV_010101_P01';
import { EV_010201_P01 } from './eval/EV_010201_P01';
import { EV_010301_P01 } from './eval/EV_010301_P01';
import { MD_050102_P01 } from './model/MD_050102_P01';
import { HM_060101_P02 } from './home/HM_060101_P02';
import { HM_060101_P06 } from './home/HM_060101_P06';

type ModalItem = {
  id: string;
  label: string;
  type: '2xsmall' | 'xsmall' | 'small' | 'medium' | 'large';
  element: ReactNode;
  showFooter?: boolean;
  cancelText?: string;
  confirmText?: string;
  confirmDisabled?: boolean;
};

const _modal_list: ModalItem[] = [
  {
    id: 'POP_ALERT',
    label: '오류',
    type: '2xsmall',
    element: <POP_ALERT />,
    showFooter: true,
  },
  {
    id: 'POP_ALERT02',
    label: '안내',
    type: '2xsmall',
    element: <POP_ALERT02 />,
    showFooter: true,
  },
  {
    id: 'POP_ALERT03',
    label: '안내',
    type: '2xsmall',
    element: <POP_ALERT03 />,
    showFooter: true,
  },
  {
    id: 'POP_ALERT04',
    label: '사전 안내',
    type: '2xsmall',
    element: <POP_ALERT04 />,
    showFooter: true,
  },
  {
    id: 'POP_ALERT05',
    label: '안내',
    type: '2xsmall',
    element: <POP_ALERT05 />,
    cancelText: '아니요',
    confirmText: '예',
    showFooter: true,
  },
  {
    id: 'POP_ALERT06',
    label: '안내',
    type: '2xsmall',
    element: <POP_ALERT06 />,
    cancelText: '아니요',
    confirmText: '예',
    showFooter: true,
  },
  {
    id: 'POP_ALERT07',
    label: '안내',
    type: '2xsmall',
    element: <POP_ALERT07 />,
    confirmText: '확인',
    showFooter: true,
  },
  {
    id: 'LG_010101_P02',
    label: '로그아웃 안내',
    type: 'medium',
    element: <LG_010101_P02 />,
  },
  {
    id: 'LG_010101_P03',
    label: '로그아웃 안내',
    type: 'medium',
    element: <LG_010101_P03 />,
  },
  {
    id: 'LG_010101_P06',
    label: 'SMS 인증',
    type: 'xsmall',
    element: <LG_010101_P06 />,
    showFooter: true,
  },
  {
    id: 'DT_020302_P06',
    label: 'MD파일 구성 조회',
    type: 'large',
    element: <DT_020302_P06 />,
    showFooter: false,
    cancelText: '',
    confirmDisabled: true,
  },
  {
    id: 'MD_030101_P07',
    label: '콘솔 로그',
    type: 'large',
    element: <MD_030101_P07 />,
    showFooter: false,
  },
  {
    id: 'MD_030102_P02',
    label: '학습 데이터세트 선택',
    type: 'large',
    element: <MD_030102_P02 />,
    showFooter: true,
    cancelText: '닫기',
    confirmText: '확인',
  },
  {
    id: 'CM_020101_P03',
    label: '간편결재 결과',
    type: 'medium',
    element: <CM_020101_P03 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P17',
    label: '로그',
    type: 'medium',
    element: <AG_010102_P17 />,
    showFooter: false,
  },
  {
    id: 'AG_010102_P18',
    label: '키테이블 선택',
    type: 'large',
    element: <AG_010102_P18 />,
    showFooter: true,
    cancelText: '',
    confirmText: '저장',
  },
  {
    id: 'DT_030101_P04',
    label: '미리보기',
    type: 'large',
    element: <DT_030101_P04 />,
    showFooter: true,
    cancelText: '',
    confirmText: '미리보기',
  },
  {
    id: 'AG_010102_P20',
    label: 'LLM 선택',
    type: 'large',
    element: <AG_010102_P20 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P21',
    label: '프롬프트 선택',
    type: 'large',
    element: <AG_010102_P21 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P22',
    label: '퓨샷 선택',
    type: 'large',
    element: <AG_010102_P22 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P23',
    label: '도구 선택',
    type: 'large',
    element: <AG_010102_P23 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P24',
    label: '코드 수정',
    type: 'medium',
    element: <AG_010102_P24 />,
    showFooter: true,
    cancelText: '',
    confirmText: '저장',
  },
  {
    id: 'AG_010102_P25',
    label: '지식 선택',
    type: 'large',
    element: <AG_010102_P25 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P26',
    label: 'Re-Rank 모델 선택',
    type: 'large',
    element: <AG_010102_P26 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P32',
    label: 'MCP서버 선택',
    type: 'large',
    element: <AG_010102_P32 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P33',
    label: 'MCP서버 툴 리스트',
    type: 'large',
    element: <AG_010102_P33 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P34',
    label: '에이전트 APP 선택',
    type: 'large',
    element: <AG_010102_P34 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'AG_010102_P35',
    label: 'Python Code 내보내기',
    type: 'large',
    element: <AG_010102_P35 />,
    showFooter: true,
    confirmText: '내보내기',
  },
  {
    id: 'AG_010102_P36',
    label: 'LLM 파라미터',
    type: 'medium',
    element: <AG_010102_P36 />,
    showFooter: true,
    confirmText: '확인',
  },
  {
    id: 'MD_040101_P03',
    label: '파라미터 설정',
    type: 'large',
    element: <MD_040101_P03 />,
    showFooter: true,
    cancelText: '초기화',
    confirmText: '저장',
  },
  {
    id: 'MD_040101_P02',
    label: '추론 프롬프트 선택',
    type: 'large',
    element: <MD_040101_P02 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'MD_040101_P01',
    label: '모델 선택',
    type: 'large',
    element: <MD_040101_P01 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'DP_010102_P02',
    label: 'API Key 발급',
    type: 'medium',
    element: <DP_010102_P02 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
    confirmDisabled: true,
  },
  {
    id: 'DP_040102_P02',
    label: '세이프티 필터',
    type: 'large',
    element: <DP_040102_P02 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'PR_010102_P03',
    label: '워크플로우 추가',
    type: 'large',
    element: <PR_010102_P03 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'CM_020101_P02',
    label: '결재자 선택',
    type: 'large',
    element: <CM_020101_P02 />,
    showFooter: true,
    cancelText: '취소',
    confirmText: '확인',
  },
  {
    id: 'HM_010101_P04',
    label: 'IDE 선택',
    type: 'medium',
    element: <HM_010101_P04 />,
    showFooter: true,
    cancelText: '사용종료',
    confirmText: '바로가기',
  },
  {
    id: 'DT_020303_P03 ',
    label: '미리보기',
    type: 'medium',
    element: <DT_020303_P03 />,
    showFooter: false,
  },
  {
    id: 'DT_020303_P06 ',
    label: '청크 정보',
    type: 'medium',
    element: <DT_020303_P06 />,
    showFooter: false,
  },
  {
    id: 'PR_030101_P02',
    label: '가드레일 프롬프트 선택',
    type: 'large',
    element: <PR_030101_P02 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'MD_030103_P01',
    label: '파인튜닝 선택',
    type: 'large',
    element: <MD_030103_P01 />,
    showFooter: true,
    cancelText: '',
    confirmText: '확인',
  },
  {
    id: 'DT_020101_P08',
    label: '파일 구성 조회',
    type: 'large',
    element: <DT_020101_P08 />,
    showFooter: false,
  },
  {
    id: 'DT_020101_P11',
    label: '지식 등록 테스트',
    type: 'medium',
    element: <DT_020101_P11 />,
    showFooter: true,
    cancelText: '',
    confirmText: '테스트',
  },
  {
    id: 'DT_010102_P01',
    label: '메타데이터',
    type: 'medium',
    element: <DT_010102_P01 />,
    showFooter: false,
  },
  {
    id: 'DT_020303_P01',
    label: '메타데이터',
    type: 'medium',
    element: <DT_020303_P01 />,
    showFooter: false,
  },
  {
    id: 'AG_030101_P03',
    label: '툴 상세',
    type: 'medium',
    element: <AG_030101_P03 />,
    showFooter: false,
  },
  {
    id: 'EV_010101_P01',
    label: '저지평가 가이드',
    type: 'large',
    element: <EV_010101_P01 />,
    showFooter: false,
  },
  {
    id: 'EV_010201_P01',
    label: '정성평가 가이드',
    type: 'large',
    element: <EV_010201_P01 />,
    showFooter: false,
  },
  {
    id: 'EV_010301_P01',
    label: '정량평가 가이드',
    type: 'large',
    element: <EV_010301_P01 />,
    showFooter: false,
  },
  {
    id: 'MD_050102_P01',
    label: '모델 검사 결과',
    type: 'large',
    element: <MD_050102_P01 />,
    showFooter: false,
  },
  {
    id: 'HM_060101_P02',
    label: '사용 기간 연장',
    type: 'large',
    element: <HM_060101_P02 />,
    showFooter: true,
    confirmText: '설정',
  },
  {
    id: 'HM_060101_P06',
    label: '권한 계정 목록',
    type: 'large',
    element: <HM_060101_P06 />,
    showFooter: true,
    confirmText: '확인',
  },
];

const getModalById = (id: string) => {
  return _modal_list.find(item => item.id === id);
};

export const ModalList = () => {
  const { openModal } = useModal();
  const [searchParams] = useSearchParams();
  const modalId = searchParams.get('id'); // "DT_0101010"
  const modalItem = modalId ? getModalById(modalId) : undefined;

  // 한 번만 열기 위한 ref
  const openedRef = React.useRef(false);
  React.useEffect(() => {
    if (modalItem && !openedRef.current) {
      openedRef.current = true;
      openModal({
        title: modalItem.label,
        type: modalItem.type,
        body: modalItem.element,
        showFooter: modalItem.showFooter,
        cancelText: modalItem.cancelText,
        confirmText: modalItem.confirmText,
        confirmDisabled: modalItem.confirmDisabled,
      });
    }
  }, [modalItem, openModal]);

  function onOpenModal(target: ModalItem) {
    openModal({
      title: target.label,
      type: target.type,
      body: target.element,
      showFooter: target.showFooter,
      cancelText: target.cancelText,
      confirmText: target.confirmText,
      confirmDisabled: target.confirmDisabled,
    });
  }
  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='모달 화면 리스트' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 아티클 */}
          <article className='table-wrap'>
            <table className='tbl_type_a'>
              <colgroup>
                <col width='20%' />
                <col width='60%' />
                <col width='20%' />
              </colgroup>
              <thead>
                <tr>
                  <th>id</th>
                  <th>화면명</th>
                  <th>보기</th>
                </tr>
              </thead>
              <tbody>
                {_modal_list.map(item => (
                  <tr key={item.id}>
                    <td>{item.id}</td>
                    <td>{item.label}</td>
                    <td>
                      <UIButton2 className='btn-tertiary-blue' style={{ width: '150px' }} onClick={() => onOpenModal(item)}>
                        보기
                      </UIButton2>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </article>
        </UIPageBody>

        {/* 페이지 푸터 */}
        {/* <UIPageFooter></UIPageFooter> */}
      </section>
    </DesignLayout>
  );
};
