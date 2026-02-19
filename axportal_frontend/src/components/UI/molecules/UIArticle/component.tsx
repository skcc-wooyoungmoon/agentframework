import type { UIArticleProps } from './types';

export const UIArticle = ({ children, className, ...innerProps }: UIArticleProps) => {
  return (
    <article className={className} {...innerProps}>
      {children}
    </article>
  );
};
