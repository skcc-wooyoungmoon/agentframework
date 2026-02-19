import { useRef, useState } from 'react';
import { UIButton2, UICheckbox2, UIIcon2, UIProgress, UITypography } from '../../../atoms';
import { UIMoreMenuPopup, type UIMoreMenuType } from '../../grid';
import type { UIGridCardProps } from './types';

export function UIGridCard<TData = any>({ id, data, title, caption, checkbox, progressValue, statusArea, rows, width, moreMenuConfig, onClick }: UIGridCardProps<TData>) {
  const moreButtonRef = useRef<HTMLButtonElement>(null);
  const cardRef = useRef<HTMLDivElement>(null);

  const buildStyle = () => {
    const styles: React.CSSProperties = {};

    if (width) {
      styles.width = typeof width === 'number' ? `${width}px` : width;
    }

    return styles;
  };

  // 더보기 메뉴
  const [moreMenu, setMoreMenu] = useState<UIMoreMenuType<TData>>({
    isOpen: false,
    x: 0,
    y: 0,
    data: null,
  });

  const updateMoreMenu = (value: Partial<UIMoreMenuType<TData>>) => {
    setMoreMenu(prev => ({ ...prev, ...value }));
  };

  const handleMoreButtonClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.stopPropagation();
    // 절대 좌표 사용 (화면 기준)
    updateMoreMenu({
      isOpen: true,
      data,
    });
  };

  const handleClick = (e: React.MouseEvent<HTMLDivElement>) => {
    e.preventDefault();
    onClick?.(e);
  };

  const handleCheckboxChange = (checked: boolean, value: string, event?: React.ChangeEvent<HTMLInputElement>) => {
    // 이벤트 전파 막기
    if (event) {
      event.stopPropagation();
    }
    checkbox?.onChange?.(checked, value, event);
  };

  return (
    <ul>
      <li key={id} style={{ position: 'relative' }}>
        <div className='grid-card' style={buildStyle()} ref={cardRef} onClick={handleClick}>
          <div className='grid-card-header'>
            {checkbox && (
              <div className='grid-card-checkbox' onClick={e => e.stopPropagation()}>
                <UICheckbox2 name='basic1' value={id} className='box' checked={checkbox.checked} onChange={handleCheckboxChange} />
              </div>
            )}
            <div className='grid-card-title pb-3'>
              <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
                {title}
              </UITypography>
              {caption && (
                <UITypography variant='body-1' className='secondary-neutral-600'>
                  {caption}
                </UITypography>
              )}
            </div>
            {moreMenuConfig && (
              <div>
                <UIButton2 ref={moreButtonRef} className='btn-ic' onClick={handleMoreButtonClick}>
                  <UIIcon2 className='ic-action-more ic-24' />
                </UIButton2>
              </div>
            )}
          </div>
          <div className='grid-card-body'>
            {statusArea && <div className='grid-card-status pb-3'>{statusArea}</div>}
            {progressValue !== undefined && progressValue !== null && (
              <div className='grid-card-progress'>
                <UIProgress showPercent={true} value={progressValue}></UIProgress>
              </div>
            )}
            {rows &&
              rows.map(item => {
                // value가 배열이면 태그 렌더링
                // if (Array.isArray(item.value)) {
                //   if (item.value.length === 0) {
                //     return (
                //       <div key={item.label} className='grid-card-row items-center'>
                //         <span className='label'>{item.label}</span>
                //         <span className='value'></span>
                //       </div>
                //     );
                //   }
                //   return (
                //     <div key={item.label} className='grid-card-row items-center'>
                //       <span className='label'>{item.label}</span>
                //       <span className='value'>
                //         <div className='flex gap-1 flex-wrap'>
                //           {item.value.slice(0, 2).map((tag: string, index: number) => (
                //             <UILabel key={index} variant='gray' intent='gray-type1' showIcon={false}>
                //               {tag}
                //             </UILabel>
                //           ))}
                //           {item.value.length > 2 && <span className='text-gray-500 text-sm'>+{item.value.length - 2}</span>}
                //         </div>
                //       </span>
                //     </div>
                //   );
                // }
                // 일반 값이면 기본 렌더링
                return (
                  <div key={item.label} className='grid-card-row items-center'>
                    <span className='label'>{item.label}</span>
                    <span className='value'>{item.value}</span>
                  </div>
                );
              })}
          </div>
        </div>
        {moreMenuConfig && <UIMoreMenuPopup type='card' {...moreMenu} menuConfig={moreMenuConfig} onClose={() => setMoreMenu({ isOpen: false, data: null })} />}
      </li>
    </ul>
  );
}
