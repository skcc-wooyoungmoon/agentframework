export type BaseAction<T> = {
  label: string;
  // eslint-disable-next-line no-unused-vars
  handler: (data: T) => void;
  className?: string;
};

export type ActionConfig<T> = {
  // eslint-disable-next-line no-unused-vars
  onEdit?: (data: T) => void;
  // eslint-disable-next-line no-unused-vars
  onDelete?: (id: string, data?: T) => void;
  // eslint-disable-next-line no-unused-vars
  onCopy?: (id: string) => void;
  customActions?: Array<BaseAction<T>>;
};
