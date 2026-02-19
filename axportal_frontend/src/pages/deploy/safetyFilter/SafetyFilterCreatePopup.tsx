import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import {
  UIArticle,
  UIFormField,
  UIGroup,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCreateSafetyFilter } from '@/services/deploy/safetyFilter';
import { useQueryClient } from '@tanstack/react-query';
import { useModal } from '@/stores/common/modal';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

interface SafetyFilterCreateProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

/**
 * 배포 > 세이프티 필터 > 세이프티 필터 생성 팝업
 */
export const SafetyFilterCreatePopup: React.FC<SafetyFilterCreateProps> = ({ isOpen, onClose }) => {
  const navigate = useNavigate();
  const { openAlert } = useModal();
  const { showCancelConfirm } = useCommonPopup();
  const queryClient = useQueryClient();

  const [selectedCategory, setSelectedCategory] = useState('');
  const [bannedWords, setBannedWords] = useState<string[]>(['']); // 금지어 배열 (최초 1개)
  const [focusIndex, setFocusIndex] = useState<number | null>(null); // 포커스할 인덱스
  const bannedWordInputRefs = useRef<(HTMLInputElement | null)[]>([]); // 금지어 입력 필드 ref 배열

  // 세이프티 필터 생성 API
  const { mutate: createFilter } = useCreateSafetyFilter({
    onSuccess: async ({ data: { filterGroupId } }) => {
      // 목록 캐시 무효화
      await queryClient.invalidateQueries({ queryKey: ['GET', '/safety-filter'] });

      openAlert({
        title: '완료',
        message: '세이프티 필터 생성을 완료하였습니다.',
        onConfirm: () => {
          // 폼 초기화
          setSelectedCategory('');
          setBannedWords(['']);
          onClose();
          navigate(`/deploy/safetyFilter/${filterGroupId}`);
        },
      });
    },
  });

  // 팝업이 열릴 때 폼 초기화
  useEffect(() => {
    if (isOpen) {
      setSelectedCategory('');
      setBannedWords(['']);
    }
  }, [isOpen]);

  // 포커스 인덱스 변경 시 해당 입력 필드에 포커스
  useEffect(() => {
    if (focusIndex !== null && focusIndex >= 0 && focusIndex < bannedWordInputRefs.current.length && bannedWordInputRefs.current[focusIndex]) {
      bannedWordInputRefs.current[focusIndex]?.focus();
      setFocusIndex(null); // 포커스 후 초기화
    }
  }, [focusIndex]);

  // 금지어 추가 (금지어 추가 버튼 클릭 시 호출)
  const handleAddBannedWord = () => {
    const newBannedWords = [...bannedWords, '']; // 마지막에 새로운 빈 입력 필드 추가
    setBannedWords(newBannedWords);
    setFocusIndex(bannedWords.length); // 새로 추가된 마지막 필드에 포커스 설정
  };

  // 금지어 삭제 (삭제 버튼 클릭 시 호출)
  const handleRemoveBannedWord = (index: number) => {
    // 마지막 하나 남은 항목은 삭제하지 않고 빈 값으로 초기화
    if (bannedWords.length === 1) {
      setBannedWords(['']);
      return;
    }

    const newBannedWords = bannedWords.filter((_, i) => i !== index);
    setBannedWords(newBannedWords);
  };

  // 금지어 값 변경
  const handleBannedWordChange = (index: number, value: string) => {
    // 인덱스 유효성 검증
    if (index < 0 || index >= bannedWords.length) {
      return;
    }

    const newBannedWords = [...bannedWords];
    newBannedWords[index] = value;
    setBannedWords(newBannedWords);
  };

  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        // 상태 초기화
        setSelectedCategory('');
        setBannedWords(['']);
        onClose();
      },
    });
  };

  // 만들기 버튼 핸들러
  const handleCreate = () => {
    // 금지어 배열 필터링 (빈 값 제거)
    const filteredWords = bannedWords.filter(word => word && word.trim() !== '');

    // 최종 검증: 필터링 후에도 금지어가 없으면 생성 불가
    if (!selectedCategory.trim() || filteredWords.length === 0) {
      return;
    }

    createFilter({
      filterGroupName: selectedCategory,
      stopWords: filteredWords,
    });
  };

  // 저장 버튼 활성화 조건
  // 1. 카테고리가 입력되어야 함
  // 2. 모든 금지어 필드가 입력되어야 함 (빈 필드가 하나라도 있으면 비활성화)
  const allBannedWordsValid = bannedWords.length > 0 && bannedWords.every(word => word && word.trim() !== '');
  const isSaveDisabled = !selectedCategory.trim() || !allBannedWordsValid;

  return (
    <>
      {/* DesignLayout 기본 구조 */}

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='세이프티 필터 생성' position='left' />
            {/* 레이어 팝업 바디 */}
            {/* <UIPopupBody></UIPopupBody> */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={isSaveDisabled} onClick={handleCreate}>
                    만들기
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='세이프티 필터 생성' description='분류와 금지어를 입력해 새로운 규칙을 만들어보세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  분류
                </UITypography>
                <UIInput.Text value={selectedCategory} onChange={e => setSelectedCategory(e.target.value)} placeholder='분류 입력' style={{ width: '100%' }} maxLength={50} />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                  금지어
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  차단할 키워드를 입력해주세요. 띄어쓰기나 동사의 기본형도 포함할 수 있습니다.
                </UITypography>
                <UIGroup gap={8} direction='column' align='start'>
                  {/* 금지어 입력 필드들 */}
                  {bannedWords.map((word, index) => (
                    <div key={index} style={{ width: '100%' }}>
                      <UIUnitGroup gap={8} direction='row' align='start'>
                        <div className='flex-1'>
                          <UIInput.Text
                            ref={el => {
                              bannedWordInputRefs.current[index] = el as HTMLInputElement;
                            }}
                            value={word}
                            maxLength={255}
                            onChange={e => handleBannedWordChange(index, e.target.value)}
                            onKeyDown={e => {
                              // Enter 키 입력 시 금지어 추가
                              if (e.key === 'Enter') {
                                e.preventDefault();
                                // 한글 조합 중에는 실행하지 않음 (조합 완료 후에만 처리)
                                if (e.nativeEvent.isComposing) {
                                  return;
                                }
                                handleAddBannedWord();
                              }
                            }}
                            placeholder='금지어 입력'
                          />
                        </div>
                        <UIButton2 className='ic-system-48-delete cursor-pointer' onClick={() => handleRemoveBannedWord(index)}>
                          {''}
                        </UIButton2>
                      </UIUnitGroup>
                    </div>
                  ))}

                  {/* 금지어 추가 버튼 */}
                  <div>
                    <UIButton2 className='btn-secondary-outline-blue' onClick={handleAddBannedWord}>
                      금지어 추가
                    </UIButton2>
                  </div>
                </UIGroup>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
