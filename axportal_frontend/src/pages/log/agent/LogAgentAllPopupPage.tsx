import { Button } from '@/components/common/auth';
import { UILabel, UITypography } from '@/components/UI';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIArticle, UIDataList, UIPopupBody, UIUnitGroup } from '@/components/UI/molecules';
import { UIAlarm, UIAlarmGroup, UITabs } from '@/components/UI/organisms';
import type { LogPopupData } from '@/services/log/agent/types';
import { useMemo, useState } from 'react';

interface LogAgentAllPopupPageProps {
  selectedLog: LogPopupData | null;
  onClose: () => void;
}

// Python 딕셔너리 문자열을 포맷팅하는 함수 (원문 형식 유지)
const formatPythonDict = (str: string): string => {
  if (!str || str.trim() === '') {
    return str;
  }

  try {
    let result = '';
    let indent = 0;
    const indentSize = 2;
    let i = 0;
    const len = str.length;
    let inString = false;
    let stringChar = '';

    while (i < len) {
      const char = str[i];
      const prevChar = i > 0 ? str[i - 1] : '';
      const nextChar = i < len - 1 ? str[i + 1] : '';

      // 문자열 내부 처리
      if (!inString && (char === "'" || char === '"')) {
        inString = true;
        stringChar = char;
        result += char;
        i++;
        continue;
      }

      if (inString) {
        // 이스케이프된 따옴표 처리
        if (char === '\\' && nextChar === stringChar) {
          result += char + nextChar;
          i += 2;
          continue;
        }
        // 문자열 종료
        if (char === stringChar) {
          inString = false;
          stringChar = '';
          result += char;
          i++;
          continue;
        }
        result += char;
        i++;
        continue;
      }

      // 들여쓰기 적용
      if (char === '{' || char === '[') {
        result += char;
        indent++;
        i++;
        if (i < len) {
          const nextNonSpace = str.slice(i).match(/^\s*([^\s])/);
          if (nextNonSpace && nextNonSpace[1] !== '}' && nextNonSpace[1] !== ']') {
            result += '\n' + ' '.repeat(indent * indentSize);
          }
        }
        continue;
      }

      if (char === '}' || char === ']') {
        indent--;
        if (indent < 0) indent = 0;
        if (prevChar !== '{' && prevChar !== '[') {
          result += '\n' + ' '.repeat(indent * indentSize);
        }
        result += char;
        i++;
        continue;
      }

      if (char === ',') {
        result += char;
        i++;
        if (i < len) {
          const nextNonSpace = str.slice(i).match(/^\s*([^\s])/);
          if (nextNonSpace && nextNonSpace[1] !== '}' && nextNonSpace[1] !== ']') {
            result += '\n' + ' '.repeat(indent * indentSize);
          }
        }
        continue;
      }

      if (char === ':') {
        result += char + ' ';
        i++;
        continue;
      }

      // 공백 처리
      if (char === ' ' || char === '\n' || char === '\t') {
        if (prevChar !== ' ' && prevChar !== '\n' && prevChar !== '\t' && prevChar !== ':' && prevChar !== '') {
          result += ' ';
        }
        i++;
        continue;
      }

      result += char;
      i++;
    }

    return result;
  } catch (error) {
    return str;
  }
};

