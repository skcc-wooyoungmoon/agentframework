/**
 * react-toastify 완전 대체 - 순수 React + Tailwind CSS 기반 토스트 시스템
 * react-toastify 의존성 완전 제거
 */
import React, { createContext, useCallback, useContext, useEffect, useRef, useState } from 'react';
import { stringUtils } from '@/utils/common';

// Toast 타입 정의
export type ToastType = 'success' | 'error' | 'warning' | 'info';
export type ToastPosition = 'top-right' | 'top-left' | 'top-center' | 'bottom-right' | 'bottom-left' | 'bottom-center';

export interface Toast {
  id: string;
  type: ToastType;
  content: React.ReactNode;
  title?: string;
  position: ToastPosition;
  autoClose?: number | false;
  hideProgressBar?: boolean;
  closeOnClick?: boolean;
  pauseOnHover?: boolean;
  draggable?: boolean;
  progress?: number;
  createdAt: number;
}

export interface ToastOptions {
  position?: ToastPosition;
  autoClose?: number | false;
  hideProgressBar?: boolean;
  closeOnClick?: boolean;
  pauseOnHover?: boolean;
  draggable?: boolean;
  title?: string;
}

// Toast Context
interface ToastContextType {
  toasts: Toast[];
  addToast: (toast: Omit<Toast, 'id' | 'createdAt' | 'progress'>) => string;
  removeToast: (id: string) => void;
  updateToast: (id: string, updates: Partial<Toast>) => void;
  dismissAll: () => void;
}

const ToastContext = createContext<ToastContextType | null>(null);

// Toast Provider
export const ABToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<Toast[]>([]);
  const timersRef = useRef<Map<string, NodeJS.Timeout>>(new Map());

  const addToast = useCallback((toastData: Omit<Toast, 'id' | 'createdAt' | 'progress'>) => {
    const id = stringUtils.secureRandomString(9);
    const newToast: Toast = {
      ...toastData,
      id,
      createdAt: Date.now(),
      progress: 100,
    };

    setToasts(prev => [...prev, newToast]);

    // Auto close 설정
    if (toastData.autoClose !== false) {
      const autoCloseTime = toastData.autoClose || 3000;
      const timer = setTimeout(() => {
        removeToast(id);
      }, autoCloseTime);
      timersRef.current.set(id, timer);

      // Progress bar 애니메이션
      if (!toastData.hideProgressBar) {
        const progressInterval = setInterval(() => {
          setToasts(prev =>
            prev.map(toast => {
              if (toast.id === id) {
                const elapsed = Date.now() - toast.createdAt;
                const progress = Math.max(0, 100 - (elapsed / autoCloseTime) * 100);
                return { ...toast, progress };
              }
              return toast;
            })
          );
        }, 50);

        setTimeout(() => clearInterval(progressInterval), autoCloseTime);
      }
    }

    return id;
  }, []);

  const removeToast = useCallback((id: string) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
    const timer = timersRef.current.get(id);
    if (timer) {
      clearTimeout(timer);
      timersRef.current.delete(id);
    }
  }, []);

  const updateToast = useCallback((id: string, updates: Partial<Toast>) => {
    setToasts(prev => prev.map(toast => (toast.id === id ? { ...toast, ...updates } : toast)));
  }, []);

  const dismissAll = useCallback(() => {
    setToasts([]);
    timersRef.current.forEach(timer => clearTimeout(timer));
    timersRef.current.clear();
  }, []);

  return (
    <ToastContext.Provider value={{ toasts, addToast, removeToast, updateToast, dismissAll }}>
      {children}
      <ToastContainer />
    </ToastContext.Provider>
  );
};

// Toast Hook
export const useABToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useABToast must be used within ABToastProvider');
  }

  const { addToast, removeToast, updateToast, dismissAll } = context;

  const showToast = useCallback(
    (content: React.ReactNode, type: ToastType = 'info', options?: ToastOptions) => {
      return addToast({
        type,
        content,
        position: options?.position || 'top-right',
        autoClose: options?.autoClose,
        hideProgressBar: options?.hideProgressBar,
        closeOnClick: options?.closeOnClick,
        pauseOnHover: options?.pauseOnHover,
        draggable: options?.draggable,
        title: options?.title,
      });
    },
    [addToast]
  );

  return {
    showToast,
    showSuccess: (content: React.ReactNode, options?: ToastOptions) => showToast(content, 'success', options),
    showError: (content: React.ReactNode, options?: ToastOptions) => showToast(content, 'error', options),
    showWarning: (content: React.ReactNode, options?: ToastOptions) => showToast(content, 'warning', options),
    showInfo: (content: React.ReactNode, options?: ToastOptions) => showToast(content, 'info', options),
    dismiss: removeToast,
    dismissAll,
    update: updateToast,
  };
};

