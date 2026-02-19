// import React, { useMemo, useState } from 'react';

import { UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
// import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { DesignLayout } from '@/design/components/DesignLayout';
import { UIAccordion } from '@/components/UI/molecules/UIAccordion';
import { UIGroup } from '@/components/UI/molecules';

export const EV_010102 = () => {
  // 저지평가 전체 아코디언 데이터
  const judgementEvaluationItems = [
    {
      title: '저지평가 (Judgement Evaluation)',
      defaultOpen: true,
      showNoticeIcon: false,
      arrowPosition: 'right' as const,
      content: (
        <div className='accordion'>
          {/* 상단 아이콘 영역 */}
          <div className='accordion-icon'>
            <UIImage src='/assets/images/data/ico-radio-visual12.svg' alt='저지평가 아이콘' className='' loading='eager' />
          </div>

          {/* 평가 주체 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 주체
              </UITypography>
            </h2>
            <ul className='accordion-list'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Evaluation Task
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    User Metric으로 LLM 모델이 평가
                  </UITypography>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAG Checker Task
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    Text 분해 기법으로 LLM 모델이 평가
                  </UITypography>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAGAs Task
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    RAGAs 프레임워크로 LLM모델이 평가
                  </UITypography>
                </span>
              </li>
            </ul>
          </div>

          {/* 평가 지표 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 지표
              </UITypography>
            </h2>
            <ul className='accordion-list'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Evaluation Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      User Metric
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Metric : Safety, RAG Quality가 제공 (단, 사용자가 평가를 위해 직접 새로운 Metric 등록 가능)
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAG Checker Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      System Metric
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Metric : F1, Precision, Recall 등
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAGAs Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      System Metric
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Metric : ResponseGroundedness, AnswerAccuracy,… 중 선택
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>

          {/* 평가 대상 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 대상
              </UITypography>
            </h2>
            <ul className='accordion-list'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Evaluation Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Response Set
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필수 컬럼 : Query, Response + User Metric에 따라 Meta 컬럼 필요
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAG Checker Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Response Set
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필수 컬럼 : Query, Response, Retrieved Context, Expected Response
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAGAs Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Response Set
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필수 컬럼 : Query, Response + 선택된 Metric에 따라 Retrived Context, Ground Truth 필요
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>

          {/* 사전 조건 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                사전 조건
              </UITypography>
            </h2>
            <ul className='accordion-list'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Evaluation Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Evaluation Model 준비 (등록/기본 모델 활용)
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Evaluation model :Datumo_rag_evaluator, Datumo_safety_evaluator
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Target Model / Agent 등록
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 평가할 모델 또는 에이전트를 등록
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      User Metric 준비 (등록/기본 메트릭 활용)
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필요에 따라 등록하며, 기본 Metric 선택 가능
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAG Checker Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Evaluation Model 준비 (등록/기본 모델 활용)
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Evaluation model :Datumo_rag_checker
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Target Model / Agent 등록
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 평가할 모델 또는 에이전트를 등록
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    RAGAs Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Evaluation Model 준비 (등록/기본 모델 활용)
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본으로 제공되는 RAGAs 모델은 없음
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Target Model / Agent 등록
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 평가할 모델 또는 에이전트를 등록
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>
        </div>
      ),
    },
  ];

  // 정성평가 전체 아코디언 데이터
  const humanEvaluationItems = [
    {
      title: '정성평가 (Human Evaluation)',
      defaultOpen: true,
      showNoticeIcon: false,
      arrowPosition: 'right' as const,
      content: (
        <div className='accordion'>
          {/* 상단 아이콘 영역 */}
          <div className='accordion-icon'>
            <UIImage src='/assets/images/data/ico-radio-visual06.svg' alt='정성평가 아이콘' className='' loading='eager' />
          </div>

          {/* 평가 주체 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 주체
              </UITypography>
            </h2>
            <ul className='accordion-list'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Manual Evaluation
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    User Metric으로 LLM 모델이 평가
                  </UITypography>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Interactive Evaluation
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    Text 분해 기법으로 LLM 모델이 평가
                  </UITypography>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    -
                  </UITypography>
                </span>
              </li>
            </ul>
          </div>

          {/* 평가 지표 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 지표
              </UITypography>
            </h2>
            <ul className='accordion-list h-[252px]'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Manual Evaluation
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      User Metric
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Metric : F1, Precision, Recall 등
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Interactive Evaluation
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      System Metric
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Metric : F1, Precision, Recall 등
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>

          {/* 평가 대상 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 대상
              </UITypography>
            </h2>
            <ul className='accordion-list h-[272px]'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Manual Evaluation
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Response Set
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필수 컬럼 : Query, Response + User Metric에 따라 Meta 컬럼 필요
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Interactive Evaluation
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Response Set
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필수 컬럼 : Query, Response, Retrieved Context, Expected Response
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>

          {/* 사전 조건 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                사전 조건
              </UITypography>
            </h2>
            <ul className='accordion-list h-[512px]'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Manual Evaluation
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Evaluation Model 준비 (등록/기본 모델 활용)
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Evaluation model :Datumo_rag_evaluator, Datumo_safety_evaluator
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Target Model / Agent 등록
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 평가할 모델 또는 에이전트를 등록
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      User Metric 준비 (등록/기본 메트릭 활용)
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필요에 따라 등록하며, 기본 Metric 선택 가능
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Interactive Evaluation
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Evaluation Model 준비 (등록/기본 모델 활용)
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Evaluation model :Datumo_rag_checker
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Target Model / Agent 등록
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 평가할 모델 또는 에이전트를 등록
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>
        </div>
      ),
    },
  ];

  // 정량평가 전체 아코디언 데이터
  const quantitativeEvaluationItems = [
    {
      title: '정량평가 (Quantitative Evaluation)',
      defaultOpen: true,
      showNoticeIcon: false,
      arrowPosition: 'right' as const,
      content: (
        <div className='accordion'>
          {/* 상단 아이콘 영역 */}
          <div className='accordion-icon'>
            <UIImage src='/assets/images/data/ico-radio-visual13.svg' alt='정량평가 아이콘' className='' loading='eager' />
          </div>

          {/* 평가 주체 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 주체
              </UITypography>
            </h2>
            <ul className='accordion-list'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Harness Task
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    LM Harness 프레임워크 기반으로 평가
                  </UITypography>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Reference-based
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    정량평가용 프레임워크 기반으로 평가
                  </UITypography>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    -
                  </UITypography>
                </span>
              </li>
            </ul>
          </div>

          {/* 평가 지표 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 지표
              </UITypography>
            </h2>
            <ul className='accordion-list h-[252px]'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Harness Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      System Metric
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Metric : KMMLU, HRM8K, KOBEST… 중 선택
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Reference-based
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      System Metric
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 기본 Metric : BLEU, TER, METEOR,… 중 선택
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>

          {/* 평가 대상 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                평가 대상
              </UITypography>
            </h2>
            <ul className='accordion-list h-[272px]'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Harness Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      공개용 벤치마크 Dataset
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Reference-based
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Response Set
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 필수 컬럼 : Query, Response, Expected Response
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>

          {/* 사전 조건 */}
          <div className='accordion-col-item'>
            <h2>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                사전 조건
              </UITypography>
            </h2>
            <ul className='accordion-list h-[512px]'>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Harness Task
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Target Model / Agent 등록
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 평가할 모델 또는 에이전트를 등록
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    Reference-based
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      Target Model / Agent 등록
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-500'>
                      * 평가할 모델 또는 에이전트를 등록
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
              <li>
                <span>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    -
                  </UITypography>
                </span>
                <span>
                  <UIGroup gap={4} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      -
                    </UITypography>
                  </UIGroup>
                </span>
              </li>
            </ul>
          </div>
        </div>
      ),
    },
  ];

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='평가 가이드 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <UITypography variant='body-1' className='secondary-neutral-700'>
              Datumo에서 제공하는 평가 시스템의 기본적인 구조와 특징, 정상적인 평가 진행을 위한 준비 과정을 조회하고 각 평가를 비교할 수 있습니다.
            </UITypography>
          </UIArticle>
          <UIArticle className='pt-4'>
            <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
              가이드 항목 정의
            </UITypography>
            <div className='info-box mt-3'>
              <ul>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      평가 주체
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      ‘누가 평가할 것인가' 에 대한 내용으로, 평가 결과를 만들어내는 대상을 안내합니다.
                    </UITypography>
                  </UIGroup>
                </li>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      평가 지표
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      ‘어떤 Metric으로 평가할 것인가'에 대한 내용으로, 평가에서 사용할 기준(Metric)을 안내합니다.
                    </UITypography>
                  </UIGroup>
                </li>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      평가 대상
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      ‘어떤 데이터로 평가할 것인가'에 대한 내용으로, 평가에서 사용할 데이터셋을 안내합니다.
                    </UITypography>
                  </UIGroup>
                </li>
                <li>
                  <UIGroup gap={16} direction='row'>
                    <UITypography variant='body-1' className='col-gray w-[109px]'>
                      사전 조건
                    </UITypography>
                    <UITypography variant='body-1' className='secondary-neutral-700'>
                      평가 진행을 위해 각 Task별로 사전에 준비해야 하는 항목들을 안내합니다.
                    </UITypography>
                  </UIGroup>
                </li>
              </ul>
            </div>
          </UIArticle>

          <UIArticle className='pt-4'>
            {/* justify-between / justify-around / justify-evenly */}
            <div className='flex justify-between'>
              {/* 저지평가 (Judgement Evaluation) */}
              <UIAccordion items={judgementEvaluationItems} variant='box' allowMultiple={false} className='!w-[500px] shrink-0' />
              {/* 정성평가 (Human Evaluation) */}
              <UIAccordion items={humanEvaluationItems} variant='box' allowMultiple={false} className='!w-[500px] shrink-0' />
              {/* 정량평가 (Quantitative Evaluation) */}
              <UIAccordion items={quantitativeEvaluationItems} variant='box' allowMultiple={false} className='!w-[500px] shrink-0' />
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
