import { json } from '@codemirror/lang-json';
import { python } from '@codemirror/lang-python';
import { yaml } from '@codemirror/lang-yaml';
import CodeMirror from '@uiw/react-codemirror';
import { useEffect, useRef, useState } from 'react';

import type { Extension } from '@codemirror/state';

import type { UICodeProps } from './types';

import '@/styles/codemirror.css';

export function UICode({
  value = '',
  onChange,
  readOnly = false,
  disabled = false,
  width = '50%',
  height,
  minHeight = '500px',
  maxHeight,
  maxWidth = '',
  language = 'python',
  theme = 'light',
  wordWrap = false,
}: UICodeProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [containerHeight, setContainerHeight] = useState<string | undefined>(height || minHeight);
  const resizeTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (!containerRef.current) return;

    // 초기 높이 설정
    if (height) {
      setContainerHeight(height);
    } else {
      setContainerHeight(minHeight);
    }

    // ResizeObserver로 높이 변화 감지 (debouncing 적용)
    const resizeObserver = new ResizeObserver(entries => {
      // 기존 타이머 취소
      if (resizeTimeoutRef.current) {
        clearTimeout(resizeTimeoutRef.current);
      }

      // debounce: 50ms 후에 높이 업데이트
      resizeTimeoutRef.current = setTimeout(() => {
        for (const entry of entries) {
          let newHeight = entry.contentRect.height;

          // maxHeight가 설정되어 있으면 그 이상으로 커질 수 없음
          if (maxHeight) {
            const maxHeightPx = parseFloat(maxHeight);
            if (!isNaN(maxHeightPx)) {
              newHeight = Math.min(newHeight, maxHeightPx);
            }
          }

          setContainerHeight(`${newHeight}px`);
        }
      }, 50);
    });

    resizeObserver.observe(containerRef.current);

    return () => {
      resizeObserver.disconnect();
      if (resizeTimeoutRef.current) {
        clearTimeout(resizeTimeoutRef.current);
      }
    };
  }, [height, minHeight, maxHeight]);

  // wordWrap 변경 시 CodeMirror 내부 DOM에 직접 스타일 적용 및 강제 유지 (자동 줄바꿈 / 개행 사용시) wordWrap={true} 사용
  useEffect(() => {
    if (!containerRef.current || !wordWrap) return;

    const applyWrapStyles = () => {
      const editor = containerRef.current?.querySelector('.cm-editor') as HTMLElement;
      const scroller = containerRef.current?.querySelector('.cm-scroller') as HTMLElement;
      const content = containerRef.current?.querySelector('.cm-content') as HTMLElement;

      if (!editor || !scroller || !content) return;

      // Editor 설정
      editor.setAttribute(
        'style',
        `
        width: 100% !important;
        max-width: 100% !important;
        overflow-x: hidden !important;
        overflow-y: auto !important;
      `
      );

      // Scroller 설정
      scroller.setAttribute(
        'style',
        `
        width: 100% !important;
        max-width: 100% !important;
        box-sizing: border-box !important;
        padding: 10px 0 10px 0 !important;
        overflow-x: hidden !important;
        overflow-y: auto !important;
      `
      );

      // Content 설정
      content.setAttribute(
        'style',
        `
        width: calc(100% - 40px) !important;
        white-space: pre-wrap !important;
        word-break: break-word !important;
        overflow-wrap: break-word !important;
        word-wrap: break-word !important;
      `
      );

      // 모든 cm-line에 스타일 강제 적용
      const lines = containerRef.current?.querySelectorAll('.cm-line') as NodeListOf<HTMLElement>;
      lines.forEach((line: HTMLElement) => {
        line.setAttribute(
          'style',
          `
          width: calc(100% - 40px) !important;
          white-space: pre-wrap !important;
          word-break: break-word !important;
          overflow-wrap: break-word !important;
          word-wrap: break-word !important;
          display: block !important;
          box-sizing: border-box !important;
        `
        );
      });
    };

    // 초기 적용
    applyWrapStyles();

    // 100ms 간격으로 계속 강제 적용 (CodeMirror의 재렌더링 대비)
    const interval = setInterval(applyWrapStyles, 100);

    // MutationObserver로 DOM 변경 감시
    const observer = new MutationObserver(() => {
      applyWrapStyles();
    });

    const contentElement = containerRef.current.querySelector('.cm-content');
    if (contentElement) {
      observer.observe(contentElement, {
        childList: true,
        subtree: true,
      });
    }

    return () => {
      clearInterval(interval);
      observer.disconnect();
    };
  }, [wordWrap]);

  const getLanguageExtension = (): Extension => {
    switch (language) {
      case 'json':
        return json();
      case 'yaml':
        return yaml();
      case 'python':
        return python();
      default:
        return python();
    }
  };
  return (
    <div
      ref={containerRef}
      style={{
        width,
        resize: 'vertical',
        minHeight,
        boxSizing: 'border-box',
        ...(maxHeight && { maxHeight }),
        ...(height && { height }),
      }}
    >
      <CodeMirror
        value={value}
        height={containerHeight}
        style={{
          minHeight,
          ...(maxHeight && { maxHeight }),
          ...(maxWidth && { maxWidth }),
        }}
        basicSetup={{
          lineNumbers: true,
          foldGutter: true,
          highlightActiveLine: true,
          indentOnInput: true,
          tabSize: 4,
          bracketMatching: true,
        }}
        extensions={[getLanguageExtension()]}
        theme={theme === 'light' ? 'light' : theme === 'dark' ? 'dark' : 'none'}
        onChange={onChange}
        readOnly={readOnly || disabled}
        editable={!disabled}
      />
    </div>
  );
}
