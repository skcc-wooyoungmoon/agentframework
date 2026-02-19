import React, { useState, useEffect, useRef } from 'react';
import { UIButton2, UIIcon2, UITypography } from '../../atoms';
import type { UIQnaFewshotProps } from './types';

export const UIQnaFewshot: React.FC<UIQnaFewshotProps> = ({
  qnaPairs = [],
  label = 'Q&A',
  required = false,
  showAddButton = true,
  showDeleteButton = false,
  onAddQna,
  onDeleteQna,
  onQuestionChange,
  onAnswerChange,
  questionErrorMessage = '질문을 입력해 주세요.',
  answerErrorMessage = '답변을 입력해 주세요.',
  className = '',
}) => {
  const [scrollStates, setScrollStates] = useState<Record<string, { showTop: boolean; showBottom: boolean }>>({});
  const textareaRefs = useRef<Record<string, HTMLTextAreaElement | null>>({});

  useEffect(() => {
    const newStates: Record<string, { showTop: boolean; showBottom: boolean }> = {};

    qnaPairs.forEach(pair => {
      const textarea = textareaRefs.current[pair.id];
      if (textarea) {
        const hasScroll = textarea.scrollHeight > textarea.clientHeight;
        newStates[pair.id] = {
          showTop: false,
          showBottom: hasScroll,
        };
      }
    });

    setScrollStates(newStates);
  }, [qnaPairs]);

  const handleScroll = (pairId: string, e: React.UIEvent<HTMLTextAreaElement>) => {
    const target = e.currentTarget;
    const hasScroll = target.scrollHeight > target.clientHeight;
    const isAtTop = target.scrollTop === 0;
    const isAtBottom = target.scrollTop + target.clientHeight >= target.scrollHeight - 5;

    setScrollStates(prev => ({
      ...prev,
      [pairId]: {
        showTop: !isAtTop && hasScroll,
        showBottom: !isAtBottom && hasScroll,
      },
    }));
  };

  return (
    <div className={'w-full ' + className}>
      <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={required}>
        {label}
      </UITypography>

      {/* Few-shot List */}
      <div className='w-full mt-2'>
        {/* QnA Pairs Container */}
        <div className={showAddButton ? 'mb-[16px]' : ''}>
          {qnaPairs.map((pair, index) => (
            <div key={pair.id} className={index > 0 ? 'mt-3' : ''}>
              {/* Q&A Pair Container with Individual Border */}
              <div className={'border rounded-2xl overflow-hidden ' + (pair.questionError || pair.answerError ? 'border-[#d61111]' : 'border-gray-300')}>
                <div className='flex gap-0'>
                  {/* Question Area */}
                  <div className='flex-1'>
                    {/* Question Header */}
                    <div className={'bg-white px-8 py-5 border-b ' + (pair.questionError ? 'border-[#d61111]' : 'border-gray-300')}>
                      <div className='flex items-center gap-2'>
                        <span className='text-base font-semibold text-gray-800'>Q.</span>
                        <span className='text-base font-semibold text-gray-800'>질문</span>
                      </div>
                    </div>

                    {/* Question Content */}
                    <div className='bg-white relative'>
                      <textarea
                        value={pair.question}
                        onChange={e => onQuestionChange?.(pair.id, e.target.value)}
                        className='w-full h-28 px-8 py-5 resize-none border-0 text-base text-gray-800 leading-6 focus:outline-none'
                        placeholder='질문 입력'
                      />
                      {/* Scrollbar placeholder */}
                      <div className='absolute right-0 top-0 w-4 h-full bg-gray-100 opacity-50'></div>
                    </div>
                  </div>

                  {/* Vertical Divider */}
                  <div className='w-px bg-gray-300'></div>

                  {/* Answer Area */}
                  <div className='flex-1'>
                    {/* Answer Header */}
                    <div className={'bg-white px-8 py-5 border-b ' + (pair.answerError ? 'border-[#d61111]' : 'border-gray-300')}>
                      <div className='flex items-center justify-between'>
                        <div className='flex items-center gap-2'>
                          <span className='text-base font-semibold text-gray-800'>A.</span>
                          <span className='text-base font-semibold text-gray-800'>답변</span>
                        </div>
                        {(() => {
                          const shouldShowDelete = typeof showDeleteButton === 'function' ? showDeleteButton(index) : showDeleteButton;
                          return shouldShowDelete ? (
                            <UIButton2
                              className='btn-text-14-icon-left-negative'
                              leftIcon={{ className: 'ic-system-24-outline-gray-trash', children: '' }}
                              onClick={() => onDeleteQna?.(pair.id)}
                            >
                              퓨샷 삭제
                            </UIButton2>
                          ) : null;
                        })()}
                      </div>
                    </div>

                    {/* Answer Content */}
                    <div className='bg-blue-100 relative'>
                      {/* Top gradient overlay */}
                      <div
                        className='absolute top-0 left-0 right-0 h-[34px] pointer-events-none transition-opacity duration-300'
                        style={{
                          background: 'linear-gradient(0deg, rgba(255, 255, 255, 0) 2.94%, #FFFFFF 102.94%)',
                          opacity: (scrollStates[pair.id]?.showTop ?? false) ? 1 : 0,
                        }}
                      ></div>
                      <textarea
                        ref={(el) => {
                          if (el) textareaRefs.current[pair.id] = el;
                        }}
                        value={pair.answer}
                        onChange={e => onAnswerChange?.(pair.id, e.target.value)}
                        onScroll={e => handleScroll(pair.id, e)}
                        className='w-full h-28 px-8 py-5 resize-none border-0 text-base text-gray-800 leading-6 bg-blue-100 focus:outline-none'
                        placeholder='답변 입력'
                      />
                      {/* Bottom gradient overlay */}
                      <div
                        className='absolute bottom-0 left-0 right-0 h-[34px] pointer-events-none transition-opacity duration-300'
                        style={{
                          background: 'linear-gradient(180deg, rgba(255, 255, 255, 0) 2.94%, #FFFFFF 102.94%)',
                          opacity: (scrollStates[pair.id]?.showBottom ?? false) ? 1 : 0,
                        }}
                      ></div>
                      {/* Scrollbar placeholder */}
                      {/* <div className='absolute right-0 top-0 w-4 h-full bg-gray-200 opacity-50'></div> */}
                    </div>
                  </div>
                </div>
              </div>

              {/* Error Messages */}
              {(pair.questionError || pair.answerError) && (
                <div className='flex w-full'>
                  {/* Question Error Message */}
                  <div className='flex-1'>
                    {pair.questionError && (
                      <div className='pl-[12px]'>
                        <span className='text-sm text-[#d61111] leading-5'>{questionErrorMessage}</span>
                      </div>
                    )}
                  </div>

                  {/* Answer Error Message */}
                  <div className='flex-1'>
                    {pair.answerError && (
                      <div className='pl-[12px]'>
                        <span className='text-sm text-[#d61111] leading-5'>{answerErrorMessage}</span>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>

        {/* Add Button */}
        {showAddButton && (
          <div className='flex items-center gap-2'>
            <UIIcon2 className='ic-system-12-plus-blue text-blue-800 !w-[12px] !h-[12px]' />
            <UIButton2 className='btn-text-14-underline-point' onClick={onAddQna}>
              Q&A 추가
            </UIButton2>
          </div>
        )}
      </div>
    </div>
  );
};
