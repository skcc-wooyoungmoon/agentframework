export type FilterOption = {
  label: string;
  value: string;
  disabled?: boolean;
};

export type FilterFieldType = 'select' | 'input' | 'checkbox' | 'radio' | 'date' | 'multiselect';

export type FilterField = {
  name: string;
  label: string;
  type: FilterFieldType;
  options?: FilterOption[];
  value: string | string[] | boolean;
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
  // eslint-disable-next-line no-unused-vars
  onChange: (value: string | string[] | boolean) => void;
};

export type FilterProps = {
  fields: FilterField[];
  modalId: string;
  title?: string;
  // eslint-disable-next-line no-unused-vars
  onApply: (values: Record<string, any>) => void;
  onCancel?: () => void;
  onReset?: () => void;
  showResetButton?: boolean;
};

export type FilterConfig = {
  id: string;
  title: string;
  fields: Omit<FilterField, 'value' | 'onChange'>[];
  defaultValues?: Record<string, any>;
};

export type AppliedFilter = {
  [key: string]: any;
};

export type FilterState = {
  applied: AppliedFilter;
  temp: AppliedFilter;
  isVisible: boolean;
};
