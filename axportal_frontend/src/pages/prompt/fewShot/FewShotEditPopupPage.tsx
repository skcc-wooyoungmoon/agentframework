import { useEffect, useRef, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import type { QnaPair } from '@/components/UI/organisms';
import { UILayerPopup, UIPopupAside, UIQnaFewshot } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useUpdateFewShot } from '@/services/prompt/fewshot/fewShotPrompts.services';
import { useModal } from '@/stores/common/modal';

interface FewShotEditPopupPageProps extends LayerPopupProps {
  fewShotUuid: string;
  fewShotName: string;
  items: QnaPair[];
  tags: string[];
  onUpdateSuccess?: () => void; // 업데이트 성공 시 콜백 추가
}

export function FewShotEditPopupPage({ fewShotUuid = '', fewShotName = '', items = [], tags: initialTags = [], currentStep, onClose, onUpdateSuccess }: FewShotEditPopupPageProps) {
  const { openConfirm, openAlert } = useModal();

  const [datasetName, setDatasetName] = useState(fewShotName || '');
  const [tags, setTags] = useState<string[]>(initialTags || []);
  const [qnaPairs, setQnaPairs] = useState<QnaPair[]>(items || []);

  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false);

  // 팝업이 처음 열릴 때만 props를 초기값으로 설정하기 위한 ref
  const prevCurrentStepRef = useRef<number>(0);
  // 저장 성공 후 props 업데이트를 무시하기 위한 플래그
  const shouldIgnorePropsUpdateRef = useRef<boolean>(false);

  // 팝업이 처음 열릴 때만 props를 초기값으로 설정
  useEffect(() => {
    if (currentStep === 1 && prevCurrentStepRef.current !== 1) {
      // 팝업이 열릴 때
      if (fewShotName) {
        setDatasetName(fewShotName);
      }
      if (initialTags.length > 0) {
        setTags(initialTags);
      }
      if (items.length > 0) {
        const updatedQaPairs = items.map(item => ({
          ...item,
          questionError: false,
          answerError: false,
        }));
        setQnaPairs(updatedQaPairs);
      }
      setHasAttemptedSave(false);
      shouldIgnorePropsUpdateRef.current = false;
    }
    prevCurrentStepRef.current = currentStep || 0;
  }, [currentStep, fewShotName, initialTags, items]);

  // items prop이 변경될 때 qnaPairs 업데이트 (팝업이 열려있고 저장 중이 아닐 때만)
  useEffect(() => {
    if (currentStep !== 1) return; // 팝업이 닫혀있으면 무시
    if (shouldIgnorePropsUpdateRef.current) return; // 저장 중이거나 저장 성공 후에는 무시

    const updatedQaPairs = items.map(item => ({
      ...item,
      questionError: hasAttemptedSave ? !item.question || item.question.trim() === '' : !!(item as any).questionError,
      answerError: hasAttemptedSave ? !item.answer || item.answer.trim() === '' : !!(item as any).answerError,
    }));
    setQnaPairs(updatedQaPairs);
  }, [items, hasAttemptedSave, currentStep]);

  // initialTags prop이 변경될 때 tags 업데이트 (팝업이 열려있고 저장 중이 아닐 때만)
  useEffect(() => {
    if (currentStep !== 1) return; // 팝업이 닫혀있으면 무시
    if (shouldIgnorePropsUpdateRef.current) return; // 저장 중이거나 저장 성공 후에는 무시
    setTags(initialTags);
  }, [initialTags, currentStep]);

  // fewShotName prop이 변경될 때 datasetName 업데이트 (팝업이 열려있고 저장 중이 아닐 때만)
  useEffect(() => {
    if (currentStep !== 1) return; // 팝업이 닫혀있으면 무시
    if (shouldIgnorePropsUpdateRef.current) return; // 저장 중이거나 저장 성공 후에는 무시
    // fewShotName이 실제로 값이 있을 때만 업데이트 (데이터 로딩 중 빈 문자열 무시)
    if (fewShotName && fewShotName.trim() !== '') {
      setDatasetName(fewShotName);
    }
  }, [fewShotName, currentStep]);

  /**
   * 닫기 버튼 클릭
   */
  const handleClose = () => {
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        setDatasetName(fewShotName);
        setTags(initialTags);
        setQnaPairs(items);
        setHasAttemptedSave(false);
        handleClose();
      },
      onCancel: () => {},
    });
  };

  const { mutate: updateFewShotMutation, isPending } = useUpdateFewShot({
    onSuccess: () => {
      // 저장 성공 후 props 업데이트를 무시하도록 플래그 유지
      openAlert({
        title: '완료',
        message: '수정사항이 저장되었습니다.',
        onConfirm: () => {
          onUpdateSuccess?.();
          handleClose();
        },
      });
    },
    onError: /* error */ () => {
      // 저장 실패 시 플래그 리셋하여 다시 수정 가능하도록
      shouldIgnorePropsUpdateRef.current = false;
      // console.error('퓨샷 업데이트 실패:', error);
    },
  });

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
   * 변경사항 감지 함수
   */
  const hasChanges = () => {
    // 이름 변경 확인
    if (datasetName.trim() !== fewShotName.trim()) {
      return true;
    }

    // 태그 변경 확인
    if (tags.length !== initialTags.length) {
      return true;
    }
    const tagsChanged = tags.some((tag, index) => tag !== initialTags[index]) || initialTags.some((tag, index) => tag !== tags[index]);
    if (tagsChanged) {
      return true;
    }

    // Q&A 변경 확인
    if (qnaPairs.length !== items.length) {
      return true;
    }
    const qnaChanged = qnaPairs.some((pair, index) => {
      const originalPair = items[index];
      return !originalPair || pair.question !== originalPair.question || pair.answer !== originalPair.answer;
    });
    if (qnaChanged) {
      return true;
    }

    return false;
  };

  const handleSave = async () => {
    // 저장 시도 표시 및 에러 갱신 트리거
    setHasAttemptedSave(true);

    // 변경사항이 없으면 alert만 표시
    if (!hasChanges()) {
      await openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
      });
      return;
    }

    // QnaPair를 FewShotItem으로 변환
    const fewShotItems = qnaPairs.map(pair => ({
      itemQuery: pair.question,
      itemAnswer: pair.answer,
    }));

    // string[]를 FewShotTag[]로 변환
    const fewShotTags = tags.map(tag => ({ tag }));

    // 실제 에러 상태 확인
    const hasErrors = qnaPairs.some(pair => pair.questionError || pair.answerError || !pair.question || !pair.answer);

    // 에러 로그 출력
    if (hasErrors) {
      return;
    }

    if (tags.length === 0) {
      return;
    }

    if (!datasetName.trim()) {
      return;
    } else if (datasetName.length > 50) {
      return;
    }

    // 저장 시작 시점부터 props 업데이트를 무시하도록 플래그 설정
    shouldIgnorePropsUpdateRef.current = true;

    updateFewShotMutation({
      uuid: fewShotUuid,
      items: fewShotItems,
      newName: datasetName,
      release: false,
      tags: fewShotTags,
    });
  };

  const handleAddQna = () => {
    setQnaPairs([
      ...qnaPairs,
      {
        id: (qnaPairs.length + 1).toString(),
        question: '',
        answer: '',
        questionError: true, // 빈 값이므로 에러 상태
        answerError: true, // 빈 값이므로 에러 상태
      },
    ]);
  };

  const handleDeleteQna = (id: string) => {
    // 첫 번째 항목(index 0)은 삭제할 수 없음
    const targetIndex = qnaPairs.findIndex(pair => pair.id === id);
    if (targetIndex === 0) {
      return;
    }
    setQnaPairs(qnaPairs.filter(pair => pair.id !== id));
  };

  const handleQuestionChange = (id: string, question: string) => {
    const questionError = !question || question.trim() === '';

    setQnaPairs(prevPairs => {
      const updatedPairs = prevPairs.map(pair =>
        pair.id === id
          ? {
              ...pair,
              question,
              questionError,
            }
          : pair
      );

      return updatedPairs;
    });
  };

  const handleAnswerChange = (id: string, answer: string) => {
    const answerError = !answer || answer.trim() === '';

    setQnaPairs(prevPairs => {
      const updatedPairs = prevPairs.map(pair =>
        pair.id === id
          ? {
              ...pair,
              answer,
              answerError,
            }
          : pair
      );

      return updatedPairs;
    });
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
            <UIPopupHeader title='퓨샷 수정' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button
                    auth={AUTH_KEY.PROMPT.FEW_SHOT_UPDATE}
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
