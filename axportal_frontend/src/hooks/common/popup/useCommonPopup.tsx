import { useCallback } from 'react';

import { useModal } from '@/stores/common/modal/useModal';

/**
 * 공통 팝업을 관리하는 커스텀 훅
 *
 * @description
 * 다양한 공통 팝업(취소 확인, 수정 완료, 삭제 확인, 완료/실패 알림 등)을 관리하는 훅입니다.
 *
 * @example
 * ```tsx
 * const { showCancelConfirm, showEditComplete, showDeleteConfirm } = useCommonPopup();
 * ```
 */
export const useCommonPopup = () => {
  const { openConfirm, openAlert } = useModal();

  /**
   * 취소 확인 팝업을 표시하는 함수
   *
   * @description
   * 등록, 수정 데이터가 담긴 팝업에서 취소 버튼 클릭 시 노출되는 취소 확인 팝업
   * - "아니요" 버튼 클릭 시: 팝업만 닫히고 화면 상태 유지
   * - "예" 버튼 클릭 시: onConfirmCancel 콜백 실행 (데이터 롤백 및 화면 닫기)
   */
  const showCancelConfirm = useCallback(
    (options?: { onConfirm?: () => void; onCancel?: () => void }) => {
      openConfirm({
        title: '안내',
        message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          // "예" 버튼 클릭 시: 컨펌 닫히며 등록 또는 수정 없이 데이터 롤백 날림 처리
          options?.onConfirm?.();
        },
        onCancel: () => {
          options?.onCancel?.();
        },
      });
    },
    [openConfirm]
  );

  /**
   * 수정 완료 얼럿을 표시하는 함수
   *
   * @description
   * 수정 레이어 팝업에서 수정이 완료된 후 저장 버튼 클릭 시 노출되는 완료 얼럿
   * - 확인 버튼 클릭 시: 얼럿과 수정 팝업이 닫히며 바닥 화면 새로고침 후 노출
   */
  const showEditComplete = useCallback(
    (options?: { onConfirm?: () => void }) => {
      openAlert({
        title: '완료',
        message: '수정사항이 저장되었습니다.',
        confirmText: '확인',
        onConfirm: () => {
          options?.onConfirm?.();
        },
      });
    },
    [openAlert]
  );

  /**
   * 수정 내용 없음 얼럿을 표시하는 함수
   *
   * @description
   * 수정 레이어 팝업에서 수정된 내용이 아예 없는 상태로 저장 버튼 클릭 시 노출
   * - 확인 버튼 클릭 시: 얼럿 닫히며 수정 팝업 유지
   */
  const showNoEditContent = useCallback(
    (options?: { onConfirm?: () => void }) => {
      openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
        confirmText: '확인',
        onConfirm: () => {
          options?.onConfirm?.();
        },
      });
    },
    [openAlert]
  );

  /**
   * 삭제 항목 선택 안내 얼럿을 표시하는 함수
   *
   * @description
   * 리스트 그리드의 체크박스에서 아무것도 선택하지 않은 상태로 삭제 버튼 클릭 시 노출
   * - 확인 버튼 클릭 시: 얼럿 닫히며 바닥 화면 유지
   */
  const showDeleteItemNotSelected = useCallback(
    (options?: { onConfirm?: () => void }) => {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해 주세요.',
        confirmText: '확인',
        onConfirm: () => {
          options?.onConfirm?.();
        },
      });
    },
    [openAlert]
  );

  /**
   * 삭제 확인 컨펌을 표시하는 함수
   *
   * @description
   * 상세 정보 화면에서 삭제 버튼 클릭 시 또는 리스트 화면 그리드에서 체크박스 선택 후 삭제 버튼 클릭 시 노출
   * - "아니요" 버튼 클릭 시: 컨펌 닫히며 바닥 화면 유지
   * - "예" 버튼 클릭 시: 컨펌 닫히며 삭제 처리 시도
   */
  const showDeleteConfirm = useCallback(
    (options?: { onConfirm?: () => void; onCancel?: () => void }) => {
      openConfirm({
        title: '안내',
        message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          // "예" 버튼 클릭 시: 컨펌 닫히며 삭제 처리 시도
          options?.onConfirm?.();
        },
        onCancel: () => {
          // "아니요" 버튼 클릭 시: 컨펌 닫히며 바닥 화면 유지
          options?.onCancel?.();
        },
      });
    },
    [openConfirm]
  );

  /**
   * 삭제 완료 얼럿을 표시하는 함수
   *
   * @description
   * 삭제 확인 컨펌에서 삭제 버튼 클릭 시 삭제가 성공적으로 처리된 경우 노출
   * - 삭제 대상 이름과 건수를 동적으로 표시
   * - 확인 버튼 클릭 시: 얼럿 닫히며 바닥 화면 표시 (상세 화면에서 삭제 시 리스트 화면으로 이동, 리스트 화면에서 삭제 시 리스트 새로고침)
   */
  const showDeleteComplete = useCallback(
    (options: {
      itemName: string; // 예: '데이터세트', '지식', '모델' 등
      onConfirm?: () => void;
    }) => {
      openAlert({
        title: '완료',
        message: `${options.itemName} 삭제되었습니다.`,
        confirmText: '확인',
        onConfirm: () => {
          options?.onConfirm?.();
        },
      });
    },
    [openAlert]
  );

  /**
   * 완료 얼럿을 표시하는 함수
   *
   * @description
   * '추가', '등록', '생성' 등 스텝 팝업 내 해당 작업이 정상적으로 처리될 시 노출
   * 예) 데이터세트 만들기, 모델추가, 파인튜닝만들기, 모델배포하기 등
   * - 확인 버튼 클릭 시: 해당 얼럿과 스텝 팝업 닫히며 바닥 화면 새로고침 후 노출
   */
  const showComplete = useCallback(
    (options: {
      itemName: string; // 예: '데이터세트를', '지식을', '모델을' 등
      onConfirm?: () => void;
    }) => {
      openAlert({
        title: '완료',
        message: `${options.itemName} 완료하였습니다.`,
        confirmText: '확인',
        onConfirm: () => {
          options?.onConfirm?.();
        },
      });
    },
    [openAlert]
  );

  /**
   * 실패 얼럿을 표시하는 함수
   *
   * @description
   * '추가', '등록', '생성' 등 스텝 팝업 내 해당 작업이 정상 처리되지 않은 경우 노출
   * 예) 데이터세트 만들기, 모델 추가, 파인튜닝 만들기, 모델 배포하기 등
   * - 확인 버튼 클릭 시: 해당 얼럿 닫힘 처리
   */
  const showFailure = useCallback(
    (options?: { itemName: string; onConfirm?: () => void }) => {
      openAlert({
        title: '실패',
        message: `${options?.itemName}에 실패하였습니다.`,
        confirmText: '확인',
        onConfirm: () => {
          options?.onConfirm?.();
        },
      });
    },
    [openAlert]
  );

  /**
   * 작업 완료 (부분 성공/실패) 얼럿을 표시하는 함수
   *
   * @description
   * 작업이 완료되었지만 일부는 성공하고 일부는 실패한 경우 노출
   * - 제목: "안내"
   * - 메시지: "{작업명}이 완료되었습니다.\n{n건 성공, n건 실패}\n실패한 항목은 확인 후 다시 시도해주세요."
   * - 확인 버튼 클릭 시: 해당 얼럿 닫힘 처리
   */
  const showTaskPartialComplete = useCallback(
    (options: {
      taskName: string; // 예: '삭제', '추가', '등록' 등
      successCount: number;
      failureCount: number;
      onConfirm?: () => void;
    }) => {
      openAlert({
        title: '안내',
        message: `${options.taskName} 완료되었습니다.\n${options.successCount}건 성공, ${options.failureCount}건 실패\n실패한 항목은 확인 후 다시 시도해주세요.`,
        confirmText: '확인',
        onConfirm: () => {
          options?.onConfirm?.();
        },
      });
    },
    [openAlert]
  );

  return {
    showCancelConfirm,
    showEditComplete,
    showNoEditContent,
    showDeleteItemNotSelected,
    showDeleteConfirm,
    showDeleteComplete,
    showComplete,
    showFailure,
    showTaskPartialComplete,
  };
};
