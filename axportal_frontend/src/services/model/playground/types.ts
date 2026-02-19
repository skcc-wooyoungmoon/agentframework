import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import type { InfPromptMessageType } from '@/services/prompt/inference/types';

/**
 * 모델 플레이그라운드 관련 타입 정의
 */

/**
 * 모델 파라미터 설정
 */
export type ModelParameters = {
  temperature?: number;
  topP?: number;
  presencePenalty?: number;
  frequencyPenalty?: number;
  maxTokens?: number;
  temperatureChecked?: boolean;
  topPChecked?: boolean;
  presencePenaltyChecked?: boolean;
  frequencyPenaltyChecked?: boolean;
  maxTokensChecked?: boolean;
};

/**
 * 플레이그라운드용 모델 타입 (GetModelDeployResponse + 파라미터)
 */
export type PlaygroundModel = GetModelDeployResponse & {
  instanceId: string; // 플레이그라운드에서 각 모델 인스턴스를 구분하기 위한 고유 ID
  parameters?: ModelParameters;
};

export type ModelPlaygroundChatRequest = {
  model: string;
  systemPrompt?: string;
  userPrompt: string;
  maxTokens?: number;
  temperature?: number;
  topP?: number;
  frequencyPenalty?: number;
  presencePenalty?: number;
  stream?: boolean;
  projectId?: string;
  servingId: string;
};

export type ModelPlaygroundChatResponse = {
  id: string;
  model: string;
  choices: ChatMessage[];
  usage: TokenUsage;
  createdAt: string;
  stream: boolean;
  error?: string;
};

export type ChatMessage = {
  index: number;
  message: MessageContent;
  finishReason: string;
};

export type MessageContent = {
  role: string;
  content: string;
};

export type TokenUsage = {
  promptTokens: number;
  completionTokens: number;
  totalTokens: number;
};

export type MessageType = {
  message: string;
  messageId: string;
  mtype: number;
  order: number;
};

export type PromptData = {
  promptUuid: string;
  versionUuid?: string;
  messages?: InfPromptMessageType[];
  isLoading: boolean;
  error?: any;
};
