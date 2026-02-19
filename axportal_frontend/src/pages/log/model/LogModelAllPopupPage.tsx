import { useMemo, useState } from 'react';

import { UIButton2, UILabel, UITypography } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIArticle, UIDataList, UIPopupBody, UITabList, UIUnitGroup } from '@/components/UI/molecules';
import { UIAlarm, UIAlarmGroup } from '@/components/UI/organisms';
import type { LogGridItem } from '@/services/log/model/types.ts';
import { dateUtils } from '@/utils/common';

type LogModelAllPopupPageProps = {
  logItem: LogGridItem;
  onClose: () => void;
};

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
        // 다음 문자가 닫는 괄호가 아니면 줄바꿈과 들여쓰기 추가
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
        // 이전 문자가 여는 괄호가 아니면 줄바꿈과 들여쓰기 추가
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
        // 다음 문자가 닫는 괄호가 아니면 줄바꿈과 들여쓰기 추가
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

      // 공백 처리 (연속된 공백은 하나로)
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
    // 파싱 실패 시 원문 그대로 반환
    return str;
  }
};

export const LogModelAllPopupPage = ({ logItem, onClose }: LogModelAllPopupPageProps) => {
  // 탭 상태
  const [activeTab, setActiveTab] = useState('request');

  const textAreaTitle = useMemo(() => {
    switch (activeTab) {
      case 'request':
        return 'JSON';
      case 'response':
        return 'Dictionary';
      case 'error':
        return '';
      default:
        return '';
    }
  }, [activeTab]);

  const textAreaValue = useMemo(() => {
    switch (activeTab) {
      case 'request':
        return logItem.inputJson;
      case 'response':
        return logItem.outputJson;
      case 'error':
        return logItem?.errorMessage || '';
      default:
        return '';
    }
  }, [activeTab]);

  const formattedJsonValue = useMemo(() => {
    try {
      if (activeTab === 'error') {
        return textAreaValue;
      }
      // 먼저 JSON 형식으로 파싱 시도
      const parsed = JSON.parse(textAreaValue || '{}');
      return JSON.stringify(parsed, null, 2);
    } catch (error) {
      // JSON 파싱 실패 시 Python 딕셔너리 문자열을 직접 포맷팅
      return formatPythonDict(textAreaValue || '');
    }
  }, [textAreaValue, activeTab]);

  // 팝업 핸들러
  const handleClose = () => {
    // console.log('필터 닫기');
    onClose();
  };

  const handleDownload = () => {
    const dataStr = JSON.stringify(formattedJsonValue, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `log_${logItem.modelServingId}_${logItem.requestTime.replace(/[: ]/g, '_')}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  // 탭 아이템 정의
  const logTabItems = [
    { id: 'request', label: 'Request' },
    { id: 'response', label: 'Response' },
    { id: 'error', label: 'Error Message' },
  ];

  const dataList = useMemo(() => {
    if (!logItem) return [];
    return [
      { dataName: '요청시간', dataValue: dateUtils.formatDateWithPattern(logItem.requestTime, 'yyyy.MM.dd HH:mm:ss', false) },
      { dataName: '응답시간', dataValue: logItem.responseTime ? dateUtils.formatDateWithPattern(logItem.responseTime, 'yyyy.MM.dd HH:mm:ss', false) : '' },
      { dataName: '총 소요시간', dataValue: logItem.elapsedTime ? `${Math.floor(logItem.elapsedTime * 1000)}ms` : '' },
      {
        dataName: '상태',
        dataValue: (
          <UILabel variant='badge' intent={logItem.errorCode ? 'error' : 'complete'}>
            {logItem.errorCode ? '실패' : '정상'}
          </UILabel>
        ),
      },
      { dataName: '토큰합계', dataValue: logItem.totalTokens },
      { dataName: '거래 식별자', dataValue: logItem.user },
    ];
  }, [logItem]);

  return (
    <>
      {/* 사용로그 상세 */}
      <UIAlarm size='large' onClose={handleClose} title='사용로그 상세'>
        <UIAlarmGroup>
          <section className='section-popup-content' style={{ padding: '0' }}>
            <UIPopupBody>
              <UIArticle>
                <UIDataList gap={12} direction='column' datalist={dataList}>
                  {null}
                </UIDataList>
              </UIArticle>
              <UIArticle>
                <UITabList items={logTabItems} activeId={activeTab} size='large' onTabClick={setActiveTab} />
              </UIArticle>
              {/*<UIArticle className='!mt-[24px]'>*/}
              {/*  <UITextArea2 value={textAreaValue || ''} readOnly={true} placeholder='' />*/}
              {/*</UIArticle>*/}
              <UIArticle>
                <UIUnitGroup gap={16} direction='column' align='center'>
                  <div className='flex justify-between w-full'>
                    <UITypography variant='title-4' className='secondary-neutral-900 text-title-4-sb'>
                      {textAreaTitle}
                    </UITypography>
                    {activeTab !== 'error' && (
                      <UIButton2 className='btn-tertiary-outline' onClick={handleDownload}>
                        로그 다운로드
                      </UIButton2>
                    )}
                  </div>
                  <div>
                    {/* 실제 에디트 코드 영역 */}
                    <UICode value={formattedJsonValue} language='json' theme='dark' width='100%' minHeight='640px' height='640px' maxHeight='640px' readOnly={true} />
                  </div>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupBody>
          </section>
        </UIAlarmGroup>
      </UIAlarm>
    </>
  );
};
