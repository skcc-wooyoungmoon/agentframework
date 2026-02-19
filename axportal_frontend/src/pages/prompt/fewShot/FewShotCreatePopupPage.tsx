import { useEffect, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import type { QnaPair } from '@/components/UI/organisms';
import { UILayerPopup, UIPopupAside, UIQnaFewshot } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useCreateFewShot } from '@/services/prompt/fewshot/fewShotPrompts.services';
import { useModal } from '@/stores/common/modal';
import { useNavigate } from 'react-router-dom';
interface FewShotCreatePopupPageProps extends LayerPopupProps {
  projectId?: string;
  onCreateSuccess?: () => void; // 생성 성공 시 콜백 추가
}

export function FewShotCreatePopupPage({ currentStep, onClose, projectId = '' }: FewShotCreatePopupPageProps) {
  const { openAlert, openConfirm } = useModal();
  const navigate = useNavigate();

  const [datasetName, setDatasetName] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false); // save 버튼 클릭 여부

  // 초기 상태는 에러가 아닌 상태로 설정
  const [qnaPairs, setQnaPairs] = useState<QnaPair[]>([
    {
      id: '1',
      question: '',
      answer: '',
      questionError: false, // 초기에는 에러가 아님
      answerError: false, // 초기에는 에러가 아님
    },
  ]);

  // Q&A 에러 상태를 별도 useEffect로 관리
  useEffect(() => {
    if (hasAttemptedSave) {
      setQnaPairs(prevPairs =>
        prevPairs.map(pair => ({
          ...pair,
          questionError: !pair.question || pair.question.trim() === '',
          answerError: !pair.answer || pair.answer.trim() === '',
        }))
      );
    }
  }, [hasAttemptedSave, qnaPairs.map(pair => pair.question + pair.answer).join('|')]);

  const { mutate: createFewShotMutation, isPending } = useCreateFewShot({
    onSuccess: ({ data: { fewShotUuid } }) => {
      openAlert({
        title: '완료',
        message: `퓨샷 생성을 완료하였습니다.`,
        onConfirm: () => {
          // onCreateSuccess?.();
          navigate(`/prompt/fewShot/${fewShotUuid}`);
          handleClose();

        },
      });
    },
    onError: /* error */ () => {
      // console.error('퓨샷 생성 실패:', error);
    },
  });

  /**
   * 닫기 버튼 클릭
   */
  const handleClose = () => {
    // 팝업 닫을 때 상태 초기화
    setDatasetName('');
    setTags([]);
    setQnaPairs([
      {
        id: '1',
        question: '',
        answer: '',
        questionError: false,
        answerError: false,
      },
    ]);
    setHasAttemptedSave(false);
    onClose();
  };

  /**
   * 취소 버튼 클릭
   */
  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => { },
    });
  };

  /**
   * 필수값 검증 함수
   */
  const isFormValid = () => {
    // 이름 검증
    if (!datasetName.trim() || datasetName.length > 50) {
      return false;
    }

    // 태그 검증
    if (tags.length === 0) {
      return false;
    }

    // Q&A 검증 (모든 질문과 답변이 비어있지 않아야 함)
    const hasEmptyQuestions = qnaPairs.some(pair => !pair.question || pair.question.trim() === '');
    const hasEmptyAnswers = qnaPairs.some(pair => !pair.answer || pair.answer.trim() === '');

    if (hasEmptyQuestions || hasEmptyAnswers) {
      return false;
    }

    return true;
  };

  /**
   * 퓨샷 저장
   */
  const handleSave = () => {
    // save 버튼 클릭 표시
    setHasAttemptedSave(true);

    // 유효성 검사
    const isNameEmpty = datasetName.trim() === '';
    const isNameTooLong = datasetName.length > 50;
    const isTagsEmpty = tags.length === 0;
    const hasEmptyQuestions = qnaPairs.some(pair => !pair.question || pair.question.trim() === '');
    const hasEmptyAnswers = qnaPairs.some(pair => !pair.answer || pair.answer.trim() === '');

    // 에러가 있으면 저장하지 않음
    if (isNameEmpty || isNameTooLong || isTagsEmpty || hasEmptyQuestions || hasEmptyAnswers) {
      return;
    }

    // QnaPair를 FewShotItem으로 변환
    const fewShotItems = qnaPairs.map(pair => ({
      itemQuery: pair.question,
      itemAnswer: pair.answer,
    }));

    // string[]를 FewShotTag[]로 변환
    const fewShotTags = tags.map(tag => ({ tag }));

    createFewShotMutation({
      items: fewShotItems,
      name: datasetName,
      release: false,
      tags: fewShotTags,
      projectId: projectId,
    });
  };

  /**
   * Q&A 추가
   */
  const handleAddQna = () => {
    setQnaPairs([
      ...qnaPairs,
      {
        id: (qnaPairs.length + 1).toString(),
        question: '',
        answer: '',
        questionError: false, // 초기에는 에러가 아님
        answerError: false, // 초기에는 에러가 아님
      },
    ]);
  };

  /**
   * Q&A 질문 변경
   * @param id 변경할 Q&A의 id
   * @param question 변경할 Q&A 질문
   */
  const handleQuestionChange = (id: string, question: string) => {
    setQnaPairs(prevPairs => {
      const updatedPairs = prevPairs.map(pair =>
        pair.id === id
          ? {
            ...pair,
            question,
          }
          : pair
      );

      return updatedPairs;
    });
  };

  /**
   * Q&A 답변 변경
   * @param id 변경할 Q&A의 id
   * @param answer 변경할 Q&A 답변
   */
  const handleAnswerChange = (id: string, answer: string) => {
    setQnaPairs(prevPairs => {
      const updatedPairs = prevPairs.map(pair =>
        pair.id === id
          ? {
            ...pair,
            answer,
          }
          : pair
      );

      return updatedPairs;
    });
  };

  /**
   * Q&A 삭제
   * @param id 삭제할 Q&A의 id
   */
  const handleDeleteQna = (id: string) => {
    const index = qnaPairs.findIndex(pair => pair.id === id);
    // 인덱스가 0이 아닌 경우에만 삭제 허용
    if (index > 0) {
      setQnaPairs(qnaPairs.filter(pair => pair.id !== id));
    }
  };

  return (
    <>
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='퓨샷 등록' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button
                    auth={AUTH_KEY.PROMPT.FEW_SHOT_CREATE}
                    className='btn-tertiary-blue'
                    style={{ width: '80px' }}
                    onClick={handleSave}
                    disabled={isPending || !isFormValid()}
                  >
                    저장
                  </Button>
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
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={datasetName} onChange={e => setDatasetName(e.target.value)} placeholder='이름 입력' maxLength={50} />
              </UIFormField>
            </UIArticle>

            {/* Q&A Few-shot 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIQnaFewshot
                  qnaPairs={qnaPairs}
                  label='Q&A'
                  required={true}
                  showAddButton={true}
                  showDeleteButton={index => index > 0} // index 0인 첫 번째 항목은 삭제 버튼 숨김
                  onAddQna={handleAddQna}
                  onDeleteQna={handleDeleteQna}
                  onQuestionChange={handleQuestionChange}
                  onAnswerChange={handleAnswerChange}
                  questionErrorMessage='질문을 입력해 주세요.'
                  answerErrorMessage='답변을 입력해 주세요.'
                  className='w-full'
                />
              </UIFormField>
            </UIArticle>

            {/* 태그 섹션 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={newTags => setTags(newTags.slice(0, 7))} label='태그' required={true} />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
}
