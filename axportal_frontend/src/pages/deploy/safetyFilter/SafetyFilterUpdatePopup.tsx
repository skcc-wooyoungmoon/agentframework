import React, { useEffect, useRef, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { type SafetyFilterDetail, useUpdateSafetyFilter } from '@/services/deploy/safetyFilter';
import { useModal } from '@/stores/common/modal';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

interface SafetyFilterUpdatePopupProps {
  isOpen: boolean;
  onClose: () => void;
  onSave?: () => void;
  filterGroupId: string;
  safetyFilterData?: SafetyFilterDetail;
}

/**
 * 세이프티 필터 수정 팝업
 */
export const SafetyFilterUpdatePopup: React.FC<SafetyFilterUpdatePopupProps> = ({ isOpen, onClose, onSave, filterGroupId, safetyFilterData }) => {
  const { openAlert } = useModal();
  const { showCancelConfirm, showNoEditContent, showEditComplete } = useCommonPopup();

  // 분류
  const [filterGroupName, setFilterGroupName] = useState('');

  // 금지어 리스트
  const [bannedWords, setBannedWords] = useState<string[]>([]);

  // 포커스할 인덱스
  const [focusIndex, setFocusIndex] = useState<number | null>(null);

  // 금지어 입력 필드 ref 배열
  const bannedWordInputRefs = useRef<(HTMLInputElement | null)[]>([]);

  // 초기 데이터 저장 (변경 여부 확인용)
  const [initialData, setInitialData] = useState<{
    filterGroupName: string;
    bannedWords: string[];
  }>({
    filterGroupName: '',
    bannedWords: [],
  });

  // 수정 API 호출
  const { mutate: updateSafetyFilter } = useUpdateSafetyFilter(filterGroupId, {
    onSuccess: async () => {
      // 캐시 무효화 먼저 실행 (await으로 완료 대기)
      if (onSave) {
        await onSave();
      }

      showEditComplete({
        onConfirm: () => {
          // 캐시 갱신 완료 후 팝업 닫음
          onClose();
        },
      });
    },
  });

  // 팝업이 열릴 때 데이터 초기화
  useEffect(() => {
    if (isOpen && safetyFilterData) {
      // stopWords 배열에서 stopWord 값만 추출
      const stopWordsList = safetyFilterData.stopWords.map(item => item.stopWord);
      setFilterGroupName(safetyFilterData.filterGroupName);
      setBannedWords(stopWordsList);
      // 초기 데이터 저장
      setInitialData({
        filterGroupName: safetyFilterData.filterGroupName,
        bannedWords: stopWordsList,
      });
    }
  }, [isOpen, safetyFilterData]);

  // 포커스 인덱스 변경 시 해당 입력 필드에 포커스
  useEffect(() => {
    if (focusIndex !== null && bannedWordInputRefs.current[focusIndex]) {
      bannedWordInputRefs.current[focusIndex]?.focus();
      setFocusIndex(null); // 포커스 후 초기화
    }
  }, [focusIndex]);

  // 금지어 변경 핸들러
  const handleBannedWordChange = (index: number, value: string) => {
    const newBannedWords = [...bannedWords];
    newBannedWords[index] = value;
    setBannedWords(newBannedWords);
  };

  // 금지어 추가 (금지어 추가 버튼 클릭 시 호출)
  const handleAddBannedWord = () => {
    const newBannedWords = [...bannedWords, '']; // 마지막에 빈 필드 추가
    setBannedWords(newBannedWords);
    setFocusIndex(bannedWords.length); // 새로 추가된 마지막 필드에 포커스 설정
  };

  // 금지어 삭제
  const handleRemoveBannedWord = (index: number) => {
    if (bannedWords.length <= 1) {
      openAlert({
        title: '안내',
        message: '최소 1개 이상의 금지어가 필요합니다.',
        confirmText: '확인',
      });
      return;
    }
    const newBannedWords = bannedWords.filter((_, i) => i !== index);
    setBannedWords(newBannedWords);
  };

  // 취소 버튼 클릭 핸들러
  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  // 저장 버튼 활성화 조건
  const isSaveDisabled = !filterGroupName.trim() || bannedWords.some(word => !word.trim());

  // 저장 버튼 클릭
  const handleSave = () => {
    // 변경 여부 확인
    const hasChanges = filterGroupName !== initialData.filterGroupName || JSON.stringify(bannedWords) !== JSON.stringify(initialData.bannedWords);

    if (!hasChanges) {
      showNoEditContent();
      return;
    }

    // 유효성 검사
    const filledWords = bannedWords.filter(word => word.trim() !== '');
    if (!filterGroupName.trim()) {
      openAlert({
        title: '안내',
        message: '분류를 입력해주세요.',
        confirmText: '확인',
      });
      return;
    }

    if (filledWords.length === 0) {
      openAlert({
        title: '안내',
        message: '최소 1개 이상의 금지어를 입력해주세요.',
        confirmText: '확인',
      });
      return;
    }

    // 수정 데이터 준비
    const updateData = {
      filterGroupName,
      stopWords: filledWords,
    };

    // API 호출
    updateSafetyFilter(updateData);
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='세이프티 필터 수정' position='left' />
          {/* 레이어 팝업 바디 */}
          {/* <UIPopupBody></UIPopupBody> */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={isSaveDisabled} onClick={handleSave}>
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
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='세이프티 필터 수정' description='현재 등록된 금지어를 수정하거나 새로운 단어를 추가할 수 있습니다.' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          {/* 분류 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                분류
              </UITypography>
              <UIInput.Text
                value={filterGroupName}
                placeholder='분류 입력'
                onChange={e => {
                  setFilterGroupName(e.target.value);
                }}
              />
            </UIFormField>
          </UIArticle>

          {/* 금지어 입력 필드 */}
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
  );
};
