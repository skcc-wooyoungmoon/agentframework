export interface QnaPair {
  /** 질문 내용 */
  question: string;
  /** 답변 내용 */
  answer: string;
  /** QnA 쌍의 고유 ID */
  id: string;
  /** 질문 에러 상태 */
  questionError?: boolean;
  /** 답변 에러 상태 */
  answerError?: boolean;
}

export interface UIQnaFewshotProps {
  /** QnA 쌍 데이터 목록 */
  qnaPairs: QnaPair[];
  /** 라벨 텍스트 */
  label?: string;
  /** 필수 입력 표시 여부 */
  required?: boolean;
  /** Q&A 추가 버튼 표시 여부 */
  showAddButton?: boolean;
  /** 퓨샷 지우기 버튼 표시 여부 */
  showDeleteButton?: boolean | ((index: number) => boolean);
  /** Q&A 추가 버튼 클릭 핸들러 */
  onAddQna?: () => void;
  /** QnA 쌍 삭제 핸들러 */
  onDeleteQna?: (id: string) => void;
  /** 질문 변경 핸들러 */
  onQuestionChange?: (id: string, question: string) => void;
  /** 답변 변경 핸들러 */
  onAnswerChange?: (id: string, answer: string) => void;
  /** 질문 에러 메시지 */
  questionErrorMessage?: string;
  /** 답변 에러 메시지 */
  answerErrorMessage?: string;
  /** 추가 CSS 클래스 */
  className?: string;
}
