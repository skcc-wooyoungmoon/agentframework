/**
 * 날짜를 yyyy.MM.dd 형식으로 포맷팅
 */
const formatDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}.${month}.${day}`;
};

/**
 * yyyy.MM.dd 문자열을 Date 객체로 안정적으로 변환
 * 기본 Date.parse는 브라우저별 해석이 다를 수 있어 직접 파싱한다.
 */
const parseDotSeparatedDate = (value?: string): Date | null => {
  if (!value) return null;
  const [yearStr, monthStr, dayStr] = value.split('.');
  const year = Number(yearStr);
  const month = Number(monthStr);
  const day = Number(dayStr);

  if ([year, month, day].some(num => Number.isNaN(num))) {
    return null;
  }

  const parsed = new Date(year, month - 1, day);
  if (parsed.getFullYear() !== year || parsed.getMonth() !== month - 1 || parsed.getDate() !== day) {
    return null;
  }

  return parsed;
};

/**
 * 초기 날짜 범위값 생성 (시작일: 1개월 전, 종료일: 현재 날짜)
 * yyyy.MM.dd 형식의 문자열 반환
 */
export const getInitialDateRange = (): { startDate: string; endDate: string } => {
  const today = new Date();
  const endDate = formatDate(today);

  // 시작일: 1개월 전
  const oneMonthAgo = new Date(today);
  oneMonthAgo.setMonth(today.getMonth() - 1);
  const startDate = formatDate(oneMonthAgo);

  return { startDate, endDate };
};

/**
 * 날짜 범위 유효성 검증 및 상대 날짜 자동 조정
 * 시작일이 종료일보다 크면 종료일을 시작일과 같게 설정
 * 종료일이 시작일보다 작으면 시작일을 종료일과 같게 설정
 */
export const validateAndAdjustDateRange = (
  updatedField: 'startDate' | 'endDate',
  newValue: string,
  currentStartDate: string,
  currentEndDate: string
): { startDate: string; endDate: string } => {
  if (!newValue) {
    // 입력값이 없으면 해당 필드만 업데이트
    return updatedField === 'startDate' ? { startDate: newValue, endDate: currentEndDate } : { startDate: currentStartDate, endDate: newValue };
  }

  const nextDate = parseDotSeparatedDate(newValue);

  if (updatedField === 'startDate') {
    const currentEnd = parseDotSeparatedDate(currentEndDate);

    // 시작일이 종료일보다 크면 종료일을 시작일로 설정
    if (nextDate && currentEnd && nextDate > currentEnd) {
      return { startDate: newValue, endDate: newValue };
    }

    return { startDate: newValue, endDate: currentEndDate };
  } else {
    const currentStart = parseDotSeparatedDate(currentStartDate);

    // 종료일이 시작일보다 작으면 시작일을 종료일로 설정
    if (nextDate && currentStart && nextDate < currentStart) {
      return { startDate: newValue, endDate: newValue };
    }

    return { startDate: currentStartDate, endDate: newValue };
  }
};