export function LogAgentAllPopupPage({ selectedLog, onClose }: LogAgentAllPopupPageProps) {
  const [activeTab, setActiveTab] = useState('request');

  if (!selectedLog) return null;

  // 날짜 포맷팅 함수
  const formatDateTime = (dateTimeStr: string | undefined | null): string => {
    if (!dateTimeStr) return '-';
    try {
      const date = new Date(dateTimeStr);
      if (isNaN(date.getTime())) return dateTimeStr;
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    } catch (error) {
      console.warn('날짜 포맷팅 실패:', error);
      return dateTimeStr || '-';
    }
  };

  const getRequestTime = () => {
    return selectedLog.sessionItems?.[0]?.requestTime || selectedLog.requestTime || '-';
  };

  const getResponseTime = () => {
    return selectedLog.sessionItems?.[0]?.responseTime || selectedLog.responseTime || '-';
  };

  const getTotalDuration = () => {
    const sessionElapsedTime = selectedLog.sessionItems?.[0]?.elapsedTime;
    if (sessionElapsedTime) {
      return `${Math.round(sessionElapsedTime * 1000)}ms`;
    }
    if (selectedLog.elapsedTime) {
      return `${Math.round(selectedLog.elapsedTime * 1000)}ms`;
    }
    if (selectedLog.latency) {
      return `${Math.round(parseFloat(selectedLog.latency) * 1000)}ms`;
    }
    return '-';
  };

  const getTotalTokens = () => {
    const sessionTotalTokens = selectedLog.sessionItems?.[0]?.totalTokens;
    if (sessionTotalTokens) {
      return sessionTotalTokens;
    }
    return selectedLog.totalTokens || 0;
  };

  const getErrorMessage = () => {
    // camelCase와 snake_case 모두 확인
    const sessionItem = selectedLog.sessionItems?.[0];
    const errorMessage = sessionItem?.errorMessage || (sessionItem as any)?.error_message || selectedLog.errorMessage || (selectedLog as any)?.error_message || null;
    return errorMessage;
  };

  const statusConfig = selectedLog.status === 'normal' ? { label: '정상', intent: 'complete' as const } : { label: '실패', intent: 'error' as const };

  const beautifyJson = (json: any): string => {
    if (!json) return '';

    if (typeof json === 'string') {
      try {
        const parsed = JSON.parse(json);
        return JSON.stringify(parsed, null, 2);
      } catch (error) {
        return formatPythonDict(json);
      }
    }

    try {
      return JSON.stringify(json, null, 2);
    } catch (error) {
      return String(json);
    }
  };

  // 포맷된 JSON 값 (하단 JSON 영역 - 전체 원본 데이터 표시)
  const formattedJsonValue = useMemo(() => {
    if (activeTab === 'error') {
      const errorMessage = getErrorMessage();

      if (!errorMessage) {
        return '에러 메시지가 없습니다.';
      }
      return errorMessage;
    }

    let source: any;

    if (activeTab === 'request') {
      source = selectedLog?.inputJson;
    } else if (activeTab === 'response') {
      source = selectedLog?.outputJson;
    } else {
      source = selectedLog?.content;
    }

    if (!source) {
      return '';
    }

    return beautifyJson(source);
  }, [activeTab, selectedLog]);

  // 탭 옵션 (실패인 경우에만 Error 탭 추가)
  const tabOptions = useMemo(() => {
    const tabs = [
      { id: 'request', label: 'Request' },
      { id: 'response', label: 'Response' },
    ];

    // 실패(status === 'error')인 경우에만 Error 탭 추가
    if (selectedLog.status === 'error') {
      tabs.push({ id: 'error', label: 'Error' });
    }

    return tabs;
  }, [selectedLog.status]);

  const handleDownload = () => {
    const dataStr = JSON.stringify(formattedJsonValue, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `log_${selectedLog.id}_${selectedLog.requestTime.replace(/[: ]/g, '_')}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  // 에러 로그 여부 확인
  const isErrorLog = selectedLog.status === 'error' || getErrorMessage() !== null;

  return (
    <>
      <UIAlarm size='large' onClose={onClose} title='사용로그 상세'>
        <UIAlarmGroup>
          <section className='section-popup-content' style={{ padding: '0' }}>
            <UIPopupBody>
              <UIArticle>
                <UIDataList
                  gap={12}
                  direction='column'
                  datalist={[
                    { dataName: '요청시간', dataValue: formatDateTime(getRequestTime()) },
                    { dataName: '응답시간', dataValue: formatDateTime(getResponseTime()) },
                    { dataName: '총 소요시간', dataValue: getTotalDuration() },
                    {
                      dataName: '상태',
                      dataValue: (
                        <UILabel variant='badge' intent={statusConfig.intent}>
                          {statusConfig.label}
                        </UILabel>
                      ),
                    },
                    { dataName: '토큰합계', dataValue: getTotalTokens().toLocaleString() },
                    { dataName: '거래 식별자', dataValue: selectedLog.sessionItems?.[0]?.user || selectedLog.user || '-' },
                    { dataName: '호출 종류', dataValue: selectedLog.funcType || '-' },
                  ]}
                >
                  {null}
                </UIDataList>
              </UIArticle>

              <UIArticle>
                <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='medium' variant='body-2' />
              </UIArticle>

              <UIArticle>
                <UIUnitGroup gap={16} direction='column' align='center'>
                  <div className='flex justify-between w-full'>
                    {activeTab === 'error' ? (
                      <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                        Error Message
                      </UITypography>
                    ) : activeTab === 'response' ? (
                      <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                        JSON | Dictionary
                      </UITypography>
                    ) : (
                      <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                        JSON
                      </UITypography>
                    )}
                    {!isErrorLog && (
                      <Button className='btn-option-outline' onClick={handleDownload}>
                        로그 다운로드
                      </Button>
                    )}
                  </div>
                  <div>
                    <UICode
                      value={formattedJsonValue}
                      language={activeTab === 'error' ? undefined : 'json'}
                      theme='dark'
                      width='100%'
                      minHeight='640px'
                      height='640px'
                      maxHeight='640px'
                      disabled={true}
                    />
                  </div>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupBody>
          </section>
        </UIAlarmGroup>
      </UIAlarm>
    </>
  );
}

export default LogAgentAllPopupPage;
