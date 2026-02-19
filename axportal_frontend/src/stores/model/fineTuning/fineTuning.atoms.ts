import { atom } from 'jotai';
import type { UIStepperItem } from '@/components/UI/molecules';
import type { FineTuningTraining } from '@/services/model/fineTuning/types';
import type { ModelCtlgType } from '@/services/model/ctlg/types';
import type { GetDatasetsResponse } from '@/services/data/types.ts';

// 파인튜닝 상태 타입 정의
export type FineTuningStatus =
  | 'initialized' // 초기화
  | 'starting' // 시작중
  | 'stopping' // 중지중
  | 'stopped' // 중지
  | 'resource_allocating' // 할당중
  | 'resource_allocated' // 할당완료
  | 'training' // 학습중
  | 'trained' // 학습완료
  | 'error'; // 실패

// 상태값 상수 정의
export const FINE_TUNING_STATUS = {
  INITIALIZED: 'initialized' as const,
  STARTING: 'starting' as const,
  STOPPING: 'stopping' as const,
  STOPPED: 'stopped' as const,
  RESOURCE_ALLOCATION: 'resource-allocation' as const,
  RESOURCE_ALLOCATED: 'resource-allocated' as const,
  TRAINING: 'training' as const,
  TRAINED: 'trained' as const,
  COMPLETED: 'completed' as const,
  ERROR: 'error' as const,
} as const;

// 상태값 한글 표시명 매핑
export const FINE_TUNING_STATUS_LABELS: Record<FineTuningStatus, string> = {
  initialized: '초기화',
  starting: '시작중',
  stopping: '중지중',
  stopped: '중지됨',
  resource_allocating: '할당중',
  resource_allocated: '할당됨',
  training: '학습중',
  trained: '학습완료',
  error: '오류',
};

// 파인튜닝 목록 데이터
export const fineTuningListAtom = atom<FineTuningTraining[]>([]);

// 로딩 상태
export const fineTuningLoadingAtom = atom<boolean>(false);

// 에러 상태
export const fineTuningErrorAtom = atom<string | null>(null);

// 네트워크 에러 상태
export const fineTuningNetworkErrorAtom = atom<boolean>(false);

// 검색 조건
export const fineTuningSearchAtom = atom<{
  name: boolean;
  status: boolean;
  tuningType: boolean;
  modelType: boolean;
}>({
  name: false,
  status: false,
  tuningType: false,
  modelType: false,
});

// 검색 값들 (기존 - 호환성을 위해 유지)
export const fineTuningSearchValuesAtom = atom<{
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  tuningType: string;
  modelType: string;
}>({
  dateType: '생성일시',
  dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
  searchType: '이름',
  searchKeyword: '',
  tuningType: '전체',
  modelType: '전체',
});

// 개별 검색 필터 atoms
export const fineTuningDateTypeAtom = atom<string>('생성일시');
export const fineTuningFromDateAtom = atom<string>('2025.01.01');
// 오늘 날짜를 가져오는 함수
const getTodayString = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  const day = String(today.getDate()).padStart(2, '0');
  return `${year}.${month}.${day}`;
};

export const fineTuningToDateAtom = atom<string>(getTodayString());
export const fineTuningSearchTypeAtom = atom<string>('이름');
export const fineTuningSearchKeywordAtom = atom<string>('');
export const fineTuningStatusAtom = atom<string>('전체');

// 선택된 항목들
export const fineTuningSelectedIdsAtom = atom<string[]>([]);

// 페이지네이션
export const fineTuningPaginationAtom = atom<{
  currentPage: number;
  totalPages: number;
  pageSize: number;
}>({
  currentPage: 1,
  totalPages: 1,
  pageSize: 12,
});

// 뷰 타입 (grid/card)
export const fineTuningViewTypeAtom = atom<'grid' | 'card'>('grid');

// 파인튜닝 상세 데이터
export const fineTuningDetailAtom = atom<FineTuningTraining | null>(null);

// 파인튜닝 상세 로딩 상태
export const fineTuningDetailLoadingAtom = atom<boolean>(false);

// 파인튜닝 상세 에러 상태
export const fineTuningDetailErrorAtom = atom<string | null>(null);

// 현재 선택된 파인튜닝 상태
export const currentFineTuningStatusAtom = atom<FineTuningStatus | null>(null);

// 상태값 변경 이력
export const fineTuningStatusHistoryAtom = atom<
  Array<{
    status: FineTuningStatus;
    timestamp: string;
    message?: string;
  }>
>([]);

// 상태값 필터링
export const fineTuningStatusFilterAtom = atom<FineTuningStatus[]>([]);

// 상태값 통계
export const fineTuningStatusStatsAtom = atom<Record<FineTuningStatus, number>>({
  initialized: 0,
  starting: 0,
  stopping: 0,
  stopped: 0,
  resource_allocating: 0,
  resource_allocated: 0,
  training: 0,
  trained: 0,
  error: 0,
});

// 선택된 파인튜닝 데이터
export const selectedFineTuningAtom = atom<FineTuningTraining | null>(null);

// 파인튜닝 생성 위저드 상태
export const fineTuningWizardCurrentStepAtom = atom<number>(1);
export const fineTuningWizardIsOpenAtom = atom<boolean>(false);

// Step 1: 모델 선택
export const fineTuningSelectedModelIdAtom = atom<string>('');
export const fineTuningSelectedModelAtom = atom<ModelCtlgType | null>(null); // 선택된 모델의 전체 정보
export const fineTuningModelSearchTextAtom = atom<string>('');
export const fineTuningModelSearchFilterAtom = atom<string>('모델명');
export const fineTuningModelCurrentPageAtom = atom<number>(1);

