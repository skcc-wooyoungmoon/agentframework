/**
 * CSV 다운로드 유틸리티 함수
 */

/**
 * 데이터를 CSV 파일로 다운로드
 * @param data CSV로 변환할 데이터 배열
 * @param filename 파일명 (확장자 포함)
 */
export const downloadCsv = (data: Record<string, any>[], filename: string) => {
  if (!data || data.length === 0) {
    // console.warn('다운로드할 데이터가 없습니다.');
    return;
  }

  // CSV 헤더 생성
  const headers = Object.keys(data[0]);
  
  // CSV 내용 생성
  const csvContent = [
    // 헤더 행
    headers.map(header => `"${header}"`).join(','),
    // 데이터 행들
    ...data.map(row => 
      headers.map(header => {
        const value = row[header];
        // 값이 null이거나 undefined인 경우 빈 문자열로 처리
        if (value === null || value === undefined) {
          return '""';
        }
        // 문자열에 쉼표나 따옴표가 포함된 경우 처리
        const stringValue = String(value);
        if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n')) {
          // 따옴표를 두 개로 이스케이프하고 전체를 따옴표로 감싸기
          return `"${stringValue.replace(/"/g, '""')}"`;
        }
        return `"${stringValue}"`;
      }).join(',')
    )
  ].join('\n');

  // BOM 추가 (Excel에서 한글 깨짐 방지)
  const BOM = '\uFEFF';
  const csvWithBOM = BOM + csvContent;

  // Blob 생성
  const blob = new Blob([csvWithBOM], { type: 'text/csv;charset=utf-8;' });
  
  // 다운로드 링크 생성 및 클릭
  const link = document.createElement('a');
  if (link.download !== undefined) {
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }
};

/**
 * JSON 데이터를 CSV로 변환하여 다운로드
 * @param jsonData JSON 데이터 배열
 * @param filename 파일명
 * @param customHeaders 커스텀 헤더 매핑 (선택사항)
 */
export const downloadJsonAsCsv = (
  jsonData: any[], 
  filename: string, 
  customHeaders?: Record<string, string>
) => {
  if (!jsonData || jsonData.length === 0) {
    // console.warn('다운로드할 JSON 데이터가 없습니다.');
    return;
  }

  // 모든 키를 수집하여 헤더 생성
  const allKeys = new Set<string>();
  jsonData.forEach(item => {
    Object.keys(item).forEach(key => allKeys.add(key));
  });

  const headers = Array.from(allKeys);
  
  // 데이터 변환
  const csvData = jsonData.map(item => {
    const row: Record<string, any> = {};
    headers.forEach(header => {
      let value = item[header];
      
      // 중첩된 객체나 배열인 경우 JSON 문자열로 변환
      if (typeof value === 'object' && value !== null) {
        value = JSON.stringify(value);
      }
      
      // 커스텀 헤더가 있는 경우 적용
      const finalHeader = customHeaders?.[header] || header;
      row[finalHeader] = value;
    });
    return row;
  });

  downloadCsv(csvData, filename);
};
