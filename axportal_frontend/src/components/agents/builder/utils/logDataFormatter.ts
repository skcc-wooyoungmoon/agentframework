/**
 * 로그 데이터 포맷팅 유틸리티
 * 노드의 innerData.logData를 { time, log } 형태로 정규화
 */

export interface FormattedLogData {
  time: string;
  log: string;
}

/**
 * 로그 데이터를 { time, log, type } 형태로 포맷팅
 * @param logData - 원본 로그 데이터 (다양한 형태 가능)
 * @returns 정규화된 로그 데이터 배열
 */
export const formatLogData = (logData: any[] | undefined | null): Array<{ time: string; log: string; type?: string }> => {
  if (!logData || !Array.isArray(logData)) {
    return [];
  }

  return logData.map(item => {
    // 이미 { time, log } 형태면 그대로 사용
    if (typeof item === 'object' && item !== null && 'log' in item) {
      let logString = '';
      
      // log 필드가 문자열이면 그대로 사용
      if (typeof item.log === 'string') {
        logString = item.log;
      } 
      // log 필드가 객체면 JSON.stringify로 변환
      else if (typeof item.log === 'object' && item.log !== null) {
        try {
          logString = JSON.stringify(item.log, null, 2);
        } catch (e) {
          logString = String(item.log);
        }
      } 
      // 그 외의 경우 String으로 변환
      else {
        logString = String(item.log || '');
      }
      
      return {
        time: item.time || new Date().toISOString(),
        log: logString,
        type: item.type // type 필드도 보존
      };
    }
    
    // 문자열이면 { time, log } 형태로 변환
    if (typeof item === 'string') {
      return {
        time: new Date().toISOString(),
        log: item
      };
    }
    
    // 객체면 JSON.stringify로 변환
    if (typeof item === 'object' && item !== null) {
      try {
        return {
          time: item.time || new Date().toISOString(),
          log: JSON.stringify(item, null, 2),
          type: item.type
        };
      } catch (e) {
        return {
          time: new Date().toISOString(),
          log: String(item)
        };
      }
    }
    
    // 그 외의 경우
    return {
      time: new Date().toISOString(),
      log: String(item)
    };
  });
};

