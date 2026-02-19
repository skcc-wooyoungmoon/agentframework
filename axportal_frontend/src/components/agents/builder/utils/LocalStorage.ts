const getData = (key: string): unknown | undefined => {
  try {
    const data = localStorage.getItem(key);

    if (data) {
      return JSON.parse(data);
    }
  } catch (error) {
    // localStorage 접근 또는 JSON 파싱 실패 시 에러 로깅
    console.error('localStorage 데이터 조회 실패:', error);
    return undefined;
  }
};

const setData = (key: string, value: unknown): void => {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    // localStorage 저장 실패 시 에러 로깅 (예: 저장 공간 부족)
    console.error('localStorage 데이터 저장 실패:', error);
  }
};

export { getData, setData };