// Toast API (react-toastify 호환)
export const ABToast = {
  success: (content: React.ReactNode, options?: ToastOptions) => {
    // Provider 없이도 사용할 수 있도록 전역 상태 관리
    const event = new CustomEvent('ab-toast', {
      detail: { type: 'success', content, options },
    });
    window.dispatchEvent(event);
  },
  error: (content: React.ReactNode, options?: ToastOptions) => {
    const event = new CustomEvent('ab-toast', {
      detail: { type: 'error', content, options },
    });
    window.dispatchEvent(event);
  },
  warning: (content: React.ReactNode, options?: ToastOptions) => {
    const event = new CustomEvent('ab-toast', {
      detail: { type: 'warning', content, options },
    });
    window.dispatchEvent(event);
  },
  info: (content: React.ReactNode, options?: ToastOptions) => {
    const event = new CustomEvent('ab-toast', {
      detail: { type: 'info', content, options },
    });
    window.dispatchEvent(event);
  },
  promise: <T = unknown,>(
    promise: Promise<T>,
    messages: {
      pending: string;
      success: string;
      error: string;
    }
  ): Promise<T> => {
    // const _pendingId = ABToast.info(messages.pending, { autoClose: false };

    return promise
      .then(result => {
        ABToast.success(messages.success);
        return result;
      })
      .catch(error => {
        ABToast.error(messages.error);
        throw error;
      });
  },
  dismiss: (toastId?: string) => {
    const event = new CustomEvent('ab-toast-dismiss', {
      detail: { id: toastId },
    });
    window.dispatchEvent(event);
  },
  update: (toastId: string, options: ToastOptions) => {
    const event = new CustomEvent('ab-toast-update', {
      detail: { id: toastId, options },
    });
    window.dispatchEvent(event);
  },
};

// Toast Container
const ToastContainer: React.FC = () => {
  const context = useContext(ToastContext);
  if (!context) return null;

  const { toasts, removeToast, updateToast } = context;

  // 전역 이벤트 리스너
  useEffect(() => {
    const handleToast = (event: CustomEvent) => {
      const { type, content, options } = event.detail;
      context.addToast({
        type,
        content,
        position: options?.position || 'top-right',
        autoClose: options?.autoClose,
        hideProgressBar: options?.hideProgressBar,
        closeOnClick: options?.closeOnClick,
        pauseOnHover: options?.pauseOnHover,
        draggable: options?.draggable,
        title: options?.title,
      });
    };

    const handleDismiss = (event: CustomEvent) => {
      const { id } = event.detail;
      if (id) {
        removeToast(id);
      } else {
        context.dismissAll();
      }
    };

    const handleUpdate = (event: CustomEvent) => {
      const { id, options } = event.detail;
      updateToast(id, options);
    };

    window.addEventListener('ab-toast', handleToast as EventListener);
    window.addEventListener('ab-toast-dismiss', handleDismiss as EventListener);
    window.addEventListener('ab-toast-update', handleUpdate as EventListener);

    return () => {
      window.removeEventListener('ab-toast', handleToast as EventListener);
      window.removeEventListener('ab-toast-dismiss', handleDismiss as EventListener);
      window.removeEventListener('ab-toast-update', handleUpdate as EventListener);
    };
  }, [context, removeToast, updateToast]);

  // Position별로 토스트 그룹화
  const toastsByPosition = toasts.reduce(
    (acc, toast) => {
      if (!acc[toast.position]) {
        acc[toast.position] = [];
      }
      acc[toast.position].push(toast);
      return acc;
    },
    {} as Record<ToastPosition, Toast[]>
  );

  const positionClasses = {
    'top-right': 'top-4 right-4',
    'top-left': 'top-4 left-4',
    'top-center': 'top-4 left-1/2 transform -translate-x-1/2',
    'bottom-right': 'bottom-4 right-4',
    'bottom-left': 'bottom-4 left-4',
    'bottom-center': 'bottom-4 left-1/2 transform -translate-x-1/2',
  };

  return (
    <>
      {Object.entries(toastsByPosition).map(([position, positionToasts]) => (
        <div key={position} className={['fixed z-50 flex flex-row space-x-2', positionClasses[position as ToastPosition]].filter(e => !!e).join(' ')}>
          {positionToasts.map(toast => (
            <ABToastItem key={toast.id} toast={toast} onClose={() => removeToast(toast.id)} />
          ))}
        </div>
      ))}
    </>
  );
};

// Individual Toast Item
interface ABToastItemProps {
  toast: Toast;
  onClose: () => void;
}

