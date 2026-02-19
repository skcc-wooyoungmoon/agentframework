import type { ReactNode } from 'react';

/** 파일 아이템 인터페이스 */
export interface UIFileBoxItem {
  /** 파일 Id */
  fileId?: number;
  /** 파일명 */
  fileName: string;
  /** 파일 크기 (KB 단위) */
  fileSize?: number;
  /** 업로드 프로그래스 (0-100) */
  progress?: number;
  /** 파일 상태 */
  status?: 'none' | 'error';
  /** 토글 활성화 여부 */
  toggleEnabled?: boolean;
  /** 파일 객체 */
  file?: File;
  /** 가이드 메시지 텍스트 */
  guideMessage?: string;
  /** 메타데이터 버튼 텍스트 (txtBtn 타입에서 사용) */
  metadataText?: string;
}

export interface UIFileBoxProps {
  /** 파일 업로드 타입 - default: 기본 파일 선택, txtBtn: 텍스트 버튼 형태, toggle: 토글 스위치가 있는 형태, processing: 지식 생성 중, completed: 지식 생성 완료, link: 링크 형태 */
  variant?: 'default' | 'txtBtn' | 'toggle' | 'processing' | 'completed' | 'link';

  /** 컴포넌트 크기 - default: 기본 크기, wide: 가로로 긴 크기, full: 화면 전체 너비 */
  size?: 'default' | 'wide' | 'full';

  /** 파일 목록 (여러 파일을 한 번에 렌더링할 때 사용, 최대 6개까지 노출되고 이후 스크롤) */
  items?: UIFileBoxItem[];

  /** 파일 데이터 객체 (UIFileBoxItem 형태로 전달, 개별 props보다 우선) */
  data?: Partial<UIFileBoxItem>;

  /** 업로드된 파일 정보 */
  file?: File | null;

  /** 파일명 표시 텍스트 */
  fileName?: string;

  /** 파일 Id */
  fileId?: number;

  /** 파일 크기 표시 (KB 단위로 표시) */
  fileSize?: number;

  /** 업로드 프로그래스바 (단위 %) */
  progress?: number;

  /** 상태 파일용량 오버시 error */
  status?: 'none' | 'error';

  /** 추가 라벨 텍스트 (toggle 타입에서 사용) */
  label?: string;

  /** 토글 활성화 여부 (toggle 타입에서 사용) */
  toggleEnabled?: boolean;

  /** 컴포넌트 비활성화 여부 */
  disabled?: boolean;

  /** 파일 선택 시 콜백 함수 */
  onFileSelect?: (file: File | null) => void;

  /** 파일 삭제 시 콜백 함수 (items 사용 시 index를 받음) */
  onFileRemove?: (index?: number) => void;

  /** 토글 변경 시 콜백 함수 (toggle 타입에서 사용, items 사용 시 index를 받음) */
  onToggleChange?: (enabled: boolean, index?: number) => void;

  /** 허용되는 파일 타입 */
  accept?: string;

  /** 추가 CSS 클래스 */
  className?: string;

  /** 가이드 메시지 텍스트 */
  guideMessage?: string;

  /** 메타데이터 버튼 텍스트 (txtBtn 타입에서 사용) */
  metadataText?: string;

  /** 메타데이터 버튼 클릭 시 콜백 함수 (txtBtn 타입에서 사용) */
  onMetadataClick?: () => void;

  /** 링크파일 클릭 시 콜백 함수 */
  onClickFileName?: () => void;

  /** children 요소 */
  children?: ReactNode;
}
