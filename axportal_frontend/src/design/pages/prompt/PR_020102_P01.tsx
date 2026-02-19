import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupHeader, UIPopupBody, UIPopupFooter, UIUnitGroup, UIFormField, UIInput } from '@/components/UI/molecules';
import { UILayerPopup, UIQnaFewshot, UIPopupAside } from '@/components/UI/organisms';
import type { QnaPair } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const PR_020102_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const [datasetName, setDatasetName] = useState('AI Chatbot');
  const [tags, setTags] = useState<string[]>(['supervised Fintuning', 'Test']);
  const [qnaPairs, setQnaPairs] = useState<QnaPair[]>([
    {
      id: '1',
      question: '프랑스의 수도는 어디인가요?',
      answer:
        '답변프랑스의 수도는 **파리(Paris)**입니다. 프랑스 정치, 경제, 문화의 중심지이자 세계적인 관광 도시로도 유명하죠.\n에펠탑, 루브르 박물관, 샹젤리제 거리 등이 있는 도시입니다.\n에펠탑, 루브르 박물관, 샹젤리제 거리 등이 있는 도시입니다. 입력',
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

  const handleAddQna = () => {};

  const handleQuestionChange = (id: string, question: string) => {
    setQnaPairs(qnaPairs.map(pair => (pair.id === id ? { ...pair, question } : pair)));
  };

  const handleAnswerChange = (id: string, answer: string) => {
    setQnaPairs(qnaPairs.map(pair => (pair.id === id ? { ...pair, answer } : pair)));
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'data', label: '데이터' }}
        initialSubMenu={{
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-data-storage',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              데이터 도구
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              Ingestion Tool 만들기 진행 중...
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
            <UIPopupHeader title='퓨샷 수정' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 추가 컨텐츠가 필요한 경우 여기에 */}</UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={false}>
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
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={datasetName} placeholder='이름 입력' onChange={e => setDatasetName(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* Q&A Few-shot 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIQnaFewshot
                  label='Q&A' // [251104_퍼블수정] : label='Q&A' 수정 /
                  qnaPairs={qnaPairs}
                  required={true} // [251104_퍼블수정] : required={true} 로 변경
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
            {/* 태그 섹션 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