const ABToastItem: React.FC<ABToastItemProps> = ({ toast, onClose }) => {
  const [isHovered, setIsHovered] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });
  const itemRef = useRef<HTMLDivElement>(null);

  const typeClasses = {
    success: 'bg-green-50 border-green-200 text-green-800',
    error: 'bg-red-50 border-red-200 text-red-800',
    warning: 'bg-yellow-50 border-yellow-200 text-yellow-800',
    info: 'bg-blue-50 border-blue-200 text-blue-800',
  };

  const iconClasses = {
    success: 'text-green-500',
    error: 'text-red-500',
    warning: 'text-yellow-500',
    info: 'text-blue-500',
  };

  const icons = {
    success: (
      <svg className='w-5 h-5' fill='currentColor' viewBox='0 0 20 20'>
        <path
          fillRule='evenodd'
          d='M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z'
          clipRule='evenodd'
        />
      </svg>
    ),
    error: (
      <svg className='w-5 h-5' fill='currentColor' viewBox='0 0 20 20'>
        <path
          fillRule='evenodd'
          d='M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z'
          clipRule='evenodd'
        />
      </svg>
    ),
    warning: (
      <svg className='w-5 h-5' fill='currentColor' viewBox='0 0 20 20'>
        <path
          fillRule='evenodd'
          d='M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z'
          clipRule='evenodd'
        />
      </svg>
    ),
    info: (
      <svg className='w-5 h-5' fill='currentColor' viewBox='0 0 20 20'>
        <path
          fillRule='evenodd'
          d='M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z'
          clipRule='evenodd'
        />
      </svg>
    ),
  };

  const handleMouseEnter = () => {
    if (toast.pauseOnHover) {
      setIsHovered(true);
    }
  };

  const handleMouseLeave = () => {
    if (toast.pauseOnHover) {
      setIsHovered(false);
    }
  };

  const handleClick = () => {
    if (toast.closeOnClick) {
      onClose();
    }
  };

  const handleDragStart = (e: React.MouseEvent) => {
    if (!toast.draggable) return;

    setIsDragging(true);
    const rect = itemRef.current?.getBoundingClientRect();
    if (rect) {
      setDragOffset({
        x: e.clientX - rect.left,
        y: e.clientY - rect.top,
      });
    }
  };

  const handleDragMove = (e: MouseEvent) => {
    if (!isDragging || !toast.draggable) return;

    const newX = e.clientX - dragOffset.x;
    const newY = e.clientY - dragOffset.y;

    if (itemRef.current) {
      itemRef.current.style.transform = `translate(${newX}px, ${newY}px)`;
    }
  };

  const handleDragEnd = () => {
    setIsDragging(false);
  };

  useEffect(() => {
    if (isDragging) {
      document.addEventListener('mousemove', handleDragMove);
      document.addEventListener('mouseup', handleDragEnd);
      return () => {
        document.removeEventListener('mousemove', handleDragMove);
        document.removeEventListener('mouseup', handleDragEnd);
      };
    }
  }, [isDragging, dragOffset]);

  return (
    <div
      ref={itemRef}
      className={[
        'max-w-sm w-full bg-white shadow-xl rounded-2xl pointer-events-auto border border-gray-100 overflow-hidden transition-all duration-300 backdrop-blur-sm',
        typeClasses[toast.type],
        toast.draggable && 'cursor-move',
        isDragging && 'scale-105 shadow-2xl'
      ].filter(e => !!e).join(' ')}
      style={{
        writingMode: 'horizontal-tb',
        textOrientation: 'mixed',
        direction: 'ltr',
        unicodeBidi: 'normal',
      }}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      onClick={handleClick}
      onMouseDown={handleDragStart}
    >
      <div className='p-4'>
        <div className='flex items-center justify-between w-full gap-3'>
          {/* 왼쪽: 아이콘 */}
          <div className={['flex-shrink-0 p-2 rounded-full bg-opacity-20', iconClasses[toast.type]].filter(e => !!e).join(' ')}>{icons[toast.type]}</div>

          {/* 중앙: 텍스트 */}
          <div className='flex-1'>
            {toast.title && <p className='text-base font-semibold text-gray-800 mb-1'>{toast.title}</p>}
            <p
              className='text-sm text-gray-600 leading-relaxed'
              style={{
                margin: 0,
                padding: 0,
                display: 'block',
                writingMode: 'horizontal-tb',
                textOrientation: 'mixed',
                direction: 'ltr',
                unicodeBidi: 'normal',
                whiteSpace: 'normal',
                lineHeight: '1.6',
              }}
            >
              {toast.content}
            </p>
          </div>

          {/* 오른쪽: 닫기 버튼 */}
          <button
            className='flex-shrink-0 p-1 rounded-full text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-gray-300'
            onClick={e => {
              e.stopPropagation();
              onClose();
            }}
          >
            <span className='sr-only'>Close</span>
            <svg className='h-4 w-4' viewBox='0 0 20 20' fill='currentColor'>
              <path
                fillRule='evenodd'
                d='M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z'
                clipRule='evenodd'
              />
            </svg>
          </button>
        </div>
      </div>

      {/* Progress Bar */}
      {!toast.hideProgressBar && toast.autoClose !== false && (
        <div className='h-1 bg-gray-200'>
          <div
            className={['h-full transition-all duration-100', typeClasses[toast.type].split(' ')[0].replace('bg-', 'bg-').replace('-50', '-500')].filter(e => !!e).join(' ')}
            style={{
              width: `${100 - (toast.progress ?? 100)}%`,
              transition: isHovered ? 'none' : 'width 100ms linear',
            }}
          />
        </div>
      )}
    </div>
  );
};