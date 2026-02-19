import React from 'react';

/**
 * 마크다운 텍스트를 파싱하여 React 요소로 변환하는 함수
 */
export const parseMarkdownUtils = (text: string): React.ReactNode => {
    if (!text) return null;

    // 이스케이프된 \n을 실제 줄바꿈으로 변환
    // <br> 태그도 줄바꿈으로 변환
    const normalizedText = text.replace(/\\n/g, '\n').replace(/<br\s*\/?>/gi, '\n');

    const lines = normalizedText.split('\n');
    const elements: React.ReactNode[] = [];
    let codeBlock: string[] = [];
    let inCodeBlock = false;
    let codeLanguage = '';
    let listItems: { type: 'ul' | 'ol'; items: React.ReactNode[] } | null = null;
    let elementKey = 0;
    let htmlBlock: { tag: string; content: string[] } | null = null;
    let tableRows: string[] = [];
    let inTable = false;

    const getKey = () => `el-${elementKey++}`;

    const parseTable = (rows: string[]): React.ReactNode => {
        if (rows.length < 2) return null; // 최소 헤더 + 구분선 필요

        const headerRow = rows[0];
        const separatorRow = rows[1];
        const dataRows = rows.slice(2);

        // 헤더 파싱
        const headerCells = headerRow
            .split('|')
            .map(cell => cell.trim())
            .filter(cell => cell.length > 0);

        // 구분선에서 정렬 정보 추출 (선택적)
        const separatorCells = separatorRow.split('|').map(cell => cell.trim());
        const alignments = separatorCells
            .map(cell => {
                if (cell.startsWith(':') && cell.endsWith(':')) return 'center';
                if (cell.endsWith(':')) return 'right';
                return 'left';
            })
            .filter((_, idx) => idx > 0 && idx < separatorCells.length - 1); // 첫 번째와 마지막 빈 요소 제외

        // 테이블 헤더 생성
        const headerCellsElements = headerCells.map((cell, idx) =>
            React.createElement(
                'th',
                {
                    key: `th-${idx}`,
                    style: {
                        border: '1px solid #d1d5db',
                        padding: '0.5rem',
                        textAlign: (alignments[idx] || 'left') as 'left' | 'center' | 'right',
                        fontWeight: 700,
                        backgroundColor: '#f9fafb',
                    },
                },
                parseInline(cell)
            )
        );

        // 테이블 바디 생성
        const bodyRows = dataRows.map((row, rowIdx) => {
            const cells = row
                .split('|')
                .map(cell => cell.trim())
                .filter(cell => cell.length > 0);

            const cellsElements = cells.map((cell, cellIdx) =>
                React.createElement(
                    'td',
                    {
                        key: `td-${rowIdx}-${cellIdx}`,
                        style: {
                            border: '1px solid #d1d5db',
                            padding: '0.5rem',
                            textAlign: (alignments[cellIdx] || 'left') as 'left' | 'center' | 'right',
                        },
                    },
                    parseInline(cell)
                )
            );

            return React.createElement('tr', { key: `tr-${rowIdx}` }, cellsElements);
        });

        return React.createElement(
            'table',
            {
                key: getKey(),
                style: {
                    borderCollapse: 'collapse',
                    width: '100%',
                    margin: '1rem 0',
                    border: '1px solid #d1d5db',
                },
            },
            React.createElement('thead', { key: 'thead' }, React.createElement('tr', { key: 'tr-header' }, headerCellsElements)),
            React.createElement('tbody', { key: 'tbody' }, bodyRows)
        );
    };

    const flushList = () => {
        if (listItems) {
            const ListTag = listItems.type === 'ul' ? 'ul' : 'ol';
            elements.push(
                React.createElement(
                    ListTag,
                    {
                        key: getKey(),
                        style: {
                            listStyleType: listItems.type === 'ul' ? 'disc' : 'decimal',
                            paddingLeft: '1.25rem',
                            margin: '0.5rem 0',
                        },
                    },
                    listItems.items
                )
            );
            listItems = null;
        }
    };

    const parseInline = (line: string): React.ReactNode => {
        if (!line) return line;

        const parts: React.ReactNode[] = [];
        let partIndex = 0;

        // HTML 태그와 마크다운 패턴을 모두 포함한 복합 패턴
        // HTML 태그를 먼저 체크 (더 구체적인 패턴)
        // 마크다운: 인라인 코드, 볼드, 이탤릭, 링크, 이미지
        const combinedPattern =
            /(<s>.*?<\/s>|<u>.*?<\/u>|<del>.*?<\/del>|<ins>.*?<\/ins>|<strong>.*?<\/strong>|<em>.*?<\/em>|`[^`]+`|\*\*[^*]+\*\*|__[^_]+__|\*[^*\n]+\*|_[^_\n]+_|!\[[^\]]*\]\([^)]+\)|\[[^\]]+\]\([^)]+\))/gs;

        const matches = [...line.matchAll(combinedPattern)];

        if (matches.length === 0) {
            return line;
        }

        let lastIndex = 0;
        matches.forEach(match => {
            // 매치 전 텍스트 추가
            if (match.index !== undefined && match.index > lastIndex) {
                parts.push(line.slice(lastIndex, match.index));
            }

            const matchedText = match[0];

            // HTML 태그 처리
            if (matchedText.startsWith('<s>') && matchedText.endsWith('</s>')) {
                // 취소선
                const content = matchedText.slice(3, -4);
                parts.push(React.createElement('s', { key: `strike-${partIndex++}`, style: { textDecoration: 'line-through' } }, content));
            } else if (matchedText.startsWith('<u>') && matchedText.endsWith('</u>')) {
                // 밑줄
                const content = matchedText.slice(3, -4);
                parts.push(React.createElement('u', { key: `underline-${partIndex++}`, style: { textDecoration: 'underline' } }, content));
            } else if (matchedText.startsWith('<del>') && matchedText.endsWith('</del>')) {
                // 삭제선 (del)
                const content = matchedText.slice(5, -6);
                parts.push(React.createElement('del', { key: `del-${partIndex++}`, style: { textDecoration: 'line-through' } }, content));
            } else if (matchedText.startsWith('<ins>') && matchedText.endsWith('</ins>')) {
                // 밑줄 (ins)
                const content = matchedText.slice(5, -6);
                parts.push(React.createElement('ins', { key: `ins-${partIndex++}`, style: { textDecoration: 'underline' } }, content));
            } else if (matchedText.startsWith('<strong>') && matchedText.endsWith('</strong>')) {
                // 볼드 (strong)
                const content = matchedText.slice(8, -9);
                parts.push(React.createElement('strong', { key: `strong-${partIndex++}`, style: { fontWeight: 700 } }, content));
            } else if (matchedText.startsWith('<em>') && matchedText.endsWith('</em>')) {
                // 이탤릭 (em)
                const content = matchedText.slice(4, -5);
                parts.push(React.createElement('em', { key: `em-${partIndex++}`, style: { fontStyle: 'italic' } }, content));
            } else if (matchedText.startsWith('`') && matchedText.endsWith('`')) {
                // 인라인 코드
                parts.push(
                    React.createElement(
                        'code',
                        {
                            key: `inline-code-${partIndex++}`,
                            style: {
                                backgroundColor: '#f3f4f6',
                                padding: '0.125rem 0.375rem',
                                borderRadius: '0.25rem',
                                fontSize: '0.875rem',
                                fontFamily: 'monospace',
                                color: '#dc2626',
                            },
                        },
                        matchedText.slice(1, -1)
                    )
                );
            } else if (matchedText.startsWith('**') && matchedText.endsWith('**')) {
                // 볼드 (**)
                parts.push(React.createElement('strong', { key: `bold-${partIndex++}`, style: { fontWeight: 700 } }, matchedText.slice(2, -2)));
            } else if (matchedText.startsWith('__') && matchedText.endsWith('__')) {
                // 볼드 (__)
                parts.push(React.createElement('strong', { key: `bold-${partIndex++}`, style: { fontWeight: 700 } }, matchedText.slice(2, -2)));
            } else if (matchedText.startsWith('![')) {
                // 이미지
                const imgMatch = matchedText.match(/!\[([^\]]*)\]\(([^)]+)\)/);
                if (imgMatch) {
                    parts.push(
                        React.createElement('img', {
                            key: `img-${partIndex++}`,
                            src: imgMatch[2],
                            alt: imgMatch[1],
                            style: { maxWidth: '100%', margin: '0.5rem 0', borderRadius: '0.25rem' },
                        })
                    );
                }
            } else if (matchedText.startsWith('[') && matchedText.includes('](')) {
                // 링크
                const linkMatch = matchedText.match(/\[([^\]]+)\]\(([^)]+)\)/);
                if (linkMatch) {
                    parts.push(
                        React.createElement(
                            'a',
                            {
                                key: `link-${partIndex++}`,
                                href: linkMatch[2],
                                target: '_blank',
                                rel: 'noopener noreferrer',
                                style: { color: '#2563eb', textDecoration: 'underline' },
                            },
                            linkMatch[1]
                        )
                    );
                }
            } else if (matchedText.startsWith('*') && matchedText.endsWith('*') && !matchedText.startsWith('**')) {
                // 이탤릭 (*)
                parts.push(React.createElement('em', { key: `italic-${partIndex++}`, style: { fontStyle: 'italic' } }, matchedText.slice(1, -1)));
            } else if (matchedText.startsWith('_') && matchedText.endsWith('_') && !matchedText.startsWith('__')) {
                // 이탤릭 (_)
                parts.push(React.createElement('em', { key: `italic-${partIndex++}`, style: { fontStyle: 'italic' } }, matchedText.slice(1, -1)));
            } else {
                parts.push(matchedText);
            }

            lastIndex = (match.index || 0) + matchedText.length;
        });

        // 나머지 텍스트 추가
        if (lastIndex < line.length) {
            parts.push(line.slice(lastIndex));
        }

        return parts.length === 1 ? parts[0] : parts;
    };

    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const trimmedLine = line.trim();

        // 코드 블록 시작/종료 (``` 로 시작하는지 체크)
        if (trimmedLine.startsWith('```')) {
            if (!inCodeBlock) {
                flushList();
                inCodeBlock = true;
                codeLanguage = trimmedLine.slice(3).trim();
                codeBlock = [];
            } else {
                inCodeBlock = false;
                elements.push(
                    React.createElement(
                        'pre',
                        {
                            key: getKey(),
                            style: {
                                backgroundColor: '#1f2937',
                                color: '#f3f4f6',
                                padding: '1rem',
                                borderRadius: '0.5rem',
                                overflowX: 'auto',
                                margin: '0.75rem 0',
                            },
                        },
                        React.createElement(
                            'code',
                            {
                                className: codeLanguage ? `language-${codeLanguage}` : undefined,
                                style: {
                                    fontSize: '0.875rem',
                                    fontFamily: 'monospace',
                                    whiteSpace: 'pre-wrap',
                                    wordBreak: 'break-word',
                                },
                            },
                            codeBlock.join('\n')
                        )
                    )
                );
            }
            continue;
        }

        // 코드 블록 내용
        if (inCodeBlock) {
            codeBlock.push(line);
            continue;
        }

        // 테이블 처리 (HTML 블록이 아닐 때만)
        if (!htmlBlock) {
            const isTableRow = trimmedLine.startsWith('|') && trimmedLine.endsWith('|') && trimmedLine.length > 2;
            const isTableSeparator = /^\|[\s\-:]*\|[\s\-:]*\|/.test(trimmedLine);

            if (isTableRow || isTableSeparator) {
                if (!inTable) {
                    flushList();
                    inTable = true;
                    tableRows = [];
                }
                tableRows.push(trimmedLine);
                continue;
            } else if (inTable) {
                // 테이블 종료 (빈 줄이나 다른 요소가 나왔을 때)
                if (tableRows.length >= 2) {
                    const tableElement = parseTable(tableRows);
                    if (tableElement) {
                        elements.push(tableElement);
                    }
                }
                inTable = false;
                tableRows = [];
            }
        }

        // 빈 줄 처리 (테이블 종료 후)
        if (trimmedLine === '') {
            flushList();
            continue;
        }

        // HTML 블록 태그 처리 (<details>, <summary> 등)
        // 한 줄에 완전한 HTML 블록이 있는 경우 처리
        const htmlBlockMatch = trimmedLine.match(/^<(\w+)(?:\s[^>]*)?>(.*?)<\/\1>$/s);
        if (htmlBlockMatch) {
            flushList();
            const tagName = htmlBlockMatch[1].toLowerCase();
            const content = htmlBlockMatch[2];

            if (['details', 'summary', 'div', 'section', 'article', 'p', 'span'].includes(tagName)) {
                let parsedContent: React.ReactNode;
                if (tagName === 'details') {
                    // <summary> 태그 찾기
                    const summaryMatch = content.match(/<summary>(.*?)<\/summary>/s);
                    if (summaryMatch) {
                        const summaryContent = summaryMatch[1].trim();
                        const detailsContent = content.replace(/<summary>.*?<\/summary>/s, '').trim();

                        const children: React.ReactNode[] = [React.createElement('summary', { key: 'summary' }, parseInline(summaryContent))];

                        if (detailsContent) {
                            const detailsParsed = parseMarkdownUtils(detailsContent);
                            if (detailsParsed) {
                                // parseMarkdown은 div로 감싸진 요소를 반환하므로 children 추출
                                const detailsElement = detailsParsed as any;
                                if (detailsElement?.props?.children) {
                                    const detailsChildren = detailsElement.props.children;
                                    if (Array.isArray(detailsChildren)) {
                                        children.push(...detailsChildren);
                                    } else {
                                        children.push(detailsChildren);
                                    }
                                } else {
                                    children.push(detailsParsed);
                                }
                            }
                        }

                        // details 태그에 직접 children 전달 (Fragment 사용 안 함)
                        elements.push(React.createElement('details', { key: getKey() }, children));
                        continue;
                    } else {
                        parsedContent = parseMarkdownUtils(content);
                    }
                } else if (tagName === 'summary') {
                    parsedContent = parseInline(content);
                } else {
                    parsedContent = parseMarkdownUtils(content);
                }

                elements.push(React.createElement(tagName as React.ElementType, { key: getKey() }, parsedContent));
                continue;
            }
        }

        // 여러 줄에 걸친 HTML 블록 처리
        const htmlBlockStartMatch = trimmedLine.match(/^<(\w+)(?:\s[^>]*)?>$/);
        const htmlBlockEndMatch = trimmedLine.match(/^<\/(\w+)>$/);
        const htmlBlockSelfClosingMatch = trimmedLine.match(/^<(\w+)(?:\s[^>]*)?\s*\/>$/);

        // 자체 닫힘 태그 처리
        if (htmlBlockSelfClosingMatch) {
            flushList();
            const tagName = htmlBlockSelfClosingMatch[1].toLowerCase();
            elements.push(React.createElement(tagName as React.ElementType, { key: getKey() }));
            continue;
        }

        if (htmlBlockStartMatch && !htmlBlock) {
            // HTML 블록 시작
            flushList();
            const tagName = htmlBlockStartMatch[1].toLowerCase();
            if (['details', 'summary', 'div', 'section', 'article', 'p', 'span'].includes(tagName)) {
                htmlBlock = { tag: tagName, content: [] };
                continue;
            }
        } else if (htmlBlockEndMatch && htmlBlock) {
            // HTML 블록 종료
            const closingTag = htmlBlockEndMatch[1].toLowerCase();
            if (closingTag === htmlBlock.tag) {
                const content = htmlBlock.content.join('\n');

                // <details> 안에 <summary>가 있는 경우 처리
                let parsedContent: React.ReactNode;
                if (htmlBlock.tag === 'details') {
                    // <summary> 태그 찾기
                    const summaryMatch = content.match(/<summary>(.*?)<\/summary>/s);
                    if (summaryMatch) {
                        const summaryContent = summaryMatch[1].trim();
                        const detailsContent = content.replace(/<summary>.*?<\/summary>/s, '').trim();

                        const children: React.ReactNode[] = [React.createElement('summary', { key: 'summary' }, parseInline(summaryContent))];

                        if (detailsContent) {
                            // details 내용을 다시 파싱 (재귀)
                            const detailsParsed = parseMarkdownUtils(detailsContent);
                            if (detailsParsed) {
                                // parseMarkdown은 div로 감싸진 요소를 반환하므로 children 추출
                                const detailsElement = detailsParsed as any;
                                if (detailsElement?.props?.children) {
                                    const detailsChildren = detailsElement.props.children;
                                    if (Array.isArray(detailsChildren)) {
                                        children.push(...detailsChildren);
                                    } else {
                                        children.push(detailsChildren);
                                    }
                                } else {
                                    children.push(detailsParsed);
                                }
                            }
                        }

                        // details 태그에 직접 children 전달 (Fragment 사용 안 함)
                        elements.push(React.createElement('details', { key: getKey() }, children));
                        htmlBlock = null;
                        continue;
                    } else {
                        // summary가 없으면 내용만 파싱
                        parsedContent = parseMarkdownUtils(content);
                    }
                } else if (htmlBlock.tag === 'summary') {
                    parsedContent = parseInline(content);
                } else {
                    // 다른 태그는 인라인 파싱
                    parsedContent = parseInline(content);
                }

                elements.push(React.createElement(htmlBlock.tag as React.ElementType, { key: getKey() }, parsedContent));
                htmlBlock = null;
                continue;
            }
        }

        if (htmlBlock) {
            // HTML 블록 내용 수집
            htmlBlock.content.push(line);
            continue;
        }

        // 헤더 처리 (# ~ ######)
        // "- # Header" 또는 "* # Header" 형식도 헤더로 처리
        const headerCleanLine = trimmedLine.replace(/^[-*+]\s+/, '');
        const headerMatch = headerCleanLine.match(/^(#{1,6})\s+(.+)$/);
        if (headerMatch) {
            flushList();
            const level = headerMatch[1].length;
            const headerStyles: Record<number, React.CSSProperties> = {
                1: { fontSize: '1.5rem', fontWeight: 700, margin: '1rem 0', lineHeight: 1.3 },
                2: { fontSize: '1.25rem', fontWeight: 700, margin: '0.875rem 0', lineHeight: 1.3 },
                3: { fontSize: '1.125rem', fontWeight: 700, margin: '0.75rem 0', lineHeight: 1.3 },
                4: { fontSize: '1rem', fontWeight: 700, margin: '0.625rem 0', lineHeight: 1.3 },
                5: { fontSize: '0.875rem', fontWeight: 700, margin: '0.5rem 0', lineHeight: 1.3 },
                6: { fontSize: '0.75rem', fontWeight: 700, margin: '0.375rem 0', lineHeight: 1.3 },
            };
            elements.push(React.createElement(`h${level}` as React.ElementType, { key: getKey(), style: headerStyles[level] }, parseInline(headerMatch[2])));
            continue;
        }

        // 수평선
        if (/^(-{3,}|\*{3,}|_{3,})$/.test(trimmedLine)) {
            flushList();
            elements.push(
                React.createElement('hr', {
                    key: getKey(),
                    style: { margin: '1rem 0', border: 'none', borderTop: '1px solid #d1d5db' },
                })
            );
            continue;
        }

        // 인용문
        if (trimmedLine.startsWith('>')) {
            flushList();
            const quoteContent = trimmedLine.slice(1).trim();
            elements.push(
                React.createElement(
                    'blockquote',
                    {
                        key: getKey(),
                        style: {
                            borderLeft: '4px solid #9ca3af',
                            paddingLeft: '1rem',
                            margin: '0.5rem 0',
                            color: '#6b7280',
                            fontStyle: 'italic',
                        },
                    },
                    parseInline(quoteContent)
                )
            );
            continue;
        }

        // 순서 없는 리스트 (-, *, +) - 단, #으로 시작하지 않는 경우만
        const ulMatch = trimmedLine.match(/^[-*+]\s+(.+)$/);
        if (ulMatch && !ulMatch[1].match(/^#{1,6}\s/)) {
            if (!listItems || listItems.type !== 'ul') {
                flushList();
                listItems = { type: 'ul', items: [] };
            }
            listItems.items.push(React.createElement('li', { key: `li-${listItems.items.length}`, style: { margin: '0.25rem 0' } }, parseInline(ulMatch[1])));
            continue;
        }

        // 순서 있는 리스트 (1. 2. 3.)
        const olMatch = trimmedLine.match(/^\d+\.\s+(.+)$/);
        if (olMatch) {
            if (!listItems || listItems.type !== 'ol') {
                flushList();
                listItems = { type: 'ol', items: [] };
            }
            listItems.items.push(React.createElement('li', { key: `li-${listItems.items.length}`, style: { margin: '0.25rem 0' } }, parseInline(olMatch[1])));
            continue;
        }

        // 일반 텍스트
        flushList();
        elements.push(React.createElement('p', { key: getKey(), style: { margin: '0.25rem 0' } }, parseInline(line)));
    }

    // 마지막 리스트 처리
    flushList();

    // 테이블이 닫히지 않은 경우 처리
    if (inTable && tableRows.length >= 2) {
        const tableElement = parseTable(tableRows);
        if (tableElement) {
            elements.push(tableElement);
        }
    }

    // 코드 블록이 닫히지 않은 경우 처리
    if (inCodeBlock && codeBlock.length > 0) {
        elements.push(
            React.createElement(
                'pre',
                {
                    key: getKey(),
                    style: {
                        backgroundColor: '#1f2937',
                        color: '#f3f4f6',
                        padding: '1rem',
                        borderRadius: '0.5rem',
                        overflowX: 'auto',
                        margin: '0.75rem 0',
                    },
                },
                React.createElement(
                    'code',
                    {
                        className: codeLanguage ? `language-${codeLanguage}` : undefined,
                        style: {
                            fontSize: '0.875rem',
                            fontFamily: 'monospace',
                            whiteSpace: 'pre-wrap',
                            wordBreak: 'break-word',
                        },
                    },
                    codeBlock.join('\n')
                )
            )
        );
    }

    return React.createElement('div', { className: 'markdown-content' }, elements);
};
