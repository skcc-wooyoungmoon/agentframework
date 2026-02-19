export type ToolType = 'custom_api' | 'custom_code';

export interface Tool {
  id: string;
  name: string;
  description: string;
  input_keys?: ToolInputKey[];
  code?: string;
  tool_type: ToolType;
  project_id?: string;
  server_url?: string;
  method?: string;
  created_at?: string;
  updated_at?: string;
  'api_param:'?: string;
}

export type ToolInputKey = {
  key: string;
  comment: string;
  required: boolean;
  type: string;
  default_value: string | null;
};
