import React, { useState } from 'react';

import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupHeader, UIPopupFooter, UIFormField, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UILayerPopup, UIQnaFewshot, UIPopupAside } from '@/components/UI/organisms';
import type { QnaPair } from '@/components/UI/organisms';

import { UIButton2 } from '../../../components/UI/atoms/UIButton2';
import { DesignLayout } from '../../components/DesignLayout';

export const PR_020101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const [datasetName, setDatasetName] = useState(''); // 에러 상태를 위해 빈 값으로 시작
  const [tags, setTags] = useState<string[]>([]); // 에러 상태를 위해 빈 배열로 시작
  // 에러 상태를 위해 빈 값과 에러 플래그 설정
  const [qnaPairs, setQnaPairs] = useState<QnaPair[]>([
    {
      id: '1',
      question: '', // 빈 값으로 설정
      answer: '', // 빈 값으로 설정
      questionError: true, // 에러 상태 활성화
      answerError: true, // 에러 상태 활성화
    },
  ]);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleSave = () => {
    handleClose();
  };

  const handleAddQna = () => {
  };

  const handleQuestionChange = (id: string, question: string) => {
    setQnaPairs(qnaPairs.map(pair => (pair.id === id ? { ...pair, question, questionError: question.trim() === '' } : pair)));
  };

  const handleAnswerChange = (id: string, answer: string) => {
    setQnaPairs(qnaPairs.map(pair => (pair.id === id ? { ...pair, answer, answerError: answer.trim() === '' } : pair)));
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'prompt', label: '프롬프트' }}
        initialSubMenu={{
          id: 'fewshot',
          label: '퓨샷',
          icon: 'ic-lnb-menu-20-fewshot',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              프롬프트 퓨샷
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              퓨샷 생성 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='퓨샷 생성' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupBody>
            {/* 이름 입력 필드 - 에러 상태 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={datasetName} onChange={e => setDatasetName(e.target.value)} placeholder='이름 입력' error='이름을 입력해 주세요.' />
              </UIFormField>
            </UIArticle>

            {/* Q&A Few-shot 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIQnaFewshot
                  qnaPairs={qnaPairs}
                  required={true}
                  showAddButton={true}
                  showDeleteButton={index => index > 0}
                  onAddQna={handleAddQna}
                  onDeleteQna={(id: string) => {
                    setQnaPairs(qnaPairs.filter(pair => pair.id !== id));
                  }}
                  onQuestionChange={handleQuestionChange}
                  onAnswerChange={handleAnswerChange}
                  questionErrorMessage='질문을 입력해 주세요.'
                  answerErrorMessage='답변을 입력해 주세요.'
                />
              </UIFormField>
            </UIArticle>

            {/* 태그 섹션 - 에러 상태 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' required={true} />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