// Step 2: 기본 정보 입력
export const fineTuningNameAtom = atom<string>('');
export const fineTuningDescriptionAtom = atom<string>('');
export const fineTuningLearningTypeAtom = atom<string>('supervised');
export const fineTuningPftTypeAtom = atom<string>('lora');
export const fineTuningAdjustmentTechAtom = atom<string>('basic');

// Step 3: 자원 할당
export const fineTuningCpuValueAtom = atom<number>(0);
export const fineTuningMemoryValueAtom = atom<number>(0);
export const fineTuningGpuValueAtom = atom<number>(0);
export const fineTuningScalingGroupAtom = atom<string>('none');

// Step 4: 데이터세트 선택
export const fineTuningSelectedDatasetIdsAtom = atom<any[]>([]);
export const fineTuningDatasetSearchTextAtom = atom<string>('');
export const fineTuningDatasetSearchFilterAtom = atom<string>('이름');
export const fineTuningDatasetCurrentPageAtom = atom<number>(1);

// Step 5: 파라미터 설정
export const fineTuningLearningEpochsAtom = atom<string>('1');
export const fineTuningValidationRatioAtom = atom<number>(0.2);
export const fineTuningValidationRatioTextAtom = atom<string>('0.2');
export const fineTuningLearningRateAtom = atom<string>('0.0001');
export const fineTuningBatchSizeAtom = atom<string>('1');
export const fineTuningEarlyStopAtom = atom<boolean>(true);
export const fineTuningPatienceAtom = atom<string>('3');

// Step 6: 입력정보 확인 (모든 데이터를 종합)
export const fineTuningFinalDataAtom = atom(get => ({
  selectedModelId: get(fineTuningSelectedModelIdAtom),
  name: get(fineTuningNameAtom),
  description: get(fineTuningDescriptionAtom),
  learningType: get(fineTuningLearningTypeAtom),
  pftType: get(fineTuningPftTypeAtom),
  adjustmentTech: get(fineTuningAdjustmentTechAtom),
  cpuValue: get(fineTuningCpuValueAtom),
  memoryValue: get(fineTuningMemoryValueAtom),
  gpuValue: get(fineTuningGpuValueAtom),
  selectedDatasetIds: get(fineTuningSelectedDatasetIdsAtom),
  learningEpochs: get(fineTuningLearningEpochsAtom),
  validationRatio: get(fineTuningValidationRatioAtom),
  learningRate: get(fineTuningLearningRateAtom),
  batchSize: get(fineTuningBatchSizeAtom),
  earlyStop: get(fineTuningEarlyStopAtom),
  patience: get(fineTuningPatienceAtom),
}));

// 모든 파인튜닝 상태 초기화 함수
export const resetAllFineTuningDataAtom = atom(null, (_, set) => {
  // Step 1: 모델 선택 초기화
  set(fineTuningSelectedModelIdAtom, '');
  set(fineTuningSelectedModelAtom, null);
  set(fineTuningModelSearchTextAtom, '');
  set(fineTuningModelSearchFilterAtom, '모델명');
  set(fineTuningModelCurrentPageAtom, 1);

  // Step 2: 기본 정보 입력 초기화
  set(fineTuningNameAtom, '');
  set(fineTuningDescriptionAtom, '');
  set(fineTuningLearningTypeAtom, 'supervised');
  set(fineTuningPftTypeAtom, 'lora');
  set(fineTuningAdjustmentTechAtom, 'basic');

  // Step 3: 자원 할당 초기화
  set(fineTuningCpuValueAtom, 0);
  set(fineTuningMemoryValueAtom, 0);
  set(fineTuningGpuValueAtom, 0);
  set(fineTuningScalingGroupAtom, 'none');

  // Step 4: 데이터세트 선택 초기화
  set(fineTuningSelectedDatasetIdsAtom, []);
  set(fineTuningDatasetSearchTextAtom, '');
  set(fineTuningDatasetSearchFilterAtom, '이름');
  set(fineTuningDatasetCurrentPageAtom, 1);

  // Step 5: 파라미터 설정 초기화
  set(fineTuningLearningEpochsAtom, '1');
  set(fineTuningValidationRatioAtom, 0.2);
  set(fineTuningValidationRatioTextAtom, '0.2');
  set(fineTuningLearningRateAtom, '0.0001');
  set(fineTuningBatchSizeAtom, '1');
  set(fineTuningEarlyStopAtom, true);
  set(fineTuningPatienceAtom, '3');

  // 위저드 상태 초기화
  set(fineTuningWizardCurrentStepAtom, 1);
  set(fineTuningWizardIsOpenAtom, false);
});

// 스테퍼 아이템 관리
export const fineTuningStepperItemsAtom = atom<UIStepperItem[]>([
  {
    id: 'step1',
    label: '모델 선택',
    step: 1,
  },
  {
    id: 'step2',
    label: '기본 정보 입력',
    step: 2,
  },
  {
    id: 'step3',
    label: '자원 할당',
    step: 3,
  },
  {
    id: 'step4',
    label: '데이터세트 선택',
    step: 4,
  },
  {
    id: 'step5',
    label: '파라미터 설정',
    step: 5,
  },
  {
    id: 'step6',
    label: '입력정보 확인',
    step: 6,
  },
]);

export const datasetSelectPopupAtom = atom<GetDatasetsResponse[]>([]);

// 파인튜닝 선택 팝업 상태
export const fineTuningSelectPopupAtom = atom<string[]>([]);
