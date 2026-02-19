import { UITextLabel } from '@/components/UI/atoms';
import React from 'react';
import { UIGroup } from '../UIGroup';
import type { UIVersionCardItem, UIVersionCardProps } from './types';

export const UIVersionCard: React.FC<UIVersionCardProps> = ({ versions, onVersionClick, className = '' }) => {
  const handleClick = (item: UIVersionCardItem) => {
    if (onVersionClick) {
      onVersionClick(item);
    }
  };

  return (
    <div className={'card-version ' + className}>
      <UIGroup gap={16} direction='column'>
        {versions.map((item, index) => (
          <div key={index} className={`card-version-item ${item.isActive ? 'active' : ''}`} onClick={() => handleClick(item)}>
            {item.tags && item.tags.length > 0 && (
              <div className='tags'>
                {item.tags.map((tag, tagIndex) => (
                  <span key={tagIndex} className={`tag ${tag.intent === 'gray' ? 'latest' : 'release'}`}>
                    <UITextLabel intent={tag.intent}>{tag.label}</UITextLabel>
                  </span>
                ))}
              </div>
            )}

            <h3 className='version'>{item.version}</h3>
            <p className='date'>{item.date}</p>
          </div>
        ))}
      </UIGroup>
    </div>
  );
};
